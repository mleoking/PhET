// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property2;

import edu.colorado.phet.common.phetcommon.util.function.Function2;

/**
 * @author Sam Reid
 */
public class And extends BinaryBooleanProperty {
    public And( final Observable<Boolean> a, final Observable<Boolean> b ) {
        super( a, b, new Function2<Boolean, Boolean, Boolean>() {
            public Boolean apply( Boolean a, Boolean b ) {
                return a && b;
            }
        } );
    }

    public static void main( String[] args ) {
        Observable<Boolean> a = new Observable<Boolean>( true );
        a.addObserver( new Observer<Boolean>() {
            public void update( UpdateEvent<Boolean> event ) {
                System.out.println( "a changed: " + event );
            }
        } );
        Observable<Boolean> b = new Observable<Boolean>( false );

        final And phetAndProperty = new And( a, b );
        phetAndProperty.addObserver( new Observer<Boolean>() {
            public void update( UpdateEvent<Boolean> event ) {
                System.out.println( "event = " + event );
            }
        } );

        a.setValue( true );
        b.setValue( true );

        a.setValue( false );
    }
}