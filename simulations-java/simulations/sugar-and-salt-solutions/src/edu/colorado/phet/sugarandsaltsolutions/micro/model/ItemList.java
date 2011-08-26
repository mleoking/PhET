package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.util.ArrayList;
import java.util.Collection;

import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.CompositeDoubleProperty;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Observable list class that can be observed for items added or removed.
 *
 * @author Sam Reid
 */
public class ItemList<T> extends ObservableList<T> {

    //Property that can be used to monitor the number of items in the list.
    //It is typed as Double since that package provides support for composition (through >, +, etc)
    //When support is added for IntegerProperty, this should be switched to use IntegerProperty instead of DoubleProperty
    //TODO: this shouldn't provide a settable interface
    public final DoubleProperty size = new DoubleProperty( 0.0 ) {{
        VoidFunction1<T> listener = new VoidFunction1<T>() {
            public void apply( T t ) {
                set( size() + 0.0 );
            }
        };
        addElementAddedObserver( listener );
        addElementRemovedObserver( listener );
    }};

    //Create an empty list
    public ItemList() {
    }

    //Convenience constructor to construct an ItemList from an array
    public ItemList( T[] elements ) {
        for ( T element : elements ) {
            add( element );
        }
    }

    //Create an ItemList from a Collection
    public ItemList( Collection<T> elements ) {
        for ( T element : elements ) {
            add( element );
        }
    }

    //Count the items in the list that match the predicate
    public int count( Function1<T, Boolean> predicate ) {
        int count = 0;
        for ( T item : this ) {
            if ( predicate.apply( item ) ) {
                count++;
            }
        }
        return count;
    }

    //Determine whether the list contains an item matching the specified predicate
    public boolean contains( Function1<T, Boolean> predicate ) {
        for ( T item : this ) {
            if ( predicate.apply( item ) ) {
                return true;
            }
        }
        return false;
    }

    // return the first matching item, or None if there is none
    public Option<T> find( Function1<T, Boolean> predicate ) {
        ItemList<T> results = filter( predicate );
        if ( results.isEmpty() ) {
            return new None<T>();
        }
        else {
            return new Some<T>( results.get( 0 ) );
        }
    }

    //Count the items in the list that are an instance of the specified class
    public int count( final Class<? extends T>... clazz ) {
        return filter( clazz ).size();
    }

    //Remove all instances that match the specified classes
    public void clear( final Class<? extends T>... clazz ) {
        for ( T item : filter( clazz ) ) {
            remove( item );
        }
    }

    //Collect all items from the list that match the predicate
    public ArrayList<T> filterToArrayList( final Function1<T, Boolean> predicate ) {
        return new ArrayList<T>() {{
            for ( T item : ItemList.this ) {
                if ( predicate.apply( item ) ) {
                    add( item );
                }
            }
        }};
    }

    //Collect all items from the list that match the predicate and return a new ItemList
    public ItemList<T> filter( final Function1<T, Boolean> predicate ) {
        return new ItemList<T>() {{
            for ( T item : ItemList.this ) {
                if ( predicate.apply( item ) ) {
                    add( item );
                }
            }
        }};
    }

    //Determine which items are instances of the specified classes
    public ItemList<T> filter( final Class<? extends T>... clazz ) {
        return filter( new Function1<T, Boolean>() {
            public Boolean apply( T t ) {
                for ( Class<? extends T> aClass : clazz ) {
                    if ( aClass.isInstance( t ) ) {
                        return true;
                    }
                }
                return false;
            }
        } );
    }

    public CompositeDoubleProperty propertyCount( final Class<? extends T> type ) {
        return new CompositeDoubleProperty( new Function0<Double>() {
            public Double apply() {
                return count( type ) + 0.0;
            }
        }, size );
    }

    public ArrayList<T> toList() {
        return new ArrayList<T>( this );
    }

    public <U> ItemList<U> map( final Function1<T, U> map ) {
        return new ItemList<U>() {{
            for ( T t : ItemList.this ) {
                add( map.apply( t ) );
            }
        }};
    }

    //Combine the specified initial value with all elements from the list using the specified combination function
    public <U> U foldLeft( final U initialValue, Function2<T, U, U> combiner ) {
        U runningTotal = initialValue;
        for ( T t : this ) {
            runningTotal = combiner.apply( t, runningTotal );
        }
        return runningTotal;
    }

    public static void main( String[] args ) {
        ItemList<Double> list = new ItemList<Double>( new Double[] { 1.0, 2.0, 3.0, 5.0 } );
        System.out.println( "sum " + list.foldLeft( 0.0, new Function2<Double, Double, Double>() {
            public Double apply( Double a, Double b ) {
                return a + b;
            }
        } ) );
    }
}