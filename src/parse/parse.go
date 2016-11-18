package parse

import "lex"

type State interface {
    NewTaker() lex.Taker
    DeleteTaker(lex.Taker)
}

type pancake struct {
    state string
    taker lex.Taker
}

type Parser struct {
    stack []pancake
    States map[string]State
}

func New() *Parser {
    return &Parser{States: make(map[string]State)}
}

func (p *Parser) Take(m lex.Match) error {
    return p.stack[len(p.stack) -1].taker.Take(m)
}

func (p *Parser) Push(name string) bool {
    state, ok := p.States[name]
    if ok {
        p.stack = append(p.stack, pancake{name, state.NewTaker()})
    }
    return ok
}

func (p *Parser) Pop() bool {
    if len(p.stack) <= 1 {
        return false
    }
    top := p.stack[len(p.stack) - 1]
    p.States[top.state].DeleteTaker(top.taker)
    p.stack = p.stack[:len(p.stack) - 1]
    return true
}