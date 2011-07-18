/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2011-01-16 20:27:43 -0800 (Sun, 16 Jan 2011) $
 * $Revision: 14982 $
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
package org.jmol.modelset;


import org.jmol.util.ArrayUtil;
import org.jmol.util.Logger;
import org.jmol.util.Quaternion;
import org.jmol.viewer.JmolConstants;
import org.jmol.script.Token;

import java.util.Hashtable;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class Group {

  private int groupIndex;
  
  public int getGroupIndex() {
    return groupIndex;
  }
  
  public void setGroupIndex(int groupIndex) {
    this.groupIndex = groupIndex;
  }

  protected Chain chain;
  int seqcode;
  protected short groupID;
  protected boolean isAmino;
  int selectedIndex;
  public int firstAtomIndex = -1;
  public int leadAtomIndex = -1;
  public int lastAtomIndex;
  private final static int SEQUENCE_NUMBER_FLAG = 0x80;
  private final static int INSERTION_CODE_MASK = 0x7F; //7-bit character codes, please!
  private final static int SEQUENCE_NUMBER_SHIFT = 8;
  
  public int shapeVisibilityFlags = 0;
  
  private float phi = Float.NaN;
  private float psi = Float.NaN;
  private float omega = Float.NaN;
  private float straightness = Float.NaN;
  private float mu = Float.NaN;
  private float theta = Float.NaN;
  
  protected boolean calcBioParameters() {
    return false;
  }

  public boolean haveParameters() {
    return true;
  }
  
  public void setGroupParameter(int tok, float f) {
    switch (tok) {
    case Token.phi:
      phi = f;
      break;
    case Token.psi:
      psi = f;
      break;
    case Token.omega:
      omega = f;
      break;
    case Token.eta:
      mu = f;
      break;
    case Token.theta:
      theta = f;
      break;
    case Token.straightness:
      straightness = f;
      break;
    }
  }

  public float getGroupParameter(int tok) {
    if (!haveParameters())
      calcBioParameters();
    switch (tok) {
    case Token.omega:
      return omega;
    case Token.phi:
      return phi;
    case Token.psi:
      return psi;
    case Token.eta:
      return mu;
    case Token.theta:
      return theta;
    case Token.straightness:
      return straightness;
    }
    return Float.NaN;
  }

  public Group(Chain chain, String group3, int seqcode,
        int firstAtomIndex, int lastAtomIndex) {
    this.chain = chain;
    this.seqcode = seqcode;
    
    if (group3 == null)
      group3 = "";
    groupID = getGroupID(group3);
    isAmino = (groupID >= 1 && groupID < JmolConstants.GROUPID_AMINO_MAX); 

    this.firstAtomIndex = firstAtomIndex;
    this.lastAtomIndex = lastAtomIndex;
  }

  public void setModelSet(ModelSet modelSet) {
    chain.modelSet = modelSet;  
  }
  
  public final void setShapeVisibility(int visFlag, boolean isVisible) {
    if(isVisible) {
      shapeVisibilityFlags |= visFlag;        
    } else {
      shapeVisibilityFlags &=~visFlag;
    }
}

  final boolean isGroup3(String group3) {
    return group3Names[groupID].equalsIgnoreCase(group3);
  }

  final String getGroup3() {
    return group3Names[groupID];
  }

  public static String getGroup3(short groupID) {
    return group3Names[groupID];
  }

  public final char getGroup1() {
    if (groupID >= JmolConstants.predefinedGroup1Names.length)
      return '?';
    return JmolConstants.predefinedGroup1Names[groupID];
  }

  public final short getGroupID() {
    return groupID;
  }

  public final ModelSet getModelSet() {
    return chain.getModelSet();
  }

  public final char getChainID() {
    return chain.chainID;
  }

  public int getBioPolymerLength() {
    return 0;
  }

  public int getMonomerIndex() {
    return -1;
  }

  public Object getStructure() {
    return null;
  }

  public int getStrucNo() {
    return 0;
  }
  
  public byte getProteinStructureType() {
    return JmolConstants.PROTEIN_STRUCTURE_NOT;
  }

  public byte getProteinStructureSubType() {
    return getProteinStructureType();
  }


  /**
   * 
   * @param iType
   * @param monomerIndexCurrent
   * @return type
   */
  public int setProteinStructureType(byte iType, int monomerIndexCurrent) {
    return -1;
  }

  public boolean isProtein() { return false; }
  public boolean isNucleic() { return false; }
  public boolean isDna() { return false; }
  public boolean isRna() { return false; }
  public boolean isPurine() { return false; }
  public boolean isPyrimidine() { return false; }
  public boolean isCarbohydrate() { return false; }

  ////////////////////////////////////////////////////////////////
  // static stuff for group ids
  ////////////////////////////////////////////////////////////////

  private static Map<String, Short> htGroup = new Hashtable<String, Short>();

  static String[] group3Names = new String[128];
  static short group3NameCount = 0;
  
  static {
    for (int i = 0; i < JmolConstants.predefinedGroup3Names.length; ++i) {
      addGroup3Name(JmolConstants.predefinedGroup3Names[i]);
    }
  }
  
  synchronized static short addGroup3Name(String group3) {
    if (group3NameCount == group3Names.length)
      group3Names = ArrayUtil.doubleLength(group3Names);
    short groupID = group3NameCount++;
    group3Names[groupID] = group3;
    htGroup.put(group3, Short.valueOf(groupID));
    return groupID;
  }

  public static short getGroupID(String group3) {
    if (group3 == null)
      return -1;
    short groupID = lookupGroupID(group3);
    return (groupID != -1) ? groupID : addGroup3Name(group3);
  }

  public static short lookupGroupID(String group3) {
    if (group3 != null) {
      Short boxedGroupID = htGroup.get(group3);
      if (boxedGroupID != null)
        return boxedGroupID.shortValue();
    }
    return -1;
  }

  ////////////////////////////////////////////////////////////////
  // seqcode stuff
  ////////////////////////////////////////////////////////////////

  public final int getResno() {
    return (seqcode == Integer.MIN_VALUE ? 0 : seqcode >> SEQUENCE_NUMBER_SHIFT); 
  }

  public final int getSeqcode() {
    return seqcode;
  }

  public final int getSeqNumber() {
    return seqcode >> SEQUENCE_NUMBER_SHIFT;
  }

  public final static int getSequenceNumber(int seqcode) {
    return (haveSequenceNumber(seqcode)? seqcode >> SEQUENCE_NUMBER_SHIFT 
        : Integer.MAX_VALUE);
  }
  
  public final static int getInsertionCodeValue(int seqcode) {
    return (seqcode & INSERTION_CODE_MASK);
  }
  
  public final static boolean haveSequenceNumber(int seqcode) {
    return ((seqcode & SEQUENCE_NUMBER_FLAG) != 0);
  }

  public final String getSeqcodeString() {
    return getSeqcodeString(seqcode);
  }

  public static int getSeqcode(int sequenceNumber, char insertionCode) {
    if (sequenceNumber == Integer.MIN_VALUE)
      return sequenceNumber;
    if (! ((insertionCode >= 'A' && insertionCode <= 'Z') ||
           (insertionCode >= 'a' && insertionCode <= 'z') ||
           (insertionCode >= '0' && insertionCode <= '9') ||
           insertionCode == '?' || insertionCode == '*')) {
      if (insertionCode != ' ' && insertionCode != '\0')
        Logger.warn("unrecognized insertionCode:" + insertionCode);
      insertionCode = '\0';
    }
    return ((sequenceNumber == Integer.MAX_VALUE ? 0 
        : (sequenceNumber << SEQUENCE_NUMBER_SHIFT) | SEQUENCE_NUMBER_FLAG))
        + insertionCode;
  }

  public static String getSeqcodeString(int seqcode) {
    if (seqcode == Integer.MIN_VALUE)
      return null;
    return (seqcode & INSERTION_CODE_MASK) == 0
      ? "" + (seqcode >> SEQUENCE_NUMBER_SHIFT)
      : "" + (seqcode >> SEQUENCE_NUMBER_SHIFT) 
           + '^' + (char)(seqcode & INSERTION_CODE_MASK);
  }

  public char getInsertionCode() {
    if (seqcode == Integer.MIN_VALUE)
      return '\0';
    return (char)(seqcode & INSERTION_CODE_MASK);
  }
  
  public static char getInsertionCode(int seqcode) {
    if (seqcode == Integer.MIN_VALUE)
      return '\0';
    return (char)(seqcode & INSERTION_CODE_MASK);
  }
  
  public final int selectAtoms(BitSet bs) {
    bs.set(firstAtomIndex, lastAtomIndex + 1);
    return lastAtomIndex;
  }

  public boolean isSelected(BitSet bs) {
    for (int i = firstAtomIndex; i <= lastAtomIndex; ++i)
      if (bs.get(i))
        return true;
    return false;
  }

  boolean isHetero() {
    // just look at the first atom of the group
    return chain.getAtom(firstAtomIndex).isHetero();
  }
  
  @Override
  public String toString() {
    return "[" + getGroup3() + "-" + getSeqcodeString() + "]";
  }

  protected int scaleToScreen(int Z, int mar) {
    return chain.modelSet.viewer.scaleToScreen(Z, mar);
  }
  
  protected boolean isCursorOnTopOf(Atom atom, int x, int y, int radius, Atom champ) {
    return chain.modelSet.isCursorOnTopOf(atom , x, y, radius, champ);
  }
  
  protected boolean isAtomHidden(int atomIndex) {
    return chain.modelSet.isAtomHidden(atomIndex);
  }

  /**
   * BE CAREFUL: FAILURE TO NULL REFERENCES TO model WILL PREVENT FINALIZATION
   * AND CREATE A MEMORY LEAK.
   * 
   * @return associated Model
   */
  public Model getModel() {
    return chain.model;
  }
  
  public int getModelIndex() {
    return chain.model.getModelIndex();
  }
  
  public int getSelectedMonomerCount() {
    return 0;
  }

  public int getSelectedMonomerIndex() {
    return -1;
  }
  
  public int getSelectedGroupIndex() {
    return selectedIndex;
  }
  
  /**
   * 
   * @param atomIndex
   * @return T/F
   */
  public boolean isLeadAtom(int atomIndex) {
    return false;
  }
  
  public Atom getLeadAtom(Atom atom) { //for sticks
    Atom a = getLeadAtom();
    return (a == null ? atom : a);
  }
  
  public Atom getLeadAtom() {
    return null; // but see Monomer class
  }

  /**
   * 
   * @param qType
   * @return quaternion
   */
  public Quaternion getQuaternion(char qType) {
    return null;
  }
  
  public Quaternion getQuaternionFrame(Atom[] atoms) {
    if (lastAtomIndex - firstAtomIndex < 3)
      return null;
    int pt = firstAtomIndex;
    return Quaternion.getQuaternionFrame(atoms[pt], atoms[++pt], atoms[++pt]);
  }

  /**
   * 
   * @param i
   */
  public void setProteinStructureId(int i) {
  }

  /**
   * 
   * @param tokType
   * @param qType
   * @param mStep
   * @return helix data of some sort
   */
  public Object getHelixData(int tokType, char qType, int mStep) {
        switch (tokType) {
        case Token.point:
          return new Point3f();
        case Token.axis:
        case Token.radius:
          return new Vector3f();
        case Token.angle:
          return new Float(Float.NaN);
        case Token.array:
        case Token.list:
          return new String[] {};
        }
    return "";
  }

  /**
   * 
   * @param type
   * @return T/F
   */
  public boolean isWithinStructure(byte type) {
    return false;
  }

  public String getProteinStructureTag() {
    return null;
  }

  public String getStructureId() {
    return "";
  }

  public int getBioPolymerIndexInModel() {
    return -1;
  }

  /**
   * 
   * @param g
   * @return T/F
   */
  public boolean isCrossLinked(Group g) {
    return false;
  }

  /**
   * 
   * @param vReturn
   * @return T/F
   */
  public boolean getCrossLinkLeadAtomIndexes(List<Integer> vReturn) {
    return false;
  }

  public boolean isConnectedPrevious() {
    return false;
  }

  public Atom getNitrogenAtom() {
    return null;
  }

  public Atom getCarbonylOxygenAtom() {
    return null;
  }
}
