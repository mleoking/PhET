// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.messages;

import edu.colorado.phet.common.phetcommon.simsharing.IMessageSource;
import edu.colorado.phet.common.phetcommon.simsharing.IMessageType;
import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingMessage;

/**
 * @author Sam Reid
 */
public class UserMessage extends SimSharingMessage<UserComponent, UserAction> {
    public UserMessage( IMessageSource source, IMessageType messageType, UserComponent object, UserAction action, Parameter... parameters ) {
        super( source, messageType, object, action, parameters );
    }
}
