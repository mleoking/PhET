/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2010-10-02 16:11:05 -0700 (Sat, 02 Oct 2010) $
 * $Revision: 14386 $
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

package org.jmol.shape;

import org.jmol.api.JmolMeasurementClient;
import org.jmol.g3d.*;
import org.jmol.modelset.Atom;
import org.jmol.modelset.Measurement;
import org.jmol.modelset.MeasurementData;
import org.jmol.modelset.MeasurementPending;
import org.jmol.util.BitSetUtil;
import org.jmol.util.Escape;
import org.jmol.util.Point3fi;
import org.jmol.modelset.TickInfo;
import org.jmol.viewer.JmolConstants;
import org.jmol.script.Token;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Hashtable;
import java.util.Map;


public class Measures extends Shape implements JmolMeasurementClient {

  private BitSet bsColixSet;
  private BitSet bsSelected;
  private String strFormat;
  private boolean mustBeConnected = false;
  private boolean mustNotBeConnected = false;
  private float[] rangeMinMax = {Float.MAX_VALUE, Float.MAX_VALUE};

  private Atom[] atoms;

  int measurementCount = 0;
  final List<Measurement> measurements = new ArrayList<Measurement>();
  MeasurementPending measurementPending;
  
  short mad = (short)-1;
  short colix; // default to none in order to contrast with background
  
  Font3D font3d;

  TickInfo tickInfo;
  TickInfo defaultTickInfo;
  
  @Override
  protected void initModelSet() {
    for (int i = measurements.size(); --i >= 0; ) {
      Measurement m = measurements.get(i);
      if (m != null)
        m.modelSet = modelSet;
    }
    atoms = modelSet.atoms;
  }
  
  @Override
  public void initShape() {
    super.initShape();
    font3d = g3d.getFont3D(JmolConstants.MEASURE_DEFAULT_FONTSIZE);
  }

  @Override
  protected void setSize(int size, BitSet bsSelected) {
    mad = (short)size;
  }

