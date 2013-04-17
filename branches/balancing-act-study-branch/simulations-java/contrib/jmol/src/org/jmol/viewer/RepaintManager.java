/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2011-03-23 20:46:59 -0700 (Wed, 23 Mar 2011) $
 * $Revision: 15335 $
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
 *  Lesser General License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jmol.viewer;

import org.jmol.api.JmolRendererInterface;
import org.jmol.g3d.*;
import org.jmol.modelset.ModelSet;
import org.jmol.shape.Shape;
import org.jmol.shape.ShapeRenderer;
import org.jmol.util.Logger;

import java.awt.Rectangle;
import java.util.BitSet;

import javax.vecmath.Point3f;

class RepaintManager {

  private Viewer viewer;
  private ShapeManager shapeManager;

  RepaintManager(Viewer viewer, ShapeManager shapeManager) {
    this.viewer = viewer;
    this.shapeManager = shapeManager;
  }

  void clear() {
    clear(-1);
  }

  private int holdRepaint = 0;
  boolean repaintPending;

  void pushHoldRepaint() {
    ++holdRepaint;
    //System.out.println("repaintManager pushHoldRepaint holdRepaint=" + holdRepaint + " thread=" + Thread.currentThread().getName());
  }

  //private int test = 0;

  void popHoldRepaint(boolean andRepaint) {
    --holdRepaint;
    //System.out.println("repaintManager popHoldRepaint holdRepaint=" + holdRepaint + " thread=" + Thread.currentThread().getName());
    if (holdRepaint <= 0) {
      holdRepaint = 0;
      if (andRepaint) {
        repaintPending = true;
        //System.out.println("RM popholdrepaint TRUE " + (test++));
        viewer.repaint();
      }
    }
  }

  boolean refresh() {
    if (repaintPending)
      return false;
    repaintPending = true;
    if (holdRepaint == 0) {
      //System.out.println("RM refresh() " + (test++));
      viewer.repaint();
    }
    return true;
  }

  synchronized void requestRepaintAndWait() {
    //System.out.println("RM requestRepaintAndWait() " + (test++));
    viewer.repaint();
    try {
      //System.out.println("repaintManager requestRepaintAndWait I am waiting for a repaint: thread=" + Thread.currentThread().getName());
      wait(viewer.getRepaintWait());  // more than a second probably means we are locked up here
      if (repaintPending) {
        Logger.error("repaintManager requestRepaintAndWait timeout");
        repaintDone();
      }
    } catch (InterruptedException e) {
      //System.out.println("repaintManager requestRepaintAndWait interrupted thread=" + Thread.currentThread().getName());
    }
    //System.out.println("repaintManager requestRepaintAndWait I am no longer waiting for a repaint: thread=" + Thread.currentThread().getName());
  }

  synchronized void repaintDone() {
    repaintPending = false;
    //System.out.println("repaintManager repaintDone thread=" + Thread.currentThread().getName());
    notify(); // to cancel any wait in requestRepaintAndWait()
  }

  void render(Graphics3D g3d, ModelSet modelSet) {// , Rectangle rectClip
    //System.out.println("render " + (test++));
    //System.out.println("repaintManager render thread=" + Thread.currentThread().getName());
    render1(g3d, modelSet); // , rectClip
    Rectangle band = viewer.getRubberBandSelection();
    if (band != null && g3d.setColix(viewer.getColixRubberband()))
      g3d.drawRect(band.x, band.y, 0, 0, band.width, band.height);
  }

  private boolean logTime;
  
