package ru.ifmo.ctddev.paragen.mathLogic;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


public class Task2 extends ExpressionParser {


    List<ExpressionTree> assumptionList;
    ExpressionTree A;
    PrintWriter writer;

    public Task2() {
        super();

    }

    public void run(String in, String out) {

        try {
            writer = new PrintWriter(out);
            final BufferedReader reader = new BufferedReader(new FileReader(in));
            List<ExpressionTree> assumption = new ArrayList<>();
            String header = reader.readLine();
            String[] array = header.split(",");
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < array.length - 1; ++i) {
                assumption.add(new ExpressionTree(array[i]));
                builder.append(array[i]);
                if (i < array.length - 2)  {
                    builder.append(',');
                }
            }
            assumption.add(new ExpressionTree((array[array.length - 1].split("\\|-"))[0]));
            List<ExpressionTree> oldProve = new ArrayList<>();
            reader.lines().forEachOrdered(s->oldProve.add(new ExpressionTree(s)));
            builder.append("|-").append(assumption.get(assumption.size() - 1).asString()).append("->" + oldProve.get(oldProve.size() - 1).asString());
            writer.println(builder.toString());
            deduction(assumption,oldProve).forEach(writer::println);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    List<String> deduction(List<ExpressionTree> assumptions, List<ExpressionTree> oldProve) {
        List<String> ans = new ArrayList<>();
        A = assumptions.get(assumptions.size() - 1);
        assumptionList = assumptions.subList(0,assumptions.size() -1);
        oldProve.forEach(s->{ans.addAll(check(s));});
        return ans;
    }
    private List<String> check(ExpressionTree expr) {
        int num = checkAxioms(expr);
        List<String> ans = new ArrayList<>();
        String s = expr.asString();
        if (num != 0) {

            ans.add(s);
            ans.add(s + "->" + A.asString()+ "->" + s);


        } else if ((num = checkAS(expr)) != -1) {
            String tmp = assumptionList.get(num).asString();
            ans.add(tmp + "->" + A.asString() + "->" + tmp);
            ans.add(tmp);
        } else {

            if (expr.equals(A)) {

                ans.add(s + "->" + s + "->" + s);
                ans.add(String.format("(%1$S->(%1$S->%1$S))->(%1$S->(%1$S->%1$S)->%1$S)->(%1$S->%1$S)", s));
                ans.add(String.format("(%1$S->(%1$S->%1$S)->%1$S)->(%1$S->%1$S)", s));
                ans.add(String.format("%1$S->(%1$S->%1$S)->%1$S", s));


            } else {
                Pair pair = checkMP(expr);
                if (pair != null) {
                    String first =  proofList.get(pair.first - 1).asString();
                    ans.add(String.format("(%1$S->%2$S)->(%1$S->%2$S->%3$S)->(%1$S->%3$S)", A.asString(), first, s));
                    ans.add(String.format("(%1$S->%2$S->%3$S)->(%1$S->%3$S)", A.asString(), first, s));

                } else {
                    System.err.println("Something wrong");
                }
            }

        }

        ans.add(A.asString() + "->" + s);
        proofList.add(expr);

        return ans;

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
