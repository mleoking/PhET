/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view.graphics.mousecontrols;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Oct 9, 2003
 * Time: 12:53:40 AM
 * Copyright (c) Oct 9, 2003 by Sam Reid
 */
public class HandCursorControl implements MouseInputListener {
    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
        e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void mouseExited(MouseEvent e) {
        e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }
//
//    AbstractShape shape;
//
//    public HandCursorControl(AbstractShape shape) {
//        this.shape = shape;
//    }
//
//    public boolean canHandleMousePress(MouseEvent event, Point2D.Double modelLoc) {
//        return shape.contains(modelLoc.x, modelLoc.y);
//    }
//
//    public void mouseDragged(MouseEvent event, Point2D.Double modelLoc) {
//    }
//
//    public void mousePressed(MouseEvent event, Point2D.Double modelLoc) {
//    }
//
//    public void mouseReleased(MouseEvent event, Point2D.Double modelLoc) {
//    }
//
//    public void mouseEntered(MouseEvent event, Point2D.Double modelLoc) {
//        event.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//    }
//
//    public void mouseExited(MouseEvent event, Point2D.Double modelLoc) {
//        event.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//    }
//
//    public void mouseClicked( MouseEvent e ) {
//    }
//
//    public void mousePressed( MouseEvent e ) {
//    }
//
//    public void mouseReleased( MouseEvent e ) {
//    }
//
//    public void mouseEntered( MouseEvent e ) {
//    }
//
//    public void mouseExited( MouseEvent e ) {
//    }
//
//    public void mouseDragged( MouseEvent e ) {
//    }
//
//    public void mouseMoved( MouseEvent e ) {
//    }
}
