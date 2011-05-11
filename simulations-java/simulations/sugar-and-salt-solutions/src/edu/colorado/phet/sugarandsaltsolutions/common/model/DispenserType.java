// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

/**
 * Enum pattern for Salt and Sugar dispensers, to keep track of which one the user is using.
 *
 * @author Sam Reid
 */
public class DispenserType {
    public static DispenserType SALT = new DispenserType();
    public static DispenserType SUGAR = new DispenserType();

    //Enum pattern, so no other instances should be created
    private DispenserType() {
    }
}
