/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-11-23 12:49:25 -0600 (Fri, 23 Nov 2007) $
 * $Revision: 8655 $
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

package org.jmol.minimize;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.jmol.api.JmolEdge;
import org.jmol.api.MinimizerInterface;
import org.jmol.i18n.GT;
import org.jmol.minimize.forcefield.ForceField;
import org.jmol.modelset.Atom;
import org.jmol.modelset.AtomCollection;
import org.jmol.modelset.Bond;
import org.jmol.util.ArrayUtil;
import org.jmol.util.BitSetUtil;
import org.jmol.util.Elements;
import org.jmol.util.Escape;
import org.jmol.util.Logger;
import org.jmol.util.Parser;

import org.jmol.script.Token;
import org.jmol.viewer.Viewer;

public class Minimizer implements MinimizerInterface {

  public Viewer viewer;
  public Atom[] atoms;
  public MinAtom[] minAtoms;
  public MinBond[] minBonds;
  public BitSet bsMinFixed;
  private int atomCount;
  private int bondCount;
  private int[] atomMap; 
 
  public int[][] angles;
  public int[][] torsions;
  public double[] partialCharges;
  
  private int steps = 50;
  private double crit = 1e-3;

  private static List<String[]> atomTypes;
  private ForceField pFF;
  private String ff = "UFF";
  private BitSet bsTaint, bsSelected, bsAtoms;
  private BitSet bsFixedDefault;
  private BitSet bsFixed;
  private BitSet bsAromatic;
  
  public List<Object[]> constraints;
  
  private boolean isSilent;
  
  public Minimizer() {
  }

  public void setProperty(String propertyName, Object value) {
    if (propertyName.equals("cancel")) {
      stopMinimization(false);
      return;
    }
    if (propertyName.equals("clear")) {
      if (minAtoms != null) {
        stopMinimization(false);
        clear();
      }
      return;
    }
    if (propertyName.equals("constraint")) {
      addConstraint((Object[]) value);
      return;
    }
    if (propertyName.equals("fixed")) {
      bsFixedDefault = (BitSet) value;
      return;
    }
    if (propertyName.equals("stop")) {
      stopMinimization(true);
      return;
    }
    if (propertyName.equals("viewer")) {
      viewer = (Viewer) value;
      return;
    }
  }

  public Object getProperty(String propertyName, int param) {
    if (propertyName.equals("log")) {
      return (pFF == null ? "" : pFF.getLogData());
    }
    return null;
  }
  
  private Map<String, Object[]> constraintMap;

  private void addConstraint(Object[] c) {
    if (c == null)
      return;
    int[] atoms = (int[]) c[0];
    int nAtoms = atoms[0];
    if (nAtoms == 0) {
      constraints = null;
      return;
    }
    if (constraints == null) {
      constraints = new ArrayList<Object[]>();
      constraintMap = new Hashtable<String, Object[]>();
    }
    if (atoms[1] > atoms[nAtoms]) {
        ArrayUtil.swap(atoms, 1, nAtoms);
        if (nAtoms == 4)
          ArrayUtil.swap(atoms, 2, 3);
    }
    String id = Escape.escape(atoms);
    Object[] c1 = constraintMap.get(id);
    if (c1 != null) {
      c1[2] = c[2]; // just set target value
      return;
    }
    constraintMap.put(id, c);
    constraints.add(c);
  }
    
  private void clear() {
    setMinimizationOn(false);
    atomCount = 0;
    bondCount = 0;
    bsAromatic = null;
    atoms = null;
    minAtoms = null;
    minBonds = null;
    angles = null;
    torsions = null;
    partialCharges = null;
    coordSaved = null;
    atomMap = null;
    bsTaint = null;
    bsAtoms = null;
    bsFixed = null;
    bsFixedDefault = null;
    bsMinFixed = null;
    bsSelected = null;
    constraints = null;
    constraintMap = null;
    pFF = null;
    //  viewer = null;
  }
  
