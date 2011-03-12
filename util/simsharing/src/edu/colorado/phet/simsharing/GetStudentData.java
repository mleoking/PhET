// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.io.Serializable;

/**
 * @author Sam Reid
 */
public class GetStudentData implements Serializable {

    private final StudentID selected;
    private int index;

    public GetStudentData( StudentID selected, int index ) {
        this.selected = selected;
        this.index = index;
    }

    public StudentID getStudentID() {
        return selected;
    }

    public int getIndex() {
        return index;
    }
}
