package oyyq.numbercalculator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import oyyq.numbercalculator.datastructure.Expression;
import oyyq.numbercalculator.datastructure.Operator;
import oyyq.numbercalculator.util.ExpressionSimplifier;

public class NumberCalculator {

    private HashMap<String, Boolean> failedNumbers;
    private Number[]                 numbers;
    private LinkedList<Expression>    results;
    private Number                   target;

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
        this.results = new LinkedList<Expression>();
    }

    public boolean calculate() {
        return calculate(false);
    }

    public boolean calculate(boolean findAll) {
        failedNumbers = new HashMap<>();
        // the list is sorted
        List<Expression> expressions = getExpressions();
        boolean result = search(expressions, findAll);
        return result;
    }

    public Number[] getNumbers() {
        return numbers.clone();
    }

    public int getNumbersCount() {
        return numbers.length;
    }

    @SuppressWarnings("unchecked")
    public List<Expression> getResults() {
        return (List<Expression>) results.clone();
    }

    public Number getTarget() {
        return target;
    }

    // add result to the expressions list, ensuring the order
    private void addToOrderedExpressionList(Expression result, List<Expression> expressions) {
        ListIterator<Expression> iterator = expressions.listIterator();
        while (iterator.hasNext()) {
            // find the one before which to insert
            if (result.getValue().compareTo(iterator.next().getValue()) <= 0) {
                iterator.previous();
                iterator.add(result);
                return;
            }
        }
        // not inserted yet, should be last
        expressions.add(result);
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

    private List<Expression> getExpressions() {
        Number[] numbers = this.numbers.clone();
        Arrays.sort(numbers);
        LinkedList<Expression> expressions = new LinkedList<Expression>();
        for (Number number : numbers) {
            expressions.add(new Expression(number));
        }
        return expressions;
    }

    private String getHashKey(List<Expression> expressions) {
        if (expressions != null) {
            StringBuilder sb = new StringBuilder();
            for (Expression expression : expressions) {
                sb.append(expression.getValue()).append(",");
            }
            return sb.toString();
        }
        return null;
    }

    private boolean search(List<Expression> expressions, boolean findAll) {
        // if the combination of the numbers has been proved to fail, don't try it again!
        if (expressions == null || failedNumbers.get(getHashKey(expressions)) != null) {
            return false;
        }
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
            // no need to try another same expression
            if (i > 0 && expressions.get(i).isSameAs(expressions.get(i - 1))) {
                continue;
            }
            for (int j = i + 1; j < size; j++) {
                // no need to try another same expression
                if (j > i + 1 && expressions.get(j).isSameAs(expressions.get(j - 1))) {
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
                            // plus and multiply
                            result = new Expression(operand2, Operator.PLUS, operand1);
                            addToOrderedExpressionList(result, expressions);
                            if (search(expressions, findAll)) {
                                found = true;
                                if (!findAll) {
                                    return found;
                                }
                            } else {
                                failedNumbers.put(getHashKey(expressions), true);
                            }
                            expressions.remove(result);
                            break;
                        case MINUS:
                            // try both cases for minus
                            result = new Expression(operand2, Operator.MINUS, operand1);
                            addToOrderedExpressionList(result, expressions);
                            if (search(expressions, findAll)) {
                                found = true;
                                if (!findAll) {
                                    return found;
                                }
                            } else {
                                failedNumbers.put(getHashKey(expressions), true);
                            }
                            expressions.remove(result);

                            result = new Expression(operand1, Operator.MINUS, operand2);
                            addToOrderedExpressionList(result, expressions);
                            if (search(expressions, findAll)) {
                                found = true;
                                if (!findAll) {
                                    return found;
                                }
                            } else {
                                failedNumbers.put(getHashKey(expressions), true);
                            }
                            expressions.remove(result);
                            break;
                        case MULTIPLY:
                            // we always put the larger number on the left for
                            // plus and multiply
                            result = new Expression(operand2, Operator.MULTIPLY, operand1);
                            addToOrderedExpressionList(result, expressions);
                            if (search(expressions, findAll)) {
                                found = true;
                                if (!findAll) {
                                    return found;
                                }
                            } else {
                                failedNumbers.put(getHashKey(expressions), true);
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
                                } else {
                                    failedNumbers.put(getHashKey(expressions), true);
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
                                } else {
                                    failedNumbers.put(getHashKey(expressions), true);
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
