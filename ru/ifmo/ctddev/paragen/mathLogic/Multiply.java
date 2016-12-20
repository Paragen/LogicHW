package ru.ifmo.ctddev.paragen.mathLogic;


import java.util.Map;

public class Multiply extends Node{
    Multiply(Node first, Node second) {
        super(first, second);
        priority = 9;
    }

    @Override
    String asString() {
        return "*";
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
