// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IMessageSource;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IMessageType;
import edu.colorado.phet.common.phetcommon.util.ObservableList;

/**
 * Sim-sharing event.
 * The object (required) is the subject of the event.
 * The action (required) is the verb of the event.
 * The parameters (optional) provide additional information about the event.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingMessage<T, U> {

    enum MessageType implements IMessageType {user, system, model}

    private final IMessageSource messageSource;
    private final IMessageType messageType;
    private final T object;
    private final U action;
    private final Parameter[] parameters;

    public SimSharingMessage( IMessageSource source, IMessageType messageType, T object, U action, Parameter... parameters ) {
        this.messageSource = source;
        this.object = object;
        this.action = action;
        this.parameters = parameters;
        this.messageType = messageType;
    }

    public String toString( String delimiter ) {
        String parameterText = new ObservableList<Parameter>( parameters ).mkString( delimiter );
        return messageType + delimiter + object + delimiter + action + delimiter + parameterText;
    }
}