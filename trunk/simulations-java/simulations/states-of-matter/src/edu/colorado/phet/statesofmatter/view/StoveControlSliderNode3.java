// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.statesofmatter.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.colorado.phet.statesofmatter.StatesOfMatterSimSharing.UserComponents;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.statesofmatter.StatesOfMatterStrings.STOVE_CONTROL_PANEL_COOL_LABEL;
import static edu.colorado.phet.statesofmatter.StatesOfMatterStrings.STOVE_CONTROL_PANEL_HEAT_LABEL;

/**
 * This class is the slider that is used to control the StoveNode, causing it
 * to add heat or cooling to the simulated system.  This slider is
 * horizontally oriented.
 * TODO: This version of the slider was used on a stove with a base at the
 * bottom, but then the team decided that they wanted a stove with a
 * vertical slider on the body of the stove, so this version is obsolete
 * as of Nov 4, 2011.  This should be kept around for a while in case the
 * team changes their collective mind again.  After, say, a year it can and
 * should be deleted.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class StoveControlSliderNode3 extends HSliderNode {

    private static final Color LEFT_SIDE_TRACK_COLOR = new Color( 0, 0, 240 );     // Meant to look cold.
    private static final Color RIGHT_SIDE_TRACK_COLOR = new Color( 255, 69, 0 );  // Meant to look warm.

    private static final Font LABEL_FONT = new PhetFont( 24, true );

    public StoveControlSliderNode3( final SettableProperty<Double> value ) {
        super( UserComponents.stoveSlider, -1, 1, value, new BooleanProperty( true ) );

        // Show labels for add, zero and remove.
        addLabel( +1, new PhetPText( STOVE_CONTROL_PANEL_HEAT_LABEL, LABEL_FONT ) );
//        addLabel( 0.0, new PhetPText( STOVE_CONTROL_PANEL_ZERO_LABEL, LABEL_FONT ) );
        addLabel( -1, new PhetPText( STOVE_CONTROL_PANEL_COOL_LABEL, LABEL_FONT ) );

        // Return to 0 when the user releases the slider.
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseReleased( PInputEvent event ) {
                value.set( 0.0 );
            }
        } );

        // Show a gradient in the track that goes from orange to light blue to
        // indicate the heat/coolness setting.
        setTrackFillPaint( new GradientPaint( 0, 0, LEFT_SIDE_TRACK_COLOR, (float) super.trackLength, 0, RIGHT_SIDE_TRACK_COLOR, false ) );
    }
}