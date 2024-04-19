package com.github.ahhoefel.parser.example;

import org.junit.Assert;
import org.junit.Test;

public class ExpressionParserTest {

        private ExpressionParser parser;

        public ExpressionParserTest() {
                this.parser = new ExpressionParser();
        }

        @Test
        public void testValue() {
                String result = parser.parse("v1").toString();
                String expected = String.join("\n",
                                "Rule: expression ⟹  value",
                                "0. Rule: value ⟹  identifier",
                                "0. 0. Rule: identifier ⟹  alpha identifierTail",
                                "0. 0. 0. Token: alpha(v) @ :1:1:1",
                                "0. 0. 1. Rule: identifierTail ⟹  alphanumeric identifierTail",
                                "0. 0. 1. 0. Rule: alphanumeric ⟹  numeral",
                                "0. 0. 1. 0. 0. Token: numeral(1) @ :1:2:1",
                                "0. 0. 1. 1. Rule: identifierTail ⟹ \n");
                Assert.assertEquals(expected, result);

                result = parser.parse("12").toString();
                expected = String.join("\n",
                                "Rule: expression ⟹  value",
                                "0. Rule: value ⟹  number",
                                "0. 0. Rule: number ⟹  numeral number",
                                "0. 0. 0. Token: numeral(1) @ :1:1:1",
                                "0. 0. 1. Rule: number ⟹  numeral",
                                "0. 0. 1. 0. Token: numeral(2) @ :1:2:1\n");
                Assert.assertEquals(expected, result);
        }

        @Test
        public void testSimple() {
                String result = parser.parse("3+a").toString();
                String expected = String.join("\n",
                                "Rule: expression ⟹  expression plus expression",
                                "0. Rule: expression ⟹  value",
                                "0. 0. Rule: value ⟹  number",
                                "0. 0. 0. Rule: number ⟹  numeral",
                                "0. 0. 0. 0. Token: numeral(3) @ :1:1:1",
                                "1. Token: plus(+) @ :1:2:1",
                                "2. Rule: expression ⟹  value",
                                "2. 0. Rule: value ⟹  identifier",
                                "2. 0. 0. Rule: identifier ⟹  alpha identifierTail",
                                "2. 0. 0. 0. Token: alpha(a) @ :1:3:1",
                                "2. 0. 0. 1. Rule: identifierTail ⟹ \n");
                Assert.assertEquals(expected, result);

                result = parser.parse("a*5").toString();
                expected = String.join("\n",
                                "Rule: expression ⟹  expression times expression",
                                "0. Rule: expression ⟹  value",
                                "0. 0. Rule: value ⟹  identifier",
                                "0. 0. 0. Rule: identifier ⟹  alpha identifierTail",
                                "0. 0. 0. 0. Token: alpha(a) @ :1:1:1",
                                "0. 0. 0. 1. Rule: identifierTail ⟹ ",
                                "1. Token: times(*) @ :1:2:1",
                                "2. Rule: expression ⟹  value",
                                "2. 0. Rule: value ⟹  number",
                                "2. 0. 0. Rule: number ⟹  numeral",
                                "2. 0. 0. 0. Token: numeral(5) @ :1:3:1\n");
                Assert.assertEquals(expected, result);
        }

        @Test
        public void testPrecedence() {
                String result = parser.parse("3+4*5").toString();
                String expected = String.join("\n",
                                "Rule: expression ⟹  expression plus expression",
                                "0. Rule: expression ⟹  value",
                                "0. 0. Rule: value ⟹  number",
                                "0. 0. 0. Rule: number ⟹  numeral",
                                "0. 0. 0. 0. Token: numeral(3) @ :1:1:1",
                                "1. Token: plus(+) @ :1:2:1",
                                "2. Rule: expression ⟹  expression times expression",
                                "2. 0. Rule: expression ⟹  value",
                                "2. 0. 0. Rule: value ⟹  number",
                                "2. 0. 0. 0. Rule: number ⟹  numeral",
                                "2. 0. 0. 0. 0. Token: numeral(4) @ :1:3:1",
                                "2. 1. Token: times(*) @ :1:4:1",
                                "2. 2. Rule: expression ⟹  value",
                                "2. 2. 0. Rule: value ⟹  number",
                                "2. 2. 0. 0. Rule: number ⟹  numeral",
                                "2. 2. 0. 0. 0. Token: numeral(5) @ :1:5:1\n");
                Assert.assertEquals(expected, result);

                result = parser.parse("3*4+5").toString();
                expected = String.join("\n",
                                "Rule: expression ⟹  expression plus expression",
                                "0. Rule: expression ⟹  expression times expression",
                                "0. 0. Rule: expression ⟹  value",
                                "0. 0. 0. Rule: value ⟹  number",
                                "0. 0. 0. 0. Rule: number ⟹  numeral",
                                "0. 0. 0. 0. 0. Token: numeral(3) @ :1:1:1",
                                "0. 1. Token: times(*) @ :1:2:1",
                                "0. 2. Rule: expression ⟹  value",
                                "0. 2. 0. Rule: value ⟹  number",
                                "0. 2. 0. 0. Rule: number ⟹  numeral",
                                "0. 2. 0. 0. 0. Token: numeral(4) @ :1:3:1",
                                "1. Token: plus(+) @ :1:4:1",
                                "2. Rule: expression ⟹  value",
                                "2. 0. Rule: value ⟹  number",
                                "2. 0. 0. Rule: number ⟹  numeral",
                                "2. 0. 0. 0. Token: numeral(5) @ :1:5:1\n");
                Assert.assertEquals(expected, result);
        }
}
