package ru.ifmo.ctddev.paragen.mathLogic;


import java.util.Map;

public class UniversalQuantifier extends  Node {

    private Node varBy;
    UniversalQuantifier(Node first, Node second) {
        super(second,null);
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
        return "@";
    }

    @Override
    boolean evaluate(Map<String, Boolean> values) {
        return false;
    }

    @Override
    protected String str() {
        return asString() + varBy.str()+ getRight().wrap(priority);
    }
}
