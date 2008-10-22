package edu.colorado.phet.common.motion.model;

import java.util.ArrayList;

/**
 * Author: Sam Reid
 * Jun 26, 2007, 12:20:50 PM
 */
public class DefaultVariable implements IVariable {
    private double value;
    private ArrayList listeners = new ArrayList();

    public DefaultVariable( double value ) {
        this.value = value;
    }

    public void setValue( double value ) {
        if ( Double.isNaN( value ) ) {
            throw new IllegalArgumentException( "NaN" );
        }
        if ( !equals( value, this.value ) ) {
            this.value = value;
            for ( int i = 0; i < listeners.size(); i++ ) {
                ( (IVariable.Listener) listeners.get( i ) ).valueChanged();
            }
        }
    }

    //account for NaN
    private boolean equals( double a, double b ) {
        return a == b || ( Double.isNaN( a ) && Double.isNaN( b ) );
    }

    public void addListener( IVariable.Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( IVariable.Listener listener ) {
        listeners.remove( listener );
    }

    public double getValue() {
        return value;
    }

    public String toString() {
        return value + "";
    }
}
