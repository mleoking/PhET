// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.view;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.beerslawlab.common.BLLConstants;
import edu.colorado.phet.beerslawlab.common.model.IFluid;
import edu.colorado.phet.beerslawlab.concentration.model.Faucet;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Fluid coming out of a faucet.
 * Origin is at the top center, to simplify alignment with the center of the faucet's output pipe.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class FaucetFluidNode extends PPath {

    public FaucetFluidNode( final Faucet faucet, final IFluid fluid, final double maxWidth, final double height ) {
        setPickable( false );
        setChildrenPickable( false );

        setStroke( BLLConstants.FLUID_STROKE );
        setStrokePaint( BLLConstants.createFluidStrokeColor( fluid.getFluidColor() ) );

        setOffset( faucet.location.toPoint2D() );

        // match the color of the fluid
        fluid.addFluidColorObserver( new SimpleObserver() {
            public void update() {
                setPaint( fluid.getFluidColor() );
            }
        } );

        // set the width of the shape to match the flow rate
        faucet.flowRate.addObserver( new SimpleObserver() {
            public void update() {
                if ( faucet.flowRate.get() == 0 ) {
                    setPathTo( new Rectangle2D.Double() ); // empty rectangle
                }
                else {
                    double width = maxWidth * faucet.flowRate.get() / faucet.maxFlowRate;
                    setPathTo( new Rectangle2D.Double( -width / 2, 0, width, height ) );
                }
            }
        } );
    }
}
