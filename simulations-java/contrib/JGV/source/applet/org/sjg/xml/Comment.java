/**
 * Free XML Parser for Java 1.1
 * http://vredungmand.dk/programming/sjg_xml/
 *
 * Represents an XML-comment.
 */

package org.sjg.xml;

public class Comment {
    private String text;
    /**
     * Returns the text of this comment.
     */
    public String getText() { return text; }

    /**
     * Returns the data of this data section.
     */
    public String toString() { return "<!--"+text+"-->"; }
    /**
     * Constructs a new data comment containing the given data.
     */
    public Comment (String text) {
	this.text = text;
    }
}
