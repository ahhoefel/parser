package com.github.ahhoefel.parser.example;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;

import com.github.ahhoefel.parser.LayeredParser;
import com.github.ahhoefel.parser.Locateable;
import com.github.ahhoefel.parser.LocateableList;
import com.github.ahhoefel.parser.ShiftReduceResolver;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.Token;
import com.github.ahhoefel.parser.action.AppendAction;
import com.github.ahhoefel.parser.action.ConcatAction;
import com.github.ahhoefel.parser.lang.LanguageComponent;
import com.github.ahhoefel.parser.lang.LexicalMapping;
import com.github.ahhoefel.parser.lang.RangeEmitter;
import com.github.ahhoefel.parser.lang.Rule;
import com.github.ahhoefel.parser.lang.RuleEmitter;
import com.github.ahhoefel.parser.lang.SymbolProvider;

@SuppressWarnings("rawtypes")
public class LayeredGrammar extends LayeredParser.Layer<Iterator<Token<String>>, LocateableList> {

    public static class SymbolName {
        private String name;
        private boolean isTerminal;

        private SymbolName(String name, boolean isTerminal) {
            this.name = name;
            this.isTerminal = isTerminal;
        }

        public static SymbolName terminal(String name) {
            return new SymbolName(name, true);
        }

        public static SymbolName nonTerminal(String name) {
            return new SymbolName(name, false);
        }
    }

    public LayeredGrammar() {
        super("LayeredGrammar", LocateableList.class, new TerminalLayeredParser(new CharacterMapping()), "start",
                new OneOrMoreComponent("word", SymbolName.terminal("alpha"), Optional.of(ConcatAction.SINGLETON)),
                new OneOrMoreComponent("whitespace", SymbolName.terminal("space"), Optional.of(ConcatAction.SINGLETON)),
                new OrComponent("token", SymbolName.nonTerminal("period"), SymbolName.nonTerminal("word"),
                        SymbolName.nonTerminal("whitespace")),
                new OneOrMoreComponent("start", SymbolName.nonTerminal("token"), Optional.of(AppendAction.SINGLETON)),
                new PeriodComponent());
    }

    public static class OrComponent implements LanguageComponent {
        private SymbolName[] symbols;
        private String symbol;

        public OrComponent(String symbol, SymbolName... symbols) {
            this.symbols = symbols;
            this.symbol = symbol;
        }

        @Override
        public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter emit) {
            Symbol symbol = provider.createAndExport(this.symbol);
            for (SymbolName s : this.symbols) {
                Symbol orComponent;
                if (s.isTerminal) {
                    orComponent = provider.requireTerminal(s.name);
                } else {
                    orComponent = provider.require(s.name);
                }
                Rule orRule = emit.emit(symbol, orComponent);
                orRule.setAction(e -> e[0]);
            }
        }
    }

    public static class OneOrMoreComponent implements LanguageComponent {
        private String symbolName;
        private SymbolName characterName;
        private Optional<Function<Locateable[], Locateable>> action;

        public OneOrMoreComponent(String symbolName, SymbolName characterName,
                Optional<Function<Locateable[], Locateable>> action) {
            this.symbolName = symbolName;
            this.characterName = characterName;
            this.action = action;
        }

        @Override
        public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter rules) {
            Symbol symbol = provider.createAndExport(symbolName);

            Symbol character;
            if (characterName.isTerminal) {
                character = provider.requireTerminal(characterName.name);
            } else {
                character = provider.require(characterName.name);
            }
            Rule rule = rules.emit(symbol, character, symbol);
            if (action.isPresent()) {
                rule.setAction(action.get());
            }
            Rule terminalRule = rules.emit(symbol, character);
            if (action.isPresent()) {
                terminalRule.setAction(action.get());
            }
            resolver.addShiftPreference(terminalRule, character);
        }
    }

    public static class PeriodComponent implements LanguageComponent {
        @Override
        public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter rules) {
            Symbol period = provider.createAndExport("period");
            Symbol dot = provider.requireTerminal("dot");
            Rule rule = rules.emit(period, dot);
            rule.setAction(ConcatAction.SINGLETON);
        }
    }

    public static class CharacterMapping implements LexicalMapping {
        public void provideRanges(RangeEmitter range) {
            range.map('A', 'Z', "alpha");
            range.map('a', 'z', "alpha");
            range.map(' ', "space");
            range.map('.', "dot");
        }
    }
}
