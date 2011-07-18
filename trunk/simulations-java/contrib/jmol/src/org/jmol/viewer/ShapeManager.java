/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2010-02-15 07:31:37 -0600 (Mon, 15 Feb 2010) $
 * $Revision: 12396 $
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

import java.util.BitSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;

import org.jmol.atomdata.RadiusData;
import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.Atom;
import org.jmol.modelset.Group;
import org.jmol.modelset.ModelSet;
import org.jmol.script.Token;
import org.jmol.shape.Shape;
import org.jmol.util.Logger;
import org.jmol.util.Point3fi;


public class ShapeManager {

  private Viewer viewer;
  private Graphics3D g3d;
  
  public ShapeManager(Viewer viewer) {
    this.viewer = viewer;
    g3d = viewer.getGraphics3D();
  }

  public ShapeManager(Viewer viewer, ModelSet modelSet) {
    this(viewer);
    resetShapes();
    loadDefaultShapes(modelSet);
  }

  private Shape[] shapes;

  public Shape[] getShapes() {
    return shapes;
  }
  
  public void resetShapes() {
    if (!viewer.isDataOnly())
      shapes = new Shape[JmolConstants.SHAPE_MAX];
  }
  
  private Shape allocateShape(int shapeID) {
    if (shapeID == JmolConstants.SHAPE_HSTICKS || shapeID == JmolConstants.SHAPE_SSSTICKS
        || shapeID == JmolConstants.SHAPE_STRUTS)
      return null;
    String className = JmolConstants.getShapeClassName(shapeID);
    try {
      Class<?> shapeClass = Class.forName(className);
      Shape shape = (Shape) shapeClass.newInstance();
      viewer.setShapeErrorState(shapeID, "allocate");
      shape.initializeShape(viewer, g3d, modelSet, shapeID);
      viewer.setShapeErrorState(-1, null);
      return shape;
    } catch (Exception e) {
      Logger.error("Could not instantiate shape:" + className, e);
    }
    return null;
  }

  public Shape getShape(int i) {
    //FrameRenderer
    return (shapes == null ? null : shapes[i]);
  }
  
  public void setShapeSize(int shapeID, int size, RadiusData rd, BitSet bsSelected) {
    if (shapes == null)
      return;
    if (bsSelected == null && 
        (shapeID != JmolConstants.SHAPE_STICKS || size != Integer.MAX_VALUE))
      bsSelected = viewer.getSelectionSet(false);
    if (rd != null && rd.value != 0 && rd.vdwType == Token.temperature)
      modelSet.getBfactor100Lo();
    viewer.setShapeErrorState(shapeID, "set size");
    if (rd != null && rd.value != 0 || rd == null && size != 0)
      loadShape(shapeID);
    if (shapes[shapeID] != null) {
      shapes[shapeID].setShapeSize(size, rd, bsSelected);
    }
    viewer.setShapeErrorState(-1, null);
  }

  public Shape loadShape(int shapeID) {
    if (shapes == null)
      return null;
    if (shapes[shapeID] == null)
      shapes[shapeID] = allocateShape(shapeID);
    return shapes[shapeID];
  }

  public void setShapeProperty(int shapeID, String propertyName, Object value,
                               BitSet bsSelected) {
    if (shapes == null || shapes[shapeID] == null)
      return;
    viewer.setShapeErrorState(shapeID, "set " + propertyName);
    shapes[shapeID].setShapeProperty(propertyName.intern(), value, bsSelected);
    viewer.setShapeErrorState(-1, null);
  }

  public void releaseShape(int shapeID) {
    if (shapes != null) 
      shapes[shapeID] = null;  
  }
  
  public Object getShapeProperty(int shapeID, String propertyName, int index) {
    if (shapes == null || shapes[shapeID] == null)
      return null;
    viewer.setShapeErrorState(shapeID, "get " + propertyName);
    Object result = shapes[shapeID].getProperty(propertyName, index);
    viewer.setShapeErrorState(-1, null);
    return result;
  }

  public boolean getShapeProperty(int shapeID, String propertyName, Object[] data) {
    if (shapes == null || shapes[shapeID] == null)
      return false;
    viewer.setShapeErrorState(shapeID, "get " + propertyName);
    boolean result = shapes[shapeID].getProperty(propertyName, data);
    viewer.setShapeErrorState(-1, null);
    return result;
  }

  public int getShapeIdFromObjectName(String objectName) {
    if (shapes != null)
      for (int i = JmolConstants.SHAPE_MIN_SPECIAL; i < JmolConstants.SHAPE_MAX_MESH_COLLECTION; ++i)
        if (shapes[i] != null && shapes[i].getIndexFromName(objectName) >= 0)
          return i;
    return -1;
  }

