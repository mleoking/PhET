// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.statesofmatter.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Stroke;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.slider.VSliderNode;
import edu.colorado.phet.statesofmatter.StatesOfMatterSimSharing.UserComponents;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * This class is the slider that is used to control the StoveNode, causing it
 * to add heat or cooling to the simulated system.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class StoveControlSliderNode extends VSliderNode {

    private static final Color TOP_SIDE_TRACK_COLOR = new Color( 255, 69, 0 );    // Meant to look warm.
    private static final Color BOTTOM_SIDE_TRACK_COLOR = new Color( 0, 0, 240 );  // Meant to look cold.

    private static final Font LABEL_FONT = new PhetFont( 20, true );

    public StoveControlSliderNode( final SettableProperty<Double> value ) {
        super( UserComponents.stoveSlider, -1, 1, 6, 75, value, new BooleanProperty( true ) );

        // Show labels for add, zero, and remove.
        addLabel( +1, new PhetPText( StatesOfMatterStrings.STOVE_CONTROL_PANEL_HEAT_LABEL, LABEL_FONT ) );
        addLabel( 0.0, new TickMark() );
        addLabel( -1, new PhetPText( StatesOfMatterStrings.STOVE_CONTROL_PANEL_COOL_LABEL, LABEL_FONT ) );

        // Return to 0 when the user releases the slider.
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseReleased( PInputEvent event ) {
                value.set( 0.0 );
            }
        } );


        // Show a gradient in the track that goes from orange to light blue to
        // indicate the heat/coolness setting.
        setTrackFillPaint( new GradientPaint( 0, 0, TOP_SIDE_TRACK_COLOR, 0, (float) trackLength, BOTTOM_SIDE_TRACK_COLOR, false ) );
    }

    // Convenience class for create a tick mark that works for this slider.
    private static class TickMark extends PNode {
        private static final double INDENT = 4;
        private static final double LENGTH = 10;
        private static final float STROKE_WIDTH = 2;
        private static final Stroke STROKE = new BasicStroke( STROKE_WIDTH );

        private TickMark() {
            DoubleGeneralPath path = new DoubleGeneralPath( INDENT, STROKE_WIDTH / 2 ) {{
                lineTo( INDENT + LENGTH, STROKE_WIDTH / 2 );
            }};
            addChild( new PhetPPath( path.getGeneralPath(), STROKE, Color.BLACK ) );
        }
    }
}
