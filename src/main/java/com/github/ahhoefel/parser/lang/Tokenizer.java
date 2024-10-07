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
        int lineNumber = 0;
        int charNumber = 0;
        for (int i = 0; i < input.length(); i++) {
            list.add(tokenizer.of(input.charAt(i), new CodeLocation(target, lineNumber, charNumber++, i)));
            if (input.charAt(i) == '\n') {
                lineNumber++;
                charNumber = 0;
            }
        }
        return list;
    }
}
