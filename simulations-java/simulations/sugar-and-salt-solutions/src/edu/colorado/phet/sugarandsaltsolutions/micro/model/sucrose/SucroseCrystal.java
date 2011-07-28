package edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Crystal;

import static edu.colorado.phet.sugarandsaltsolutions.common.util.Units.nanometersToMeters;
import static edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel.sizeScale;

/**
 * This crystal for sugar updates the positions of the molecules to ensure they move together
 *
 * @author Sam Reid
 */
public class SucroseCrystal extends Crystal<SucroseMolecule> {

    public SucroseCrystal( ImmutableVector2D position, SucroseLattice lattice, double angle ) {
        super( position,

               //Sugar size is actually about 1 nm, but we need to make them closer together or the sucrose lattices look disjoint
               //Also, scale everything by the model sizeScale, including distances between atoms
               nanometersToMeters( 0.5 ) * sizeScale,

               lattice, angle
        );
    }
}