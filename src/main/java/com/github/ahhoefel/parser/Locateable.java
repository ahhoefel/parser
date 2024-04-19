package com.github.ahhoefel.parser;

import com.github.ahhoefel.parser.io.CodeLocation;

public interface Locateable {

    public CodeLocation getLocation();

    public void setLocation(CodeLocation location);
}
