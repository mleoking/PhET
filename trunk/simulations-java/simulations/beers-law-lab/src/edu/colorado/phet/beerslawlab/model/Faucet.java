// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.model;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Faucet model, used for input and output faucets.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Faucet implements Resettable {

    public final Property<Double> flowPercentage; // varies from 0-1, required by common-code view node
    public final Property<Boolean> enabled;
    private final Property<Double> flowRate; // derived from flow percentage

    public Faucet( final double maxFlowRate ) {
        assert ( maxFlowRate > 0 );

        this.flowRate = new Property<Double>( 0d );
        this.flowPercentage = new Property<Double>( 0d );
        this.enabled = new Property<Boolean>( true );

        // Convert flow percentage to rate.
        flowPercentage.addObserver( new SimpleObserver() {
            public void update() {
                flowRate.set( flowPercentage.get() * maxFlowRate );
            }
        } );
    }

    public void addFlowRateObserver( SimpleObserver observer ) {
        flowRate.addObserver( observer );
    }

    public double getFlowRate() {
        return flowRate.get();
    }

    public void reset() {
        flowPercentage.reset();
        enabled.reset();
        //flowRate is derived
    }
}
