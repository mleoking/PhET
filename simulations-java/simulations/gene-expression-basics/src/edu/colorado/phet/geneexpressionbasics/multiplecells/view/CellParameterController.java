// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
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
    private static final Font LABEL_FONT = new PhetFont( 14 );

    public CellParameterController( String labelText, Color color ) {
        PNode content = new VBox(
                10,
                new PText( labelText ) {{
                    setFont( LABEL_FONT );
                }},
                new ParameterSliderNode( 0, 100, new Property<Double>( 0.0 ) )
        );
        addChild( new ControlPanelNode( content, color ) );
    }

    private static class ParameterSliderNode extends PNode {
        private static final double SLIDER_TRACK_WIDTH = 100;
        private static final double SLIDER_TRACK_HEIGHT = 4;
        private static final Font SLIDER_LABEL_FONT = new PhetFont( 12 );

        private ParameterSliderNode( double min, double max, SettableProperty<Double> settableProperty ) {
            HSliderNode sliderNode = new HSliderNode( min, max, SLIDER_TRACK_WIDTH, SLIDER_TRACK_HEIGHT, settableProperty, new BooleanProperty( true ) ) {
                @Override protected Paint getTrackFillPaint( Rectangle2D trackRect ) {
                    return Color.BLACK;
                }
            };
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
