/* $RCSfile$
 * $Author: nicove $
 * $Date: 2010-08-06 16:30:20 -0700 (Fri, 06 Aug 2010) $
 * $Revision: 13880 $
 *
 * Copyright (C) 2003-2005  Miguel, Jmol Development
 *
 * Contact: jmol-developers@lists.sf.net, jmol-developers@lists.sf.net
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
package org.jmol.viewer;

import org.jmol.script.Token;
import org.jmol.util.ArrayUtil;
import org.jmol.util.BitSetUtil;
import org.jmol.util.Escape;

import org.jmol.api.JmolSelectionListener;
import org.jmol.i18n.GT;
import org.jmol.modelset.ModelSet;

import java.util.BitSet;
import java.util.Hashtable;
import java.util.Map;

class SelectionManager {

  private Viewer viewer;

  private JmolSelectionListener[] listeners = new JmolSelectionListener[0];

  SelectionManager(Viewer viewer) {
    this.viewer = viewer;
  }

  private final BitSet bsHidden = new BitSet();
  private final BitSet bsSelection = new BitSet();
  private final BitSet bsFixed = new BitSet();

  BitSet bsSubset; // set in Eval and only pointed to here
  private BitSet bsDeleted;

  void deleteModelAtoms(BitSet bsDeleted) {
    BitSetUtil.deleteBits(bsHidden, bsDeleted);
    BitSetUtil.deleteBits(bsSelection, bsDeleted);
    BitSetUtil.deleteBits(bsSubset, bsDeleted);
    BitSetUtil.deleteBits(bsFixed, bsDeleted);
    BitSetUtil.deleteBits(this.bsDeleted, bsDeleted);
  }


  // this is a tri-state. the value -1 means unknown
  private final static int TRUE = 1;
  private final static int FALSE = 0;
  private final static int UNKNOWN = -1;
  private int empty = TRUE;

  private boolean hideNotSelected;

  void clear() {
    clearSelection(true);
    hide(null, true);
    setSelectionSubset(null);
    bsDeleted = null;
    setMotionFixedAtoms(null);
  }

  void hide(BitSet bs, boolean isQuiet) {
    bsHidden.clear();
    if (bs != null)
      bsHidden.or(bs);
    ModelSet modelSet = viewer.getModelSet();
    if (modelSet != null)
      modelSet.setBsHidden(bsHidden);
    if (!isQuiet)
      viewer.reportSelection(GT._("{0} atoms hidden", ""
          + bsHidden.cardinality()));
  }

  void display(BitSet bsAll, BitSet bs, boolean isQuiet) {
    if (bs == null) {
      bsHidden.clear();
    } else {
      bsHidden.or(bsAll);
      bsHidden.andNot(bs);
    }
    BitSetUtil.andNot(bsHidden, bsDeleted);
    ModelSet modelSet = viewer.getModelSet();
    if (modelSet != null)
      modelSet.setBsHidden(bsHidden);
    if (!isQuiet)
      viewer.reportSelection(GT._("{0} atoms hidden", ""
          + bsHidden.cardinality()));
  }

  BitSet getHiddenSet() {
    return bsHidden;
  }

  boolean getHideNotSelected() {
    return hideNotSelected;
  }

  void setHideNotSelected(boolean TF) {
    hideNotSelected = TF;
    if (TF)
      selectionChanged(false);
  }

  boolean isSelected(int atomIndex) {
    return (atomIndex >= 0 && bsSelection.get(atomIndex));
  }

  void select(BitSet bs, boolean isQuiet) {
    if (bs == null) {
      selectAll(true);
      if (!viewer.getRasmolSetting(Token.hydrogen))
        excludeSelectionSet(viewer.getAtomBits(Token.hydrogen, null));
      if (!viewer.getRasmolSetting(Token.hetero))
        excludeSelectionSet(viewer.getAtomBits(Token.hetero, null));
      selectionChanged(false);
    } else {
      setSelectionSet(bs);
    }
    boolean reportChime = viewer.getMessageStyleChime();
    if (!reportChime && isQuiet)
      return;
    int n = getSelectionCount();
    if (reportChime)
      viewer.reportSelection((n == 0 ? "No atoms" : n == 1 ? "1 atom" : n
          + " atoms")
          + " selected!");
    else if (!isQuiet)
      viewer.reportSelection(GT._("{0} atoms selected", n));
  }

  void selectAll(boolean isQuiet) {
    int count = viewer.getAtomCount();
    empty = (count == 0) ? TRUE : FALSE;
    for (int i = count; --i >= 0;)
      bsSelection.set(i);
    BitSetUtil.andNot(bsSelection, bsDeleted);
    selectionChanged(isQuiet);
  }

  void clearSelection(boolean isQuiet) {
    setHideNotSelected(false);
    bsSelection.clear();
    empty = TRUE;
    selectionChanged(isQuiet);
  }

  public boolean isAtomSelected(int atomIndex) {
    return (
        (bsSubset == null || bsSubset.get(atomIndex))
        && bsDeleted == null || !bsDeleted.get(atomIndex))
        && bsSelection.get(atomIndex);
  }
  
  public void setSelectedAtom(int atomIndex, boolean TF) {
    if (atomIndex < 0) {
      selectionChanged(true);
      return;
    }
    if (bsSubset != null && !bsSubset.get(atomIndex)
        || bsDeleted != null && bsDeleted.get(atomIndex))
      return;
    bsSelection.set(atomIndex, TF);
    if (TF)
      empty = FALSE;
    else
      empty = UNKNOWN;
  }

  void setSelectionSet(BitSet set) {
    bsSelection.clear();
    if (set != null)
      bsSelection.or(set);
    empty = UNKNOWN;
    selectionChanged(false);
  }

  void setSelectionSubset(BitSet bs) {

    // for informational purposes only
    // the real copy is in Eval so that eval operations
    // can all use it directly, and so that all these
    // operations still work properly on the full set of atoms

    bsSubset = bs;
  }

  boolean isInSelectionSubset(int atomIndex) {
    return (atomIndex < 0 || bsSubset == null || bsSubset.get(atomIndex));
  }

  void invertSelection() {
    BitSetUtil.invertInPlace(bsSelection, viewer.getAtomCount());
    empty = (bsSelection.length() > 0 ? FALSE : TRUE);
    selectionChanged(false);
  }

  private void excludeSelectionSet(BitSet setExclude) {
    if (setExclude == null || empty == TRUE)
      return;
    bsSelection.andNot(setExclude);
    empty = UNKNOWN;
  }

  private final BitSet bsTemp = new BitSet();

  int getSelectionCount() {
    if (empty == TRUE)
      return 0;
    empty = TRUE;
    BitSet bs;
    if (bsSubset != null) {
      bsTemp.clear();
      bsTemp.or(bsSubset);
      bsTemp.and(bsSelection);
      bs = bsTemp;
    } else {
      bs = bsSelection;
    }
    int count = bs.cardinality();
    if (count > 0)
      empty = FALSE;
    return count;
  }

  void addListener(JmolSelectionListener listener) {
    for (int i = listeners.length; --i >= 0;)
      if (listeners[i] == listener) {
        listeners[i] = null;
        break;
      }
    int len = listeners.length;
    for (int i = len; --i >= 0;)
      if (listeners[i] == null) {
        listeners[i] = listener;
        return;
      }
    if (listeners.length == 0)
      listeners = new JmolSelectionListener[1];
    else
      listeners = (JmolSelectionListener[]) ArrayUtil.doubleLength(listeners);
    listeners[len] = listener;
  }

  private void selectionChanged(boolean isQuiet) {
    if (hideNotSelected)
      hide(BitSetUtil.copyInvert(bsSelection, viewer.getAtomCount()), isQuiet);
    if (isQuiet || listeners.length == 0)
      return;
    for (int i = listeners.length; --i >= 0;)
      if (listeners[i] != null)
        listeners[i].selectionChanged(bsSelection);
  }

  String getState(StringBuffer sfunc) {
    StringBuffer commands = new StringBuffer();
    if (sfunc != null) {
      sfunc.append("  _setSelectionState;\n");
      commands.append("function _setSelectionState() {\n");
    }
    StateManager.appendCmd(commands, viewer.getTrajectoryInfo());
    Map<String, BitSet> temp = new Hashtable<String, BitSet>();
    String cmd = null;
    addBs(commands, "hide ", bsHidden);
    addBs(commands, "subset ", bsSubset);
    addBs(commands, "delete ", bsDeleted);
    addBs(commands, "fix ", bsFixed);
    temp.put("-", bsSelection);
    cmd = StateManager.getCommands(temp, null);
    if (cmd == null)
      StateManager.appendCmd(commands, "select none");
    else
      commands.append(cmd);
    StateManager.appendCmd(commands, "set hideNotSelected " + hideNotSelected);
    commands.append(viewer.getShapeProperty(JmolConstants.SHAPE_STICKS,
        "selectionState"));
    if (viewer.getSelectionHaloEnabled())
      StateManager.appendCmd(commands, "SelectionHalos ON");
    if (sfunc != null)
      commands.append("}\n\n");
    return commands.toString();
  }

  private static void addBs(StringBuffer sb, String key, 
                            BitSet bs) {
    if (bs == null || bs.length() == 0)
      return;
    StateManager.appendCmd(sb, key + Escape.escape(bs));
  }

  int deleteAtoms(BitSet bs) {
    BitSet bsNew = BitSetUtil.copy(bs);
    if (bsDeleted == null) {
      bsDeleted = bsNew;
    } else {
      bsNew.andNot(bsDeleted);
      bsDeleted.or(bs);
    }
    bsHidden.andNot(bsDeleted);
    bsSelection.andNot(bsDeleted);
    return bsNew.cardinality();
  }

  BitSet getDeletedAtoms() {
    return bsDeleted;
  }

  BitSet getSelectionSet(boolean includeDeleted) {
    if (includeDeleted || bsDeleted == null && bsSubset == null)
      return bsSelection;
    BitSet bs = new BitSet();
    bs.or(bsSelection);
    excludeAtoms(bs, false);
    return bs;
  }

  BitSet getSelectionSubset() {
    return bsSubset;
  }

  void excludeAtoms(BitSet bs, boolean ignoreSubset) {
    if (bsDeleted != null)
      bs.andNot(bsDeleted);
    if (!ignoreSubset && bsSubset != null)
      bs.and(bsSubset);
  }

  void processDeletedModelAtoms(BitSet bsAtoms) {
    if (bsDeleted != null)
      BitSetUtil.deleteBits(bsDeleted, bsAtoms);
    if (bsSubset != null)
      BitSetUtil.deleteBits(bsSubset, bsAtoms);
    BitSetUtil.deleteBits(bsFixed, bsAtoms);
    BitSetUtil.deleteBits(bsHidden, bsAtoms);
    BitSet bs = BitSetUtil.copy(bsSelection);
    BitSetUtil.deleteBits(bs, bsAtoms);
    setSelectionSet(bs);
  }

  void setMotionFixedAtoms(BitSet bs) {
    if (bs == null)
      bsFixed.clear();
    else
      bsFixed.or(bs);
  }

  BitSet getMotionFixedAtoms() {
    return bsFixed;
  }

}
