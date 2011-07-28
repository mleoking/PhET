package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumchloride;

import java.util.ArrayList;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.Bond;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.BondType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ImmutableList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Lattice;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.LatticeSite;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Sodium;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SquareLattice;

/**
 * Data structures and algorithms for creating and modeling a salt crystal lattice.  Instances are immutable.
 *
 * @author Sam Reid
 */
public class SodiumChlorideLattice extends SquareLattice<SphericalParticle> {

    public SodiumChlorideLattice() {
        super( new ImmutableList<SphericalParticle>( new Sodium() ), new ImmutableList<Bond<SphericalParticle>>() );
    }

    public SodiumChlorideLattice( SphericalParticle particle ) {
        super( new ImmutableList<SphericalParticle>( particle ), new ImmutableList<Bond<SphericalParticle>>() );
    }

    public SodiumChlorideLattice( ImmutableList<SphericalParticle> components, ImmutableList<Bond<SphericalParticle>> bonds ) {
        super( components, bonds );
    }

    @Override protected void testAddSite( ArrayList<LatticeSite<SphericalParticle>> latticeSites, SphericalParticle particle, ArrayList<Bond<SphericalParticle>> bonds, BondType type ) {
        if ( !containsBondType( bonds, type ) ) {
            latticeSites.add( new SodiumChlorideSite( particle, type ) );
        }
    }

    //Sample main to test lattice construction
    public static void main( String[] args ) {
        Lattice lattice = new SodiumChlorideLattice().grow( 100 );
        System.out.println( "saltLattice = " + lattice );
    }
}