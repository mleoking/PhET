// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property3;

import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Returns a boolean AND over its arguments, and signifying changes when its value changes.  This provides read-only access to the computed value.
 *
 * @author Sam Reid
 */
//TODO not general, limited to 2 booleans
public class And extends OperatorBoolean {

    public And( GettableObservable0<Boolean> a, GettableObservable0<Boolean> b ) {
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
        final And and = new And( visible, selected );
        and.addObserver( new VoidFunction0() {
            public void apply() {
                System.out.println( "and value changed: " + and.get() );
            }
        } );
        selected.set( true );
        selected.reset();

        //Convert to a NewProperty so that we can attach a listener with a callback value
        and.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean o ) {
                System.out.println( "o = " + o );
            }
        } );
        visible.set( true );
        selected.set( true );
    }

}
