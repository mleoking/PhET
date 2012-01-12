/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2006-08-27 21:07:49 -0500 (Sun, 27 Aug 2006) $
 * $Revision: 5420 $
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


import java.io.BufferedReader;
import java.util.Map;

import netscape.javascript.JSObject;

import org.jmol.api.JmolAdapter;
import org.xml.sax.XMLReader;

/**
 * A crude ArgusLab .agl file Reader - http://www.planaria-software.com/
 * 
 * Use this reader as a template for adding new XML readers.
 * 
 */

public class XmlArgusReader extends XmlReader {

  /*
   * Enter any implemented field names in the 
   * implementedAttributes array. It is for when the XML 
   * is already loaded in the DOM of an XML page.
   * 
   */

  private String[] argusImplementedAttributes = { 
      "order", //bond
  };

  private String[] keepCharsList = { 
      "name", "x", "y", "z", "formalchg", "atomkey", "atsym", 
      "e00", "e01", "e02", "e03", 
      "e10", "e11", "e12", "e13", 
      "e20", "e21", "e22", "e23", 
      "e30", "e31", "e32", "e33"
  };

  private String atomName1;
  private String atomName2;
  private int bondOrder;

  // this param is used to keep track of the parent element type
  private int elementContext;
  private final static int UNSET = 0;
  private final static int MOLECULE = 1;
  private final static int ATOM = 2;
  private final static int BOND = 3;
  private final static int TRANSFORMMAT = 4;

  private float[] trans;
  private int ptTrans;
  
  XmlArgusReader() {
  }

  @Override
  protected void processXml(XmlReader parent,
                           AtomSetCollection atomSetCollection,
                           BufferedReader reader, XMLReader xmlReader) {
    this.parent = parent;
    this.reader = reader;
    this.atomSetCollection = atomSetCollection;
    new ArgusHandler(xmlReader);
    parseReaderXML(xmlReader);
  }

  @Override
  protected void processXml(XmlReader parent,
                            AtomSetCollection atomSetCollection,
                            BufferedReader reader, JSObject DOMNode) {
    this.parent = parent;
    this.atomSetCollection = atomSetCollection;
    implementedAttributes = argusImplementedAttributes;
    (new ArgusHandler()).walkDOMTree(DOMNode);
  }

  @Override
  public void processStartElement(String namespaceURI, String localName, String qName,
                                  Map<String, String> atts) {
    //System.out.println("open " + localName);
    for (int i = keepCharsList.length; --i >= 0;)
      if (keepCharsList[i].equals(localName)) {
        setKeepChars(true);
        break;
      }

    if ("molecule".equals(localName)) {
      atomSetCollection.newAtomSet();
      return;
    }
    if ("atom".equals(localName)) {
      elementContext = ATOM;
      atom = new Atom();
      return;
    }
    if ("bond".equals(localName)) {
      elementContext = BOND;
      atomName1 = null;
      atomName2 = null;
      bondOrder = parseBondToken(atts.get("order"));
      return;
    }    
    if ("transformmat".equals(localName)) {
      elementContext = TRANSFORMMAT;
      trans = new float[16];
      return;
    }
  }

  int parseBondToken(String str) {
    float floatOrder = parseFloat(str);
    if (Float.isNaN(floatOrder) && str.length() >= 1) {
      str = str.toUpperCase();
      switch (str.charAt(0)) {
      case 'S':
        return 1;
      case 'D':
        return 2;
      case 'T':
        return 3;
      case 'A':
        return JmolAdapter.ORDER_AROMATIC;
      }
      return parseInt(str);
    }
    if (floatOrder == 1.5)
      return JmolAdapter.ORDER_AROMATIC;
    if (floatOrder == 2)
      return 2;
    if (floatOrder == 3)
      return 3;
    return 1;
  }

  @Override
  public void processEndElement(String uri, String localName, String qName) {
    //System.out.println("close " + localName);
    if (chars != null && chars.length() > 0
        && chars.charAt(chars.length() - 1) == '\n')
      chars = chars.substring(0, chars.length() - 1);
    if ("molecule".equals(localName)) {
      elementContext = UNSET;
      return;
    }
    if ("atom".equals(localName)) {
      if (atom.elementSymbol != null && !Float.isNaN(atom.z)) {
        parent.setAtomCoord(atom);
        atomSetCollection.addAtomWithMappedName(atom);
      }
      atom = null;
      elementContext = UNSET;
      return;
    }
    if ("bond".equals(localName)) {
      if (atomName2 != null)
        atomSetCollection.addNewBond(atomName1, atomName2, bondOrder);
      elementContext = UNSET;
      return;
    }

    if ("transformmat".equals(localName)) {
      elementContext = UNSET;
      parent.setTransform(
          trans[0],trans[1],trans[2],
          trans[4],trans[5],trans[6],
          trans[8],trans[9],trans[10]);
      return;
    }

    // contextual assignments 

    if (elementContext == MOLECULE) {
      if ("name".equals(localName)) {
        atomSetCollection.setAtomSetName(chars);
        setKeepChars(false);
      }
      return;
    }
    if (atom != null && elementContext == ATOM) {
      if ("x".equals(localName)) {
        atom.x = parseFloat(chars);
      } else if ("y".equals(localName)) {
        atom.y = parseFloat(chars);
        return;
      } else if ("z".equals(localName)) {
        atom.z = parseFloat(chars);
        return;
      } else if ("atsym".equals(localName)) {
        atom.elementSymbol = chars;
        return;
      } else if ("formalchg".equals(localName)) {
        atom.formalCharge = parseInt(chars);
      } else if ("atomkey".equals(localName)) {
        atom.atomName = chars;
      }
      setKeepChars(false);
      return;
    }
    if (elementContext == BOND) {
      if ("atomkey".equals(localName)) {
        if (atomName1 == null)
          atomName1 = chars;
        else
          atomName2 = chars;
        setKeepChars(false);
      }
      return;
    }
    
    if (elementContext == TRANSFORMMAT) {
      trans[ptTrans++] = parseFloat(chars);
      setKeepChars(false);
      return;
    }
  }

  class ArgusHandler extends JmolXmlHandler {

    ArgusHandler() {
    }

    ArgusHandler(XMLReader xmlReader) {
      setHandler(xmlReader, this);
    }
  }
}
