// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.davissongermer;

import edu.colorado.phet.quantumwaveinterference.model.Potential;
import edu.colorado.phet.quantumwaveinterference.model.potentials.CompositePotential;
import edu.colorado.phet.quantumwaveinterference.model.potentials.PrecomputedPotential;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Feb 4, 2006
 * Time: 9:29:30 PM
 */

public class ConcreteAtomLattice implements Potential {
    private CompositePotential compositePotential = new CompositePotential();
    private PrecomputedPotential potential;
    private Point headAtom;

    public ConcreteAtomLattice( int latticeWidth, int latticeHeight ) {
        potential = new PrecomputedPotential( compositePotential, latticeWidth, latticeHeight );
    }

    public double getPotential( int x, int y, int timestep ) {
        return potential.getPotential( x, y, timestep );
    }

    public void addAtomPotentialNoUpdate( AtomPotential atomPotential ) {
        compositePotential.addPotential( atomPotential );
    }

    public void updateAll() {
        potential.setPotential( compositePotential );
    }

    public AtomPotential[] getPotentials() {
        ArrayList list = new ArrayList();
        for( int i = 0; i < compositePotential.numPotentials(); i++ ) {
            list.add( compositePotential.potentialAt( i ) );
        }
        return (AtomPotential[])list.toArray( new AtomPotential[0] );
    }

    public void setHeadAtom( int x, int y ) {
        this.headAtom = new Point( x, y );
    }

    public Point getHeadAtom() {
        return new Point( headAtom );
    }
}
