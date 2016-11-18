package parens

import (
    "testing"
    "parse"
    "lex"
    "lex/trie"
    "fmt"
)

func TestParens(t *testing.T) {
    tests := []struct{
        str string
        ok bool
    }{
        {"{}", true},
        {"{}{}", true},
        {"{{}}", true},
        {"{}}{", false},
        {"{{}}{}", true},
        {"}{", false},
    }
    for _, test := range tests {
        parser := parse.New()
        parens := &Parens{parser}
        parser.States["parens"] = parens
        parser.Push("parens")
        b := lex.Buffer{
            Taker: parser,
            Lex: trie.New("trie", "{", "}"),
        }
        n, err := b.Read([]byte(test.str))
        if (n == len(test.str) && err == nil) != test.ok {
            if test.ok {
                t.Errorf("Expected no error on %s: %d read, error %s", test.str, n, err)
            } else {
                t.Errorf("Expected error on %s", test.str)
            }
        }
        fmt.Println("Done test!")
    }
}