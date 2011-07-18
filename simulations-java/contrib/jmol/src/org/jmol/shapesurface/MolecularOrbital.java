/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2006-07-14 16:23:28 -0500 (Fri, 14 Jul 2006) $
 * $Revision: 5305 $
 *
 * Copyright (C) 2005 Miguel, Jmol Development
 *
 * Contact: jmol-developers@lists.sf.net,jmol-developers@lists.sourceforge.net
 * Contact: hansonr@stolaf.edu
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

package org.jmol.shapesurface;

import java.util.BitSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point4f;

import org.jmol.util.ArrayUtil;
import org.jmol.util.Escape;
import org.jmol.util.Logger;
import org.jmol.viewer.JmolConstants;
import org.jmol.script.Token;
import org.jmol.shape.Shape;
import org.jmol.jvxl.data.JvxlCoder;
import org.jmol.jvxl.readers.Parameters;

public class MolecularOrbital extends Isosurface {

  @Override
  public void initShape() {
    super.initShape();
    myType = "molecularOrbital";
    super.setProperty("thisID", "mo", null);
  }

  // these are globals, stored here and only passed on when the they are needed. 

  private String moTranslucency = null;
  private Float moTranslucentLevel = null;
  private Point4f moPlane = null;
  private Float moCutoff = null;
  private Float moResolution = null;
  private Float moScale = null;
  private Integer moColorPos = null;
  private Integer moColorNeg = null;
  private boolean moIsPositiveOnly = false;
  private int moFill = Token.nofill;
  private int moMesh = Token.mesh;
  private int moDots = Token.nodots;
  private int moFrontOnly = Token.frontonly;
  private String moTitleFormat = null;
  private boolean moDebug;
  private int myColorPt;
  private String strID;
  private int moNumber;
  private float[] moLinearCombination;
  private Map<String, Map<String, Object>> htModels;
  private Map<String, Object> thisModel;

