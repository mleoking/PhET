/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2011-03-07 18:47:51 -0800 (Mon, 07 Mar 2011) $
 * $Revision: 15247 $
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

package org.jmol.adapter.smarter;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.BitSet;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;

import org.jmol.api.Interface;
import org.jmol.api.JmolAdapter;
import org.jmol.api.SymmetryInterface;
import org.jmol.api.VolumeDataInterface;
import org.jmol.util.ArrayUtil;
import org.jmol.util.BitSetUtil;
import org.jmol.util.Logger;
import org.jmol.util.Parser;
import org.jmol.util.TextFormat;
import org.jmol.viewer.JmolConstants;

@SuppressWarnings("unchecked")
public class AtomSetCollection {

  private String fileTypeName;
  public String getFileTypeName() {
    return fileTypeName;
  }
  
  private String collectionName;
  public String getCollectionName() {
    return collectionName;
  }

  public void setCollectionName(String collectionName) {
    if (collectionName != null) {
      collectionName = collectionName.trim();
      if (collectionName.length() == 0)
        return;
      this.collectionName = collectionName;
    }
  }

  private Map<String, Object> atomSetCollectionAuxiliaryInfo = new Hashtable<String, Object>();
  public Map<String, Object> getAtomSetCollectionAuxiliaryInfo() {
    return atomSetCollectionAuxiliaryInfo;
  }
  
  private final static String[] globalBooleans = {"someModelsHaveFractionalCoordinates",
    "someModelsHaveSymmetry", "someModelsHaveUnitcells", "someModelsHaveCONECT", "isPDB"};

  public final static int GLOBAL_FRACTCOORD = 0;
  public final static int GLOBAL_SYMMETRY = 1;
  public final static int GLOBAL_UNITCELLS = 2;
  private final static int GLOBAL_CONECT = 3;
  private final static int GLOBAL_ISPDB = 4;

  public void setIsPDB() {
    setGlobalBoolean(GLOBAL_ISPDB);
  }

  public void clearGlobalBoolean(int globalIndex) {
    atomSetCollectionAuxiliaryInfo.remove(globalBooleans[globalIndex]);
  }
  
  private void setGlobalBoolean(int globalIndex) {
    setAtomSetCollectionAuxiliaryInfo(globalBooleans[globalIndex], Boolean.TRUE);
  }
  
  final public static String[] notionalUnitcellTags =
  { "a", "b", "c", "alpha", "beta", "gamma" };

  private int atomCount;
  public int getAtomCount() {
    return atomCount;
  }
  
  private Atom[] atoms = new Atom[256];
  public Atom[] getAtoms() {
    return atoms;
  }
  
  public Atom getAtom(int i) {
    return atoms[i];
  }
  
  private int bondCount;
  public int getBondCount() {
    return bondCount;
  }
  
  private Bond[] bonds = new Bond[256];
  public Bond[] getBonds() {
    return bonds;
  }
  
  public Bond getBond(int i) {
    return bonds[i];
  }
  
  private int structureCount;
  public int getStructureCount() {
    return structureCount;
  }
  
  private Structure[] structures = new Structure[16];
  public Structure[] getStructures() {
    return structures;
  }
  
  private int atomSetCount;
  public int getAtomSetCount() {
    return atomSetCount;
  }
  
  private int currentAtomSetIndex = -1;
  public int getCurrentAtomSetIndex() {
    return currentAtomSetIndex;
  }
  public void setCurrentAtomSetIndex(int i) {
    currentAtomSetIndex = i;
  }

  private int[] atomSetNumbers = new int[16];
  private int[] atomSetAtomCounts = new int[16];
  private int[] atomSetBondCounts = new int[16];
  private Map<String, Object>[] atomSetAuxiliaryInfo = new Hashtable[16];
  private int[] latticeCells;

  public String errorMessage;

  public boolean coordinatesAreFractional;
  private boolean isTrajectory;    
  private int trajectoryStepCount = 0;
  private Point3f[] trajectoryStep;
  private List<Point3f[]> trajectorySteps;
  boolean doFixPeriodic;
  public void setDoFixPeriodic() {
    doFixPeriodic = true;
  }

  float[] notionalUnitCell = new float[6]; 
  // expands to 22 for cartesianToFractional matrix as array (PDB)

  private boolean allowMultiple;
  
  public AtomSetCollection(String fileTypeName,
      AtomSetCollectionReader atomSetCollectionReader) {
    this.fileTypeName = fileTypeName;
    allowMultiple = (atomSetCollectionReader == null || atomSetCollectionReader.desiredVibrationNumber < 0);
    // set the default PATH properties as defined in the SmarterJmolAdapter
    Properties p = new Properties();
    p.put("PATH_KEY", SmarterJmolAdapter.PATH_KEY);
    p.put("PATH_SEPARATOR", SmarterJmolAdapter.PATH_SEPARATOR);
    setAtomSetCollectionAuxiliaryInfo("properties", p);
  }

  
  /**
   * Creates an AtomSetCollection based on an array of AtomSetCollection
   * 
   * @param array Array of AtomSetCollection
   */
  
  public AtomSetCollection(AtomSetCollection[] array) {
    this("Array", null);
    setAtomSetCollectionAuxiliaryInfo("isMultiFile", Boolean.TRUE);
    for (int i = 0; i < array.length; i++) {
      appendAtomSetCollection(i, array[i]);
    }
  }

  /**
   * Creates an AtomSetCollection based on a Vector of 
   *   AtomSetCollection or Vector (from zipped zip files)
   * 
   * @param list Vector of AtomSetCollection
   */
  
  public AtomSetCollection(List<?> list) {
    this("Array", null);
    setAtomSetCollectionAuxiliaryInfo("isMultiFile", Boolean.TRUE);
    appendAtomSetCollection(list);
  }

  private void appendAtomSetCollection(List<?> list) {
    int n = list.size();
    if (n == 0) {
      errorMessage = "No file found!";
      return;
    }
      
    for (int i = 0; i < n; i++) {
      Object o = list.get(i);
      if (o instanceof List)
        appendAtomSetCollection((List<?>) o);
      else
        appendAtomSetCollection(i, (AtomSetCollection) o);
    }  
  }
  
  public void setTrajectory() {
    if (!isTrajectory) {
      trajectorySteps = new ArrayList<Point3f[]>();
    }
    isTrajectory = true;
    addTrajectoryStep();
  }
  
