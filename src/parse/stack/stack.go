package stack

import "lex"

type State interface {
    Take(*Parser, lex.Match) error
}

type Parser struct {
    Stack []State
}

func New(start State) *Parser {
    return &Parser{[]State{start}}
}

func (p *Parser) Take(m lex.Match) error {
    return p.Stack[len(p.Stack) -1].Take(p, m)
}

func (p *Parser) Push(s State) {
    p.Stack = append(p.Stack, s)
}

func (p *Parser) Pop() State {
    if len(p.Stack) == 0 {
        return nil
    }
    top := p.Stack[len(p.Stack) - 1]
    p.Stack = p.Stack[:len(p.Stack) - 1]
    return top
}