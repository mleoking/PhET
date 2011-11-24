// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents an immutable permutation, that can "permute" an ordered collection.
 * TODO: Can we use generics to say that we implement Function1? Not possible so far to get the proper type preservation
 */
public class Permutation {
    private final int[] indices;

    /**
     * @param size Number of elements
     * @return An identity permutation with a specific number of elements
     */
    public static Permutation identity( int size ) {
        assert ( size >= 0 );
        int[] indices = new int[size];
        for ( int i = 0; i < size; i++ ) {
            indices[i] = i;
        }
        return new Permutation( indices );
    }

    /**
     * Creates a permutation that will rearrange a list so that newList[i] = oldList[permutation[i]]
     */
    public Permutation( int[] permutation ) {
        this.indices = permutation;
    }

    public Permutation( List<Integer> permutation ) {
        indices = new int[permutation.size()];
        for ( int i = 0; i < permutation.size(); i++ ) {
            indices[i] = permutation.get( i );
        }
    }

    /**
     * Permute a list of objects, and return the result.
     */
    public <T> List<T> apply( List<T> objects ) {
        if ( objects.size() != indices.length ) {
            throw new IllegalArgumentException( "Permutation length " + size() + " not equal to list length " + objects.size() );
        }
        List<T> result = new ArrayList<T>( objects.size() );
        for ( int i = 0; i < objects.size(); i++ ) {
            result.add( objects.get( indices[i] ) );
        }
        return result;
    }

    /**
     * Permute a single index
     */
    public int apply( int index ) {
        return indices[index];
    }

    public int size() {
        return indices.length;
    }

    /**
     * @return The inverse of this permutation
     */
    public Permutation inverted() {
        int[] newPermutation = new int[size()];
        for ( int i = 0; i < size(); i++ ) {
            newPermutation[indices[i]] = i;
        }
        return new Permutation( newPermutation );
    }

    @Override public String toString() {
        return Arrays.toString( indices );
    }

    public static void main( String[] args ) {
        Permutation a = new Permutation( new int[] { 1, 4, 3, 2, 0 } );
        System.out.println( a );

        Permutation b = a.inverted();
        System.out.println( b );
    }
}
