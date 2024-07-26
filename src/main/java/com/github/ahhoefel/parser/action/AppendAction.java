package com.github.ahhoefel.parser.action;

import java.util.function.Function;

import com.github.ahhoefel.parser.Locateable;
import com.github.ahhoefel.parser.LocateableList;

/**
 * Appends the second result to the list returned by the first.
 * 
 * This action only applies to rules of the form R -> L A where the action on L
 * returns a list and the desired result of R is [L..., A].
 * 
 * In particular, we use this for grammars like:
 * 
 * <pre>
 * L -> L a
 * L -> a
 * </pre>
 * 
 * Use the provided static PrependAction.SINGLETON, rather than instantiating
 * this class.
 */
public class AppendAction implements Function<Locateable[], Locateable> {

  public static final AppendAction SINGLETON = new AppendAction();

  private AppendAction() {
  }

  @Override
  @SuppressWarnings("unchecked")
  public Locateable apply(Locateable[] objects) {
    LocateableList<Locateable> list;
    if (objects.length == 0) {
      list = new LocateableList<>();
    } else if (objects[0] instanceof LocateableList) {
      list = (LocateableList<Locateable>) objects[0];
    } else {
      list = new LocateableList<>();
      list.add(objects[0]);
    }
    if (objects.length > 1 && objects[1] != null) {
      list.add(objects[1]);
    }
    return list;
  }
}
