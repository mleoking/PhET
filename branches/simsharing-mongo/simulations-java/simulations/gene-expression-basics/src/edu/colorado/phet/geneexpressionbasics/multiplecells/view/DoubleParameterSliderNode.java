// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author John Blanco
 */ // Class that defines the slider and its labels.
class DoubleParameterSliderNode extends PNode {

    private static final Font TITLE_FONT = new PhetFont( 14 );
    private static final double SLIDER_TRACK_WIDTH = 100;
    private static final double SLIDER_TRACK_HEIGHT = 4;
    private static final Font LABEL_FONT = new PhetFont( 12 );

    DoubleParameterSliderNode( double min, double max, final SettableProperty<Double> settableProperty, String htmlLabelText ) {

        // Create the label.
        PNode labelNode = new HTMLNode( htmlLabelText ) {{
            setFont( TITLE_FONT );
        }};

        // Create the slider node.
        HSliderNode sliderNode = new HSliderNode( min, max, SLIDER_TRACK_WIDTH, SLIDER_TRACK_HEIGHT, settableProperty, new BooleanProperty( true ) ) {
            @Override protected Paint getTrackFillPaint( Rectangle2D trackRect ) {
                return Color.BLACK;
            }
        };

        // Add the labels to the slider node.
        // TODO: i18n
        sliderNode.addLabel( min, new PText( "Low" ) {{
            setFont( LABEL_FONT );
        }} );
        sliderNode.addLabel( max, new PText( "High" ) {{
            setFont( LABEL_FONT );
        }} );

        // Add the label and slider to a vertical box.
        addChild( new VBox( 0, labelNode, sliderNode ) );
    }
}
