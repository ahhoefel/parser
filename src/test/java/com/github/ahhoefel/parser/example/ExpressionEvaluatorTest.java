package com.github.ahhoefel.parser.example;

import org.junit.Assert;
import org.junit.Test;

import com.github.ahhoefel.parser.example.ExpressionEvaluator.LocateableInteger;

public class ExpressionEvaluatorTest {

        private ExpressionEvaluator parser;

        public ExpressionEvaluatorTest() {
                this.parser = new ExpressionEvaluator();
        }

        @Test
        public void tests() {
                Assert.assertEquals(7, ((LocateableInteger) parser.parse("3+4")).getValue());
                Assert.assertEquals(12, ((LocateableInteger) parser.parse("3*4")).getValue());
                Assert.assertEquals(-1, ((LocateableInteger) parser.parse("3-4")).getValue());
                Assert.assertEquals(17, ((LocateableInteger) parser.parse("3*4+5")).getValue());
                Assert.assertEquals(23, ((LocateableInteger) parser.parse("3+4*5")).getValue());
                Assert.assertEquals(27, ((LocateableInteger) parser.parse("3*(4+5)")).getValue());
                Assert.assertEquals(35, ((LocateableInteger) parser.parse("(3+4)*5")).getValue());
        }
}
