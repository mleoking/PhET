/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2011-03-24 21:19:06 -0700 (Thu, 24 Mar 2011) $
 * $Revision: 15341 $

 *
 * Copyright (C) 2003-2005  The Jmol Development Team
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

package org.jmol.modelset;


import org.jmol.viewer.JmolConstants;
import org.jmol.script.Token;
import org.jmol.viewer.Viewer;
import org.jmol.api.JmolEdge;
import org.jmol.api.JmolNode;
import org.jmol.api.SymmetryInterface;
import org.jmol.atomdata.RadiusData;
import org.jmol.g3d.Graphics3D;
import org.jmol.util.Elements;
import org.jmol.util.Point3fi;

import java.util.BitSet;
import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

final public class Atom extends Point3fi implements JmolNode {

  private final static byte VIBRATION_VECTOR_FLAG = 1;
  private final static byte IS_HETERO_FLAG = 2;
  private final static byte FLAG_MASK = 3;
  
  public static final int RADIUS_MAX = 16;

  Group group;
  private BitSet atomSymmetry;
  int atomSite;
  private float userDefinedVanDerWaalRadius;
  
  private short atomicAndIsotopeNumber;
  private byte formalChargeAndFlags;
  private byte valence;
  char alternateLocationID;
  public byte atomID;
  public byte getAtomID() {
    return atomID;
  }
  
  public short madAtom;

  short colixAtom;
  byte paletteID = JmolConstants.PALETTE_CPK;

  Bond[] bonds;
  
  /**
   * 
   * @return  bonds -- WHICH MAY BE NULL
   * 
   */
  public Bond[] getBonds() {
    return bonds;
  }

  public void setBonds(Bond[] bonds) {
    this.bonds = bonds;  // for Smiles equating
  }
  
  int nBondsDisplayed = 0;
  int nBackbonesDisplayed = 0;
  
  public int getNBackbonesDisplayed() {
    return nBackbonesDisplayed;
  }
  
  int clickabilityFlags;
  int shapeVisibilityFlags;

  public Atom(int modelIndex, int atomIndex,
        float x, float y, float z, float radius,
        BitSet atomSymmetry, int atomSite,
        short atomicAndIsotopeNumber, int formalCharge, 
        boolean isHetero, char alternateLocationID) {
    this.modelIndex = (short)modelIndex;
    this.atomSymmetry = atomSymmetry;
    this.atomSite = atomSite;
    this.index = atomIndex;
    this.atomicAndIsotopeNumber = atomicAndIsotopeNumber;
    if (isHetero)
      formalChargeAndFlags = IS_HETERO_FLAG;
    setFormalCharge(formalCharge);
    this.alternateLocationID = alternateLocationID;
    userDefinedVanDerWaalRadius = radius;
    set(x, y, z);
  }

  public final void setShapeVisibilityFlags(int flag) {
    shapeVisibilityFlags = flag;
  }

  public final void setShapeVisibility(int flag, boolean isVisible) {
    if(isVisible) {
      shapeVisibilityFlags |= flag;        
    } else {
      shapeVisibilityFlags &=~flag;
    }
  }
  
  public boolean isBonded(Atom atomOther) {
    if (bonds != null)
      for (int i = bonds.length; --i >= 0;)
        if (bonds[i].getOtherAtom(this) == atomOther)
          return true;
    return false;
  }

  public Bond getBond(Atom atomOther) {
    if (bonds != null)
      for (int i = bonds.length; --i >= 0;)
        if (bonds[i].getOtherAtom(atomOther) != null)
          return bonds[i];
    return null;
  }

  void addDisplayedBond(int stickVisibilityFlag, boolean isVisible) {
    nBondsDisplayed += (isVisible ? 1 : -1);
    setShapeVisibility(stickVisibilityFlag, (nBondsDisplayed > 0));
  } 
  
  public void addDisplayedBackbone(int backboneVisibilityFlag, boolean isVisible) {
    nBackbonesDisplayed += (isVisible ? 1 : -1);
    setShapeVisibility(backboneVisibilityFlag, isVisible);
  }
  
  void deleteBond(Bond bond) {
    // this one is used -- from Bond.deleteAtomReferences
    if (bonds != null)
      for (int i = bonds.length; --i >= 0;)
        if (bonds[i] == bond) {
          deleteBond(i);
          return;
        }
  }

  private void deleteBond(int i) {
    int newLength = bonds.length - 1;
    if (newLength == 0) {
      bonds = null;
      return;
    }
    Bond[] bondsNew = new Bond[newLength];
    int j = 0;
    for ( ; j < i; ++j)
      bondsNew[j] = bonds[j];
    for ( ; j < newLength; ++j)
      bondsNew[j] = bonds[j + 1];
    bonds = bondsNew;
  }

  void clearBonds() {
    bonds = null;
  }

  public int getBondedAtomIndex(int bondIndex) {
    return bonds[bondIndex].getOtherAtom(this).index;
  }

  /*
   * What is a MAR?
   *  - just a term that Miguel made up
   *  - an abbreviation for Milli Angstrom Radius
   * that is:
   *  - a *radius* of either a bond or an atom
   *  - in *millis*, or thousandths of an *angstrom*
   *  - stored as a short
   *
   * However! In the case of an atom radius, if the parameter
   * gets passed in as a negative number, then that number
   * represents a percentage of the vdw radius of that atom.
   * This is converted to a normal MAR as soon as possible
   *
   * (I know almost everyone hates bytes & shorts, but I like them ...
   *  gives me some tiny level of type-checking ...
   *  a rudimentary form of enumerations/user-defined primitive types)
   */

