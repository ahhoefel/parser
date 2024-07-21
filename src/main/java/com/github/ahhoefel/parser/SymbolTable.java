package com.github.ahhoefel.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** A container for Symbols and their labels. */
public class SymbolTable {
  private List<Symbol> symbols;
  private List<Symbol> unmodifiableSymbols;
  private Map<String, Symbol> labelMap;

  public static class NonTerminalTable extends TerminalTable {
    private Symbol startp;
    private Symbol start;

    public NonTerminalTable() {
      super();
      startp = newSymbol("startp");
      start = newSymbol("start");
    }

    public Symbol getAugmentedStart() {
      return startp;
    }

    public Symbol getStart() {
      return start;
    }
  }

  public static class TerminalTable extends SymbolTable {
    private Symbol eof;

    public TerminalTable() {
      super();
      eof = newSymbol("eof");
    }

    public Symbol getEof() {
      return eof;
    }
  }

  public SymbolTable() {
    symbols = new ArrayList<>();
    unmodifiableSymbols = Collections.unmodifiableList(symbols);
    labelMap = new HashMap<>();

  }

  public Symbol newSymbol(String label) {
    Symbol s = new Symbol(label, symbols.size());
    symbols.add(s);
    labelMap.put(label, s);
    return s;
  }

  public List<Symbol> getSymbols() {
    return unmodifiableSymbols;
  }

  public Optional<Symbol> getSymbolByLabel(String label) {
    if (labelMap.containsKey(label)) {
      return Optional.of(labelMap.get(label));
    }
    return Optional.empty();
  }

  public int size() {
    return unmodifiableSymbols.size();
  }

  public boolean contains(Symbol s) {
    return s.getIndex() < symbols.size() && symbols.get(s.getIndex()) == s;
  }
}
