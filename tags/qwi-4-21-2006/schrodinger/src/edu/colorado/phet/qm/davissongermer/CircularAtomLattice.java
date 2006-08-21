/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Feb 18, 2006
 * Time: 12:40:41 PM
 * Copyright (c) Feb 18, 2006 by Sam Reid
 */

public class CircularAtomLattice extends FractionalAtomLattice {
    public CircularAtomLattice( double atomRadius, double spacingBetweenAtoms, double y0, double potential ) {
        super( atomRadius, spacingBetweenAtoms, y0, potential );
    }

    protected AtomPotential createPotential( Point center, int conceteDiameter, double potential ) {
        return new CircularPotential( center, conceteDiameter, potential );
    }
}
