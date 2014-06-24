package oyyq.numbercalculator;

import java.util.ArrayList;
import java.util.Arrays;

import oyyq.numbercalculator.datastructure.Expression;
import oyyq.numbercalculator.misc.CardsSolutionsMap;

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
        // NumberCalculator calculator = new NumberCalculator(
        // new Number[] {2, 7, 11, 11, 13}, 24);
        // Number[] numbers = calculator.getNumbers();
        // for (Number number : numbers) {
        // System.out.print(number + " ");
        // }
        // System.out.println();
        long start = System.currentTimeMillis();
        // System.out.println(calculator.calculate(true));
        // System.err.println(System.currentTimeMillis() - start);
        ArrayList<Expression> results;
        // results = calculator.getResults();
        // System.out.println(results.size());
        // for (Expression result : results) {
        // System.out.println(result.toString());
        // }
        // System.exit(0);
        int solveCount = 0;
        int solvableCount = 0;
        int maxSolves = 0;
        boolean more = true;
        int moreCount = 0;
        int moreSolveCount = 0;
        CardsSolutionsMap map = CardsSolutionsMap.getInstance();
        for (int i = 1; i <= 13; i++) {
            for (int j = i; j <= 13; j++) {
                for (int k = j; k <= 13; k++) {
                    for (int l = k; l <= 13; l++) {
                        more = false;
                        NumberCalculator calc = new NumberCalculator(new Number[] {i, j, k, l});
                        String numbers = "[" + i + "," + j + "," + k + "," + l + "]";
                        StringBuilder sb = new StringBuilder();
                        sb.append(numbers).append("\n");
                        sb.append(calc.calculate(true)).append("\n");
                        results = calc.getResults();
                        sb.append(results.size()).append("\n");
                        solveCount += results.size();
                        if (results.size() > 0) {
                            solvableCount++;
                        }
                        if (results.size() > maxSolves) {
                            maxSolves = results.size();
                        }
                        for (Expression result : results) {
                            if (!map.hasThisSolution(numbers, result.toString().replaceAll(" ", ""))) {
                                sb.append("MORE: ").append(result).append("\n");
                                more = true;
                                moreSolveCount++;
                            } else {
                                sb.append("SAME: ").append(result).append("\n");
                            }
                        }
                        if (more) {
                            moreCount++;
                            System.out.println(sb);
                        }
                    }
                }
            }
        }
        System.out.println(solvableCount + " " + solveCount + " " + maxSolves);
        System.out.println(moreCount + " " + moreSolveCount);
        System.err.println(System.currentTimeMillis() - start);
    }

}
