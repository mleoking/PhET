// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager.DELIMITER;

/**
 * Adds information to all messages to be sent message to be sent/logged such as a timestamp.
 *
 * @author Sam Reid
 */
public class AugmentedMessage {
    private final long time;
    private final SimSharingMessage message;

    public AugmentedMessage( SimSharingMessage message ) {
        this( System.currentTimeMillis(), message );
    }

    public AugmentedMessage( long time, SimSharingMessage message ) {
        this.time = time;
        this.message = message;
    }

    @Override public String toString() {
        return time + DELIMITER + message.toString();
    }
}