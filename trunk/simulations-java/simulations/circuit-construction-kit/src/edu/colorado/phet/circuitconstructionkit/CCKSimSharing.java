// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * This class is where all the user components, actions, and such are defined
 * for the sim, which are necessary for the "sim sharing" (a.k.a. data
 * collection) feature.
 *
 * @author John Blanco
 * @author Sam Reid
 */
public class CCKSimSharing {
    public static enum UserComponents implements IUserComponent {
        battery, wire, lightBulb
    }

    public static enum UserActions implements IUserAction {
        createdComponent, removedComponent, connectionFormed
    }
}
