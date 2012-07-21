// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.model;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
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

    //Pipe that the flux meter attaches to and measures
    public final Pipe pipe;

    //Flag that indicates whether the property is visible
    public final Property<Boolean> visible = new Property<Boolean>( false );

    //Distance along the horizontal length of the pipe, zero is at the left side
    public final Property<Double> x = new Property<Double>( 0.0 );

    //Computed values for area and flux
    public final ObservableProperty<Double> area;
    public final ObservableProperty<Double> flux;

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

        //Assume incompressible fluid (like water), so the flow rate must remain constant throughout the pipe
        //flux = rate / area, so rate = flux * area
        flux = new CompositeDoubleProperty( new Function0<Double>() {
            public Double apply() {
                return pipe.flowRate.get() / area.get();
            }
        }, pipe.flowRate, area );
    }

    public Vector2D getTop() {
        return pipe.getPoint( x.get(), 1 );
    }

    public Vector2D getBottom() {
        return pipe.getPoint( x.get(), 0 );
    }

    public void reset() {
        visible.reset();
        x.reset();
    }
}