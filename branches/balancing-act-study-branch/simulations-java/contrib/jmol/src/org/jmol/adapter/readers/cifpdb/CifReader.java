/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2006-10-20 07:48:25 -0500 (Fri, 20 Oct 2006) $
 * $Revision: 5991 $
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
package org.jmol.adapter.readers.cifpdb;

import org.jmol.adapter.smarter.*;
import org.jmol.api.JmolAdapter;
import org.jmol.api.JmolLineReader;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;

import org.jmol.util.CifDataReader;
import org.jmol.util.Logger;

/**
 * A true line-free CIF file reader for CIF and mmCIF files.
 *
 *<p>
 * <a href='http://www.iucr.org/iucr-top/cif/'>
 * http://www.iucr.org/iucr-top/cif/
 * </a>
 * 
 * <a href='http://www.iucr.org/iucr-top/cif/standard/cifstd5.html'>
 * http://www.iucr.org/iucr-top/cif/standard/cifstd5.html
 * </a>
 *
 * @author Miguel, Egon, and Bob (hansonr@stolaf.edu)
 * 
 * symmetry added by Bob Hanson:
 * 
 *  setSpaceGroupName()
 *  setSymmetryOperator()
 *  setUnitCellItem()
 *  setFractionalCoordinates()
 *  setAtomCoord()
 *  applySymmetryAndSetTrajectory()
 *  
 */
public class CifReader extends AtomSetCollectionReader implements JmolLineReader {

  private CifDataReader tokenizer = new CifDataReader(this);

  private String thisDataSetName = "";
  private String chemicalName = "";
  private String thisStructuralFormula = "";
  private String thisFormula = "";
  private boolean iHaveDesiredModel;
  private boolean isPDB = false;
  private Map<String, String> htHetero;
  private boolean isMolecular;
  private String molecularType = "GEOM_BOND default";
  
  @Override
  public void initializeReader() throws Exception {
    isMolecular = (filter != null && filter.indexOf("MOLECUL") >= 0);
    if (isMolecular) {
      if (!doApplySymmetry) {
        doApplySymmetry = true;
        latticeCells[0] = 1;
        latticeCells[1] = 1;
        latticeCells[2] = 1;
      }
      molecularType = "filter \"MOLECULAR\"";
    }

    int nAtoms = 0;
    /*
     * Modified for 10.9.64 9/23/06 by Bob Hanson to remove as much as possible
     * of line dependence. a loop could now go:
     * 
     * blah blah blah loop_ _a _b _c 0 1 2 0 3 4 0 5 6 loop_......
     * 
     * we don't actually check that any skpped loop has the proper number of
     * data points --- some multiple of the number of data keys -- but other
     * than that, we are checking here for proper CIF syntax, and Jmol will
     * report if it finds data where a key is supposed to be.
     */
    line = "";
    boolean skipping = false;
    while ((key = tokenizer.peekToken()) != null) {
      if (key.startsWith("data_")) {
        if (iHaveDesiredModel)
          break;
        skipping = !doGetModel(++modelNumber);
        if (skipping) {
          tokenizer.getTokenPeeked();
        } else {
          chemicalName = "";
          thisStructuralFormula = "";
          thisFormula = "";
          if (nAtoms == atomSetCollection.getAtomCount())
            // we found no atoms -- must revert
            atomSetCollection.removeAtomSet();
          else
            applySymmetryAndSetTrajectory();
          processDataParameter();
          iHaveDesiredModel = (isLastModel(modelNumber));
          nAtoms = atomSetCollection.getAtomCount();
        }
        continue;
      }
      if (key.startsWith("loop_")) {
        if (skipping) {
          tokenizer.getTokenPeeked();
          skipLoop();
        } else {
          processLoopBlock();
        }
        continue;
      }
      // global_ and stop_ are reserved STAR keywords
      // see http://www.iucr.org/iucr-top/lists/comcifs-l/msg00252.html
      // http://www.iucr.org/iucr-top/cif/spec/version1.1/cifsyntax.html#syntax

      // stop_ is not allowed, because nested loop_ is not allowed
      // global_ is a reserved STAR word; not allowed in CIF
      // ah, heck, let's just flag them as CIF ERRORS
      /*
       * if (key.startsWith("global_") || key.startsWith("stop_")) {
       * tokenizer.getTokenPeeked(); continue; }
       */
      if (key.indexOf("_") != 0) {
        Logger.warn("CIF ERROR ? should be an underscore: " + key);
        tokenizer.getTokenPeeked();
      } else if (!getData()) {
        continue;
      }
      if (!skipping) {
        key = key.replace('.', '_');
        if (key.startsWith("_chemical_name")) {
          processChemicalInfo("name");
        } else if (key.startsWith("_chemical_formula_structural")) {
          processChemicalInfo("structuralFormula");
        } else if (key.startsWith("_chemical_formula_sum")) {
          processChemicalInfo("formula");
        } else if (key.startsWith("_cell_")) {
          processCellParameter();
        } else if (key.startsWith("_symmetry_space_group_name_H-M")
            || key.startsWith("_symmetry_space_group_name_Hall")) {
          processSymmetrySpaceGroupName();
        } else if (key.startsWith("_atom_sites_fract_tran")) {
          processUnitCellTransformMatrix();
        } else if (key.startsWith("_pdbx_entity_nonpoly")) {
          processNonpolyData();
        }
      }
    }

    if (atomSetCollection.getAtomCount() == nAtoms)
      atomSetCollection.removeAtomSet();
    else
      applySymmetryAndSetTrajectory();
    if (htSites != null)
      addSites(htSites);
    atomSetCollection.setCollectionName("<collection of "
        + atomSetCollection.getAtomSetCount() + " models>");
    continuing = false;
  }

  @Override
  protected void finalizeReader() throws Exception {
    super.finalizeReader();
    String header = tokenizer.getFileHeader();
    if (header.length() > 0)
        atomSetCollection.setAtomSetCollectionAuxiliaryInfo("fileHeader",
          header);
  }

  private int nMolecular = 0;

  @Override
  public void applySymmetryAndSetTrajectory() throws Exception {
    // This speeds up calculation, because no crosschecking
    // No special-position atoms in mmCIF files, because there will
    // be no center of symmetry, no rotation-inversions, 
    // no atom-centered rotation axes, and no mirror or glide planes. 
    atomSetCollection.setCheckSpecial(!isPDB);
    boolean doCheck = doCheckUnitCell && !isPDB;
    super.applySymmetryAndSetTrajectory();
    if (doCheck && (bondTypes.size() > 0 || isMolecular))
      setBondingAndMolecules();
  }

  ////////////////////////////////////////////////////////////////
  // processing methods
  ////////////////////////////////////////////////////////////////

  /**
   *  initialize a new atom set
   *  
   */
  private void processDataParameter() {
    bondTypes.clear();
    tokenizer.getTokenPeeked();
    thisDataSetName = (key.length() < 6 ? "" : key.substring(5));
    if (thisDataSetName.length() > 0) {
      if (atomSetCollection.getCurrentAtomSetIndex() >= 0) {
        // note that there can be problems with multi-data mmCIF sets each with
        // multiple models; and we could be loading multiple files!
        atomSetCollection.newAtomSet();
      } else {
        atomSetCollection.setCollectionName(thisDataSetName);
      }
    }
    Logger.debug(key);
  }
  
