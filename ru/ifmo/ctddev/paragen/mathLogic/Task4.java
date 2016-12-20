package ru.ifmo.ctddev.paragen.mathLogic;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Task4 {

    PrintWriter writer;
    BufferedReader reader;

    List<String> splitByComa(String s) {
        int balance = 0, last = 0;
        List<String> answer = new ArrayList<>();
        for (int i = 0; i < s.length(); ++i) {
            switch (s.charAt(i)) {
                case '(' :
                    ++balance;
                    break;
                case ')' :
                    --balance;
                    break;
                case ',' :
                    if (balance == 0) {
                        answer.add(s.substring(last,i));
                        last = i + 1;
                    }
                    break;
            }
        }
        answer.add(s.substring(last));
        return answer;
    }

    public void run(String in, String out) {
        try {
            writer = new PrintWriter(new FileWriter(out));
            reader = new BufferedReader(new FileReader(in));
            String header = reader.readLine(),a = "",b;
            String[] buf = header.split("\\|-");
            boolean flag = !buf[0].equals("");
            b = buf[1];
            Checker checker = new Checker();
            FormalParser parser = new FormalParser();
            List<String> tmp = null;
            if (flag) {
                tmp = splitByComa(buf[0]);
                a = tmp.get(tmp.size() - 1);
                checker.setAssumptions(tmp.stream().map(parser::parse).collect(Collectors.toList()));
            }
            boolean correct = true;
            String message = "";
            List<Node> answer = new ArrayList<>();
            List<String> lines = new ArrayList<>();
            while ((header = reader.readLine()) != null) {
                correct = checker.check(parser.parse(header),flag);
                if (!correct) {
                    message = checker.getError();
                    break;
                } else if (flag) {
                    answer.addAll(checker.getProve());
                } else {
                    lines.add(header);
                }
            }
            if (correct) {
                if (flag) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < tmp.size() - 1; ++i) {
                        if (i != 0) {
                            builder.append(",");
                        }
                        builder.append(tmp.get(i));
                    }
                    builder.append("|-").append('(').append(a).append(')').append("->").append('(').append(b).append(')');
                    writer.println(builder.toString());
                    answer.forEach(node -> writer.println(node.toString()));
                } else {
                    writer.println("|-" + b);
                    lines.forEach(writer::println);
                }
            } else {
                writer.println(message);
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public static void main(String[] args) {

    }
}