  /**
   * Appends an AtomSetCollection
   * 
   * @param collectionIndex collection index for new model number
   * @param collection AtomSetCollection to append
   */
  protected void appendAtomSetCollection(int collectionIndex,
                                         AtomSetCollection collection) {
    // Initialisations
    int existingAtomsCount = atomCount;
    // auxiliary info
    // Clone each AtomSet
    int clonedAtoms = 0;
    setAtomSetCollectionAuxiliaryInfo("loadState", collection.getAtomSetCollectionAuxiliaryInfo("loadState"));
    for (int atomSetNum = 0; atomSetNum < collection.atomSetCount; atomSetNum++) {
      newAtomSet();
      // must fix referencing for someModelsHaveCONECT business
      Map info = atomSetAuxiliaryInfo[currentAtomSetIndex] = collection.atomSetAuxiliaryInfo[atomSetNum];
      int[] atomInfo = (int[]) info.get("PDB_CONECT_firstAtom_count_max");
      if (atomInfo != null)
        atomInfo[0] += existingAtomsCount;
      setAtomSetAuxiliaryInfo("title", collection.collectionName);
      setAtomSetName(collection.getAtomSetName(atomSetNum));
      for (int atomNum = 0; atomNum < collection.atomSetAtomCounts[atomSetNum]; atomNum++) {
        try {
          newCloneAtom(collection.atoms[clonedAtoms]);
        } catch (Exception e) {
          errorMessage = "appendAtomCollection error: " + e;
        }
        clonedAtoms++;
      }

      //Structures: We must incorporate any global structures (modelIndex == -1) into this model
      //explicitly. Whew! This is because some cif _data structures have multiple PDB models (1skt)
      for (int i = 0; i < collection.structureCount; i++)
        if (collection.structures[i].modelIndex == atomSetNum
            || collection.structures[i].modelIndex == -1)
          addStructure(collection.structures[i]);

      // numbers
      atomSetNumbers[currentAtomSetIndex] = ((collectionIndex + 1) * 1000000)
          + collection.atomSetNumbers[atomSetNum];

      // Note -- this number is used for Model.modelNumber. It is a combination of
      // file number * 1000000 + PDB MODEL NUMBER, which could be anything.
      // Adding the file number here indicates that we have multiple files.
      // But this will all be adjusted in ModelLoader.finalizeModels(). BH 11/2007
      
    }
    // Clone bonds
    for (int bondNum = 0; bondNum < collection.bondCount; bondNum++) {
      Bond bond = collection.bonds[bondNum];
      addNewBond(bond.atomIndex1 + existingAtomsCount, bond.atomIndex2
          + existingAtomsCount, bond.order);
    }
    // Set globals
    for (int i = globalBooleans.length; --i >= 0;)
      if (Boolean.TRUE.equals(
          collection.getAtomSetCollectionAuxiliaryInfo(globalBooleans[i])))
        setGlobalBoolean(i);
    
  }

  void setNoAutoBond() {
      setAtomSetCollectionAuxiliaryInfo("noAutoBond", Boolean.TRUE);
  }
  
  void freeze() {
    //Logger.debug("AtomSetCollection.freeze; atomCount = " + atomCount);
    if (isTrajectory)
      finalizeTrajectory();
    getList(true);
    getList(false);
    for (int i = 0; i < atomSetCount; i++) {
      setAtomSetAuxiliaryInfo("initialAtomCount", Integer
          .valueOf(atomSetAtomCounts[i]), i);
      setAtomSetAuxiliaryInfo("initialBondCount", Integer
          .valueOf(atomSetBondCounts[i]), i);
    }
  }

  private void getList(boolean isAltLoc) {
    int i;
    for (i = atomCount; --i >= 0;)
      if (atoms[i] != null && (isAltLoc ? atoms[i].alternateLocationID : atoms[i].insertionCode) != '\0')
        break;
    if (i < 0)
      return;
    String[] lists = new String[atomSetCount];
    for (i = 0; i < atomSetCount; i++)
      lists[i] = "";
    int pt;
    for (i = 0; i < atomCount; i++) {
      if (atoms[i] == null)
        continue;
      char id = (isAltLoc ? atoms[i].alternateLocationID
          : atoms[i].insertionCode);
      if (id != '\0' && lists[pt = atoms[i].atomSetIndex].indexOf(id) < 0)
        lists[pt] += id;
    }
    String type = (isAltLoc ? "altLocs" : "insertionCodes");
    for (i = 0; i < atomSetCount; i++)
      if (lists[i].length() > 0)
        setAtomSetAuxiliaryInfo(type, lists[i], i);
  }

  void finish() {
    atoms = null;
    atomSetAtomCounts = new int[16];
    atomSetAuxiliaryInfo = new Hashtable[16];
    atomSetCollectionAuxiliaryInfo = new Hashtable<String, Object>();
    atomSetCount = 0;
    atomSetNumbers = new int[16];
    atomSymbolicMap = new Hashtable<Object, Integer>();
    bonds = null;
    cartesians = null;
    connectLast = null;
    currentAtomSetIndex = -1;
    latticeCells = null;
    notionalUnitCell = null;
    symmetry = null;
    structures = new Structure[16];
    structureCount = 0;
    trajectoryStep = null;
    trajectorySteps = null;
    vConnect = null;
    vd = null;
  }

  public void discardPreviousAtoms() {
    for (int i = atomCount; --i >= 0; )
      atoms[i] = null;
    atomCount = 0;
    clearSymbolicMap();
    atomSetCount = 0;
    currentAtomSetIndex = -1;
    for (int i = atomSetAuxiliaryInfo.length; --i >= 0; ) {
      atomSetAtomCounts[i] = 0;
      atomSetBondCounts[i] = 0;
      atomSetAuxiliaryInfo[i] = null;
    }
  }

  public void removeAtomSet() {
    if (currentAtomSetIndex < 0)
      return;
    currentAtomSetIndex--;
    atomSetCount--;
  }
  
  Atom newCloneAtom(Atom atom) throws Exception {
    //Logger.debug("newCloneAtom()");
    Atom clone = atom.cloneAtom();
    addAtom(clone);
    return clone;
  }

  // FIX ME This should really also clone the other things pertaining
  // to an atomSet, like the bonds (which probably should be remade...)
  // but also the atomSetProperties and atomSetName...
  public void cloneFirstAtomSet() throws Exception {
    if (!allowMultiple)
      return;
    newAtomSet();
    for (int i = 0, firstCount = atomSetAtomCounts[0]; i < firstCount; ++i)
      newCloneAtom(atoms[i]);
  }

  public void cloneFirstAtomSetWithBonds(int nBonds) throws Exception {
    if (!allowMultiple)
      return;
    cloneFirstAtomSet();
    int firstCount = atomSetAtomCounts[0];
    for (int bondNum = 0; bondNum < nBonds; bondNum++) {
      Bond bond = bonds[bondCount - nBonds];
      addNewBond(bond.atomIndex1 + firstCount, bond.atomIndex2 + firstCount,
          bond.order);
    }
  }

  public void cloneLastAtomSet() throws Exception {
    cloneLastAtomSet(-1);
  }
  
  public void cloneLastAtomSet(int atomCount) throws Exception {
    if (!allowMultiple)
      return;
    int count = (atomCount > 0 ? atomCount : getLastAtomSetAtomCount());
    int atomIndex = getLastAtomSetAtomIndex();
    newAtomSet();
    for ( ; --count >= 0; ++atomIndex)
      newCloneAtom(atoms[atomIndex]);
  }
  
  public int getFirstAtomSetAtomCount() {
    return atomSetAtomCounts[0];
  }

  public int getLastAtomSetAtomCount() {
    return atomSetAtomCounts[currentAtomSetIndex];
  }

  public int getLastAtomSetAtomIndex() {
    //Logger.debug("atomSetCount=" + atomSetCount);
    return atomCount - atomSetAtomCounts[currentAtomSetIndex];
  }

  public Atom addNewAtom() {
    Atom atom = new Atom();
    addAtom(atom);
    return atom;
  }

  public void addAtom(Atom atom) {
    if (atomCount == atoms.length) {
      if (atomCount > 200000)
        atoms = (Atom[])ArrayUtil.ensureLength(atoms, atomCount + 50000);
      else
        atoms = (Atom[])ArrayUtil.doubleLength(atoms);
    }
    atom.atomIndex = atomCount;
    atoms[atomCount++] = atom;
    if (atomSetCount == 0)
      newAtomSet();
    atom.atomSetIndex = currentAtomSetIndex;
    atom.atomSite = atomSetAtomCounts[currentAtomSetIndex]++;
  }

  public void addAtomWithMappedName(Atom atom) {
    addAtom(atom);
    mapMostRecentAtomName();
  }

