/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2011-03-11 16:34:40 -0800 (Fri, 11 Mar 2011) $
 * $Revision: 15276 $
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
package org.openscience.jmol.app.jmolpanel;

import org.jmol.api.*;
import org.jmol.console.KeyJMenuItem;
import org.jmol.i18n.GT;
import org.openscience.jmol.app.jmolpanel.GuiMap;
import org.openscience.jmol.app.jmolpanel.JmolPanel;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import javax.swing.*;
import javax.swing.event.*;

public class DisplayPanel extends JPanel
  implements JmolFrame, ComponentListener, Printable {
  
  StatusBar status;
  GuiMap guimap;
  JmolViewer viewer;
    
  private String displaySpeed;

  private Dimension startupDimension;
  boolean haveDisplay;
  Point border;
  boolean haveBorder;
  MeasurementTable measurementTable;
  JmolPanel jmolPanel;

  private JFrame frame;
  
  public JFrame getFrame() {
    return frame;
  }
  
  DisplayPanel(JmolPanel jmol) {
    jmolPanel = jmol;
    frame = jmol.frame;
    status = jmol.status;
    guimap = jmol.guimap;
    border = jmol.jmolApp.border;
    haveDisplay = jmol.jmolApp.haveDisplay;
    startupDimension = new Dimension(jmol.startupWidth, jmol.startupHeight);
    setFocusable(true);
    if (System.getProperty("painttime", "false").equals("true"))
      showPaintTime = true;
    displaySpeed = System.getProperty("display.speed");
    if (displaySpeed == null) {
      displaySpeed = "ms";
    }
    setDoubleBuffered(false);
  }

  void setViewer(JmolViewer viewer) {
    this.viewer = viewer;
    viewer.setScreenDimension(haveDisplay? getSize(dimSize) : startupDimension);
  }

 
  // for now, default to true
  private boolean showPaintTime = true;

  // current dimensions of the display screen
  final Dimension dimSize = new Dimension();
  private final Rectangle rectClip = new Rectangle();

  public void start() {
    addComponentListener(this);
  }

  AbstractButton buttonRotate;
  AbstractButton buttonModelkit;
  
  ButtonGroup toolbarButtonGroup = new ButtonGroup();

  void setRotateMode() {
    if (buttonRotate != null)
      buttonRotate.setSelected(true);
      viewer.setSelectionHalos(false);
  }
    
  void setModelkitMode() {
    if (buttonModelkit != null)
      buttonModelkit.setSelected(true);
    viewer.setSelectionHalos(false);
  }

  public void componentHidden(java.awt.event.ComponentEvent e) {
    //System.out.println("DisplayPanel.componentHidden");
  }

  public void componentMoved(java.awt.event.ComponentEvent e) {
    //System.out.println("DisplayPanel.componentMoved " + e.getComponent().getX() + " " + e.getComponent().getY());
  }

  public void componentResized(java.awt.event.ComponentEvent e) {
    //System.out.println("DisplayPanel.componentResized");
    updateSize();
  }

  public void componentShown(java.awt.event.ComponentEvent e) {
    //System.out.println("DisplayPanel.componentShown");
    updateSize();
  }

  private void updateSize() {
    viewer.setScreenDimension(haveDisplay? getSize(dimSize) : startupDimension);
    setRotateMode();
    if (haveDisplay)
      status.setStatus(2, dimSize.width + " x " + dimSize.height);
    viewer.refresh(3, "updateSize");
  }

  @Override
  public void paint(Graphics g) {
    if (showPaintTime)
      startPaintClock();
    if (dimSize.width == 0)
      return;
    //System.out.println("DisplayPanel:paint");System.out.flush();

    g.getClipBounds(rectClip);
    viewer.renderScreenImage(g, dimSize, rectClip);
    if (border == null)
      border = new Point();
    if (!haveBorder)
      setBorder();
    if (showPaintTime)
      stopPaintClock();
  }
   
  void setBorder() {
    if (dimSize.width < 50)
      return;
    border.x = startupDimension.width - dimSize.width;
    border.y = startupDimension.height - dimSize.height;
    haveBorder = true;    
  }
  
  public int print(Graphics g, PageFormat pf, int pageIndex) {
    Graphics2D g2 = (Graphics2D)g;
    if (pageIndex > 0)
      return Printable.NO_SUCH_PAGE;
    rectClip.x = rectClip.y = 0;
    int screenWidth = rectClip.width = viewer.getScreenWidth();
    int screenHeight = rectClip.height = viewer.getScreenHeight();
    Image image = viewer.getScreenImage(null);
    int pageX = (int)pf.getImageableX();
    int pageY = (int)pf.getImageableY();
    int pageWidth = (int)pf.getImageableWidth();
    int pageHeight = (int)pf.getImageableHeight();
    float scaleWidth = pageWidth / (float)screenWidth;
    float scaleHeight = pageHeight / (float)screenHeight;
    float scale = (scaleWidth < scaleHeight ? scaleWidth : scaleHeight);
    if (scale < 1) {
      int width =(int)(screenWidth * scale);
      int height =(int)(screenHeight * scale);
      g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                          RenderingHints.VALUE_RENDER_QUALITY);
      g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                          RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      g2.drawImage(image, pageX, pageY, width, height, null);
    } else {
      g2.drawImage(image, pageX, pageY, null);
    }
    viewer.releaseScreenImage();
    return Printable.PAGE_EXISTS;
  }

  // The actions:

  private HomeAction homeAction = new HomeAction();
  private DefineCenterAction defineCenterAction = new DefineCenterAction();
  private Action frontAction        = new MoveToAction("front",  "moveto 2.0 front");
  private Action topAction          = new MoveToAction("top",    "moveto 1.0 front;moveto 2.0 top");
  private Action bottomAction       = new MoveToAction("bottom", "moveto 1.0 front;moveto 2.0 bottom");
  private Action rightAction        = new MoveToAction("right",  "moveto 1.0 front;moveto 2.0 right");
  private Action leftAction         = new MoveToAction("left",   "moveto 1.0 front;moveto 2.0 left");
  private Action hydrogensAction    = new CheckBoxMenuItemAction("hydrogensCheck",    "set showHydrogens");
  private Action measurementsAction = new CheckBoxMenuItemAction("measurementsCheck", "set showMeasurements");
  private Action perspectiveAction  = new CheckBoxMenuItemAction("perspectiveCheck",  "set PerspectiveDepth");
  private Action axesAction         = new CheckBoxMenuItemAction("axesCheck",         "set showAxes");
  private Action boundboxAction     = new CheckBoxMenuItemAction("boundboxCheck",     "set showBoundBox");
  // next three are not implemented
  private Action deleteAction       = new SetStatusAction("delete", GT._("Delete atoms"));
  private Action zoomAction         = new SetStatusAction("zoom",   null);
  private Action xlateAction        = new SetStatusAction("xlate",  null);
  //

  // script actions are defined in Properties/Jmol-resource.properties
  // 
  
  /**
   * Action calling setStatus() 
   */
  private class SetStatusAction extends AbstractAction {
    private final String statusText;

    public SetStatusAction(String name, String status) {
      super(name);
      this.statusText = status;
      this.setEnabled(true);
    }

    public void actionPerformed(ActionEvent e) {
      viewer.setSelectionHalos(false);
      if (statusText != null) {
        status.setStatus(1, statusText);
      } else {
        status.setStatus(1, ((JComponent) e.getSource()).getToolTipText());
      }
    }
  }

  /**
   * Action calling moveTo() 
   */
  private class MoveToAction extends AbstractAction {
    private final String action;

    public MoveToAction(String name, String action) {
      super(name);
      this.action = action;
      this.setEnabled(true);
    }

    public void actionPerformed(ActionEvent e) {
      if (viewer.getShowBbcage() || viewer.getBooleanProperty("showUnitCell")) {
        viewer.evalStringQuiet(action);
      } else {
        viewer.evalStringQuiet("boundbox on;" + action + ";delay 1;boundbox off");
      }
    }
  }

  class DefineCenterAction extends AbstractAction {

    public DefineCenterAction() {
      super("definecenter");
      this.setEnabled(true);
    }

    public void actionPerformed(ActionEvent e) {
      viewer.evalStringQuiet("center (selected)");
      setRotateMode();
    }
  }

  class HomeAction extends AbstractAction {

    public HomeAction() {
      super("home");
      this.setEnabled(true);
    }

    public void actionPerformed(ActionEvent e) {
      viewer.homePosition();
      setRotateMode();
    }
  }

  /**
   * Action calling evalStringQuiet(&lt;action&gt; + CheckBoxState) 
   */
  private class CheckBoxMenuItemAction extends AbstractAction {
    private final String action;

    public CheckBoxMenuItemAction(String name, String action) {
      super(name);
      this.action = action;
      this.setEnabled(true);
    }

    public void actionPerformed(ActionEvent e) {
      JCheckBoxMenuItem cbmi = (JCheckBoxMenuItem) e.getSource();
      viewer.evalStringQuiet(action + " " + cbmi.isSelected());
    }
  }

  private MenuListener menuListener = new MenuListener() {
      public void menuSelected(MenuEvent e) {
        String menuKey = KeyJMenuItem.getKey(e.getSource());
        if (menuKey.equals("display")) {
          setDisplayMenuState();
        }
      }
      public void menuDeselected(MenuEvent e) {
      }
      public void menuCanceled(MenuEvent e) {
      }
    };

  public MenuListener getMenuListener() {
    return menuListener;
  }

  void setDisplayMenuState() {
    guimap.setSelected("perspectiveCheck", viewer.getPerspectiveDepth());
    guimap.setSelected("hydrogensCheck", viewer.getShowHydrogens());
    guimap.setSelected("measurementsCheck", viewer.getShowMeasurements());
    guimap.setSelected("axesCheck", viewer.getShowAxes());
    guimap.setSelected("boundboxCheck", viewer.getShowBbcage());
  }

  public Action[] getActions() {

    return new Action[] {
      deleteAction, zoomAction, xlateAction,
      frontAction, topAction, bottomAction, rightAction, leftAction,
      defineCenterAction,
      hydrogensAction, measurementsAction,
      homeAction, perspectiveAction,
      axesAction, boundboxAction,
    };
  }

  // code to record last and average times
  // last and average of all the previous times are shown in the status window

  private static int timeLast = 0;
  private static int timeCount;
  private static int timeTotal;

  private void resetTimes() {
    timeCount = timeTotal = 0;
    timeLast = -1;
  }

  private void recordTime(int time) {
    if (timeLast != -1) {
      timeTotal += timeLast;
      ++timeCount;
    }
    timeLast = time;
  }

  private long timeBegin;
  private int lastMotionEventNumber;

  private void startPaintClock() {
    timeBegin = System.currentTimeMillis();
    int motionEventNumber = viewer.getMotionEventNumber();
    if (lastMotionEventNumber != motionEventNumber) {
      lastMotionEventNumber = motionEventNumber;
      resetTimes();
    }
  }

  private void stopPaintClock() {
    int time = (int)(System.currentTimeMillis() - timeBegin);
    recordTime(time);
    showTimes();
  }

  private String fmt(int num) {
    if (num < 0)
      return "---";
    if (num < 10)
      return "  " + num;
    if (num < 100)
      return " " + num;
    return "" + num;
  }

  private void showTimes() {
    int timeAverage =
      (timeCount == 0)
      ? -1
      : (timeTotal + timeCount/2) / timeCount; // round, don't truncate
    if (displaySpeed.equalsIgnoreCase("fps")) {
        status.setStatus(3, fmt(1000/timeLast) + "FPS : " + fmt(1000/timeAverage) + "FPS");
    } else {
      status.setStatus(3, viewer.getParameter("_memory")+" Mb; " + fmt(timeLast) + "/" + timeAverage + " ms");
    }
  }

}


