package com.github.ahhoefel.parser.lang;

import com.github.ahhoefel.parser.Symbol;

public interface RangeEmitter {

    Symbol map(char c, String label);

    Symbol map(char from, char to, String label);

}
