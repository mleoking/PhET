// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.event.simsharing;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Standard listener to use when a piccolo component is non-interactive, so we can see if/when they are clicked on.
 *
 * @author Sam Reid
 */
public class NonInteractiveUserComponent extends PBasicInputEventHandler {

    private final IUserComponent userComponent;

    public NonInteractiveUserComponent( IUserComponent userComponent ) {
        this.userComponent = userComponent;
    }

    @Override public void mousePressed( PInputEvent event ) {
        SimSharingManager.sendNonInteractiveUserMessage( userComponent, UserActions.pressed );
        super.mousePressed( event );
    }
}