package oyyq.numbercalculator.datastructure;

import com.github.kiprobinson.util.BigFraction;

public class Expression {

    private Expression  operand1;
    private Expression  operand2;
    private Operator    operator;
    private boolean     simple;
    private int         totalOperatorCount;
    private BigFraction value;

    private static BigFraction calcValue(Expression op1, Operator operator, Expression op2) {
        BigFraction result = null;
        switch (operator) {
            case PLUS:
                result = op1.value.add(op2.value);
                break;
            case MINUS:
                result = op1.value.subtract(op2.value);
                break;
            case MULTIPLY:
                result = op1.value.multiply(op2.value);
                break;
            case DIVIDE:
                if (op2.value.equalsNumber(0)) {
                    throw new ArithmeticException("Divide by zero");
                }
                result = op1.value.divide(op2.value);
                break;
            default:
                throw new IllegalArgumentException("Illegal operator");
        }
        return result;
    }

    public Expression(Expression operand1, Operator operator, Expression operand2) {
        if (operand1 == null || operand2 == null || operator == null) {
            throw new IllegalArgumentException("Null parameter");
        }
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.operator = operator;
        value = calcValue(operand1, operator, operand2);
        simple = false;
        totalOperatorCount = operand1.totalOperatorCount + operand2.totalOperatorCount;
    }

    public Expression(Number value) {
        this.value = BigFraction.valueOf(value);
        operand1 = operand2 = null;
        operator = null;
        simple = true;
        totalOperatorCount = 1;
    }

    public Expression(Number operand1, Operator operator, Number operand2) {
        this(new Expression(operand1), operator, new Expression(operand2));
    }

    public Expression getOperand1() {
        return operand1;
    }

    public Expression getOperand2() {
        return operand2;
    }

    public Operator getOperator() {
        return operator;
    }

    public int getTotalOperatorCount() {
        return totalOperatorCount;
    }

    public BigFraction getValue() {
        return value;
    }

    public boolean isSimple() {
        return simple;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean fullParenthesis) {
        final String SPACE = " ";
        final String OPEN_PARENTHESIS = "(";
        final String CLOSE_PARENTHESIS = ")";

        StringBuilder sb = new StringBuilder();
        if (operator != null) {
            // we have to use parantheses to guarantee the lower operators
            // calculates first
            // (a + b) * c
            if (!operand1.simple
                    && (fullParenthesis || (operand1.operator.getPriority() < operator
                            .getPriority()))) {
                sb.append(OPEN_PARENTHESIS).append(operand1.toString(fullParenthesis))
                        .append(CLOSE_PARENTHESIS);
            } else {
                sb.append(operand1.toString(fullParenthesis));
            }
            sb.append(SPACE).append(operator).append(SPACE);

            // guarantee the lower operators calculates first too, plus another
            // case: a - (b + c) or a / (b / c) but not a - b * c
            if (!operand2.simple
                    && (fullParenthesis || ((operand2.operator.getPriority() < operator
                            .getPriority() || (!operator.isExchangable() && operator.getPriority() == operand2.operator
                            .getPriority()))))) {
                sb.append(OPEN_PARENTHESIS).append(operand2.toString(fullParenthesis))
                        .append(CLOSE_PARENTHESIS);
            } else {
                sb.append(operand2.toString(fullParenthesis));
            }
        } else {
            if (value.getDenominator().intValue() == 1) {
                sb.append(value.getNumerator());
            } else {
                sb.append(OPEN_PARENTHESIS).append(value.toString()).append(CLOSE_PARENTHESIS);
            }
        }
        return sb.toString();
    }
}
