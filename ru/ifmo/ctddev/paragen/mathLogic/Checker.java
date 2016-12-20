package ru.ifmo.ctddev.paragen.mathLogic;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Checker {

    public  class Expression {
        private Node head;
        boolean errorFlag = false;

        Expression(String s) {
            head = new FormalParser().parse(s);
        }

        Expression(Node node) {
            head = node;
        }


        Node getHead() {
            return head;
        }

        @Override
        public boolean equals(Object object) {
            Expression expression = (Expression)object;
            return equals(head, expression.getHead());
        }

        private boolean equals(Node first, Node second) {
            if (first == null || second == null) {
                return first == second;
            }

            boolean flag = true;
            List<Node> firstList = first.getChildren(), secondList = second.getChildren();

            if (first.getClass() != second.getClass() || firstList.size() != secondList.size() || !first.equals(second)) {
                return false;
            }

            for (int i = 0; i < firstList.size(); ++i) {
                if (!equals(firstList.get(i), secondList.get(i))) {
                    flag = false;
                    break;
                }
            }

            return flag;
        }

        boolean equalsScheme(Expression expression) {
            return equalsScheme(head, expression.getHead(), new HashMap<String, Node>());
        }

        private boolean equalsScheme(Node first, Node second, Map<String, Node> map) {
            if (first == null || second == null) {
                return first == second;
            }

            Class current = first.getClass();

            if (current == Predicate.class) {
                if (map.containsKey(first.asString())) {
                    return equals(map.get(first.asString()), second);
                } else {
                    map.put(first.asString(), second);
                    return true;
                }
            } else if (current == second.getClass()) {
                boolean flag = true;
                List<Node> firstList = first.getChildren(), secondList = second.getChildren();
                if (firstList.size() != secondList.size() || !first.equals(second)) {
                    return false;
                }

                for (int i = 0; i < firstList.size(); ++i) {
                    if (!equalsScheme(firstList.get(i), secondList.get(i), map)) {
                        flag = false;
                        break;
                    }
                }
                return flag;
            }


            return false;
        }

        boolean equalsAxiom(Expression expression) {
            return equalsAxiom(head, expression.getHead(), new HashMap<String, String>());
        }

        private boolean equalsAxiom(Node first, Node second, Map<String, String> map) {
            if (first == null || second == null) {
                return first == second;
            }

            Class current;
            if ((current = first.getClass()) == second.getClass()) {
                if (current == FormalVariable.class) {
                    if (map.containsKey(first.asString())) {
                        return map.get(first.asString()).equals(second.asString());
                    } else {
                        map.put(first.asString(), second.asString());
                        return true;
                    }
                }
                boolean flag = true;
                List<Node> firstList = first.getChildren(), secondList = second.getChildren();
                if (firstList.size() != secondList.size() || !first.equals(second)) {
                    return false;
                }

                for (int i = 0; i < firstList.size(); ++i) {
                    if (!equalsAxiom(firstList.get(i), secondList.get(i), map)) {
                        flag = false;
                        break;
                    }
                }
                return flag;
            }

            return false;
        }

        Node availableToPut(Expression second, String varName) {
            Map<String,Node> map = new HashMap<>();
            if (!availableToPut(head, second.head, varName, map, new ArrayList<>())) {
                Node node = map.get(varName);
                if (node == null) {// костыль
                    return head;
                }

                return node;
            }
            return  null;
        }

        boolean availableToPut(Node first, Node second, String varName, Map<String, Node> assoc, List<String> unitive) {
            if (first == null || second == null) {
                return first == second;
            }
            Class aClass;
            if ((aClass = first.getClass()) == FormalVariable.class && first.asString().equals(varName) && !unitive.contains(varName)) {

                if (!assoc.containsKey(varName)) {
                    List<String> list = new ArrayList<>();
                    getVariableList(second, list);
                    boolean flag = true;
                    for (String str : list) {
                        if (unitive.contains(str)) {
                            flag = false;
                            break;
                        }
                    }
                    assoc.put(varName, second);
                    if (!flag) {
                        errorFlag = true;
                        return false;
                    }

                    return true;
                } else {
                    return equals(assoc.get(varName), second);
                }
            }

            boolean toDel = false;
            if (aClass == UniversalQuantifier.class || aClass == ExistentialQuantifier.class) {
                unitive.add(first.getLeft().asString());
                toDel = true;
            }

            boolean flag = true;
            List<Node> firstList = first.getChildren(), secondList = second.getChildren();
            if (aClass != second.getClass() || firstList.size() != secondList.size() || !first.equals(second)) {
                return false;
            }

            for (int i = 0; i < firstList.size(); ++i) {
                if (!availableToPut(firstList.get(i), secondList.get(i), varName, assoc, unitive)) {
                    flag = false;
                    break;
                }
            }

            if (toDel) {
                unitive.remove(unitive.size() - 1);
            }
            return flag;
        }

        Expression spell(Node target, Node distinct) {
            return new Expression(spell(head, target, distinct, new TreeSet<>()));
        }

        Node spell(Node current, Node target, Node distinct, Set<String> unitive) {
            if (current == null) {
                return null;
            }
            boolean flag = false;
            Class aClass = current.getClass();
            if (aClass == FormalVariable.class) {
                if (current.equals(target) && !unitive.contains(target.asString())) {
                    return distinct;
                }
            } else if (aClass == ExistentialQuantifier.class || aClass == UniversalQuantifier.class) {
                if (!unitive.contains(current.getLeft().asString())) {
                    flag = true;
                    unitive.add(current.getLeft().asString());
                }
            }
            List<Node> children = current.getChildren();
            Node node = (Node) current.clone();
            node.children = new ArrayList<>();
            for (int i = 0; i < children.size(); ++i) {
                node.children.add(spell(children.get(i), target, distinct, unitive));
            }

            if (flag) {
                unitive.remove(current.getLeft().asString());
            }

            return node;
        }

        List<String> getVariableList() {
            List<String> list = new ArrayList<>();
            getVariableList(head, list);
            return list.stream().distinct().collect(Collectors.toList());
        }

        void getVariableList(Node node, List<String> list) {
            if (node.getClass() == FormalVariable.class) {
                list.add(node.asString());
                return;
            }

            List<Node> tmp = node.getChildren();
            for (Node val : tmp) {
                getVariableList(val, list);
            }
        }

        List<String> getFree() {
            List<String> answer = new ArrayList<>();
            getFree(head, new TreeSet<String>(), answer);
            return answer.stream().distinct().collect(Collectors.toList());
        }

        private void getFree(Node node, Set<String> set, List<String> answer) {
            if (node == null) {
                return;
            }

            boolean flag = false;
            if (node.getClass() == UniversalQuantifier.class || node.getClass() == ExistentialQuantifier.class) {
                String s = node.getLeft().asString();
                if (!set.contains(s)) {
                    flag = true;
                    set.add(s);
                }
            }

            if (node.getClass() == FormalVariable.class) {
                if (!set.contains(node.asString())) {
                    answer.add(node.asString());
                }
            }

            for (Node tmp : node.getChildren()) {
                getFree(tmp, set, answer);
            }

            if (flag) {
                set.remove(node.getLeft().asString());
            }
        }


    }

    final private List<Expression> axioms, schemes, assumptions;
    private List<Expression> proofs, currentProve;
    private boolean errorFlag;
    private String msg;
    private Expression alpha;

    Checker() {

        errorFlag = false;
        msg = "";
        proofs = new ArrayList<>();
        assumptions = new ArrayList<>();

        schemes = Stream.of("(A->B->A)",
                "(A->B)->(A->B->C)->(A->C)",
                "A->B->A&B",
                "A&B->A",
                "A&B->B",
                "A->A|B",
                "B->A|B",
                "(A->C)->(B->C)->(A|B->C)",
                "(A->B)->(A->!B)->!A",
                "!!A->A").map(Expression::new).collect(Collectors.toList());

        axioms = Stream.of("a=b->a'=b'",
                "a=b->a=c->b=c",
                "a'=b'->a=b",
                "!a'=0",
                "a+b'=(a+b)'",
                "a+0=a",
                "a*0=a",
                "a*b'=a*b+a").map(Expression::new).collect(Collectors.toList());

    }

    void setAssumptions(List<Node> assumptions) {
        this.assumptions.clear();
        this.assumptions.addAll(assumptions.subList(0, assumptions.size() - 1).stream().map(Expression::new).collect(Collectors.toList()));
        alpha = new Expression(assumptions.get(assumptions.size() - 1));
    }


    void clear() {
        assumptions.clear();
        proofs.clear();
    }

    String getError() {
        if (!errorFlag) {
            return "Вывод некорректен начиная с формулы номер " + proofs.size() + msg;
        } else {
            return "Correct";
        }
    }

    List<Node> getProve() {
        return currentProve.stream().map(Expression::getHead).collect(Collectors.toList());
    }

    boolean check(Node node, boolean df) {
        msg = "";
        errorFlag = false;
        currentProve = new ArrayList<>();
        Expression expression = new Expression(node);
        boolean answer = checkAssumption(expression,df)|checkAxioms(expression, df) | checkRules(expression, df);
        proofs.add(expression);
        if (df) {
            currentProve.add(new Expression(new Implication(alpha.getHead(), node)));
        }
        errorFlag = answer;
        return answer;
    }

    boolean checkAssumption(Expression expression, boolean df) {
        if (assumptions.contains(expression)) {
            if (df) {
                currentProve.add(expression);
                currentProve.add(new Expression(new Implication(expression.getHead(), new Implication(alpha.getHead(), expression.getHead()))));
            }
            return  true;
        }

        if (df && expression.equals(alpha)) {
            currentProve.addAll(aToA(expression));
            return true;
        }
        return  false;
    }

    boolean checkRules(Expression expression, boolean df) {

        int num;
        if ((num = mp(expression)) != -1) {
            if (df) {
                currentProve.addAll(modusDeduction(expression.getHead(), proofs.get(num).getHead(), alpha.getHead()));
            }
            return true;
        }

        String tmp;
        if ((tmp = universalRule(expression)) != null) {
            if (df) {
                if (alpha.getFree().contains(tmp)) {
                    msg = ": используется правило с квантором по переменной " + tmp + ", входящей свободно в допущение " + alpha.getHead().toString();
                    return false;
                } else {
                    currentProve.addAll(universalDeduction(expression, alpha));
                }
            }
            return true;
        }

        if ((tmp = existenceRule(expression)) != null) {
            if (df) {
                if (alpha.getFree().contains(tmp)) {
                    msg = ": используется правило с квантором по переменной " + tmp + ", входящей свободно в допущение " + alpha.getHead().toString();
                    return false;
                } else {
                    currentProve.addAll(existenceDeduction(expression, alpha));
                }
            }
            return true;
        }

        return false;
    }

    String universalRule(Expression expression) {
        if (expression.getHead().getClass() == Implication.class) {
            Node fi = expression.getHead().getLeft();
            Node psi = expression.getHead().getRight();
            if (psi.getClass() == UniversalQuantifier.class) {
                if (new Expression(fi).getFree().contains(psi.getLeft().asString())) {
                    msg = ": переменная " + psi.getLeft().asString() + " входит свободно в формулу " + fi.toString();
                } else {
                    Expression pattern = new Expression(new Implication(fi, psi.getRight()));
                    for (Expression expr : proofs) {
                        if (expr.equals(pattern)) {
                            return psi.getLeft().asString();
                        }
                    }
                }
            }
        }
        return null;
    }

    List<Expression> universalDeduction(Expression expression, Expression A) {
        Node fi = expression.getHead().getLeft();
        Node x = expression.getHead().getRight();
        Node psi = x.getRight();
        x = x.getLeft();
        List<Expression> oldProve = new ArrayList<>();
        List<Expression> assumption = new ArrayList<>();
        List<Expression> answer = new ArrayList<>();

        assumption.add(new Expression(new Implication(A.getHead(), new Implication(fi, psi))));
        assumption.add(new Expression(new Conjunction(A.getHead(), fi)));

        oldProve.addAll(assumption);
        oldProve.add(new Expression(new Implication(new Conjunction(A.getHead(), fi), A.getHead())));
        oldProve.add(new Expression(new Implication(new Conjunction(A.getHead(), fi), fi)));
        oldProve.add(A);
        oldProve.add(new Expression(fi));
        //oldProve.add(new Expression(new Implication(A.getHead(), new Implication(fi, psi))));
        oldProve.add(new Expression(new Implication(fi, psi)));
        oldProve.add(new Expression(psi));

        answer.addAll(fullDeduction(assumption, oldProve));
        answer.add(new Expression(new Implication(new Conjunction(A.getHead(), fi), psi)));
        psi = new UniversalQuantifier(x, psi);
        answer.add(new Expression(new Implication(new Conjunction(A.getHead(), fi), psi)));

        oldProve.clear();
        assumption.clear();

        assumption.add(new Expression(new Implication(new Conjunction(A.getHead(), fi), psi)));
        assumption.add(A);
        assumption.add(new Expression(fi));

        oldProve.addAll(assumption);
        oldProve.add(new Expression(new Implication(A.getHead(), new Implication(fi, new Conjunction(A.getHead(), fi)))));
        oldProve.add(new Expression(new Implication(fi, new Conjunction(A.getHead(), fi))));
        oldProve.add(new Expression(new Conjunction(A.getHead(), fi)));
        //oldProve.add(new Expression(new Implication(new Conjunction(A.getHead(), fi), psi)));
        oldProve.add(new Expression(psi));

        answer.addAll(fullDeduction(assumption, oldProve));
        //answer.add(new Expression(new Implication(A.getHead(), new Implication(fi, psi))));

        return answer;
    }

    List<Expression> fullDeduction(List<Expression> assumptions, List<Expression> oldProve) {
        while (!assumptions.isEmpty()) {
            oldProve = deduction(assumptions, oldProve);
            assumptions.remove(assumptions.size() - 1);
        }
        return oldProve;
    }

    String existenceRule(Expression expression) {
        if (expression.getHead().getClass() == Implication.class) {
            Node fi = expression.getHead().getLeft();
            Node psi = expression.getHead().getRight();
            if (fi.getClass() == ExistentialQuantifier.class) {
                if (new Expression(psi).getFree().contains(fi.getLeft().asString())) {
                    msg = ": переменная " + fi.getLeft().asString() + " входит свободно в формулу " + psi.toString();
                } else {
                    Expression pattern = new Expression(new Implication(fi.getRight(), psi));
                    for (Expression expr : proofs) {
                        if (expr.equals(pattern)) {
                            return fi.getLeft().asString();
                        }
                    }
                }
            }
        }
        return null;
    }

    List<Expression> existenceDeduction(Expression expression, Expression A) {
        Node fi = expression.getHead().getRight(), x = expression.getHead().getLeft();
        Node psi = x.getRight();
        x = x.getLeft();

        List<Expression> assumption = new ArrayList<>();
        List<Expression> oldProve = new ArrayList<>();

        Expression tmp = new Expression(new Implication(A.getHead(), new Implication(psi, fi)));
        assumption.add(tmp);
        assumption.add(new Expression(psi));
        assumption.add(A);

        oldProve.add(tmp);
        oldProve.add(A);
        oldProve.add(new Expression(psi));
        oldProve.add(new Expression(new Implication(psi, fi)));
        oldProve.add(new Expression(fi));

        List<Expression> answer;

        answer = fullDeduction(assumption, oldProve);
        answer.add(new Expression(new Implication(psi, new Implication(A.getHead(), fi))));
        psi = new ExistentialQuantifier(x, psi);
        answer.add(new Expression(new Implication(psi, new Implication(A.getHead(), fi))));

        oldProve.clear();
        assumption.clear();

        tmp = new Expression(new Implication(psi, new Implication(A.getHead(), fi)));
        assumption.add(tmp);
        assumption.add(A);
        assumption.add(new Expression(psi));

        oldProve.add(tmp);
        oldProve.add(A);
        oldProve.add(new Expression(psi));
        oldProve.add(new Expression(new Implication(A.getHead(), fi)));
        oldProve.add(new Expression(fi));

        answer.addAll(fullDeduction(assumption, oldProve));
        //answer.add(new Expression(new Implication(A.getHead(), new Implication(psi, fi))));

        return answer;
    }

    int mp(Expression expression) {
        for (int i = 0; i < proofs.size(); ++i) {
            Node tmp = proofs.get(i).getHead();
            if (tmp.getClass() == Implication.class && expression.equals(expression.getHead(), tmp.getRight())) {
                for (int j = 0; j < proofs.size(); ++j) {
                    Expression expr = proofs.get(j);
                    if (expr.equals(expr.getHead(), tmp.getLeft())) {
                        return j;
                    }
                }
            }
        }
        return -1;
    }


    boolean checkAxioms(Expression expression, boolean df) {
        boolean flag = false;
        for (Expression ax : axioms) {
            if (ax.equalsAxiom(expression)) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            for (Expression ax : schemes) {
                if (ax.equalsScheme(expression)) {
                    flag = true;
                    break;
                }
            }
        }

        if (!flag) {
            flag = schemeOne(expression);
        }
        if (!flag) {
            flag = schemeTwo(expression);
        }
        if (!flag) {
            flag = schemeThree(expression);
        }
        if (flag && df) {
            currentProve.add(expression);
            currentProve.add(new Expression(new Implication(expression.getHead(), new Implication(alpha.getHead(), expression.getHead()))));
        }

        return flag;
    }

    boolean schemeOne(Expression expression) {
        if (expression.getHead().getClass() == Implication.class) {
            if (expression.getHead().getLeft().getClass() == UniversalQuantifier.class) {
                String varName = expression.getHead().getLeft().getLeft().asString();
                Node left = expression.getHead().getLeft().getRight(), right = expression.getHead().getRight();
                Expression tmp = new Expression(left);
                Node node;
                if ((node = tmp.availableToPut(new Expression(right), varName)) == null) {
                    return true;
                }
                if (tmp.errorFlag) {
                    msg = ": терм " + node.toString() + " не свободен для подстановки в формулу " + tmp.getHead().toString() + " вместо переменной " + varName;
                }
            }
        }

        return false;
    }

    boolean schemeTwo(Expression expression) {
        if (expression.getHead().getClass() == Implication.class) {
            if (expression.getHead().getRight().getClass() == ExistentialQuantifier.class) {
                String varName = expression.getHead().getRight().getLeft().asString();
                Node left = expression.getHead().getLeft(), right = expression.getHead().getRight().getRight();
                Expression tmp = new Expression(right);
                Node node;
                if ((node = tmp.availableToPut(new Expression(left), varName)) == null) {
                    return true;
                }
                if (tmp.errorFlag) {
                    msg = ": терм " + node.toString() + " не свободен для подстановки в формулу " + tmp.getHead().toString() + " вместо переменной " + varName;
                }
            }
        }

        return false;
    }

    boolean schemeThree(Expression expression) {
        Node node = expression.getHead();
        if (node.getClass() == Implication.class) {
            Expression fi = new Expression(node.getRight());
            Node left = node.getLeft();
            if (left.getClass() == Conjunction.class && left.getRight().getClass() == UniversalQuantifier.class) {
                Node x = left.getRight().getLeft();
                Node pattern = new Implication(
                        new Conjunction(fi.spell(x, new FormalVariable("0")).getHead(),
                                new UniversalQuantifier(x, new Implication(fi.getHead(), fi.spell(x, new Prime(x)).getHead()))),
                        fi.getHead());
                if (new Expression(pattern).equals(expression)) {
                    return true;
                }
            }
        }
        return false;
    }

    List<Expression> deduction(List<Expression> assumption, List<Expression> oldProve) {
        Node A = assumption.get(assumption.size() - 1).getHead();
        assumption = assumption.subList(0, assumption.size() - 1);
        List<Expression> currentProve = new ArrayList<>();
        for (Expression expr : oldProve) {
            if (checkAxioms(expr, false) | assumption.contains(expr)) {

                currentProve.add(expr);
                currentProve.add(new Expression(new Implication(expr.getHead(), new Implication(A, expr.getHead()))));


            } else {

                if (expr.equals(new Expression(A))) {
                    currentProve.addAll(aToA(expr));

                } else {
                    List<Expression> buf = proofs;
                    proofs = oldProve;
                    int num = mp(expr);
                    proofs = buf;
                    if (num != -1) {
                        Node one = expr.getHead(), two = oldProve.get(num).getHead();
                        currentProve.addAll(modusDeduction(one, two, A));

                    } else {
                        System.err.println("Something wrong with expression: " + expr.getHead().toString());
                    }
                }
            }
            currentProve.add(new Expression(new Implication(A, expr.getHead())));
        }
        return currentProve;


    }

    List<Expression> aToA(Expression A) {
        String s = "(" + A.getHead().toString() + ")";
        List<String> ans = new ArrayList<>();
        FormalParser parser = new FormalParser();
        ans.add(s + "->" + s + "->" + s);
        ans.add(String.format("(%1$s->(%1$s->%1$s))->(%1$s->(%1$s->%1$s)->%1$s)->(%1$s->%1$s)", s));
        ans.add(String.format("(%1$s->(%1$s->%1$s)->%1$s)->(%1$s->%1$s)", s));
        ans.add(String.format("%1$s->(%1$s->%1$s)->%1$s", s));
        return ans.stream().map(str -> new Expression(parser.parse(str))).collect(Collectors.toList());
    }

    List<Expression> modusDeduction(Node one, Node two, Node A) {
        List<Expression> currentProve = new ArrayList<>();
        currentProve.add(new Expression(new Implication(new Implication(A, two),
                new Implication(new Implication(A, new Implication(two, one)),
                        new Implication(A, one)))));
        currentProve.add(new Expression(
                new Implication(new Implication(A, new Implication(two, one)),
                        new Implication(A, one))));
        return currentProve;
    }
}