  public boolean minimize(int steps, double crit, BitSet bsSelected,
                          BitSet bsFixed, boolean haveFixed, boolean forceSilent) {
    isSilent = (forceSilent || viewer.getBooleanProperty("minimizationSilent"));
    Object val;
    if (steps == Integer.MAX_VALUE) {
      val = viewer.getParameter("minimizationSteps");
      if (val != null && val instanceof Integer)
        steps = ((Integer) val).intValue();
    }
    this.steps = steps;

    // if the user indicated minimize ... FIX ... or we don't have any defualt,
    // use the bsFixed coming in here, which is set to "nearby and in frame" in that case. 
    // and if something is fixed, then AND it with "nearby and in frame" as well.
    if (!haveFixed && bsFixedDefault != null)
      bsFixed.and(bsFixedDefault);
    if (crit <= 0) {
      val = viewer.getParameter("minimizationCriterion");
      if (val != null && val instanceof Float)
        crit = ((Float) val).floatValue();
    }
    this.crit = Math.max(crit, 0.0001);

    if (minimizationOn)
      return false;

    Logger.info("minimize: initializing (steps = " + steps + " criterion = "
        + crit + ") ...");

    getForceField();
    if (pFF == null) {
      Logger.error(GT._("Could not get class for force field {0}", ff));
      return false;
    }
    if (bsSelected.cardinality() == 0) {
      Logger.error(GT._("No atoms selected -- nothing to do!"));
      return false;
    }
    atoms = viewer.getModelSet().atoms;
    bsAtoms = BitSetUtil.copy(bsSelected);
    if (bsFixed != null)
      bsAtoms.or(bsFixed);
    atomCount = bsAtoms.cardinality();

    boolean sameAtoms = BitSetUtil.areEqual(bsSelected, this.bsSelected);
    this.bsSelected = bsSelected;
    if (!sameAtoms)
      bsAromatic = null;
    if ((!sameAtoms || !BitSetUtil.areEqual(bsFixed, this.bsFixed))
        && !setupMinimization()) {
      clear();
      return false;
    }
    if (steps > 0) {
      bsTaint = BitSetUtil.copy(bsAtoms);
      BitSetUtil.andNot(bsTaint, bsFixed);
      viewer.setTaintedAtoms(bsTaint, AtomCollection.TAINT_COORD);
    }
    if (bsFixed != null)
      this.bsFixed = bsFixed;
    setAtomPositions();

    if (constraints != null) {
      for (int i = constraints.size(); --i >= 0;) {
        Object[] constraint = constraints.get(i);
        int[] aList = (int[]) constraint[0];
        int[] minList = (int[]) constraint[1];
        int nAtoms = aList[0] = Math.abs(aList[0]);
        for (int j = 1; j <= nAtoms; j++) {
          if (steps <= 0 || !bsAtoms.get(aList[j])) {
            aList[0] = -nAtoms; // disable
            break;
          }
          minList[j - 1] = atomMap[aList[j]];
        }
      }
    }

    pFF.setConstraints(this);

    // minimize and store values

    if (steps <= 0)
      getEnergyOnly();
    else if (isSilent || !viewer.useMinimizationThread())
      minimizeWithoutThread();
    else
      setMinimizationOn(true);
    return true;
  }

