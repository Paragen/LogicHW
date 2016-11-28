package ru.ifmo.ctddev.paragen.mathLogic;


import java.util.Map;

public class Negation extends Node {

    Negation(Node second) {
        super(null, second);
    }

    @Override
    String asString() {
        return "!";
    }

    @Override
    boolean evaluate(Map<String, Boolean> values) {
        return !rightSon.evaluate(values);
    }
}
