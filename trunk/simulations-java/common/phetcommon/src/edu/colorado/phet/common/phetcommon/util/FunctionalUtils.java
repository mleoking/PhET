// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

/**
 * Utilities that simplify everyday functional programming.
 *
 * @author Jonathan Olson
 */
public class FunctionalUtils {

    // Returns the first object in the collection that satisfies the predicate, or null otherwise
    public static <T> T firstOrNull( Collection<T> collection, Function1<? super T, Boolean> predicate ) {
        return first( collection, predicate ).getOrElse( null );
    }

    // Returns the first object in the collection that satisfies the predicate, in option form
    public static <T> Option<T> first( Collection<T> collection, Function1<? super T, Boolean> predicate ) {
        for ( T t : collection ) {
            if ( predicate.apply( t ) ) {
                return new Some<T>( t );
            }
        }
        return new None<T>();
    }

    // Returns the last object in the collection that satisfies the predicate (if any), in option form
    public static <T> Option<T> last( Collection<T> collection, Function1<? super T, Boolean> predicate ) {
        final ArrayList<T> reversed = new ArrayList<T>( collection );
        Collections.reverse( reversed );
        return first( reversed, predicate );
    }

    // Returns whether predicate( element ) is true for any element in the collection
    public static <T> boolean exists( Collection<T> collection, Function1<? super T, Boolean> predicate ) {
        return first( collection, predicate ).isSome();
    }

    // Returns whether predicate( element ) is true for every element in the collection
    public static <T> boolean every( Collection<T> collection, Function1<? super T, Boolean> predicate ) {
        return !exists( collection, negate( predicate ) );
    }

    // Returns the number of items in the collection for which the predicate returns true
    public static <T> int count( Collection<T> collection, Function1<? super T, Boolean> predicate ) {
        int result = 0;
        for ( T t : collection ) {
            if ( predicate.apply( t ) ) {
                result++;
            }
        }
        return result;
    }

    // convenience method for clearer counting
    public static int count( Collection<?> collection ) {
        return collection.size();
    }

    // Picks an element out of the collection, with no guaranteed semantics. Useful for sets
    public static <T> T pick( Collection<T> collection ) {
        return collection.iterator().next();
    }

    // Returns a list filtered by the predicate. I.e., the list will contain every element where predicate( element ) == true
    public static <T> List<T> filter( Collection<T> collection, Function1<? super T, Boolean> predicate ) {
        List<T> result = new ArrayList<T>();
        for ( T t : collection ) {
            if ( predicate.apply( t ) ) {
                result.add( t );
            }
        }
        return result;
    }

    // Returns two lists filtered by the predicate. I.e., the first list will contain every element where predicate( element ) == true, and the second list will contain the rest
    public static <T> Pair<List<T>, List<T>> partition( Collection<T> collection, Function1<? super T, Boolean> predicate ) {
        List<T> trueResult = new ArrayList<T>();
        List<T> falseResult = new ArrayList<T>();
        for ( T t : collection ) {
            if ( predicate.apply( t ) ) {
                trueResult.add( t );
            }
            else {
                falseResult.add( t );
            }
        }
        return new Pair<List<T>, List<T>>( trueResult, falseResult );
    }

    // Negate a boolean function
    public static <T> Function1<T, Boolean> negate( final Function1<T, Boolean> predicate ) {
        return new Function1<T, Boolean>() {
            public Boolean apply( T t ) {
                return !predicate.apply( t );
            }
        };
    }

    public static <T> List<T> concat( Collection<? extends T>... collections ) {
        List<T> result = new ArrayList<T>();
        for ( Collection<? extends T> collection : collections ) {
            result.addAll( collection );
        }
        return result;
    }

    // Flatten (concatenate) collections together into a list
    public static <T> List<T> flatten( Collection<? extends Collection<? extends T>> collections ) {
        List<T> result = new ArrayList<T>();
        for ( Collection<? extends T> collection : collections ) {
            result.addAll( collection );
        }
        return result;
    }

