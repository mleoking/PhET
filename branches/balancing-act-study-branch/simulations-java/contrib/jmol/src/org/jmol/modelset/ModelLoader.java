/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-05-18 08:19:45 -0500 (Fri, 18 May 2007) $
 * $Revision: 7742 $

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

import org.jmol.util.BitSetUtil;
import org.jmol.util.ArrayUtil;
import org.jmol.util.Elements;
import org.jmol.util.Logger;
import org.jmol.util.Quaternion;
import org.jmol.util.TextFormat;
import org.jmol.viewer.JmolConstants;
import org.jmol.script.Token;
import org.jmol.viewer.Viewer;

import org.jmol.api.Interface;
import org.jmol.api.JmolAdapter;
import org.jmol.api.JmolBioResolver;
import org.jmol.api.JmolEdge;
import org.jmol.api.JmolMolecule;
import org.jmol.api.SymmetryInterface;
import org.jmol.atomdata.RadiusData;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/* 
 * 
 * This subclass contains only the private methods 
 * used to load a model. Methods exclusively after 
 * file loading are included only in the superclass, ModelSet,
 * and its superclasses, ModelCollection, BondCollection, and AtomCollection.
 * 
 * Bob Hanson, 5/2007
 *  
 */

public final class ModelLoader extends ModelSet {

  //public void finalize() {
  //  System.out.println("ModelLoader " + this + " finalized");
  //}
  

  
  private ModelLoader mergeModelSet;
  private boolean merging;
  private String jmolData; // from a PDB remark "Jmol PDB-encoded data"

  private final int[] specialAtomIndexes = new int[JmolConstants.ATOMID_MAX];
  private String[] group3Lists;
  private int[][] group3Counts;
  
  public ModelLoader(Viewer viewer, String name) {
    this.viewer = viewer;
    viewer.resetShapes();
    preserveState = viewer.getPreserveState();
    initializeInfo(name, null);
    createModelSet(null, null, null);
    modelSetName = name;
    viewer.setStringProperty("_fileType", "");
  }

  public ModelLoader(Viewer viewer, StringBuffer loadScript,
      Object atomSetCollection, ModelLoader mergeModelSet, String modelSetName,
      BitSet bsNew) {
    this.viewer = viewer;
    JmolAdapter adapter = viewer.getModelAdapter();
    this.modelSetName = modelSetName;
    this.mergeModelSet = mergeModelSet;
    merging = (mergeModelSet != null && mergeModelSet.atomCount > 0);
    if (!merging) {
      viewer.resetShapes();
    }
    preserveState = viewer.getPreserveState();

    Map<String, Object> info = adapter.getAtomSetCollectionAuxiliaryInfo(atomSetCollection);
    info.put("loadScript", loadScript);
    initializeInfo(adapter.getFileTypeName(atomSetCollection).toLowerCase().intern(), info);
    createModelSet(adapter, atomSetCollection, bsNew);
    // dumpAtomSetNameDiagnostics(adapter, atomSetCollection);
  }
/*
  private void dumpAtomSetNameDiagnostics(JmolAdapter adapter, Object atomSetCollection) {
    int frameModelCount = modelCount;
    int adapterAtomSetCount = adapter.getAtomSetCount(atomSetCollection);
    if (Logger.debugging) {
      Logger.debug(
          "----------------\n" + "debugging of AtomSetName stuff\n" +
          "\nframeModelCount=" + frameModelCount +
          "\nadapterAtomSetCount=" + adapterAtomSetCount + "\n -- \n");
      for (int i = 0; i < adapterAtomSetCount; ++i) {
        Logger.debug(
            "atomSetName[" + i + "]=" + adapter.getAtomSetName(atomSetCollection, i) +
            " atomSetNumber[" + i + "]=" + adapter.getAtomSetNumber(atomSetCollection, i));
      }
    }
  }
*/

  private boolean someModelsHaveUnitcells;
  private boolean isTrajectory;
  private boolean doMinimize;
  private String fileHeader;

  @SuppressWarnings("unchecked")
  private void initializeInfo(String name, Map<String, Object> info) {
    g3d = viewer.getGraphics3D();
    //long timeBegin = System.currentTimeMillis();
    modelSetTypeName = name;
    isXYZ = (modelSetTypeName == "xyz");
    modelSetAuxiliaryInfo = info;
    modelSetProperties = (Properties) getModelSetAuxiliaryInfo("properties");
    //isMultiFile = getModelSetAuxiliaryInfoBoolean("isMultiFile"); -- no longer necessary
    isPDB = getModelSetAuxiliaryInfoBoolean("isPDB");
    jmolData = (String) getModelSetAuxiliaryInfo("jmolData");
    fileHeader = (String) getModelSetAuxiliaryInfo("fileHeader");
    trajectorySteps = (List<Point3f[]>) getModelSetAuxiliaryInfo("trajectorySteps");
    isTrajectory = (trajectorySteps != null);
    if (isTrajectory)
      info.remove("trajectorySteps");
    noAutoBond = getModelSetAuxiliaryInfoBoolean("noAutoBond");
    is2D = getModelSetAuxiliaryInfoBoolean("is2D");
    doMinimize = is2D && getModelSetAuxiliaryInfoBoolean("doMinimize");
    adapterTrajectoryCount = (trajectorySteps == null ? 0 : trajectorySteps.size()); 
    someModelsHaveSymmetry = getModelSetAuxiliaryInfoBoolean("someModelsHaveSymmetry");
    someModelsHaveUnitcells = getModelSetAuxiliaryInfoBoolean("someModelsHaveUnitcells");
    someModelsHaveFractionalCoordinates = getModelSetAuxiliaryInfoBoolean("someModelsHaveFractionalCoordinates");
    if (merging) {
      someModelsHaveSymmetry |= mergeModelSet.getModelSetAuxiliaryInfoBoolean("someModelsHaveSymmetry");
      someModelsHaveUnitcells |= mergeModelSet.getModelSetAuxiliaryInfoBoolean("someModelsHaveUnitcells");
      someModelsHaveFractionalCoordinates |= mergeModelSet.getModelSetAuxiliaryInfoBoolean("someModelsHaveFractionalCoordinates");
      someModelsHaveAromaticBonds |= mergeModelSet.someModelsHaveAromaticBonds;
      modelSetAuxiliaryInfo.put("someModelsHaveSymmetry", Boolean.valueOf(someModelsHaveSymmetry));
      modelSetAuxiliaryInfo.put("someModelsHaveUnitcells", Boolean.valueOf(someModelsHaveUnitcells));
      modelSetAuxiliaryInfo.put("someModelsHaveFractionalCoordinates", Boolean.valueOf(someModelsHaveFractionalCoordinates));
      modelSetAuxiliaryInfo.put("someModelsHaveAromaticBonds", Boolean.valueOf(someModelsHaveAromaticBonds));
    }
  }

  private final Map<Object, Atom> htAtomMap = new Hashtable<Object, Atom>();

  private final static int defaultGroupCount = 32;
  private Chain[] chainOf;
  private String[] group3Of;
  private int[] seqcodes;
  private int[] firstAtomIndexes;

