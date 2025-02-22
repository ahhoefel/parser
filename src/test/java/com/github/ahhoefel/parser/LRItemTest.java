package com.github.ahhoefel.parser;

import com.github.ahhoefel.parser.lang.Rule;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

public class LRItemTest {

    @Test
    public void testClosure() {
        SymbolTable.NonTerminalTable nonTerminals = new SymbolTable.NonTerminalTable();
        Symbol start = nonTerminals.getStart();
        Symbol a = nonTerminals.newSymbol("A");
        Symbol b = nonTerminals.newSymbol("B");

        SymbolTable.TerminalTable terminals = new SymbolTable.TerminalTable();
        Symbol x = terminals.newSymbol("x");
        Symbol y = terminals.newSymbol("y");
        Symbol z = terminals.newSymbol("z");

        Rule r0 = new Rule(start, List.of(a, b));
        Rule r1 = new Rule(a, List.of(x, a, b));
        Rule r2 = new Rule(a, List.of());
        Rule r3 = new Rule(b, List.of(y));
        Rule r4 = new Rule(b, List.of(z));
        Grammar grammar = new Grammar(terminals, nonTerminals, List.of(r0, r1, r2, r3, r4), null);
        Grammar.FirstSymbols first = grammar.first();
        Set<MarkedRule> closure = LRItem.closure(new MarkedRule(r0, 0, terminals.getEof()), grammar, first);
        Assert.assertEquals(closure, Set.of(new MarkedRule(r0, 0, terminals.getEof()), new MarkedRule(r1, 0, y),
                new MarkedRule(r1, 0, z), new MarkedRule(r2, 0, y), new MarkedRule(r2, 0, z)));
    }

    @Test
    public void testClosureSeed() {
        SymbolTable.NonTerminalTable nonTerminals = new SymbolTable.NonTerminalTable();
        Symbol start = nonTerminals.getStart();
        Symbol t = nonTerminals.newSymbol("T");
        Symbol f = nonTerminals.newSymbol("F");

        SymbolTable.TerminalTable terminals = new SymbolTable.TerminalTable();
        Symbol plus = terminals.newSymbol("+");
        Symbol times = terminals.newSymbol("*");
        Symbol n = terminals.newSymbol("n");
        Symbol lparen = terminals.newSymbol("(");
        Symbol rparen = terminals.newSymbol(")");

        Rule r1 = new Rule(start, List.of(t));
        Rule r2 = new Rule(start, List.of(start, plus, t));
        Rule r3 = new Rule(t, List.of(f));
        Rule r4 = new Rule(t, List.of(t, times, f));
        Rule r5 = new Rule(f, List.of(n));
        Rule r6 = new Rule(f, List.of(lparen, start, rparen));
        Grammar grammar = new Grammar(terminals, nonTerminals, List.of(r1, r2, r3, r4, r5, r6), null);
        Grammar.FirstSymbols first = grammar.first();

        Set<MarkedRule> closure = LRItem.closure(new MarkedRule(r6, 1, plus), grammar, first);
        Assert.assertEquals(closure,
                Set.of(new MarkedRule(r1, 0, plus), new MarkedRule(r1, 0, rparen), new MarkedRule(r2, 0, plus),
                        new MarkedRule(r2, 0, rparen), new MarkedRule(r3, 0, plus), new MarkedRule(r3, 0, times),
                        new MarkedRule(r3, 0, rparen), new MarkedRule(r4, 0, plus), new MarkedRule(r4, 0, times),
                        new MarkedRule(r4, 0, rparen), new MarkedRule(r5, 0, plus), new MarkedRule(r5, 0, times),
                        new MarkedRule(r5, 0, rparen), new MarkedRule(r6, 0, plus), new MarkedRule(r6, 0, times),
                        new MarkedRule(r6, 0, rparen), new MarkedRule(r6, 1, plus)));
        // [start => ^ T , )],
        // [start => ^ T , +]
        // [start => ^ start + T , )],
        // [start => ^ start + T , +],
        // [T => ^ F , )],
        // [T => ^ F , +],
        // [T => ^ F , *],
        // [T => ^ T * F , )],
        // [T => ^ T * F , *],
        // [T => ^ T * F , +],
        // [F => ^ n , +],
        // [F => ^ n , *],
        // [F => ^ n , )],
        // [F => ( ^ start ) , +],
        // [F => ^ ( start ) , )],
        // [F => ^ ( start ) , *],
        // [F => ^ ( start ) , +],

        closure = LRItem.closure(Set.of(new MarkedRule(r1, 1, terminals.getEof())), grammar, first);
        Assert.assertEquals(closure, Set.of(new MarkedRule(r1, 1, terminals.getEof())));
    }
}
