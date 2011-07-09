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

    public ImmutableList( T... elm ) {
        for ( T t : elm ) {
            elements.add( t );
        }
    }

    public Iterator<T> iterator() {
        return elements.iterator();
    }

    public int size() {
        return elements.size();
    }
}
