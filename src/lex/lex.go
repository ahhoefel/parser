package lex

// FOOO

import "lex/tag"

type Lex interface {
    Next(rune)
    Match() bool
    Tag() tag.Tag
    Error() bool
    Reset()
}

type Match struct{
    Index int
    Str string
    Tag tag.Tag
}

type Taker interface {
    Take(Match) error
}

type SimpleTaker []Match

func (t *SimpleTaker) Take(m Match) error {
    *t = append(*t, m)
    return nil
}