    // Generalized Scala-style mkString
    public static <T> String mkString( Collection<T> collection, Function1<? super T, String> toString, String separator ) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for ( T t : collection ) {
            if ( !first ) {
                builder.append( separator );
            }
            first = false;
            builder.append( toString.apply( t ) );
        }
        return builder.toString();
    }

    // Scala-style mkString
    public static <T> String mkString( Collection<T> collection, String separator ) {
        return mkString( collection, new Function1<T, String>() {
            public String apply( T t ) {
                return t.toString();
            }
        }, separator );
    }

    // Standard functional map function
    public static <T, U> List<U> map( Collection<T> collection, Function1<? super T, ? extends U> mapper ) {
        List<U> result = new ArrayList<U>();
        for ( T t : collection ) {
            result.add( mapper.apply( t ) );
        }
        return result;
    }

    // Returns a unique list from a collection, in no particular order
    public static <T> List<T> unique( Collection<T> collection ) {
        Set<T> set = new HashSet<T>();
        for ( T t : collection ) {
            set.add( t );
        }
        return new ArrayList<T>( set );
    }

    // Inverts a comparator, to make sorting backwards a bit easier
    public static <T> Comparator<T> invertComparator( final Comparator<T> comparator ) {
        return new Comparator<T>() {
            public int compare( T a, T b ) {
                return comparator.compare( b, a );
            }
        };
    }

    // Repeats a task (runnable-fork) a certain number of times
    public static void repeat( Runnable runnable, int times ) {
        for ( int i = 0; i < times; i++ ) {
            runnable.run();
        }
    }

    // Returns a list of integers from A to B (including both A to B)
    public static List<Integer> rangeInclusive( int a, int b ) {
        List<Integer> result = new ArrayList<Integer>( b - a + 1 );
        for ( int i = a; i <= b; i++ ) {
            result.add( i );
        }
        return result;
    }

    // a list with all elements of the minuend that are not in the subtrahend
    public static <T> List<T> subtract( Collection<T> minuend, final Collection<? extends T> subtrahend ) {
        return filter( minuend, new Function1<T, Boolean>() {
            public Boolean apply( T item ) {
                return !subtrahend.contains( item );
            }
        } );
    }

    /**
     * A list with all combinations of elements of order "n". items are not repeated in a single combination.
     * NOTE: not spectacularly fast
     * NOTE: relative order of items will be preserved in the returned combinations
     * <p/>
     * EXAMPLE: combinations( Arrays.asList( 1, 4, 5, 8, 12 ), 3 )
     * returns lists of the following in order:
     * 1 4 5
     * 1 4 8
     * 1 4 12
     * 1 5 8
     * 1 5 12
     * 1 8 12
     * 4 5 8
     * 4 5 12
     * 4 8 12
     * 5 8 12
     *
     * @param collection The collection to draw from
     * @param n          How many elements should be in each combination
     * @return List of combinations
     */
    public static <T> List<List<T>> combinations( Collection<T> collection, int n ) {
        // make it a list so we can index it (but don't do the copying if not necessary)
        List<T> list = ( collection instanceof List<?> ) ? ( (List<T>) collection ) : new ArrayList<T>( collection );

        return recursiveCominations( list, new ArrayList<T>(), n, 0 );
    }

    // our combinations will all start with the "candidate" list, and the remaining elements will be filled with elements from "list" at or past "position"
    private static <T> List<List<T>> recursiveCominations( List<T> list, List<T> candidate, int n, int position ) {
        // how many more items to we have to add to our candidate?
        int remainingQuantity = n - candidate.size();

        // base case
        if ( remainingQuantity == 0 ) {
            ArrayList<List<T>> result = new ArrayList<List<T>>();
            result.add( candidate );
            return result;
        }

        List<List<T>> result = new ArrayList<List<T>>();
        for ( int i = position; i <= list.size() - remainingQuantity; i++ ) {
            List<T> nextCandidate = new ArrayList<T>( candidate );
            nextCandidate.add( list.get( i ) );
            result.addAll( recursiveCominations( list, nextCandidate, n, i + 1 ) );
        }
        return result;
    }

    // a faster variant of combinations(), unrolled for pairs of elements
    public static <T> List<Pair<T, T>> pairs( Collection<T> collection ) {
        // make it a list so we can index it (but don't do the copying if not necessary)
        List<T> list = ( collection instanceof List<?> ) ? ( (List<T>) collection ) : new ArrayList<T>( collection );

        List<Pair<T, T>> result = new ArrayList<Pair<T, T>>();
        int size = collection.size();
        for ( int i = 0; i < size - 1; i++ ) {
            T t = list.get( i );
            for ( int j = i + 1; j < size; j++ ) {
                result.add( new Pair<T, T>( t, list.get( j ) ) );
            }
        }
        return result;
    }
}
