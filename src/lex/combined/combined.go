package combined

import "lex"
import "lex/tag"

type combinedLex struct{
   lexers []lex.Lex
   passing []lex.Lex
   match int
}

func New(s []lex.Lex) lex.Lex {
    p := make([]lex.Lex, len(s))
    copy(p,s)
    return &combinedLex{
        lexers: s,
        passing: p,
        match: -1,
    }
}

func (l *combinedLex) Next(r rune) {
    n := 0
    l.match = -1
    for _, m := range l.passing {
        m.Next(r)
        if !m.Error() {
            l.passing[n] = m
            if l.match == -1 && m.Match() {
                l.match = n
            }
            n++
        }
    }
    l.passing = l.passing[:n]
}

func (l *combinedLex) Match() bool {
    return l.match != -1
}

func (l *combinedLex) Tag() tag.Tag {
    if l.match == -1 {
        return nil
    }
    return l.passing[l.match].Tag()
}

func (l *combinedLex) Error() bool {
    return len(l.passing) == 0
}

func (l *combinedLex) Reset() {
    l.passing = l.passing[0:0]
    for _, m := range l.lexers {
        l.passing = append(l.passing, m)
    }
    l.match = -1
}