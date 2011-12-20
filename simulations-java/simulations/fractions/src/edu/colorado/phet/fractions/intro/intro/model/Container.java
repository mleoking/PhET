// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.model;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Model for one container which holds some number of slices.
 * Do not mutate, other code relies on this class being immutable.
 *
 * @author Sam Reid
 */
public class Container {
    public final int numCells;
    public final ArrayList<Integer> filledCells;

    Container( int numCells, int[] filledCells ) {
        this( numCells, toList( filledCells ) );
    }

    private static ArrayList<Integer> toList( int[] filledCells ) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for ( int cell : filledCells ) {
            list.add( cell );
        }
        return list;
    }

    @Override public String toString() {
        return filledCells.toString();
    }

    Container( int numCells, ArrayList<Integer> filledCells ) {
        this.numCells = numCells;
        this.filledCells = filledCells;
    }

    public static Container full( final int denominator ) {
        return new Container( denominator, new ArrayList<Integer>() {{
            for ( int i = 0; i < denominator; i++ ) {
                add( i );
            }
        }} );
    }

    public boolean isFull() {
        return new HashSet<Integer>( filledCells ).equals( new HashSet<Integer>( RandomFill.listAll( numCells ) ) );
    }

    public Container addRandom() {
        if ( isFull() ) {
            throw new RuntimeException( "tried to add to full container" );
        }
        final HashSet<Integer> empty = new HashSet<Integer>( RandomFill.listAll( numCells ) ) {{
            removeAll( filledCells );
        }};
        final ArrayList<Integer> listOfEmptyCells = new ArrayList<Integer>( empty );
        ArrayList<Integer> newFilledCells = new ArrayList<Integer>( filledCells ) {{
            final int randomIndex = RandomFill.random.nextInt( empty.size() );
            add( listOfEmptyCells.get( randomIndex ) );
        }};
        return new Container( numCells, newFilledCells );
    }

    public Boolean isEmpty( int cell ) {
        return filledCells.contains( cell );
    }

    //Return a copy but with the specified cell toggled
    public Container toggle( int cell ) {
        return null;
    }
}