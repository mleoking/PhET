// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import java.util.HashMap;

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

    private final IMessageType messageType;
    private final T object;
    private final U action;

    //Implemented as an array of parameter objects to make it easy to factor out repeated usages such as componentType(*) or canvasX parameters.
    //Alternatively could have been implemented as a map.  Since it is not a map, we have to make sure no two parameter keys are the same.
    private final Parameter[] parameters;

    public SimSharingMessage( IMessageType messageType, T object, U action, final Parameter... parameters ) {
        this.object = object;
        this.action = action;
        this.parameters = parameters;
        this.messageType = messageType;

        //If a parameter name was provided twice with 2 different values, that is a problem.
        //May also be a problem if same parameter is provided twice with the same value, but we will allow it since it may provide some flexibility in otherwise tricky client code
        new HashMap<String, String>() {{
            for ( Parameter parameter : parameters ) {
                if ( containsKey( parameter.name ) && !get( parameter.name ).equals( parameter.value ) ) {
                    throw new RuntimeException( "Parameter mismatch: " + parameter.name + ", value1 = " + get( parameter.name ) + ", value2 = " + parameter.value );
                }
            }
        }};
    }

    public String toString( String delimiter ) {
        String parameterText = new ObservableList<Parameter>( parameters ).mkString( delimiter );
        return messageType + delimiter + object + delimiter + action + delimiter + parameterText;
    }
}