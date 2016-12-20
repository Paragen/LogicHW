package ru.ifmo.ctddev.paragen.mathLogic;


import java.util.List;
import java.util.Map;

public class Predicate extends Node{

    private String name;

    Predicate(String name, List<Node> list) {
        super();
        this.name = name;
        children = list;
        priority = 6;
    }

    Predicate(Node first, Node second) {
        super(first,second);
        name = "=";
        priority = 6;
    }

    @Override
    String asString() {
        return name;
    }

    @Override
    boolean evaluate(Map<String, Boolean> values) {
        return false;
    }

    @Override
    protected String str() {
        if (name.equals("=")) {
            return getLeft().str() + asString() + getRight().str();
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(asString());
            if (children.size() > 0) {
                builder.append('(');

                for (int i = 0; i < children.size(); ++i) {

                    builder.append(children.get(i).str());

                    if (i + 1 != children.size()) {
                        builder.append(',');
                    }
                }

                builder.append(')');
            }
            return builder.toString();
        }
    }
}
