package whitespace


type Whitespace int
const (
    Empty Whitespace = iota
    Match
    Error
)

func NewLexer() *Whitespace {
    w := Empty
    return &w
}

func (w *Whitespace) Next(r rune) {
    if *w == Error {
        return
    }
    if r == ' ' || r == '\t' || r == '\n' || r == '\r' {
        *w = Match
    } else {
        *w = Error
    }
}

func (w *Whitespace)  Match() bool {
    return *w == Match
}

func (w *Whitespace)  Error() bool {
    return *w == Error
}


func (w *Whitespace)  Reset() {
    *w = Empty
}