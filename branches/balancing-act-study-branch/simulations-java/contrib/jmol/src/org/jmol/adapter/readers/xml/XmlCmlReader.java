/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2006-09-16 09:53:18 -0500 (Sat, 16 Sep 2006) $
 * $Revision: 5561 $
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
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.StringTokenizer;

import netscape.javascript.JSObject;

import org.jmol.api.JmolAdapter;
import org.xml.sax.XMLReader;
import org.jmol.adapter.readers.cifpdb.CifReader;
import org.jmol.util.Logger;
import org.jmol.util.Parser;

/**
 * A CML2 Reader - 
 * If passed a bufferedReader (from a file or inline string), we
 * generate a SAX parser and use callbacks to construct an
 * AtomSetCollection.
 * If passed a JSObject (from LiveConnect) we treat it as a JS DOM
 * tree, and walk the tree, (using the same processing as the SAX
 * parser) to construct the AtomSetCollection.
 * 
 * symmetry added by Bob Hanson:
 * 
 *  setSpaceGroupName()
 *  setUnitCellItem()
 *  setFractionalCoordinates()
 *  setAtomCoord()
 *  applySymmetryAndSetTrajectory()
 *
 *
 * "isotope" added 4/6/2009 Bob Hanson
 * 
 */

/* TODO 9/06
 * 
 *  
 *  We need to implement the capability to load a specific
 *  model as well as indicate the number of unit cells to load. 
 *  
 * Follow the equivalent in CIF files to see how this is done. 
 * 
 */

public class XmlCmlReader extends XmlReader {


  /*
   * Enter any implemented field names in the 
   * implementedAttributes array. It is for when the XML 
   * is already loaded in the DOM of an XML page.
   * 
   */

  private String[] cmlImplementedAttributes = { "id", //general
      "title", //molecule, atom, scalar
      "label", "name", //old atom
      "x3", "y3", "z3", "x2", "y2", "isotope", //atom 
      "elementType", "formalCharge", //atom
      "atomId", //atomArray
      "atomRefs2", "order", //bond
      "atomRef1", "atomRef2", //bondArray
      "dictRef", //scalar
      "spaceGroup", //symmetry
  };

  ////////////////////////////////////////////////////////////////
  // Main body of class; variablaes & functiopns shared by DOM & SAX alike.

  // the same atom array gets reused
  // it will grow to the maximum length;
  // atomCount holds the current number of atoms
  private int atomCount;
  private Atom[] atomArray = new Atom[100];

  private int bondCount;
  private Bond[] bondArray = new Bond[100];

  // the same string array gets reused
  // tokenCount holds the current number of tokens
  // see breakOutTokens
  private int tokenCount;
  String[] tokens = new String[16];

  private int nModules = 0;
  private int moduleNestingLevel = 0;
  private boolean haveMolecule = false;
  private String localSpaceGroupName;
  private boolean processing = true;
  /**
   * state constants
   */
  final protected int START = 0, 
    CML = 1, 
    CRYSTAL = 2, 
    CRYSTAL_SCALAR = 3,
    CRYSTAL_SYMMETRY = 4, 
    CRYSTAL_SYMMETRY_TRANSFORM3 = 5, 
    MOLECULE = 6,
    MOLECULE_ATOM_ARRAY = 7, 
    MOLECULE_ATOM = 8, 
    MOLECULE_ATOM_SCALAR = 9,
    MOLECULE_BOND_ARRAY = 10, 
    MOLECULE_BOND = 11, 
    MOLECULE_FORMULA = 12,
    MOLECULE_ATOM_BUILTIN = 13, 
    MOLECULE_BOND_BUILTIN = 14,
    MODULE = 15,
    SYMMETRY = 17,
    LATTICE_VECTOR = 18;

  /**
   * the current state
   */
  protected int state = START;

  /*
   * added 2/2007  Bob Hanson:
   * 
   * <crystal id="struct" dictRef="castep:ucell">
      <cellParameter latticeType="real" parameterType="length"
        units="castepunits:a">4.592100143433e0 4.592100143433e0 2.957400083542e0</cellParameter>
      <cellParameter latticeType="real" parameterType="angle"
        units="castepunits:degree">9.000000000000e1 9.000000000000e1 9.000000000000e1</cellParameter>
    </crystal>

   * 
   */

  XmlCmlReader() {
  }

