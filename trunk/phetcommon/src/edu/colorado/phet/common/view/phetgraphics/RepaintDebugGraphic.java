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

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.view.ApparatusPanel;

import java.awt.*;

/**
 * RepaintDebugGraphic
 *
 * @author ?
 * @version $Revision$
 */
public class RepaintDebugGraphic extends PhetGraphic implements ClockTickListener {
    private int r = 255;
    private int g = 255;
    private int b = 255;
    private int alpha = 255;
    private ApparatusPanel panel;
    private AbstractClock clock;
    private boolean active = false;

    public RepaintDebugGraphic( ApparatusPanel panel, AbstractClock clock, int transparency ) {
        super( panel );
        this.panel = panel;
        this.clock = clock;
        setActive( true );
        setIgnoreMouse( true );
        setTransparency( transparency );
    }

    public RepaintDebugGraphic( ApparatusPanel panel, AbstractClock clock ) {
        this( panel, clock, 128 );
    }

    public void setTransparency( int alpha ) {
        this.alpha = alpha;
    }

    public void paint( Graphics2D gr ) {
        if( isVisible() ) {
            gr.setColor( new Color( r, g, b, alpha ) );
            gr.fillRect( 0, 0, panel.getWidth(), panel.getHeight() );
        }
    }

    public void clockTicked( ClockTickEvent event ) {
        r = ( r - 1 + 255 ) % 255;
        g = ( g - 2 + 255 ) % 255;
        b = ( b - 3 + 255 ) % 255;
    }

    public void setActive( boolean active ) {

        if( this.active == active ) {
            return;
        }
        this.active = active;
        if( active ) {
            clock.addClockTickListener( this );
        }
        else {
            clock.removeClockTickListener( this );
        }

    }

    protected Rectangle determineBounds() {
        return new Rectangle( 0, 0, panel.getWidth(), panel.getHeight() );
    }

    public boolean isActive() {
        return active;
    }
}