  public void setModelVisibility() {
    if (shapes == null || shapes[JmolConstants.SHAPE_BALLS] == null)
      return;

    //named objects must be set individually
    //in the future, we might include here a BITSET of models rather than just a modelIndex

    // all these isTranslucent = f() || isTranslucent are that way because
    // in general f() does MORE than just check translucency. 
    // so isTranslucent = isTranslucent || f() would NOT work.

    BitSet bs = viewer.getVisibleFramesBitSet();
    
    //NOT balls (that is done later)
    for (int i = 1; i < JmolConstants.SHAPE_MAX; i++)
      if (shapes[i] != null)
        shapes[i].setVisibilityFlags(bs);
    // BALLS sets the JmolConstants.ATOM_IN_MODEL flag.
    shapes[JmolConstants.SHAPE_BALLS].setVisibilityFlags(bs);

    //set clickability -- this enables measures and such
    for (int i = 0; i < JmolConstants.SHAPE_MAX; ++i) {
      Shape shape = shapes[i];
      if (shape != null)
        shape.setModelClickability();
    }
  }

  float getAtomShapeValue(int tok, Group group, int atomIndex) {
    int iShape = JmolConstants.shapeTokenIndex(tok);
    if (iShape < 0 || shapes[iShape] == null) 
      return 0;
    int mad = shapes[iShape].getSize(atomIndex);
    if (mad == 0) {
      if ((group.shapeVisibilityFlags & shapes[iShape].myVisibilityFlag) == 0)
        return 0;
      mad = shapes[iShape].getSize(group);
    }
    return mad / 2000f;
  }

  public boolean frankClicked(int x, int y) {
    Shape frankShape = shapes[JmolConstants.SHAPE_FRANK];
    return (frankShape != null && frankShape.wasClicked(x, y));
  }

  public boolean checkObjectHovered(int x, int y, BitSet bsVisible, boolean checkBonds) {
    Shape shape    = shapes[JmolConstants.SHAPE_STICKS];
    if (checkBonds && shape != null 
        && shape.checkObjectHovered(x, y, bsVisible))
      return true;
    shape = shapes[JmolConstants.SHAPE_ECHO];
    if (shape != null && shape.checkObjectHovered(x, y, bsVisible))
      return true;
    shape = shapes[JmolConstants.SHAPE_ISOSURFACE];
    if (shape != null && shape.checkObjectHovered(x, y, bsVisible))
      return true;
    shape = shapes[JmolConstants.SHAPE_DRAW];
    if (shape != null && viewer.getDrawHover() 
        && shape.checkObjectHovered(x, y, bsVisible))
      return true;
    shape = shapes[JmolConstants.SHAPE_FRANK];
    if (viewer.getShowFrank() && shape != null  
        && shape.checkObjectHovered(x, y, bsVisible))
      return true;
    return false;
  }

  public Token checkObjectClicked(int x, int y, int modifiers,
                                    BitSet bsVisible) {
    Shape shape;
    Point3fi pt = null;
    if ((shape = shapes[JmolConstants.SHAPE_ISOSURFACE]) != null
        && (viewer.getDrawPicking() || viewer.getNavigationMode() && viewer.getNavigateSurface()) 
         && (pt = shape.checkObjectClicked(x, y, modifiers, bsVisible)) != null)
      return new Token(Token.isosurface, pt);

    if (modifiers != 0 && viewer.getBondPicking()
        && (pt = shapes[JmolConstants.SHAPE_STICKS].checkObjectClicked(x, y,
            modifiers, bsVisible)) != null)
      return new Token(Token.bonds, pt);

    if ((shape = shapes[JmolConstants.SHAPE_ECHO])!= null && modifiers != 0
        && (pt = shape.checkObjectClicked(x, y, modifiers, bsVisible)) != null)
      return new Token(Token.echo, pt);
    if ((shape = shapes[JmolConstants.SHAPE_DRAW]) != null && 
        (pt = shape.checkObjectClicked(x, y, modifiers, bsVisible)) != null)
      return new Token(Token.draw, pt);
    return null;
  }
 
  public boolean checkObjectDragged(int prevX, int prevY, int x, int y,
                          int modifiers, BitSet bsVisible, int iShape) {
    for (int i = iShape; i < JmolConstants.SHAPE_MAX; ++i) {
      Shape shape = shapes[i];
      if (shape != null
          && shape.checkObjectDragged(prevX, prevY, x, y, modifiers, bsVisible)
          || iShape > 0)
        return true;
    }
    return false;
  }

  public Map<String, Object> getShapeInfo() {
    Map<String, Object> info = new Hashtable<String, Object>();
    StringBuffer commands = new StringBuffer();
    if (shapes != null)
      for (int i = 0; i < JmolConstants.SHAPE_MAX; ++i) {
        Shape shape = shapes[i];
        if (shape != null) {
          String shapeType = JmolConstants.shapeClassBases[i];
          List<Map<String, Object>> shapeDetail = shape.getShapeDetail();
          if (shapeDetail != null)
            info.put(shapeType, shapeDetail);
        }
      }
    if (commands.length() > 0)
      info.put("shapeCommands", commands.toString());
    return info;
  }

