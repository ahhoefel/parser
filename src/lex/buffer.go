package lex

import (
    "fmt"
    "lex/tag"
    "unicode/utf8"
)

type Buffer struct {
    Taker Taker
    Lex Lex
    buf []rune
    runesFed int
    bytesFed int
    lastMatchSize int
    lastMatchTag tag.Tag
}

func (b *Buffer) Read(p []byte) (int, error) {
    n := 0
    bl := len(b.buf)
    for {
        r, size := utf8.DecodeRune(p)
        if size == 0 {
            break
        }
        n += size
        p = p[size:]
        b.buf = append(b.buf, r)
    }
    err := b.feed(bl)
    if err != nil {
        return n, err
    }
    // Check if the bytes are utf8 encoded. This is difficult because we
    // can't tell if more runes are coming. E.g. p[0] could be a valid rune
    // start, but it could require another byte, which might never arrive.
    // Can we detect the end of file?
    if len(p) > 0 && !utf8.RuneStart(p[0]) {
        return n, fmt.Errorf("Not utf8 encoded. Invalid rune start %x.", p[0])
    }
    return n, nil
}

func (b *Buffer) Close() error {
    for len(b.buf) > 0 {
        if err := b.sendMatch(); err != nil {
            return err
        }
        if err := b.feed(0); err != nil {
            return err
        }
    }
    return nil
}

func (b *Buffer) feed(start int) error {
    for i := start; i < len(b.buf); i++ {
        b.Lex.Next(b.buf[i])
        if b.Lex.Match() {
            b.lastMatchSize = i + 1
            b.lastMatchTag = b.Lex.Tag()
        }
        if b.Lex.Error() {
            if err := b.sendMatch(); err != nil {
                return err
            }
            i = -1 // zero on next iteration
        }
    }
    return nil
}

func (b *Buffer) sendMatch() error {
    if b.lastMatchSize == 0 {
        return fmt.Errorf("failed to parse starting at position %d", b.runesFed)
    }
    match := Match{b.runesFed, string(b.buf[:b.lastMatchSize]), b.lastMatchTag}
    b.buf = b.buf[b.lastMatchSize:]
    b.runesFed += b.lastMatchSize
    b.lastMatchSize = 0
    b.lastMatchTag = nil
    b.Lex.Reset()
    return b.Taker.Take(match)
}