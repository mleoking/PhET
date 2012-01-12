/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2011-02-14 10:10:27 -0800 (Mon, 14 Feb 2011) $
 * $Revision: 15166 $
 *
 * Copyright (C) 2005  The Jmol Development Team
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

package org.jmol.smiles;

import java.util.BitSet;

import java.util.List;

import javax.vecmath.Point3f;

import org.jmol.api.JmolEdge;
import org.jmol.api.JmolNode;
import org.jmol.util.Elements;
import org.jmol.util.Logger;

//import org.jmol.util.Logger;

/**
 * This class represents an atom in a <code>SmilesMolecule</code>.
 */
public class SmilesAtom extends Point3f implements JmolNode {

  final static int STEREOCHEMISTRY_DEFAULT = 0;
  final static int STEREOCHEMISTRY_ALLENE = 2;
  //  final static int STEREOCHEMISTRY_DOUBLE_BOND = 3;
  final static int STEREOCHEMISTRY_TETRAHEDRAL = 4;
  final static int STEREOCHEMISTRY_TRIGONAL_BIPYRAMIDAL = 5;
  final static int STEREOCHEMISTRY_OCTAHEDRAL = 6;
  final static int STEREOCHEMISTRY_SQUARE_PLANAR = 8;

  static int getChiralityClass(String xx) {
    return ("0;11;AL;33;TH;TP;OH;77;SP;".indexOf(xx) + 1) / 3;
  }

  static final String UNBRACKETED_SET = "B, C, N, O, P, S, F, Cl, Br, I,";

  static boolean allowSmilesUnbracketed(String xx) {
    return (UNBRACKETED_SET.indexOf(xx + ",") >= 0);
  }

  int index;
  String atomName;
  String residueName;
  String residueChar;
  boolean isBioAtom;
  char bioType; //* p n r d 
  boolean isLeadAtom;
  int notBondedIndex = -1;
  boolean notCrossLinked;

  void setBioAtom(char bioType) {
    isBioAtom = (bioType != '\0');
    this.bioType = bioType;
    if (parent != null) {
      parent.bioType = bioType;
      parent.isBioAtom = isBioAtom;
    }
  }

  void setAtomName(String name) {
    if (name == null)
      return;
    if (name.length() > 0)
      atomName = name;
    if (name.equals("0"))
      isLeadAtom = true;
    // ensure that search does not skip groups
    if (parent != null) {
      parent.atomName = name;
    }
  }

  boolean not;
  boolean selected;
  boolean hasSymbol;
  boolean isFirst = true;

  int jmolIndex = -1;
  short elementNumber = -2; // UNDEFINED (could be A or a or *)

  private short atomicMass = Short.MIN_VALUE;
  private int charge;
  int missingHydrogenCount = Integer.MIN_VALUE;
  int implicitHydrogenCount = Integer.MIN_VALUE;
  private int matchingAtom = -1;
  private int chiralClass = Integer.MIN_VALUE;
  private int chiralOrder = Integer.MIN_VALUE;
  private boolean isAromatic;
  SmilesAtom parent;
  SmilesBond[] bonds = new SmilesBond[4];
  int bondCount;

  public void setBonds(SmilesBond[] bonds) {
    this.bonds = bonds;
  }

  int iNested = 0;

  SmilesAtom[] atomsOr;
  int nAtomsOr;

  SmilesAtom[] primitives;
  int nPrimitives;

  public SmilesAtom addAtomOr() {
    if (atomsOr == null)
      atomsOr = new SmilesAtom[2];
    if (nAtomsOr >= atomsOr.length) {
      SmilesAtom[] tmp = new SmilesAtom[atomsOr.length * 2];
      System.arraycopy(atomsOr, 0, tmp, 0, atomsOr.length);
      atomsOr = tmp;
    }
    SmilesAtom sAtom = new SmilesAtom(index);
    sAtom.parent = this;
    atomsOr[nAtomsOr] = sAtom;
    nAtomsOr++;
    return sAtom;
  }

  public SmilesAtom addPrimitive() {
    if (primitives == null)
      primitives = new SmilesAtom[2];
    if (nPrimitives >= primitives.length) {
      SmilesAtom[] tmp = new SmilesAtom[primitives.length * 2];
      System.arraycopy(primitives, 0, tmp, 0, primitives.length);
      primitives = tmp;
    }
    SmilesAtom sAtom = new SmilesAtom(index);
    sAtom.parent = this;
    primitives[nPrimitives] = sAtom;
    setSymbol("*");
    hasSymbol = false;
    nPrimitives++;
    return sAtom;
  }

