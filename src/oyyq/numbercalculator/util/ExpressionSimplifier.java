package oyyq.numbercalculator.util;

import java.util.ArrayList;

import oyyq.numbercalculator.datastructure.Expression;
import oyyq.numbercalculator.datastructure.Operator;

public class ExpressionSimplifier {

    public static Expression simplify(Expression result) {
        if (result.isSimple()) {
            return result;
        }
        boolean hasSimplified = false;
        do {
            hasSimplified = false;
            Expression simplified = reorderConsecutivePlusOrMultiply(result);
            if (!simplified.toString(true).equals(result.toString(true))) {
                hasSimplified = true;
                result = simplified;
            }

            simplified = reorderConsecutiveMinusOrDivide(result);
            if (!simplified.toString(true).equals(result.toString(true))) {
                hasSimplified = true;
                result = simplified;
            }

            simplified = simplifyDivideAbsoluteOne(result);
            if (!simplified.toString(true).equals(result.toString(true))) {
                hasSimplified = true;
                result = simplified;
            }

            simplified = simplifyMultiplyOne(result);
            if (!simplified.toString(true).equals(result.toString(true))) {
                hasSimplified = true;
                result = simplified;
            }

            simplified = simplifyMinusOrDivide(result);
            if (!simplified.toString(true).equals(result.toString(true))) {
                hasSimplified = true;
                result = simplified;
            }

            simplified = putMinusAndDivideToRightmost(result);
            if (!simplified.toString(true).equals(result.toString(true))) {
                hasSimplified = true;
                result = simplified;
            }

            simplified = simplifyMinusNegative(result);
            if (!simplified.toString(true).equals(result.toString(true))) {
                hasSimplified = true;
                result = simplified;
            }

            simplified = simplifyNegativeMultiplyOrDivideNegative(result);
            if (!simplified.toString(true).equals(result.toString(true))) {
                hasSimplified = true;
                result = simplified;
            }

        } while (hasSimplified);
        return result;
    }

    /**
     * add an expression to the ordered list
     * 
     * @param result
     *            The expression to add
     * @param expressions
     *            The list
     */
    private static void addToOrderedExpressionList(Expression result,
            ArrayList<Expression> expressions) {
        int length = expressions.size();
        for (int i = 0; i < length; i++) {
            Expression expi = expressions.get(i);
            if (aShouldBeforeB(result, expi)) {
                expressions.add(i, result);
                return;
            }
        }
        expressions.add(result);
    }

