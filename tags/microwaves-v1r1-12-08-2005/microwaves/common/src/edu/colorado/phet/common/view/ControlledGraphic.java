/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.mousecontrol.MouseControl;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Oct 9, 2003
 * Time: 12:45:24 AM
 * Copyright (c) Oct 9, 2003 by Sam Reid
 */
public class ControlledGraphic implements InteractiveGraphic{
    MouseControl mc;
    Graphic g;

    public ControlledGraphic(MouseControl mc, Graphic g) {
        this.mc = mc;
        this.g = g;
    }

    public boolean canHandleMousePress(MouseEvent event, Point2D.Double modelLoc) {
        return mc.canHandleMousePress(event, modelLoc);
    }

    public void mouseDragged(MouseEvent event, Point2D.Double modelLoc) {
        mc.mouseDragged(event, modelLoc);
    }

    public void mousePressed(MouseEvent event, Point2D.Double modelLoc) {
        mc.mousePressed(event, modelLoc);
    }

    public void mouseReleased(MouseEvent event, Point2D.Double modelLoc) {
        mc.mouseReleased(event, modelLoc);
    }

    public void mouseEntered(MouseEvent event, Point2D.Double modelLoc) {
        mc.mouseEntered(event, modelLoc);
    }

    public void mouseExited(MouseEvent event, Point2D.Double modelLoc) {
        mc.mouseExited(event, modelLoc);
    }

    public void paint(Graphics2D g) {
        this.g.paint(g);
    }
}
