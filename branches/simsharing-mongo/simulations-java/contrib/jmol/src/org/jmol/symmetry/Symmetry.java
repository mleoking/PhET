/* $RCSfiodelle$allrueFFFF
 * $Author: egonw $
 * $Date: 2005-11-10 09:52:44 -0600 (Thu, 10 Nov 2005) $
 * $Revision: 4255 $
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


package org.jmol.symmetry;

import java.util.BitSet;
import java.util.Map;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;

import org.jmol.api.SymmetryInterface;
import org.jmol.modelset.Atom;
import org.jmol.util.Logger;

public class Symmetry implements SymmetryInterface {
  
  // NOTE: THIS CLASS IS VERY IMPORTANT.
  // IN ORDER TO MODULARIZE IT, IT IS REFERENCED USING 
  // xxxx = (SymmetryInterface) Interface.getOptionInterface("symmetry.Symmetry");

  /* Symmetry is a wrapper class that allows access to the package-local
   * classes PointGroup, SpaceGroup, SymmetryInfo, and UnitCell.
   * 
   * When symmetry is detected in ANY model being loaded, a SymmetryInterface
   * is established for ALL models.
   * 
   * The SpaceGroup information could be saved with each model, but because this 
   * depends closely on what atoms have been selected, and since tracking that with atom
   * deletion is a bit complicated, instead we just use local instances of that class.
   * 
   * The three PointGroup methods here could be their own interface; they are just here
   * for convenience.
   * 
   * The file readers use SpaceGroup and UnitCell methods
   * 
   * The modelSet and modelLoader classes use UnitCell and SymmetryInfo 
   * 
   */
  private PointGroup pointGroup;
  private SpaceGroup spaceGroup;
  private SymmetryInfo symmetryInfo;
  private UnitCell unitCell;
  
  public Symmetry() {
    // instantiated ONLY using
    // symmetry = (SymmetryInterface) Interface.getOptionInterface("symmetry.Symmetry");
    // DO NOT use symmetry = new Symmetry();
    // as that will invalidate the Jar file modularization    
  }
  
  public SymmetryInterface setPointGroup(SymmetryInterface siLast,
                                         Atom[] atomset, BitSet bsAtoms,
                                         boolean haveVibration,
                                         float distanceTolerance,
                                         float linearTolerance) {
    pointGroup = PointGroup.getPointGroup(siLast == null ? null
        : ((Symmetry) siLast).pointGroup, atomset, bsAtoms, haveVibration,
        distanceTolerance, linearTolerance);
    return this;
  }
  
  public String getPointGroupName() {
    return pointGroup.getName();
  }

  public Object getPointGroupInfo(int modelIndex, boolean asDraw,
                                  boolean asInfo, String type, int index,
                                  float scale) {
    if (!asDraw && !asInfo && pointGroup.textInfo != null)
      return pointGroup.textInfo;
    else if (asDraw && pointGroup.isDrawType(type, index))
      return pointGroup.drawInfo;
    else if (asInfo && pointGroup.info != null)
      return pointGroup.info;
    return pointGroup.getInfo(modelIndex, asDraw, asInfo, type, index, scale);
  }

  // SpaceGroup methods
  
  public void setSpaceGroup(boolean doNormalize) {
    if (spaceGroup == null)
      spaceGroup = new SpaceGroup(doNormalize);
  }

  public int addSpaceGroupOperation(String xyz, int opId) {
    return spaceGroup.addSymmetry(xyz, opId);
  }

  public void addSpaceGroupOperation(Matrix4f mat) {
    spaceGroup.addSymmetry("=" + 
        SymmetryOperation.getXYZFromMatrix(mat, false, false, false), 0);    
  }

  public void setLattice(int latt) {
    spaceGroup.setLattice(latt);
  }

  public String getSpaceGroupName() {
    return (symmetryInfo != null ? symmetryInfo.spaceGroup
        : spaceGroup != null ? spaceGroup.getName() : "");
  }

  public Object getSpaceGroup() {
    return spaceGroup;
  }
  
  public void setSpaceGroup(SymmetryInterface symmetry) {
    spaceGroup = (symmetry == null ? null : (SpaceGroup) symmetry.getSpaceGroup());
  }

  public boolean createSpaceGroup(int desiredSpaceGroupIndex, String name,
                                  float[] notionalUnitCell) {
    spaceGroup = SpaceGroup.createSpaceGroup(desiredSpaceGroupIndex, name,
        notionalUnitCell);
    if (spaceGroup != null && Logger.debugging)
      Logger.debug("using generated space group " + spaceGroup.dumpInfo(null));
    return spaceGroup != null;
  }

  public boolean haveSpaceGroup() {
    return (spaceGroup != null);
  }

  public String getSpaceGroupInfo(String name, SymmetryInterface cellInfo) {
    return SpaceGroup.getInfo(name, cellInfo);
  }

  public Object getLatticeDesignation() {
    return spaceGroup.getLatticeDesignation();
  }

  public void setFinalOperations(Point3f[] atoms, int iAtomFirst, int noSymmetryCount, boolean doNormalize) {
    spaceGroup.setFinalOperations(atoms, iAtomFirst, noSymmetryCount, doNormalize);
  }

  public int getSpaceGroupOperationCount() {
    return spaceGroup.finalOperations.length;
  }  
  
  public Matrix4f getSpaceGroupOperation(int i) {
    return spaceGroup.finalOperations[i];
  }

  public String getSpaceGroupXyz(int i, boolean doNormalize) {
    return spaceGroup.finalOperations[i].getXyz(doNormalize);
  }

  public void newSpaceGroupPoint(int i, Point3f atom1, Point3f atom2,
                       int transX, int transY, int transZ) {
    if (spaceGroup.finalOperations == null) {
      // temporary spacegroups don't have to have finalOperations
      if (!spaceGroup.operations[i].isFinalized)
        spaceGroup.operations[i].doFinalize();
      spaceGroup.operations[i].newPoint(atom1, atom2, transX, transY, transZ);
      return;
    }
    spaceGroup.finalOperations[i].newPoint(atom1, atom2, transX, transY, transZ);
  }
    
  public Object rotateEllipsoid(int i, Point3f ptTemp, Vector3f[] axes, Point3f ptTemp1,
                                Point3f ptTemp2) {
    return spaceGroup.finalOperations[i].rotateEllipsoid(ptTemp, axes, unitCell, ptTemp1,
        ptTemp2);
  }

  // UnitCell methods
  
  public boolean haveUnitCell() {
    return (unitCell != null);
  }

  public String getUnitsymmetryInfo() {
    return (unitCell == null ? "no unit cell information" : unitCell.dumpInfo(false));
  }

  public void setUnitCell(float[] notionalUnitCell) {
    unitCell = new UnitCell(notionalUnitCell);
  }

  public void setUnitCellOrientation(Matrix3f matUnitCellOrientation) {
    if (unitCell != null)
      unitCell.setOrientation(matUnitCellOrientation);
  }

  public void toCartesian(Point3f fpt, boolean isAbsolute) {
    if (unitCell == null)
      return;
    unitCell.toCartesian(fpt, isAbsolute);
    
  }

  public Matrix4f getMatrixToCartesians() {
    return (unitCell == null ? null : unitCell.matrixFractionalToCartesian);
  }

  public Object[] getEllipsoid(float[] parBorU) {
    return unitCell.getEllipsoid(parBorU);
  }

  public Point3f ijkToPoint3f(int nnn) {
    return UnitCell.ijkToPoint3f(nnn);
  }

  public void toFractional(Point3f pt, boolean isAbsolute) {
    if (unitCell != null)
      unitCell.toFractional(pt, isAbsolute);
  }

  public Point3f[] getUnitCellVertices() {
    return unitCell.getVertices();
  }

  public Point3f getCartesianOffset() {
    return unitCell.getCartesianOffset();
  }

  public float[] getNotionalUnitCell() {
    return unitCell == null ? null : unitCell.getNotionalUnitCell();
  }

  public float[] getUnitCellAsArray() {
    return unitCell == null ? null : unitCell.getUnitCellAsArray();
  }

  public void toUnitCell(Point3f pt, Point3f offset) {
    if (/*(symmetryInfo == null || symmetryInfo.isMultiCell)
        && why that ? should be OK for PDB files as well */ unitCell != null)
      unitCell.toUnitCell(pt, offset);
  }

  public void setUnitCellOffset(Point3f pt) {
    unitCell.setOffset(pt);
  }

  public void setOffset(int nnn) {
    unitCell.setOffset(nnn);
  }

  public Point3f getFractionalOffset() {
    return unitCell.getFractionalOffset();
  }

  public Point3f[] getCanonicalCopy(float scale) {
    return unitCell.getCanonicalCopy(scale);
  }

  public float getUnitsymmetryInfo(int infoType) {
    return unitCell.getInfo(infoType);
  }

  public int getModelIndex() {
    return symmetryInfo.modelIndex;
  }

  public void setModelIndex(int i) {
    symmetryInfo.modelIndex = i;    
  }

  public boolean getCoordinatesAreFractional() {
    return symmetryInfo.coordinatesAreFractional;
  }

  public int[] getCellRange() {
    return symmetryInfo.cellRange;
  }

  public String getSymmetryInfoString() {
    return symmetryInfo.symmetryInfoString;
  }

  public String[] getSymmetryOperations() {
    return symmetryInfo.symmetryOperations;
  }

  public boolean isPeriodic() {
    return symmetryInfo.isPeriodic();
  }

  public void setSymmetryInfo(int modelIndex, Map<String, Object> modelAuxiliaryInfo) {
    symmetryInfo = new SymmetryInfo();
    float[] notionalUnitcell = symmetryInfo.setSymmetryInfo(modelIndex,
        modelAuxiliaryInfo);
    if (notionalUnitcell == null)
      return;
    setUnitCell(notionalUnitcell);
    modelAuxiliaryInfo.put("infoUnitCell", getUnitCellAsArray());
    setUnitCellOffset((Point3f) modelAuxiliaryInfo.get("unitCellOffset"));
    if (modelAuxiliaryInfo.containsKey("jmolData"))
      setUnitCellAllFractionalRelative(true);
    Matrix3f matUnitCellOrientation = (Matrix3f) modelAuxiliaryInfo.get("matUnitCellOrientation");
    if (matUnitCellOrientation != null)
      setUnitCellOrientation(matUnitCellOrientation);
    if (Logger.debugging)
      Logger
          .debug("symmetryInfos[" + modelIndex + "]:\n" + unitCell.dumpInfo(true));
  }

  public float getUnitCellInfo(int infoType) {
    return unitCell.getInfo(infoType);
  }

  public String getUnitCellInfo() {
    return (unitCell == null ? "no unit cell information" 
        : unitCell.dumpInfo(false));
  }

  public Object[] getSymmetryOperationDescription(int isym,
                                                SymmetryInterface cellInfo, 
                                                Point3f pt1, Point3f pt2, String id) {
    return spaceGroup.operations[isym].getDescription(cellInfo, pt1, pt2, id);
  }
  
  public boolean isSlab() {
    return (unitCell == null ? false : unitCell.isSlab());
  }

  public boolean isPolymer() {
    return (unitCell == null ? false : unitCell.isPolymer());
  }

  public void setMinMaxLatticeParameters(Point3i minXYZ, Point3i maxXYZ) {
    if (unitCell != null)
      unitCell.setMinMaxLatticeParameters(minXYZ, maxXYZ);
  }

  public void setUnitCellAllFractionalRelative(boolean TF) {
    if (unitCell != null)
      unitCell.setAllFractionalRelative(TF);
  }

  public String getMatrixFromString(String xyz, float[] temp, boolean allowScaling) {
    return SymmetryOperation.getMatrixFromString(xyz, temp, false, allowScaling);
  }

  public boolean checkDistance(Point3f f1, Point3f f2, float distance, float dx, 
                               int iRange, int jRange, int kRange, Point3f ptOffset) {
    return (unitCell != null && unitCell.checkDistance(f1, f2, distance, dx, 
        iRange, jRange, kRange, ptOffset));
  }

}  
