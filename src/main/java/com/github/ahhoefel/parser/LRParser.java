package com.github.ahhoefel.parser;

import java.util.*;

import com.github.ahhoefel.parser.SymbolTable.TerminalTable;
import com.github.ahhoefel.parser.io.ParseError;
import com.github.ahhoefel.parser.io.Target;
import com.github.ahhoefel.parser.lang.GrammarBuilder;
import com.github.ahhoefel.parser.lang.LanguageComponent;
import com.github.ahhoefel.parser.lang.Rule;

/**
 * A canonical LR parser.
 * 
 * <p>
 * The LRParser is constructed with a grammar and is used for parsing strings
 * and applying the actions on the grammar rules.
 * 
 * <p>
 * Specifically, the LRParser needs a LexicalMapping to map characters to
 * Symbols used in the grammar. The grammar is specified by one or
 * more LanguageComponents which each describe part of the grammer. They can
 * create symbols, import and export them, and use symbols from the
 * LexicalMapping.
 * 
 * <p>
 * Actions are specified in the grammar which map the results
 * for the terms in each rule into the result for that Rule. This process is not
 * type safe. Each action is simply a
 * {@code Function<Locateable[], Locateable>}.
 * 
 * <p>
 * Once the LRParser is constructed, the parse method can be called on a string
 * to obtain the result. Optionally, a target can be provided to annotate
 * CodeLocations with the source file.
 * 
 * @see com.github.ahhoefel.parser.example
 */
public class LRParser {

  private LRTable table;
  private Grammar grammar;

  public LRParser(TerminalTable terminals, String startSymbol, LanguageComponent... components) {
    this.grammar = GrammarBuilder.build(terminals, startSymbol, components);
    this.table = getCanonicalLRTable(grammar);
  }

  public SymbolTable.TerminalTable getTerminals() {
    return grammar.getTerminals();
  }

  public SymbolTable.TerminalTable getNonTerminals() {
    return grammar.getNonTerminals();
  }

  public <T> T parse(@SuppressWarnings("rawtypes") Iterator<Token> tokens, Class<T> clazz) {
    return parse(Optional.empty(), tokens, clazz);
  }

  public <T> T parse(Target target, @SuppressWarnings("rawtypes") Iterator<Token> tokens, Class<T> clazz) {
    return parse(Optional.of(target), tokens, clazz);
  }

  public <T> T parse(Optional<Target> target, @SuppressWarnings("rawtypes") Iterator<Token> tokens, Class<T> clazz) {
    ErrorLog log = new ErrorLog();

    if (!log.isEmpty()) {
      throw new ParseException(log);
    }
    Object o = parseTokens(table, tokens, grammar.getAugmentedStartRule().getSource(),
        Optional.empty(), log);
    if (!log.isEmpty()) {
      System.out.println(table);
      throw new ParseException(log);
    }
    try {
      return clazz.cast(o);
    } catch (ClassCastException e) {
      System.out.println(table.toString());
      throw new RuntimeException("Class cast exception of object " + o.toString(), e);
    }
  }

  private static LRTable getCanonicalLRTable(Grammar g) {
    Grammar.FirstSymbols first = g.first();
    List<LRItem> items = getLRItems(g, first, 1);
    List<LRTable.State> states = new ArrayList<>();
    for (LRItem item : items) {
      states.add(item.toState(g));
    }
    return new LRTable(states, g.getTerminals().getEof(), g.getAugmentedStartRule().getSource());
  }

  /**
   * @param lookAhead Use 1 for a Canonical LR Table and 0 for an SLR table.
   */
  private static List<LRItem> getLRItems(Grammar g, Grammar.FirstSymbols first, int lookAhead) {
    Rule start = g.getAugmentedStartRule();
    MarkedRule markedStart = new MarkedRule(start, 0, g.getTerminals().getEof());
    LRItem startItem = new LRItem(Set.of(markedStart), g, first, lookAhead);
    Map<LRItem, Integer> itemMap = new HashMap<>();
    List<LRItem> items = new ArrayList<>();
    List<LRItem> queue = new ArrayList<>();
    queue.add(startItem);
    items.add(startItem);
    while (!queue.isEmpty()) {
      LRItem item = queue.remove(queue.size() - 1);
      Map<Symbol, Set<MarkedRule>> nexts = gotos(item);
      for (Map.Entry<Symbol, Set<MarkedRule>> entry : nexts.entrySet()) {
        LRItem nextItem = new LRItem(entry.getValue(), g, first, lookAhead);
        int nextItemIndex;
        if (itemMap.containsKey(nextItem)) {
          nextItemIndex = itemMap.get(nextItem);
          nextItem = items.get(nextItemIndex);
        } else {
          nextItemIndex = items.size();
          items.add(nextItem);
          queue.add(nextItem);
          itemMap.put(nextItem, nextItemIndex);
        }
        item.next.put(entry.getKey(), nextItem);
        item.nextIndex.put(entry.getKey(), nextItemIndex);
        nextItem.index = nextItemIndex;
      }
    }
    return items;
  }

