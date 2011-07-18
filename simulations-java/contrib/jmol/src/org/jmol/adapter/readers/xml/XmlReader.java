/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2006-08-02 11:48:43 -0500 (Wed, 02 Aug 2006) $
 * $Revision: 5364 $
 *
 * Copyright (C) 2003-2005  Miguel, Jmol Development, www.jmol.org
 *
 * Contact: jmol-developers@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jmol.adapter.readers.xml;

import org.jmol.adapter.smarter.*;


import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;
import java.util.Hashtable;
import java.util.Map;

import netscape.javascript.JSObject;

import org.jmol.util.Logger;

/**
 * A generic XML reader template -- by itself, does nothing.
 * 
 * The actual readers are XmlCmlReader, XmlMolproReader (which is an
 * extension of XmlCmlReader, XmlChem3dReader, and XmlOdysseyReader, which is wholely different.
 * 
 * 
 * XmlReader takes all XML streams, whether from a file reader or from DOM.
 *  
 * This class functions as a resolver, since it:
 *  (1) identifying the specific strain of XML to be handled, and 
 *  (2) passing the responsibility on to the correct format-specific XML readers. 
 * There are parallel entry points and handler methods for reader and DOM. Each 
 * format-specific XML reader then assigns its own handler to manage the 
 * parsing of elements.
 * 
 * In addition, this class handles generic XML tag parsing.
 * 
 * XmlReader.JmolXmlHandler extends DefaultHandler is the generic interface to both reader and DOM element parsing.
 * 
 * XmlCmlReader extends XmlReader and is where I'd like Andrew to take charge.
 * XmlCmlReader.CmlHandler extends XmlReader.JmolXmlHandler is generic
 * 
 * XmlMolproReader extends XmlCmlReader. If you feel like expanding on that, feel free.
 * XmlMolproReader.MolprolHandler extends XmlCmlReader.CmlHandler adds Molpro-specific XML tag processing
 * 
 * XmlChem3dReader extends XmlReader. That one is simple; no need to expand on it at this time.
 * XmlChem3dReader.Chem3dHandler extends XmlReader.JmolXmlHandler is generic
 * 
 * XmlOdysseyReader extends XmlReader. That one is simple; no need to expand on it at this time.
 * XmlOdysseyReader.OdysseyHandler extends XmlReader.JmolXmlHandler is generic
 * 
 * Note that the tag processing routines are shared between SAX 
 * and DOM processors. This means that attributes must be
 * transformed from either Attributes (SAX) or JSObjects (DOM)
 * to Hashtable name:value pairs. This is taken care of in JmolXmlHandler
 * for all readers. 
 * 
 * TODO 27/8/06:
 * 
 * Several aspects of CifReader are NOT YET implemented here. 
 * These include loading a specific model when there are several,
 * applying the symmetry, and loading fractional coordinates. [DONE for CML reader 2/2007 RMH]
 * 
 * The DOM reader is NOT CHECKED OVER, and I do not think that it supports
 * reading characters between start/end tags:
 * 
 *  <tag>characters</tag>
 *  
 *  If you work on this, please read formats other than CML into DOM so that
 *  we can see that that works as well.
 *  
 *  Test files:
 *  
 *  molpro:  vib.xml
 *  odyssey: water.xodydata
 *  cml: a wide variety of files in data-files. Feel free to prune if some are
 *  not of use.
 * 
 * -Bob Hanson
 * 
 */

public class XmlReader extends AtomSetCollectionReader {

  //XmlReader subReader; // the actual reader; to be determined
  protected XmlReader parent;    // XmlReader itself; to be assigned by the subReader

  protected Atom atom;

  String[] implementedAttributes = { "id" };

  /////////////// file reader option //////////////

  @Override
  public void initializeReader() throws Exception {
    XMLReader xmlReader = getXMLReader();
    if (xmlReader == null) {
      atomSetCollection = new AtomSetCollection("xml", this);
      atomSetCollection.errorMessage = "No XML reader found";
      return;
    }
    processXml(xmlReader);
    continuing = false;
  }

  private XMLReader getXMLReader() {
    XMLReader xmlr = null;
    // JAXP is preferred (comes with Sun JVM 1.4.0 and higher)
    if (xmlr == null
        && System.getProperty("java.version").compareTo("1.4") >= 0)
      xmlr = allocateXmlReader14();
    // Aelfred is the first alternative.
    if (xmlr == null)
      xmlr = allocateXmlReaderAelfred2();
    return xmlr;
  }

