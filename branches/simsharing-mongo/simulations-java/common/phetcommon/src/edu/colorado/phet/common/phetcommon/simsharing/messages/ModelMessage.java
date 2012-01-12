// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.messages;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingMessage;

/**
 * Message sent about something the model did (without direct user interaction, though it may be a response to a user action.)
 *
 * @author Sam Reid
 */
public class ModelMessage extends SimSharingMessage<ModelObject, IModelAction> {
    public ModelMessage( IMessageType messageType, ModelObject object, IModelAction action, ParameterSet parameters ) {
        super( messageType, object, action, parameters );
    }
}