package trie

import "testing"

func TestTrie(t *testing.T) {
    input := []string{"abbot", "llama", "lake", "latke", "cat", "llamas"}
    missing := []string{"a", "abb", "dog", "llamass", "lakes", "la", "car", "lake "}
    
    trie := &Trie{}
    for _, w := range input{
        trie.Add(w)   
    }
    for _, w := range input{
        if !trie.Contains(w) {
            t.Errorf("trie.Contains(%s) == false, want true", w)
        }
    }
    for _, w := range missing{
        if trie.Contains(w) {
            t.Errorf("trie.Contains(%s) == true, want false", w)
        }
    }
}