  public void setMadAtom(Viewer viewer, RadiusData rd) {
    madAtom = calculateMad(viewer, rd);
  }
  /* what about these?    
  if (Float.isNaN(fsize)) {
    case -1000: // temperature
      break;
*/
  
  public short calculateMad(Viewer viewer, RadiusData rd) {
    if (rd == null)
      return 0;
    float f = rd.value;
    if (f == 0)
      return 0;
    switch (rd.type) {
    case RadiusData.TYPE_SCREEN:
       return (short) f;
    case RadiusData.TYPE_FACTOR:
    case RadiusData.TYPE_OFFSET:
      float r = 0;
      switch (rd.vdwType) {
      case Token.temperature:
        float tmax = viewer.getBfactor100Hi();
        r = (tmax > 0 ? getBfactor100() / tmax : 0);
        break;
      case Token.ionic:
        r = getBondingRadiusFloat();
        break;
      case Token.adpmin:
      case Token.adpmax:
        r = getADPMinMax(rd.vdwType == Token.adpmax);
        break;
      default:
        r = getVanderwaalsRadiusFloat(viewer, rd.vdwType);
      }
      if (rd.type == RadiusData.TYPE_FACTOR)
        f *= r;
      else
        f += r;
      break;
    default:
    case RadiusData.TYPE_ABSOLUTE:
      break;
    }
    short mad = (short) (f < 0 ? f: f * 2000);
    if (mad < 0 && f > 0)
      mad = 0;
    return mad; 
  }

  public float getADPMinMax(boolean isMax) {
    Object[] ellipsoid = getEllipsoid();
    if (ellipsoid == null)
      return 0;
    return ((float[])ellipsoid[1])[isMax ? 5 : 3];
  }

  public int getRasMolRadius() {
    return Math.abs(madAtom / 8); //  1000r = 1000d / 2; rr = (1000r / 4);
  }

  public int getCovalentBondCount() {
    if (bonds == null)
      return 0;
    int n = 0;
    Bond b;
    for (int i = bonds.length; --i >= 0; )
      if (((b = bonds[i]).order & JmolEdge.BOND_COVALENT_MASK) != 0
          && !b.getOtherAtom(this).isDeleted())
        ++n;
    return n;
  }

  public int getCovalentHydrogenCount() {
    if (bonds == null)
      return 0;
    int n = 0;
    for (int i = bonds.length; --i >= 0; ) {
      if ((bonds[i].order & JmolEdge.BOND_COVALENT_MASK) == 0)
        continue;
      Atom a = bonds[i].getOtherAtom(this);
      if (a.valence >= 0 && a.getElementNumber() == 1)
        ++n;
    }
    return n;
  }

  public JmolEdge[] getEdges() {
    return bonds;
  }
  
  public void setColixAtom(short colixAtom) {
    this.colixAtom = colixAtom;
  }

  public void setPaletteID(byte paletteID) {
    this.paletteID = paletteID;
  }

  public void setTranslucent(boolean isTranslucent, float translucentLevel) {
    colixAtom = Graphics3D.getColixTranslucent(colixAtom, isTranslucent, translucentLevel);    
  }

  public boolean isTranslucent() {
    return Graphics3D.isColixTranslucent(colixAtom);
  }

  public short getElementNumber() {
    return (short) (atomicAndIsotopeNumber % 128);
  }
  
  public short getIsotopeNumber() {
    return (short) (atomicAndIsotopeNumber >> 7);
  }
  
  public short getAtomicAndIsotopeNumber() {
    return atomicAndIsotopeNumber;
  }

  public void setAtomicAndIsotopeNumber(int n) {
    if (n < 0 || (n % 128) >= Elements.elementNumberMax || n > Short.MAX_VALUE)
      n = 0;
    atomicAndIsotopeNumber = (short) n;
  }

  public String getElementSymbol(boolean withIsotope) {
    return Elements.elementSymbolFromNumber(withIsotope ? atomicAndIsotopeNumber : atomicAndIsotopeNumber % 128);    
  }
  
  public String getElementSymbol() {
    return getElementSymbol(true);
  }

  public char getAlternateLocationID() {
    return alternateLocationID;
  }
  
  boolean isAlternateLocationMatch(String strPattern) {
    if (strPattern == null)
      return (alternateLocationID == '\0');
    if (strPattern.length() != 1)
      return false;
    char ch = strPattern.charAt(0);
    return (ch == '*' 
        || ch == '?' && alternateLocationID != '\0' 
        || alternateLocationID == ch);
  }

  public boolean isHetero() {
    return (formalChargeAndFlags & IS_HETERO_FLAG) != 0;
  }

  public boolean hasVibration() {
    return (formalChargeAndFlags & VIBRATION_VECTOR_FLAG) != 0;
  }

  void setFormalCharge(int charge) {
    formalChargeAndFlags = (byte)((formalChargeAndFlags & FLAG_MASK) 
        | ((charge == Integer.MIN_VALUE ? 0 : charge > 7 ? 7 : charge < -3 ? -3 : charge) << 2));
  }
  
  void setVibrationVector() {
    formalChargeAndFlags |= VIBRATION_VECTOR_FLAG;
  }
  
  public int getFormalCharge() {
    return formalChargeAndFlags >> 2;
  }

