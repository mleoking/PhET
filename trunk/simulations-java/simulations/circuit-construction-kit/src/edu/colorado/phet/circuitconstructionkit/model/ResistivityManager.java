// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model;

import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.circuitconstructionkit.model.components.Wire;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * User: Sam Reid
 * Date: Sep 12, 2006
 * Time: 11:56:28 PM
 */
public class ResistivityManager extends CircuitListenerAdapter {
    private Circuit circuit;
    public static final double DEFAULT_RESISTIVITY = 1E-4;//previously 1E-8, see #2241
    public Property<Double> resistivity = new Property<Double>( DEFAULT_RESISTIVITY );
    private Property<Boolean> enabled = new Property<Boolean>( true );

    public ResistivityManager( Circuit circuit ) {
        this.circuit = circuit;
    }

    public Circuit getCircuit() {
        return circuit;
    }

    public void junctionsMoved() {
        changed();
    }

    private void changed() {
        if ( enabled.get() ) {
            for ( int i = 0; i < getCircuit().numBranches(); i++ ) {
                Branch b = getCircuit().branchAt( i );
                if ( b instanceof Wire ) {//make sure it's not a component.
                    double resistance = getResistance( b );
                    b.setResistance( resistance );
                }
            }
        }
    }

    private double getResistance( Branch b ) {
        double length = b.getLength();
        double resistance = length * resistivity.get();
        if ( resistance < CCKModel.MIN_RESISTANCE ) {
            return CCKModel.MIN_RESISTANCE;
        }
        else {
            return resistance;
        }
    }

    public boolean isEnabled() {
        return enabled.get();
    }

    public void setEnabled( boolean enabled ) {
        this.enabled.set( enabled );
        if ( enabled ) {
            changed();
        }
    }

    public double getResistivity() {
        return resistivity.get();
    }

    public void setResistivity( double resistivity ) {
//        System.out.println( "resistivity = " + resistivity );
        if ( this.resistivity.get() != resistivity ) {
            this.resistivity.set( resistivity );
            changed();
        }
    }

    public void reset() {
        enabled.reset();
        resistivity.reset();

        //Values may not have changed, but call "changed()" just in case
        changed();
    }
}
