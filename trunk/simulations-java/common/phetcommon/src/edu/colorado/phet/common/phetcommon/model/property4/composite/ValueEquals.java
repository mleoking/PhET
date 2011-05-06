// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property4.composite;


import edu.colorado.phet.common.phetcommon.model.property4.*;

/**
 * Keeps track of whether a property has a specified value.
 * </p>
 * This demonstrates an alternative way to implement CompositeFunction.apply.
 * Instead of accessing function args via CompositeFunction.getArgs, args can be
 * passed to the function's apply method via final constructor parameters.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ValueEquals<T> extends CompositeProperty<Boolean> {

    public ValueEquals( final GettableProperty<T> property, final T value ) {
        super( new CompositeFunction<Boolean>( property ) {
            @Override public Boolean apply() {
                if ( property.getValue() == null ) {
                    return property.getValue() == value;
                }
                else {
                    return property.getValue().equals( value );
                }
            }
        } );
    }

    public static void main( String[] args ) {
        SettableProperty<Integer> age = new SettableProperty<Integer>( 20 );
        ValueEquals<Integer> isAtTopOfTheHill = new ValueEquals<Integer>( age, 30 ) {{
            addListener( new PropertyChangeListener<Integer>() {
                public void propertyChanged( PropertyChangeEvent<Integer> event ) {
                    System.out.println( "isAtTopOfTheHill: " + event.toString() );
                }
            } );
        }};
        age.setValue( 29 );
        assert ( isAtTopOfTheHill.getValue() == false );
        age.setValue( 30 );
        assert ( isAtTopOfTheHill.getValue() == true );
        age.setValue( 40 );
        assert ( isAtTopOfTheHill.getValue() == true );
    }
}
