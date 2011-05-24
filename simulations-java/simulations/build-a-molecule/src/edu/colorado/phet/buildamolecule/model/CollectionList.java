//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * An internal list of collections that a user will be able to scroll through using a control on the collection area
 */
public class CollectionList {

    private final List<KitCollection> collections;
    private int currentIndex = 0;
    public final Property<KitCollection> currentCollection;
    private final List<Listener> listeners = new LinkedList<Listener>();
    private LayoutBounds layoutBounds;//picometers

    public CollectionList( KitCollection firstCollection, LayoutBounds layoutBounds ) {
        this.layoutBounds = layoutBounds;
        collections = new LinkedList<KitCollection>();
        currentCollection = new Property<KitCollection>( firstCollection );
        addCollection( firstCollection );
    }

    private void switchTo( KitCollection collection ) {
        currentIndex = collections.indexOf( collection );
        currentCollection.set( collection );
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void addCollection( KitCollection collection ) {
        collections.add( collection );

        for ( Listener listener : new ArrayList<Listener>( listeners ) ) {
            listener.addedCollection( collection );
        }

        // switch to collection
        currentIndex = collections.indexOf( collection );
        currentCollection.set( collection );
    }

    public void removeCollection( KitCollection collection ) {
        assert ( currentCollection.get() != collection );
        collections.remove( collection );
        for ( Listener listener : new ArrayList<Listener>( listeners ) ) {
            listener.removedCollection( collection );
        }
    }

    public void reset() {
        // switch to the first collection
        switchTo( collections.get( 0 ) );

        // reset it
        collections.get( 0 ).resetAll();

        // remove all the other collections
        for ( KitCollection collection : new ArrayList<KitCollection>( collections ) ) {
            if ( currentCollection.get() != collection ) {
                removeCollection( collection );
            }
        }
    }

    public PBounds getAvailableKitBounds() {
        return layoutBounds.getAvailableKitBounds();
    }

    public PBounds getAvailablePlayAreaBounds() {
        return layoutBounds.getAvailablePlayAreaBounds();
    }

    public boolean hasPreviousCollection() {
        return currentIndex > 0;
    }

    public boolean hasNextCollection() {
        return currentIndex < collections.size() - 1;
    }

    public void switchToPreviousCollection() {
        switchTo( collections.get( currentIndex - 1 ) );
    }

    public void switchToNextCollection() {
        switchTo( collections.get( currentIndex + 1 ) );
    }

    public List<KitCollection> getCollections() {
        return new ArrayList<KitCollection>( collections );
    }

    /*---------------------------------------------------------------------------*
    * listeners
    *----------------------------------------------------------------------------*/

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public static interface Listener {
        void addedCollection( KitCollection collection );

        void removedCollection( KitCollection collection );
    }

    public static class Adapter implements Listener {
        public void addedCollection( KitCollection collection ) {

        }

        public void removedCollection( KitCollection collection ) {

        }
    }

}
