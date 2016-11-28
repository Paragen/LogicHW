package ru.ifmo.ctddev.paragen.mathLogic;


import java.util.Map;

public class Disjunction extends Node{

    Disjunction(Node first, Node second) {
        super(first, second);
    }

    @Override
    String asString() {
        return "|";
    }

    @Override
    boolean evaluate(Map<String, Boolean> values) {
        return leftSon.evaluate(values)||rightSon.evaluate(values);
    }
}