  private boolean setupMinimization() {

    // add all atoms

    coordSaved = null;
    atomMap = new int[atoms.length];
    minAtoms = new MinAtom[atomCount];
    int elemnoMax = 0;
    BitSet bsElements = new BitSet();
    for (int i = bsAtoms.nextSetBit(0), pt = 0; i >= 0; i = bsAtoms
        .nextSetBit(i + 1), pt++) {
      Atom atom = atoms[i];
      atomMap[i] = pt;
      int atomicNo = atoms[i].getElementNumber();
      elemnoMax = Math.max(elemnoMax, atomicNo);
      bsElements.set(atomicNo);
      minAtoms[pt] = new MinAtom(pt, atom, new double[] { atom.x, atom.y,
          atom.z }, null);
    }

    Logger.info(GT._("{0} atoms will be minimized.", "" + atomCount));
    Logger.info("minimize: creating bonds...");

    // add all bonds
    List<int[]> bondInfo = new ArrayList<int[]>();
    bondCount = 0;
    for (int i = bsAtoms.nextSetBit(0); i >= 0; i = bsAtoms.nextSetBit(i + 1)) {
      Bond[] bonds = atoms[i].getBonds();
      if (bonds != null)
        for (int j = 0; j < bonds.length; j++) {
          int i2 = atoms[i].getBondedAtomIndex(j);
          if (i2 > i && bsAtoms.get(i2)) {
            int bondOrder = bonds[j].getCovalentOrder();
            switch (bondOrder) {
            case 1:
            case 2:
            case 3:
              break;
            case JmolEdge.BOND_AROMATIC:
              bondOrder = 5;
              break;
            default:
              bondOrder = 1;
            }
            bondCount++;
            bondInfo.add(new int[] { atomMap[i], atomMap[i2], bondOrder });
          }
        }
    }
    int[] atomIndexes;

    minBonds = new MinBond[bondCount];
    for (int i = 0; i < bondCount; i++) {
      MinBond bond = minBonds[i] = new MinBond(atomIndexes = bondInfo.get(i), false, false);
      int atom1 = atomIndexes[0];
      int atom2 = atomIndexes[1];
      minAtoms[atom1].bonds.add(bond);
      minAtoms[atom2].bonds.add(bond);
      minAtoms[atom1].nBonds++;
      minAtoms[atom2].nBonds++;
    }

    for (int i = 0; i < atomCount; i++)
      atomIndexes = minAtoms[i].getBondedAtomIndexes();

    // set the atom types

    Logger.info("minimize: setting atom types...");

    if (atomTypes == null)
      atomTypes = getAtomTypes();
    if (atomTypes == null)
      return false;
    int nElements = atomTypes.size();
    bsElements.clear(0);
    for (int i = 0; i < nElements; i++) {
      String[] data = atomTypes.get(i);
      String smarts = data[0];
      if (smarts == null)
        continue;
      BitSet search = getSearch(smarts, elemnoMax, bsElements);
      // if the 0 bit in bsElements gets set, then the element is not present,
      // and there is no need to search for it;
      // if search is null, then we are done -- max elemno exceeded
      if (bsElements.get(0))
        bsElements.clear(0);
      else if (search == null)
        break;
      else
        for (int j = bsAtoms.nextSetBit(0), pt = 0; j < atoms.length && j >= 0; j = bsAtoms.nextSetBit(j + 1)) {
            if (search.get(j)) {
              minAtoms[pt].type = data[1].intern();
              //System.out.println("pt=" +pt + " UFF type=" + data[1]);
            }
            pt++;
          }
    }

    // set the model

    Logger.info("minimize: getting angles...");
    getAngles();
    Logger.info("minimize: getting torsions...");
    getTorsions();

    pFF.setModel(this);

    if (!pFF.setup()) {
      Logger.error(GT._("could not setup force field {0}", ff));
      return false;
    }
    return true;
  }
  
  private void setAtomPositions() {
    for (int i = 0; i < atomCount; i++)
      minAtoms[i].set();
    bsMinFixed = null;
    if (bsFixed != null) {
      bsMinFixed = new BitSet();
      for (int i = bsAtoms.nextSetBit(0), pt = 0; i >= 0; i = bsAtoms
          .nextSetBit(i + 1), pt++)
        if (bsFixed.get(i))
          bsMinFixed.set(pt);
    }
  }
  //////////////// atom type support //////////////////
  
  
  private final static int TOKEN_ELEMENT_ONLY = 0;
  private final static int TOKEN_ELEMENT_CHARGED = 1;
  private final static int TOKEN_ELEMENT_CONNECTED = 2;
  private final static int TOKEN_AROMATIC = 3;
  private final static int TOKEN_ELEMENT_SP = 4;
 // private final static int TOKEN_ELEMENT_SP2 = 5;
  private final static int TOKEN_ELEMENT_ALLYLIC = 6;
  