  public void addAtomWithMappedSerialNumber(Atom atom) {
    addAtom(atom);
    mapMostRecentAtomSerialNumber();
  }

  public Bond addNewBond(int atomIndex1, int atomIndex2) {
    return addNewBond(atomIndex1, atomIndex2, 1);
  }

  Bond addNewBond(String atomName1, String atomName2) {
    return addNewBond(atomName1, atomName2, 1);
  }

  public Bond addNewBond(int atomIndex1, int atomIndex2, int order) {
    if (atomIndex1 < 0 || atomIndex1 >= atomCount ||
        atomIndex2 < 0 || atomIndex2 >= atomCount)
      return null;
    Bond bond = new Bond(atomIndex1, atomIndex2, order);
    addBond(bond);
    return bond;
  }
  
  public Bond addNewBond(String atomName1, String atomName2, int order) {
    return addNewBond(getAtomNameIndex(atomName1),
                      getAtomNameIndex(atomName2),
                      order);
  }

  public Bond addNewBondWithMappedSerialNumbers(int atomSerial1, int atomSerial2,
                                         int order) {
    return addNewBond(getAtomSerialNumberIndex(atomSerial1),
                      getAtomSerialNumberIndex(atomSerial2),
                      order);
  }


  List<int[]> vConnect;
  int connectNextAtomIndex = 0;
  int connectNextAtomSet = 0;
  int[] connectLast;
  
  public void addConnection(int[] is) {
    if (vConnect == null) {
      connectLast = null;
      vConnect = new ArrayList<int[]>();
    }
    if (connectLast != null) {
      if (is[0] == connectLast[0] 
          && is[1] == connectLast[1] 
          && is[2] != JmolAdapter.ORDER_HBOND) {
        connectLast[2]++;
        return;
      }
    }
    vConnect.add(connectLast = is);
  }

  public void connectAll(int maxSerial) {
    if (vConnect == null) {
      return;
    }
    int firstAtom = connectNextAtomIndex;
    for (int i = connectNextAtomSet; i < atomSetCount; i++) {
      setGlobalBoolean(GLOBAL_CONECT);
      setAtomSetAuxiliaryInfo("PDB_CONECT_firstAtom_count_max", new int[] {firstAtom, atomSetAtomCounts[i], maxSerial}, i);
      setAtomSetAuxiliaryInfo("PDB_CONECT_bonds", vConnect, i);
      firstAtom += atomSetAtomCounts[i];
    }
    vConnect = null;
    connectNextAtomSet = currentAtomSetIndex + 1;
    connectNextAtomIndex = firstAtom;
  }

  public void addBond(Bond bond) {
    if (trajectoryStepCount > 0)
      return;
    if (bond.atomIndex1 < 0 ||
        bond.atomIndex2 < 0 ||
        bond.order < 0 ||
        //do not allow bonds between models
        atoms[bond.atomIndex1].atomSetIndex != atoms[bond.atomIndex2].atomSetIndex) {
      if (Logger.debugging) {
        Logger.debug(
            ">>>>>>BAD BOND:" + bond.atomIndex1 + "-" +
            bond.atomIndex2 + " order=" + bond.order);
      }
      return;
    }
    if (bondCount == bonds.length)
      bonds = (Bond[])ArrayUtil.setLength(bonds, bondCount + 1024);
    bonds[bondCount++] = bond;
    atomSetBondCounts[currentAtomSetIndex]++;
  }

  public void addStructure(Structure structure) {
    if (structureCount == structures.length)
      structures = (Structure[])ArrayUtil.setLength(structures,
                                                      structureCount + 32);
    structure.modelIndex = currentAtomSetIndex;
    structures[structureCount++] = structure;
    if (structure.strandCount >= 1) {
      int i = structureCount;
      for (i = structureCount; --i >= 0 
        && structures[i].modelIndex == currentAtomSetIndex
        && structures[i].structureID.equals(structure.structureID); ) {
      }
      i++;
      int n = structureCount - i;
      for (; i < structureCount; i++) 
        structures[i].strandCount = n;
    }
  }

  public void addVibrationVector(int iatom, float vx, float vy, float vz,
                                 boolean withSymmetry) {
    if (!withSymmetry) {
      addVibrationVector(iatom, vx, vy, vz);
      return;
    }
    int atomSite = atoms[iatom].atomSite;
    int atomSetIndex = atoms[iatom].atomSetIndex;
    for (int i = iatom; i < atomCount && atoms[i].atomSetIndex == atomSetIndex; i++) {
      if (atoms[i].atomSite == atomSite)
        addVibrationVector(i, vx, vy, vz);
    }
  }

  public void addVibrationVector(int iatom, float x, float y, float z) {
    if (!allowMultiple)
      iatom = iatom % atomCount;
    Atom atom = atoms[iatom];
    atom.vectorX = x;
    atom.vectorY = y;
    atom.vectorZ = z;
  }

  void setAtomSetSpaceGroupName(String spaceGroupName) {
    setAtomSetAuxiliaryInfo("spaceGroup", spaceGroupName+"");
  }
    
  public void setCoordinatesAreFractional(boolean coordinatesAreFractional) {
    this.coordinatesAreFractional = coordinatesAreFractional;
    setAtomSetAuxiliaryInfo(
        "coordinatesAreFractional",
        Boolean.valueOf(coordinatesAreFractional));
    if (coordinatesAreFractional)
      setGlobalBoolean(GLOBAL_FRACTCOORD);    
  }
  
  float symmetryRange;
  void setSymmetryRange(float factor) {
    symmetryRange = factor;
    setAtomSetCollectionAuxiliaryInfo("symmetryRange", new Float(factor));
  }
  
  void setLatticeCells(int[] latticeCells, boolean applySymmetryToBonds, 
                       boolean doPackUnitCell, String supercell) {
    //set when unit cell is determined
    // x <= 555 and y >= 555 indicate a range of cells to load
    // AROUND the central cell 555 and that
    // we should normalize (z = 1) or pack unit cells (z = -1) or not (z = 0)
    // in addition (Jmol 11.7.36) z = -2 does a full 3x3x3 around the designated cells
    // but then only delivers the atoms that are within the designated cells. 
    // Normalization is the moving of the center of mass into the unit cell.
    // Starting with Jmol 12.0.RC23 we do not normalize a CIF file that 
    // is being loaded without {i j k} indicated.
    
    this.latticeCells = latticeCells;
    boolean isLatticeRange = (latticeCells[0] <= 555  && latticeCells[1] >= 555
        && (latticeCells[2] == 0 || latticeCells[2] == 1 || latticeCells[2] == -1));
    doNormalize = latticeCells[0] != 0 && (!isLatticeRange || latticeCells[2] == 1);
    this.applySymmetryToBonds = applySymmetryToBonds;
    this.doPackUnitCell = doPackUnitCell;
    if (supercell != null)
      setSuperCell(supercell);
  }
  
  private float[] scTemp;
  private void setSuperCell(String supercell) {
    if (scTemp != null)
      return;
    scTemp = new float[16];
    if (getSymmetry().getMatrixFromString(supercell, scTemp, true) == null) {
      scTemp = null;
      return;
    }
    Logger.info("Using supercell \n" + new Matrix4f(scTemp));
  }
 
  SymmetryInterface symmetry;
  public SymmetryInterface getSymmetry() {
    if (symmetry == null)
      symmetry = (SymmetryInterface) Interface.getOptionInterface("symmetry.Symmetry");
    return symmetry;
  }
  
  boolean haveUnitCell = false;
  
