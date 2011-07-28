package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumchloride;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.Bond;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.BondType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ImmutableList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Lattice;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.LatticeSite;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Chloride;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Sodium;

/**
 * Identifies open (available) bonding site in a SaltLattice
 *
 * @author Sam Reid
 */
public class SodiumChlorideSite extends LatticeSite<SphericalParticle> {
    public SodiumChlorideSite( SphericalParticle component, BondType type ) {
        super( component, type );
    }

    @Override public Lattice<SphericalParticle> grow( Lattice<SphericalParticle> lattice ) {
        SphericalParticle newIon = getOppositeComponent();
        return new SodiumChlorideLattice( new ImmutableList<SphericalParticle>( lattice.components, newIon ), new ImmutableList<Bond<SphericalParticle>>( lattice.bonds, new Bond<SphericalParticle>( component, newIon, type ) ) );
    }

    public SphericalParticle getOppositeComponent() {
        return ( component instanceof Sodium ) ? new Chloride() : new Sodium();
    }
}