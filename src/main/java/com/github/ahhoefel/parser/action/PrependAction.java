package com.github.ahhoefel.parser.action;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

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
 * The resulting list is implemented with LinkedList, so element access by index
 * should be avoided.
 * 
 * Use the provided static PrependAction.SINGLETON, rather than instantiating
 * this class.
 */
public class PrependAction implements Function<Object[], Object> {

  public static final PrependAction SINGLETON = new PrependAction();

  private PrependAction() {
  }

  @Override
  @SuppressWarnings("unchecked")
  public Object apply(Object[] objects) {
    List<Object> list;
    if (objects.length == 1) {
      list = new LinkedList<Object>();
    } else {
      list = (List<Object>) objects[1];
    }
    if (objects[0] != null) {
      list.addFirst(objects[0]);
    }
    return list;
  }
}
