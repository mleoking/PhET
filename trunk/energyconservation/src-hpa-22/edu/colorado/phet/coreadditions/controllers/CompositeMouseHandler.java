/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.controllers;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 20, 2003
 * Time: 11:32:46 PM
 * Copyright (c) Sep 20, 2003 by Sam Reid
 */
public class CompositeMouseHandler implements MouseHandler {
    ArrayList list = new ArrayList();

    public void addMouseHandler( MouseHandler mh ) {
        list.add( mh );
    }

    public int numMouseHandlers() {
        return list.size();
    }

    public void removeMouseHandler( MouseHandler mh ) {
        list.remove( mh );
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        for( int i = 0; i < list.size(); i++ ) {
            MouseHandler mouseHandler = (MouseHandler)list.get( i );
            if( mouseHandler.canHandleMousePress( event ) ) {
                return true;
            }
        }
        return false;
    }

    public void mousePressed( MouseEvent event ) {
        for( int i = 0; i < list.size(); i++ ) {
            MouseHandler mouseHandler = (MouseHandler)list.get( i );
            mouseHandler.mousePressed( event );
        }
    }

    public void mouseDragged( MouseEvent event ) {
        for( int i = 0; i < list.size(); i++ ) {
            MouseHandler mouseHandler = (MouseHandler)list.get( i );
            mouseHandler.mouseDragged( event );
        }
    }

    public void mouseReleased( MouseEvent event ) {
        for( int i = 0; i < list.size(); i++ ) {
            MouseHandler mouseHandler = (MouseHandler)list.get( i );
            mouseHandler.mouseReleased( event );
        }
    }

    public void mouseEntered( MouseEvent event ) {
        for( int i = 0; i < list.size(); i++ ) {
            MouseHandler mouseHandler = (MouseHandler)list.get( i );
            mouseHandler.mouseEntered( event );
        }
    }

    public void mouseExited( MouseEvent event ) {
        for( int i = 0; i < list.size(); i++ ) {
            MouseHandler mouseHandler = (MouseHandler)list.get( i );
            mouseHandler.mouseExited( event );
        }
    }
}
