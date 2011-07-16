package edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice;

import java.util.ArrayList;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ImmutableList;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.BondType.*;

/**
 * The square lattice (used e.g., in salt) makes connections up, down, left or right.
 *
 * @author Sam Reid
 */
public abstract class SquareLattice extends Lattice {
    public SquareLattice( ImmutableList<Component> components, ImmutableList<Bond> bonds ) {
        super( components, bonds );
    }

    //Find the available sites where a new component might be added
    @Override protected ArrayList<OpenSite> getOpenSites() {
        ArrayList<OpenSite> openSites = new ArrayList<OpenSite>();
        for ( Component component : components ) {
            for ( BondType bondType : new BondType[] { UP, DOWN, LEFT, RIGHT } ) {
                testAddSite( openSites, component, getBonds( component ), bondType );
            }
        }
        return openSites;
    }
}
