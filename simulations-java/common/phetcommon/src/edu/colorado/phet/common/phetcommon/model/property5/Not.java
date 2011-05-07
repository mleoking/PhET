// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property5;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Provides the negation of a Property<Boolean>
 *
 * @author Sam Reid
 */
public class Not extends SettableProperty<Boolean> {
    private Property<Boolean> parent;
    private Boolean lastValue;

    public Not( final Property<Boolean> parent ) {
        this.parent = parent;
        lastValue = getValue();
        parent.addObserver( new SimpleObserver() {
            public void update() {
                notifyObservers( getValue(), lastValue );
                lastValue = getValue();
            }
        } );
    }

    public And and( SettableProperty<Boolean> p ) {
        return new And( this, p );
    }

    @Override
    public Boolean getValue() {
        return !parent.getValue();
    }

    public void setValue( Boolean value ) {
        parent.setValue( !value );//we'll observe the change in the constructor listener, and fire notifications.
    }

    public static Not not( Property<Boolean> p ) {
        return new Not( p );
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
        a.setValue( false );


        System.out.println( "Setting a to true" );
        a.setValue( true );
    }
}
