package edu.colorado.phet.common.phetcommon.model;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * This adapter class converts an enumeration property to a boolean property indicating true if the specified element is selected.
 * It can be used, e.g. to map the enumeration property values into boolean radio button handlers.
 *
 * @param <T> the property value type
 * @author Sam Reid
 */
public class IsSelected<T> extends BooleanProperty {
    public IsSelected( final T a, final Property<T> p ) {
        super( p.getValue() == a );
        p.addObserver( new SimpleObserver() {
            public void update() {
                setValue( p.getValue().equals( a ) );
            }
        } );
        addObserver( new SimpleObserver() {
            public void update() {
                if ( getValue() ) {
                    p.setValue( a );
                }
            }
        } );
    }

    public static <T> BooleanProperty isSelected( T a, Property<T> p ) {
        return new IsSelected<T>( a, p );
    }
}
