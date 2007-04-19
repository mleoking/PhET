package net.sourceforge.retroweaver.tests;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;

public class VerifierTest extends AbstractTest {

	public void testVerifierNoWarnings() {
		// Shouldn't generate warning, because Retroweaver handles Iterables
		class It implements Iterable {
			public Iterator iterator() {
				return Arrays.asList(new int[] { 1, 2, 3 }).iterator();
			}
		}

		// Shouldn't generate a warning, because Retroweaver handles
		// StringBuilder
		String s1 = "a" + 3;

		// Shouldn't generate warnings, because Retroweaver handles autoboxing
		// and autounboxing
		Integer i = 3;
		int i_ = i;

		assertTrue("testVerifierNoWarnings", (i_ == 3) && (s1.equals("a3")));
	}

	/**
	 * RefVerifier tests, shouldn't generate warnings
	 * 
	 * @author Guntis Ozols
	 */
	public void testVerifierNoWarnings2() throws IOException {
		assertEquals("Class name", getCells().getClass().getName(),
				"[[Lnet.sourceforge.retroweaver.tests.VerifierTest$Cell;");

		ByteArrayOutputStreamEx bout = new ByteArrayOutputStreamEx();
		bout.write("Hello\n".getBytes());
		bout.writeTo(new ByteArrayOutputStream());
		Hello2 hello = new HelloImpl();
		hello.hello();

		success("testVerifierNoWarnings2");
	}

	interface Hello1 {
		void hello();
	}

	interface Hello2 extends Hello1 {
	}

	static class HelloImpl implements Hello2 {
		public void hello() {
		}
	}

	static class Cell {
	}

	static class ByteArrayOutputStreamEx extends ByteArrayOutputStream {
		public void writeTo(OutputStream out) throws IOException {
			out.write(buf, 0, count);
		}
	}

	static Cell[][] getCells() {
		return new Cell[1][1];
	}

}

interface Address {

	int getZip();
}

abstract class AbstractAddress implements Address {

	public void displayZip() {
		System.out.println(getZip());
	}
}