  private static Map<Symbol, Set<MarkedRule>> gotos(LRItem item) {
    Map<Symbol, Set<MarkedRule>> nexts = new HashMap<>();
    for (MarkedRule rule : item.rules) {
      Optional<Symbol> optSymbol = rule.getSymbolAtIndex();
      if (!optSymbol.isPresent()) {
        continue;
      }
      Symbol symbol = optSymbol.get();
      MarkedRule nextRule = new MarkedRule(rule.getRule(), rule.getIndex() + 1, rule.getLookAhead());
      if (nexts.containsKey(symbol)) {
        nexts.get(symbol).add(nextRule);
      } else {
        Set<MarkedRule> n = new HashSet<>();
        n.add(nextRule);
        nexts.put(symbol, n);
      }
    }
    return nexts;
  }

  public String toString() {
    return table.toString();
  }

  private static class SymbolState {
    public Symbol symbol;
    public int stateIndex;

    public SymbolState(Symbol symbol, int stateIndex) {
      this.symbol = symbol;
      this.stateIndex = stateIndex;
    }

    public String toString() {
      return String.format("(Symbol: %s, State: %d)", symbol.toString(), stateIndex);
    }
  }

  @SuppressWarnings("rawtypes")
  private static <C extends Locateable> Object parseTokens(LRTable table, Iterator<Token> tokens, Symbol start,
      Optional<C> context,
      ErrorLog log) {
    Stack<SymbolState> stack = new Stack<>();
    Stack<Locateable> result = new Stack<>();
    SymbolState symbolState = new SymbolState(start, 0);

    Token nextToken;
    Symbol nextSymbol;
    boolean lastIteration = false;
    if (tokens.hasNext()) {
      nextToken = tokens.next();
      nextSymbol = nextToken.getSymbol();
    } else {
      lastIteration = true;
      nextToken = new Token<String>(table.eof, "eof", null);
      nextSymbol = table.eof;
    }

    while (true) {
      LRTable.State state = table.state.get(symbolState.stateIndex);
      if (state.shift.containsKey(nextSymbol)) {
        stack.push(new SymbolState(nextSymbol, state.shift.get(nextSymbol)));
        result.push(nextToken);
        if (tokens.hasNext()) {
          nextToken = tokens.next();
          nextSymbol = nextToken.getSymbol();
        } else if (lastIteration) {
          result.pop(); // Remove eof
          break;
        } else {
          lastIteration = true;
          nextToken = new Token<String>(table.eof, "eof", null);
          nextSymbol = table.eof;
        }
      } else if (state.reduce.containsKey(nextSymbol)) {
        Rule rule = state.reduce.get(nextSymbol);
        int numChildren = rule.getSymbols().size();
        int numParameters = numChildren + (context.isPresent() ? 1 : 0);
        Locateable[] children = new Locateable[numParameters];
        for (int i = 0; i < numChildren; i++) {
          stack.pop();
          children[numChildren - i - 1] = result.pop();
        }
        if (context.isPresent()) {
          children[numParameters - 1] = context.get();
        }
        stack.push(new SymbolState(rule.getSource(),
            table.state.get(stack.isEmpty() ? 0 : stack.peek().stateIndex).state.get(rule.getSource())));
        try {
          result.push(rule.getAction().apply(children));
        } catch (Exception e) {
          log.add(new ParseError(nextToken.getLocation(),
              "ParseActionException at rule " + rule, e));
          return null;
        }
      } else {
        String out = "Parsing error.\n";
        out += "Next token: " + nextToken + "\n";
        out += "State: " + symbolState + "\n";
        out += "Stack (top):\n";
        for (int i = 0; stack.deepPeek(i).isPresent(); i++) {
          Optional<SymbolState> s = stack.deepPeek(i);
          out += "\t" + s.get() + "\n";
        }
        out += "(bottom)";
        log.add(new ParseError(nextToken.getLocation(), out));
        return null;
      }
      symbolState = stack.peek();
    }
    return result.pop();
  }
}
