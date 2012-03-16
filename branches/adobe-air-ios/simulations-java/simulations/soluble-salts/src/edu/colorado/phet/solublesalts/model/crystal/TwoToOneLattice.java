// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model.crystal;

import edu.colorado.phet.solublesalts.model.ion.Ion;

/**
 * TwoToOneLattice
 *
 * @author Ron LeMaster
 */
public class TwoToOneLattice extends Lattice {

    private Class oneIonClass;
    private Class twoIonClass;

    public TwoToOneLattice( Class oneIonClass, Class twoIonClass, double spacing ) {
        super( spacing );
        this.oneIonClass = oneIonClass;
        this.twoIonClass = twoIonClass;
    }

    public Object clone() {
        return new TwoToOneLattice( oneIonClass, twoIonClass, spacing );
    }

    protected int getNumNeighboringSites( Ion ion ) {
        return oneIonClass.isInstance( ion ) ? 4 : 2;
    }
}