  /*
Token[keyword(0x80064) value="expressionBegin"]
Token[keyword(0x2880034) intValue=2621446(0x280006) value="="]
Token[integer(0x2) intValue=6(0x6) value="6"]
Token[keyword(0x880020) value="and"]
Token[keyword(0x108002a) value="connected"]
Token[keyword(0x880000) value="("]
Token[integer(0x2) intValue=3(0x3) value="3"]
Token[keyword(0x880001) value=")"]

   */
  private final static int PT_ELEMENT = 2;
  private final static int PT_CHARGE = 5;
  private final static int PT_CONNECT = 6;
  
  private final static Token[][] tokenTypes = new Token[][] {
         /*0*/  new Token[]{
       Token.tokenExpressionBegin,
       new Token(Token.opEQ, Token.elemno), 
       Token.intToken(0), //2
       Token.tokenExpressionEnd},
         /*1*/  new Token[]{
       Token.tokenExpressionBegin,
       new Token(Token.opEQ, Token.elemno), 
       Token.intToken(0), //2
       Token.tokenAnd, 
       new Token(Token.opEQ, Token.formalcharge),
       Token.intToken(0), //5
       Token.tokenExpressionEnd},
         /*2*/  new Token[]{
       Token.tokenExpressionBegin,
       new Token(Token.opEQ, Token.elemno), 
       Token.intToken(0)  ,  // 2
       Token.tokenAnd, 
       Token.tokenConnected,
       Token.tokenLeftParen,
       Token.intToken(0),   // 6
       Token.tokenRightParen,
       Token.tokenExpressionEnd},
         /*3*/  new Token[]{     // not used this way
       Token.tokenExpressionBegin,
       new Token(Token.identifier, "flatring"),
       Token.tokenExpressionEnd},
         /*4*/  new Token[]{ //sp == connected(1,"triple") or connected(2, "double")
       Token.tokenExpressionBegin,
       new Token(Token.opEQ, Token.elemno), 
       Token.intToken(0)  ,  // 2
       Token.tokenAnd, 
       Token.tokenLeftParen,
       Token.tokenConnected,
       Token.tokenLeftParen,
       Token.intToken(1),
       Token.tokenComma,
       new Token(Token.string, "triple"),
       Token.tokenRightParen,
       Token.tokenOr,
       Token.tokenConnected,
       Token.tokenLeftParen,
       Token.intToken(2),
       Token.tokenComma,
       new Token(Token.string, "double"),
       Token.tokenRightParen,
       Token.tokenRightParen,
       Token.tokenExpressionEnd},
         /*5*/  new Token[]{  // sp2 == connected(1, double)
       Token.tokenExpressionBegin,
       new Token(Token.opEQ, Token.elemno), 
       Token.intToken(0)  ,  // 2
       Token.tokenAnd, 
       new Token(Token.connected, "connected"),
       Token.tokenLeftParen,
       Token.intToken(1),
       Token.tokenComma,
       new Token(Token.string, "double"),
       Token.tokenRightParen,
       Token.tokenExpressionEnd},
       /*6*/  new Token[]{ //Nv vinylic == connected(3) && connected(connected("double"))
       Token.tokenExpressionBegin,
       new Token(Token.opEQ, Token.elemno), 
       Token.intToken(0)  ,  // 2
       Token.tokenAnd, 
       Token.tokenConnected,
       Token.tokenLeftParen,
       Token.intToken(3),
       Token.tokenRightParen,
       Token.tokenAnd, 
       Token.tokenConnected,
       Token.tokenLeftParen,
       Token.tokenConnected,
       Token.tokenLeftParen,
       new Token(Token.string, "double"),
       Token.tokenRightParen,
       Token.tokenRightParen,
       Token.tokenExpressionEnd},
  };
  
