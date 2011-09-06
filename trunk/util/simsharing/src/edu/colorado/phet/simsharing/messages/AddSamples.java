// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.messages;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class is used to send samples to the server, they can be batched up or sent one at a time.
 *
 * @author Sam Reid
 */
public class AddSamples<T> implements Serializable {
    private SessionID sessionID;
    private ArrayList<T> data;

    public AddSamples( SessionID sessionID, ArrayList<T> data ) {
        this.sessionID = sessionID;
        this.data = data;
    }

    public SessionID getSessionID() {
        return sessionID;
    }

    public ArrayList<T> getData() {
        return data;
    }
}