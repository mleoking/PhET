// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import java.io.Serializable;
import java.util.ArrayList;

import edu.colorado.phet.simsharing.StudentID;

/**
 * @author Sam Reid
 */
public class RecordingList implements Serializable {
    private ArrayList<StudentID> list = new ArrayList<StudentID>();

    public void add( StudentID studentID ) {
        list.add( studentID );
    }

    public int size() {
        return list.size();
    }

    public StudentID get( int i ) {
        return list.get( i );
    }

    public Object[] toArray() {
        return list.toArray( new StudentID[0] );
    }
}
