package com.github.ahhoefel.parser.io;

import java.util.Objects;
import java.util.Optional;

import com.github.ahhoefel.parser.Locateable;

public class CodeLocation {

  private Optional<Target> target;
  private int lineNumber;
  private int character;
  private int position;
  private int length;

  public CodeLocation(Optional<Target> target, int length) {
    this.target = target;
    this.lineNumber = 0;
    this.character = 0;
    this.position = 0;
    this.length = length;
  }

  public CodeLocation(Optional<Target> target, int lineNumber, int character, int position) {
    this.target = target;
    this.lineNumber = lineNumber;
    this.character = character;
    this.position = position;
    this.length = 1;
  }

  public CodeLocation(CodeLocation from, CodeLocation to) {
    if (from.target.isPresent() && !to.target.isPresent()) {
      throw new RuntimeException(
          "Codelocations for \"from\" is present, but \"to\" is not present. From: " + from.target.get());
    }
    if (!from.target.isPresent() && to.target.isPresent()) {
      throw new RuntimeException(
          "Codelocations for \"to\" is present, but \"from\" is not present. To: " + to.target.get());
    }
    if (from.target.isPresent() && to.target.isPresent() && !Objects.equals(from.target, to.target)) {
      throw new RuntimeException("Codelocations cannot span multiple targets:" + from.target + ", " + to.target);
    }
    this.target = from.target;
    this.lineNumber = from.lineNumber;
    this.character = from.character;
    this.position = from.position;
    this.length = from.length + to.length;
  }

  public CodeLocation(Locateable[] locateables) {
    int i = 0;
    for (; i < locateables.length; i++) {
      if (locateables[i].getLocation() == null) {
        break;
      }
    }
    assert i == locateables.length : "Location missing on parameter " + i;
    CodeLocation from = locateables[0].getLocation();
    this.target = from.target;
    this.lineNumber = from.lineNumber;
    this.character = from.character;
    this.position = from.position;
    for (Locateable locatable : locateables) {
      this.length += locatable.getLocation().length;
    }
  }

  public String toString() {
    return String.format("%s:%d:%d:%d", target.isPresent() ? target.get() : "", lineNumber + 1, character + 1, length);
  }

  public boolean equals(Object o) {
    if (!(o instanceof CodeLocation)) {
      return false;
    }
    CodeLocation location = (CodeLocation) o;
    boolean sameLineChar = lineNumber == location.lineNumber && character == location.character;
    boolean samePos = position == location.position;
    return Objects.equals(target, location.target) && (sameLineChar || samePos);
  }
}
