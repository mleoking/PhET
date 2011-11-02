// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.util;

import java.util.ArrayList;
import java.util.Collection;
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

    // Negate a boolean function
    public static <T> Function1<T, Boolean> negate( final Function1<T, Boolean> predicate ) {
        return new Function1<T, Boolean>() {
            public Boolean apply( T t ) {
                return !predicate.apply( t );
            }
        };
    }

    // Flatten (concatenate) collections together into a list
    public static <T> List<T> flatten( Collection<? extends T>... collections ) {
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
}
