package edu.colorado.phet.sugarandsaltsolutions.micro.model;

/**
 * Type of bond, for a square lattice it can be up, down, left or right.
 *
 * @author Sam Reid
 */
public abstract class BondType {
    public static final BondType UP = new BondType( "up" ) {
        @Override public BondType reverse() {
            return DOWN;
        }
    };
    public static final BondType DOWN = new BondType( "down" ) {
        @Override public BondType reverse() {
            return UP;
        }
    };
    public static final BondType LEFT = new BondType( "left" ) {
        @Override public BondType reverse() {
            return RIGHT;
        }
    };
    public static final BondType RIGHT = new BondType( "right" ) {
        @Override public BondType reverse() {
            return LEFT;
        }
    };
    private String name;

    public BondType( String name ) {
        this.name = name;
    }

    @Override public String toString() {
        return name;
    }

    public abstract BondType reverse();
}