  // a percentage value in the range 0-100
  public int getOccupancy100() {
    byte[] occupancies = group.chain.modelSet.occupancies;
    return occupancies == null ? 100 : occupancies[index];
  }

  // This is called bfactor100 because it is stored as an integer
  // 100 times the bfactor(temperature) value
  public int getBfactor100() {
    short[] bfactor100s = group.chain.modelSet.bfactor100s;
    if (bfactor100s == null)
      return 0;
    return bfactor100s[index];
  }

  public boolean setRadius(float radius) {
    return !Float.isNaN(userDefinedVanDerWaalRadius = (radius > 0 ? radius : Float.NaN));  
  }
  
  public void delete(BitSet bsBonds) {
    valence = -1;
    if (bonds != null)
      for (int i = bonds.length; --i >= 0; ) {
        Bond bond = bonds[i];
        bond.getOtherAtom(this).deleteBond(bond);
        bsBonds.set(bond.index);
      }
    bonds = null;
  }

  public boolean isDeleted() {
    return (valence < 0);
  }

  public void setValence(int nBonds) {
    if (isDeleted()) // no resurrection
      return;
    valence = (byte) (nBonds < 0 ? 0 : nBonds < 0xEF ? nBonds : 0xEF);
  }

  public int getValence() {
    if (isDeleted())
      return -1;
    int n = valence;
    if (n == 0 && bonds != null)
      for (int i = bonds.length; --i >= 0;)
        n += bonds[i].getValence();
    return n;
  }

  public int getImplicitHydrogenCount() {
    int targetValence = getTargetValence();
    int charge = getFormalCharge();
    if (charge != 0)
      targetValence += (targetValence == 4 ? -Math.abs(charge) : charge);
    int n = targetValence - getValence();
    return (n < 0 ? 0 : n);
  }

  int getTargetValence() {
    switch (getElementNumber()) {
    case 6: //C
    case 14: //Si      
      return 4;
    case 5:  // B
    case 7:  // N
    case 15: // P
      return 3;
    case 8: //O
    case 16: //S
      return 2;
    case 9: // F
    case 17: // Cl
    case 35: // Br
    case 53: // I
      return 1;
    }
    return -1;
  }


  public float getDimensionValue(int dimension) {
    return (dimension == 0 ? x : (dimension == 1 ? y : z));
  }

  public float getVanderwaalsRadiusFloat(Viewer viewer, int iType) {
    // called by atomPropertyFloat as VDW_AUTO,
    // AtomCollection.filAtomData with VDW_AUTO or VDW_NOJMOL
    // AtomCollection.findMaxRadii with VDW_AUTO
    // AtomCollection.getAtomPropertyState with VDW_AUTO
    // AtomCollection.getVdwRadius with passed on type
    return (Float.isNaN(userDefinedVanDerWaalRadius) 
        ? viewer.getVanderwaalsMar(atomicAndIsotopeNumber % 128, getVdwType(iType)) / 1000f
        : userDefinedVanDerWaalRadius);
  }

  /**
   * 
   * @param iType
   * @return if VDW_AUTO, will return VDW_AUTO_JMOL, VDW_AUTO_RASMOL, or VDW_AUTO_BABEL
   *         based on the model type
   */
  private int getVdwType(int iType) {
    switch (iType) {
    case JmolConstants.VDW_AUTO:
      iType = group.chain.modelSet.getDefaultVdwType(modelIndex);
      break;
    case JmolConstants.VDW_NOJMOL:
      iType = group.chain.modelSet.getDefaultVdwType(modelIndex);
      if (iType == JmolConstants.VDW_AUTO_JMOL)
        iType = JmolConstants.VDW_AUTO_BABEL;
      break;
    }
    return iType;
  }

  public float getCovalentRadiusFloat() {
    return JmolConstants.getBondingRadiusFloat(atomicAndIsotopeNumber % 128, 0);
  }

  public float getBondingRadiusFloat() {
    float[] ionicRadii = group.chain.modelSet.ionicRadii;
    float r = (ionicRadii == null ? 0 : ionicRadii[index]);
    return (r == 0 ? JmolConstants.getBondingRadiusFloat(atomicAndIsotopeNumber % 128,
        getFormalCharge()) : r);
  }

  float getVolume(Viewer viewer, int iType) {
    float r1 = (iType < 0 ? userDefinedVanDerWaalRadius : Float.NaN);
    if (Float.isNaN(r1))
      r1 = viewer.getVanderwaalsMar(getElementNumber(), getVdwType(iType)) / 1000f;
    double volume = 0;
    if (bonds != null)
      for (int j = 0; j < bonds.length; j++) {
        if (!bonds[j].isCovalent())
          continue;
        Atom atom2 = bonds[j].getOtherAtom(this);
        float r2 = (iType < 0 ? atom2.userDefinedVanDerWaalRadius : Float.NaN);
        if (Float.isNaN(r2))
          r2 = viewer.getVanderwaalsMar(atom2.getElementNumber(), atom2
              .getVdwType(iType)) / 1000f;
        float d = distance(atom2);
        if (d > r1 + r2)
          continue;
        if (d + r1 <= r2)
          return 0;

        // calculate hidden spherical cap height and volume
        // A.Bondi, J. Phys. Chem. 68, 1964, 441-451.

        double h = r1 - (r1 * r1 + d * d - r2 * r2) / (2.0 * d);
        volume -= Math.PI / 3 * h * h * (3 * r1 - h);
      }
    return (float) (volume + 4 * Math.PI / 3 * r1 * r1 * r1);
  }

