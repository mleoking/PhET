package net.sourceforge.retroweaver.tests;

public class ThreadTest extends AbstractTest {

	public void testStackTrace() {
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		
		assertNotNull("not null", stack);

		assertTrue("not empty", stack.length > 0);
	}

}
