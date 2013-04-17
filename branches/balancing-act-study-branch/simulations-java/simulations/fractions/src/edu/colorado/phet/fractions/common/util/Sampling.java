// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.common.util;

import fj.P2;
import fj.data.List;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static fj.Equal.intEqual;
import static fj.data.List.iterableList;
import static fj.data.List.range;

/**
 * Methods that allow sampling with uniform probability without replacement from different lists.
 *
 * @author Sam Reid
 */
public class Sampling {

    private static final Random random = new Random();

    public static <T> T chooseOneWithSeed( long seed, final List<T> list ) {
        Random random = new Random( seed );
        return list.index( random.nextInt( list.length() ) );
    }

    public static <T> T chooseOne( final List<T> list ) {
        return list.index( random.nextInt( list.length() ) );
    }

    //Chooses 2 different values (i.e. without replacement)
    public static <T> P2<T, T> chooseTwo( final List<T> list ) {
        final List<Integer> indices = range( 0, list.length() );
        int firstIndexToChoose = chooseOne( indices );
        int secondIndexToChoose = chooseOne( indices.delete( firstIndexToChoose, intEqual ) );
        return new DefaultP2<T, T>( list.index( firstIndexToChoose ), list.index( secondIndexToChoose ) );
    }

    //Select the specified number without replacement
    public static <T> List<T> choose( int num, final List<T> list ) {
        ArrayList<T> mutableList = new ArrayList<T>( list.toCollection() );
        Collections.shuffle( mutableList );
        while ( mutableList.size() > num ) {
            mutableList.remove( 0 );
        }
        return iterableList( mutableList );
    }
}