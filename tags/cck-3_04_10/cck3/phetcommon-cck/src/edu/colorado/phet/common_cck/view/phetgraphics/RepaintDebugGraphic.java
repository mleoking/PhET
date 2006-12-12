/** Sam Reid*/
package edu.colorado.phet.common_cck.view.phetgraphics;

import edu.colorado.phet.common_cck.model.clock.AbstractClock;
import edu.colorado.phet.common_cck.model.clock.ClockTickListener;
import edu.colorado.phet.common_cck.view.ApparatusPanel;
import edu.colorado.phet.common_cck.view.graphics.Graphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 1, 2004
 * Time: 3:33:37 AM
 * Copyright (c) Jul 1, 2004 by Sam Reid
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
