/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2010-11-13 11:00:48 -0800 (Sat, 13 Nov 2010) $
 * $Revision: 14647 $
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

import java.util.BitSet;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;


import org.jmol.util.OutputStringBuffer;
import org.jmol.viewer.Viewer;

abstract public class Polymer {

  /*
   * this is a new class of "polymer" that does not necessarily have anything
   * created from it. Jmol can use it instead of any bioPolymer subclass, since
   * there are now no references to any bio-related classes in Viewer. 
   * 
   * 
   */
  
  // these arrays will be one longer than the polymerCount
  // we probably should have better names for these things
  // holds center points between alpha carbons or sugar phosphoruses

  public Model model;
  protected Point3f[] leadMidpoints;
  protected Point3f[] leadPoints;
  protected Point3f[] controlPoints;
  // holds the vector that runs across the 'ribbon'
  protected Vector3f[] wingVectors;

  protected int[] leadAtomIndices;

  protected int type = TYPE_NOBONDING;
  public int bioPolymerIndexInModel;
  public int monomerCount;

  protected final static int TYPE_NOBONDING = 0; // could be phosphorus or alpha
  protected final static int TYPE_AMINO = 1;
  protected final static int TYPE_NUCLEIC = 2;
  protected final static int TYPE_CARBOHYDRATE = 3;
  
  protected Polymer() {
  }

  public int getType() {
    return type;
  }

  /**
   * 
   * @param bs
   */
  public void getRange(BitSet bs) {
  }

  /**
   * 
   * @param last
   * @param bs
   * @param vList
   * @param isTraceAlpha
   * @param sheetSmoothing
   * @return number of points
   */
  public int getPolymerPointsAndVectors(int last, BitSet bs, List<Point3f[]> vList,
                                        boolean isTraceAlpha,
                                        float sheetSmoothing) {
    return 0;
  }

  /**
   * 
   * @param type
   * @param structureID
   * @param serialID
   * @param strandCount
   * @param startChainID
   * @param startSeqcode
   * @param endChainID
   * @param endSeqcode
   */
  public void addSecondaryStructure(byte type, 
                                    String structureID, int serialID, int strandCount,
                                    char startChainID,
                                    int startSeqcode, char endChainID,
                                    int endSeqcode) {
  }

  /**
   * @param bioPolymers
   * @param bioPolymerCount
   * @param vHBonds TODO
   * @param doReport
   * @param dsspIgnoreHydrogen 
   * @param setStructure
   * @return                  DSSP report
   */
  public String calculateStructures(Polymer[] bioPolymers, int bioPolymerCount,
                                    List<Bond> vHBonds, boolean doReport,
                                    boolean dsspIgnoreHydrogen,
                                    boolean setStructure) {
    return null;
  }

  /**
   * @param alphaOnly  
   */
  public void calculateStructures(boolean alphaOnly) {
  }

  public void clearStructures() {
  }

  public String getSequence() {
    return "";
  }

  
  /**
   * 
   * @param bs
   * @return info
   */
  public Map<String, Object> getPolymerInfo(BitSet bs) {
    return null;
  }

  /**
   * 
   * @param bsConformation
   */
  public void setConformation(BitSet bsConformation) {
  }

  /**
   * 
   * @param polymer
   * @param bsA
   * @param bsB
   * @param vHBonds
   * @param nMaxPerResidue
   * @param min 
   * @param checkDistances
   * @param dsspIgnoreHydrogens 
   */
  public void calcRasmolHydrogenBonds(Polymer polymer, BitSet bsA,
           BitSet bsB, List<Bond> vHBonds, int nMaxPerResidue, int[][][] min, 
           boolean checkDistances, boolean dsspIgnoreHydrogens) {
    // subclasses should override if they know how to calculate hbonds
  }
  
  /**
   * 
   * @param bsSelected
   */
  public void calcSelectedMonomersCount(BitSet bsSelected) {
  }

  /**
   * 
   * @param group1
   * @param nGroups
   * @param bsInclude
   * @param bsResult
   */
  public void getPolymerSequenceAtoms(int group1,
                                      int nGroups, BitSet bsInclude,
                                      BitSet bsResult) {
  }

  public Point3f[] getLeadMidpoints() {
    return null;
  }
  
  public void recalculateLeadMidpointsAndWingVectors() {  
  }
  
  /**
   * 
   * @param viewer
   * @param ctype
   * @param qtype
   * @param mStep
   * @param derivType
   * @param bsAtoms
   * @param bsSelected
   * @param bothEnds
   * @param isDraw
   * @param addHeader
   * @param tokens
   * @param pdbATOM
   * @param pdbCONECT
   * @param bsWritten
   */
  public void getPdbData(Viewer viewer, char ctype, char qtype, int mStep, int derivType, 
              BitSet bsAtoms, BitSet bsSelected, boolean bothEnds, 
              boolean isDraw, boolean addHeader, LabelToken[] tokens, 
              OutputStringBuffer pdbATOM, StringBuffer pdbCONECT, BitSet bsWritten) {
    return;
  }

  /**
   * 
   * @param modelSet
   * @param atoms
   * @param bs1
   * @param bs2
   * @param vCA
   * @param thresh
   * @param delta
   * @param allowMultiple
   * @return List [ {atom1, atom2}, {atom1, atom2}...]
   */
  public List<Atom[]> calculateStruts(ModelSet modelSet, Atom[] atoms, BitSet bs1, BitSet bs2,
      List<Atom> vCA, float thresh, int delta, boolean allowMultiple) {
    return null;
  }

  public boolean isDna() { return false; }
  public boolean isRna() { return false; }

  /**
   * 
   * @param residues
   * @param bs
   * @param bsResult
   */
  public void getRangeGroups(int residues, BitSet bs, BitSet bsResult) {
  }

  public Group[] getGroups() {
    return null;
  }

  /**
   * @param structureList  protein only -- helix, sheet, turn definitions
   */
  public void setStructureList(float[][] structureList) {
  }

  /**
   * @param bsConformation  
   * @param conformationIndex 
   */
  public void getConformation(BitSet bsConformation, int conformationIndex) {
  }
}
