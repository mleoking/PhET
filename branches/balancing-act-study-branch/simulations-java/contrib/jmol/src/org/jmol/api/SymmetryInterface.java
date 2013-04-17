package org.jmol.api;

import java.util.BitSet;
import java.util.Map;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;

import org.jmol.modelset.Atom;

public interface SymmetryInterface {

  public abstract SymmetryInterface setPointGroup(
                                     SymmetryInterface pointGroupPrevious,
                                     Atom[] atomset, BitSet bsAtoms,
                                     boolean haveVibration,
                                     float distanceTolerance,
                                     float linearTolerance);

  public abstract String getPointGroupName();

  public abstract Object getPointGroupInfo(int modelIndex, boolean asDraw,
                                           boolean asInfo, String type,
                                           int index, float scale);

  public abstract void setSpaceGroup(boolean doNormalize);

  public abstract int addSpaceGroupOperation(String xyz, int opId);

  /**
   * set symmetry lattice type using Hall rotations
   * 
   * @param latt SHELX index or character lattice character P I R F A B C S T or \0
   * 
   */
  public abstract void setLattice(int latt);

  public abstract String getSpaceGroupName();

  public abstract Object getSpaceGroup();

  public abstract void setSpaceGroup(SymmetryInterface symmetry);

  public abstract boolean createSpaceGroup(int desiredSpaceGroupIndex,
                                           String name,
                                           float[] notionalUnitCell);

  public abstract boolean haveSpaceGroup();

  public abstract String getSpaceGroupInfo(String name, SymmetryInterface cellInfo);

  public abstract Object getLatticeDesignation();

  public abstract void setFinalOperations(Point3f[] atoms, int iAtomFirst,
                                          int noSymmetryCount,
                                          boolean doNormalize);

  public abstract int getSpaceGroupOperationCount();

  public abstract Matrix4f getSpaceGroupOperation(int i);

  public abstract String getSpaceGroupXyz(int i, boolean doNormalize);

  public abstract void newSpaceGroupPoint(int i, Point3f atom1, Point3f atom2,
                                          int transX, int transY, int transZ);

  public abstract Object rotateEllipsoid(int i, Point3f ptTemp,
                                         Vector3f[] axes, Point3f ptTemp1,
                                         Point3f ptTemp2);

  public abstract void setUnitCellAllFractionalRelative(boolean TF);
  
  public abstract void setUnitCell(float[] notionalUnitCell);

  public abstract void toCartesian(Point3f pt, boolean asAbsolue);

  public abstract Object[] getEllipsoid(float[] parBorU);

  public abstract Point3f ijkToPoint3f(int nnn);

  public abstract void toFractional(Point3f pt, boolean isAbsolute);

  public abstract Point3f[] getUnitCellVertices();

  public abstract Point3f[] getCanonicalCopy(float scale);

  public abstract Point3f getCartesianOffset();

  public abstract float[] getNotionalUnitCell();

  public abstract float[] getUnitCellAsArray();

  public abstract void toUnitCell(Point3f pt, Point3f offset);

  public abstract void setUnitCellOffset(Point3f pt);

  public abstract void setOffset(int nnn);

  public abstract Point3f getFractionalOffset();

  public abstract float getUnitCellInfo(int infoType);

  public abstract int getModelIndex();

  public abstract void setModelIndex(int i);

  public abstract boolean getCoordinatesAreFractional();

  public abstract int[] getCellRange();

  public abstract String getSymmetryInfoString();

  public abstract String[] getSymmetryOperations();

  public abstract boolean haveUnitCell();

  public abstract String getUnitCellInfo();

  public abstract boolean isPeriodic();

  public abstract void setSymmetryInfo(int modelIndex, Map<String, Object> modelAuxiliaryInfo);

  public abstract Object[] getSymmetryOperationDescription(int iSym,
                                                         SymmetryInterface cellInfo, 
                                                         Point3f pt1, Point3f pt2, String id);

  public abstract boolean isPolymer();

  public abstract boolean isSlab();

  public abstract void addSpaceGroupOperation(Matrix4f mat);

  public abstract void setMinMaxLatticeParameters(Point3i minXYZ, Point3i maxXYZ);

  public abstract void setUnitCellOrientation(Matrix3f matUnitCellOrientation);

  public abstract String getMatrixFromString(String xyz, float[] temp, boolean allowScaling);

  public abstract Matrix4f getMatrixToCartesians();

  public abstract boolean checkDistance(Point3f f1, Point3f f2, float distance, 
                                        float dx, int iRange, int jRange, int kRange, Point3f ptOffset);
}
