/**
 * Free XML Parser for Java 1.1
 * http://vredungmand.dk/programming/sjg_xml/
 *
 * Tokenizer for the parser. Splits the input into tokens either being text data or an xml tag.
 */
package org.sjg.xml;

public class Tokenizer {
    public Tokenizer(String text) { this.text = text; }
    private String text;
    private int pointer;
    public boolean hasMoreElements() { return pointer < text.length(); }
    public String nextElement() {
	if (text.charAt(pointer) == '<') {
	    return nextTag();
	} else {
	    return nextString();
	}
    }
    private String nextTag() {
	boolean withinQuote = false;
	int start = pointer;
	do {
	    switch (text.charAt(pointer)) {
	    case '"': withinQuote =! withinQuote;
	    }
	    pointer ++;
	} while ((pointer < text.length()) && ((text.charAt(pointer)!='>') || withinQuote));
	if (pointer < text.length()) pointer++;
	else throw new Error("Tokenizer error: < without > at end of text");
	return text.substring(start, pointer);
    }
    private String nextString() {
	boolean withinQuote = false;
	int start = pointer;
	do {
	    switch (text.charAt(pointer)) {
	    case '"': withinQuote =! withinQuote;
	    }
	    pointer ++;
	} while ((pointer < text.length()) && ((text.charAt(pointer)!='<') || withinQuote));
	return text.substring(start, pointer);
    }
}
