package ru.ifmo.ctddev.paragen.mathLogic;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class Task5 {

    String toFormal(int num) {
        StringBuilder builder = new StringBuilder();
        builder.append('0');
        for (int i = 0; i < num; ++i) {
            builder.append('\'');
        }
        return builder.toString();
    }

    void run(String in, String out) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(in));
            PrintWriter writer = new PrintWriter(out);
            String s = reader.readLine();

            String[] buf = s.split(" ");
            int first, second;
            first = Integer.parseInt(buf[0]);
            second = Integer.parseInt(buf[1]);
            String sfirst = toFormal(first), ssecond = toFormal(second);

            writer.println("|-" + sfirst + '+' + ssecond + '=' + toFormal(first + second));


            partOne(sfirst, writer);
            if (second > 0) {
                aEqualsA(writer);
            }

            for (int i = 0; i < second ; ++i) {
                partTwo(sfirst, toFormal(i), toFormal(first + i ), writer);
            }


            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void partOne(String term, PrintWriter writer) {
        String s = "(A->A->A)";

        String[] buf = {
                s,
                "a+0=a",
                "(a+0=a)->" + s + "->(a+0=a)",
                s + "->(a+0=a)",
                s + "->@a(a+0=a)",
                "@a(a+0=a)",
                "@a(a+0=a)->(" + term + "+0=" + term + ")",
                term + "+0=" + term
        };
        Arrays.asList(buf).forEach(writer::println);
    }

    private void partTwo(String t1, String t2, String t3, PrintWriter writer) {
        String s = "(A->A->A)";

        String[] buf = {
                "a=b->a'=b'",
                "(a=b->a'=b')->" + s + "->(a=b->a'=b')",
                s + "->(a=b->a'=b')",
                s + "->@b(a=b->a'=b')",
                s + "->@a@b(a=b->a'=b')",
                "@a@b(a=b->a'=b')",
        };
        Arrays.asList(buf).forEach(writer::println);

        writer.println(String.format("@a@b(a=b->a'=b')->@b(%1$s+%2$s=b->(%1$s+%2$s)'=b')", t1, t2));
        writer.println(String.format("@b(%1$s+%2$s=b->(%1$s+%2$s)'=b')", t1, t2));
        writer.println(String.format("@b(%1$s+%2$s=b->(%1$s+%2$s)'=b')->(%1$s+%2$s=%3$s->(%1$s+%2$s)'=%3$s')", t1, t2, t3));
        writer.println(String.format("(%1$s+%2$s=%3$s->(%1$s+%2$s)'=%3$s')", t1, t2, t3));
        writer.println(String.format("(%1$s+%2$s)'=%3$s'", t1, t2, t3)); // (t1 + t2)' = t3'

        String[] buf1 = {
                "a+b'=(a+b)'",
                "(a+b'=(a+b)')->" + s + "->(a+b'=(a+b)')",
                s + "->(a+b'=(a+b)')",
                s + "->@b(a+b'=(a+b)')",
                s + "->@a@b(a+b'=(a+b)')",
                "@a@b(a+b'=(a+b)')"
        };
        Arrays.asList(buf1).forEach(writer::println);

        writer.println(String.format("@a@b(a+b'=(a+b)')->@b(%1$s+b'=(%1$s+b)')", t1));
        writer.println(String.format("@b(%1$s+b'=(%1$s+b)')", t1));
        writer.println(String.format("@b(%1$s+b'=(%1$s+b)')->(%1$s+%2$s'=(%1$s+%2$s)')", t1, t2));
        writer.println(String.format("%1$s+%2$s'=(%1$s+%2$s)'", t1, t2)); // t1 + t2' = (t1 + t2)'

        String[] buf2 = {
                "a=b->a=c->b=c",
                "(a=b->a=c->b=c)->" + s + "->(a=b->a=c->b=c)",
                s + "->(a=b->a=c->b=c)",
                s + "->@c(a=b->a=c->b=c)",
                s + "->@b@c(a=b->a=c->b=c)",
                s + "->@a@b@c(a=b->a=c->b=c)",
                "@a@b@c(a=b->a=c->b=c)"
        };
        Arrays.asList(buf2).forEach(writer::println);

        writer.println(String.format("@a@b@c(a=b->a=c->b=c)->@b@c((%1$s+%2$s)'=b->(%1$s+%2$s)'=c->b=c)", t1, t2));
        writer.println(String.format("@b@c((%1$s+%2$s)'=b->(%1$s+%2$s)'=c->b=c)", t1, t2));
        writer.println(String.format("@b@c((%1$s+%2$s)'=b->(%1$s+%2$s)'=c->b=c)->@c((%1$s+%2$s)'=(%1$s+%2$s')->(%1$s+%2$s)'=c->(%1$s+%2$s')=c)", t1, t2));
        writer.println(String.format("@c((%1$s+%2$s)'=(%1$s+%2$s')->(%1$s+%2$s)'=c->(%1$s+%2$s')=c)", t1, t2));
        writer.println(String.format("@c((%1$s+%2$s)'=(%1$s+%2$s')->(%1$s+%2$s)'=c->(%1$s+%2$s')=c)->((%1$s+%2$s)'=(%1$s+%2$s')->(%1$s+%2$s)'=%3$s'->(%1$s+%2$s')=%3$s')", t1, t2, t3));
        writer.println(String.format("((%1$s+%2$s)'=(%1$s+%2$s')->(%1$s+%2$s)'=%3$s'->(%1$s+%2$s')=%3$s')", t1, t2, t3)); // (t1 + t2)' = t1 + t2' -> (t1 + t2)' = t3' -> t1 + t2' = t3'

        String[] buf3 = {
                "a=b->a=c->b=c",
                "(a=b->a=c->b=c)->" + s + "->(a=b->a=c->b=c)",
                s + "->(a=b->a=c->b=c)",
                s + "->@c(a=b->a=c->b=c)",
                s + "->@b@c(a=b->a=c->b=c)",
                s + "->@a@b@c(a=b->a=c->b=c)",
                "@a@b@c(a=b->a=c->b=c)"
        };
        Arrays.asList(buf3).forEach(writer::println);

        writer.println(String.format("@a@b@c(a=b->a=c->b=c)->@b@c((%1$s+%2$s')=b->(%1$s+%2$s')=c->b=c)", t1, t2));
        writer.println(String.format("@b@c((%1$s+%2$s')=b->(%1$s+%2$s')=c->b=c)", t1, t2));
        writer.println(String.format("@b@c((%1$s+%2$s')=b->(%1$s+%2$s')=c->b=c)->@c((%1$s+%2$s')=(%1$s+%2$s)'->(%1$s+%2$s')=c->(%1$s+%2$s)'=c)", t1, t2));
        writer.println(String.format("@c((%1$s+%2$s')=(%1$s+%2$s)'->(%1$s+%2$s')=c->(%1$s+%2$s)'=c)", t1, t2));
        writer.println(String.format("@c((%1$s+%2$s')=(%1$s+%2$s)'->(%1$s+%2$s')=c->(%1$s+%2$s)'=c)->((%1$s+%2$s')=(%1$s+%2$s)'->(%1$s+%2$s')=%1$s+%2$s'->(%1$s+%2$s)'=%1$s+%2$s')", t1, t2));
        writer.println(String.format("((%1$s+%2$s')=(%1$s+%2$s)'->(%1$s+%2$s')=%1$s+%2$s'->(%1$s+%2$s)'=%1$s+%2$s')", t1, t2));
        writer.println(String.format("(%1$s+%2$s'=%1$s+%2$s')->((%1$s+%2$s)'=%1$s+%2$s')", t1, t2));

        writer.println("(a=a)->" + s + "->(a=a)");
        writer.println(s + "->(a=a)");
        writer.println(s + "->@a(a=a)");
        writer.println("@a(a=a)");
        writer.println(String.format("@a(a=a)->(%1$s+%2$s'=%1$s+%2$s')", t1, t2));
        writer.println(String.format("(%1$s+%2$s'=%1$s+%2$s')", t1, t2)); // t1 + t2' = t1 + t2'

        writer.println(String.format("((%1$s+%2$s)'=%1$s+%2$s')", t1, t2)); // (t1 + t2)' = t1 + t2'

        writer.println(String.format("(%1$s+%2$s)'=%3$s'->(%1$s+%2$s')=%3$s'", t1, t2, t3));
        writer.println(String.format("%1$s+%2$s'=%3$s'", t1, t2, t3));
    }

    private void aEqualsA(PrintWriter writer) {
        String s = "(A->A->A)";

        String[] buf = {
                "a=b->a=c->b=c",
                "(a=b->a=c->b=c)->" + s + "->(a=b->a=c->b=c)",
                s + "->(a=b->a=c->b=c)",
                s + "->@c(a=b->a=c->b=c)",
                s + "->@b@c(a=b->a=c->b=c)",
                s + "->@a@b@c(a=b->a=c->b=c)",
                "@a@b@c(a=b->a=c->b=c)",
                "@a@b@c(a=b->a=c->b=c)->@b@c(a+0=b->a+0=c->b=c)",
                "@b@c(a+0=b->a+0=c->b=c)",
                "@b@c(a+0=b->a+0=c->b=c)->@c(a+0=a->a+0=c->a=c)",
                "@c(a+0=a->a+0=c->a=c)",
                "@c(a+0=a->a+0=c->a=c)->(a+0=a->a+0=a->a=a)",
                "(a+0=a->a+0=a->a=a)",
                "a+0=a",
                "a+0=a->a=a",
                "a=a"
        };

        Arrays.asList(buf).forEach(writer::println);
    }

    public static void main(String[] args) {
        //removed
    }
}
