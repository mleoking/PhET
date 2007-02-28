/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view.mousecontrol;

import edu.colorado.phet.common.view.mousecontrol.AbstractShape;
import edu.colorado.phet.common.view.mousecontrol.MouseControl;
import edu.colorado.phet.common.view.mousecontrol.Translatable;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Oct 9, 2003
 * Time: 12:43:47 AM
 * Copyright (c) Oct 9, 2003 by Sam Reid
 */
public class TranslationControl implements MouseControl {
    private Translatable t;
    private Point2D.Double last;
    private AbstractShape shape;

    public TranslationControl(Translatable t, AbstractShape shape) {
        this.t = t;
        this.shape = shape;
    }

    public void mouseDragged(MouseEvent event, Point2D.Double modelLoc) {
        Point2D.Double dx = new Point2D.Double(modelLoc.x - last.x, modelLoc.y - last.y);
        t.translate(dx.x, dx.y);
        last = modelLoc;
    }

    public boolean canHandleMousePress(MouseEvent event, Point2D.Double modelLoc) {
        return shape.contains(modelLoc.x,modelLoc.y);
    }

    public void mousePressed(MouseEvent event, Point2D.Double modelLoc) {
        last = modelLoc;
    }

    public void mouseReleased(MouseEvent event, Point2D.Double modelLoc) {
    }

    public void mouseEntered(MouseEvent event, Point2D.Double modelLoc) {
//        event.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void mouseExited(MouseEvent event, Point2D.Double modelLoc) {
//        event.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}
