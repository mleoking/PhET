// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.messages;

/**
 * Enum for actions performed by the system.
 *
 * @author Sam Reid
 */
public enum SystemActions implements ISystemAction {
    started, stopped, connectedToServer, sentEvent, exited, shown,
    ipAddressLookup

}
