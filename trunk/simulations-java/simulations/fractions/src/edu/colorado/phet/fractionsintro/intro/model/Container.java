// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

import java.util.Collection;
import java.util.HashSet;

/**
 * Model for one container which holds some number of slices.
 * Do not mutate, other code relies on this class being immutable.
 *
 * @author Sam Reid
 */
public class Container {
    public final int numCells;
    public final HashSet<Integer> filledCells;
    public final int numFilledCells;

    public Container( int numCells, int[] filledCells ) {
        this( numCells, toSet( filledCells ) );
    }

    private static HashSet<Integer> toSet( int[] filledCells ) {
        HashSet<Integer> list = new HashSet<Integer>();
        for ( int cell : filledCells ) {
            list.add( cell );
        }
        return list;
    }

    @Override public String toString() {
        return filledCells.toString();
    }

    public Container( int numCells, final Collection<Integer> filledCells ) {
        this( numCells, new HashSet<Integer>() {{
            for ( Integer filledCell : filledCells ) {
                add( filledCell );
            }
        }} );
    }

    public Container( int numCells, HashSet<Integer> filledCells ) {
        this.numCells = numCells;
        this.filledCells = filledCells;
        this.numFilledCells = filledCells.size();
    }

    public static Container full( final int denominator ) {
        return new Container( denominator, new HashSet<Integer>() {{
            for ( int i = 0; i < denominator; i++ ) {
                add( i );
            }
        }} );
    }

    public boolean isFull() {
        return new HashSet<Integer>( filledCells ).equals( new HashSet<Integer>( RandomFill.listAll( numCells ) ) );
    }

    public Boolean isEmpty() {
        return filledCells.size() == 0;
    }

    public Boolean isEmpty( int cell ) {
        return !filledCells.contains( cell );
    }

    //Return a copy but with the specified cell toggled
    public Container toggle( final int cell ) {
        if ( !filledCells.contains( cell ) ) {
            return new Container( numCells, new HashSet<Integer>( filledCells ) {{add( cell );}} );
        }
        else {
            return new Container( numCells, new HashSet<Integer>( filledCells ) {{remove( cell );}} );
        }
    }

    public int getLowestEmptyCell() {
        for ( int i = 0; i < numCells; i++ ) {
            if ( isEmpty( i ) ) { return i; }
        }
        return -1;
    }

    public int getHighestFullCell() {
        for ( int i = numCells - 1; i >= 0; i-- ) {
            if ( !isEmpty( i ) ) { return i; }
        }
        return -1;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }

        Container container = (Container) o;

        if ( numCells != container.numCells ) { return false; }
        if ( numFilledCells != container.numFilledCells ) { return false; }
        if ( filledCells != null ? !filledCells.equals( container.filledCells ) : container.filledCells != null ) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = numCells;
        result = 31 * result + ( filledCells != null ? filledCells.hashCode() : 0 );
        result = 31 * result + numFilledCells;
        return result;
    }
}