  @Override
  public String toString() {
    String s = (residueChar != null || residueName != null ? (residueChar == null ? residueName
        : residueChar)
        + "." + atomName
        : elementNumber == -1 ? "A" : elementNumber == -2 ? "*" : Elements
            .elementSymbolFromNumber(elementNumber));
    if (isAromatic)
      s = s.toLowerCase();
    return "[" + s + '.' + index
        + (matchingAtom >= 0 ? "(" + matchingAtom + ")" : "")
        //    + " ch:" + charge 
        //    + " ar:" + isAromatic 
        //    + " H:" + explicitHydrogenCount
        //    + " h:" + implicitHydrogenCount
        + "]";
  }

  /**
   * Constructs a <code>SmilesAtom</code>.
   * 
   * @param index Atom number in the molecule. 
   */
  public SmilesAtom(int index) {
    this.index = index;
  }

  int component;
  int atomSite;
  int degree = -1;
  int nonhydrogenDegree = -1;
  int valence = 0;
  int connectivity = -1;
  int ringMembership = Integer.MIN_VALUE;
  int ringSize = Integer.MIN_VALUE;
  int ringConnectivity = -1;

  public SmilesAtom(int iComponent, int ptAtom, int flags, short atomicNumber,
      int charge) {
    component = iComponent;
    index = ptAtom;
    this.atomSite = flags;
    this.elementNumber = atomicNumber;
    this.charge = charge;
  }

  /**
   * Finalizes the hydrogen count hydrogens in a <code>SmilesMolecule</code>.
   * "missing" here means the number of atoms not present in the SMILES string
   * for unbracketed atoms or the number of hydrogen atoms "CC" being really CH3CH3
   * or explicitly mentioned in the bracketed atom, "[CH2]". These hydrogen atoms
   * are not part of the topological model constructed and need to be accounted for. 
   * 
   * @param molecule Molecule containing the atom.
   * @return false if inappropriate
   */
  public boolean setHydrogenCount(SmilesSearch molecule) {
    // only called for SMILES search -- simple C or [C]
    if (missingHydrogenCount != Integer.MIN_VALUE)
      return true;
    // Determining max count
    int count = getDefaultCount(elementNumber, isAromatic);
    if (count == -2)
      return false;
    if (count == -1)
      return true;

    if (elementNumber == 7 && isAromatic && bondCount == 2) {
      // is it -N= or -NH- ? 
      if (bonds[0].bondType == SmilesBond.TYPE_SINGLE
           && bonds[1].bondType == SmilesBond.TYPE_SINGLE)
        count++;
    }
    for (int i = 0; i < bondCount; i++) {
      SmilesBond bond = bonds[i];
      switch (bond.bondType) {
      case SmilesBond.TYPE_ANY: // for aromatics
        if (elementNumber == 7) {
          Logger.info("Ambiguous bonding to aromatic N found -- MF may be in error");
        }
        count -= 1;
        break;
      case SmilesBond.TYPE_SINGLE:
      case SmilesBond.TYPE_DIRECTIONAL_1:
      case SmilesBond.TYPE_DIRECTIONAL_2:
        count -= 1;
        break;
      case SmilesBond.TYPE_DOUBLE:
        count -= (isAromatic && elementNumber == 6 ? 1 : 2);
        break;
      case SmilesBond.TYPE_TRIPLE:
        count -= 3;
        break;
      }
    }

    if (count > 0)
      missingHydrogenCount = count;
    return true;
  }

  static int getDefaultCount(int elementNumber, boolean isAromatic) {
    // not a complete set...
    // B, C, N, O, P, S, F, Cl, Br, and I
    // B (3), C (4), N (3,5), O (2), P (3,5), S (2,4,6), and 1 for the halogens

    switch (elementNumber) {
    case 0:
    case -1: // A a
      return -1;
    case 6: // C
      return (isAromatic ? 3 : 4);
    case 8: // O
    case 16: // S
      return 2;
    case 7: // N
      // note -- it is necessary to indicate explicitly
      // single bonds to aromatic n if a proper MF is desired
      return (isAromatic ? 2 : 3);
    case 5: // B
    case 15: // P
      return 3;
    case 9: // F
    case 17: // Cl
    case 35: // Br
    case 53: // I
      return 1;
    }
    return -2;
  }

