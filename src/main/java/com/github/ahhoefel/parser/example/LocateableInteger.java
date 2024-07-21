package com.github.ahhoefel.parser.example;

import com.github.ahhoefel.parser.Locateable;
import com.github.ahhoefel.parser.io.CodeLocation;

public class LocateableInteger implements Locateable {

    private CodeLocation location;
    private int value;

    public LocateableInteger(int value, CodeLocation location) {
        this.value = value;
        this.location = location;
    }

    public int getValue() {
        return value;
    }

    @Override
    public CodeLocation getLocation() {
        return location;
    }

    @Override
    public void setLocation(CodeLocation location) {
        this.location = location;
    }

    public LocateableInteger plus(LocateableInteger other) {
        return new LocateableInteger(value + other.value, new CodeLocation(this.location, other.location));
    }

    public LocateableInteger times(LocateableInteger other) {
        return new LocateableInteger(value * other.value, new CodeLocation(this.location, other.location));
    }

    public LocateableInteger minus(LocateableInteger other) {
        return new LocateableInteger(value - other.value, new CodeLocation(this.location, other.location));
    }

    public String toString() {
        return Integer.toString(value);
    }
}