  int getCurrentBondCount() {
    return bonds == null ? 0 : bonds.length;
  }

  public short getColix() {
    return colixAtom;
  }

  public byte getPaletteID() {
    return paletteID;
  }

  public float getRadius() {
    return Math.abs(madAtom / (1000f * 2));
  }

  public int getIndex() {
    return index;
  }

  public int getAtomSite() {
    return atomSite;
  }

  public BitSet getAtomSymmetry() {
    return atomSymmetry;
  }

   void setGroup(Group group) {
     this.group = group;
   }

   public Group getGroup() {
     return group;
   }
   
   public void setGroupBits(BitSet bs) {
     group.selectAtoms(bs);
   }
   
   public String getAtomName() {
     return (atomID > 0 ? JmolConstants.getSpecialAtomName(atomID) 
         : group.chain.modelSet.atomNames[index]);
   }
   
   public String getAtomType() {
    String[] atomTypes = group.chain.modelSet.atomTypes;
    String type = (atomTypes == null ? null : atomTypes[index]);
    return (type == null ? getAtomName() : type);
  }
   
   public int getAtomNumber() {
     int[] atomSerials = group.chain.modelSet.atomSerials;
     // shouldn't ever be null.
     return (atomSerials != null ? atomSerials[index] : index);
//        : group.chain.modelSet.isZeroBased ? atomIndex : atomIndex);
   }

   public boolean isInFrame() {
     return ((shapeVisibilityFlags & JmolConstants.ATOM_IN_FRAME) != 0);
   }

   public int getShapeVisibilityFlags() {
     return shapeVisibilityFlags;
   }
   
   public boolean isShapeVisible(int shapeVisibilityFlag) {
     return ((shapeVisibilityFlags & shapeVisibilityFlag) != 0);
   }

   public float getPartialCharge() {
     float[] partialCharges = group.chain.modelSet.partialCharges;
     return partialCharges == null ? 0 : partialCharges[index];
   }

   public Object[] getEllipsoid() {
     return group.chain.modelSet.getEllipsoid(index);
   }

   /**
    * Given a symmetry operation number, the set of cells in the model, and the
    * number of operations, this method returns either 0 or the cell number (555, 666)
    * of the translated symmetry operation corresponding to this atom.
    * 
    * atomSymmetry is a bitset that is created in adapter.smarter.AtomSetCollection
    * 
    * It is arranged as follows:
    * 
    * |--overall--|---cell1---|---cell2---|---cell3---|...
    * 
    * |012..nOps-1|012..nOps-1|012..nOp-1s|012..nOps-1|...
    * 
    * If a bit is set, it means that the atom was created using that operator
    * operating on the base file set and translated for that cell.
    * 
    * If any bit is set in any of the cell blocks, then the same
    * bit will also be set in the overall block. This allows for
    * rapid determination of special positions and also of
    * atom membership in any operation set.
    * 
    *  Note that it is not necessarily true that an atom is IN the designated
    *  cell, because one can load {nnn mmm 0}, and then, for example, the {-x,-y,-z}
    *  operator sends atoms from 555 to 444. Still, those atoms would be marked as
    *  cell 555 here, because no translation was carried out. 
    *  
    *  That is, the numbers 444 in symop=3444 do not refer to a cell, per se. 
    *  What they refer to is the file-designated operator plus a translation of
    *  {-1 -1 -1/1}. 
    * 
    * @param symop        = 0, 1, 2, 3, ....
    * @param cellRange    = {444, 445, 446, 454, 455, 456, .... }
    * @param nOps         = 2 for x,y,z;-x,-y,-z, for example
    * @return cell number such as 565
    */
   public int getSymmetryTranslation(int symop, int[] cellRange, int nOps) {
     int pt = symop;
     for (int i = 0; i < cellRange.length; i++)
       if (atomSymmetry.get(pt += nOps))
         return cellRange[i];
     return 0;
   }
   
   /**
    * Looks for a match in the cellRange list for this atom within the specified translation set
    * select symop=0NNN for this
    * 
    * @param cellNNN
    * @param cellRange
    * @param nOps
    * @return     matching cell number, if applicable
    */
   public int getCellTranslation(int cellNNN, int[] cellRange, int nOps) {
     int pt = nOps;
     for (int i = 0; i < cellRange.length; i++)
       for (int j = 0; j < nOps;j++, pt++)
       if (atomSymmetry.get(pt) && cellRange[i] == cellNNN)
         return cellRange[i];
     return 0;
   }
   
   String getSymmetryOperatorList() {
    String str = "";
    ModelSet f = group.chain.modelSet;
    int nOps = f.getModelSymmetryCount(modelIndex);
    if (nOps == 0 || atomSymmetry == null)
      return "";
    int[] cellRange = f.getModelCellRange(modelIndex);
    int pt = nOps;
    int n = (cellRange == null ? 1 : cellRange.length);
    for (int i = 0; i < n; i++)
      for (int j = 0; j < nOps; j++)
        if (atomSymmetry.get(pt++))
          str += "," + (j + 1) + "" + cellRange[i];
    return str.substring(1);
  }
   
  public int getModelIndex() {
    return modelIndex;
  }
   
  public int getMoleculeNumber() {
    return (group.chain.modelSet.getMoleculeIndex(index) + 1);
  }
   
  private float getFractionalCoord(char ch, boolean asAbsolute) {
    Point3f pt = getFractionalCoord(asAbsolute);
    return (ch == 'X' ? pt.x : ch == 'Y' ? pt.y : pt.z);
  }
    
