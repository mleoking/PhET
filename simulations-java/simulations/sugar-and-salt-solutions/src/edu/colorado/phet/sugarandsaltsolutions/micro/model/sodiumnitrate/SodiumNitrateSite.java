// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.Bond;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.BondType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ImmutableList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Lattice;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.LatticeSite;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.SodiumIonParticle;

/**
 * Identifies open (available) bonding site in a Sodium Nitrate crystal
 *
 * @author Sam Reid
 */
public class SodiumNitrateSite extends LatticeSite<Particle> {
    public SodiumNitrateSite( Particle component, BondType type ) {
        super( component, type );
    }

    @Override public Lattice<Particle> grow( Lattice<Particle> lattice ) {
        Particle newIon = getOppositeComponent();
        return new SodiumNitrateLattice( new ImmutableList<Particle>( lattice.components, newIon ), new ImmutableList<Bond<Particle>>( lattice.bonds, new Bond<Particle>( component, newIon, type ) ) );
    }

    public Particle getOppositeComponent() {
        return ( component instanceof SodiumIonParticle ) ? new NitrateMolecule() : new SodiumIonParticle();
    }
}