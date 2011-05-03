// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property3;

import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

/**
 * Returns a boolean AND over its arguments, and signifying changes when its value changes.  This provides read-only access to the computed value.
 *
 * @author Sam Reid
 */
public class And
        <T extends Gettable<Boolean> & Observable0>//parents are gettable and observable but not settable
        extends OperatorBoolean<T> {

    public And( T a, T b ) {
        super( a, b, new Function2<Boolean, Boolean, Boolean>() {
            public Boolean apply( Boolean a, Boolean b ) {
                return a && b;
            }
        } );
    }

    //Test that demonstrates usage of And
    public static void main( String[] args ) {
        Property<Boolean> visible = new Property<Boolean>( true );
        Property<Boolean> selected = new Property<Boolean>( false );
        final And<Property<Boolean>> and = new And<Property<Boolean>>( visible, selected );
        and.addObserver( new VoidFunction0() {
            public void apply() {
                System.out.println( "and value changed: " + and.get() );
            }
        } );
        selected.set( true );
        selected.reset();
    }
}
