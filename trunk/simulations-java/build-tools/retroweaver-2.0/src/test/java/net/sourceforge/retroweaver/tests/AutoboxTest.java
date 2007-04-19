package net.sourceforge.retroweaver.tests;

/**
 * Ensures that Retroweaver performs autoboxing conversions
 */

public class AutoboxTest extends AbstractTest {

	public void testByte() {
		Byte b;
		b = -3;
		assertEquals("ByteSmall", b.byteValue(), (byte) -3);
		b = 3;
		assertEquals("ByteSmall", b.byteValue(), (byte) 3);
	}

	public void testBoolean() {
		Boolean bool;
		bool = true;
		assertTrue("BooleanTrue", bool.booleanValue());
		bool = false;
		assertFalse("BooleanFalse", bool.booleanValue());
	}

	public void testCharacter() {
		Character c;
		c = 'a';
		assertEquals("CharacterSmall", c.charValue(), 'a');
		c = '\u1000';
		assertEquals("CharacterLarge", c.charValue(), '\u1000');
	}

	public void testShort() {
		Short s;
		s = -3;
		assertEquals("ShortSmall", s.shortValue(), (short) -3);
		s = 3;
		assertEquals("ShortSmall", s.shortValue(), (short) 3);
		s = 3000;
		assertEquals("ShortLarge", s.shortValue(), (short) 3000);
	}

	public void testInteger() {
		Integer i;
		i = -3;
		assertEquals("IntegerSmall", i.intValue(), -3);
		i = 3;
		assertEquals("IntegerSmall", i.intValue(), 3);
		i = 3000;
		assertEquals("IntegerLarge", i.intValue(), 3000);
	}

	public void testLong() {
		Long l;
		l = -3L;
		assertEquals("Long", l.longValue(), -3L);
		l = 3L;
		assertEquals("Long", l.longValue(),  3L);
	}

	public void testFloat() {
		Float f;
		f = -3.2f;
		assertEquals("Float", f.floatValue(), -3.2f);
		f = 3.2f;
		assertEquals("Float", f.floatValue(), 3.2f);
		f = Float.NaN;
		assertTrue("Float NaN", f.isNaN());
	}

	public void testDouble() {
		Double d;
		d = -3.2d;
		assertEquals("Double", d.doubleValue(), -3.2d);
		d = 3.2d;
		assertEquals("Double", d.doubleValue(), 3.2d);
		d = Double.NaN;
		assertTrue("Double NaN", d.isNaN());
	}

	public void testIdentity() {
		// tests cached values from lookup tables:
		// check that same object is returned
		Boolean b1, b2;
		b1 = false; b2 = false; assertSame("Boolean", b1, b2);
		b1 = true; b2 = true; assertSame("Boolean", b1, b2);
		
		Byte byte1, byte2;
		byte1 = 1; byte2 = 1; assertSame("Byte", byte1, byte2);
		
		Character c1, c2;
		c1 = 'c'; c2 = 'c'; assertSame("Character", c1, c2);
		
		Short s1, s2;
		s1 = 1; s2 = 1; assertSame("Short", s1, s2);
		
		Integer i1, i2;
		i1 = 1; i2 = 1; assertSame("Integer", i1, i2);
	}
	
	public void testArray() {
		 Object[] dummies = new Object[] { 1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 6, 0,
	                7, 0, 8, 0, 9, 0, 10, 0, };
		 
		 assertEquals("testArray", 20, dummies.length);
	}
}