  private XMLReader allocateXmlReader14() {
    XMLReader xmlr = null;
    try {
      javax.xml.parsers.SAXParserFactory spf = javax.xml.parsers.SAXParserFactory
          .newInstance();
      spf.setNamespaceAware(true);
      javax.xml.parsers.SAXParser saxParser = spf.newSAXParser();
      xmlr = saxParser.getXMLReader();
      Logger.debug("Using JAXP/SAX XML parser.");
    } catch (Exception e) {
      Logger.debug("Could not instantiate JAXP/SAX XML reader: "
          + e.getMessage());
    }
    return xmlr;
  }

  private XMLReader allocateXmlReaderAelfred2() {
    XMLReader xmlr = null;
    try {
      xmlr = (XMLReader) this.getClass().getClassLoader().loadClass(
          "gnu.xml.aelfred2.XmlReader").newInstance();
      Logger.debug("Using Aelfred2 XML parser.");
    } catch (Exception e) {
      Logger.debug("Could not instantiate Aelfred2 XML reader!");
    }
    return xmlr;
  }

  private Object processXml(XMLReader xmlReader) throws Exception {
    atomSetCollection = new AtomSetCollection(readerName, this);
    Object res = getXmlReader();
    if (res instanceof String)
      return res;
    XmlReader thisReader = (XmlReader)res;
    thisReader.processXml(this, atomSetCollection, reader, xmlReader);
    return thisReader;
  }

  private Object getXmlReader() {    
    String className = null;
    Class<?> atomSetCollectionReaderClass;
    String err = null;
    XmlReader thisReader = null;
    try {
      int pt = readerName.indexOf("(");
      String name = (pt < 0 ? readerName : readerName.substring(0, pt));
      className = Resolver.getReaderClassBase(name);
      atomSetCollectionReaderClass = Class.forName(className);//,true, Thread.currentThread().getContextClassLoader());
      thisReader = (XmlReader) atomSetCollectionReaderClass
          .newInstance();
    } catch (Exception e) {
      err = "File reader was not found:" + className;
      Logger.error(err);
      return err;
    }
    return thisReader;
  }
  
  /**
   * 
   * @param parent
   * @param atomSetCollection
   * @param reader
   * @param xmlReader
   */
  protected void processXml(XmlReader parent,
                         AtomSetCollection atomSetCollection,
                          BufferedReader reader, XMLReader xmlReader) {
  }

  protected void parseReaderXML(XMLReader xmlReader) {
    xmlReader.setEntityResolver(new DummyResolver());
    InputSource is = new InputSource(reader);
    is.setSystemId("foo");
    try {
      xmlReader.parse(is);
    } catch (Exception e) {
      e.printStackTrace();
      atomSetCollection.errorMessage = "XML parsing error: " + e.getMessage();
    }
  }

  /////////////// DOM option //////////////

  @Override
  protected void processXml(Object DOMNode) {
    atomSetCollection = new AtomSetCollection(readerName, this);
    String className = null;
    Class<?> atomSetCollectionReaderClass;
    XmlReader thisReader = null;
    String name = readerName.substring(0, readerName.indexOf("("));
    try {
      className = Resolver.getReaderClassBase(name);
      atomSetCollectionReaderClass = Class.forName(className);//,true, Thread.currentThread().getContextClassLoader());
      thisReader = (XmlReader) atomSetCollectionReaderClass
          .newInstance();
      thisReader.processXml(this, atomSetCollection, reader, (JSObject) DOMNode);
    } catch (Exception e) {
      atomSetCollection.errorMessage = "File reader was not found:" + className;
    }
  }

  /**
   * 
   * @param parent
   * @param atomSetCollection
   * @param reader
   * @param DOMNode
   */
  protected void processXml(XmlReader parent,
                            AtomSetCollection atomSetCollection,
                            BufferedReader reader, JSObject DOMNode) {
  }

  /**
   * 
   * @param namespaceURI
   * @param localName
   * @param qName
   * @param atts
   */
  protected void processStartElement(String namespaceURI, String localName, String qName,
                                     Map<String, String> atts) {
    /* 
     * specific to each xml reader
     */
  }

  /*
   *  keepChars is used to signal 
   *  that characters between end tags should be kept
   *  
   */

  protected boolean keepChars;
  protected String chars;
  protected void setKeepChars(boolean TF) {
    keepChars = TF;
    chars = null;
  }

  /**
   * 
   * @param uri
   * @param localName
   * @param qName
   */
  protected void processEndElement(String uri, String localName, String qName) {
    /* 
     * specific to each xml reader
     */
  }

  public static class DummyResolver implements EntityResolver {
    public InputSource resolveEntity(String publicID, String systemID)
        throws SAXException {
      if (Logger.debugging) {
        Logger.debug(
            "Jmol SAX EntityResolver not resolving:" +
            "\n  publicID: " + publicID +
            "\n  systemID: " + systemID
          );
      }
      return new InputSource(new BufferedReader(new StringReader("")));
    }
  }
  