  /**
   * reads some of the more interesting info 
   * into specific atomSetAuxiliaryInfo elements
   * 
   * @param type    "name" "formula" etc.
   * @throws Exception
   */
  private void processChemicalInfo(String type) throws Exception {
    if (type.equals("name"))
      chemicalName = data = tokenizer.fullTrim(data);
    else if (type.equals("structuralFormula"))
      thisStructuralFormula = data = tokenizer.fullTrim(data);
    else if (type.equals("formula"))
      thisFormula = data = tokenizer.fullTrim(data);
    if (Logger.debugging) {
      Logger.debug(type + " = " + data);
    }
  }

  /**
   * done by AtomSetCollectionReader
   * 
   * @throws Exception
   */
  private void processSymmetrySpaceGroupName() throws Exception {
    setSpaceGroupName((key.equals("_symmetry_space_group_name_H-M") ? "HM:" : "Hall:") + data);
  }

  final public static String[] cellParamNames = { 
    "_cell_length_a", 
    "_cell_length_b",
    "_cell_length_c", 
    "_cell_angle_alpha", 
    "_cell_angle_beta",
    "_cell_angle_gamma" 
  };

  /**
   * unit cell parameters -- two options, so we use MOD 6
   * 
   * @throws Exception
   */
  private void processCellParameter() throws Exception {
    for (int i = cellParamNames.length; --i >= 0;)
      if (isMatch(key, cellParamNames[i])) {
        setUnitCellItem(i, parseFloat(data));
        return;
      }
  }

  final private static String[] TransformFields = {
      "x[1][1]", "x[1][2]", "x[1][3]", "r[1]",
      "x[2][1]", "x[2][2]", "x[2][3]", "r[2]",
      "x[3][1]", "x[3][2]", "x[3][3]", "r[3]",
  };

  /**
   * 
   * the PDB transformation matrix cartesian --> fractional
   * 
   * @throws Exception
   */
  private void processUnitCellTransformMatrix() throws Exception {
    /*
     * PDB:
     
     SCALE1       .024414  0.000000  -.000328        0.00000
     SCALE2      0.000000   .053619  0.000000        0.00000
     SCALE3      0.000000  0.000000   .044409        0.00000

     * CIF:

     _atom_sites.fract_transf_matrix[1][1]   .024414 
     _atom_sites.fract_transf_matrix[1][2]   0.000000 
     _atom_sites.fract_transf_matrix[1][3]   -.000328 
     _atom_sites.fract_transf_matrix[2][1]   0.000000 
     _atom_sites.fract_transf_matrix[2][2]   .053619 
     _atom_sites.fract_transf_matrix[2][3]   0.000000 
     _atom_sites.fract_transf_matrix[3][1]   0.000000 
     _atom_sites.fract_transf_matrix[3][2]   0.000000 
     _atom_sites.fract_transf_matrix[3][3]   .044409 
     _atom_sites.fract_transf_vector[1]      0.00000 
     _atom_sites.fract_transf_vector[2]      0.00000 
     _atom_sites.fract_transf_vector[3]      0.00000 

     */
    float v = parseFloat(data);
    if (Float.isNaN(v))
      return;
    for (int i = 0; i < TransformFields.length; i++) {
      if (key.indexOf(TransformFields[i]) >= 0) {
        setUnitCellItem(6 + i, v);
        return;
      }
    }
  }
  
  ////////////////////////////////////////////////////////////////
  // loop_ processing
  ////////////////////////////////////////////////////////////////

  private String key;
  private String data;
  
  /**
   * 
   * @return TRUE if data, even if ''; FALSE if '.' or  '?' or eof.
   * 
   * @throws Exception
   */
  private boolean getData() throws Exception {
    key = tokenizer.getTokenPeeked();
    data = tokenizer.getNextToken();
    if (Logger.debugging)
      Logger.debug(key  + " " + data);
    if (data == null) {
      Logger.warn("CIF ERROR ? end of file; data missing: " + key);
      return false;
    }
    return (data.length() == 0 || data.charAt(0) != '\0');
  }
  
  /**
   * processes loop_ blocks of interest or skips the data
   * 
   * @throws Exception
   */
  private void processLoopBlock() throws Exception {
    tokenizer.getTokenPeeked(); //loop_
    String str = tokenizer.peekToken();
    if (str == null)
      return;
    if (str.startsWith("_atom_site_") || str.startsWith("_atom_site.")) {
      if (!processAtomSiteLoopBlock())
        return;
      atomSetCollection.setAtomSetName(thisDataSetName);
      atomSetCollection.setAtomSetAuxiliaryInfo("chemicalName", chemicalName);
      atomSetCollection.setAtomSetAuxiliaryInfo("structuralFormula",
          thisStructuralFormula);
      atomSetCollection.setAtomSetAuxiliaryInfo("formula", thisFormula);
      return;
    }
    if (str.startsWith("_atom_type")) {
      processAtomTypeLoopBlock();
      return;
    }
    if (str.startsWith("_geom_bond")) {
      if (!doApplySymmetry) {
        isMolecular = true;
        doApplySymmetry = true;
        latticeCells[0] = 1;
        latticeCells[1] = 1;
        latticeCells[2] = 1;
      }
      if (isMolecular)
        processGeomBondLoopBlock();
      else 
        skipLoop();
      return;
    }
    if (str.startsWith("_pdbx_entity_nonpoly")) {
      processNonpolyLoopBlock();
      return;
    }
    if (str.startsWith("_chem_comp")) {
      processChemCompLoopBlock();
      return;
    }
    if (str.startsWith("_struct_conf") && !str.startsWith("_struct_conf_type")) {
      processStructConfLoopBlock();
      return;
    }
    if (str.startsWith("_struct_sheet_range")) {
      processStructSheetRangeLoopBlock();
      return;
    }
    if (str.startsWith("_symmetry_equiv_pos")
        || str.startsWith("_space_group_symop")) {
      if (ignoreFileSymmetryOperators) {
        Logger.warn("ignoring file-based symmetry operators");
        skipLoop();
      } else {
        processSymmetryOperationsLoopBlock();
      }
      return;
    }
    if (str.startsWith("_struct_site")) {
      processStructSiteBlock();
      return;
    }
    skipLoop();
  }

  ////////////////////////////////////////////////////////////////
  // atom type data
  ////////////////////////////////////////////////////////////////


  private Map<String, Float> atomTypes;
  private List<Object[]> bondTypes = new ArrayList<Object[]>();
  
  final private static byte ATOM_TYPE_SYMBOL = 0;
  final private static byte ATOM_TYPE_OXIDATION_NUMBER = 1;

  final private static String[] atomTypeFields = { 
      "_atom_type_symbol",
      "_atom_type_oxidation_number", 
  };

