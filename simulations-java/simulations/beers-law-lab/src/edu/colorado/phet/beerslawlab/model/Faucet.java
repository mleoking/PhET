// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.model;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Faucet model, used for input and output faucets.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Faucet implements Resettable {

    public final Property<Double> flowRate;
    public final Property<Boolean> enabled;

    public Faucet( final double maxFlowRate ) {
        assert ( maxFlowRate > 0 );
        this.flowRate = new Property<Double>( 0d );
        this.enabled = new Property<Boolean>( true );
    }

    public void reset() {
        flowRate.reset();
        enabled.reset();
    }
}
