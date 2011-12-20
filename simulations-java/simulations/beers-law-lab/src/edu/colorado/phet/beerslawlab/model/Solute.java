// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.model;

import java.awt.Color;

/**
 * Model of a solute, an immutable data structure.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Solute {

    public final String name; // localized name
    public final String formula; // chemical formula, not localized
    public final double molarMass; // g/mol
    public final double saturatedConcentration; // M, in beaker
    public final double solutionConcentration; // M, in dropper
    public final Color solutionColor; // color in a fully-saturated solution
    public final Color precipitateColor; // color as a precipitate
    public final double precipitateSize; // size of the precipitate particles in view coordinates
    public final int precipitateParticlesPerMole; // number of precipitate particles to show per mol of saturation

    // For most solutes, the color of the precipitate is the same as the color in solution.
    public Solute( String name, String formula, double molarMass, double saturatedConcentration, double solutionConcentration, Color solutionColor, double precipitateSize, int precipitateParticlesPerMole ) {
        this( name, formula, molarMass, saturatedConcentration, solutionConcentration, solutionColor, solutionColor, precipitateSize, precipitateParticlesPerMole );
    }

    public Solute( String name, String formula, double molarMass, double saturatedConcentration, double solutionConcentration,
                   Color solutionColor, Color precipitateColor, double precipitateSize, int precipitateParticlesPerMole ) {
        this.name = name;
        this.formula = formula;
        this.molarMass = molarMass;
        this.saturatedConcentration = saturatedConcentration;
        this.solutionConcentration = solutionConcentration;
        this.solutionColor = solutionColor;
        this.precipitateColor = precipitateColor;
        this.precipitateSize = precipitateSize;
        this.precipitateParticlesPerMole = precipitateParticlesPerMole;
    }
}
