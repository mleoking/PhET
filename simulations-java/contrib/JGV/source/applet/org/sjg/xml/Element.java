/**
 * Free XML Parser for Java 1.1
 * http://vredungmand.dk/programming/sjg_xml/
 *
 * Class that represents an XML element.
 */

package org.sjg.xml;

import java.util.*;

public class Element {
    private Hashtable attributes = new Hashtable();
    private String name;
    private Element parent;
    private Vector contents = new Vector();
    /**
     * Returns the attribute given a name.
     */
    public Attribute getAttribute(String name) {
	if (!attributes.containsKey(name)) throw new Error("Unknown attribute name '"+name+"' in element '"+this.name+"'");
	return (Attribute)attributes.get(name);
    }
    /**
     * Returns whether an attribute of a given name exists.
     */
    public boolean hasAttribute(String name) { return attributes.containsKey(name); }

    public Enumeration attributes() { return attributes.elements(); };
    /**
     * Returns an enumeration over the mixed contents of this element - of type String or Element.
     */
    public Enumeration elements() { return contents.elements(); }
    /**
     * Returns an enumeration over the subelements of this element that have the given name.
     */
    public Enumeration elements(String name) {
	Vector r = new Vector();
	for (Enumeration e=elements(); e.hasMoreElements(); ) {
	    Object o = e.nextElement();
	    if ((o instanceof Element) && (((Element)o).getName().equals(name))) r.addElement(o);
	}
	return r.elements();
    }
    /**
     * Returns the name of this element.
     */
    public String getName() { return name; }
    /**
     * Returns the parent element of this element. If no parent then null is returned.
     */
    public Element getParent() { return parent;	}
    /**
     * Returns a textual representation of the contents of this element.
     */
    public String getContents() {
	StringBuffer result = new StringBuffer(1024);
	for (Enumeration e = elements(); e.hasMoreElements();)
	    result.append(e.nextElement().toString());
	return result.toString();
    }
    /**
     * Returns a textual representation of this element.
     */
    public String toString() {
	String result = "<"+name;
	for (Enumeration e = attributes.keys(); e.hasMoreElements();) {
	    String key = (String)e.nextElement();
	    result += " "+key+" = \""+getAttribute(key).getValue()+"\"";
	}
	result += ">";
	result += getContents();
	result += "</"+name+">";
	return result;
    }
    // Used by parser
    void addAttribute(String key, String value) { attributes.put(key, new Attribute(key, value)); }
    void addContents(Object o) { contents.addElement(o); }
    Element (String name) { this.name = name; }
}
