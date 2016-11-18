package trie

import (
    "testing"
    "lex/tag"
)

func TestTrie(t *testing.T) {
    input := []string{"abbot", "llama", "lake", "latke", "cat", "llamas"}
    missing := []string{"a", "abb", "dog", "llamass", "lakes", "la", "car", "lake "}
    
    trie := NewTrie(input)
    for _, w := range input{
        if !trie.Contains(w) {
            t.Errorf("trie.Contains(%s) == false, want true", w)
        }
    }
    for _, w := range missing{
        if trie.Contains(w) {
            t.Errorf("trie.Contains(%s) == true, want false", w)
        }
    }
}

func TestTrieLex(t *testing.T) {
    trie := New("tag", "{", "}")
      tests := []struct{
        input rune
        match bool
        tag tag.Tag
        err bool
    }{
        {'{', true, tag.New("tag"), false},
        {'}', true, tag.New("tag"), false},
    }
    for i, test := range tests {
        trie.Reset()
        trie.Next(test.input)
        if trie.Match() != test.match {
            t.Errorf("match not equal on input %d: expected %v, got %v", i, test.match, trie.Match())
        }
        if !tag.Equal(trie.Tag(), test.tag) {
            t.Errorf("tag not equal on input %d: expected %v, got %v", i, test.tag, trie.Tag())
        }
        if trie.Error() != test.err {
            t.Errorf("error not equal on input %d: expected %v, got %v", i, test.err, trie.Error())
        }
    }
}