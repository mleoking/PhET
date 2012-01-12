/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2006-10-22 14:12:46 -0500 (Sun, 22 Oct 2006) $
 * $Revision: 5999 $
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

import org.jmol.api.Interface;
import org.jmol.api.JmolAdapter;
import org.jmol.api.JmolViewer;
import org.jmol.api.SymmetryInterface;
import org.jmol.util.BinaryDocument;
import org.jmol.util.BitSetUtil;
import org.jmol.util.Logger;
import org.jmol.util.Parser;
import org.jmol.util.Quaternion;

import java.io.BufferedReader;
import java.io.OutputStream;

import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import javax.vecmath.Matrix3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/*
 * Notes 9/2006 Bob Hanson
 * 
 * all parsing functions now moved to org.jmol.util.Parser
 * 
 * to add symmetry capability to any reader, some or all of the following 
 * methods need to be there:
 * 
 *  setFractionalCoordinates()
 *  setSpaceGroupName()
 *  setUnitCell()
 *  setUnitCellItem()
 *  setAtomCoord()
 * 
 * At the very minimum, you need:
 * 
 *  setAtomCoord()
 * 
 * so that:
 *  (a) atom coordinates can be turned fractional by load parameters
 *  (b) symmetry can be applied once per model in the file
 *  
 *  If you know where the end of the atom+bond data are, then you can
 *  use applySymmetryAndSetTrajectory() once, just before exiting. Otherwise, use it
 *  twice -- it has a check to make sure it doesn't RUN twice -- once
 *  at the beginning and once at the end of the model.
 *  
 * htParams is used for passing information to the readers
 * and for returning information from the readers
 * 
 * It won't be null at this stage.
 * 
 * from Eval or Viewer:
 * 
 *  applySymmetryToBonds
 *  atomTypes (for Mol2Reader)
 *  bsModels
 *  filter
 *  firstLastStep
 *  firstLastSteps
 *  getHeader
 *  isTrajectory
 *  lattice
 *  manifest (for SmarterJmolAdapter)
 *  modelNumber
 *  spaceGroupIndex
 *  symmetryRange
 *  unitcell
 *  packed
 *  
 * from FileManager:
 * 
 *  fullPathName
 *  subFileList (for SmarterJmolAdapter)
 * 
 * from MdTopReader:
 *   
 *  isPeriodic
 *  templateAtomCount
 *  
 * from MdCrdReader:   
 * 
 *  trajectorySteps
 *  
 * from Resolver:
 * 
 *  filteredAtomCount
 *  ptFile
 *  readerName
 *  templateAtomCount
 *  
 *  
 * from AtomSetCollectionReader:
 *  
 *  bsFilter
 *  
 * 
 */

public abstract class AtomSetCollectionReader {

  public final static float ANGSTROMS_PER_BOHR = 0.5291772f; // used by SpartanArchive

  public boolean isBinary;

  public AtomSetCollection atomSetCollection;
  protected BufferedReader reader;
  protected BinaryDocument doc;
  protected String readerName;
  public Map<String, Object> htParams;
  //protected String parameterData;

  // buffer
  public String line, prevline;
  protected int[] next = new int[1];
  protected long ptLine;

  // protected/public state variables
  public int[] latticeCells;
  public boolean doProcessLines;
  public boolean iHaveUnitCell;
  public boolean iHaveSymmetryOperators;
  public boolean continuing = true;

  protected JmolViewer viewer; // used by GenNBOReader and by CifReader

  protected boolean doApplySymmetry;
  protected boolean ignoreFileSymmetryOperators;
  protected boolean isTrajectory;
  public boolean applySymmetryToBonds;
  protected boolean doCheckUnitCell;
  protected boolean getHeader;
  protected boolean isSequential;
  protected int templateAtomCount;
  public int modelNumber;
  protected int vibrationNumber;
  public int desiredVibrationNumber = Integer.MIN_VALUE;
  protected BitSet bsModels;
  protected boolean havePartialChargeFilter;
  public String calculationType = "?";
  protected String spaceGroup;
  protected boolean ignoreFileUnitCell;
  protected boolean ignoreFileSpaceGroupName;
  protected float[] notionalUnitCell; //0-5 a b c alpha beta gamma; 6-21 matrix c->f
  protected int desiredModelNumber = Integer.MIN_VALUE;
  protected SymmetryInterface symmetry;
  protected OutputStream os;
  protected boolean iHaveFractionalCoordinates;
  protected boolean doPackUnitCell;

  // private state variables

  private boolean doConvertToFractional;
  private boolean merging;
  private boolean fileCoordinatesAreFractional;
  private float symmetryRange;
  private int[] firstLastStep;
  private int lastModelNumber = Integer.MAX_VALUE;
  private int desiredSpaceGroupIndex = -1;
  private Point3f fileScaling;
  private Point3f fileOffset;
  private Point3f fileOffsetFractional;
  private Point3f unitCellOffset;
  private String supercell;
  private boolean unitCellOffsetFractional;

  /*  
    public void finalize() {
      System.out.println(this + " finalized");
    }
  */

  String fileName;

  void setup(String fileName, Map<String, Object> htParams, Object reader) {
    this.htParams = htParams;
    this.fileName = fileName.replace('\\', '/');
    if (reader instanceof BufferedReader)
      this.reader = (BufferedReader) reader;
    else if (reader instanceof BinaryDocument)
      this.doc = (BinaryDocument) reader;
  }

  Object readData() throws Exception {
    initialize();
    atomSetCollection = new AtomSetCollection(readerName, this);
    try {
      initializeReader();
      if (doc == null) {
        if (line == null && continuing)
          readLine();
        while (line != null && continuing)
          if (checkLine())
            readLine();
      } else {
        processBinaryDocument(doc);
      }
      finalizeReader();
    } catch (Throwable e) {
      setError(e);
    }
    if (reader != null)
      reader.close();
    if (doc != null)
      doc.close();
    return finish();
  }

