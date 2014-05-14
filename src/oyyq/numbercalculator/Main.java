package oyyq.numbercalculator;

import java.util.ArrayList;
import java.util.Arrays;

import oyyq.numbercalculator.datastructure.Expression;

public class Main {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        // System.err.close();
        // int odd;
        // int even;
        // int max = -1;
        // for (int number = 1; number <= 100; number++) {
        // odd = -1;
        // even = -1;
        // System.out.println(number);
        // for (int count = 1;; count++) {
        // Number[] numbers = new Number[count];
        // Arrays.fill(numbers, number);
        // NumberCalculator calc = new NumberCalculator(numbers);
        // if (calc.calculate(false)) {
        // if ((count & 1) == 0) {
        // even = count;
        // } else {
        // odd = count;
        // }
        // }
        // if (even > 0 && odd > 0) {
        // if (Math.abs(even - odd) != 1) {
        // System.err.println("Failed at:" + number + ": " + even + " " + odd);
        // System.exit(1);
        // }
        // if (Math.min(even, odd) > max) {
        // max = Math.min(even, odd);
        // }
        // break;
        // }
        // }
        // }
        // System.out.println(max);
        // System.exit(0);
        NumberCalculator calculator = new NumberCalculator(
                new Number[] {5, 7, 7, 13, 13}, 24);
        Number[] numbers = calculator.getNumbers();
        for (Number number : numbers) {
            System.out.print(number + " ");
        }
        System.out.println();
        long start = System.currentTimeMillis();
        System.out.println(calculator.calculate(true));
        System.err.println(System.currentTimeMillis() - start);
        ArrayList<Expression> results;
        results = calculator.getResults();
        System.out.println(results.size());
        for (Expression result : results) {
            System.out.println(result.toString());
        }
        System.exit(0);
        // int solveCount = 0;
        // int solvableCount = 0;
        // int maxSolves = 0;
        // for (int i = 1; i <= 13; i++) {
        // for (int j = i; j <= 13; j++) {
        // for (int k = j; k <= 13; k++) {
        // for (int l = k; l <= 13; l++) {
        // NumberCalculator calc = new NumberCalculator(new Number[] {i, j, k, l});
        // System.out.println(i + " " + j + " " + k + " " + l);
        // System.out.println(calc.calculate(true));
        // results = calc.getResults();
        // System.out.println(results.size());
        // solveCount += results.size();
        // if (results.size() > 0) {
        // solvableCount++;
        // }
        // if (results.size() > maxSolves) {
        // maxSolves = results.size();
        // }
        // for (Expression result : results) {
        // System.out.println(result);
        // }
        // }
        // }
        // }
        // }
        // System.out.println(solvableCount + " " + solveCount + " " + maxSolves);
        // System.err.println(System.currentTimeMillis() - start);
    }

}