  /*
  Token[keyword(0x108002a) value="connected"]
        Token[keyword(0x880000) value="("]
        Token[integer(0x2) intValue=1(0x1) value="1"]
        Token[keyword(0x880008) value=","]
        Token[string(0x4) value="triple"]
        Token[keyword(0x880001) value=")"]
        Token[keyword(0x880018) value="or"]
        Token[keyword(0x108002a) value="connected"]
        Token[keyword(0x880000) value="("]
        Token[integer(0x2) intValue=2(0x2) value="2"]
        Token[keyword(0x880008) value=","]
        Token[string(0x4) value="double"]
        Token[keyword(0x880001) value=")"]
        Token[keyword(0x80065) value="expressionEnd"]
  */
  private BitSet getSearch(String smarts, int elemnoMax, BitSet bsElements) {
    /*
     * 
     * only a few possibilities --
     *
     * [#n] an element --> elemno=n
     * [XDn] element X with n connections
     * [X^n] element X with n+1 connections
     * [X+n] element X with formal charge +n
     * 
     */

    Token[] search = null;

    int len = smarts.length();
    search = tokenTypes[TOKEN_ELEMENT_ONLY];
    int n = smarts.charAt(len - 2) - '0';
    int elemNo = 0;
    if (n >= 10)
      n = 0;
    boolean isAromatic = false;
    if (smarts.charAt(1) == '#') {
      elemNo = Parser.parseInt(smarts.substring(2, len - 1));
    } else {
      String s = smarts.substring(1, (n > 0 ? len - 3 : len - 1));
      if (s.equals(s.toLowerCase())) {
        s = s.toUpperCase();
        isAromatic = true;
      }
      elemNo = Elements.elementNumberFromSymbol(s, false);
    }
    if (elemNo > elemnoMax)
      return null;
    if (!bsElements.get(elemNo)) {
      bsElements.set(0);
      return null;
    }
    switch (smarts.charAt(len - 3)) {
    case 'D':
      search = tokenTypes[TOKEN_ELEMENT_CONNECTED];
      search[PT_CONNECT].intValue = n;
      break;
    case '^': //1 or 2
      search = tokenTypes[TOKEN_ELEMENT_SP + (n - 1)];
      break;
    case '+':
      search = tokenTypes[TOKEN_ELEMENT_CHARGED];
      search[PT_CHARGE].intValue = n;
      break;
    case '-':
      search = tokenTypes[TOKEN_ELEMENT_CHARGED];
      search[PT_CHARGE].intValue = -n;
      break;
    case 'A': // amide/allylic (also just plain C=X)
      search = tokenTypes[TOKEN_ELEMENT_ALLYLIC];
      break;
    } 
    search[PT_ELEMENT].intValue = elemNo;
    Object v = viewer.evaluateExpression(search);
    if (!(v instanceof BitSet))
      return null;
    BitSet bs = (BitSet) v;
    if (isAromatic && bs.cardinality() > 0) {
      if (bsAromatic == null)
        bsAromatic = (BitSet) viewer.evaluateExpression(tokenTypes[TOKEN_AROMATIC]);
      bs.and(bsAromatic);
    }
    if (Logger.debugging && bs.cardinality() > 0)
      Logger.debug(smarts + " minimize atoms=" + bs);
    return bs;
  }
  
  public void getAngles() {

    List<int[]> vAngles = new ArrayList<int[]>();

    for (int ib = 0; ib < atomCount; ib++) {
      MinAtom atomB = minAtoms[ib];
      int n = atomB.nBonds;
      if (n < 2)
        continue;
      //for all central atoms....
      int[] atomList = atomB.getBondedAtomIndexes();
      for (int ia = 0; ia < n - 1; ia++)
        for (int ic = ia + 1; ic < n; ic++) {
          // note! center in center; ia < ic  (not like OpenBabel)
          vAngles.add(new int[] { atomList[ia], ib, atomList[ic] });
/*          System.out.println (" " 
              + minAtoms[atomList[ia]].getIdentity() + " -- " 
              + minAtoms[ib].getIdentity() + " -- " 
              + minAtoms[atomList[ic]].getIdentity());
*/        }
    }
    
    angles = new int[vAngles.size()][];
    for (int i = vAngles.size(); --i >= 0; )
      angles[i] = vAngles.get(i);
    Logger.info(angles.length + " angles");
  }

