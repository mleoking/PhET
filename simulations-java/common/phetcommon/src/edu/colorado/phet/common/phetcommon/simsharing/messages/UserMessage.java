// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.messages;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingMessage;

/**
 * Message class for events performed by the user.
 *
 * @author Sam Reid
 */
public class UserMessage extends SimSharingMessage<UserComponent, UserAction> {
    public UserMessage( IMessageType messageType, UserComponent object, UserAction action, Parameter... parameters ) {
        super( messageType, object, action, parameters );
    }
}
