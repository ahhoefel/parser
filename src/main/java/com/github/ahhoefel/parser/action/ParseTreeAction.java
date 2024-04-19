package com.github.ahhoefel.parser.action;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.github.ahhoefel.parser.Locateable;
import com.github.ahhoefel.parser.Token;
import com.github.ahhoefel.parser.lang.Rule;

/**
 * Parses into a generic tree structure.
 * 
 * This is the default action on rules. @see ParseTree.
 */

public class ParseTreeAction implements Function<Locateable[], Locateable> {

    private Rule rule;

    public ParseTreeAction(Rule rule) {
        this.rule = rule;
    }

    @Override
    public Locateable apply(Locateable[] objects) {
        List<ParseTree> children = new ArrayList<>();
        for (Object o : objects) {
            if (o instanceof Token) {
                children.add(new ParseTree((Token) o));
            } else if (o instanceof ParseTree) {
                children.add((ParseTree) o);
            } else {
                throw new RuntimeException(String.format(
                        "Cannot build parse tree from %s: expected Token or ParseTree. Likely an action hasn't been specified on a parse rule.",
                        o.getClass().toString()));
            }
        }
        return new ParseTree(rule, children);
    }
}