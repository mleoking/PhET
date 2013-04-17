// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.umd.cs.piccolo.PNode;

/**
 * Class that depicts a slider that controls a parameter of type Integer in a
 * logarithmic (as opposed to linear) fashion.
 *
 * @author John Blanco
 */
class LogarithmicIntegerParameterSliderNode extends PNode {

    LogarithmicIntegerParameterSliderNode( IUserComponent userComponent, final int min, final int max, final SettableProperty<Integer> settableProperty, String htmlLabelText ) {

        // Connect the integer property to a double property.
        final Property<Double> doubleProperty = new DoubleProperty( (double) settableProperty.get() );

        doubleProperty.addObserver( new VoidFunction1<Double>() {
            public void apply( Double newValue ) {
                settableProperty.set( (int) Math.round( newValue ) );
            }
        } );

        settableProperty.addObserver( new VoidFunction1<Integer>() {
            public void apply( Integer newValue ) {
                doubleProperty.set( (double) newValue );
            }
        } );

        addChild( new LogarithmicParameterSliderNode( userComponent, min, max, doubleProperty, htmlLabelText ) );
    }
}
