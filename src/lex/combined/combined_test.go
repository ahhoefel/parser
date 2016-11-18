package combined

import "testing"
import "lex/trie"
import "lex/tag"

func TestLex(t *testing.T) {
    l := New([]Lex{
        trie.New("tag1", "foo", "foots"),
        trie.New("tag2", "foo", "fool"),
    })
    tests := []struct{
        input rune
        match bool
        tag tag.Tag
        err bool
    }{
        {'f', false, nil, false},
        {'o', false, nil, false},
        {'o', true, tag.New("tag1"), false},
        {'l', true, tag.New("tag2"), false},
        {'s', false, nil, true},
    }
    for i, test := range tests {
        l.Next(test.input)
        if l.Match() != test.match {
            t.Errorf("match not equal on input %d: expected %v, got %v", i, test.match, l.Match())
        }
        if !tag.Equal(l.Tag(), test.tag) {
            t.Errorf("tag not equal on input %d: expected %v, got %v", i, test.tag, l.Tag())
        }
        if l.Error() != test.err {
            t.Errorf("error not equal on input %d: expected %v, got %v", i, test.err, l.Error())
        }
    }
}