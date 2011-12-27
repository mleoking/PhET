// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Model of the concentration meter.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationMeter implements Resettable {

    private final Property<Double> value;
    public final ConcentrationMeterBody body;
    public final ConcentrationMeterProbe probe;

    public ConcentrationMeter( ImmutableVector2D bodyLocation, PBounds bodyDragBounds,
                               ImmutableVector2D probeLocation, PBounds probeDragBounds,
                               Solution solution, Beaker beaker ) {
        this.value = new Property<Double>( null );
        this.body = new ConcentrationMeterBody( bodyLocation, bodyDragBounds, solution, beaker );
        this.probe = new ConcentrationMeterProbe( probeLocation, probeDragBounds, solution, beaker, value );
    }

    // Gets the value to be displayed by the meter, null if the meter is not reading a value.
    public Double getValue() {
        return value.get();
    }

    public void addValueObserver( SimpleObserver observer ) {
        value.addObserver( observer );
    }

    public void reset() {
        this.value.reset();
        this.body.reset();
        this.probe.reset();
    }

    // Meter body
    public static class ConcentrationMeterBody extends Movable {
        public ConcentrationMeterBody( ImmutableVector2D location, PBounds dragBounds, Solution solution, Beaker beaker ) {
            super( location, dragBounds );
        }
    }

    // Meter probe
    public static class ConcentrationMeterProbe extends Movable {
        public ConcentrationMeterProbe( ImmutableVector2D location, PBounds dragBounds, final Solution solution, final Beaker beaker, final Property<Double> value ) {
            super( location, dragBounds );

            SimpleObserver observer = new SimpleObserver() {
                public void update() {
                    if ( isInSolution( solution, beaker ) ) {
                        value.set( solution.getConcentration() );
                    }
                    else {
                        value.set( null );
                    }
                }
            };
            this.location.addObserver( observer );
            solution.addConcentrationObserver( observer );
            solution.volume.addObserver( observer );
        }

        private boolean isInSolution( Solution solution, Beaker beaker ) {
            double w = beaker.getWidth();
            double h = beaker.getHeight() * solution.volume.get() / beaker.getVolume();
            double x = beaker.getX() - ( beaker.getWidth() / 2 );
            double y = beaker.getY() - h;
            PBounds bounds = new PBounds( x, y, w, h );
            return bounds.contains( location.get().toPoint2D() );
        }
    }
}