  private float getFractionalUnitCoord(char ch) {
    Point3f pt = getFractionalUnitCoord(false);
    return (ch == 'X' ? pt.x : ch == 'Y' ? pt.y : pt.z);
  }

  private Point3f getFractionalCoord(boolean asAbsolute) {
    // asAbsolute TRUE uses the original unshifted matrix
    SymmetryInterface[] c = group.chain.modelSet.unitCells;
    Point3f pt = new Point3f(this);
    if (c != null && c[modelIndex].haveUnitCell())
      c[modelIndex].toFractional(pt, asAbsolute);
    return pt;
  }
  
  public Point3f getFractionalUnitCoord(boolean asCartesian) {
    SymmetryInterface[] c = group.chain.modelSet.unitCells;
    Point3f pt = new Point3f(this);
    if (c != null && c[modelIndex].haveUnitCell()) {
      if (group.chain.model.isJmolDataFrame) {
        c[modelIndex].toFractional(pt, false);
        if (asCartesian)
          c[modelIndex].toCartesian(pt, false);
      } else {
        c[modelIndex].toUnitCell(pt, null);
        if (!asCartesian)
          c[modelIndex].toFractional(pt, false);
      }
    }
    return pt;
  }
  
  public float getFractionalUnitDistance(Point3f pt, Point3f ptTemp1, Point3f ptTemp2) {
    SymmetryInterface[] c = group.chain.modelSet.unitCells;
    if (c == null) 
      return distance(pt);
    ptTemp1.set(this);
    ptTemp2.set(pt);
    if (group.chain.model.isJmolDataFrame) {
      c[modelIndex].toFractional(ptTemp1, true);
      c[modelIndex].toFractional(ptTemp2, true);
    } else {
      c[modelIndex].toUnitCell(ptTemp1, null);
      c[modelIndex].toUnitCell(ptTemp2, null);
    }
    return ptTemp1.distance(ptTemp2);
  }
  
  void setFractionalCoord(int tok, float fValue, boolean asAbsolute) {
    SymmetryInterface[] c = group.chain.modelSet.unitCells;
    if (c != null && c[modelIndex].haveUnitCell())
      c[modelIndex].toFractional(this, asAbsolute);
    switch (tok) {
    case Token.fux:
    case Token.fracx:
      x = fValue;
      break;
    case Token.fuy:
    case Token.fracy:
      y = fValue;
      break;
    case Token.fuz:
    case Token.fracz:
      z = fValue;
      break;
    }
    if (c != null && c[modelIndex].haveUnitCell())
      c[modelIndex].toCartesian(this, asAbsolute);
  }
  
  void setFractionalCoord(Point3f ptNew, boolean asAbsolute) {
    setFractionalCoord(this, ptNew, asAbsolute);
  }
  
  void setFractionalCoord(Point3f pt, Point3f ptNew, boolean asAbsolute) {
    pt.set(ptNew);
    SymmetryInterface[] c = group.chain.modelSet.unitCells;
    if (c != null && c[modelIndex].haveUnitCell())
      c[modelIndex].toCartesian(pt, asAbsolute && !group.chain.model.isJmolDataFrame);
  }

  boolean isCursorOnTopOf(int xCursor, int yCursor,
                        int minRadius, Atom competitor) {
    int r = screenDiameter / 2;
    if (r < minRadius)
      r = minRadius;
    int r2 = r * r;
    int dx = screenX - xCursor;
    int dx2 = dx * dx;
    if (dx2 > r2)
      return false;
    int dy = screenY - yCursor;
    int dy2 = dy * dy;
    int dz2 = r2 - (dx2 + dy2);
    if (dz2 < 0)
      return false;
    if (competitor == null)
      return true;
    int z = screenZ;
    int zCompetitor = competitor.screenZ;
    int rCompetitor = competitor.screenDiameter / 2;
    if (z < zCompetitor - rCompetitor)
      return true;
    int dxCompetitor = competitor.screenX - xCursor;
    int dx2Competitor = dxCompetitor * dxCompetitor;
    int dyCompetitor = competitor.screenY - yCursor;
    int dy2Competitor = dyCompetitor * dyCompetitor;
    int r2Competitor = rCompetitor * rCompetitor;
    int dz2Competitor = r2Competitor - (dx2Competitor + dy2Competitor);
    return (z - Math.sqrt(dz2) < zCompetitor - Math.sqrt(dz2Competitor));
  }

  /*
   *  DEVELOPER NOTE (BH):
   *  
   *  The following methods may not return 
   *  correct values until after modelSet.finalizeGroupBuild()
   *  
   */
   
  public String getInfo() {
    return getIdentity(true);
  } 

  String getInfoXYZ(boolean useChimeFormat) {
    // for atom picking
    if (useChimeFormat) {
      String group3 = getGroup3(true);
      char chainID = getChainID();
      Point3f pt = (group.chain.modelSet.unitCells == null ? null : getFractionalCoord(true));
      return "Atom: " + (group3 == null ? getElementSymbol() : getAtomName()) + " " + getAtomNumber() 
          + (group3 != null && group3.length() > 0 ? 
              (isHetero() ? " Hetero: " : " Group: ") + group3 + " " + getResno() 
              + (chainID != 0 && chainID != ' ' ? " Chain: " + chainID : "")              
              : "")
          + " Model: " + getModelNumber()
          + " Coordinates: " + x + " " + y + " " + z
          + (pt == null ? "" : " Fractional: "  + pt.x + " " + pt.y + " " + pt.z); 
    }
    return getIdentityXYZ(true);
  }