  @Override
  public void setProperty(String propertyName, Object value, BitSet bsIgnored) {
    // the following can be used with "select measures ({bitset})"
    
    Measurement mt;
    if ("clearModelIndex" == propertyName) {
      for (int i = 0; i < measurementCount; i++)
        measurements.get(i).setModelIndex((short) 0);
      return;
    }
    
    if ("color" == propertyName) {
      setColor(value == null ? Graphics3D.INHERIT_ALL : Graphics3D.getColix(value));
      return;
    } 

    if ("delete" == propertyName) {
      delete(value);
      setIndices();
      return;
    } 
    
    if ("font" == propertyName) {
      font3d = (Font3D) value;
      return;
    }
    
    if ("hideAll" == propertyName) {
      showHide(((Boolean) value).booleanValue());
      return;
    }
    
    if ("pending" == propertyName) {
      pending((MeasurementPending) value);
      return;
    }
    
    boolean isRefresh;
    if ((isRefresh = ("refresh" == propertyName)) 
        || "refreshTrajectories" == propertyName) {
      for (int i = measurements.size(); --i >= 0;)
        if ((mt = measurements.get(i)) != null 
            && (isRefresh || mt.isTrajectory()))
          mt.refresh();
      return;
    } 

    if ("select" == propertyName) {
      BitSet bs = (BitSet) value;
      if (bs == null || BitSetUtil.cardinalityOf(bs) == 0) {
        bsSelected = null;
      } else {
        bsSelected = new BitSet();
        bsSelected.or(bs);
      }
      return;
    }
    
    if ("setFormats" == propertyName) {
      setFormats((String) value);
      return;
    }

    //any one of the following clears the "select measures" business
    
    bsSelected = null;

    if ("maps" == propertyName) {
      int[][] maps = (int[][]) value;
      for (int i = 0; i < maps.length; i++) {
        int len = maps[i].length;
        if (len < 2 || len > 4) 
          continue;
        int[] v = new int[len + 1];
        v[0] = len;
        System.arraycopy(maps[i], 0, v, 1, len);
        toggleOn(v);
      }
    } else if ("measure" == propertyName) {
      MeasurementData md = (MeasurementData) value;
      tickInfo = md.tickInfo;
      if (md.tickInfo != null && md.tickInfo.id.equals("default")) {
        defaultTickInfo = md.tickInfo;
        return;
      }
      rangeMinMax[0] = md.rangeMinMax[0];
      rangeMinMax[1] = md.rangeMinMax[1];
      mustBeConnected = md.mustBeConnected;
      mustNotBeConnected = md.mustNotBeConnected;
      strFormat = md.strFormat;
      if (md.isAll) {
        if (tickInfo != null)
          define(md, Token.delete);
        define(md, md.tokAction);
        setIndices();
        return;
      }
      Measurement pt = setSingleItem(md.points);
      switch (md.tokAction) {
      case Token.delete:
        define(Integer.MIN_VALUE, pt, true, false, false);
        setIndices();
        break;
      case Token.on:
        showHide(pt, false);          
        break;
      case Token.off:
        showHide(pt, true);
        break;
      case Token.define:
        delete(pt);
        toggle(pt);        
        break;
      case Token.opToggle:
        toggle(pt);        
      }
      return;
    }
    
    if ("clear" == propertyName) {
      clear();
      return;
    }
    
    if ("deleteModelAtoms" == propertyName) {
      atoms = (Atom[])((Object[])value)[1];
      int modelIndex = ((int[]) ((Object[]) value)[2])[0];
      int firstAtomDeleted = ((int[])((Object[])value)[2])[1];
      int nAtomsDeleted = ((int[])((Object[])value)[2])[2];
      int atomMax = firstAtomDeleted + nAtomsDeleted;
      for (int i = measurementCount; --i >= 0;) {
        mt = measurements.get(i);
        int[] indices = mt.getCountPlusIndices();
        for (int j = 1; j <= indices[0]; j++) {
          int iAtom = indices[j];
          if (iAtom >= firstAtomDeleted) {
            if (iAtom < atomMax) {
              deleteMeasurement(i);
              break;
            }
            indices[j] -= nAtomsDeleted;
          } else if (iAtom < 0) {
            Point3fi pt = mt.getAtom(j);
            if (pt.modelIndex > modelIndex) {
              pt.modelIndex--;
            } else if (pt.modelIndex == modelIndex) {
              deleteMeasurement(i);
              break;
            }
          }
        }
      }
      return;
    }

    if ("hide" == propertyName) {
      showHide(new Measurement(modelSet, (int[]) value, null, null), true);
      return;
    }
    
    if ("reformatDistances" == propertyName) {
      reformatDistances();
      return;
    }
    
    if ("show" == propertyName) {
      showHide(new Measurement(modelSet, (int[]) value, null, null), false);
      return;
    }
    
    if ("toggle" == propertyName) {
      toggle(new Measurement(modelSet, (int[]) value, null, null));
      return;
    }
    
    if ("toggleOn" == propertyName) {
      toggleOn((int[]) value);
      return;
    }
    
  }

  private Measurement setSingleItem(List<Object> vector) {
    Point3fi[] points = new Point3fi[4];
    int[] indices = new int[5];
    indices[0] = vector.size();
    for (int i = vector.size(); --i >= 0; ) {
      Object value = vector.get(i);
      if (value instanceof BitSet) {
        int atomIndex = ((BitSet) value).nextSetBit(0);
        if (atomIndex < 0)
          return null;
        indices[i + 1] = atomIndex;
      } else {
        points[i] = (Point3fi) value;
        indices[i + 1] = -2 - i;
      }
    }
    return new Measurement(modelSet, indices, points, tickInfo == null ? defaultTickInfo : tickInfo);
  }

  @Override
  public Object getProperty(String property, int index) {
    if ("pending".equals(property))
      return measurementPending;
    if ("count".equals(property))
      return Integer.valueOf(measurementCount);
    if ("countPlusIndices".equals(property))
      return (index < measurementCount ? 
          measurements.get(index).getCountPlusIndices() : null);
    if ("stringValue".equals(property))
      return (index < measurementCount ? measurements.get(index).getString() : null);
    if ("pointInfo".equals(property))
      return measurements.get(index / 10).getLabel(index % 10, false, false);
    if ("info".equals(property))
      return getAllInfo();
    if ("infostring".equals(property))
      return getAllInfoAsString();
    return null;
  }

