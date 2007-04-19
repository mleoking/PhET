package net.sourceforge.retroweaver.tests;

import junit.framework.TestCase;

public abstract class AbstractTest extends TestCase {

	public void success() {
	}

	public void success(String msg) {
	}

	public void assertArraySameItems(String msg, Object[] a1, Object[] a2) {
		if (a1 == null) {
			assertNull(msg, a2);
		} else if (a2 == null || a1.length != a2.length) {
			fail(msg);
		} else {
			for (int i = 0; i < a1.length; i++) {
				assertSame(msg, a1[i], a2[i]);
			}
		}
	}
}
