package net.sourceforge.retroweaver.runtime.java.io;

import java.io.CharArrayWriter;

public class CharArrayWriter_ {

	public static CharArrayWriter append(CharArrayWriter w, char c) {
		w.write(c);
		return w;
	}

	public static CharArrayWriter append(CharArrayWriter w, CharSequence csq) {
		String s = csq==null?"null":csq.toString();
		w.write(s, 0, s.length());
		return w;
	}

	public static CharArrayWriter append(CharArrayWriter w, CharSequence csq, int start, int end) {
		String s = csq==null?"null".substring(start, end):csq.subSequence(start, end).toString();
		w.write(s, 0, s.length());
		return w;
	}

}
