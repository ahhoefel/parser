package com.github.ahhoefel.parser;

import java.util.Iterator;
import java.util.Optional;

import com.github.ahhoefel.parser.SymbolTable.TerminalTable;
import com.github.ahhoefel.parser.io.Target;
import com.github.ahhoefel.parser.lang.LanguageComponent;
import com.github.ahhoefel.parser.lang.LexicalMapping;
import com.github.ahhoefel.parser.lang.RangeTokenizer;
import com.github.ahhoefel.parser.lang.Tokenizer;

public interface LayeredParser<T> {

    public SymbolTable.TerminalTable getSymbols();

    default T parse(String s) {
        return parse(Optional.empty(), s);
    }

    default T parse(Target t, String s) {
        return parse(Optional.of(t), s);
    }

    public T parse(Optional<Target> t, String s);

    public static class TerminalLayeredParser implements LayeredParser<Iterator<Token<String>>> {
        private RangeTokenizer tokenizer;

        public TerminalLayeredParser(LexicalMapping lex) {
            this.tokenizer = new RangeTokenizer();
            lex.provideRanges(tokenizer);
        }

        public Iterator<Token<String>> parse(Optional<Target> target, String s) {
            ErrorLog log = new ErrorLog();
            Iterator<Token<String>> tokens = Tokenizer.tokenize(tokenizer, target, s, log).iterator();
            if (!log.isEmpty()) {
                throw new ParseException(log);
            }
            return tokens;
        }

        @Override
        public TerminalTable getSymbols() {
            return tokenizer.getTerminals();
        }
    }

    public static class Layer<S, T> implements LayeredParser<T> {
        private Class<T> clazz;
        private LayeredParser<S> parent;
        private LRParser parser;

        public Layer(Class<T> clazz, LayeredParser<S> parent, LRParser parser) {
            this.clazz = clazz;
            this.parent = parent;
            this.parser = parser;
        }

        public Layer(Class<T> clazz, LayeredParser<S> parent, String startSymbol, LanguageComponent... components) {
            this.clazz = clazz;
            this.parent = parent;
            parser = new LRParser(parent.getSymbols(), startSymbol, components);
        }

        @Override
        public TerminalTable getSymbols() {
            return parser.getNonTerminals();
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public T parse(Optional<Target> target, String s) {
            S result = parent.parse(target, s);
            System.out.println(result);
            return parser.parse((Iterator<Token>) result, clazz);
        }
    }

}