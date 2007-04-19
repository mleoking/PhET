package net.sourceforge.retroweaver.runtime.java.lang;

import java.util.regex.Pattern;

import net.sourceforge.retroweaver.runtime.java.util.Formatter;

public class String_ {

	private String_() {
		// private constructor
	}

	public static String replace(String s, CharSequence target,
            CharSequence replacement) {
		if (target == null || replacement == null) {
			throw new NullPointerException();
		}

		Pattern p = Pattern.compile(target.toString(), Pattern.LITERAL);

		return p.matcher(s).replaceAll(replacement.toString());
	}

	public static String format(String s, Object... params) {
		return new Formatter().format(s, params).toString();
	}

	public static boolean contains(String s, CharSequence seq) {
		if (seq == null) {
			throw new NullPointerException();
		}
		return s.indexOf(seq.toString()) != -1;
	}

}
