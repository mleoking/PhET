/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.util;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.ApparatusPanel2;

import java.awt.*;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;

/**
 * A utility class that draws crosshair lines at the location of the mouse, and prints the coordinates of the mouse.
 * <p>
 * Intended for use when designing a laying out a simulation. 
 */
public class MouseTracker extends CompositePhetGraphic implements MouseMotionListener, ApparatusPanel2.ChangeListener {
    private PhetTextGraphic readout;
    private Point mouseLocation = new Point();
    private Dimension canvasSize = new Dimension();

    public MouseTracker( ApparatusPanel2 component ) {
        super( component );
        component.addMouseMotionListener( this );
        component.addChangeListener( this );

        readout = new PhetTextGraphic( component, new Font( "Lucida sans", Font.PLAIN, 10 ),
                                       "", Color.black );
        this.addGraphic( readout );
        this.addGraphic( new CrosshairGraphic( component ));
    }

    protected Rectangle determineBounds() {
        return new Rectangle( canvasSize );
    }

    private class CrosshairGraphic extends PhetGraphic {
        protected CrosshairGraphic( Component component ) {
            super( component );
        }

        protected Rectangle determineBounds() {
            return getComponent().getBounds();
        }

        public void paint( Graphics2D g2 ) {
            GraphicsState gs = new GraphicsState( g2 );

            g2.setColor( Color.red );
            g2.drawLine( 0, (int)mouseLocation.getY(), canvasSize.width, (int)mouseLocation.getY() );
            g2.drawLine( (int)mouseLocation.getX(), 0, (int)mouseLocation.getX(), canvasSize.height );
            readout.setLocation( mouseLocation.x + 8, mouseLocation.y - 12 );
            readout.setText( "[" + mouseLocation.x + ":" + mouseLocation.y + "]" );

            gs.restoreGraphics();
        }
    }

    //-----------------------------------------------------------------
    // ApparatusPanel2.ChangeListener implementation
    //-----------------------------------------------------------------
    public void canvasSizeChanged( ApparatusPanel2.ChangeEvent event ) {
        canvasSize.setSize( event.getCanvasSize() );
    }

    //-----------------------------------------------------------------
    // MouseListener implementation
    //-----------------------------------------------------------------

    public void mouseDragged( MouseEvent e ) {
    }

    public void mouseMoved( MouseEvent e ) {
        mouseLocation.setLocation( e.getPoint() );
        setBoundsDirty();
        repaint();
    }
}
