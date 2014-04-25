package oyyq.numbercalculator;

import java.util.ArrayList;
import java.util.Arrays;

import oyyq.numbercalculator.datastructure.Expression;
import oyyq.numbercalculator.datastructure.Operator;
import oyyq.numbercalculator.util.ExpressionSimplifier;

public class NumberCalculator {

    private Number[]              numbers;
    private ArrayList<Expression> results;
    private Number                target;

    public NumberCalculator(Number[] numbers) {
        this(numbers, 24);
    }

    public NumberCalculator(Number[] numbers, Number target) {
        if (numbers == null || target == null) {
            throw new IllegalArgumentException("Null parameter");
        }
        if (numbers.length == 0) {
            throw new IllegalArgumentException("Empty numbers");
        }
        for (Number number : numbers) {
            if (number == null) {
                throw new IllegalArgumentException("Null number");
            }
        }
        this.target = target;
        this.numbers = numbers;
        this.results = new ArrayList<>();
    }

    public boolean calculate() {
        return calculate(false);
    }

    public boolean calculate(boolean findAll) {
        // the list is sorted
        ArrayList<Expression> expressions = getExpressions();
        return search(expressions, findAll);
    }

    public Number[] getNumbers() {
        return numbers.clone();
    }

    public int getNumbersCount() {
        return numbers.length;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Expression> getResults() {
        return (ArrayList<Expression>) results.clone();
    }

    public Number getTarget() {
        return target;
    }

    // add result to the expressions list, ensuring the order
    private void addToOrderedExpressionList(Expression result, ArrayList<Expression> expressions) {
        int length = expressions.size();
        for (int i = 0; i < length; i++) {
            // find the one before which to insert
            if (result.getValue().compareTo(expressions.get(i).getValue()) <= 0) {
                expressions.add(i, result);
                break;
            }
        }
        if (expressions.size() == length) {
            expressions.add(result);
        }
    }

    private void addToResults(Expression result) {
        result = ExpressionSimplifier.simplify(result);
        for (Expression ori : results) {
            if (ori.toString().equals(result.toString())) {
                return;
            }
        }
        results.add(result);
    }

    private ArrayList<Expression> getExpressions() {
        Number[] numbers = this.numbers.clone();
        Arrays.sort(numbers);
        ArrayList<Expression> expressions = new ArrayList<>();
        for (Number number : numbers) {
            expressions.add(new Expression(number));
        }
        return expressions;
    }

    private boolean search(ArrayList<Expression> expressions, boolean findAll) {
        boolean found = false;
        int size = expressions.size();
        if (size == 1) {
            Expression exp0 = expressions.get(0);
            if (exp0.getValue().equalsNumber(target)) {
                addToResults(exp0);
                return true;
            }
            return false;
        }
        for (int i = 0; i < size; i++) {
            // no need to try another same number
            if (i > 0 && expressions.get(i).getValue().equals(expressions.get(i - 1).getValue())) {
                continue;
            }
            for (int j = i + 1; j < size; j++) {
                // no need to try another same number
                if (j > i + 1
                        && expressions.get(j).getValue().equals(expressions.get(j - 1).getValue())) {
                    continue;
                }
                Expression operand1 = expressions.get(i);
                Expression operand2 = expressions.get(j);
                expressions.remove(operand1);
                expressions.remove(operand2);
                Expression result;
                for (Operator operator : Operator.values()) {
                    switch (operator) {
                        case PLUS:
                            // we always put the larger number on the left for
                            // plus, minus, and multiply
                            result = new Expression(operand2, Operator.PLUS, operand1);
                            addToOrderedExpressionList(result, expressions);
                            if (search(expressions, findAll)) {
                                found = true;
                                if (!findAll) {
                                    return found;
                                }
                            }
                            expressions.remove(result);
                            break;
                        case MINUS:
                            // we always put the larger number on the left for
                            // plus, minus, and multiply
                            result = new Expression(operand2, Operator.MINUS, operand1);
                            addToOrderedExpressionList(result, expressions);
                            if (search(expressions, findAll)) {
                                found = true;
                                if (!findAll) {
                                    return found;
                                }
                            }
                            expressions.remove(result);
                            break;
                        case MULTIPLY:
                            // we always put the larger number on the left for
                            // plus, minus, and multiply
                            result = new Expression(operand2, Operator.MULTIPLY, operand1);
                            addToOrderedExpressionList(result, expressions);
                            if (search(expressions, findAll)) {
                                found = true;
                                if (!findAll) {
                                    return found;
                                }
                            }
                            expressions.remove(result);
                            break;
                        case DIVIDE:
                            // try one case
                            if (!operand2.getValue().equalsNumber(0)) {
                                result = new Expression(operand1, Operator.DIVIDE, operand2);
                                addToOrderedExpressionList(result, expressions);
                                if (search(expressions, findAll)) {
                                    found = true;
                                    if (!findAll) {
                                        return found;
                                    }
                                }
                                expressions.remove(result);
                            }

                            // try the other case
                            if (!operand1.getValue().equalsNumber(0)) {
                                result = new Expression(operand2, Operator.DIVIDE, operand1);
                                addToOrderedExpressionList(result, expressions);
                                if (search(expressions, findAll)) {
                                    found = true;
                                    if (!findAll) {
                                        return found;
                                    }
                                }
                                expressions.remove(result);
                            }
                            break;
                    }
                }

                // put the original expressions back, ensure the order
                expressions.add(i, operand1);
                expressions.add(j, operand2);
            }
        }
        return found;
    }

}
