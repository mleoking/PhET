/** Sam Reid*/
package edu.colorado.phet.common.view.phetgraphics;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.view.ApparatusPanel;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 1, 2004
 * Time: 3:33:37 AM
 * Copyright (c) Jul 1, 2004 by Sam Reid
 */
public class RepaintDebugGraphic extends PhetGraphic implements ClockTickListener {
    private int r = 255;
    private int g = 255;
    private int b = 255;
    private ApparatusPanel panel;
    private AbstractClock clock;
    private boolean active = false;

    public RepaintDebugGraphic( ApparatusPanel panel, AbstractClock clock ) {
        super( panel );
        this.panel = panel;
        this.clock = clock;
        setActive( true );
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

    protected Rectangle determineBounds() {
        return panel.getBounds();//TODO fix this for the correct rectangle.
    }
}
