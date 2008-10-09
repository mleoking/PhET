/**
 * Free XML Parser for Java 1.1
 * http://vredungmand.dk/programming/sjg_xml/
 *
 * An xml parser. Use the static methods parse(String) or parse(InputStream)
 */

package org.sjg.xml;

import java.util.*;
import java.io.*;

public class Parser {
    final static int OPEN_TAG = 0;
    final static int CLOSE_TAG = 1;
    final static int OPEN_CLOSE_TAG = 2;
    private Stack stack = new Stack();
    private Element topElement;
    private Element rootElement;
    private StringBuffer header = new StringBuffer(1024);
    private void pushElement(Element element) {
	if (topElement == null)
	    rootElement = element;
	else
	    topElement.addContents(element);
	stack.push(element);
	topElement = element;
    }
    private void popElement() {
	stack.pop();
	try {
	    topElement = (Element)stack.peek();
	} catch (Exception e) { topElement = null; }
    }
    private void newElement(String s2) {
	String s;
	String name = "";
	int tagStyle;
	if (s2.endsWith("/>")) {
	    tagStyle = OPEN_CLOSE_TAG;
	    s = s2.substring(1,s2.length()-2);
	} else if (s2.startsWith("</")) {
	    tagStyle = CLOSE_TAG;
	    s = s2.substring(2,s2.length()-1);
	} else {
	    tagStyle = OPEN_TAG;
	    s = s2.substring(1,s2.length()-1);
	}
	try {
	    if (s.indexOf(' ') < 0) {
		name = s;
		switch (tagStyle) {
		case OPEN_TAG: pushElement(new Element(name)); break;
		case CLOSE_TAG:
		    if (topElement.getName().equals(name)) popElement();
		    else throw new Error("Expected close of '"+topElement.getName()+"' instead of "+s2);
		    break;
		case OPEN_CLOSE_TAG:
		    pushElement(new Element(name));
		    popElement();
		}
	    } else {
		Element element = null;
		name = s.substring(0, s.indexOf(' '));
		switch (tagStyle) {
		case OPEN_TAG:
		    element = new Element(name);
		    pushElement(element);
		    break;
		case CLOSE_TAG:
	            new Error("Syntax Error: "+s2);
		    break;
		case OPEN_CLOSE_TAG:
		    element = new Element(name);
		    pushElement(element);
		    popElement();
		}
		String data = s.substring(s.indexOf(' ')+1);
		boolean withinQuote = false;
		boolean atValue = false;
		StringBuffer value=new StringBuffer(128);
		StringBuffer attribute=new StringBuffer(32);
		for (int i = 0; i < data.length(); i++) {
		    switch (data.charAt(i)) {
		    case '"':withinQuote =! withinQuote; break;
		    case ' ':
			if ((atValue==true) && (withinQuote==true)) value.append(data.charAt(i)); else
			    if (value.length() > 0) {
				element.addAttribute(attribute.toString(), value.toString());
				atValue=false;
				value = new StringBuffer();
				attribute = new StringBuffer();
			    }
			break;
		    case '=':
			if (withinQuote == false) {
			    atValue=true;
			}
			break;
		    default:
			if (atValue) value.append(data.charAt(i));
			else attribute.append(data.charAt(i));
		    }
		}
		if (value.length() > 0) {
		    element.addAttribute(attribute.toString(), value.toString());
		}
	    }
	} catch (Exception e) {
	    throw new Error("Cannot parse element '"+s2+"' - ("+e+")");
	}
    }
    private void newData(String s) {
	if (topElement != null) topElement.addContents(new Data(s));
	else if (rootElement == null) header.append(s);
    }
    private void newComment(String s) {
	if (topElement != null) topElement.addContents(new Comment(s.substring(4, s.length()-3)));
	else if (rootElement == null) header.append(s);
    }
    private void newProcessingInstruction(String s) {
	if (topElement != null) topElement.addContents(new ProcessingInstruction(s.substring(2, s.length()-2)));
	else if (rootElement == null) header.append(s);
    }
    private Document parseText(String text) {
	for (Tokenizer tokenizer = new Tokenizer(text); tokenizer.hasMoreElements(); ) {
	    String e = tokenizer.nextElement();
	    if ((e.startsWith("<?")) && (e.endsWith("?>"))) newProcessingInstruction(e); else
	    if ((e.startsWith("<!--")) && (e.endsWith("-->"))) newComment(e); else
	    if (e.charAt(0) == '<') newElement(e);
	    else newData(e);
	}
	return new Document(header.toString(), rootElement);
    }
    /**
     * Parses a text string.
     */
    public static Document parse(String text) {
	return (new Parser()).parseText(text);
    }
    /**
     * Parses an input stream.
     */
    public static Document parse(InputStream is) {
	StringBuffer s = new StringBuffer(10000);
	try {
	    int time = 0;
	    while (is.available() == 0) {
		time ++;
		try { Thread.sleep(100); } catch (Exception e) { }
		if (time > 100) throw new Error("Parser: InputStream timed out");
	    }
	    BufferedReader br = new BufferedReader(new InputStreamReader(is));
	    while (br.ready()) {
		s.append(br.readLine());
		s.append("\n");
	    }
	    br.close();
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
	return (new Parser()).parseText(s.toString());
    }
}

