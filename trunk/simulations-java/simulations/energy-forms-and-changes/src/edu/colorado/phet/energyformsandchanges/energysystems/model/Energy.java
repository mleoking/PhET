// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import edu.colorado.phet.energyformsandchanges.common.model.EnergyType;

/**
 * Class the represents an amount and type of energy as well as other
 * attributes that are specific to the energy type.
 *
 * @author John Blanco
 */
public class Energy {

    public final EnergyType type;
    public final double amount; // In Joules.
    public final double direction; // In radians.  Only relevant for some energy types.  Zero is to the right, pi/2 is up, and so forth.

    /**
     * Constructor for cases where direction is meaningless.
     *
     * @param type   Energy type.
     * @param amount Amount of energy, in joules.
     */
    public Energy( EnergyType type, double amount ) {
        this( type, amount, 0 );

    }

    /**
     * Main constructor.
     *
     * @param type      Energy type.
     * @param amount    Amount of energy, in joules.
     * @param direction Direction of energy, in radians.  Not meaningful for
     *                  all energy types.  Zero indicates to the right, PI/2
     *                  is up, and so forth.
     */
    public Energy( EnergyType type, double amount, double direction ) {
        this.type = type;
        this.amount = amount;
        this.direction = direction;
    }

}