  public void getTorsions() {

    List<int[]> vTorsions = new ArrayList<int[]>();

    // extend all angles a-b-c by one, but only
    // when when c > b -- other possibility will be found
    // starting with angle d-c-b
    for (int i = angles.length; --i >= 0;) {
      int[] angle = angles[i];
      int ia = angle[0];
      int ib = angle[1];
      int ic = angle[2];
      if (ic > ib && minAtoms[ic].nBonds != 1) {
        int[] atomList = minAtoms[ic].getBondedAtomIndexes();
        for (int j = 0; j < atomList.length; j++) {
          int id = atomList[j];
          if (id != ia && id != ib) {
            vTorsions.add(new int[] { ia, ib, ic, id });
/*            System.out.println(" " + minAtoms[ia].getIdentity() + " -- "
                + minAtoms[ib].getIdentity() + " -- "
                + minAtoms[ic].getIdentity() + " -- "
                + minAtoms[id].getIdentity() + " ");
*/
          }
        }
      }
      if (ia > ib && minAtoms[ia].nBonds != 1) {
        int[] atomList = minAtoms[ia].getBondedAtomIndexes();
        for (int j = 0; j < atomList.length; j++) {
          int id = atomList[j];
          if (id != ic && id != ib) {
            vTorsions.add(new int[] { ic, ib, ia, id });
/*            System.out.println(" " + minAtoms[ic].getIdentity() + " -- "
                + minAtoms[ib].getIdentity() + " -- "
                + minAtoms[ia].getIdentity() + " -- "
                + minAtoms[id].getIdentity() + " ");
*/          }
        }
      }
      
    }

    torsions = new int[vTorsions.size()][];
    for (int i = vTorsions.size(); --i >= 0;)
      torsions[i] = vTorsions.get(i);

    Logger.info(torsions.length + " torsions");

  }

  
  
  
  ///////////////////////////// minimize //////////////////////
  
 
  public ForceField getForceField() {
    if (pFF == null) {
      try {
        String className = getClass().getName();
        className = className.substring(0, className.lastIndexOf(".")) 
        + ".forcefield.ForceField" + ff;
        Logger.info( "minimize: using " + className);
        pFF = (ForceField) Class.forName(className).newInstance();
      } catch (Exception e) {
        Logger.error(e.getMessage());
      }
    }
    //Logger.info("minimize: forcefield = " + pFF);
    return pFF;
  }
  
  public List<String[]> getAtomTypes() {
    getForceField();
    return (pFF == null ? null : pFF.getAtomTypes());
  }
  
  /* ***************************************************************
   * Minimization thead support
   ****************************************************************/

  boolean minimizationOn;

  private MinimizationThread minimizationThread;

  private void setMinimizationOn(boolean minimizationOn) {
    //System.out.println("Minimizer setMinimizationOn "+ minimizationOn + " " + minimizationThread + " " + this.minimizationOn);
    this.minimizationOn = minimizationOn;
    if (!minimizationOn) {
      if (minimizationThread != null) {
        //minimizationThread.interrupt(); // did not seem to work with applet
        minimizationThread = null;
      }
      return;
    }
    if (minimizationThread == null) {
      minimizationThread = new MinimizationThread();
      minimizationThread.start();
    }
  }

  private void getEnergyOnly() {
    if (pFF == null || viewer == null)
      return;
    pFF.steepestDescentInitialize(steps, crit);      
    viewer.setFloatProperty("_minimizationEnergyDiff", 0);
    viewer.setFloatProperty("_minimizationEnergy", (float) pFF.getEnergy());
    viewer.setStringProperty("_minimizationStatus", "calculate");
    viewer.notifyMinimizationStatus();
  }
  
  public boolean startMinimization() {
   try {
      Logger.info("minimizer: startMinimization");
      viewer.setIntProperty("_minimizationStep", 0);
      viewer.setStringProperty("_minimizationStatus", "starting");
      viewer.setFloatProperty("_minimizationEnergy", 0);
      viewer.setFloatProperty("_minimizationEnergyDiff", 0);
      viewer.notifyMinimizationStatus();
      viewer.saveCoordinates("minimize", bsTaint);
      pFF.steepestDescentInitialize(steps, crit);
      viewer.setFloatProperty("_minimizationEnergy", (float) pFF.getEnergy());
      saveCoordinates();
    } catch (Exception e) {
      Logger.error("minimization error viewer=" + viewer + " pFF = " + pFF);
      return false;
    }
    minimizationOn = true;
    return true;
  }

