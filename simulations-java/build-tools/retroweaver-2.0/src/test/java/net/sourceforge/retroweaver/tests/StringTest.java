package net.sourceforge.retroweaver.tests;

public class StringTest extends AbstractTest {

	public void testStringReplace() {
		CharSequence cs1 = "ab";
		CharSequence cs2 = "12";
		
		String in = "abcdef ab";

		String out = in.replace(cs1, cs2);

		assertEquals("testStringReplace", out, "12cdef 12");
	}

}
