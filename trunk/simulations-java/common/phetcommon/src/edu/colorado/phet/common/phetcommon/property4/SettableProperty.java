// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.property4;

/**
 * A property that is publicly gettable and settable.
 * If the value is changed, listeners are notified.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SettableProperty<T> extends GettableProperty<T> {

    public SettableProperty( T value ) {
        super( value );
    }

    public void setValue( T value ) {
        super.setValue( value );
    }

    public static void main( String[] args ) {
        GettableProperty<Integer> age = new GettableProperty<Integer>( 40 ) {{
            addListener( new PropertyChangeListener<Integer>() {
                public void propertyChanged( PropertyChangeEvent<Integer> event ) {
                    System.out.println( "age: " + event.toString() );
                }
            } );
        }};
        assert ( age.getValue() == 40 );
        age.setValue( 45 );
        assert ( age.getValue() == 45 );
        age.setValue( 50 );
        assert ( age.getValue() == 50 );
    }
}
