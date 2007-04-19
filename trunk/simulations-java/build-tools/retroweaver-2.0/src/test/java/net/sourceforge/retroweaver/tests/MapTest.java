package net.sourceforge.retroweaver.tests;

import java.util.Collections;
import java.util.Map;

public class MapTest extends AbstractTest {

	public void testEmptyMap() {
		Map<Object, Object> m = Collections.emptyMap();
		// replaced with Collections.EMPTY_MAP

		assertEquals("testEmptyMap", m.size(), 0);
	}

}
