package ru.ifmo.ctddev.paragen.mathLogic;


import java.util.Map;

public class Implication extends Node {

    Implication(Node first, Node second) {
        super(first, second);
        priority =1;
    }

    @Override
    String asString() {
        return "->";
    }

    @Override
    boolean evaluate(Map<String, Boolean> values) {
        return (!getLeft().evaluate(values))||getRight().evaluate(values);
    }


    @Override
    protected String str() {
        return getLeft().wrap(priority) + asString() + getRight().wrap(priority);
    }
}
