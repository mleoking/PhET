// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.common;

import java.util.*;

public class ListDecorator implements List {
    public ListDecorator() {
        this.underlying = new ArrayList();
    }

    public ListDecorator( List underlying ) {
        this.underlying = underlying;
    }

    protected List underlying;

    public int size() {
        return underlying.size();
    }

    public boolean isEmpty() {
        return underlying.isEmpty();
    }

    public boolean contains( Object o ) {
        return underlying.contains( o );
    }

    public Iterator iterator() {
        return underlying.iterator();
    }

    public Object[] toArray() {
        return underlying.toArray();
    }

    public Object[] toArray( Object[] objects ) {
        return underlying.toArray( objects );
    }

    public boolean add( Object o ) {
        return underlying.add( o );
    }

    public boolean remove( Object o ) {
        return underlying.remove( o );
    }

    public boolean containsAll( Collection collection ) {
        return underlying.containsAll( collection );
    }

    public boolean addAll( Collection collection ) {
        return underlying.addAll( collection );
    }

    public boolean addAll( int i, Collection collection ) {
        return underlying.addAll( i, collection );
    }

    public boolean removeAll( Collection collection ) {
        return underlying.removeAll( collection );
    }

    public boolean retainAll( Collection collection ) {
        return underlying.retainAll( collection );
    }

    public void clear() {
        underlying.clear();
    }

    public boolean equals( Object o ) {
        return underlying.equals( o );
    }

    public int hashCode() {
        return underlying.hashCode();
    }

    public Object get( int i ) {
        return underlying.get( i );
    }

    public Object set( int i, Object o ) {
        return underlying.set( i, o );
    }

    public void add( int i, Object o ) {
        underlying.add( i, o );
    }

    public Object remove( int i ) {
        return underlying.remove( i );
    }

    public int indexOf( Object o ) {
        return underlying.indexOf( o );
    }

    public int lastIndexOf( Object o ) {
        return underlying.lastIndexOf( o );
    }

    public ListIterator listIterator() {
        return underlying.listIterator();
    }

    public ListIterator listIterator( int i ) {
        return underlying.listIterator( i );
    }

    public List subList( int i, int i1 ) {
        return underlying.subList( i, i1 );
    }
}
