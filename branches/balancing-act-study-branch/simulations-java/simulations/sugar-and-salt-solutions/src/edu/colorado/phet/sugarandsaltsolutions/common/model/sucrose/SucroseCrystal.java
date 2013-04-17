// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model.sucrose;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Formula;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Particle;

import static edu.colorado.phet.sugarandsaltsolutions.common.model.Units.nanometersToMeters;

/**
 * This crystal for sugar updates the positions of the molecules to ensure they move together
 *
 * @author Sam Reid
 */
public class SucroseCrystal extends Crystal<Sucrose> {


    public SucroseCrystal( Vector2D position, double angle ) {

        //Sugar size is actually about 1 nm, but we need to make them closer together or the sucrose lattices look disjoint
        //Also, scale everything by the model sizeScale, including distances between atoms
        super( Formula.SUCROSE, position,

               //Spacing between adjacent sucrose molecules, in meters
               nanometersToMeters( 0.5 ) * SugarAndSaltSolutionsApplication.sizeScale.get(),

               angle );
    }

    //Create a new Sucrose to be added to the crystal
    @Override public Sucrose createPartner( Sucrose original ) {
        return new Sucrose();
    }

    //Create a single sucrose molecule to begin the crystal
    @Override protected Sucrose createConstituentParticle( Class<? extends Particle> type ) {
        return new Sucrose();
    }
}