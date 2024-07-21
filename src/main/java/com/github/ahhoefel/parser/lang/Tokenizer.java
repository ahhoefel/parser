package com.github.ahhoefel.parser.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.ahhoefel.parser.ErrorLog;
import com.github.ahhoefel.parser.SymbolTable.TerminalTable;
import com.github.ahhoefel.parser.io.CodeLocation;
import com.github.ahhoefel.parser.io.Target;

public interface Tokenizer<T> {
    T of(int character, CodeLocation location);

    T eof();

    TerminalTable getTerminals();

    public static <T> List<T> tokenize(Tokenizer<T> tokenizer, Optional<Target> target, String input,
            ErrorLog log) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < input.length(); i++) {
            list.add(tokenizer.of(input.charAt(i), new CodeLocation(target, 0, i, i)));
        }
        list.add(tokenizer.eof());
        return list;
    }
}
