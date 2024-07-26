package com.github.ahhoefel.parser.example;

import java.util.Iterator;

import com.github.ahhoefel.parser.LayeredParser;
import com.github.ahhoefel.parser.ShiftReduceResolver;
import com.github.ahhoefel.parser.Symbol;
import com.github.ahhoefel.parser.Token;
import com.github.ahhoefel.parser.action.ConcatAction;
import com.github.ahhoefel.parser.action.ParseTree;
import com.github.ahhoefel.parser.lang.LanguageComponent;
import com.github.ahhoefel.parser.lang.LexicalMapping;
import com.github.ahhoefel.parser.lang.RangeEmitter;
import com.github.ahhoefel.parser.lang.Rule;
import com.github.ahhoefel.parser.lang.RuleEmitter;
import com.github.ahhoefel.parser.lang.SymbolProvider;

public class BalancedParser extends LayeredParser.Layer<Iterator<Token<String>>, ParseTree> {

    public BalancedParser() {
        super(ParseTree.class, new TerminalLayeredParser(new AlphanumericAndParenMapping()), "balanced",
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

            Symbol alphanumeric = provider.requireTerminal("alphanumeric");
            Symbol lparen = provider.requireTerminal("lparen");
            resolver.addShiftPreference(concat, alphanumeric);
            resolver.addShiftPreference(concat, lparen);
        }
    }

    public static class BracketComponent implements LanguageComponent {
        @Override
        public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter rules) {
            Symbol balanced = provider.require("balanced");
            Symbol identifier = provider.require("identifier");
            Symbol lparen = provider.requireTerminal("lparen");
            Symbol rparen = provider.requireTerminal("rparen");
            rules.emit(balanced, lparen, balanced, rparen);
            rules.emit(balanced, identifier);
        }
    }

    public static class IdentifierComponent implements LanguageComponent {
        @Override
        public void provideRules(SymbolProvider provider, ShiftReduceResolver resolver, RuleEmitter rules) {
            Symbol identifier = provider.createAndExport("identifier");
            Symbol alphanumeric = provider.requireTerminal("alphanumeric");
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
