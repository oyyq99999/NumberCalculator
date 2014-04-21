package oyyq.numbercalculator;

import java.util.ArrayList;

import oyyq.numbercalculator.datastructure.Expression;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		NumberCalculator calculator = new NumberCalculator(new Number[] { 4, 11, 11, 12, 13 }, 24);
		Number[] numbers = calculator.getNumbers();
		for (Number number : numbers) {
			System.out.print(number + " ");
		}
		System.out.println();
		// System.err.close();
		long start = System.currentTimeMillis();
		System.out.println(calculator.calculate(true));
		System.err.println(System.currentTimeMillis() - start);
		ArrayList<Expression> results = calculator.getResults();
		System.out.println(results.size());
		for (Expression result : results) {
			System.out.println(result.toString());
		}
		System.exit(0);
		int solveCount = 0;
		int solvableCount = 0;
		int maxSolves = 0;
		for (int i = 1; i <= 13; i++) {
			for (int j = i; j <= 13; j++) {
				for (int k = j; k <= 13; k++) {
					for (int l = k; l <= 13; l++) {
						NumberCalculator calc = new NumberCalculator(new Number[] { i, j, k, l });
						System.out.println(i + " " + j + " " + k + " " + l);
						System.out.println(calc.calculate(true));
						results = calc.getResults();
						System.out.println(results.size());
						solveCount += results.size();
						if (results.size() > 0) {
							solvableCount++;
						}
						if (results.size() > maxSolves) {
							maxSolves = results.size();
						}
						for (Expression result : results) {
							System.out.println(result);
						}
					}
				}
			}
		}
		System.out.println(solvableCount + " " + solveCount + " " + maxSolves);
	}

}
