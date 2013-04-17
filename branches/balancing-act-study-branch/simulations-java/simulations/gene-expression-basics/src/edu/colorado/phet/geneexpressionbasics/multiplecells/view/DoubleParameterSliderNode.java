// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsResources.Strings.HIGH;
import static edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsResources.Strings.LOW;

/**
 * Class that depicts a slider that controls a parameter of type Double.
 *
 * @author John Blanco
 */
class DoubleParameterSliderNode extends PNode {

    private static final Font TITLE_FONT = new PhetFont( 14 );
    private static final double SLIDER_TRACK_LENGTH = 100;
    private static final double SLIDER_TRACK_THICKNESS = 4;
    private static final Font LABEL_FONT = new PhetFont( 12 );

    /**
     * Constructor that uses default labels for the low and high ends of the
     * slider.
     *
     * @param userComponent
     * @param min
     * @param max
     * @param settableProperty
     * @param htmlLabelText
     */
    DoubleParameterSliderNode( IUserComponent userComponent, double min, double max,
                               final SettableProperty<Double> settableProperty, String htmlLabelText ) {
        this( userComponent, min, max, settableProperty, htmlLabelText, LOW, HIGH );
    }

    /**
     * Main constructor.
     *
     * @param userComponent
     * @param min
     * @param max
     * @param settableProperty
     * @param htmlLabelText
     * @param lowEndLabel
     * @param highEndLabel
     */
    DoubleParameterSliderNode( IUserComponent userComponent, double min, double max,
                               final SettableProperty<Double> settableProperty, String htmlLabelText,
                               String lowEndLabel, String highEndLabel ) {

        // Create the label.
        PNode labelNode = new HTMLNode( htmlLabelText ) {{
            setFont( TITLE_FONT );
        }};

        // Create the slider node.
        HSliderNode sliderNode = new HSliderNode( userComponent, min, max, SLIDER_TRACK_THICKNESS, SLIDER_TRACK_LENGTH, settableProperty, new BooleanProperty( true ) ) {{
            setTrackFillPaint( Color.black );
        }};

        // Add the labels to the slider node.
        sliderNode.addLabel( min, new PhetPText( lowEndLabel, LABEL_FONT ) );
        sliderNode.addLabel( max, new PhetPText( highEndLabel, LABEL_FONT ) );

        // Add the label and slider to a vertical box.
        addChild( new VBox( 0, labelNode, sliderNode ) );
    }
}
