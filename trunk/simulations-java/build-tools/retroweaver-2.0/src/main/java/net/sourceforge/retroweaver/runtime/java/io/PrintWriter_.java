package net.sourceforge.retroweaver.runtime.java.io;

import java.io.PrintWriter;

public class PrintWriter_ {

	public static PrintWriter append(PrintWriter w, char c) {
		w.write(c);
		return w;
	}

	public static PrintWriter append(PrintWriter w, CharSequence csq) {
		w.write(csq==null?"null":csq.toString());
		return w;
	}

	public static PrintWriter append(PrintWriter w, CharSequence csq, int start, int end) {
		w.write(csq==null?"null".substring(start, end):csq.subSequence(start, end).toString());
		return w;
	}

}
