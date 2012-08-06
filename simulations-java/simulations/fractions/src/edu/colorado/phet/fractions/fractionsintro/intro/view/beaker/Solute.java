// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.intro.view.beaker;

import java.awt.Color;

/**
 * Taken from the Dilutions sim
 * Model of a solute, an immutable data structure.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Solute {

    public final double saturatedConcentration; // M
    public final Color solutionColor; // the solute's color in a fully-saturated solution

    // For most solutes, the color of the precipitate is the same as the color in solution.
    public Solute( String name, String formula, double maxConcentration, Color solutionColor, double precipitateSize, int precipitateParticlesPerMole ) {
        this( name, formula, maxConcentration, solutionColor, solutionColor, precipitateSize, precipitateParticlesPerMole );
    }

    private Solute( String name, String formula, double saturatedConcentration, Color solutionColor, Color precipitateColor, double precipitateSize, int precipitateParticlesPerMole ) {
        this.saturatedConcentration = saturatedConcentration;
        this.solutionColor = solutionColor;
    }
}