  /**
   * 
   * reads the oxidation number and associates it with an atom name, which can
   * then later be associated with the right atom indirectly.
   * 
   * @throws Exception
   */
  private void processAtomTypeLoopBlock() throws Exception {
    parseLoopParameters(atomTypeFields);
    for (int i = propertyCount; --i >= 0;)
      if (fieldOf[i] == NONE) {
        skipLoop();
        return;
      }

    while (tokenizer.getData()) {
      String atomTypeSymbol = null;
      float oxidationNumber = Float.NaN;
      for (int i = 0; i < tokenizer.fieldCount; ++i) {
        switch (fieldProperty(i)) {
        case NONE:
          break;
        case ATOM_TYPE_SYMBOL:
          atomTypeSymbol = field;
          break;
        case ATOM_TYPE_OXIDATION_NUMBER:
          oxidationNumber = parseFloat(field);
          break;
        }
      }
      if (atomTypeSymbol == null || Float.isNaN(oxidationNumber))
        continue;
      if (atomTypes == null)
        atomTypes = new Hashtable<String, Float>();
      atomTypes.put(atomTypeSymbol, new Float(oxidationNumber));
    }
  }

  ////////////////////////////////////////////////////////////////
  // atom site data
  ////////////////////////////////////////////////////////////////

  final private static byte NONE = -1;
  final private static byte TYPE_SYMBOL = 0;
  final private static byte LABEL = 1;
  final private static byte AUTH_ATOM = 2;
  final private static byte FRACT_X = 3;
  final private static byte FRACT_Y = 4;
  final private static byte FRACT_Z = 5;
  final private static byte CARTN_X = 6;
  final private static byte CARTN_Y = 7;
  final private static byte CARTN_Z = 8;
  final private static byte OCCUPANCY = 9;
  final private static byte B_ISO = 10;
  final private static byte COMP_ID = 11;
  final private static byte ASYM_ID = 12;
  final private static byte SEQ_ID = 13;
  final private static byte INS_CODE = 14;
  final private static byte ALT_ID = 15;
  final private static byte GROUP_PDB = 16;
  final private static byte MODEL_NO = 17;
  final private static byte DUMMY_ATOM = 18;
  final private static byte DISORDER_GROUP = 19;
  final private static byte ANISO_LABEL = 20;
  final private static byte ANISO_MMCIF_ID = 21;
  final private static byte ANISO_U11 = 22;
  final private static byte ANISO_U22 = 23;
  final private static byte ANISO_U33 = 24;
  final private static byte ANISO_U12 = 25;
  final private static byte ANISO_U13 = 26;
  final private static byte ANISO_U23 = 27;
  final private static byte ANISO_MMCIF_U11 = 28;
  final private static byte ANISO_MMCIF_U22 = 29;
  final private static byte ANISO_MMCIF_U33 = 30;
  final private static byte ANISO_MMCIF_U12 = 31;
  final private static byte ANISO_MMCIF_U13 = 32;
  final private static byte ANISO_MMCIF_U23 = 33;
  final private static byte U_ISO_OR_EQUIV = 34;
  final private static byte ANISO_B11 = 35;
  final private static byte ANISO_B22 = 36;
  final private static byte ANISO_B33 = 37;
  final private static byte ANISO_B12 = 38;
  final private static byte ANISO_B13 = 39;
  final private static byte ANISO_B23 = 40;
  final private static byte ANISO_Beta_11 = 41;
  final private static byte ANISO_Beta_22 = 42;
  final private static byte ANISO_Beta_33 = 43;
  final private static byte ANISO_Beta_12 = 44;
  final private static byte ANISO_Beta_13 = 45;
  final private static byte ANISO_Beta_23 = 46;
  final private static byte ADP_TYPE = 47;

  final private static String[] atomFields = { 
      "_atom_site_type_symbol",
      "_atom_site_label", 
      "_atom_site_auth_atom_id", 
      "_atom_site_fract_x",
      "_atom_site_fract_y", 
      "_atom_site_fract_z", 
      "_atom_site_Cartn_x",
      "_atom_site_Cartn_y", 
      "_atom_site_Cartn_z", 
      "_atom_site_occupancy",
      "_atom_site_b_iso_or_equiv", 
      "_atom_site_auth_comp_id",
      "_atom_site_auth_asym_id", 
      "_atom_site_auth_seq_id",
      "_atom_site_pdbx_PDB_ins_code", 
      "_atom_site_label_alt_id",
      "_atom_site_group_PDB", 
      "_atom_site_pdbx_PDB_model_num",
      "_atom_site_calc_flag", 
      "_atom_site_disorder_group",
      "_atom_site_aniso_label", 
      "_atom_site_anisotrop_id",
      "_atom_site_aniso_U_11",
      "_atom_site_aniso_U_22",
      "_atom_site_aniso_U_33",
      "_atom_site_aniso_U_12",
      "_atom_site_aniso_U_13",
      "_atom_site_aniso_U_23",
      "_atom_site_anisotrop_U[1][1]",
      "_atom_site_anisotrop_U[2][2]",
      "_atom_site_anisotrop_U[3][3]",
      "_atom_site_anisotrop_U[1][2]",
      "_atom_site_anisotrop_U[1][3]",
      "_atom_site_anisotrop_U[2][3]",
      "_atom_site_U_iso_or_equiv",
      "_atom_site_aniso_B_11",
      "_atom_site_aniso_B_22",
      "_atom_site_aniso_B_33",
      "_atom_site_aniso_B_12",
      "_atom_site_aniso_B_13",
      "_atom_site_aniso_B_23",
      "_atom_site_aniso_Beta_11",
      "_atom_site_aniso_Beta_22",
      "_atom_site_aniso_Beta_33",
      "_atom_site_aniso_Beta_12",
      "_atom_site_aniso_Beta_13",
      "_atom_site_aniso_Beta_23",
      "_atom_site_adp_type",
  };

  /* to: hansonr@stolaf.edu
   * from: Zukang Feng zfeng@rcsb.rutgers.edu
   * re: Two mmCIF issues
   * date: 4/18/2006 10:30 PM
   * "You should always use _atom_site.auth_asym_id for PDB chain IDs."
   * 
   * 
   */

