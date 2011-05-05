// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.property4.types;

import edu.colorado.phet.common.phetcommon.property4.*;

/**
 * Logical "or" of a variable number of boolean properties.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Or extends CompositeProperty<Boolean> {

    public Or( GettableProperty<Boolean>... args ) {
        super( new CompositeFunction<Boolean>( args ) {
            public Boolean apply() {
                boolean b = false;
                for ( GettableProperty<Boolean> arg : getArgs() ) {
                    b = b || arg.getValue();
                    if ( b ) {
                        break;
                    }
                }
                return b;
            }
        } );
    }

    public static void main( String[] args ) {
        SettableProperty<Boolean> isSaturday = new SettableProperty<Boolean>( false );
        SettableProperty<Boolean> isSunday = new SettableProperty<Boolean>( false );
        Or isWeekend = new Or( isSaturday, isSunday ) {{
            addListener( new PropertyChangeListener<Integer>() {
                public void propertyChanged( PropertyChangeEvent<Integer> event ) {
                    System.out.println( "isWeekend: " + event.toString() );
                }
            } );
        }};
        assert ( isWeekend.getValue() == false );
        isSaturday.setValue( true );
        assert ( isWeekend.getValue() == true );
        isSunday.setValue( true );
        assert ( isWeekend.getValue() == true );
    }
}
