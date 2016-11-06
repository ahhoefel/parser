package lex

import "testing"
import "trie"

func TestLex(t *testing.T) {
    g := &GroupLexer{}
    g.Add(trie.NewLexer("foo", "bar"))
    g.Add(trie.NewLexer("foo", "baz"))
    input := "barfoobaz"
    out := []*Match{
        nil,
        nil,
        &Match{0, "bar"},
        nil,
        nil,
        &Match{0, "foo"},
        nil,
        nil,
        &Match{1, "baz"},
    }
    for i, r := range input{
        m := g.Next(r)
        if out[i] == nil {
            if m != nil {
                t.Errorf("Expected no match at character %d", i)
            }
            continue
        }
        if m == nil {
            t.Errorf("Expected match at character %d", i)
            continue
        }
        if m.Index != out[i].Index || m.Str != out[i].Str {
            t.Errorf("Match{%d, %s} at %d, expected Match{%d, %s}", m.Index, m.Str, i, out[i].Index, out[i].Str)
        }
    }
}