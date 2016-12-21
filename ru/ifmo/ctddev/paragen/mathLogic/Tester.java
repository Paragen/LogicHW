package ru.ifmo.ctddev.paragen.mathLogic;




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

        } else {
            System.out.println("Usage : [task number] [input] [output]");
        }
    }
}
