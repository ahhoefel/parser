package com.github.ahhoefel.parser.example;

import com.github.ahhoefel.parser.LRParser;
import com.github.ahhoefel.parser.ShiftReduceResolver;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.action.ConcatAction;
import com.github.ahhoefel.parser.lang.LanguageComponent;
import com.github.ahhoefel.parser.lang.LexicalMapping;
import com.github.ahhoefel.parser.lang.RangeEmitter;
import com.github.ahhoefel.parser.lang.Rule;
import com.github.ahhoefel.parser.lang.RuleEmitter;
import com.github.ahhoefel.parser.lang.SymbolProvider;

public class BalancedParser extends LRParser {

    public BalancedParser() {
        super(new AlphanumericAndParenMapping(), "balanced",
                new StartComponent(), new BracketComponent(), new IdentifierComponent());
    }

    public static void main(String[] args) {
        BalancedParser parser = new BalancedParser();
        System.out.println(parser.toString());
        System.out.println(parser.parse("(a1)((b)c)"));
    }

    public static class StartComponent implements LanguageComponent {
        @Override
        public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter rules) {
            Symbol balanced = provider.createAndExport("balanced");
            Rule concat = rules.emit(balanced, balanced, balanced);

            Symbol alphanumeric = provider.require("alphanumeric");
            Symbol lparen = provider.require("lparen");
            resolver.addShiftPreference(concat, alphanumeric);
            resolver.addShiftPreference(concat, lparen);
        }
    }

    public static class BracketComponent implements LanguageComponent {
        @Override
        public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter rules) {
            Symbol balanced = provider.require("balanced");
            Symbol identifier = provider.require("identifier");
            Symbol lparen = provider.require("lparen");
            Symbol rparen = provider.require("rparen");
            rules.emit(balanced, lparen, balanced, rparen);
            rules.emit(balanced, identifier);
        }
    }

    public static class IdentifierComponent implements LanguageComponent {
        @Override
        public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter rules) {
            Symbol identifier = provider.createAndExport("identifier");
            Symbol alphanumeric = provider.require("alphanumeric");
            Rule identifierRule = rules.emit(identifier, alphanumeric, identifier);
            identifierRule.setAction(ConcatAction.SINGLETON);
            Rule alpha = rules.emit(identifier, alphanumeric);
            alpha.setAction(ConcatAction.SINGLETON);
            resolver.addShiftPreference(alpha, alphanumeric);
        }
    }

    public static class AlphanumericAndParenMapping implements LexicalMapping {
        public void provideRanges(RangeEmitter range) {
            range.map('A', 'Z', "alphanumeric");
            range.map('a', 'z', "alphanumeric");
            range.map('0', '9', "alphanumeric");
            range.map('(', "lparen");
            range.map(')', "rparen");
        }
    }
}
