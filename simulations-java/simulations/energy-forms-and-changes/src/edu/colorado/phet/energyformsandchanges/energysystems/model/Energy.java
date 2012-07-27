// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

/**
 * Class the represents an amount and type of energy as well as other
 * attributes that are specific to the energy type.
 *
 * @author John Blanco
 */
public class Energy {

    public final Type type;
    public final double amount; // In Joules.
    public final double direction; // In radians.  Only relevant for some energy types.  Zero is to the right, pi/2 is up, and so forth.

    /**
     * Constructor for cases where direction is meaningless.
     *
     * @param type   Energy type.
     * @param amount Amount of energy, in joules.
     */
    public Energy( Type type, double amount ) {
        this( type, amount, 0 );

    }

    /**
     * Main constructor.
     *
     * @param type      Energy type.
     * @param amount    Amount of energy, in joules.
     * @param direction Direction of energy.  Not meaningful for all energy types.
     */
    public Energy( Type type, double amount, double direction ) {
        this.type = type;
        this.amount = amount;
        this.direction = direction;
    }

    enum Type {THERMAL, ELECTRICAL, MECHANICAL, SOLAR}
}
