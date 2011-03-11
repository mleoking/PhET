// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import java.io.Serializable;
import java.util.ArrayList;

import edu.colorado.phet.simsharing.StudentID;
import edu.colorado.phet.simsharing.StudentSummary;

/**
 * @author Sam Reid
 */
public class StudentList implements Serializable {
    private final ArrayList<StudentSummary> students;

    public StudentList( ArrayList<StudentSummary> students ) {
        this.students = students;
    }

    public boolean containsStudent( StudentID studentID ) {
        for ( StudentSummary student : students ) {
            if ( student.getStudentID().equals( studentID ) ) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return students.size();
    }

    public StudentSummary get( int i ) {
        return students.get( i );
    }
}