  public void loadDefaultShapes(ModelSet modelSet) {
    setShapeModelSet(modelSet);
    loadShape(JmolConstants.SHAPE_BALLS);
    loadShape(JmolConstants.SHAPE_STICKS);
    loadShape(JmolConstants.SHAPE_MEASURES);
    loadShape(JmolConstants.SHAPE_BBCAGE);
    loadShape(JmolConstants.SHAPE_UCCAGE);
  }

  public void refreshShapeTrajectories(int baseModel, BitSet bs) {
    Integer Imodel = Integer.valueOf(baseModel);
    for (int i = 0; i < JmolConstants.SHAPE_MAX; i++)
      if (shapes[i] != null)
        setShapeProperty(i, "refreshTrajectories", Imodel, bs);    
  }

  public void deleteShapeAtoms(Object[] value, BitSet bs) {
    if (shapes != null)
      for (int j = 0; j < JmolConstants.SHAPE_MAX; j++)
        if (shapes[j] != null)
          setShapeProperty(j, "deleteModelAtoms", value, bs);
  }

  public void setLabel(String strLabel, BitSet bsSelection) {
    if (strLabel != null) { // force the class to load and display
      loadShape(JmolConstants.SHAPE_LABELS);
      setShapeSize(JmolConstants.SHAPE_LABELS, 0, null, bsSelection);
    }
    setShapeProperty(JmolConstants.SHAPE_LABELS, "label", strLabel, bsSelection);
  }

  public void setAtomLabel(String strLabel, int i) {
    if (shapes == null)
      return;
    loadShape(JmolConstants.SHAPE_LABELS);
    shapes[JmolConstants.SHAPE_LABELS].setProperty("label:"+strLabel, Integer.valueOf(i), null);
  }
  
  public void findNearestShapeAtomIndex(int x, int y, Atom[] closest, BitSet bsNot) {
    if (shapes != null)
      for (int i = 0; i < shapes.length && closest[0] == null; ++i)
        if (shapes[i] != null)
          shapes[i].findNearestAtomIndex(x, y, closest, bsNot);
  }

  public void getShapeState(StringBuffer commands, boolean isAll) {
    if (shapes == null)
      return;
    String cmd;
    for (int i = 0; i < JmolConstants.SHAPE_MAX; ++i) {
      Shape shape = shapes[i];
      if (shape != null && (isAll || JmolConstants.isShapeSecondary(i))
          && (cmd = shape.getShapeState()) != null && cmd.length() > 1)
        commands.append(cmd);
    }
    commands.append("  select *;\n");
  }

  public void resetBioshapes(BitSet bsAllAtoms) {
    if (shapes == null)
      return;
    for (int i = 0; i < shapes.length; ++i)
      if (shapes[i] != null && shapes[i].isBioShape) {
        shapes[i].setModelSet(modelSet);
        shapes[i].setShapeSize(0, null, bsAllAtoms);
        shapes[i].setShapeProperty("color",
            new Byte(JmolConstants.PALETTE_NONE), bsAllAtoms);
      }
  }

  private ModelSet modelSet;
  private void setShapeModelSet(ModelSet newModelSet) {
    modelSet = newModelSet;
    if (shapes == null)
      return;
    for (int i = 0; i < shapes.length; ++i)
      if (shapes[i] != null)
        shapes[i].setModelSet(newModelSet);
  }

  public void mergeShapes(Shape[] newShapes) {
    if (newShapes == null)
      return;
    if (shapes == null)
      shapes = newShapes;
    else
      for (int i = 0; i < newShapes.length; ++i)
        if (newShapes[i] != null) {
          if (shapes[i] == null)
            loadShape(i);
          shapes[i].merge(newShapes[i]);
        }
  }

  private final BitSet bsOK = new BitSet();
  public BitSet transformAtoms(boolean firstPass) {
    if (!firstPass)
      return bsOK;
    bsOK.clear();
    Atom[] atoms = modelSet.atoms;
    Vector3f[] vibrationVectors = modelSet.vibrationVectors;
    for (int i = modelSet.getAtomCount(); --i >= 0;) {
      Atom atom = atoms[i];
      if ((atom.getShapeVisibilityFlags() & JmolConstants.ATOM_IN_FRAME) == 0)
        continue;
      bsOK.set(i);
      Point3i screen;
      if (vibrationVectors != null && atom.hasVibration())
        screen = viewer.transformPoint(atom, vibrationVectors[i]);
      else
        screen = viewer.transformPoint(atom);
      // ultimately I would like to dissociate the rendering 
      // from the modelSet completely. 
      atom.screenX = screen.x;
      atom.screenY = screen.y;
      atom.screenZ = screen.z;
      atom.screenDiameter = viewer.scaleToScreen(screen.z, Math
          .abs(atom.madAtom));
//      System.out.println("shapeman " + atom + " scaleToScreen(" + screen.z + "," + atom.madAtom + ")=" + atom.screenDiameter);
    }
    return bsOK;
  }
}
