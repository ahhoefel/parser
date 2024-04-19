package com.github.ahhoefel.parser;

import java.util.List;
import java.util.Map;

import com.github.ahhoefel.parser.lang.Rule;

public class LRTable {
  public List<State> state;
  public Symbol eof;
  public Symbol augmentedStart;

  public LRTable(List<State> state, Symbol eof, Symbol augmentedStart) {
    this.state = state;
    this.eof = eof;
    this.augmentedStart = augmentedStart;
  }

  public static class State {
    LRItem item;
    Map<Symbol, Rule> reduce;
    Map<Symbol, Integer> shift;
    Map<Symbol, Integer> state;

    public State(LRItem item, Map<Symbol, Rule> reduce, Map<Symbol, Integer> shift, Map<Symbol, Integer> state) {
      this.item = item;
      this.reduce = reduce;
      this.shift = shift;
      this.state = state;
    }

    public String toString() {
      return "Marked Rules: " + item.getMarkedRules() + "\nReduce: " + reduce + "\nShift: " + shift
          + "\nState: "
          + state
          + '\n';
    }
  }

  public String toString() {
    StringBuilder out = new StringBuilder();
    out.append("LRTable\n");
    int i = 0;
    for (State state : this.state) {
      out.append(i).append(":\n").append(state).append("\n");
      i++;
    }
    return out.toString();
  }
}
