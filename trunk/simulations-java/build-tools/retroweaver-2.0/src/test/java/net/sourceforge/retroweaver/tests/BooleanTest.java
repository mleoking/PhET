package net.sourceforge.retroweaver.tests;

public class BooleanTest extends AbstractTest{

	public void testParseBoolean() {
		assertTrue("testParseBoolean", Boolean.parseBoolean("true"));
		assertTrue("testParseBoolean", Boolean.parseBoolean("tRue"));

		assertFalse("testParseBoolean", Boolean.parseBoolean(null));
		assertFalse("testParseBoolean", Boolean.parseBoolean("false"));
		assertFalse("testParseBoolean", Boolean.parseBoolean("boolean"));
		assertFalse("testParseBoolean", Boolean.parseBoolean("truee"));
	}

	public void testCompareTo() {
		Boolean true1 = Boolean.TRUE;
		Boolean false1 = Boolean.FALSE;
		Boolean true2 = new Boolean("TRUE"); // NOPMD by xlv
		Boolean false2 = new Boolean("FALSE"); // NOPMD by xlv
		
		assertEquals("testCompareTo", 0, true1.compareTo(true1));
		assertEquals("testCompareTo", 0, true1.compareTo(true2));
		assertEquals("testCompareTo", 0, false1.compareTo(false1));
		assertEquals("testCompareTo", 0, false1.compareTo(false2));

		assertTrue("testCompareTo", true1.compareTo(false1) > 0);
		assertTrue("testCompareTo", false1.compareTo(true1) < 0);
	}

}
