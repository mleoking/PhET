// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro.model;

import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.CompositeDoubleProperty;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.Max;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.Min;

/**
 * Model of the amount in moles, concentration, amount dissolved, amount precipitated, for sugar and salt in the introductory (macro) tab.
 *
 * @author Sam Reid
 */
public class SoluteModel {

    //The amount of the solute in moles
    public final DoubleProperty moles;

    //The amount that precipitated (solidified)
    public final CompositeDoubleProperty molesPrecipitated;

    //The amount that is dissolved is solution
    public final CompositeDoubleProperty molesDissolved;

    //The amount of moles necessary to fully saturate the solution, past this, the solute will start to precipitate.
    public final CompositeDoubleProperty saturationPointMoles;

    //The volume (in SI) of the amount of solid
    //Solid precipitate should push up the water level, so that every mole of salt takes up 0.02699 L, and every mole of sugar takes up 0.2157 L
    public final CompositeDoubleProperty solidVolume;

    //The amount in grams
    public final CompositeDoubleProperty grams;

    //The molar mass, the mass (in grams) per mole
    public final double gramsPerMole;

    //Volume in meters cubed per solid mole
    public final double volumePerSolidMole;

    public SoluteModel( DoubleProperty waterVolume, double saturationPoint, double volumePerSolidMole, double gramsPerMole ) {
        this.volumePerSolidMole = volumePerSolidMole;
        this.gramsPerMole = gramsPerMole;
        //Salt moles and concentration
        moles = new DoubleProperty( 0.0 );
        saturationPointMoles = waterVolume.times( saturationPoint );
        molesDissolved = new Min( moles, saturationPointMoles );
        molesPrecipitated = new Max( moles.minus( molesDissolved ), 0.0 );
        solidVolume = molesPrecipitated.times( volumePerSolidMole );
        grams = moles.times( gramsPerMole );
    }
}