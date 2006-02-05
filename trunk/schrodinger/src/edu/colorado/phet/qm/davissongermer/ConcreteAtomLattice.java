/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.qm.model.Potential;
import edu.colorado.phet.qm.model.potentials.CompositePotential;
import edu.colorado.phet.qm.model.potentials.PrecomputedPotential;

/**
 * User: Sam Reid
 * Date: Feb 4, 2006
 * Time: 9:29:30 PM
 * Copyright (c) Feb 4, 2006 by Sam Reid
 */

public class ConcreteAtomLattice implements Potential {
    private CompositePotential compositePotential = new CompositePotential();
    private PrecomputedPotential potential;

    public ConcreteAtomLattice( int latticeWidth, int latticeHeight ) {
        potential = new PrecomputedPotential( compositePotential, latticeWidth, latticeHeight );
    }

    public double getPotential( int x, int y, int timestep ) {
        return potential.getPotential( x, y, timestep );
    }

    public void addCircularPotential( CircularPotential circularPotential ) {
        compositePotential.addPotential( circularPotential );
        potential.setPotential( compositePotential );
    }

}
