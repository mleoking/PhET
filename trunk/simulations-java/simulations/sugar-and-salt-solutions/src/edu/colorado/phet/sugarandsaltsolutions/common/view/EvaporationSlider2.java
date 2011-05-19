// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas.CONTROL_FONT;

/**
 * @author Sam Reid
 */
public class EvaporationSlider2 extends ControlPanelNode {
    public EvaporationSlider2( final SettableProperty<Integer> evaporationRate ) {
        super( new HBox() {{
            //Add a label
            addChild( new PText( "Evaporation" ) {{setFont( CONTROL_FONT );}} );

            //Add the slider
            addChild( new PSwing( new PropertySlider( 0, 100, evaporationRate ) {{
                //Showing ticks makes the knob larger and pointy (even though there are no ticks
                setPaintTicks( true );

                //Show none and lots labels at the extrema
                setPaintLabels( true );
                setLabelTable( new Hashtable() {{
                    put( 0, new JLabel( "none" ) );
                    put( 100, new JLabel( "lots" ) );
                }} );
                addMouseListener( new MouseAdapter() {
                    @Override public void mouseReleased( MouseEvent e ) {
                        evaporationRate.set( 0 );

                        //To make sure the slider goes back to zero, it is essential to set the value to something other than zero first
                        //Just calling setValue(0) here or waiting for the callback from the model doesn't work if the user was dragging the knob
                        setValue( 1 );
                        setValue( 0 );
                    }
                } );
            }} ) );
        }} );
    }
}
