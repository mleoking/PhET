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
public class MoleculeC extends SimpleMolecule {
//    private static double RADIUS = 16;
    private static double RADIUS = 8;

    public MoleculeC() {
        super( MoleculeC.RADIUS );
    }

    public MoleculeC( Point2D location, Vector2D velocity, Vector2D acceleration, double mass, double charge ) {
        super( MoleculeC.RADIUS, location, velocity, acceleration, mass, charge );
    }
}
