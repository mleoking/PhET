/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-04-09 19:01:40 -0500 (Mon, 09 Apr 2007) $
 * $Revision: 7365 $

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
 *  Lesser General License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.jmol.shapebio;

import java.util.BitSet;
import java.util.Map;

import javax.vecmath.Vector3f;

import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.Atom;
import org.jmol.modelsetbio.BioPolymer;
import org.jmol.modelsetbio.Monomer;
import org.jmol.modelsetbio.NucleicMonomer;
import org.jmol.modelsetbio.NucleicPolymer;
import org.jmol.shape.Shape;
import org.jmol.shape.Mesh;
import org.jmol.util.Logger;
import org.jmol.viewer.JmolConstants;

class BioShape {

  int modelIndex;
  int modelVisibilityFlags = 0;

  BioShapeCollection shape;
  
  BioPolymer bioPolymer;
  
  Mesh[] meshes;
  boolean[] meshReady;

  short[] mads;
  short[] colixes;
  byte[] paletteIDs;

  BitSet bsColixSet;
  BitSet bsSizeSet;
  BitSet bsSizeDefault = new BitSet();
  boolean isActive;
  
  int monomerCount;
  Monomer[] monomers;

  Vector3f[] wingVectors;
  int[] leadAtomIndices;

  BioShape(BioShapeCollection shape, int modelIndex, BioPolymer bioPolymer) {
    this.shape = shape;
    this.modelIndex = modelIndex;
    this.bioPolymer = bioPolymer;
    isActive = shape.isActive;
    monomerCount = bioPolymer.monomerCount;
    if (monomerCount > 0) {
      colixes = new short[monomerCount];
      paletteIDs = new byte[monomerCount];
      mads = new short[monomerCount + 1];
      monomers = (Monomer[]) bioPolymer.getGroups();
      meshReady = new boolean[monomerCount];
      meshes = new Mesh[monomerCount];
      wingVectors = bioPolymer.getWingVectors();
      leadAtomIndices = bioPolymer.getLeadAtomIndices();
      //Logger.debug("mps assigning wingVectors and leadMidpoints");
    }
  }

  boolean hasBfactorRange = false;
  int bfactorMin, bfactorMax;
  int range;
  float floatRange;

  void calcBfactorRange() {
    bfactorMin = bfactorMax =
      monomers[0].getLeadAtom().getBfactor100();
    for (int i = monomerCount; --i > 0; ) {
      int bfactor =
        monomers[i].getLeadAtom().getBfactor100();
      if (bfactor < bfactorMin)
        bfactorMin = bfactor;
      else if (bfactor > bfactorMax)
        bfactorMax = bfactor;
    }
    range = bfactorMax - bfactorMin;
    floatRange = range;
    hasBfactorRange = true;
  }

  private final static double eightPiSquared100 = 8 * Math.PI * Math.PI * 100;
  /**
   * Calculates the mean positional displacement in milliAngstroms.
   * <p>
   * <a href='http://www.rcsb.org/pdb/lists/pdb-l/200303/000609.html'>
   * http://www.rcsb.org/pdb/lists/pdb-l/200303/000609.html
   * </a>
   * <code>
   * > -----Original Message-----
   * > From: pdb-l-admin@sdsc.edu [mailto:pdb-l-admin@sdsc.edu] On 
   * > Behalf Of Philipp Heuser
   * > Sent: Thursday, March 27, 2003 6:05 AM
   * > To: pdb-l@sdsc.edu
   * > Subject: pdb-l: temperature factor; occupancy
   * > 
   * > 
   * > Hi all!
   * > 
   * > Does anyone know where to find proper definitions for the 
   * > temperature factors 
   * > and the values for occupancy?
   * > 
   * > Alright I do know, that the atoms with high temperature 
   * > factors are more 
   * > disordered than others, but what does a temperature factor of 
   * > a specific 
   * > value mean exactly.
   * > 
   * > 
   * > Thanks in advance!
   * > 
   * > Philipp
   * > 
   * pdb-l: temperature factor; occupancy
   * Bernhard Rupp br@llnl.gov
   * Thu, 27 Mar 2003 08:01:29 -0800
   * 
   * * Previous message: pdb-l: temperature factor; occupancy
   * * Next message: pdb-l: Structural alignment?
   * * Messages sorted by: [ date ] [ thread ] [ subject ] [ author ]
   * 
   * Isotropic B is defined as 8*pi**2<u**2>.
   * 
   * Meaning: eight pi squared =79
   * 
   * so B=79*mean square displacement (from rest position) of the atom.
   * 
   * as u is in Angstrom, B must be in Angstrom squared.
   * 
   * example: B=79A**2
   * 
   * thus, u=sqrt([79/79]) = 1 A mean positional displacement for atom.
   * 
   * 
   * See also 
   * 
   * http://www-structure.llnl.gov/Xray/comp/comp_scat_fac.htm#Atomic
   * 
   * for more examples.
   * 
   * BR
   *</code>
   *
   * @param bFactor100
   * @return ?
   */
  short calcMeanPositionalDisplacement(int bFactor100) {
    return (short)(Math.sqrt(bFactor100/eightPiSquared100) * 1000);
  }

  void findNearestAtomIndex(int xMouse, int yMouse, Atom[] closest, BitSet bsNot) {
    bioPolymer.findNearestAtomIndex(xMouse, yMouse, closest, mads,
        shape.myVisibilityFlag, bsNot);
  }
  
