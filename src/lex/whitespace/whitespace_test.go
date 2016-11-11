package whitespace

import "testing"
import "lex/trie"
import "lex"

func TestWhitespace(t *testing.T) {
    taker := (*lex.SimpleTaker)(&[]lex.Match{})
    g := lex.NewGroupLexer(taker)
    g.Add(NewLexer())
    g.Add(trie.NewLexer("foo", "bar"))
    input := "  bar\t foo bar\n"
    expected := []lex.Match{
        {0, "  "},
        {1, "bar"},
        {0, "\t "},
        {1, "foo"},
        {0, " "},
        {1, "bar"},
        {0, "\n"},
    }
    
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