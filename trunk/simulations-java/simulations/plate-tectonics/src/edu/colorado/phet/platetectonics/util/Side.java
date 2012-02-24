// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.platetectonics.util;

import java.util.Collection;
import java.util.List;

/**
 * Makes handling of mirror cases and directionality much easier.
 * <p/>
 * Generally assumes the left-to-right ordering of arrays with indices 0 to size-1
 */
public enum Side {
    LEFT,
    RIGHT;

    // return the <side>-most index in a left-to-right array
    public int getIndex( int size ) {
        return this == LEFT ? 0 : size - 1;
    }

    public <T> int getIndex( Collection<T> collection ) {
        return getIndex( collection.size() );
    }

    // return the next of a new <side>-most index in a left-to-right array
    public int getNewIndex( int size ) {
        return this == LEFT ? 0 : size;
    }

    public <T> int getNewIndex( Collection<T> collection ) {
        return getNewIndex( collection.size() );
    }

    // add an item to this side of the list
    public <T> void addToList( List<T> list, T item ) {
        list.add( getNewIndex( list ), item );
    }

    // remove an item from this side of the list
    public <T> void removeFromList( List<T> list ) {
        list.remove( getIndex( list ) );
    }

    // get an item from one end of the list
    public <T> T getEnd( List<T> list ) {
        return list.get( getIndex( list ) );
    }

    public Side opposite() {
        return this == LEFT ? RIGHT : LEFT;
    }

    // direction of motion sign for cartesian coordinates
    public int getSign() {
        return this == LEFT ? -1 : 1;
    }
}
