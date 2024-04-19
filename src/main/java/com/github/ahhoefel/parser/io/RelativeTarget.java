package com.github.ahhoefel.parser.io;

import java.nio.file.Path;
import java.util.Objects;

public class RelativeTarget implements Target {

  private Path source;
  private Path relative;

  public RelativeTarget(Path source, String target) {
    this.source = source;
    this.relative = source.resolve(target);
  }

  public RelativeTarget(Path source, Path target) {
    this.source = source;
    this.relative = source.relativize(target);
  }

  public Path getSource() {
    return source;
  }

  public Path getRelativePath() {
    return relative;
  }

  public Path getPath() {
    return source.resolve(relative);
  }

  public String toString() {
    return "//" + relative.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof RelativeTarget)) {
      return false;
    }
    RelativeTarget t = (RelativeTarget) obj;
    return Objects.equals(source, t.source) && Objects.equals(relative, t.relative);
  }

  @Override
  public int hashCode() {
    return source.hashCode() + 31 * relative.hashCode();
  }
}
