/* Copyright 2004, Sam Reid */
package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.energyskatepark.view.BodyGraphic;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * User: Sam Reid
 * Date: Oct 31, 2005
 * Time: 12:45:49 AM
 * Copyright (c) Oct 31, 2005 by Sam Reid
 */

public class AutoPan {
    private EnergySkateParkModule module;

    public AutoPan( final EC3Canvas energyCanvas, EnergySkateParkModule module ) {
        this.module = module;
        getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
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

    private IClock getClock() {
        return module.getClock();
    }
}
