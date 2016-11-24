package ru.ifmo.ctddev.paragen.mathLogic;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


public class Task2 extends ExpressionParser {


    List<ExpressionTree> assumptionList;
    List<String> sourceAxioms, sourceProof, sourceAssumption;
    String A;
    ExpressionTree pA;
    PrintWriter writer;

    public Task2() {

        super();
        assumptionList = new ArrayList<>();
        sourceProof = new ArrayList<>();
        sourceAxioms = setAxioms();
        sourceAssumption = new ArrayList<>();

    }

    public void run(String in, String out) {

        try {
            writer = new PrintWriter(out);
            final BufferedReader reader = new BufferedReader(new FileReader(in));
            String header = reader.readLine();
            String[] array = header.split(",");
            for (int i = 0; i < array.length - 1; ++i) {
                assumptionList.add(new ExpressionTree(array[i]));
                sourceAssumption.add("(" + array[i] + ")");
            }
            String[] buf = (array[array.length - 1].split("\\|-"));
            A = "(" + buf[0] + ")";
            pA = new ExpressionTree(buf[0]);
            reader.lines().forEachOrdered(this::check);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void check(String s) {
        ExpressionTree expr = new ExpressionTree(s);
        int num = checkAxioms(expr);
        s = "(" + s + ")";
        if (num != 0) {

            writer.println(s);
            writer.println(s + "->" + A + "->" + s);


        } else if ((num = checkAS(expr)) != -1) {
            String tmp = sourceAssumption.get(num);
            writer.println(tmp + "->" + A + "->" + tmp);
            writer.println(tmp);
        } else {

            if (expr.equals(pA)) {

                writer.println(s + "->" + s + "->" + s);
                writer.println(String.format("(%1$S->(%1$S->%1$S))->(%1$S->(%1$S->%1$S)->%1$S)->(%1$S->%1$S)", s));
                writer.println(String.format("(%1$S->(%1$S->%1$S)->%1$S)->(%1$S->%1$S)", s));
                writer.println(String.format("%1$S->(%1$S->%1$S)->%1$S", s));


            } else {
                Pair pair = checkMP(expr);
                if (pair != null) {
                    String first = "(" + sourceProof.get(pair.first - 1) + ")";
                    writer.println(String.format("(%1$S->%2$S)->(%1$S->%2$S->%3$S)->(%1$S->%3$S)", A, first, s));
                    writer.println(String.format("(%1$S->%2$S->%3$S)->(%1$S->%3$S)", A, first, s));

                } else {
                    System.err.println("Something wrong");
                }
            }

        }

        writer.println(A + "->" + s);
        proofList.add(expr);
        sourceProof.add(s);

    }

    private int checkAS(ExpressionTree curr) {
        for (int i = 0; i < assumptionList.size(); ++i) {
            if (assumptionList.get(i).equals(curr)) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        String s = "";
        for (int i = 1; i < 4; ++i) {
            new Task2().run("/home/ouroboros/gitRepositories/logic2014/tests/HW2/contra" + s + ".in", "/home/ouroboros/contra"+ s + ".out");
            new ExpressionParser().run("/home/ouroboros/contra" + s + ".out","/home/ouroboros/checkedContra" + s + ".out");
            s = Integer.toString(i);
        }

    }

}
