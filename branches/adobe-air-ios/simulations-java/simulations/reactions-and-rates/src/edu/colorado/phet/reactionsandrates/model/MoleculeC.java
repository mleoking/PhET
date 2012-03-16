// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.model;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

import java.awt.geom.Point2D;

/**
 * MoleculeA
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculeC extends SimpleMolecule {
    private static double RADIUS = 8;

    public static double getRADIUS() {
        return RADIUS;
    }

    public MoleculeC() {
        super( MoleculeC.RADIUS );
    }

    public MoleculeC( Point2D location, Vector2D velocity, Vector2D acceleration, double mass, double charge ) {
        super( MoleculeC.RADIUS, location, velocity, acceleration, mass, charge );
    }

    public Object clone() {
        return super.clone();
    }
}
