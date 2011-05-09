// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import java.io.Serializable;
import java.util.ArrayList;

import edu.colorado.phet.simsharing.SessionID;
import edu.colorado.phet.simsharing.StudentSummary;

/**
 * @author Sam Reid
 */
public class StudentList implements Serializable {
    private final ArrayList<StudentSummary> students;

    public StudentList( ArrayList<StudentSummary> students ) {
        this.students = students;
    }

    public boolean containsStudent( SessionID sessionID ) {
        for ( StudentSummary student : students ) {
            if ( student.getSessionID().equals( sessionID ) ) {
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

    public ArrayList<StudentSummary> toList() {
        return new ArrayList<StudentSummary>( students );
    }
}