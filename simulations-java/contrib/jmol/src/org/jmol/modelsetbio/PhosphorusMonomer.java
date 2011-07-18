/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-02-15 11:45:59 -0600 (Thu, 15 Feb 2007) $
 * $Revision: 6834 $
 *
 * Copyright (C) 2004-2005  The Jmol Development Team
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
package org.jmol.modelsetbio;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.jmol.modelset.Atom;
import org.jmol.modelset.Chain;
import org.jmol.util.Quaternion;
import org.jmol.viewer.JmolConstants;

public class PhosphorusMonomer extends Monomer {

  protected final static byte P = 0;

  private final static byte[] phosphorusOffsets = { P };

  private static float MAX_ADJACENT_PHOSPHORUS_DISTANCE = 8.0f;
 
  protected boolean isPurine;
  protected boolean isPyrimidine;

  @Override
  public final boolean isNucleic() {return true;}

  static Monomer
    validateAndAllocate(Chain chain, String group3, int seqcode,
                        int firstIndex, int lastIndex,
                        int[] specialAtomIndexes) {
    //Logger.debug("PhosphorusMonomer.validateAndAllocate");
    if (firstIndex != lastIndex ||
        specialAtomIndexes[JmolConstants.ATOMID_NUCLEIC_PHOSPHORUS]
        != firstIndex)
      return null;
    return new PhosphorusMonomer(chain, group3, seqcode,
                            firstIndex, lastIndex, phosphorusOffsets);
  }
  
  ////////////////////////////////////////////////////////////////

  protected PhosphorusMonomer(Chain chain, String group3, int seqcode,
               int firstAtomIndex, int lastAtomIndex,
               byte[] offsets) {
    super(chain, group3, seqcode,
          firstAtomIndex, lastAtomIndex, offsets);
    if (group3.indexOf('T') >= 0)
      chain.setIsDna(true);
    if (group3.indexOf('U') + group3.indexOf('I') > -2)
        chain.setIsRna(true);
    isPurine = (group3.indexOf('A') + group3.indexOf('G') + group3.indexOf('I') > -3);
    isPyrimidine = (group3.indexOf('T') + group3.indexOf('C') + group3.indexOf('U') > -3);
  }

  Atom getP() {
    return getAtomFromOffsetIndex(P);
  }

  boolean isPhosphorusMonomer() { return true; }

  @Override
  public boolean isDna() { return chain.isDna(); }

  @Override
  public boolean isRna() { return chain.isRna(); }

  @Override
  public boolean isPurine() { return isPurine; }
  @Override
  public boolean isPyrimidine() { return isPyrimidine; }

  @Override
  public Object getStructure() { return chain; }

  @Override
  public byte getProteinStructureType() {
    return JmolConstants.PROTEIN_STRUCTURE_NONE;
  }

  @Override
  boolean isConnectedAfter(Monomer possiblyPreviousMonomer) {
    if (possiblyPreviousMonomer == null)
      return true;
    if (! (possiblyPreviousMonomer instanceof PhosphorusMonomer))
      return false;
    // 1PN8 73:d and 74:d are 7.001 angstroms apart
    // but some P atoms are up to 7.4 angstroms apart
    float distance =
      getLeadAtom().distance(possiblyPreviousMonomer.getLeadAtom());
    return distance <= MAX_ADJACENT_PHOSPHORUS_DISTANCE;
  }
  
  @Override
  public Quaternion getQuaternion(char qType) {
    //vA = ptP(i+1) - ptP
    //vB = ptP(i-1) - ptP
    int i = monomerIndex;
    if (i == 0 || i >= bioPolymer.monomerCount - 1)
      return null;
    Point3f ptP = bioPolymer.monomers[i].getAtomFromOffsetIndex(P);
    Point3f ptA, ptB;
    ptA = bioPolymer.monomers[i + 1].getAtomFromOffsetIndex(P);
    ptB = bioPolymer.monomers[i - 1].getAtomFromOffsetIndex(P);
    if (ptP == null || ptA == null || ptB == null)
      return null;
    Vector3f vA = new Vector3f();
    Vector3f vB = new Vector3f();
    vA.sub(ptA, ptP);
    vB.sub(ptB, ptP);
    return Quaternion.getQuaternionFrame(vA, vB, null, false);
  }
  
  @Override
  Point3f getQuaternionFrameCenter(char qType) {
    return getAtomFromOffsetIndex(P);
  }
  
  @Override
  public Object getHelixData(int tokType, char qType, int mStep) {
    return getHelixData2(tokType, qType, mStep);
  }
  

}
