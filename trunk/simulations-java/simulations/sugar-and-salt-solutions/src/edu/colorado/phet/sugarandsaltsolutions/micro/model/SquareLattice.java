package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.util.ArrayList;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.BondType.*;

/**
 * The square lattice (used e.g., in salt) makes connections up, down, left or right.
 *
 * @author Sam Reid
 */
public abstract class SquareLattice<T extends SquareLattice<T>> extends Lattice<T> {
    public SquareLattice( ImmutableList<Component> components, ImmutableList<Bond> bonds ) {
        super( components, bonds );
    }

    //Find the available sites where a new component might be added
    @Override protected ArrayList<LatticeSite<T>> getOpenSites() {
        ArrayList<LatticeSite<T>> latticeSites = new ArrayList<LatticeSite<T>>();
        for ( Component component : components ) {
            for ( BondType bondType : new BondType[] { UP, DOWN, LEFT, RIGHT } ) {
                testAddSite( latticeSites, component, getBonds( component ), bondType );
            }
        }
        return latticeSites;
    }
}
