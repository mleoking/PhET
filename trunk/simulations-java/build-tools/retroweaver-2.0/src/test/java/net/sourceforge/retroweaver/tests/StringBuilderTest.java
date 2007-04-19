package net.sourceforge.retroweaver.tests;

/**
 * Ensures that Retroweaver can handle compiler-generated calls to StringBuilder
 * that are new to 1.5.
 */
public class StringBuilderTest extends AbstractTest {

	public void testSimple() {
		String a = "Hello, ";
		String b = "world!";
		String c = a + b;
		assertEquals("StringBuilder", c, "Hello, world!");

		String s = "This is " + StringBuilderTest.class + " version " + 1;
		assertEquals("StringBuilder", s,
				"This is class net.sourceforge.retroweaver.tests.StringBuilderTest version 1");
	}

}
