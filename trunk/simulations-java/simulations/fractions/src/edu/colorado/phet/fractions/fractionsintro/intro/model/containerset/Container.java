// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.intro.model.containerset;

import fj.F;
import fj.data.List;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Model for one container which holds some number of slices.
 * Do not make mutable, other code relies on this class being immutable.
 *
 * @author Sam Reid
 */
public final @Data class Container {
    public final int numCells;
    public final List<Integer> filledCells;

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

    private Container( int numCells, final Collection<Integer> filledCells ) {
        this( numCells, List.iterableList( new HashSet<Integer>() {{
            for ( Integer filledCell : filledCells ) {
                add( filledCell );
            }
        }} ) );
    }

    public Container( int numCells, List<Integer> filledCells ) {
        this.numCells = numCells;
        this.filledCells = filledCells;
    }

    public Boolean isEmpty() {
        return filledCells.length() == 0;
    }

    public Boolean isEmpty( final int cell ) {
        return !filledCells.exists( cellIndex( cell ) );
    }

    private F<Integer, Boolean> cellIndex( final int cell ) {
        return new F<Integer, Boolean>() {
            @Override public Boolean f( Integer i ) {
                return i == cell;
            }
        };
    }

    //Return a copy but with the specified cell toggled
    public Container toggle( final int cell ) {
        if ( !filledCells.exists( cellIndex( cell ) ) ) {
            return new Container( numCells, new HashSet<Integer>( filledCells.toCollection() ) {{add( cell );}} );
        }
        else {
            return new Container( numCells, new HashSet<Integer>( filledCells.toCollection() ) {{remove( cell );}} );
        }
    }

    public Container scale( final int scale ) {
        return new Container( numCells * scale, List.iterableList( new ArrayList<Integer>() {{
            for ( Integer filledCell : filledCells ) {
                for ( int i = 0; i < scale; i++ ) {
                    add( filledCell * scale + i );
                }
            }
        }} ) );
    }
}