  public void setNotionalUnitCell(float[] info, Matrix3f matUnitCellOrientation, Point3f unitCellOffset) {
    notionalUnitCell = new float[info.length];
    this.unitCellOffset = unitCellOffset;
    for (int i = 0; i < info.length; i++)
      notionalUnitCell[i] = info[i];
    haveUnitCell = true;
    setAtomSetAuxiliaryInfo("notionalUnitcell", notionalUnitCell);
    setGlobalBoolean(GLOBAL_UNITCELLS);
    getSymmetry().setUnitCell(notionalUnitCell);
    // we need to set the auxiliary info as well, because 
    // ModelLoader creates a new symmetry object.
    if (unitCellOffset != null){
      symmetry.setUnitCellOffset(unitCellOffset);
      setAtomSetAuxiliaryInfo("unitCellOffset", unitCellOffset);
    }
    if (matUnitCellOrientation != null) {
      symmetry.setUnitCellOrientation(matUnitCellOrientation);
      setAtomSetAuxiliaryInfo("matUnitCellOrientation", matUnitCellOrientation);
    }
  }

  boolean addSpaceGroupOperation(String xyz) {
    getSymmetry().setSpaceGroup(doNormalize);
    return (symmetry.addSpaceGroupOperation(xyz, 0) >= 0);
  }
  
  public void setLatticeParameter(int latt) {
    getSymmetry().setSpaceGroup(doNormalize);
    symmetry.setLattice(latt);
  }
  
  void applySymmetry() throws Exception {
    //parameters are counts of unit cells as [a b c]
    applySymmetry(latticeCells[0], latticeCells[1], Math.abs(latticeCells[2]));
  }

  void applySymmetry(SymmetryInterface symmetry) throws Exception {
    getSymmetry().setSpaceGroup(symmetry);
    //parameters are counts of unit cells as [a b c]
    applySymmetry(latticeCells[0], latticeCells[1], Math.abs(latticeCells[2]));
  }

  boolean doNormalize = true;
  boolean doPackUnitCell = false;
   
  private void applySymmetry(int maxX, int maxY, int maxZ) throws Exception {
    if (!coordinatesAreFractional || !getSymmetry().haveSpaceGroup())
      return;
    if (scTemp != null) {

      // supercell:

      // 1) get all atoms for cells necessary

      rminx = Float.MAX_VALUE;
      rminy = Float.MAX_VALUE;
      rminz = Float.MAX_VALUE;
      rmaxx = -Float.MAX_VALUE;
      rmaxy = -Float.MAX_VALUE;
      rmaxz = -Float.MAX_VALUE;

      Point3f ptx = setSym(0, 1, 2);
      Point3f pty = setSym(4, 5, 6);
      Point3f ptz = setSym(8, 9, 10);

      minXYZ = new Point3i((int) rminx, (int) rminy, (int) rminz);
      maxXYZ = new Point3i((int) rmaxx, (int) rmaxy, (int) rmaxz);
      applyAllSymmetry();

      // 2) set all atom coordinates to Cartesians

      int iAtomFirst = getLastAtomSetAtomIndex();
      for (int i = iAtomFirst; i < atomCount; i++)
        symmetry.toCartesian(atoms[i], true);

      // 3) create the supercell unit cell

      symmetry = null;
      setNotionalUnitCell(new float[] { 0, 0, 0, 0, 0, 0, ptx.x, ptx.y, ptx.z,
          pty.x, pty.y, pty.z, ptz.x, ptz.y, ptz.z }, null,
          (Point3f) getAtomSetAuxiliaryInfo(currentAtomSetIndex,
              "unitCellOffset"));
      setAtomSetSpaceGroupName("P1");
      getSymmetry().setSpaceGroup(doNormalize);
      symmetry.addSpaceGroupOperation("x,y,z", 0);

      // 4) reset atoms to fractional values

      for (int i = iAtomFirst; i < atomCount; i++)
        symmetry.toFractional(atoms[i], true);

      // 5) apply the full lattice symmetry now
      
      needEllipsoids = false;
      
      // ?? TODO
      atomSetAuxiliaryInfo[currentAtomSetIndex].remove("matUnitCellOrientation");

    }

    minXYZ = new Point3i();
    maxXYZ = new Point3i(maxX, maxY, maxZ);
    applyAllSymmetry();
    scTemp = null;
  }

  private Point3f setSym(int i, int j, int k) {
    Point3f pt = new Point3f();
    pt.set(scTemp[i], scTemp[j], scTemp[k]);
    setSymmetryMinMax(pt);
    symmetry.toCartesian(pt, false);
    return pt;
  }

  private float rminx, rminy, rminz, rmaxx, rmaxy, rmaxz;
   
  private void setSymmetryMinMax(Point3f c) {
    if (rminx > c.x)
      rminx = c.x;
    if (rminy > c.y)
      rminy = c.y;
    if (rminz > c.z)
      rminz = c.z;
    if (rmaxx < c.x)
      rmaxx = c.x;
    if (rmaxy < c.y)
      rmaxy = c.y;
    if (rmaxz < c.z)
      rmaxz = c.z;
  }
   
  private boolean isInSymmetryRange(Point3f c) {
    return (c.x >= rminx && c.y >= rminy && c.z >= rminz 
        && c.x <= rmaxx && c.y <= rmaxy && c.z <= rmaxz);
  }

  private final Point3f ptOffset = new Point3f();
  
  private Point3f unitCellOffset;
  
  private Point3i minXYZ, maxXYZ;
  
  private static boolean isWithinCell(Point3f pt, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
    float slop = 0.02f;
    return (pt.x > minX - slop && pt.x < maxX + slop 
        && pt.y > minY - slop && pt.y < maxY + slop 
        && pt.z > minZ - slop && pt.z < maxZ + slop);
  }

  private boolean needEllipsoids;
  public void setAnisoBorU(Atom atom, float[] data, int type) {
    needEllipsoids = true;
    atom.anisoBorU = data;
    data[6] = type;
  }
  
  public float[] getAnisoBorU(Atom atom) {
    return atom.anisoBorU;
  }
  