  /**
   * Returns the atom index of the atom.
   * 
   * @return Atom index.
   */
  public int getIndex() {
    return index;
  }

  /**
   * 
   * @return whether symbol was lower case
   */
  public boolean isAromatic() {
    return isAromatic;
  }

  /**
   * Sets the symbol of the atm.
   * 
   * @param symbol Atom symbol.
   * @return  false if invalid symbol
   */
  public boolean setSymbol(String symbol) {
    isAromatic = symbol.equals(symbol.toLowerCase());
    hasSymbol = true;
    if (symbol.equals("*")) {
      isAromatic = false;
      elementNumber = -2;
      return true;
    }
    if (symbol.equals("a") || symbol.equals("A")) {
      elementNumber = -1;
      return true;
    }
    if (symbol.equals("Xx")) {
      elementNumber = 0;
      return true;
    }

    if (isAromatic)
      symbol = symbol.substring(0, 1).toUpperCase()
          + (symbol.length() == 1 ? "" : symbol.substring(1));
    elementNumber = Elements.elementNumberFromSymbol(symbol, true);
    return (elementNumber != 0);
  }

  /**
   *  Returns the atomic number of the element or 0
   * 
   * @return atomicNumber
   */
  public short getElementNumber() {
    return elementNumber;
  }

  /**
   * Returns the atomic mass of the atom.
   * 
   * @return Atomic mass.
   */
  public short getAtomicMass() {
    return atomicMass;
  }

  /**
   * Sets the atomic mass of the atom.
   * 
   * @param mass Atomic mass.
   */
  public void setAtomicMass(int mass) {
    atomicMass = (short) mass;
  }

  /**
   * Returns the charge of the atom.
   * 
   * @return Charge.
   */
  public int getCharge() {
    return charge;
  }

  /**
   * Sets the charge of the atom.
   * 
   * @param charge Charge.
   */
  public void setCharge(int charge) {
    this.charge = charge;
  }

  /**
   * Returns the number of a matching atom in a molecule.
   * This value is temporary, it is used during the pattern matching algorithm.
   * 
   * @return matching atom.
   */
  public int getMatchingAtom() {
    return matchingAtom;
  }

  /**
   * Sets the number of a matching atom in a molecule.
   * This value is temporary, it is used during the pattern matching algorithm.
   * 
   * @param atom Temporary: number of a matching atom in a molecule.
   */
  public void setMatchingAtom(int atom) {
    matchingAtom = atom;
  }

  /**
   * Returns the chiral class of the atom.
   * (see <code>CHIRALITY_...</code> constants)
   * 
   * @return Chiral class.
   */
  public int getChiralClass() {
    return chiralClass;
  }

  /**
   * Sets the chiral class of the atom.
   * (see <code>CHIRALITY_...</code> constants)
   * 
   * @param chiralClass Chiral class.
   */
  public void setChiralClass(int chiralClass) {
    this.chiralClass = chiralClass;
  }

  /**
   * Returns the chiral order of the atom.
   * 
   * @return Chiral order.
   */
  public int getChiralOrder() {
    return chiralOrder;
  }

  /**
   * Sets the chiral order of the atom.
   * 
   * @param chiralOrder Chiral order.
   */
  public void setChiralOrder(int chiralOrder) {
    this.chiralOrder = chiralOrder;
  }

  /**
   * Sets the number of explicit hydrogen atoms bonded with this atom.
   * 
   * @param count Number of hydrogen atoms.
   */
  public void setExplicitHydrogenCount(int count) {
    missingHydrogenCount = count;
  }

  /**
   * Sets the number of implicit hydrogen atoms bonded with this atom.
   * 
   * @param count Number of hydrogen atoms.
   */
  public void setImplicitHydrogenCount(int count) {
    implicitHydrogenCount = count;
  }

  public void setDegree(int degree) {
    this.degree = degree;
  }

  public void setNonhydrogenDegree(int degree) {
    nonhydrogenDegree = degree;
  }

  public void setValence(int valence) {
    this.valence = valence;
  }

  public void setConnectivity(int connectivity) {
    this.connectivity = connectivity;
  }

  public void setRingMembership(int rm) {
    ringMembership = rm;
  }

