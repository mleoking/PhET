// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.colorado.phet.simsharing.messages.SessionID;
import edu.colorado.phet.simsharing.messages.StudentSummary;

/**
 * @author Sam Reid
 */
public class StudentList implements Serializable {
    private final ArrayList<StudentSummary> students;

    public StudentList( ArrayList<StudentSummary> students ) {

        //Copy is made to support DBI on input parameter
        this.students = new ArrayList<StudentSummary>( students );

        //Sort by uptime so new students appear at the bottom
        Collections.sort( students, new Comparator<StudentSummary>() {
            public int compare( StudentSummary o1, StudentSummary o2 ) {
                return Double.compare( o1.getUpTime(), o2.getUpTime() );
            }
        } );
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