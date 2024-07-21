package com.github.ahhoefel.parser.example;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.github.ahhoefel.parser.ParseException;
import com.github.ahhoefel.parser.io.ParseError;

public class BalancedParserTest {

    private BalancedParser parser;

    public BalancedParserTest() {
        this.parser = new BalancedParser();
    }

    @Test
    public void testEmpty() {
        boolean exceptionThrown = false;
        try {
            parser.parse("");
        } catch (ParseException e) {
            exceptionThrown = true;
            List<ParseError> errors = e.getErrorLog().getErrors();
            Assert.assertEquals(1, errors.size());
            String expected = String.join("\n",
                    "no location ",
                    "Parsing error.",
                    "Next token: eof(eof) @ null",
                    "State: (Symbol: startp, State: 0)",
                    "Stack (top):",
                    "(bottom)");
            Assert.assertEquals(expected, errors.get(0).toString());
        }
        Assert.assertTrue(exceptionThrown);
    }

    @Test
    public void testSimple() {
        String result = parser.parse("(a1)").toString();
        String expected = String.join("\n",
                "Rule: balanced ⟹  lparen balanced rparen",
                "0. Token: lparen(() @ :1:1:1",
                "1. Rule: balanced ⟹  identifier",
                "1. 0. Token: alphanumeric(a1) @ :1:2:2",
                "2. Token: rparen()) @ :1:4:1\n");
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testConcat() {
        String result = parser.parse("(a)(b)").toString();
        String expected = String.join("\n",
                "Rule: balanced ⟹  balanced balanced",
                "0. Rule: balanced ⟹  lparen balanced rparen",
                "0. 0. Token: lparen(() @ :1:1:1",
                "0. 1. Rule: balanced ⟹  identifier",
                "0. 1. 0. Token: alphanumeric(a) @ :1:2:1",
                "0. 2. Token: rparen()) @ :1:3:1",
                "1. Rule: balanced ⟹  lparen balanced rparen",
                "1. 0. Token: lparen(() @ :1:4:1",
                "1. 1. Rule: balanced ⟹  identifier",
                "1. 1. 0. Token: alphanumeric(b) @ :1:5:1",
                "1. 2. Token: rparen()) @ :1:6:1\n");
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testNested() {
        String result = parser.parse("((a))(b)").toString();
        String expected = String.join("\n",
                "Rule: balanced ⟹  balanced balanced",
                "0. Rule: balanced ⟹  lparen balanced rparen",
                "0. 0. Token: lparen(() @ :1:1:1",
                "0. 1. Rule: balanced ⟹  lparen balanced rparen",
                "0. 1. 0. Token: lparen(() @ :1:2:1",
                "0. 1. 1. Rule: balanced ⟹  identifier",
                "0. 1. 1. 0. Token: alphanumeric(a) @ :1:3:1",
                "0. 1. 2. Token: rparen()) @ :1:4:1",
                "0. 2. Token: rparen()) @ :1:5:1",
                "1. Rule: balanced ⟹  lparen balanced rparen",
                "1. 0. Token: lparen(() @ :1:6:1",
                "1. 1. Rule: balanced ⟹  identifier",
                "1. 1. 0. Token: alphanumeric(b) @ :1:7:1",
                "1. 2. Token: rparen()) @ :1:8:1\n");
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testUnbalanced() {
        boolean exceptionThrown = false;
        try {
            parser.parse("((a)");
        } catch (ParseException e) {
            exceptionThrown = true;
            List<ParseError> errors = e.getErrorLog().getErrors();
            Assert.assertEquals(1, errors.size());
            String expected = String.join("\n",
                    "no location ",
                    "Parsing error.",
                    "Next token: eof(eof) @ null",
                    "State: (Symbol: rparen, State: 12)",
                    "Stack (top):",
                    "\t(Symbol: rparen, State: 12)",
                    "\t(Symbol: balanced, State: 10)",
                    "\t(Symbol: lparen, State: 8)",
                    "\t(Symbol: lparen, State: 4)",
                    "(bottom)");
            Assert.assertEquals(expected, errors.get(0).toString());
        }
        Assert.assertTrue(exceptionThrown);
    }
}
