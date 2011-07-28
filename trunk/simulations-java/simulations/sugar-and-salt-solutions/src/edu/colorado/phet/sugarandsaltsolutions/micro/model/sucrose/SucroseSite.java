package edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.Bond;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.BondType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ImmutableList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Lattice;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.LatticeSite;

/**
 * A way to grow a sugar crystal.
 *
 * @author Sam Reid
 */
public class SucroseSite extends LatticeSite<SucroseMolecule> {
    public SucroseSite( SucroseMolecule component, BondType type ) {
        super( component, type );
    }

    @Override public Lattice<SucroseMolecule> grow( Lattice<SucroseMolecule> lattice ) {
        SucroseMolecule newSucroseMolecule = new SucroseMolecule();
        return new SucroseLattice( new ImmutableList<SucroseMolecule>( lattice.components, newSucroseMolecule ), new ImmutableList<Bond<SucroseMolecule>>( lattice.bonds, new Bond<SucroseMolecule>( component, newSucroseMolecule, type ) ) );
    }
}