  private int currentModelIndex;
  private Model currentModel;
  private char currentChainID;
  private Chain currentChain;
  private int currentGroupSequenceNumber;
  private char currentGroupInsertionCode;
  private String currentGroup3;

  private Group nullGroup; // used in Atom

  private int baseModelIndex = 0;
  private int baseModelCount = 0;
  private int baseAtomIndex = 0;
  private int baseGroupIndex = 0;

  private int baseTrajectoryCount = 0;
  private boolean appendNew;
  private int adapterModelCount = 0;
  private int adapterTrajectoryCount = 0;
  private boolean noAutoBond;
  private boolean is2D;
  
  private void createModelSet(JmolAdapter adapter, Object atomSetCollection,
                              BitSet bsNew) {
    int nAtoms = (adapter == null ? 0 : adapter.getAtomCount(atomSetCollection));
    if (nAtoms > 0)
      Logger.info("reading " + nAtoms + " atoms");
    adapterModelCount = (adapter == null ? 1 : adapter
        .getAtomSetCount(atomSetCollection));
    // cannot append a trajectory into a previous model
    appendNew = (!merging || adapter == null || adapterModelCount > 1
        || isTrajectory || viewer.getAppendNew());
    htAtomMap.clear();
    chainOf = new Chain[defaultGroupCount];
    group3Of = new String[defaultGroupCount];
    seqcodes = new int[defaultGroupCount];
    firstAtomIndexes = new int[defaultGroupCount];
    currentChainID = '\uFFFF';
    currentChain = null;
    currentGroupInsertionCode = '\uFFFF';
    currentGroup3 = "xxxxx";
    currentModelIndex = -1;
    currentModel = null;
    if (merging) {
      baseModelCount = mergeModelSet.modelCount;
      baseTrajectoryCount = mergeModelSet.getTrajectoryCount();
      if (baseTrajectoryCount > 0) {
        if (isTrajectory) {
          for (int i = 0; i < trajectorySteps.size(); i++) {
            mergeModelSet.trajectorySteps.add(trajectorySteps.get(i));
          }
        }
        trajectorySteps = mergeModelSet.trajectorySteps;
      }
    }
    initializeAtomBondModelCounts(nAtoms);
    if (bsNew != null && doMinimize) {
      bsNew.set(baseAtomIndex, baseAtomIndex + nAtoms);
    }
    if (adapter == null) {
      setModelNameNumberProperties(0, -1, "", 1, null, null, null);
    } else {
      if (adapterModelCount > 0) {
        Logger.info("ModelSet: haveSymmetry:" + someModelsHaveSymmetry
            + " haveUnitcells:" + someModelsHaveUnitcells
            + " haveFractionalCoord:" + someModelsHaveFractionalCoordinates);
        Logger.info(adapterModelCount + " model" + (modelCount == 1 ? "" : "s")
            + " in this collection. Use getProperty \"modelInfo\" or"
            + " getProperty \"auxiliaryInfo\" to inspect them.");
      }
      Quaternion q = (Quaternion) getModelSetAuxiliaryInfo("defaultOrientationQuaternion");
      if (q != null) {
        Logger.info("defaultOrientationQuaternion = " + q);
        Logger
            .info("Use \"set autoLoadOrientation TRUE\" before loading or \"restore orientation DEFAULT\" after loading to view this orientation.");
      }
      iterateOverAllNewModels(adapter, atomSetCollection);
      iterateOverAllNewAtoms(adapter, atomSetCollection);
      iterateOverAllNewBonds(adapter, atomSetCollection);
      if (adapter != null && merging && !appendNew) {
        Map<String, Object> info = adapter.getAtomSetAuxiliaryInfo(
            atomSetCollection, 0);
        setModelAuxiliaryInfo(baseModelIndex, "initialAtomCount", info
            .get("initialAtomCount"));
        setModelAuxiliaryInfo(baseModelIndex, "initialBondCount", info
            .get("initialBondCount"));
      }
      initializeUnitCellAndSymmetry();
      initializeBonding();
      setAtomProperties();
    }

    finalizeGroupBuild(); // set group offsets and build monomers

    if (adapter != null) {
      calculatePolymers(baseGroupIndex, null);
      iterateOverAllNewStructures(adapter, atomSetCollection);
      adapter.finish(atomSetCollection);
    }

    // only now can we access all of the atom's properties

    RadiusData rd = viewer.getDefaultRadiusData();
    for (int i = baseAtomIndex; i < atomCount; i++)
      atoms[i].setMadAtom(viewer, rd);
    for (int i = models[baseModelIndex].firstAtomIndex; i < atomCount; i++)
      models[atoms[i].modelIndex].bsAtoms.set(i);
    setDefaultRendering(viewer.getSmallMoleculeMaxAtoms());
    freeze();
    calcBoundBoxDimensions(null, 1);

    if (is2D) {
      applyStereochemistry();
    }

    finalizeShapes();
    if (mergeModelSet != null) {
      mergeModelSet.releaseModelSet();
    }
    mergeModelSet = null;
  }

  private void setDefaultRendering(int maxAtoms) {
    StringBuffer sb = new StringBuffer();
    for (int i = baseModelIndex; i < modelCount; i++)
      if (models[i].isPDB)
        models[i].getDefaultLargePDBRendering(sb, maxAtoms);
    if (sb.length() == 0)
      return;
    sb.append("select *;");
    String script = (String) getModelSetAuxiliaryInfo("jmolscript");
    if (script == null)
      script = "";
    sb.append(script);
    modelSetAuxiliaryInfo.put("jmolscript", sb.toString());
  }

  @SuppressWarnings("unchecked")
  private void setAtomProperties() {
    int atomIndex = baseAtomIndex;
    int modelAtomCount = 0;
    for (int i = baseModelIndex; i < modelCount; atomIndex += modelAtomCount, i++) {
      modelAtomCount = models[i].bsAtoms.cardinality();
      Map<String, String> atomProperties = (Map<String, String>) getModelAuxiliaryInfo(i,
          "atomProperties");
      if (atomProperties == null)
        continue;
      for (Map.Entry<String, String> entry : atomProperties.entrySet()) {
        String key = entry.getKey();
        String value = entry.getValue();
        // no deletions yet...
        BitSet bs = getModelAtomBitSetIncludingDeleted(i, true);
        key = "property_" + key.toLowerCase();
        Logger.info("creating " + key + " for model " + getModelName(i));
        viewer.setData(key, new Object[] { key, value, bs }, atomCount, 0,
            0, Integer.MAX_VALUE, 0);
      }
    }
  }


  @Override
  protected void releaseModelSet() {
    group3Lists = null;
    group3Counts = null;
    groups = null;
    super.releaseModelSet();
  }