  /**
   * reads atom data in any order
   * 
   * @return TRUE if successful; FALS if EOF encountered
   * @throws Exception
   */
  boolean processAtomSiteLoopBlock() throws Exception {
    int currentModelNO = -1;
    boolean isAnisoData = false;
    parseLoopParameters(atomFields);
    if (fieldOf[CARTN_X] != NONE) {
      setFractionalCoordinates(false);
      disableField(FRACT_X);
      disableField(FRACT_Y);
      disableField(FRACT_Z);
    } else if (fieldOf[FRACT_X] != NONE) {
      setFractionalCoordinates(true);
      disableField(CARTN_X);
      disableField(CARTN_Y);
      disableField(CARTN_Z);
    } else if (fieldOf[ANISO_LABEL] != NONE) {
      // standard CIF
      isAnisoData = true;
    } else if (fieldOf[ANISO_MMCIF_ID] != NONE) {
      // MMCIF
      isAnisoData = true;
    } else {
      // it is a different kind of _atom_site loop block
      skipLoop();
      return false;
    }
    int iAtom = -1;
    float[] data;
    while (tokenizer.getData()) {
      Atom atom = new Atom();
      for (int i = 0; i < tokenizer.fieldCount; ++i) {
        switch (fieldProperty(i)) {
        case NONE:
          break;
        case TYPE_SYMBOL:
          String elementSymbol;
          if (field.length() < 2) {
            elementSymbol = field;
          } else {
            char ch1 = Character.toLowerCase(field.charAt(1));
            if (Atom.isValidElementSymbol(firstChar, ch1))
              elementSymbol = "" + firstChar + ch1;
            else
              elementSymbol = "" + firstChar;
          }
          atom.elementSymbol = elementSymbol;
          if (atomTypes != null && atomTypes.containsKey(field)) {
            float charge = atomTypes.get(field).floatValue();
            atom.formalCharge = (int) (charge + (charge < 0 ? -0.5 : 0.5));
            //because otherwise -1.6 is rounded UP to -1, and  1.6 is rounded DOWN to 1
            if (Math.abs(atom.formalCharge - charge) > 0.1)
              if (Logger.debugging) {
                Logger.debug("CIF charge on " + field + " was " + charge
                    + "; rounded to " + atom.formalCharge);
              }
          }
          break;
        case LABEL:
        case AUTH_ATOM:
          atom.atomName = field;
          break;
        case CARTN_X:
        case FRACT_X:
          atom.x = parseFloat(field);
          break;
        case CARTN_Y:
        case FRACT_Y:
          atom.y = parseFloat(field);
          break;
        case CARTN_Z:
        case FRACT_Z:
          atom.z = parseFloat(field);
          break;
        case OCCUPANCY:
          float floatOccupancy = parseFloat(field);
          if (!Float.isNaN(floatOccupancy))
            atom.occupancy = (int) (floatOccupancy * 100);
          break;
        case B_ISO:
          atom.bfactor = parseFloat(field) * (isPDB ? 1 : 100f);
          break;
        case COMP_ID:
          atom.group3 = field;
          break;
        case ASYM_ID:
          if (field.length() > 1)
            Logger.warn("Don't know how to deal with chains more than 1 char: "
                + field);
          atom.chainID = firstChar;
          break;
        case SEQ_ID:
          atom.sequenceNumber = parseInt(field);
          break;
        case INS_CODE:
          atom.insertionCode = firstChar;
          break;
        case ALT_ID:
          atom.alternateLocationID = firstChar;
        break;
        case DISORDER_GROUP:          
          if (firstChar == '-' && field.length() > 1) {
            atom.alternateLocationID = field.charAt(1);
            atom.ignoreSymmetry = true;
          } else {
            atom.alternateLocationID = firstChar;
          }
          break;
        case GROUP_PDB:
          isPDB = true;
          if ("HETATM".equals(field))
            atom.isHetero = true;
          break;
        case MODEL_NO:
          int modelNO = parseInt(field);
          if (modelNO != currentModelNO) {
            atomSetCollection.newAtomSet();
            currentModelNO = modelNO;
          }
          break;
        case DUMMY_ATOM:
          //see http://www.iucr.org/iucr-top/cif/cifdic_html/
          //            1/cif_core.dic/Iatom_site_calc_flag.html
          if ("dum".equals(field)) {
            atom.x = Float.NaN;
            continue; //skip 
          }
          break;
        case ADP_TYPE:
          if (field.equalsIgnoreCase("Uiso")) {
            int j = fieldOf[U_ISO_OR_EQUIV];
            if (j != NONE) {
              data = atomSetCollection.getAnisoBorU(atom);
              if (data == null)
                atomSetCollection.setAnisoBorU(atom, data = new float[8], 8);
              data[7] = parseFloat(tokenizer.loopData[j]);
              // Ortep Type 8: D = 2pi^2, C = 2, a*b*
            }
          }
          break;
        case ANISO_LABEL:
          iAtom = atomSetCollection.getAtomNameIndex(field);
          if (iAtom < 0)
            return false;
          atom = atomSetCollection.getAtom(iAtom);
          break;
        case ANISO_MMCIF_ID:
          atom = atomSetCollection.getAtom(++iAtom);
          break;
        case ANISO_U11:
        case ANISO_U22:
        case ANISO_U33:
        case ANISO_U12:
        case ANISO_U13:
        case ANISO_U23:
        case ANISO_MMCIF_U11:
        case ANISO_MMCIF_U22:
        case ANISO_MMCIF_U33:
        case ANISO_MMCIF_U12:
        case ANISO_MMCIF_U13:
        case ANISO_MMCIF_U23:
          data = atomSetCollection.getAnisoBorU(atom);
          if (data == null) {
            atomSetCollection.setAnisoBorU(atom, data = new float[8], 8);
            // Ortep type 8: D = 2pi^2, C = 2, a*b*
          }
          int iType = (propertyOf[i] - ANISO_U11) % 6;
          data[iType] = parseFloat(field);
          break;
        case ANISO_B11:
        case ANISO_B22:
        case ANISO_B33:
        case ANISO_B12:
        case ANISO_B13:
        case ANISO_B23:
          data = atomSetCollection.getAnisoBorU(atom);
          if (data == null) {
            atomSetCollection.setAnisoBorU(atom, data = new float[8], 4);
            // Ortep Type 4: D = 1/4, C = 2, a*b*
          }
           int iTypeB = (propertyOf[i] - ANISO_B11) % 6;
           data[iTypeB] = parseFloat(field);
          break;
        case ANISO_Beta_11:
        case ANISO_Beta_22:
        case ANISO_Beta_33:
        case ANISO_Beta_12:
        case ANISO_Beta_13:
        case ANISO_Beta_23:
          data = atomSetCollection.getAnisoBorU(atom);
          if (data == null) {
            atomSetCollection.setAnisoBorU(atom, data = new float[8], 0);
            //Ortep Type 0: D = 1, c = 2 -- see org.jmol.symmetry/UnitCell.java
          }
          int iTypeBeta = (propertyOf[i] - ANISO_Beta_11) % 6;
          data[iTypeBeta] = parseFloat(field);
          break;
        }
      }
      if (Float.isNaN(atom.x) || Float.isNaN(atom.y) || Float.isNaN(atom.z)) {
        Logger.warn("atom " + atom.atomName
            + " has invalid/unknown coordinates");
      } else {
        if (isAnisoData || !filterAtom(atom, iAtom))
          continue;
        setAtomCoord(atom);
        atomSetCollection.addAtomWithMappedName(atom);
        if (atom.isHetero && htHetero != null) {
          atomSetCollection.setAtomSetAuxiliaryInfo("hetNames", htHetero);
          atomSetCollection.setAtomSetCollectionAuxiliaryInfo("hetNames",
              htHetero);
          htHetero = null;
        }
      }
    }
    if (isPDB) {
      atomSetCollection.setIsPDB();
      atomSetCollection.setAtomSetAuxiliaryInfo("isPDB", Boolean.TRUE);
    }
    atomSetCollection.setAtomSetAuxiliaryInfo("isCIF", Boolean.TRUE);
    return true;
  }
     
  ////////////////////////////////////////////////////////////////
  // bond data
  ////////////////////////////////////////////////////////////////

  final private static byte GEOM_BOND_ATOM_SITE_LABEL_1 = 0;
  final private static byte GEOM_BOND_ATOM_SITE_LABEL_2 = 1;
  final private static byte GEOM_BOND_DISTANCE = 2;
  //final private static byte GEOM_BOND_SITE_SYMMETRY_2 = 3;

  final private static String[] geomBondFields = { 
      "_geom_bond_atom_site_label_1",
      "_geom_bond_atom_site_label_2",
      "_geom_bond_distance",
    //  "_geom_bond_site_symmetry_2",
  };

