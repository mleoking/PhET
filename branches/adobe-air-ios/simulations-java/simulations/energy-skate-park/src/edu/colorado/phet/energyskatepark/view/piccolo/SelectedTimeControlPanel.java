// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.piccolo;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.energyskatepark.AbstractEnergySkateParkModule;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class SelectedTimeControlPanel extends RichPNode {
    public SelectedTimeControlPanel( AbstractEnergySkateParkModule module, ConstantDtClock timeModelClock ) {
        if ( module.useTimeSlider ) {
            addChild( new PSwing( new ESPSimSpeedSlider( module, timeModelClock ) ) );
        }
        else {
            addChild( new EnergySkateParkTimeControlPanel( module, timeModelClock ) );
        }
    }
}
