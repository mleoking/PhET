// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.molarity.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.util.ColorRange;

/**
 * Model of a solute, an immutable data structure.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Solute {

    public final String name;
    public final String formula;
    public final double saturatedConcentration; // M
    public final ColorRange solutionColor; // color range for a solution with non-zero concentration
    public final Color particleColor; // the solute's color as a particle
    public final double particleSize; // particles are square, this is the length of one side
    public final int particlesPerMole; // number of particles to show per mol of saturation

    // For most solutes, particles have the same color as the saturated solution.
    public Solute( String name, String formula, double maxConcentration, ColorRange solutionColor, double particleSize, int particlesPerMole ) {
        this( name, formula, maxConcentration, solutionColor, solutionColor.getMax(), particleSize, particlesPerMole );
    }

    public Solute( String name, String formula, double saturatedConcentration, ColorRange solutionColor, Color particleColor, double particleSize, int particlesPerMole ) {
        this.name = name;
        this.formula = formula;
        this.saturatedConcentration = saturatedConcentration;
        this.solutionColor = solutionColor;
        this.particleColor = particleColor;
        this.particleSize = particleSize;
        this.particlesPerMole = particlesPerMole;
    }
}
