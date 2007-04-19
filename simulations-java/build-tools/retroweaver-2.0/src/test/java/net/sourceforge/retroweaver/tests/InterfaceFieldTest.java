package net.sourceforge.retroweaver.tests;

public class InterfaceFieldTest extends AbstractTest {

	public void testInterfaceField() {
		assertEquals("NAMES length", ConstImpl.NAMES.length, 3);
	}

	interface Const {
		String[] NAMES = new String[] { "A", "B", "C" };
	}

	static class ConstImpl implements Const {
	}

}
