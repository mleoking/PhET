/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view.graphics.mousecontrols;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Oct 9, 2003
 * Time: 12:43:47 AM
 * Copyright (c) Oct 9, 2003 by Sam Reid
 */
public class TranslationHandler implements MouseInputListener {

    TranslationListener translationListener;
    private Point last;

    public TranslationHandler( TranslationListener translationListener ) {
        this.translationListener = translationListener;
    }

    public void mouseDragged( MouseEvent event ) {
        if( last == null ) {
            mousePressed( event );
            return;
        }
        Point modelLoc = event.getPoint();
        Point dx = new Point( modelLoc.x - last.x, modelLoc.y - last.y );
        TranslationEvent trEvent = new TranslationEvent( event, event.getX(), event.getY(), dx.x, dx.y );
        translationListener.translationOccurred( trEvent );
        last = modelLoc;
    }

    public void mouseMoved( MouseEvent e ) {
    }

    public void mouseClicked( MouseEvent e ) {
    }

    public void mousePressed( MouseEvent event ) {
        last = event.getPoint();
    }

    public void mouseReleased( MouseEvent e ) {
    }

    public void mouseEntered( MouseEvent e ) {
    }

    public void mouseExited( MouseEvent e ) {
    }

}
