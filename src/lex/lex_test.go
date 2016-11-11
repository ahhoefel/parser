package lex

import "testing"
import "lex/trie"

func TestLex(t *testing.T) {
    g := &GroupLexer{}
    g.Add(trie.NewLexer("foo", "bar"))
    g.Add(trie.NewLexer("foo", "baz"))
    input := "barfoobaz"
    expected := []Match{
        {0, "bar"},
        {0, "foo"},
        {1, "baz"},
    }
    taker := (*SimpleTaker)(&[]Match{})
    g.taker = taker
    for _, r := range input{
        g.Next(r)
    }
    if err := g.Flush(); err != nil {
        t.Errorf("Unexpected error on flush:", err)
    }
    if len(*taker) != len(expected) {
        t.Errorf("Got %v, expected %v", *taker, expected)
    } else {
        for i, e := range expected {
            if e != (*taker)[i] {
                t.Errorf("Got %v, expected %v", *taker, expected)
            }
        }
    }
}