// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.umd.cs.piccolo.PNode;

/**
 * Class that defines a Piccolo slider node that controls an integer parameter.
 *
 * @author John Blanco
 */
class IntegerParameterSliderNode extends PNode {

    IntegerParameterSliderNode( int min, int max, final SettableProperty<Integer> settableProperty, String htmlLabelText ) {

        // Create a property of type double and hook it to the integer
        // property.  This makes it so that when the double property
        // changes in such a way that it yields a new integer value, the
        // integer property is set.
        final Property<Double> doubleProperty = new Property<Double>( (double) settableProperty.get() );
        doubleProperty.addObserver( new VoidFunction1<Double>() {
            public void apply( Double value ) {
                settableProperty.set( (int) Math.round( value ) );
            }
        } );

        // Hook up the data flow in the other direction, so that if the
        // integer value changes (which may occur, for example, when the
        // property is reset).  Sets the double property.
        settableProperty.addObserver( new VoidFunction1<Integer>() {
            public void apply( Integer integer ) {
                doubleProperty.set( (double) integer );
            }
        } );

        // Create the slider node.
        addChild( new DoubleParameterSliderNode( min, max, doubleProperty, htmlLabelText ) );
    }
}
