/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/view/util/FractionSpring.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:03 $
 */
package edu.colorado.phet.common.view.util;

import javax.swing.*;

/**
 * FractionSpring
 *
 * @author ?
 * @version $Revision: 1.1.1.1 $
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

