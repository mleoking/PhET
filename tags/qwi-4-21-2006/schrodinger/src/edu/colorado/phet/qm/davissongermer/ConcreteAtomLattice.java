/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.qm.model.Potential;
import edu.colorado.phet.qm.model.potentials.CompositePotential;
import edu.colorado.phet.qm.model.potentials.PrecomputedPotential;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Feb 4, 2006
 * Time: 9:29:30 PM
 * Copyright (c) Feb 4, 2006 by Sam Reid
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
