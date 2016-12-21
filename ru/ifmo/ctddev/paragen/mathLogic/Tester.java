package ru.ifmo.ctddev.paragen.mathLogic;


import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.Scanner;

public class Tester {
    public static void main(String[] args) {
        if (args.length >=3 ) {
            switch (Integer.parseInt(args[0])) {
                case 1:
                    new ExpressionParser().run(args[1],args[2]);
                    break;
            case 2:
                    new Task2().run(args[1],args[2]);
                    break;
            case 3:
                    new Task3().run(args[1],args[2]);
                    break;
            case 4:
                    new Task4().run(args[1],args[2]);
                    break;
                case 5:
                    new Task5().run(args[1],args[2]);
                    break;
            }
            return ;
        }
        final String in = "/home/ouroboros/gitRepositories/logic2014/tests/" , out = "/home/ouroboros/answers/";
        long inTime ;
        int num = 1;
        if (args.length == 0) {
            Scanner scanner = new Scanner(System.in);
            num =scanner.nextInt();
            scanner.close();
        } else {
            num = Integer.parseInt(args[0]);
        }
        switch (num) {
            case 2:

                inTime = System.currentTimeMillis();
                new Task2().run(in + "HW2/contra" +  ".in", out + "hw2/contra"  + ".out" );
                System.out.println("Done in: " + (((double) System.currentTimeMillis() - inTime) / 1000) + " s");

                for (int i = 1; i < 3; ++i) {


                    inTime = System.currentTimeMillis();
                    new Task2().run(in + "HW2/contra" + i + ".in", out + "hw2/contra" + i + ".out" );
                    System.out.println("Done in: " + (((double) System.currentTimeMillis() - inTime) / 1000) + " s");
                }

                break;
            case  1:
                for (int i = 1; i < 7; ++i) {
                    if (i == 2) {
                        ++i;
                    }
                    System.out.println("Testing good " + i);
                    inTime = System.currentTimeMillis();
                    new ExpressionParser().run(in + "HW1/good" + i + ".in", out + "hw1/good" + i + ".out" );
                    System.out.println("Done in: " + (((double) System.currentTimeMillis() - inTime) / 1000) + " s");
                }
                for (int i = 1; i < 7; ++i) {
                    System.out.println("Testing wrong " + i);
                    inTime = System.currentTimeMillis();
                    new ExpressionParser().run(in + "HW1/wrong" + i + ".in", out + "hw1/wrong" + i + ".out" );
                    System.out.println("Done in: " + (((double) System.currentTimeMillis() - inTime) / 1000) + " s");

                }
                break;
            case  3:

                for (int i = 1; i < 8; ++i) {

                    System.out.println("Testing good " + i);
                    inTime = System.currentTimeMillis();
                    new Task3().run(in + "HW3/true" + i + ".in", out + "hw3/true" + i + ".out" );
                    System.out.println("Done in: " + (((double) System.currentTimeMillis() - inTime) / 1000) + " s");
                }
                for (int i = 1; i < 2; ++i) {
                    System.out.println("Testing wrong " + i);
                    inTime = System.currentTimeMillis();
                    new Task3().run(in + "HW3/false" + i + ".in", out + "hw3/false" + i + ".out" );
                    System.out.println("Done in: " + (((double) System.currentTimeMillis() - inTime) / 1000) + " s");

                }
                break;
            case  4:
                for (int i = 1; i < 16; ++i) {
                    if (i == 3) {
                        i = 5;
                    }
                    System.out.println("Testing good " + i);
                    inTime = System.currentTimeMillis();
                    new Task4().run(in + "HW4/correct" + i + ".in", out + "hw4/correct" + i + ".out" );
                    System.out.println("Done in: " + (((double) System.currentTimeMillis() - inTime) / 1000) + " s");
                }
                for (int i = 1; i < 11; ++i) {
                    System.out.println("Testing wrong " + i);
                    inTime = System.currentTimeMillis();
                    new Task4().run(in + "HW4/incorrect" + i + ".in", out + "hw4/incorrect" + i + ".out" );
                    System.out.println("Done in: " + (((double) System.currentTimeMillis() - inTime) / 1000) + " s");

                }
                break;
            case  5:
                for (int i = 1; i < 4; ++i) {
                    inTime = System.currentTimeMillis();
                    new Task5().run(out + "test5/test" + i , out + "hw5/test" + i + ".out" );
                    System.out.println("Done in: " + (((double) System.currentTimeMillis() - inTime) / 1000) + " s");
                }
                break;
            default:
                System.out.println("nope");
        }
    }
}
