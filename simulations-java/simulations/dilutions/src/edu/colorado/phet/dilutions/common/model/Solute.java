// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.common.model;

import java.awt.Color;

/**
 * Model of a solute.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Solute {

    public final String name;
    public final String formula;
    public final double saturatedConcentration; // M
    public final Color solutionColor;
    public final Color precipitateColor;
    public final double precipitateScale; // how much to scale the precipitate particles in the view
    public final int precipitateParticlesMultiplier; // multiply number of moles of precipitate by this number to arrive at number of particles to render

    // For most solutes, the color of the precipitate is the same as the color in solution.
    public Solute( String name, String formula, double maxConcentration, Color solutionColor, double precipitateScale, int precipitateParticlesMultiplier ) {
        this( name, formula, maxConcentration, solutionColor, solutionColor, precipitateScale, precipitateParticlesMultiplier );
    }

    public Solute( String name, String formula, double saturatedConcentration, Color solutionColor, Color precipitateColor, double precipitateScale, int precipitateParticlesMultiplier ) {
        this.name = name;
        this.formula = formula;
        this.saturatedConcentration = saturatedConcentration;
        this.solutionColor = solutionColor;
        this.precipitateColor = precipitateColor;
        this.precipitateScale = precipitateScale;
        this.precipitateParticlesMultiplier = precipitateParticlesMultiplier;
    }
}
