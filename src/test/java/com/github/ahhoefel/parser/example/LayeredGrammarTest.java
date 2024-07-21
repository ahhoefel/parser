package com.github.ahhoefel.parser.example;

import org.junit.Assert;
import org.junit.Test;

public class LayeredGrammarTest {

    private LayeredGrammar parser;

    public LayeredGrammarTest() {
        this.parser = new LayeredGrammar();
    }

    @Test
    public void testSimple() {
        String result = parser.parse("this is a sentence.").toString();
        String expected = "alpha(this) @ :1:1:4, " +
                "space( ) @ :1:5:1, " +
                "alpha(is) @ :1:6:2, " +
                "space( ) @ :1:8:1, " +
                "alpha(a) @ :1:9:1, " +
                "space( ) @ :1:10:1, " +
                "alpha(sentence) @ :1:11:8, " +
                "dot(.) @ :1:19:1";
        Assert.assertEquals(expected, result);
    }
}
