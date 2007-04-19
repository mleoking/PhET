package net.sourceforge.retroweaver.runtime.java.math;

import java.math.BigDecimal;

public class BigDecimal_ {

	private BigDecimal_() {
		// private constructor
	}

	public static BigDecimal BigDecimal(int val) {
		// replace int constructor with double one after convertion i2d, no loss of precision
		double d = val;
		return new BigDecimal(d);
	}

	public static BigDecimal BigDecimal(long val) {
		// longs cannot be converted to double, use toString() instead
		String s = Long.toString(val);
		return new BigDecimal(s);
	}

	public static final BigDecimal ZERO = BigDecimal.valueOf(0);

	public static final BigDecimal ONE = BigDecimal.valueOf(1);

	public static final BigDecimal TEN = BigDecimal.valueOf(10);

	public static BigDecimal valueOf(double val) {
		return new BigDecimal(Double.toString(val));
	}

}
