// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Sam Reid
 */
public class StudentList implements Serializable {
    ArrayList<StudentID> studentIDs;

    public StudentList( ArrayList<StudentID> studentIDs ) {
        this.studentIDs = studentIDs;
    }

    public StudentID[] toArray() {
        return studentIDs.toArray( new StudentID[studentIDs.size()] );
    }
}