    /**
     * <p>
     * judges if <i>a</i> should before <i>b</i> in the final expression.
     * </p>
     * <b>rules:</b>
     * <ol>
     * <li>The one which has <b>more operands</b> in total should on the left, when equals,</li>
     * <li>The one which has a <b>larger value</b> should on the left, when equals again,</li>
     * <li>Compare the string form of the two expressions, the 'larger' one on the left.
     * </ol>
     * 
     * @param a
     *            one expression
     * @param b
     *            the other
     * @return true if a should before b, false otherwise.
     */
    private static boolean aShouldBeforeB(Expression a, Expression b) {
        int compare = a.getTotalOperandsCount() - b.getTotalOperandsCount();
        if (compare > 0) {
            return true;
        } else if (compare == 0) {
            compare = a.getValue().compareTo(b.getValue());
            if (compare > 0) {
                return true;
            } else if (compare == 0) {
                compare = a.toString().compareTo(b.toString());
                if (compare >= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private static Expression putMinusAndDivideToRightmost(Expression oldExpression) {
        Expression newExpression = oldExpression;
        if (oldExpression.isSimple()) {
            return oldExpression;
        }
        Operator oper = oldExpression.getOperator();
        Expression operand1 = oldExpression.getOperand1();
        Expression operand2 = oldExpression.getOperand2();
        // (a - b) + c => a + c - b
        // (a / b) * c => a * c / b
        if ((oper == Operator.PLUS && operand1.getOperator() == Operator.MINUS)
                || (oper == Operator.MULTIPLY && operand1.getOperator() == Operator.DIVIDE)) {
            Expression tmp = new Expression(operand1.getOperand1(), oper, operand2);
            newExpression = new Expression(tmp, operand1.getOperator(), operand1.getOperand2());
        }
        operand1 = newExpression.getOperand1();
        operand2 = newExpression.getOperand2();
        oper = newExpression.getOperator();

        // a + (b - c) => a + b - c
        // a * (b / c) => a * b / c
        if ((oper == Operator.PLUS && operand2.getOperator() == Operator.MINUS)
                || (oper == Operator.MULTIPLY && operand2.getOperator() == Operator.DIVIDE)) {
            Expression tmp = new Expression(operand1, oper, operand2.getOperand1());
            newExpression = new Expression(tmp, operand2.getOperator(), operand2.getOperand2());
        }
        operand1 = newExpression.getOperand1();
        operand2 = newExpression.getOperand2();
        oper = newExpression.getOperator();
        if (!operand1.isSimple() || !operand2.isSimple()) {
            operand1 = putMinusAndDivideToRightmost(operand1);
            operand2 = putMinusAndDivideToRightmost(operand2);
            newExpression = new Expression(operand1, oper, operand2);
        }
        return newExpression;
    }

    private static Expression reorderConsecutiveMinusOrDivide(Expression oldExpression) {
        Expression newExpression = oldExpression;
        if (oldExpression.isSimple()) {
            return oldExpression;
        }
        Operator oper = oldExpression.getOperator();
        Expression operand1 = oldExpression.getOperand1();
        Expression operand2 = oldExpression.getOperand2();
        if (!oper.isExchangable()) {
            ArrayList<Expression> operand2s = new ArrayList<>();
            addToOrderedExpressionList(operand2, operand2s);
            while (operand1.getOperator() == oper) {
                addToOrderedExpressionList(operand1.getOperand2(), operand2s);
                operand1 = operand1.getOperand1();
            }
            while (operand2s.size() > 1) {
                operand1 = new Expression(operand1, oper, operand2s.get(0));
                operand2s.remove(0);
            }
            operand2 = operand2s.get(0);
            newExpression = new Expression(operand1, oper, operand2);
        }

        // do the recursion for each operand
        operand1 = newExpression.getOperand1();
        operand2 = newExpression.getOperand2();
        oper = newExpression.getOperator();
        if (!operand1.isSimple() || !operand2.isSimple()) {
            operand1 = reorderConsecutiveMinusOrDivide(operand1);
            operand2 = reorderConsecutiveMinusOrDivide(operand2);
            newExpression = new Expression(operand1, oper, operand2);
        }
        return newExpression;
    }

    private static Expression reorderConsecutivePlusOrMultiply(Expression oldExpression) {
        Expression newExpression = oldExpression;
        if (oldExpression.isSimple()) {
            return oldExpression;
        }
        Operator oper = oldExpression.getOperator();
        Expression operand1 = oldExpression.getOperand1();
        Expression operand2 = oldExpression.getOperand2();
        if (oper.isExchangable()) {
            ArrayList<Expression> operands = new ArrayList<>();
            addToOrderedExpressionList(operand1, operands);
            addToOrderedExpressionList(operand2, operands);
            int size = Integer.MAX_VALUE;
            do {
                size = operands.size();
                for (int i = 0; i < size; i++) {
                    Expression operandi = operands.get(i);

                    // should expand
                    if (operandi.getOperator() == oper) {
                        Expression operandi1 = operandi.getOperand1();
                        Expression operandi2 = operandi.getOperand2();
                        operands.remove(operandi);
                        addToOrderedExpressionList(operandi1, operands);
                        addToOrderedExpressionList(operandi2, operands);
                        break;
                    }
                }
            } while (size < operands.size());

            while (operands.size() > 1) {
                operand1 = operands.get(0);
                operand2 = operands.get(1);
                operands.remove(operand1);
                operands.remove(operand2);
                Expression result = new Expression(operand1, oper, operand2);
                addToOrderedExpressionList(result, operands);
            }
            newExpression = operands.get(0);
        }

        // do the recursion for each operand
        operand1 = newExpression.getOperand1();
        operand2 = newExpression.getOperand2();
        oper = newExpression.getOperator();
        if (!operand1.isSimple() || !operand2.isSimple()) {
            operand1 = reorderConsecutivePlusOrMultiply(operand1);
            operand2 = reorderConsecutivePlusOrMultiply(operand2);
            newExpression = new Expression(operand1, oper, operand2);
        }

        return newExpression;
    }

    private static Expression simplifyDivideAbsoluteOne(Expression oldExpression) {
        Expression newExpression = oldExpression;
        if (oldExpression.isSimple()) {
            return oldExpression;
        }
        if (oldExpression.getOperator() == Operator.DIVIDE
                && oldExpression.getOperand2().getValue().abs().equalsNumber(1)) {
            newExpression = new Expression(oldExpression.getOperand1(), Operator.MULTIPLY,
                    oldExpression.getOperand2());
        }
        Expression operand1 = newExpression.getOperand1();
        Expression operand2 = newExpression.getOperand2();
        Operator operator = newExpression.getOperator();
        if (!operand1.isSimple() || !operand2.isSimple()) {
            operand1 = simplifyDivideAbsoluteOne(operand1);
            operand2 = simplifyDivideAbsoluteOne(operand2);
            newExpression = new Expression(operand1, operator, operand2);
        }
        return newExpression;
    }

    private static Expression simplifyMinusNegative(Expression oldExpression) {
        Expression newExpression = oldExpression;
        if (oldExpression.isSimple()) {
            return oldExpression;
        }
        Expression operand1 = oldExpression.getOperand1();
        Expression operand2 = oldExpression.getOperand2();
        Operator oper = oldExpression.getOperator();

        // a - (b - c) */ d => a + (c - b) */ d while c > b
        // a - b */ (c - d) => a + b */ (d - c) while d > c
        if (oper == Operator.MINUS && operand2.getValue().compareTo(0) < 0) {
            Expression newOperand2 = tryToNegateExpression(operand2);
            if (!newOperand2.equals(operand2)) {
                newExpression = new Expression(operand1, Operator.PLUS, newOperand2);
            }
        }
        
        // a */ (b - c) + d => d - a */ (c - b) while c > b
        // (a - b) */ c + d => d - (b - a) */ c while b > a
        if (oper == Operator.PLUS && operand1.getValue().compareTo(0) < 0) {
            Expression newOperand1 = tryToNegateExpression(operand1);
            if (!newOperand1.equals(operand1)) {
                newExpression = new Expression(operand2, Operator.MINUS, newOperand1);
            }
        }
        operand1 = newExpression.getOperand1();
        operand2 = newExpression.getOperand2();
        oper = newExpression.getOperator();
        if (!operand1.isSimple() || !operand2.isSimple()) {
            operand1 = simplifyMinusNegative(operand1);
            operand2 = simplifyMinusNegative(operand2);
            newExpression = new Expression(operand1, oper, operand2);
        }
        return newExpression;
    }

    private static Expression simplifyMinusOrDivide(Expression oldExpression) {
        Expression newExpression = oldExpression;
        if (oldExpression.isSimple()) {
            return oldExpression;
        }
        Expression operand1 = oldExpression.getOperand1();
        Expression operand2 = oldExpression.getOperand2();
        Operator oper = oldExpression.getOperator();

        // a - (b +- c) => a - b -+ c
        if (oper == Operator.MINUS) {
            Operator oper2 = operand2.getOperator();
            if (oper2 == Operator.PLUS) {
                Expression tmp = new Expression(operand1, oper, operand2.getOperand1());
                newExpression = new Expression(tmp, Operator.MINUS, operand2.getOperand2());
            } else if (oper2 == Operator.MINUS) {
                Expression tmp = new Expression(operand1, oper, operand2.getOperand1());
                newExpression = new Expression(tmp, Operator.PLUS, operand2.getOperand2());
            }
        }
        operand1 = newExpression.getOperand1();
        operand2 = newExpression.getOperand2();
        oper = newExpression.getOperator();

        // a / (b */ c) => a / b /* c
        if (oper == Operator.DIVIDE) {
            Operator oper2 = operand2.getOperator();
            if (oper2 == Operator.MULTIPLY) {
                Expression tmp = new Expression(operand1, oper, operand2.getOperand1());
                newExpression = new Expression(tmp, Operator.DIVIDE, operand2.getOperand2());
            } else if (oper2 == Operator.DIVIDE) {
                Expression tmp = new Expression(operand1, oper, operand2.getOperand1());
                newExpression = new Expression(tmp, Operator.MULTIPLY, operand2.getOperand2());
            }
        }
        operand1 = newExpression.getOperand1();
        operand2 = newExpression.getOperand2();
        oper = newExpression.getOperator();
        if (!operand1.isSimple() || !operand2.isSimple()) {
            operand1 = simplifyMinusOrDivide(operand1);
            operand2 = simplifyMinusOrDivide(operand2);
            newExpression = new Expression(operand1, oper, operand2);
        }
        return newExpression;
    }

    private static Expression simplifyMultiplyOne(Expression oldExpression) {
        Expression newExpression = oldExpression;
        if (oldExpression.isSimple()) {
            return oldExpression;
        }
        Expression operand1 = oldExpression.getOperand1();
        Expression operand2 = oldExpression.getOperand2();
        Operator oper = oldExpression.getOperator();

        if (oper.getPriority() == Operator.PRIORITY_PLUS_MINUS
                && operand1.getOperator() == Operator.MULTIPLY) {
            if (operand1.getOperand1().getValue().equalsNumber(1)) {
                Expression tmp = new Expression(operand1.getOperand2(), oper, operand2);
                newExpression = new Expression(tmp, Operator.MULTIPLY, operand1.getOperand1());
            } else if (operand1.getOperand2().getValue().equalsNumber(1)) {
                Expression tmp = new Expression(operand1.getOperand1(), oper, operand2);
                newExpression = new Expression(tmp, Operator.MULTIPLY, operand1.getOperand2());
            }
        }
        operand1 = newExpression.getOperand1();
        operand2 = newExpression.getOperand2();
        oper = newExpression.getOperator();

        if (oper.getPriority() == Operator.PRIORITY_PLUS_MINUS
                && operand2.getOperator() == Operator.MULTIPLY) {
            if (operand2.getOperand1().getValue().equalsNumber(1)) {
                Expression tmp = new Expression(operand1, oper, operand2.getOperand2());
                newExpression = new Expression(tmp, Operator.MULTIPLY, operand2.getOperand1());
            } else if (operand2.getOperand2().getValue().equalsNumber(1)) {
                Expression tmp = new Expression(operand1, oper, operand2.getOperand1());
                newExpression = new Expression(tmp, Operator.MULTIPLY, operand2.getOperand2());
            }
        }
        operand1 = newExpression.getOperand1();
        operand2 = newExpression.getOperand2();
        oper = newExpression.getOperator();
        if (!operand1.isSimple() || !operand2.isSimple()) {
            operand1 = simplifyMultiplyOne(operand1);
            operand2 = simplifyMultiplyOne(operand2);
            newExpression = new Expression(operand1, oper, operand2);
        }

        return newExpression;
    }

    private static Expression simplifyNegativeMultiplyOrDivideNegative(Expression oldExpression) {
        Expression newExpression = oldExpression;
        if (oldExpression.isSimple()) {
            return oldExpression;
        }
        Expression operand1 = oldExpression.getOperand1();
        Expression operand2 = oldExpression.getOperand2();
        Operator oper = oldExpression.getOperator();

        // (a - b) */ (c - d) => (b - a) */ (d - c) while b > a and d > c
        if ((oper == Operator.MULTIPLY || oper == Operator.DIVIDE)
                && operand1.getValue().compareTo(0) < 0 && operand2.getValue().compareTo(0) < 0) {
            Expression newOperand1 = tryToNegateExpression(operand1);
            if (newOperand1 != operand1) {
                Expression newOperand2 = tryToNegateExpression(operand2);
                if (newOperand2 != operand2) {
                    newExpression = new Expression(newOperand1, oper, newOperand2);
                }
            }
        }
        operand1 = newExpression.getOperand1();
        operand2 = newExpression.getOperand2();
        oper = newExpression.getOperator();
        if (!operand1.isSimple() || !operand2.isSimple()) {
            operand1 = simplifyNegativeMultiplyOrDivideNegative(operand1);
            operand2 = simplifyNegativeMultiplyOrDivideNegative(operand2);
            newExpression = new Expression(operand1, oper, operand2);
        }
        return newExpression;
    }

    private static Expression tryToNegateExpression(Expression oldExpression) {
        Expression newExpression = oldExpression;
        if (oldExpression == null || oldExpression.isSimple()) {
            return oldExpression;
        }
        Expression operand1 = oldExpression.getOperand1();
        Expression operand2 = oldExpression.getOperand2();
        Operator oper = oldExpression.getOperator();
        switch (oper) {
            case MINUS:
                newExpression = new Expression(operand2, oper, operand1);
                break;
            case MULTIPLY:
            case DIVIDE:
                Expression newOperand1 = tryToNegateExpression(operand1);
                if (newOperand1 != operand1) {
                    newExpression = new Expression(newOperand1, oper, operand2);
                    break;
                }
                Expression newOperand2 = tryToNegateExpression(operand2);
                if (newOperand2 != operand2) {
                    newExpression = new Expression(operand1, oper, newOperand2);
                    break;
                }
                break;
            case PLUS:
            default:
                break;
        }
        return newExpression;
    }
}
