// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.model;

import edu.colorado.phet.beerslawlab.common.model.Movable;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Model of the shaker, contains solute in solid form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Shaker extends Movable {

    public final double orientation; // rotation angle, in radians
    public final Property<Solute> solute;
    public final Property<Boolean> visible;
    public final Property<Boolean> empty;
    private final double maxDispensingRate; // mol/sec
    private final Property<Double> dispensingRate; // mol/sec
    private Vector2D previousLocation;

    public Shaker( Vector2D location, double orientation, PBounds dragBounds, Property<Solute> solute, double maxDispensingRate ) {
        super( location, dragBounds );
        assert ( dragBounds.contains( location.toPoint2D() ) );

        this.orientation = orientation;
        this.solute = solute;
        this.visible = new Property<Boolean>( true );
        this.empty = new Property<Boolean>( false );
        this.maxDispensingRate = maxDispensingRate;
        this.dispensingRate = new Property<Double>( 0d );
        this.previousLocation = location;

        // set the dispensing rate to zero when the shaker becomes empty or invisible
        RichSimpleObserver rateObserver = new RichSimpleObserver() {
            public void update() {
                if ( empty.get() || !visible.get() ) {
                    dispensingRate.set( 0d );
                }
            }
        };
        rateObserver.observe( empty, visible );
    }

    public double getDispensingRate() {
        return dispensingRate.get();
    }

    public void addDispensingRateObserver( SimpleObserver observer ) {
        dispensingRate.addObserver( observer );
    }

    /*
     * This is called whenever the simulation clock ticks.
     * Sets the dispensing rate if the shaker is moving.
     */
    public void stepInTime() {
        if ( visible.get() && !empty.get() ) {
            if ( previousLocation.equals( location.get() ) ) {
                this.dispensingRate.set( 0d ); // shaker is not moving, don't dispense anything
            }
            else {
                this.dispensingRate.set( maxDispensingRate ); // this seems to work fine
            }
        }
        previousLocation = new Vector2D( getX(), getY() );
    }

    @Override public void reset() {
        super.reset();
        dispensingRate.reset();
        previousLocation = new Vector2D( getX(), getY() ); // to prevent shaker from dispensing solute when its location is reset
    }
}
