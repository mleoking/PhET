// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.faucet;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

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

        //Wire up 2-way communication with the Property
        final Property<Double> sliderProperty = new Property<Double>( flowRate.get() ) {
            @Override public void set( Double value ) {
                if ( allowed.get() ) {
                    super.set( value );
                }
                else {
                    super.set( 0.0 );
                }
            }
        };
        sliderProperty.addObserver( new VoidFunction1<Double>() {
            public void apply( Double value ) {
                if ( allowed.get() ) {
                    flowRate.set( value );
                }
                else {
                    flowRate.set( 0.0 );
                }
            }
        } );
        flowRate.addObserver( new VoidFunction1<Double>() {
            public void apply( Double value ) {
                sliderProperty.set( value );
            }
        } );
        final SliderNode sliderNode = new SliderNode( 0, sliderProperty, 1, allowed );

        //Fix the size so it will fit into the specified image dimensions
        double scale = 85.0 / sliderNode.getFullBounds().getWidth();
        sliderNode.scale( scale );

        //The two styles for user input are
        // a) the user has to hold the slider knob or it will snap back to zero
        // b) the user can put the slider at a value, then let go and it will retain that value (until no more water is allowed)

        final VoidFunction0 snapToZero = new VoidFunction0() {
            public void apply() {
                //Turn off the flow level
                flowRate.set( 0.0 );
            }
        };

        if ( userHasToHoldTheSliderKnob ) {
            //Set the flow back to zero when the user lets go, the user has to hold the slider to keep the faucet on
            sliderNode.addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mouseReleased( PInputEvent event ) {
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
            sliderNode.addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mouseReleased( PInputEvent event ) {
                    moveSliderToZero.apply( allowed.get() );
                }
            } );
        }
        addChild( sliderNode );
    }
}