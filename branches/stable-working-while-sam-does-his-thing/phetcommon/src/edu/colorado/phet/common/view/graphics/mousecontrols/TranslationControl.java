/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.graphics.mousecontrols;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * TranslationControl
 *
 * @author sam Reid
 * @version $Revision$
 */
public class TranslationControl implements MouseInputListener {
    private Translatable t;
    private Point last;

    public TranslationControl( Translatable t ) {
        this.t = t;
    }

    public void mouseDragged( MouseEvent event ) {
        if( last == null ) {
            mousePressed( event );
            return;
        }
        Point modelLoc = event.getPoint();
        Point2D.Double dx = new Point2D.Double( modelLoc.x - last.x, modelLoc.y - last.y );
        t.translate( dx.x, dx.y );
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
