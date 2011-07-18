/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2006-10-22 14:12:46 -0500 (Sun, 22 Oct 2006) $
 * $Revision: 5999 $
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

import java.io.BufferedReader;
import java.util.Map;

import javax.vecmath.Vector3f;

import netscape.javascript.JSObject;

import org.jmol.adapter.smarter.*;
import org.jmol.util.Logger;
import org.jmol.util.Parser;
import org.xml.sax.XMLReader;

/**
 * 
 * Vasp vasprun.xml reader
 * 
 * @author hansonr
 * 
 */

public class XmlVaspReader extends XmlReader {
  
  /*
   * Enter any implemented field names in the 
   * implementedAttributes array. It is for when the XML 
   * is already loaded in the DOM of an XML page.
   * 
   */

  String[] vaspImplementedAttributes = { "name" };

  XmlVaspReader() {
  }
  
  @Override
  protected void processXml(XmlReader parent,
                           AtomSetCollection atomSetCollection,
                           BufferedReader reader, XMLReader xmlReader) {
    this.parent = parent;
    parent.doProcessLines = true;
    this.reader = reader;
    this.atomSetCollection = atomSetCollection;
    new VaspHandler(xmlReader);
    parseReaderXML(xmlReader);
  }

  @Override
  protected void processXml(XmlReader parent,
                            AtomSetCollection atomSetCollection,
                            BufferedReader reader, JSObject DOMNode) {
    this.parent = parent;
    parent.doProcessLines = true;
    this.atomSetCollection = atomSetCollection;
    implementedAttributes = vaspImplementedAttributes;
    
    (new VaspHandler()).walkDOMTree(DOMNode);
  }

  StringBuffer data;
  String name;
  int atomCount;
  int iAtom;
  boolean modelRead = false;
  boolean readThisModel = true;
  boolean isE_wo_entrp = false;
  boolean isE_fr_energy = false;
  String enthalpy = null;
  String gibbsEnergy = null;
  @Override
  public void processStartElement(String namespaceURI, String localName,
                                  String qName, Map<String, String> atts) {
    if (Logger.debugging) 
      Logger.debug("xmlvasp: start " + localName);

    if (!parent.continuing)
      return;

    if ("calculation".equals(localName)) {
      enthalpy = null;
      gibbsEnergy = null;
      return;
    }
        
    if ("i".equals(localName)) {
      String s = atts.get("name");
      if (s.charAt(0) != 'e')
        return;
      isE_wo_entrp = s.equals("e_wo_entrp");
      isE_fr_energy = s.equals("e_fr_energy");
      keepChars = (isE_wo_entrp || isE_fr_energy);
      return;
    }

    if ("structure".equals(localName)) {
      if (!parent.doGetModel(++parent.modelNumber)) {
        parent.checkLastModel();
        return;
      }
      parent.setFractionalCoordinates(true);
      atomSetCollection.setDoFixPeriodic();
      atomSetCollection.newAtomSet();
      if (enthalpy != null) {
        atomSetCollection.setAtomSetAuxiliaryInfo("enthalpy", Double.valueOf(enthalpy));
      }
      if (gibbsEnergy != null) {
        atomSetCollection.setAtomSetEnergy("" + gibbsEnergy, parseFloat(gibbsEnergy));
        atomSetCollection.setAtomSetAuxiliaryInfo("gibbsEnergy", Double.valueOf(gibbsEnergy));
      }
      if (enthalpy != null && gibbsEnergy != null)
        atomSetCollection.setAtomSetName("Enthalpy = " + enthalpy + " eV Gibbs Energy = " + gibbsEnergy + " eV");
      return;
    }
    if (!parent.doProcessLines)
      return;
    
    if ("v".equals(localName)) {
      keepChars = (data != null);
      return;
    }

    if ("c".equals(localName)) {
      keepChars = (iAtom < atomCount);
      return;
    }

    if ("varray".equals(localName)) {
      name = atts.get("name");
      if (name != null && Parser.isOneOf(name, "basis;positions;forces"))
        data = new StringBuffer();
      return;
    }

    if ("atoms".equals(localName)) {
      keepChars = true;
      return;
    }
    
  }

