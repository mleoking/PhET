// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.io.Serializable;

/**
 * @author Sam Reid
 */
public class GetStudentData implements Serializable {

    private final StudentID selected;
    private final Time time;

    public GetStudentData( StudentID selected, Time time ) {
        this.selected = selected;
        this.time = time;
    }

    public StudentID getStudentID() {
        return selected;
    }

    public Time getTime() {
        return time;
    }
}
