// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.util.ColorRange;

/**
 * Model of a solute, an immutable data structure.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Solute {

    // The form that the solute is delivered in, either a solid or in a stock solution.
    public static enum SoluteForm {
        SOLID, STOCK_SOLUTION
    }

    public final String name; // localized name
    public final String formula; // chemical formula, not localized
    public final double molarMass; // g/mol
    public final double saturatedConcentration; // M, in beaker
    public final double stockSolutionConcentration; // M, stock solution in dropper
    public final ColorRange solutionColor; // color range for a solution with non-zero concentration
    public final Color precipitateColor; // color as a precipitate  //TODO rename this particleColor, if used for both shaker and precipitate particles
    public final double particleSize; // solid particles are square, this is the length of one side
    public final int precipitateParticlesPerMole; // number of precipitate particles to show per mol of saturation

    // For most solutes, the color of the precipitate is the same as the color of the saturated solution.
    public Solute( String name, String formula, double molarMass, double saturatedConcentration, double stockSolutionConcentration, ColorRange solutionColor, double particleSize, int precipitateParticlesPerMole ) {
        this( name, formula, molarMass, saturatedConcentration, stockSolutionConcentration, solutionColor, solutionColor.getMax(), particleSize, precipitateParticlesPerMole );
    }

    public Solute( String name, String formula, double molarMass, double saturatedConcentration, double stockSolutionConcentration,
                   ColorRange solutionColor, Color precipitateColor, double particleSize, int precipitateParticlesPerMole ) {
        this.name = name;
        this.formula = formula;
        this.molarMass = molarMass;
        this.saturatedConcentration = saturatedConcentration;
        this.stockSolutionConcentration = stockSolutionConcentration;
        this.solutionColor = solutionColor;
        this.precipitateColor = precipitateColor;
        this.particleSize = particleSize;
        this.precipitateParticlesPerMole = precipitateParticlesPerMole;
    }
}
