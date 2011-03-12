// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.io.Serializable;

/**
 * @author Sam Reid
 */
public class StudentID implements Serializable {
    private int index;
    private String name;

    public StudentID() {
    }

    public StudentID( int index, String name ) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " #" + index;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }

        StudentID studentID = (StudentID) o;

        if ( index != studentID.index ) { return false; }
        if ( name != null ? !name.equals( studentID.name ) : studentID.name != null ) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = index;
        result = 31 * result + ( name != null ? name.hashCode() : 0 );
        return result;
    }
}
