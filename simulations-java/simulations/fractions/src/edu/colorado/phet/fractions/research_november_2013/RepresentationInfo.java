// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.fractions.research_november_2013;

import java.util.ArrayList;

import edu.colorado.phet.fractions.common.math.Fraction;

/**
 * State for each representation
 */
public class RepresentationInfo {
    ArrayList<Fraction> fractions = new ArrayList<Fraction>();

    public void fractionChanged( Integer numerator, Integer denominator ) {
        fractions.add( new Fraction( numerator, denominator ) );
    }

    @Override public String toString() {
        return fractions.toString();
    }
}