/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2010-12-26 05:18:20 -0800 (Sun, 26 Dec 2010) $
 * $Revision: 14901 $
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

import org.jmol.api.JmolEdge;
import org.jmol.api.JmolNode;

/**
 * Bond in a SmilesMolecule
 */
public class SmilesBond implements JmolEdge {

  // Bond orders
  public final static int TYPE_UNKNOWN = -1;
  public final static int TYPE_NONE = 0;
  public final static int TYPE_SINGLE = 1;
  public final static int TYPE_DOUBLE = 2;
  public final static int TYPE_TRIPLE = 3;
  public final static int TYPE_AROMATIC = 0x11;
  public final static int TYPE_DIRECTIONAL_1 = 0x101;
  public final static int TYPE_DIRECTIONAL_2 = 0x201;
  public final static int TYPE_ATROPISOMER_1 = 0x301;
  public final static int TYPE_ATROPISOMER_2 = 0x401;
  public final static int TYPE_RING = 0x41;
  public final static int TYPE_ANY = 0x51;
  public final static int TYPE_BIO_SEQUENCE = 0x60;
  public final static int TYPE_BIO_PAIR = 0x70;
  public final static int TYPE_MULTIPLE = 999;


  static String getBondOrderString(int order) {
    switch (order) {
    case 1:
      return "";
    case 2:
      return "=";
    case 3:
      return "#";
    default:
      return "";
    }
  }

  private SmilesAtom atom1;
  private SmilesAtom atom2;

  int bondType;
  int index = -1;

  public int getIndex() {
    return index;
  }

  boolean isNot;
  JmolEdge matchingBond;

  public SmilesBond[] primitives;
  public int nPrimitives;
  public SmilesBond[] bondsOr;
  public int nBondsOr;

  public void set(SmilesBond bond) {
    // not the atoms.
    bondType = bond.bondType;
    isNot = bond.isNot;
    primitives = bond.primitives;
    nPrimitives = bond.nPrimitives;
    bondsOr = bond.bondsOr;
    nBondsOr = bond.nBondsOr;
  }

  public SmilesBond addBondOr() {
    if (bondsOr == null)
      bondsOr = new SmilesBond[2];
    if (nBondsOr >= bondsOr.length) {
      SmilesBond[] tmp = new SmilesBond[bondsOr.length * 2];
      System.arraycopy(bondsOr, 0, tmp, 0, bondsOr.length);
      bondsOr = tmp;
    }
    SmilesBond sBond = new SmilesBond(TYPE_UNKNOWN, false);
    bondsOr[nBondsOr] = sBond;
    nBondsOr++;
    return sBond;
  }

  public SmilesBond addPrimitive() {
    if (primitives == null)
      primitives = new SmilesBond[2];
    if (nPrimitives >= primitives.length) {
      SmilesBond[] tmp = new SmilesBond[primitives.length * 2];
      System.arraycopy(primitives, 0, tmp, 0, primitives.length);
      primitives = tmp;
    }
    SmilesBond sBond = new SmilesBond(TYPE_UNKNOWN, false);
    primitives[nPrimitives] = sBond;
    nPrimitives++;
    return sBond;
  }

  @Override
  public String toString() {
    return atom1 + " -" + (isNot ? "!" : "") + bondType + "- " + atom2;
  }

  /**
   * SmilesBond constructor
   * 
   * @param atom1 First atom
   * @param atom2 Second atom
   * @param bondType Bond type
   * @param isNot 
   */
  public SmilesBond(SmilesAtom atom1, SmilesAtom atom2, int bondType,
      boolean isNot) {
    set(atom1, atom2);
    set(bondType, isNot);
  }

  SmilesBond(int bondType, boolean isNot) {
    set(bondType, isNot);
  }

  void set(int bondType, boolean isNot) {
    this.bondType = bondType;
    this.isNot = isNot;
  }

  void set(SmilesAtom atom1, SmilesAtom atom2) {
    if (atom1 != null) {
      this.atom1 = atom1;
      atom1.addBond(this);
    }
    if (atom2 != null) {
      this.atom2 = atom2;
      atom2.isFirst = false;
      atom2.addBond(this);
    }
  }

  static boolean isBondType(char ch, boolean isSearch, boolean isBioSequence)
      throws InvalidSmilesException {
    if ("-=#:/\\.+!,&;@~^'".indexOf(ch) < 0)
      return false;
    if (!isSearch && "-=#:/\\.~^'".indexOf(ch) < 0) // ~ here for BIOSMARTS
      throw new InvalidSmilesException("SMARTS bond type " + ch
          + " not allowed in SMILES");
    if(isBioSequence && ch == '~')
      return false;
    return true;
  }

  /**
   * @param code Bond code
   * @return Bond type
   */
  public static int getBondTypeFromCode(char code) {
    switch (code) {
    case '.':
      return TYPE_NONE;
    case '-':
      return TYPE_SINGLE;
    case '=':
      return TYPE_DOUBLE;
    case '#':
      return TYPE_TRIPLE;
    case ':':
      return TYPE_AROMATIC;
    case '/':
      return TYPE_DIRECTIONAL_1;
    case '\\':
      return TYPE_DIRECTIONAL_2;
    case '^':
      return TYPE_ATROPISOMER_1;
    case '\'':
      return TYPE_ATROPISOMER_2;
    case '@':
      return TYPE_RING;
    case '~':
      return TYPE_ANY;
    case '+':
      return TYPE_BIO_SEQUENCE;
    }
    return TYPE_UNKNOWN;
  }

  public SmilesAtom getAtom1() {
    return atom1;
  }

  public SmilesAtom getAtom2() {
    return atom2;
  }

  void setAtom2(SmilesAtom atom) {
    this.atom2 = atom;
    if (atom2 != null) {
      // NO! could be after . as in .[C@H]12      atom2.isFirst = false;
      atom.addBond(this);
    }
  }

  public int getBondType() {
    return bondType;
  }

  public SmilesAtom getOtherAtom(SmilesAtom a) {
    return (atom1 == a ? atom2 : atom1);
  }

  public int getAtomIndex1() {
    return atom1.index;
  }

  public int getAtomIndex2() {
    return atom2.index;
  }

  public int getCovalentOrder() {
    return bondType;
  }

  public int getOrder() {
    return bondType;
  }

  public JmolNode getOtherAtom(JmolNode atom) {
    return (atom == atom1 ? atom2 : atom == atom2 ? atom1 : null);
  }

  public boolean isCovalent() {
    return bondType != TYPE_BIO_PAIR;
  }

  public int getValence() {
    return (bondType & 7);
  }

  public boolean isHydrogen() {
    return bondType == TYPE_BIO_PAIR;
  }

  void switchAtoms() {
    SmilesAtom a = atom1;
    atom1 = atom2;
    atom2 = a;
    switch (bondType) {
    case TYPE_ATROPISOMER_1:
      bondType = TYPE_ATROPISOMER_2;
      break;
    case TYPE_ATROPISOMER_2:
      bondType = TYPE_ATROPISOMER_1;
      break;
    case TYPE_DIRECTIONAL_1:
      bondType = TYPE_DIRECTIONAL_2;
      break;
    case TYPE_DIRECTIONAL_2:
      bondType = TYPE_DIRECTIONAL_1;
      break;
    }
  }

}
