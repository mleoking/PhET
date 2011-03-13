// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.io.Serializable;

/**
 * @author Sam Reid
 */
public class GetStudentData implements Serializable {

    private final SessionID selected;
    private int index;

    public GetStudentData( SessionID selected, int index ) {
        this.selected = selected;
        this.index = index;
    }

    public SessionID getStudentID() {
        return selected;
    }

    public int getIndex() {
        return index;
    }
}
