// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Faucet model, used for input and output faucets.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Faucet implements Resettable {

    public final ImmutableVector2D location; // center of output pipe
    public final double inputPipeLength;
    public final double maxFlowRate; // L/sec
    public final Property<Double> flowRate; // L/sec
    public final Property<Boolean> enabled;

    public Faucet( ImmutableVector2D location, double inputPipeLength, double maxFlowRate ) {
        assert ( maxFlowRate > 0 );
        this.location = location;
        this.inputPipeLength = inputPipeLength;
        this.maxFlowRate = maxFlowRate;
        this.flowRate = new Property<Double>( 0d );
        this.enabled = new Property<Boolean>( true );
    }

    public void reset() {
        flowRate.reset();
        enabled.reset();
    }
}