  public class JmolXmlHandler extends DefaultHandler {

    public JmolXmlHandler() {
    }

    public JmolXmlHandler(XMLReader xmlReader) {
      setHandler(xmlReader, this);
    }

    public void setHandler(XMLReader xmlReader, JmolXmlHandler handler) {
      try {
        xmlReader.setFeature("http://xml.org/sax/features/validation", false);
        xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);
        xmlReader.setEntityResolver(handler);
        xmlReader.setContentHandler(handler);
        xmlReader.setErrorHandler(handler);
      } catch (Exception e) {
        Logger.error("ERROR IN XmlReader.JmolXmlHandler.setHandler", e);
      }

    }

    @Override
    public void startDocument() {
    }

    @Override
    public void endDocument() {
    }

    /*
     * see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     * startElement and endElement should be extended in each reader
     */

    public Map<String, String> atts;

    private String context = "";
    @Override
    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes attributes) {
      getAttributes(attributes);
      if (Logger.debugging) {
        context +=  " " + localName;
        Logger.debug(context);
      }
      startElement(namespaceURI, localName, qName);
    }

    private void startElement(String namespaceURI, String localName, String qName) {
      processStartElement(namespaceURI, localName, qName, atts);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
      if (Logger.debugging) {
        Logger.debug("");//"end   " + indent);
        context = context.substring(0, context.lastIndexOf(" "));
      }
      processEndElement(uri, localName, qName);
    }

    // Won't work for DOM? -- equivalent of "innerHTML"

    @Override
    public void characters(char[] ch, int start, int length) {
      if (keepChars) {
        if (chars == null) {
          chars = new String(ch, start, length);
        } else {
          chars += new String(ch, start, length);
        }
      }
    }

    // Methods for entity resolving, e.g. getting a DTD resolved

    public InputSource resolveEntity(String name, String publicId,
                                     String baseURI, String systemId) {
      if (Logger.debugging) {
        Logger.debug(
            "Not resolving this:" +
            "\n      name: " + name +
            "\n  systemID: " + systemId +
            "\n  publicID: " + publicId +
            "\n   baseURI: " + baseURI
          );
      }
      return null;
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) {
      if (Logger.debugging) {
        Logger.debug(
            "Not resolving this:" +
            "\n  publicID: " + publicId +
            "\n  systemID: " + systemId
          );
      }
      return null;
    }

    @Override
    public void error(SAXParseException exception) {
      Logger.error("SAX ERROR:" + exception.getMessage());
    }

    @Override
    public void fatalError(SAXParseException exception) {
      Logger.error("SAX FATAL:" + exception.getMessage());
    }

    @Override
    public void warning(SAXParseException exception) {
      Logger.warn("SAX WARNING:" + exception.getMessage());
    }

    ////////////////////////////////////////////////////////////////

    // walk DOM tree given by JSObject. For every element, call
    // startElement with the appropriate strings etc., and then
    // endElement when the element is closed.

    protected void walkDOMTree(JSObject DOMNode) {
      String namespaceURI = (String) DOMNode.getMember("namespaceURI");
      String localName = (String) DOMNode.getMember("localName");
      String qName = (String) DOMNode.getMember("nodeName");
      JSObject attributes = (JSObject) DOMNode.getMember("attributes");
      getAttributes(attributes);
      startElement(namespaceURI, localName, qName);
      if (((Boolean) DOMNode.call("hasChildNodes", null))
          .booleanValue()) {
        for (JSObject nextNode = (JSObject) DOMNode.getMember("firstChild"); 
               nextNode != null; 
               nextNode = (JSObject) nextNode.getMember("nextSibling"))
          walkDOMTree(nextNode);
      }
      endElement(namespaceURI, localName, qName);
    }

    ////////////////////

    private void getAttributes(Attributes attributes) {
      int nAtts = attributes.getLength();
      atts = new Hashtable<String, String>(nAtts);
      for (int i = nAtts; --i >= 0;)
        atts.put(attributes.getLocalName(i), attributes.getValue(i));
    }

    private void getAttributes(JSObject attributes) {
      if (attributes == null) {
        atts = new Hashtable<String, String>(0);
        return;
      }

      // load up only the implemented attributes

      int nAtts = ((Number) attributes.getMember("length")).intValue();
      atts = new Hashtable<String, String>(nAtts);
      for (int i = implementedAttributes.length; --i >= 0;) {
        Object[] attArgs = { implementedAttributes[i] };
        JSObject attNode = (JSObject) attributes.call("getNamedItem", attArgs);
        if (attNode != null) {
          String attLocalName = (String) attNode.getMember("name");
          String attValue = (String) attNode.getMember("value");
          atts.put(attLocalName, attValue);
        }
      }
    }
  }
}