  String getIdentityXYZ(boolean allInfo) {
    Point3f pt = (group.chain.model.isJmolDataFrame ? getFractionalCoord(false) : this);
    return getIdentity(allInfo) + " " + pt.x + " " + pt.y + " " + pt.z;  
  }
  
  String getIdentity(boolean allInfo) {
    StringBuffer info = new StringBuffer();
    String group3 = getGroup3(true);
    String seqcodeString = getSeqcodeString();
    char chainID = getChainID();
    if (group3 != null && group3.length() > 0) {
      info.append("[");
      info.append(group3);
      info.append("]");
    }
    if (seqcodeString != null)
      info.append(seqcodeString);
    if (chainID != 0 && chainID != ' ') {
      info.append(":");
      info.append(chainID);
    }
    if (!allInfo)
      return info.toString();
    if (info.length() > 0)
      info.append(".");
    info.append(getAtomName());
    if (info.length() == 0) {
      // since atomName cannot be null, this is unreachable
      info.append(getElementSymbol(false));
      info.append(" ");
      info.append(getAtomNumber());
    }
    if (alternateLocationID != 0) {
      info.append("%");
      info.append(alternateLocationID);
    }
    if (group.chain.modelSet.getModelCount() > 1) {
      info.append("/");
      info.append(getModelNumberForLabel());
    }
    info.append(" #");
    info.append(getAtomNumber());
    return info.toString();
  }

  public int getGroupIndex() {
    return group.getGroupIndex();
  }
  
  public String getGroup3(boolean allowNull) {
    String group3 = group.getGroup3();
    return (allowNull 
        || group3 != null && group3.length() > 0 
        ? group3 : "UNK");
  }

  public String getGroup1(char c0) {
    char c = group.getGroup1();
    return (c != '\0' ? "" + c : c0 != '\0' ? "" + c0 : "");
  }

  boolean isGroup3(String group3) {
    return group.isGroup3(group3);
  }

  public boolean isProtein() {
    return group.isProtein();
  }

  public boolean isCarbohydrate() {
    return group.isCarbohydrate();
  }

  public boolean isNucleic() {
    return group.isNucleic();
  }

  public boolean isDna() {
    return group.isDna();
  }
  
  public boolean isRna() {
    return group.isRna();
  }

  public boolean isPurine() {
    return group.isPurine();
  }

  public boolean isPyrimidine() {
    return group.isPyrimidine();
  }

  int getSeqcode() {
    return group.getSeqcode();
  }

  public int getResno() {
    return group.getResno();   
  }

  public boolean isClickable() {
    // certainly if it is not visible, then it can't be clickable
    if (!isVisible(0))
      return false;
    int flags = shapeVisibilityFlags | group.shapeVisibilityFlags;
    return ((flags & clickabilityFlags) != 0);
  }

  public int getClickabilityFlags() {
    return clickabilityFlags;
  }
  
  public void setClickable(int flag) {
    if (flag == 0)
      clickabilityFlags = 0;
    else
      clickabilityFlags |= flag;
  }
  
  /**
   * determine if an atom or its PDB group is visible
   * @param flags TODO
   * @return true if the atom is in the "select visible" set
   */
  public boolean isVisible(int flags) {
    // Is the atom's model visible? Is the atom NOT hidden?
    if (!isInFrame() || group.chain.modelSet.isAtomHidden(index))
      return false;
    // Is any shape associated with this atom visible?
    if (flags != 0)
      return (isShapeVisible(flags));  
    flags = shapeVisibilityFlags;
    // Is its PDB group visible in any way (cartoon, e.g.)?
    //  An atom is considered visible if its PDB group is visible, even
    //  if it does not show up itself as part of the structure
    //  (this will be a difference in terms of *clickability*).
    // except BACKBONE -- in which case we only see the lead atoms
    if (group.shapeVisibilityFlags != JmolConstants.BACKBONE_VISIBILITY_FLAG
        || isLeadAtom())
      flags |= group.shapeVisibilityFlags;

    // We know that (flags & AIM), so now we must remove that flag
    // and check to see if any others are remaining.
    // Only then is the atom considered visible.
    return ((flags & ~JmolConstants.ATOM_IN_FRAME) != 0);
  }

  public boolean isLeadAtom() {
    return group.isLeadAtom(index);
  }
  
  public float getGroupParameter(int tok) {
    return group.getGroupParameter(tok);
  }

  public char getChainID() {
    return group.chain.chainID;
  }

  public int getSurfaceDistance100() {
    return group.chain.modelSet.getSurfaceDistance100(index);
  }

  public Vector3f getVibrationVector() {
    return group.chain.modelSet.getVibrationVector(index, false);
  }

  public float getVibrationCoord(char ch) {
    return group.chain.modelSet.getVibrationCoord(index, ch);
  }


  public int getPolymerLength() {
    return group.getBioPolymerLength();
  }

  public int getPolymerIndexInModel() {
    return group.getBioPolymerIndexInModel();
  }

  public int getMonomerIndex() {
    return group.getMonomerIndex();
  }
  
  public int getSelectedGroupCountWithinChain() {
    return group.chain.getSelectedGroupCount();
  }

  public int getSelectedGroupIndexWithinChain() {
    return group.getSelectedGroupIndex();
  }

