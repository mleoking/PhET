/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-10-14 12:33:20 -0500 (Sun, 14 Oct 2007) $
 * $Revision: 8408 $

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
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.jmol.modelset;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.vecmath.Matrix3f;
import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Vector3f;

import org.jmol.api.AtomIndexIterator;
import org.jmol.api.Interface;
import org.jmol.api.JmolAdapter;
import org.jmol.api.JmolBioResolver;
import org.jmol.api.JmolEdge;
import org.jmol.api.JmolMolecule;
import org.jmol.api.SymmetryInterface;
import org.jmol.bspt.Bspf;
import org.jmol.bspt.CubeIterator;
import org.jmol.util.ArrayUtil;
import org.jmol.util.BitSetUtil;
import org.jmol.util.Escape;

import org.jmol.util.Elements;
import org.jmol.util.Logger;
import org.jmol.util.Measure;
import org.jmol.util.OutputStringBuffer;
import org.jmol.util.Parser;
import org.jmol.util.Point3fi;
import org.jmol.util.Quaternion;
import org.jmol.util.TextFormat;
import org.jmol.util.TriangleData;
import org.jmol.util.XmlUtil;
import org.jmol.viewer.JmolConstants;
import org.jmol.viewer.ShapeManager;
import org.jmol.script.Token;
import org.jmol.viewer.Viewer;
import org.jmol.viewer.StateManager.Orientation;

abstract public class ModelCollection extends BondCollection {

  /**
   * initial transfer of model data from old to new model set.
   * Note that all new models are added later, AFTER the old ones. This is 
   * very important, because all of the old atom numbers must map onto the
   * same numbers in the new model set, or the state script will not run
   * properly, among other problems.
   * 
   * @param mergeModelSet
   */
  protected void mergeModelArrays(ModelSet mergeModelSet) {
    atoms = mergeModelSet.atoms;
    bonds = mergeModelSet.bonds;
    stateScripts = mergeModelSet.stateScripts;
    proteinStructureTainted = mergeModelSet.proteinStructureTainted;
    thisStateModel = -1;
    bsSymmetry = mergeModelSet.bsSymmetry;
    modelFileNumbers = mergeModelSet.modelFileNumbers;  // file * 1000000 + modelInFile (1-based)
    modelNumbersForAtomLabel = mergeModelSet.modelNumbersForAtomLabel;
    modelNames = mergeModelSet.modelNames;
    modelNumbers = mergeModelSet.modelNumbers;
    frameTitles = mergeModelSet.frameTitles;
    mergeAtomArrays(mergeModelSet);
  }

  protected void mergeModels(ModelSet mergeModelSet) {
    for (int i = 0; i < mergeModelSet.modelCount; i++) {
      Model m = models[i] = mergeModelSet.models[i];
      m.modelSet = (ModelSet) this;
      for (int j = 0; j < m.chainCount; j++)
        m.chains[j].setModelSet(m.modelSet);
    }
  }

  @Override
  protected void releaseModelSet() {
    /*
     * Probably unnecessary, but here for general accounting.
     * 
     * I added this when I was trying to track down a memory bug.
     * I know that you don't have to do this, but I was concerned
     * that somewhere in this mess was a reference to modelSet. 
     * As it turns out, it was in models[i] (which was not actually
     * nulled but, rather, transferred to the new model set anyway).
     * Quite amazing that that worked at all, really. Some models were
     * referencing the old modelset, some the new. Yeiks!
     * 
     * Bob Hanson 11/7/07
     * 
     */
    models = null;
    bsSymmetry = null;
    bsAll = null;
    unitCells = null;
    super.releaseModelSet();
  }

  
  protected BitSet bsSymmetry;
  
  protected String modelSetName;

  public String getModelSetName() {
    return modelSetName;
  }

  protected Model[] models = new Model[1];

  public Model[] getModels() {
    return models;
  }

  protected int modelCount;

  public int getModelCount() {
    return modelCount;
  }

  SymmetryInterface[] unitCells;

  public SymmetryInterface[] getCellInfos() {
    return unitCells;
  }

  public SymmetryInterface getUnitCell(int modelIndex) {
    return (unitCells != null && modelIndex >= 0 && modelIndex < unitCells.length 
        ? unitCells[modelIndex] : null);
  }

  /**
   * 
   * @param type
   * @param plane
   * @param scale
   * @param modelIndex
   * @param flags
   *          1 -- edges only 2 -- triangles only 3 -- both
   * @return Vector
   */
  public List<Object> getPlaneIntersection(int type, Point4f plane, float scale,
                                     int flags, int modelIndex) {
    Point3f[] pts = null;
    switch (type) {
    case Token.unitcell:
      SymmetryInterface uc = getUnitCell(modelIndex);
      if (uc == null)
        return null;
      pts = uc.getCanonicalCopy(scale);
      break;
    case Token.boundbox:
      pts = boxInfo.getCanonicalCopy(scale);
      break;
    }
    List<Object> v = new ArrayList<Object>();
    v.add(pts);
    return TriangleData.intersectPlane(plane, v, flags);
  }

  protected int[] modelNumbers = new int[1];  // from adapter -- possibly PDB MODEL record; possibly modelFileNumber
  protected int[] modelFileNumbers = new int[1];  // file * 1000000 + modelInFile (1-based)
  protected String[] modelNumbersForAtomLabel = new String[1];
  protected String[] modelNames = new String[1];
  protected String[] frameTitles = new String[1];

  public String getModelName(int modelIndex) {
    return modelCount < 1 ? "" 
        : modelIndex >= 0 ? modelNames[modelIndex]
        : modelNumbersForAtomLabel[-1 - modelIndex];
  }

  public String getModelTitle(int modelIndex) {
    return (String) getModelAuxiliaryInfo(modelIndex, "title");
  }

  public String getModelFileName(int modelIndex) {
    return (String) getModelAuxiliaryInfo(modelIndex, "fileName");
  }

  public void setFrameTitle(BitSet bsFrames, Object title) {
    if (title instanceof String[]) {
      String[] list = (String[]) title;
      for (int i = bsFrames.nextSetBit(0), n = 0; i >= 0; i = bsFrames.nextSetBit(i + 1)) 
        if (n < list.length)
          frameTitles[i] = list[n++];      
    } else {
      for (int i = bsFrames.nextSetBit(0); i >= 0; i = bsFrames.nextSetBit(i + 1)) 
        frameTitles[i] = (String) title;
    }
  }
  
  public String getFrameTitle(int modelIndex) {
    return (modelIndex >= 0 && modelIndex < modelCount ?
        frameTitles[modelIndex] : "");
  }
  
  public String getModelNumberForAtomLabel(int modelIndex) {
    return modelNumbersForAtomLabel[modelIndex];
  }
  
  protected BitSet[] elementsPresent;

  protected boolean isXYZ;
  protected boolean isPDB;

  protected Properties modelSetProperties;
  protected Map<String, Object> modelSetAuxiliaryInfo;

  protected Group[] groups;
  protected int groupCount;
  protected boolean haveBioClasses = true;
  protected JmolBioResolver jbr = null;
  
  protected void calculatePolymers(int baseGroupIndex, BitSet alreadyDefined) {
    if (jbr == null)
      return;
    if (alreadyDefined != null) {
      jbr.clearBioPolymers(groups, groupCount, alreadyDefined);
    }
    boolean checkPolymerConnections = !viewer.isPdbSequential();
    for (int i = baseGroupIndex; i < groupCount; ++i) {
      Group g = groups[i];
      boolean doCheck = checkPolymerConnections 
        && !isJmolDataFrame(atoms[g.firstAtomIndex].modelIndex);
      Polymer bp = jbr.buildBioPolymer(g, groups, i, doCheck);
      if (bp == null || bp.monomerCount == 0)
        continue;
      addBioPolymerToModel(bp, g.getModel());
      i += bp.monomerCount - 1;
    }
  }

  protected void addBioPolymerToModel(Polymer polymer, Model model) {
    if (model.bioPolymers.length == 0 || polymer == null)
      model.bioPolymers = new Polymer[8];
    if (polymer == null) {
      model.bioPolymerCount = 0;
      return;
    }
    if (model.bioPolymerCount == model.bioPolymers.length)
      model.bioPolymers = (Polymer[])ArrayUtil.doubleLength(model.bioPolymers);
    polymer.bioPolymerIndexInModel = model.bioPolymerCount;
    model.bioPolymers[model.bioPolymerCount++] = polymer;
  }

  /**
   * deprecated due to multimodel issues, 
   * but required by an interface -- do NOT remove.
   * 
   * @return      just the first unit cell
   * 
   */
  public float[] getNotionalUnitcell() {
    return (unitCells == null || unitCells[0] == null ? null : unitCells[0]
        .getNotionalUnitCell());
  }

  //new way:

  protected boolean someModelsHaveSymmetry;
  protected boolean someModelsHaveAromaticBonds;
  protected boolean someModelsHaveFractionalCoordinates;

  public boolean setCrystallographicDefaults() {
    return !isPDB && someModelsHaveSymmetry && someModelsHaveFractionalCoordinates;
  }

  private final Matrix3f matTemp = new Matrix3f();
  private final Matrix3f matInv = new Matrix3f();
  private final Point3f ptTemp = new Point3f();

  ////////////////////////////////////////////

  private final Point3f averageAtomPoint = new Point3f();
  private boolean isBbcageDefault;
  private BitSet bboxModels;
  private BitSet bboxAtoms;
  private final BoxInfo boxInfo = new BoxInfo();
  {
    boxInfo.setBbcage(1);
  }
  
  public Point3f getAverageAtomPoint() {
    return averageAtomPoint;
  }

  public Point3f getBoundBoxCenter(int modelIndex) {
    if (isJmolDataFrame(modelIndex))
      return new Point3f();
    return boxInfo.getBoundBoxCenter();
  }

  public Vector3f getBoundBoxCornerVector() {
    return boxInfo.getBoundBoxCornerVector();
  }

  public Point3fi[] getBboxVertices() {
    return boxInfo.getBboxVertices();
  }

  public Map<String, Object> getBoundBoxInfo() {
    return boxInfo.getBoundBoxInfo();
  }

  public BitSet getBoundBoxModels() {
    return bboxModels;
  }
  
  public void setBoundBox(Point3f pt1, Point3f pt2, boolean byCorner, float scale) {
    isBbcageDefault = false;
    bboxModels = null;
    bboxAtoms = null;
    boxInfo.setBoundBox(pt1, pt2, byCorner, scale);
  }

  public String getBoundBoxCommand(boolean withOptions) {
    if (!withOptions && bboxAtoms != null)
      return "boundbox " + Escape.escape(bboxAtoms);
    ptTemp.set(boxInfo.getBoundBoxCenter());
    Vector3f bbVector = boxInfo.getBoundBoxCornerVector();
    String s = (withOptions ? "boundbox " + Escape.escape(ptTemp) + " "
        + Escape.escape(bbVector) + "\n#or\n" : "");
    ptTemp.sub(bbVector);
    s += "boundbox corners " + Escape.escape(ptTemp) + " ";
    ptTemp.scaleAdd(2, bbVector, ptTemp);
    float v = Math.abs(8 * bbVector.x * bbVector.y * bbVector.z);
    s += Escape.escape(ptTemp) + " # volume = " + v;
    return s;
  }

  public int getDefaultVdwType(int modelIndex) {
    return (!models[modelIndex].isPDB ? JmolConstants.VDW_AUTO_BABEL
        : models[modelIndex].hydrogenCount == 0 ? JmolConstants.VDW_AUTO_JMOL
        : JmolConstants.VDW_AUTO_BABEL); // RASMOL is too small
  }

  public boolean setRotationRadius(int modelIndex, float angstroms) {
    if (isJmolDataFrame(modelIndex)) {
      models[modelIndex].defaultRotationRadius = angstroms;
      return false;
    }
    return true;
  }

  public float calcRotationRadius(int modelIndex, Point3f center) {
    if (isJmolDataFrame(modelIndex)) {
      float r = models[modelIndex].defaultRotationRadius;
      return (r == 0 ? 10 : r);
    }
    float maxRadius = 0;
    for (int i = atomCount; --i >= 0;) {
      if (isJmolDataFrame(atoms[i])) {
        modelIndex = atoms[i].modelIndex;
        while (i >= 0 && atoms[i].modelIndex == modelIndex)
          i--;
        continue;
      }
        Atom atom = atoms[i];
        float distAtom = center.distance(atom);
        float outerVdw = distAtom + getRadiusVdwJmol(atom);
        if (outerVdw > maxRadius)
          maxRadius = outerVdw;     
    }
    return (maxRadius == 0 ? 10 : maxRadius);
  }

  public void calcBoundBoxDimensions(BitSet bs, float scale) {
    if (bs != null && bs.nextSetBit(0) < 0)
      bs = null;
    if (bs == null && isBbcageDefault || atomCount < 2)
      return;
    bboxModels = getModelBitSet(bboxAtoms = BitSetUtil.copy(bs), false);
    if (calcAtomsMinMax(bs, boxInfo) == atomCount)
      isBbcageDefault = true;
    if (bs == null) { // from modelLoader or reset
      averageAtomPoint.set(getAtomSetCenter(null));
      if (unitCells != null)
        calcUnitCellMinMax();
    }
    boxInfo.setBbcage(scale);
  }

  public BoxInfo getBoxInfo(BitSet bs, float scale) {
    if (bs == null)
      return boxInfo;
    BoxInfo bi = new BoxInfo();
    calcAtomsMinMax(bs, bi);
    bi.setBbcage(scale);
    return bi;
  }

  public int calcAtomsMinMax(BitSet bs, BoxInfo boxInfo) {
    boxInfo.reset();
    int nAtoms = 0;
    boolean isAll = (bs == null);
    int i0 = (isAll ? atomCount - 1 : bs.nextSetBit(0));
    for (int i = i0; i >= 0; i = (isAll ? i - 1 : bs.nextSetBit(i + 1))) {
      nAtoms++;
      if (!isJmolDataFrame(atoms[i]))
        boxInfo.addBoundBoxPoint(atoms[i]);
    }
    return nAtoms;
  }

  private void calcUnitCellMinMax() {
    for (int i = 0; i < modelCount; i++) {
      if (!unitCells[i].getCoordinatesAreFractional())
        continue;
      Point3f[] vertices = unitCells[i].getUnitCellVertices();
      for (int j = 0; j < 8; j++)
        boxInfo.addBoundBoxPoint(vertices[j]);
    }
  }

