// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.faucet;

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
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Slider control shown at the top of the faucet to control the flow rate.  Positioned within the image and scaled up/down with the image to ensure good correspondence
 *
 * @author Sam Reid
 */
public class FaucetSliderNode extends PNode {
    /**
     * Creates a slider control to be shown on the faucet to control the flow rate
     *
     * @param allowed                    flag to indicate if more water can be added with this control, used to zero the control
     * @param flowRate                   the rate of flow, between 0 and 1
     * @param userHasToHoldTheSliderKnob flag to indicate whether the user has to hold down the knob to maintain a flow rate, and if the knob will snap back to zero if the user lets go
     */
    public FaucetSliderNode( final ObservableProperty<Boolean> allowed, final Property<Double> flowRate, final boolean userHasToHoldTheSliderKnob ) {

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

            //The two styles for user input are
            // a) the user has to hold the slider knob or it will snap back to zero
            // b) the user can put the slider at a value, then let go and it will retain that value (until no more water is allowed)

            final VoidFunction0 snapToZero = new VoidFunction0() {
                public void apply() {
                    //Turn off the flow level
                    flowRate.set( 0.0 );

                    //To make sure the slider goes back to zero, it is essential to set the value to something other than zero first
                    //Just calling setValue(0) here or waiting for the callback from the model doesn't work if the user was dragging the knob
                    setValue( 1 );
                    setValue( 0 );
                }
            };

            if ( userHasToHoldTheSliderKnob ) {
                //Set the flow back to zero when the user lets go, the user has to hold the slider to keep the faucet on
                addMouseListener( new MouseAdapter() {
                    @Override public void mouseReleased( MouseEvent e ) {
                        snapToZero.apply();
                    }
                } );
            }
            else {
                //When no more water is allowed, snap the slider back to zero
                final VoidFunction1<Boolean> moveSliderToZero = new VoidFunction1<Boolean>() {
                    public void apply( Boolean allowed ) {
                        if ( !allowed ) {
                            snapToZero.apply();
                        }
                    }
                };
                allowed.addObserver( moveSliderToZero );

                //If no more fluid is allowed when the user lets go of the slider, snap it back to zero
                addMouseListener( new MouseAdapter() {
                    @Override public void mouseReleased( MouseEvent e ) {
                        moveSliderToZero.apply( allowed.get() );
                    }
                } );
            }
        }} ) {{

            //Mac sliders render lower than windows slider, so have to compensate
            translate( 0, -2 + ( PhetUtilities.isMacintosh() ? -8 : 0 ) );
        }};

        addChild( sliderNode );
    }
}