  boolean stepMinimization() {
    if (!minimizationOn)
      return false;
    boolean doRefresh = (!isSilent && viewer.getBooleanProperty("minimizationRefresh"));
    viewer.setStringProperty("_minimizationStatus", "running");
    boolean going = pFF.steepestDescentTakeNSteps(1);
    int currentStep = pFF.getCurrentStep();
    viewer.setIntProperty("_minimizationStep", currentStep);
    viewer.setFloatProperty("_minimizationEnergy", (float) pFF.getEnergy());
    viewer.setFloatProperty("_minimizationEnergyDiff", (float) pFF.getEnergyDiff());
    viewer.notifyMinimizationStatus();
    if (doRefresh) {
      updateAtomXYZ();
      viewer.refresh(3, "minimization step " + currentStep);
    }
    return going;
  }

  void endMinimization() {
    updateAtomXYZ();
    setMinimizationOn(false);
    boolean failed = pFF.detectExplosion();
    if (failed)
      restoreCoordinates();
    viewer.setIntProperty("_minimizationStep", pFF.getCurrentStep());
    viewer.setFloatProperty("_minimizationEnergy", (float) pFF.getEnergy());
    viewer.setStringProperty("_minimizationStatus", (failed ? "failed" : "done"));
    viewer.notifyMinimizationStatus();
    viewer.refresh(3, "Minimizer:done" + (failed ? " EXPLODED" : "OK"));
    Logger.info("minimizer: endMinimization");
}

  double[][] coordSaved;
  
  private void saveCoordinates() {
    if (coordSaved == null)
      coordSaved = new double[atomCount][3];
    for (int i = 0; i < atomCount; i++) 
      for (int j = 0; j < 3; j++)
        coordSaved[i][j] = minAtoms[i].coord[j];
  }
  
  private void restoreCoordinates() {
    if (coordSaved == null)
      return;
    for (int i = 0; i < atomCount; i++) 
      for (int j = 0; j < 3; j++)
        minAtoms[i].coord[j] = coordSaved[i][j];
    updateAtomXYZ();
  }

  private void stopMinimization(boolean coordAreOK) {
    if (!minimizationOn)
      return;
    setMinimizationOn(false);
    if (coordAreOK)
      endMinimization();
    else
      restoreCoordinates();
  }
  
  void updateAtomXYZ() {
    if (steps <= 0)
      return;
    for (int i = 0; i < atomCount; i++) {
      MinAtom minAtom = minAtoms[i];
      Atom atom = minAtom.atom;
      atom.x = (float) minAtom.coord[0];
      atom.y = (float) minAtom.coord[1];
      atom.z = (float) minAtom.coord[2];
    }
    viewer.refreshMeasures(false);
  }

  private void minimizeWithoutThread() {
    //for batch operation
    if (!startMinimization())
      return;
    while (stepMinimization()) {
    }
    endMinimization();
  }
  
  class MinimizationThread extends Thread {
    
    MinimizationThread() {
      this.setName("MinimizationThread");
    }
    
    @Override
    public void run() {
      long startTime = System.currentTimeMillis();
      long lastRepaintTime = startTime;
      
      //should save the atom coordinates
      if (!startMinimization())
          return;
      try {
        do {
          long currentTime = System.currentTimeMillis();
          int elapsed = (int) (currentTime - lastRepaintTime);
          int sleepTime = 33 - elapsed;
          if (sleepTime > 0)
            Thread.sleep(sleepTime);
          lastRepaintTime = currentTime = System.currentTimeMillis();
          if (!stepMinimization())
            endMinimization();            
          elapsed = (int) (currentTime - startTime);
        } while (minimizationOn && !isInterrupted());
      } catch (Exception e) {
        if (minimizationOn)
          Logger.error(e.getMessage());
      }
    }
  }

  public void report(String msg, boolean isEcho) {
    if (isSilent)
      Logger.info(msg);
    else if (isEcho)
      viewer.showString(msg, false);
    else
      viewer.scriptEcho(msg);    
  }
}
