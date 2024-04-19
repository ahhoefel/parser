package com.github.ahhoefel.parser.lang;

import com.github.ahhoefel.parser.Symbol;

public interface RuleEmitter {
    // Adds new rules to the language and returns the rule for other uses.
    Rule emit(Symbol from, Symbol... to);
}
