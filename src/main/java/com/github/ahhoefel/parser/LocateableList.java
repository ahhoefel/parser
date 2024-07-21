package com.github.ahhoefel.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.ahhoefel.parser.io.CodeLocation;

public class LocateableList<T> implements Locateable {

    private CodeLocation location;
    private List<T> list;

    public LocateableList() {
        this.list = new ArrayList<T>();
    }

    public LocateableList(List<T> list) {
        this.list = list;
    }

    public void add(T t) {
        this.list.add(t);
    }

    public void addFirst(T t) {
        this.list.addFirst(t);
    }

    public List<T> getList() {
        return list;
    }

    @Override
    public CodeLocation getLocation() {
        return location;
    }

    @Override
    public void setLocation(CodeLocation location) {
        this.location = location;
    }

    public String toString() {
        return list.stream().map(Object::toString)
                .collect(Collectors.joining(", "));
    }
}
