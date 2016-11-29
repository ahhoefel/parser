package lex

import (
    "testing"
    "lex/trie"
    "lex/tag"
)

func TestBuffer(t *testing.T) {
    taker := SimpleTaker([]Match{})
    b := Buffer{
        Taker: &taker,
        Lex: trie.New("tag", "abc", "abcdef","abcdefgh", "ggg"),
    }
    expected := SimpleTaker{
        Match{0, "abcdef", tag.New("tag")},
        Match{6, "ggg", tag.New("tag")},
    }
    n, err := b.Read([]byte("abcde")) 
    if n != 5 {
        t.Errorf("expected n == 5, got %d", n)
    }
    if err != nil {
        t.Errorf("expected err == nil, got %v", err)
    }
    n, err = b.Read([]byte("fggg")) 
    if err == nil {
        err = b.Close()
    }
    if n != 4 {
        t.Errorf("expected n == 4, got %d", n)
    }
    if err != nil {
        t.Errorf("expected err == nil, got %v", err)
    }
    if len(taker) != len(expected) {
        t.Errorf("expected len(taker) == %d, got %d", len(expected), len(taker))
    }
    for i, m := range taker {
        if m.Index != expected[i].Index || m.Str != expected[i].Str || !tag.Equal(m.Tag, expected[i].Tag) {
            t.Errorf("taker[%d] != %v, got %v", i, expected[i], m)   
        }
    }
    n, err = b.Read([]byte("xyz")) 
      if n != 3 {
        t.Errorf("expected n == 3, got %d", n)
    }
    if err == nil {
        t.Error("expected err != nil, got nil")
    }
}


func TestBufferParens(t *testing.T) {
    taker := SimpleTaker([]Match{})
    b := Buffer{
        Taker: &taker,
        Lex: trie.New("tag", "{", "}"),
    }
    expected := SimpleTaker{
        Match{0, "{", tag.New("tag")},
        Match{1, "}", tag.New("tag")},
        Match{2, "{", tag.New("tag")},
        Match{3, "}", tag.New("tag")},
    }
    n, err := b.Read([]byte("{}{}")) 
    if err == nil {
        err = b.Close()
    }
    if err != nil {
        t.Errorf("expected err == nil, got %v", err)
    }
    if n != 4 {
        t.Errorf("expected n == 4, got %d", n)
    }
    if len(taker) != 4 {
        t.Errorf("expected 4 matches, got %d", len(taker))
    }
    for i, m := range taker {
        if m.Index != expected[i].Index || m.Str != expected[i].Str || !tag.Equal(m.Tag, expected[i].Tag) {
            t.Errorf("taker[%d] != %v, got %v", i, expected[i], m)   
        }
    }
}