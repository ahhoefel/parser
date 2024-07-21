package com.github.ahhoefel.parser;

import com.github.ahhoefel.parser.io.CodeLocation;

public class Token<V> implements Locateable {

  private final V value;
  private final Symbol symbol;
  private CodeLocation location;

  public Token(Symbol symbol, V value, CodeLocation location) {
    this.symbol = symbol;
    this.value = value;
    this.location = location;
  }

  public void setLocation(CodeLocation location) {
    this.location = location;
  }

  public String toString() {
    return String.format("%s(%s) @ %s", symbol, value, location);
  }

  public Symbol getSymbol() {
    return symbol;
  }

  public V getValue() {
    return value;
  }

  public CodeLocation getLocation() {
    return location;
  }

  public boolean equals(Object o) {
    if (!(o instanceof Token<?>)) {
      return false;
    }
    Token<?> t = (Token<?>) o;
    return value.equals(t.value) && symbol.equals(t.symbol);
  }
}
