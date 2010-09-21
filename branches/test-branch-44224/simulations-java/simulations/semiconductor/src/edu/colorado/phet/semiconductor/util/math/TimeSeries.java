package edu.colorado.phet.semiconductor.util.math;

import java.util.ArrayList;

/**
 * Keeps the last -size- elements added, and allows access starting at the beginning of the local history.
 */
public class TimeSeries {
    ArrayList list;
    private int size;

    public TimeSeries( int size ) {
        this.size = size;
        list = new ArrayList();
    }

    public int length() {
        return size;
    }

    public void add( Object obj ) {
        list.add( obj );
        if ( list.size() > size ) {
            list.remove( 0 );
        }
    }

    public Object[] toArray( Object[] type ) {
        return list.toArray( type );
    }

    public String toString() {
        return list.toString();
    }

}
