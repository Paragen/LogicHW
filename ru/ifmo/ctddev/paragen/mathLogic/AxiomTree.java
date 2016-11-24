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
        if (first.isLeaf) {
            if (map.containsKey(first.varName)) {
                return check(map.get(first.varName),second);
            } else {
                map.put(first.varName,second);
                return true;
            }
        } else {
            return first.equals(second)&&check(first.leftSon,second.leftSon,map)&&check(first.rightSon,second.rightSon,map);
        }
    }
}
