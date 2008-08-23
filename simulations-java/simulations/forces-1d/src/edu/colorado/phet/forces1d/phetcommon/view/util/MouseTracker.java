/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.forces1d.phetcommon.view.util;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import edu.colorado.phet.forces1d.phetcommon.view.ApparatusPanel2;
import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.PhetTextGraphic;

/**
 * A utility class that draws crosshair lines at the location of the mouse, and prints the coordinates of the mouse.
 * <p/>
 * Intended for use when designing a laying out a simulation.
 */
public class MouseTracker extends CompositePhetGraphic implements MouseMotionListener, ApparatusPanel2.ChangeListener {
    private PhetTextGraphic readout;
    private Point mouseLocation = new Point();
    private Dimension canvasSize = new Dimension();
    private Stroke stroke = new BasicStroke( 1f );

    public MouseTracker( ApparatusPanel2 component ) {
        super( component );

        component.addMouseMotionListener( this );
        component.addChangeListener( this );

        readout = new PhetTextGraphic( component, new Font( "Lucida sans", Font.PLAIN, 10 ),
                                       "", Color.black );
        this.addGraphic( readout );
        this.addGraphic( new CrosshairGraphic( component ) );
        setCanvasSize( component.getCanvasSize() );
    }

    protected Rectangle determineBounds() {
        return new Rectangle( canvasSize );
    }

    public void setCanvasSize( Dimension size ) {
        canvasSize = size;
    }

    public void paint( Graphics2D g2 ) {
        GraphicsState gs = new GraphicsState( g2 );

        Rectangle rect = new Rectangle( readout.getBounds() );
        rect.setBounds( rect.x - 2, rect.y - 2, rect.width + 3, rect.height + 2 );
        g2.setColor( Color.white );
        g2.fill( rect );
        g2.setColor( Color.black );
        g2.draw( rect );
        super.paint( g2 );

        gs.restoreGraphics();
    }

    //----------------------------------------------------------------
    // Graphic classes
    //----------------------------------------------------------------

    private class CrosshairGraphic extends PhetGraphic {
        protected CrosshairGraphic( Component component ) {
            super( component );
        }

        protected Rectangle determineBounds() {
            return new Rectangle( canvasSize );
        }

        public void paint( Graphics2D g2 ) {
            GraphicsState gs = new GraphicsState( g2 );

            g2.setStroke( stroke );
            g2.setColor( Color.red );
            g2.drawLine( 0, (int) mouseLocation.getY(), canvasSize.width, (int) mouseLocation.getY() );
            g2.drawLine( (int) mouseLocation.getX(), 0, (int) mouseLocation.getX(), canvasSize.height );

            gs.restoreGraphics();
        }
    }

    //-----------------------------------------------------------------
    // ApparatusPanel2.ChangeListener implementation
    //-----------------------------------------------------------------
    public void canvasSizeChanged( ApparatusPanel2.ChangeEvent event ) {
        setCanvasSize( event.getCanvasSize() );
    }

    //-----------------------------------------------------------------
    // MouseListener implementation
    //-----------------------------------------------------------------

    public void mouseDragged( MouseEvent e ) {
        mouseMoved( e );
    }

    public void mouseMoved( MouseEvent e ) {
        mouseLocation.setLocation( e.getPoint() );
        readout.setLocation( mouseLocation.x + 8, mouseLocation.y - 16 );
        readout.setText( "[" + mouseLocation.x + ":" + mouseLocation.y + "]" );
        setBoundsDirty();
        repaint();
    }
}