  private void applyAllSymmetry() throws Exception {
    int noSymmetryCount = getLastAtomSetAtomCount();
    int iAtomFirst = getLastAtomSetAtomIndex();
    if (needEllipsoids)
      for (int i = iAtomFirst; i < atomCount; i++)
        atoms[i].ellipsoid = symmetry.getEllipsoid(atoms[i].anisoBorU);

    bondCount0 = bondCount;

    symmetry
        .setFinalOperations(atoms, iAtomFirst, noSymmetryCount, doNormalize);
    int operationCount = symmetry.getSpaceGroupOperationCount();
    getSymmetry().setMinMaxLatticeParameters(minXYZ, maxXYZ);
    if (doPackUnitCell || symmetryRange != 0 && maxXYZ.x - minXYZ.x == 1
        && maxXYZ.y - minXYZ.y == 1 && maxXYZ.z - minXYZ.z == 1)
      switch ((int) getSymmetry()
          .getUnitCellInfo(JmolConstants.INFO_DIMENSIONS)) {
      case 3:
        // standard
        minXYZ.z--;
        maxXYZ.z++;
        // fall through;
      case 2:
        // slab or standard
        minXYZ.y--;
        maxXYZ.y++;
        // fall through;
      case 1:
        // slab, polymer, or standard
        minXYZ.x--;
        maxXYZ.x++;
      }
    int nCells = (maxXYZ.x - minXYZ.x) * (maxXYZ.y - minXYZ.y)
        * (maxXYZ.z - minXYZ.z);
    int cartesianCount = (checkSpecial ? noSymmetryCount * operationCount
        * nCells : symmetryRange > 0 ? noSymmetryCount * operationCount // checking
    // against
        // {1 1
        // 1}
        : symmetryRange < 0 ? 1 // checking against symop=1555 set; just a box
            : 1 // not checking
    );
    cartesians = new Point3f[cartesianCount];
    for (int i = 0; i < noSymmetryCount; i++)
      atoms[i + iAtomFirst].bsSymmetry = new BitSet(operationCount
          * (nCells + 1));
    int pt = 0;
    int[] unitCells = new int[nCells];
    int iCell = 0;
    int cell555Count = 0;
    float absRange = Math.abs(symmetryRange);
    boolean checkSymmetryRange = (symmetryRange != 0);
    boolean checkRangeNoSymmetry = (symmetryRange < 0);
    boolean checkRange111 = (symmetryRange > 0);
    if (checkSymmetryRange) {
      rminx = Float.MAX_VALUE;
      rminy = Float.MAX_VALUE;
      rminz = Float.MAX_VALUE;
      rmaxx = -Float.MAX_VALUE;
      rmaxy = -Float.MAX_VALUE;
      rmaxz = -Float.MAX_VALUE;
    }
    // always do the 555 cell first
    Matrix4f op = symmetry.getSpaceGroupOperation(0);
    if (doPackUnitCell)
      ptOffset.set(0, 0, 0);
    for (int tx = minXYZ.x; tx < maxXYZ.x; tx++)
      for (int ty = minXYZ.y; ty < maxXYZ.y; ty++)
        for (int tz = minXYZ.z; tz < maxXYZ.z; tz++) {
          unitCells[iCell++] = 555 + tx * 100 + ty * 10 + tz;
          if (tx != 0 || ty != 0 || tz != 0 || cartesians.length == 0)
            continue;
          for (pt = 0; pt < noSymmetryCount; pt++) {
            Atom atom = atoms[iAtomFirst + pt];
            Point3f c = new Point3f(atom);
            op.transform(c);
            symmetry.toCartesian(c, false);
            if (doPackUnitCell) {
              symmetry.toUnitCell(c, ptOffset);
              atom.set(c);
              symmetry.toFractional(atom, false);
            }
            atom.bsSymmetry.set(iCell * operationCount);
            atom.bsSymmetry.set(0);
            if (checkSymmetryRange)
              setSymmetryMinMax(c);
            if (pt < cartesianCount)
              cartesians[pt] = c;
          }
          if (checkRangeNoSymmetry) {
            rminx -= absRange;
            rminy -= absRange;
            rminz -= absRange;
            rmaxx += absRange;
            rmaxy += absRange;
            rmaxz += absRange;
          }
          cell555Count = pt = symmetryAddAtoms(iAtomFirst, noSymmetryCount, 0,
              0, 0, 0, pt, iCell * operationCount);
        }
    if (checkRange111) {
      rminx -= absRange;
      rminy -= absRange;
      rminz -= absRange;
      rmaxx += absRange;
      rmaxy += absRange;
      rmaxz += absRange;
    }
    
    if (scTemp != null) {
      
    }
    // now apply all the translations
    
    iCell = 0;
    for (int tx = minXYZ.x; tx < maxXYZ.x; tx++)
      for (int ty = minXYZ.y; ty < maxXYZ.y; ty++)
        for (int tz = minXYZ.z; tz < maxXYZ.z; tz++) {
          iCell++;
          if (tx != 0 || ty != 0 || tz != 0)
            pt = symmetryAddAtoms(iAtomFirst, noSymmetryCount, tx, ty, tz,
                cell555Count, pt, iCell * operationCount);
        }
    if (iCell * noSymmetryCount == atomCount - iAtomFirst)
      appendAtomProperties(iCell);
    setSymmetryOps();
    setAtomSetAuxiliaryInfo("presymmetryAtomIndex", Integer.valueOf(iAtomFirst));
    setAtomSetAuxiliaryInfo("presymmetryAtomCount", Integer
        .valueOf(noSymmetryCount));
    setAtomSetAuxiliaryInfo("latticeDesignation", symmetry
        .getLatticeDesignation());
    setAtomSetAuxiliaryInfo("unitCellRange", unitCells);
    symmetry.setSpaceGroup(null);
    notionalUnitCell = new float[6];
    coordinatesAreFractional = false;
    // turn off global fractional conversion -- this will be model by model
    setAtomSetAuxiliaryInfo("hasSymmetry", Boolean.TRUE);
    setGlobalBoolean(GLOBAL_SYMMETRY);
  }

  
  private void setSymmetryOps() {
    int operationCount = symmetry.getSpaceGroupOperationCount();
    if (operationCount > 0) {
      String[] symmetryList = new String[operationCount];
      for (int i = 0; i < operationCount; i++)
        symmetryList[i] = "" + symmetry.getSpaceGroupXyz(i, doNormalize);
      setAtomSetAuxiliaryInfo("symmetryOperations", symmetryList);
    }
    setAtomSetAuxiliaryInfo("symmetryCount", Integer.valueOf(operationCount));
  }

  Point3f[] cartesians;
  int bondCount0;
  int bondIndex0;
  boolean applySymmetryToBonds = false;
  boolean checkSpecial = true;

  public void setCheckSpecial(boolean TF) {
    checkSpecial = TF;
  }
  
  private final Point3f ptTemp = new Point3f();
  private final Point3f ptTemp1 = new Point3f();
  private final Point3f ptTemp2 = new Point3f();
  