  /**
   * 
   * reads bond data -- N_ijk symmetry business is ignored,
   * so we only indicate bonds within the unit cell to just the
   * original set of atoms. "connect" script or "set forceAutoBond"
   * will override these values.
   * 
   * @throws Exception
   */
  private void processGeomBondLoopBlock() throws Exception {
    parseLoopParameters(geomBondFields);
    for (int i = propertyCount; --i >= 0;)
      if (fieldOf[i] == NONE) {
        Logger.warn("?que? missing _geom_bond property:" + i);
        skipLoop();
        return;
      }

    String name1 = null;
    String name2 = null;
    while (tokenizer.getData()) {
      int atomIndex1 = -1;
      int atomIndex2 = -1;
      float distance = 0;
      float dx = 0;
      //String siteSym2 = null;
      for (int i = 0; i < tokenizer.fieldCount; ++i) {
        switch (fieldProperty(i)) {
        case NONE:
          break;
        case GEOM_BOND_ATOM_SITE_LABEL_1:
          atomIndex1 = atomSetCollection.getAtomNameIndex(name1 = field);
          break;
        case GEOM_BOND_ATOM_SITE_LABEL_2:
          atomIndex2 = atomSetCollection.getAtomNameIndex(name2 = field);
          break;
        case GEOM_BOND_DISTANCE:
          distance = parseFloat(field);
          int pt = field.indexOf('('); 
          if (pt >= 0) {
            char[] data = field.toCharArray();
            String sdx = field.substring(pt + 1, field.length() - 1);
            int n = sdx.length();
            for (int j = pt; --j >= 0;) {
              if (data[j] == '.')
                --j;
               data[j] = (--n < 0 ? '0' : sdx.charAt(n));
            }
            dx = parseFloat(String.valueOf(data));
            if (Float.isNaN(dx)) {
              Logger.info("error reading uncertainty for " + line);
              dx = 0.015f;
            }
            // TODO -- this is the full +/- (dx) in x.xxx(dx) -- is that too large?
          } else {
            dx = 0.015f;
          }
          break;
        //case GEOM_BOND_SITE_SYMMETRY_2:
          //siteSym2 = field;
          //break;
        }
      }
      if (atomIndex1 < 0 || atomIndex2 < 0)
        continue;
      if (distance > 0) 
        bondTypes.add(new Object[] { name1, name2, new Float(distance), new Float(dx) });
    }
  }
  
  ////////////////////////////////////////////////////////////////
  // HETATM identity
  ////////////////////////////////////////////////////////////////

  final private static byte NONPOLY_ENTITY_ID = 0;
  final private static byte NONPOLY_NAME = 1;
  final private static byte NONPOLY_COMP_ID = 2;

  final private static String[] nonpolyFields = { 
      "_pdbx_entity_nonpoly_entity_id",
      "_pdbx_entity_nonpoly_name", 
      "_pdbx_entity_nonpoly_comp_id", 
  };
  
  /**
   * 
   * optional nonloop format -- see 1jsa.cif
   * 
   */
  private String[] hetatmData;
  private void processNonpolyData() {
    if (hetatmData == null)
      hetatmData = new String[3];
    for (int i = nonpolyFields.length; --i >= 0;)
      if (isMatch(key, nonpolyFields[i])) {
        hetatmData[i] = data;
        break;
      }
    if (hetatmData[NONPOLY_NAME] == null || hetatmData[NONPOLY_COMP_ID] == null)
      return;
    addHetero(hetatmData[NONPOLY_COMP_ID], hetatmData[NONPOLY_NAME]);
    hetatmData = null;
  }


  final private static byte CHEM_COMP_ID = 0;
  final private static byte CHEM_COMP_NAME = 1;

  final private static String[] chemCompFields = { 
      "_chem_comp_id",
      "_chem_comp_name",  
  };
  

  /**
   * 
   * a general name definition field. Not all hetero
   * 
   * @throws Exception
   */
  private void processChemCompLoopBlock() throws Exception {
    parseLoopParameters(chemCompFields);
    while (tokenizer.getData()) {
      String groupName = null;
      String hetName = null;
      for (int i = 0; i < tokenizer.fieldCount; ++i) {
        switch (fieldProperty(i)) {
        case NONE:
          break;
        case CHEM_COMP_ID:
          groupName = field;
          break;
        case CHEM_COMP_NAME:
          hetName = field;
          break;
        }
      }
      if (groupName == null || hetName == null)
        return;
      addHetero(groupName, hetName);
    }
  }

  /**
   * 
   * a HETERO name definition field. Maybe not all hetero? nonpoly?
   * 
   * @throws Exception
   */
  private void processNonpolyLoopBlock() throws Exception {
    parseLoopParameters(nonpolyFields);
    while (tokenizer.getData()) {
      String groupName = null;
      String hetName = null;
      for (int i = 0; i < tokenizer.fieldCount; ++i) {
        switch (fieldProperty(i)) {
        case NONE:
        case NONPOLY_ENTITY_ID:
          break;
        case NONPOLY_COMP_ID:
          groupName = field;
          break;
        case NONPOLY_NAME:
          hetName = field;
          break;
        }
      }
      if (groupName == null || hetName == null)
        return;
      addHetero(groupName, hetName);
    }
  }

  private void addHetero(String groupName, String hetName) {
    if (!JmolAdapter.isHetero(groupName))
      return;
    if (htHetero == null)
      htHetero = new Hashtable<String, String>();
    htHetero.put(groupName, hetName);
    if (Logger.debugging) {
      Logger.debug("hetero: " + groupName + " = " + hetName);
    }
  }
  
  ////////////////////////////////////////////////////////////////
  // helix and turn structure data
  ////////////////////////////////////////////////////////////////

  final private static byte CONF_TYPE_ID = 0;
  final private static byte BEG_ASYM_ID = 1;
  final private static byte BEG_SEQ_ID = 2;
  final private static byte BEG_INS_CODE = 3;
  final private static byte END_ASYM_ID = 4;
  final private static byte END_SEQ_ID = 5;
  final private static byte END_INS_CODE = 6;
  final private static byte STRUCT_ID = 7;
  final private static byte SERIAL_NO = 8;
  final private static byte HELIX_CLASS = 9;

  final private static String[] structConfFields = { 
      "_struct_conf_conf_type_id",
      "_struct_conf_beg_auth_asym_id", 
      "_struct_conf_beg_auth_seq_id",
      "_struct_conf_pdbx_beg_PDB_ins_code",
      "_struct_conf_end_auth_asym_id", 
      "_struct_conf_end_auth_seq_id",
      "_struct_conf_pdbx_end_PDB_ins_code",
      "_struct_conf_id", 
      "_struct_conf_pdbx_PDB_helix_id", 
      "_struct_conf_pdbx_PDB_helix_class"
  };

