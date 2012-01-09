// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.view;

import edu.colorado.phet.beerslawlab.BLLSimSharing.Parameters;
import edu.colorado.phet.beerslawlab.model.Faucet;
import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.piccolophet.nodes.faucet.FaucetNode;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragSequenceEventHandler.DragFunction;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Faucet with sim-sharing.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BLLFaucetNode extends FaucetNode {

    public BLLFaucetNode( final UserComponent userComponent, final Faucet faucet ) {
        super( faucet.getMaxFlowRate(), faucet.flowRate, faucet.enabled, faucet.getInputPipeLength(), true );

        setOffset( faucet.getLocation() );

        // sim-sharing
        getDragHandler().setStartEndDragFunction( new DragFunction() {
            public void apply( UserAction action, Parameter xParameter, Parameter yParameter, PInputEvent event ) {
                SimSharingManager.sendUserMessage( userComponent, action, Parameter.param( Parameters.flowRate, faucet.flowRate.get() ) );
            }
        } );
    }
}
