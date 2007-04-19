package net.sourceforge.retroweaver.tests;

import java.io.IOException;

public class AppendableTest extends AbstractTest {

	public void testAppendable() throws IOException {
		// Should generate a warning - implementing interface from 1.5
		class Ap implements Appendable {
			public Appendable append(char c) {
				return this;
			}

			public Appendable append(CharSequence csq) {
				return this;
			}

			public Appendable append(CharSequence csq, int start, int end) {
				return this;
			}
		}

		CharSequence csq = "abc";

		Ap a = new Ap();
		a.append('c').append(csq).append(csq, 0, 1);
	}

}
