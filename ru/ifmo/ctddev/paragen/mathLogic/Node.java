package ru.ifmo.ctddev.paragen.mathLogic;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

abstract class Node implements Cloneable {

    //protected final Node leftSon, rightSon;
    List<Node> children;
    Node (Node first, Node second) {
        children = new ArrayList<>();
        if (first != null) {
            children.add(first);
        }
        if (second != null) {
            children.add(second);
        }

    }

    Node() {}

    boolean isVariable() {
        return false;
    }

    Node getLeft() {
        return children.get(0);
    }

    Node getRight() {
        return children.get(1);
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

    List<Node> getChildren() {
        return children;
    }

    protected int priority;
    public String toString() {
        return wrap(priority - 1)  ;
    }

    protected String wrap(int priority) {
        String s = str();
        if (this.priority <= priority) {
            s = "(" + s + ")";
        }
        return s;
    }


    abstract protected String str();

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
