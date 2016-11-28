package ru.ifmo.ctddev.paragen.mathLogic;


import java.util.Map;

abstract class Node {

    protected final Node leftSon, rightSon;
    Node (Node first, Node second) {
        leftSon = first;
        rightSon = second;
    }

    boolean isVariable() {
        return false;
    }

    Node getLeft() {
        return leftSon;
    }

    Node getRight() {
        return rightSon;
    }

    static Node nodeFactory(String s, Node first, Node second) {
        switch (s) {
            case "-":
            case "->":
                return new Implication(first,second);
            case "|":
                return  new  Disjunction(first,second);
            case "&":
                return new Conjunction(first,second);
            case "!":
                return new Negation(second);
            default:
                return new Variable(s);
        }
    }

    boolean equals(Node another) {
        if (another == null ) {
            return false;
        }

        if (isVariable() && another.isVariable()) {
            return asString().equals(another.asString());
        }

        return getClass().equals(another.getClass());
    }

    abstract String asString();

    abstract boolean evaluate(Map<String,Boolean> values);
}
