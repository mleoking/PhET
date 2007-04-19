package net.sourceforge.retroweaver.tests;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CollectionsTest extends AbstractTest {

	public void testEmptyList() {
		List<Object> l = Collections.emptyList();

		assertEquals("testEmptyList", l.size(), 0);
	}

	public void testEmptySet() {
		Set<Object> s = Collections.emptySet();

		assertEquals("testEmptySet", s.size(), 0);
	}

	public void testArrays() {
		int[] a = { 1, 2};
		assertEquals("testArrays", "[1, 2]", Arrays.toString(a));
	}

}