  @Override
  public void setProperty(String propertyName, Object value, BitSet bs) {

    // in the case of molecular orbitals, we just cache the information and
    // then send it all at once. 

    if ("init" == propertyName) {
      myColorPt = 0;
      moDebug = false;
      int modelIndex = ((Integer) value).intValue();
      strID = getId(modelIndex);
      Logger.info("MO init " + strID);
      // overide bitset selection
      super.setProperty("init", null, null);
      super.setProperty("modelIndex", Integer.valueOf(modelIndex), null);
      if (htModels == null)
        htModels = new Hashtable<String, Map<String,Object>>();
      if (!htModels.containsKey(strID))
        htModels.put(strID, new Hashtable<String, Object>());
      thisModel = htModels.get(strID);
      moNumber = (!thisModel.containsKey("moNumber") ? 0 : ((Integer) thisModel
          .get("moNumber")).intValue());
      moLinearCombination = (float[]) thisModel.get("moLinearCombination");
      return;
    }

    if ("cutoff" == propertyName) {
      thisModel.put("moCutoff", value);
      thisModel.put("moIsPositiveOnly", Boolean.FALSE);
      return;
    }
    
    if ("scale" == propertyName) {
      thisModel.put("moScale", value);
      return;
    }

    if ("cutoffPositive" == propertyName) {
      thisModel.put("moCutoff", value);
      thisModel.put("moIsPositiveOnly", Boolean.TRUE);
      return;
    }

    if ("resolution" == propertyName) {
      thisModel.put("moResolution", value);
      return;
    }

    if ("titleFormat" == propertyName) {
      moTitleFormat = (String) value;
      return;
    }

    if ("color" == propertyName) {
      if (!(value instanceof Integer))
        return;
      thisModel.remove("moTranslucency");
      super.setProperty("color", value, bs);
      propertyName = "colorRGB";
      myColorPt = 0;
      //fall through
    }

    if ("colorRGB" == propertyName) {
      moColorPos = (Integer) value;
      if (myColorPt++ == 0)
        moColorNeg = moColorPos;
      thisModel.put("moColorNeg", moColorNeg);
      thisModel.put("moColorPos", moColorPos);
      return;
    }

    if ("plane" == propertyName) {
      if (value == null)
        thisModel.remove("moPlane");
      else
        thisModel.put("moPlane", value);
      return;
    }

    if ("molecularOrbital" == propertyName) {
      if (value instanceof Integer) {
        moNumber = ((Integer) value).intValue();
        thisModel.put("moNumber", value);
        thisModel.remove("moLinearCombination");
        moLinearCombination = null;
      } else {
        moNumber = 0;
        moLinearCombination = (float[]) value;
        thisModel.put("moNumber", Integer.valueOf(0));
        thisModel.put("moLinearCombination", moLinearCombination);
      }
      setOrbital(moNumber, moLinearCombination);
      return;
    }

    if ("translucentLevel" == propertyName) {
      if (thisModel == null) {
        if (currentMesh == null)
          return;
        thisModel = htModels.get(currentMesh.thisID);
      }
      thisModel.put("moTranslucentLevel", value);
      //pass through
    }

    if ("delete" == propertyName) {
      htModels.remove(strID);
      moNumber = 0;
      moLinearCombination = null;
      //pass through
    }

    if ("token" == propertyName) {
      int tok = ((Integer) value).intValue();
      switch (tok) {
      case Token.dots:
      case Token.nodots:
        moDots = tok;
        break;
      case Token.fill:
      case Token.nofill:
        moFill = tok;
        break;
      case Token.mesh:
      case Token.nomesh:
        moMesh = tok;
        break;
      case Token.frontonly:
      case Token.notfrontonly:
        moFrontOnly = tok;
        break;
      }
      // pass through  
    }

    if ("translucency" == propertyName) {
      if (thisModel == null) {
        if (currentMesh == null)
          return;
        thisModel = htModels.get(currentMesh.thisID);
      }
      thisModel.put("moTranslucency", value);
      //pass through
    }

    if (propertyName == "deleteModelAtoms") {
      int modelIndex = ((int[]) ((Object[]) value)[2])[0];
      Map<String, Map<String, Object>> htModelsNew = new Hashtable<String, Map<String, Object>>();
      for (int i = meshCount; --i >= 0;) {
        if (meshes[i] == null)
          continue;
        if (meshes[i].modelIndex == modelIndex) {
          meshCount--;
          if (meshes[i] == currentMesh) {
            currentMesh = null;
            thisModel = null;
          }
          meshes = (IsosurfaceMesh[]) ArrayUtil.deleteElements(meshes, i, 1);
          continue;
        }
        Map<String, Object> htModel = htModels.get(meshes[i].thisID);
        if (meshes[i].modelIndex > modelIndex) {
          meshes[i].modelIndex--;
          meshes[i].thisID = getId(meshes[i].modelIndex);
        }
        htModelsNew.put(meshes[i].thisID, htModel);
      }
      htModels = htModelsNew;
      return;
    }
    super.setProperty(propertyName, value, bs);
  }

