// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Model of the dropper, contains solute in solid form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Shaker extends Movable {

    public final Property<Solute> solute;
    public final Property<Boolean> enabled;
    private final double maxDispensingRate;
    private final Property<Double> dispensingRate; // mol/sec

    public Shaker( ImmutableVector2D location, PBounds dragBounds, Property<Solute> solute, double maxDispensingRate ) {
        super( location, dragBounds );
        assert ( dragBounds.contains( location.toPoint2D() ) );
        this.solute = solute;
        this.enabled = new Property<Boolean>( true );
        this.maxDispensingRate = maxDispensingRate;
        this.dispensingRate = new Property<Double>( 0d );
    }

    public double getMaxDispensingRate() {
        return maxDispensingRate;
    }

    public double getDispensingRate() {
        return dispensingRate.get();
    }

    public void setDispensingRate( double dispensingRate ) {
        if ( enabled.get() ) {
            this.dispensingRate.set( dispensingRate );
        }
    }

    public void addDispensingRateObserver( SimpleObserver observer ) {
        dispensingRate.addObserver( observer );
    }
}
