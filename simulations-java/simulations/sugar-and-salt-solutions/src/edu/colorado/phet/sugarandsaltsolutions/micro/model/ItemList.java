package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Observable list class that can be observed for items added or removed.
 *
 * @author Sam Reid
 */
public class ItemList<T> implements Iterable<T> {
    private final ArrayList<T> items = new ArrayList<T>();
    private final ListenerList<T> itemAddedListeners = new ListenerList<T>();
    private final ListenerList<T> itemRemovedListeners = new ListenerList<T>();

    public void addItemAddedListener( VoidFunction1<T> listener ) {
        itemAddedListeners.addListener( listener );
    }

    public void removeItemAddedListener( VoidFunction1<T> listener ) {
        itemAddedListeners.removeListener( listener );
    }

    public void addItemRemovedListener( VoidFunction1<T> listener ) {
        itemRemovedListeners.addListener( listener );
    }

    public void removeItemRemovedListener( VoidFunction1<T> listener ) {
        itemRemovedListeners.removeListener( listener );
    }

    public void add( T item ) {
        items.add( item );
        itemAddedListeners.notifyListeners( item );
    }

    public void remove( T item ) {
        items.remove( item );
        itemRemovedListeners.notifyListeners( item );
    }

    public Iterator<T> iterator() {
        return items.iterator();
    }

    public boolean contains( T item ) {
        return items.contains( item );
    }
}