  private int symmetryAddAtoms(int iAtomFirst, int noSymmetryCount, int transX,
                               int transY, int transZ, int baseCount, int pt,
                               int iCellOpPt) throws Exception {
    boolean isBaseCell = (baseCount == 0);
    boolean addBonds = (bondCount0 > bondIndex0 && applySymmetryToBonds);
    int[] atomMap = (addBonds ? new int[noSymmetryCount] : null);
    if (doPackUnitCell)
      ptOffset.set(transX, transY, transZ);

    //symmetryRange < 0 : just check symop=1 set
    //symmetryRange > 0 : check against {1 1 1}

    // if we are not checking special atoms, then this is a PDB file
    // and we return all atoms within a cubical volume around the 
    // target set. The user can later use select within() to narrow that down
    // This saves immensely on time.

    float range2 = symmetryRange * symmetryRange;
    boolean checkRangeNoSymmetry = (symmetryRange < 0);
    boolean checkRange111 = (symmetryRange > 0);
    boolean checkSymmetryMinMax = (isBaseCell && checkRange111);
    checkRange111 &= !isBaseCell;
    boolean checkSymmetryRange = (checkRangeNoSymmetry || checkRange111);
    boolean checkDistances = (checkSpecial || checkSymmetryRange);
    boolean addCartesian = (checkSpecial || checkSymmetryMinMax);
    if (checkRangeNoSymmetry)
      baseCount = noSymmetryCount;
    int nOperations = symmetry.getSpaceGroupOperationCount();
    int atomMax = iAtomFirst + noSymmetryCount;
    Point3f ptAtom = new Point3f();
    for (int iSym = 0; iSym < nOperations; iSym++) {
      if (isBaseCell && symmetry.getSpaceGroupXyz(iSym, true).equals("x,y,z"))
        continue;

      /* pt0 sets the range of points cross-checked. 
       * If we are checking special positions, then we have to check
       *   all previous atoms. 
       * If we are doing a symmetry range check relative to {1 1 1}, then
       *   we have to check only the base set. (checkRange111 true)
       * If we are doing a symmetry range check on symop=1555 (checkRangeNoSymmetry true), 
       *   then we don't check any atoms and just use the box.
       *    
       */

      int pt0 = (checkSpecial ? pt : checkRange111 ? baseCount : 0);
      for (int i = iAtomFirst; i < atomMax; i++) {
        if (atoms[i].ignoreSymmetry)
          continue;
        symmetry.newSpaceGroupPoint(iSym, atoms[i], ptAtom, transX, transY, transZ);
        Atom special = null;
        Point3f cartesian = new Point3f(ptAtom);
        symmetry.toCartesian(cartesian, false);
        if (doPackUnitCell) {
          symmetry.toUnitCell(cartesian, ptOffset);
          ptAtom.set(cartesian);
          symmetry.toFractional(ptAtom, false);
          if (!isWithinCell(ptAtom, minXYZ.x + 1, maxXYZ.x - 1, minXYZ.y + 1, maxXYZ.y - 1, minXYZ.z + 1, maxXYZ.z - 1))
            continue;
        }
        if (checkSymmetryMinMax)
          setSymmetryMinMax(cartesian);
        if (checkDistances) {

          /* checkSpecial indicates that we are looking for atoms with (nearly) the
           * same cartesian position.  
           */
          float minDist2 = Float.MAX_VALUE;
          if (checkSymmetryRange && !isInSymmetryRange(cartesian))
            continue;
          for (int j = pt0; --j >= 0;) {
            float d2 = cartesian.distanceSquared(cartesians[j]);
            if (checkSpecial && d2 < 0.0001) {
              special = atoms[iAtomFirst + j];
              break;
            }
            if (checkRange111 && j < baseCount && d2 < minDist2)
              minDist2 = d2;
          }
          if (checkRange111 && minDist2 > range2)
            continue;
        }
        int atomSite = atoms[i].atomSite;
        if (special != null) {
          if (addBonds)
            atomMap[atomSite] = special.atomIndex;
          special.bsSymmetry.set(iCellOpPt + iSym);
          special.bsSymmetry.set(iSym);
        } else {
          if (addBonds)
            atomMap[atomSite] = atomCount;
          Atom atom1 = newCloneAtom(atoms[i]);
          atom1.set(ptAtom);
          atom1.atomSite = atomSite;
          atom1.bsSymmetry = BitSetUtil.setBit(iCellOpPt + iSym);
          atom1.bsSymmetry.set(iSym);
          if (addCartesian)
            cartesians[pt++] = cartesian;
          if (atoms[i].ellipsoid != null) {
            Object axes = atoms[i].ellipsoid[0];
            Object lengths = atoms[i].ellipsoid[1];
            if (axes != null) {
              // note -- PDB reader specifically turns off cartesians
              if (addCartesian) {
                ptTemp.set(cartesians[i - iAtomFirst]);
              } else {
                ptTemp.set(atoms[i]);
                symmetry.toCartesian(ptTemp, false);
              }
              axes = symmetry.rotateEllipsoid(iSym, ptTemp, (Vector3f[]) axes, ptTemp1, ptTemp2);
            }
            atom1.ellipsoid = new Object[] { axes, lengths };
          }
        }
      }
      if (addBonds) {
        // Clone bonds
        for (int bondNum = bondIndex0; bondNum < bondCount0; bondNum++) {
          Bond bond = bonds[bondNum];
          Atom atom1 = atoms[bond.atomIndex1];
          Atom atom2 = atoms[bond.atomIndex2];
          if (atom1 == null || atom2 == null)
            continue;
          int iAtom1 = atomMap[atom1.atomSite];
          int iAtom2 = atomMap[atom2.atomSite];
          if (iAtom1 >= atomMax || iAtom2 >= atomMax)
            addNewBond(iAtom1, iAtom2, bond.order);
        }
      }
    }
    return pt;
  }
  
  public void applySymmetry(List<Matrix4f> biomts, float[] notionalUnitCell, boolean applySymmetryToBonds, String filter) {
    if (latticeCells != null && latticeCells[0] != 0) {
      Logger.error("Cannot apply biomolecule when lattice cells are indicated");
      return;
    }
    doNormalize = false;
    symmetry = null;
    getSymmetry();
    setNotionalUnitCell(notionalUnitCell, null, unitCellOffset);
    getSymmetry().setSpaceGroup(doNormalize);
    addSpaceGroupOperation("x,y,z");
    setAtomSetSpaceGroupName("biomolecule");
    int len = biomts.size();
    this.applySymmetryToBonds = applySymmetryToBonds;
    bondCount0 = bondCount;
    boolean addBonds = (bondCount0 > bondIndex0 && applySymmetryToBonds);
    int[] atomMap = (addBonds ? new int[atomCount] : null);
    int iAtomFirst = getLastAtomSetAtomIndex();
    int atomMax = atomCount;
    if (filter.indexOf("#<") >= 0) {
      len = Math.min(len, Parser.parseInt(filter.substring(filter.indexOf("#<") + 2)) - 1);
      filter = TextFormat.simpleReplace(filter, "#<", "_<");
    }
    for (int iAtom = iAtomFirst; iAtom < atomMax; iAtom++)
      atoms[iAtom].bsSymmetry = BitSetUtil.setBit(0);
    for (int i = 1; i < len; i++) { 
      if (filter.indexOf("!#") >= 0) {
        if (filter.indexOf("!#" + (i + 1) + ";") >= 0)
          continue;
      } else if (filter.indexOf("#") >= 0
          && filter.indexOf("#" + (i + 1) + ";") < 0) {
        continue;
      }
      Matrix4f mat = biomts.get(i);
      //Vector3f trans = new Vector3f();    
      for (int iAtom = iAtomFirst; iAtom < atomMax; iAtom++) {
        try {
          int atomSite = atoms[iAtom].atomSite;
          Atom atom1;
          if (addBonds)
            atomMap[atomSite] = atomCount;
            atom1 = newCloneAtom(atoms[iAtom]);
            atom1.atomSite = atomSite;
          mat.transform(atom1);
          atom1.bsSymmetry = BitSetUtil.setBit(i);
          if (addBonds) {
            // Clone bonds
            for (int bondNum = bondIndex0; bondNum < bondCount0; bondNum++) {
              Bond bond = bonds[bondNum];
              int iAtom1 = atomMap[atoms[bond.atomIndex1].atomSite];
              int iAtom2 = atomMap[atoms[bond.atomIndex2].atomSite];
              if (iAtom1 >= atomMax || iAtom2 >= atomMax)
                addNewBond(iAtom1, iAtom2, bond.order);
            }
          }
        } catch (Exception e) {
          errorMessage = "appendAtomCollection error: " + e;
        }
      }
      mat.m03 /= notionalUnitCell[0];
      mat.m13 /= notionalUnitCell[1];
      mat.m23 /= notionalUnitCell[2];
      if (symmetry != null && i > 0)
        symmetry.addSpaceGroupOperation(mat);
      //System.out.println("biomt " + i + " " + atomCount);
    }
    int noSymmetryCount = atomMax - iAtomFirst;
    setAtomSetAuxiliaryInfo("presymmetryAtomIndex", Integer.valueOf(iAtomFirst));
    setAtomSetAuxiliaryInfo("presymmetryAtomCount", Integer.valueOf(noSymmetryCount));
    setAtomSetAuxiliaryInfo("biosymmetryCount", Integer.valueOf(len));
    if (symmetry != null) {
      symmetry.setFinalOperations(atoms, iAtomFirst, noSymmetryCount, doNormalize);
      setSymmetryOps();
    }
    symmetry = null;
    notionalUnitCell = new float[6];
    coordinatesAreFractional = false; 
    setAtomSetAuxiliaryInfo("hasSymmetry", Boolean.TRUE);
    setGlobalBoolean(GLOBAL_SYMMETRY);
    //TODO: need to clone bonds
  }
  
