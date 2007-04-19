package net.sourceforge.retroweaver.tests;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BigDecimalTest extends AbstractTest {

	public void testInt() {
		int i = Integer.MAX_VALUE;
		
		BigDecimal bd = new BigDecimal(i);
		
		assertEquals("testInt", bd.toString(), Integer.toString(i));
	}

	private BigDecimal identity(BigDecimal d) { return d; }

	public void testIntInCall() {
		int i = Integer.MAX_VALUE;
		
		BigDecimal bd = identity(new BigDecimal(i));
		
		assertEquals("testInt", bd.toString(), Integer.toString(i));
	}

	public void testLong() {
		long l = Long.MAX_VALUE;
		
		BigDecimal bd = new BigDecimal(l);
		
		assertEquals("testLong", bd.toString(), Long.toString(l));
	}

	public void testStaticFields() {
		assertEquals("testStaticFields", "0", BigDecimal.ZERO.toString());
		assertEquals("testStaticFields", "1", BigDecimal.ONE.toString());
		assertEquals("testStaticFields", "10", BigDecimal.TEN.toString());

		assertEquals("testStaticFields", "10", BigInteger.TEN.toString());
	}

	public void testValueOf() {
		double d = 1.23456d;
		BigDecimal bd = BigDecimal.valueOf(d);
		assertEquals("testValueOf", Double.toString(d), bd.toString());
	}

}