  protected Object readData(Object node) throws Exception {
    initialize();
    atomSetCollection = new AtomSetCollection(readerName, this);
    initializeReader();
    processXml(node);
    return finish();
  }

  /**
   * 
   * @param DOMNode
   */
  protected void processXml(Object DOMNode) {
    // XML readers only
  }

  /**
   * @param doc  
   * @throws Exception 
   */
  protected void processBinaryDocument(BinaryDocument doc) throws Exception {
    // Binary readers only
  }

  protected void initializeReader() throws Exception {
    // reader-dependent
  }

  /**
   * @return true if need to read new line
   * @throws Exception 
   * 
   */
  protected boolean checkLine() throws Exception {
    // reader-dependent
    return true;
  }

  /**
   * sets continuing and doProcessLines
   * 
   * @return TRUE if continuing, FALSE if not
   * 
   */
  public boolean checkLastModel() {
    if (isLastModel(modelNumber) && doProcessLines) {
      continuing = false;
      return false;
    }
    doProcessLines = false;
    return true;
  }

  /**
   * after reading a model, Q: Is this the last model?
   * 
   * @param modelNumber
   * @return  Yes/No
   */
  public boolean isLastModel(int modelNumber) {
    return (desiredModelNumber > 0 || modelNumber >= lastModelNumber);
  }

  protected void finalizeReader() throws Exception {
    applySymmetryAndSetTrajectory();
  }

  /////////////////////////////////////////////////////////////////////////////////////

  private Object finish() {
    String s = (String) htParams.get("loadState");
    atomSetCollection.setAtomSetCollectionAuxiliaryInfo("loadState",
        s == null ? "" : s);
    s = (String) htParams.get("smilesString");
    if (s != null)
      atomSetCollection.setAtomSetCollectionAuxiliaryInfo("smilesString", s);
    if (!htParams.containsKey("templateAtomCount"))
      htParams.put("templateAtomCount", Integer.valueOf(atomSetCollection
          .getAtomCount()));
    if (htParams.containsKey("bsFilter"))
      htParams.put("filteredAtomCount", Integer.valueOf(BitSetUtil
          .cardinalityOf((BitSet) htParams.get("bsFilter"))));
    if (!calculationType.equals("?"))
      atomSetCollection.setAtomSetCollectionAuxiliaryInfo("calculationType",
          calculationType);

    String name = atomSetCollection.getFileTypeName();
    String fileType = name;
    if (fileType.indexOf("(") >= 0)
      fileType = fileType.substring(0, fileType.indexOf("("));
    for (int i = atomSetCollection.getAtomSetCount(); --i >= 0;) {
      atomSetCollection.setAtomSetAuxiliaryInfo("fileName", fileName, i);
      atomSetCollection.setAtomSetAuxiliaryInfo("fileType", fileType, i);
    }
    atomSetCollection.freeze();
    if (atomSetCollection.errorMessage != null)
      return atomSetCollection.errorMessage + "\nfor file " + fileName
          + "\ntype " + name;
    if (atomSetCollection.getAtomCount() == 0)
      return "No atoms found\nfor file " + fileName + "\ntype " + name;
    return atomSetCollection;
  }

  private void setError(Throwable e) {
    e.printStackTrace();
    if (line == null)
      atomSetCollection.errorMessage = "Unexpected end of file after line "
          + --ptLine + ":\n" + prevline;
    else
      atomSetCollection.errorMessage = "Error reading file at line " + ptLine
          + ":\n" + line + "\n" + e.getMessage();
  }

