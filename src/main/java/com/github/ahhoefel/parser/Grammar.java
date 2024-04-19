package com.github.ahhoefel.parser;

import java.util.*;

import com.github.ahhoefel.parser.lang.RangeTokenizer;
import com.github.ahhoefel.parser.lang.Rule;

public class Grammar {

  private RangeTokenizer tokenizer;
  private SymbolTable.TerminalTable terminals;
  private SymbolTable.NonTerminalTable nonTerminals;
  private List<Rule> rules;
  private List<List<Rule>> rulesBySymbolIndex;
  private Rule augmentedStart;
  private ShiftReduceResolver resolver;

  public Grammar(RangeTokenizer tokenizer,
      SymbolTable.NonTerminalTable nonTerminals,
      List<Rule> rules,
      ShiftReduceResolver resolver) {
    this.tokenizer = tokenizer;
    this.terminals = tokenizer.getTerminals();
    this.nonTerminals = nonTerminals;
    this.resolver = resolver;
    this.augmentedStart = new Rule(nonTerminals.getAugmentedStart(), List.of(nonTerminals.getStart()))
        .setAction(e -> e[0]);
    this.rules = new ArrayList<>();
    this.rules.add(augmentedStart);
    this.rules.addAll(rules);
    rulesBySymbolIndex = new ArrayList<>(nonTerminals.size());
    for (int i = 0; i < nonTerminals.size(); i++) {
      rulesBySymbolIndex.add(new ArrayList<>());
    }
    for (Rule rule : rules) {
      rulesBySymbolIndex.get(rule.getSource().getIndex()).add(rule);
    }
  }

  public String toString() {
    StringBuilder out = new StringBuilder();
    for (Rule rule : rules)
      out.append(rule).append("\n");
    return out.toString();
  }

  public RangeTokenizer getTokenizer() {
    return tokenizer;
  }

  public SymbolTable.TerminalTable getTerminals() {
    return terminals;
  }

  public SymbolTable.NonTerminalTable getNonTerminals() {
    return nonTerminals;
  }

  public Rule getAugmentedStartRule() {
    return augmentedStart;
  }

  public List<Rule> get(Symbol symbol) {
    return rulesBySymbolIndex.get(symbol.getIndex());
  }

  public boolean isTerminal(Symbol symbol) {
    return terminals.contains(symbol);
  }

  public ShiftReduceResolver getShiftReduceResolver() {
    return resolver;
  }

  public FirstSymbols first() {
    return new FirstSymbols();
  }

  public static Set<Symbol> epsilons(Grammar g) {
    Set<Symbol> epsilonRules = new HashSet<>();
    Set<Symbol> visited = new HashSet<>();
    for (Symbol symbol : g.nonTerminals.getSymbols()) {
      isEpsilonNonTerminal(g, epsilonRules, visited, symbol);
    }
    return epsilonRules;
  }

  private static boolean isEpsilonNonTerminal(Grammar g, Set<Symbol> epsilonRules, Set<Symbol> visited, Symbol source) {
    if (visited.contains(source)) {
      return epsilonRules.contains(source);
    }
    visited.add(source);
    for (Rule rule : g.get(source)) {
      if (isEpsilonRule(g, epsilonRules, visited, rule)) {
        epsilonRules.add(source);
        return true;
      }
    }
    return false;
  }

  private static boolean isEpsilonRule(Grammar g, Set<Symbol> epsilonRules, Set<Symbol> visited, Rule rule) {
    for (Symbol symbol : rule.getSymbols()) {
      if (g.isTerminal(symbol)) {
        return false;
      }
    }
    for (Symbol symbol : rule.getSymbols()) {
      if (!isEpsilonNonTerminal(g, epsilonRules, visited, symbol)) {
        return false;
      }
    }
    return true;
  }

  public static NonTerminalMap<Set<Symbol>> firstNonTerminals(Grammar g, Set<Symbol> epsilons) {
    NonTerminalMap<Set<Symbol>> firsts = new NonTerminalMap<>(g);
    for (Symbol symbol : g.nonTerminals.getSymbols()) {
      firsts.set(symbol, new HashSet<>());
    }
    for (Rule rule : g.rules) {
      addSimpleFirsts(g, rule, epsilons, firsts);
    }
    for (Symbol symbol : g.nonTerminals.getSymbols()) {
      transitiveClosure(symbol, firsts);
    }
    return firsts;
  }