  private void render1(Graphics3D g3d, ModelSet modelSet) { // , Rectangle
                                                            // rectClip
    if (modelSet == null || !viewer.mustRenderFlag())
      return;
    if (logTime)
      Logger.startTimer();



    logTime = viewer.getTestFlag1();
    viewer.finalizeTransformParameters();
    try {
      if (bsAtoms != null)
        translateSelected();
      g3d.renderBackground();
      if (renderers == null)
        renderers = new ShapeRenderer[JmolConstants.SHAPE_MAX];
      for (int i = 0; i < JmolConstants.SHAPE_MAX && g3d.currentlyRendering(); ++i) {
        Shape shape = shapeManager.getShape(i);
        if (shape == null)
          continue;
        getRenderer(i, g3d).render(g3d, modelSet, shape);
        if (logTime)
          Logger.checkTimer("render time " + JmolConstants.getShapeClassName(i));
      }
    } catch (Exception e) {
      e.printStackTrace();
      Logger.error("rendering error? ");
    }
    if (logTime)
      Logger.checkTimer("render time");
  }

  private ShapeRenderer[] renderers;
  
  void clear(int iShape) {
    if (renderers ==  null)
      return;
    if (iShape >= 0)
      renderers[iShape] = null;
    else
      for (int i = 0; i < JmolConstants.SHAPE_MAX; ++i)
        renderers[i] = null;
  }

  private ShapeRenderer getRenderer(int shapeID, Graphics3D g3d) {
    if (renderers[shapeID] == null)
      renderers[shapeID] = allocateRenderer(shapeID, g3d);
    return renderers[shapeID];
  }

  private ShapeRenderer allocateRenderer(int shapeID, Graphics3D g3d) {
    String className = JmolConstants.getShapeClassName(shapeID) + "Renderer";
    try {
      Class<?> shapeClass = Class.forName(className);
      ShapeRenderer renderer = (ShapeRenderer) shapeClass.newInstance();
      renderer.setViewerG3dShapeID(viewer, g3d, shapeID);
      return renderer;
    } catch (Exception e) {
      Logger.error("Could not instantiate renderer:" + className, e);
    }
    return null;
  }

  String generateOutput(String type, Graphics3D g3d, ModelSet modelSet,
                        String fileName) {

    JmolRendererInterface g3dExport = null;
    Object output = null;
    boolean isOK = false;
    try {
      if (fileName == null) {
        output = new StringBuffer();
      } else {
        output = fileName;
      }
      Class<?> export3Dclass = Class.forName("org.jmol.export.Export3D");
      g3dExport = (JmolRendererInterface) export3Dclass.newInstance();
      isOK = viewer.initializeExporter(g3dExport, type, output);
    } catch (Exception e) {
    }
    if (!isOK) {
      Logger.error("Cannot export " + type);
      return null;
    }
    g3dExport.renderBackground();
    for (int i = 0; i < JmolConstants.SHAPE_MAX; ++i) {
      Shape shape = shapeManager.getShape(i);
      if (shape != null)
        getRenderer(i, g3d).render(g3dExport, modelSet, shape);
    }
    return g3dExport.finalizeOutput();
  }

  // These next are to allow during-rendering operations.
  
  private BitSet bsAtoms;
  private Point3f ptOffset = new Point3f();
  
  void setSelectedTranslation(BitSet bsAtoms, char xyz, int xy) {
    this.bsAtoms = bsAtoms;
    switch (xyz) {
    case 'X':
    case 'x':
      ptOffset.x += xy;
      break;
    case 'Y':
    case 'y':
      ptOffset.y += xy;
      break;
    case 'Z':
    case 'z':
      ptOffset.z += xy;
      break;
    }
    //System.out.println(xyz + " " + xy + " " + ptOffset);
  }
  
  void translateSelected() {
    Point3f ptCenter = viewer.getAtomSetCenter(bsAtoms);
    Point3f pt = new Point3f();
    viewer.transformPoint(ptCenter, pt);
    pt.add(ptOffset);
    viewer.unTransformPoint(pt, pt);
    pt.sub(ptCenter);
    viewer.setAtomCoordRelative(pt, bsAtoms); 
    bsAtoms = null;
    ptOffset.set(0, 0, 0);
  }
}
