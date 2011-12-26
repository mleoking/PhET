// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.view;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.beerslawlab.model.Faucet;
import edu.colorado.phet.beerslawlab.model.IFluid;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Fluid coming out of a faucet.
 * Origin is at the top center, to simplify alignment with the center of the faucet's output pipe.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OutputFluidNode extends PPath {

    public OutputFluidNode( final BLLFaucetNode faucetNode, final double height, final IFluid fluid, final Faucet faucet ) {
        setPickable( false );
        setChildrenPickable( false );

        setStroke( null );

        setOffset( globalToLocal( faucetNode.getGlobalOutputCenter() ) );

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
                    setPathTo( new Rectangle2D.Double( 0, 0, 0, 0 ) );
                }
                else {
                    double maxWidth = globalToLocal( faucetNode.getGlobalOutputSize() ).getWidth();
                    double width = maxWidth * faucet.flowRate.get() / faucet.getMaxFlowRate();
                    setPathTo( new Rectangle2D.Double( -width / 2, 0, width, height ) );
                }
            }
        } );
    }
}
