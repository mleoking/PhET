// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.io.Serializable;

/**
 * @author Sam Reid
 */
public class StudentDataSample implements Serializable {
    private StudentID studentID;
    private Object data;

    public StudentDataSample( StudentID studentID, Object data ) {
        this.studentID = studentID;
        this.data = data;
    }

    public StudentID getStudentID() {
        return studentID;
    }

    public Object getData() {
        return data;
    }
}