  private static void addSimpleFirsts(Grammar g, Rule rule, Set<Symbol> epsilons, NonTerminalMap<Set<Symbol>> firsts) {
    for (Symbol symbol : rule.getSymbols()) {
      if (g.isTerminal(symbol)) {
        return;
      }
      firsts.get(rule.getSource()).add(symbol);
      if (!epsilons.contains(symbol)) {
        return;
      }
    }
  }

  private static void transitiveClosure(Symbol source, NonTerminalMap<Set<Symbol>> firsts) {
    List<Symbol> toVisit = new ArrayList<>();
    toVisit.addAll(firsts.get(source));
    while (!toVisit.isEmpty()) {
      Symbol symbol = toVisit.remove(toVisit.size() - 1);
      Set<Symbol> nexts = firsts.get(symbol);
      for (Symbol next : nexts) {
        if (!firsts.get(source).contains(next)) {
          firsts.get(source).add(next);
          toVisit.add(next);
        }
      }
    }
  }

  public static class NonTerminalMap<T> {
    private T[] values;
    private Grammar grammar;

    @SuppressWarnings("unchecked")
    public NonTerminalMap(Grammar g) {
      grammar = g;
      values = (T[]) new Object[g.nonTerminals.getSymbols().size()];
    }

    public void set(Symbol key, T value) {
      values[key.getIndex()] = value;
    }

    public T get(Symbol key) {
      return values[key.getIndex()];
    }

    public String toString() {
      StringBuilder out = new StringBuilder();
      for (int i = 0; i < values.length; i++) {
        out.append(grammar.getNonTerminals().getSymbols().get(i));
        out.append(": ");
        out.append(values[i]);
        out.append("\n");
      }
      return out.toString();
    }

    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
      if (!(o instanceof NonTerminalMap)) {
        return false;
      }
      return Arrays.equals(((NonTerminalMap<T>) o).values, this.values);
    }
  }

  public static NonTerminalMap<Set<Symbol>> firstTerminals(Grammar g, Set<Symbol> epsilons,
      NonTerminalMap<Set<Symbol>> firstNonTerminals) {
    NonTerminalMap<Set<Symbol>> firsts = new NonTerminalMap<>(g);
    for (Symbol key : g.nonTerminals.getSymbols()) {
      firsts.set(key, new HashSet<>());
    }
    // Finds the first terminal in every rule following epsilon generating
    // non-terminals.
    for (Rule rule : g.rules) {
      for (Symbol symbol : rule.getSymbols()) {
        if (g.isTerminal(symbol)) {
          firsts.get(rule.getSource()).add(symbol);
          break;
        }
        if (!epsilons.contains(symbol)) {
          break;
        }
      }
    }
    // Set the first terminals for each symbol to be the firsts from first
    // non-terminals.
    for (Symbol key : g.nonTerminals.getSymbols()) {
      for (Symbol firstNonTerminal : firstNonTerminals.get(key)) {
        firsts.get(key).addAll(firsts.get(firstNonTerminal));
      }
    }
    return firsts;
  }

  public class FirstSymbols {
    private NonTerminalMap<Set<Symbol>> nonTerminals;
    private NonTerminalMap<Set<Symbol>> terminals;
    private Set<Symbol> epsilons;

    public FirstSymbols() {
      this.epsilons = epsilons(Grammar.this);
      this.nonTerminals = firstNonTerminals(Grammar.this, epsilons);
      this.terminals = firstTerminals(Grammar.this, epsilons, nonTerminals);
    }

    public Set<Symbol> getEpsilons() {
      return epsilons;
    }

    public Set<Symbol> getFirstTerminals(Symbol nonTerminal) {
      return terminals.get(nonTerminal);
    }

    public Set<Symbol> getFirstNonTerminals(Symbol nonTerminal) {
      return nonTerminals.get(nonTerminal);
    }
  }
}