  private void initializeAtomBondModelCounts(int nAtoms) {
    int trajectoryCount = adapterTrajectoryCount;
    if (merging) {
      if (appendNew) {
        baseModelIndex = baseModelCount;
        modelCount = baseModelCount + adapterModelCount;
      } else {
        baseModelIndex = viewer.getCurrentModelIndex();
        if (baseModelIndex < 0)
          baseModelIndex = baseModelCount - 1;
        modelCount = baseModelCount;
      }
      atomCount = baseAtomIndex = mergeModelSet.atomCount;
      bondCount = mergeModelSet.bondCount;
      groupCount = baseGroupIndex = mergeModelSet.groupCount;
      mergeModelArrays(mergeModelSet);
      growAtomArrays(atomCount + nAtoms);
    } else {
      modelCount = adapterModelCount;
      atomCount = 0;
      bondCount = 0;
      atoms = new Atom[nAtoms];
      bonds = new Bond[250 + nAtoms]; // was "2 *" -- WAY overkill.
    }
    if (trajectoryCount > 1)
      modelCount += trajectoryCount - 1;
    models = (Model[]) ArrayUtil.setLength(models, modelCount);
    modelFileNumbers = ArrayUtil.setLength(modelFileNumbers, modelCount);
    modelNumbers = ArrayUtil.setLength(modelNumbers, modelCount);
    modelNumbersForAtomLabel = ArrayUtil.setLength(modelNumbersForAtomLabel, modelCount);
    modelNames = ArrayUtil.setLength(modelNames, modelCount);
    frameTitles = ArrayUtil.setLength(frameTitles, modelCount);
    if (merging)
      mergeModels(mergeModelSet);
  }

  private void mergeGroups() {
    Map<String, Object> info = mergeModelSet.getAuxiliaryInfo(null);
    String[] mergeGroup3Lists = (String[]) info.get("group3Lists");
    int[][] mergeGroup3Counts = (int[][]) info.get("group3Counts");
    if (mergeGroup3Lists != null) {
      for (int i = 0; i < baseModelCount; i++) {
        group3Lists[i + 1] = mergeGroup3Lists[i + 1];
        group3Counts[i + 1] = mergeGroup3Counts[i + 1];
        structuresDefinedInFile.set(i);
      }
      group3Lists[0] = mergeGroup3Lists[0];
      group3Counts[0] = mergeGroup3Counts[0];
    }
    //if merging PDB data into an already-present model, and the 
    //structure is defined, consider the current structures in that 
    //model to be undefined. Not guarantee to work.
    if (!appendNew && isPDB) 
      structuresDefinedInFile.clear(baseModelIndex);
  }