  public void setRingSize(int rs) {
    ringSize = rs;
  }

  public void setRingConnectivity(int rc) {
    ringConnectivity = rc;
  }

  public int getModelIndex() {
    return component;
  }

  public int getAtomSite() {
    return atomSite;
  }

  public int getImplicitHydrogenCount() {
    // searching a SMILES string all H atoms will 
    // be explicitly defined
    return 0;
  }

  public int getExplicitHydrogenCount() {
    return missingHydrogenCount;
  }

  public int getFormalCharge() {
    return charge;
  }

  public short getIsotopeNumber() {
    return atomicMass;
  }

  public short getAtomicAndIsotopeNumber() {
    return Elements.getAtomicAndIsotopeNumber(elementNumber, atomicMass);
  }

  public String getAtomName() {
    return atomName == null ? "" : atomName;
  }

  public String getGroup3(boolean allowNull) {
    return residueName == null ? "" : residueName;
  }

  public String getGroup1(char c0) {
    return residueChar == null ? "" : residueChar;
  }

  /**
   * Add a bond to the atom.
   * 
   * @param bond Bond to add.
   */
  void addBond(SmilesBond bond) {
    if (bondCount >= bonds.length) {
      SmilesBond[] tmp = new SmilesBond[bonds.length * 2];
      System.arraycopy(bonds, 0, tmp, 0, bonds.length);
      bonds = tmp;
    }
    //if (Logger.debugging)
    //Logger.debug("adding bond to " + this + ": " + bond.getAtom1() + " " + bond.getAtom2());
    bonds[bondCount] = bond;
    bondCount++;
  }

  public void setBondArray() {
    if (bonds.length > bondCount) {
      SmilesBond[] tmp = new SmilesBond[bondCount];
      System.arraycopy(bonds, 0, tmp, 0, bondCount);
      bonds = tmp;
    }
    if (atomsOr != null && atomsOr.length > nAtomsOr) {
      SmilesAtom[] tmp = new SmilesAtom[atomsOr.length];
      System.arraycopy(atomsOr, 0, tmp, 0, nAtomsOr);
      atomsOr = tmp;
    }
    if (primitives != null && primitives.length > nPrimitives) {
      SmilesAtom[] tmp = new SmilesAtom[primitives.length];
      System.arraycopy(primitives, 0, tmp, 0, nPrimitives);
      primitives = tmp;
    }
    for (int i = 0; i < bonds.length; i++) {
      if (isBioAtom && bonds[i].bondType == SmilesBond.TYPE_AROMATIC)
        bonds[i].bondType = SmilesBond.TYPE_BIO_PAIR;
      if (bonds[i].getAtom1().index > bonds[i].getAtom2().index) {
        // it is possible, particularly for a connection to a an atom 
        // with a branch:   C(CCCN1)1
        // for the second assigned atom to not have the
        // higher index. That would prevent SmilesParser
        // from checking bonds. (atom 1 in this case, for 
        // example, would be the second atom in a bond for
        // which the first atom (N) would not yet be assigned.
        bonds[i].switchAtoms();
      }
    }
  }

  public JmolEdge[] getEdges() {
    return (parent != null ? parent.getEdges() : bonds);
  }

  /**
   * Returns the bond at index <code>number</code>.
   * 
   * @param number Bond number.
   * @return Bond.
   */
  public SmilesBond getBond(int number) {
    return (parent != null ? parent.getBond(number) : number >= 0
        && number < bondCount ? bonds[number] : null);
  }

  /**
   * Returns the number of bonds of this atom.
   * 
   * @return Number of bonds.
   */
  public int getCovalentBondCount() {
    return getBondCount();
  }

  public int getBondCount() {
    return (parent != null ? parent.getCovalentBondCount() : bondCount);
  }

  public int getMatchingBondedAtom(int i) {
    if (parent != null)
      return parent.getMatchingBondedAtom(i);
    if (i >= bondCount)
      return -1;
    SmilesBond b = bonds[i];
    return (b.getAtom1() == this ? b.getAtom2() : b.getAtom1()).matchingAtom;
  }

  public int getBondedAtomIndex(int j) {
    return (parent != null ? parent.getBondedAtomIndex(j) : bonds[j]
        .getOtherAtom(this).index);
  }

  private int covalentHydrogenCount = -1;
  
