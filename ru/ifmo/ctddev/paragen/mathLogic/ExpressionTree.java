package ru.ifmo.ctddev.paragen.mathLogic;


import java.io.*;
import java.util.LinkedList;
import java.util.List;


public class ExpressionTree {

    Node head;

    protected class Node {
        boolean isLeaf;
        char opID;
        String varName;
        Node leftSon, rightSon;

        Node(char opID, Node left, Node right) {
            this.opID = opID;
            isLeaf = false;
            leftSon = left;
            rightSon = right;
        }

        Node(String varName) {
            this.varName = varName;
            isLeaf = true;
        }

        boolean equals(Node another) {
            if (another == null || isLeaf != another.isLeaf) {
                return false;
            }

            if (isLeaf) {
                return varName.equals(another.varName);
            } else {
                return opID == another.opID;
            }
        }
    }

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


    public ExpressionTree(String s) {
        head = parse(s);
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
                    res = new Node(expr.data.substring(expr.shift - length - 1, expr.shift - 1));
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
                res = new Node(ch, res, parseExpr(expr, val));

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

    private int checkDepth(Node val) {
        int l = 0, r = 0;
        if (val.isLeaf) {
            return 1;
        }
        if (val.leftSon != null) {
            l = checkDepth(val.leftSon);
        }
        if (val.rightSon != null) {
            r = checkDepth(val.rightSon);
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

        int sons = 0, curr = 1, shift = 0, save ;
        while (!list.isEmpty()) {

            space = spaceList.remove(0);
            save = saveList.remove(0);
            val = list.remove(0);
            for (int i = 0; i < space - shift; ++i) {
                printWriter.print(' ');
            }
            shift = space;
            if (val.isLeaf) {
                printWriter.print(val.varName);
            } else {
                printWriter.print(val.opID);
            }

            if (val.leftSon != null) {
                list.add(val.leftSon);
                spaceList.add((space + save) / 2);
                saveList.add(save);
                ++sons;
            }
            if (val.rightSon != null) {
                list.add(val.rightSon);
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
        if (head != null && !head.isLeaf && head.opID == '-') {
            if (check(head.rightSon,curr.head)) {
                return new ExpressionTree(head.leftSon);
            }
        }
        return null;
    }

    public boolean equals(ExpressionTree another) {
        if (head == null || another.head == null) {
            return false;
        }
        return check(head,another.head);
    }

    protected boolean check(Node first, Node second) {
        if (first == null) {
            return first == second;
        }
        if (first.isLeaf) {
            return first.equals(second);
        } else {
            return first.equals(second)&&check(first.leftSon,second.leftSon)&&check(first.rightSon,second.rightSon);
        }
    }

}