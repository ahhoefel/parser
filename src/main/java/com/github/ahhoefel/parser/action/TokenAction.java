package com.github.ahhoefel.parser.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.github.ahhoefel.parser.Locateable;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.Token;

public class TokenAction implements Function<Locateable[], Locateable> {

  private Symbol terminal;
  private Map<String, Symbol> keywordMap;

  public TokenAction(Symbol terminal) {
    this(terminal, List.of());
  }

  public TokenAction(Symbol terminal, List<Symbol> keywords) {
    this.terminal = terminal;
    if (terminal == null) {
      throw new RuntimeException("Token action cannot have null terminal.");
    }
    keywordMap = new HashMap<>();
    for (Symbol keyword : keywords) {
      keywordMap.put(keyword.toString(), keyword);
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public Locateable apply(Locateable[] objects) {
    Token token = (Token) objects[0];
    Object value = token.getValue();
    Symbol symbol = keywordMap.get(value);
    if (symbol == null) {
      symbol = terminal;
    }
    return new Token(symbol, value, token.getLocation());
  }
}
