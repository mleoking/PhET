// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

/**
 * Enum pattern for Salt and Sugar dispensers, to keep track of which one the user is using.
 *
 * @author Sam Reid
 */
public class DispenserType {
    public static DispenserType SALT = new DispenserType( 11, 17 );
    public static DispenserType SUGAR = new DispenserType( 6, 1, 8 );

    //List of elements comprising the solute
    private final Integer[] elements;

    //Enum pattern, so no other instances should be created
    private DispenserType( Integer... elements ) {
        this.elements = elements;
    }

    public Integer[] getElementAtomicMasses() {
        return elements;
    }
}
