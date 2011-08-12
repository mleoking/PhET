package edu.colorado.phet.sugarandsaltsolutions.micro.model.glucose;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;

import static edu.colorado.phet.sugarandsaltsolutions.common.util.Units.nanometersToMeters;
import static edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel.sizeScale;

/**
 * This crystal for sugar updates the positions of the molecules to ensure they move together
 *
 * @author Sam Reid
 */
public class GlucoseCrystal extends Crystal<Glucose> {

    public GlucoseCrystal( ImmutableVector2D position, double angle ) {

        //Sugar size is actually about 1 nm, but we need to make them closer together or the sucrose lattices look disjoint
        //Also, scale everything by the model sizeScale, including distances between atoms
        super( position, nanometersToMeters( 0.5 ) * sizeScale, angle );
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