  private void clear() {
    if (measurementCount == 0)
      return;
    measurementCount = 0;
    measurements.clear();
    viewer.setStatusMeasuring("measureDeleted", -1, "all", 0);
  }

  private void setColor(short colix) {
    if (bsColixSet == null)
      bsColixSet = new BitSet();
      if (bsSelected == null)
        this.colix = colix;
    Measurement mt;
    for (int i = measurements.size(); --i >= 0; )
      if ((mt = measurements.get(i)) != null
          && (bsSelected != null && bsSelected.get(i) || bsSelected == null
              && (colix == Graphics3D.INHERIT_ALL || mt.getColix() == Graphics3D.INHERIT_ALL))) {
        mt.setColix(colix);
        bsColixSet.set(i);
      }
  }

  private void setFormats(String format) {
    if (format != null && format.length() == 0)
      format = null;
    for (int i = measurements.size(); --i >= 0;)
      if (bsSelected == null || bsSelected.get(i))
        measurements.get(i).formatMeasurement(format, null, false);
  }
  
  private void showHide(boolean isHide) {
    for (int i = measurements.size(); --i >= 0;)
      if (bsSelected == null || bsSelected.get(i))
        measurements.get(i).setHidden(isHide);
  }

  private void showHide(Measurement m, boolean isHide) {
    int i = find(m);
    if (i >= 0)
      measurements.get(i).setHidden(isHide);
  }
  
  private void toggle(Measurement m) {
    rangeMinMax[0] = Float.MAX_VALUE;
    //toggling one that is hidden should be interpreted as DEFINE
    int i = find(m);
    Measurement mt;
    if (i >= 0 && !(mt = measurements.get(i)).isHidden()) // delete it and all like it
      define(i, mt, true, false, false);
    else // define OR turn on if measureAllModels
      define(-1, m, false, true, false);
    setIndices();
  }

  private void toggleOn(int[] indices) {
    rangeMinMax[0] = Float.MAX_VALUE;
    //toggling one that is hidden should be interpreted as DEFINE
    bsSelected = new BitSet();
    define(Integer.MIN_VALUE, new Measurement(modelSet, indices, null, defaultTickInfo), false, true, true);
    setIndices();
    reformatDistances();
  }

  private void delete(Measurement m) {
    rangeMinMax[0] = Float.MAX_VALUE;
    //toggling one that is hidden should be interpreted as DEFINE
    int i = find(m);
    if (i >= 0)
      define(i, measurements.get(i), true, false, false);
    setIndices();
  }

  private void delete(Object value) {
    if (value instanceof int[]) {
      define(Integer.MIN_VALUE, new Measurement(modelSet, (int[])value, null, null), true, false, false);
      return;
    }
    if ((value instanceof Integer))
      deleteMeasurement(((Integer)value).intValue());   
  }

  private void define(int iPt, Measurement m, boolean isDelete, boolean isShow,
                      boolean doSelect) {
    if (!viewer.getMeasureAllModelsFlag()) {
      if (isDelete) {
        if (iPt == Integer.MIN_VALUE)
          iPt = find(m);
        if (iPt >= 0)
          deleteMeasurement(iPt);
        return;
      }
      defineMeasurement(iPt, m, doSelect);
      return;
    }
    if (isShow) { // make sure all like this are deleted, not just hidden
      define(iPt, m, true, false, false); // self-reference
      if (isDelete)
        return;
    }
    // we create a set of atoms involving all atoms with the
    // same atom number in each model
    List<Object> points = new ArrayList<Object>();
    int nPoints = m.getCount();
    for (int i = 1; i <= nPoints; i++) {
      int atomIndex = m.getAtomIndex(i);
      points.add(atomIndex >= 0 ? (Object) viewer.getAtomBits(
          Token.atomno, Integer.valueOf(atoms[atomIndex].getAtomNumber()))
          : (Object) m.getAtom(i));
    }
    MeasurementData md = new MeasurementData(points, 
                   tokAction,
                   rangeMinMax, 
                   strFormat, null,
                   tickInfo,
                   mustBeConnected,
                   mustNotBeConnected,
                   true);
    define(md, (isDelete ? Token.delete 
        : Token.define
        ));
  }

