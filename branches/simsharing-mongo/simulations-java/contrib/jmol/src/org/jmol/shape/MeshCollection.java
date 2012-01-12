/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-04-18 01:25:52 -0500 (Wed, 18 Apr 2007) $
 * $Revision: 7435 $
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

import org.jmol.g3d.*;
import org.jmol.viewer.JmolConstants;
import org.jmol.viewer.StateManager;
import org.jmol.script.ScriptVariable;
import org.jmol.script.Token;

import java.util.BitSet;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;

import org.jmol.util.Escape;
import org.jmol.util.ArrayUtil;
import org.jmol.util.Logger;
import org.jmol.util.TextFormat;

public abstract class MeshCollection extends Shape {

  // Draw, Isosurface(LcaoCartoon MolecularOrbital Pmesh)
    
  public int meshCount;
  public Mesh[] meshes = new Mesh[4];
  public Mesh currentMesh;
  public int modelCount;
  public boolean isFixed;  
  public String script;
  public int nUnnamed;
  public short colix;
  public String myType;
  public boolean explicitID;
  public String actualID;
  protected String previousMeshID;
  protected Mesh linkedMesh;
  protected boolean iHaveModelIndex;
  protected int modelIndex;
  protected boolean allowContourLines;
  protected boolean haveContours = false;
  
  protected float displayWithinDistance2;
  protected boolean isDisplayWithinNot;
  protected List<Point3f> displayWithinPoints;
  protected BitSet bsDisplay;

  public String[] title;
  protected boolean allowMesh = true;
  
  protected Mesh pickedMesh = null;
  protected int pickedModel;
  protected int pickedVertex;

  private Mesh setMesh(String thisID) {
    linkedMesh = null;
    if (thisID == null || TextFormat.isWild(thisID)) {
      if (thisID != null)
        previousMeshID = thisID;
      currentMesh = null;
      return null;
    }
    currentMesh = getMesh(thisID);
    if (currentMesh == null) {
      allocMesh(thisID, null);
    } else if (thisID.equals(JmolConstants.PREVIOUS_MESH_ID)) {
      linkedMesh = currentMesh.linkedMesh;
    }
    if (currentMesh.thisID == null) {
      currentMesh.thisID = myType + (++nUnnamed);
      if (htObjects != null)
        htObjects.put(currentMesh.thisID.toUpperCase(), currentMesh);
    }
    previousMeshID = currentMesh.thisID;
    return currentMesh;
  }

  protected Map<String, Mesh> htObjects;
  
  public void allocMesh(String thisID, Mesh m) {
    // this particular version is only run from privately;
    // isosurface and draw both have overriding methods
    int index = meshCount++;
    meshes = (Mesh[])ArrayUtil.ensureLength(meshes, meshCount * 2);
    currentMesh = meshes[index] = (m == null ? new Mesh(thisID, g3d, colix, index) : m);
    currentMesh.index = index;
    if (thisID != null && htObjects != null)
      htObjects.put(thisID.toUpperCase(), currentMesh);
    previousMeshID = null;
  }

  /**
   * called by ParallelProcessor at completion
   * 
   * @param shape 
   * 
   */
  @Override
  public void merge(Shape shape) {
    MeshCollection mc = (MeshCollection) shape;
    for (int i = 0; i < mc.meshCount; i++) {
      if (mc.meshes[i] != null) {
        Mesh m = mc.meshes[i];
        Mesh m0 = getMesh(m.thisID);
        if (m0 == null) {
          allocMesh(m.thisID, m);
        } else {
          meshes[m0.index] = m;
          m.index = m0.index;
        }
      }      
    }
    previousMeshID = null;
    currentMesh = null;
  }

  @Override
  public void initShape() {
    super.initShape();
    colix = Graphics3D.ORANGE;
    modelCount = viewer.getModelCount();
  }
  
