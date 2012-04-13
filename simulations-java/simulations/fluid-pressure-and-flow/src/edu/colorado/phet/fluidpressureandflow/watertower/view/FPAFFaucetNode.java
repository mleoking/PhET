// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.view;

import java.awt.Insets;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.piccolophet.nodes.faucet.FaucetNode;
import edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.UserComponents;
import edu.colorado.phet.fluidpressureandflow.watertower.model.FaucetFlowRate;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.phetcommon.model.property.SettableNot.not;
import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.MANUAL;
import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.MATCH_LEAKAGE;

/**
 * Faucet node with customizations for the Fluid Pressure and Flow water tower tab, including:
 * 1. Smaller size to fit nicely in the canvas
 * 2. Radio buttons to switch between automatic and manual flow
 *
 * @author Sam Reid
 */
public class FPAFFaucetNode extends FaucetNode {
    public FPAFFaucetNode( IUserComponent userComponent, final FaucetFlowRate faucetFlowRate, ObservableProperty<Boolean> allowed ) {
        super( userComponent, faucetFlowRate.flow, allowed, 10000, false );

        //Manually tuned to fit nicely in the scene
        setScale( 0.84 );
        translate( 138, 4 );

        //Radio buttons to choose between automatic and manual control
        PNode radioButtons = new PSwing( new VerticalLayoutPanel() {{
            //Bring the radio buttons a bit closer together
            setInsets( new Insets( -6, 0, 0, 0 ) );
            add( new FaucetRadioButton( UserComponents.faucetManualRadioButton, MANUAL, not( faucetFlowRate.automatic ) ) );
            add( new FaucetRadioButton( UserComponents.faucetMatchLeakageRadioButton, MATCH_LEAKAGE, faucetFlowRate.automatic ) );
            setBackground( WaterTowerCanvas.TRANSPARENT );
        }} );
        addChild( radioButtons );

        // Right-align radio buttons with slider's center, and vertically-align with center of input pipe.
        radioButtons.setOffset( globalToLocal( getGlobalHandleCenter() ).getX() - radioButtons.getFullBounds().getWidth(),
                                globalToLocal( getGlobalInputCenter() ).getY() - ( radioButtons.getFullBoundsReference().getHeight() / 2 ) );

        //TODO Should use FaucetNode.getGlobalInputSize to verify that the height of the buttons is less than the height of the input pipe. If not, scale accordingly.

        // Slider is invisible when in "auto" mode
        faucetFlowRate.automatic.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean auto ) {
                setSliderVisible( !auto );
            }
        } );
    }
}