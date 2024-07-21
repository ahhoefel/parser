package com.github.ahhoefel.parser.action;

import java.util.function.Function;

import com.github.ahhoefel.parser.Locateable;
import com.github.ahhoefel.parser.LocateableList;

/**
 * Prepends the first result to the list returned by the second.
 * 
 * This action only applies to rules of the form R -> A L where the action on L
 * returns a list
 * and the desired result of R is [A, L..].
 * 
 * In particular, we use this for grammars like:
 * 
 * <pre>
 * L -> a L
 * L -> a
 * </pre>
 * 
 * This class should be reimplemented using a LinkedLocatableList, as currently
 * prepending is O(n).
 * Use the provided static PrependAction.SINGLETON, rather than instantiating
 * this class.
 */
public class PrependAction implements Function<Locateable[], Locateable> {

  public static final PrependAction SINGLETON = new PrependAction();

  private PrependAction() {
  }

  @Override
  @SuppressWarnings("unchecked")
  public Locateable apply(Locateable[] objects) {
    LocateableList<Locateable> list;
    if (objects.length == 1) {
      list = new LocateableList<>();
    } else {
      list = (LocateableList<Locateable>) objects[1];
    }
    if (objects[0] != null) {
      list.addFirst(objects[0]);
    }
    return list;
  }
}
