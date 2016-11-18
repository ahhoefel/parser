package tag

type Tag interface {
    Tag() string
}

type tagString string

func New(s string) Tag {
    t := tagString(s)
    return &t
}

func (t *tagString) Tag() string {
    return string(*t)
}

func Equal(s, t Tag) bool {
    if t == nil || s == nil {
        return s == t
    }
    return s.Tag() == t.Tag()
}