  @Override
  protected void processXml(XmlReader parent,
                           AtomSetCollection atomSetCollection,
                           BufferedReader reader, XMLReader xmlReader) {
    this.parent = parent;
    this.reader = reader;
    this.atomSetCollection = atomSetCollection;
    new CmlHandler(xmlReader);
    parseReaderXML(xmlReader);
  }

  @Override
  protected void processXml(XmlReader parent,
                            AtomSetCollection atomSetCollection,
                            BufferedReader reader, JSObject DOMNode) {
    this.parent = parent;
    this.atomSetCollection = atomSetCollection;
    implementedAttributes = cmlImplementedAttributes;
    (new CmlHandler()).walkDOMTree(DOMNode);
  }

  private String scalarDictRef;
  //String scalarDictKey;
  private String scalarDictValue;
  private String scalarTitle;
  private String cellParameterType;
  private boolean checkedSerial;
  private boolean isSerial;

  // counter that is incremented each time a molecule element is started and 
  // decremented when finished.  Needed so that only 1 atomSet created for each
  // parent molecule that exists.
  private int moleculeNesting = 0;
  private int latticeVectorPtr = 0;
  private boolean embeddedCrystal = false;
  private Properties atomIdNames;

  @Override
  public void processStartElement(String uri, String name, String qName,
                                  Map<String, String> atts) {
    // if (!uri.equals(NAMESPACE_URI))
    // return;

    /*
     * try { System.out.println(name + "::" + atts.get("name")); } catch
     * (Exception e) { System.out.println(name); }
     */
    if (!processing)
      return;
    switch (state) {
    case START:
      if (name.equals("molecule")) {
        state = MOLECULE;
        haveMolecule = true;
        if (moleculeNesting == 0) {
          createNewAtomSet(atts);
        }
        moleculeNesting++;
      } else if (name.equals("crystal")) {
        state = CRYSTAL;
      } else if (name.equals("symmetry")) {
        state = SYMMETRY;
        if (atts.containsKey("spaceGroup")) {
          localSpaceGroupName = atts.get("spaceGroup");
        } else {
          localSpaceGroupName = "P1";
          parent.clearUnitCell();
        }
      } else if (name.equals("module")) {
        moduleNestingLevel++;
        nModules++;
      } else if (name.equals("latticeVector")) {
        state = LATTICE_VECTOR;
        setKeepChars(true);
      }

      break;
    case CRYSTAL:
      if (name.equals("scalar")) {
        state = CRYSTAL_SCALAR;
        setKeepChars(true);
        scalarTitle = atts.get("title");
        getDictRefValue(atts);
      } else if (name.equals("symmetry")) {
        state = CRYSTAL_SYMMETRY;
        if (atts.containsKey("spaceGroup")) {
          localSpaceGroupName = atts.get("spaceGroup");
          for (int i = 0; i < localSpaceGroupName.length(); i++)
            if (localSpaceGroupName.charAt(i) == '_')
              localSpaceGroupName = localSpaceGroupName.substring(0, i)
                  + localSpaceGroupName.substring((i--) + 1);
        }
      } else if (name.equals("cellParameter")) {
        if (atts.containsKey("parameterType")) {
          cellParameterType = atts.get("parameterType");
          setKeepChars(true);
        }
      }
      break;
    case LATTICE_VECTOR:
      /*
       * <lattice dictRef="castep:latticeVectors"> <latticeVector
       * units="castepunits:A" dictRef="cml:latticeVector">1.980499982834e0
       * 3.430000066757e0 0.000000000000e0</latticeVector> <latticeVector
       * units="castepunits:A" dictRef="cml:latticeVector">-1.980499982834e0
       * 3.430000066757e0 0.000000000000e0</latticeVector> <latticeVector
       * units="castepunits:A" dictRef="cml:latticeVector">0.000000000000e0
       * 0.000000000000e0 4.165999889374e0</latticeVector> </lattice>
       */
      setKeepChars(true);
      break;
    case SYMMETRY:
    case CRYSTAL_SCALAR:
    case CRYSTAL_SYMMETRY:
      if (name.equals("transform3")) {
        state = CRYSTAL_SYMMETRY_TRANSFORM3;
        setKeepChars(true);
      }
      break;
    case CRYSTAL_SYMMETRY_TRANSFORM3:
    case MOLECULE:
      if (name.equals("crystal")) {
        state = CRYSTAL;
        embeddedCrystal = true;
      }
      if (name.equals("molecule")) {
        state = MOLECULE;
        moleculeNesting++;
      }
      if (name.equals("bondArray")) {
        state = MOLECULE_BOND_ARRAY;
        bondCount = 0;
        if (atts.containsKey("order")) {
          breakOutBondTokens(atts.get("order"));
          for (int i = tokenCount; --i >= 0;)
            bondArray[i].order = parseBondToken(tokens[i]);
        }
        if (atts.containsKey("atomRef1")) {
          breakOutBondTokens(atts.get("atomRef1"));
          for (int i = tokenCount; --i >= 0;)
            bondArray[i].atomIndex1 = atomSetCollection
                .getAtomNameIndex(tokens[i]);
        }
        if (atts.containsKey("atomRef2")) {
          breakOutBondTokens(atts.get("atomRef2"));
          for (int i = tokenCount; --i >= 0;)
            bondArray[i].atomIndex2 = atomSetCollection
                .getAtomNameIndex(tokens[i]);
        }
      }
      if (name.equals("atomArray")) {
        state = MOLECULE_ATOM_ARRAY;
        atomCount = 0;
        boolean coords3D = false;
        if (atts.containsKey("atomID")) {
          breakOutAtomTokens(atts.get("atomID"));
          for (int i = tokenCount; --i >= 0;)
            atomArray[i].atomName = tokens[i];
        }
        if (atts.containsKey("x3")) {
          coords3D = true;
          breakOutAtomTokens(atts.get("x3"));
          for (int i = tokenCount; --i >= 0;)
            atomArray[i].x = parseFloat(tokens[i]);
        }
        if (atts.containsKey("y3")) {
          breakOutAtomTokens(atts.get("y3"));
          for (int i = tokenCount; --i >= 0;)
            atomArray[i].y = parseFloat(tokens[i]);
        }
        if (atts.containsKey("z3")) {
          breakOutAtomTokens(atts.get("z3"));
          for (int i = tokenCount; --i >= 0;)
            atomArray[i].z = parseFloat(tokens[i]);
        }
        if (atts.containsKey("x2")) {
          breakOutAtomTokens(atts.get("x2"));
          for (int i = tokenCount; --i >= 0;)
            atomArray[i].x = parseFloat(tokens[i]);
        }
        if (atts.containsKey("y2")) {
          breakOutAtomTokens(atts.get("y2"));
          for (int i = tokenCount; --i >= 0;)
            atomArray[i].y = parseFloat(tokens[i]);
        }
        if (atts.containsKey("elementType")) {
          breakOutAtomTokens(atts.get("elementType"));
          for (int i = tokenCount; --i >= 0;)
            atomArray[i].elementSymbol = tokens[i];
        }
        for (int i = atomCount; --i >= 0;) {
          Atom atom = atomArray[i];
          if (!coords3D)
            atom.z = 0;
          addAtom(atom);
        }
      }
      if (name.equals("formula")) {
        state = MOLECULE_FORMULA;
      }
      break;
    case MOLECULE_BOND_ARRAY:
      if (name.equals("bond")) {
        state = MOLECULE_BOND;
        int order = -1;
        tokenCount = 0;
        if (atts.containsKey("atomRefs2"))
          breakOutTokens(atts.get("atomRefs2"));
        if (atts.containsKey("order"))
          order = parseBondToken(atts.get("order"));
        if (tokenCount == 2 && order > 0) {
          addNewBond(tokens[0], tokens[1], order);
        }
      }
      break;
    case MOLECULE_ATOM_ARRAY:
      if (name.equals("atom")) {
        state = MOLECULE_ATOM;
        atom = new Atom();
        parent.setFractionalCoordinates(false);
        String id = atts.get("id");
        if (atts.containsKey("name"))
          atom.atomName = atts.get("name");
        else if (atts.containsKey("title"))
          atom.atomName = atts.get("title");
        else if (atts.containsKey("label"))
          atom.atomName = atts.get("label");
        else
          atom.atomName = id;
        if (!checkedSerial) {
          // this is important because the atomName may not be unique
          // (as in PDB files)
          isSerial = (id != null && id.length() > 1 && id.startsWith("a")
              && Parser.parseInt(id.substring(1)) != Integer.MIN_VALUE);
          checkedSerial = true;
        } 
        if (isSerial)
          atom.atomSerial = Parser.parseInt(id.substring(1));
        if (atts.containsKey("xFract")
            && (parent.iHaveUnitCell || !atts.containsKey("x3"))) {
          parent.setFractionalCoordinates(true);
          atom.set(parseFloat(atts.get("xFract")),
              parseFloat(atts.get("yFract")), parseFloat(atts.get("zFract")));
        } else if (atts.containsKey("x3")) {
          atom.set(parseFloat(atts.get("x3")),
              parseFloat(atts.get("y3")), parseFloat(atts.get("z3")));
        } else if (atts.containsKey("x2")) {
          atom.set(parseFloat(atts.get("x2")),
              parseFloat(atts.get("y2")), 0);
        }
        if (atts.containsKey("elementType")) {
          String sym = atts.get("elementType");
          if (atts.containsKey("isotope"))
            atom.elementNumber = (short) ((parseInt(atts.get("isotope")) << 7) + JmolAdapter.getElementNumber(sym));
          atom.elementSymbol = sym;
        }
        if (atts.containsKey("formalCharge"))
          atom.formalCharge = parseInt(atts.get("formalCharge"));
      }

      break;
    case MOLECULE_BOND:
      if (atts.containsKey("builtin")) {
        setKeepChars(true);
        state = MOLECULE_BOND_BUILTIN;
        scalarDictValue = atts.get("builtin");
      }
      break;
    case MOLECULE_ATOM:
      if (name.equals("scalar")) {
        state = MOLECULE_ATOM_SCALAR;
        setKeepChars(true);
        scalarTitle = atts.get("title");
        getDictRefValue(atts);
      } else if (atts.containsKey("builtin")) {
        setKeepChars(true);
        state = MOLECULE_ATOM_BUILTIN;
        scalarDictValue = atts.get("builtin");
      }
      break;
    case MOLECULE_ATOM_SCALAR:
      break;
    case MOLECULE_FORMULA:
      break;
    case MOLECULE_ATOM_BUILTIN:
      break;
    case MOLECULE_BOND_BUILTIN:
      break;
    }
  }

