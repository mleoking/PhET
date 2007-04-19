package net.sourceforge.retroweaver.tests;

import java.io.IOException;
import java.io.Writer;

public class WriterTest extends AbstractTest {

	static class MyWriter extends Writer {
		public void close() {}
		public void flush() {}
		public void write(char cbuf[], int off, int len) throws IOException {}

		public Writer append(CharSequence csq) throws IOException {return null;};
		public Writer append(CharSequence csq, int start, int end) throws IOException {return null;};
		public Writer append(char c) throws IOException {return null;};
	}

	public void testWriter() {

		MyWriter w = new MyWriter();
		try {
			w.write(' ');

			success("testWriter");
		} catch (IOException ioe) {
		} finally {
			w.close();
		}
	}

}
