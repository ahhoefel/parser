package parens

import (
    "parse"
    "lex"
    "fmt"
)

type Parens struct{
    Parser *parse.Parser
}

func (p *Parens) NewTaker() lex.Taker {
    return p
}

func (p *Parens) DeleteTaker(t lex.Taker) {}

func (p *Parens) Take(m lex.Match) error {
    switch m.Str {
    case "{":
        _ = p.Parser.Push("parens")
    case "}":
        if ok := p.Parser.Pop(); !ok {
            return fmt.Errorf("error parsing parens")
        }
    }
    return nil
}