 @SuppressWarnings("unchecked")
 @Override
 public void setProperty(String propertyName, Object value, BitSet bs) {

   if (propertyName == "setXml") {
     if (currentMesh != null)
       currentMesh.xmlProperties = xmlProperties;
   }
   
    if ("init" == propertyName) {
      title = null;
      return;
    }

    if ("link" == propertyName) {
      if (meshCount >= 2 && currentMesh != null)
        currentMesh.linkedMesh = meshes[meshCount - 2];
      return;
    }

    if ("lattice" == propertyName) {
      if (currentMesh != null)
        currentMesh.lattice = (Point3f) value;
      return;
    }

    if ("variables" == propertyName) {
      if (currentMesh != null && currentMesh.scriptCommand != null && !currentMesh.scriptCommand.startsWith("{"))
        currentMesh.scriptCommand = "{\n" 
          + StateManager.getVariableList((Map<String, ScriptVariable>) value, 0, false) + "\n" + currentMesh.scriptCommand;
      return;
    }

    if ("thisID" == propertyName) {
      String id = (String) value;
      setMesh(id);
      checkExplicit(id);
      return;
    }

    if ("title" == propertyName) {
      if (value == null) {
        title = null;
      } else if (value instanceof String[]) {
        title = (String[]) value;
      } else {
        int nLine = 1;
        String lines = (String) value;
        for (int i = lines.length(); --i >= 0;)
          if (lines.charAt(i) == '|')
            nLine++;
        title = new String[nLine];
        nLine = 0;
        int i0 = -1;
        for (int i = 0; i < lines.length(); i++)
          if (lines.charAt(i) == '|') {
            title[nLine++] = lines.substring(i0 + 1, i);
            i0 = i;
          }
        title[nLine] = lines.substring(i0 + 1);
      }
      return;
    }

    if ("delete" == propertyName) {
      deleteMesh();
      return;
    }

    if ("reset" == propertyName) {
      String thisID = (String) value;
      if (setMesh(thisID) == null)
        return;
//      deleteMesh();
      setMesh(thisID);
      return;
    }

    if ("color" == propertyName) {
      if (value == null)
        return;
      colix = Graphics3D.getColix(value);
      setTokenProperty(Token.color, false, false);
      return;
    }

    if ("translucency" == propertyName) {
      setTokenProperty(Token.translucent, (((String) value).equals("translucent")), false);
      return;
    }

    if ("hidden" == propertyName) {
      value = Integer.valueOf(((Boolean)value).booleanValue() ? Token.off: Token.on);
      propertyName = "token";
      //continue
    }

    if ("token" == propertyName) {
      int tok = ((Integer) value).intValue();
      int tok2 = 0;
      boolean test = true;
      switch (tok) {
      case Token.display:
      case Token.on:
      case Token.frontlit:
      case Token.backlit:
      case Token.fullylit:
      case Token.dots:
      case Token.fill:
      case Token.triangles:
      case Token.frontonly:
        break;
      case Token.off:
        test = false;
        tok = Token.on;
        break;
      case Token.contourlines:
        tok2 = Token.mesh;
        break;
      case Token.nocontourlines:
        test = false;
        // TODO leave this as is for now; probably correct...
        tok = Token.contourlines;//(allowContourLines ? Token.contourlines : Token.mesh);
        tok2 = Token.mesh;
        break;
      case Token.mesh:
        tok2 = Token.contourlines;
        break;
      case Token.nomesh:
        test = false;
        tok = Token.mesh;
        tok2 = Token.contourlines;
        break;
      case Token.nodots:
        test = false;
        tok = Token.dots;
        break;
      case Token.nofill:
        test = false;
        tok = Token.fill;
        break;
      case Token.notriangles:
        test = false;
        tok = Token.triangles;
        break;
      case Token.notfrontonly:
        test = false;
        tok = Token.frontonly;
        break;
      default:
        Logger.error("PROBLEM IN MESHCOLLECTION: token? " + Token.nameOf(tok));
      }
      setTokenProperty(tok, test, false);
      if (tok2 != 0)
          setTokenProperty(tok2, test, true);
      return;
    }
    super.setProperty(propertyName, value, bs);
  }

  protected void checkExplicit(String id) {
    if (explicitID) // not twice
      return;
    explicitID = (id != null && !id.equals(JmolConstants.PREVIOUS_MESH_ID));
    if (explicitID)
      previousMeshID = id;
  } 
  
