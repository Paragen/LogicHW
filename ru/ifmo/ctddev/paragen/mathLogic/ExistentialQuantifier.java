package ru.ifmo.ctddev.paragen.mathLogic;


import java.util.List;
import java.util.Map;

public class ExistentialQuantifier extends Node  {

    private Node varBy;
    ExistentialQuantifier(Node first, Node second) {
        super(null, second);
        varBy = first;
        priority = 4;
    }

    @Override
    Node getLeft() {
        return varBy;
    }

    @Override
    Node getRight() {
        return children.get(0);
    }

    @Override
    String asString() {
        return "?";
    }

    @Override
    boolean evaluate(Map<String, Boolean> values) {
        return false;
    }


    @Override
    protected String str() {
        return asString() + varBy.str() + getRight().wrap(priority);
    }
}
