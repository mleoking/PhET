/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view.mousecontrol;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Oct 9, 2003
 * Time: 12:53:40 AM
 * Copyright (c) Oct 9, 2003 by Sam Reid
 */
public class HandCursorControl implements MouseControl {

    AbstractShape shape;

    public HandCursorControl(AbstractShape shape) {
        this.shape = shape;
    }

    public boolean canHandleMousePress(MouseEvent event, Point2D.Double modelLoc) {
        return shape.contains(modelLoc.x, modelLoc.y);
    }

    public void mouseDragged(MouseEvent event, Point2D.Double modelLoc) {
    }

    public void mousePressed(MouseEvent event, Point2D.Double modelLoc) {
    }

    public void mouseReleased(MouseEvent event, Point2D.Double modelLoc) {
    }

    public void mouseEntered(MouseEvent event, Point2D.Double modelLoc) {
        event.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void mouseExited(MouseEvent event, Point2D.Double modelLoc) {
        event.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}
