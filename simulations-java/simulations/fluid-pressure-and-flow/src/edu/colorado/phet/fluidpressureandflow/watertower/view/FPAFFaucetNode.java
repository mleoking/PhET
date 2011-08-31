// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.view;

import java.awt.Insets;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.piccolophet.nodes.faucet.FaucetNode;
import edu.colorado.phet.fluidpressureandflow.watertower.model.FaucetFlowRate;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.phetcommon.model.property.SettableNot.not;
import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.MANUAL;
import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.MATCH_LEAKAGE;

/**
 * Faucet node with customizations for Fluid Pressure and Flow's water tower tab, including:
 * 1. Smaller size to fit nicely in the canvas
 * 2. Radio buttons to switch between automatic and manual flow
 *
 * @author Sam Reid
 */
public class FPAFFaucetNode extends FaucetNode {
    public FPAFFaucetNode( final FaucetFlowRate faucetFlowRate ) {
        super( faucetFlowRate.flow, new Property<Boolean>( true ), 10000 );

        //Manually tuned to fit nicely in the scene
        setScale( 0.7 );
        translate( 203, 5 );

        //Radio buttons to choose between automatic and manual control
        //Add as a child of the faucetImageNode so they will move and scale with it, since they are fitted to the pipe itself
        faucetImageNode.addChild( new PSwing( new VerticalLayoutPanel() {{

            //Bring the radio buttons a bit closer together
            setInsets( new Insets( -6, 0, 0, 0 ) );
            add( new FaucetRadioButton( MANUAL, not( faucetFlowRate.automatic ) ) );
            add( new FaucetRadioButton( MATCH_LEAKAGE, faucetFlowRate.automatic ) );
            setBackground( WaterTowerCanvas.TRANSPARENT );
        }} ) {{

            //Right align with slider's center and move down to the center of the pipe
            setOffset( faucetSliderNode.getFullBounds().getCenterX() - getFullBounds().getWidth(), 30 );
        }} );

        //Faucet slider should be invisible when in "auto" mode
        faucetFlowRate.automatic.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean auto ) {
                faucetSliderNode.setVisible( !auto );
            }
        } );
    }
}
