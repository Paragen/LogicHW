package ru.ifmo.ctddev.paragen.mathLogic;


import java.util.ArrayList;
import java.util.List;

public class FormalParser {

    private class ShiftedString {

        final String data;
        private int shift;

        ShiftedString(String s) {
            data = s;
        }

        char getNext() {
            char ch = data.charAt(shift++);
            if (shift == data.length()) {
                --shift;
            }
            return ch;
        }

        char getCurrent() {
            return data.charAt(shift);
        }
    }

    public Node parse(String s) {
        s = "(" + s + ")";
        return parseUnary(new ShiftedString(s));
    }

    private Node parseImplication(ShiftedString s) {
        Node node = parseDisjunction(s);
        if (s.getCurrent() == '-') {
            s.getNext();
            s.getNext();
            return new Implication(node, parseImplication(s));
        }
        return node;
    }

    private Node parseDisjunction(ShiftedString s) {
        Node node = parseConjunction(s);
        while (s.getCurrent() == '|') {
            s.getNext();
            node = new Disjunction(node, parseConjunction(s));
        }
        return node;
    }

    private Node parseConjunction(ShiftedString s) {
        Node node = parseUnary(s);
        while (s.getCurrent() == '&') {
            s.getNext();
            node = new Conjunction(node, parseUnary(s));
        }
        return node;
    }

    private Node parseEquality(ShiftedString s) {
        Node node = parseUnary(s);
        if (s.getCurrent() == '=') {
            s.getNext();
            return new Predicate(node, parseUnary(s));
        }
        return node;
    }

    private Node parseUnary(ShiftedString s) {
        Node node = parseMul(s);
        switch (s.getCurrent()) {
            case '!':
                s.getNext();
                return new Negation(parseUnary(s));
            case '?':
                s.getNext();
                return new ExistentialQuantifier(new FormalVariable(getName(s)), parseUnary(s));
            case '@':
                s.getNext();
                return new UniversalQuantifier(new FormalVariable(getName(s)), parseUnary(s));
            case '=':
                s.getNext();
                return new Predicate(node, parseMul(s));
            default:
                if (Character.isUpperCase(s.getCurrent())) {
                    return parsePredicate(s);
                }
                return node;
        }
    }

    private Node parsePredicate(ShiftedString s) {

        String name = getName(s);
        if (s.getCurrent() == '(') {
            List<Node> nodes = new ArrayList<>();
            char ch;
            while ((ch = s.getNext()) == ',' || ch == '(') {
                nodes.add(parseMul(s));
            }
            return new Predicate(name, nodes);
        }
        return new Predicate(name, new ArrayList<>());

    }

    private String getName(ShiftedString s) {
        StringBuilder builder = new StringBuilder();
        builder.append(s.getNext());
        while (Character.isDigit(s.getCurrent())) {
            builder.append(s.getNext());
        }
        return builder.toString();
    }

    private Node parseMul(ShiftedString s) {
        Node node = parseSum(s);
        while (s.getCurrent() == '*') {
            s.getNext();
            node = new Multiply(node, parseSum(s));
        }
        return node;
    }

    private Node parseSum(ShiftedString s) {
        Node node = parseTerm(s);
        while (s.getCurrent() == '+') {
            s.getNext();
            node = new Addition(node, parseTerm(s));
        }
        return node;
    }

    private Node parseTerm(ShiftedString s) {
        Node node = null;
        if (s.getCurrent() == '(') {
            s.getNext();
            node = parseImplication(s);
            s.getNext();
        } else if (Character.isLowerCase(s.getCurrent()) || s.getCurrent() == '0') {
            String name = getName(s);
            if (s.getCurrent() == '(') {
                List<Node> nodes = new ArrayList<>();
                char ch;
                while ((ch = s.getNext()) == ',' || ch == '(') {
                    nodes.add(parseMul(s));
                }
                node = new Function(name, nodes);
            } else {
                node = new FormalVariable(name);
            }
        }
        return parsePrime(s, node);
    }

    private Node parsePrime(ShiftedString s, Node node) {
        if (s.getCurrent() == '\'') {
            s.getNext();
            return parsePrime(s, new Prime(node));
        } else {
            return node;
        }
    }
}
