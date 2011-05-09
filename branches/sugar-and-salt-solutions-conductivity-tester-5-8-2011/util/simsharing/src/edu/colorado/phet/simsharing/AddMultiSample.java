// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Sam Reid
 */
public class AddMultiSample implements Serializable {
    private SessionID sessionID;
    private ArrayList<String> data;

    public AddMultiSample( SessionID sessionID, ArrayList<String> data ) {
        this.sessionID = sessionID;
        this.data = data;
    }

    public SessionID getSessionID() {
        return sessionID;
    }

    public ArrayList<String> getData() {
        return data;
    }
}
