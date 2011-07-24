package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

import static java.lang.Math.PI;
import static java.lang.Math.random;

/**
 * A crystal represents 0 or more (usually 1 or more) constituents which can be put into solution.  It is constructed from a lattice.
 *
 * @author Sam Reid
 */
public class Crystal extends Compound {
    public Crystal( ImmutableVector2D position ) {
        super( position );
    }

    //Splits up all constituents from the crystal lattice, returning the components (particles or molecules) that should move about freely
    public ArrayList<? extends Particle> dissolve() {
        ArrayList<Particle> freeParticles = new ArrayList<Particle>();
        for ( Constituent constituent : this ) {
            constituent.particle.velocity.set( velocity.get().getRotatedInstance( random() * PI * 2 ) );
            freeParticles.add( constituent.particle );
        }
        return freeParticles;
    }
}