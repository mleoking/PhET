/**
 * Class: FractionSpring
 * Package: edu.colorado.phet.coreadditions.layout
 * Author: Another Guy
 * Date: Jul 14, 2004
 */
package edu.colorado.phet.radiowaves.coreadditions;

import javax.swing.*;

/**
 * An extension of javax.swing.Spring, for use in layouts.
 */
public class FractionSpring extends Spring {

    protected Spring parent;

    protected double fraction;

    public FractionSpring( Spring p, double f ) {
        if( p == null ) {
            throw new NullPointerException( "Parent spring cannot be null" );
        }
        parent = p;
        fraction = f;
    }

    public int getValue() {
        return (int)Math.round( parent.getValue() * fraction );
    }

    public int getPreferredValue() {
        return (int)Math.round( parent.getPreferredValue() * fraction );
    }

    public int getMinimumValue() {
        return (int)Math.round( parent.getMinimumValue() * fraction );
    }

    public int getMaximumValue() {
        return (int)Math.round( parent.getMaximumValue() * fraction );
    }

    public void setValue( int val ) {
        // Uncomment this next line to watch when our spring is resized:
        // System.err.println("Value to setValue: " + val);
        if( val == UNSET ) {
            return;
        }
        throw new UnsupportedOperationException( "Cannot set value on a derived spring" );
    }

    public static FractionSpring half( Spring s ) {
        return new FractionSpring( s, 0.5 );
    }
}