  @SuppressWarnings("unchecked")
  private void initialize() {

    initializeSymmetry();
    viewer = (JmolViewer) htParams.get("viewer");
    htParams.remove("viewer"); // don't pass this on to user
    getHeader = htParams.containsKey("getHeader");
    isSequential = htParams.containsKey("isSequential");
    readerName = (String) htParams.get("readerName");
    //parameterData = (String) htParams.get("parameterData");
    if (htParams.containsKey("vibrationNumber"))
      desiredVibrationNumber = ((Integer) htParams.get("vibrationNumber"))
          .intValue();
    else if (htParams.containsKey("modelNumber"))
      desiredModelNumber = ((Integer) htParams.get("modelNumber")).intValue();
    applySymmetryToBonds = htParams.containsKey("applySymmetryToBonds");
    bsFilter = (BitSet) htParams.get("bsFilter");
    setFilter((String) htParams.get("filter"));
    // ptFile < 0 indicates just one file being read
    // ptFile >= 0 indicates multiple files are being loaded
    // if the file is not the first read in the LOAD command, then
    // we look to see if it was loaded using LOAD ... "..." COORD ....
    int ptFile = (htParams.containsKey("ptFile") ? ((Integer) htParams
        .get("ptFile")).intValue() : -1);
    isTrajectory = htParams.containsKey("isTrajectory");
    if (ptFile > 0 && htParams.containsKey("firstLastSteps")) {
      Object val = ((List<Object>) htParams.get("firstLastSteps"))
          .get(ptFile - 1);
      if (val instanceof BitSet) {
        bsModels = (BitSet) val;
      } else {
        firstLastStep = (int[]) val;
      }
    } else if (htParams.containsKey("firstLastStep")) {
      firstLastStep = (int[]) htParams.get("firstLastStep");
    } else if (htParams.containsKey("bsModels")) {
      bsModels = (BitSet) htParams.get("bsModels");
    }
    if (htParams.containsKey("templateAtomCount"))
      templateAtomCount = ((Integer) htParams.get("templateAtomCount"))
          .intValue();
    if (bsModels != null || firstLastStep != null)
      desiredModelNumber = Integer.MIN_VALUE;
    if (bsModels == null && firstLastStep != null) {
      if (firstLastStep[0] < 0)
        firstLastStep[0] = 0;
      if (firstLastStep[2] == 0 || firstLastStep[1] < firstLastStep[0])
        firstLastStep[1] = -1;
      if (firstLastStep[2] < 1)
        firstLastStep[2] = 1;
      bsModels = BitSetUtil.setBit(firstLastStep[0]);
      if (firstLastStep[1] > firstLastStep[0]) {
        for (int i = firstLastStep[0]; i <= firstLastStep[1]; i += firstLastStep[2])
          bsModels.set(i);
      }
    }
    if (bsModels != null && (firstLastStep == null || firstLastStep[1] != -1))
      lastModelNumber = bsModels.length();
    symmetryRange = (htParams.containsKey("symmetryRange") ? ((Float) htParams
        .get("symmetryRange")).floatValue() : 0);

    latticeCells = new int[3];
    if (htParams.containsKey("lattice")) {
      Point3f pt = ((Point3f) htParams.get("lattice"));
      latticeCells[0] = (int) pt.x;
      latticeCells[1] = (int) pt.y;
      latticeCells[2] = (int) pt.z;
      doPackUnitCell = (htParams.containsKey("packed") || latticeCells[2] < 0);
    }
    if (htParams.containsKey("supercell"))
      supercell = (String) htParams.get("supercell");
    doApplySymmetry = (latticeCells[0] > 0 && latticeCells[1] > 0);
    //allows for {1 1 1} or {1 1 -1} or {555 555 0|1|-1} (-1  being "packed")
    if (!doApplySymmetry) {
      latticeCells[0] = 0;
      latticeCells[1] = 0;
      latticeCells[2] = 0;
    }

    //this flag FORCES symmetry -- generally if coordinates are not fractional,
    //we may note the unit cell, but we do not apply symmetry
    //with this flag, we convert any nonfractional coordinates to fractional
    //if a unit cell is available.

    if (htParams.containsKey("spaceGroupIndex")) {
      // three options include:
      // = -1: normal -- use operators if present or name if not
      // = -2: user is supplying operators or name
      // >=0: spacegroup fully determined
      // = -999: ignore -- just the operators

      desiredSpaceGroupIndex = ((Integer) htParams.get("spaceGroupIndex"))
          .intValue();
      if (desiredSpaceGroupIndex == -2)
        spaceGroup = (String) htParams.get("spaceGroupName");
      ignoreFileSpaceGroupName = (desiredSpaceGroupIndex == -2 || desiredSpaceGroupIndex >= 0);
      ignoreFileSymmetryOperators = (desiredSpaceGroupIndex != -1);
    }
    if (htParams.containsKey("unitCellOffset")) {
      fileScaling = new Point3f(1, 1, 1);
      fileOffset = (Point3f) htParams.get("unitCellOffset");
      fileOffsetFractional = new Point3f(fileOffset);
      unitCellOffsetFractional = htParams
          .containsKey("unitCellOffsetFractional");
    }
    if (htParams.containsKey("unitcell")) {
      float[] fParams = (float[]) htParams.get("unitcell");
      if (fParams.length == 9) {
        // these are vectors
        addPrimitiveLatticeVector(0, fParams, 0);
        addPrimitiveLatticeVector(1, fParams, 3);
        addPrimitiveLatticeVector(2, fParams, 6);
      } else {
        setUnitCell(fParams[0], fParams[1], fParams[2], fParams[3], fParams[4],
            fParams[5]);
      }
      ignoreFileUnitCell = iHaveUnitCell;
    }
    merging = htParams.containsKey("merging");

    if (htParams.containsKey("OutputStream"))
      os = (OutputStream) htParams.get("OutputStream");

  }

  public boolean haveModel;

  public boolean doGetModel(int modelNumber) {
    // modelNumber is 1-based, but firstLastStep is 0-based
    boolean isOK = (bsModels == null ? desiredModelNumber < 1
        || modelNumber == desiredModelNumber
        : modelNumber > lastModelNumber ? false : modelNumber > 0
            && bsModels.get(modelNumber - 1)
            || haveModel
            && firstLastStep != null
            && firstLastStep[1] < 0
            && (firstLastStep[2] < 2 || (modelNumber - 1 - firstLastStep[0])
                % firstLastStep[2] == 0));
    if (isOK && desiredModelNumber == 0)
      atomSetCollection.discardPreviousAtoms();
    haveModel |= isOK;
    if (isOK)
      doProcessLines = true;
    return isOK;
  }

  private String previousSpaceGroup;
  private float[] previousUnitCell;

  protected void initializeSymmetry() {
    previousSpaceGroup = spaceGroup;
    previousUnitCell = notionalUnitCell;
    iHaveUnitCell = ignoreFileUnitCell;
    if (!ignoreFileUnitCell) {
      notionalUnitCell = new float[22];
      //0-5 a b c alpha beta gamma; 6-21 m00 m01... m33 cartesian-->fractional
      for (int i = 22; --i >= 0;)
        notionalUnitCell[i] = Float.NaN;
      symmetry = null;
    }
    if (!ignoreFileSpaceGroupName)
      spaceGroup = "unspecified!";

    doCheckUnitCell = false;
  }

  protected void newAtomSet(String name) {
    if (atomSetCollection.getCurrentAtomSetIndex() >= 0) {
      atomSetCollection.newAtomSet();
      atomSetCollection.setCollectionName("<collection of "
          + (atomSetCollection.getCurrentAtomSetIndex() + 1) + " models>");
    } else {
      atomSetCollection.setCollectionName(name);
    }
    Logger.debug(name);
  }

  protected int cloneLastAtomSet(int atomCount) throws Exception {
    applySymmetryAndSetTrajectory();
    int lastAtomCount = atomSetCollection.getLastAtomSetAtomCount();
    atomSetCollection.cloneLastAtomSet(atomCount);
    if (atomSetCollection.haveUnitCell) {
      iHaveUnitCell = true;
      doCheckUnitCell = true;
      spaceGroup = previousSpaceGroup;
      notionalUnitCell = previousUnitCell;
    }
    return lastAtomCount;
  }

  public void setSpaceGroupName(String name) {
    if (ignoreFileSpaceGroupName)
      return;
    spaceGroup = name.trim();
    Logger.info("Setting space group name to " + spaceGroup);
  }

