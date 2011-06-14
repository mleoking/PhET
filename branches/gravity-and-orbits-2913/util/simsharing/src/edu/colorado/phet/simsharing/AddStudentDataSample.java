// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.io.Serializable;

/**
 * @author Sam Reid
 */
public class AddStudentDataSample implements Serializable {
    private SessionID sessionID;
    private Object data;

    public AddStudentDataSample( SessionID sessionID, Object data ) {
        this.sessionID = sessionID;
        this.data = data;
    }

    public SessionID getSessionID() {
        return sessionID;
    }

    public Object getData() {
        return data;
    }
}
