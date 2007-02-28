/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Feb 18, 2006
 * Time: 12:40:41 PM
 * Copyright (c) Feb 18, 2006 by Sam Reid
 */

public class SquareAtomLattice extends FractionalAtomLattice {
    public SquareAtomLattice( double atomRadius, double spacingBetweenAtoms, double y0, double potential ) {
        super( atomRadius, spacingBetweenAtoms, y0, potential );
    }

    protected AtomPotential createPotential( Point center, int concreteDiameter, double potential ) {
        return new RectanglePotential( center, concreteDiameter, potential );
    }
}