  public void setSymmetryOperator(String xyz) {
    if (ignoreFileSymmetryOperators)
      return;
    atomSetCollection.setLatticeCells(latticeCells, applySymmetryToBonds,
        doPackUnitCell, supercell);
    if (!atomSetCollection.addSpaceGroupOperation(xyz))
      Logger.warn("Skipping symmetry operation " + xyz);
    iHaveSymmetryOperators = true;
  }

  private int nMatrixElements = 0;

  private void initializeCartesianToFractional() {
    for (int i = 0; i < 16; i++)
      if (!Float.isNaN(notionalUnitCell[6 + i]))
        return; //just do this once
    for (int i = 0; i < 16; i++)
      notionalUnitCell[6 + i] = ((i % 5 == 0 ? 1 : 0));
    nMatrixElements = 0;
  }

  public void clearUnitCell() {
    if (ignoreFileUnitCell)
      return;
    for (int i = 6; i < notionalUnitCell.length; i++)
      notionalUnitCell[i] = Float.NaN;
    checkUnitCell(6);
  }

  public void setUnitCellItem(int i, float x) {
    if (ignoreFileUnitCell)
      return;
    if (i == 0 && x == 1 || i == 3 && x == 0)
      return;
    if (!Float.isNaN(x) && i >= 6 && Float.isNaN(notionalUnitCell[6]))
      initializeCartesianToFractional();
    notionalUnitCell[i] = x;
    if (Logger.debugging) {
      Logger.debug("setunitcellitem " + i + " " + x);
    }
    //System.out.println("atomSetCollection unitcell item " + i + " = " + x);
    if (i < 6 || Float.isNaN(x))
      iHaveUnitCell = checkUnitCell(6);
    else if (++nMatrixElements == 12)
      checkUnitCell(22);
  }

  protected Matrix3f matUnitCellOrientation;

  public void setUnitCell(float a, float b, float c, float alpha, float beta,
                          float gamma) {
    if (ignoreFileUnitCell)
      return;
    clearUnitCell();
    notionalUnitCell[0] = a;
    notionalUnitCell[1] = b;
    notionalUnitCell[2] = c;
    if (alpha != 0)
      notionalUnitCell[3] = alpha;
    if (beta != 0)
      notionalUnitCell[4] = beta;
    if (gamma != 0)
      notionalUnitCell[5] = gamma;
    iHaveUnitCell = checkUnitCell(6);
  }

  public void addPrimitiveLatticeVector(int i, float[] xyz, int i0) {
    if (ignoreFileUnitCell)
      return;
    if (i == 0)
      for (int j = 0; j < 6; j++)
        notionalUnitCell[j] = 0;
    i = 6 + i * 3;
    notionalUnitCell[i++] = xyz[i0++];
    notionalUnitCell[i++] = xyz[i0++];
    notionalUnitCell[i] = xyz[i0];
    if (Float.isNaN(notionalUnitCell[0])) {
      for (i = 0; i < 6; i++)
        notionalUnitCell[i] = -1;
    }
    iHaveUnitCell = checkUnitCell(15);
  }

  private boolean checkUnitCell(int n) {
    for (int i = 0; i < n; i++)
      if (Float.isNaN(notionalUnitCell[i]))
        return false;
    getSymmetry().setUnitCell(notionalUnitCell);
    if (doApplySymmetry)
      doConvertToFractional = !fileCoordinatesAreFractional;
    //if (but not only if) applying symmetry do we force conversion
    checkUnitCellOffset();
    return true;
  }

  private void checkUnitCellOffset() {
    if (symmetry == null || fileOffsetFractional == null)
      return;
    fileOffset.set(fileOffsetFractional);
    if (unitCellOffsetFractional != fileCoordinatesAreFractional) {
      if (unitCellOffsetFractional)
        symmetry.toCartesian(fileOffset, false);
      else
        symmetry.toFractional(fileOffset, false);
    }
  }

  protected SymmetryInterface getSymmetry() {
    symmetry = (SymmetryInterface) Interface
        .getOptionInterface("symmetry.Symmetry");
    return symmetry;
  }

  public void setFractionalCoordinates(boolean TF) {
    iHaveFractionalCoordinates = fileCoordinatesAreFractional = TF;
    checkUnitCellOffset();
  }

  /////////// FILTER /////////////////

  protected BitSet bsFilter;
  protected String filter;
  private boolean haveAtomFilter;
  private boolean filterAltLoc;
  private boolean filterGroup3;
  private boolean filterChain;
  private boolean filterAtomType;
  protected boolean filterHetero;
  private boolean filterEveryNth;
  private int filterN;
  private int nFiltered;
  private boolean doSetOrientation;
  protected boolean addVibrations;
  protected boolean useAltNames;
  public boolean readMolecularOrbitals;

  // MANY: "NOVIB" "NOMO"
  // CSF, SPARTAN: "NOORIENT"
  // CRYSTAL: "CONV" (conventional), "INPUT"
  // GAMESS-US:  "CHARGE=LOW"
  // JME, MOL: "NOMIN"
  // MOL:  "2D"
  // Molden: "INPUT" "GEOM" "NOGEOM"
  // MOReaders: "NBOCHARGES"
  // PDB: "BIOMOLECULE n;" "NOSYMMETRY"  "CONF n"
  // Spartan: "INPUT", "ESPCHARGES"
  // P2N: "ALTNAME"

