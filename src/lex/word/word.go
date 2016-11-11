package word

import "math"
import "unicode"

type Word struct {
    Chars func(rune) bool
    MinChars int
    MaxChars int
    len int
}

func New(c func(rune) bool) *Word{
    return &Word{c, 1, math.MaxInt32, 0}
}

func (w *Word) Next(r rune) {
    if w.len == -1 {
        return
    }
    if w.Chars(r) {
        w.len++
    } else {
        w.len = -1
    }
}

func (w *Word)  Match() bool {
    return w.len != -1 && w.len >= w.MinChars && w.len <= w.MaxChars
}

func (w *Word)  Error() bool {
    return w.len == -1
}


func (w *Word)  Reset() {
    w.len = 0
}

var Whitespace = New(unicode.IsSpace)
var Alpha = New(unicode.IsLetter)
var Numeric = New(unicode.IsDigit)
var All = New(func(r rune) bool{return true})