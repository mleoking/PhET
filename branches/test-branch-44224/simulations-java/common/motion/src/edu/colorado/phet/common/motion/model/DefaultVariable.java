package edu.colorado.phet.common.motion.model;

import java.util.ArrayList;

/**
 * Author: Sam Reid
 * Jun 26, 2007, 12:20:50 PM
 */
public class DefaultVariable implements IVariable {
    private double value;
    private ArrayList<Listener> listeners = new ArrayList<Listener>();

    public DefaultVariable( double value ) {
        this.value = value;
    }

    public void setValue( double value ) {
        if ( Double.isNaN( value ) ) {
            throw new IllegalArgumentException( "NaN" );
        }
        if ( !equals( value, this.value ) ) {
            this.value = value;
            for (Listener listener : listeners) {
                listener.valueChanged();
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
