// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.messages;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingMessage;

/**
 * Message for events performed automatically by the system, like startup.
 *
 * @author Sam Reid
 */
public class SystemMessage extends SimSharingMessage<ISystemObject, ISystemAction> {
    public SystemMessage( IMessageType messageType, ISystemObject object, ISystemAction systemAction, ParameterSet parameters ) {
        super( messageType, object, systemAction, parameters );
    }
}