  protected void setFilter(String filter0) {
    if (filter0 != null)
      filter0 = filter0.toUpperCase();
    filter = filter0;
    doSetOrientation = !checkFilter("NOORIENT");
    addVibrations = !checkFilter("NOVIB");
    readMolecularOrbitals = !checkFilter("NOMO");
    useAltNames = checkFilter("ALTNAME");
    if (filter == null)
      return;
    filterAtomType = checkFilter("*.") || checkFilter("!.");
    filterHetero = checkFilter("HETATM"); // PDB
    filterGroup3 = checkFilter("[");
    filterChain = checkFilter(":");
    filterAltLoc = checkFilter("%");
    filterEveryNth = checkFilter("/=");
    if (filterEveryNth)
      filterN = parseInt(filter.substring(filter.indexOf("/=") + 2));
    if (filterN == Integer.MIN_VALUE)
      filterEveryNth = false;
    haveAtomFilter = filterAtomType || filterGroup3 || filterChain
        || filterAltLoc || filterHetero || filterEveryNth || checkFilter("/=");
    if (bsFilter == null) {
      // bsFilter is usually null, but from MDTOP it gets set to indicate
      // which atoms were selected by the filter. This then
      // gets used by COORD files to load just those coordinates
      // and it returns the bitset of filtered atoms
      bsFilter = new BitSet();
      htParams.put("bsFilter", bsFilter);
      filter = (";" + filter + ";").replace(',', ';');
      Logger.info("filtering with " + filter);
      if (haveAtomFilter) {
        int ipt;
        filter1 = filter;
        if ((ipt = filter.indexOf("|")) >= 0) {
          filter1 = filter.substring(0, ipt).trim() + ";";
          filter2 = ";" + filter.substring(ipt).trim();
        }
      }
    }
  }

  private String filter1, filter2;

  public boolean checkFilter(String key) {
    return (filter != null && filter.indexOf(key) >= 0);
  }

  /**
   * @param atom
   * @param iAtom
   * @return        true if we want this atom
   */
  protected boolean filterAtom(Atom atom, int iAtom) {
    if (!haveAtomFilter)
      return true;
    // cif, mdtop, pdb, gromacs, pqr
    boolean isOK = checkFilter(atom, filter1);
    if (filter2 != null)
      isOK |= checkFilter(atom, filter2);
    if (isOK && filterEveryNth)
      isOK = (((nFiltered++) % filterN) == 0);
    bsFilter.set(iAtom >= 0 ? iAtom : atomSetCollection.getAtomCount(), isOK);
    return isOK;
  }

  private boolean checkFilter(Atom atom, String f) {
    return (!filterGroup3 || atom.group3 == null || !filterReject(f, "[",
        atom.group3.toUpperCase() + "]"))
        && (!filterAtomType || atom.atomName == null || !filterReject(f, ".",
            atom.atomName.toUpperCase() + ";"))
        && (!filterChain || atom.chainID == '\0' || !filterReject(f, ":", ""
            + atom.chainID))
        && (!filterAltLoc || atom.alternateLocationID == '\0' || !filterReject(
            f, "%", "" + atom.alternateLocationID))
        && (!filterHetero || !filterReject(f, "HETATM",
            atom.isHetero ? "HETATM" : "ATOM"));
  }

  private boolean filterReject(String f, String code, String atomCode) {
    return (f.indexOf(code) >= 0 && (f.indexOf("!" + code) >= 0 ? f
        .indexOf(atomCode) >= 0 : f.indexOf(atomCode) < 0));
  }

  protected void set2D() {
    // MOL and JME
    atomSetCollection.setAtomSetCollectionAuxiliaryInfo("is2D", Boolean.TRUE);
    if (!checkFilter("NOMIN"))
      atomSetCollection.setAtomSetCollectionAuxiliaryInfo("doMinimize",
          Boolean.TRUE);
  }

  public boolean doGetVibration(int vibrationNumber) {
    // vibrationNumber is 1-based
    return addVibrations
        && (desiredVibrationNumber <= 0 || vibrationNumber == desiredVibrationNumber);
  }

  private Matrix3f matrixRotate;

  public void setTransform(float x1, float y1, float z1, float x2, float y2,
                           float z2, float x3, float y3, float z3) {
    if (matrixRotate != null || !doSetOrientation)
      return;
    matrixRotate = new Matrix3f();
    Vector3f v = new Vector3f();
    // rows in Sygress/CAChe and Spartan become columns here
    v.set(x1, y1, z1);
    v.normalize();
    matrixRotate.setColumn(0, v);
    v.set(x2, y2, z2);
    v.normalize();
    matrixRotate.setColumn(1, v);
    v.set(x3, y3, z3);
    v.normalize();
    matrixRotate.setColumn(2, v);
    atomSetCollection.setAtomSetCollectionAuxiliaryInfo(
        "defaultOrientationMatrix", new Matrix3f(matrixRotate));
    // first two matrix column vectors define quaternion X and XY plane
    Quaternion q = new Quaternion(matrixRotate);
    atomSetCollection.setAtomSetCollectionAuxiliaryInfo(
        "defaultOrientationQuaternion", q);
    Logger.info("defaultOrientationMatrix = " + matrixRotate);

  }

  /////////////////////////////

  public void setAtomCoord(Atom atom, float x, float y, float z) {
    atom.set(x, y, z);
    setAtomCoord(atom);
  }

  public void setAtomCoord(Atom atom) {
    // fileScaling is used by the PLOT command to 
    // put data into PDB format, preserving name/residue information,
    // and still get any xyz data into the allotted column space.
    if (fileScaling != null) {
      atom.x = atom.x * fileScaling.x + fileOffset.x;
      atom.y = atom.y * fileScaling.y + fileOffset.y;
      atom.z = atom.z * fileScaling.z + fileOffset.z;
    }
    if (doConvertToFractional && !fileCoordinatesAreFractional
        && symmetry != null) {
      if (!symmetry.haveUnitCell())
        symmetry.setUnitCell(notionalUnitCell);
      symmetry.toFractional(atom, false);
      iHaveFractionalCoordinates = true;
    }
    //if (Logger.debugging)
    //Logger.debug(" atom "+atom.atomName + " " + atom.x + " " + atom.y+" "+atom.z);
    doCheckUnitCell = true;
  }

