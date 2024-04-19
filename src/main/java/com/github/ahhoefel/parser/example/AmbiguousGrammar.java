package com.github.ahhoefel.parser.example;

import com.github.ahhoefel.parser.LRParser;
import com.github.ahhoefel.parser.ShiftReduceResolver;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.lang.LanguageComponent;
import com.github.ahhoefel.parser.lang.LexicalMapping;
import com.github.ahhoefel.parser.lang.RangeEmitter;
import com.github.ahhoefel.parser.lang.Rule;
import com.github.ahhoefel.parser.lang.RuleEmitter;
import com.github.ahhoefel.parser.lang.SymbolProvider;

public class AmbiguousGrammar extends LRParser {

    public AmbiguousGrammar() {
        super(new LetterAMapping(), "S",
                new ReduceAmbiguousComponent());
    }

    public static void main(String[] args) {
        try {
            new LRParser(new LetterAMapping(), "S",
                    new ReduceAmbiguousComponent());
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            new LRParser(new LetterAMapping(), "S",
                    new ShiftReduceAmbiguousComponent());
        } catch (Exception e) {
            System.out.println(e);
        }

        LRParser parser = new LRParser(new LetterAAndPlusMapping(), "S",
                new ShiftReduceFixableComponent());
        System.out.println(parser.parse("a+a+a"));
    }

    public static class ReduceAmbiguousComponent implements LanguageComponent {
        @Override
        public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter rules) {
            Symbol symbolS = provider.createAndExport("S");
            Symbol symbolA = provider.create("A");
            Symbol letterA = provider.require("letterA");
            rules.emit(symbolS, symbolA);
            rules.emit(symbolS, letterA);
            rules.emit(symbolA, letterA);
        }
    }

    public static class ShiftReduceAmbiguousComponent implements LanguageComponent {
        @Override
        public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter rules) {
            Symbol symbolS = provider.createAndExport("S");
            Symbol letterA = provider.require("letterA");
            rules.emit(symbolS, letterA, symbolS, letterA);
            rules.emit(symbolS);
        }
    }

    public static class ShiftReduceFixableComponent implements LanguageComponent {
        @Override
        public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter rules) {
            Symbol symbolS = provider.createAndExport("S");
            Symbol plus = provider.require("plus");
            Symbol letterA = provider.require("letterA");
            rules.emit(symbolS, symbolS, plus, symbolS);
            rules.emit(symbolS, letterA);
        }
    }

    public static class LetterAMapping implements LexicalMapping {
        public void provideRanges(RangeEmitter range) {
            range.map('a', "letterA");
        }
    }

    public static class LetterAAndPlusMapping implements LexicalMapping {
        public void provideRanges(RangeEmitter range) {
            range.map('a', "letterA");
            range.map('+', "plus");
        }
    }
}
