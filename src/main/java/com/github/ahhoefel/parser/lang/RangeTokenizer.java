package com.github.ahhoefel.parser.lang;

import java.util.Optional;

import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.Token;
import com.github.ahhoefel.parser.SymbolTable.TerminalTable;
import com.github.ahhoefel.parser.io.CodeLocation;

public class RangeTokenizer implements RangeEmitter, Tokenizer {

    private Symbol[] charTerminalMap;
    private TerminalTable terminals;
    private Symbol eof;
    private Symbol unknown;

    public RangeTokenizer() {
        this.charTerminalMap = new Symbol[256];
        this.terminals = new TerminalTable();
        eof = this.terminals.newSymbol("eof");
        unknown = this.terminals.newSymbol("unknown");
    }

    @Override
    public Symbol map(char c, String label) {
        return this.map(c, c, label);
    }

    @Override
    public Symbol map(char from, char to, String label) {
        Optional<Symbol> optSymbol = terminals.getSymbolByLabel(label);
        Symbol symbol;
        if (optSymbol.isPresent()) {
            symbol = optSymbol.get();
        } else {
            symbol = terminals.newSymbol(label);
        }

        for (int i = (int) from; i <= (int) to; i++) {
            charTerminalMap[i] = symbol;
        }
        return symbol;
    }

    public TerminalTable getTerminals() {
        return terminals;
    }

    public Token of(int c, CodeLocation location) {
        Symbol symbol;
        String value;
        if (c < 0) {
            symbol = eof;
            value = "eof";

        } else if (c >= charTerminalMap.length) {
            symbol = unknown;
            value = Character.toString((char) c);
        } else {
            symbol = charTerminalMap[c];
            if (symbol == null) {
                symbol = unknown;
            }
            value = Character.toString((char) c);
        }
        return new Token(symbol, value, location);
    }
}