  protected void addSites(Map<String, Map<String, Object>> htSites) {
    atomSetCollection.setAtomSetAuxiliaryInfo("pdbSites", htSites);
    String sites = "";
    for (Map.Entry<String, Map<String, Object>> entry : htSites.entrySet()) {
      String name = entry.getKey();
      Map<String, Object> htSite = entry.getValue();
      char ch;
      for (int i = name.length(); --i >= 0;)
        if (!Character.isLetterOrDigit(ch = name.charAt(i)) && ch != '\'')
          name = name.substring(0, i) + "_" + name.substring(i + 1);
      //String seqNum = (String) htSite.get("seqNum");
      String groups = (String) htSite.get("groups");
      if (groups.length() == 0)
        continue;
      addSiteScript("@site_" + name + " " + groups);
      //addJmolScript("@" + seqNum + " " + groups);
      addSiteScript("site_" + name + " = \"" + groups + "\".split(\",\")");
      sites += (sites == "" ? "" : ",") + "site_" + name;
    }
    addSiteScript("site_list = \"" + sites + "\".split(\",\")");
  }

  public void applySymmetryAndSetTrajectory() throws Exception {
    if (iHaveUnitCell && doCheckUnitCell) {
      atomSetCollection.setCoordinatesAreFractional(iHaveFractionalCoordinates);
      atomSetCollection.setNotionalUnitCell(notionalUnitCell,
          matUnitCellOrientation, unitCellOffset);
      atomSetCollection.setAtomSetSpaceGroupName(spaceGroup);
      atomSetCollection.setSymmetryRange(symmetryRange);
      if (doConvertToFractional || fileCoordinatesAreFractional) {
        atomSetCollection.setLatticeCells(latticeCells, applySymmetryToBonds,
            doPackUnitCell, supercell);
        if (ignoreFileSpaceGroupName || !iHaveSymmetryOperators) {
          if (createSpaceGroup()) {
            atomSetCollection.setAtomSetSpaceGroupName(symmetry
                .getSpaceGroupName());
            atomSetCollection.applySymmetry(symmetry);
          }
        } else {
          atomSetCollection.applySymmetry();
        }
      }
      if (iHaveFractionalCoordinates && merging && symmetry != null) {
        // when merging (with appendNew false), we must return cartesians
        atomSetCollection.toCartesian(symmetry);
        atomSetCollection.setCoordinatesAreFractional(false);
        // We no longer allow merging of multiple-model files
        // when the file to be appended has fractional coordinates and vibrations
        addVibrations = false;
      }
    }
    if (isTrajectory)
      atomSetCollection.setTrajectory();
    initializeSymmetry();
  }

  protected boolean createSpaceGroup() {
    return getSymmetry().createSpaceGroup(desiredSpaceGroupIndex,
        (spaceGroup.indexOf("!") >= 0 ? "P1" : spaceGroup), notionalUnitCell);
  }

  @SuppressWarnings("unchecked")
  public void setMOData(Map<String, Object> moData) {
    atomSetCollection.setAtomSetAuxiliaryInfo("moData", moData);
    if (moData == null)
      return;
    List<Map<String, Object>> orbitals = (List<Map<String, Object>>) moData
        .get("mos");
    if (orbitals != null)
      Logger.info(orbitals.size() + " molecular orbitals read in model "
          + atomSetCollection.getAtomSetCount());
  }

  public static String getElementSymbol(int elementNumber) {
    return JmolAdapter.getElementSymbol(elementNumber);
  }

  /**
   * fills an array with a predefined number of lines of data that is 
   * arranged in fixed FORTRAN-like column format
   *   
   * @param data
   * @param col0
   * @param colWidth
   * @throws Exception
   */
  protected void fillDataBlock(String[][] data, int col0, int colWidth)
      throws Exception {
    if (colWidth == 0) {
      fillDataBlock(data);
      return;
    }
    int nLines = data.length;
    for (int i = 0; i < nLines; i++) {
      discardLinesUntilNonBlank();
      int nFields = (line.length() - col0) / colWidth;
      data[i] = new String[nFields];
      for (int j = 0, start = col0; j < nFields; j++, start += colWidth)
        data[i][j] = line.substring(start, start + colWidth);
    }
  }

  /**
   * fills an array with a pre-defined number of lines of token data,
   * skipping blank lines in the process
   * 
   * @param data
   * @throws Exception
   */
  protected void fillDataBlock(String[][] data) throws Exception {
    int nLines = data.length;
    for (int i = 0; i < nLines; i++)
      data[i] = getTokens(discardLinesUntilNonBlank());
  }

  /**
   * fills a float array with string data from a file
   * @param temp
   * @param line0 TODO
   * @param width TODO
   * @throws Exception
   */
  protected void fillFloatArray(float[] temp, String line0, int width)
      throws Exception {
    String[] tokens = new String[0];
    int pt = 0;
    for (int i = 0; i < temp.length; i++) {
      while (tokens != null && pt >= tokens.length) {
        if (line0 == null)
          line0 = readLine();
        if (width == 0) {
          tokens = getTokens(line0);
        } else {
          tokens = new String[line0.length() / width];
          for (int j = 0; j < tokens.length; j++)
            tokens[j] = line0.substring(j * width, (j + 1) * width);
        }
        line0 = null;
        pt = 0;
      }
      if (tokens == null)
        break;
      temp[i] = parseFloat(tokens[pt++]);
    }
  }

