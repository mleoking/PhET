package net.sourceforge.retroweaver.tests;

public class SystemTest extends AbstractTest {

	public void testNanoTime() {
		Long t1 = System.nanoTime();
		Long t2 = System.nanoTime();

		assertTrue("testNanoTime", t2 >= t1);
	}

}
