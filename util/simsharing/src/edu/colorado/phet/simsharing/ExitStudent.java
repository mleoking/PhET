// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.io.Serializable;

/**
 * @author Sam Reid
 */
public class ExitStudent implements Serializable {
    private StudentID studentID;

    public ExitStudent( StudentID studentID ) {
        this.studentID = studentID;
    }

    public StudentID getStudentID() {
        return studentID;
    }
}
