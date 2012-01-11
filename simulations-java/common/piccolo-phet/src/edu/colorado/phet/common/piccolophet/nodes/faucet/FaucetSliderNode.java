// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.faucet;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponents;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.slider.simsharing.SimSharingHSliderNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Slider control shown at the top of the faucet to control the flow.
 * When the slider is disabled, the flow is set to zero.
 *
 * @author Sam Reid
 */
public class FaucetSliderNode extends SimSharingHSliderNode {

    /**
     * Creates a slider control to be shown on the faucet to control the flow.
     *
     * @param enabled                property to indicate if the slider is enabled
     * @param flowRatePercentage     the percentage of max flow rate, between 0 and 1
     * @param snapToZeroWhenReleased does the knob snap back to zero when the user releases it?
     */
    public FaucetSliderNode( final ObservableProperty<Boolean> enabled, final Property<Double> flowRatePercentage, final boolean snapToZeroWhenReleased ) {
        super( UserComponents.faucetSlider, 0, 1, flowRatePercentage, enabled );

        // Sets the flow to zero.
        final VoidFunction0 snapToZero = new VoidFunction0() {
            public void apply() {
                flowRatePercentage.set( 0.0 );
            }
        };

        // Sets the flow to zero when the user releases the slider thumb.
        if ( snapToZeroWhenReleased ) {
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mouseReleased( PInputEvent event ) {
                    snapToZero.apply();
                }
            } );
        }

        // When the faucet is disabled, snap the slider back to zero.
        enabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean enabled ) {
                if ( !enabled ) {
                    snapToZero.apply();
                }
            }
        } );
    }
}