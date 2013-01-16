// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;

/**
 * For randomly choosing generically-typed values from lists or lists-of-lists.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class RandomChooser<T> {

    protected final Random random;

    public RandomChooser( Random random ) {
        this.random = random;
    }

    // Chooses a value from a list, removes the value from the list.
    public T choose( ArrayList<T> list ) {
        int index = randomIndex( list );
        final T value = list.get( index );
        list.remove( index );
        return value;
    }

    // Chooses a value from a list of lists, removes it from the list in which it was found.
    public T chooseFromLists( ArrayList<ArrayList<T>> lists ) {
        return chooseFromLists( lists, ChallengeFactory.rangeToList( new IntegerRange( 0, lists.size() - 1 ) ) );
    }

    /**
     * Chooses a value from a list of lists, removes it from the list in which it was found, removes the list from the listIndices.
     * Use this if you want to exclude sub-lists from further consideration.
     *
     * @param lists lists from which the value may be chosen
     * @param listIndices indices of the lists that will be considered when choosing a value
     */
    public T chooseFromLists( ArrayList<ArrayList<T>> lists, ArrayList<Integer> listIndices ) {
        int index = randomIndex( listIndices );
        ArrayList<T> bin = lists.get( listIndices.get( index ) );
        listIndices.remove( index );
        return choose( bin );
    }

    // Gets a random index for a specified list.
    private int randomIndex( List list ) {
        return random.nextInt( list.size() );
    }
}
