/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molectularreactions.view.energy;

import edu.colorado.phet.piccolo.nodes.RegisterablePNode;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.PNode;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.*;
import java.awt.Cursor;

/**
 * Cursor
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
class EnergyCursor extends PNode {
    private double width = 10;

    EnergyCursor( double height ) {
        Rectangle2D cursorShape = new Rectangle2D.Double( -width / 2, 0, width, height );
        PPath cursorPPath = new PPath( cursorShape );
        cursorPPath.setStroke( new BasicStroke( 1 ) );
        cursorPPath.setStrokePaint( new Color( 200, 200, 200 ) );
        cursorPPath.setPaint( new Color( 200, 200, 200, 200 ) );
        addChild( cursorPPath );
        this.addInputEventListener( new MouseHandler( this ) );
    }

    /**
     * Handles mousing on the cursor
     */
    private static class MouseHandler extends PBasicInputEventHandler {
        EnergyCursor energyCursor;

        public MouseHandler( EnergyCursor energyCursor ) {
            this.energyCursor = energyCursor;
        }

        public void mouseEntered( PInputEvent event) {
            PhetPCanvas ppc = (PhetPCanvas) event.getComponent();
            ppc.setCursor(new Cursor( Cursor.W_RESIZE_CURSOR));
        }

        public void mouseExited(PInputEvent event) {
            PhetPCanvas ppc = (PhetPCanvas) event.getComponent();
            ppc.setCursor( Cursor.getDefaultCursor());
        }

        public void mouseDragged(PInputEvent event) {
            double dx = event.getDelta().getWidth();
            System.out.println( "dx = " + dx );
            Point2D p = energyCursor.getOffset();
            energyCursor.setOffset( energyCursor.getOffset().getX() + dx,
                                    energyCursor.getOffset().getY() );
        }
    }
}
