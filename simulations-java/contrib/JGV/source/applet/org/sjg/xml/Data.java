/**
 * Free XML Parser for Java 1.1
 * http://vredungmand.dk/programming/sjg_xml/
 *
 * Represents a data section of an XML-document.
 */

package org.sjg.xml;

public class Data {
    private String text;
    /**
     * Returns the data of this data section.
     */
    public String toString() { return text; }
    /**
     * Constructs a new data section containing the given data.
     */
    public Data (String text) {
	this.text = text;
    }
}