  private void addNewBond(String a1, String a2, int order) {
    parent.applySymmetryToBonds = true;
    if (isSerial)
      atomSetCollection.addNewBondWithMappedSerialNumbers(Parser.parseInt(a1.substring(1)),
          Parser.parseInt(a2.substring(1)), order);
      else
        atomSetCollection.addNewBond(a1, a2, order);
  }

  private void getDictRefValue(Map<String, String> atts) {
    scalarDictRef = atts.get("dictRef");
    if (scalarDictRef != null) {
      int iColon = scalarDictRef.indexOf(":");
      scalarDictValue = scalarDictRef.substring(iColon + 1);
    }
  }

  @Override
  public void processEndElement(String uri, String name, String qName) {
    // if (!uri.equals(NAMESPACE_URI))
    // return;
    // System.out.println("END: " + name);
    if (!processing)
      return;
    switch (state) {
    case START:
      if (name.equals("module")) {
        if (--moduleNestingLevel == 0) {
          if (parent.iHaveUnitCell)
            applySymmetryAndSetTrajectory();
          atomIdNames = atomSetCollection.setAtomNames(atomIdNames);
        }
      }
      break;
    case CRYSTAL:
      if (name.equals("crystal")) {
        if (embeddedCrystal) {
          state = MOLECULE;
          embeddedCrystal = false;
        } else {
          state = START;
        }
      } else if (name.equals("cellParameter") && keepChars) {
        String[] tokens = getTokens(chars);
        setKeepChars(false);
        if (tokens.length != 3 || cellParameterType == null) {
        } else if (cellParameterType.equals("length")) {
          for (int i = 0; i < 3; i++)
            parent.setUnitCellItem(i, parseFloat(tokens[i]));
          break;
        } else if (cellParameterType.equals("angle")) {
          for (int i = 0; i < 3; i++)
            parent.setUnitCellItem(i + 3, parseFloat(tokens[i]));
          break;
        }
        // if here, then something is wrong
        Logger.error("bad cellParameter information: parameterType="
            + cellParameterType + " data=" + chars);
        parent.setFractionalCoordinates(false);
      }
      break;
    case CRYSTAL_SCALAR:
      if (name.equals("scalar")) {
        state = CRYSTAL;
        if (scalarTitle != null)
          checkUnitCellItem(AtomSetCollection.notionalUnitcellTags, scalarTitle);
        else if (scalarDictRef != null)
          checkUnitCellItem(CifReader.cellParamNames, (scalarDictValue
              .startsWith("_") ? scalarDictValue : "_" + scalarDictValue));
      }
      setKeepChars(false);
      scalarTitle = null;
      scalarDictRef = null;
      break;
    case CRYSTAL_SYMMETRY_TRANSFORM3:
      if (name.equals("transform3")) {
        // setSymmetryOperator("xyz matrix: " + chars);
        // the problem is that these matricies are in CARTESIAN coordinates, not
        // ijk coordinates
        setKeepChars(false);
        state = CRYSTAL_SYMMETRY;
      }
      break;
    case LATTICE_VECTOR:
      float[] values = new float[3];
      getTokensFloat(chars, values, 3);
      parent.addPrimitiveLatticeVector(latticeVectorPtr, values, 0);
      latticeVectorPtr = (latticeVectorPtr + 1) % 3;
      setKeepChars(false);
      state = START;
      break;
    case CRYSTAL_SYMMETRY:
    case SYMMETRY:
      if (name.equals("symmetry"))
        state = (state == CRYSTAL_SYMMETRY ? CRYSTAL : START);
      if (moduleNestingLevel == 0 && parent.iHaveUnitCell && !embeddedCrystal)
        applySymmetryAndSetTrajectory();
      break;
    case MOLECULE:
      if (name.equals("molecule")) {
        if (--moleculeNesting == 0) {
          // if <molecule> is within <molecule>, then
          // we have to wait until the end of all <molecule>s to
          // apply symmetry.
          applySymmetryAndSetTrajectory();
          atomIdNames = atomSetCollection.setAtomNames(atomIdNames);
          state = START;
        } else {
          state = MOLECULE;
        }
      }
      break;
    case MOLECULE_BOND_ARRAY:
      if (name.equals("bondArray")) {
        state = MOLECULE;
        for (int i = 0; i < bondCount; ++i)
          atomSetCollection.addBond(bondArray[i]);
        parent.applySymmetryToBonds = true;
      }
      break;
    case MOLECULE_ATOM_ARRAY:
      if (name.equals("atomArray")) {
        state = MOLECULE;
        for (int i = 0; i < atomCount; ++i)
          addAtom(atomArray[i]);
      }
      break;
    case MOLECULE_BOND:
      if (name.equals("bond")) {
        state = MOLECULE_BOND_ARRAY;
      }
      break;
    case MOLECULE_ATOM:
      if (name.equals("atom")) {
        state = MOLECULE_ATOM_ARRAY;
        addAtom(atom);
        atom = null;
      }
      break;
    case MOLECULE_ATOM_SCALAR:
      if (name.equals("scalar")) {
        state = MOLECULE_ATOM;
        if ("jmol:charge".equals(scalarDictRef)) {
          atom.partialCharge = parseFloat(chars);
        } else if (scalarDictRef != null
            && "_atom_site_label".equals(scalarDictValue)) {
          if (atomIdNames == null)
            atomIdNames = new Properties();
          atomIdNames.put(atom.atomName, chars);
        }
      }
      setKeepChars(false);
      scalarTitle = null;
      scalarDictRef = null;
      break;
    case MOLECULE_ATOM_BUILTIN:
      state = MOLECULE_ATOM;
      if (scalarDictValue.equals("x3"))
        atom.x = parseFloat(chars);
      else if (scalarDictValue.equals("y3"))
        atom.y = parseFloat(chars);
      else if (scalarDictValue.equals("z3"))
        atom.z = parseFloat(chars);
      else if (scalarDictValue.equals("elementType"))
        atom.elementSymbol = chars;
      setKeepChars(false);
      break;
    case MOLECULE_BOND_BUILTIN: // ACD Labs
      state = MOLECULE_BOND;
      if (scalarDictValue.equals("atomRef")) {
        if (tokenCount == 0)
          tokens = new String[2];
        if (tokenCount < 2)
          tokens[tokenCount++] = chars;
      } else if (scalarDictValue.equals("order")) {
        int order = parseBondToken(chars);
        if (order > 0 && tokenCount == 2)
          addNewBond(tokens[0], tokens[1], order);
      }
      setKeepChars(false);
      break;
    case MOLECULE_FORMULA:
      state = MOLECULE;
      break;
    }
  }