  Map<Object, Integer> atomSymbolicMap = new Hashtable<Object, Integer>();

  void mapMostRecentAtomName() {
    //from ?? 
    if (atomCount > 0) {
      int index = atomCount - 1;
      String atomName = atoms[index].atomName;
      if (atomName != null)
        atomSymbolicMap.put(atomName, Integer.valueOf(index));
    }
  }

  public void clearSymbolicMap() {
    atomSymbolicMap.clear();
    haveMappedSerials = false;
  }

  boolean haveMappedSerials;
  void mapMostRecentAtomSerialNumber() {
    // from ??
    if (atomCount == 0)
      return;
    int index = atomCount - 1;
    int atomSerial = atoms[index].atomSerial;
    if (atomSerial != Integer.MIN_VALUE)
      atomSymbolicMap.put(Integer.valueOf(atomSerial), Integer.valueOf(index));
    haveMappedSerials = true;
  }

  public void createAtomSerialMap() {
    if (haveMappedSerials || currentAtomSetIndex < 0)
      return;
    for (int i = getLastAtomSetAtomCount(); i < atomCount; i++) {
      int atomSerial = atoms[i].atomSerial;
      if (atomSerial != Integer.MIN_VALUE)
        atomSymbolicMap.put(Integer.valueOf(atomSerial), Integer.valueOf(i));
    }
    haveMappedSerials = true;
  }

  void mapAtomName(String atomName, int atomIndex) {
    atomSymbolicMap.put(atomName, Integer.valueOf(atomIndex));
  }

  public int getAtomNameIndex(String atomName) {
    //for new Bond -- inconsistent with mmCIF altLoc
    int index = -1;
    Object value = atomSymbolicMap.get(atomName);
    if (value != null)
      index = ((Integer)value).intValue();
    return index;
  }

  public int getAtomSerialNumberIndex(int serialNumber) {
    int index = -1;
    Object value = atomSymbolicMap.get(Integer.valueOf(serialNumber));
    if (value != null)
      index = ((Integer)value).intValue();
    return index;
  }
  
  public void setAtomSetCollectionAuxiliaryInfo(String key, Object value) {
    atomSetCollectionAuxiliaryInfo.put(key, value);
  }
  
  /**
   * Sets the partial atomic charges based on atomSetCollection auxiliary info
   *
   * @param auxKey The auxiliary key name that contains the charges
   * @return true if the data exist; false if not  
   */

  public boolean setAtomSetCollectionPartialCharges(String auxKey) {
    if (! atomSetCollectionAuxiliaryInfo.containsKey(auxKey)) {
      return false;
    }
    List<Float> atomData = (List<Float>) atomSetCollectionAuxiliaryInfo.get(auxKey);
    for (int i = atomData.size(); --i >= 0;)
      atoms[i].partialCharge = atomData.get(i).floatValue();
    Logger.info("Setting partial charges type " + auxKey);
    return true;
  }
  
  public void mapPartialCharge(String atomName, float charge) {
    atoms[getAtomNameIndex(atomName)].partialCharge = charge;  
  }
  
  public Object getAtomSetCollectionAuxiliaryInfo(String key) {
    return atomSetCollectionAuxiliaryInfo.get(key);
  }
  
  ////////////////////////////////////////////////////////////////
  // atomSet stuff
  ////////////////////////////////////////////////////////////////
  
  private void addTrajectoryStep() {
    trajectoryStep = new Point3f[atomCount];
    Point3f[] prevSteps = (trajectoryStepCount == 0 ? null 
        : (Point3f[]) trajectorySteps.get(trajectoryStepCount - 1));
    for (int i = 0; i < atomCount; i++) {
      Point3f pt = new Point3f(atoms[i]);
      if (doFixPeriodic && prevSteps != null)
        pt = fixPeriodic(pt, prevSteps[i]);
      trajectoryStep[i] = pt;
    }
    trajectorySteps.add(trajectoryStep);
    trajectoryStepCount++;
  }
  
  private Point3f fixPeriodic(Point3f pt, Point3f pt0) {
    pt.x = fixPoint(pt.x, pt0.x);   
    pt.y = fixPoint(pt.y, pt0.y);   
    pt.z = fixPoint(pt.z, pt0.z);   
    return pt;
  }

  private float fixPoint(float x, float x0) {
    while (x - x0 > 0.9) {
      x -= 1;
    }
    while (x - x0 < -0.9) {
      x += 1;
    }
    return x;
  }

  void finalizeTrajectory(List<Point3f[]> trajectorySteps) {
    this.trajectorySteps = trajectorySteps;
    trajectoryStepCount = trajectorySteps.size();
    finalizeTrajectory();
  }

  private void finalizeTrajectory() {
    if (trajectoryStepCount == 0)
      return;
    //reset atom positions to original trajectory
    Point3f[] trajectory = trajectorySteps.get(0);
    for (int i = 0; i < atomCount; i++)
      atoms[i].set(trajectory[i]);
    setAtomSetCollectionAuxiliaryInfo("trajectorySteps", trajectorySteps);
  }
 
  public void newAtomSet() {
    if (!allowMultiple && currentAtomSetIndex >= 0)
      discardPreviousAtoms();
    bondIndex0 = bondCount;
    if (isTrajectory) {
      discardPreviousAtoms();
    }    
    currentAtomSetIndex = atomSetCount++;
    if (atomSetCount > atomSetNumbers.length) {
      atomSetAtomCounts = ArrayUtil.doubleLength(atomSetAtomCounts);
      atomSetBondCounts = ArrayUtil.doubleLength(atomSetBondCounts);
      atomSetAuxiliaryInfo = (Map<String, Object>[]) ArrayUtil.doubleLength(atomSetAuxiliaryInfo);
    }
    if (atomSetCount + trajectoryStepCount > atomSetNumbers.length) {
      atomSetNumbers = ArrayUtil.doubleLength(atomSetNumbers);
    }
    if (isTrajectory) {
      atomSetNumbers[currentAtomSetIndex + trajectoryStepCount] = atomSetCount + trajectoryStepCount;
    } else {
      atomSetNumbers[currentAtomSetIndex] = atomSetCount;
    }
    atomSymbolicMap.clear();
    setAtomSetAuxiliaryInfo("title", collectionName);    
  }

  /**
  * Sets the name for the current AtomSet
  *
  * @param atomSetName The name to be associated with the current AtomSet
  */
  public void setAtomSetName(String atomSetName) {
    setAtomSetAuxiliaryInfo("name", atomSetName, currentAtomSetIndex);
    if (!allowMultiple)
      setCollectionName(atomSetName);
  }
  
  /**
   * Sets the atom set names of the last n atomSets
   * 
   * @param atomSetName
   *          The name
   * @param n
   *          The number of last AtomSets that needs these set
   */
  public void setAtomSetNames(String atomSetName, int n) {
    for (int idx = currentAtomSetIndex; --n >= 0 && idx >= 0; --idx)
      setAtomSetAuxiliaryInfo("name", atomSetName, idx);
  }

  /**
  * Sets the number for the current AtomSet
  *
  * @param atomSetNumber The number for the current AtomSet.
  */
  public void setAtomSetNumber(int atomSetNumber) {
    if (isTrajectory)
      atomSetNumbers[currentAtomSetIndex + trajectoryStepCount] = atomSetNumber;
    else
      atomSetNumbers[currentAtomSetIndex] = atomSetNumber;
  }
  
