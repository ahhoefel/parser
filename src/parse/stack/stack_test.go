package stack

import (
    "fmt"
    "lex"
    "lex/trie"
    "testing"
)

type parens struct {
    children []*parens
}

func (p *parens) String() string {
    out := ""
    for _, c := range p.children {
        out += "(" + c.String() + ")"
    }
    return out
}

func (p *parens) Take(parser *Parser, match lex.Match) error {
    switch match.Str {
    case "(":
        x := &parens{}
        p.children = append(p.children, x)
        parser.Push(x)      
    case ")":
        if len(parser.Stack) <= 1 {
            return fmt.Errorf("Unable to pop.")
        }
        parser.Pop()
    default:
        fmt.Errorf("unrecognised match %s", match.Str)
    }
    return nil
}

func TestStack(t *testing.T) {
    tests := []struct{
        str string
        ok bool
    }{
        {"()", true},
        {"()()", true},
        {"(())", true},
        {"())(", false},
        {"(())()", true},
        {")(", false},
    }
    for _, test := range tests {
        t.Logf("Running test on %s", test.str)
        p := &parens{}
        s := New(p)
        b := lex.Buffer{
            Taker: s,
            Lex: trie.New("trie", "(", ")"),
        }
        n, err := b.Read([]byte(test.str))
        if err == nil {
            err = b.Close()
        }
        if test.ok {
            if n != len(test.str) || err != nil {
                t.Fatalf("expected no error on %s: %d read, error %s", test.str, n, err)
            }
            if len(s.Stack) != 1 {
                t.Fatalf("expected stack size one on %s, got %d", test.str, len(s.Stack))
            } else if s.Stack[0].(*parens).String() != test.str {
                t.Fatalf("expected parse as %s, got %s", test.str, s.Stack[0].(*parens).String())
            }
        } else if n == len(test.str) && err == nil {
            t.Fatalf("expected error on input %s", test.str)
        }
    }
}