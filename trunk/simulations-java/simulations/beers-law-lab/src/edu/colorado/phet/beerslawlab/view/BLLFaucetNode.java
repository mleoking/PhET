// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.beerslawlab.model.Faucet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.piccolophet.nodes.faucet.FaucetNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Faucet with sim-sharing.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BLLFaucetNode extends PNode {

    private final FaucetNode faucetNode;

    public BLLFaucetNode( final IUserComponent userComponent, final Faucet faucet ) {
        // use composition, so we can move the origin to the center of the output pipe
        faucetNode = new FaucetNode( userComponent, faucet.getMaxFlowRate(), faucet.flowRate, faucet.enabled, faucet.getInputPipeLength(), true );
        addChild( faucetNode );
        Point2D originOffset = globalToLocal( faucetNode.getGlobalOutputCenter() );
        faucetNode.setOffset( faucet.getLocation().getX() - originOffset.getX(), faucet.getLocation().getY() - originOffset.getY() );
    }

    public double getFluidWidth() {
        return globalToLocal( faucetNode.getGlobalOutputSize() ).getWidth();
    }
}
