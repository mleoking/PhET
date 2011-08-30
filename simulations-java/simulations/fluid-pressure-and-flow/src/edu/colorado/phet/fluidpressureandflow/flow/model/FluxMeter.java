// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.CompositeDoubleProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * Model for the flux meter which the user can use to measure the flux at different points along the pipe.
 *
 * @author Sam Reid
 */
public class FluxMeter {
    public final Pipe pipe;
    public final Property<Boolean> visible = new Property<Boolean>( false );
    public final Property<Double> x = new Property<Double>( 0.0 );
    public final ObservableProperty<Double> area;
    public final ObservableProperty<Double> rate;

    public FluxMeter( final Pipe pipe ) {
        this.pipe = pipe;

        //Compute the area as the pi * r * r of the pipe, and make sure it updates when the user drags the cross section or deforms the pipe
        area = new CompositeDoubleProperty( new Function0<Double>() {
            public Double apply() {
                return pipe.getCrossSectionalArea( x.get() );
            }
        }, x );
        pipe.addShapeChangeListener( new SimpleObserver() {
            public void update() {
                area.notifyIfChanged();
            }
        } );

        //flux = rate / area, so rate = flux * area
        rate = new CompositeDoubleProperty( new Function0<Double>() {
            public Double apply() {
                return pipe.flux.get() * area.get();
            }
        }, pipe.flux, area );
    }

    public ImmutableVector2D getTop() {
        return pipe.getPoint( x.get(), 1 );
    }

    public ImmutableVector2D getBottom() {
        return pipe.getPoint( x.get(), 0 );
    }
}