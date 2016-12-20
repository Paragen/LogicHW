package ru.ifmo.ctddev.paragen.mathLogic;


import java.util.Map;

public class Variable extends Node {

    private final String name;

    Variable(String s) {
        super(null, null);
        name = s;
    }

    @Override
    boolean isVariable() {
        return true;
    }

    @Override
    String asString() {
        return name;
    }

    @Override
    boolean evaluate(Map<String, Boolean> values) {
        return values.get(name);
    }

    @Override
    protected String str() {
        return asString();
    }
}
