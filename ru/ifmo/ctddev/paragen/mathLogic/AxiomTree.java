package ru.ifmo.ctddev.paragen.mathLogic;


import java.util.HashMap;
import java.util.Map;

public class AxiomTree extends ExpressionTree{
    public AxiomTree(String s) {
        super(s);
    }

    @Override
    public boolean equals(ExpressionTree another) {
        if (head == null || another.head == null) {
            return false;
        }
        Map<String, Node> map = new HashMap<>();
        return check(head,another.head,map);
    }

    private boolean check(Node first, Node second,Map<String,Node> map) {
        if (first == null) {
            return first == second;
        }
        if (first.isVariable()) {
            if (map.containsKey(first.asString())) {
                return check(map.get(first.asString()),second);
            } else {
                map.put(first.asString(),second);
                return true;
            }
        } else {
            return first.equals(second)&&check(first.getLeft(),second.getLeft(),map)&&check(first.getRight(),second.getRight(),map);
        }
    }
}
