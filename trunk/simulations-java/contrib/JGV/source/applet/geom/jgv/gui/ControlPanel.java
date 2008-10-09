//
// Copyright (C) 1998-2000 Geometry Technologies, Inc. <info@geomtech.com>
// Copyright (C) 2002-2004 DaerMar Communications, LLC <jgv@daermar.com>
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//

package geom.jgv.gui;

import geom.jgv.JGVApplet;
import geom.jgv.controller.DragConstants;
import geom.jgv.controller.JGVController;
import geom.jgv.gui.LoadGeomFrame;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.applet.*;


public class ControlPanel
    extends Panel
    implements ActionListener, DragConstants, ItemListener {

    JGVPanel parent;
    Choice motionChoice;
    Choice editChoice;
    int dragMode = 0;
    int actionMode = 0;
    Button loadButton;
    Frame loadGeomFrame = null;
    AnnotateGeomFrame annotateGeomFrame = null;
    ImageLabel logo;
    GridBagLayout gridbag;
    JGVController controller = null;

    Color background = new Color( (float)0.5,
                                  (float)0.5,
                                  (float)0.5 );

    public ControlPanel(JGVPanel parent, JGVApplet applet) {
      this.parent = parent;

      Image logoImage = applet.getImageNow("logo.gif");
      logo = new ImageLabel(logoImage);
      loadButton = new Button("Load Object");
      loadButton.addActionListener(this);

      motionChoice = new Choice();
      motionChoice.addItem("Rotates [r]");
      motionChoice.addItem("Translates [t]");
      motionChoice.addItem("Scales [s]");
      motionChoice.addItem("Home [h]");
      motionChoice.addItem("Test Picking [m]");
      motionChoice.addItemListener(this);

      if (parent.isEditor()) {
          editChoice = new Choice();
          editChoice.addItem("Actions");
          editChoice.addItem("Annotation");
          //editChoice.addItem("Selection");
          editChoice.addItemListener(this);
      } else {
          editChoice = null;
      }



//      setLayout(new FlowLayout());
      gridbag = new GridBagLayout();
      setLayout(gridbag);
      reLayout();
    }

    // NOTE: the reLayout() method removes all the widgets from
    //   the panel and packs them back into it.  This is used
    //   in two places:
    //     1. In the constructor above, to lay out the panel
    //	      in the first place; in this case, the removeAll() is
    //	      unnecessary but harmless.
    //     2. In setDragMode below, to cause the Choice widget to
    //	      properly redisplay itself after a different selection
    //	      has been made.  This is because of a bug in the Netscape
    //	      AWT implementation (on the SGI, at least), which causes
    //	      the choice widget to fail to redisplay itself when its
    //	      value has been changed programmatically (by call its
    //	      select()) method.  I tried all sorts of tricks to work
    //	      around this (like calling the repaint(), invalidate(),
    //	      validate(), update(), paint(), and paintAll() methods of
    //	      both the Choice widget and of the panel itself), but
    //	      this is the only thing I've found that actually works.
    //	      // BTW, this scheme seems to be necessary only on SGI
    //	      Netscape.  Other AWT implementations seem to get it
    //	      right.
    //  mbp Fri Feb  7 15:24:28 1997
    public void reLayout() {
      GridBagConstraints c = new GridBagConstraints();
      removeAll();
      Label l;
      c.insets = new Insets(2,2,2,2);
      gridbag.setConstraints(logo, c);
      add(logo);
      gridbag.setConstraints(loadButton, c);
      add(loadButton);
      c.weightx = 1;
      c.anchor = GridBagConstraints.EAST;
      l = new Label("Motions");
      l.setBackground(this.background);
      gridbag.setConstraints(l, c);
      add(l);
      c.anchor = GridBagConstraints.WEST;
      gridbag.setConstraints(motionChoice, c);
      add(motionChoice);


      if (editChoice != null) {
          l = new Label("Modes");
          l.setBackground(this.background);
          c.anchor = GridBagConstraints.EAST;
          gridbag.setConstraints(l, c);
          add(l);
          c.anchor = GridBagConstraints.WEST;
          gridbag.setConstraints(editChoice, c);
          add(editChoice);
      }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(loadButton)) {
            if (loadGeomFrame == null) {
                loadGeomFrame = new LoadGeomFrame(controller);
                loadGeomFrame.setSize(300,150);
            }
            loadGeomFrame.show();
            loadGeomFrame.setVisible(true);
            loadGeomFrame.toFront();
        }
    }

    //public boolean action(Event e, Object arg) {
    public void itemStateChanged(ItemEvent e) {
      if (e.getSource().equals(motionChoice)) {
        switch (motionChoice.getSelectedIndex()) {
          case 0:
            controller.setDragMode(DRAG_ORBIT);
            break;
          case 1:
            controller.setDragMode(DRAG_TRANSLATE);
            break;
          case 2:
            controller.setDragMode(DRAG_SCALE);
            break;
          case 3:
            parent.reset();
            setDragMode(dragMode);
            break;
          case 4:
            controller.setDragMode(DRAG_PICK);
            break;
        }
      } else if (e.getSource().equals(editChoice)) {
        switch (editChoice.getSelectedIndex()) {
          case 0:
            controller.setActionMode(DRAG_ACTION);
            break;
          case 1:
            controller.setActionMode(DRAG_ANNOTATE);
            break;
          case 2:
            controller.setActionMode(DRAG_SELECT);
            break;
        }
      }
    }

    public void setDragMode(int mode) {
      if (dragMode != mode) {
        switch (mode) {
          case DRAG_ORBIT:
          case DRAG_ROTATE:
            motionChoice.select(0);
            break;
          case DRAG_TRANSLATE:
            motionChoice.select(1);
            break;
          case DRAG_SCALE:
            motionChoice.select(2);
            break;
          case DRAG_PICK:
            motionChoice.select(4);
            break;
        }
        dragMode = mode;
      }
      //reLayout();
    }

    public void setActionMode(int mode) {
      if (actionMode != mode && editChoice != null) {
        switch (mode) {
          case DRAG_ACTION:
            editChoice.select(0);
            break;
          case DRAG_ANNOTATE:
            editChoice.select(1);
            break;
          case DRAG_SELECT:
            editChoice.select(2);
            break;
        }
        actionMode = mode;
      }
      //reLayout();
    }

    // This is apparently how to give the panel background
    // a particular appearance --- draw it here. Contained
    // components get drawn on top of it.
    public void paint(Graphics g) {
        Dimension dim = getSize();

        g.setColor(background);
        g.fill3DRect(1, 1, dim.width-2, dim.height-2, true);

	g.setColor(Color.black);
	g.drawRect(0, 0, dim.width-1, dim.height-1);
    }