  private int find(Measurement m) {
    return Measurement.find(measurements, m);
  }

  private void setIndices() {
    for (int i = 0; i < measurementCount; i++)
      measurements.get(i).setIndex(i);
  }
  
  private int tokAction;
  
  private void define(MeasurementData md, int tokAction) {
    this.tokAction = tokAction;
    md.define(this, modelSet);
  }

  public void processNextMeasure(Measurement m) {
    // a callback from Measurement.define
    // all atom bitsets have been iterated
    int iThis = find(m);
    if (iThis >= 0) {
      if (tokAction == Token.delete) {
        deleteMeasurement(iThis);
      } else if (strFormat != null) {
        measurements.get(iThis).formatMeasurement(strFormat,
            null, true);
      } else {
        measurements.get(iThis).setHidden(tokAction == Token.off);
      }
    } else if (tokAction == Token.define || tokAction == Token.opToggle) {
      m.tickInfo = (tickInfo == null ? defaultTickInfo : tickInfo);
      defineMeasurement(-1, m, true);
    }
  }

  private void defineMeasurement(int i, Measurement m, boolean doSelect) {
    float value = m.getMeasurement();
    if (rangeMinMax[0] != Float.MAX_VALUE
        && (value < rangeMinMax[0] || value > rangeMinMax[1]))
      return;
    if (i == Integer.MIN_VALUE)
      i = find(m);
    if (i >= 0) {
      measurements.get(i).setHidden(false);
      if (doSelect)
        bsSelected.set(i);
      return;
    }
    Measurement measureNew = new Measurement(modelSet, m, value, colix,
        strFormat, measurementCount);
    measurements.add(measureNew);
    viewer.setStatusMeasuring("measureCompleted", measurementCount++,
        measureNew.toVector().toString(), measureNew.getValue());
  }

  private void deleteMeasurement(int i) {
    String msg = measurements.get(i).toVector().toString();
    measurements.remove(i);
    measurementCount--;
    viewer.setStatusMeasuring("measureDeleted", i, msg, 0);
  }

  private void pending(MeasurementPending measurementPending) {
    this.measurementPending = measurementPending;
    if (measurementPending == null)
      return;
    if (measurementPending.getCount() > 1)
      viewer.setStatusMeasuring("measurePending",
          measurementPending.getCount(), measurementPending.toVector().toString(), measurementPending.getValue());
  }

  private void reformatDistances() {
    for (int i = measurementCount; --i >= 0; )
      measurements.get(i).reformatDistanceIfSelected();    
  }
  
  private List<Map<String, Object>> getAllInfo() {
    List<Map<String, Object>> info = new ArrayList<Map<String,Object>>();
    for (int i = 0; i< measurementCount; i++) {
      info.add(getInfo(i));
    }
    return info;
  }
  
  private String getAllInfoAsString() {
    String info = "Measurement Information";
    for (int i = 0; i< measurementCount; i++) {
      info += "\n" + getInfoAsString(i);
    }
    return info;
  }
  
