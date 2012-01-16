// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.common.model;

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
    public final Color precipitateColor; // the solute's color as a precipitate
    public final double precipitateSize; // size of the precipitate particles in view coordinates
    public final int precipitateParticlesPerMole; // number of precipitate particles to show per mol of saturation

    // For most solutes, the color of the precipitate is the same as the color in solution.
    public Solute( String name, String formula, double maxConcentration, ColorRange solutionColor, double precipitateSize, int precipitateParticlesPerMole ) {
        this( name, formula, maxConcentration, solutionColor, solutionColor.getMax(), precipitateSize, precipitateParticlesPerMole );
    }

    public Solute( String name, String formula, double saturatedConcentration, ColorRange solutionColor, Color precipitateColor, double precipitateSize, int precipitateParticlesPerMole ) {
        this.name = name;
        this.formula = formula;
        this.saturatedConcentration = saturatedConcentration;
        this.solutionColor = solutionColor;
        this.precipitateColor = precipitateColor;
        this.precipitateSize = precipitateSize;
        this.precipitateParticlesPerMole = precipitateParticlesPerMole;
    }
}
