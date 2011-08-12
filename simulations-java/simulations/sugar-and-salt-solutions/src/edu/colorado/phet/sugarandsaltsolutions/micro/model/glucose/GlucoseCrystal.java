// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.glucose;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.SucroseCrystal;

/**
 * This crystal for sugar updates the positions of the molecules to ensure they move together
 *
 * @author Sam Reid
 */
public class GlucoseCrystal extends Crystal<Glucose> {

    public GlucoseCrystal( ImmutableVector2D position, double angle ) {

        //Glucose is about half as big as sucrose and hence should be half as far away on the lattice
        super( position, SucroseCrystal.SPACING / 2, angle );
    }

    //Create a new Sucrose to be added to the crystal
    @Override public Glucose createPartner( Glucose original ) {
        return new Glucose();
    }

    //Create a single sucrose molecule to begin the crystal
    @Override protected Glucose createSeed() {
        return new Glucose();
    }

    //Sucrose is always in the majority since it is the only component type; thus it can always be removed
    @Override protected Class<? extends Particle> getMajorityType() {
        return Glucose.class;
    }
}