  private void setTokenProperty(int tokProp, boolean bProp, boolean testD) {
    if (currentMesh == null) {
      String key = (explicitID && previousMeshID != null
          && TextFormat.isWild(previousMeshID) ? previousMeshID.toUpperCase()
          : null);
      if (key != null && key.length() == 0)
        key = null;
      for (int i = 0; i < meshCount; i++)
        if (key == null
            || TextFormat.isMatch(meshes[i].thisID.toUpperCase(), key, true, true))
          setMeshTokenProperty(meshes[i], tokProp, bProp, testD);
    } else {
      setMeshTokenProperty(currentMesh, tokProp, bProp, testD);
      if (linkedMesh != null)
        setMeshTokenProperty(linkedMesh, tokProp, bProp, testD);
    }
  }
 
  private void setMeshTokenProperty(Mesh m, int tokProp, boolean bProp, boolean testD) {
    if (testD && (!m.havePlanarContours || m.drawTriangles == m.showContourLines))
      return;
    switch (tokProp) {
    case Token.display:
      m.bsDisplay = bsDisplay;
      if (bsDisplay == null && displayWithinPoints != null) 
        m.setShowWithin(displayWithinPoints, displayWithinDistance2, isDisplayWithinNot);
      return;
    case Token.on:
      m.visible = bProp;
      return;
    case Token.color:
      m.colix = colix;
      return;
    case Token.translucent:
      m.setTranslucent(bProp, translucentLevel);
      return;
    case Token.frontlit:
    case Token.backlit:
    case Token.fullylit:
      m.setLighting(tokProp);
      return;
    case Token.dots:
      m.showPoints = bProp;
      return;
    case Token.mesh:
      m.drawTriangles = bProp;
      return;
    case Token.fill:
      m.fillTriangles = bProp;
      return;
    case Token.triangles:
      m.showTriangles = bProp;
      return;
    case Token.contourlines:
      m.showContourLines = bProp;
      return;
    case Token.frontonly:
      m.frontOnly = bProp;
      return;
    }
  }

  @Override
  public boolean getProperty(String property, Object[] data) {
    if (property == "checkID") {
      String key = ((String) data[0]).toUpperCase();
      boolean isWild = TextFormat.isWild(key);
      for (int i = meshCount; --i >= 0;) {
        String id = meshes[i].thisID;
        if (id.equalsIgnoreCase(key) || isWild
            && TextFormat.isMatch(id.toUpperCase(), key, true, true)) {
          data[1] = id;
          return true;
        }
      }
      return false;
    }
    if (property == "getCenter") {
      String id = (String) data[0];
      int index = ((Integer)data[1]).intValue();
      Mesh m;
      if ((m = getMesh(id)) == null || m.vertices == null)
          return false;
      if (index == Integer.MAX_VALUE)
        data[2] = new Point3f(m.index + 1, meshCount, m.vertexCount);
      else 
        data[2] = m.vertices[m.getVertexIndexFromNumber(index)];
      return true;
    }
    return false;
  }

  @Override
  public Object getProperty(String property, int index) {
   Mesh m;
    if (property == "count") {
      int n = 0;
      for (int i = 0; i < meshCount; i++)
        if ((m = meshes[i]) != null && m.vertexCount > 0)
          n++;
      return Integer.valueOf(n);
    }
    if (property == "ID")
      return (currentMesh == null ? null : currentMesh.thisID);
    if (property == "list") {
      StringBuffer sb = new StringBuffer();
      int k = 0;
      for (int i = 0; i < meshCount; i++) {
         if ((m = meshes[i]) == null || m.vertexCount == 0)
          continue;
        sb.append((++k)).append(" id:" + m.thisID).append(
            "; model:" + viewer.getModelNumberDotted(m.modelIndex)).append(
            "; vertices:" + m.vertexCount).append(
            "; polygons:" + m.polygonCount).append("; visible:" + m.visible);
        if (m.title != null) {
          String s = "";
          for (int j = 0; j < m.title.length; j++)
            s += (j == 0 ? "; title:" : " | ") + m.title[j];
          if (s.length() > 10000)
            s = s.substring(0, 10000) + "...";
          sb.append(s);
        }
        sb.append('\n');
      }
      return sb.toString();
    }
    if (property == "vertices")
      return getVertices(currentMesh);
    return null;
  }

  private Object getVertices(Mesh mesh) {
    if (mesh == null)
      return null;
    return mesh.vertices;
  }
 
  protected void clean() {
    for (int i = meshCount; --i >= 0;)
      if (meshes[i] == null || meshes[i].vertexCount == 0)
        deleteMesh(i);
  }

