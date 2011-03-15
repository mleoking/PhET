// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.io.Serializable;
import java.util.ArrayList;

import edu.colorado.phet.gravityandorbits.simsharing.GravityAndOrbitsApplicationState;

/**
 * @author Sam Reid
 */
public class AddMultiSample implements Serializable {
    private SessionID sessionID;
    private ArrayList<GravityAndOrbitsApplicationState> data;

    public AddMultiSample( SessionID sessionID, ArrayList<GravityAndOrbitsApplicationState> data ) {
        this.sessionID = sessionID;
        this.data = data;
    }

    public SessionID getSessionID() {
        return sessionID;
    }

    public ArrayList<GravityAndOrbitsApplicationState> getData() {
        return data;
    }
}
