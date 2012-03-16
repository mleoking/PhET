/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2006-03-12 21:37:51 -0600 (Sun, 12 Mar 2006) $
 * $Revision: 4586 $
 *
 * Copyright (C) 2002-2005  The Jmol Development Team
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

package org.jmol.shapespecial;

import org.jmol.shape.Shape;
import org.jmol.util.BitSetUtil;

import org.jmol.util.ArrayUtil;
import org.jmol.util.Logger;
import org.jmol.util.TextFormat;
import org.jmol.script.Token;

import org.jmol.api.JmolEdge;
import org.jmol.g3d.*;
import org.jmol.modelset.Atom;
import org.jmol.modelset.Bond;
import org.jmol.modelset.BondIterator;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class Dipoles extends Shape {

  final static short DEFAULT_MAD = 5;
  final static float DEFAULT_OFFSETSIDE = 0.40f;

  int dipoleCount = 0;
  Dipole[] dipoles = new Dipole[4];

  private Dipole currentDipole;
  private Dipole tempDipole;
  private Point3f startCoord = new Point3f();
  private Point3f endCoord = new Point3f();
  private float dipoleValue;
  private boolean isUserValue;
  private boolean isBond;
  private boolean iHaveTwoEnds;
  private int atomIndex1;
  private int atomIndex2;
  private short colix;
  private Vector3f calculatedDipole;
  private String wildID;
  

  @Override
  public void setProperty(String propertyName, Object value, BitSet bs) {

    if ("init" == propertyName) {
      tempDipole = new Dipole();
      tempDipole.dipoleValue = 1;
      tempDipole.mad = DEFAULT_MAD;
      atomIndex1 = -1;
      tempDipole.modelIndex = -1;
      dipoleValue = 0;
      calculatedDipole = null;
      isUserValue = isBond = iHaveTwoEnds = false;
      if (currentDipole != null)
        Logger.debug("current dipole: " + currentDipole.thisID);
      return;
    }

    if ("calculate" == propertyName) {
      calculatedDipole = viewer.calculateMolecularDipole();
      Logger.info("calculated molecular dipole = " + calculatedDipole + " "
          + (calculatedDipole == null ? "" : "" + calculatedDipole.length()));
      return;
    }

    if ("thisID" == propertyName) {
      wildID = null;
      String thisID = (String) value;
      if (thisID == null || TextFormat.isWild(thisID)) {
        currentDipole = null;
        if (thisID != null)
          wildID = thisID.toUpperCase();
        return;
      }
      currentDipole = findDipole(thisID);
      if (currentDipole == null)
        currentDipole = allocDipole(thisID, "");
      Logger.debug("current dipole now " + currentDipole.thisID);
      tempDipole = currentDipole;
      if (thisID.equals("molecular")) {
        Vector3f v = calculatedDipole;
        if (v == null) {
          v = viewer.getModelDipole();
          Logger.info("file molecular dipole = " + v + " "
              + (v != null ? "" + v.length() : ""));
        }
        if (v == null)
          calculatedDipole = v = viewer.calculateMolecularDipole();
        if (v == null) {
          Logger
              .warn("No molecular dipole found for this model; setting to {0 0 0}");
          v = new Vector3f();
        }
        tempDipole.set(new Point3f(0, 0, 0), new Vector3f(-v.x, -v.y, -v.z));
        tempDipole.type = Dipole.DIPOLE_TYPE_MOLECULAR;
        tempDipole.thisID = "molecular";
        setDipole();
      }
      return;
    }

    if ("bonds" == propertyName) {
      isBond = true;
      currentDipole = null;
      for (int i = dipoleCount; --i >= 0;)
        if (isBondDipole(i))
          return;
      getBondDipoles(); // only once if any bond dipoles are defined
      return;
    }

    if ("on" == propertyName) {
      setProperty(Token.on, isBond, 0, 0);
      return;
    }

    if ("off" == propertyName) {
      setProperty(Token.off, isBond, 0, 0);
      return;
    }

    if ("delete" == propertyName) {
      if (wildID == null && currentDipole == null) {
        clear(false);
        return;
      }
      setProperty(Token.delete, isBond, 0, 0);
      return;
    }

    if ("width" == propertyName) {
      short mad = tempDipole.mad = (short) (((Float) value).floatValue() * 1000);
      if (currentDipole == null)
        setProperty(Token.wireframe, isBond, mad, 0);  //
      return;
    }

    if ("offset" == propertyName) {
      float offset = tempDipole.offsetAngstroms = ((Float) value).floatValue();
      if (currentDipole == null)
        setProperty(Token.axes, isBond, 0, offset);
      return;
    }

    if ("offsetPercent" == propertyName) {
      int offsetPercent = tempDipole.offsetPercent = ((Integer) value).intValue();
      if (tempDipole.dipoleValue != 0)
        tempDipole.offsetAngstroms = offsetPercent / 100f
            * tempDipole.dipoleValue;
      if (currentDipole == null)
        setProperty(Token.percent, isBond, 0, offsetPercent / 100f);
      return;
    }

    if ("offsetSide" == propertyName) {
      float offsetSide = ((Float) value).floatValue();
      setProperty(Token.sidechain, isBond, 0, offsetSide);
      return;
    }

    if ("cross" == propertyName) {
      setProperty(Token.cross, isBond, (((Boolean) value).booleanValue() ? 1 : 0), 0);
      return;
    }

    if ("color" == propertyName) {
      colix = Graphics3D.getColix(value);
      if (isBond) {
        setColixDipole(colix, JmolEdge.BOND_COVALENT_MASK, bs);
      } else if (value != null) {
        setProperty(Token.color, false, 0, 0);
      }
      return;
    }

    if ("translucency" == propertyName) {
      setProperty(Token.translucent, isBond, (value.equals("translucent") ? 1 : 0), 0);
      return;
    }

    if ("clear" == propertyName) {
      currentDipole = null;
      clear(false);
    }

    if ("clearBonds" == propertyName) {
      clear(true);
    }

    if ("startSet" == propertyName) {
      BitSet atomset = (BitSet) value;
      startCoord = viewer.getAtomSetCenter(atomset);
      tempDipole.set(startCoord, new Point3f(0, 0, 0), dipoleValue);
      if (BitSetUtil.cardinalityOf(atomset) == 1)
        atomIndex1 = atomset.nextSetBit(0);
      return;
    }

    if ("atomBitset" == propertyName) {
      BitSet atomset = (BitSet) value;
      atomIndex1 = atomset.nextSetBit(0);
      startCoord = modelSet.atoms[atomIndex1];
      atomset.clear(atomIndex1);
      propertyName = "endSet";
      //passes to endSet
    }

    if ("endSet" == propertyName) {
      iHaveTwoEnds = true;
      BitSet atomset = (BitSet) value;
      if (atomIndex1 >= 0 && BitSetUtil.cardinalityOf(atomset) == 1) {
        atomIndex2 = atomset.nextSetBit(0);
        tempDipole.set(modelSet.atoms[atomIndex1], modelSet.atoms[atomIndex2],
            1);
        currentDipole = findDipole(tempDipole.thisID, tempDipole.dipoleInfo);
        tempDipole.thisID = currentDipole.thisID;
        if (isSameAtoms(currentDipole, tempDipole.dipoleInfo)) {
          tempDipole = currentDipole;
          if (dipoleValue > 0)
            tempDipole.dipoleValue = dipoleValue;
        }
      } else {
        tempDipole.set(startCoord, viewer.getAtomSetCenter(atomset),
            dipoleValue);
      }
      //NOTTTTTT!!!! currentDipole = tempDipole;
      return;
    }

    if ("startCoord" == propertyName) {
      startCoord.set((Point3f) value);
      tempDipole.set(startCoord, new Point3f(0, 0, 0), dipoleValue);
      return;
    }

    if ("endCoord" == propertyName) {
      iHaveTwoEnds = true;
      endCoord.set((Point3f) value);
      tempDipole.set(startCoord, endCoord, dipoleValue);
      dumpDipoles("endCoord");
      return;
    }

    if ("value" == propertyName) {
      dipoleValue = ((Float) value).floatValue();
      isUserValue = true;
      tempDipole.set(dipoleValue);
      if (tempDipole.offsetPercent != 0)
        tempDipole.offsetAngstroms = tempDipole.offsetPercent / 100f
            * tempDipole.dipoleValue;
      return;
    }

    if ("set" == propertyName) {
      if (isBond || !iHaveTwoEnds)
        return;
      setDipole();
      setModelIndex();
      return;
    }

    if (propertyName == "deleteModelAtoms") {
      int modelIndex = ((int[])((Object[])value)[2])[0];
      for (int i = dipoleCount; --i >= 0;)
        if (dipoles[i].modelIndex > modelIndex) {
          dipoles[i].modelIndex--;
        } else if (dipoles[i].modelIndex == modelIndex) {
          if (dipoles[i] == currentDipole)
            currentDipole = null;            
          dipoles = (Dipole[]) ArrayUtil.deleteElements(dipoles, i, 1);
          dipoleCount--;
        }
      currentDipole = null;
      return;
    }

  }

  private void setProperty(int tok, boolean bondOnly, int iValue, float fValue) {
    if (currentDipole != null)
      setProperty(tok, currentDipole, iValue, fValue);
    else {
      for (int i = dipoleCount; --i >= 0;)
        if (!bondOnly || isBondDipole(i))
          if (wildID == null
              || TextFormat.isMatch(dipoles[i].thisID.toUpperCase(), wildID,
                  true, true))
            setProperty(tok, dipoles[i], iValue, fValue);
    }
  }

  private void setProperty(int tok, Dipole dipole, int iValue, float fValue) {
    switch (tok) {
    case Token.on:
      dipole.visible = true;
      return;
    case Token.off:
      dipole.visible = false;       
      return;
    case Token.delete:
      deleteDipole(dipole);
      return;
    case Token.wireframe:
      dipole.mad = tempDipole.mad = (short) iValue;
      return;
    case Token.axes:
      dipole.offsetAngstroms = fValue;
      return;
    case Token.percent:
      dipole.offsetAngstroms = fValue * dipole.dipoleValue;
      return;
    case Token.sidechain:
      dipole.offsetSide = fValue;
      return;
    case Token.cross:
      dipole.noCross = (iValue == 0);
      return;
    case Token.color:
      dipole.colix = colix;
      return;
    case Token.translucent:
      dipole.setTranslucent(iValue == 1, translucentLevel);
      return;
    }
    Logger.error("Unkown dipole property! " + Token.nameOf(tok));
  }

  @Override
  public boolean getProperty(String property, Object[] data) {
    if (property == "checkID") {
      String key = ((String) data[0]).toUpperCase();
      boolean isWild = TextFormat.isWild(key);
      for (int i = dipoleCount; --i >= 0;) {
        String id = dipoles[i].thisID;
        if (id.equalsIgnoreCase(key) || isWild
            && TextFormat.isMatch(id.toUpperCase(), key, true, true)) {
          data[1] = id;
          return true;
        }
      }
      return false;
    }
    return false;
  }
  
  @Override
  public Object getProperty(String property, int index) {
    if (property.equals("list")) {
      return getShapeState();
    }
    return null;
  }

  private void getBondDipoles() {
    float[] partialCharges = modelSet.getPartialCharges();
    if (partialCharges == null)
      return;
    clear(true);
    Bond[] bonds = modelSet.getBonds();
    for (int i = bonds.length; --i >= 0;) {
      Bond bond = bonds[i];
      if (!bond.isCovalent())
        continue;
      float c1 = partialCharges[bond.getAtomIndex1()];
      float c2 = partialCharges[bond.getAtomIndex2()];
      if (c1 != c2)
        setDipole(bond.getAtom1(), bond.getAtom2(), c1, c2);
    }
  }
  
  private boolean isBondDipole(int i) {
    if (i >= dipoles.length || dipoles[i] == null)
      return false;
    return (dipoles[i].isBondType());
  }

  private void setColixDipole(short colix, int bondTypeMask, BitSet bs) {
    if (colix == Graphics3D.USE_PALETTE)
      return; // not implemented
    BondIterator iter = modelSet.getBondIterator(bondTypeMask, bs);
    while (iter.hasNext()) {
      Dipole d = findBondDipole(iter.next());
      if (d != null)
        d.colix = colix;
    }
  }

  private void setDipole() {
    if (currentDipole == null)
      currentDipole = allocDipole("", "");
    currentDipole.set(tempDipole.thisID, tempDipole.dipoleInfo,
        tempDipole.atoms, tempDipole.dipoleValue, tempDipole.mad,
        tempDipole.offsetAngstroms, tempDipole.offsetPercent,
        tempDipole.offsetSide, tempDipole.origin, tempDipole.vector);
    currentDipole.isUserValue = isUserValue;
    currentDipole.modelIndex = viewer.getCurrentModelIndex();
  }

  final private static float E_ANG_PER_DEBYE = 0.208194f;

  private void setDipole(Atom atom1, Atom atom2, float c1, float c2) {
    Dipole dipole = findDipole(atom1, atom2, true);
    float value = (c1 - c2) / 2f * atom1.distance(atom2) / E_ANG_PER_DEBYE;
    if (value < 0) {
      dipole.set(atom2, atom1, -value);
    } else {
      dipole.set(atom1, atom2, value);
    }
    dipole.type = Dipole.DIPOLE_TYPE_BOND;
    dipole.modelIndex = atom1.getModelIndex();
  }

  private int getDipoleIndex(String dipoleInfo, String thisID) {
    if (dipoleInfo != null && dipoleInfo.length() > 0)
      for (int i = dipoleCount; --i >= 0;)
        if (isSameAtoms(dipoles[i], dipoleInfo))
          return i;
    return getIndexFromName(thisID);
  }

  private boolean isSameAtoms(Dipole dipole, String dipoleInfo) {
    // order-independent search for two atoms:
    // looking for (xyz)(x'y'z') in (xyz)(x'y'z')(xyz)(x'y'z')
    return (dipole != null && dipole.isBondType() && (dipole.dipoleInfo + dipole.dipoleInfo)
        .indexOf(dipoleInfo) >= 0);
  }

  private int getDipoleIndex(int atomIndex1, int atomIndex2) {
    for (int i = dipoleCount; --i >= 0;) {
      if (dipoles[i] != null
          && dipoles[i].atoms[0] != null
          && dipoles[i].atoms[1] != null
          && (dipoles[i].atoms[0].getIndex() == atomIndex1
              && dipoles[i].atoms[1].getIndex() == atomIndex2 || dipoles[i].atoms[1]
              .getIndex() == atomIndex1
              && dipoles[i].atoms[0].getIndex() == atomIndex2))
        return i;
    }
    return -1;
  }

  private void deleteDipole(Dipole dipole) {
    if (dipole == null)
      return;
    if (currentDipole == dipole)
      currentDipole = null;
    int i;
    for (i = dipoleCount; dipoles[--i] != dipole;) {
    }
    if (i < 0)
      return;
    for (int j = i + 1; j < dipoleCount; ++j)
      dipoles[j - 1] = dipoles[j];
    dipoles[--dipoleCount] = null;
  }

  private Dipole findDipole(String thisID) {
    int dipoleIndex = getIndexFromName(thisID);
    if (dipoleIndex >= 0) {
      return dipoles[dipoleIndex];
    }
    return null;
  }

  private Dipole findDipole(Atom atom1, Atom atom2, boolean doAllocate) {
    int dipoleIndex = getDipoleIndex(atom1.getIndex(), atom2.getIndex());
    if (dipoleIndex >= 0) {
      return dipoles[dipoleIndex];
    }
    return (doAllocate ? allocDipole("", "") : null);
  }

  private Dipole findBondDipole(Bond bond) {
    Dipole d = findDipole(bond.getAtom1(), bond.getAtom2(), false);
    return (d == null || d.atoms[0] == null ? null : d);
  }

  private Dipole findDipole(String thisID, String dipoleInfo) {
    // must be able to identify a dipole from its ID only SECONDARILY,
    // as we want one dipole per bond. So we look for coord ID.
    int dipoleIndex = getDipoleIndex(dipoleInfo, thisID);
    if (dipoleIndex >= 0) {
      if (thisID.length() > 0)
        dipoles[dipoleIndex].thisID = thisID;
      return dipoles[dipoleIndex];
    }
    return allocDipole(thisID, dipoleInfo);
  }

  private Dipole allocDipole(String thisID, String dipoleInfo) {
    dipoles = (Dipole[]) ArrayUtil.ensureLength(dipoles, dipoleCount + 1);
    if (thisID == null || thisID.length() == 0)
      thisID = "dipole" + (dipoleCount + 1);
    Dipole d = dipoles[dipoleCount++] = new Dipole(viewer
        .getCurrentModelIndex(), thisID, dipoleInfo, colix, DEFAULT_MAD, true);
    return d;
  }

  private void dumpDipoles(String msg) {
    for (int i = dipoleCount; --i >= 0;) {
      Dipole dipole = dipoles[i];
      Logger.info("\n\n" + msg + " dump dipole " + i + " " + dipole + " "
          + dipole.thisID + " " + dipole.dipoleInfo + " "
          + dipole.visibilityFlags + " mad=" + dipole.mad + " vis="
          + dipole.visible + "\n orig" + dipole.origin + " " + " vect"
          + dipole.vector + " val=" + dipole.dipoleValue);
    }
    if (currentDipole != null)
      Logger.info(" current = " + currentDipole + currentDipole.origin);
    if (tempDipole != null)
      Logger.info(" temp = " + tempDipole + " " + tempDipole.origin);
  }

  private void clear(boolean clearBondDipolesOnly) {
    if (clearBondDipolesOnly) {
      for (int i = dipoleCount; --i >= 0;)
        if (isBondDipole(i))
          deleteDipole(dipoles[i]);
      return;
    }
    for (int i = dipoleCount; --i >= 0;)
      if (!isBond || isBondDipole(i))
        deleteDipole(dipoles[i]);
  }

  @Override
  public int getIndexFromName(String thisID) {
    if (thisID == null)
      return -1;
    for (int i = dipoleCount; --i >= 0;) {
      if (dipoles[i] != null && thisID.equals(dipoles[i].thisID))
        return i;
    }
    return -1;
  }

  @Override
  public List<Map<String, Object>> getShapeDetail() {
    List<Map<String, Object>> V = new ArrayList<Map<String,Object>>();
    Map<String, Object> atomInfo;
    for (int i = 0; i < dipoleCount; i++) {
      Map<String, Object> info = new Hashtable<String, Object>();
      Dipole dipole = dipoles[i];
      info.put("ID", dipole.thisID);
      info.put("vector", dipole.vector);
      info.put("origin", dipole.origin);
      if (dipole.atoms[0] != null) {
        atomInfo = new Hashtable<String, Object>();
        viewer.getAtomIdentityInfo(dipole.atoms[0].getIndex(), atomInfo);
        List<Map<String, Object>> atoms = new ArrayList<Map<String,Object>>();
        atoms.add(atomInfo);
        atomInfo = new Hashtable<String, Object>();
        viewer.getAtomIdentityInfo(dipole.atoms[1].getIndex(), atomInfo);
        atoms.add(atomInfo);
        info.put("atoms", atoms);
        info.put("magnitude", new Float(dipole.vector.length()));
      }
      V.add(info);
    }
    return V;
  }

  private void setModelIndex() {
    if (currentDipole == null)
      return;
    currentDipole.visible = true;
    currentDipole.modelIndex = viewer.getCurrentModelIndex();
  }

  @Override
  public void setVisibilityFlags(BitSet bs) {
    /*
     * set all fixed objects visible; others based on model being displayed
     * 
     */
    for (int i = dipoleCount; --i >= 0;) {
      Dipole dipole = dipoles[i];
      dipole.visibilityFlags = ((dipole.modelIndex < 0 || bs
          .get(dipole.modelIndex))
          && dipole.mad != 0
          && dipole.visible
          && dipole.origin != null
          && dipole.vector != null
          && dipole.vector.length() != 0
          && dipole.dipoleValue != 0 ? myVisibilityFlag : 0);
    }
    //dumpDipoles("setVis");
  }

  @Override
  public String getShapeState() {
    if (dipoleCount == 0)
      return "";
    StringBuffer s = new StringBuffer();
    int thisModel = -1;
    int modelCount = viewer.getModelCount();
    for (int i = 0; i < dipoleCount; i++) {
      Dipole dipole = dipoles[i];
      if (dipole.isValid) {
        if (modelCount > 1 && dipole.modelIndex != thisModel)
          appendCmd(s, "frame "
              + viewer.getModelNumberDotted(thisModel = dipole.modelIndex));
        s.append(dipole.getShapeState());
        appendCmd(s, getColorCommand("dipole", dipole.colix));
      }
    }
    return s.toString();
  }
}