  /**
   * Extracts a block of frequency data from a file. This block may be of two
   * types -- either X Y Z across a row or each of X Y Z on a separate line.
   * Data is presumed to be in fixed FORTRAN-like column format, not
   * space-separated columns.
   * 
   * @param iAtom0
   *          the first atom to be assigned a frequency
   * @param atomCount
   *          the number of atoms to be assigned
   * @param modelAtomCount
   *          the number of atoms in each model
   * @param ignore
   *          the frequencies to ignore because the user has selected only
   *          certain vibrations to be read or for whatever reason; length
   *          serves to set the number of frequencies to be read
   * @param isWide
   *          when TRUE, this is a table that has X Y Z for each mode within the
   *          same row; when FALSE, this is a table that has X Y Z for each mode
   *          on a separate line.
   * @param col0
   *          the column in which data starts
   * @param colWidth
   *          the width of the data columns
   * @param atomIndexes
   *          an array either null or indicating exactly which atoms get the
   *          frequencies (used by CrystalReader)
   * @throws Exception
   */
  protected void fillFrequencyData(int iAtom0, int atomCount,
                                   int modelAtomCount, boolean[] ignore,
                                   boolean isWide, int col0, int colWidth,
                                   int[] atomIndexes) throws Exception {
    boolean withSymmetry = (modelAtomCount != atomCount);
    if (atomIndexes != null)
      atomCount = atomIndexes.length;
    int nLines = (isWide ? atomCount : atomCount * 3);
    int nFreq = ignore.length;
    String[][] data = new String[nLines][];
    fillDataBlock(data, col0, colWidth);
    for (int i = 0, atomPt = 0; i < nLines; i++, atomPt++) {
      String[] values = data[i];
      String[] valuesY = (isWide ? null : data[++i]);
      String[] valuesZ = (isWide ? null : data[++i]);
      int dataPt = values.length - (isWide ? nFreq * 3 : nFreq) - 1;
      for (int j = 0; j < nFreq; j++) {
        ++dataPt;
        String x = values[dataPt];
        if (x.charAt(0) == ')') // AMPAC reader!
          x = x.substring(1);
        float vx = parseFloat(x);
        float vy = parseFloat(isWide ? values[++dataPt] : valuesY[dataPt]);
        float vz = parseFloat(isWide ? values[++dataPt] : valuesZ[dataPt]);
        if (ignore[j])
          continue;
        int iAtom = (atomIndexes == null ? atomPt : atomIndexes[atomPt]);
        if (iAtom < 0)
          continue;
        if (Logger.debugging)
          Logger.debug("vib " + iAtom + "/" + j + ": " + vx + " " + vy + " "
              + vz);
        atomSetCollection.addVibrationVector(iAtom0 + modelAtomCount * j
            + iAtom, vx, vy, vz, withSymmetry);
      }
    }
  }

  protected void discardLines(int nLines) throws Exception {
    for (int i = nLines; --i >= 0;)
      readLine();
  }

  protected String discardLinesUntilStartsWith(String startsWith)
      throws Exception {
    while (readLine() != null && !line.startsWith(startsWith)) {
    }
    return line;
  }

  protected String discardLinesUntilContains(String containsMatch)
      throws Exception {
    while (readLine() != null && line.indexOf(containsMatch) < 0) {
    }
    return line;
  }

  protected String discardLinesUntilContains(String s1, String s2)
      throws Exception {
    while (readLine() != null && line.indexOf(s1) < 0 && line.indexOf(s2) < 0) {
    }
    return line;
  }

  protected void discardLinesUntilBlank() throws Exception {
    while (readLine() != null && line.trim().length() != 0) {
    }
  }

  protected String discardLinesUntilNonBlank() throws Exception {
    while (readLine() != null && line.trim().length() == 0) {
    }
    return line;
  }

  protected void checkLineForScript(String line) {
    this.line = line;
    checkLineForScript();
  }

  public void checkLineForScript() {
    if (line.indexOf("Jmol") >= 0) {
      if (line.indexOf("Jmol PDB-encoded data") >= 0) {
        atomSetCollection.setAtomSetCollectionAuxiliaryInfo("jmolData", line);
        if (!line.endsWith("#noautobond"))
          line += "#noautobond";
      }
      if (line.indexOf("Jmol data min") >= 0) {
        Logger.info(line);
        // The idea here is to use a line such as the following:
        //
        // REMARK   6 Jmol data min = {-1 -1 -1} max = {1 1 1} 
        //                      unScaledXyz = xyz / {10 10 10} + {0 0 0} 
        //                      plotScale = {100 100 100}
        //
        // to pass on to Jmol how to graph non-molecular data. 
        // The format allows for the actual data to be linearly transformed
        // so that it fits into the PDB format for x, y, and z coordinates.
        // This adapter will then unscale the data and also pass on to
        // Jmol the unit cell equivalent that takes the actual data (which
        // will be considered the fractional coordinates) to Jmol coordinates,
        // which will be a cube centered at {0 0 0} and ranging from {-100 -100 -100}
        // to {100 100 100}.
        //
        // Jmol 12.0.RC23 uses this to pass through the adapter a quaternion,
        // ramachandran, or other sort of plot.

        float[] data = new float[15];
        parseStringInfestedFloatArray(line.substring(10).replace('=', ' ')
            .replace('{', ' ').replace('}', ' '), data);
        Point3f minXYZ = new Point3f(data[0], data[1], data[2]);
        Point3f maxXYZ = new Point3f(data[3], data[4], data[5]);
        fileScaling = new Point3f(data[6], data[7], data[8]);
        fileOffset = new Point3f(data[9], data[10], data[11]);
        Point3f plotScale = new Point3f(data[12], data[13], data[14]);
        if (plotScale.x <= 0)
          plotScale.x = 100;
        if (plotScale.y <= 0)
          plotScale.y = 100;
        if (plotScale.z <= 0)
          plotScale.z = 100;
        if (fileScaling.y == 0)
          fileScaling.y = 1;
        if (fileScaling.z == 0)
          fileScaling.z = 1;
        setFractionalCoordinates(true);
        latticeCells = new int[3];
        atomSetCollection.setLatticeCells(latticeCells, true, false, supercell);
        setUnitCell(plotScale.x * 2 / (maxXYZ.x - minXYZ.x), plotScale.y * 2
            / (maxXYZ.y - minXYZ.y), plotScale.z * 2
            / (maxXYZ.z == minXYZ.z ? 1 : maxXYZ.z - minXYZ.z), 90, 90, 90);
        /*
        unitCellOffset = new Point3f(minXYZ);
        symmetry.toCartesian(unitCellOffset);
        System.out.println(unitCellOffset);
        unitCellOffset = new Point3f(maxXYZ);
        symmetry.toCartesian(unitCellOffset);
        System.out.println(unitCellOffset);
        */
        unitCellOffset = new Point3f(plotScale);
        unitCellOffset.scale(-1);
        symmetry.toFractional(unitCellOffset, false);
        unitCellOffset.scaleAdd(-1f, minXYZ, unitCellOffset);
        symmetry.setUnitCellOffset(unitCellOffset);
        /*
        Point3f pt = new Point3f(minXYZ);
        symmetry.toCartesian(pt);
        System.out.println("ASCR minXYZ " + pt);
        pt.set(maxXYZ);
        symmetry.toCartesian(pt);
        System.out.println("ASCR maxXYZ " + pt);
        */
        atomSetCollection.setAtomSetCollectionAuxiliaryInfo("jmolDataScaling",
            new Point3f[] { minXYZ, maxXYZ, plotScale });
      }
    }
    if (line.endsWith("#noautobond")) {
      line = line.substring(0, line.lastIndexOf('#')).trim();
      atomSetCollection.setNoAutoBond();
    }
    int pt = line.indexOf("jmolscript:");
    if (pt >= 0) {
      String script = line.substring(pt + 11, line.length());
      if (script.indexOf("#") >= 0) {
        script = script.substring(0, script.indexOf("#"));
      }
      addJmolScript(script);
      line = line.substring(0, pt).trim();
    }
  }