  public int getCovalentHydrogenCount() {
    if (covalentHydrogenCount >= 0)
      return covalentHydrogenCount;
    if (parent != null)
      return parent.getCovalentHydrogenCount();
    covalentHydrogenCount = 0;
    for (int k = 0; k < bonds.length; k++)
      if (bonds[k].getOtherAtom(this).elementNumber == 1)
        covalentHydrogenCount++;
    return covalentHydrogenCount;
  }

  public int getValence() {
    if (parent != null)
      return parent.getValence();
    int n = valence;
    if (n <= 0 && bonds != null)
      for (int i = bonds.length; --i >= 0;)
        n += bonds[i].getValence();
    valence = n;
    return n;
  }

  /**
   * if atom is null, return bond TO this atom (bond.getAtom2() == this)
   * otherwise, return bond connecting this atom with
   * that atom
   *  
   * @param atom
   * @return  bond
   */
  SmilesBond getBondTo(SmilesAtom atom) {
    if (parent != null)
      return parent.getBondTo(atom);
    SmilesBond bond;
    for (int k = 0; k < bonds.length; k++) {
      if ((bond = bonds[k]) == null)
        continue;
      if (atom == null ? bond.getAtom2() == this 
          : bond.getOtherAtom(this) == atom)
        return bond;
    }
    return null;
  }

  SmilesBond getBondNotTo(SmilesAtom atom, boolean allowH) {
    SmilesBond bond;
    for (int k = 0; k < bonds.length; k++) {
      if ((bond = bonds[k]) == null)
        continue;
      SmilesAtom atom2 = bond.getOtherAtom(this);
      if (atom != atom2 && (allowH || atom2.elementNumber != 1))
        return bond;
    }
    return null;
  }

  public boolean isLeadAtom() {
    return isLeadAtom;
  }

  public int getOffsetResidueAtom(String name, int offset) {
    if (isBioAtom)
      for (int k = 0; k < bonds.length; k++)
        if (bonds[k].getAtomIndex1() == index
            && bonds[k].bondType == SmilesBond.TYPE_BIO_SEQUENCE)
          return bonds[k].getOtherAtom(this).index;
    return -1;
  }

  public void setGroupBits(BitSet bs) {
    bs.set(index);
    return;
  }

  public boolean isCrossLinked(JmolNode node) {
    SmilesBond bond = getBondTo((SmilesAtom) node);
    return bond.isHydrogen();
  }

  public boolean getCrossLinkLeadAtomIndexes(List<Integer> vLinks) {
    for (int k = 0; k < bonds.length; k++)
      if (bonds[k].bondType == SmilesBond.TYPE_BIO_PAIR)
        vLinks.add(Integer.valueOf(bonds[k].getOtherAtom(this).index));
    return true;
  }

  public String getGroupType() {
    return null;
  }

  public int getResno() {
    return 0;
  }

  public char getChainID() {
    return '\0';
  }

  static String getAtomLabel(int atomicNumber, int isotopeNumber, int valence,
                             int charge, int nH, boolean isAromatic,
                             String stereo) {
    String sym = Elements.elementSymbolFromNumber(atomicNumber);
    if (isAromatic) {
      sym = sym.toLowerCase();
      if (atomicNumber != 6)
        valence = Integer.MAX_VALUE; // force [n]
    }
    int count = (stereo.length() > 0 || isotopeNumber != 0 || charge != 0 ? -1
        : getDefaultCount(atomicNumber, false));
    return (count == valence ? sym : "["
        + (isotopeNumber <= 0 ? "" : "" + isotopeNumber) + sym
        + (charge < 0 ? "" + charge : charge > 0 ? "+" + charge : "") + stereo
        + (nH > 1 ? "H" + nH : nH == 1 ? "H" : "") + "]");
  }

  public boolean isDna() {
    return bioType == 'd';
  }

  public boolean isRna() {
    return bioType == 'r';
  }

  public boolean isNucleic() {
    return bioType == 'n' || bioType == 'r' || bioType == 'd';
  }

  public boolean isProtein() {
    return bioType == 'p';
  }

  public boolean isPurine() {
    return residueChar != null && isNucleic() && "AG".indexOf(residueChar) >= 0;
  }

  public boolean isPyrimidine() {
    return residueChar != null && isNucleic()
        && "CTUI".indexOf(residueChar) >= 0;
  }

  public boolean isDeleted() {
    return false;
  }

}
