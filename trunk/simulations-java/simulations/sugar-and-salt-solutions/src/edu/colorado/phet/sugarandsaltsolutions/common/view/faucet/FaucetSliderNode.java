// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view.faucet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Slider control shown at the top of the faucet to control the flow rate.  Positioned within the image and scaled up/down with the image to ensure good correspondence
 *
 * @author Sam Reid
 */
public class FaucetSliderNode extends PNode {
    public FaucetSliderNode( final ObservableProperty<Boolean> allowed, final Property<Double> flowRate ) {

        //Create the slider
        final PSwing sliderNode = new PSwing( new JSlider( 0, 100 ) {{
            setBackground( new Color( 0, 0, 0, 0 ) );

            //Make the slider thumb wider on Windows 7
            setPaintTicks( true );

            //Fix the size so it will fit into the specified image dimensions
            setPreferredSize( new Dimension( 95, getPreferredSize().height ) );

            //Wire up 2-way communication with the Property
            addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    //Only change the flow rate if the beaker can accommodate
                    if ( allowed.get() ) {
                        flowRate.set( getValue() / 100.0 );
                    }
                }
            } );
            flowRate.addObserver( new VoidFunction1<Double>() {
                public void apply( Double value ) {
                    setValue( (int) ( value * 100 ) );
                }
            } );

            //Set the flow back to zero when the user lets go, the user has to hold the slider to keep the faucet on
            addMouseListener( new MouseAdapter() {
                @Override public void mouseReleased( MouseEvent e ) {

                    //Turn off the flow level
                    flowRate.set( 0.0 );

                    //To make sure the slider goes back to zero, it is essential to set the value to something other than zero first
                    //Just calling setValue(0) here or waiting for the callback from the model doesn't work if the user was dragging the knob
                    setValue( 1 );
                    setValue( 0 );
                }
            } );
        }} ) {{

            //Mac sliders render lower than windows slider, so have to compensate
            translate( 0, -2 + ( PhetUtilities.isMacintosh() ? -8 : 0 ) );
        }};

        addChild( sliderNode );
    }
}