  public float calcRotationRadius(BitSet bs) {
    // Eval getZoomFactor
    Point3f center = getAtomSetCenter(bs);
    float maxRadius = 0;
    for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
      Atom atom = atoms[i];
      float distAtom = center.distance(atom);
      float outerVdw = distAtom + getRadiusVdwJmol(atom);
      if (outerVdw > maxRadius)
        maxRadius = outerVdw;
    }
    return (maxRadius == 0 ? 10 : maxRadius);
  }

  /**
   * 
   * @param vAtomSets
   * @param addCenters
   * @return             array of two lists of points, centers first if desired
   */

  public Point3f[][] getCenterAndPoints(List<BitSet[]> vAtomSets, boolean addCenters) {
    BitSet bsAtoms1, bsAtoms2;
    int n = (addCenters ? 1 : 0);
    for (int ii = vAtomSets.size(); --ii >= 0;) {
      BitSet[] bss = vAtomSets.get(ii);
      bsAtoms1 = bss[0];
      bsAtoms2 = bss[1];
      n += Math.min(bsAtoms1.cardinality(), bsAtoms2.cardinality());
    }
    Point3f[][] points = new Point3f[2][n];
    if (addCenters) {
      points[0][0] = new Point3f();
      points[1][0] = new Point3f();
    }
    for (int ii = vAtomSets.size(); --ii >= 0;) {
      BitSet[] bss = vAtomSets.get(ii);
      bsAtoms1 = bss[0];
      bsAtoms2 = bss[1];
      for (int i = bsAtoms1.nextSetBit(0), j = bsAtoms2.nextSetBit(0); i >= 0 && j >= 0; i = bsAtoms1
          .nextSetBit(i + 1), j = bsAtoms2.nextSetBit(j + 1)) {
        points[0][--n] = atoms[i];
        points[1][n] = atoms[j];
        if (addCenters) {
          points[0][0].add(atoms[i]);
          points[1][0].add(atoms[j]);
        }
      }
    }
    if (addCenters) {
      points[0][0].scale(1f/(points[0].length - 1));
      points[1][0].scale(1f/(points[1].length - 1));
    }
    return points;
  }

  public Point3f getAtomSetCenter(BitSet bs) {
    Point3f ptCenter = new Point3f(0, 0, 0);
    int nPoints = 0;
    if (bs != null)
      for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
        if (!isJmolDataFrame(atoms[i])) {
          nPoints++;
          ptCenter.add(atoms[i]);
        }
      }
    if (nPoints > 0)
      ptCenter.scale(1.0f / nPoints);
    return ptCenter;
  }

  @Override
  public void setAtomProperty(BitSet bs, int tok, int iValue, float fValue,
                              String sValue, float[] values, String[] list) {
    super.setAtomProperty(bs, tok, iValue, fValue, sValue, values, list);
    if ((tok == Token.valence || tok == Token.formalcharge)
        && viewer.getSmartAromatic())
      assignAromaticBonds();
  }
  
  protected List<StateScript> stateScripts = new ArrayList<StateScript>();
  /*
   * stateScripts are connect commands that must be executed in sequence.
   * 
   * What I fear is that in deleting models we must delete these connections,
   * and in deleting atoms, the bitsets may not be retrieved properly. 
   * 
   * 
   */
  private int thisStateModel = 0;

  public StateScript addStateScript(String script1, BitSet bsBonds, BitSet bsAtoms1,
                             BitSet bsAtoms2, String script2,
                             boolean addFrameNumber, boolean postDefinitions) {
    int iModel = viewer.getCurrentModelIndex();
    if (addFrameNumber) {
      if (thisStateModel != iModel)
        script1 = "frame "
            + (iModel < 0 ? "" + iModel : getModelNumberDotted(iModel)) + ";\n  "
            + script1;
      thisStateModel = iModel;
    } else {
      thisStateModel = -1;
    }
    StateScript stateScript = new StateScript(thisStateModel, script1, bsBonds, bsAtoms1,
        bsAtoms2, script2, postDefinitions);
    if (stateScript.isValid()) {
      stateScripts.add(stateScript);
    }
    return stateScript;
  }

  public static class StateScript {
    private int modelIndex;
    private BitSet bsBonds;
    private BitSet bsAtoms1;
    private BitSet bsAtoms2;
    private String script1;
    private String script2;
    boolean inDefinedStateBlock;
    
    StateScript(int modelIndex, String script1, BitSet bsBonds, BitSet bsAtoms1,
        BitSet bsAtoms2, String script2, boolean inDefinedStateBlock) {
      this.modelIndex = modelIndex;
      this.script1 = script1;
      this.bsBonds = BitSetUtil.copy(bsBonds);
      this.bsAtoms1 = BitSetUtil.copy(bsAtoms1);
      this.bsAtoms2 = BitSetUtil.copy(bsAtoms2);
      this.script2 = script2;
      this.inDefinedStateBlock = inDefinedStateBlock;
    }
    
    public boolean isValid() {
      return script1 != null && script1.length() > 0
        && (bsBonds == null || bsBonds.nextSetBit(0) >= 0)
        && (bsAtoms1 == null || bsAtoms1.nextSetBit(0) >= 0)
        && (bsAtoms2 == null || bsAtoms2.nextSetBit(0) >= 0);
    }

    @Override
    public String toString() {
      if (!isValid())
        return "";
      StringBuffer sb = new StringBuffer(script1);
      if (bsBonds != null)
        sb.append(" ").append(Escape.escape(bsBonds, false));
      if (bsAtoms1 != null)
        sb.append(" ").append(Escape.escape(bsAtoms1));
      if (bsAtoms2 != null)
        sb.append(" ").append(Escape.escape(bsAtoms2));
      if (script2 != null)
        sb.append(" ").append(script2);
      String s = sb.toString();
      if (!s.endsWith(";"))
        s += ";";
      return s;
    }

    public boolean isConnect() {
      return (script1.indexOf("connect") == 0);
    }
    
    public boolean deleteAtoms(int modelIndex, BitSet bsBonds, BitSet bsAtoms) {
      //false return means delete this script
      if (modelIndex == this.modelIndex) 
        return false;
      if (modelIndex > this.modelIndex) {
//        this.modelIndex--;
        return true;
      }
      BitSetUtil.deleteBits(this.bsBonds, bsBonds);
      BitSetUtil.deleteBits(this.bsAtoms1, bsAtoms);
      BitSetUtil.deleteBits(this.bsAtoms2, bsAtoms);
      return isValid();
    }

    public void setModelIndex(int index) {
      modelIndex = index; // for creating data frames 
    }
  }
  
  /**
   * allows rebuilding of PDB structures;
   * also accessed by ModelManager from Eval
   * 
   * @param alreadyDefined    set to skip calculation
   * @param asDSSP
   * @param doReport 
   * @param dsspIgnoreHydrogen 
   * @param setStructure
   * @param includeAlpha 
   * @return  report
   *  
   */
  protected String calculateStructuresAllExcept(BitSet alreadyDefined,
                                                boolean asDSSP,
                                                boolean doReport,
                                                boolean dsspIgnoreHydrogen,
                                                boolean setStructure,
                                                boolean includeAlpha) {
    freezeModels();
    String ret = "";
    BitSet bsModels = BitSetUtil.copyInvert(alreadyDefined, modelCount);
   //working here -- testing reset
   //TODO bsModels first for not setStructure, after that for setstructure....
    if (setStructure)
      setDefaultStructure(bsModels);
    for (int i = bsModels.nextSetBit(0); i >= 0; i = bsModels.nextSetBit(i + 1)) {
        ret += models[i].calculateStructures(asDSSP, doReport,
            dsspIgnoreHydrogen, setStructure, includeAlpha);
    }
    if (setStructure) {
      setStructureIds();
    }
    return ret;
  }

  
  public void setDefaultStructure(BitSet bsModels) {
    for (int i = bsModels.nextSetBit(0); i >= 0; i = bsModels.nextSetBit(i + 1)) 
      if (models[i].isPDB && models[i].defaultStructure == null)
        models[i].defaultStructure = getProteinStructureState(models[i].bsAtoms, false, false, 0);
  }

  public void setProteinType(BitSet bs, byte iType) {
    int monomerIndexCurrent = -1;
    int iLast = -1;
    BitSet bsModels = getModelBitSet(bs, false);
    setDefaultStructure(bsModels);
    for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
      if (iLast != i - 1)
        monomerIndexCurrent = -1;
      monomerIndexCurrent = atoms[i].group.setProteinStructureType(iType,
          monomerIndexCurrent);
      int modelIndex = atoms[i].modelIndex;
      proteinStructureTainted = models[modelIndex].structureTainted = true;
      iLast = i = atoms[i].group.lastAtomIndex;
    }
    int[] lastStrucNo = new int[modelCount];
    for (int i = 0; i < atomCount; ) {
      int modelIndex = atoms[i].modelIndex; 
      if (!bsModels.get(modelIndex)) {
        i = models[modelIndex].firstAtomIndex + models[modelIndex].atomCount;
        continue;
      }
      iLast = atoms[i].getStrucNo();
      if (iLast < 1000 && iLast > lastStrucNo[modelIndex])
        lastStrucNo[modelIndex] = iLast;
      i = atoms[i].group.lastAtomIndex + 1;
    }
    for (int i = 0; i < atomCount; ) {
      int modelIndex = atoms[i].modelIndex; 
      if (!bsModels.get(modelIndex)) {
        i = models[modelIndex].firstAtomIndex + models[modelIndex].atomCount;
        continue;
      }
      if (atoms[i].getStrucNo() > 1000)
        atoms[i].group.setProteinStructureId(++lastStrucNo[modelIndex]);
      i = atoms[i].group.lastAtomIndex + 1;
    }
  }
  
  private void freezeModels() {
    for (int iModel = modelCount; --iModel >= 0;) {
      Model m = models[iModel];
      m.chains = (Chain[])ArrayUtil.setLength(m.chains, m.chainCount);
      m.groupCount = -1;
      m.getGroupCount();      
      for (int i = 0; i < m.chainCount; ++i)
        m.chains[i].groups = (Group[])ArrayUtil.setLength(m.chains[i].groups, m.chains[i].groupCount);
      m.bioPolymers = (Polymer[])ArrayUtil.setLength(m.bioPolymers, m.bioPolymerCount);
    }
  }
  
  public float[][] getStructureList() {
    return viewer.getStructureList();
  }

  public void setStructureList(float[][] structureList) {
    for (int iModel = modelCount; --iModel >= 0;) {
      Model m = models[iModel];
      m.bioPolymers = (Polymer[])ArrayUtil.setLength(m.bioPolymers, m.bioPolymerCount);
      for (int i = m.bioPolymerCount; --i >= 0; ) {
        m.bioPolymers[i].setStructureList(structureList);
      }
    }
  }


  public BitSet setConformation(BitSet bsAtoms) {
    BitSet bsModels = getModelBitSet(bsAtoms, false);
    for (int i = bsModels.nextSetBit(0); i >= 0; i = bsModels.nextSetBit(i + 1))
      models[i].setConformation(bsAtoms);
    return bsAtoms;
  }

  public BitSet getConformation(int modelIndex, int conformationIndex,
                                boolean doSet) {
    BitSet bs = new BitSet();
    for (int i = modelCount; --i >= 0;)
      if (i == modelIndex || modelIndex < 0) {
        String altLocs = getAltLocListInModel(i);
        int nAltLocs = getAltLocCountInModel(i);
        if (conformationIndex > 0 && conformationIndex >= nAltLocs)
          continue;
        BitSet bsConformation = viewer.getModelUndeletedAtomsBitSet(i);
        if (conformationIndex >= 0) {
          if (!models[i].getPdbConformation(bsConformation, conformationIndex))
            for (int c = nAltLocs; --c >= 0;)
              if (c != conformationIndex)
                bsConformation.andNot(getAtomBits(Token.spec_alternate, altLocs
                    .substring(c, c + 1)));
        }
        if (bsConformation.nextSetBit(0) >= 0) {
          bs.or(bsConformation);
          if (doSet)
            models[i].setConformation(bsConformation);
        }
      }
    return bs;
  }

  @SuppressWarnings("unchecked")
  public Map<String, String> getHeteroList(int modelIndex) {
    Map<String, String> htFull = new Hashtable<String, String>();
    boolean ok = false;
    for (int i = modelCount; --i >= 0;)
      if (modelIndex < 0 || i == modelIndex) {
        Map<String, String> ht = (Map<String, String>) getModelAuxiliaryInfo(i, "hetNames");
        if (ht == null)
          continue;
        ok = true;
        for (Map.Entry<String, String> entry : ht.entrySet()) {
          String key = entry.getKey();
          htFull.put(key, entry.getValue());
        }
      }
    return (ok ? htFull : (Map<String, String>) getModelSetAuxiliaryInfo("hetNames"));
  }

  public Properties getModelSetProperties() {
    return modelSetProperties;
  }

  public Map<String, Object> getModelSetAuxiliaryInfo() {
    return modelSetAuxiliaryInfo;
  }

  public String getModelSetProperty(String propertyName) {
    // no longer used in Jmol
    return (modelSetProperties == null ? null : modelSetProperties
        .getProperty(propertyName));
  }

  public Object getModelSetAuxiliaryInfo(String keyName) {
    // the preferred method now
    return (modelSetAuxiliaryInfo == null ? null : modelSetAuxiliaryInfo.get(keyName));
  }

  protected boolean getModelSetAuxiliaryInfoBoolean(String keyName) {
    return (modelSetAuxiliaryInfo != null
        && modelSetAuxiliaryInfo.containsKey(keyName) && ((Boolean) modelSetAuxiliaryInfo
        .get(keyName)).booleanValue());
  }

