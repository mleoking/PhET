// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model.shapes;

import lombok.Data;

import edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction;

/**
 * Target the user tries to create when creating pictures to match a given numeric representation.
 * Not sure this warrants a class, but created for symmetry with NumberTarget and in case more information is added.
 *
 * @author Sam Reid
 */
public @Data class ShapeTarget {
    public final Fraction fraction;
}