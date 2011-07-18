/* $RCSfile$
 * $Author: nicove $
 * $Date: 2007-03-25 06:44:28 -0500 (Sun, 25 Mar 2007) $
 * $Revision: 7224 $
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

import org.jmol.api.JmolEdge;
import org.jmol.i18n.GT;
import org.jmol.modelset.Atom;
import org.jmol.modelset.Bond;
import org.jmol.modelset.HBond;
import org.jmol.modelset.Model;
import org.jmol.modelset.Polymer;
import org.jmol.script.Token;
//import org.jmol.util.Escape;
import org.jmol.util.Escape;
import org.jmol.util.Logger;
import org.jmol.util.Measure;
import org.jmol.util.TextFormat;
import org.jmol.viewer.JmolConstants;
import org.jmol.viewer.Viewer;

//import java.util.ArrayList;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class AminoPolymer extends AlphaPolymer {

  /*
   *  methods herein:
   *  
   *  (1) constructor and utilities
   *  (2) Ramachandran angles
   *  (3) dipole/dipole classical hydrogen bond calculation
   *  (4) DSSP secondary structure determination
   *  (5) Ramachandran-angle-based secondary structure determination
   *  
   *  See also AlphaPolymer.java for alpha-carbon-only SS determination
   *  
   */
  
  
  AminoPolymer(Monomer[] monomers) {
    super(monomers);
    type = TYPE_AMINO;
    for (int i = 0; i < monomerCount; ++i)
      if (!((AminoMonomer) monomers[i]).hasOAtom())
        return;
    hasWingPoints = true;
  }

  @Override
  protected void resetHydrogenPoints() {
    ProteinStructure ps;
    ProteinStructure psLast = null;
    for (int i = 0; i < monomerCount; i++) {
      if ((ps = getProteinStructure(i)) != null && ps != psLast)
        (psLast = ps).resetAxes();
      ((AminoMonomer) monomers[i]).resetHydrogenPoint();
    }
  }

  ///////////// Ramachandran angles ///////////////
  //
  // G. N. Ramachandran and V. Sasisekharan,
  // "Conformation of Polypeptides and Proteins" 
  // in Advances in Protein Chemistry, D.C. Rees, Ed.,
  // Volume 23, Elsevier, 1969, p 284
  // 
  /////////////////////////////////////////////////
  
  
  @Override
  protected boolean calcPhiPsiAngles() {
    for (int i = 0; i < monomerCount - 1; ++i)
      calcPhiPsiAngles((AminoMonomer) monomers[i],
          (AminoMonomer) monomers[i + 1]);
    return true;
  }

  private void calcPhiPsiAngles(AminoMonomer residue1, AminoMonomer residue2) {

    /*
     *   N1-Ca1-C1-N2-Ca2-C2
     *    residue1  residue2
     *   low -----------> high   atomIndex
     * 
     * UNfortunately, omega is defined for residue 1 (page 294)
     * such that the residue having unusual omega is not the
     * proline itself but the one prior to it.
     * 
     */
    Point3f nitrogen1 = residue1.getNitrogenAtom();
    Point3f alphacarbon1 = residue1.getLeadAtom();
    Point3f carbon1 = residue1.getCarbonylCarbonAtom();
    Point3f nitrogen2 = residue2.getNitrogenAtom();
    Point3f alphacarbon2 = residue2.getLeadAtom();
    Point3f carbon2 = residue2.getCarbonylCarbonAtom();

    residue2.setGroupParameter(Token.phi, Measure.computeTorsion(carbon1,
        nitrogen2, alphacarbon2, carbon2, true));
    residue1.setGroupParameter(Token.psi, Measure.computeTorsion(nitrogen1,
        alphacarbon1, carbon1, nitrogen2, true));
    // to offset omega so cis-prolines show up off the plane, 
    // we would have to use residue2 here:
    residue1.setGroupParameter(Token.omega, Measure.computeTorsion(
        alphacarbon1, carbon1, nitrogen2, alphacarbon2, true));
  }

  @Override
  protected float calculateRamachandranHelixAngle(int m, char qtype) {
    float psiLast = (m == 0 ? Float.NaN : monomers[m - 1]
        .getGroupParameter(Token.psi));
    float psi = monomers[m].getGroupParameter(Token.psi);
    float phi = monomers[m].getGroupParameter(Token.phi);
    float phiNext = (m == monomerCount - 1 ? Float.NaN : monomers[m + 1]
        .getGroupParameter(Token.phi));
    float psiNext = (m == monomerCount - 1 ? Float.NaN : monomers[m + 1]
        .getGroupParameter(Token.psi));
    switch (qtype) {
    default:
    case 'p':
    case 'r':
    case 'P':
      /* 
       * an approximation by Bob Hanson and Steven Braun 7/7/2009
       * 
       * P-straightness utilizes phi[i], psi[i] and phi[i+1], psi[i+1]
       * and is approximated as:
       * 
       *   1 - 2 acos(|cos(theta/2)|) / PI
       * 
       * where 
       * 
       *   cos(theta/2) = dq[i]\dq[i-1] = cos(dPsi/2)cos(dPhi/2) - cos(alpha)sin(dPsi/2)sin(dPhi/2)
       * 
       * and 
       * 
       *   dPhi = phi[i+1] - phi[i]
       *   dPsi = psi[i+1] - psi[i]
       * 
       */
      float dPhi = (float) ((phiNext - phi) / 2 * Math.PI / 180);
      float dPsi = (float) ((psiNext - psi) / 2 * Math.PI / 180);
      return (float) (180 / Math.PI * 2 * Math.acos(Math.cos(dPsi)
          * Math.cos(dPhi) - Math.sin(dPsi) * Math.sin(dPhi) / 3));
    case 'c':
    case 'C':
      /* an approximation by Bob Hanson and Dan Kohler, 7/2008
       * 
       * The near colinearity of the C_alpha-C and N'-C_alpha'
       * allows for the remarkably simple relationship
       * 
       *  psi[i] - psi[i-1] + phi[i+1] - phi[i]
       *
       */
      return (psi - psiLast + phiNext - phi);
    }
  }

  //////////////////////////////////////////////////
  //
  // RasMol/DSSP dipole/dipole hydrogen bond calculation 
  //
  //    W. Kabsch and C. Sander, Biopolymers, 
  //    vol 22, 1983, pp 2577-2637
  // 
  //////////////////////////////////////////////////
  
  @Override
  public void calcRasmolHydrogenBonds(Polymer polymer, BitSet bsA, BitSet bsB,
                                      List<Bond> vHBonds, int nMaxPerResidue,
                                      int[][][] min, boolean checkDistances, 
                                      boolean dsspIgnoreHydrogens) {
    if (polymer == null)
      polymer = this;
    if (!(polymer instanceof AminoPolymer))
      return;
    Point3f pt = new Point3f();
    Vector3f vNH = new Vector3f();
    AminoMonomer source;
    int[][] min1 = (min == null ? new int[2][3] : null);
    for (int i = 1; i < monomerCount; ++i) { //not first N
      if (min == null) {
        min1[0][0] = min1[1][0] = bioPolymerIndexInModel;
        min1[0][1] = min1[1][1] = Integer.MIN_VALUE;
        min1[0][2] = min1[1][2] = 0;
      } else {
        min1 = min[i];
      }
      if ((source = ((AminoMonomer) monomers[i])).getNHPoint(pt, vNH,
          checkDistances, dsspIgnoreHydrogens)) {
        boolean isInA = (bsA == null || bsA.get(source.getNitrogenAtom().index));
        if (!isInA)
          continue;
        // for DSSP, we also knock out all groups having no carbonyl oxygen
        if (!checkDistances && source.getCarbonylOxygenAtom() == null)
          continue;
        checkRasmolHydrogenBond(source, polymer, i, pt,
            (isInA ? bsB : bsA), vHBonds, min1, checkDistances);
      }
    }
  }

  // max distance from RasMol 2.7.2.1.1  #define MaxHDist ((Long)2250*2250) 
  private final static float maxHbondAlphaDistance = 9;
  private final static float maxHbondAlphaDistance2 = maxHbondAlphaDistance
      * maxHbondAlphaDistance;
  // this next was fixed in Jmol 12.1.14; was just 0.5f (0.71*0.71) since Jmol 10.0.00
  private final static float minimumHbondDistance2 = 0.5f * 0.5f; 

  private void checkRasmolHydrogenBond(AminoMonomer source, Polymer polymer,
                                       int indexDonor, Point3f hydrogenPoint,
                                       BitSet bsB, List<Bond> vHBonds,
                                       int[][] min, boolean checkDistances) {
    Point3f sourceAlphaPoint = source.getLeadAtom();
    Point3f sourceNitrogenPoint = source.getNitrogenAtom();
    Atom nitrogen = source.getNitrogenAtom();
    int[] m;
    for (int i = polymer.monomerCount; --i >= 0;) {
      if (polymer == this && (i == indexDonor || i + 1 == indexDonor 
          /* || i - 1 == indexDonor*/ ))
        continue; 
      // 3mn5 GLY36->ARG37 is an example where we can have i-1 be the donor  
      AminoMonomer target = (AminoMonomer) ((BioPolymer) polymer).monomers[i];
      Atom oxygen = target.getCarbonylOxygenAtom();
      if (oxygen == null || bsB != null && !bsB.get(oxygen.index))
        continue;
      Point3f targetAlphaPoint = target.getLeadAtom();
      float dist2 = sourceAlphaPoint.distanceSquared(targetAlphaPoint);
      if (dist2 >= maxHbondAlphaDistance2)
        continue;
      int energy = calcHbondEnergy(sourceNitrogenPoint, hydrogenPoint, target,
          checkDistances);
      if (energy < min[0][2]) {
        m = min[1];
        min[1] = min[0];
        min[0] = m;
      } else if (energy < min[1][2]) {
        m = min[1];
      } else {
        continue;
      }
      m[0] = polymer.bioPolymerIndexInModel;
      m[1] = (energy < -500 ? i : -1 - i); // so that it will not be found, but we can check it
      m[2] = energy;

    }
    if (vHBonds != null)
      for (int i = 0; i < 2; i++)
        if (min[i][1] >= 0)
          addResidueHydrogenBond(nitrogen,
              ((AminoMonomer) ((AminoPolymer) polymer).monomers[min[i][1]])
                  .getCarbonylOxygenAtom(), (polymer == this ? indexDonor : -99), min[i][1],
              min[i][2] / 1000f, vHBonds);
  }

  /**
   * based on RasMol 2.7.2.1.1 model
   * 
   * checkDistances: 
   * 
   * When we are seriously looking for H bonds, we want to 
   * also check that distCN > distCH and that the OH distance
   * is less than 3 Angstroms. Otherwise that's just too strange 
   * a hydrogen bond. (We get hydrogen bonds from i to i+2, for example)
   * 
   * This check is skipped for an actual DSSP calc., where we want the 
   * original definition and are not actually creating hydrogen bonds
   * 
   *     H .......... O
   *     |            |
   *     |            |
   *     N            C
   * 
   * @param nitrogenPoint
   * @param hydrogenPoint
   * @param target
   * @param checkDistances
   * @return               energy in cal/mol or 0 (none)
   */
  private int calcHbondEnergy(Point3f nitrogenPoint, Point3f hydrogenPoint,
                              AminoMonomer target, boolean checkDistances) {
    Point3f targetOxygenPoint = target.getCarbonylOxygenAtom();

    if (targetOxygenPoint == null)
      return 0;
    float distON2 = targetOxygenPoint.distanceSquared(nitrogenPoint);
    if (distON2 < minimumHbondDistance2)
      return 0;

    float distOH2 = targetOxygenPoint.distanceSquared(hydrogenPoint);
    if (distOH2 < minimumHbondDistance2)
      return 0;

    Point3f targetCarbonPoint = target.getCarbonylCarbonAtom();
    float distCH2 = targetCarbonPoint.distanceSquared(hydrogenPoint);
    if (distCH2 < minimumHbondDistance2)
      return 0;

    float distCN2 = targetCarbonPoint.distanceSquared(nitrogenPoint);
    if (distCN2 < minimumHbondDistance2)
      return 0;

    double distOH = Math.sqrt(distOH2);
    double distCH = Math.sqrt(distCH2);
    double distCN = Math.sqrt(distCN2);
    double distON = Math.sqrt(distON2);

    int energy = HBond.getEnergy(distOH, distCH, distCN, distON);

    boolean isHbond = (energy < -500 
        && (!checkDistances || distCN > distCH && distOH <= 3.0f));
    return (!isHbond && checkDistances || energy < -9900 ? 0 : energy);
  }

  private void addResidueHydrogenBond(Atom nitrogen, Atom oxygen,
                                      int indexAminoGroup,
                                      int indexCarbonylGroup, float energy,
                                      List<Bond> vHBonds) {
    int order;
    switch (indexAminoGroup - indexCarbonylGroup) {
    case 2:
      order = JmolEdge.BOND_H_PLUS_2;
      break;
    case 3:
      order = JmolEdge.BOND_H_PLUS_3;
      break;
    case 4:
      order = JmolEdge.BOND_H_PLUS_4;
      break;
    case 5:
      order = JmolEdge.BOND_H_PLUS_5;
      break;
    case -3:
      order = JmolEdge.BOND_H_MINUS_3;
      break;
    case -4:
      order = JmolEdge.BOND_H_MINUS_4;
      break;
    default:
      order = JmolEdge.BOND_H_CALC;
    }
    vHBonds.add(new HBond(nitrogen, oxygen, order, energy));
  }

  ////////////////////// DSSP /////////////////////
  //
  //    W. Kabsch and C. Sander, Biopolymers, vol 22, 1983, pp 2577-2637
  // 
  //
  //   ------------------license permission-----------------
  //
  //   ---------- Forwarded message ----------
  //   From: Gert Vriend <vriend@cmbi.ru.nl>
  //   Date: Wed, Oct 6, 2010 at 12:28 PM
  //   Subject: Re: DSSP license
  //   To: Robert Hanson <hansonr@stolaf.edu>
  // 
  //   Dear Robert Hanson,
  // 
  //   Feel free to freely distribute your DSSP-like code with JMOL, using any 
  //   license form you want (but preferably one that avoids people from 'stealing' 
  //   the code for activities that go against the spirit of free software exchanges).
  // 
  //   Please put somewhere (doesn't need to be a prominent place, but should be 
  //   clickable/visible one way or another):
  //   "We thank Wolfgang Kabsch and Chris Sander for writing the DSSP software, 
  //   and we thank the CMBI for maintaining it to the extent that it was easy to 
  //   re-engineer for our purposes." 
  // 
  //   Greetings
  //   Gert
  //
  //
  //   ------------------end of license permission-----------------
  //
  //   Added note by Bob Hanson 10/7/2010:
  //   
  //   Although the DSSP code from CMBI was inspected in order to confirm 
  //   conformance with that exact implementation of the algorithm described in
  //   the Kabsch and Sander paper, none of that code was extracted. That is to 
  //   say, this is an entirely different implementation.
  //
  //   This implementation of the DSSP algorithm is based solely upon the published 
  //   description of that algorithm -- as evidenced by the quoted statements
  //   in the Java doc and comments accompanying each method -- and has essentially
  //   no relationship to the Pascal/C++ code, other than that it produces a 
  //   similar result. 
  // 
  //   My approach to identifying chain breaks (use of the BioPolymer class), 
  //   cataloging bridges (using hash tables), and generating the SUMMARY line (using 
  //   bit sets), is an entirely different approach than that used by the original 
  //   authors of the DSSP code.
  //
  //   This implementation has been verified against 1769 high-resolution structures. 
  //   (see http://jmol.svn.sourceforge.net/viewvc/jmol/trunk/Jmol-datafiles/dssp/cullpdb_pc20_res1.6_R0.25_d101001_chains1769)
  //   which is from http://dunbrack.fccc.edu/Guoli/pisces_download.php#culledpdb.
  //
  //   All structures were verified either via comparison with REST data from RCSB, or for the
  //   few cases for which that was not possible, by direct comparison with DSSP files
  //   provided by http://swift.cmbi.ru.nl/gv/dssp.
  //
  //   Still, we cannot guarantee that this code will always result in an EXACT MATCH 
  //   to the Pascal/C++ code provided by CMBI, and we fully expect that there are 
  //   special cases where, particularly, the CMBI code senses a chain break 
  //   even though Jmol does not, or vice-versa, resulting in different analyses.
  //
  //   Known differences to CMBI DSSP:
  //
  //     Chain breaks
  //     ------------
  // 
  //   Jmol has its own way to calculate chain breaks. In addition,
  //   for these purposes, it does consider amino acids lacking a 
  //   carbonyl oxygen to be a chain break, for consistency with DSSP.
  //   When that is the case, no NH hydrogen bond is allowed from that
  //   residue, and no helix is allowed to span it.
  //
  //     Backbone amide hydrogens
  //     ------------------------
  //
  //   This code allows for the use of file-based backbone amide hydrogen
  //   positions (via SET dsspCalculateHydrogenAlways FALSE), which the CMBI code does 
  //   not. Certainly for some models (1def, for example) that produces a different 
  //   result, because it changes the values of calculated hydrogen bond energies.
  //
  //     Alternative locations
  //     ---------------------
  //
  //   As part of this implementation, the CONFIGURATION command was modified to be
  //   consistent with a proper interpretation of the PDB file - that codes A, B, C,
  //   etc., are only relevant WITHIN a specific residue, not across the entire file,
  //   though in most cases that is true as well. 
  //   
  //   Jmol allows for setting "configurations" that include only a subset of the
  //   alternative locations across an entire model. Thus, while the CMBI implementation
  //   of DSSP allows for reading the first configuration, Jmol allows for getting 
  //   the DSSP secondary structure analysis for any configuration. Simply use 
  //   the command CONFIGURATION n, where "n" is a number 1, 2, 3, etc., just prior
  //   to the CALCULATE STRUCTURE command.
  //
  //     Alternative SS methods
  //     ----------------------
  // 
  //   Jmol also allows for a strictly geometric method -- without hydrogen bonding --
  //   to identify sheet strands (not ladders or full "sheets") and helices. This is 
  //   invoked by
  //
  //   CALCULATE STRUCTURE RAMACHANDRAN
  //
  //     Bend (S)
  //     --------
  //
  //   Jmol does not report S in the summary line, only B, E, H, G, I, and T.
  //
  //     Surface Accessibility
  //     ---------------------
  // 
  //   Jmol does not implement surface accessibility.
  //
  //     Detailed Reporting
  //     ------------------
  //
  //   Jmol does not create the detailed report that CMBI DSSP does.
  //   We do not report hydrogen bond energies, C-alpha points, sheet 
  //   or ladder designators, or disulfide bonds.
  // 
  //   Instead, our report includes the helix-5, helix-4, and helix-3 lines
  //   as well as the summary line similar to that given in the original paper
  //   along with a summary list of stretches of B, E, H, G, I, and T structure.
  //
  //   You can use SET DEBUG TRUE to send voluminous amounts of 
  //   information to the Java console if you wish.
  //
  //   One final implementation note: It is curious that the DSSP algorithm 
  //   orders the SUMMARY line -- our final assignments -- as: H B E G I T S. 
  //   The curious thing is that this TECHNICALLY allows for calculated bridges being
  //   assignable to H groups. As noted below, I don't think this is physically possible,
  //   but it seems to me there must be SOME reason to do this rather than the more 
  //   obvious order: B E H G I T S. So there's a bit of a mystery there. My implementation
  //   adds a warning at the end of the helix-4 line if such a bridge should ever appear. 
  //   If this warning is seen, it probably means you forgot to use the CONFIGURATION command.
  // 
  ////////////////////// DSSP /////////////////////

  /**
   * 
   * @param bioPolymers  
   * @param bioPolymerCount 
   * @param vHBonds 
   * @param doReport
   * @param dsspIgnoreHydrogens 
   * @param setStructure
   * @return                 helix-5, helix-4, helix-3, and SUMMARY lines        
   */

  protected static String calculateStructuresDssp(Polymer[] bioPolymers,
                                                  int bioPolymerCount,
                                                  List<Bond> vHBonds,
                                                  boolean doReport,
                                                  boolean dsspIgnoreHydrogens,
                                                  boolean setStructure) {
    if (bioPolymerCount == 0)
      return "";

    Model m = bioPolymers[0].model;
    StringBuffer sb = new StringBuffer();
    sb.append("Jmol ").append(Viewer.getJmolVersion()).append(
        " DSSP analysis for model ").append(m.getModelNumberDotted()).append(
        " - ").append(m.getModelTitle()).append("\n");
    if (m.modelIndex == 0)
      sb.append("\nW. Kabsch and C. Sander, Biopolymers, vol 22, 1983, pp 2577-2637\n")
        .append(
              "\nWe thank Wolfgang Kabsch and Chris Sander for writing the DSSP software,\n")
          .append(
              "and we thank the CMBI for maintaining it to the extent that it was easy to\n")
          .append(
              "re-engineer for our purposes. At this point in time, we make no guarantee\n")
          .append(
              "that this code gives precisely the same analysis as the code available via license\n")
          .append("from CMBI at http://swift.cmbi.ru.nl/gv/dssp\n");

    if (setStructure && m.modelIndex == 0)
      sb.append("\nAll bioshapes have been deleted and must be regenerated.\n");

    if (m.nAltLocs > 0)
      sb.append("\nNote: This model contains alternative locations. Use  'CONFIGURATION 1' to be consistent with CMBI DSSP.\n");

    // for each AminoPolymer, we need:
    // (1) a label reading "...EEE....HHHH...GGG...BTTTB...IIIII..."
    // (2) a residue-bitset to indicate that an assignment has been made already
    // (3) an atom-bitset to indicate we have a bad residue (no carbonyl O)

    char[][] labels = new char[bioPolymerCount][];
    BitSet[] bsDone = new BitSet[bioPolymerCount];
    BitSet bsBad = new BitSet();
    boolean haveWarned = false;

    for (int i = 0; i < bioPolymerCount; i++) {
      if (!(bioPolymers[i] instanceof AminoPolymer))
        continue;
      AminoPolymer ap = (AminoPolymer) bioPolymers[i];
      if (!haveWarned
          && ((AminoMonomer) ap.monomers[0]).getExplicitNH() != null) {
        if (dsspIgnoreHydrogens)
          sb
              .append(GT
                  ._(
                      "NOTE: Backbone amide hydrogen positions are present and will be ignored. Their positions will be approximated, as in standard DSSP analysis.\nUse {0} to not use this approximation.\n\n",
                      "SET dsspCalculateHydrogenAlways FALSE"));
        else
          sb
              .append(GT
                  ._(
                      "NOTE: Backbone amide hydrogen positions are present and will be used. Results may differ significantly from standard DSSP analysis.\nUse {0} to ignore these hydrogen positions.\n\n",
                      "SET dsspCalculateHydrogenAlways TRUE"));
        haveWarned = true;
      }
      bioPolymers[i].recalculateLeadMidpointsAndWingVectors();
      labels[i] = new char[bioPolymers[i].monomerCount];
      bsDone[i] = new BitSet();
      // lacking a C=O counts as done or "chain break"
      for (int j = 0; j < ap.monomerCount; j++)
        if (((AminoMonomer) ap.monomers[j]).getCarbonylOxygenAtom() == null)
          bsBad.set(ap.monomers[j].leadAtomIndex);
    }

    // Step 1: Create a polymer-based array of dual-minimum NH->O connections
    //         similar to those used in Rasmol.

    int[][][][] min = getDualHydrogenBondArray(bioPolymers, bioPolymerCount,
        dsspIgnoreHydrogens);

    // NOTE: (p. 2587) "Structural overalaps are eliminated in this line by giving 
    //                  priority to H,B,E,G,I,T,S in this order." 
    //
    // We do B and E first, then H G I. Oddly enough, this technically allows for 
    // bridges to helix groups; I think, though, that is impossible.
    // These will be flagged on the helix-3 line with a warning.

    // Step 2: Find the bridges and mark them all as "B".

    List<Bridge> bridgesA = new ArrayList<Bridge>();
    List<Bridge> bridgesP = new ArrayList<Bridge>();
    Map<String, Bridge> htBridges = new Hashtable<String, Bridge>();
    Map<int[][], Boolean> htLadders = new Hashtable<int[][], Boolean>();
    getBridges(bioPolymers, min, bridgesA, bridgesP, htBridges, htLadders,
        bsBad, vHBonds, bsDone);

    // Step 3: Find the ladders and bulges, mark them as "E", and add the sheet structures.

    getSheetStructures(bioPolymers, bridgesA, bridgesP, htBridges, htLadders,
        labels, bsDone, doReport, setStructure);

    // Step 4: Find the helices and mark them as "G", "H", or "I", 
    //         mark remaining turn residues as "T", and add the helix and turn structures.

    String[] reports = new String[bioPolymerCount];
    for (int i = 0; i < bioPolymerCount; i++)
      if (min[i] != null)
        reports[i] = ((AminoPolymer) bioPolymers[i]).findHelixes(min[i], i,
            bsDone[i], labels[i], doReport, setStructure, vHBonds, bsBad);

    // Done!

    if (doReport) {
      StringBuffer sbSummary = new StringBuffer();
      sb.append("\n------------------------------\n");
      for (int i = 0; i < bioPolymerCount; i++)
        if (labels[i] != null) {
          AminoPolymer ap = (AminoPolymer) bioPolymers[i];
          sbSummary.append(ap.dumpSummary(labels[i]));
          sb.append(reports[i]).append(
              ap.dumpTags("$.1: " + String.valueOf(labels[i]), bsBad, 2));
        }
      if (bsBad.nextSetBit(0) >= 0)
        sb
            .append("\nNOTE: '!' indicates a residue that is missing a backbone carbonyl oxygen atom.\n");
      sb.append("\n").append("SUMMARY:" + sbSummary);
    }

    return sb.toString();
  }

  /**
   * 
   * (p. 2579):
   * 
   * Hydrogen bonds in proteins have little wave-function overlap and are well
   * described by an electrostatic model:
   * 
   *   E = q1q2(1/r(ON) + 1/r(CH) - 1/r(OH) - 1/r(CN)) * f
   *  
   * with q1 = 0.42e and q2 = 0.20e, e being the unit electron charge and r(AB)
   * the interatomic distance from A to B. In chemical units, r is in angstroms,
   * the dimensional factor f = 332, and E is in kcal/mol. We ... assign an H bond
   * between C=O of residue i and N-H of residue j if E is less than the cutoff,
   * i.e., "Hbond(i,j) =: [E < -0.5 kcal/mol]."
   * 
   * @param bioPolymers
   * @param bioPolymerCount
   * @param dsspIgnoreHydrogens 
   * @return                array of dual-minmum NH-->O=C H bonds
   * 
   */
  private static int[][][][] getDualHydrogenBondArray(Polymer[] bioPolymers,
                                                    int bioPolymerCount,
                                                    boolean dsspIgnoreHydrogens) {
    
    // The min[][][][] array:  min[iPolymer][i][[hb1],[hb2]]
    //   where i is the index of the NH end of the bond, 
    //   and [hb1] and [hb2] are [iPolymer2,i2,iEnergy]
    //   and i2 is the index of the C=O end of the bond
    //   if iEnergy is < -500 and -1 - (that number) if iEnergy is >= -500
    
    //   This part is the same as the Rasmol hydrogen bond calculation
    //

    int[][][][] min = new int[bioPolymerCount][][][];
    for (int i = 0; i < bioPolymerCount; i++) {
      if (!(bioPolymers[i] instanceof AminoPolymer))
        continue;
      int n = bioPolymers[i].monomerCount;
      min[i] = new int[n][2][3];
      for (int j = 0; j < n; ++j) {
        min[i][j][0][1] = min[i][j][1][1] = Integer.MIN_VALUE;
        min[i][j][0][2] = min[i][j][1][2] = 0;
      }
    }

    for (int i = 0; i < bioPolymerCount; i++)
      if (min[i] != null)
        for (int j = 0; j < bioPolymerCount; j++)
          if (min[j] != null)
            bioPolymers[i].calcRasmolHydrogenBonds(bioPolymers[j], null, null,
                null, 2, min[i], false, dsspIgnoreHydrogens);

    return min;
  }

  /**
   * (p. 2581):
   * 
   * A basic turn pattern (Fig. 2) is a single H bond of type (i,i+n). We
   * assign an n-turn at residue i if there is an H bond from CO(i) to NH(i+n)....
   *   When the pattern is found, the ends of the H bond are indicated using ">" at i
   * and "<" at i+n...; the residues bracketed by the H bond are noted "3," "4," or "5"
   * unless they are also end points of other H bonds. Coincidence of ">" and "<" at
   * one residue is indicated by "X." ... Residues bracketed by the hydrogen bond
   * are marked "T," unless they are part of an n-helix (defined below). 
   * 
   * (p. 2582):
   * 
   * A minimal helix is defined by two consecutive n-turns.... Longer helices are 
   * defined as overlaps of minimal helices.... Residues bracketed by H bonds are 
   * labeled G, H, I.... Long helices can deviate from regularity in that not all 
   * possible H bonds are formed. This possibility is implicit in the above helix 
   * definition.
   * 
   * @param min
   * @param iPolymer
   * @param bsDone
   * @param labels
   * @param doReport
   * @param setStructure
   * @param vHBonds 
   * @param bsBad 
   * @return             string label
   */
  private String findHelixes(int[][][] min, int iPolymer, BitSet bsDone,
                             char[] labels, boolean doReport, boolean setStructure,
                             List<Bond> vHBonds, BitSet bsBad) {
    if (Logger.debugging)
      for (int j = 0; j < monomerCount; j++)
        Logger.debug(iPolymer + "." + monomers[j].getResno() + "\t"
            + Escape.escape(min[j]));

    BitSet bsTurn = new BitSet();

    String line4 = findHelixes(4, min, iPolymer,
        JmolConstants.PROTEIN_STRUCTURE_HELIX_ALPHA, JmolEdge.BOND_H_PLUS_4, bsDone,
        bsTurn, labels, doReport, setStructure, vHBonds, bsBad);
    String line3 = findHelixes(3, min, iPolymer,
        JmolConstants.PROTEIN_STRUCTURE_HELIX_310, JmolEdge.BOND_H_PLUS_3,
        bsDone, bsTurn, labels, doReport, setStructure, vHBonds, bsBad);
    String line5 = findHelixes(5, min, iPolymer,
        JmolConstants.PROTEIN_STRUCTURE_HELIX_PI, JmolEdge.BOND_H_PLUS_5,
        bsDone, bsTurn, labels, doReport, setStructure, vHBonds, bsBad);

    // G, H, and I have been set; now set what is left over as turn

    if (setStructure)
      setStructure(bsTurn, JmolConstants.PROTEIN_STRUCTURE_TURN);
    
    if (doReport) {
      setTag(labels, bsTurn, 'T');
      return dumpTags("$.5: " + line5 + "\n" + "$.4: " + line4 + "\n" + "$.3: "
          + line3, bsBad, 1);
    }
    

    return "";
  }

  private String findHelixes(int pitch, int[][][] min, int thisIndex,
                             byte subtype, int type, BitSet bsDone,
                             BitSet bsTurn, char[] labels, 
                             boolean doReport, boolean setStructure,
                             List<Bond> vHBonds, BitSet bsBad) {

    // The idea here is to run down the polymer setting bit sets
    // that identify start, stop, N, and X codes: >, <, 3, 4, 5, and X
    // In addition, we create a bit set that will identify G H or I.

    BitSet bsStart = new BitSet();
    BitSet bsNNN = new BitSet();
    BitSet bsX = new BitSet();
    BitSet bsStop = new BitSet();
    BitSet bsHelix = new BitSet();
    
    String warning = "";

    // index is to the NH (higher index) end, not the C=O end
    
    for (int i = pitch; i < monomerCount; ++i) {
      int i0 = i - pitch;
      int bpt = 0;
      if (min[i][0][0] == thisIndex && min[i][0][1] == i0
          || min[i][bpt = 1][0] == thisIndex && min[i][1][1] == i0) {

        // the basic indicators are >33< or >444< or >5555<

        // we use bit sets here for efficiency

        int ia = monomers[i0].leadAtomIndex;
        int ipt = bsBad.nextSetBit(ia);
        if (ipt >= ia && ipt <= monomers[i].leadAtomIndex)
          continue;

        bsStart.set(i0);         //   >
        bsNNN.set(i0 + 1, i);    //    nnnn
        bsStop.set(i);           //        <

        // a run of HHHH or GGG or IIIII is made if: 
        // (1) the previous position was a start for this n-helix, and
        // (2) no position within that run has already been assigned one of BEHGI
        // also look for >< and mark those with an X

        // Note: The DSSP assignment priority is HBEGITS, so H must ignore determination of B or E.
        // This would appear as "H" with a bridge connection in DSSP output.
        // I don't think it's possible. An antiparallel bridge would require connections
        // between NH and CO of the same group or NH and CO of adjacent groups, but
        // they would be in a helix and not oriented at all the correct direction;
        // a parallel bridge would require connections between NH and CO for two 
        // groups separated by a group. This is certainly impossible in an alpha helix.
        // Still, that is what we are implementing here -- just as described -- H with
        // the possibility of a bridge.
        
        ipt = bsDone.nextSetBit(i0);
        boolean isClear = (ipt < 0 || ipt >= i);
        boolean addH = false;
        if (i0 > 0 && bsStart.get(i0 - 1) && (pitch == 4 || isClear)) {
          bsHelix.set(i0, i);
          if (!isClear)
            warning += "  WARNING! Bridge to helix at " + monomers[ipt];
          addH = true;
        } else if (isClear || bsDone.nextClearBit(ipt) < i) {
          addH = true;
        }
        if (bsStop.get(i0))
          bsX.set(i0);
        if (addH && vHBonds != null) {
          addHbond(vHBonds, monomers[i], monomers[i0], min[i][bpt][2], type,
              null);
        }
      }
    }

    char[] taglines;
    if (doReport) {
      taglines = new char[monomerCount];
      setTag(taglines, bsNNN, (char) ('0' + pitch)); // 345
      setTag(taglines, bsStart, '>'); // may overwrite n
      setTag(taglines, bsStop, '<'); // may overwrite n or ">"
      setTag(taglines, bsX, 'X'); // may overwrite "<"
    } else {
      taglines = null;
    }

    // update the bit sets based on this type of helix

    bsDone.or(bsHelix); // add HELIX to DONE
    bsNNN.andNot(bsDone); // remove DONE from nnnnn
    bsTurn.or(bsNNN); // add nnnnn to TURN
    bsTurn.andNot(bsHelix); // remove HELIX from TURN

    // create the Jmol helix structures of the given subtype

    if (setStructure)
      setStructure(bsHelix, subtype); // GHI;

    if (doReport) {
      setTag(labels, bsHelix, (char) ('D' + pitch));
      return String.valueOf(taglines) + warning;
    }
    return "";
  }

  /**
   * (p. 2581):
   * 
   * Two nonoverlapping stretches of three residues each, i-1,i,i+1 and
   * j-1,j,j+1, form either a parallel or antiparallel bridge, depending on
   * which of two basic patterns (Fig. 2) is matched. We assign a bridge
   * between residues i and j if there are two H bonds characteristic of 
   * beta-structure; in particular:
   * 
   *      Parallel Bridge(i,j) =: [Hbond(i-1,j) and Hbond(j,i+1)] or
   *                              [Hbond(j-1,i) and Hbond(i,j+1)]
   *                          
   *  Antiparallel Bridge(i,j) =: [Hbond(i,j) and Hbond(j,i)] or
   *                              [Hbond(i-1,j+1) and Hbond(j-1,i+1)]
   *                          
   * @param bioPolymers
   * @param min
   * @param bridgesA 
   * @param bridgesP 
   * @param htBridges 
   * @param htLadders 
   * @param bsBad 
   * @param vHBonds 
   * @param bsDone
   */
  private static void getBridges(Polymer[] bioPolymers, int[][][][] min,
                                 List<Bridge> bridgesA, List<Bridge> bridgesP,
                                 Map<String, Bridge> htBridges,
                                 Map<int[][], Boolean> htLadders, BitSet bsBad,
                                 List<Bond> vHBonds, BitSet[] bsDone) {
    // ooooooh! It IS possible to have 3 bridges to the same residue. (3A5F) 
    // 
    Atom[] atoms = bioPolymers[0].model.getModelSet().atoms;
    Bridge bridge = null;

    Map<String, Boolean> htTemp = new Hashtable<String, Boolean>();
    for (int p1 = 0; p1 < min.length; p1++)
      if (bioPolymers[p1] instanceof AminoPolymer) {
        AminoPolymer ap1 = ((AminoPolymer) bioPolymers[p1]);
        int n = min[p1].length - 1;
        for (int a = 1; a < n; a++) {
          int ia = ap1.monomers[a].leadAtomIndex;
          if (bsBad.get(ia))
            continue;
          for (int p2 = p1; p2 < min.length; p2++)
            if (bioPolymers[p2] instanceof AminoPolymer)
              for (int b = (p1 == p2 ? a + 3 : 1); b < min[p2].length - 1; b++) {
                AminoPolymer ap2 = (AminoPolymer) bioPolymers[p2];
                int ib = ap2.monomers[b].leadAtomIndex;
                if (bsBad.get(ib))
                  continue;
                if ((bridge = getBridge(min, p1, a, p2, b, bridgesP, atoms[ia],
                    atoms[ib], ap1, ap2, vHBonds, htTemp, false, htLadders)) != null) {
                } else if ((bridge = getBridge(min, p1, a, p2, b, bridgesA,
                    atoms[ia], atoms[ib], ap1, ap2, vHBonds, htTemp, true,
                    htLadders)) != null) {
                  bridge.isAntiparallel = true;
                } else {
                  continue;
                }
                if (Logger.debugging)
                  Logger.debug("Bridge found " + bridge);
                //setDone(bsDone1, bsDone2, ia);
                //setDone(bsDone1, bsDone2, ib);
                bsDone[p1].set(a);
                bsDone[p2].set(b);
                htBridges.put(ia + "-" + ib, bridge);
              }
        }
      }
  }

  private static int[][] sheetOffsets = {
    new int[] {0, -1, 1, 0, 1,  0, 0, -1 },
    new int[] {0,  0, 0, 0, 1, -1, 1, -1 }
  };
  
  private static Bridge getBridge(int[][][][] min, int p1, int a, int p2, int b,
                                 List<Bridge> bridges, Atom atom1, Atom atom2,
                                 AminoPolymer ap1, AminoPolymer ap2,
                                 List<Bond> vHBonds,
                                 Map<String, Boolean> htTemp,
                                 boolean isAntiparallel, 
                                 Map<int[][], Boolean> htLadders) {

    int[] b1 = null, b2 = null;
    int ipt = 0;
    int[] offsets = (isAntiparallel ? sheetOffsets[1] : sheetOffsets[0]);
    if ((b1 = isHbonded(a + offsets[0], b + offsets[1], p1, p2, min)) != null
        && (b2 = isHbonded(b + offsets[2], a + offsets[3], p2, p1, min)) != null
        || (b1 = isHbonded(a + offsets[ipt = 4], b + offsets[5], p1, p2, min)) != null
        && (b2 = isHbonded(b + offsets[6], a + offsets[7], p2, p1, min)) != null) {
      Bridge bridge = ap1.new Bridge(atom1, atom2, htLadders);
      bridges.add(bridge);
      if (vHBonds != null) {
        int type = (isAntiparallel ? JmolEdge.BOND_H_MINUS_3
            : JmolEdge.BOND_H_PLUS_2);
        addHbond(vHBonds, ap1.monomers[a + offsets[ipt]], ap2.monomers[b
            + offsets[++ipt]], b1[2], type, htTemp);
        addHbond(vHBonds, ap2.monomers[b + offsets[++ipt]], ap1.monomers[a
            + offsets[++ipt]], b2[2], type, htTemp);
      }
      return bridge;
    }
    return null;
  }

  private static void addHbond(List<Bond> vHBonds, Monomer donor,
                               Monomer acceptor, int iEnergy, int type, Map<String, Boolean> htTemp) {
    Atom nitrogen = ((AminoMonomer)donor).getNitrogenAtom();
    Atom oxygen = ((AminoMonomer) acceptor).getCarbonylOxygenAtom();
    if (htTemp != null) {
      String key = nitrogen.index + " " + oxygen.index;
      if (htTemp.containsKey(key))
        return;
      htTemp.put(key, Boolean.TRUE);
    }
    vHBonds.add(new HBond(nitrogen, oxygen, type, iEnergy / 1000f));
  }

  /**
   * 
   * "sheet =: a set of one or more ladders connected by shared residues" (p. 2582)
   * 
   * @param bioPolymers
   * @param bridgesA
   * @param bridgesP
   * @param htBridges
   * @param htLadders 
   * @param labels
   * @param bsDone 
   * @param doReport
   * @param setStructure 
   */
  private static void getSheetStructures(Polymer[] bioPolymers,
                                         List<Bridge> bridgesA,
                                         List<Bridge> bridgesP,
                                         Map<String, Bridge> htBridges,
                                         Map<int[][], Boolean> htLadders, char[][] labels, 
                                         BitSet[] bsDone,
                                         boolean doReport, 
                                         boolean setStructure) {

    // check to be sure all bridges are part of bridgeList

    if (bridgesA.size() == 0 && bridgesP.size() == 0)
      return;
    createLadders(bridgesA, htBridges, htLadders, true);
    createLadders(bridgesP, htBridges, htLadders, false);

    BitSet bsEEE = new BitSet();
    BitSet bsB = new BitSet();
    Iterator<int[][]> e = htLadders.keySet().iterator();
    while (e.hasNext()) {
      int[][] ladder = e.next();
      if (ladder[0][0] == ladder[0][1] && ladder[1][0] == ladder[1][1]) {
        bsB.set(ladder[0][0]);
        bsB.set(ladder[1][0]);
      } else {
        bsEEE.set(ladder[0][0], ladder[0][1] + 1);
        bsEEE.set(ladder[1][0], ladder[1][1] + 1);
      }
    }
    // add Jmol structures and set sheet labels to "E"
    
    BitSet bsSheet = new BitSet();
    BitSet bsBridge = new BitSet();

    for (int i = bioPolymers.length; --i >= 0;) {
      if (!(bioPolymers[i] instanceof AminoPolymer))
        continue;
      bsSheet.clear();
      bsBridge.clear();
      AminoPolymer ap = (AminoPolymer) bioPolymers[i];
      for (int iStart = 0; iStart < ap.monomerCount; ) {
        int index = ap.monomers[iStart].leadAtomIndex;
        if (bsEEE.get(index)) {
          int iEnd = iStart + 1;
          while (iEnd < ap.monomerCount
              && bsEEE.get(ap.monomers[iEnd].leadAtomIndex))
            iEnd++;
          bsSheet.set(iStart, iEnd);
          iStart = iEnd;
        } else {
          if (bsB.get(index))
            bsBridge.set(iStart);
          ++iStart;
        }
      }
      if (doReport) {
        ap.setTag(labels[i], bsBridge, 'B');
        ap.setTag(labels[i], bsSheet, 'E');
      } 
      if (setStructure) { 
        ap.setStructure(bsSheet, JmolConstants.PROTEIN_STRUCTURE_SHEET);
      }
      bsDone[i].or(bsSheet);
      bsDone[i].or(bsBridge);
    }    
  }

  private class Bridge {
    protected Atom a, b;
    protected int[][] ladder;
    protected boolean isAntiparallel;
    
    protected Bridge(Atom a, Atom b, Map<int[][], Boolean> htLadders) {
      this.a = a;
      this.b = b;
      ladder = new int[2][2];
      ladder[0][0] = ladder[0][1] = Math.min(a.index, b.index);
      ladder[1][0] = ladder[1][1] = Math.max(a.index, b.index);
      addLadder(htLadders);
    }
    
    private void addLadder(Map<int[][], Boolean> htLadders) {
      htLadders.put(ladder, (isAntiparallel ? Boolean.TRUE : Boolean.FALSE));
    }

    @Override
    public String toString() {
      return (isAntiparallel ? "a " : "p ") + a + " - " + b + "\t" + Escape.escape(ladder);
    }
    protected boolean addBridge(Bridge bridge,  Map<int[][], Boolean> htLadders) {
      if (bridge == null || bridge.isAntiparallel != isAntiparallel
          || !canAdd(bridge) || !bridge.canAdd(this))
        return false;
      extendLadder(bridge.ladder[0][0], bridge.ladder[1][0]);
      extendLadder(bridge.ladder[0][1], bridge.ladder[1][1]);
      bridge.ladder = ladder;
      if (bridge.ladder != ladder) {
        htLadders.remove(bridge.ladder);
        addLadder(htLadders);
      }
      return true;
    }

    private boolean canAdd(Bridge bridge) {
      int index1 = bridge.a.index;
      int index2 = bridge.b.index;
      // no crossing of ladder rungs (2WUJ)
      return (isAntiparallel ?
          (index1 >= ladder[0][1] && index2 <= ladder[1][0] 
          || index1 <= ladder[0][0] && index2 >= ladder[1][1]) 
        : (index1 <= ladder[0][0] && index2 <= ladder[1][0] 
          || index1 >= ladder[0][1] && index2 >= ladder[1][1]));
    }

    private void extendLadder(int index1, int index2) {
      if (ladder[0][0] > index1)
        ladder[0][0] = index1;
      if (ladder[0][1] < index1)
        ladder[0][1] = index1;
      if (ladder[1][0] > index2)
        ladder[1][0] = index2;
      if (ladder[1][1] < index2)
        ladder[1][1] = index2;
    }
  }
  
  /**
   * "ladder =: one or more consecutive bridges of identical type" (p. 2582)
   *   
   * "For beta structures, we define explicitly: a bulge-linked ladder consists
   *  of two (perfect) ladder or bridges of the same type connected by at most one
   *  extra residue on one strand and at most four extra resideus on the other
   *  strand.... all residues in bulge-linked ladders are marked "E," including
   *  the extra residues." (p. 2585)
   *  
   * @param bridges
   * @param htBridges
   * @param htLadders 
   * @param isAntiparallel 
   *  
   */
  private static void createLadders(List<Bridge> bridges,
                                 Map<String, Bridge> htBridges, 
                                 Map<int[][], Boolean> htLadders,
                                 boolean isAntiparallel) {
    int dir = (isAntiparallel ? -1 : 1);
    int n = bridges.size();
    for (int i = 0; i < n; i++)
      checkBridge(bridges.get(i), htBridges, htLadders, isAntiparallel, 1, dir);
    for (int i = 0; i < n; i++)
      checkBulge(bridges.get(i), htBridges, htLadders, isAntiparallel, 1);
  }

  /**
   * check to see if another bridge exists offset by n1 and n2 from the two ends of a bridge
   * 
   * @param bridge
   * @param htBridges
   * @param htLadders 
   * @param isAntiparallel
   * @param n1 
   * @param n2 
   * @return TRUE if bridge is part of a ladder
   */
  private static boolean checkBridge(Bridge bridge,
                                     Map<String, Bridge> htBridges,
                                     Map<int[][], Boolean> htLadders,
                                     boolean isAntiparallel, int n1, int n2) {
    Bridge b = htBridges.get(bridge.a.getOffsetResidueAtom("0", n1) + "-"
        + bridge.b.getOffsetResidueAtom("0", n2));
    return (b != null && bridge.addBridge(b, htLadders));
  }

  private static void checkBulge(Bridge bridge, Map<String, Bridge> htBridges,
                                 Map<int[][], Boolean> htLadders, boolean isAntiparallel, 
                                 int dir) {
    int dir1 = (isAntiparallel ? -1 : 1);
    for (int i = 0; i < 3; i++)
      for (int j = (i == 0 ? 1 : 0); j < 6; j++) {
        checkBridge(bridge, htBridges, htLadders, isAntiparallel, i * dir, j * dir1);
        if (j > i) 
          checkBridge(bridge, htBridges, htLadders, isAntiparallel, j * dir, i * dir1);
      }
  }

  private void setStructure(BitSet bs, byte type) {
    for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
      int i2 = bs.nextClearBit(i);
      if (i2 < 0)
        i2 = monomerCount;
      addSecondaryStructure(type, null, 0, 0, i, i2 - 1);
      i = i2;
    }
  }

  private static int[] isHbonded(int indexDonor, int indexAcceptor, int pDonor,
                                 int pAcceptor, int[][][][] min) {
    if (indexDonor < 0 || indexAcceptor < 0)
      return null;
    int[][][] min1 = min[pDonor];
    int[][][] min2 = min[pAcceptor];
    if (indexDonor >= min1.length || indexAcceptor >= min2.length)
      return null;
    return (min1[indexDonor][0][0] == pAcceptor
        && min1[indexDonor][0][1] == indexAcceptor ? min1[indexDonor][0]
        : min1[indexDonor][1][0] == pAcceptor
            && min1[indexDonor][1][1] == indexAcceptor ? min1[indexDonor][1]
            : null);
  }

  private void setTag(char[] tags, BitSet bs, char ch) {
    for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1))
      tags[i] = ch;
  }

  private String dumpSummary(char[] labels) {
    char id = monomers[0].getLeadAtom().getChainID();
    String prefix = (id == '\0' ? "" : String.valueOf(id) + ":");
    StringBuffer sb = new StringBuffer();
    char lastChar = '\0';
    char insCode1 = '\0';
    char insCode2 = '\0';
    int firstResno = -1, lastResno = -1;
    for (int i = 0; i <= monomerCount; i++) {
      if (i == monomerCount || labels[i] != lastChar) {
        if (lastChar != '\0')
          sb.append('\n').append(lastChar).append(" : ").append(prefix).append(firstResno)
          .append(insCode1 == '\0' ? "" : String.valueOf(insCode1))
              .append("_").append(prefix).append(lastResno)
              .append(insCode2 == '\0' ? "" : String.valueOf(insCode2)); 
        if (i == monomerCount)
          break;
        lastChar = labels[i];
        firstResno = monomers[i].getResno();
        insCode1 = monomers[i].getInsertionCode();
        
      }
      lastResno = monomers[i].getResno();
      insCode2 = monomers[i].getInsertionCode();
    }    
    return sb.toString();
  }

  private String dumpTags(String lines, BitSet bsBad, int mode) {
    String prefix = monomers[0].getLeadAtom().getChainID() 
    + "." + (bioPolymerIndexInModel + 1);
    lines = TextFormat.simpleReplace(lines, "$", prefix);
    int iFirst = monomers[0].getResno();
    String pre = "\n" + prefix;
    StringBuffer sb = new StringBuffer();
    StringBuffer sb0 = new StringBuffer(pre + ".8: ");
    StringBuffer sb1 = new StringBuffer(pre + ".7: ");
    StringBuffer sb2 = new StringBuffer(pre + ".6: ");
    StringBuffer sb3 = new StringBuffer(pre + ".0: ");
    int i = iFirst;
    for (int ii = 0; ii < monomerCount; ii++) {
      i = monomers[ii].getResno();
      sb0.append(i % 100 == 0 ? "" + ((i / 100) % 100) : " ");
      sb1.append(i % 10 == 0 ? "" + ((i / 10) % 10) : " ");
      sb2.append(i % 10);
      sb3.append(bsBad.get(monomers[ii].leadAtomIndex) ? 
          '!' : monomers[ii].getGroup1());
    }
    if ((mode & 1) == 1)
      sb.append(sb0).append(sb1).append(sb2);
    sb.append("\n");
    sb.append(lines);
    if ((mode & 2) == 2) {
      sb.append(sb3);
      sb.append("\n\n");
    }
    return sb.toString().replace('\0', '.');
  }

  ////////////////////////////////////////////////////////
  //
  // Ramachandran-angle-based structure determination 
  //
  //
  ////////////////////////////////////////////////////////
  /*
   * New code for assigning secondary structure based on 
   * phi-psi angles instead of hydrogen bond patterns.
   *
   * molvisions 2005 10 12
   *
   */

  @Override
  public void calculateStructures(boolean alphaOnly) {
    if (alphaOnly)
      return;
    if (structureList == null)
      structureList = model.getModelSet().getStructureList();
    char[] structureTags = new char[monomerCount];
    for (int i = 0; i < monomerCount - 1; ++i) {
      AminoMonomer leadingResidue = (AminoMonomer) monomers[i];
      AminoMonomer trailingResidue = (AminoMonomer) monomers[i + 1];
      float phi = trailingResidue.getGroupParameter(Token.phi);
      float psi = leadingResidue.getGroupParameter(Token.psi);
      if (isHelix(psi, phi)) {
        //this next is just Bob's attempt to separate different helices
        //it is CONSERVATIVE -- it displays fewer helices than before
        //thus allowing more turns and (presumably) better rockets.

        structureTags[i] = (phi < 0 && psi < 25 ? '4' : '3');
      } else if (isSheet(psi, phi)) {
        structureTags[i] = 's';
      } else if (isTurn(psi, phi)) {
        structureTags[i] = 't';
      } else {
        structureTags[i] = 'n';
      }

      if (Logger.debugging)
        Logger.debug((0 + this.monomers[0].getChainID()) + " aminopolymer:" + i
            + " " + trailingResidue.getGroupParameter(Token.phi) + ","
            + leadingResidue.getGroupParameter(Token.psi) + " "
            + structureTags[i]);
    }

    // build alpha helix stretches
    for (int start = 0; start < monomerCount; ++start) {
      if (structureTags[start] == '4') {
        int end;
        for (end = start + 1; end < monomerCount && structureTags[end] == '4'; ++end) {
        }
        end--;
        if (end >= start + 3) {
          addSecondaryStructure(JmolConstants.PROTEIN_STRUCTURE_HELIX, null, 0,
              0, start, end);
        }
        start = end;
      }
    }

    for (int start = 0; start < monomerCount; ++start) {
      if (structureTags[start] == '3') {
        int end;
        for (end = start + 1; end < monomerCount && structureTags[end] == '3'; ++end) {
        }
        end--;
        if (end >= start + 3) {
          addSecondaryStructure(JmolConstants.PROTEIN_STRUCTURE_HELIX, null, 0,
              0, start, end);
        }
        start = end;
      }
    }

    // build beta sheet stretches
    for (int start = 0; start < monomerCount; ++start) {
      if (structureTags[start] == 's') {
        int end;
        for (end = start + 1; end < monomerCount && structureTags[end] == 's'; ++end) {
        }
        end--;
        if (end >= start + 2) {
          addSecondaryStructure(JmolConstants.PROTEIN_STRUCTURE_SHEET, null, 0,
              0, start, end);
        }
        start = end;
      }
    }

    // build turns
    for (int start = 0; start < monomerCount; ++start) {
      if (structureTags[start] == 't') {
        int end;
        for (end = start + 1; end < monomerCount && structureTags[end] == 't'; ++end) {
        }
        end--;
        if (end >= start + 2) {
          addSecondaryStructure(JmolConstants.PROTEIN_STRUCTURE_TURN, null, 0,
              0, start, end);
        }
        start = end;
      }
    }
  }

  /**
   * 
   * @param psi N-C-CA-N torsion for NEXT group
   * @param phi C-CA-N-C torsion for THIS group
   * @return whether this corresponds to a helix
   */
  private boolean isTurn(float psi, float phi) {
    return checkPhiPsi(structureList[JmolConstants.PROTEIN_STRUCTURE_TURN],
        psi, phi);
  }

  private boolean isSheet(float psi, float phi) {
    return checkPhiPsi(structureList[JmolConstants.PROTEIN_STRUCTURE_SHEET],
        psi, phi);
  }

  private boolean isHelix(float psi, float phi) {
    return checkPhiPsi(structureList[JmolConstants.PROTEIN_STRUCTURE_HELIX],
        psi, phi);
  }

  private static boolean checkPhiPsi(float[] list, float psi, float phi) {
    for (int i = 0; i < list.length; i += 4)
      if (phi >= list[i] && phi <= list[i + 1] && psi >= list[i + 2]
          && psi <= list[i + 3])
        return true;
    return false;
  }

  private float[][] structureList; // kept in StateManager.globalSettings

  @Override
  public void setStructureList(float[][] structureList) {
    this.structureList = structureList;
  }


}