  private String previousScript;

  protected void addJmolScript(String script) {
    Logger.info("#jmolScript: " + script);
    if (previousScript == null)
      previousScript = "";
    else if (!previousScript.endsWith(";"))
      previousScript += ";";
    previousScript += script;
    atomSetCollection.setAtomSetCollectionAuxiliaryInfo("jmolscript",
        previousScript);
  }

  private String siteScript;

  protected void addSiteScript(String script) {
    if (siteScript == null)
      siteScript = "";
    else if (!siteScript.endsWith(";"))
      siteScript += ";";
    siteScript += script;
    atomSetCollection.setAtomSetCollectionAuxiliaryInfo("sitescript",
        siteScript);
  }

  public String readLine() throws Exception {
    prevline = line;
    line = reader.readLine();
    if (os != null && line != null) {
      os.write(line.getBytes());
      os.write('\n');
    }
    ptLine++;
    if (Logger.debugging)
      Logger.debug(line);
    //System.out.println("readLine " + ptLine + " " + line);
    return line;
  }

  final static protected String[] getStrings(String sinfo, int nFields,
                                             int width) {
    String[] fields = new String[nFields];
    for (int i = 0, pt = 0; i < nFields; i++, pt += width)
      fields[i] = sinfo.substring(pt, pt + width);
    return fields;
  }

  // parser functions are static, so they need notstatic counterparts

  protected void parseStringInfestedFloatArray(String s, float[] data) {
    Parser.parseStringInfestedFloatArray(s, null, data);
  }

  protected String[] getTokens() {
    return Parser.getTokens(line);
  }

  protected static void getTokensFloat(String s, float[] f, int n) {
    Parser.parseFloatArray(getTokens(s), f, n);
  }

  public static String[] getTokens(String s) {
    return Parser.getTokens(s);
  }

  protected static String[] getTokens(String s, int iStart) {
    return Parser.getTokens(s, iStart);
  }

  protected float parseFloat() {
    return Parser.parseFloat(line, next);
  }

  public float parseFloat(String s) {
    next[0] = 0;
    return Parser.parseFloat(s, next);
  }

  protected float parseFloat(String s, int iStart, int iEnd) {
    next[0] = iStart;
    return Parser.parseFloat(s, iEnd, next);
  }

  protected int parseInt() {
    return Parser.parseInt(line, next);
  }

  public int parseInt(String s) {
    next[0] = 0;
    return Parser.parseInt(s, next);
  }

  protected int parseInt(String s, int iStart) {
    next[0] = iStart;
    return Parser.parseInt(s, next);
  }

  protected int parseInt(String s, int iStart, int iEnd) {
    next[0] = iStart;
    return Parser.parseInt(s, iEnd, next);
  }

  protected String parseToken() {
    return Parser.parseToken(line, next);
  }

  protected String parseToken(String s) {
    next[0] = 0;
    return Parser.parseToken(s, next);
  }

  protected String parseTokenNext(String s) {
    return Parser.parseToken(s, next);
  }

  protected String parseToken(String s, int iStart, int iEnd) {
    next[0] = iStart;
    return Parser.parseToken(s, iEnd, next);
  }

  protected static String parseTrimmed(String s, int iStart) {
    return Parser.parseTrimmed(s, iStart);
  }

  protected static String parseTrimmed(String s, int iStart, int iEnd) {
    return Parser.parseTrimmed(s, iStart, iEnd);
  }

  /**
   * get all integers after letters
   * negative entries are spaces (1Xn)
   * 
   * @param s
   * @return Vector of integers
   */
  protected static ArrayList<Integer> getFortranFormatLengths(String s) {
    ArrayList<Integer> vdata = new ArrayList<Integer>();
    int n = 0;
    int c = 0;
    int factor = 1;
    boolean inN = false;
    boolean inCount = true;
    s += ",";
    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);
      switch (ch) {
      case '.':
        inN = false;
        continue;
      case ',':
        for (int j = 0; j < c; j++)
          vdata.add(Integer.valueOf(n * factor));
        inN = false;
        inCount = true;
        c = 0;
        continue;
      case 'X':
        n = c;
        c = 1;
        factor = -1;
        continue;

      }
      boolean isDigit = Character.isDigit(ch);
      if (isDigit) {
        if (inN)
          n = n * 10 + ch - '0';
        else if (inCount)
          c = c * 10 + ch - '0';
      } else if (Character.isLetter(ch)) {
        n = 0;
        inN = true;
        inCount = false;
        factor = 1;
      } else {
        inN = false;
      }
    }
    return vdata;
  }
  /*
    static {
      System.out.println(Escape.toJSON(null, getFortranFormatLengths("(5A10")));    
      System.out.println(Escape.toJSON(null, getFortranFormatLengths("(5E10.3,2A4.6")));    
      System.out.println(Escape.toJSON(null, getFortranFormatLengths("(5A4,2X,2I20")));
    }
  */

}
