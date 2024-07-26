package com.github.ahhoefel.parser.lang;

import com.github.ahhoefel.parser.Symbol;

// Provides access to symbols from external components by their string name.
public interface SymbolProvider {
    Symbol require(String symbol);

    Symbol requireTerminal(String symbol);

    Symbol createAndExport(String symbol);

    Symbol create(String symbol);
}