  private void checkUnitCellItem(String[] tags, String value) {
    for (int i = tags.length; --i >= 0;)
      if (value.equals(tags[i])) {
        parent.setUnitCellItem(i, parseFloat(chars));
        return;
      }
  }

  private void addAtom(Atom atom) {
    if ((atom.elementSymbol == null && atom.elementNumber < 0)
        || Float.isNaN(atom.z))
      return;
    parent.setAtomCoord(atom);
    if (isSerial)
      atomSetCollection.addAtomWithMappedSerialNumber(atom);
    else
      atomSetCollection.addAtomWithMappedName(atom);
  }

  int parseBondToken(String str) {
    float floatOrder = parseFloat(str);
    if (Float.isNaN(floatOrder) && str.length() >= 1) {
      str = str.toUpperCase();
      switch (str.charAt(0)) {
      case 'S':
        return JmolAdapter.ORDER_COVALENT_SINGLE;
      case 'D':
        return JmolAdapter.ORDER_COVALENT_DOUBLE;
      case 'T':
        return JmolAdapter.ORDER_COVALENT_TRIPLE;
      case 'A':
        return JmolAdapter.ORDER_AROMATIC;
      case 'P':
        //TODO: Note, this could be elaborated more specifically
        return JmolAdapter.ORDER_PARTIAL12;
      }
      return parseInt(str);
    }
    if (floatOrder == 1.5)
      return JmolAdapter.ORDER_AROMATIC;
    if (floatOrder == 2)
      return JmolAdapter.ORDER_COVALENT_DOUBLE;
    if (floatOrder == 3)
      return JmolAdapter.ORDER_COVALENT_TRIPLE;
    return JmolAdapter.ORDER_COVALENT_SINGLE;
  }