/*
  int getModelSetAuxiliaryInfoInt(String keyName) {
    if (modelSetAuxiliaryInfo != null
        && modelSetAuxiliaryInfo.containsKey(keyName)) {
      return ((Integer) modelSetAuxiliaryInfo.get(keyName)).intValue();
    }
    return Integer.MIN_VALUE;
  }
*/

  protected List<Point3f[]> trajectorySteps;

  protected int getTrajectoryCount() {
    return (trajectorySteps == null ? 0 : trajectorySteps.size());
  }

  public int getTrajectoryIndex(int modelIndex) {
    return models[modelIndex].trajectoryBaseIndex;
  }
  
  public boolean isTrajectory(int modelIndex) {
    return models[modelIndex].isTrajectory;
  }
  
  public boolean isTrajectory(int[] countPlusIndices) {
    if (countPlusIndices == null)
      return false;
    int count = countPlusIndices[0];
    int atomIndex;
    for (int i = 1; i <= count; i++)
      if ((atomIndex = countPlusIndices[i]) >= 0 
          && models[atoms[atomIndex].modelIndex].isTrajectory)
        return true;
    return false;
  }

  public BitSet getModelBitSet(BitSet atomList, boolean allTrajectories) {
    BitSet bs = new BitSet();
    int modelIndex = 0;
    boolean isAll = (atomList == null);
    int i0 = (isAll ? 0 : atomList.nextSetBit(0));
    for (int i = i0; i >= 0 && i < atomCount; i = (isAll ? i + 1 : atomList
        .nextSetBit(i + 1))) {
      bs.set(modelIndex = atoms[i].modelIndex);
      if (allTrajectories) {
        int iBase = models[modelIndex].trajectoryBaseIndex;
        for (int j = 0; j < modelCount; j++)
          if (models[j].trajectoryBaseIndex == iBase)
            bs.set(j);
      }
      i = models[modelIndex].firstAtomIndex + models[modelIndex].atomCount - 1;
    }
    return bs;
  }

  /** 
   * only some models can be iterated through.
   * models for which trajectoryBaseIndexes[i] != i are trajectories only
   * 
   * @param allowJmolData
   * @return  bitset of models
   */
  public BitSet getIterativeModels(boolean allowJmolData) {
    BitSet bs = new BitSet();
    for (int i = 0; i < modelCount; i++) {
      if (!allowJmolData && isJmolDataFrame(i))
        continue;
      if (models[i].trajectoryBaseIndex == i)
        bs.set(i);      
    }
    return bs;
  }
  
  protected boolean isTrajectorySubFrame(int i) {
    return (models[i].isTrajectory && models[i].trajectoryBaseIndex != i);
  }

  public void selectDisplayedTrajectories(BitSet bs) {
    //when a trajectory is selected, the atom's modelIndex is
    //switched to that of the selected trajectory
    //even though the underlying model itself is not changed.
    for (int i = 0; i < modelCount; i++) {
      if (models[i].isTrajectory && atoms[models[i].firstAtomIndex].modelIndex != i)
        bs.clear(i);
    }
  }
  
  public String getModelNumberDotted(int modelIndex) {
    return (modelCount < 1 || modelIndex >= modelCount || modelIndex < 0 ? "" : 
      Escape.escapeModelFileNumber(modelFileNumbers[modelIndex]));
  }

  public int getModelNumber(int modelIndex) {
    return modelNumbers[modelIndex];
  }

  public int getModelFileNumber(int modelIndex) {
    return modelFileNumbers[modelIndex];
  }

  public Properties getModelProperties(int modelIndex) {
    return models[modelIndex].properties;
  }

  public String getModelProperty(int modelIndex, String property) {
    Properties props = models[modelIndex].properties;
    return props == null ? null : props.getProperty(property);
  }

  public Map<String, Object> getModelAuxiliaryInfo(int modelIndex) {
    return (modelIndex < 0 ? null : models[modelIndex].auxiliaryInfo);
  }

  public void setModelAuxiliaryInfo(int modelIndex, Object key, Object value) {
    models[modelIndex].auxiliaryInfo.put((String) key, value);
  }

  public Object getModelAuxiliaryInfo(int modelIndex, String key) {
    if (modelIndex < 0) {
      return null;
    }
    return models[modelIndex].auxiliaryInfo.get(key);
  }

  protected boolean getModelAuxiliaryInfoBoolean(int modelIndex, String keyName) {
    Map<String, Object> info = models[modelIndex].auxiliaryInfo;
    return (info != null && info.containsKey(keyName) && ((Boolean) info
        .get(keyName)).booleanValue());
  }

  protected int getModelAuxiliaryInfoInt(int modelIndex, String keyName) {
    Map<String, Object> info = models[modelIndex].auxiliaryInfo;
    if (info != null && info.containsKey(keyName)) {
      return ((Integer) info.get(keyName)).intValue();
    }
    return Integer.MIN_VALUE;
  }

  public String getModelAtomProperty(Atom atom, String text) {
    Object data = getModelAuxiliaryInfo(atom.modelIndex, text);
    if (!(data instanceof Object[]))
      return "";
    Object[] sdata = (Object[]) data;
    int iatom = atom.index - models[atom.modelIndex].firstAtomIndex;
    return (iatom < sdata.length ? sdata[iatom].toString() : "");
  }

  public int getInsertionCountInModel(int modelIndex) {
    return models[modelIndex].nInsertions;
  }

  public String getModelFileType(int modelIndex) {
    return (String) getModelAuxiliaryInfo(modelIndex, "fileType");
  }
  
  public static int modelFileNumberFromFloat(float fDotM) {
    //only used in the case of select model = someVariable
    //2.1 and 2.10 will be ambiguous and reduce to 2.1  

    int file = (int) (fDotM);
    int model = (int) ((fDotM - file + 0.00001) * 10000);
    while (model != 0 && model % 10 == 0)
      model /= 10;
    return file * 1000000 + model;
  }

  public int getAltLocCountInModel(int modelIndex) {
    return models[modelIndex].nAltLocs;
  }

  public int getChainCount(boolean addWater) {
    int chainCount = 0;
    for (int i = modelCount; --i >= 0;)
      chainCount += models[i].getChainCount(addWater);
    return chainCount;
  }

  public int getBioPolymerCount() {
    int polymerCount = 0;
    for (int i = modelCount; --i >= 0;)
      if (!isTrajectorySubFrame(i))
        polymerCount += models[i].getBioPolymerCount();
    return polymerCount;
  }

  public int getBioPolymerCountInModel(int modelIndex) {
    return (modelIndex < 0 ? getBioPolymerCount()
        : isTrajectorySubFrame(modelIndex) ? 0 : models[modelIndex].getBioPolymerCount());
  }

  public void getPolymerPointsAndVectors(BitSet bs, List<Point3f[]> vList) {
    boolean isTraceAlpha = viewer.getTraceAlpha();
    float sheetSmoothing = viewer.getSheetSmoothing();
    int last = Integer.MAX_VALUE - 1;
    for (int i = 0; i < modelCount; ++i) {
      int polymerCount = models[i].getBioPolymerCount();
      for (int ip = 0; ip < polymerCount; ip++)
        last = models[i].getBioPolymer(ip)
            .getPolymerPointsAndVectors(last, bs, vList, isTraceAlpha, sheetSmoothing);
    }
  }
  
  public void recalculateLeadMidpointsAndWingVectors(int modelIndex) {
    if (modelIndex < 0) {
      for (int i = 0; i < modelCount; i++)
        recalculateLeadMidpointsAndWingVectors(i);
      return;
    }
    int polymerCount = models[modelIndex].getBioPolymerCount();
    for (int ip = 0; ip < polymerCount; ip++)
      models[modelIndex].getBioPolymer(ip)
          .recalculateLeadMidpointsAndWingVectors();
  }
  
  public Point3f[] getPolymerLeadMidPoints(int iModel, int iPolymer) {
    return models[iModel].getBioPolymer(iPolymer).getLeadMidpoints();
  }

  public int getChainCountInModel(int modelIndex, boolean countWater) {
    if (modelIndex < 0)
      return getChainCount(countWater);
    return models[modelIndex].getChainCount(countWater);
  }

  public int getGroupCount() {
    int groupCount = 0;
    for (int i = modelCount; --i >= 0;)
      groupCount += models[i].getGroupCount();
    return groupCount;
  }

  public int getGroupCountInModel(int modelIndex) {
    if (modelIndex < 0)
      return getGroupCount();
    return models[modelIndex].getGroupCount();
  }

  public void calcSelectedGroupsCount(BitSet bsSelected) {
    for (int i = modelCount; --i >= 0;)
      models[i].calcSelectedGroupsCount(bsSelected);
  }

  public void calcSelectedMonomersCount(BitSet bsSelected) {
    for (int i = modelCount; --i >= 0;)
      models[i].calcSelectedMonomersCount(bsSelected);
  }


  /**
   * These are not actual hydrogen bonds. They are N-O bonds in proteins and
   * nucleic acids The method is called by AminoPolymer and NucleicPolymer
   * methods, which are indirectly called by ModelCollection.autoHbond
   * @param bsA
   * @param bsB
   * @param vHBonds
   *          vector of bonds to fill; if null, creates the HBonds
   * @param nucleicOnly
   * @param nMax 
   * @param dsspIgnoreHydrogens 
   */

  public void calcRasmolHydrogenBonds(BitSet bsA, BitSet bsB, List<Bond> vHBonds, 
                                      boolean nucleicOnly, int nMax, boolean dsspIgnoreHydrogens) {
    boolean isSame = (bsB == null || bsA.equals(bsB));
    for (int i = modelCount; --i >= 0;)
      if (models[i].trajectoryBaseIndex == i) {
        if (vHBonds == null) {
          clearRasmolHydrogenBonds(i, bsA);
          if (!isSame)
            clearRasmolHydrogenBonds(i, bsB);
        }
        getRasmolHydrogenBonds(models[i], bsA, bsB, vHBonds, nucleicOnly, nMax, dsspIgnoreHydrogens);
      }
  }

  public void calculateStraightness() {
    if (getHaveStraightness())
      return;
    char ctype = 'S';//(viewer.getTestFlag3() ? 's' : 'S');
    char qtype = viewer.getQuaternionFrame();
    int mStep = viewer.getHelixStep();
    // testflag3 ON  --> preliminary: Hanson's original normal-based straightness
    // testflag3 OFF --> final: Kohler's new quaternion-based straightness
    for (int i = modelCount; --i >= 0;) {
      Model model = models[i];
      int nPoly = model.getBioPolymerCount();
      for (int p = 0; p < nPoly; p++)
        model.bioPolymers[p].getPdbData(viewer, ctype, qtype, mStep, 2, null, 
            null, false, false, false, null, null, null, new BitSet());
    }
    setHaveStraightness(true);
  }

  public Quaternion[] getAtomGroupQuaternions(BitSet bsAtoms, int nMax,
                                              char qtype) {
    // run through list, getting quaternions. For simple groups, 
    // go ahead and take first three atoms
    // for PDB files, do not include NON-protein groups.
    int n = 0;
    List<Quaternion> v = new ArrayList<Quaternion>();
    for (int i = bsAtoms.nextSetBit(0); i >= 0 && n < nMax; i = bsAtoms.nextSetBit(i + 1)) {
      Group g = atoms[i].group;
      Quaternion q = g.getQuaternion(qtype);
      if (q == null) {
        if (g.seqcode == Integer.MIN_VALUE)
          q = g.getQuaternionFrame(atoms); // non-PDB just use first three atoms
        if (q == null)
          continue;
      }
      n++;
      v.add(q);
      i = g.lastAtomIndex;
    }
    Quaternion[] qs = new Quaternion[v.size()];
    for (int i = 0; i < qs.length; i++) {
      qs[i] = v.get(i);
    }
    return qs;
  }

  public String getPdbAtomData(BitSet bs, OutputStringBuffer sb) {
    if (atomCount == 0)
      return "";
    if (sb == null)
      sb = new OutputStringBuffer(null);
    int iModel = atoms[0].modelIndex;
    int iModelLast = -1;
    boolean showModels = (iModel != atoms[atomCount - 1].modelIndex);
    LabelToken[] t4x = null;
    LabelToken[] t3x = null;
    LabelToken[] t4h = null;
    LabelToken[] t3h = null;
    LabelToken[] t4a = null;
    LabelToken[] t3a = null;
    LabelToken[] tokens;
    for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
      Atom a = atoms[i];
      if (showModels && a.modelIndex != iModelLast) {
        if (iModelLast != -1)
          sb.append("ENDMDL\n");
        iModelLast = a.modelIndex;
        sb.append("MODEL     " + (iModelLast + 1) + "\n");
      }
      String sa = a.getAtomName();
      boolean leftJustify = (a.getElementSymbol().length() == 2 
          || sa.length() >= 4
          || Character.isDigit(sa.charAt(0)));
      if (!models[a.modelIndex].isPDB)
        tokens = (leftJustify ? 
            (t4x == null ? LabelToken.compile(viewer, "HETATM%5.-5i %-4.4a%1AUNK %1c   1%1E   %8.3x%8.3y%8.3z%6.2Q%6.2b          ", '\0', null)
                : t4x)
            : (t3x == null ? LabelToken.compile(viewer, "HETATM%5.-5i  %-3.3a%1AUNK %1c   1%1E   %8.3x%8.3y%8.3z%6.2Q%6.2b          ", '\0', null)
                : t3x));
      else if (a.isHetero())
        tokens = (leftJustify ? 
            (t4h == null ? LabelToken.compile(viewer, "HETATM%5.-5i %-4.4a%1A%3.-3n %1c%4.-4R%1E   %8.3x%8.3y%8.3z%6.2Q%6.2b          ", '\0', null)
                : t4h)
            : (t3h == null ? LabelToken.compile(viewer, "HETATM%5.-5i  %-3.3a%1A%3.-3n %1c%4.-4R%1E   %8.3x%8.3y%8.3z%6.2Q%6.2b          ", '\0', null)
                : t3h));
      else 
        tokens = (leftJustify ?
            (t4a == null ? LabelToken.compile(viewer, "ATOM  %5.-5i %-4.4a%1A%3.-3n %1c%4.-4R%1E   %8.3x%8.3y%8.3z%6.2Q%6.2b          ", '\0', null)
                : t4a)
            : (t3a == null ? LabelToken.compile(viewer, "ATOM  %5.-5i  %-3.3a%1A%3.-3n %1c%4.-4R%1E   %8.3x%8.3y%8.3z%6.2Q%6.2b          ", '\0', null)
                : t3a));
      String XX = a.getElementSymbol(false).toUpperCase();
      sb.append(LabelToken.formatLabel(viewer, a, tokens, '\0', null))
          .append(XX.length() == 1 ? " " + XX : XX.substring(0, 2)).append("  \n");
    }
    if (showModels)
      sb.append("ENDMDL\n");
    return sb.toString();
  }
  
  /* **********************
   * 
   * Jmol Data Frame methods
   * 
   *****************************/

  public String getPdbData(int modelIndex, String type, BitSet bsSelected,
                           Object[] parameters, OutputStringBuffer sb) {
    if (isJmolDataFrame(modelIndex))
      modelIndex = getJmolDataSourceFrame(modelIndex);
    if (modelIndex < 0)
      return "";
    boolean isPDB = models[modelIndex].isPDB;
    if (parameters == null && !isPDB)
      return null;
    Model model = models[modelIndex];
    if (sb == null)
      sb = new OutputStringBuffer(null);
    StringBuffer pdbCONECT = new StringBuffer();
    boolean isDraw = (type.indexOf("draw") >= 0);
    BitSet bsAtoms = null;
    BitSet bsWritten = new BitSet();
    char ctype = '\0';
    LabelToken[] tokens = LabelToken.compile(viewer, "ATOM  %-6i%4a%1A%3n %1c%4R%1E   ", 
        '\0', null);
    if (parameters == null) {
      boolean bothEnds = false;
      ctype = (type.length() > 11 && type.indexOf("quaternion ") >= 0 ? type
          .charAt(11) : 'R');
      char qtype = (ctype != 'R' ? 'r' : type.length() > 13
          && type.indexOf("ramachandran ") >= 0 ? type.charAt(13) : 'R');
      if (qtype == 'r')
        qtype = viewer.getQuaternionFrame();
      int mStep = viewer.getHelixStep();
      int derivType = (type.indexOf("diff") < 0 ? 0 : type.indexOf("2") < 0 ? 1
          : 2);
      int nPoly = model.getBioPolymerCount();
      if (!isDraw) {
        sb.append("REMARK   6 Jmol PDB-encoded data: " + type + ";");
        if (ctype != 'R') {
          sb.append("  quaternionFrame = \"" + qtype + "\"");
          bothEnds = true; //???
        }
        sb.append("\nREMARK   6 Jmol Version ").append(Viewer.getJmolVersion())
            .append('\n');
        if (ctype == 'R')
          sb
              .append("REMARK   6 Jmol data min = {-180 -180 -180} max = {180 180 180} "
                  + "unScaledXyz = xyz * {1 1 1} + {0 0 0} plotScale = {100 100 100}\n");
        else
          sb
              .append("REMARK   6 Jmol data min = {-1 -1 -1} max = {1 1 1} "
                  + "unScaledXyz = xyz * {0.1 0.1 0.1} + {0 0 0} plotScale = {100 100 100}\n");
      }
      
      for (int p = 0; p < nPoly; p++)
        model.bioPolymers[p].getPdbData(viewer, ctype, qtype, mStep, derivType,
            bsAtoms, bsSelected, bothEnds, isDraw, p == 0, tokens, sb, 
            pdbCONECT, bsWritten);
      bsAtoms = viewer.getModelUndeletedAtomsBitSet(modelIndex);
    } else {
      // plot property x y z....
      bsAtoms = (BitSet) parameters[0];
      float[] dataX = (float[]) parameters[1];
      float[] dataY = (float[]) parameters[2];
      float[] dataZ = (float[]) parameters[3];
      boolean haveZ = (dataZ != null);
      Point3f minXYZ = (Point3f) parameters[4];
      Point3f maxXYZ = (Point3f) parameters[5];
      Point3f factors = (Point3f) parameters[6];
      Point3f center = (Point3f) parameters[7];
      sb.append("REMARK   6 Jmol PDB-encoded data: ").append(type)
          .append(";\n");
      sb.append("REMARK   6 Jmol data").append(" min = ").append(
          Escape.escape(minXYZ)).append(" max = ")
          .append(Escape.escape(maxXYZ)).append(" unScaledXyz = xyz * ")
          .append(Escape.escape(factors)).append(" + ").append(
              Escape.escape(center)).append(";\n");
      String strExtra = "";
      Atom atomLast = null;
      for (int i = bsAtoms.nextSetBit(0), n = 0; i >= 0; i = bsAtoms
          .nextSetBit(i + 1), n++) {
        float x = dataX[n];
        float y = dataY[n];
        float z = (haveZ ? dataZ[n] : 0f);
        if (Float.isNaN(x) || Float.isNaN(y) || Float.isNaN(z))
          continue;
        Atom a = atoms[i];
        sb.append(LabelToken.formatLabel(viewer, a, tokens, '\0', null));
        if (isPDB)
          bsWritten.set(i);
        sb.append(TextFormat.sprintf(
            "%-8.2f%-8.2f%-10.2f    %6.3f          %2s    %s\n", new Object[] {
                a.getElementSymbol(false).toUpperCase(), strExtra,
                new float[] { x, y, z, 0f } }));
        if (atomLast != null
            && atomLast.getPolymerIndexInModel() == a.getPolymerIndexInModel())
          pdbCONECT.append("CONECT").append(
              TextFormat.formatString("%5i", "i", atomLast.getAtomNumber()))
              .append(TextFormat.formatString("%5i", "i", a.getAtomNumber()))
              .append('\n');
        atomLast = a;
      }
    }
    sb.append(pdbCONECT.toString());
    if (isDraw)
      return sb.toString();
    bsSelected.and(bsAtoms);
    if (isPDB)
      sb.append("\n\n"
          + getProteinStructureState(bsWritten, false, ctype == 'R', 1));
    return sb.toString();
  }

  public boolean isJmolDataFrame(int modelIndex) {
    return (modelIndex >= 0 && modelIndex < modelCount && models[modelIndex].isJmolDataFrame);
  }
  
  private boolean isJmolDataFrame(Atom atom) {
    return (models[atom.modelIndex].isJmolDataFrame);
  }

  public void setJmolDataFrame(String type, int modelIndex, int modelDataIndex) {
    Model model = models[type == null ? models[modelDataIndex].dataSourceFrame
        : modelIndex];
    if (type == null) {
      //leaving a data frame -- just set generic to this one if quaternion
      type = models[modelDataIndex].jmolFrameType;
    }
    if (modelIndex >= 0) {
      if (model.dataFrames == null) {
        model.dataFrames = new Hashtable<String, Integer>();
      }
      models[modelDataIndex].dataSourceFrame = modelIndex;
      models[modelDataIndex].jmolFrameType = type;
      model.dataFrames.put(type, Integer.valueOf(modelDataIndex));
    }  
    if (type.startsWith("quaternion") && type.indexOf("deriv") < 0) { //generic quaternion
      type = type.substring(0, type.indexOf(" "));
      model.dataFrames.put(type, Integer.valueOf(modelDataIndex));
    }
  }

  public int getJmolDataFrameIndex(int modelIndex, String type) {
    if (models[modelIndex].dataFrames == null) {
      return -1;
    }
    Integer index = models[modelIndex].dataFrames.get(type);
    return (index == null ? -1 : index.intValue());
  }

  protected void clearDataFrameReference(int modelIndex) {
    for (int i = 0; i < modelCount; i++) { 
      Map<String, Integer> df = models[i].dataFrames;
      if (df == null) {
        continue;
      }
      Iterator<Integer> e = df.values().iterator();
      while (e.hasNext()) {
        if ((e.next()).intValue() == modelIndex) {
          e.remove();
        }
      }
    }  
  }
  
  public String getJmolFrameType(int modelIndex) {
    return (modelIndex >= 0 && modelIndex < modelCount ? 
        models[modelIndex].jmolFrameType : "modelSet");
  }

  public int getJmolDataSourceFrame(int modelIndex) {
    return (modelIndex >= 0 && modelIndex < modelCount ? 
        models[modelIndex].dataSourceFrame : -1);
  }

  public void saveModelOrientation(int modelIndex, Orientation orientation) {
    models[modelIndex].orientation = orientation;
  }

  public Orientation getModelOrientation(int modelIndex) {
    return models[modelIndex].orientation;
  }

  /*
   final static String[] pdbRecords = { "ATOM  ", "HELIX ", "SHEET ", "TURN  ",
   "MODEL ", "SCALE",  "HETATM", "SEQRES",
   "DBREF ", };
   */

  private final static String[] pdbRecords = { "ATOM  ", "MODEL ", "HETATM" };

  private String getFullPDBHeader(int modelIndex) {
    if (modelIndex < 0)
      return "";
    String info = (String) getModelAuxiliaryInfo(modelIndex, "fileHeader");
    if (info != null)
      return info;
    info = viewer.getCurrentFileAsString();
    int ichMin = info.length();
    for (int i = pdbRecords.length; --i >= 0;) {
      int ichFound;
      String strRecord = pdbRecords[i];
      switch (ichFound = (info.startsWith(strRecord) ? 0 : info.indexOf("\n"
          + strRecord))) {
      case -1:
        continue;
      case 0:
        setModelAuxiliaryInfo(modelIndex, "fileHeader", "");
        return "";
      default:
        if (ichFound < ichMin)
          ichMin = ++ichFound;
      }
    }
    info = info.substring(0, ichMin);
    setModelAuxiliaryInfo(modelIndex, "fileHeader", info);
    return info;
  }

  public String getPDBHeader(int modelIndex) {
    return (isPDB ? getFullPDBHeader(modelIndex) : getFileHeader(modelIndex));
  }

  public String getFileHeader(int modelIndex) {
    if (modelIndex < 0)
      return "";
    if (isPDB)
      return getFullPDBHeader(modelIndex);
    String info = (String) getModelAuxiliaryInfo(modelIndex, "fileHeader");
    if (info == null)
      info = modelSetName;
    if (info != null)
      return info;
    return "no header information found";
  }

  public Map<String, Object> getModelInfo(BitSet bsModels) {
    Map<String, Object> info = new Hashtable<String, Object>();
    info.put("modelSetName", modelSetName);
    info.put("modelCount", Integer.valueOf(modelCount));
    info.put("modelSetHasVibrationVectors", Boolean
        .valueOf(modelSetHasVibrationVectors()));
    if (modelSetProperties != null) {
      info.put("modelSetProperties", modelSetProperties);
    }
    info.put("modelCountSelected", Integer.valueOf(BitSetUtil.cardinalityOf(bsModels)));
    info.put("modelsSelected", bsModels);
    List<Map<String, Object>> vModels = new ArrayList<Map<String,Object>>();
    getMolecules();
    
    for (int i = bsModels.nextSetBit(0); i >= 0; i = bsModels.nextSetBit(i + 1)) {
      Map<String, Object> model = new Hashtable<String, Object>();
      model.put("_ipt", Integer.valueOf(i));
      model.put("num", Integer.valueOf(getModelNumber(i)));
      model.put("file_model", getModelNumberDotted(i));
      model.put("name", getModelName(i));
      String s = getModelTitle(i);
      if (s != null) {
        model.put("title", s);
      }
      s = getModelFileName(i);
      if (s != null) {
        model.put("file", s);
      }
      model.put("vibrationVectors", Boolean.valueOf(modelHasVibrationVectors(i)));
      model.put("atomCount", Integer.valueOf(models[i].atomCount));
      model.put("bondCount", Integer.valueOf(models[i].getBondCount()));
      model.put("groupCount", Integer.valueOf(models[i].getGroupCount()));
      model.put("moleculeCount", Integer.valueOf(models[i].moleculeCount));
      model.put("polymerCount", Integer.valueOf(models[i].bioPolymerCount));
      model.put("chainCount", Integer.valueOf(getChainCountInModel(i, true)));
      if (models[i].properties != null) {
        model.put("modelProperties", models[i].properties);
      }
      Float energy = (Float) getModelAuxiliaryInfo(i, "Energy");
      if (energy != null) {
        model.put("energy", energy);        
      }
      model.put("atomCount", Integer.valueOf(models[i].atomCount));
      vModels.add(model);
    }
    info.put("models", vModels);
    return info;
  }

  //////////////  individual models ////////////////

  public int getAltLocIndexInModel(int modelIndex, char alternateLocationID) {
    if (alternateLocationID == '\0') {
      return 0;
    }
    String altLocList = getAltLocListInModel(modelIndex);
    if (altLocList.length() == 0) {
      return 0;
    }
    return altLocList.indexOf(alternateLocationID) + 1;
  }

  public int getInsertionCodeIndexInModel(int modelIndex, char insertionCode) {
    if (insertionCode == '\0')
      return 0;
    String codeList = getInsertionListInModel(modelIndex);
    if (codeList.length() == 0)
      return 0;
    return codeList.indexOf(insertionCode) + 1;
  }

  public String getAltLocListInModel(int modelIndex) {
    if (modelIndex < 0)
      return "";
    String str = (String) getModelAuxiliaryInfo(modelIndex, "altLocs");
    return (str == null ? "" : str);
  }

  private String getInsertionListInModel(int modelIndex) {
    String str = (String) getModelAuxiliaryInfo(modelIndex, "insertionCodes");
    return (str == null ? "" : str);
  }

  public int getModelSymmetryCount(int modelIndex) {
    String[] operations;
    return (models[modelIndex].biosymmetryCount > 0 
        || unitCells == null || unitCells[modelIndex] == null 
        || (operations = unitCells[modelIndex].getSymmetryOperations()) == null
        ? models[modelIndex].biosymmetryCount : operations.length);
  }
  
  public String getSymmetryOperation(int modelIndex, String spaceGroup, 
                                     int symOp, Point3f pt1, Point3f pt2, 
                                     String drawID, boolean labelOnly) {
    Map<String, Object> sginfo = getSpaceGroupInfo(modelIndex, spaceGroup, symOp, pt1, pt2, drawID); 
    if (sginfo == null)
      return "";
    Object[][] infolist = (Object[][]) sginfo.get("operations");
    if (infolist == null)
      return "";
    StringBuffer sb = new StringBuffer();
    symOp--;
    for (int i = 0; i < infolist.length; i++) {
      if (infolist[i] == null || symOp >= 0 && symOp != i)
        continue;
      if (drawID != null)
        return (String) infolist[i][3];
      if (sb.length() > 0)
        sb.append('\n');
      if (!labelOnly) {
        if (symOp < 0)
          sb.append(i + 1).append("\t");
        sb.append(infolist[i][0]).append("\t");
      }
      sb.append(infolist[i][2]);
    }
    if (sb.length() == 0 && drawID != null)
      sb.append ("draw " + drawID + "* delete");
    return sb.toString();
  }

  public int[] getModelCellRange(int modelIndex) {
    if (unitCells == null)
      return null;
    return unitCells[modelIndex].getCellRange();
  }

  public boolean modelHasVibrationVectors(int modelIndex) {
    if (vibrationVectors != null)
      for (int i = atomCount; --i >= 0;)
        if ((modelIndex < 0 || atoms[i].modelIndex == modelIndex)
            && vibrationVectors[i] != null && vibrationVectors[i].length() > 0)
          return true;
    return false;
  }

  public BitSet getElementsPresentBitSet(int modelIndex) {
    if (modelIndex >= 0)
      return elementsPresent[modelIndex];
    BitSet bs = new BitSet();
    for (int i = 0; i < modelCount; i++)
      bs.or(elementsPresent[i]);
    return bs;
  }

  private String getSymmetryInfoAsString(int modelIndex) {
    SymmetryInterface unitCell = getUnitCell(modelIndex);
    return (unitCell == null ? "no symmetry information" 
        : unitCell.getSymmetryInfoString());
  }

  ///////// molecules /////////


  public List<Map<String, Object>> getMoleculeInfo(BitSet bsAtoms) {
    if (moleculeCount == 0) {
      getMolecules();
    }
    List<Map<String, Object>> V = new ArrayList<Map<String,Object>>();
    BitSet bsTemp = new BitSet();
    for (int i = 0; i < moleculeCount; i++) {
      bsTemp = BitSetUtil.copy(bsAtoms);
      JmolMolecule m = molecules[i];
      bsTemp.and(m.atomList);
      if (bsTemp.length() > 0) {
        Map<String, Object> info = new Hashtable<String, Object>();
        info.put("number", Integer.valueOf(m.moleculeIndex + 1)); //for now
        info.put("modelNumber", getModelNumberDotted(m.modelIndex));
        info.put("numberInModel", Integer.valueOf(m.indexInModel + 1));
        info.put("nAtoms", Integer.valueOf(m.atomCount));
        info.put("nElements", Integer.valueOf(m.nElements));
        info.put("mf", m.getMolecularFormula(false));
        V.add(info);
      }
    }
    return V;
  }

  public int getMoleculeIndex(int atomIndex) {
    //ColorManager
    if (moleculeCount == 0)
      getMolecules();
    for (int i = 0; i < moleculeCount; i++) {
      if (molecules[i].atomList.get(atomIndex))
        return molecules[i].indexInModel;
    }
    return 0;
  }
  
  public BitSet getMoleculeBitSet(BitSet bs) {
    // returns cumulative sum of all atoms in molecules containing these atoms
    if (moleculeCount == 0)
      getMolecules();
    BitSet bsResult = BitSetUtil.copy(bs);
    BitSet bsInitial = BitSetUtil.copy(bs);
    int i;
    BitSet bsTemp = new BitSet();
    while ((i = bsInitial.length()) > 0) {
      bsTemp = getMoleculeBitSet(i - 1);
      bsInitial.andNot(bsTemp);
      bsResult.or(bsTemp);
    }
    return bsResult;
  }

  public BitSet getMoleculeBitSet(int atomIndex) {
    if (moleculeCount == 0)
      getMolecules();
    for (int i = 0; i < moleculeCount; i++)
      if (molecules[i].atomList.get(atomIndex))
        return molecules[i].atomList;
    return null;
  }


  public void rotateAtoms(Matrix3f mNew, Matrix3f matrixRotate,
                             BitSet bsAtoms, boolean fullMolecule,
                             Point3f center, boolean isInternal) {
    bspf = null;
    BitSet bs = (fullMolecule ? getMoleculeBitSet(bsAtoms) : BitSetUtil.copy(bsAtoms));
    BitSetUtil.andNot(bs, viewer.getMotionFixedAtoms());
    if (mNew == null) {
      matTemp.set(matrixRotate);
    } else {
      matInv.set(matrixRotate);
      matInv.invert();
      ptTemp.set(0, 0, 0);
      matTemp.mul(mNew, matrixRotate);
      matTemp.mul(matInv, matTemp);
    }
    int n = 0;
    for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i+1)) {
        if (isInternal) {
          atoms[i].sub(center);
          matTemp.transform(atoms[i]);
          atoms[i].add(center);          
        } else {
          ptTemp.add(atoms[i]);
          matTemp.transform(atoms[i]);
          ptTemp.sub(atoms[i]);
        }
        taint(i, TAINT_COORD);
        n++;
      }
    if (n == 0)
      return;
    if (!isInternal) {
      ptTemp.scale(1f / n);
      for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) 
        atoms[i].add(ptTemp);
    }
    recalculateLeadMidpointsAndWingVectors(-1);
  }

  public void invertSelected(Point3f pt, Point4f plane, int iAtom,
                             BitSet invAtoms, BitSet bs) {
    bspf = null;
    if (pt != null) {
      for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
        float x = (pt.x - atoms[i].x) * 2;
        float y = (pt.y - atoms[i].y) * 2;
        float z = (pt.z - atoms[i].z) * 2;
        setAtomCoordRelative(i, x, y, z);
      }
      return;
    }
    if (plane != null) {
      // ax + by + cz + d = 0
      Vector3f norm = new Vector3f(plane.x, plane.y, plane.z);
      norm.normalize();
      float d = (float) Math.sqrt(plane.x * plane.x + plane.y * plane.y
          + plane.z * plane.z);
      for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
        float twoD = -Measure.distanceToPlane(plane, d, atoms[i]) * 2;
        float x = norm.x * twoD;
        float y = norm.y * twoD;
        float z = norm.z * twoD;
        setAtomCoordRelative(i, x, y, z);
      }
      return;
    }
    if (iAtom >= 0) {
      Atom thisAtom = atoms[iAtom];
      // stereochemical inversion at iAtom
      Bond[] bonds = thisAtom.bonds;
      if (bonds == null)
        return;
      BitSet bsAtoms = new BitSet();
      List<Point3f> vNot = new ArrayList<Point3f>();
      BitSet bsModel = viewer.getModelUndeletedAtomsBitSet(thisAtom.modelIndex);
      for (int i = 0; i < bonds.length; i++) {
        Atom a = bonds[i].getOtherAtom(thisAtom);
        if (invAtoms.get(a.index)) {
            bsAtoms.or(JmolMolecule.getBranchBitSet(atoms, bsModel, a.index, iAtom, true, true));
        } else {
          vNot.add(a);
        }
      }
      if (vNot.size() == 0)
        return;
      pt = Measure.getCenterAndPoints(vNot)[0];
      Vector3f v = new Vector3f(thisAtom);
      v.sub(pt);
      Quaternion q = new Quaternion(v, 180);
      rotateAtoms(null, q.getMatrix(), bsAtoms, false, thisAtom, true);
    }
  }

  public Vector3f getModelDipole(int modelIndex) {
    if (modelIndex < 0)
      return null;
    Vector3f dipole = (Vector3f) getModelAuxiliaryInfo(modelIndex, "dipole");
    if (dipole == null)
      dipole = (Vector3f) getModelAuxiliaryInfo(modelIndex, "DIPOLE_VEC");
    return dipole;
  }

  ////////// molecules /////////////

  public Vector3f calculateMolecularDipole(int modelIndex) {
    if (partialCharges == null || modelIndex < 0)
      return null;
    int nPos = 0;
    int nNeg = 0;
    float cPos = 0;
    float cNeg = 0;
    Vector3f pos = new Vector3f();
    Vector3f neg = new Vector3f();
    for (int i = 0; i < atomCount; i++) {
      if (atoms[i].modelIndex != modelIndex)
        continue;
      float c = partialCharges[i];
      if (c < 0) {
        nNeg++;
        cNeg += c;
        neg.scaleAdd(c, atoms[i], neg);
      } else if (c > 0) {
        nPos++;
        cPos += c;
        pos.scaleAdd(c, atoms[i], pos);
      }
    }
    if (nNeg == 0 || nPos == 0)
      return null;
    pos.scale(1f/cPos);
    neg.scale(1f/cNeg);
    pos.sub(neg);
    Logger.warn("CalculateMolecularDipole: this is an approximate result -- needs checking");
    pos.scale(cPos * 4.8f); //1e-10f * 1.6e-19f/ 3.336e-30f;
    
    // SUM_Q[(SUM_pos Q_iRi) / SUM_Q   -  (SUM_neg Q_iRi) / (-SUM_Q) ]    
    // this is really just SUM_i (Q_iR_i). Don't know why that would not work. 
    
    
    //http://www.chemistry.mcmaster.ca/esam/Chapter_7/section_3.html
    // 1 Debye = 3.336e-30 Coulomb-meter; C_e = 1.6022e-19 C
    return pos;
  }
  
  public int getMoleculeCountInModel(int modelIndex) {
    //ColorManager
    //not implemented for pop-up menu -- will slow it down.
    int n = 0;
    if (moleculeCount == 0)
      getMolecules();
    if (modelIndex < 0)
      return moleculeCount;
    for (int i = 0; i < modelCount; i++) {
      if (modelIndex == i)
        n += models[i].moleculeCount;
    }
    return n;
  }

  private BitSet selectedMolecules = new BitSet();
  private int selectedMoleculeCount;

  public void calcSelectedMoleculesCount(BitSet bsSelected) {
    if (moleculeCount == 0)
      getMolecules();
    selectedMolecules.xor(selectedMolecules);
    selectedMoleculeCount = 0;
    BitSet bsTemp = new BitSet();
    for (int i = 0; i < moleculeCount; i++) {
      BitSetUtil.copy(bsSelected, bsTemp);
      bsTemp.and(molecules[i].atomList);
      if (bsTemp.length() > 0) {
        selectedMolecules.set(i);
        selectedMoleculeCount++;
      }
    }
  }

  public JmolMolecule[] getMolecules() {
    if (moleculeCount > 0)
      return molecules;
    if (molecules == null)
      molecules = new JmolMolecule[4];
    moleculeCount = 0;
    BitSet bsExclude = new BitSet(atomCount);
    BitSet bsBranch;
    Model m = null;
    BitSet[] bsModelAtoms = new BitSet[modelCount];
    for (int i = 0; i < modelCount; i++) {
      // TODO: Trajetories?
      bsModelAtoms[i] = viewer.getModelUndeletedAtomsBitSet(i);
      m = models[i];
      m.moleculeCount = 0;
      int count = 0;
      int bpt = m.getBioPolymerCount();
      for (int j = 0; j < bpt; j++) {
        bsBranch = new BitSet();
        m.getBioPolymer(j).getRange(bsBranch);
        int iAtom = bsBranch.nextSetBit(0);
        if (iAtom >= 0)
          molecules = JmolMolecule.addMolecule(molecules, moleculeCount++, atoms,
              iAtom, bsBranch, m.modelIndex, count++, bsExclude);
      }
    }
    molecules = JmolMolecule.getMolecules(atoms, bsModelAtoms, molecules,
        moleculeCount, bsExclude);
    moleculeCount = molecules.length;
    for (int i = moleculeCount; --i >= 0;) {
      m = models[molecules[i].modelIndex];
      m.firstMoleculeIndex = i;
      m.moleculeCount++;
    }
    return molecules;
  }

  public boolean hasCalculatedHBonds(BitSet bs) {
    BitSet bsModels = getModelBitSet(bs, false);
    for (int i = bsModels.nextSetBit(0); i >= 0; i = bsModels.nextSetBit(i + 1))
      if (models[atoms[i].modelIndex].hasRasmolHBonds)
        return true;
    return false;
  }

  public void clearRasmolHydrogenBonds(int baseIndex, BitSet bsAtoms) {
    //called by calcRasmolHydrogenBonds (bsAtoms not null) fom autoHBond
    //      and setTrajectory (bsAtoms null)
    BitSet bsDelete = new BitSet();
    int nDelete = 0;
    models[baseIndex].hasRasmolHBonds = false;
    for (int i = bondCount; --i >= 0;) {
      Bond bond = bonds[i];
      if (baseIndex >= 0 
           && models[bond.atom1.modelIndex].trajectoryBaseIndex != baseIndex
          || (bond.order & JmolEdge.BOND_H_CALC_MASK) == 0)
        continue;
      if (bsAtoms != null && !bsAtoms.get(bond.atom1.index)) {
        models[baseIndex].hasRasmolHBonds = true;
        continue;
      }
      bsDelete.set(i);
      nDelete++;
    }        
    if (nDelete > 0)
      deleteBonds(bsDelete, false);
  }

  //////////// iterators //////////
  
  //private final static boolean MIX_BSPT_ORDER = false;
  private final static boolean showRebondTimes = true;

  protected void initializeBspf() {
    if (bspf == null) {
      if (showRebondTimes && Logger.debugging)
        Logger.startTimer();
      bspf = new Bspf(3);
      /*      if (MIX_BSPT_ORDER) {
       Logger.debug("mixing bspt order");
       int stride = 3;
       int step = (atomCount + stride - 1) / stride;
       for (int i = 0; i < step; ++i)
       for (int j = 0; j < stride; ++j) {
       int k = i * stride + j;
       if (k >= atomCount)
       continue;
       Atom atom = atoms[k];
       bspf.addTuple(atom.modelIndex, atom);
       }
       } else {
       */
      Logger.debug("sequential bspt order");
      for (int i = atomCount; --i >= 0;) {
        // important that we go backward here, because we are going to 
        // use System.arrayCopy to expand the array ONCE only
        Atom atom = atoms[i];
        bspf.addTuple(models[atom.modelIndex].trajectoryBaseIndex, atom);
      }
      //      }
      if (showRebondTimes && Logger.debugging) {
        Logger.checkTimer("Time to build bspf");
        bspf.stats();
        //        bspf.dump();
      }
    }
  }

  protected void initializeBspt(int modelIndex) {
    initializeBspf();
    if (bspf.isInitialized(modelIndex))
      return;
    bspf.initialize(modelIndex, atoms, viewer.getModelUndeletedAtomsBitSet(modelIndex));
  }
 
  public void setIteratorForPoint(AtomIndexIterator iterator, int modelIndex,
                                  Point3f pt, float distance) {
    initializeBspt(modelIndex);
    iterator.set(modelIndex, models[modelIndex].firstAtomIndex, Integer.MAX_VALUE, pt, distance);    
  }

  public void setIteratorForAtom(AtomIndexIterator iterator, int modelIndex,
                                   int atomIndex, float distance) {
    if (modelIndex < 0)
      modelIndex = atoms[atomIndex].modelIndex;
    modelIndex = models[modelIndex].trajectoryBaseIndex;    
    initializeBspt(modelIndex);
    iterator.set(modelIndex, models[modelIndex].firstAtomIndex, atomIndex, atoms[atomIndex], distance);    
  }

  public AtomIndexIterator getSelectedAtomIterator(BitSet bsSelected,
                                                    boolean isGreaterOnly,
                                                    boolean modelZeroBased, boolean hemisphereOnly) {
    //EnvelopeCalculation, IsoSolventReader
    // This iterator returns only atoms OTHER than the atom specified
    // and with the specified restrictions. 
    // Model zero-based means the index returned is within the model, 
    // not the full atom set. broken in 12.0.RC6; repaired in 12.0.RC15
    
    initializeBspf();
    AtomIteratorWithinModel iter = new AtomIteratorWithinModel();
    iter.initialize(bspf, bsSelected, isGreaterOnly, modelZeroBased, hemisphereOnly, viewer.isParallel()); 
    return iter;
  }
  
  ////////// bonds /////////

  @Override
  public int getBondCountInModel(int modelIndex) {
    return (modelIndex < 0 ? bondCount : models[modelIndex].getBondCount());
  }

  ////////// struts /////////


  /** see comments in org.jmol.modelsetbio.AlphaPolymer.java
   * 
   * Struts are calculated for atoms in bs1 connecting to atoms in bs2.
   * The two bitsets may overlap. 
   * 
   * @param bs1
   * @param bs2
   * @return     number of struts added
   */
  public int calculateStruts(BitSet bs1, BitSet bs2) {
    // select only ONE model
    makeConnections(0, Float.MAX_VALUE, JmolEdge.BOND_STRUT, JmolConstants.CONNECT_DELETE_BONDS, bs1, bs2, null, false, 0);
    int iAtom = bs1.nextSetBit(0);
    if (iAtom < 0)
      return 0;
    int modelIndex = atoms[iAtom].modelIndex;
    Model model = models[modelIndex];
    if (!model.isPDB)
      return 0;

    // only check the atoms in THIS model
    List<Atom> vCA = new ArrayList<Atom>();
    Atom a1 = null;
    BitSet bsCheck;
    if (bs1.equals(bs2)) {
      bsCheck = bs1;
    } else {
      bsCheck = BitSetUtil.copy(bs1);
      bsCheck.or(bs2);
    }
    bsCheck.and(viewer.getModelUndeletedAtomsBitSet(modelIndex));
    for (int i = bsCheck.nextSetBit(0); i >= 0; i = bsCheck.nextSetBit(i + 1))
      if (atoms[i].isVisible(0)
          && atoms[i].atomID == JmolConstants.ATOMID_ALPHA_CARBON
          && atoms[i].getGroupID() != JmolConstants.GROUPID_CYSTEINE)
        vCA.add((a1 = atoms[i]));
    if (vCA.size() == 0)
      return 0;    
    float thresh = viewer.getStrutLengthMaximum();
    short mad = (short) (viewer.getStrutDefaultRadius() * 2000);
    int delta = viewer.getStrutSpacingMinimum();
    boolean strutsMultiple = viewer.getStrutsMultiple();
    List<Atom[]> struts = model.getBioPolymer(a1.getPolymerIndexInModel())
        .calculateStruts((ModelSet) this, atoms, bs1, bs2, vCA, thresh, delta, strutsMultiple);
    for (int i = 0; i < struts.size(); i++) {
      Atom[] o = struts.get(i);
      bondAtoms(o[0], o[1], JmolEdge.BOND_STRUT, mad, null, 0, true);
    }
    return struts.size();
  }

  public int getAtomCountInModel(int modelIndex) {
    return (modelIndex < 0 ? atomCount : models[modelIndex].atomCount);
  }
  
  protected BitSet bsAll;

  protected ShapeManager shapeManager;
  
  /**
   * note -- this method returns ALL atoms, including deleted.
   * @param bsModels
   * @return   bitset of atoms
   */
  public BitSet getModelAtomBitSetIncludingDeleted(BitSet bsModels) {
    BitSet bs = new BitSet();
    if (bsModels == null && bsAll == null)
      bsAll = BitSetUtil.setAll(atomCount);
    if (bsModels == null)
      bs.or(bsAll);
    else
      for (int i = bsModels.nextSetBit(0); i >= 0; i = bsModels.nextSetBit(i + 1))
        bs.or(getModelAtomBitSetIncludingDeleted(i, false));
    return bs;
  }
  /**
   * Note that this method returns all atoms, included deleted ones.
   * If you don't want deleted atoms, then use viewer.getModelAtomBitSetUndeleted(modelIndex, TRUE)
   * 
   * @param modelIndex
   * @param asCopy     MUST BE TRUE IF THE BITSET IS GOING TO BE MODIFIED!
   * @return either the actual bitset or a copy
   */
  public BitSet getModelAtomBitSetIncludingDeleted(int modelIndex, boolean asCopy) {
    BitSet bs = (modelIndex < 0 ? bsAll : models[modelIndex].bsAtoms);
    if (bs == null)
      bs = bsAll = BitSetUtil.setAll(atomCount);
    return (asCopy ? BitSetUtil.copy(bs) : bs);
  }

  /**
   * general unqualified lookup of atom set type
   * 
   * @param tokType
   * @param specInfo
   * @return BitSet; or null if we mess up the type
   */
  @Override
  public BitSet getAtomBits(int tokType, Object specInfo) {
    int[] info;
    BitSet bs;
    Point3f pt;
    switch (tokType) {
    default:
      return super.getAtomBits(tokType, specInfo);
    case Token.basepair:
      return getBasePairBits((String)specInfo);
    case Token.boundbox:
      BoxInfo boxInfo = getBoxInfo((BitSet) specInfo, 1);
      bs = getAtomsWithin(boxInfo.getBoundBoxCornerVector().length() + 0.0001f,
          boxInfo.getBoundBoxCenter(), null, -1);
      for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1))
        if (!boxInfo.isWithin(atoms[i]))
          bs.clear(i);
      return bs;
    case Token.molecule:
      return getMoleculeBitSet((BitSet) specInfo);
    case Token.sequence:
      return getSequenceBits((String)specInfo, null);
    case Token.spec_seqcode_range:
      info = (int[]) specInfo;
      int seqcodeA = info[0];
      int seqcodeB = info[1];
      char chainID = (char) info[2];
      bs = new BitSet();
      boolean caseSensitive = viewer.getChainCaseSensitive();
      if (!caseSensitive)
        chainID = Character.toUpperCase(chainID);
      for (int i = modelCount; --i >= 0;)
        models[i].selectSeqcodeRange(seqcodeA, seqcodeB, chainID, bs,
            caseSensitive);
      return bs;
    case Token.specialposition:
      bs = new BitSet(atomCount);
      int modelIndex = -1;
      int nOps = 0;
      for (int i = atomCount; --i >= 0;) {
        Atom atom = atoms[i];
        BitSet bsSym = atom.getAtomSymmetry();
        if (bsSym != null) {
          if (atom.modelIndex != modelIndex) {
            modelIndex = atom.modelIndex;
            if (getModelCellRange(modelIndex) == null)
              continue;
            nOps = getModelSymmetryCount(modelIndex);
          }
          // special positions are characterized by
          // multiple operator bits set in the first (overall)
          // block of nOpts bits.
          // only strictly true with load {nnn mmm 1}

          int n = 0;
          for (int j = nOps; --j >= 0;)
            if (bsSym.get(j))
              if (++n > 1) {
                bs.set(i);
                break;
              }
        }
      }
      return bs;
    case Token.symmetry:
      return BitSetUtil.copy(bsSymmetry == null ? bsSymmetry = new BitSet(
              atomCount)
              : bsSymmetry);
    case Token.unitcell:
      // select UNITCELL (a relative quantity)
      bs = new BitSet();
      SymmetryInterface unitcell = viewer.getCurrentUnitCell();
      if (unitcell == null)
        return bs;
      Point3f cell = new Point3f(1, 1, 1);
      pt = new Point3f();
      for (int i = atomCount; --i >= 0;)
        if (isInLatticeCell(i, cell, pt, false))
          bs.set(i);
      return bs;
    case Token.cell:
      // select cell=555 (an absolute quantity)
      bs = new BitSet();
      info = (int[]) specInfo;
      Point3f ptcell = new Point3f(info[0] / 1000f, info[1] / 1000f,
          info[2] / 1000f);
      pt = new Point3f();
      boolean isAbsolute = !viewer.getFractionalRelative();
      for (int i = atomCount; --i >= 0;)
        if (isInLatticeCell(i, ptcell, pt, isAbsolute))
          bs.set(i);
      return bs;
    }
  }


  private boolean isInLatticeCell(int i, Point3f cell, Point3f pt, boolean isAbsolute) {
    // this is the one method that allows for an absolute fractional cell business
    // but it is always called with isAbsolute FALSE.
    // so then it is determining values for select UNITCELL and the like.
    
    int iModel = atoms[i].modelIndex;
    SymmetryInterface uc = getUnitCell(iModel);
    if (uc == null)
      return false;
    pt.set(atoms[i]);
    uc.toFractional(pt, isAbsolute);
    float slop = 0.02f;
    // {1 1 1} here is the original cell
    if (pt.x < cell.x - 1f - slop || pt.x > cell.x + slop)
      return false;
    if (pt.y < cell.y - 1f - slop || pt.y > cell.y + slop)
      return false;
    if (pt.z < cell.z - 1f - slop || pt.z > cell.z + slop)
      return false;
    return true;
  }


  /**
   * Get atoms within a specific distance of any atom in a specific set of atoms
   * either within all models or within just the model(s) of those atoms
   * 
   * @param distance
   * @param bs
   * @param withinAllModels
   * @return the set of atoms
   */
  public BitSet getAtomsWithin(float distance, BitSet bs,
                               boolean withinAllModels) {
    BitSet bsResult = new BitSet();
    BitSet bsCheck = getIterativeModels(false);
    AtomIndexIterator iter = getSelectedAtomIterator(null, false, false, false);
    if (withinAllModels) {
      for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1))
        for (int iModel = modelCount; --iModel >= 0;) {
          if (!bsCheck.get(iModel))
            continue;
          if (distance < 0) {
            getAtomsWithin(distance, atoms[i].getFractionalUnitCoord(true),
                bsResult, -1);
            continue;
          }
          setIteratorForAtom(iter, iModel, i, distance);
          iter.addAtoms(bsResult);
        }
    } else {
      bsResult.or(bs);
      for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
        if (distance < 0) {
            getAtomsWithin(distance, atoms[i], bsResult, atoms[i].modelIndex);
            continue;
          }
          setIteratorForAtom(iter, -1, i, distance);
          iter.addAtoms(bsResult);
        }
    }
    iter.release();
    return bsResult;
  }

  public BitSet getGroupsWithin(int nResidues, BitSet bs) {
    BitSet bsCheck = getIterativeModels(false);
    BitSet bsResult = new BitSet();
    for (int iModel = modelCount; --iModel >= 0;) {
      if (!bsCheck.get(iModel))
        continue;
      Model m = models[iModel];
      for (int i = m.bioPolymerCount; --i >= 0;)
        m.bioPolymers[i].getRangeGroups(nResidues, bs, bsResult);
    }
    return bsResult;
  }

  public BitSet getAtomsWithin(float distance, Point3f coord, BitSet bsResult,
                               int modelIndex) {

     if (bsResult == null)
      bsResult = new BitSet();

    if (distance < 0) { // check all unitCell distances
      distance = -distance;
      final Point3f ptTemp1 = new Point3f();
      final Point3f ptTemp2 = new Point3f();
      for (int i = atomCount; --i >= 0;) {
        Atom atom = atoms[i];
        if (modelIndex >= 0 && atoms[i].modelIndex != modelIndex)
          continue;
        if (!bsResult.get(i)
            && atom.getFractionalUnitDistance(coord, ptTemp1, ptTemp2) <= distance)
          bsResult.set(atom.index);
      }
      return bsResult;
    }

    BitSet bsCheck = getIterativeModels(true);
    AtomIndexIterator iter = getSelectedAtomIterator(null, false, false, false);
    for (int iModel = modelCount; --iModel >= 0;) {
      if (!bsCheck.get(iModel))
        continue;
      setIteratorForAtom(iter, -1, models[iModel].firstAtomIndex, -1);
      iter.set(coord, distance);
      iter.addAtoms(bsResult);
    }
    iter.release();
    return bsResult;
  }
 
  private String getBasePairInfo(BitSet bs) {
    StringBuffer info = new StringBuffer();
    List<Bond> vHBonds = new ArrayList<Bond>();
    calcRasmolHydrogenBonds(bs, bs, vHBonds, true, 1, false);      
    for (int i = vHBonds.size(); --i >= 0;) {
      Bond b = vHBonds.get(i);
      getAtomResidueInfo(info, b.atom1);
      info.append(" - ");
      getAtomResidueInfo(info, b.atom2);
      info.append("\n");
    }
    return info.toString();
  }

  private static void getAtomResidueInfo(StringBuffer info, Atom atom) {
    info.append("[").append(atom.getGroup3(false)).append("]").append(
        atom.getSeqcodeString()).append(":");
    char id = atom.getChainID();
    info.append(id == '\0' ? " " : "" + id);
  }

  private BitSet getBasePairBits(String specInfo) {
    BitSet bs = new BitSet();
    if (specInfo.length() % 2 != 0)
      return bs;
    BitSet bsA = null;
    BitSet bsB = null;
    List<Bond> vHBonds = new ArrayList<Bond>();
    if (specInfo.length() == 0) {
      bsA = bsB = viewer.getModelUndeletedAtomsBitSet(-1);
      calcRasmolHydrogenBonds(bsA, bsB, vHBonds, true, 1, false);      
    } else {
      for (int i = 0; i < specInfo.length();) {
        bsA = getSequenceBits(specInfo.substring(i, ++i), null);
        if (bsA.cardinality() == 0)
          continue;
        bsB = getSequenceBits(specInfo.substring(i, ++i), null);
        if (bsB.cardinality() == 0)
          continue;
        calcRasmolHydrogenBonds(bsA, bsB, vHBonds, true, 1, false);
      }
    }
    BitSet bsAtoms = new BitSet();
    for (int i = vHBonds.size(); --i >= 0;) {
      Bond b = vHBonds.get(i);
      bsAtoms.set(b.atom1.index);
      bsAtoms.set(b.atom2.index);
    }
    return super.getAtomBits(Token.group, bsAtoms);
  }

  public BitSet getSequenceBits(String specInfo, BitSet bs) {
    if (bs == null)
      bs = viewer.getModelUndeletedAtomsBitSet(-1);
    int lenInfo = specInfo.length();
    BitSet bsResult = new BitSet();
    if (lenInfo == 0)
      return bsResult;
    for (int i = 0; i < modelCount; ++i) {
      int polymerCount = getBioPolymerCountInModel(i);
      for (int ip = 0; ip < polymerCount; ip++) {
        Polymer bp = models[i].getBioPolymer(ip);
        String sequence = bp.getSequence();
        int j = -1;
        while ((j = sequence.indexOf(specInfo, ++j)) >=0)
          bp.getPolymerSequenceAtoms(j, lenInfo, bs, bsResult);
      }
    }
    return bsResult;
  }

  // ////////// Bonding methods ////////////

  @Override
  public void deleteBonds(BitSet bsBonds, boolean isFullModel) {
    if (!isFullModel) {
      BitSet bsA = new BitSet();
      BitSet bsB = new BitSet();
      for (int i = bsBonds.nextSetBit(0); i >= 0; i = bsBonds.nextSetBit(i + 1)) {
        Atom atom1 = bonds[i].atom1;
        if (models[atom1.modelIndex].isModelKit)
          continue;
        bsA.clear();
        bsB.clear();
        bsA.set(atom1.index);
        bsB.set(bonds[i].getAtomIndex2());
        addStateScript("connect ", null, bsA, bsB, "delete", false, true);
      }
    }
    super.deleteBonds(bsBonds, isFullModel);
  }

  protected int[] makeConnections(float minDistance, float maxDistance,
                                  int order, int connectOperation,
                                  BitSet bsA, BitSet bsB, BitSet bsBonds,
                                  boolean isBonds, float energy) {
    if (bsBonds == null)
      bsBonds = new BitSet();
    boolean matchAny = (order == JmolEdge.BOND_ORDER_ANY);
    boolean matchNull = (order == JmolEdge.BOND_ORDER_NULL);
    if (matchNull)
      order = JmolEdge.BOND_COVALENT_SINGLE; //default for setting
    boolean matchHbond = Bond.isHydrogen(order);
    boolean identifyOnly = false;
    boolean modifyOnly = false;
    boolean createOnly = false;
    boolean autoAromatize = false;
    float minDistanceSquared = minDistance * minDistance;
    float maxDistanceSquared = maxDistance * maxDistance;
    switch (connectOperation) {
    case JmolConstants.CONNECT_DELETE_BONDS:
      return deleteConnections(minDistance, maxDistance, order, bsA, bsB,
          isBonds, matchNull, minDistanceSquared, maxDistanceSquared);
    case JmolConstants.CONNECT_AUTO_BOND:
      if (order != JmolEdge.BOND_AROMATIC)
        return autoBond(bsA, bsB, bsBonds, isBonds, matchHbond);
      modifyOnly = true;
      autoAromatize = true;
      break;
    case JmolConstants.CONNECT_IDENTIFY_ONLY:
      identifyOnly = true;
      break;
    case JmolConstants.CONNECT_MODIFY_ONLY:
      modifyOnly = true;
      break;
    case JmolConstants.CONNECT_CREATE_ONLY:
      createOnly = true;
      break;
    }
    defaultCovalentMad = viewer.getMadBond();
    boolean minDistanceIsFractionRadius = (minDistance < 0);
    boolean maxDistanceIsFractionRadius = (maxDistance < 0);
    if (minDistanceIsFractionRadius)
      minDistance = -minDistance;
    if (maxDistanceIsFractionRadius)
      maxDistance = -maxDistance;
    short mad = getDefaultMadFromOrder(order);
    int nNew = 0;
    int nModified = 0;
    Bond bondAB = null;
    int m = (isBonds ? 1 : atomCount);
    Atom atomA = null;
    Atom atomB = null;
    float dAB = 0;
    float dABcalc = 0;
    short newOrder = (short) (order | JmolEdge.BOND_NEW);
    for (int iA = bsA.nextSetBit(0); iA >= 0; iA = bsA.nextSetBit(iA + 1)) {
      if (isBonds) {
        bondAB = bonds[iA];
        atomA = bondAB.atom1;
        atomB = bondAB.atom2;
      } else {
        atomA = atoms[iA];
        if (atomA.isDeleted())
          continue;
      }
      for (int iB = (isBonds ? m : bsB.nextSetBit(0)); iB >= 0; iB = (isBonds ? iB - 1 : bsB.nextSetBit(iB + 1))) {
        if (!isBonds) {
          if (iB == iA)
            continue;
          atomB = atoms[iB];
          if (atomA.modelIndex != atomB.modelIndex || atomB.isDeleted())
            continue;
          if (atomA.alternateLocationID != atomB.alternateLocationID
              && atomA.alternateLocationID != '\0'
              && atomB.alternateLocationID != '\0')
            continue;
          bondAB = atomA.getBond(atomB);
        }
        if (bondAB == null && (identifyOnly || modifyOnly) || bondAB != null
            && createOnly)
          continue;
        float distanceSquared = atomA.distanceSquared(atomB);
        if (minDistanceIsFractionRadius || maxDistanceIsFractionRadius) {
          dAB = atomA.distance(atomB);
          dABcalc = atomA.getBondingRadiusFloat() + atomB.getBondingRadiusFloat();
        }
        if ((minDistanceIsFractionRadius ? dAB < dABcalc * minDistance 
                 : distanceSquared < minDistanceSquared)
            || (maxDistanceIsFractionRadius ? dAB > dABcalc * maxDistance 
                 : distanceSquared > maxDistanceSquared))
          continue;       
        if (bondAB != null) {
          if (!identifyOnly && !matchAny) {
            bondAB.setOrder(order);
            bsAromatic.clear(bondAB.index);
          }
          if (!identifyOnly || matchAny 
              || order == bondAB.order || newOrder == bondAB.order  
              || matchHbond && bondAB.isHydrogen()) {
            bsBonds.set(bondAB.index);
            nModified++;
          }
        } else {
          bsBonds.set(
               bondAtoms(atomA, atomB, order, mad, bsBonds, energy, true).index);
          nNew++;
        }
      }
    }
    if (autoAromatize)
      assignAromaticBonds(true, bsBonds);
    if (!identifyOnly)
      shapeManager.setShapeSize(JmolConstants.SHAPE_STICKS, Integer.MIN_VALUE, null, bsBonds);
    return new int[] { nNew, nModified };
  }

  public int autoBond(BitSet bsA, BitSet bsB, BitSet bsExclude, BitSet bsBonds,
                      short mad, boolean preJmol11_9_24) {
    // unfortunately, 11.9.24 changed the order with which atoms were processed
    // for autobonding. This means that state script prior to that that use
    // select BOND will be misread by later version.
    if (preJmol11_9_24)
      return autoBond_Pre_11_9_24(bsA, bsB, bsExclude, bsBonds, mad);
    if (atomCount == 0)
      return 0;
    if (mad == 0)
      mad = 1;
    // null values for bitsets means "all"
    if (maxBondingRadius == Float.MIN_VALUE)
      findMaxRadii();
    float bondTolerance = viewer.getBondTolerance();
    float minBondDistance = viewer.getMinBondDistance();
    float minBondDistance2 = minBondDistance * minBondDistance;
    int nNew = 0;
    if (showRebondTimes)// && Logger.debugging)
      Logger.startTimer();
    /*
     * miguel 2006 04 02 note that the way that these loops + iterators are
     * constructed, everything assumes that all possible pairs of atoms are
     * going to be looked at. for example, the hemisphere iterator will only
     * look at atom indexes that are >= (or <= ?) the specified atom. if we are
     * going to allow arbitrary sets bsA and bsB, then this will not work. so,
     * for now I will do it the ugly way. maybe enhance/improve in the future.
     */
    int lastModelIndex = -1;
    boolean isAll = (bsA == null);
    BitSet bsCheck;
    int i0;
    if (isAll) {
      i0 = 0;
      bsCheck = null;
    } else {
      if (bsA.equals(bsB)) {
        bsCheck = bsA;
      } else {
        bsCheck = BitSetUtil.copy(bsA);
        bsCheck.or(bsB);
      }
      i0 = bsCheck.nextSetBit(0);
    }
    AtomIndexIterator iter = getSelectedAtomIterator(null, false, false, true);
    for (int i = i0; i >= 0 && i < atomCount; i = (isAll ? i + 1 : bsCheck
        .nextSetBit(i + 1))) {
      boolean isAtomInSetA = (isAll || bsA.get(i));
      boolean isAtomInSetB = (isAll || bsB.get(i));
      Atom atom = atoms[i];
      if (atom.isDeleted())
        continue;
      int modelIndex = atom.modelIndex;
      // no connections allowed in a data frame
      if (modelIndex != lastModelIndex) {
        lastModelIndex = modelIndex;
        if (isJmolDataFrame(modelIndex)) {
          i = models[modelIndex].firstAtomIndex + models[modelIndex].atomCount
              - 1;
          continue;
        }
      }
      // Covalent bonds
      float myBondingRadius = atom.getBondingRadiusFloat();
      if (myBondingRadius == 0)
        continue;
      boolean isFirstExcluded = (bsExclude != null && bsExclude.get(i));
      float searchRadius = myBondingRadius + maxBondingRadius + bondTolerance;
      setIteratorForAtom(iter, -1, i, searchRadius);
      while (iter.hasNext()) {
        Atom atomNear = atoms[iter.next()];
        if (atomNear.isDeleted())
          continue;
        int atomIndexNear = atomNear.index;
        boolean isNearInSetA = (isAll || bsA.get(atomIndexNear));
        boolean isNearInSetB = (isAll || bsB.get(atomIndexNear));
        // BOTH must be excluded in order to ignore bonding
        if (!isNearInSetA && !isNearInSetB
            || !(isAtomInSetA && isNearInSetB || isAtomInSetB && isNearInSetA)
            || isFirstExcluded && bsExclude.get(atomIndexNear))
          continue;
        short order = getBondOrder(myBondingRadius, atomNear
            .getBondingRadiusFloat(), iter.foundDistance2(), minBondDistance2,
            bondTolerance);
        if (order > 0 && checkValencesAndBond(atom, atomNear, order, mad, bsBonds))
            nNew++;
      }
      iter.release();
    }
    if (showRebondTimes)//&& Logger.debugging)
      Logger.checkTimer("Time to autoBond");
    return nNew;
  }

  private int autoBond_Pre_11_9_24(BitSet bsA, BitSet bsB, BitSet bsExclude, BitSet bsBonds, short mad) {
    if (atomCount == 0)
      return 0;
    if (mad == 0)
      mad = 1;
    // null values for bitsets means "all"
    if (maxBondingRadius == Float.MIN_VALUE)
      findMaxRadii();
    float bondTolerance = viewer.getBondTolerance();
    float minBondDistance = viewer.getMinBondDistance();
    float minBondDistance2 = minBondDistance * minBondDistance;
    int nNew = 0;
    initializeBspf();
    if (showRebondTimes && Logger.debugging)
      Logger.startTimer();
    /*
     * miguel 2006 04 02
     * note that the way that these loops + iterators are constructed,
     * everything assumes that all possible pairs of atoms are going to
     * be looked at.
     * for example, the hemisphere iterator will only look at atom indexes
     * that are >= (or <= ?) the specified atom.
     * if we are going to allow arbitrary sets bsA and bsB, then this will
     * not work.
     * so, for now I will do it the ugly way.
     * maybe enhance/improve in the future.
     */
    int lastModelIndex = -1;
    for (int i = atomCount; --i >= 0;) {
      boolean isAtomInSetA = (bsA == null || bsA.get(i));
      boolean isAtomInSetB = (bsB == null || bsB.get(i));
      if (!isAtomInSetA && !isAtomInSetB)
        //|| bsExclude != null && bsExclude.get(i))
        continue;
      Atom atom = atoms[i];
      if (atom.isDeleted())
        continue;
      int modelIndex = atom.modelIndex;
      //no connections allowed in a data frame
      if (modelIndex != lastModelIndex) {
        lastModelIndex = modelIndex;
        if (isJmolDataFrame(modelIndex)) {
          for (; --i >= 0;)
            if (atoms[i].modelIndex != modelIndex)
              break;
          i++;
          continue;
        }
      }
      // Covalent bonds
      float myBondingRadius = atom.getBondingRadiusFloat();
      if (myBondingRadius == 0)
        continue;
      float searchRadius = myBondingRadius + maxBondingRadius + bondTolerance;
      initializeBspt(modelIndex);
      CubeIterator iter = bspf.getCubeIterator(modelIndex);
      iter.initialize(atom, searchRadius, true);
      while (iter.hasMoreElements()) {
        Atom atomNear = (Atom) iter.nextElement();
        if (atomNear == atom || atomNear.isDeleted())
          continue;
        int atomIndexNear = atomNear.index;
        boolean isNearInSetA = (bsA == null || bsA.get(atomIndexNear));
        boolean isNearInSetB = (bsB == null || bsB.get(atomIndexNear));
        if (!isNearInSetA && !isNearInSetB 
            || bsExclude != null && bsExclude.get(atomIndexNear)
            && bsExclude.get(i) //this line forces BOTH to be excluded in order to ignore bonding
             )
          continue;
        if (!(isAtomInSetA && isNearInSetB || isAtomInSetB && isNearInSetA))
          continue;
        short order = getBondOrder(myBondingRadius, atomNear
            .getBondingRadiusFloat(), iter.foundDistance2(), minBondDistance2,
            bondTolerance);
        if (order > 0) {
          if (checkValencesAndBond(atom, atomNear, order, mad, bsBonds))
            nNew++;
        }
      }
      iter.release();
    }
    if (showRebondTimes && Logger.debugging)
      Logger.checkTimer("Time to autoBond");
    return nNew;
  }

  private int[] autoBond(BitSet bsA, BitSet bsB, BitSet bsBonds,
                         boolean isBonds, boolean matchHbond) {
    if (isBonds) {
      BitSet bs = bsA;
      bsA = new BitSet();
      bsB = new BitSet();
      for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
        bsA.set(bonds[i].atom1.index);
        bsB.set(bonds[i].atom2.index);
      }
    }
    return new int[] {
        matchHbond ? autoHbond(bsA, bsB) 
            : autoBond(bsA, bsB, null, bsBonds, viewer.getMadBond(), false), 0 };
  }
  
  private static float hbondMin = 2.5f;

  /**
   * a generalized formation of HBONDS, carried out in relation to calculate
   * HBONDS {atomsFrom} {atomsTo}. The calculation can create pseudo-H bonds for
   * files that do not contain H atoms.
   * 
   * @param bsA
   *          "from" set (must contain H if that is desired)
   * @param bsB
   *          "to" set
   * @return negative number of pseudo-hbonds or number of actual hbonds formed
   */
  public int autoHbond(BitSet bsA, BitSet bsB) {
    bsHBondsRasmol = new BitSet();
    boolean haveHAtoms = false;
    for (int i = bsA.nextSetBit(0); i >= 0 && !haveHAtoms; i = bsA
        .nextSetBit(i + 1))
      if (atoms[i].getElementNumber() == 1)
        haveHAtoms = true;
    boolean useRasMol = viewer.getHbondsRasmol();
    if (bsB == null || useRasMol && !haveHAtoms) {
      Logger.info((bsB == null ? "DSSP " : "RasMol") + " pseudo-hbond calculation");
      calcRasmolHydrogenBonds(bsA, bsB, null, false, Integer.MAX_VALUE, false);
      return -BitSetUtil.cardinalityOf(bsHBondsRasmol);
    }
    Logger.info(haveHAtoms ? "Standard Hbond calculation"
        : "Jmol pseudo-hbond calculation");
    BitSet bsCO = null;
    if (!haveHAtoms) {
      bsCO = new BitSet();
      for (int i = bsA.nextSetBit(0); i >= 0; i = bsA.nextSetBit(i + 1)) {
        int atomID = atoms[i].atomID;
        switch (atomID) {
        case JmolConstants.ATOMID_TERMINATING_OXT:
        case JmolConstants.ATOMID_CARBONYL_OXYGEN:
        case JmolConstants.ATOMID_CARBONYL_OD1:
        case JmolConstants.ATOMID_CARBONYL_OD2:
        case JmolConstants.ATOMID_CARBONYL_OE1:
        case JmolConstants.ATOMID_CARBONYL_OE2:
          bsCO.set(i);
          break;
        }
      }
    }
    float maxXYDistance = viewer.getHbondsDistanceMax();
    float minAttachedAngle = (float) (viewer.getHbondsAngleMin() * Math.PI / 180);
    float hbondMax2 = maxXYDistance * maxXYDistance;
    float hbondMin2 = hbondMin * hbondMin;
    float hxbondMin2 = 1;
    float hxbondMax2 = (maxXYDistance > hbondMin ? hbondMin2 : hbondMax2);
    float hxbondMax = (maxXYDistance > hbondMin ? hbondMin : maxXYDistance);
    int nNew = 0;
    float d2 = 0;
    Vector3f v1 = new Vector3f();
    Vector3f v2 = new Vector3f();
    if (showRebondTimes && Logger.debugging)
      Logger.startTimer();
    Point3f C = null;
    Point3f D = null;
    AtomIndexIterator iter = getSelectedAtomIterator(bsB, false, false, false);

    for (int i = bsA.nextSetBit(0); i >= 0; i = bsA.nextSetBit(i + 1)) {
      Atom atom = atoms[i];
      int elementNumber = atom.getElementNumber();
      boolean isH = (elementNumber == 1);
      if (!isH && (haveHAtoms || elementNumber != 7 && elementNumber != 8)
          || isH && !haveHAtoms)
        continue;
      float min2, max2, dmax;
      boolean firstIsCO;
      if (isH) {
        Bond[] b = atom.bonds;
        if (b == null)
          continue;
        boolean isOK = false;
        for (int j = 0; j < b.length && !isOK; j++) {
          Atom a2 = b[j].getOtherAtom(atom);
          int element = a2.getElementNumber();
          isOK = (element == 7 || element == 8);
        }
        if (!isOK)
          continue;
        dmax = hxbondMax;
        min2 = hxbondMin2;
        max2 = hxbondMax2;
        firstIsCO = false;
      } else {
        dmax = maxXYDistance;
        min2 = hbondMin2;
        max2 = hbondMax2;
        firstIsCO = bsCO.get(i);
      }
      setIteratorForAtom(iter, -1, atom.index, dmax);
      while (iter.hasNext()) {
        Atom atomNear = atoms[iter.next()];
        int elementNumberNear = atomNear.getElementNumber();
        if (atomNear == atom || !isH && elementNumberNear != 7
            && elementNumberNear != 8 || isH && elementNumberNear == 1
            || (d2 = iter.foundDistance2()) < min2 || d2 > max2 || firstIsCO
            && bsCO.get(atomNear.index) || atom.isBonded(atomNear)) {
          continue;
        }
        if (minAttachedAngle > 0) {
          v1.sub(atom, atomNear);
          if ((D = checkMinAttachedAngle(atom, minAttachedAngle, v1,
              v2, haveHAtoms)) == null)
            continue;
          v1.scale(-1);
          if ((C = checkMinAttachedAngle(atomNear, minAttachedAngle, v1,
              v2, haveHAtoms)) == null)
            continue;
        }
        float energy = 0;
        short bo;
        if (isH && !Float.isNaN(C.x) && !Float.isNaN(D.x)) {
          /*
           * A crude calculation based on simple distances. In the NH -- O=C
           * case this reads DH -- A=C
           * 
           * (+) H .......... A (-) | | | | (-) D C (+)
           * 
           * 
           * E = Q/rAH - Q/rAD + Q/rCD - Q/rCH
           */

          bo = JmolEdge.BOND_H_CALC;
          energy = HBond.getEnergy((float) Math.sqrt(d2), C.distance(atom), C
              .distance(D), atomNear.distance(D)) / 1000f;
        } else {
          bo = JmolEdge.BOND_H_REGULAR;
        }
        bsHBondsRasmol.set(addHBond(atom, atomNear, bo, energy));
        nNew++;
      }
    }
    iter.release();
    shapeManager.setShapeSize(JmolConstants.SHAPE_STICKS, Integer.MIN_VALUE, null,
        bsHBondsRasmol);
    if (showRebondTimes && Logger.debugging)
      Logger.checkTimer("Time to hbond");
    return (haveHAtoms ? nNew : -nNew);
  }

  private static Point3f checkMinAttachedAngle(Atom atom1,
                                               float minAngle, Vector3f v1,
                                               Vector3f v2, boolean haveHAtoms) {
    Bond[] bonds = atom1.bonds;
    if (bonds == null || bonds.length == 0)
      return new Point3f(Float.NaN, 0, 0);
    Atom X = null;
    float dMin = Float.MAX_VALUE;
    for (int i = bonds.length; --i >= 0;)
      if (bonds[i].isCovalent()) {
        Atom atomA = bonds[i].getOtherAtom(atom1);
        if (!haveHAtoms && atomA.getElementNumber() == 1)
          continue;
        v2.sub(atom1, atomA);
        float d = v2.angle(v1);
        if (d < minAngle)
          return null;
        if (d < dMin) {
          X = atomA;
          dMin = d;
        }
      }
    return X;
  }


  //////////// state definition ///////////

  boolean proteinStructureTainted = false;
  
  void setStructureIds() {
    int id;
    int idnew = 0;
    int lastid = -1;
    int imodel = -1;
    int lastmodel = -1;
    for (int i = 0; i < atomCount; i++) {
      if ((imodel = atoms[i].modelIndex) != lastmodel) {
        idnew = 0;
        lastmodel = imodel;
        lastid = -1;
      }
      if ((id = atoms[i].getStrucNo()) != lastid 
               && id != 0) {
        atoms[i].getGroup().setProteinStructureId(++idnew);
        lastid = idnew;
      }
    }
  }

  public String getProteinStructureState(BitSet bsAtoms, boolean taintedOnly,
                                         boolean needPhiPsi, int mode) {
    boolean showMode = (mode == 3);
    boolean pdbFileMode = (mode == 1);
    boolean scriptMode = (mode == 0);
    BitSet bs = null;
    StringBuffer cmd = new StringBuffer();
    StringBuffer sbTurn = new StringBuffer();
    StringBuffer sbHelix = new StringBuffer();
    StringBuffer sbSheet = new StringBuffer();
    int itype = 0;
    int isubtype = 0;
    int id = 0;
    int iLastAtom = 0;
    int iLastModel = -1;
    int lastId = -1;
    int res1 = 0;
    int res2 = 0;
    String sid = "";
    String group1 = "";
    String group2 = "";
    String chain1 = "";
    String chain2 = "";
    int n = 0;
    int nHelix = 0;
    int nTurn = 0;
    int nSheet = 0;
    BitSet bsTainted = null;
    if (taintedOnly) {
      if (!proteinStructureTainted)
        return "";
      bsTainted = new BitSet();
      for (int i = 0; i < atomCount; i++)
        if (models[atoms[i].modelIndex].isStructureTainted())
          bsTainted.set(i);
      bsTainted.set(atomCount);
    }
    for (int i = 0; i <= atomCount; i++)
      if (i == atomCount || bsAtoms == null || bsAtoms.get(i)) {
        if (taintedOnly && !bsTainted.get(i))
          continue;
        id = 0;
        if (i == atomCount || (id = atoms[i].getStrucNo()) != lastId) {
          if (bs != null) {
            if (itype == JmolConstants.PROTEIN_STRUCTURE_HELIX
                || itype == JmolConstants.PROTEIN_STRUCTURE_TURN
                || itype == JmolConstants.PROTEIN_STRUCTURE_SHEET) {
              n++;
              if (scriptMode) {
                int iModel = atoms[iLastAtom].modelIndex;
                String comment = "    \t# model="
                    + getModelNumberDotted(iModel);
                if (iLastModel != iModel) {
                  iLastModel = iModel;
                    cmd.append("  structure none ").append(
                        Escape.escape(getModelAtomBitSetIncludingDeleted(
                            iModel, false))).append(comment).append(";\n");
                }
                comment += " & (" + res1 + " - " + res2 + ")";
                String stype = JmolConstants.getProteinStructureName(isubtype,
                    false);
                  cmd.append("  structure ").append(stype).append(" ").append(
                      Escape.escape(bs)).append(comment).append(";\n");
              } else {
                String str;
                int nx;
                StringBuffer sb;
                // NNN III GGG C RRRR GGG C RRRR
                // HELIX 99 99 LYS F 281 LEU F 293 1
                // NNN III 2 GGG CRRRR GGG CRRRR
                // SHEET 1 A 8 ILE A 43 ASP A 45 0
                // NNN III GGG CRRRR GGG CRRRR
                // TURN 1 T1 PRO A 41 TYR A 44
                switch (itype) {
                case JmolConstants.PROTEIN_STRUCTURE_HELIX:
                  nx = ++nHelix;
                  if (sid == null || pdbFileMode)
                    sid = TextFormat.formatString("%3N %3N", "N", nx);
                  str = "HELIX  %ID %3GROUPA %1CA %4RESA  %3GROUPB %1CB %4RESB";
                  sb = sbHelix;
                  String type = null;
                  switch (isubtype) {
                  case JmolConstants.PROTEIN_STRUCTURE_HELIX:
                  case JmolConstants.PROTEIN_STRUCTURE_HELIX_ALPHA:
                    type = "  1";
                    break;
                  case JmolConstants.PROTEIN_STRUCTURE_HELIX_310:
                    type = "  5";
                    break;
                  case JmolConstants.PROTEIN_STRUCTURE_HELIX_PI:
                    type = "  3";
                    break;
                  }
                  if (type != null)
                    str += type;
                  break;
                case JmolConstants.PROTEIN_STRUCTURE_SHEET:
                  nx = ++nSheet;
                  if (sid == null || pdbFileMode) {
                    sid = TextFormat.formatString("%3N %3A 0", "N", nx);
                    sid = TextFormat.formatString(sid, "A", "S" + nx);
                  }
                  str = "SHEET  %ID %3GROUPA %1CA%4RESA  %3GROUPB %1CB%4RESB";
                  sb = sbSheet;
                  break;
                case JmolConstants.PROTEIN_STRUCTURE_TURN:
                default:
                  nx = ++nTurn;
                  if (sid == null || pdbFileMode)
                    sid = TextFormat.formatString("%3N %3N", "N", nx);
                  str = "TURN   %ID %3GROUPA %1CA%4RESA  %3GROUPB %1CB%4RESB";
                  sb = sbTurn;
                  break;
                }
                str = TextFormat.formatString(str, "ID", sid);
                str = TextFormat.formatString(str, "GROUPA", group1);
                str = TextFormat.formatString(str, "CA", chain1);
                str = TextFormat.formatString(str, "RESA", res1);
                str = TextFormat.formatString(str, "GROUPB", group2);
                str = TextFormat.formatString(str, "CB", chain2);
                str = TextFormat.formatString(str, "RESB", res2);
                sb.append(str);
                if (showMode)
                  sb.append(" strucno= ").append(lastId);
                sb.append("\n");

                /*
                 * HELIX 1 H1 ILE 7 PRO 19 1 3/10 CONFORMATION RES 17,19 1CRN 55
                 * HELIX 2 H2 GLU 23 THR 30 1 DISTORTED 3/10 AT RES 30 1CRN 56
                 * SHEET 1 S1 2 THR 1 CYS 4 0 1CRNA 4 SHEET 2 S1 2 CYS 32 ILE 35
                 */
              }
            }
            bs = null;
          }
          if (id == 0
              || bsAtoms != null
              && needPhiPsi
              && (Float.isNaN(atoms[i].getGroupParameter(Token.phi)) || Float
                  .isNaN(atoms[i].getGroupParameter(Token.psi))))
            continue;
        }
        char ch = atoms[i].getChainID();
        if (ch == 0)
          ch = ' ';
        if (bs == null) {
          bs = new BitSet();
          res1 = atoms[i].getResno();
          group1 = atoms[i].getGroup3(false);
          chain1 = "" + ch;
        }
        itype = atoms[i].getProteinStructureType();
        isubtype = atoms[i].getProteinStructureSubType();
        sid = atoms[i].getProteinStructureTag();
        bs.set(i);
        lastId = id;
        res2 = atoms[i].getResno();
        group2 = atoms[i].getGroup3(false);
        chain2 = "" + ch;
        iLastAtom = i;
      }
    if (n > 0)
      cmd.append("\n");
    return (scriptMode ? cmd.toString() : sbHelix.append(sbSheet).append(
        sbTurn).append(cmd).toString());
  }
  
  public String getModelInfoAsString() {
    StringBuffer sb = new StringBuffer("model count = ");
    sb.append(modelCount).append("\nmodelSetHasVibrationVectors:")
        .append(modelSetHasVibrationVectors());
    if (modelSetProperties == null) {
      sb.append("\nProperties: null");
    } else {
      Enumeration<?> e = modelSetProperties.propertyNames();
      sb.append("\nProperties:");
      while (e.hasMoreElements()) {
        String propertyName = (String)e.nextElement();
        sb.append("\n ").append(propertyName).append("=")
            .append(modelSetProperties.getProperty(propertyName));
      }
    }
    for (int i = 0; i < modelCount; ++i)
      sb.append("\n").append(i)
          .append(":").append(getModelNumberDotted(i))
          .append(":").append(getModelName(i))
          .append(":").append(getModelTitle(i))
          .append("\nmodelHasVibrationVectors:")
          .append(modelHasVibrationVectors(i));
    return sb.toString();
  }
  
  public String getSymmetryInfoAsString() {
    StringBuffer sb = new StringBuffer("Symmetry Information:");
    for (int i = 0; i < modelCount; ++i)
      sb.append("\nmodel #").append(getModelNumberDotted(i))
          .append("; name=").append(getModelName(i)).append("\n")
          .append(getSymmetryInfoAsString(i));
    return sb.toString();
  }

  public BitSet getAtomsConnected(float min, float max, int intType, BitSet bs) {
    BitSet bsResult = new BitSet();
    int[] nBonded = new int[atomCount];
    int i;
    boolean ishbond = (intType == JmolEdge.BOND_HYDROGEN_MASK);
    boolean isall = (intType == JmolEdge.BOND_ORDER_ANY);
    for (int ibond = 0; ibond < bondCount; ibond++) {
      Bond bond = bonds[ibond];
      if (isall || bond.is(intType) || ishbond && bond.isHydrogen()) {
        if (bs.get(bond.atom1.index)) {
          nBonded[i = bond.atom2.index]++;
          bsResult.set(i);
        }
        if (bs.get(bond.atom2.index)) {
          nBonded[i = bond.atom1.index]++;
          bsResult.set(i);
        }
      }
    }
    boolean nonbonded = (min == 0);
    for (i = atomCount; --i >= 0;) {
      int n = nBonded[i];
      if (n < min || n > max)
        bsResult.clear(i);
      else if (nonbonded && n == 0)
        bsResult.set(i);
    }
    return bsResult;
  }

  public String getModelExtract(BitSet bs, boolean doTransform,
                                boolean isModelKit, String type) {
    boolean asV3000 = (type.equalsIgnoreCase("V3000"));
    boolean asSDF = (type.equalsIgnoreCase("SDF"));
    boolean asXYZVIB = (type.equalsIgnoreCase("XYZVIB"));
    StringBuffer mol = new StringBuffer();
    if (!asXYZVIB) {
      mol.append(isModelKit ? "Jmol Model Kit" : viewer.getFullPathName()
          .replace('\\', '/'));
      String version = Viewer.getJmolVersion();
      Calendar c = Calendar.getInstance();
      mol.append("\n__Jmol-").append(version.substring(0, 2));
      TextFormat.rFill(mol, "_00", "" + (1 + c.get(Calendar.MONTH)));
      TextFormat.rFill(mol, "00", "" + c.get(Calendar.DAY_OF_MONTH));
      mol.append(("" + c.get(Calendar.YEAR)).substring(2, 4));
      TextFormat.rFill(mol, "00", "" + c.get(Calendar.HOUR_OF_DAY));
      TextFormat.rFill(mol, "00", "" + c.get(Calendar.MINUTE));
      mol.append("3D 1   1.00000     0.00000     0");
      //       This line has the format:
      //  IIPPPPPPPPMMDDYYHHmmddSSssssssssssEEEEEEEEEEEERRRRRR
      //  A2<--A8--><---A10-->A2I2<--F10.5-><---F12.5--><-I6->
      mol.append("\nJmol version ").append(Viewer.getJmolVersion()).append(
          " EXTRACT: ").append(Escape.escape(bs)).append("\n");
    }
    BitSet bsAtoms = BitSetUtil.copy(bs);
    for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1))
      if (doTransform && atoms[i].isDeleted())
        bsAtoms.clear(i);
    BitSet bsBonds = getCovalentBondsForAtoms(bsAtoms);
    if (!asXYZVIB && bsAtoms.cardinality() == 0)
      return "";
    boolean isOK = true;
    Quaternion q = (doTransform ? viewer.getRotationQuaternion() : null);
    if (asSDF) {
      String header = mol.toString();
      mol = new StringBuffer();
      BitSet bsModels = getModelBitSet(bsAtoms, true);
      for (int i = bsModels.nextSetBit(0); i >= 0; i = bsModels
          .nextSetBit(i + 1)) {
        mol.append(header);
        BitSet bsTemp = BitSetUtil.copy(bsAtoms);
        bsTemp.and(getModelAtomBitSetIncludingDeleted(i, false));
        bsBonds = getCovalentBondsForAtoms(bsTemp);
        if (!(isOK = addMolFile(mol, bsTemp, bsBonds, false, q)))
          break;
        mol.append("$$$$\n");
      }
    } else if (asXYZVIB) {
      LabelToken[] tokens1 = LabelToken.compile(viewer,
          "%-2e %10.5x %10.5y %10.5z %10.5vx %10.5vy %10.5vz\n", '\0', null);
      LabelToken[] tokens2 = LabelToken.compile(viewer,
          "%-2e %10.5x %10.5y %10.5z\n", '\0', null);
      BitSet bsModels = getModelBitSet(bsAtoms, true);
      for (int i = bsModels.nextSetBit(0); i >= 0; i = bsModels
          .nextSetBit(i + 1)) {
        BitSet bsTemp = BitSetUtil.copy(bsAtoms);
        bsTemp.and(getModelAtomBitSetIncludingDeleted(i, false));
        if (bsTemp.cardinality() == 0)
          continue;
        mol.append(bsTemp.cardinality()).append('\n');
        Properties props = models[i].properties;
        mol.append("Model[" + (i + 1) + "]: ");
        if (frameTitles[i] != null && frameTitles[i].length() > 0) {
          mol.append(frameTitles[i].replace('\n', ' '));
        } else if (props == null) {
          mol.append("Jmol " + Viewer.getJmolVersion());
        } else {
          StringBuffer sb = new StringBuffer();
          Enumeration<?> e = props.propertyNames();
          while (e.hasMoreElements()) {
            String propertyName = (String) e.nextElement();
            if (propertyName.equals(".PATH"))
              mol.append("PATH=").append(props.getProperty(propertyName));
            else
              sb.append(";").append(propertyName).append("=").append(
                  props.getProperty(propertyName));
          }
          mol.append(sb.toString().replace('\n', ' '));
        }
        mol.append('\n');
        for (int j = bsTemp.nextSetBit(0); j >= 0; j = bsTemp.nextSetBit(j + 1))
          mol.append(LabelToken.formatLabel(viewer, atoms[j],
              (getVibrationVector(j, false) == null ? tokens2 : tokens1), '\0',
              null));
      }
    } else {
      isOK = addMolFile(mol, bsAtoms, bsBonds, asV3000, q);
    }
    return (isOK ? mol.toString()
        : "ERROR: Too many atoms or bonds -- use V3000 format.");
  }
  
  private boolean addMolFile(StringBuffer mol, BitSet bsAtoms, BitSet bsBonds,
                          boolean asV3000, Quaternion q) {
    int nAtoms = bsAtoms.cardinality();
    int nBonds = bsBonds.cardinality();
    if (!asV3000 && (nAtoms > 999 || nBonds > 999))
      return false;
    int[] atomMap = new int[atomCount];
    Point3f pTemp = new Point3f();
    if (asV3000) {
      mol.append("  0  0  0  0  0  0            999 V3000");
    } else {
      TextFormat.rFill(mol, "   ", "" + nAtoms);
      TextFormat.rFill(mol, "   ", "" + nBonds);
      mol.append("  0  0  0  0              1 V2000");
    }
    mol.append("\n");
    if (asV3000) {
      mol.append("M  V30 BEGIN CTAB\nM  V30 COUNTS ").append(nAtoms)
          .append(" ").append(nBonds).append(" 0 0 0\n").append(
              "M  V30 BEGIN ATOM\n");
    }
    for (int i = bsAtoms.nextSetBit(0), n = 0; i >= 0; i = bsAtoms
        .nextSetBit(i + 1))
      getAtomRecordMOL(mol, atomMap[i] = ++n, atoms[i], q, pTemp,
          asV3000);
    if (asV3000) {
      mol.append("M  V30 END ATOM\nM  V30 BEGIN BOND\n");
    }
    for (int i = bsBonds.nextSetBit(0), n = 0; i >= 0; i = bsBonds
        .nextSetBit(i + 1))
      getBondRecordMOL(++n, mol, i, atomMap, asV3000);
    // 21 21 0 0 0
    if (asV3000) {
      mol.append("M  V30 END BOND\nM  V30 END CTAB\n");
    }
    mol.append("M  END\n");
    return true;
  }

  private BitSet getCovalentBondsForAtoms(BitSet bsAtoms) {
    BitSet bsBonds = new BitSet();
    for (int i = 0; i < bondCount; i++) {
      Bond bond = bonds[i];
      if (bsAtoms.get(bond.atom1.index) && bsAtoms.get(bond.atom2.index)
          && bond.isCovalent())
        bsBonds.set(i);
    }
    return bsBonds;
  }

  /*
  L-Alanine
  GSMACCS-II07189510252D 1 0.00366 0.00000 0
  Figure 1, J. Chem. Inf. Comput. Sci., Vol 32, No. 3., 1992
  0 0 0 0 0 999 V3000
  M  V30 BEGIN CTAB
  M  V30 COUNTS 6 5 0 0 1
  M  V30 BEGIN ATOM
  M  V30 1 C -0.6622 0.5342 0 0 CFG=2
  M  V30 2 C 0.6622 -0.3 0 0
  M  V30 3 C -0.7207 2.0817 0 0 MASS=13
  M  V30 4 N -1.8622 -0.3695 0 0 CHG=1
  M  V30 5 O 0.622 -1.8037 0 0
  M  V30 6 O 1.9464 0.4244 0 0 CHG=-1
  M  V30 END ATOM
  M  V30 BEGIN BOND
  M  V30 1 1 1 2
  M  V30 2 1 1 3 CFG=1
  M  V30 3 1 1 4
  M  V30 4 2 2 5
  M  V30 5 1 2 6
  M  V30 END BOND
  M  V30 END CTAB
  M  END
   */

  private void getAtomRecordMOL(StringBuffer sb, int i, Atom a, Quaternion q,
                                Point3f pTemp, boolean asV3000) {
    //   -0.9920    3.2030    9.1570 Cl  0  0  0  0  0
    //    3.4920    4.0920    5.8700 Cl  0  0  0  0  0
    //012345678901234567890123456789012
    if (models[a.modelIndex].isTrajectory)
      a.setFractionalCoord(ptTemp, trajectorySteps.get(a.modelIndex)[a.index
          - models[a.modelIndex].firstAtomIndex], true);
    else
      pTemp.set(a);
    if (q != null)
      q.transform(pTemp, pTemp);
    String sym = (a.isDeleted() ? "Xx" : Elements.elementSymbolFromNumber(a
        .getElementNumber()));
    int iso = a.getIsotopeNumber();
    int charge = a.getFormalCharge();
    if (asV3000) {
      sb.append("M  V30 ").append(i).append(" ").append(sym).append(" ")
          .append(pTemp.x).append(" ").append(pTemp.y).append(" ").append(
              pTemp.z).append(" 0");
      if (charge != 0)
        sb.append(" CHG=").append(charge);
      if (iso != 0)
        sb.append(" MASS=").append(iso);
    } else {
      sb.append(TextFormat
          .sprintf("%10.5p%10.5p%10.5p", new Object[] { pTemp }));
      sb.append(" ").append(sym);
      if (sym.length() == 1)
        sb.append(" ");
      if (iso > 0)
        iso -= Elements.getNaturalIsotope(a.getElementNumber());
      sb.append(" ");
      TextFormat.rFill(sb, "  ", "" + iso);
      TextFormat.rFill(sb, "   ", "" + (charge == 0 ? 0 : 4 - charge));
      sb.append("  0  0  0  0");
    }
    sb.append("\n");
  }

  private void getBondRecordMOL(int n, StringBuffer sb, int i,int[] atomMap, boolean asV3000){
  //  1  2  1  0
    Bond b = bonds[i];
    int a1 = atomMap[b.atom1.index];
    int a2 = atomMap[b.atom2.index];
    int order = b.getValence();
    if (order > 3)
      order = 1;
    switch (b.order & ~JmolEdge.BOND_NEW) {
    case JmolAdapter.ORDER_AROMATIC:
      order = 4;
      break;
    case JmolAdapter.ORDER_PARTIAL12:
      order = 5;
      break;
    case JmolAdapter.ORDER_AROMATIC_SINGLE:
      order = 6;
      break;
    case JmolAdapter.ORDER_AROMATIC_DOUBLE:
      order = 7;
      break;
    }
   if (asV3000) {
      sb.append("M  V30 ").append(n)
          .append(" ").append(order)
          .append(" ").append(a1)
          .append(" ").append(a2);
    } else {
      TextFormat.rFill(sb, "   ","" + a1);
      TextFormat.rFill(sb, "   ","" + a2);
      sb.append("  ").append(order).append("  0  0  0");
    }
    sb.append("\n");
  }
  
  @Override
  public String getChimeInfo(int tok, BitSet bs) {
    switch (tok) {
    case Token.info:
      break;
    case Token.basepair:
      return getBasePairInfo(bs);
    default:
      return super.getChimeInfo(tok, bs);
    }
    int n = 0;
    StringBuffer sb = new StringBuffer();
    int nHetero = 0;
    if (models[0].isPDB) {
      sb
          .append("\nMolecule name ....... "
              + getModelSetAuxiliaryInfo("COMPND"));
      sb.append("\nSecondary Structure . PDB Data Records");
      sb.append("\nBrookhaven Code ..... " + modelSetName);
      for (int i = modelCount; --i >= 0;)
        n += models[i].getChainCount(false);
      sb.append("\nNumber of Chains .... " + n);
      n = 0;
      for (int i = modelCount; --i >= 0;)
        n += models[i].getGroupCount(false);
      nHetero = 0;
      for (int i = modelCount; --i >= 0;)
        nHetero += models[i].getGroupCount(true);
      sb.append("\nNumber of Groups .... " + n);
      if (nHetero > 0)
        sb.append(" (" + nHetero + ")");
      for (int i = atomCount; --i >= 0;)
        if (atoms[i].isHetero())
          nHetero++;
    }
    sb.append("\nNumber of Atoms ..... " + (atomCount - nHetero));
    if (nHetero > 0)
      sb.append(" (" + nHetero + ")");
    sb.append("\nNumber of Bonds ..... " + bondCount);
    sb.append("\nNumber of Models ...... " + modelCount);
    if (models[0].isPDB) {
      int nH = 0;
      int nS = 0;
      int nT = 0;
      int id;
      int lastid = -1;
      for (int i = 0; i < atomCount; i++) {
        if (atoms[i].modelIndex != 0)
          break;
        if ((id = atoms[i].getStrucNo()) != lastid && id != 0) {
          lastid = id;
          switch (atoms[i].getProteinStructureType()) {
          case JmolConstants.PROTEIN_STRUCTURE_HELIX:
            nH++;
            break;
          case JmolConstants.PROTEIN_STRUCTURE_SHEET:
            nS++;
            break;
          case JmolConstants.PROTEIN_STRUCTURE_TURN:
            nT++;
            break;
          }
        }
      }
      sb.append("\nNumber of Helices ... " + nH);
      sb.append("\nNumber of Strands ... " + nS);
      sb.append("\nNumber of Turns ..... " + nT);
    }
    return sb.append('\n').toString().substring(1);
  }

  public String getModelFileInfo(BitSet frames) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < modelCount; ++i) {
      if (frames != null && !frames.get(i))
        continue;
      String file_model = getModelNumberDotted(i);
      sb.append("\n\nfile[\"").append(file_model)
          .append("\"] = ").append(Escape.escape(getModelFileName(i)))
          .append("\ntitle[\"").append(file_model)
          .append("\"] = ").append(Escape.escape(getModelTitle(i)))
          .append("\nname[\"").append(file_model)
          .append("\"] = ").append(Escape.escape(getModelName(i)));
    }
    return sb.toString();
  }
  
  public Map<String, Object> getAuxiliaryInfo(BitSet bsModels) {
    Map<String, Object> info = modelSetAuxiliaryInfo;
    if (info == null)
      return info;
    List<Map<String, Object>> models = new ArrayList<Map<String, Object>>();
    for (int i = 0; i < modelCount; ++i) {
      if (bsModels != null && !bsModels.get(i)) {
        continue;
      }
      Map<String, Object> modelinfo = getModelAuxiliaryInfo(i);
      models.add(modelinfo);
    }
    info.put("models",models);
    return info;
  }

  public List<Map<String, Object>> getAllAtomInfo(BitSet bs) {
    List<Map<String, Object>> V = new ArrayList<Map<String,Object>>();
    for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
      V.add(getAtomInfoLong(i));
    }
    return V;
  }

  public void getAtomIdentityInfo(int i, Map<String, Object> info) {
    info.put("_ipt", Integer.valueOf(i));
    info.put("atomIndex", Integer.valueOf(i));
    info.put("atomno", Integer.valueOf(getAtomNumber(i)));
    info.put("info", getAtomInfo(i, null));
    info.put("sym", getElementSymbol(i));
  }
  
  private Map<String, Object> getAtomInfoLong(int i) {
    Atom atom = atoms[i];
    Map<String, Object> info = new Hashtable<String, Object>();
    getAtomIdentityInfo(i, info);
    info.put("element", getElementName(i));
    info.put("elemno", Integer.valueOf(getElementNumber(i)));
    info.put("x", new Float(atoms[i].x));
    info.put("y", new Float(atoms[i].y));
    info.put("z", new Float(atoms[i].z));
    info.put("coord", new Point3f(atom));
    if (vibrationVectors != null && vibrationVectors[i] != null) {
      info.put("vibVector", new Vector3f(vibrationVectors[i]));
    }
    info.put("bondCount", Integer.valueOf(atom.getCovalentBondCount()));
    info.put("radius", Float.valueOf((float) (atom.getRasMolRadius() / 120.0)));
    info.put("model", atom.getModelNumberForLabel());
    info.put("visible", Boolean.valueOf(atoms[i].isVisible(0)));
    info.put("clickabilityFlags", Integer.valueOf(atom.clickabilityFlags));
    info.put("visibilityFlags", Integer.valueOf(atom.shapeVisibilityFlags));
    info.put("spacefill", new Float(atom.getRadius()));
    String strColor = Escape.escapeColor(viewer.getColorArgbOrGray(atom.colixAtom));
    if (strColor != null)
      info.put("color", strColor);
    info.put("colix", Integer.valueOf(atom.colixAtom));
    boolean isTranslucent = atom.isTranslucent();
    if (isTranslucent)
      info.put("translucent", Boolean.valueOf(isTranslucent));
    info.put("formalCharge", Integer.valueOf(atom.getFormalCharge()));
    info.put("partialCharge", new Float(atom.getPartialCharge()));
    float d = atom.getSurfaceDistance100() / 100f;
    if (d >= 0)
      info.put("surfaceDistance", new Float(d));
    if (models[atom.modelIndex].isPDB) {
      info.put("resname", atom.getGroup3(false));
      int seqNum = atom.getSeqNumber();
      char insCode = atom.getInsertionCode();
      if (seqNum > 0)
        info.put("resno", Integer.valueOf(seqNum));
      if (insCode != 0)
        info.put("insertionCode", "" + insCode);
      char chainID = atom.getChainID();
      info.put("name", getAtomName(i));
      info.put("chain", (chainID == '\0' ? "" : "" + chainID));
      info.put("atomID", Integer.valueOf(atom.atomID));
      info.put("groupID", Integer.valueOf(atom.getGroupID()));
      if (atom.alternateLocationID != '\0')
        info.put("altLocation", "" + atom.alternateLocationID);
      info.put("structure", Integer.valueOf(atom.getProteinStructureType()));
      info.put("polymerLength", Integer.valueOf(atom.getPolymerLength()));
      info.put("occupancy", Integer.valueOf(atom.getOccupancy100()));
      int temp = atom.getBfactor100();
      info.put("temp", Integer.valueOf(temp / 100));
    }
    return info;
  }  

  public List<Map<String, Object>> getAllBondInfo(BitSet bs) {
    List<Map<String, Object>> V = new ArrayList<Map<String,Object>>();
    int thisAtom = (bs.cardinality() == 1 ? bs.nextSetBit(0) : -1);
    for (int i = 0; i < bondCount; i++) {
      if (thisAtom >= 0? (bonds[i].atom1.index == thisAtom || bonds[i].atom2.index == thisAtom) 
          : bs.get(bonds[i].atom1.index) && bs.get(bonds[i].atom2.index)) {
        V.add(getBondInfo(i));
      }
    }
    return V;
  }

  private Map<String, Object> getBondInfo(int i) {
    Bond bond = bonds[i];
    Atom atom1 = bond.atom1;
    Atom atom2 = bond.atom2;
    Map<String, Object> info = new Hashtable<String, Object>();
    info.put("_bpt", Integer.valueOf(i));
    Map<String, Object> infoA = new Hashtable<String, Object>();
    getAtomIdentityInfo(atom1.index, infoA);
    Map<String, Object> infoB = new Hashtable<String, Object>();
    getAtomIdentityInfo(atom2.index, infoB);
    info.put("atom1",infoA);
    info.put("atom2",infoB);
    info.put("order", Float.valueOf(JmolConstants.getBondOrderNumberFromOrder(bonds[i].order)));
    info.put("radius", Float.valueOf((float) (bond.mad/2000.)));
    info.put("length_Ang", Float.valueOf(atom1.distance(atom2)));
    info.put("visible", Boolean.valueOf(bond.shapeVisibilityFlags != 0));
    String strColor = Escape.escapeColor(viewer.getColorArgbOrGray(bond.colix));
    if (strColor != null) 
      info.put("color", strColor);
    info.put("colix", Integer.valueOf(bond.colix));
    boolean isTranslucent = bond.isTranslucent();
    if (isTranslucent)
      info.put("translucent", Boolean.valueOf(isTranslucent));
   return info;
  }  
  
  public Map<String, List<Map<String, Object>>> getAllChainInfo(BitSet bs) {
    Map<String, List<Map<String, Object>>> finalInfo = new Hashtable<String, List<Map<String,Object>>>();
    List<Map<String, Object>> modelVector = new ArrayList<Map<String,Object>>();
    for (int i = 0; i < modelCount; ++i) {
      Map<String, Object> modelInfo = new Hashtable<String, Object>();
      List<Map<String, List<Map<String, Object>>>> info = getChainInfo(i, bs);
      if (info.size() > 0) {
        modelInfo.put("modelIndex", Integer.valueOf(i));
        modelInfo.put("chains", info);
        modelVector.add(modelInfo);
      }
    }
    finalInfo.put("models",modelVector);
    return finalInfo;
  }

  private List<Map<String, List<Map<String, Object>>>> getChainInfo(int modelIndex, BitSet bs) {
    Model model = models[modelIndex];
    int nChains = model.getChainCount(true);
    List<Map<String, List<Map<String, Object>>>> infoChains = new ArrayList<Map<String,List<Map<String,Object>>>>();
    for(int i = 0; i < nChains; i++) {
      Chain chain = model.getChain(i);
      List<Map<String, Object>> infoChain = new ArrayList<Map<String,Object>>();
      int nGroups = chain.getGroupCount();
      Map<String, List<Map<String, Object>>> arrayName = new Hashtable<String, List<Map<String,Object>>>();
      for (int igroup = 0; igroup < nGroups; igroup++) {
        Group group = chain.getGroup(igroup);
        if (!bs.get(group.firstAtomIndex)) 
          continue;
        Map<String, Object> infoGroup = new Hashtable<String, Object>();
        infoGroup.put("groupIndex", Integer.valueOf(igroup));
        infoGroup.put("groupID", Short.valueOf(group.getGroupID()));
        String s = group.getSeqcodeString();
        if (s != null)
          infoGroup.put("seqCode", s);
        infoGroup.put("_apt1", Integer.valueOf(group.firstAtomIndex));
        infoGroup.put("_apt2", Integer.valueOf(group.lastAtomIndex));
        infoGroup.put("atomInfo1", getAtomInfo(group.firstAtomIndex, null));
        infoGroup.put("atomInfo2", getAtomInfo(group.lastAtomIndex, null));
        infoGroup.put("visibilityFlags", Integer.valueOf(group.shapeVisibilityFlags));
        infoChain.add(infoGroup);
      }
      if (! infoChain.isEmpty()) { 
        arrayName.put("residues", infoChain);
        infoChains.add(arrayName);
      }
    }
    return infoChains;
  }  
  
  public Map<String, List<Map<String, Object>>> getAllPolymerInfo(BitSet bs) {
    Map<String, List<Map<String, Object>>> finalInfo = new Hashtable<String, List<Map<String,Object>>>();
    List<Map<String, Object>> modelVector = new ArrayList<Map<String,Object>>();
    for (int i = 0; i < modelCount; ++i) {
      Map<String, Object> modelInfo = new Hashtable<String, Object>();
      List<Map<String, Object>> info = new ArrayList<Map<String, Object>>();
      int polymerCount = models[i].getBioPolymerCount();
      for (int ip = 0; ip < polymerCount; ip++) {
        Map<String, Object> polyInfo = models[i].getBioPolymer(ip).getPolymerInfo(bs); 
        if (! polyInfo.isEmpty())
          info.add(polyInfo);
      }
      if (info.size() > 0) {
        modelInfo.put("modelIndex", Integer.valueOf(i));
        modelInfo.put("polymers", info);
        modelVector.add(modelInfo);
      }
    }
    finalInfo.put("models", modelVector);
    return finalInfo;
  }

  public String getUnitCellInfoText() {
    int modelIndex = viewer.getCurrentModelIndex();
    if (modelIndex < 0)
      return "no single current model";
    if (unitCells == null)
      return "not applicable";
    return unitCells[modelIndex].getUnitCellInfo();
  }

  private SymmetryInterface symTemp;

  @SuppressWarnings("unchecked")
  public Map<String, Object> getSpaceGroupInfo(int modelIndex, String spaceGroup, 
                                     int symOp, Point3f pt1, Point3f pt2, String drawID) {
    String strOperations = null;
    Map<String, Object> info = null;
    SymmetryInterface cellInfo = null;
    Object[][] infolist = null;
    if (spaceGroup == null) {
      if (modelIndex <= 0) {
        modelIndex = (pt1 instanceof Atom ? ((Atom) pt1).modelIndex 
            : viewer.getCurrentModelIndex());
      }
      if (modelIndex < 0) {
        strOperations = "no single current model";
      } else if (unitCells == null || unitCells[modelIndex] == null) {
        strOperations = "not applicable";
      }
      if (strOperations != null) {
        info = new Hashtable<String, Object>();
        info.put("spaceGroupInfo", strOperations);
        info.put("symmetryInfo", "");
        return info;
      }
      if (pt1 == null && drawID == null && symOp != 0) {
        info = (Map<String, Object>) getModelAuxiliaryInfo(modelIndex, "spaceGroupInfo");
      }
      if (info != null) {
        return info;
      }
      info = new Hashtable<String, Object>();
      if (pt1 == null && drawID == null && symOp == 0)
        setModelAuxiliaryInfo(modelIndex, "spaceGroupInfo", info);
      cellInfo = unitCells[modelIndex];
      spaceGroup = cellInfo.getSpaceGroupName();
      String[] list = unitCells[modelIndex].getSymmetryOperations();
      if (list == null) {
        strOperations = "\n no symmetry operations employed";
      } else {
        getSymTemp(true);
        symTemp.setSpaceGroup(false);
        strOperations = "\n" + list.length + " symmetry operations employed:";
        infolist = new Object[list.length][];
        for (int i = 0; i < list.length; i++) {
          int iSym = symTemp.addSpaceGroupOperation("=" + list[i], i + 1);
          if (iSym < 0)
            continue;
          infolist[i] = (symOp > 0 && symOp - 1 != iSym ? null 
              : symTemp.getSymmetryOperationDescription(iSym, cellInfo, pt1, pt2, drawID));
          if (infolist[i] != null)
            strOperations += "\n" + (i + 1) + "\t" + infolist[i][0] + "\t"
                + infolist[i][2];
        }
      }
    } else {
      info = new Hashtable<String, Object>();
    }
    info.put("spaceGroupName", spaceGroup);
    getSymTemp(true);
    String data = symTemp.getSpaceGroupInfo(spaceGroup, cellInfo);
    if (infolist != null) {
      info.put("operations", infolist);
      info.put("symmetryInfo", strOperations);
    }
    if (data == null)
      data = "could not identify space group from name: " + spaceGroup
          + "\nformat: show spacegroup \"2\" or \"P 2c\" "
          + "or \"C m m m\" or \"x, y, z;-x ,-y, -z\"";
    info.put("spaceGroupInfo", data);
    return info;
  }
  
  public Object getSymmetryInfo(BitSet bsAtoms, String xyz, int op, Point3f pt, 
                                Point3f pt2, String id, int type) {
    int iModel = -1;
    if (bsAtoms == null) {
      iModel = viewer.getCurrentModelIndex();
      if (iModel < 0)
        return "";
      bsAtoms = viewer.getModelUndeletedAtomsBitSet(iModel);
    }
    int iAtom = bsAtoms.nextSetBit(0);
    if (iAtom < 0)
      return "";
    iModel = atoms[iAtom].modelIndex;
    SymmetryInterface uc = getUnitCell(iModel);
    if (uc == null)
      return "";
    if (pt2 != null)
      return getSymmetryOperation(iModel, null, op, pt, pt2, 
          (id == null ? "sym" : id), type == Token.label);
    if (xyz == null) {
      String[] ops = uc.getSymmetryOperations();
      if (ops == null || op == 0 || Math.abs(op) > ops.length)
        return "";
      if (op > 0) {
        xyz = ops[op - 1 ];
      } else {
        xyz = ops[-1 - op];
      }
    } else {
      op = 0;
    }
    getSymTemp(false); 
    symTemp.setSpaceGroup(false);
    int iSym = symTemp.addSpaceGroupOperation((op < 0 ? "!" : "=") + xyz, Math.abs(op));
    if (iSym < 0)
      return "";
    symTemp.setUnitCell(uc.getNotionalUnitCell());
    Object[] info;
    pt = new Point3f(pt == null ? atoms[iAtom] : pt);
    if (type == Token.point) {
      uc.toFractional(pt, false);
      if (Float.isNaN(pt.x))
        return "";
      Point3f sympt = new Point3f();
      symTemp.newSpaceGroupPoint(iSym, pt, sympt, 0, 0, 0);
      symTemp.toCartesian(sympt, false);
      return sympt;
    }
    // null id means "array info only" but here we want the draw commands
    info = symTemp.getSymmetryOperationDescription(iSym, uc, pt, pt2,
        (id == null ? "sym" : id));
    int ang = ((Integer)info[9]).intValue();
    /*
     *  xyz (Jones-Faithful calculated from matrix)
     *  xyzOriginal (Provided by operation) 
     *  description ("C2 axis", for example) 
     *  translation vector (fractional)  
     *  translation vector (cartesian)
     *  inversion point 
     *  axis point 
     *  axis vector
     *  angle of rotation
     *  matrix representation
     */
    switch (type) {
    case Token.array:
      return info;
    case Token.list:
      String[] sinfo = new String[] {
          (String) info[0],
          (String) info[1],
          (String) info[2],
          // skipping DRAW commands here
          Escape.escape((Vector3f)info[4]),
          Escape.escape((Vector3f)info[5]),
          Escape.escape((Point3f)info[6]),
          Escape.escape((Point3f)info[7]),
          Escape.escape((Vector3f)info[8]),
          "" + info[9],
          "" + Escape.escape(info[10])
        };
        return sinfo;
    case Token.info:
      return info[0];
    default:
    case Token.label:
      return info[2];
    case Token.draw:
      return info[3];
    case Token.translation: 
      // skipping fractional translation
      return info[5]; // cartesian translation
    case Token.center:
      return info[6];
    case Token.point:
      return info[7];
    case Token.axis:
    case Token.plane:
      return ((ang == 0) == (type == Token.plane)? (Vector3f) info[8] : null);
    case Token.angle:
      return info[9];
    case Token.matrix4f:
      return info[10];
    } 
  }

  private void getSymTemp(boolean forceNew) {
    if (symTemp == null || forceNew)
      symTemp = (SymmetryInterface) Interface.getOptionInterface("symmetry.Symmetry");
  }

  protected void deleteModel(int modelIndex, int firstAtomIndex, int nAtoms,
                          BitSet bsAtoms, BitSet bsBonds) {
    /*
     *   ModelCollection.modelSetAuxiliaryInfo["group3Lists", "group3Counts, "models"]
     * ModelCollection.stateScripts ?????
     */
    if (modelIndex < 0) {
      //final deletions
      bspf = null;
      bsAll = null;
      molecules = null;
      moleculeCount = 0;
      isBbcageDefault = false;
      calcBoundBoxDimensions(null, 1);
      return;
    }
    
    modelNumbers = (int[]) ArrayUtil
        .deleteElements(modelNumbers, modelIndex, 1);
    modelFileNumbers = (int[]) ArrayUtil.deleteElements(modelFileNumbers,
        modelIndex, 1);
    modelNumbersForAtomLabel = (String[]) ArrayUtil.deleteElements(
        modelNumbersForAtomLabel, modelIndex, 1);
    modelNames = (String[]) ArrayUtil.deleteElements(modelNames, modelIndex, 1);
    frameTitles = (String[]) ArrayUtil.deleteElements(frameTitles, modelIndex,
        1);
    thisStateModel = -1;
    String[] group3Lists = (String[]) getModelSetAuxiliaryInfo("group3Lists");
    int[][] group3Counts = (int[][]) getModelSetAuxiliaryInfo("group3Counts");
    int ptm = modelIndex + 1;
    if (group3Lists != null && group3Lists[ptm] != null) {
      for (int i = group3Lists[ptm].length() / 6; --i >= 0;)
        if (group3Counts[ptm][i] > 0) {
          group3Counts[0][i] -= group3Counts[ptm][i];
          if (group3Counts[0][i] == 0)
            group3Lists[0] = group3Lists[0].substring(0, i * 6) + ",["
                + group3Lists[0].substring(i * 6 + 2);
        }
    }
    if (group3Lists != null) {
      modelSetAuxiliaryInfo.put("group3Lists", ArrayUtil.deleteElements(
          group3Lists, modelIndex, 1));
      modelSetAuxiliaryInfo.put("group3Counts", ArrayUtil.deleteElements(
          group3Counts, modelIndex, 1));
    }

    //fix cellInfos array
    if (unitCells != null) {
      for (int i = modelCount; --i > modelIndex;) {
        unitCells[i].setModelIndex(unitCells[i].getModelIndex() - 1);
      }
      unitCells = (SymmetryInterface[]) ArrayUtil.deleteElements(unitCells, modelIndex, 1);
    }

    // correct stateScripts, particularly CONNECT scripts
    for (int i = stateScripts.size(); --i >= 0;) {
      if (!stateScripts.get(i).deleteAtoms(modelIndex, bsBonds, bsAtoms)) {
        stateScripts.remove(i);
      }
    }
    
    // set to recreate bounding box
    deleteModelAtoms(firstAtomIndex, nAtoms, bsAtoms);
    viewer.deleteModelAtoms(firstAtomIndex, nAtoms, bsAtoms);
  }

  @SuppressWarnings("unchecked")
  public String getMoInfo(int modelIndex) {
    StringBuffer sb = new StringBuffer();
    for (int m = 0; m < modelCount; m++) {
      if (modelIndex >= 0 && m != modelIndex) {
        continue;
      }
      Map<String, Object> moData = (Map<String, Object>) viewer.getModelAuxiliaryInfo(m, "moData");
      if (moData == null) {
        continue;
      }
      List<Map<String, Object>> mos = (List<Map<String, Object>>) (moData.get("mos"));
      int nOrb = (mos == null ? 0 : mos.size());
      if (nOrb == 0) {
        continue;
      }
      for (int i = nOrb; --i >= 0; ) {
        Map<String, Object> mo = mos.get(i);
        String type = (String) mo.get("type");
        if (type == null) {
          type = "";
        }
        String units = (String) mo.get("energyUnits");
        if (units == null) {
          units = "";
        }
        Float occ = (Float) mo.get("occupancy");
        if (occ != null) {
          type = "occupancy " + occ.floatValue() + " " + type;
        }
        String sym = (String) mo.get("symmetry");
        if (sym != null) {
          type += sym;
        }
        String energy = "" + mo.get("energy");
        if (Float.isNaN(Parser.parseFloat(energy)))
          sb.append(TextFormat.sprintf(
              "model %-2s;  mo %-2i # %s\n", new Object[] {
                  getModelNumberDotted(m), Integer.valueOf(i + 1),
                  type }));
        else 
          sb.append(TextFormat.sprintf(
              "model %-2s;  mo %-2i # energy %-8.3f %s %s\n", new Object[] {
                  getModelNumberDotted(m), Integer.valueOf(i + 1),
                  mo.get("energy"), units, type }));
      }
    }
    return sb.toString();
  }

  public void assignAtom(int atomIndex, String type, boolean autoBond) {

    if (type == null)
      type = "C";

    // not as simple as just defining an atom.
    // if we click on an H, and C is being defined,
    // this sprouts an sp3-carbon at that position.

    Atom atom = atoms[atomIndex];
    BitSet bs = new BitSet();
    boolean wasH = (atom.getElementNumber() == 1);
    int atomicNumber = Elements.elementNumberFromSymbol(type, true);

    // 1) change the element type or charge

    boolean isDelete = false;
    if (atomicNumber > 0) {
      setElement(atom, atomicNumber);
      viewer.setShapeSize(JmolConstants.SHAPE_BALLS, 
          viewer.getDefaultRadiusData(), BitSetUtil.setBit(atomIndex));
      setAtomName(atomIndex, type + atom.getAtomNumber());
      if (!models[atom.modelIndex].isModelKit)
        taint(atomIndex, TAINT_ATOMNAME);
    } else if (type.equals("Pl")) {
      atom.setFormalCharge(atom.getFormalCharge() + 1);
    } else if (type.equals("Mi")) {
      atom.setFormalCharge(atom.getFormalCharge() - 1);
    } else if (type.equals("X")) {
      isDelete = true;
    } else if (!type.equals(".")) {
      return; // uninterpretable
    }

    // 2) delete noncovalent bonds and attached hydrogens for that atom.

    removeUnnecessaryBonds(atom, isDelete);

    // 3) adjust distance from previous atom.

    float dx = 0;
    if (atom.getCovalentBondCount() == 1)
      if (wasH) {
        dx = 1.50f;
      } else if (!wasH && atomicNumber == 1) {
        dx = 1.0f;
      }
    if (dx != 0) {
      Vector3f v = new Vector3f(atom);
      v.sub(atoms[atom.getBondedAtomIndex(0)]);
      float d = v.length();
      v.normalize();
      v.scale(dx - d);
      setAtomCoordRelative(atomIndex, v.x, v.y, v.z);
    }

    BitSet bsA = BitSetUtil.setBit(atomIndex);

    if (atomicNumber != 1 && autoBond) {

      // 4) clear out all atoms within 1.0 angstrom
      bspf = null;
      bs = getAtomsWithin(1.0f, bsA, false);
      bs.andNot(bsA);
      if (bs.nextSetBit(0) >= 0)
        viewer.deleteAtoms(bs, false);

      // 5) attach nearby non-hydrogen atoms (rings)

      bs = viewer.getModelUndeletedAtomsBitSet(atom.modelIndex);
      bs.andNot(getAtomBits(Token.hydrogen, null));
      makeConnections(0.1f, 1.8f, 1, JmolConstants.CONNECT_CREATE_ONLY, bsA,
          bs, null, false, 0);

      // 6) add hydrogen atoms

    }
    viewer.addHydrogens(bsA, false, true);
  }

  public void deleteAtoms(BitSet bs) {
    if (bs == null)
      return;
    BitSet bsBonds = new BitSet();
    for (int i = bs.nextSetBit(0); i >= 0 && i < atomCount ; i = bs.nextSetBit(i + 1))
      atoms[i].delete(bsBonds);
    BitSet bsTemp = new BitSet();
    for (int i = 0; i < modelCount; i++) {
      BitSetUtil.copy(bs, bsTemp);
      bsTemp.and(models[i].bsAtoms);
      models[i].bsDeleted.or(bsTemp);
    }
    deleteBonds(bsBonds, false);
  }

  public void appendLoadStates(StringBuffer commands) {
    for (int i = 0; i < modelCount; i++) { 
      if (isJmolDataFrame(i) || isTrajectorySubFrame(i))
        continue;
      int pt = commands.indexOf(models[i].loadState);
      if (pt < 0 || pt != commands.lastIndexOf(models[i].loadState))
        commands.append(models[i].loadState);
      if (models[i].isModelKit) {
        BitSet bs = getModelAtomBitSetIncludingDeleted(i, false);
        if (tainted != null) {
          if (tainted[TAINT_COORD] != null)
            tainted[TAINT_COORD].andNot(bs);
          if (tainted[TAINT_ELEMENT] != null)
            tainted[TAINT_ELEMENT].andNot(bs);
        }
        models[i].loadScript = new StringBuffer(); 
        Viewer.getInlineData(commands, getModelExtract(bs, false, true, "MOL"), i > 0);
      } else {
        commands.append(models[i].loadScript);
      }
    }
  }

  /*
   * <molecule title="acetic_acid.mol"
   * xmlns="http://www.xml-cml.org/schema/cml2/core"
   * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   * xsi:schemaLocation="http://www.xml-cml.org/schema/cml2/core cmlAll.xsd">
   * <atomArray> <atom id="a1" elementType="C" x3="0.1853" y3="0.0096"
   * z3="0.4587"/> <atom id="a2" elementType="O" x3="0.6324" y3="1.0432"
   * z3="0.8951"/> <atom id="a3" elementType="C" x3="-1.0665" y3="-0.1512"
   * z3="-0.3758"/> <atom id="a4" elementType="O" x3="0.7893" y3="-1.1734"
   * z3="0.6766" formalCharge="-1"/> <atom id="a5" elementType="H" x3="-1.7704"
   * y3="-0.8676" z3="0.1055"/> <atom id="a6" elementType="H" x3="-0.8068"
   * y3="-0.5215" z3="-1.3935"/> <atom id="a7" elementType="H" x3="-1.5889"
   * y3="0.8259" z3="-0.4854"/> </atomArray> <bondArray> <bond atomRefs2="a1 a2"
   * order="partial12"/> <bond atomRefs2="a1 a3" order="S"/> <bond
   * atomRefs2="a1 a4" order="partial12"/> <bond atomRefs2="a3 a5" order="S"/>
   * <bond atomRefs2="a3 a6" order="S"/> <bond atomRefs2="a3 a7" order="S"/>
   * </bondArray> </molecule>
   */
  public String getModelCml(BitSet bs, int atomsMax, boolean addBonds) {
    StringBuffer sb = new StringBuffer("");
    int nAtoms = BitSetUtil.cardinalityOf(bs);
    if (nAtoms == 0)
      return "";
    XmlUtil.openTag(sb, "molecule");
    XmlUtil.openTag(sb, "atomArray");
    BitSet bsAtoms = new BitSet();
    for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
      if (--atomsMax < 0)
        break;
      Atom atom = atoms[i];
      String name = atom.getAtomName();
      TextFormat.simpleReplace(name, "\"", "''");
      bsAtoms.set(atom.index);
      XmlUtil
          .appendTag(sb, "atom/", new String[] { 
              "id", "a" + (atom.index + 1),
              "title", atom.getAtomName(),
              "elementType", atom.getElementSymbol(), 
              "x3", "" + atom.x, 
              "y3", "" + atom.y, 
              "z3", "" + atom.z });
    }
    XmlUtil.closeTag(sb, "atomArray");
    if (addBonds) {
      XmlUtil.openTag(sb, "bondArray");
      for (int i = 0; i < bondCount; i++) {
        Bond bond = bonds[i];
        Atom a1 = bond.atom1;
        Atom a2 = bond.atom2;
        if (!bsAtoms.get(a1.index) || !bsAtoms.get(a2.index))
          continue;
        String order = JmolConstants.getCmlOrder(bond.order);
        if (order == null)
          continue;
        XmlUtil.appendTag(sb, "bond/", new String[] { 
            "atomRefs2", "a" + (bond.atom1.index + 1) + " a" + (bond.atom2.index + 1), 
            "order", order, });
      }
      XmlUtil.closeTag(sb, "bondArray");
    }
    XmlUtil.closeTag(sb, "molecule");
    return sb.toString();
  }

  // atom addition //
  
  protected void growAtomArrays(int newLength) {
    atoms = (Atom[]) ArrayUtil.setLength(atoms, newLength);
    if (vibrationVectors != null)
      vibrationVectors = (Vector3f[]) ArrayUtil.setLength(vibrationVectors,
          newLength);
    if (occupancies != null)
      occupancies = ArrayUtil.setLength(occupancies, newLength);
    if (bfactor100s != null)
      bfactor100s = ArrayUtil.setLength(bfactor100s, newLength);
    if (partialCharges != null)
      partialCharges = ArrayUtil.setLength(partialCharges, newLength);
    if (ellipsoids != null)
      ellipsoids = (Object[][]) ArrayUtil.setLength(ellipsoids, newLength);
    if (atomNames != null)
      atomNames = ArrayUtil.setLength(atomNames, newLength);
    if (atomTypes != null)
      atomTypes = ArrayUtil.setLength(atomTypes, newLength);
    if (atomSerials != null)
      atomSerials = ArrayUtil.setLength(atomSerials, newLength);
  }

  protected Atom addAtom(int modelIndex, Group group, short atomicAndIsotopeNumber,
                       String atomName, int atomSerial, int atomSite, float x, float y, float z) {
    return addAtom(modelIndex, group, atomicAndIsotopeNumber, atomName, atomSerial,
        atomSite, x, y, z, Float.NaN, Float.NaN, Float.NaN, Float.NaN, 0, 0,
        100, Float.NaN, null, false, '\0', (byte) 0, null);
  }
  protected Atom addAtom(int modelIndex, Group group,
                         short atomicAndIsotopeNumber, String atomName, int atomSerial,
                         int atomSite, float x, float y, float z,
                         float radius, float vectorX, float vectorY,
                         float vectorZ, int formalCharge, float partialCharge,
                         int occupancy, float bfactor, Object[] ellipsoid,
                         boolean isHetero, char alternateLocationID,
                         byte specialAtomID, BitSet atomSymmetry) {
    Atom atom = new Atom(modelIndex, atomCount, x, y, z, radius, atomSymmetry,
        atomSite, atomicAndIsotopeNumber, formalCharge, isHetero,
        alternateLocationID);
    models[modelIndex].atomCount++;
    models[modelIndex].bsAtoms.set(atomCount);
    if (atomicAndIsotopeNumber % 128 == 1)
      models[modelIndex].hydrogenCount++;
    atoms[atomCount] = atom;
    setBFactor(atomCount, bfactor);
    setOccupancy(atomCount, occupancy);
    setPartialCharge(atomCount, partialCharge);
    if (ellipsoid != null)
      setEllipsoid(atomCount, ellipsoid);
    atom.group = group;
    atom.colixAtom = viewer
        .getColixAtomPalette(atom, JmolConstants.PALETTE_CPK);
    if (atomName != null) {
      int i;
      if ((i = atomName.indexOf('\0')) >= 0) {
        if (atomTypes == null)
          atomTypes = new String[atoms.length];
        atomTypes[atomCount] = atomName.substring(i + 1);
        atomName = atomName.substring(0, i);
      }
      atom.atomID = specialAtomID;
      if (specialAtomID == 0) {
        if (atomNames == null)
          atomNames = new String[atoms.length];
        atomNames[atomCount] = atomName.intern();
      }
    }
    if (atomSerial != Integer.MIN_VALUE) {
      if (atomSerials == null)
        atomSerials = new int[atoms.length];
      atomSerials[atomCount] = atomSerial;
    }
    if (!Float.isNaN(vectorX))
      setVibrationVector(atomCount, vectorX, vectorY, vectorZ);
    atomCount++;
    return atom;
  }

  public String getInlineData(int modelIndex) {
    StringBuffer data = null;
    if (modelIndex >= 0)
      data = models[modelIndex].loadScript;
    else
      for (modelIndex = modelCount; --modelIndex >= 0; )
        if ((data = models[modelIndex].loadScript).length() > 0)
          break;
    int pt = data.lastIndexOf("data \"");
    if (pt < 0)
      return null;
    pt = data.indexOf("\"", pt + 7);
    int pt2 = data.lastIndexOf("end \"");
    if (pt2 < pt || pt < 0)
      return null;
    return data.substring(pt + 2, pt2);
  }

  public boolean isAtomPDB(int i) {
    return i >= 0 && models[atoms[i].modelIndex].isPDB;
  }

  public boolean isAtomAssignable(int i) {
    return i >= 0 && atoms[i].modelIndex == modelCount - 1;
  }

  public int getGroupAtom(Atom atom, int offset, String name) {
    Group g = atom.group;
    int monomerIndex = g.getMonomerIndex();
    if (monomerIndex < 0)
      return -1;
    Group[] groups = models[atom.modelIndex].getBioPolymer(
        g.getBioPolymerIndexInModel()).getGroups();
    int ipt = monomerIndex + offset;
    if (ipt >= 0 && ipt < groups.length) {
      Group m = groups[ipt];
      if (offset == 1 && !m.isConnectedPrevious())
        return -1;
      if ("0".equals(name))
        return m.leadAtomIndex;
      int max = m.lastAtomIndex;
      for (int i = m.firstAtomIndex; i <= max; i++)
        if (name == null || name.equalsIgnoreCase(atoms[i].getAtomName()))
          return i;
    }
    return -1;
  }

  public boolean haveModelKit() {
    for (int i = 0; i < modelCount; i++)
      if (models[i].isModelKit)
        return true;
    return false;
  }

  public BitSet getModelKitStateBitset(BitSet bs, BitSet bsDeleted) {
    // task here is to remove bits from bs that are deleted atoms in 
    // models that are model kits.

    BitSet bs1 = BitSetUtil.copy(bsDeleted);
    for (int i = 0; i < modelCount; i++)
      if (!models[i].isModelKit)
        bs1.andNot(models[i].bsAtoms);
    return BitSetUtil.deleteBits(bs, bs1);
  }
  
  public void setAtomNamesAndNumbers(int iFirst, int baseAtomIndex, AtomCollection mergeSet) {
    // first, validate that all atomSerials are NaN
    if (baseAtomIndex < 0)
      iFirst = models[atoms[iFirst].modelIndex].firstAtomIndex;
    if (atomSerials == null)
      atomSerials = new int[atomCount];
    if (atomNames == null)
      atomNames = new String[atomCount];
    // now, we'll assign 1-based atom numbers within each model
    boolean isZeroBased = isXYZ && viewer.getZeroBasedXyzRasmol();
    int lastModelIndex = Integer.MAX_VALUE;
    int atomNo = 1;
    for (int i = iFirst; i < atomCount; ++i) {
      Atom atom = atoms[i];
      if (atom.modelIndex != lastModelIndex) {
        lastModelIndex = atom.modelIndex;
        atomNo = (isZeroBased ? 0 : 1);
      }
      // 1) do not change numbers assigned by adapter
      // 2) do not change the number already assigned when merging
      // 3) restart numbering with new atoms, not a continuation of old
      if (atomSerials[i] == 0 || baseAtomIndex < 0)
        atomSerials[i] = (i < baseAtomIndex ? mergeSet.atomSerials[i]
            : atomNo);
      if (atomNames[i] == null || baseAtomIndex < 0)
        atomNames[i] = (atom.getElementSymbol() + atomSerials[i]).intern();
      if (!models[lastModelIndex].isModelKit || atom.getElementNumber() > 0 && !atom.isDeleted())
        atomNo++;
    }
  }  
}
