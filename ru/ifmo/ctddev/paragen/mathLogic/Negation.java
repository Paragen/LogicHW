package ru.ifmo.ctddev.paragen.mathLogic;


import java.util.Map;

public class Negation extends Node {

    Negation(Node second) {
        super(null, second);
        priority = 5;
    }

    @Override
    Node getRight() {
        return  null;
    }

    @Override
    String asString() {
        return "!";
    }

    @Override
    boolean evaluate(Map<String, Boolean> values) {
        return !getLeft().evaluate(values);
    }


    @Override
    protected String str() {
        return asString() + getLeft().wrap(priority);
    }
}
