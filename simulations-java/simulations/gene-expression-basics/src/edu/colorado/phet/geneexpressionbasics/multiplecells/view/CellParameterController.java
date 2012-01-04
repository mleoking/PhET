// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * PNode that contains a slider and a textual title and allows the user to
 * control the level of some parameter.
 *
 * @author John Blanco
 */
public class CellParameterController extends PNode {
    private static final Font LABEL_FONT = new PhetFont( 16, true );

    public CellParameterController( String labelHtml, Property<Integer> parameter, IntegerRange range, Color backgroundColor ) {
        // Parameter checking.
        assert parameter.get() >= range.getMin() && parameter.get() <= range.getMax();

        // Create the content for this panel.
        PNode content = new VBox(
                10,
                new HTMLNode( labelHtml ) {{
                    setFont( LABEL_FONT );
                }},
                new ParameterSliderNode( range.getMin(), range.getMax(), parameter )
        );

        // Add the content to a control panel.
        addChild( new ControlPanelNode( content, backgroundColor ) );
    }

    /**
     * Convenience class for a slider node of a certain size and with the
     * needed labels.
     */
    private static class ParameterSliderNode extends PNode {
        private static final double SLIDER_TRACK_WIDTH = 100;
        private static final double SLIDER_TRACK_HEIGHT = 4;
        private static final Font SLIDER_LABEL_FONT = new PhetFont( 12 );

        private ParameterSliderNode( int min, int max, final SettableProperty<Integer> settableProperty ) {

            // Create a property of type double and hook it to the integer
            // property.
            Property<Double> doubleProperty = new Property<Double>( (double) settableProperty.get() );
            doubleProperty.addObserver( new VoidFunction1<Double>() {
                public void apply( Double value ) {
                    settableProperty.set( (int) Math.round( value ) );
                }
            } );

            // Create the slider node.
            HSliderNode sliderNode = new HSliderNode( min, max, SLIDER_TRACK_WIDTH, SLIDER_TRACK_HEIGHT, doubleProperty, new BooleanProperty( true ) ) {
                @Override protected Paint getTrackFillPaint( Rectangle2D trackRect ) {
                    return Color.BLACK;
                }
            };

            // Add the label to the slider node.
            // TODO: i18n
            sliderNode.addLabel( min, new PText( "Low" ) {{
                setFont( SLIDER_LABEL_FONT );
            }} );
            sliderNode.addLabel( max, new PText( "High" ) {{
                setFont( SLIDER_LABEL_FONT );
            }} );
            addChild( sliderNode );
        }
    }
}
