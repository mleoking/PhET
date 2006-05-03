/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model.ion;

import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;

/**
 * Silver
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Silver extends Ion {
    public static final double RADIUS = 4;
    private static IonProperties ionProperties = new IonProperties( 47, 1, RADIUS );

    public Silver() {
        super( ionProperties );
    }

    public Silver( Point2D position, Vector2D velocity, Vector2D acceleration ) {
        super( position,
               velocity,
               acceleration,
               ionProperties );
    }
}
