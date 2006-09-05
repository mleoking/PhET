/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model;

import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;

/**
 * MoleculeA
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculeB extends SimpleMolecule {
    private static double RADIUS = 10;

    public MoleculeB() {
        super( MoleculeB.RADIUS );
    }

    public MoleculeB( Point2D location, Vector2D velocity, Vector2D acceleration, double mass, double charge ) {
        super( MoleculeB.RADIUS, location, velocity, acceleration, mass, charge );
    }
}
