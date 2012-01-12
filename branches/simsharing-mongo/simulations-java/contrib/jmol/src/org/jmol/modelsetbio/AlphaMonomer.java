/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-02-15 11:45:59 -0600 (Thu, 15 Feb 2007) $
 * $Revision: 6834 $
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
package org.jmol.modelsetbio;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.jmol.modelset.Atom;
import org.jmol.modelset.Chain;
import org.jmol.util.Quaternion;
import org.jmol.viewer.JmolConstants;

public class AlphaMonomer extends Monomer {

  final static byte[] alphaOffsets = { 0 };

  @Override
  public boolean isProtein() {
    return true;
  }
  
  static Monomer
    validateAndAllocate(Chain chain, String group3, int seqcode,
                        int firstIndex, int lastIndex,
                        int[] specialAtomIndexes) {
    if (firstIndex != lastIndex ||
        specialAtomIndexes[JmolConstants.ATOMID_ALPHA_CARBON] != firstIndex)
      return null;
    return new AlphaMonomer(chain, group3, seqcode,
                            firstIndex, lastIndex, alphaOffsets);
  }
  
  ////////////////////////////////////////////////////////////////

  AlphaMonomer(Chain chain, String group3, int seqcode,
               int firstAtomIndex, int lastAtomIndex,
               byte[] offsets) {
    super(chain, group3, seqcode,
          firstAtomIndex, lastAtomIndex, offsets);
  }

  boolean isAlphaMonomer() { return true; }

  protected ProteinStructure proteinStructure;
  protected Point3f nitrogenHydrogenPoint;
  
  @Override
  public ProteinStructure getProteinStructure() { return proteinStructure; }

  @Override
  public Object getStructure() { return getProteinStructure(); }

  @Override
  void setStructure(ProteinStructure proteinStructure) {
    this.proteinStructure = proteinStructure;
    if (proteinStructure == null)
      nitrogenHydrogenPoint = null;
  }
  
  @Override
  public void setProteinStructureId(int id) {
    if (proteinStructure != null)
      proteinStructure.uniqueID = id;
  }

  @Override
  public byte getProteinStructureType() {
    return proteinStructure == null ? JmolConstants.PROTEIN_STRUCTURE_NONE
        : proteinStructure.type;
  }

  @Override
  public byte getProteinStructureSubType() {
    return proteinStructure == null ? JmolConstants.PROTEIN_STRUCTURE_NONE
        : proteinStructure.subtype;
  }

  @Override
  public int getStrucNo() {
    return proteinStructure != null ? proteinStructure.uniqueID : 0;
  }

  @Override
  public boolean isHelix() {
    return proteinStructure != null &&
      proteinStructure.type == JmolConstants.PROTEIN_STRUCTURE_HELIX;
  }

  @Override
  public boolean isSheet() {
    return proteinStructure != null &&
      proteinStructure.type == JmolConstants.PROTEIN_STRUCTURE_SHEET;
  }

  /**
   * 
   * @param iType
   * @param monomerIndexCurrent   a pointer to the current ProteinStructure
   * @return                      a pointer to this ProteinStructure
   */
  @Override
  public int setProteinStructureType(byte iType, int monomerIndexCurrent) {
    if (monomerIndexCurrent < 0 
        || monomerIndexCurrent > 0 && monomerIndex == 0) {
      if (proteinStructure != null) {
        int nAbandoned = proteinStructure.removeMonomer(monomerIndex);
        if (nAbandoned > 0)
          getBioPolymer().removeProteinStructure(monomerIndex + 1, nAbandoned);
      }
      switch (iType) {
      case JmolConstants.PROTEIN_STRUCTURE_HELIX:
      case JmolConstants.PROTEIN_STRUCTURE_HELIX_ALPHA:
      case JmolConstants.PROTEIN_STRUCTURE_HELIX_310:
      case JmolConstants.PROTEIN_STRUCTURE_HELIX_PI:
        setStructure(new Helix((AlphaPolymer) bioPolymer, monomerIndex, 1, 0, iType));
        break;
      case JmolConstants.PROTEIN_STRUCTURE_SHEET:
        setStructure(new Sheet((AlphaPolymer) bioPolymer, monomerIndex, 1, 0, iType));
        break;
      case JmolConstants.PROTEIN_STRUCTURE_TURN:
        setStructure(new Turn((AlphaPolymer) bioPolymer, monomerIndex, 1, 0));
        break;
      case JmolConstants.PROTEIN_STRUCTURE_NONE:
        setStructure(null);
      }
    } else {
      setStructure(getBioPolymer().getProteinStructure(monomerIndexCurrent));
      if (proteinStructure != null)
        proteinStructure.addMonomer(monomerIndex);
    }
    return monomerIndex;
  }
  
  final public Atom getAtom(byte specialAtomID) {
    return (specialAtomID == JmolConstants.ATOMID_ALPHA_CARBON
            ? getLeadAtom()
            : null);
  }

  final public Point3f getAtomPoint(byte specialAtomID) {
    return (specialAtomID == JmolConstants.ATOMID_ALPHA_CARBON
            ? getLeadAtom()
            : null);
  }

  @Override
  boolean isConnectedAfter(Monomer possiblyPreviousMonomer) {
    if (possiblyPreviousMonomer == null)
      return true;
    if (! (possiblyPreviousMonomer instanceof AlphaMonomer))
      return false;
    Atom atom1 = getLeadAtom();
    Atom atom2 = possiblyPreviousMonomer.getLeadAtom();
    return atom1.isBonded(atom2) || atom1.distance(atom2) <= 4.2f;
    // jan reichert in email to miguel on 10 May 2004 said 4.2 looked good
  }
  
  @Override
  Point3f getQuaternionFrameCenter(char qType) {
    switch (qType) {
    case 'b':
    case 'c':
    case 'C': // ramachandran
    case 'x':
      return getLeadAtom();
    default:
    case 'a':
    case 'n':
    case 'p':
    case 'P': // ramachandran
    case 'q': // Quine
      return null;
    }
  }

  @Override
  public Object getHelixData(int tokType, char qType, int mStep) {
    return getHelixData2(tokType, qType, mStep);
  }
  
  @Override
  public Quaternion getQuaternion(char qType) {
    /*
     * also NucleicMonomer, AminoMonomer
     * 
     * This definition is only for alpha-only chains
     *   
     */
    
    Vector3f vA = new Vector3f();
    Vector3f vB = new Vector3f();
    Vector3f vC = null;

    switch (qType) {
    default:
    case 'a':
    case 'n':
    case 'p':
    case 'q':
      return null;
    case 'b':
    case 'c':
    case 'x':
      //vA = ptCa(i+1) - ptCa
      //vB = ptCa(i-1) - ptCa
      if (monomerIndex == 0 
          || monomerIndex == bioPolymer.monomerCount - 1)
        return null;
      Point3f ptCa = getLeadAtom();
      Point3f ptCaNext = bioPolymer.getLeadPoint(monomerIndex + 1);
      Point3f ptCaPrev = bioPolymer.getLeadPoint(monomerIndex - 1);
      vA.sub(ptCaNext, ptCa);
      vB.sub(ptCaPrev, ptCa);
      break;
    }
    return Quaternion.getQuaternionFrame(vA, vB, vC, false);
  }
  

}
