// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property4.composite;

import edu.colorado.phet.common.phetcommon.model.property4.*;

/**
 * Logical "and" of a variable number of boolean properties.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class And extends CompositeProperty<Boolean> {

    public And( GettableProperty<Boolean>... args ) {
        super( new CompositeFunction<Boolean>( args ) {
            public Boolean apply() {
                boolean b = true;
                for ( GettableProperty<Boolean> arg : getArgs() ) {
                    b = b && arg.getValue();
                }
                return b;
            }
        } );
    }

    public static void main( String[] args ) {
        SettableProperty<Boolean> isMoving = new SettableProperty<Boolean>( false );
        SettableProperty<Boolean> isBreathing = new SettableProperty<Boolean>( false );
        And isAlive = new And( isMoving, isBreathing ) {{
            addListener( new PropertyChangeListener<Integer>() {
                public void propertyChanged( PropertyChangeEvent<Integer> event ) {
                    System.out.println( "isAlive: " + event.toString() );
                }
            } );
        }};
        isMoving.setValue( true );
        isBreathing.setValue( true );
    }
}
