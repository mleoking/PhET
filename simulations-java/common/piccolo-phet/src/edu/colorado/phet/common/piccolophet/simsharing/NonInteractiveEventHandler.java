// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.simsharing;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.Parameter.param;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys.interactive;

/**
 * Standard event handler to use when a piccolo component is non-interactive,
 * so we can see if/when they are clicked on.
 *
 * @author Sam Reid
 */
public class NonInteractiveEventHandler extends PBasicInputEventHandler {

    private final IUserComponent userComponent;
    private final IUserComponentType componentType;

    // Convenience method, since most such components are sprites.
    public NonInteractiveEventHandler( IUserComponent userComponent ) {
        this( userComponent, UserComponentTypes.sprite );
    }

    public NonInteractiveEventHandler( IUserComponent userComponent, IUserComponentType componentType ) {
        this.userComponent = userComponent;
        this.componentType = componentType;
    }

    @Override public void mousePressed( PInputEvent event ) {
        SimSharingManager.sendUserMessage( userComponent, componentType, UserActions.pressed, param( interactive, false ) );
        super.mousePressed( event );
    }
}