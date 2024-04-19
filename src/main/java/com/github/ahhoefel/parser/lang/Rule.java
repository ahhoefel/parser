package com.github.ahhoefel.parser.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import com.github.ahhoefel.parser.Locateable;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.action.ParseTreeAction;

public class Rule {
  private Symbol source;
  private List<Symbol> symbols;
  private Function<Locateable[], Locateable> action;

  public Rule(Symbol source, List<Symbol> symbols) {
    assert source != null;

    int i = 0;
    for (; i < symbols.size(); i++) {
      if (symbols.get(i) == null) {
        break;
      }
    }
    assert i == symbols.size() : "Null symbol at index : " + i;
    assert symbols.stream().allMatch(x -> x != null);
    this.source = source;
    this.symbols = symbols;
    this.action = new ParseTreeAction(this);
  }

  public Rule(Symbol source, List<Symbol> symbols, Function<Locateable[], Locateable> action) {
    assert source != null;
    assert symbols.stream().allMatch(x -> x != null);
    this.source = source;
    this.symbols = symbols;
    this.action = action;
  }

  public Symbol getSource() {
    return source;
  }

  public List<Symbol> getSymbols() {
    return symbols;
  }

  public Function<Locateable[], Locateable> getAction() {
    return action;
  }

  public Rule setAction(Function<Locateable[], Locateable> action) {
    this.action = action;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Rule)) {
      return false;
    }
    Rule other = (Rule) o;
    return Objects.equals(source, other.source) && Objects.equals(symbols, other.symbols);
  }

  @Override
  public int hashCode() {
    return source.hashCode() + 31 * symbols.hashCode();
  }

  public String toString() {
    StringBuffer buf = new StringBuffer();
    buf.append(source.toString());
    buf.append(" ⟹ ");
    for (Symbol symbol : symbols) {
      buf.append(" ");
      buf.append(symbol.toString());
    }
    return buf.toString();
  }

  public static class Builder {

    private List<Rule> rules;

    public Builder() {
      rules = new ArrayList<>();
    }

    public Rule add(Symbol from, Symbol... to) {
      Rule r = new Rule(from, Arrays.asList(to));
      rules.add(r);
      return r;
    }

    public List<Rule> build() {
      return rules;
    }
  }
}