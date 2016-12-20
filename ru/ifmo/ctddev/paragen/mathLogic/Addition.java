package ru.ifmo.ctddev.paragen.mathLogic;


import java.util.Map;

public class Addition extends Node {

    Addition(Node first, Node second) {
        super(first, second);
        priority = 8;
    }

    @Override
    String asString() {
        return "+";
    }

    @Override
    boolean evaluate(Map<String, Boolean> values) {
        return false;
    }


    @Override
    protected String str() {
        return getLeft().wrap(priority) + asString() + getRight().wrap(priority);
    }


}
