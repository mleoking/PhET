// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Class that depicts a slider that controls a parameter of type Integer in a
 * logarithmic (as opposed to linear) fashion.
 *
 * @author John Blanco
 */
class LogarithmicIntegerParameterSliderNode extends PNode {

    private static final Font TITLE_FONT = new PhetFont( 14 );
    private static final double SLIDER_TRACK_LENGTH = 100;
    private static final double SLIDER_TRACK_THICKNESS = 4;
    private static final Font LABEL_FONT = new PhetFont( 12 );

    LogarithmicIntegerParameterSliderNode( IUserComponent userComponent, final double min, final double max, final SettableProperty<Integer> settableProperty, String htmlLabelText ) {

        // Parameter checking.
        if ( min <= 0 ) {
            throw new IllegalArgumentException( "Can't have a value of 0 or less for a logarithmic slider." );
        }
        else if ( min > max ) {
            throw new IllegalArgumentException( "min value must be smaller than max value." );
        }

        // Create the label.
        PNode labelNode = new HTMLNode( htmlLabelText ) {{
            setFont( TITLE_FONT );
        }};

        double sliderMin = Math.log10( min );
        double sliderMax = Math.log10( max );

        // Property that is controlled by slider and then converted to an
        // exponential value.
        final Property<Double> passThroughProperty = new Property<Double>( Math.log10( settableProperty.get() ) );

        // Create the slider node.
        HSliderNode sliderNode = new HSliderNode( userComponent, sliderMin, sliderMax, SLIDER_TRACK_THICKNESS, SLIDER_TRACK_LENGTH, passThroughProperty, new BooleanProperty( true ) ) {{
            setTrackFillPaint( Color.black );
        }};

        // Conversion to exponential.
        passThroughProperty.addObserver( new VoidFunction1<Double>() {
            public void apply( Double sliderValue ) {
                settableProperty.set( (int) Math.round( Math.pow( 10, sliderValue ) ) );
            }
        } );

        // Hook up the data flow in the other direction, so that if the
        // controlled value changes (which may occur, for example, when the
        // property is reset) this changes too.
        settableProperty.addObserver( new VoidFunction1<Integer>() {
            public void apply( Integer value ) {
                passThroughProperty.set( Math.log10( value ) );
            }
        } );

        // Add the labels to the slider node.
        // TODO: i18n
        sliderNode.addLabel( sliderMin, new PText( "Low" ) {{
            setFont( LABEL_FONT );
        }} );
        sliderNode.addLabel( sliderMax, new PText( "High" ) {{
            setFont( LABEL_FONT );
        }} );

        // Add the label and slider to a vertical box.
        addChild( new VBox( 0, labelNode, sliderNode ) );
    }
}
