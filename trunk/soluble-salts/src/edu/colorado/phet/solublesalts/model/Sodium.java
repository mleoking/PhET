/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model;

import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;

/**
 * Sodium
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Sodium extends Ion {

    private static IonProperties ionProperties = new IonProperties( 11, 1, 9.5 );

    public Sodium() {
        super( ionProperties );
    }

    public Sodium( Point2D position, Vector2D velocity, Vector2D acceleration ) {
        super( position,
               velocity,
               acceleration,
               ionProperties );
    }
}
