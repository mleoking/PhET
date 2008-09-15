package edu.colorado.phet.balloons;

import java.util.Vector;

public class TruncatedSeries {
    Vector v;
    int size;
    int pointerIndex;
    int addCount = 0;

    public TruncatedSeries( int size ) {
        this.size = size;
        v = new Vector();
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
            v.setElementAt( obj, pointerIndex );
        }
        pointerIndex++;
        if( pointerIndex >= size ) {
            pointerIndex = 0;
        }
        addCount++;
    }

    public Vector get() {
        /**Start at pointerindex and add the elements in the right order.*/
        Vector x = new Vector();
        int index = pointerIndex;
        if( index == v.size() ) {
            return (Vector)v.clone();
        }
        while( x.size() < v.size() && index < v.size() ) {
            x.add( v.get( index ) );
            index++;
            if( index >= size ) {
                index = 0;
            }
        }
        return x;
    }

    public String toString() {
        return get().toString();
    }

    public static void main( String[] args ) {
        TruncatedSeries ts = new TruncatedSeries( 5 );
        for( int i = 0; i < 10; i++ ) {
            ts.add( new Integer( i ) );
            System.out.println( "i=" + i + ", ts=" + ts );
        }
    }
}