  boolean haveUnitCell = false;
  String[] atomNames;
  String[] atomSyms;
  String atomName;
  String atomSym;
  float a;
  float b;
  float c;
  float alpha;
  float beta;
  float gamma;
  
  @Override
  public void processEndElement(String uri, String localName, String qName) {

    if (Logger.debugging) 
      Logger.debug("xmlvasp: end " + localName);

    while (true) {

      if (!parent.doProcessLines)
        break;

      if (isE_wo_entrp) {
        isE_wo_entrp = false;
        enthalpy = chars.trim();
        break;
      }

      if (isE_fr_energy) {
        isE_fr_energy = false;
        gibbsEnergy = chars.trim();
        break;
      }

      if ("v".equals(localName) && data != null) {
        data.append(chars);
        break;
      }

      if ("c".equals(localName)) {
        if (iAtom < atomCount) {
          if (atomName == null) {
            atomName = atomSym = chars.trim();
          } else {
            atomNames[iAtom++] = atomName + chars.trim();
            atomName = null;
          }
        }
        break;
      }

      if ("atoms".equals(localName)) {
        atomCount = parseInt(chars);
        atomNames = new String[atomCount];
        atomSyms = new String[atomCount];
        iAtom = 0;
        break;
      }

      if ("varray".equals(localName) && data != null) {
        if (name == null) {
        } else if ("basis".equals(name) && !haveUnitCell) {
          haveUnitCell = true;
          float[] ijk = new float[9];
          getTokensFloat(data.toString(), ijk, 9);
          Vector3f va = new Vector3f(ijk[0], ijk[1], ijk[2]);
          Vector3f vb = new Vector3f(ijk[3], ijk[4], ijk[5]);
          Vector3f vc = new Vector3f(ijk[6], ijk[7], ijk[8]);
          a = va.length();
          b = vb.length();
          c = vc.length();
          va.normalize();
          vb.normalize();
          vc.normalize();
          alpha = (float) (Math.acos(vb.dot(vc)) * 180 / Math.PI);
          beta = (float) (Math.acos(va.dot(vc)) * 180 / Math.PI);
          gamma = (float) (Math.acos(va.dot(vb)) * 180 / Math.PI);
        } else if ("positions".equals(name)) {
          parent.setUnitCell(a, b, c, alpha, beta, gamma);
          float[] fdata = new float[atomCount * 3];
          getTokensFloat(data.toString(), fdata, atomCount * 3);
          int fpt = 0;
          for (int i = 0; i < atomCount; i++) {
            Atom atom = atomSetCollection.addNewAtom();
            parent.setAtomCoord(atom, fdata[fpt++], fdata[fpt++], fdata[fpt++]);
            atom.elementSymbol = atomSyms[i];
            atom.atomName = atomNames[i];
          }
        } else if ("forces".equals(name)) {
          float[] fdata = new float[atomCount * 3];
          getTokensFloat(data.toString(), fdata, atomCount * 3);
          int fpt = 0;
          int i0 = atomSetCollection.getLastAtomSetAtomIndex();

          //TODO question here as to whether these need transformation

          for (int i = 0; i < atomCount; i++)
            atomSetCollection.addVibrationVector(i0 + i, fdata[fpt++],
                fdata[fpt++], fdata[fpt++]);
        }
        data = null;
        break;
      }
      if ("structure".equals(localName)) {
        try {
          parent.applySymmetryAndSetTrajectory();
        } catch (Exception e) {
          // TODO
        }
        break;
      }
      
      return;
    }
    chars = null;
    keepChars = false;
  }

  class VaspHandler extends JmolXmlHandler {

    VaspHandler() {
    }

    VaspHandler(XMLReader xmlReader) {
      setHandler(xmlReader, this);
    }
  }

  

}
