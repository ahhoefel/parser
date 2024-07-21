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

    public LayeredGrammar() {
        super(LocateableList.class, new TerminalLayeredParser(new CharacterMapping()), "start",
                new OneOrMoreComponent("word", "alpha", Optional.of(ConcatAction.SINGLETON)),
                new OneOrMoreComponent("whitespace", "space", Optional.of(ConcatAction.SINGLETON)),
                new OrComponent("token", "period", "word", "whitespace"),
                new OneOrMoreComponent("start", "token", Optional.of(AppendAction.SINGLETON)),
                new PeriodComponent());
    }

    public static class OrComponent implements LanguageComponent {
        private String[] symbols;
        private String symbol;

        public OrComponent(String symbol, String... symbols) {
            this.symbols = symbols;
            this.symbol = symbol;
        }

        @Override
        public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter emit) {
            Symbol symbol = provider.createAndExport(this.symbol);
            for (String s : this.symbols) {
                Symbol orComponent = provider.require(s);
                Rule orRule = emit.emit(symbol, orComponent);
                orRule.setAction(e -> e[0]);
            }
        }
    }

    public static class OneOrMoreComponent implements LanguageComponent {
        private String symbolName;
        private String characterName;
        private Optional<Function<Locateable[], Locateable>> action;

        public OneOrMoreComponent(String symbolName, String characterName,
                Optional<Function<Locateable[], Locateable>> action) {
            this.symbolName = symbolName;
            this.characterName = characterName;
            this.action = action;
        }

        @Override
        public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter rules) {
            Symbol symbol = provider.createAndExport(symbolName);
            Symbol character = provider.require(characterName);
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
            Symbol dot = provider.require("dot");
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
