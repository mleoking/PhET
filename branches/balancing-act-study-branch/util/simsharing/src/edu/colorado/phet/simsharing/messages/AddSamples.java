// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.messages;

import java.io.Serializable;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.simsharing.state.SimState;

/**
 * This class is used to send samples to the server, they can be batched up or sent one at a time.
 *
 * @author Sam Reid
 */
public class AddSamples implements Serializable {
    public final SessionID sessionID;
    public final ArrayList<SimState> data;

    public AddSamples( SessionID sessionID, ArrayList<SimState> data ) {
        this.sessionID = sessionID;
        this.data = data;
    }

    @Override public String toString() {
        return "id = " + sessionID + ", data = " + data;
    }
}