  //this routine breaks out all the tokens in a string
  // results ar e placed into the tokens array
  void breakOutTokens(String str) {
    StringTokenizer st = new StringTokenizer(str);
    tokenCount = st.countTokens();
    if (tokenCount > tokens.length)
      tokens = new String[tokenCount];
    for (int i = 0; i < tokenCount; ++i) {
      try {
        tokens[i] = st.nextToken();
      } catch (NoSuchElementException nsee) {
        tokens[i] = null;
      }
    }
  }

  void breakOutAtomTokens(String str) {
    breakOutTokens(str);
    checkAtomArrayLength(tokenCount);
  }

  void checkAtomArrayLength(int newAtomCount) {
    if (atomCount == 0) {
      if (newAtomCount > atomArray.length)
        atomArray = new Atom[newAtomCount];
      for (int i = newAtomCount; --i >= 0;)
        atomArray[i] = new Atom();
      atomCount = newAtomCount;
    } else if (newAtomCount != atomCount) {
      throw new IndexOutOfBoundsException("bad atom attribute length");
    }
  }

  void breakOutBondTokens(String str) {
    breakOutTokens(str);
    checkBondArrayLength(tokenCount);
  }

  void checkBondArrayLength(int newBondCount) {
    if (bondCount == 0) {
      if (newBondCount > bondArray.length)
        bondArray = new Bond[newBondCount];
      for (int i = newBondCount; --i >= 0;)
        bondArray[i] = new Bond();
      bondCount = newBondCount;
    } else if (newBondCount != bondCount) {
      throw new IndexOutOfBoundsException("bad bond attribute length");
    }
  }

  private void createNewAtomSet(Map<String, String> atts) {
    atomSetCollection.newAtomSet();
    String collectionName = null;
    if (atts.containsKey("title"))
      collectionName = atts.get("title");
    else if (atts.containsKey("id"))
      collectionName = atts.get("id");
    if (collectionName != null) {
      atomSetCollection.setAtomSetName(collectionName);
    }
  }
  
  @Override
  public void applySymmetryAndSetTrajectory() {
    if (moduleNestingLevel > 0 || !haveMolecule)
      return;
    if (localSpaceGroupName == null)
      return;
    parent.setSpaceGroupName(localSpaceGroupName);
    parent.iHaveSymmetryOperators = iHaveSymmetryOperators;
    try {
      parent.applySymmetryAndSetTrajectory();
    } catch (Exception e) {
      e.printStackTrace();
      Logger.error("applySymmetry failed: " + e);
    }
  }

  class CmlHandler extends JmolXmlHandler {

    CmlHandler() {
    }

    CmlHandler(XMLReader xmlReader) {
      setHandler(xmlReader, this);
    }

  }

}
