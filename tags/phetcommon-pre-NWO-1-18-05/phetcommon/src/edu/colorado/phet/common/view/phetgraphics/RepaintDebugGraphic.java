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
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;

/**
 * RepaintDebugGraphic
 *
 * @author Sam Reid
 * @version $Revision$
 */
public class RepaintDebugGraphic implements Graphic, ClockTickListener {
    private int r = 255;
    private int g = 255;
    private int b = 255;
    private ApparatusPanel panel;
    private AbstractClock clock;
    private boolean active = false;

    public RepaintDebugGraphic( ApparatusPanel panel, AbstractClock clock ) {
        this.panel = panel;
        this.clock = clock;
    }

    public void paint( Graphics2D gr ) {
        gr.setColor( new Color( r, g, b ) );
        gr.fillRect( 0, 0, panel.getWidth(), panel.getHeight() );
    }

    public void clockTicked( AbstractClock c, double dt ) {
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
            panel.addGraphic( this, Double.NEGATIVE_INFINITY );
        }
        else {
            clock.removeClockTickListener( this );
            panel.removeGraphic( this );
        }
    }

}
