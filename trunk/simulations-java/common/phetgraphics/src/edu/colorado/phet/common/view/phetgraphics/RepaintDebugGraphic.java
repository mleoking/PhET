/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.phetgraphics;

import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.ClockListener;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.ApparatusPanel;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

/**
 * RepaintDebugGraphic
 *
 * @author ?
 * @version $Revision$
 */
public class RepaintDebugGraphic extends PhetGraphic implements ClockListener {
    private int r = 255;
    private int g = 255;
    private int b = 255;
    private int alpha = 255;
    private ApparatusPanel panel;
    private IClock clock;
    private boolean active = false;

    public RepaintDebugGraphic( ApparatusPanel panel, IClock clock ) {
        this( panel, clock, 128 );
    }

    public RepaintDebugGraphic( ApparatusPanel panel, IClock clock, int transparency ) {
        super( panel );
        this.panel = panel;
        this.clock = clock;
        setActive( true );
        setIgnoreMouse( true );
        setTransparency( transparency );
    }

    public void setTransparency( int alpha ) {
        this.alpha = alpha;
    }

    public void paint( Graphics2D g2 ) {
        if( isVisible() ) {
            super.saveGraphicsState( g2 );
            super.updateGraphicsState( g2 );
            g2.setColor( new Color( r, g, b, alpha ) );
            g2.setTransform( new AffineTransform() );
            g2.fillRect( 0, 0, panel.getWidth(), panel.getHeight() );
            super.restoreGraphicsState();
        }
    }

    public void clockTicked( ClockEvent event ) {
        r = ( r - 1 + 255 ) % 255;
        g = ( g - 2 + 255 ) % 255;
        b = ( b - 3 + 255 ) % 255;
    }

    public void clockStarted( ClockEvent clockEvent ) {
    }

    public void clockPaused( ClockEvent clockEvent ) {
    }

    public void simulationTimeChanged( ClockEvent clockEvent ) {
    }

    public void simulationTimeReset( ClockEvent clockEvent ) {
    }

    public void setActive( boolean active ) {

        if( this.active == active ) {
            return;
        }
        this.active = active;
        if( active ) {
            clock.addClockListener( this );
        }
        else {
            clock.removeClockListener( this );
        }

    }

    protected Rectangle determineBounds() {
        return new Rectangle( 0, 0, panel.getWidth(), panel.getHeight() );
    }

    public boolean isActive() {
        return active;
    }

    /**
     * Make it so that pressing SPACE while the apparatus panel has focus will enable a RepaintDebugGraphic.
     *
     * @param apparatusPanel
     * @param clock
     */
    public static void enable( final ApparatusPanel apparatusPanel, IClock clock ) {
        final RepaintDebugGraphic debugGraphic = new RepaintDebugGraphic( apparatusPanel, clock );
        apparatusPanel.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                apparatusPanel.requestFocus();
            }
        } );
        debugGraphic.setVisible( false );
        apparatusPanel.addKeyListener( new KeyListener() {
            public void keyPressed( KeyEvent e ) {
                if( e.getKeyCode() == KeyEvent.VK_SPACE ) {
                    boolean active = !debugGraphic.isActive();

                    debugGraphic.setActive( active );
                    debugGraphic.setVisible( active );
                    if( active ) {
                        apparatusPanel.addGraphic( debugGraphic, Double.POSITIVE_INFINITY );
                    }
                    else {
                        apparatusPanel.removeGraphic( debugGraphic );
                    }
                    apparatusPanel.paintImmediately( 0, 0, apparatusPanel.getWidth(), apparatusPanel.getHeight() );
                }
            }

            public void keyReleased( KeyEvent e ) {
            }

            public void keyTyped( KeyEvent e ) {
            }
        } );
        debugGraphic.setActive( false );
        debugGraphic.setVisible( false );
    }
}
