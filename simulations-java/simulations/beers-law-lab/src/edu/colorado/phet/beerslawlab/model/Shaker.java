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

    //TODO might be better if these points were in the coordinate system of the image file
    // Locations of the shaker holes, relative to the shaker's local origin, and specific to the shaker image file.
    private static final ImmutableVector2D[] RELATIVE_HOLE_LOCATIONS = {
            new ImmutableVector2D( 0, 0 ),
            new ImmutableVector2D( -10, -16 ),
            new ImmutableVector2D( -16, -10 ),
            new ImmutableVector2D( -8, 5 ),
            new ImmutableVector2D( 8, -5 ),
            new ImmutableVector2D( 10, 17 ),
            new ImmutableVector2D( 21, 15 ),
    };

    public final Property<Solute> solute;
    public final Property<Boolean> visible;
    public final Property<Boolean> empty;
    private final double maxDispensingRate;
    private final Property<Double> dispensingRate; // mol/sec

    public Shaker( ImmutableVector2D location, PBounds dragBounds, Property<Solute> solute, double maxDispensingRate ) {
        super( location, dragBounds );
        assert ( dragBounds.contains( location.toPoint2D() ) );
        this.solute = solute;
        this.visible = new Property<Boolean>( true );
        this.empty = new Property<Boolean>( false );
        this.maxDispensingRate = maxDispensingRate;
        this.dispensingRate = new Property<Double>( 0d );

        // set the dispensing rate to zero when the shaker becomes empty
        this.empty.addObserver( new SimpleObserver() {
            public void update() {
                if ( !Shaker.this.empty.get() ) {
                    dispensingRate.set( 0d );
                }
            }
        } );
    }

    public double getMaxDispensingRate() {
        return maxDispensingRate;
    }

    public double getDispensingRate() {
        return dispensingRate.get();
    }

    public void setDispensingRate( double dispensingRate ) {
        if ( !empty.get() ) {
            this.dispensingRate.set( dispensingRate );
        }
    }

    public void addDispensingRateObserver( SimpleObserver observer ) {
        dispensingRate.addObserver( observer );
    }

    public ImmutableVector2D[] getRelativeHoleLocations() {
        return RELATIVE_HOLE_LOCATIONS;
    }
}
