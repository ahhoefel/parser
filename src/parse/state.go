package parse

import "lex/word"

type StateName string

type State struct {
    Name StateName
    Lex *GroupLexer
    Next func(Match) StateName
}

var ErrorState = State{
    StateName("error"),
    word.All,
    func(m Match) StateName {
        return StateName("error")
    },
}