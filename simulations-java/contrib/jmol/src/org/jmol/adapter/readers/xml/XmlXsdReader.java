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

import java.io.BufferedReader;
import java.util.BitSet;
import java.util.Map;

import netscape.javascript.JSObject;

import org.jmol.util.TextFormat;
import org.xml.sax.*;

/**
 * An XML reader for Materials Studio .xsd files   http://accelrys.com/products/materials-studio/
 * 
 * Bob Hanson hansonr@stolaf.edu 6/3/09
 * 
 */

public class XmlXsdReader extends XmlReader {

  /*
   * Enter any implemented field names in the 
   * implementedAttributes array. It is for when the XML 
   * is already loaded in the DOM of an XML page.
   * 
   */

  String[] xsdImplementedAttributes = { "ID", //general 
      "XYZ", "Connections", "Components", "IsBackboneAtom",//Atom3d
      "Connects", "Type", //Bond
      "Name", //LinearChain
  };

  private BitSet bsBackbone = new BitSet();
  
  public XmlXsdReader() {
  }

  @Override
  protected void processXml(XmlReader parent,
                           AtomSetCollection atomSetCollection,
                           BufferedReader reader, XMLReader xmlReader) {
    init(parent, atomSetCollection);
    this.reader = reader;
    new XsdHandler(xmlReader);
    parseReaderXML(xmlReader);
    fin();
  }

  @Override
  protected void processXml(XmlReader parent,
                            AtomSetCollection atomSetCollection,
                            BufferedReader reader, JSObject DOMNode) {
    init(parent, atomSetCollection);
    implementedAttributes = xsdImplementedAttributes;
    (new XsdHandler()).walkDOMTree(DOMNode);
    fin();
  }

  private void init(XmlReader parent, AtomSetCollection atomSetCollection) {
    this.parent = parent;
    this.atomSetCollection = atomSetCollection;
    parent.htParams.put("backboneAtoms", bsBackbone);
  }

  private void fin() {
    atomSetCollection.clearSymbolicMap(); 
  }

  private int iChain = -1;
  private int iGroup = 0;
  private int iAtom = 0;
  
  @Override
  public void processStartElement(String namespaceURI, String localName, String qName,
                                  Map<String, String> atts) {
    String[] tokens;
    //System.out.println(namespaceURI + " " + localName + " " + atts);
    //System.out.println("xmlchem3d: start " + localName);
    if ("Molecule".equals(localName)) {
      atomSetCollection.newAtomSet();
      atomSetCollection.setAtomSetName(atts.get("Name"));      
      return;
    }
    
    if ("LinearChain".equals(localName)) {
      iGroup = 0;
      iChain++;
    }

    if ("RepeatUnit".equals(localName)) {
      iGroup++;
    }

    if ("Atom3d".equals(localName)) {
      atom = new Atom();
      atom.elementSymbol = atts.get("Components");
      atom.atomName = atts.get("ID");
      atom.atomSerial = ++iAtom;
      if (iChain >= 0)
        atom.chainID = (char) ((iChain - 1)%26 + 'A');
      atom.group3 = "UNK";
      if (iGroup == 0)
        iGroup = 1;
      atom.sequenceNumber = iGroup;
      String xyz = atts.get("XYZ");
      if (xyz != null) {
        tokens = getTokens(xyz.replace(',',' '));
        atom.set(parseFloat(tokens[0]), parseFloat(tokens[1]), parseFloat(tokens[2]));
      }
      boolean isBackbone = "1".equals(atts.get("IsBackboneAtom"));
      if (isBackbone)
        bsBackbone.set(iAtom);
      return;
    }
    if ("Bond".equals(localName)) {
      String[] atoms = TextFormat.split(atts.get("Connects"), ',');
      int order = 1;
      if (atts.containsKey("Type")) {
        String type = atts.get("Type");
        if (type.equals("Double"))
          order = 2;
        else if (type.equals("Triple"))
          order = 3;
      }
      atomSetCollection.addNewBond(atoms[0], atoms[1], order);
      return;
    }
  }

  @Override
  public void processEndElement(String uri, String localName, String qName) {
    if ("Atom3d".equals(localName)) {
      if (atom.elementSymbol != null && !Float.isNaN(atom.z)) {
        parent.setAtomCoord(atom);
        atomSetCollection.addAtomWithMappedName(atom);
      }
      atom = null;
      return;
    }
    keepChars = false;
    chars = null;
  }

  class XsdHandler extends JmolXmlHandler {

    XsdHandler() {
    }

    XsdHandler(XMLReader xmlReader) {
      setHandler(xmlReader, this);
    }
  }

}
