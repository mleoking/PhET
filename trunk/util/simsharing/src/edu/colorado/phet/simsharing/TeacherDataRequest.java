// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.io.Serializable;

/**
 * @author Sam Reid
 */
public class TeacherDataRequest implements Serializable {

    private final StudentID selected;

    public TeacherDataRequest( StudentID selected ) {
        this.selected = selected;
    }

    public StudentID getStudentID() {
        return selected;
    }
}
