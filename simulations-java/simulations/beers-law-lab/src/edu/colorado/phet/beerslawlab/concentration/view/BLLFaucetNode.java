// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.beerslawlab.concentration.model.Faucet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.piccolophet.nodes.faucet.FaucetNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Faucet with sim-sharing.
 * Reuses the common-code faucet node via composition.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class BLLFaucetNode extends PNode {

    private final FaucetNode faucetNode;

    public BLLFaucetNode( final IUserComponent userComponent, final Faucet faucet ) {
        // use composition, so we can move the origin to the center of the output pipe
        faucetNode = new FaucetNode( userComponent, faucet.maxFlowRate, faucet.flowRate, faucet.enabled, faucet.inputPipeLength, true );
        addChild( faucetNode );
        Point2D originOffset = globalToLocal( faucetNode.getGlobalOutputCenter() );
        faucetNode.setOffset( faucet.location.getX() - originOffset.getX(), faucet.location.getY() - originOffset.getY() );
    }

    public double getFluidWidth() {
        return globalToLocal( faucetNode.getGlobalOutputSize() ).getWidth();
    }
}
