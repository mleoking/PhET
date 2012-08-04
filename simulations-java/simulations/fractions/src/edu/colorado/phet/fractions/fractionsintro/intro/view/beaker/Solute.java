// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.intro.view.beaker;

import java.awt.Color;

/**
 * Model of a solute, an immutable data structure.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Solute {

    private final String name;
    private final String formula;
    public final double saturatedConcentration; // M
    public final Color solutionColor; // the solute's color in a fully-saturated solution
    private final Color precipitateColor; // the solute's color as a precipitate
    private final double precipitateSize; // size of the precipitate particles in view coordinates
    private final int precipitateParticlesPerMole; // number of precipitate particles to show per mol of saturation

    // For most solutes, the color of the precipitate is the same as the color in solution.
    public Solute( String name, String formula, double maxConcentration, Color solutionColor, double precipitateSize, int precipitateParticlesPerMole ) {
        this( name, formula, maxConcentration, solutionColor, solutionColor, precipitateSize, precipitateParticlesPerMole );
    }

    private Solute( String name, String formula, double saturatedConcentration, Color solutionColor, Color precipitateColor, double precipitateSize, int precipitateParticlesPerMole ) {
        this.name = name;
        this.formula = formula;
        this.saturatedConcentration = saturatedConcentration;
        this.solutionColor = solutionColor;
        this.precipitateColor = precipitateColor;
        this.precipitateSize = precipitateSize;
        this.precipitateParticlesPerMole = precipitateParticlesPerMole;
    }
}
