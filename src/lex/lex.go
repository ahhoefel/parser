package lex

import "fmt"

type Lex interface {
    Next(rune)
    Match() bool
    Error() bool
    Reset()
}

type Match struct{
    Index int
    Str string
}

type MatchTaker interface {
    Take(Match)
}

type SimpleTaker []Match

func (t *SimpleTaker) Take(m Match) {
    *t = append(*t, m)
}

type GroupLexer struct{
    taker MatchTaker
    lexers []Lex
    matches []int
    match []rune
}

func NewGroupLexer(t MatchTaker) *GroupLexer {
    return &GroupLexer{taker: t}
}

func (g *GroupLexer) Add(l Lex) {
    g.lexers = append(g.lexers, l)
    g.matches = append(g.matches, 0)
}

func (g *GroupLexer) Next(r rune) {
    g.match = append(g.match, r)
    allDone := true
    for i, l := range g.lexers {
        l.Next(r)
        if !l.Error() {
            allDone = false
        }
        if l.Match() {
            g.matches[i] = len(g.match)
        }
    }
    if allDone {
        err := g.flushMatch()
        if err != nil {
            fmt.Println(err)
        }
    }
}

func (g *GroupLexer) Flush() error {
    for len(g.match) > 0 {
        if err := g.flushMatch(); err != nil {
            return err
        }
    }
    return nil
}

func (g *GroupLexer) flushMatch() error {
    lexIndex, matchSize, found := g.firstMatch()
    if !found {
        return fmt.Errorf("Parse error")
    }
    g.taker.Take(Match{lexIndex, string(g.match[:matchSize])})
    rematch := g.match[matchSize:]
    g.Reset()
    for _, r := range rematch {
        g.Next(r)
    }
    return nil
}

func (g *GroupLexer) firstMatch() (lexIndex, matchSize int, found bool) {
    for i, size := range g.matches {
        if size != 0 {
            return i, size, true
        }
    }
    return 0, 0, false
}

func (g *GroupLexer) Reset() {
    for i, l := range g.lexers {
        l.Reset()
        g.matches[i] = 0
    }
    g.match = []rune{}
}