  public int getSelectedMonomerCountWithinPolymer() {
    return group.getSelectedMonomerCount();
  }

  public int getSelectedMonomerIndexWithinPolymer() {
    return group.getSelectedMonomerIndex();
  }

  Chain getChain() {
    return group.chain;
  }

  String getModelNumberForLabel() {
    return group.chain.modelSet.getModelNumberForAtomLabel(modelIndex);
  }
  
  public int getModelNumber() {
    return group.chain.modelSet.getModelNumber(modelIndex) % 1000000;
  }
  
  public int getModelFileIndex() {
    return group.chain.model.fileIndex;
  }
  
  public int getModelFileNumber() {
    return group.chain.modelSet.getModelFileNumber(modelIndex);
  }
  
  public String getGroupType() {
    return JmolConstants.getProteinStructureName(getProteinStructureType(), true);
  }
  
  public byte getProteinStructureType() {
    return group.getProteinStructureType();
  }
  
  public byte getProteinStructureSubType() {
    return group.getProteinStructureSubType();
  }
  
  public int getStrucNo() {
    return group.getStrucNo();
  }

  public String getStructureId() {
    return group.getStructureId();
  }

  public String getProteinStructureTag() {
    return group.getProteinStructureTag();
  }

  public short getGroupID() {
    return group.groupID;
  }

  String getSeqcodeString() {
    return group.getSeqcodeString();
  }

  int getSeqNumber() {
    return group.getSeqNumber();
  }

  public char getInsertionCode() {
    return group.getInsertionCode();
  }
  
  @Override
  public boolean equals(Object obj) {
    return (this == obj);
  }

  @Override
  public int hashCode() {
    //this overrides the Point3fi hashcode, which would
    //give a different hashcode for an atom depending upon
    //its screen location! Bug fix for 11.1.43 Bob Hanson
    return index;
  }
  
  public Atom findAromaticNeighbor(int notAtomIndex) {
    if (bonds == null)
      return null;
    for (int i = bonds.length; --i >= 0; ) {
      Bond bondT = bonds[i];
      Atom a = bondT.getOtherAtom(this);
      if (bondT.isAromatic() && a.index != notAtomIndex)
        return a;
    }
    return null;
  }

  /**
   * called by isosurface and int comparator via atomProperty()
   * and also by getBitsetProperty() 
   * 
   * @param atom
   * @param tokWhat
   * @return         int value or Integer.MIN_VALUE
   */
  public static int atomPropertyInt(Atom atom, int tokWhat) {
    switch (tokWhat) {
    case Token.atomno:
      return atom.getAtomNumber();
    case Token.atomid:
      return atom.atomID;
    case Token.atomindex:
      return atom.getIndex();
    case Token.bondcount:
      return atom.getCovalentBondCount();
    case Token.color:
      return atom.group.chain.modelSet.viewer.getColorArgbOrGray(atom.getColix());
    case Token.element:
    case Token.elemno:
      return atom.getElementNumber();
    case Token.file:
      return atom.getModelFileIndex() + 1;
    case Token.formalcharge:
      return atom.getFormalCharge();
    case Token.groupid:
      return atom.getGroupID(); //-1 if no group
    case Token.groupindex:
      return atom.getGroupIndex(); 
    case Token.model:
      //integer model number -- could be PDB/sequential adapter number
      //or it could be a sequential model in file number when multiple files
      return atom.getModelNumber();
    case -Token.model:
      //float is handled differently
      return atom.getModelFileNumber();
    case Token.modelindex:
      return atom.modelIndex;
    case Token.molecule:
      return atom.getMoleculeNumber();
    case Token.occupancy:
      return atom.getOccupancy100();
    case Token.polymer:
      return atom.getGroup().getBioPolymerIndexInModel() + 1;
    case Token.polymerlength:
      return atom.getPolymerLength();
    case Token.radius:
      // the comparator uses rasmol radius, unfortunately, for integers
      return atom.getRasMolRadius();        
    case Token.resno:
      return atom.getResno();
    case Token.site:
      return atom.getAtomSite();
    case Token.structure:
      return atom.getProteinStructureType();
    case Token.substructure:
      return atom.getProteinStructureSubType();
    case Token.strucno:
      return atom.getStrucNo();
    case Token.valence:
      return atom.getValence();
    }
    return 0;      
  }