  /**
   * identifies ranges for HELIX and TURN
   * 
   * @throws Exception
   */
  private void processStructConfLoopBlock() throws Exception {
    parseLoopParameters(structConfFields);
    for (int i = propertyCount; --i >= 0;)
      if (fieldOf[i] == NONE) {
        Logger.warn("?que? missing _struct_conf property:" + i);
        skipLoop();
        return;
      }
    while (tokenizer.getData()) {
      Structure structure = new Structure(Structure.PROTEIN_STRUCTURE_HELIX);
      for (int i = 0; i < tokenizer.fieldCount; ++i) {
        switch (fieldProperty(i)) {
        case NONE:
          break;
        case CONF_TYPE_ID:
          if (field.startsWith("TURN"))
            structure.structureType = structure.substructureType = Structure.PROTEIN_STRUCTURE_TURN;
          else if (!field.startsWith("HELX"))
            structure.structureType = structure.substructureType = Structure.PROTEIN_STRUCTURE_NONE;
          break;
        case BEG_ASYM_ID:
          structure.startChainID = firstChar;
          break;
        case BEG_SEQ_ID:
          structure.startSequenceNumber = parseInt(field);
          break;
        case BEG_INS_CODE:
          structure.startInsertionCode = firstChar;
          break;
        case END_ASYM_ID:
          structure.endChainID = firstChar;
          break;
        case END_SEQ_ID:
          structure.endSequenceNumber = parseInt(field);
          break;
        case HELIX_CLASS:
          structure.substructureType = Structure.getHelixType(parseInt(field));
          break;
        case END_INS_CODE:
          structure.endInsertionCode = firstChar;
          break;
        case STRUCT_ID:
          structure.structureID = field;
          break;
        case SERIAL_NO:
          structure.serialID = parseInt(field);
          break;
        }
      }
      atomSetCollection.addStructure(structure);
    }
  }
  ////////////////////////////////////////////////////////////////
  // sheet structure data
  ////////////////////////////////////////////////////////////////

  final private static byte SHEET_ID = 0;
  final private static byte STRAND_ID = 7;

  final private static String[] structSheetRangeFields = {
    "_struct_sheet_range_sheet_id",  //unused placeholder
    "_struct_sheet_range_beg_auth_asym_id",
    "_struct_sheet_range_beg_auth_seq_id",
    "_struct_sheet_range_pdbx_beg_PDB_ins_code",
    "_struct_sheet_range_end_auth_asym_id",
    "_struct_sheet_range_end_auth_seq_id",
    "_struct_sheet_range_pdbx_end_PDB_ins_code", 
    "_struct_sheet_range_id",
  };

  /**
   * 
   * identifies sheet ranges
   * 
   * @throws Exception
   */
  private void processStructSheetRangeLoopBlock() throws Exception {
    parseLoopParameters(structSheetRangeFields);
    for (int i = propertyCount; --i >= 0;)
      if (fieldOf[i] == NONE) {
        Logger.warn("?que? missing _struct_conf property:" + i);
        skipLoop();
        return;
      }
    while (tokenizer.getData()) {
      Structure structure = new Structure(Structure.PROTEIN_STRUCTURE_SHEET);
      for (int i = 0; i < tokenizer.fieldCount; ++i) {
        switch (fieldProperty(i)) {
        case BEG_ASYM_ID:
          structure.startChainID = firstChar;
          break;
        case BEG_SEQ_ID:
          structure.startSequenceNumber = parseInt(field);
          break;
        case BEG_INS_CODE:
          structure.startInsertionCode = firstChar;
          break;
        case END_ASYM_ID:
          structure.endChainID = firstChar;
          break;
        case END_SEQ_ID:
          structure.endSequenceNumber = parseInt(field);
          break;
        case END_INS_CODE:
          structure.endInsertionCode = firstChar;
          break;
        case SHEET_ID:
          structure.strandCount = 1;
          structure.structureID = field;
          break;
        case STRAND_ID:
          structure.serialID = parseInt(field);
          break;
        }
      }
      atomSetCollection.addStructure(structure);
    }
  }

  final private static byte SITE_ID = 0;
  final private static byte SITE_COMP_ID = 1;
  final private static byte SITE_ASYM_ID = 2;
  final private static byte SITE_SEQ_ID = 3;
  final private static byte SITE_INS_CODE = 4; //???

  final private static String[] structSiteRangeFields = {
    "_struct_site_gen_site_id",  
    "_struct_site_gen_auth_comp_id", 
    "_struct_site_gen_auth_asym_id", 
    "_struct_site_gen_auth_seq_id",  
    "_struct_site_gen_label_alt_id",  //should be an insertion code, not an alt ID? 
  };

  
  /*

loop_
_struct_site_gen.id 
_struct_site_gen.site_id 
_struct_site_gen.pdbx_num_res 
_struct_site_gen.label_comp_id 
_struct_site_gen.label_asym_id 
_struct_site_gen.label_seq_id 
_struct_site_gen.auth_comp_id 
_struct_site_gen.auth_asym_id 
_struct_site_gen.auth_seq_id 
_struct_site_gen.label_atom_id 
_struct_site_gen.label_alt_id 
_struct_site_gen.symmetry 
_struct_site_gen.details 
1 CAT 5 GLN A 92  GLN A 92  . . ? ? 
2 CAT 5 GLU A 58  GLU A 58  . . ? ? 
3 CAT 5 HIS A 40  HIS A 40  . . ? ? 
4 CAT 5 TYR A 38  TYR A 38  . . ? ? 
5 CAT 5 PHE A 100 PHE A 100 . . ? ? 
# 

*/
  
  //private int siteNum;
  private Map<String, Map<String, Object>> htSites;
  
  /**
   * 
   * identifies structure sites
   * 
   * @throws Exception
   */
  private void processStructSiteBlock() throws Exception {
    parseLoopParameters(structSiteRangeFields);
    for (int i = 3; --i >= 0;)
      if (fieldOf[i] == NONE) {
        Logger.warn("?que? missing _struct_site property:" + i);
        skipLoop();
        return;
      }
    String siteID = "";
    String seqNum = "";
    String insCode = "";
    String chainID = "";
    String resID = "";
    String group = "";
    Map<String, Object> htSite = null;
    htSites = new Hashtable<String, Map<String, Object>>();
    while (tokenizer.getData()) {
      for (int i = 0; i < tokenizer.fieldCount; ++i) {
        switch (fieldProperty(i)) {
        case SITE_ID:
          if (group != "") {
            String groups = (String) htSite.get("groups");
            groups += (groups.length() == 0 ? "" : ",") + group;
            group = "";
            htSite.put("groups", groups);
          }
          siteID = field;
          htSite = htSites.get(siteID);
          if (htSite == null) {
            htSite = new Hashtable<String, Object>();
            //htSite.put("seqNum", "site_" + (++siteNum));
            htSite.put("groups", "");
            htSites.put(siteID, htSite);
          }
          seqNum = "";
          insCode = "";
          chainID = "";
          resID = "";
          break;
        case SITE_COMP_ID:
          resID = field;
          break;
        case SITE_ASYM_ID:
          chainID = field;
          break;
        case SITE_SEQ_ID:
          seqNum = field;
          break;
        case SITE_INS_CODE: //optional
          insCode = field;
          break;
        }
        if (seqNum != "" && resID != "")
          group = "[" + resID + "]" + seqNum
            + (insCode.length() > 0 ?  "^" + insCode : "")
            + (chainID.length() > 0 ? ":" + chainID : "");
      }      
    }
    if (group != "") {
      String groups = (String) htSite.get("groups");
      groups += (groups.length() == 0 ? "" : ",") + group;
      group = "";
      htSite.put("groups", groups);
    }
  }

  ////////////////////////////////////////////////////////////////
  // symmetry operations
  ////////////////////////////////////////////////////////////////

  final private static byte SYMOP_XYZ = 0;
  final private static byte SYM_EQUIV_XYZ = 1;

