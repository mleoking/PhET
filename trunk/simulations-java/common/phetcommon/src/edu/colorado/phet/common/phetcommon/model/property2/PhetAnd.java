// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property2;

/**
 * @author Sam Reid
 */
public class PhetAnd extends PhetObservable<Boolean> {
    public PhetAnd( final PhetObservable<Boolean> a, final PhetObservable<Boolean> b ) {
        super( a.getValue() && b.getValue() );
        new PhetObserver<Boolean>() {
            public void update( UpdateEvent<Boolean> event ) {
                setValue( a.getValue() && b.getValue() );
            }
        }.observe( a, b );
    }

    public static void main( String[] args ) {
        PhetObservable<Boolean> a = new PhetObservable<Boolean>( true );
        a.addObserver( new PhetObserver<Boolean>() {
            public void update( UpdateEvent<Boolean> event ) {
                System.out.println( "a changed: " + event );
            }
        } );
        PhetObservable<Boolean> b = new PhetObservable<Boolean>( false );

        final PhetAnd phetAndProperty = new PhetAnd( a, b );
        phetAndProperty.addObserver( new PhetObserver<Boolean>() {
            public void update( UpdateEvent<Boolean> event ) {
                System.out.println( "event = " + event );
            }
        } );

        a.setValue( true );
        b.setValue( true );

        a.setValue( false );
    }
}