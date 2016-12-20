package ru.ifmo.ctddev.paragen.mathLogic;


import java.util.Map;

public class Prime extends Node {

    Prime(Node first) {
        super(first, null);
        priority = 10;
    }

    @Override
    String asString() {
        return "\'";
    }

    @Override
    boolean evaluate(Map<String, Boolean> values) {
        return false;
    }

    @Override
    protected String str() {
        return getLeft().wrap(priority) + asString();
    }
}
