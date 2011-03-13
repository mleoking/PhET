// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.io.Serializable;

/**
 * @author Sam Reid
 */
public class EndSession implements Serializable {
    private SessionID studentID;

    public EndSession( SessionID studentID ) {
        this.studentID = studentID;
    }

    public SessionID getStudentID() {
        return studentID;
    }
}
