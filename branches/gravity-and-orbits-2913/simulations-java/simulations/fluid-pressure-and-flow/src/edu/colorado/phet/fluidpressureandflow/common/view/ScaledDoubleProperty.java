// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * This provides a 2-way mapping between properties, but where one of the values is scaled by a specified factor.
 * This is used to make the fluid density control work flexibly with different units.
 *
 * @author Sam Reid
 */
public class ScaledDoubleProperty extends Property<Double> {
    public ScaledDoubleProperty( final Property<Double> property, final double scale ) {
        super( property.get() * scale );
        property.addObserver( new VoidFunction1<Double>() {
            public void apply( Double value ) {
                set( value * scale );
            }
        } );
        addObserver( new VoidFunction1<Double>() {
            public void apply( Double value ) {
                property.set( value / scale );
            }
        } );
    }
}
