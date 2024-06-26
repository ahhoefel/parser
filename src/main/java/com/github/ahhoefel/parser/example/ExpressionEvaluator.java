package com.github.ahhoefel.parser.example;

import java.util.List;

import com.github.ahhoefel.parser.LRParser;
import com.github.ahhoefel.parser.Locateable;
import com.github.ahhoefel.parser.ShiftReduceResolver;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.Token;
import com.github.ahhoefel.parser.action.ConcatAction;
import com.github.ahhoefel.parser.io.CodeLocation;
import com.github.ahhoefel.parser.lang.LanguageComponent;
import com.github.ahhoefel.parser.lang.LexicalMapping;
import com.github.ahhoefel.parser.lang.RangeEmitter;
import com.github.ahhoefel.parser.lang.Rule;
import com.github.ahhoefel.parser.lang.RuleEmitter;
import com.github.ahhoefel.parser.lang.SymbolProvider;

public class ExpressionEvaluator extends LRParser {

    public ExpressionEvaluator() {
        super(new CharacterMapping(), "expression",
                new OperatorComponent(), new ValueComponent());
    }

    public static void main(String[] args) {
        ExpressionEvaluator parser = new ExpressionEvaluator();
        System.out.println(parser.toString());
        System.out.println(parser.parse("3+4*5"));
    }

    private static class Pair<S, T> {
        private S first;
        private T second;

        public Pair(S s, T t) {
            first = s;
            second = t;
        }
    }

    public static class LocateableInteger implements Locateable {

        private CodeLocation location;
        private int value;

        public LocateableInteger(int value, CodeLocation location) {
            this.value = value;
            this.location = location;
        }

        public int getValue() {
            return value;
        }

        @Override
        public CodeLocation getLocation() {
            return location;
        }

        @Override
        public void setLocation(CodeLocation location) {
            this.location = location;
        }

        public LocateableInteger plus(LocateableInteger other) {
            return new LocateableInteger(value + other.value, new CodeLocation(this.location, other.location));
        }

        public LocateableInteger times(LocateableInteger other) {
            return new LocateableInteger(value * other.value, new CodeLocation(this.location, other.location));
        }

        public LocateableInteger minus(LocateableInteger other) {
            return new LocateableInteger(value - other.value, new CodeLocation(this.location, other.location));
        }

        public String toString() {
            return Integer.toString(value);
        }
    }

    public static class OperatorComponent implements LanguageComponent {
        @Override
        public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter rules) {
            Symbol expression = provider.createAndExport("expression");
            Symbol number = provider.require("number");
            Symbol lparen = provider.require("lparen");
            Symbol rparen = provider.require("rparen");
            Symbol plus = provider.require("plus");
            Symbol minus = provider.require("minus");
            Symbol times = provider.require("times");
            Rule plusRule = rules.emit(expression, expression, plus, expression)
                    .setAction(e -> ((LocateableInteger) e[0]).plus((LocateableInteger) e[2]));
            Rule minusRule = rules.emit(expression, expression, minus, expression)
                    .setAction(e -> ((LocateableInteger) e[0]).minus((LocateableInteger) e[2]));
            Rule timesRule = rules.emit(expression, expression, times, expression)
                    .setAction(e -> ((LocateableInteger) e[0]).times((LocateableInteger) e[2]));
            rules.emit(expression, lparen, expression, rparen)
                    .setAction(e -> e[1]);
            rules.emit(expression, number)
                    .setAction(e -> new LocateableInteger(
                            Integer.parseInt(((Token) e[0]).getValue(), 10),
                            e[0].getLocation()));

            List<List<Pair<Rule, Symbol>>> precendence = List.of(
                    List.of(new Pair<>(timesRule, times)),
                    List.of(new Pair<>(plusRule, plus), new Pair<>(minusRule, minus)));

            for (int i = 0; i < precendence.size(); i++) {
                List<Pair<Rule, Symbol>> group = precendence.get(i);
                for (int j = 0; j < group.size(); j++) {
                    Pair<Rule, Symbol> pair = group.get(j);
                    for (int x = 0; x < precendence.size(); x++) {
                        List<Pair<Rule, Symbol>> groupB = precendence.get(x);
                        for (int y = 0; y < groupB.size(); y++) {
                            Pair<Rule, Symbol> pairB = groupB.get(y);
                            if (i <= x) {
                                resolver.addReducePreference(pair.first, pairB.second);
                            } else {
                                resolver.addShiftPreference(pair.first, pairB.second);
                            }
                        }
                    }
                }
            }
        }
    }

    public static class ValueComponent implements LanguageComponent {
        @Override
        public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter rules) {
            Symbol number = provider.createAndExport("number");
            Symbol numeral = provider.require("numeral");
            rules.emit(number, numeral, number).setAction(ConcatAction.SINGLETON);
            rules.emit(number, numeral).setAction(ConcatAction.SINGLETON);
        }
    }

    public static class CharacterMapping implements LexicalMapping {
        public void provideRanges(RangeEmitter range) {
            range.map('0', '9', "numeral");
            range.map('(', "lparen");
            range.map(')', "rparen");
            range.map('+', "plus");
            range.map('-', "minus");
            range.map('*', "times");
        }
    }
}
