/** Sam Reid*/
package edu.colorado.phet.movingman;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.movingman.common.GraphicsState;

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
    private MovingManModule module;
    private ApparatusPanel panel;
    private boolean active = false;
    private AbstractClock clock;

    public RepaintDebugGraphic( MovingManModule module, ApparatusPanel panel, AbstractClock clock ) {
        this.module = module;
        this.panel = panel;
        this.clock = clock;
        setActive( true );
    }

    GraphicsState state = new GraphicsState();

    public void paint( Graphics2D gr ) {
        state.saveState( gr );
        gr.setColor( new Color( r, g, b ) );
        gr.fillRect( 0, 0, panel.getWidth(), panel.getHeight() );
        state.restoreState( gr );
    }

    public void clockTicked( AbstractClock c, double dt ) {
        r = ( r - 1 + 255 ) % 255;
        g = ( g - 2 + 255 ) % 255;
        b = ( b - 3 + 255 ) % 255;
        module.repaintBackground();
    }

    public void setActive( boolean active ) {
        if( this.active == active ) {
            return;
        }
        this.active = active;
        if( active ) {
            clock.addClockTickListener( this );
            panel.addGraphic( this, -100 );
        }
        else {
            clock.removeClockTickListener( this );
            panel.removeGraphic( this );
        }
    }
}