  /**
   * Sets a property for the current AtomSet
   * used specifically for creating directories and plots of frequencies and 
   * moleular energies
   *
   * @param key The key for the property
   * @param value The value to be associated with the key
   */
  public void setAtomSetProperty(String key, String value) {
    setAtomSetProperty(key, value, currentAtomSetIndex);
  }

  /**
   * Sets the a property for the an AtomSet
   *
   * @param key The key for the property
   * @param value The value for the property
   * @param atomSetIndex The index of the AtomSet to get the property
   */
  public void setAtomSetProperty(String key, String value, int atomSetIndex) {
    // lazy instantiation of the Properties object
    Properties p = (Properties) getAtomSetAuxiliaryInfo(atomSetIndex,
        "modelProperties");
    if (p == null)
      setAtomSetAuxiliaryInfo("modelProperties", p = new Properties(),
          atomSetIndex);
    p.put(key, value);
  }


  public void setAtomSetAuxiliaryProperty(String key, String data) {
    if (!data.endsWith("\n")) {
      data += "\n";
    }
    Map p = (Map) getAtomSetAuxiliaryInfo(currentAtomSetIndex, "atomProperties");
    if (p == null) {
      setAtomSetAuxiliaryInfo("atomProperties", p = new Hashtable());
    }
    p.put(key, data);
  }

  private void appendAtomProperties(int nTimes) {
    Map<String, String> p = (Map<String, String>) getAtomSetAuxiliaryInfo(currentAtomSetIndex, "atomProperties");
    if (p == null) {
      return;
    }
    for (Map.Entry<String, String> entry : p.entrySet()) {
      String key = entry.getKey();
      String data = entry.getValue();
      StringBuilder s = new StringBuilder();
      for (int i = nTimes; --i >= 0; )
        s.append(data);   
      p.put(key, s.toString());
    }
  }


  /**
  * Sets auxiliary information for the AtomSet
  *
  * @param key The key for the property
  * @param value The value to be associated with the key
  */
  public void setAtomSetAuxiliaryInfo(String key, Object value) {
    setAtomSetAuxiliaryInfo(key, value, currentAtomSetIndex);
  }

  /**
   * Sets the partial atomic charges based on atomSet auxiliary info
   *
   * @param auxKey The auxiliary key name that contains the charges
   * @return true if the data exist; false if not  
   */

  boolean setAtomSetPartialCharges(String auxKey) {
    if (!atomSetAuxiliaryInfo[currentAtomSetIndex].containsKey(auxKey)) {
      return false;
    }
    List<Float> atomData = (List<Float>) getAtomSetAuxiliaryInfo(currentAtomSetIndex, auxKey);
    for (int i = atomData.size(); --i >= 0;) {
      atoms[i].partialCharge = atomData.get(i).floatValue();
    }
    return true;
  }
  
  Object getAtomSetAuxiliaryInfo(int index, String key) {
    return  atomSetAuxiliaryInfo[index].get(key);
  }
  
  /**
   * Sets auxiliary information for an AtomSet
   *
   * @param key The key for the property
   * @param value The value for the property
   * @param atomSetIndex The index of the AtomSet to get the property
   */
  void setAtomSetAuxiliaryInfo(String key, Object value, int atomSetIndex) {
    if (atomSetIndex < 0)
      return;
    if (atomSetAuxiliaryInfo[atomSetIndex] == null)
      atomSetAuxiliaryInfo[atomSetIndex] = new Hashtable<String, Object>();
    if (value == null)
      atomSetAuxiliaryInfo[atomSetIndex].remove(key);
    else
      atomSetAuxiliaryInfo[atomSetIndex].put(key, value);
  }

  /**
   * Sets the same properties for the last n atomSets.
   * 
   * @param key
   *          The key for the property
   * @param value
   *          The value of the property
   * @param n
   *          The number of last AtomSets that needs these set
   */
  public void setAtomSetProperties(String key, String value, int n) {
    for (int idx = currentAtomSetIndex; --n >= 0 && idx >= 0; --idx) {
      setAtomSetProperty(key, value, idx);
    }
  }
  

  /**
   * Clones the properties of the last atom set and associates it
   * with the current atom set. 
   */
  public void cloneLastAtomSetProperties() {
    cloneAtomSetProperties(currentAtomSetIndex-1);
  }

  /**
   * Clones the properties of an atom set and associated it with the
   * current atom set.
   * @param index The index of the atom set whose properties are to be cloned.
   */
  void cloneAtomSetProperties(int index) {
    Properties p = (Properties) getAtomSetAuxiliaryInfo(index, "modelProperties");
    if (p != null)
      setAtomSetAuxiliaryInfo("modelProperties", p.clone(), currentAtomSetIndex);
  }

  int getAtomSetNumber(int atomSetIndex) {
    return atomSetNumbers[atomSetIndex >= atomSetCount ? 0 : atomSetIndex];
  }

  String getAtomSetName(int atomSetIndex) {
    if (atomSetIndex >= atomSetCount)
      atomSetIndex = atomSetCount - 1;
    return (String) getAtomSetAuxiliaryInfo(atomSetIndex, "name");
  }
  
  Map<String, Object> getAtomSetAuxiliaryInfo(int atomSetIndex) {
    return atomSetAuxiliaryInfo[atomSetIndex >= atomSetCount ? atomSetCount - 1 : atomSetIndex];
  }

  //// for XmlChem3dReader, but could be for CUBE
  
  VolumeDataInterface vd;

  public BitSet bsAtoms;
  
  public void newVolumeData() {
    vd = (VolumeDataInterface) Interface.getOptionInterface("jvxl.data.VolumeData");
  }

  public void setVoxelCounts(int nPointsX, int nPointsY, int nPointsZ) {
    vd.setVoxelCounts(nPointsX, nPointsY, nPointsZ);
  }

  public void setVolumetricVector(int i, float x, float y, float z) {
    vd.setVolumetricVector(i, x, y, z);
  }

  public void setVolumetricOrigin(float x, float y, float z) {
    vd.setVolumetricOrigin(x, y, z);
  }

  public void setVoxelData(float[][][] voxelData) {
    vd.setVoxelData(voxelData);    
  }

  public Object getVolumeData() {
    VolumeDataInterface v = vd;
    vd = null; //delete adapter reference
    return v;
  }

  public Properties setAtomNames(Properties atomIdNames) {
    // for CML reader "a3" --> "N3"
    if (atomIdNames == null)
      return null;
    String s;
    for (int i = 0; i < atomCount; i++)
      if ((s = atomIdNames.getProperty(atoms[i].atomName)) != null)
        atoms[i].atomName = s;
    return null;
  }

  public void setAtomSetEnergy(String energyString, float value) {
    if (currentAtomSetIndex < 0)
      return;
    setAtomSetAuxiliaryInfo("EnergyString", energyString);
    setAtomSetAuxiliaryInfo("Energy", new Float(value));
    setAtomSetProperty("Energy", "" + value);
  }

  public void setAtomSetFrequency(String pathKey, String label, String freq, String units) {
    freq += " " + (units == null ? "cm^-1" : units);
    setAtomSetName((label == null ? "" : label + " ") + freq);
    setAtomSetProperty("Frequency", freq);
    if (label != null)
      setAtomSetProperty("FrequencyLabel", label);
    setAtomSetProperty(SmarterJmolAdapter.PATH_KEY, (pathKey == null ? ""
        : pathKey + SmarterJmolAdapter.PATH_SEPARATOR + "Frequencies")
        + "Frequencies");
  }

  void toCartesian(SymmetryInterface symmetry) {
    for (int i = getLastAtomSetAtomIndex(); i < atomCount; i++)
      symmetry.toCartesian(atoms[i], true);
  }

}
