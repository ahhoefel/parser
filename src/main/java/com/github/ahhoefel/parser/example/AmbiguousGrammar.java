package com.github.ahhoefel.parser.example;

import java.util.Iterator;
import java.util.Optional;

import com.github.ahhoefel.parser.LayeredParser;
import com.github.ahhoefel.parser.ShiftReduceResolver;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.Token;
import com.github.ahhoefel.parser.lang.LanguageComponent;
import com.github.ahhoefel.parser.lang.LexicalMapping;
import com.github.ahhoefel.parser.lang.RangeEmitter;
import com.github.ahhoefel.parser.lang.RuleEmitter;
import com.github.ahhoefel.parser.lang.SymbolProvider;

public class AmbiguousGrammar extends LayeredParser.Layer<Iterator<Token<String>>, String> {

    public AmbiguousGrammar(LanguageComponent component) {
        super(String.class, new TerminalLayeredParser(new LetterAMapping()), "S",
                component);
    }

    public static void main(String[] args) {
        try {
            new AmbiguousGrammar(new ReduceAmbiguousComponent());
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            new AmbiguousGrammar(new ShiftReduceAmbiguousComponent());

        } catch (Exception e) {
            System.out.println(e);
        }

        LayeredParser<String> parser = new Layer<Iterator<Token<String>>, String>(String.class,
                new TerminalLayeredParser(new LetterAAndPlusMapping()), "S",
                new ShiftReduceFixableComponent());
        System.out.println(parser.parse(Optional.empty(), "a+a+a"));
    }

    public static class ReduceAmbiguousComponent implements LanguageComponent {
        @Override
        public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter rules) {
            Symbol symbolS = provider.createAndExport("S");
            Symbol symbolA = provider.create("A");
            Symbol letterA = provider.requireTerminal("letterA");
            rules.emit(symbolS, symbolA);
            rules.emit(symbolS, letterA);
            rules.emit(symbolA, letterA);
        }
    }

    public static class ShiftReduceAmbiguousComponent implements LanguageComponent {
        @Override
        public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter rules) {
            Symbol symbolS = provider.createAndExport("S");
            Symbol letterA = provider.requireTerminal("letterA");
            rules.emit(symbolS, letterA, symbolS, letterA);
            rules.emit(symbolS);
        }
    }

    public static class ShiftReduceFixableComponent implements LanguageComponent {
        @Override
        public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter rules) {
            Symbol symbolS = provider.createAndExport("S");
            Symbol plus = provider.requireTerminal("plus");
            Symbol letterA = provider.requireTerminal("letterA");
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
