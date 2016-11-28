package ru.ifmo.ctddev.paragen.mathLogic;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExpressionParser {

    protected List<ExpressionTree> axiomList;
    protected List<ExpressionTree> proofList;

    ExpressionParser() {
        axiomList = new ArrayList<>();
        setAxioms().forEach((String s)-> {
            axiomList.add(new AxiomTree(s));
        });
        proofList = new ArrayList<>();
    }

    protected List<String> setAxioms() {
        List<String> str = Arrays.asList("(a->b->a",
                "(a->b)->(a->b->c)->(a->c)",
                "a->b->a&b",
                "a&b->a",
                "a&b->b",
                "a->a|b",
                "b->a|b",
                "(a->c)->(b->c)->(a|b->c)",
                "(a->b)->(a->!b)->!a",
                "!!a->a");
        return str;
    }
    long parseTime = 0, checkTime = 0;
    void run(String in, String out) {
        try {
            final PrintWriter writer = new PrintWriter(new File(out));
            final BufferedReader reader = new BufferedReader(new FileReader(new File(in)));
            String s;
            for (int i = 1; (s = reader.readLine()) != null; ++i) {
                writer.println("(" + i + ") " + s + " (" + check(s) + ")");
            }
            writer.close();
            System.out.println("parse: " + ((double)parseTime) / 1000 + " check: " + ((double)checkTime / 1000));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String check(String s) {
        long in = System.currentTimeMillis();
        ExpressionTree curr = new ExpressionTree(s);
        parseTime += System.currentTimeMillis() - in;
        in = System.currentTimeMillis();
        String ans = "Не доказано";
        int num = checkAxioms(curr);

        if (num != 0) {
            ans =  "Сх. акс. " + num;
        } else {

            Pair pair = checkMP(curr);

            if (pair != null) {
                ans =  "M.P. " + pair.first + ", " + pair.second;
            }
        }

        proofList.add(curr);
        checkTime += System.currentTimeMillis() - in;
        return ans;
    }

    protected int checkAxioms(ExpressionTree curr) {
        for (int i = 0; i < 10; ++i) {
            if (axiomList.get(i).equals(curr)) {
                return i+1;
            }
        }
        return 0;
    }

    protected Pair checkMP(ExpressionTree curr) {

        Pair pair = new Pair();
        ExpressionTree tmp;
        for (int i = 0; i < proofList.size(); ++i) {
            tmp = proofList.get(i).checkMP(curr);
            if (tmp != null) {
                for (int j = 0; j < proofList.size(); ++j) {
                    if (tmp.equals(proofList.get(j))) {
                        pair.first = j + 1;
                        pair.second = i + 1;
                        return pair;
                    }
                }
            }
        }


        return null;
    }

    protected class Pair {
        int first,second;
    }
    public static void main(String[] args) {

        final String in = "/home/ouroboros/gitRepositories/logic2014/tests/HW1/" , out = "/home/ouroboros/";
        long inTime ;
        for (int i = 1; i < 7; ++i) {
            if (i == 2) {
                ++i;
            }
            System.out.println("Testing good " + i);
            inTime = System.currentTimeMillis();
            new ExpressionParser().run(in + "good" + i + ".in", out + "good" + i + ".out" );
            System.out.println("Done in: " + (((double) System.currentTimeMillis() - inTime) / 1000) + " s");
        }
        for (int i = 1; i < 7; ++i) {
            System.out.println("Testing wrong " + i);
            inTime = System.currentTimeMillis();
            new ExpressionParser().run(in + "wrong" + i + ".in", out + "wrong" + i + ".out" );
            System.out.println("Done in: " + (((double) System.currentTimeMillis() - inTime) / 1000) + " s");

        }
    }
}


