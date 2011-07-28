// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate;

import java.util.ArrayList;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.Bond;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.BondType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ImmutableList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Lattice;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.LatticeSite;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Sodium;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SquareLattice;

/**
 * Data structures and algorithms for creating and modeling a salt crystal lattice.  Instances are immutable.
 *
 * @author Sam Reid
 */
public class SodiumNitrateLattice extends SquareLattice<Particle> {

    public SodiumNitrateLattice() {
        super( new ImmutableList<Particle>( new Sodium() ), new ImmutableList<Bond<Particle>>() );
    }

    public SodiumNitrateLattice( ImmutableList<Particle> components, ImmutableList<Bond<Particle>> bonds ) {
        super( components, bonds );
    }

    @Override protected void testAddSite( ArrayList<LatticeSite<Particle>> latticeSites, Particle component, ArrayList<Bond<Particle>> bonds, BondType type ) {
        if ( !containsBondType( bonds, type ) ) {
            latticeSites.add( new SodiumNitrateSite( component, type ) );
        }
    }

    //Sample main to test lattice construction
    public static void main( String[] args ) {
        Lattice lattice = new SodiumNitrateLattice().grow( 100 );
        System.out.println( "saltLattice = " + lattice );
    }
}