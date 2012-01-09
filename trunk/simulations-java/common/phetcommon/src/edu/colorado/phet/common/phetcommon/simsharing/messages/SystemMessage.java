// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.messages;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingMessage;

/**
 * Message for events performed automatically by the system, like startup.
 *
 * @author Sam Reid
 */
public class SystemMessage extends SimSharingMessage<SystemObject, SystemAction> {
    public SystemMessage( IMessageType messageType, SystemObject object, SystemAction systemAction, Parameter... parameters ) {
        super( messageType, object, systemAction, parameters );
    }
}
