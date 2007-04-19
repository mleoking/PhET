package net.sourceforge.retroweaver.tests;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class WriterAppendTest extends AbstractTest {

	public void testAppendChar() {
		CharArrayWriter c = new CharArrayWriter();
		Writer w = c;
		try {
			w.append('a');
		} catch (IOException ioe) {
		}

		assertEquals("testAppendChar", "a", c.toString());
	}

	public void testAppendSequence() {
		CharSequence csq = "bcd";
		CharArrayWriter c = new CharArrayWriter();
		Writer w = c;
		try {
			w.append(csq);
			w.append(csq, 0, 2);
			csq = null;
			w.append(csq);
			w.append(csq, 0, 1);
		} catch (IOException ioe) {
		}

		assertEquals("testAppendSequence", "bcdbcnulln", c.toString());
	}


	public void testStringWriterAppendChar() {
		StringWriter w = new StringWriter();
		w.append('a');

		assertEquals("testStringWriterAppendChar", "a", w.toString());
	}

	public void testStringWriterAppendSequence() {
		CharSequence csq = "bcd";
		StringWriter w = new StringWriter();
		w.append(csq);
		w.append(csq, 0, 2);
		csq = null;
		w.append(csq);
		w.append(csq, 0, 1);

		assertEquals("testStringWriterAppendSequence", "bcdbcnulln", w.toString());
	}

}