// doublebuffering method here to try to prevent flickering
// when reLayout() is called:


  Image offscreen;		// Scratch space
  boolean doublebuffered = true;	// If true, do double-buffering

  // This update method should be pluggable into anything that's a Component
  // (e.g. a subclass of Panel.)
  // We assume two instance variables declared above:
  //	 Image offscreen;		// Scratch space
  //	 boolean doublebuffered;	// If true, do double-buffering
  // With luck this may be all you need.
  public void update(Graphics screeng){
    Rectangle area = screeng.getClipBounds(); //getClipRect();
    Graphics g;

    if (doublebuffered) {
      /* If we're double-buffering, we need an (off-screen) image to
       * draw into.  Keep one lying around in "offscreen".
       * If that hasn't yet been initialized, or if our size has
       * changed since then, then create a new one.
       */
      if(offscreen == null ||
         offscreen.getWidth(null) != area.width ||
	 offscreen.getHeight(null) != area.height) {
	offscreen = createImage(area.width, area.height);
      }
      /* We'll render into the off-screen image's graphics area.
       * Start by clearing it to the background color.
       */
      g = offscreen.getGraphics();
    }
    else {
      /* Otherwise, we're single-buffered; render directly to the screen.  */
      g = screeng;
    }

    g.setColor(getBackground());
    g.fillRect(0, 0, area.width, area.height);
    g.setColor(getForeground());
    paint(g);

    /*
     * If we were double-buffering, finish by copying the image we just
     * drew into the relevant piece of the screen.
     */
    if (doublebuffered) {
      screeng.drawImage(offscreen,
	                area.x, area.y, area.width, area.height,
	                this);
    }
  }

  public void setController(JGVController controller) {
    this.controller = controller;
    if (annotateGeomFrame == null) {
        annotateGeomFrame = new AnnotateGeomFrame(controller);
        controller.setPickListener(controller.DRAG_ANNOTATE, annotateGeomFrame);
    }
  }

}
