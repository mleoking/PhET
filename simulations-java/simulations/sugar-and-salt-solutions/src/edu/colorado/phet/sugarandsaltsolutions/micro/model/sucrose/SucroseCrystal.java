package edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Formula;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;

import static edu.colorado.phet.sugarandsaltsolutions.common.util.Units.nanometersToMeters;

/**
 * This crystal for sugar updates the positions of the molecules to ensure they move together
 *
 * @author Sam Reid
 */
public class SucroseCrystal extends Crystal<Sucrose> {


    public SucroseCrystal( ImmutableVector2D position, double angle ) {

        //Sugar size is actually about 1 nm, but we need to make them closer together or the sucrose lattices look disjoint
        //Also, scale everything by the model sizeScale, including distances between atoms
        super( new Formula( Sucrose.class ), position,

               //Spacing between adjacent sucrose molecules, in meters
               nanometersToMeters( 0.5 ) * SugarAndSaltSolutionsApplication.sizeScale.get(),

               angle );
    }

    //Create a new Sucrose to be added to the crystal
    @Override public Sucrose createPartner( Sucrose original ) {
        return new Sucrose();
    }

    //Create a single sucrose molecule to begin the crystal
    @Override protected Sucrose createSeed() {
        return new Sucrose();
    }

    @Override public Class<? extends Particle> getMinorityType() {
        return Sucrose.class;
    }
}