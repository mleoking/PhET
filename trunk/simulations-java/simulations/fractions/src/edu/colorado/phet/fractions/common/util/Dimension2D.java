// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.common.util;

import lombok.Data;

import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;

//REVIEW: Strikes me as risky and potentially confusing to use the name "Dimension2D".  Can something else be used?  FDimension or something?

/**
 * Immutable class for a dimension (width and height).  Uses Lombok to generate equals, hash code.
 *
 * @author Sam Reid
 */
public @Data class Dimension2D {
    public final double width;
    public final double height;

    public Dimension2DDouble toDimension2DDouble() {
        return new Dimension2DDouble( width, height );
    }
}