// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.intro.view.beaker;

import lombok.Data;

import java.awt.Color;

/**
 * Taken from the Dilutions sim
 * Model of a solute, an immutable data structure.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public @Data class Solute {

    // For most solutes, the color of the precipitate is the same as the color in solution.
    public final double saturatedConcentration; // M
    public final Color solutionColor; // the solute's color in a fully-saturated solution
}
