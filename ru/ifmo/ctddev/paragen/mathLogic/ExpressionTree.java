package ru.ifmo.ctddev.paragen.mathLogic;


import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ExpressionTree {

    Node head;
    String str;


    private class ShiftedString {
        String data;
        int shift;

        ShiftedString(String data) {
            this.data = data;
            shift = 0;
        }

        char getNext() {

            if (++shift > data.length()) {
                --shift;
            }
            return data.charAt(shift - 1);
        }
    }

    //todo add function simplify
    public ExpressionTree(String s) {
        head = parse(s);
        str = "(" + s + ")";
    }

    String asString() {
        return str;
    }

    private Node parse(String s) {
        ShiftedString expr = new ShiftedString(s + ")");
        return parseExpr(expr, getPriority(')'));
    }

    int getPriority(char ch) {
        switch (ch) {
            case '!':
                return 1;

            case '&':
                return 2;

            case '|':
                return 3;
            case '-':
                return 4;
            case ')':
                return 5;
            default:
                return -1;
        }
    }

    private Node parseExpr(ShiftedString expr, int priority) {
        Node res = null;
        int length = 0;
        char ch;

        if (expr.getNext() == '(') {
            res = parseExpr(expr, getPriority(')'));
        } else {
            --expr.shift;
        }
        for (; ; ) {
            ch = expr.getNext();
            int val = getPriority(ch);
            if (val != -1) {
                if (ch != '!' && res == null) {
                    res = new Variable(expr.data.substring(expr.shift - length - 1, expr.shift - 1));
                }
                if (priority < val || (ch != '-' && ch != '!' && priority == val)) {
                    if (!(ch == ')' && priority == getPriority(')'))) {
                        --expr.shift;
                    }
                    return res;
                }

                if (ch == '-') {
                    ++expr.shift;
                }
                res = Node.nodeFactory(String.valueOf(ch), res, parseExpr(expr, val));

            } else {
                ++length;
            }
        }
    }

    public void printTree(String s) {
        try {
            Node val = head;
            PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(new File(s))));
            printTree(val, 2 * checkDepth(val), printWriter);
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public List<String> variableList() {
        List<Node> nodes = new LinkedList<>();
        nodes.add(head);
        List<String> answer = new ArrayList<>();
        while (nodes.size() != 0) {
            Node curr = nodes.remove(0);
            if (curr != null) {
                if (curr.isVariable()) {
                    answer.add(curr.asString());
                } else {
                    nodes.add(curr.getRight());
                    nodes.add(curr.getLeft());
                }
            }
        }
        return answer.stream().distinct().collect(Collectors.toList());
    }


    private int checkDepth(Node val) {
        int l = 0, r = 0;
        if (val.isVariable()) {
            return 1;
        }
        if (val.getLeft() != null) {
            l = checkDepth(val.getLeft());
        }
        if (val.getRight() != null) {
            r = checkDepth(val.getRight());
        }
        l = (l > r) ? l : r;
        return ++l;
    }


    private void printTree(Node val, int space, PrintWriter printWriter) {
        List<Node> list = new LinkedList<>();
        List<Integer> spaceList = new LinkedList<>(), saveList = new LinkedList<>();
        list.add(val);
        spaceList.add(space * 2);
        saveList.add(0);

        int sons = 0, curr = 1, shift = 0, save;
        while (!list.isEmpty()) {

            space = spaceList.remove(0);
            save = saveList.remove(0);
            val = list.remove(0);
            for (int i = 0; i < space - shift; ++i) {
                printWriter.print(' ');
            }
            shift = space;
            printWriter.print(val.asString());

            if (val.getLeft() != null) {
                list.add(val.getLeft());
                spaceList.add((space + save) / 2);
                saveList.add(save);
                ++sons;
            }
            if (val.getRight() != null) {
                list.add(val.getRight());
                spaceList.add((space - save) / 2 * 3 + save);
                saveList.add(space);
                ++sons;
            }
            --curr;
            if (curr == 0) {
                shift = 0;
                printWriter.println();
                curr = sons;
                sons = 0;
            }
        }
    }

    private ExpressionTree(Node node) {
        head = node;
    }

    public ExpressionTree checkMP(ExpressionTree curr) {
        if (head != null && !head.isVariable()) {
            if (check(head.getRight(), curr.head)) {
                return new ExpressionTree(head.getLeft());
            }
        }
        return null;
    }

    //todo Check by hash first
    public boolean equals(ExpressionTree another) {
        if (head == null || another.head == null) {
            return false;
        }
        return check(head, another.head);
    }

    protected boolean check(Node first, Node second) {
        if (first == null) {
            return first == second;
        }
        if (first.isVariable()) {
            return first.equals(second);
        } else {
            return first.equals(second) && check(first.getLeft(), second.getLeft()) && check(first.getRight(), second.getRight());
        }
    }

    public boolean evaluate(Map<String, Boolean> values) {
        return head.evaluate(values);
    }

    public List<String> prove(Map<String, Boolean> values) {
        return proveNode(head, values);
    }

    private List<String> proveNode(Node curr, Map<String, Boolean> values) {
        if (curr.getClass() == Implication.class) {
            return proveImplication(curr, values);
        }
        if (curr.getClass() == Conjunction.class) {
            return proveConjunction(curr, values);
        }
        if (curr.getClass() == Disjunction.class) {
            return proveDisjunction(curr, values);
        }
        if (curr.getClass() == Negation.class) {
            return proveNegation(curr, values);
        }

        return proveVariable(curr, values);
    }


    private List<String> proveVariable(Node curr, Map<String, Boolean> values) {
        List<String> answer = new ArrayList<>();
        if (curr.evaluate(values)) {
            answer.add(curr.asString());
        } else {
            answer.add("!" + curr.asString());
        }
        return answer;
    }

    private List<String> proveNegation(Node curr, Map<String, Boolean> values) {

    }

    private List<String> proveDisjunction(Node curr, Map<String, Boolean> values) {
        boolean left = curr.getLeft().evaluate(values),right = curr.getRight().evaluate(values),middle = curr.evaluate(values);
        List<String> answer;
        answer = proveNode(curr.getLeft(),values);
        String strA = answer.get(answer.size() - 1);
        answer.addAll(proveNode(curr.getRight(),values));
        String strB = answer.get(answer.size() - 1);
        if (!left) {
            strA = strA.substring(1);
        }
        if (!right) {
            strB = strB.substring(1);
        }
        String strCurr = "(" + strA + curr.asString() + strB + ")";
        if (middle) {
            answer.add(String.format("%1$S->%2$S->%3$S",strA,strB,strCurr));
            answer.add(String.format("%1$S->%2$S",strB,strCurr));
            answer.add(String.format("%1$S",strCurr));
        } else {
            if (!left) {
                answer.add(String.format("(%1$S->%2$S)->(%1$S->!%2$S)->!%1$S",strCurr,strA));
                answer.add(String.format("%1$S->%2$S",strCurr,strA));
                answer.add(String.format("!%2$S->%1$S->!%2$S",strCurr,strA));
                answer.add(String.format("(%1$S->!%2$S)->!%1$S",strCurr,strA));
                answer.add("!" + strCurr);
            } else {
                answer.add(String.format("(%1$S->%2$S)->(%1$S->!%2$S)->!%1$S",strCurr,strB));
                answer.add(String.format("%1$S->%2$S",strCurr,strB));
                answer.add(String.format("!%2$S->%1$S->!%2$S",strCurr,strB));
                answer.add(String.format("(%1$S->!%2$S)->!%1$S",strCurr,strB));
                answer.add("!" + strCurr);
            }
        }
        return answer;
    }

    private List<String> proveConjunction(Node curr, Map<String, Boolean> values) {

    }

    private List<String> proveImplication(Node curr, Map<String,Boolean> values) {

    }
}