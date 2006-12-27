package edu.colorado.phet.common.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

/**
 * Keeps the last -size- elements added, and allows access starting at the beginning of the local history.
 */
public class TimeSeries {
    ArrayList v;
    int size;
    int pointerIndex;

    public TimeSeries( int size ) {
        this.size = size;
        v = new ArrayList();
        pointerIndex = 0;
    }

    public int length() {
        return size;
    }

    public void add( Object obj ) {
        if( v.size() < size ) {
            v.add( obj );
        }
        else {
            v.set( pointerIndex, obj );
        }
        pointerIndex++;
        if( pointerIndex >= size ) {
            pointerIndex = 0;
        }
    }

    public Object[] getArray() {
        return getArray( new Object[0] );
    }

    public Object[] getArray( Object[] type ) {
        /**Start at pointerindex and add the elements in the right order.*/
        int index = pointerIndex;
        if( index == v.size() ) {
            return v.toArray();
        }
        Vector x = new Vector();
        while( x.size() < v.size() && index < v.size() ) {
            x.add( v.get( index ) );
            index++;
            if( index >= size ) {
                index = 0;
            }
        }
        return x.toArray();
    }

    public String toString() {
        return Arrays.asList( getArray() ).toString();
    }
}
