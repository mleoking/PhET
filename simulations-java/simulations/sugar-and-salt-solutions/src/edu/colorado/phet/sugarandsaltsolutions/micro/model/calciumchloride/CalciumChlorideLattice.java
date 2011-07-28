// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.calciumchloride;

import java.util.ArrayList;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.Bond;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.BondType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ImmutableList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Lattice;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.LatticeSite;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Calcium;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Chloride;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.BondType.*;

/**
 * Data structures and algorithms for creating and modeling a calcium chloride crystal lattice,
 * using the same lattice structure as in the soluble salts sim.  Instances are immutable.
 *
 * @author Sam Reid
 */
public class CalciumChlorideLattice extends Lattice<SphericalParticle> {

    public CalciumChlorideLattice() {

        //Seed with a chloride since we need 2:1 ratio of chloride to calcium
        super( new ImmutableList<SphericalParticle>( new Chloride() ), new ImmutableList<Bond<SphericalParticle>>() );
    }

    public CalciumChlorideLattice( ImmutableList<SphericalParticle> components, ImmutableList<Bond<SphericalParticle>> bonds ) {
        super( components, bonds );
    }

    @Override protected void testAddSite( ArrayList<LatticeSite<SphericalParticle>> latticeSites, SphericalParticle particle, ArrayList<Bond<SphericalParticle>> bonds, BondType type ) {
        if ( !containsBondType( bonds, type ) ) {
            latticeSites.add( new CalciumChlorideSite( particle, type ) );
        }
    }

    //Find the available sites where a new component might be added
    @Override protected ArrayList<LatticeSite<SphericalParticle>> getOpenSites() {
        ArrayList<LatticeSite<SphericalParticle>> openSites = new ArrayList<LatticeSite<SphericalParticle>>();
        for ( SphericalParticle particle : components ) {

            //Calcium bonds in all 4 directions
            if ( particle instanceof Calcium ) {
                for ( BondType bondType : new BondType[] { UP, DOWN, LEFT, RIGHT } ) {
                    testAddSite( openSites, particle, getBonds( particle ), bondType );
                }
            }
            //Chloride only bonds in the same direction as it came from
            //It only forms 2 bonds instead of 4 like Calcium
            //So if it already has 2 bonds, then it is full
            //If it only has 1 bond, then the other bond must be on the opposite side
            else if ( particle instanceof Chloride ) {
                ArrayList<Bond<SphericalParticle>> bonds = getBonds( particle );
                if ( bonds.size() == 2 ) {
                    //do nothing, already fully bonded
                }
                else if ( bonds.size() == 1 ) {
                    //add one bond on the opposite side
                    openSites.add( new CalciumChlorideSite( particle, bonds.get( 0 ).type.reverse() ) );
                }
                else {
                    //No bonds (it is the only ion in the lattice), so can go any direction
                    for ( BondType bondType : new BondType[] { UP, DOWN, LEFT, RIGHT } ) {
                        testAddSite( openSites, particle, getBonds( particle ), bondType );
                    }
                }
            }
        }
        return openSites;
    }

    //Sample main to test lattice construction
    public static void main( String[] args ) {
        Lattice lattice = new CalciumChlorideLattice().grow( 100 );

        System.out.println( "lattice = " + lattice );
    }
}