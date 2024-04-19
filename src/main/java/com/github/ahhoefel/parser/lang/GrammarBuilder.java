package com.github.ahhoefel.parser.lang;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;

import com.github.ahhoefel.parser.Grammar;
import com.github.ahhoefel.parser.ShiftReduceResolver;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.SymbolTable;

public class GrammarBuilder {
    private SymbolTable.NonTerminalTable nonTerminals;
    private List<Rule> rules;
    private ShiftReduceResolver resolver;
    private RangeTokenizer tokenizer;

    private Map<String, Symbol> notProvided;

    private GrammarBuilder() {
        resolver = new ShiftReduceResolver();
        rules = new ArrayList<>();
        nonTerminals = new SymbolTable.NonTerminalTable();
        tokenizer = new RangeTokenizer();
        notProvided = new HashMap<>();
    }

    public ShiftReduceResolver getResolver() {
        return resolver;
    }

    public SymbolTable.NonTerminalTable getNonTerminals() {
        return nonTerminals;
    }

    private RuleEmitter getRuleEmitter() {
        return new RuleEmitter() {
            public Rule emit(Symbol from, Symbol... to) {
                Rule r = new Rule(from, Arrays.asList(to));
                rules.add(r);
                return r;
            }
        };
    }

    private SymbolProvider getSymbolProvider() {
        return new SymbolProvider() {
            @Override
            public Symbol require(String label) {
                Optional<Symbol> optSymbol = nonTerminals.getSymbolByLabel(label);
                if (optSymbol.isPresent()) {
                    return optSymbol.get();
                }
                optSymbol = tokenizer.getTerminals().getSymbolByLabel(label);
                if (optSymbol.isPresent()) {
                    return optSymbol.get();
                }
                Symbol symbol = nonTerminals.newSymbol(label);
                notProvided.put(label, symbol);
                return symbol;
            }

            @Override
            public Symbol createAndExport(String label) {
                Symbol symbol;
                if (notProvided.containsKey(label)) {
                    symbol = notProvided.get(label);
                    notProvided.remove(label);
                } else {
                    symbol = nonTerminals.newSymbol(label);
                }
                return symbol;
            }

            @Override
            public Symbol create(String label) {
                return nonTerminals.newSymbol(label);
            }
        };
    }

    public static Grammar build(LexicalMapping lex, String start, LanguageComponent... components) {
        GrammarBuilder grammar = new GrammarBuilder();
        lex.provideRanges(grammar.tokenizer);
        for (LanguageComponent component : components) {
            component.provideRules(grammar.getSymbolProvider(), grammar.getResolver(), grammar.getRuleEmitter());
        }
        Symbol startSymbol = grammar.getSymbolProvider().require(start);
        if (!grammar.notProvided.isEmpty()) {
            String labels = String.join(", ", grammar.notProvided.keySet());
            throw new RuntimeException(String.format("Symbols [%s] required, but not provided.", labels));
        }
        if (startSymbol == null) {
            throw new RuntimeException(String.format("Start symbol %s not provided.", start));
        }
        grammar.getRuleEmitter().emit(grammar.nonTerminals.getStart(), startSymbol).setAction(e -> e[0]);
        Grammar g = new Grammar(grammar.tokenizer, grammar.nonTerminals, grammar.rules,
                grammar.resolver);
        return g;
    }
}
