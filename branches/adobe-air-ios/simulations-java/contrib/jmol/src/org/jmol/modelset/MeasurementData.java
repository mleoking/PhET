/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2010-01-11 09:57:12 -0600 (Mon, 11 Jan 2010) $
 * $Revision: 12093 $
 *
 * Copyright (C) 2002-2005  The Jmol Development Team
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
import java.util.List;

import org.jmol.api.JmolMeasurementClient;
import org.jmol.util.Point3fi;
import org.jmol.viewer.Viewer;

public class MeasurementData implements JmolMeasurementClient {

  /*
   * a class used to pass measurement parameters to Measures and
   * to generate a list of measurements from ScriptMathProcessor
   * 
   */
  
  private JmolMeasurementClient client;
  private List<String> measurementStrings;

  private Atom[] atoms;
  public boolean mustBeConnected;
  public boolean mustNotBeConnected;
  public TickInfo tickInfo;
  public int tokAction;
  public List<Object> points;
  public float[] rangeMinMax;
  public String strFormat;
  public boolean isAll;

  private String units;
  
  /*
   * the general constructor. tokAction is not used here, but simply
   * passed back to the 
   */
  public MeasurementData(List<Object> points, int tokAction,
                 float[] rangeMinMax, String strFormat, String units,
                 TickInfo tickInfo,
                 boolean mustBeConnected, boolean mustNotBeConnected,
                 boolean isAll) {
    this.tokAction = tokAction;
    this.points = points;
    this.rangeMinMax = rangeMinMax;
    this.strFormat = strFormat;
    this.units = units;
    this.tickInfo = tickInfo;
    this.mustBeConnected = mustBeConnected;
    this.mustNotBeConnected = mustNotBeConnected;
    this.isAll = isAll;
  }
  
  /**
   * if this is the client, then this method is 
   * called by MeasurementData when a measurement is ready
   * 
   * @param m 
   * 
   */
  public void processNextMeasure(Measurement m) {
    float value = m.getMeasurement();
    if (rangeMinMax != null && rangeMinMax[0] != Float.MAX_VALUE
        && (value < rangeMinMax[0] || value > rangeMinMax[1]))
      return;
    //System.out.println(Escape.escapeArray(m.getCountPlusIndices()));
    measurementStrings.add(m.getString(viewer, strFormat, units));
  }

  /**
   * if this is the client, then this method
   * can be called to get the result vector
   * 
   * @param viewer
   * @return Vector of formatted Strings
   * 
   */
  public List<String> getMeasurements(Viewer viewer) {
    this.viewer = viewer;
    measurementStrings = new ArrayList<String>();
    define(null, viewer.getModelSet());
    return measurementStrings;
  }
  
  private Viewer viewer;
  
  /**
   * called by the client to generate a set of measurements
   * 
   * @param client    or null to specify this to be our own client
   * @param modelSet
   */
  public void define(JmolMeasurementClient client, ModelSet modelSet) {
    this.client = (client == null ? this : client);
    atoms = modelSet.atoms;
    /*
     * sets up measures based on an array of atom selection expressions -RMH 3/06
     * 
     *(1) run through first expression, choosing model
     *(2) for each item of next bs, iterate over next bitset, etc.
     *(3) for each last bitset, trigger toggle(int[])
     *
     *simple!
     *
     */
    int nPoints = points.size();
    if (nPoints < 2)
      return;
    int modelIndex = -1;
    Point3fi[] pts = new Point3fi[4];
    int[] indices = new int[5];
    Measurement m = new Measurement(modelSet, indices, pts, null);
    m.setCount(nPoints);
    int ptLastAtom = -1;
    for (int i = 0; i < nPoints; i++) {
      Object obj = points.get(i);
      if (obj instanceof BitSet) {
        BitSet bs = (BitSet) obj;
        int nAtoms = bs.cardinality(); 
        if (nAtoms == 0)
          return;
        if (nAtoms > 1)
          modelIndex = 0;
        ptLastAtom = i;
        indices[i + 1] = bs.nextSetBit(0);
      } else {
        if (pts == null)
          pts = new Point3fi[4];
        pts[i] = (Point3fi)obj;
        indices[i + 1] = -2 - i; 
      }
    }
    nextMeasure(0, ptLastAtom, m, modelIndex);
  }

  /**
   * iterator for measurements
   * 
   * @param thispt
   * @param ptLastAtom
   * @param m
   * @param thisModel
   */
  private void nextMeasure(int thispt, int ptLastAtom, Measurement m, int thisModel ) {
    if (thispt > ptLastAtom) {
      if (m.isValid() 
          && (!mustBeConnected || m.isConnected(atoms, thispt))
          && (!mustNotBeConnected || !m.isConnected(atoms, thispt)))
        client.processNextMeasure(m);
      return;
    }
    BitSet bs = (BitSet) points.get(thispt);
    int[] indices = m.getCountPlusIndices();
    int thisAtomIndex = (thispt == 0 ? Integer.MAX_VALUE : indices[thispt]);
    if (thisAtomIndex < 0) {
      nextMeasure(thispt + 1, ptLastAtom, m, thisModel);
      return;
    }
    boolean haveNext = false;
    for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
      if (i == thisAtomIndex)
        continue;
      int modelIndex = atoms[i].getModelIndex();
      if (thisModel >= 0) {
        if (thispt == 0)
          thisModel = modelIndex;
        else if (thisModel != modelIndex)
          continue;
      }
      indices[thispt + 1] = i;
      haveNext = true;
      nextMeasure(thispt + 1, ptLastAtom, m, thisModel);
    }
    if (!haveNext)
      nextMeasure(thispt + 1, ptLastAtom, m, thisModel);
  }
    
}


