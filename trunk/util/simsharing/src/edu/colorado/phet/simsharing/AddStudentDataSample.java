// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.io.Serializable;

/**
 * @author Sam Reid
 */
public class AddStudentDataSample implements Serializable {
    private SessionID studentID;
    private Object data;

    public AddStudentDataSample( SessionID studentID, Object data ) {
        this.studentID = studentID;
        this.data = data;
    }

    public SessionID getStudentID() {
        return studentID;
    }

    public Object getData() {
        return data;
    }
}
