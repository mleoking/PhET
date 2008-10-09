/**
 * Free XML Parser for Java 1.1
 * http://vredungmand.dk/programming/sjg_xml/
 *
 * Represents an XML-document. You will want to access the root element via the getRoot() method.
 */

package org.sjg.xml;

public class Document {
    private String header;
    private Element root;
    /**
     * Return the document root.
     */
    public Element getRoot() { return root; }
    /**
     * Returns the header of the document in unparsed textual form.
     */
    public String getHeader() { return header; }
    /**
     * Constructs a new document with given header and document root.
     */
    public Document(String header, Element root) {
	this.header = header;
	this.root = root;
    }
    /**
     * Constructs a new document with the given element as root and default header.
     */
    public Document(Element root) {
	this("<?xml version=\"1.0\" standalone=\"yes\" ?>\n", root);
    }
    /**
     * Returns the textual representation of this document.
     */
    public String toString() {
	return header+root.toString();
    }
}
