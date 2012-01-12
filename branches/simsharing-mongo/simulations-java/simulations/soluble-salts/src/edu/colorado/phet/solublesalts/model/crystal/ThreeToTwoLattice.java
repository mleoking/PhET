// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model.crystal;

import edu.colorado.phet.solublesalts.model.ion.Ion;

/**
 * TwoToOneLattice
 *
 * @author Ron LeMaster
 */
public class ThreeToTwoLattice extends Lattice {

    private Class twoIonClass;
    private Class threeIonClass;

    public ThreeToTwoLattice( Class twoIonClass, Class threeIonClass, double spacing ) {
        super( spacing );
        this.twoIonClass = twoIonClass;
        this.threeIonClass = threeIonClass;
    }

    public Object clone() {
        return new ThreeToTwoLattice( twoIonClass, threeIonClass, spacing );
    }

    protected int getNumNeighboringSites( Ion ion ) {
        return twoIonClass.isInstance( ion ) ? 3 : 2;
    }
}