  private void deleteMesh() {
    if (explicitID && currentMesh != null)
      deleteMesh(currentMesh.index);
    else
      deleteMesh(explicitID && previousMeshID != null
          && TextFormat.isWild(previousMeshID) ?  
              previousMeshID : null);
    currentMesh = null;
  }

  protected void deleteMesh(String key) {
    if (key == null || key.length() == 0) {
      for (int i = meshCount; --i >= 0; )
        meshes[i] = null;
      meshCount = 0;
      nUnnamed = 0;
      if (htObjects != null)
        htObjects.clear();
    } else {
      key = key.toLowerCase();
      for (int i = meshCount; --i >= 0; ) {
        if (TextFormat.isMatch(meshes[i].thisID.toLowerCase(), key, true, true))
          deleteMesh(i);
      }
    }
  }

  public void deleteMesh(int i) {
    if (htObjects != null)
      htObjects.remove(meshes[i].thisID.toUpperCase());
    for (int j = i + 1; j < meshCount; ++j)
      meshes[--meshes[j].index] = meshes[j];
    meshes[--meshCount] = null;
  }
  
  public Mesh getMesh(String thisID) {
    int i = getIndexFromName(thisID);
    return (i < 0 ? null : meshes[i]);
  }
  
  @Override
  public int getIndexFromName(String thisID) {
    if (JmolConstants.PREVIOUS_MESH_ID.equals(thisID))
      return (previousMeshID == null ? meshCount - 1
          : getIndexFromName(previousMeshID));
    if (TextFormat.isWild(thisID)) {
      thisID = thisID.toLowerCase();
      for (int i = meshCount; --i >= 0;) {
        if (meshes[i] != null
            && TextFormat.isMatch(meshes[i].thisID, thisID, true, true))
          return i;
      }
    } else {
      if (htObjects != null) {
        Mesh m = htObjects.get(thisID.toUpperCase());
        return (m == null ? -1 : m.index);
      }
      for (int i = meshCount; --i >= 0;) {
        if (meshes[i] != null && thisID.equalsIgnoreCase(meshes[i].thisID))
          return i;
      }
    }
    return -1;
  }
  
  public void setModelIndex(int atomIndex, int modelIndex) {
    if (currentMesh == null)
      return;
    currentMesh.visible = true; 
    if ((currentMesh.atomIndex = atomIndex) >= 0)
      currentMesh.modelIndex = viewer.getAtomModelIndex(atomIndex);
    else if (isFixed)
      currentMesh.modelIndex = -1;
    else if (modelIndex >= 0)
      currentMesh.modelIndex = modelIndex;
    else
      currentMesh.modelIndex = viewer.getCurrentModelIndex();
    currentMesh.scriptCommand = script;
  }

 @Override 
 public void setVisibilityFlags(BitSet bs) {
    /*
     * set all fixed objects visible; others based on model being displayed
     * 
     */
    for (int i = meshCount; --i >= 0;) {
      Mesh mesh = meshes[i];
      mesh.visibilityFlags = (mesh.visible && mesh.isValid
          && (mesh.modelIndex < 0 || bs.get(mesh.modelIndex)
          && (mesh.atomIndex < 0 || !modelSet.isAtomHidden(mesh.atomIndex))
          ) ? myVisibilityFlag
          : 0);
    }
  }
 
  protected void getModelIndex(String script) {
    //pmesh and isosurface state
    int i;
    iHaveModelIndex = false;
    modelIndex = -1;
    if (script == null || (i = script.indexOf("MODEL({")) < 0)
      return;
    int j = script.indexOf("})", i);
    if (j < 0)
      return;
    BitSet bs = Escape.unescapeBitset(script.substring(i + 3, j + 1));
    modelIndex = (bs == null ? -1 : bs.nextSetBit(0));
    iHaveModelIndex = (modelIndex >= 0);
  }
  
  protected void setStatusPicked(int flag, Point3f v) {
    // for draw and isosurface
    viewer.setStatusAtomPicked(flag, "[\"" + myType + "\"," + Escape.escape(pickedMesh.thisID) + "," +
        + pickedModel + "," + pickedVertex + "," + v.x + "," + v.y + "," + v.z + "," 
        + (pickedMesh.title == null ? "\"\"" 
               : Escape.escape(pickedMesh.title[0]))+"]");
  }
}

 