  void setMad(short mad, BitSet bsSelected, float[] values) {
    isActive = true;
    if (bsSizeSet == null)
      bsSizeSet = new BitSet();
    int flag = shape.myVisibilityFlag;
    for (int i = monomerCount; --i >= 0; ) {
      int leadAtomIndex = leadAtomIndices[i];
      if (bsSelected.get(leadAtomIndex)) {
        if (values != null) {
          if (Float.isNaN(values[leadAtomIndex]))
            continue;
          mad = (short) (values[leadAtomIndex] * 2000);
        }
        boolean isVisible = ((mads[i] = setMad(i, mad)) > 0);
        bsSizeSet.set(i, isVisible);
        monomers[i].setShapeVisibility(flag, isVisible);
        shape.atoms[leadAtomIndex].setShapeVisibility(flag,isVisible);
        falsifyMesh(i, true);
      }
    }
    if (monomerCount > 1)
      mads[monomerCount] = mads[monomerCount - 1];
  }

  private short setMad(int groupIndex, short mad) {
    //undocumented
    bsSizeDefault.set(groupIndex, mad == -1 || mad == -2);
    if (mad >= 0)
      return mad;      
    switch (mad) {
    case -1: // trace on
    case -2: // trace structure
      if (mad == -1 && shape.madOn >= 0)
        return shape.madOn;
      switch (monomers[groupIndex].getProteinStructureType()) {
      case JmolConstants.PROTEIN_STRUCTURE_SHEET:
      case JmolConstants.PROTEIN_STRUCTURE_HELIX:
        return shape.madHelixSheet;
      case JmolConstants.PROTEIN_STRUCTURE_DNA:
      case JmolConstants.PROTEIN_STRUCTURE_RNA:
        return shape.madDnaRna;
      default:
        return shape.madTurnRandom;
      }
    case -3: // trace temperature
      {
        if (! hasBfactorRange)
          calcBfactorRange();
        Atom atom = monomers[groupIndex].getLeadAtom();
        int bfactor100 = atom.getBfactor100(); // scaled by 1000
        int scaled = bfactor100 - bfactorMin;
        if (range == 0)
          return (short)0;
        float percentile = scaled / floatRange;
        if (percentile < 0 || percentile > 1)
          Logger.error("Que ha ocurrido? " + percentile);
        return (short)((1750 * percentile) + 250);
      }
    case -4: // trace displacement
      {
        Atom atom = monomers[groupIndex].getLeadAtom();
        return // double it ... we are returning a diameter
          (short)(2 * calcMeanPositionalDisplacement(atom.getBfactor100()));
      }
    }
    Logger.error("unrecognized setMad(" + mad + ")");
    return 0;
  }

  void falsifyMesh(int index, boolean andNearby) {
    if (meshReady == null)
      return;
    meshReady[index] = false;
    if (!andNearby)
      return;
    if (index > 0)
      meshReady[index - 1] = false;
    if (index < monomerCount - 1)
      meshReady[index + 1] = false;
  }    

  void setColix(short colix, byte pid, BitSet bsSelected) {
    isActive = true;
    if (bsColixSet == null)
      bsColixSet = new BitSet();
    for (int i = monomerCount; --i >= 0;) {
      int atomIndex = leadAtomIndices[i];
      if (bsSelected.get(atomIndex)) {
        colixes[i] = shape.setColix(colix, pid, atomIndex);
        paletteIDs[i] = pid;
        bsColixSet.set(i, colixes[i] != Graphics3D.INHERIT_ALL);
      }
    }
  }
  
  void setTranslucent(boolean isTranslucent, BitSet bsSelected, float translucentLevel) {
    isActive = true;
    if (bsColixSet == null)
      bsColixSet = new BitSet();
    for (int i = monomerCount; --i >= 0; )
      if (bsSelected.get(leadAtomIndices[i])) {
        colixes[i] = Graphics3D.getColixTranslucent(colixes[i], isTranslucent, translucentLevel);
        bsColixSet.set(i, colixes[i] != Graphics3D.INHERIT_ALL);
    }
  }

  void setShapeState(Map<String, BitSet> temp,
                     Map<String, BitSet> temp2) {
    if (!isActive || bsSizeSet == null && bsColixSet == null)
      return;
    String type = JmolConstants.shapeClassBases[shape.shapeID];
    for (int i = 0; i < monomerCount; i++) {
      int atomIndex1 = monomers[i].firstAtomIndex;
      int atomIndex2 = monomers[i].lastAtomIndex;
      if (bsSizeSet != null && (bsSizeSet.get(i) 
          || bsColixSet != null && bsColixSet.get(i))) {//shapes MUST have been set with a size
        if (bsSizeDefault.get(i))
          Shape.setStateInfo(temp, atomIndex1, atomIndex2, type + (bsSizeSet.get(i) ? " on" : " off"));
        else
          Shape.setStateInfo(temp, atomIndex1, atomIndex2, type + " "
              + (mads[i] / 2000f));
      }
      if (bsColixSet != null && bsColixSet.get(i))
        Shape.setStateInfo(temp2, atomIndex1, atomIndex2, shape
            .getColorCommand(type, paletteIDs[i], colixes[i]));
    }
  }  

 void setModelClickability() {
    if (!isActive || wingVectors == null)
      return;
    boolean isNucleicPolymer = bioPolymer instanceof NucleicPolymer;
    for (int i = monomerCount; --i >= 0;) {
      if (mads[i] <= 0)
        continue;
      int iAtom = leadAtomIndices[i];
      if (monomers[i].getModel().isAtomHidden(iAtom))
        continue;
      shape.atoms[iAtom].setClickable(JmolConstants.ALPHA_CARBON_VISIBILITY_FLAG);
      if (isNucleicPolymer)
        ((NucleicMonomer) monomers[i]).setModelClickability();
    }
  }
  
}