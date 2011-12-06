// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * Container for sim-sharing event arguments.
 * This is used to provide information to components and handlers (eg, drag handlers) that need to send sim-sharing events.
 * <p/>
 * The object arg is always required.
 * The action arg is optional, for cases where the client provides the action (eg drag handlers).
 * The parameters arg is optional, since parameters are optional in sim-sharing events.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingEventArgs {

    public final String object;
    public final String action;
    public final Function0<Parameter[]> parameters;

    public SimSharingEventArgs( String object ) {
        this( object, (String) null );
    }

    public SimSharingEventArgs( String object, String action ) {
        this( object, action, new Function0<Parameter[]>() {
            public Parameter[] apply() {
                return new Parameter[] { };
            }
        } );
    }

    public SimSharingEventArgs( String object, Function0<Parameter[]> parameters ) {
        this( object, null, parameters );
    }

    public SimSharingEventArgs( String object, String action, Function0<Parameter[]> parameters ) {
        this.object = object;
        this.action = action;
        this.parameters = parameters;
    }
}
