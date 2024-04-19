package com.github.ahhoefel.parser;

import java.util.ArrayList;
import java.util.List;

import com.github.ahhoefel.parser.io.ParseError;

public class ErrorLog {

  private List<ParseError> errors;

  public ErrorLog() {
    errors = new ArrayList<>();
  }

  public void add(ParseError err) {
    this.errors.add(err);
  }

  public List<ParseError> getErrors() {
    return errors;
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (ParseError err : errors) {
      builder.append(err.toString()).append("\n");
    }
    return builder.toString();
  }

  public boolean equals(Object o) {
    if (!(o instanceof ErrorLog)) {
      return false;
    }
    ErrorLog log = (ErrorLog) o;
    return log.errors.equals(errors);
  }

  public boolean isEmpty() {
    return errors.isEmpty();
  }
}
