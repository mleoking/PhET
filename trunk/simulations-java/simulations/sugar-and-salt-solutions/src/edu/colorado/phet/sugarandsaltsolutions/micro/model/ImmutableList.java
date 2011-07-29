package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.util.function.Function1;

import static java.util.Collections.addAll;

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
        addAll( elements, elm );
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

    //Create a new list without the specified items
    public ImmutableList<T> drop( Function1<T, Boolean> drop ) {
        ImmutableList newList = new ImmutableList();
        for ( T t : elements ) {
            if ( !drop.apply( t ) ) {
                newList.elements.add( t );
            }
        }
        return newList;
    }

    //Create a new list without the specified item
    public ImmutableList<T> drop( final T element ) {
        return drop( new Function1<T, Boolean>() {
            public Boolean apply( T t ) {
                return t == element;
            }
        } );
    }

    //Determine whether the list contains any item matching the predicate
    public boolean contains( Function1<T, Boolean> predicate ) {
        for ( T element : elements ) {
            if ( predicate.apply( element ) ) {
                return true;
            }
        }
        return false;
    }

    //Determine whether the list contains the specified item
    public boolean contains( T item ) {
        return elements.contains( item );
    }
}
