// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import java.io.Serializable;
import java.util.ArrayList;

import edu.colorado.phet.gravityandorbits.simsharing.SerializableBufferedImage;
import edu.colorado.phet.simsharing.Pair;
import edu.colorado.phet.simsharing.StudentID;

/**
 * @author Sam Reid
 */
public class StudentList implements Serializable {
    private final ArrayList<Pair<StudentID, SerializableBufferedImage>> studentIDs;

    public StudentList( ArrayList<Pair<StudentID, SerializableBufferedImage>> studentIDs ) {
        this.studentIDs = studentIDs;
    }

    public ArrayList<Pair<StudentID, SerializableBufferedImage>> getStudentIDs() {
        return studentIDs;
    }

    public boolean containsStudent( StudentID studentID ) {
        for ( Pair<StudentID, SerializableBufferedImage> id : studentIDs ) {
            if ( id._1.equals( studentID ) ) {
                return true;
            }
        }
        return false;
    }
}