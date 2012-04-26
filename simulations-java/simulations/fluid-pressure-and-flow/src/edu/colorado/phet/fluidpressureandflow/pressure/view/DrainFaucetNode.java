// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponents;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.nodes.faucet.FaucetNode;
import edu.colorado.phet.common.piccolophet.nodes.faucet.FaucetSliderNode;
import edu.colorado.phet.fluidpressureandflow.FPAFSimSharing;
import edu.colorado.phet.fluidpressureandflow.pressure.model.IPool;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Images.DRAIN_FAUCET_ATTACHED;

/**
 * Shows the drain faucet below the scene 1 & 2 pools.
 *
 * @author Sam Reid
 */
public class DrainFaucetNode extends PNode {
    public DrainFaucetNode( final Property<IPool> selectedPool, final FaucetPool pool, final double offsetX, final double VIEW_OFFSET_Y, ModelViewTransform transform, ObservableProperty<Double> liquidDensity ) {
        selectedPool.valueEquals( pool ).addObserver( new VoidFunction1<Boolean>() {
            public void apply( final Boolean visible ) {
                setVisible( visible );
            }
        } );

        final PImage drainFaucetImage = new PImage( BufferedImageUtils.multiScaleToHeight( DRAIN_FAUCET_ATTACHED, (int) ( DRAIN_FAUCET_ATTACHED.getHeight() * 1.2 ) ) ) {{

            //Center the faucet over the left opening, values sampled from a drag listener
            setOffset( new Point2D.Double( offsetX, 644.3426883308715 - 1 - VIEW_OFFSET_Y ) );

            FaucetSliderNode sliderNode = new FaucetSliderNode( UserComponentChain.chain( FPAFSimSharing.UserComponents.drainFaucet, UserComponents.slider ), pool.getDrainFaucetEnabled(), 1, pool.getDrainFlowRate(), true ) {{
                setOffset( 4, 8 + 5 - 1 ); //TODO #3199, change offsets when the faucet images are revised, make these constants
                scale( FaucetNode.HANDLE_SIZE.getWidth() / getFullBounds().getWidth() * 1.2 ); //scale to fit into the handle portion of the faucet image
            }};
            addChild( sliderNode );
        }};

        //Show the water coming out of the faucet
        addChild( new OutputFlowingWaterNode( pool, pool.getDrainFlowRate(), transform, liquidDensity, pool.getDrainFaucetEnabled() ) );
        addChild( drainFaucetImage );
    }
}