  private Map<String, Object> getInfo(int index) {
    Measurement m = measurements.get(index);
    int count = m.getCount();
    Map<String, Object> info = new Hashtable<String, Object>();
    info.put("index", Integer.valueOf(index));
    info.put("type", (count == 2 ? "distance" : count == 3 ? "angle"
        : "dihedral"));
    info.put("strMeasurement", m.getString());
    info.put("count", Integer.valueOf(count));
    info.put("value", new Float(m.getValue()));
    TickInfo tickInfo = m.getTickInfo();
    if (tickInfo != null) {
      info.put("ticks", tickInfo.ticks);
      if (tickInfo.scale != null)
        info.put("tickScale", tickInfo.scale);
      if (tickInfo.tickLabelFormats != null)
        info.put("tickLabelFormats", tickInfo.tickLabelFormats);
      if (!Float.isNaN(tickInfo.first))
        info.put("tickStart", new Float(tickInfo.first));
    }
    List<Map<String, Object>> atomsInfo = new ArrayList<Map<String,Object>>();
    for (int i = 1; i <= count; i++) {
      Map<String, Object> atomInfo = new Hashtable<String, Object>();
      int atomIndex = m.getAtomIndex(i);
      atomInfo.put("_ipt", Integer.valueOf(atomIndex));
      atomInfo.put("coord", Escape.escape(m.getAtom(i)));
      atomInfo.put("atomno", Integer.valueOf(atomIndex < 0 ? -1 : atoms[atomIndex].getAtomNumber()));
      atomInfo.put("info", (atomIndex < 0 ? "<point>" : atoms[atomIndex].getInfo()));
      atomsInfo.add(atomInfo);
    }
    info.put("atoms", atomsInfo);
    return info;
  }

  private String getInfoAsString(int index) {
    return measurements.get(index).getInfoAsString(null);
  }
  
  void setVisibilityInfo() {
    BitSet bsModels = viewer.getVisibleFramesBitSet();
    out:
    for (int i = measurementCount; --i >= 0; ) {
      Measurement m = measurements.get(i);
      m.setVisible(false);
      if(mad == 0 || m.isHidden())
        continue;
      for (int iAtom = m.getCount(); iAtom > 0; iAtom--) {
        int atomIndex = m.getAtomIndex(iAtom);
        if (atomIndex >= 0) {
          if (!modelSet.atoms[atomIndex].isClickable())
            continue out;
        } else {
          int modelIndex = m.getAtom(iAtom).modelIndex;
          if (modelIndex >= 0 && !bsModels.get(modelIndex))
            continue out;
        }
      }
      m.setVisible(true);
    }
  }
  
 @Override
public String getShapeState() {
    StringBuffer commands = new StringBuffer("");
    appendCmd(commands, "measures delete");
    for (int i = 0; i < measurementCount; i++)
      appendCmd(commands, getState(i));
    appendCmd(commands, "select *; set measures " + viewer.getMeasureDistanceUnits());
    appendCmd(commands, getFontCommand("measures", font3d));
    int nHidden = 0;
    Map<String, BitSet> temp = new Hashtable<String, BitSet>();
    BitSet bs = new BitSet(measurementCount);
    for (int i = 0; i < measurementCount; i++) {
      Measurement m = measurements.get(i);
      if (m.isHidden()) {
        nHidden++;
        bs.set(i);
      }
      if (bsColixSet != null && bsColixSet.get(i))
        setStateInfo(temp, i, getColorCommand("measure", m.getColix()));
      if (m.getStrFormat() != null)
        setStateInfo(temp, i, "measure "
            + Escape.escape(m.getStrFormat()));
    }
    if (nHidden > 0)
      if (nHidden == measurementCount)
        appendCmd(commands, "measures off; # lines and numbers off");
      else
        for (int i = 0; i < measurementCount; i++)
          if (bs.get(i))
            setStateInfo(temp, i, "measure off");
    if (defaultTickInfo != null) {
      commands.append(" measure ");
      FontLineShape.addTickInfo(commands, defaultTickInfo, true);
      commands.append(";\n");
    }
    if (mad >= 0)
      commands.append(" set measurements " + (mad / 2000f));
    String s = getShapeCommands(temp, null, "select measures");
    if (s != null && s.length() != 0) {
      commands.append(s);
      appendCmd(commands, "select measures ({null})");
    }
    
    return commands.toString();
  }
  
  private String getState(int index) {
    Measurement m = measurements.get(index);
    int count = m.getCount();
    StringBuffer sb = new StringBuffer("measure");
    TickInfo tickInfo = m.getTickInfo();
    if (tickInfo != null)
      FontLineShape.addTickInfo(sb, tickInfo, true);
    for (int i = 1; i <= count; i++)
      sb.append(" ").append(m.getLabel(i, true, true));
    sb.append("; # " + getInfoAsString(index));
    return sb.toString();
  }
}
