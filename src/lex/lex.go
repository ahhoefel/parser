package lex

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

type GroupLexer struct{
    lexers []Lex
    match string
}

func (g *GroupLexer) Add(l Lex) {
    g.lexers = append(g.lexers, l)
}

func (g *GroupLexer) Next(r rune) *Match{
    g.match = g.match + string(r)
    for i, l := range g.lexers {
        l.Next(r)
        if l.Match() {
            m := &Match{i, g.match}
            g.Reset()
            return m
        }
    }
    return nil
}

func (g *GroupLexer) Reset() {
    for _, l := range g.lexers {
        l.Reset()
    }
    g.match = ""
}