package net.sourceforge.retroweaver.tests;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Demonstrates Retroweaver's support for java.lang.Iterable.
 */
public class IterableTest extends AbstractTest {

	public void testCollection() {
		// The following code works for all Iterable classes in the JDK
		// inheriting from Collection as the iterator() call is weaved into
		// its IterableMethods static equivalent.

		Iterable<Integer> itConversion = Arrays.asList(new Integer[] { 1, 2, 3 });

		String s = "";
		for (Integer i : itConversion) {
			s = s + i + ':';
		}
		assertEquals("testCollection", s, "1:2:3:");
	}
	
	public void testNull() {
		String s = "";
		try {
			Iterable<Integer> iterable = null;
			for(Integer i: iterable) {
				s = s + i + ':';
			}
			fail("testNull Collection");
		} catch (NullPointerException npe) { // NOPMD by xlv
			success("testNull Collection");
		}

		try {
			Integer[] ia = null;
			for (Integer i : ia) {
				s = s + i + ':';
			}
			fail("testNull Array");
		} catch (NullPointerException npe) { // NOPMD by xlv
			success("testNull Array");
		}
	}

	public void testArray() {
		Integer[] ia = new Integer[] { 1, 2, 3 };

		String s = "";
		for (Integer i : ia) {
			s = s + i + ':';
		}
		assertEquals("testArray", s, "1:2:3:");		
	}

	public void testWeavedIterable() {
		// This test works just fine, because the anonymous class is weaved
		// by Retroweaver.

		Iterable<Integer> it = new MyIterable();

		String s = "";
		for (Integer i : it) {
			s = s + i + ':';
		}
		assertEquals("testWeavedIterable", s, "1:2:3:");
	}

	
	public void testIterableMethod() {
		try {
			// regression test
			MyIterable.getIteratable(new MyIterable());

			success("testIterableMethod");
		} catch (NoClassDefFoundError e) {
			fail("NoClassDefFoundError: " + e.getMessage());
		}
	}

	private static class MyIterable implements Iterable<Integer> {
		public Iterator<Integer> iterator() {
			return Arrays.asList(new Integer[] { 1, 2, 3 }).iterator();
		}
		
		public static Iterable<Integer> getIteratable(MyIterable i) {
			return i;
		}
	}
}
