// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionmatcher.model;

import lombok.Data;

/**
 * Unique ID field for MovableFractions so that nodes can follow the same model elements (even though the instances may change in each time step).
 * Lombok used for equality test and hash code generation.
 *
 * @author Sam Reid
 */
public @Data class MovableFractionID {

    //Unique int value for each ID
    public final int value;

    private static int count = 0;

    public MovableFractionID() { value = count++; }

    public String toString() {
        return value + "";
    }
}