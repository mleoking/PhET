// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Class that wraps an HSliderNode in such a way that it can be used to control
 * an integer instead of a double.
 * <p/>
 * Note: This is NOT a full adaptation - only a limited version based on the
 * needs of this sim.  It wouldn't be hard to generalize, but it's not there
 * yet.
 *
 * @author John Blanco
 */
class IntegerHSliderNode extends PNode {

    private final HSliderNode sliderNode;

    /**
     * Constructor.
     *
     * @param userComponent
     * @param min
     * @param max
     * @param trackThickness
     * @param trackLength
     * @param settableProperty
     */
    IntegerHSliderNode( IUserComponent userComponent, int min, int max, double trackThickness, double trackLength, final SettableProperty<Integer> settableProperty ) {

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
        sliderNode = new HSliderNode( userComponent, min, max, trackThickness, trackLength, doubleProperty, new BooleanProperty( true ) ) {{
            setTrackFillPaint( Color.black );
        }};

        addChild( sliderNode );
    }

    public void addLabel( double value, PNode label ) {
        sliderNode.addLabel( value, label );
    }
}
