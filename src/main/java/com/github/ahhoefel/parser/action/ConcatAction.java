package com.github.ahhoefel.parser.action;

import java.util.function.Function;

import com.github.ahhoefel.parser.Locateable;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.Token;
import com.github.ahhoefel.parser.io.CodeLocation;

/**
 * Concatenates Tokens.
 * 
 * This action concatenates Tokens into a single larger token. It can only be
 * applied
 * to rules whose symbols return Tokens, e.g., terminals and non-terminal with a
 * ConcatAction on their rules.
 * 
 * Use the provided static ConcatAction.SINGLETON, rather than instantiating
 * this class.
 */
public class ConcatAction implements Function<Locateable[], Locateable> {

  public static final ConcatAction SINGLETON = new ConcatAction();

  private ConcatAction() {
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public Locateable apply(Locateable[] objects) {
    if (objects.length == 0) {
      throw new RuntimeException("ConcatAction only applies to one or more object. Got 0.");
    }
    StringBuffer buf = new StringBuffer();
    Symbol type = null;
    CodeLocation location = null;
    for (int i = 0; i < objects.length; i++) {
      Locateable o = objects[i];
      if (location == null) {
        location = o.getLocation();
      } else {
        location = new CodeLocation(location, o.getLocation());
      }
      if (o instanceof Token) {
        Token token = (Token) o;
        buf.append(token.getValue());
        if (type == null) {
          type = token.getSymbol();
        }
      } else {
        throw new RuntimeException(
            "Unsupported type (" + o.getClass() + "). Only Tokens are supported by ConcatAction.");
      }
    }
    return new Token(type, buf.toString(), location);
  }
}
