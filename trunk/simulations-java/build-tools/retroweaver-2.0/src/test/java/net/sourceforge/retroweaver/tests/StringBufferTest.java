package net.sourceforge.retroweaver.tests;

/**
 * Ensures that Retroweaver can handle append, insert and trimToSize methods
 * for StringBuffer and StringBuilder and the new CharSequence based
 * constructors that are new to 1.5.
 */
public class StringBufferTest extends AbstractTest {

	public void testStringBuilder() {
		CharSequence cs = "cs";

		StringBuilder sb = new StringBuilder(cs);
		assertEquals("testStringBuilder constructor", sb.toString(), "cs");

		sb.trimToSize();

		cs = "AB";
		sb.append(cs);
		assertEquals("testStringBuilder append", sb.toString(), "csAB");
		cs = "CD";
		sb.append(cs, 0, 2);
		assertEquals("testStringBuilder append", sb.toString(), "csABCD");

		cs = "abcd";
		sb.insert(4, cs);
		assertEquals("testStringBuilder insert", sb.toString(), "csABabcdCD");
		cs = "efgh";
		sb.insert(4, cs, 0, 4);
		assertEquals("testStringBuilder insert", sb.toString(), "csABefghabcdCD");

		cs = null;
		sb.append(cs);
		assertEquals("testStringBuilder append", sb.toString(), "csABefghabcdCDnull");
		sb.append(cs, 0, 1);
		assertEquals("testStringBuilder append", sb.toString(), "csABefghabcdCDnulln");
	}

	public void testStringBuffer() {
		CharSequence cs = "cs";

		StringBuffer sb = new StringBuffer(cs);
		assertEquals("testStringBuffer constructor", sb.toString(), "cs");

		sb.trimToSize();

		cs = "AB";
		sb.append(cs);
		assertEquals("testStringBuffer append", sb.toString(), "csAB");
		cs = "CD";
		sb.append(cs, 0, 2);
		assertEquals("testStringBuffer append", sb.toString(), "csABCD");

		cs = "abcd";
		sb.insert(4, cs);
		assertEquals("testStringBuffer insert", sb.toString(), "csABabcdCD");
		cs = "efgh";
		sb.insert(4, cs, 0, 4);
		assertEquals("testStringBuffer insert", sb.toString(), "csABefghabcdCD");

		cs = null;
		sb.append(cs);
		assertEquals("testStringBuffer append", sb.toString(), "csABefghabcdCDnull");
		sb.append(cs, 0, 1);
		assertEquals("testStringBuffer append", sb.toString(), "csABefghabcdCDnulln");
	}

}
