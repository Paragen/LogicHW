package ru.ifmo.ctddev.paragen.mathLogic;


import java.util.ArrayList;
import java.util.Map;

public class FormalVariable extends Node{


    private String name;
    FormalVariable(String name) {
        super();
        this.name = name;
        children = new ArrayList<>();
        priority = 12;
    }

    @Override
    boolean equals(Node node) {
        if (node.getClass() == FormalVariable.class) {
            return asString().equals(node.asString());
        }
        return false;
    }

    @Override
    String asString() {
        return name;
    }

    @Override
    boolean evaluate(Map<String, Boolean> values) {
        return false;
    }

    @Override
    protected String str() {
        return asString();
    }
}
