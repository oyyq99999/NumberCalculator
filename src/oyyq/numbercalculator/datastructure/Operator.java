package oyyq.numbercalculator.datastructure;

public enum Operator {
	PLUS("+", 1, true), // plus
	MINUS("-", 1, false), // minus
	MULTIPLY("*", 2, true), // multiply
	DIVIDE("/", 2, false); // divide

	public static final int PRIORITY_MULTIPLY_DIVIDE = 2;
	public static final int PRIORITY_PLUS_MINUS = 1;
	private boolean exchangable;
	private int priority;
	private String strForm;

	Operator(String strForm, int priority, boolean exchangable) {
		this.strForm = strForm;
		this.priority = priority;
		this.exchangable = exchangable;
	}

	public int getPriority() {
		return priority;
	}

	public boolean isExchangable() {
		return exchangable;
	}

	@Override
	public String toString() {
		return strForm;
	}
}
