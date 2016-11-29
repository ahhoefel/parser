package tag

type Tag interface {
    Name() string
}

type tag string

func New(s string) Tag {
    t := tag(s)
    return &t
}

func (t *tag) Name() string {
    if t == nil {
        return ""
    }
    return string(*t)
}

func Equal(s, t Tag) bool {
    if s == nil || t == nil {
        return s == t
    }
    return s.Name() == t.Name()
}

type Match interface {
    Tag
    Value() string
    Position() int
}

type match struct {
    name string
    value string
    position int
}

func NewMatch(name, value string, position int) Match {
    return &match{name, value, position}
}

func (m *match) Name() string {
    return m.name
}

func (m *match) Value() string {
    return m.value
}

func (m *match) Position() int {
    return m.position
}

type Symbol interface {}

type Terminal interface {
    Match
    Symbol() Symbol
}

type terminal struct {
    match
    s Symbol
}

func NewTerminal(name, value string, position int, sym Symbol) Terminal {
    return &terminal{match{name, value, position}, sym}
}

func (t *terminal) Symbol() Symbol {
    return t.s
}