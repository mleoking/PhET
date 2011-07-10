package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * List whose elements cannot be changed.
 *
 * @author Sam Reid
 */
public class ImmutableList<T> implements Iterable<T> {
    private ArrayList<T> elements = new ArrayList<T>();

    //This method is provided to avoid compiler warnings for 0-arg uses of the varargs constructor
    public ImmutableList() {
    }

    public ImmutableList( T... elm ) {
        for ( T t : elm ) {
            elements.add( t );
        }
    }

    //Create a list by appending an item to another list
    public ImmutableList( ImmutableList<T> originalValues, T element ) {
        elements.addAll( originalValues.elements );
        elements.add( element );
    }

    public T getFirst() {
        return elements.get( 0 );
    }

    public Iterator<T> iterator() {
        return elements.iterator();
    }

    public int size() {
        return elements.size();
    }

    @Override public String toString() {
        return elements.toString();
    }
}
