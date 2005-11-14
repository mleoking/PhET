/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.ec3.view.BodyGraphic;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * User: Sam Reid
 * Date: Oct 31, 2005
 * Time: 12:45:49 AM
 * Copyright (c) Oct 31, 2005 by Sam Reid
 */

public class AutoPan {
    private EC3Module module;

    public AutoPan( final EC3Canvas energyCanvas, EC3Module module ) {
        this.module = module;
        getClock().addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                //set zoom related to speed
                //set center on player.
                EC3RootNode rootNode = energyCanvas.getRootNode();
                if( rootNode.numBodyGraphics() > 0 ) {
                    BodyGraphic bodyGraphic = rootNode.bodyGraphicAt( 0 );
                    PBounds bodyBounds = bodyGraphic.getGlobalFullBounds();
                    rootNode.globalToLocal( bodyBounds );
                    rootNode.translateWorld( -bodyBounds.getX() + energyCanvas.getWidth() / 2, -bodyBounds.getY() + energyCanvas.getHeight() / 2 );
                }
            }
        } );
    }

    private AbstractClock getClock() {
        return module.getClock();
    }
}
