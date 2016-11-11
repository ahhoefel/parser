package trie

type Trie struct {
    chars map[rune]*Trie
    terminal bool
}

func New(s []string) *Trie{
    t := &Trie{}
    for _, w := range s {
        t.Add(w)
    }
    return t
}

func (t *Trie) Add(s string) {
    node := t
    for _, c := range s {
        tmp := node.chars[c]
        if tmp == nil {
            tmp = &Trie{
                chars: make(map[rune]*Trie),
                terminal: false,
            }
            if node.chars == nil {
                node.chars = make(map[rune]*Trie)
            }
            node.chars[c] = tmp    
        }
        node = tmp
    }
    node.terminal = true
}

func (t *Trie) Contains(s string) bool {
    node := t
    for _, c := range s {
        node = node.chars[c]
        if node == nil {
            return false
        }
    }
    return node.terminal 
}

func (t *Trie) Next(r rune) *Trie {
    if t == nil {
        return nil
    }
    return t.chars[r]
}

func (t *Trie) Match() bool {
    return t.terminal
}

type TrieLexer struct {
    head, cur *Trie
}

func NewLexer(strs ...string) *TrieLexer {
    t := &Trie{}
    for _, s := range strs {
        t.Add(s)
    }
    return &TrieLexer{t,t}
}

func (l *TrieLexer) Next(r rune) {
    l.cur = l.cur.Next(r)
}

func (l *TrieLexer) Match() bool {
    return l.cur != nil && l.cur.terminal
}

func (l *TrieLexer) Error() bool {
    return l.cur == nil
}

func (l *TrieLexer) Reset() {
    l.cur = l.head
}