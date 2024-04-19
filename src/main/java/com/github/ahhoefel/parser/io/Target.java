package com.github.ahhoefel.parser.io;

import java.nio.file.Path;

/**
 * Describes source files during parsing.
 * 
 * CodeLocations store Targets which describe the source files being parsed.
 * 
 * Since Targets are stored on CodeLocations, they are attached to parsing
 * results and can be used programatically.
 */
public interface Target {
  Path getPath();

  /*
   * Used in printing of CodeLocations.
   */
  String toString();
}
