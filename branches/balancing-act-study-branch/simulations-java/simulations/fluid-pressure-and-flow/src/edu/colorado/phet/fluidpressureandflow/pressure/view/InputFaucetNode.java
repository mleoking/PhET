// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.fluidpressureandflow.pressure.model.FluidPressureModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Node for input faucet for scene 1-2
 *
 * @author Sam Reid
 */
public class InputFaucetNode extends PNode {
    public InputFaucetNode( final FluidPressureModel model, final FaucetPool pool, final int viewOffsetY, final ModelViewTransform transform, final double x ) {

        model.pool.valueEquals( pool ).addObserver( new VoidFunction1<Boolean>() {
            public void apply( final Boolean visible ) {
                setVisible( visible );
            }
        } );

        final FluidPressureFaucetNode faucetNode = new FluidPressureFaucetNode( pool.getInputFlowRatePercentage(), pool.getInputFaucetEnabled() ) {{

            //Center the faucet over the left opening, values sampled from a drag listener
            setOffset( new Point2D.Double( x, 157.19350073855244 - viewOffsetY ) );
        }};

        //Show the water coming out of the faucet
        addChild( new InputFlowingWaterNode( pool, pool.getInputFlowRatePercentage(), transform, model.liquidDensity, pool.getInputFaucetEnabled() ) );
        addChild( faucetNode );
    }
}