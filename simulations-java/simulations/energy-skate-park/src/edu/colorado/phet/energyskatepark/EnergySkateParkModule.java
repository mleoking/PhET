// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkOptions;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkControlPanel;

/**
 * @author Sam Reid
 */
public class EnergySkateParkModule extends AbstractEnergySkateParkModule {
    private final EnergySkateParkControlPanel energySkateParkControlPanel;

    public EnergySkateParkModule( String name, ConstantDtClock clock, PhetFrame phetFrame, EnergySkateParkOptions options ) {
        super( name, clock, phetFrame, options );

        energySkateParkControlPanel = new EnergySkateParkControlPanel( this );
        setControlPanel( energySkateParkControlPanel );
    }

    @Override public void reset() {
        super.reset();
        energySkateParkControlPanel.reset();
    }
}