  private void iterateOverAllNewModels(JmolAdapter adapter, Object atomSetCollection) {

    // set private values

    if (modelCount > 0) {
      nullGroup = new Group(new Chain(this, models[baseModelIndex], ' '), "",
          0, -1, -1);
    }

    group3Lists = new String[modelCount + 1];
    group3Counts = new int[modelCount + 1][];

    structuresDefinedInFile = new BitSet();

    if (merging)
      mergeGroups();

    int iTrajectory = (isTrajectory ? baseTrajectoryCount : -1);
    int ipt = baseModelIndex;
    for (int i = 0; i < adapterModelCount; ++i, ++ipt) {
      int modelNumber = (appendNew ? adapter.getAtomSetNumber(atomSetCollection, i)
          : Integer.MAX_VALUE);
      String modelName = adapter.getAtomSetName(atomSetCollection, i);
      Map<String, Object> modelAuxiliaryInfo = adapter.getAtomSetAuxiliaryInfo(
          atomSetCollection, i);
      Properties modelProperties = (Properties) modelAuxiliaryInfo.get("modelProperties");
      viewer.setStringProperty("_fileType", (String) modelAuxiliaryInfo
          .get("fileType"));
      if (modelName == null)
        modelName = (jmolData != null && jmolData.indexOf(";") > 2 ? jmolData.substring(jmolData
            .indexOf(":") + 2, jmolData.indexOf(";"))
            : modelNumber == Integer.MAX_VALUE ? "" : ""
                + (modelNumber % 1000000));
      boolean isPDBModel = setModelNameNumberProperties(ipt, iTrajectory,
          modelName, modelNumber, modelProperties, modelAuxiliaryInfo,
          jmolData);
      if (isPDBModel) {
        group3Lists[ipt + 1] = JmolConstants.group3List;
        group3Counts[ipt + 1] = new int[JmolConstants.group3Count + 10];
        if (group3Lists[0] == null) {
          group3Lists[0] = JmolConstants.group3List;
          group3Counts[0] = new int[JmolConstants.group3Count + 10];
        }
      }
      if (getModelAuxiliaryInfo(ipt, "periodicOriginXyz") != null)
        someModelsHaveSymmetry = true;
    }
    Model m = models[baseModelIndex];
    viewer.setSmilesString((String) modelSetAuxiliaryInfo.get("smilesString"));
    String loadState = (String) modelSetAuxiliaryInfo.remove("loadState");
    StringBuffer loadScript = (StringBuffer)modelSetAuxiliaryInfo.remove("loadScript");
    if (loadScript.indexOf("Viewer.AddHydrogens") < 0 || !m.isModelKit) {
      String[] lines = TextFormat.split(loadState, '\n');
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < lines.length; i++) {
        int pt = m.loadState.indexOf(lines[i]);
        if (pt < 0 || pt != m.loadState.lastIndexOf(lines[i]))
          sb.append(lines[i]).append('\n');
      }
      m.loadState += m.loadScript.toString() + sb.toString();
      m.loadScript = new StringBuffer();
      m.loadScript.append("  ").append(loadScript).append(";\n");
      
    }
    if (isTrajectory) {
      // fill in the rest of the data
      int n = (modelCount - ipt + 1);
      Logger.info(n + " trajectory steps read");
      setModelAuxiliaryInfo(baseModelCount, "trajectoryStepCount", Integer.valueOf(n));
      for (int ia = adapterModelCount, i = ipt; i < modelCount; i++) {
        models[i] = models[baseModelCount];
        modelNumbers[i] = adapter.getAtomSetNumber(atomSetCollection, ia++);
        structuresDefinedInFile.set(i);
      }
    }
    finalizeModels(baseModelCount);
  }
    
  private boolean setModelNameNumberProperties(
                                               int modelIndex,
                                               int trajectoryBaseIndex,
                                               String modelName,
                                               int modelNumber,
                                               Properties modelProperties,
                                               Map<String, Object> modelAuxiliaryInfo,
                                               String jmolData) {
    if (modelNumber != Integer.MAX_VALUE) {
      models[modelIndex] = new Model(this, modelIndex, trajectoryBaseIndex,
          jmolData, modelProperties, modelAuxiliaryInfo);
      modelNumbers[modelIndex] = modelNumber;
      modelNames[modelIndex] = modelName;
    }
    // this next sets the bitset length to avoid 
    // unnecessary calls to System.arrayCopy
    models[modelIndex].bsAtoms.set(atoms.length + 1);
    models[modelIndex].bsAtoms.clear(atoms.length + 1);
    String codes = (String) getModelAuxiliaryInfo(modelIndex, "altLocs");
    models[modelIndex].setNAltLocs(codes == null ? 0 : codes.length());
    if (codes != null) {
      char[] altlocs = codes.toCharArray();
      Arrays.sort(altlocs);
      codes = String.valueOf(altlocs);
      setModelAuxiliaryInfo(modelIndex, "altLocs", codes);
    }
    codes = (String) getModelAuxiliaryInfo(modelIndex, "insertionCodes");
    models[modelIndex].setNInsertions(codes == null ? 0 : codes.length());
    boolean isModelKit = (modelSetName != null
        && modelSetName.startsWith("Jmol Model Kit")
        || modelName.startsWith("Jmol Model Kit") || "Jme"
        .equals(getModelAuxiliaryInfo(modelIndex, "fileType")));
    models[modelIndex].isModelKit = isModelKit;
    return models[modelIndex].isPDB = getModelAuxiliaryInfoBoolean(modelIndex,
        "isPDB");
  }

  /**
   * Model numbers are considerably more complicated in Jmol 11.
   * 
   * int modelNumber
   *  
   *   The adapter gives us a modelNumber, but that is not necessarily
   *   what the user accesses. If a single files is loaded this is:
   *   
   *   a) single file context:
   *   
   *     1) the sequential number of the model in the file , or
   *     2) if a PDB file and "MODEL" record is present, that model number
   *     
   *   b) multifile context:
   *   
   *     always 1000000 * (fileIndex + 1) + (modelIndexInFile + 1)
   *   
   *   
   * int fileIndex
   * 
   *   The 0-based reference to the file containing this model. Used
   *   when doing   "_modelnumber3.2" in a multifile context
   *   
   * int modelFileNumber
   * 
   *   An integer coding both the file and the model:
   *   
   *     file * 1000000 + modelInFile (1-based)
   *     
   *   Used all over the place. Note that if there is only one file,
   *   then modelFileNumber < 1000000.
   * 
   * String modelNumberDotted
   *   
   *   A number the user can use "1.3"
   *   
   * String modelNumberForAtomLabel
   * 
   *   Either the dotted number or the PDB MODEL number, if there is only one file
   *   
   * @param baseModelCount
   *    
   */
  private void finalizeModels(int baseModelCount) {
    if (modelCount == baseModelCount)
      return;
    String sNum;
    int modelnumber = 0;
    int lastfilenumber = -1;
    if (isTrajectory)
      for (int i = baseModelCount; ++i < modelCount;)
        modelNumbers[i] = modelNumbers[i - 1] + 1;
    if (baseModelCount > 0) {
      // load append
      if (modelNumbers[0] < 1000000) {
        // initially we had just one file
        for (int i = 0; i < baseModelCount; i++) {
          // create 1000000 model numbers for the original file models
          if (modelNames[i].length() == 0)
            modelNames[i] = "" + modelNumbers[i];
          modelNumbers[i] += 1000000;
          modelNumbersForAtomLabel[i] = "1." + (i + 1);
        }
      }
      // update file number
      int filenumber = modelNumbers[baseModelCount - 1];
      filenumber -= filenumber % 1000000;
      if (modelNumbers[baseModelCount] < 1000000)
        filenumber += 1000000;
      for (int i = baseModelCount; i < modelCount; i++)
        modelNumbers[i] += filenumber;
    }
    for (int i = baseModelCount; i < modelCount; ++i) {
      if (fileHeader != null)
        setModelAuxiliaryInfo(i, "fileHeader", fileHeader);
      int filenumber = modelNumbers[i] / 1000000;
      if (filenumber != lastfilenumber) {
        modelnumber = 0;
        lastfilenumber = filenumber;
      }
      modelnumber++;
      if (filenumber == 0) {
        // only one file -- take the PDB number or sequential number as given by adapter
        sNum = "" + getModelNumber(i);
        filenumber = 1;
      } else {
        //        //if only one file, just return the integer file number
        //      if (modelnumber == 1
        //        && (i + 1 == modelCount || models[i + 1].modelNumber / 1000000 != filenumber))
        //    sNum = filenumber + "";
        // else
        sNum = filenumber + "." + modelnumber;
      }
      modelNumbersForAtomLabel[i] = sNum;
      models[i].fileIndex = filenumber - 1;
      modelFileNumbers[i] = filenumber * 1000000 + modelnumber;
      if (modelNames[i] == null || modelNames[i].length() == 0)
        modelNames[i] = sNum;
   }
    
    if (merging)
      for (int i = 0; i < baseModelCount; i++)
        models[i].modelSet = this;
    
    // this won't do in the case of trajectories
    for (int i = 0; i < modelCount; i++) {
      setModelAuxiliaryInfo(i, "modelName", modelNames[i]);
      setModelAuxiliaryInfo(i, "modelNumber", Integer.valueOf(modelNumbers[i] % 1000000));
      setModelAuxiliaryInfo(i, "modelFileNumber", Integer.valueOf(modelFileNumbers[i]));
      setModelAuxiliaryInfo(i, "modelNumberDotted", getModelNumberDotted(i));
      String codes = (String) getModelAuxiliaryInfo(i, "altLocs");
      if (codes != null) {
        Logger.info("model " + getModelNumberDotted(i)
            + " alternative locations: " + codes);
      }
    }
  }

  private void iterateOverAllNewAtoms(JmolAdapter adapter, Object atomSetCollection) {
    // atom is created, but not all methods are safe, because it
    // has no group -- this is only an issue for debugging
    int iLast = -1;
    boolean isPDB = false;
    JmolAdapter.AtomIterator iterAtom = adapter.getAtomIterator(atomSetCollection);
    int nRead = 0;
    while (iterAtom.hasNext()) {
      nRead++;
      int modelIndex = iterAtom.getAtomSetIndex() + baseModelIndex;
      if (modelIndex != iLast) {
        currentModelIndex = modelIndex;
        currentModel = models[modelIndex];
        currentChainID = '\uFFFF';
        models[modelIndex].bsAtoms.clear();
        isPDB = models[modelIndex].isPDB;
        iLast = modelIndex;
      }
      addAtom(isPDB, iterAtom.getAtomSymmetry(), 
          iterAtom.getAtomSite(),
          iterAtom.getUniqueID(), 
          iterAtom.getElementNumber(), 
          iterAtom.getAtomName(),
          iterAtom.getFormalCharge(), 
          iterAtom.getPartialCharge(),
          iterAtom.getEllipsoid(), 
          iterAtom.getOccupancy(), 
          iterAtom.getBfactor(), 
          iterAtom.getX(),
          iterAtom.getY(), 
          iterAtom.getZ(), 
          iterAtom.getIsHetero(), 
          iterAtom.getAtomSerial(), 
          iterAtom.getChainID(), 
          iterAtom.getGroup3(),
          iterAtom.getSequenceNumber(), 
          iterAtom.getInsertionCode(), 
          iterAtom.getVectorX(), 
          iterAtom.getVectorY(), 
          iterAtom.getVectorZ(),
          iterAtom.getAlternateLocationID(),
          iterAtom.getRadius()
          );
    }
    
    iLast = -1;
    int vdwtypeLast = -1;
    for (int i = 0; i < atomCount; i++) {
      if (atoms[i].modelIndex != iLast) {
        iLast = atoms[i].modelIndex;
        models[iLast].firstAtomIndex = i;
        int vdwtype = getDefaultVdwType(iLast);
        if (vdwtype != vdwtypeLast) {
          Logger.info("Default Van der Waals type for model" + " set to " + JmolConstants.getVdwLabel(vdwtype));
          vdwtypeLast = vdwtype;
        }
      }
    }
    Logger.info(nRead + " atoms created");    
  }

  private void addAtom(boolean isPDB, BitSet atomSymmetry, int atomSite,
                       Object atomUid, short atomicAndIsotopeNumber,
                       String atomName, int formalCharge, float partialCharge,
                       Object[] ellipsoid, int occupancy, float bfactor,
                       float x, float y, float z, boolean isHetero,
                       int atomSerial, char chainID, String group3,
                       int groupSequenceNumber, char groupInsertionCode,
                       float vectorX, float vectorY, float vectorZ,
                       char alternateLocationID, float radius) {
    checkNewGroup(chainID, group3, groupSequenceNumber, groupInsertionCode);
    byte specialAtomID = 0;
    if (atomName != null) {
      if (isPDB && atomName.indexOf('*') >= 0)
        atomName = atomName.replace('*', '\'');
      specialAtomID = JmolConstants.lookupSpecialAtomID(atomName);
      if (isPDB && specialAtomID == JmolConstants.ATOMID_ALPHA_CARBON
          && "CA".equalsIgnoreCase(group3))
        specialAtomID = 0;
    }
    Atom atom = addAtom(currentModelIndex, nullGroup, atomicAndIsotopeNumber,
        atomName, atomSerial, atomSite, x, y, z, radius, vectorX, vectorY,
        vectorZ, formalCharge, partialCharge, occupancy, bfactor, ellipsoid,
        isHetero, alternateLocationID, specialAtomID, atomSymmetry);
    htAtomMap.put(atomUid, atom);
  }

  private void checkNewGroup(char chainID,
                             String group3, int groupSequenceNumber,
                             char groupInsertionCode) {
    String group3i = (group3 == null ? null : group3.intern());
    if (chainID != currentChainID) {
      currentChainID = chainID;
      currentChain = getOrAllocateChain(currentModel, chainID);
      currentGroupInsertionCode = '\uFFFF';
      currentGroupSequenceNumber = -1;
      currentGroup3 = "xxxx";
    }
    if (groupSequenceNumber != currentGroupSequenceNumber
        || groupInsertionCode != currentGroupInsertionCode
        || group3i != currentGroup3) {
      currentGroupSequenceNumber = groupSequenceNumber;
      currentGroupInsertionCode = groupInsertionCode;
      currentGroup3 = group3i;
      while (groupCount >= group3Of.length) {
        chainOf = (Chain[]) ArrayUtil.doubleLength(chainOf);
        group3Of = ArrayUtil.doubleLength(group3Of);
        seqcodes = ArrayUtil.doubleLength(seqcodes);
        firstAtomIndexes = ArrayUtil.doubleLength(firstAtomIndexes);
      }
      firstAtomIndexes[groupCount] = atomCount;
      chainOf[groupCount] = currentChain;
      group3Of[groupCount] = group3;
      seqcodes[groupCount] = Group.getSeqcode(groupSequenceNumber,
          groupInsertionCode);
      ++groupCount;
    }
  }

  private Chain getOrAllocateChain(Model model, char chainID) {
    //Logger.debug("chainID=" + chainID + " -> " + (chainID + 0));
    Chain chain = model.getChain(chainID);
    if (chain != null)
      return chain;
    if (model.chainCount == model.chains.length)
      model.chains = (Chain[])ArrayUtil.doubleLength(model.chains);
    return model.chains[model.chainCount++] = new Chain(this, model, chainID);
  }

  private void iterateOverAllNewBonds(JmolAdapter adapter, Object atomSetCollection) {
    JmolAdapter.BondIterator iterBond = adapter.getBondIterator(atomSetCollection);
    if (iterBond == null)
      return;
    short mad = viewer.getMadBond();
    short order;
    defaultCovalentMad = (jmolData == null ? mad : 0);
    boolean haveMultipleBonds = false;
    while (iterBond.hasNext()) {
      order = (short) iterBond.getEncodedOrder();
      bondAtoms(iterBond.getAtomUniqueID1(), iterBond.getAtomUniqueID2(), order);
      if (order > 1 && order != JmolEdge.BOND_STEREO_NEAR && order != JmolEdge.BOND_STEREO_FAR)
        haveMultipleBonds = true; 
    }
    if (haveMultipleBonds && someModelsHaveSymmetry && !viewer.getApplySymmetryToBonds())
      Logger.info("ModelSet: use \"set appletSymmetryToBonds TRUE \" to apply the file-based multiple bonds to symmetry-generated atoms.");
    defaultCovalentMad = mad;
  }
  
  private List<Bond> vStereo;
  private void bondAtoms(Object atomUid1, Object atomUid2, short order) {
    Atom atom1 = htAtomMap.get(atomUid1);
    if (atom1 == null) {
      Logger.error("bondAtoms cannot find atomUid1?:" + atomUid1);
      return;
    }
    Atom atom2 = htAtomMap.get(atomUid2);
    if (atom2 == null) {
      Logger.error("bondAtoms cannot find atomUid2?:" + atomUid2);
      return;
    }
    
    // note that if the atoms are already bonded then
    // Atom.bondMutually(...) will return null
    if (atom1.isBonded(atom2))
      return;
    boolean isNear = (order == JmolEdge.BOND_STEREO_NEAR);
    boolean isFar = (order == JmolEdge.BOND_STEREO_FAR);
    Bond bond;
    if (isNear || isFar) {
      bond = bondMutually(atom1, atom2, (is2D ? order : 1), getDefaultMadFromOrder(1), 0);
      if (vStereo == null) {
        vStereo = new ArrayList<Bond>();
      }
      vStereo.add(bond);
    } else {
      bond = bondMutually(atom1, atom2, order, getDefaultMadFromOrder(order), 0);
      if (bond.isAromatic()) {
        someModelsHaveAromaticBonds = true;
      }
    }
    if (bondCount == bonds.length) {
      bonds = (Bond[]) ArrayUtil.setLength(bonds, bondCount + BOND_GROWTH_INCREMENT);
    }
    setBond(bondCount++, bond);
  }

  /**
   * Pull in all spans of helix, etc. in the file(s)
   * 
   * We do turn first, because sometimes a group is defined
   * twice, and this way it gets marked as helix or sheet
   * if it is both one of those and turn.
   * 
   * @param adapter
   * @param atomSetCollection
   */
  private void iterateOverAllNewStructures(JmolAdapter adapter,
                                           Object atomSetCollection) {
    JmolAdapter.StructureIterator iterStructure = adapter
        .getStructureIterator(atomSetCollection);
    if (iterStructure != null)
      while (iterStructure.hasNext()) {
        if (iterStructure.getStructureType() != JmolConstants.PROTEIN_STRUCTURE_TURN) {
          defineStructure(iterStructure.getModelIndex(),
              iterStructure.getSubstructureType(),
              iterStructure.getStructureID(), 
              iterStructure.getSerialID(),
              iterStructure.getStrandCount(),
              iterStructure.getStartChainID(), iterStructure
                  .getStartSequenceNumber(), iterStructure
                  .getStartInsertionCode(), iterStructure.getEndChainID(),
              iterStructure.getEndSequenceNumber(), iterStructure
                  .getEndInsertionCode());
        }
      }

    // define turns LAST. (pulled by the iterator first)
    // so that if they overlap they get overwritten:

    iterStructure = adapter.getStructureIterator(atomSetCollection);
    if (iterStructure != null)
      while (iterStructure.hasNext()) {
        if (iterStructure.getStructureType() == JmolConstants.PROTEIN_STRUCTURE_TURN)
          defineStructure(iterStructure.getModelIndex(),
              iterStructure.getSubstructureType(),
              iterStructure.getStructureID(), 1, 1,
              iterStructure.getStartChainID(), iterStructure
                  .getStartSequenceNumber(), iterStructure
                  .getStartInsertionCode(), iterStructure.getEndChainID(),
              iterStructure.getEndSequenceNumber(), iterStructure
                  .getEndInsertionCode());
      }
  }
  
  private BitSet structuresDefinedInFile = new BitSet();

  private void defineStructure(int modelIndex, int subType,
                               String structureID, int serialID,
                               int strandCount, char startChainID,
                               int startSequenceNumber,
                               char startInsertionCode, char endChainID,
                               int endSequenceNumber, char endInsertionCode) {
    byte type = (byte) subType;
    if (type < 0)
      type = JmolConstants.PROTEIN_STRUCTURE_NONE;
    int startSeqCode = Group.getSeqcode(startSequenceNumber,
        startInsertionCode);
    int endSeqCode = Group.getSeqcode(endSequenceNumber, endInsertionCode);
    if (modelIndex >= 0 || isTrajectory) { //from PDB file
      if (isTrajectory)
        modelIndex = 0;
      modelIndex += baseModelIndex;
      structuresDefinedInFile.set(modelIndex);
      models[modelIndex].addSecondaryStructure(type,
          structureID, serialID, strandCount,
          startChainID, startSeqCode, endChainID, endSeqCode);
      return;
    }
    for (int i = baseModelIndex; i < modelCount; i++) {
      structuresDefinedInFile.set(i);
      models[i].addSecondaryStructure(type,
          structureID, serialID, strandCount,
          startChainID, startSeqCode, endChainID, endSeqCode);
    }
  }
  
  ////// symmetry ///////
  
  private void initializeUnitCellAndSymmetry() {
    /*
     * really THREE issues here:
     * 1) does a model have an associated unit cell that could be displayed?
     * 2) are the coordinates fractional and so need to be transformed?
     * 3) does the model have symmetry operations that were applied?
     * 
     * This must be done for each model individually.
     * 
     */

    if (someModelsHaveUnitcells) {
      unitCells = new SymmetryInterface[modelCount];
      boolean haveMergeCells = (mergeModelSet != null && mergeModelSet.unitCells != null);
      for (int i = 0; i < modelCount; i++) {
        if (haveMergeCells && i < baseModelCount) {
          unitCells[i] = mergeModelSet.unitCells[i];
        } else {
          unitCells[i] = (SymmetryInterface) Interface.getOptionInterface("symmetry.Symmetry");
          unitCells[i].setSymmetryInfo(i, getModelAuxiliaryInfo(i));
        }
      }
    }
    if (appendNew && someModelsHaveSymmetry) {
      getAtomBits(Token.symmetry, null);
      for (int iAtom = baseAtomIndex, iModel = -1, i0 = 0; iAtom < atomCount; iAtom++) {
        if (atoms[iAtom].modelIndex != iModel) {
          iModel = atoms[iAtom].modelIndex;
          i0 = baseAtomIndex
              + getModelAuxiliaryInfoInt(iModel, "presymmetryAtomIndex")
              + getModelAuxiliaryInfoInt(iModel, "presymmetryAtomCount");
        }
        if (iAtom >= i0)
          bsSymmetry.set(iAtom);
      }
    }
    if (appendNew && someModelsHaveFractionalCoordinates) {
      for (int i = baseAtomIndex; i < atomCount; i++) {
        int modelIndex = atoms[i].modelIndex;
        if (!unitCells[modelIndex].getCoordinatesAreFractional())
          continue;
        unitCells[modelIndex].toCartesian(atoms[i], false);
        //if (Logger.debugging)
          //Logger.debug("atom " + i + ": " + (Point3f) atoms[i]);
      }
    }
  }

  private void initializeBonding() {
    // perform bonding if necessary

    // 1. apply CONECT records and set bsExclude to omit them
    // 2. apply stereochemistry from JME

    BitSet bsExclude = (getModelSetAuxiliaryInfo("someModelsHaveCONECT") == null ? null
        : new BitSet());
    if (bsExclude != null)
      setPdbConectBonding(baseAtomIndex, baseModelIndex, bsExclude);

    // 2. for each model in the collection,
    int atomIndex = baseAtomIndex;
    int modelAtomCount = 0;
    boolean symmetryAlreadyAppliedToBonds = viewer.getApplySymmetryToBonds();
    boolean doAutoBond = viewer.getAutoBond();
    boolean forceAutoBond = viewer.getForceAutoBond();
    BitSet bs = null;
    boolean autoBonding = false;
    if (!noAutoBond)
      for (int i = baseModelIndex; i < modelCount; atomIndex += modelAtomCount, i++) {
        modelAtomCount = models[i].bsAtoms.cardinality();
        int modelBondCount = getModelAuxiliaryInfoInt(i, "initialBondCount");
        
        boolean modelIsPDB = models[i].isPDB;
        if (modelBondCount < 0) {
          modelBondCount = bondCount;
        }
        boolean modelHasSymmetry = getModelAuxiliaryInfoBoolean(i,
            "hasSymmetry");
        // check for PDB file with fewer than one bond per every two atoms
        // this is in case the PDB format is being usurped for non-RCSB uses
        // In other words, say someone uses the PDB format to indicate atoms and
        // connectivity. We do NOT want to mess up that connectivity here.
        // It would be OK if people used HETATM for every atom, but I think
        // people
        // use ATOM, so that's a problem. Those atoms would not be excluded from
        // the
        // automatic bonding, and additional bonds might be made.
        boolean doBond = (forceAutoBond || doAutoBond
            && (modelBondCount == 0 || modelIsPDB && jmolData == null
                && modelBondCount < modelAtomCount / 2 || modelHasSymmetry
                && !symmetryAlreadyAppliedToBonds 
                && !getModelAuxiliaryInfoBoolean(i, "hasBonds")
                ));
        if (!doBond)
          continue;
        autoBonding = true;
        if (merging || modelCount > 1) {
          if (bs == null)
            bs = new BitSet(atomCount);
          if (i == baseModelIndex || !isTrajectory)
            bs.or(models[i].bsAtoms);
        }
      }
    if (autoBonding) {
      autoBond(bs, bs, bsExclude, null, defaultCovalentMad, viewer.checkAutoBondLegacy());
      Logger
          .info("ModelSet: autobonding; use  autobond=false  to not generate bonds automatically");
    } else {
      Logger
          .info("ModelSet: not autobonding; use  forceAutobond=true  to force automatic bond creation");
    }
  }

  private void finalizeGroupBuild() {
    // run this loop in increasing order so that the
    // groups get defined going up
    groups = new Group[groupCount];
    if (merging) {
      for (int i = 0; i < baseGroupIndex; i++) {
        groups[i] = mergeModelSet.groups[i];
        groups[i].setModelSet(this);
      }
    }
    for (int i = baseGroupIndex; i < groupCount; ++i) {
      distinguishAndPropagateGroup(i, chainOf[i], group3Of[i], seqcodes[i],
          firstAtomIndexes[i], (i == groupCount - 1 ? atomCount
              : firstAtomIndexes[i + 1]));
      chainOf[i] = null;
      group3Of[i] = null;
    }
    chainOf = null;
    group3Of = null;

    if (group3Lists != null) {
      if (modelSetAuxiliaryInfo != null) {
        modelSetAuxiliaryInfo.put("group3Lists", group3Lists);
        modelSetAuxiliaryInfo.put("group3Counts", group3Counts);
      }
    }

    group3Counts = null;
    group3Lists = null;

  }

  private void distinguishAndPropagateGroup(int groupIndex, Chain chain, String group3,
                                    int seqcode, int firstAtomIndex,
                                    int maxAtomIndex) {
    /*
     * called by finalizeGroupBuild()
     * 
     * first: build array of special atom names, 
     * for example "CA" for the alpha carbon is assigned #2
     * see JmolConstants.specialAtomNames[]
     * the special atoms all have IDs based on Atom.lookupSpecialAtomID(atomName)
     * these will be the same for each conformation
     * 
     * second: creates the monomers themselves based on this information
     * thus building the byte offsets[] array for each monomer, indicating which
     * position relative to the first atom in the group is which atom.
     * Each monomer.offsets[i] then points to the specific atom of that type
     * these will NOT be the same for each conformation  
     * 
     */
    int lastAtomIndex = maxAtomIndex - 1;

    if (lastAtomIndex < firstAtomIndex)
      throw new NullPointerException();
    int modelIndex = atoms[firstAtomIndex].modelIndex;

    Group group = null;
    if (group3 != null && haveBioClasses) {
      if (jbr == null) {
        try {
          Class<?> shapeClass = Class.forName("org.jmol.modelsetbio.Resolver");
          jbr = (JmolBioResolver) shapeClass.newInstance();
          haveBioClasses = true;
        } catch (Exception e) {
          Logger.error("developer error: org.jmol.modelsetbio.Resolver could not be found");
          haveBioClasses = false;
        }
      }
      if (haveBioClasses) {
        group = jbr.distinguishAndPropagateGroup(chain, group3, seqcode,
            firstAtomIndex, maxAtomIndex, modelIndex,
            specialAtomIndexes, atoms);
      }
    }
    String key;
    if (group == null) {
      group = new Group(chain, group3, seqcode, firstAtomIndex, lastAtomIndex);
      key = "o>";
    } else { 
      key = (group.isProtein() ? "p>" : group.isNucleic() ? "n>"
          : group.isCarbohydrate() ? "c>" : "o>");
    }
    if (group3 != null)
      countGroup(modelIndex, key, group3);
    addGroup(chain, group);
    groups[groupIndex] = group;
    group.setGroupIndex(groupIndex);

    for (int i = maxAtomIndex; --i >= firstAtomIndex;)
      atoms[i].setGroup(group);

  }

  private void addGroup(Chain chain, Group group) {
    if (chain.groupCount == chain.groups.length)
      chain.groups = (Group[])ArrayUtil.doubleLength(chain.groups);
    chain.groups[chain.groupCount++] = group;
  }

  private void countGroup(int modelIndex, String code, String group3) {
    int ptm = modelIndex + 1;
    if (group3Lists == null || group3Lists[ptm] == null)
      return;
    String g3code = (group3 + "   ").substring(0, 3);
    int pt = group3Lists[ptm].indexOf(g3code);
    if (pt < 0) {
      group3Lists[ptm] += ",[" + g3code + "]";
      pt = group3Lists[ptm].indexOf(g3code);
      group3Counts[ptm] = ArrayUtil.setLength(
          group3Counts[ptm], group3Counts[ptm].length + 10);
    }
    group3Counts[ptm][pt / 6]++;
    pt = group3Lists[ptm].indexOf(",[" + g3code);
    if (pt >= 0)
      group3Lists[ptm] = group3Lists[ptm].substring(0, pt) + code
          + group3Lists[ptm].substring(pt + 2);
    //becomes x> instead of ,[ 
    //these will be used for setting up the popup menu
    if (modelIndex >= 0)
      countGroup(-1, code, group3);
  }

  private void freeze() {
    htAtomMap.clear();
    // resize arrays
    if (atomCount < atoms.length)
      growAtomArrays(atomCount);
    if (bondCount < bonds.length)
      bonds = (Bond[]) ArrayUtil.setLength(bonds, bondCount);

    // free bonds cache 

    for (int i = MAX_BONDS_LENGTH_TO_CACHE; --i > 0;) { // .GT. 0
      numCached[i] = 0;
      Bond[][] bondsCache = freeBonds[i];
      for (int j = bondsCache.length; --j >= 0;)
        bondsCache[j] = null;
    }

    setAtomNamesAndNumbers(0, baseAtomIndex, mergeModelSet);

    // find elements for the popup menus

    findElementsPresent();

    molecules = null;
    moleculeCount = 0;
    currentModel = null;
    currentChain = null;

    // finalize all structures

    if (!isPDB)
      return;
    boolean asDSSP = viewer.getDefaultStructureDSSP();
    String ret = calculateStructuresAllExcept(structuresDefinedInFile, 
          asDSSP, 
          false, true, true, asDSSP); // now DSSP
    if (ret.length() > 0)
      Logger.info(ret);
  }

  private void findElementsPresent() {
    elementsPresent = new BitSet[modelCount];
    for (int i = 0; i < modelCount; i++)
      elementsPresent[i] = new BitSet(64);
    for (int i = atomCount; --i >= 0;) {
      int n = atoms[i].getAtomicAndIsotopeNumber();
      if (n >= Elements.elementNumberMax)
        n = Elements.elementNumberMax
            + Elements.altElementIndexFromNumber(n);
      elementsPresent[atoms[i].modelIndex].set(n);
    }
  }

  private void applyStereochemistry() {

    // 1) implicit stereochemistry 
    
    set2dZ(baseAtomIndex, atomCount);

    // 2) explicit stereochemistry
    
    if (vStereo != null) {
      BitSet bsToTest = new BitSet();
      bsToTest.set(baseAtomIndex, atomCount);
      for (int i = vStereo.size(); --i >= 0;) {
        Bond b = vStereo.get(i);
        float dz2 = (b.order == JmolEdge.BOND_STEREO_NEAR ? 3 : -3);
        b.order = 1;
        if (b.atom2.z != b.atom1.z && (dz2 < 0) == (b.atom2.z < b.atom1.z))
          dz2 /= 3;
        //float dz1 = dz2/3;
        //b.atom1.z += dz1;
        BitSet bs = JmolMolecule.getBranchBitSet(atoms, bsToTest, b.atom2.index, b.atom1.index, false, true);
        bs.set(b.atom2.index); // ring structures
        for (int j = bs.nextSetBit(0); j >= 0; j = bs.nextSetBit(j + 1))
          atoms[j].z += dz2;
        // move atom2 somewhat closer to but not directly above atom1
        b.atom2.x = (b.atom1.x + b.atom2.x) /2;
        b.atom2.y = (b.atom1.y + b.atom2.y) /2;
      }
      vStereo = null;
    } 
    is2D = false;
  }


  private void set2dZ(int iatom1, int iatom2) {
    BitSet atomlist = new BitSet(iatom2);
    BitSet bsBranch = new BitSet();
    Vector3f v = new Vector3f();
    Vector3f v0 = new Vector3f(0, 1, 0);
    Vector3f v1 = new Vector3f();
    BitSet bs0 = new BitSet();
    bs0.set(iatom1, iatom2);
    for (int i = iatom1; i < iatom2; i++)
      if (!atomlist.get(i) && !bsBranch.get(i)) {
        bsBranch = getBranch2dZ(i, -1, bs0, bsBranch, v, v0, v1);
        atomlist.or(bsBranch);
      }
  }
  
  
  /**
   * @param atomIndex 
   * @param atomIndexNot 
   * @param bs0 
   * @param bsBranch  
   * @param v 
   * @param v0 
   * @param v1 
   * @return   atom bitset
   */
  private BitSet getBranch2dZ(int atomIndex, int atomIndexNot, BitSet bs0, 
                              BitSet bsBranch, Vector3f v, Vector3f v0, Vector3f v1) {
    BitSet bs = new BitSet(atomCount);
    if (atomIndex < 0)
      return bs;
    BitSet bsToTest = new BitSet();
    bsToTest.or(bs0);
    if (atomIndexNot >= 0)
      bsToTest.clear(atomIndexNot);
    setBranch2dZ(atoms[atomIndex], bs, bsToTest, v, v0, v1);
    return bs;
  }

  private static void setBranch2dZ(Atom atom, BitSet bs,
                                            BitSet bsToTest, Vector3f v,
                                            Vector3f v0, Vector3f v1) {
    int atomIndex = atom.index;
    if (!bsToTest.get(atomIndex))
      return;
    bsToTest.clear(atomIndex);
    bs.set(atomIndex);
    if (atom.bonds == null)
      return;
    for (int i = atom.bonds.length; --i >= 0;) {
      Bond bond = atom.bonds[i];
      if (bond.isHydrogen())
        continue;
      Atom atom2 = bond.getOtherAtom(atom);
      setAtom2dZ(atom, atom2, v, v0, v1);
      setBranch2dZ(atom2, bs, bsToTest, v, v0, v1);
    }
  }

  private static void setAtom2dZ(Atom atomRef, Atom atom2, Vector3f v, Vector3f v0, Vector3f v1) {
    v.set(atom2);
    v.sub(atomRef);
    v.z = 0;
    v.normalize();
    v1.cross(v0, v);
    double theta = Math.acos(v.dot(v0));
    atom2.z = atomRef.z + (float) (0.8f * Math.sin(4 * theta));
  }

  ///////////////  shapes  ///////////////
  
  private void finalizeShapes() {
    shapeManager = viewer.getShapeManager();
    if (!merging)
      shapeManager.resetShapes();
    shapeManager.loadDefaultShapes(this);
    if (someModelsHaveAromaticBonds && viewer.getSmartAromatic())
      assignAromaticBonds(false);
    if (merging) {
      if (baseModelCount == 1)
        shapeManager.setShapeProperty(JmolConstants.SHAPE_MEASURES, "clearModelIndex", null, null);
      merging = false;
    }
  }
  
  public void createAtomDataSet(int tokType, Object atomSetCollection,
                                BitSet bsSelected) {
    if (atomSetCollection == null)
      return;
    // must be one of JmolConstants.LOAD_ATOM_DATA_TYPES
    JmolAdapter adapter = viewer.getModelAdapter();
    Point3f pt = new Point3f();
    Point3f v = new Point3f();
    float tolerance = viewer.getLoadAtomDataTolerance();
    if (unitCells != null)
      for (int i = bsSelected.nextSetBit(0); i >= 0; i = bsSelected
          .nextSetBit(i + 1))
        if (atoms[i].getAtomSymmetry() != null) {
          tolerance = -tolerance;
          break;
        }
    int i = -1;
    int n = 0;
    boolean loadAllData = (BitSetUtil.cardinalityOf(bsSelected) == viewer
        .getAtomCount());
    for (JmolAdapter.AtomIterator iterAtom = adapter
        .getAtomIterator(atomSetCollection); iterAtom.hasNext();) {
      float x = iterAtom.getX();
      float y = iterAtom.getY();
      float z = iterAtom.getZ();
      if (Float.isNaN(x + y + z))
        continue;

      if (tokType == Token.xyz) {
        // we are loading selected coordinates only
        i = bsSelected.nextSetBit(i + 1);
        if (i < 0)
          break;
        n++;
        if (Logger.debugging)
          Logger.debug("atomIndex = " + i + ": " + atoms[i]
              + " --> (" + x + "," + y + "," + z);
        setAtomCoord(i, x, y, z);
        continue;
      }
      pt.set(x, y, z);
      BitSet bs = new BitSet(atomCount);
      getAtomsWithin(tolerance, pt, bs, -1);
      bs.and(bsSelected);
      if (loadAllData) {
        n = BitSetUtil.cardinalityOf(bs);
        if (n == 0) {
          Logger.warn("createAtomDataSet: no atom found at position " + pt);
          continue;
        } else if (n > 1 && Logger.debugging) {
          Logger.debug("createAtomDataSet: " + n + " atoms found at position "
              + pt);
        }
      }
      switch (tokType) {
      case Token.vibxyz:
        float vx = iterAtom.getVectorX();
        float vy = iterAtom.getVectorY();
        float vz = iterAtom.getVectorZ();
        if (Float.isNaN(vx + vy + vz))
          continue;
        v.set(vx, vy, vz);
        if (Logger.debugging)
          Logger.info("xyz: " + pt + " vib: " + v);
        setAtomCoord(bs, Token.vibxyz, v);
        break;
      case Token.occupancy:
        // [0 to 100], default 100
        setAtomProperty(bs, tokType, iterAtom.getOccupancy(), 0, null, null,
            null);
        break;
      case Token.partialcharge:
        // anything but NaN, default NaN
        setAtomProperty(bs, tokType, 0, iterAtom.getPartialCharge(), null,
            null, null);
        break;
      case Token.temperature:
        // anything but NaN but rounded to 0.01 precision and stored as a short (-32000 - 32000), default NaN
        setAtomProperty(bs, tokType, 0, iterAtom.getBfactor(), null, null, null);
        break;
      }
    }
    //finally:
    switch (tokType) {
    case Token.vibxyz:
      String vibName = adapter.getAtomSetName(atomSetCollection, 0);
      Logger.info("_vibrationName = " + vibName);
      viewer.setStringProperty("_vibrationName", vibName);
      break;
    case Token.xyz:
      Logger.info(n + " atom positions read");
      recalculateLeadMidpointsAndWingVectors(-1);
      break;
    }

  }
}
