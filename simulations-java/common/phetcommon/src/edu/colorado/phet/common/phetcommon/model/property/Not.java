// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Provides the negation of a Property<Boolean>--this one is settable and propagates the (negation) of the set value back to its parent.
 *
 * @author Sam Reid
 */
public class Not extends SettableProperty<Boolean> {
    private SettableProperty<Boolean> parent;

    public Not( final SettableProperty<Boolean> parent ) {
        super( !parent.get() );
        this.parent = parent;
    }

    public void set( Boolean value ) {
        parent.set( !value );//we'll observe the change in the constructor listener, and fire notifications.
    }

    public And and( SettableProperty<Boolean> term ){
        return new And(this,term);
    }

    public static Not not( SettableProperty<Boolean> p ) {
        return new Not( p );
    }

    @Override public Boolean get() {
        return !parent.get();
    }

    public static void main( String[] args ) {
        BooleanProperty a = new BooleanProperty( true );
        a.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean aBoolean ) {
                System.out.println( "a = " + aBoolean );
            }
        } );
        Not b = new Not( a );
        b.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean aBoolean ) {
                System.out.println( "b = " + aBoolean );
            }
        } );
        System.out.println( "Setting a to false:" );
        a.set( false );

        System.out.println( "Setting a to true" );
        a.set( true );
    }
}
