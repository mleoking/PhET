// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.view.piccolo.EnergySkateParkRootNode;
import edu.colorado.phet.energyskatepark.view.piccolo.SkaterNode;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * User: Sam Reid
 * Date: Oct 31, 2005
 * Time: 12:45:49 AM
 */

public class AutoPan {
    private EnergySkateParkModule module;

    public AutoPan( final EnergySkateParkSimulationPanel energyCanvas, EnergySkateParkModule module ) {
        this.module = module;
        getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
                //set zoom related to speed
                //set center on player.
                EnergySkateParkRootNode rootNode = energyCanvas.getRootNode();
                if( rootNode.numBodyGraphics() > 0 ) {
                    SkaterNode skaterNode = rootNode.getSkaterNode( 0 );
                    PBounds bodyBounds = skaterNode.getGlobalFullBounds();
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
