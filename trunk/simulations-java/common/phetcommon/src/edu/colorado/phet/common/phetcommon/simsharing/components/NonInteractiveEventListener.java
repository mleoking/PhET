// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;

import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.param;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys.interactive;

/**
 * Standard event listener to use when a Swing component is non-interactive,
 * so we can see if/when they are clicked on.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NonInteractiveEventListener extends MouseInputAdapter {

    private final IUserComponent userComponent;

    public NonInteractiveEventListener( IUserComponent userComponent ) {
        this.userComponent = userComponent;
    }

    @Override public void mousePressed( MouseEvent event ) {
        SimSharingManager.sendUserMessage( userComponent, UserActions.pressed, param( interactive, false ) );
        super.mousePressed( event );
    }
}
