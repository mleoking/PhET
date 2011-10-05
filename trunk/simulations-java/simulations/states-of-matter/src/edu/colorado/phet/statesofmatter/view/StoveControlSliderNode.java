// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.statesofmatter.view;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.statesofmatter.StatesOfMatterStrings.*;
import static java.awt.Color.white;

/**
 * This class is the slider that is used to control the StoveNode, causing it
 * to add heat or cooling to the simulated system.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class StoveControlSliderNode extends HSliderNode {

    private static final Color lightOrange = new Color( 255, 233, 80 );
    private static final Color lightBlue = new Color( 106, 255, 239 );

    public StoveControlSliderNode( final SettableProperty<Double> value ) {
        super( -1, 1, value, new BooleanProperty( true ) );

        //Show labels for add, zero and remove
        addLabel( +1, new PhetPText( STOVE_CONTROL_PANEL_ADD_LABEL, new PhetFont( 16, true ), lightOrange ) );
        addLabel( 0.0, new PhetPText( STOVE_CONTROL_PANEL_ZERO_LABEL, new PhetFont( 16, true ), white ) );
        addLabel( -1, new PhetPText( STOVE_CONTROL_PANEL_REMOVE_LABEL, new PhetFont( 16, true ), lightBlue ) );

        //Make it a bit smaller--this changes the layout
        scale( 0.8 );

        // Return to 0 when the user releases the slider.
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseReleased( PInputEvent event ) {
                value.set( 0.0 );
            }
        } );
    }

    //Show a gradient in the track that goes from orange to light blue to indicate the heat/coolness setting
    public Paint getTrackFillPaint( double trackWidth, double trackHeight ) {
        return new GradientPaint( 0, 0, lightOrange, (float) 0, (float) trackHeight, lightBlue, false );
    }

    public Paint getTrackStrokePaint( double trackWidth, double trackHeight ) {
        return Color.lightGray;
    }
}
