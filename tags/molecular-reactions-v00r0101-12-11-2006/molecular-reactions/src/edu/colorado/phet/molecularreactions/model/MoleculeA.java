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
public class MoleculeA extends SimpleMolecule {
    private static double RADIUS = 12;

    public static double getRADIUS() {
        return RADIUS;
    }

    public MoleculeA() {
        super( RADIUS );
    }

    public MoleculeA( Point2D location, Vector2D velocity, Vector2D acceleration, double mass, double charge ) {
        super( RADIUS, location, velocity, acceleration, mass, charge );
    }
}