  private String getId(int modelIndex) {
    return "mo_model" + viewer.getModelNumber(modelIndex);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Object getProperty(String propertyName, int param) {
    if (propertyName == "list") {
      String s = (String) super.getProperty("list", param);
      if (s.length() > 1)
        s += "cutoff = " + super.getProperty("cutoff", 0) + "\n";
      return viewer.getMoInfo(-1) + "\n" + s;
    }
    if (propertyName == "moNumber")
      return Integer.valueOf(moNumber);
    if (propertyName == "moLinearCombination")
      return moLinearCombination;
    if (propertyName == "showMO") {
      StringBuffer str = new StringBuffer();
      List<Map<String, Object>> mos = (List<Map<String, Object>>) (sg.getMoData().get("mos"));
      int nOrb = (mos == null ? 0 : mos.size());
      int thisMO = param;
      int currentMO = moNumber;
      boolean isShowCurrent = (thisMO == Integer.MIN_VALUE);
      if (thisMO == Integer.MAX_VALUE) {
        thisMO = currentMO;
      }
      if (nOrb == 0 || isShowCurrent && currentMO == 0)
        return "";
      boolean doOneMo = (thisMO != 0);
      if (currentMO == 0)
        thisMO = 0;
      boolean haveHeader = false;
      int nTotal = (thisMO > 0 ? 1 : nOrb);
      int i0 = (nTotal == 1 && currentMO > 0 ? currentMO : 1);
      for (int i = i0; i <= nOrb; i++)
        if (thisMO == 0 || thisMO == i || !doOneMo && i == currentMO) {
          if (!doOneMo) {
            Parameters params = sg.getParams();
            super.setProperty("init", params, null);
            setOrbital(i, null);
          }
          jvxlData.moleculeXml = viewer.getModelCml(viewer.getModelUndeletedAtomsBitSet(thisMesh.modelIndex), 100, true);
          if (!haveHeader) {
            str.append(JvxlCoder.jvxlGetFile(jvxlData, null, null,
                "HEADERONLY", true, nTotal, null, null));
            haveHeader = true;
          }
          str.append(JvxlCoder.jvxlGetFile(jvxlData, null, jvxlData.title,
              null, false, 1, thisMesh.getState("mo"),
              (thisMesh.scriptCommand == null ? "" : thisMesh.scriptCommand)));
          if (!doOneMo)
            super.setProperty("delete", "mo_show", null);
          if (nTotal == 1)
            break;
        }
      str.append(JvxlCoder.jvxlGetFile(jvxlData, null, null, "TRAILERONLY", true,
          0, null, null));
      return str.toString();
    }
    return super.getProperty(propertyName, param);
  }

  @Override
  protected void clearSg() {
    //sg = null; // not Molecular Orbitals
  }

  private boolean getSettings(String strID) {
    thisModel = htModels.get(strID);
    if (thisModel == null || thisModel.get("moNumber") == null)
      return false;
    moTranslucency = (String) thisModel.get("moTranslucency");
    moTranslucentLevel = (Float) thisModel.get("moTranslucentLevel");
    moPlane = (Point4f) thisModel.get("moPlane");
    moCutoff = (Float) thisModel.get("moCutoff");
    if (moCutoff == null)
      moCutoff = (Float) sg.getMoData().get("defaultCutoff");
    if (moCutoff == null) {
      moCutoff = new Float(Parameters.defaultQMOrbitalCutoff);
    }
    thisModel.put("moCutoff", new Float(moCutoff.floatValue()));
    moResolution = (Float) thisModel.get("moResolution");
    moScale = (Float) thisModel.get("moScale");
    moColorPos = (Integer) thisModel.get("moColorPos");
    moColorNeg = (Integer) thisModel.get("moColorNeg");
    moNumber = ((Integer) thisModel.get("moNumber")).intValue();
    moLinearCombination = (float[]) thisModel.get("moLinearCombination");
    Object b = thisModel.get("moIsPositiveOnly");
    moIsPositiveOnly = (b != null && ((Boolean) (b)).booleanValue());
    return true;
  }

  private void setOrbital(int moNumber, float[] linearCombination) {
    super.setProperty("reset", strID, null);
    if (moDebug)
      super.setProperty("debug", Boolean.TRUE, null);
    getSettings(strID);
    if (moScale != null)
      super.setProperty("scale", moScale, null);
    if (moResolution != null)
      super.setProperty("resolution", moResolution, null);
    if (moPlane != null) {
      super.setProperty("plane", moPlane, null);
      if (moCutoff != null) {
        super.setProperty("red", new Float(-moCutoff.floatValue()), null);
        super.setProperty("blue", moCutoff, null);
      }
    } else {
      if (moCutoff != null)
        super.setProperty((moIsPositiveOnly ? "cutoffPositive" : "cutoff"),
            moCutoff, null);
      if (moColorNeg != null)
        super.setProperty("colorRGB", moColorNeg, null);
      if (moColorPos != null)
        super.setProperty("colorRGB", moColorPos, null);
    }
    super.setProperty("title", moTitleFormat, null);
    super.setProperty("fileName", viewer.getFileName(), null);
    super.setProperty("molecularOrbital", linearCombination == null ? Integer
        .valueOf(moNumber) : linearCombination, null);
    if (moPlane != null && moColorNeg != null)
      super.setProperty("colorRGB", moColorNeg, null);
    if (moPlane != null && moColorPos != null)
      super.setProperty("colorRGB", moColorPos, null);
    currentMesh.isColorSolid = false;
    if (moTranslucentLevel != null)
      super.setProperty("translucenctLevel", moTranslucentLevel, null);
    if (moTranslucency != null)
      super.setProperty("translucency", moTranslucency, null);
    super.setProperty("token", Integer.valueOf(moFill), null);
    super.setProperty("token", Integer.valueOf(moMesh), null);
    super.setProperty("token", Integer.valueOf(moDots), null);
    super.setProperty("token", Integer.valueOf(moFrontOnly), null);
    thisModel.put("mesh", currentMesh);
    return;
  }

  @Override
  public String getShapeState() {
    if (htModels == null)
      return "";
    StringBuffer s = new StringBuffer();
    for (int i = 0; i < modelCount; i++)
      s.append(getMoState(i));
    //System.out.println("molecular orbital state " + s.length());
    return s.toString();
  }

  private String getMoState(int modelIndex) {
    strID = getId(modelIndex);
    if (!getSettings(strID))
      return "";
    StringBuffer s = new StringBuffer();
    if (modelCount > 1)
      appendCmd(s, "frame " + viewer.getModelNumber(modelIndex));
    if (moCutoff != null)
      appendCmd(s, "mo cutoff " + (sg.getIsPositiveOnly() ? "+" : "")
          + moCutoff);
    if (moScale != null)
      appendCmd(s, "mo scale " + moScale);
    if (moResolution != null)
      appendCmd(s, "mo resolution " + moResolution);
    if (moPlane != null)
      appendCmd(s, "mo plane {" + moPlane.x + " " + moPlane.y + " " + moPlane.z
          + " " + moPlane.w + "}");
    if (moTitleFormat != null)
      appendCmd(s, "mo titleFormat " + Escape.escape(moTitleFormat));
    //the following is a correct object==object test
    if (moColorNeg != null)
      appendCmd(s, "mo color "
          + Escape.escapeColor(moColorNeg.intValue())
          + (moColorNeg.equals(moColorPos) ? "" : " "
              + Escape.escapeColor(moColorPos.intValue())));
    if (moLinearCombination == null) {
      appendCmd(s, "mo " + moNumber);
    } else {
      appendCmd(s, "mo " + JmolConstants.getMOString(moLinearCombination));
    }
    if (moTranslucency != null)
      appendCmd(s, "mo translucent " + moTranslucentLevel);
    appendCmd(s, ((IsosurfaceMesh) thisModel.get("mesh")).getState("mo"));
    return s.toString();
  }
  
  @Override
  public void merge(Shape shape) {
  MolecularOrbital mo = (MolecularOrbital) shape;
  moCutoff = mo.moCutoff;
  moScale = mo.moScale;
  moResolution = mo.moResolution;
  moPlane = mo.moPlane;
  moTitleFormat = mo.moTitleFormat;
  moColorNeg = mo.moColorNeg;
  moColorPos = mo.moColorPos;
  moTranslucency = mo.moTranslucency;
  if (htModels == null)
    htModels = new Hashtable<String, Map<String, Object>>();
  Map<String, Map<String, Object>> ht = mo.htModels;
  if (ht != null) {
    for (Map.Entry<String, Map<String, Object>> entry : ht.entrySet()) {
      String key = entry.getKey();
      htModels.put(key, entry.getValue());
    }
  }
  super.merge(shape);
}

}
