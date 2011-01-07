// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.davissongermer;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Feb 18, 2006
 * Time: 12:40:41 PM
 */

public class SquareAtomLattice extends FractionalAtomLattice {
    public SquareAtomLattice( double atomRadius, double spacingBetweenAtoms, double y0, double potential ) {
        super( atomRadius, spacingBetweenAtoms, y0, potential );
    }

    protected AtomPotential createPotential( Point center, int concreteDiameter, double potential ) {
        return new RectanglePotential( center, concreteDiameter, potential );
    }
}
