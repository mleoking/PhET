/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.coreadditions;

/**
 * CircularBuffer
 * <p>
 * A circular buffer of objects. Can be used as to implement of a queue
 * without requiring memory allocation every time an element is added.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CircularBuffer {
    private Object[] values;
    private int head;
    private int tail;
    private int numValues;

    public CircularBuffer( int capacity ) {
        values = new Object[capacity];
    }

    public void add( Object value ) {
        if( numValues == values.length ) {
            throw new ArrayIndexOutOfBoundsException( "attempt to add to full buffer" );
        }
        values[head] = value;
        head = ( head + 1 ) % values.length;
        numValues++;
    }

    public Object remove() {
        if( numValues == 0 ) {
            throw new ArrayIndexOutOfBoundsException( "attempt to remove from an empty buffer" );
        }
        Object value = values[tail];
        tail = ( tail + 1 ) % values.length;
        numValues--;
        return value;
    }

    public Object get( int i ) {
        if( i > numValues - 1 ) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int idx = head - i;
        if( idx < 0 ) {
            idx += values.length;
        }
        return values[idx];
    }

    public int size() {
        return numValues;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for( int i = tail; i <= numValues; i++ ) {
            Object value = values[i % values.length];
            sb.append( value + ";" );
        }
        return sb.toString();
    }

    public static void main( String[] args ) {
        CircularBuffer cb = new CircularBuffer( 3 );
        cb.add( new Integer( 1 ));
        cb.add( new Integer(2 ));
        cb.add( new Integer(3 ));
        System.out.println( "cb = " + cb );
        Object v = cb.remove();
//        double d1 = cb.get( 0 );
//        System.out.println( "d1 = " + d1 );
//
//        System.out.println( "v = " + v );
        System.out.println( "cb = " + cb );
        cb.add( new Integer( 9 ));
        System.out.println( "cb = " + cb );

//        double d2 = cb.get( 2 );
//        System.out.println( "d2 = " + d2 );
//        System.out.println( "cb.remove() = " + cb.remove() );
//        System.out.println( "cb.remove() = " + cb.remove() );
//        System.out.println( "cb.remove() = " + cb.remove() );
//        System.out.println( "cb.remove() = " + cb.remove() );

    }
}
