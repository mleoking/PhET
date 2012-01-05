// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import edu.colorado.phet.common.phetcommon.util.ObservableList;

/**
 * Sim-sharing event.
 * The object (required) is the subject of the event.
 * The action (required) is the verb of the event.
 * The parameters (optional) provide additional information about the event.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingEvent {

    enum MessageType {user, system, model}

    private final MessageType messageType;
    private final String object;
    private final String action;
    private final Parameter[] parameters;

    private SimSharingEvent( MessageType messageType, String object, String action, Parameter... parameters ) {
        this.object = object;
        this.action = action;
        this.parameters = parameters;
        this.messageType = messageType;
    }

    public String toString( String delimiter ) {
        String parameterText = new ObservableList<Parameter>( parameters ).mkString( delimiter );
        return messageType + delimiter + object + delimiter + action + delimiter + parameterText;
    }

    //Subclass to use for user events
    public static class UserEvent extends SimSharingEvent {
        public UserEvent( String component, String action, Parameter... parameters ) {
            super( MessageType.user, component, action, parameters );
        }
    }

    //Subclass to use for system events
    public static class SystemEvent extends SimSharingEvent {
        public SystemEvent( String object, String action, Parameter... parameters ) {
            super( MessageType.system, object, action, parameters );
        }
    }

    //Subclass to use for model events
    public static class ModelEvent extends SimSharingEvent {
        public ModelEvent( String object, String action, Parameter... parameters ) {
            super( MessageType.model, object, action, parameters );
        }
    }
}
