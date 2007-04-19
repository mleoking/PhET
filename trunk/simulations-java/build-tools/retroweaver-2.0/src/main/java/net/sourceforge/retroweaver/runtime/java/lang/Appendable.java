package net.sourceforge.retroweaver.runtime.java.lang;

public interface Appendable {

	Appendable append(char c);

	Appendable append(CharSequence csq);

	Appendable append(CharSequence csq, int start, int end);

}