// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.messages;

/**
 * @author Sam Reid
 */
public class System {

    public static enum SystemObjects implements SystemObject {
        simsharingManager, application
    }

    public static enum SystemActions implements SystemAction {
        started, stopped, connectedToServer, sentEvent, exited, shown
    }

}
