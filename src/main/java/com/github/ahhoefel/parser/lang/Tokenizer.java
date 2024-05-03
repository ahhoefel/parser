package com.github.ahhoefel.parser.lang;

import java.util.Optional;

import com.github.ahhoefel.parser.ErrorLog;
import com.github.ahhoefel.parser.LocateableList;
import com.github.ahhoefel.parser.SymbolTable.TerminalTable;
import com.github.ahhoefel.parser.Token;
import com.github.ahhoefel.parser.io.CodeLocation;
import com.github.ahhoefel.parser.io.Target;

public interface Tokenizer {
    Token of(int character, CodeLocation location);

    TerminalTable getTerminals();

    public static LocateableList<Token> tokenize(Tokenizer tokenizer, Optional<Target> target, String input,
            ErrorLog log) {
        LocateableList<Token> list = new LocateableList<>();
        CodeLocation location = new CodeLocation(target, input.length());
        list.setLocation(location);
        for (int i = 0; i < input.length(); i++) {
            list.add(tokenizer.of(input.charAt(i), new CodeLocation(target, 0, i, i)));
        }
        return list;
    }
}