  /**
   * called by isosurface and int comparator via atomProperty()
   * and also by getBitsetProperty() 
   * @param viewer 
   * 
   * @param atom
   * @param tokWhat
   * @return       float value or value*100 (asInt=true) or throw an error if not found
   * 
   */  
  public static float atomPropertyFloat(Viewer viewer, Atom atom, int tokWhat) {

    switch (tokWhat) {
    case Token.radius:
      return atom.getRadius();
    case Token.selected:
      return (viewer.isAtomSelected(atom.index) ? 1 : 0);
    case Token.surfacedistance:
      atom.group.chain.modelSet.getSurfaceDistanceMax();
      return atom.getSurfaceDistance100() / 100f;
    case Token.temperature: // 0 - 9999
      return atom.getBfactor100() / 100f;
    case Token.volume:
      return atom.getVolume(viewer, JmolConstants.VDW_AUTO);

    // these next have to be multiplied by 100 if being compared
    // note that spacefill here is slightly different than radius -- no integer option
      
    case Token.adpmax:
      return atom.getADPMinMax(true);
    case Token.adpmin:
      return atom.getADPMinMax(false);
    case Token.atomx:
    case Token.x:
      return atom.x;
    case Token.atomy:
    case Token.y:
      return atom.y;
    case Token.atomz:
    case Token.z:
      return atom.z;
    case Token.covalent:
      return atom.getCovalentRadiusFloat();
    case Token.fracx:
      return atom.getFractionalCoord('X', true);
    case Token.fracy:
      return atom.getFractionalCoord('Y', true);
    case Token.fracz:
      return atom.getFractionalCoord('Z', true);
    case Token.fux:
      return atom.getFractionalCoord('X', false);
    case Token.fuy:
      return atom.getFractionalCoord('Y', false);
    case Token.fuz:
      return atom.getFractionalCoord('Z', false);
    case Token.ionic:
      return atom.getBondingRadiusFloat();
    case Token.mass:
      return atom.getMass();
    case Token.occupancy:
      return atom.getOccupancy100() / 100f;
    case Token.partialcharge:
      return atom.getPartialCharge();
    case Token.omega:
    case Token.phi:
    case Token.psi:
    case Token.eta:
    case Token.theta:
    case Token.straightness:
      return atom.getGroupParameter(tokWhat);
    case Token.spacefill:
      return atom.getRadius();
    case Token.backbone:
    case Token.cartoon:
    case Token.dots:
    case Token.ellipsoid:
    case Token.geosurface:
    case Token.halo:
    case Token.meshRibbon:
    case Token.ribbon:
    case Token.rocket:
    case Token.star:
    case Token.strands:
    case Token.trace:
      return viewer.getAtomShapeValue(tokWhat, atom.group, atom.index);
    case Token.unitx:
      return atom.getFractionalUnitCoord('X');
    case Token.unity:
      return atom.getFractionalUnitCoord('Y');
    case Token.unitz:
      return atom.getFractionalUnitCoord('Z');
    case Token.vanderwaals:
      return atom.getVanderwaalsRadiusFloat(viewer, JmolConstants.VDW_AUTO);
    case Token.vibx:
      return atom.getVibrationCoord('X');
    case Token.viby:
      return atom.getVibrationCoord('Y');
    case Token.vibz:
      return atom.getVibrationCoord('Z');
    }
    return atomPropertyInt(atom, tokWhat);
  }

  private float getMass() {
    float mass = getIsotopeNumber();
    return (mass > 0 ? mass : Elements.getAtomicMass(getElementNumber()));
  }

  public static String atomPropertyString(Viewer viewer, Atom atom, int tokWhat) {
    char ch;
    switch (tokWhat) {
    case Token.altloc:
      ch = atom.getAlternateLocationID();
      return (ch == '\0' ? "" : "" + ch);
    case Token.atomname:
      return atom.getAtomName();
    case Token.atomtype:
      return atom.getAtomType();
    case Token.chain:
      ch = atom.getChainID();
      return (ch == '\0' ? "" : "" + ch);
    case Token.sequence:
      return atom.getGroup1('?');
    case Token.group1:
      return atom.getGroup1('\0');
    case Token.group:
      return atom.getGroup3(false);
    case Token.element:
      return atom.getElementSymbol(true);
    case Token.identify:
      return atom.getIdentity(true);
    case Token.insertion:
      ch = atom.getInsertionCode();
      return (ch == '\0' ? "" : "" + ch);
    case Token.label:
    case Token.format:
      String s = atom.group.chain.modelSet.getAtomLabel(atom.getIndex());
      if (s == null)
        s = "";
      return s;
    case Token.structure:
      return JmolConstants.getProteinStructureName(atom.getProteinStructureType(), false);
    case Token.substructure:
      return JmolConstants.getProteinStructureName(atom.getProteinStructureSubType(), false);
    case Token.strucid:
      return atom.getStructureId();
    case Token.shape:
      return viewer.getHybridizationAndAxes(atom.index, null, null, "d");
    case Token.symbol:
      return atom.getElementSymbol(false);
    case Token.symmetry:
      return atom.getSymmetryOperatorList();
    }
    return ""; 
  }

  public static Tuple3f atomPropertyTuple(Atom atom, int tok) {
    switch (tok) {
    case Token.fracxyz:
      return atom.getFractionalCoord(!atom.group.chain.model.isJmolDataFrame);
    case Token.fuxyz:
      return atom.getFractionalCoord(false);
    case Token.unitxyz:
      return (atom.group.chain.model.isJmolDataFrame ? atom.getFractionalCoord(false) 
          : atom.getFractionalUnitCoord(false));
    case Token.vibxyz:
      Vector3f v = atom.getVibrationVector();
      if (v == null)
        v = new Vector3f();
      return v;
    case Token.xyz:
      return atom;
    case Token.color:
      return Graphics3D.colorPointFromInt2(
          atom.group.chain.modelSet.viewer.getColorArgbOrGray(atom.getColix())
          );
    }
    return null;
  }

  boolean isWithinStructure(byte type) {
    return group.isWithinStructure(type);
  }
  
  public int getOffsetResidueAtom(String name, int offset) {
    return group.chain.modelSet.getGroupAtom(this, offset, name);
  }
  
  public boolean isCrossLinked(JmolNode node) {
    return group.isCrossLinked(((Atom) node).getGroup());
  }

  public boolean getCrossLinkLeadAtomIndexes(List<Integer> vReturn) {
    return group.getCrossLinkLeadAtomIndexes(vReturn);
  }
  
  @Override
  public String toString() {
    return getInfo();
  }

}
