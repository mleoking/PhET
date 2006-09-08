/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Jan 25, 2005
 * Time: 3:01:28 AM
 * Copyright (c) Jan 25, 2005 by Sam Reid
 */

public class ThresholdedDragAdapter implements MouseInputListener {
    private Point lastPressLocation;
    private long lastDragTime;
    private MouseInputListener target;
    private int thresholdX = 0;
    private int thresholdY = 0;
    private long thresholdMillis = 0;
    private boolean isDragging = false;

    public ThresholdedDragAdapter( MouseInputListener target, int thresholdX, int thresholdY, long thresholdMillis ) {
        this.target = target;
        this.thresholdX = thresholdX;
        this.thresholdY = thresholdY;
        this.thresholdMillis = thresholdMillis;
    }

    public void mouseClicked( MouseEvent e ) {
        target.mouseClicked( e );
    }

    public void mouseEntered( MouseEvent e ) {
        target.mouseEntered( e );
    }

    public void mouseExited( MouseEvent e ) {
        target.mouseExited( e );
    }

    public void mousePressed( MouseEvent e ) {
        lastPressLocation = e.getPoint();
        target.mousePressed( e );
        lastDragTime = System.currentTimeMillis();
    }

    public void mouseReleased( MouseEvent e ) {
        lastPressLocation = null;
        target.mouseReleased( e );
//        System.out.println( "mouse released" + e );
    }

    public void mouseDragged( MouseEvent e ) {
        if( System.currentTimeMillis() - lastDragTime >= thresholdMillis ) {//you waited too long, now have to start dragging again.
            resetDragging();
        }
        if( lastPressLocation == null ) {
            lastPressLocation = e.getPoint();
        }
        int dx = Math.abs( lastPressLocation.x - e.getX() );
        int dy = Math.abs( lastPressLocation.y - e.getY() );
        if( dx >= thresholdX && dy >= thresholdY ) {
            isDragging = true;
        }
        if( isDragging ) {
//            System.out.println( "mouse dragged" + e );
            target.mouseDragged( e );
        }
        lastDragTime = System.currentTimeMillis();
    }

    public void mouseMoved( MouseEvent e ) {
        target.mouseMoved( e );
    }

    public void resetDragging() {
        isDragging = false;
        lastPressLocation = null;
    }
}