  final private static String[] symmetryOperationsFields = {
      "_space_group_symop_operation_xyz", 
      "_symmetry_equiv_pos_as_xyz", 
  };

  /**
   * retrieves symmetry operations
   * 
   * @throws Exception
   */
  private void processSymmetryOperationsLoopBlock() throws Exception {
    parseLoopParameters(symmetryOperationsFields);
    int nRefs = 0;
    for (int i = propertyCount; --i >= 0;)
      if (fieldOf[i] != NONE)
        nRefs++;
    if (nRefs != 1) {
      Logger.warn("?que? _symmetry_equiv or _space_group_symop property not found");
      skipLoop();
      return;
    }
    while (tokenizer.getData()) {
      for (int i = 0; i < tokenizer.fieldCount; ++i) {
        switch (fieldProperty(i)) {
        case SYMOP_XYZ:
        case SYM_EQUIV_XYZ:
          setSymmetryOperator(field);
          break;
        }
      }
    }
  }
  
  private int fieldProperty(int i) {
    return ((field = tokenizer.loopData[i]).length() > 0 
        && (firstChar = field.charAt(0)) != '\0' ? 
            propertyOf[i] : NONE);
  }

  String field;
  
  private char firstChar;
  private int[] propertyOf = new int[100]; // should be enough
  private byte[] fieldOf = new byte[atomFields.length];
  private int propertyCount;
  
  
  /**
   * sets up arrays and variables for tokenizer.getData()
   * 
   * @param fields
   * @throws Exception
   */
  private void parseLoopParameters(String[] fields) throws Exception {
    tokenizer.fieldCount = 0;
    for (int i = fields.length; --i >= 0; )
      fieldOf[i] = NONE;

    propertyCount = fields.length;
    while (true) {
      String str = tokenizer.peekToken();
      if (str == null) {
        tokenizer.fieldCount = 0;
        break;
      }
      if (str.charAt(0) != '_')
        break;
      tokenizer.getTokenPeeked();
      propertyOf[tokenizer.fieldCount] = NONE;
      for (int i = fields.length; --i >= 0;)
        if (isMatch(str, fields[i])) {
          propertyOf[tokenizer.fieldCount] = i;
          fieldOf[i] = (byte) tokenizer.fieldCount;
          break;
        }
      tokenizer.fieldCount++;
    }
    if (tokenizer.fieldCount > 0)
      tokenizer.loopData = new String[tokenizer.fieldCount];
  }

  @Override
  public String readLine() throws Exception {
    super.readLine();
    if (line.indexOf("#jmolscript:") >= 0)
      checkLineForScript();
    return line;
  }
  
  /**
   * 
   * used for turning off fractional or nonfractional coord.
   * 
   * @param fieldIndex
   */
  private void disableField(int fieldIndex) {
    int i = fieldOf[fieldIndex];
    if (i != NONE)
        propertyOf[i] = NONE;
  }

  /**
   * 
   * skips all associated loop data
   * 
   * @throws Exception
   */
  private void skipLoop() throws Exception {
    String str;
    while ((str = tokenizer.peekToken()) != null && str.charAt(0) == '_')
      str  = tokenizer.getTokenPeeked();
    while (tokenizer.getNextDataToken() != null) {
    }
  }  
  
  /**
   * 
   * @param str1
   * @param str2
   * @return TRUE if a match
   */
  private static boolean isMatch(String str1, String str2) {
    int cch = str1.length();
    if (str2.length() != cch)
      return false;
    for (int i = cch; --i >= 0;) {
      char ch1 = str1.charAt(i);
      char ch2 = str2.charAt(i);
      if (ch1 == ch2)
        continue;
      if ((ch1 == '_' || ch1 == '.') && (ch2 == '_' || ch2 == '.'))
        continue;
      if (ch1 <= 'Z' && ch1 >= 'A')
        ch1 += 'a' - 'A';
      else if (ch2 <= 'Z' && ch2 >= 'A')
        ch2 += 'a' - 'A';
      if (ch1 != ch2)
        return false;
    }
    return true;
  }
  
  /////////////////////////////////////
  //  bonding and molecular 
  /////////////////////////////////////
  
  private float[] atomRadius;
  private BitSet[] bsConnected;
  private BitSet[] bsSets;
  final private Point3f ptOffset = new Point3f();
  private BitSet bsMolecule;
  private BitSet bsExclude;
  private int firstAtom;
  private int atomCount;
  private Atom[] atoms;
  
  /**
   * (1) If GEOM_BOND records are present, we
   *     (a) use them to generate bonds
   *     (b) add H atoms to bonds if necessary
   *     (c) turn off autoBonding ("hasBonds")
   * (2) If MOLECULAR, then we
   *     (a) use {1 1 1} if lattice is not defined
   *     (b) use atomSetCollection.bonds[] to construct 
   *         a preliminary molecule and connect as we go
   *     (c) check symmetry for connections to molecule in any
   *         one of the 27 3x3 adjacent cells
   *     (d) move those atoms and their connected branch set
   *     (e) iterate as necessary to get all atoms desired
   *     (f) delete unselected atoms
   *     (g) set all coordinates as Cartesians
   *     (h) remove all unit cell information
   */
  private void setBondingAndMolecules() {
    Logger.info("CIF creating molecule "
        + (bondTypes.size() > 0 ? " using GEOM_BOND records" : ""));
    atoms = atomSetCollection.getAtoms();
    firstAtom = atomSetCollection.getLastAtomSetAtomIndex();
    int nAtoms = atomSetCollection.getLastAtomSetAtomCount();
    atomCount = firstAtom + nAtoms;

    // get list of sites based on atom names

    bsSets = new BitSet[nAtoms];
    symmetry = atomSetCollection.getSymmetry();
    for (int i = firstAtom; i < atomCount; i++) {
      int ipt = atomSetCollection.getAtomNameIndex(atoms[i].atomName)
          - firstAtom;
      if (bsSets[ipt] == null)
        bsSets[ipt] = new BitSet();
      bsSets[ipt].set(i - firstAtom);
    }

    // if molecular, we need atom connection lists and radii

    if (isMolecular) {
      atomRadius = new float[atomCount];
      for (int i = firstAtom; i < atomCount; i++) {
        int elemno = atoms[i].elementNumber = JmolAdapter
            .getElementNumber(atoms[i].getElementSymbol());
        int charge = (atoms[i].formalCharge == Integer.MIN_VALUE ? 0
            : atoms[i].formalCharge);
        if (elemno > 0)
          atomRadius[i] = JmolAdapter.getBondingRadiusFloat(elemno, charge);
      }
      bsConnected = new BitSet[atomCount];
      for (int i = firstAtom; i < atomCount; i++)
        bsConnected[i] = new BitSet();

      // Set up a working set of atoms in the "molecule".

      bsMolecule = new BitSet();

      // Set up a working set of atoms that should be excluded 
      // because they would map onto an equivalent atom's position.

      bsExclude = new BitSet();
    }

    boolean isFirst = true;
    while (createBonds(isFirst)) {
      isFirst = false;
      // main loop continues until no new atoms are found
    }

    if (isMolecular) {

      // Set bsAtoms to control which atoms and 
      // bonds are delivered by the iterators.

      if (atomSetCollection.bsAtoms == null)
        atomSetCollection.bsAtoms = new BitSet();
      atomSetCollection.bsAtoms.clear(firstAtom, atomCount);
      atomSetCollection.bsAtoms.or(bsMolecule);
      atomSetCollection.bsAtoms.andNot(bsExclude);

      // Set atom positions to be Cartesians and clear out unit cell
      // so that the model displays without it.

      for (int i = firstAtom; i < atomCount; i++) {
        if (atomSetCollection.bsAtoms.get(i))
          symmetry.toCartesian(atoms[i], true);
        else if (Logger.debugging)
          Logger.info(molecularType + " removing " + i + " "
              + atoms[i].atomName + " " + atoms[i]);
      }
      atomSetCollection.setAtomSetAuxiliaryInfo("notionalUnitcell", null);
      if (nMolecular++ == atomSetCollection.getCurrentAtomSetIndex()) {
        atomSetCollection
            .clearGlobalBoolean(AtomSetCollection.GLOBAL_FRACTCOORD);
        atomSetCollection.clearGlobalBoolean(AtomSetCollection.GLOBAL_SYMMETRY);
        atomSetCollection
            .clearGlobalBoolean(AtomSetCollection.GLOBAL_UNITCELLS);
      }
      
    }
    
    // Set return info to enable desired defaults.

    if (bondTypes.size() > 0)
      atomSetCollection.setAtomSetAuxiliaryInfo("hasBonds", Boolean.TRUE);
    atomSetCollection.setAtomSetAuxiliaryInfo("fileHasUnitCell", Boolean.TRUE);

    // Clear temporary fields.

    bondTypes.clear();
    atomRadius = null;
    bsSets = null;
    bsConnected = null;
    bsMolecule = null;
    bsExclude = null;
  }

