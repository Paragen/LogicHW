package ru.ifmo.ctddev.paragen.mathLogic;


import java.util.List;
import java.util.Map;

public class Function extends Node {

    private String name;

    Function(String name, List<Node> list) {
        super();
        this.name = name;
        children = list;
        priority = 11;
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
        StringBuilder builder = new StringBuilder();
        builder.append(asString());

        builder.append('(');

        for (int i = 0; i < children.size(); ++i) {

            builder.append(children.get(i).str());

            if (i+1 != children.size()) {
                builder.append(',');
            }
        }

        builder.append(')');

        return builder.toString();
    }
}
