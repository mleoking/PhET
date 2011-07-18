/* $RCSfile$
 * $Author: egonw $
 * $Date: 2005-11-10 09:52:44 -0600 (Thu, 10 Nov 2005) $
 * $Revision: 4255 $
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

import java.util.BitSet;

import org.jmol.modelset.Atom;
import org.jmol.modelset.Chain;
import org.jmol.modelset.Group;
import org.jmol.modelset.Polymer;
import org.jmol.viewer.JmolConstants;
import org.jmol.api.JmolBioResolver;

public final class Resolver implements JmolBioResolver {

  public Group distinguishAndPropagateGroup(Chain chain, String group3,
                                            int seqcode, int firstAtomIndex,
                                            int maxAtomIndex, int modelIndex,
                                            int[] specialAtomIndexes,
                                            Atom[] atoms) {
    /*
     * called by finalizeGroupBuild()
     * 
     * first: build array of special atom names, for example "CA" for the alpha
     * carbon is assigned #2 see JmolConstants.specialAtomNames[] the special
     * atoms all have IDs based on Atom.lookupSpecialAtomID(atomName) these will
     * be the same for each conformation
     * 
     * second: creates the monomers themselves based on this information thus
     * building the byte offsets[] array for each monomer, indicating which
     * position relative to the first atom in the group is which atom. Each
     * monomer.offsets[i] then points to the specific atom of that type these
     * will NOT be the same for each conformation
     */

    int lastAtomIndex = maxAtomIndex - 1;

    int distinguishingBits = 0;

    // clear previous specialAtomIndexes
    for (int i = JmolConstants.ATOMID_MAX; --i >= 0;)
      specialAtomIndexes[i] = Integer.MIN_VALUE;

    // go last to first so that FIRST confirmation is default
    for (int i = maxAtomIndex; --i >= firstAtomIndex;) {
      int specialAtomID = atoms[i].getAtomID();
      if (specialAtomID <= 0)
        continue;
      if (specialAtomID < JmolConstants.ATOMID_DISTINGUISHING_ATOM_MAX) {
        /*
         * save for future option -- turns out the 1jsa bug was in relation to
         * an author using the same group number for two different groups
         * 
         * if ((distinguishingBits & (1 << specialAtomID) != 0) {
         * 
         * //bh 9/21/2006: //
         * "if the group has two of the same, that cannot be right." // Thus,
         * for example, two C's doth not make a protein "carbonyl C"
         * distinguishingBits = 0; break; }
         */
        distinguishingBits |= (1 << specialAtomID);
      }
      specialAtomIndexes[specialAtomID] = i;
    }

    if (lastAtomIndex < firstAtomIndex)
      throw new NullPointerException();

    if ((distinguishingBits & JmolConstants.ATOMID_PROTEIN_MASK) == JmolConstants.ATOMID_PROTEIN_MASK)
      return AminoMonomer.validateAndAllocate(chain, group3, seqcode,
          firstAtomIndex, lastAtomIndex, specialAtomIndexes, atoms);
    if (distinguishingBits == JmolConstants.ATOMID_ALPHA_ONLY_MASK)
      return AlphaMonomer.validateAndAllocate(chain, group3, seqcode,
          firstAtomIndex, lastAtomIndex, specialAtomIndexes);
    if (((distinguishingBits & JmolConstants.ATOMID_NUCLEIC_MASK) == JmolConstants.ATOMID_NUCLEIC_MASK))
      return NucleicMonomer.validateAndAllocate(chain, group3, seqcode,
          firstAtomIndex, lastAtomIndex, specialAtomIndexes);
    if (distinguishingBits == JmolConstants.ATOMID_PHOSPHORUS_ONLY_MASK)
      return PhosphorusMonomer.validateAndAllocate(chain, group3, seqcode,
          firstAtomIndex, lastAtomIndex, specialAtomIndexes);
    if (JmolConstants.checkCarbohydrate(group3))
      return CarbohydrateMonomer.validateAndAllocate(chain, group3, seqcode,
          firstAtomIndex, lastAtomIndex);
    return null;
  }   
  
  public Polymer buildBioPolymer(Group group, Group[] groups, int i, 
                                 boolean checkPolymerConnections) {
    return (group instanceof Monomer && ((Monomer) group).getBioPolymer() == null ?
      BioPolymer.allocateBioPolymer(groups, i, checkPolymerConnections) : null);
  }
  
  public void clearBioPolymers(Group[] groups, int groupCount,
                               BitSet alreadyDefined) {
    for (int i = 0; i < groupCount; ++i) {
      Group group = groups[i];
      if (group instanceof Monomer) {
        Monomer monomer = (Monomer) group;
        if (monomer.getBioPolymer() != null
            && (alreadyDefined == null || !alreadyDefined.get(monomer.getModelIndex())))
          monomer.setBioPolymer(null, -1);
      }
    }

  }
}