  /**
   * Use the site bitset to check for atoms that are within 
   * +/-dx Angstroms of the specified distances in GEOM_BOND
   * where dx is determined by the uncertainty (dx) in the record.
   * Note that this also "connects" the atoms that might have 
   * been moved in a previous iteration.
   * 
   * Also connect H atoms based on a distance <= 1.1 Angstrom
   * from a nearby atom. 
   * 
   * Then create molecules.
   * 
   * @param doInit 
   * @return TRUE if need to continue
   */
  private boolean createBonds(boolean doInit) {
    
    // process GEOM_BOND records
    for (int i = bondTypes.size(); --i >= 0;) {
      Object[] o = bondTypes.get(i);
      float distance = ((Float) o[2]).floatValue();
      float dx = ((Float) o[3]).floatValue();
      int iatom1 = atomSetCollection.getAtomNameIndex((String) o[0]);
      int iatom2 = atomSetCollection.getAtomNameIndex((String) o[1]);
      BitSet bs1 = bsSets[iatom1 - firstAtom];
      BitSet bs2 = bsSets[iatom2 - firstAtom];
      if (bs1 == null || bs2 == null)
        continue;
      for (int j = bs1.nextSetBit(0); j >= 0; j = bs1.nextSetBit(j + 1))
        for (int k = bs2.nextSetBit(0); k >= 0; k = bs2.nextSetBit(k + 1)) {
          if ((!isMolecular || !bsConnected[j + firstAtom].get(k))
              && symmetry.checkDistance(atoms[j + firstAtom], atoms[k
                  + firstAtom], distance, dx, 0, 0, 0, ptOffset))
            addNewBond(j + firstAtom, k + firstAtom);
        }
    }
    
    // do a quick check for H-X bonds if we have GEOM_BOND
    
    if (bondTypes.size() > 0)
      for (int i = firstAtom; i < atomCount; i++)
        if (atoms[i].elementNumber == 1) {
          boolean checkAltLoc = (atoms[i].alternateLocationID != '\0');
          for (int k = firstAtom; k < atomCount; k++)
            if (k != i && atoms[k].elementNumber != 1 && 
                (!checkAltLoc 
                    || atoms[k].alternateLocationID == '\0' 
                    || atoms[k].alternateLocationID == atoms[i].alternateLocationID)) {
              if (!bsConnected[i].get(k)
                  && symmetry.checkDistance(atoms[i], atoms[k], 1.1f, 0, 0, 0,
                      0, ptOffset))
                addNewBond(i, k);
            }
        }
    if (!isMolecular)
      return false;
    
    // generate the base atom set

    if (doInit)
      for (int i = firstAtom; i < atomCount; i++)
        if (atoms[i].atomSite + firstAtom == i && !bsMolecule.get(i))
          setBs(atoms, i, bsConnected, bsMolecule);
    
    // Now look through unchecked atoms for ones that
    // are within bonding distance of the "molecular" set
    // in any one of the 27 adjacent cells in 444 - 666.
    // If an atom is found, move it along with its "branch"
    // to the new location. BUT also check that we are
    // not overlaying another atom -- if that happens
    // go ahead and move it, but mark it as excluded.
    
    float bondTolerance = viewer.getBondTolerance();
    BitSet bsBranch = new BitSet();
    Point3f cart1 = new Point3f();
    Point3f cart2 = new Point3f();
    for (int i = firstAtom; i < atomCount; i++)
      if (!bsMolecule.get(i) && !bsExclude.get(i))
        for (int j = bsMolecule.nextSetBit(0); j >= 0; j = bsMolecule
            .nextSetBit(j + 1))
          if (symmetry.checkDistance(atoms[j], atoms[i], atomRadius[i]
              + atomRadius[j] + bondTolerance, 0, 1, 1, 1, ptOffset)) {
            setBs(atoms, i, bsConnected, bsBranch);
            for (int k = bsBranch.nextSetBit(0); k >= 0; k = bsBranch
                .nextSetBit(k + 1)) {
              atoms[k].add(ptOffset);
              cart1.set(atoms[k]);
              symmetry.toCartesian(cart1, true);
              BitSet bs = bsSets[atomSetCollection
                  .getAtomNameIndex(atoms[k].atomName)
                  - firstAtom];
              if (bs != null)
                for (int ii = bs.nextSetBit(0); ii >= 0; ii = bs.nextSetBit(ii + 1)) {
                  if (ii + firstAtom == k)
                    continue;
                  cart2.set(atoms[ii + firstAtom]);
                  symmetry.toCartesian(cart2, true);
                  if (cart2.distance(cart1) < 0.1f) {
                    bsExclude.set(k);
                    break;
                  }
                }
              bsMolecule.set(k);
            }
            return true;
          }
    return false;
  }

  /**
   * add the bond and mark it for molecular processing
   * 
   * @param i
   * @param j
   */
  private void addNewBond(int i, int j) {
    atomSetCollection.addNewBond(i, j);
    if (!isMolecular)
      return;
    bsConnected[i].set(j);
    bsConnected[j].set(i);
  }

  /**
   * iteratively run through connected atoms, adding them to the set
   * 
   * @param atoms
   * @param iatom
   * @param bsBonds
   * @param bs
   */
  private void setBs(Atom[] atoms, int iatom, BitSet[] bsBonds, BitSet bs) {
    BitSet bsBond = bsBonds[iatom];
    bs.set(iatom);
    for (int i = bsBond.nextSetBit(0); i >= 0; i = bsBond.nextSetBit(i + 1)) {
      if (!bs.get(i))
        setBs(atoms, i, bsBonds, bs);
    }
  }
}
