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

    private final String object;
    private final String action;
    private final Parameter[] parameters;

    public SimSharingEvent( String object, String action, Parameter... parameters ) {
        this.object = object;
        this.action = action;
        this.parameters = parameters;
    }

    public String toString( String delimiter ) {
        String parameterText = new ObservableList<Parameter>( parameters ).mkString( delimiter );
        return object + delimiter + action + delimiter + parameterText;
    }
}
