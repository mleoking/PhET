package net.sourceforge.retroweaver.runtime.java.io;

import java.io.StringWriter;

public class StringWriter_ {

	public static StringWriter append(StringWriter w, char c) {
		w.write(c);
		return w;
	}

	public static StringWriter append(StringWriter w, CharSequence csq) {
		w.write(csq==null?"null":csq.toString());
		return w;
	}

	public static StringWriter append(StringWriter w, CharSequence csq, int start, int end) {
		w.write(csq==null?"null".substring(start, end):csq.subSequence(start, end).toString());
		return w;
	}

}
