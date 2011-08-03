// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.util;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * A list that can be observed for element addition and removals.
 *
 * @author John Blanco
 */
public class ObservableList<T> implements List<T> {

    //Underlying data structure containing the elements
    private final List<T> list = new ArrayList<T>();

    //Observers that are notified when an element is added to the list
    private final ObserverList<T> elementAddedObservers = new ObserverList<T>();

    //Observers that are notified when an element is removed from the list
    private final ObserverList<T> elementRemovedObservers = new ObserverList<T>();

    //Observers that are notified when a particular element (as determined by identity) is removed
    //It is important to use identity here so this list can work with mutable values (such as moving particles)
    private final IdentityHashMap<T, ArrayList<VoidFunction0>> particularElementRemovedObservers = new IdentityHashMap<T, ArrayList<VoidFunction0>>();

    /**
     * Default constructor.
     */
    public ObservableList() {
    }

    /**
     * Constructor that copies another collection.
     *
     * @param listToCopy
     */
    public ObservableList( ObservableList<T> listToCopy ) {
        list.addAll( listToCopy );
        // Since this is a constructor, no notifications should need to be sent.
    }


    public void addElementAddedObserver( VoidFunction1<T> elementAddedObserver ) {
        addElementAddedObserver( elementAddedObserver, true );
    }

    public void addElementAddedObserver( VoidFunction1<T> elementAddedObserver, boolean notifyImmediately ) {
        elementAddedObservers.addObserver( elementAddedObserver );
        if ( notifyImmediately ) {
            for ( T element : list ) {
                elementAddedObserver.apply( element );
            }
        }
    }

    public void removeElementAddedObserver( VoidFunction1<T> elementAddedObserver ) {
        elementAddedObservers.removeObserver( elementAddedObserver );
    }

    public void addElementRemovedObserver( VoidFunction1<T> elementRemovedObserver ) {
        elementRemovedObservers.addObserver( elementRemovedObserver );
    }

    public void removeElementRemovedObserver( VoidFunction1<T> elementRemovedObserver ) {
        elementRemovedObservers.removeObserver( elementRemovedObserver );
    }

    //Listen for the removal of a specific element
    public void addElementRemovedObserver( T element, VoidFunction0 observer ) {
        if ( !particularElementRemovedObservers.containsKey( element ) ) {
            particularElementRemovedObservers.put( element, new ArrayList<VoidFunction0>() );
        }
        particularElementRemovedObservers.get( element ).add( observer );
    }

    //Remove a observer that was listening for a specific element removal
    public void removeElementRemovedObserver( T element, VoidFunction0 observer ) {
        if ( particularElementRemovedObservers.containsKey( element ) ) {
            particularElementRemovedObservers.get( element ).remove( observer );
        }
    }

    //------------------------------------------------------------------------
    // Below are functions that modify the contents of the list, so they make
    // the modifications of the enclosed list and then send out the
    // appropriate notifications.
    //------------------------------------------------------------------------

    public boolean add( T t ) {
        boolean result = list.add( t );
        elementAddedObservers.notifyObservers( t );
        return result;
    }

    public boolean remove( Object o ) {
        boolean result = list.remove( o );
        elementRemovedObservers.notifyObservers( (T) o );

        //Notify observers that were specifically observing when the specified element would be removed
        if ( particularElementRemovedObservers.containsKey( o ) ) {
            for ( VoidFunction0 observer : new ArrayList<VoidFunction0>( particularElementRemovedObservers.get( o ) ) ) {
                observer.apply();
            }
        }

        return result;
    }

    public boolean addAll( Collection<? extends T> c ) {
        for ( T t : c ) {
            add( t );
        }
        return true;
    }

    public boolean addAll( int index, Collection<? extends T> c ) {
        boolean result = list.addAll( index, c );
        for ( T element : c ) {
            elementAddedObservers.notifyObservers( element );
        }
        return result;
    }

    public boolean removeAll( Collection<?> c ) {
        boolean anythingRemoved = false;
        for ( Object o : c ) {
            boolean wasRemoved = remove( o );
            anythingRemoved = anythingRemoved || wasRemoved;
        }
        return anythingRemoved;
    }

    public boolean retainAll( Collection<?> c ) {
        // Didn't implement this yet, please do so if it is needed.
        throw ( new NotImplementedException() );
    }

    public void clear() {
        while ( size() > 0 ) {
            remove( 0 );
        }
    }

    public void add( int index, T element ) {
        list.add( index, element );
        elementAddedObservers.notifyObservers( element );
    }

    public T remove( int index ) {
        T value = get( index );
        remove( value );
        return value;
    }

    //------------------------------------------------------------------------
    // Below are methods that are straight pass throughs to the enclosed list.
    //------------------------------------------------------------------------

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public boolean contains( Object o ) {
        return list.contains( o );
    }

    public Iterator<T> iterator() {
        return list.iterator();
    }

    public Object[] toArray() {
        return list.toArray();
    }

    public <T> T[] toArray( T[] a ) {
        return list.toArray( a );
    }

    public boolean containsAll( Collection<?> c ) {
        return list.containsAll( c );
    }

    public boolean equals( Object o ) {
        return list.equals( o );
    }

    public int hashCode() {
        return list.hashCode();
    }

    public T get( int index ) {
        return list.get( index );
    }

    public T set( int index, T element ) {
        return list.set( index, element );
    }

    public int indexOf( Object o ) {
        return list.indexOf( o );
    }

    public int lastIndexOf( Object o ) {
        return list.lastIndexOf( o );
    }

    public ListIterator<T> listIterator() {
        return list.listIterator();
    }

    public ListIterator<T> listIterator( int index ) {
        return listIterator( index );
    }

    public List<T> subList( int fromIndex, int toIndex ) {
        return list.subList( fromIndex, toIndex );
    }
}
