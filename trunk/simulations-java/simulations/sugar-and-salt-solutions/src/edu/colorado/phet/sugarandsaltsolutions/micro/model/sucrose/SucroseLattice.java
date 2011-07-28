package edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose;

import java.util.ArrayList;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.Bond;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.BondType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ImmutableList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.LatticeSite;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SquareLattice;

/**
 * Data structures and algorithms for creating and modeling a sugar crystal lattice.  Instances are immutable.
 *
 * @author Sam Reid
 */
public class SucroseLattice extends SquareLattice<SucroseMolecule> {

    public SucroseLattice() {
        super( new ImmutableList<SucroseMolecule>( new SucroseMolecule() ), new ImmutableList<Bond<SucroseMolecule>>() );
    }

    public SucroseLattice( ImmutableList<SucroseMolecule> components, ImmutableList<Bond<SucroseMolecule>> bonds ) {
        super( components, bonds );
    }

    @Override protected void testAddSite( ArrayList<LatticeSite<SucroseMolecule>> latticeSites, SucroseMolecule component, ArrayList<Bond<SucroseMolecule>> bonds, BondType type ) {
        if ( !containsBondType( bonds, type ) ) {
            latticeSites.add( new SucroseSite( component, type ) );
        }
    }
}