package com.github.ahhoefel.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.github.ahhoefel.parser.lang.Rule;

/**
 * Resolves ambiguities in grammars.
 * 
 * <p>
 * The ShiftReduceResolver provides a simple way to specify preferences
 * to either shift or reduce symbols to resolve ambiguities in grammers.
 * 
 * <p>
 * Consider the following grammar.
 * 
 * <pre>
 * S -> S + S
 * S -> a
 * </pre>
 * 
 * <p>
 * This grammer is ambigous, because both {@code a+a+a} can be parsed as either
 * {@code (a+a)+a} or {@code a+(a+a)}. When constructing the LRParser, the
 * following error is given:
 * 
 * <pre>
 * Shift/Reduce error while constructing LR table:
 * LRItem: 5
 * Marked Rules:
 * [S => S plus S ^, eof]
 * [S => S ^ plus S , plus]
 * [S => S plus S ^, plus]
 * [S => S ^ plus S , eof]
 * Next indices:
 * plus: 4
 *
 * Shift and consume terminal plus
 * Reduce S ⟹ S plus S
 * </pre>
 * 
 * <p>
 * Note that if we are in state 5 and we see the terminal plus, we are in either
 * <ol>
 * <li>at marked rule
 * "S => S ^ plus S" in which we should push the plus on the stack and
 * transition to "S => S plus ^ S", or
 * <li>we are at marked rule "S => S plus S ^" in which case we can reduce by
 * popping "S plus S" off the stack
 * and pushing "S".
 * </ol>
 * 
 * <p>
 * Option (1) leads to a right derivation {@code a+(a+a)} as eventually all
 * the terminals are pushed on to the stack before
 * reducing when the next token is eof. This preference can be stated using:
 * 
 * <pre>{@code
 * shiftReduceResolver.addShiftPreference(plusRule, plusTerminal);
 * }</pre>
 * 
 * <p>
 * Option (2) leads to a left derivation {@code (a+a)+a} as we reduce greedily.
 * 
 * <pre>{@code
 * shiftReduceResolver.addReducePreference(plusRule, plusTerminal);
 * }</pre>
 */
public class ShiftReduceResolver {

  private Map<Pair, Preference> prefs = new HashMap<>();

  public ShiftReduceResolver() {
  }

  public void addShiftPreference(Rule rule, Symbol terminal) {
    prefs.put(new Pair(rule, terminal), Preference.SHIFT);
  }

  public void addReducePreference(Rule rule, Symbol terminal) {
    prefs.put(new Pair(rule, terminal), Preference.REDUCE);
  }

  public Optional<Preference> getPreference(Rule rule, Symbol terminal) {
    return Optional.ofNullable(prefs.get(new Pair(rule, terminal)));
  }

  public String toString() {
    String out = "ShiftReduceResolver:\n";
    for (Map.Entry<Pair, Preference> e : prefs.entrySet()) {
      out += e.getKey().toString() + ": " + e.getValue() + "\n";
    }
    return out;
  }

  public enum Preference {
    SHIFT,
    REDUCE
  }

  private static class Pair {
    private Rule rule;
    private Symbol terminal;

    public Pair(Rule rule, Symbol terminal) {
      this.rule = rule;
      this.terminal = terminal;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof Pair)) {
        return false;
      }
      Pair p = (Pair) o;
      return Objects.equals(rule, p.rule) && Objects.equals(terminal, p.terminal);
    }

    @Override
    public int hashCode() {
      return rule.hashCode() + 31 * terminal.hashCode();
    }

    public String toString() {
      return String.format("Rule: %s, Terminal: %s", rule, terminal);
    }
  }
}
