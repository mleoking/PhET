// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model.crystal;

import edu.colorado.phet.solublesalts.model.ion.Ion;

/**
 * TwoToOneLattice
 *
 * @author Ron LeMaster
 */
public class ThreeToOneLattice extends Lattice {

    private Class oneIonClass;
    private Class threeIonClass;

    public ThreeToOneLattice( Class oneIonClass, Class threeIonClass, double spacing ) {
        super( spacing );
        this.oneIonClass = oneIonClass;
        this.threeIonClass = threeIonClass;
        this.spacing = spacing;
    }

    public Object clone() {
        return new ThreeToOneLattice( oneIonClass, threeIonClass, spacing );
    }

    protected int getNumNeighboringSites( Ion ion ) {
        return oneIonClass.isInstance( ion ) ? 6 : 2;
    }
}