package net.sourceforge.retroweaver.tests;

import net.sourceforge.retroweaver.runtime.java.lang.Iterable_;

/**
 * Ensures that the entire retroweaver code is tested. Note: this is the only
 * test class that should import the retroweaver classes directly.
 */

public class FullCoverageTest extends AbstractTest {

	public void testFullCoverage() {
		try {
			Iterable_.iterator("");
			fail("IterableMethods should raise an UnsupportedOperationException");
		} catch (UnsupportedOperationException e) {
		}
	}

}
