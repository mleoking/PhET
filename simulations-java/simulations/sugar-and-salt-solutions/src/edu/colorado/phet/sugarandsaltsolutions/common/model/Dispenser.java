// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

/**
 * Enum pattern for Salt and Sugar dispensers, to keep track of which one the user is using.
 *
 * @author Sam Reid
 */
public class Dispenser {
    public static Dispenser SALT = new Dispenser();
    public static Dispenser SUGAR = new Dispenser();

    //Enum pattern, so no other instances should be created
    private Dispenser() {
    }
}
