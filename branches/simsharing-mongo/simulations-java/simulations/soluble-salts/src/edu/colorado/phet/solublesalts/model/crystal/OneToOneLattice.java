// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model.crystal;

import edu.colorado.phet.solublesalts.model.ion.Ion;

/**
 * PlainCubicLattice
 *
 * @author Ron LeMaster
 */
public class OneToOneLattice extends Lattice {

    /**
     * @param spacing
     */
    public OneToOneLattice( double spacing ) {
        super( spacing );
    }

    public Object clone() {
        return new OneToOneLattice( this.spacing );
    }

    protected int getNumNeighboringSites( Ion ion ) {
        return 4;
    }
}