package edu.colorado.phet.cck3.model;

import edu.colorado.phet.cck3.model.components.Branch;

/**
 * User: Sam Reid
 * Date: Sep 12, 2006
 * Time: 11:56:28 PM
 * Copyright (c) Sep 12, 2006 by Sam Reid
 */
public class ResistivityManager extends CircuitListenerAdapter {
    private Circuit circuit;
    public static final double DEFAULT_RESISTIVITY = CCKModel.MIN_RESISTANCE;
    private double resistivity = DEFAULT_RESISTIVITY;
    private boolean enabled = true;

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
        if( enabled ) {
            for( int i = 0; i < getCircuit().numBranches(); i++ ) {
                Branch b = getCircuit().branchAt( i );
                if( b.getClass().equals( Branch.class ) ) {//make sure it's not a component.
                    double resistance = getResistance( b );
                    b.setResistance( resistance );
                }
            }
        }
    }

    private double getResistance( Branch b ) {
        double length = b.getLength();
        double resistance = length * resistivity;
        if( resistance < CCKModel.MIN_RESISTANCE ) {
            return CCKModel.MIN_RESISTANCE;
        }
        else {
            return resistance;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled( boolean enabled ) {
        this.enabled = enabled;
        if( enabled ) {
            changed();
        }
    }

    public double getResistivity() {
        return resistivity;
    }

    public void setResistivity( double resistivity ) {
        if( this.resistivity != resistivity ) {
            this.resistivity = resistivity;
            changed();
        }
    }
}
