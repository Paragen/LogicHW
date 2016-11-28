package ru.ifmo.ctddev.paragen.mathLogic;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Task3 {
    void run(String in, String out) {
        try {
            PrintWriter writer = new PrintWriter(in);
            BufferedReader reader = new BufferedReader(new FileReader(out));
            ExpressionTree expr = new ExpressionTree(reader.readLine());
            explore(expr).forEach(writer::println);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    List<String> explore(ExpressionTree expr) {
        String s = isValidity(expr);
        if (s != null) {
            List<String> tmp = new ArrayList<>();
            tmp.add(s);
            return tmp;
        } else {
            List<String> answer = new ArrayList<>();
            prove(expr,expr.variableList(),new ArrayList<>()).forEach(tmp -> answer.add(tmp.asString()));
            return answer;
        }
    }

    private String isValidity(ExpressionTree expr) {
        List<String> variables = expr.variableList();
        Map<String,Boolean> values = new HashMap<>();
        for (String s :
                variables) {
            values.put(s,false);
        }
        boolean flag = true;
        while(true) {
            if (!expr.evaluate(values)) {
                flag = false;
                break;
            }
            boolean tmp = false;
            for (int i = 0; i < variables.size(); ++i) {
                if (!values.get(variables.get(i))) {
                    values.put(variables.get(i),true);
                    for (int j = 0; j < i; ++j) {
                        values.put(variables.get(j),false);
                    }
                    tmp = true;
                }
            }
            if (!tmp) {
                break;
            }
        }
        if (!flag) {
            StringBuilder builder = new StringBuilder();
            builder.append("Высказывание ложно при ");
            for (int i = 0; i < variables.size(); ++i) {
                builder.append(variables.get(i)).append('=').append((values.get(variables.get(i))) ? 'И' : 'Л');
                if (i + 1 != variables.size()) {
                    builder.append(", ");
                }
            }
            return builder.toString();
        } else {
            return null;
        }

    }

    private List<ExpressionTree> prove(ExpressionTree toProve,List<String> variables,List<ExpressionTree> assumption) {
        if (variables.isEmpty()) {
            return toProve.prove(toValue(assumption)).stream().map(ExpressionTree::new).collect(Collectors.toList());
        } else {
            List<ExpressionTree> ans = new ArrayList<>();
            String var = variables.remove(variables.size() - 1);
            int size = assumption.size();
            assumption.add(new ExpressionTree(var));
            List<ExpressionTree> list = prove(toProve,variables,assumption);
            new Task2().deduction(assumption,list).forEach(s-> ans.add(new ExpressionTree(s)));
            assumption.remove(size);
            assumption.add(new ExpressionTree("!" + var));
            list = prove(toProve,variables,assumption);
            new Task2().deduction(assumption,list).forEach(s -> ans.add(new ExpressionTree(s)));
            assumption.remove(size);
            variables.add(var);

            List<String> buf = new ArrayList<>();
            buf.add(String.format("(%1$S->%2$S)->(!%1$S->%2$S)->(%1$S|!%1$S->%2$S)",var,toProve.asString()));
            buf.add(String.format("(!%1$S->%2$S)->(%1$S|!%1$S->%2$S)",var,toProve.asString()));
            buf.add(String.format("%1$S|!%1$S->%2$S",var,toProve.asString()));
            buf.addAll(orNot(var));
            buf.add(toProve.asString());

            buf.forEach(s->ans.add(new ExpressionTree(s)));
            return ans;
        }
    }

    private List<String> orNot(String variable) {
        List<String> answer = new ArrayList<>();

        answer.add(String.format("%1$S->%1$S|!%1$S",variable));
        answer.addAll(counterRule(new ExpressionTree(variable),new ExpressionTree(variable + "|!" + variable)));
        answer.add(String.format("!(%1$S|!%1$S)->!%1$S",variable));

        answer.add(String.format("!%1$S->%1$S|!%1$S",variable));
        answer.addAll(counterRule(new ExpressionTree("!" + variable),new ExpressionTree(variable + "|!" + variable)));
        answer.add(String.format("!(%1$S|!%1$S)->!!%1$S",variable));

        answer.add(String.format("(!(%1$S|!%1$S)->!%1$S)->(!(%1$S|!%1$S)->!!%1$S)->!!(%1$S|!%1$S)",variable));
        answer.add(String.format("(!(%1$S|!%1$S)->!!%1$S)->!!(%1$S|!%1$S)",variable));
        answer.add(String.format("!!(%1$S|!%1$S)",variable));
        answer.add(String.format("!!(%1$S|!%1$S)->(%1$S|!%1$S)",variable));
        answer.add(String.format("%1$S|!%1$S",variable));

        return answer;
    }

    private List<String> counterRule(ExpressionTree first, ExpressionTree second) {
        List<ExpressionTree> assumption = new ArrayList<>();
        assumption.add(new ExpressionTree(first.asString()+"->"+second.asString()));
        assumption.add(new ExpressionTree("!" + second.asString()));
        List<String> oldProve = new ArrayList<>();
        oldProve.add(String.format("(%1$S->%2$S)->(%1$S->!%2$S)->!%1$S",first.asString(),second.asString()));
        oldProve.add(String.format("%1$S->%2$S",first.asString(),second.asString()));
        oldProve.add(String.format("(%1$S->!%2$S)->!%1$S",first.asString(),second.asString()));
        oldProve.add(String.format("!%1$S->%2$S->!%1$S",second.asString(),first.asString()));
        oldProve.add("!"+second.asString());
        oldProve.add(String.format("%1$S->%2$S",first.asString(),second.asString()));
        oldProve.add("!" + first.asString());
        oldProve = new Task2().deduction(assumption,oldProve.stream().map(ExpressionTree::new).collect(Collectors.toList()));
        assumption.remove(1);
        return new Task2().deduction(assumption,oldProve.stream().map(ExpressionTree::new).collect(Collectors.toList()));
    }

    private Map<String,Boolean> toValue(List<ExpressionTree> list) {
        Map<String,Boolean> map = new HashMap<>();
        for (ExpressionTree curr :
                list) {
            if (curr.head.isVariable()) {
                map.put(curr.head.asString(), true);
            } else {
                map.put(curr.head.getRight().asString(), false);
            }
        }
        return map;
    }



}
