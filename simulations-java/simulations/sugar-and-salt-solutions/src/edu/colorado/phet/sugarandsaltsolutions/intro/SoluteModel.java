// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.intro;

import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.*;

/**
 * Model of the amount in moles, concentration, amount dissolved, amount precipitated, for sugar and salt in the intro tab.
 *
 * @author Sam Reid
 */
public class SoluteModel {

    //The amount of the solute in moles
    public final DoubleProperty moles;

    //The concentration in the liquid
    public final DividedBy concentration;

    //The amount that precipitated (solidified)
    public final CompositeProperty<Double> molesPrecipitated;

    //The amount that is dissolved is solution
    public final CompositeProperty<Double> molesDissolved;

    //The amount of moles necessary to fully saturate the solution, past this, the solute will start to precipitate.
    public final Times saturationPointMoles;

    public SoluteModel( DoubleProperty waterVolume, double saltSaturationPoint ) {
        //Salt moles and concentration
        moles = new DoubleProperty( 0.0 );
        saturationPointMoles = waterVolume.times( saltSaturationPoint * 1000 );
        molesDissolved = new Min( moles, saturationPointMoles );
        molesPrecipitated = new Max( new Minus( moles, molesDissolved ), new Property<Double>( 0.0 ) );
        concentration = new DividedBy( molesDissolved, waterVolume );
    }
}