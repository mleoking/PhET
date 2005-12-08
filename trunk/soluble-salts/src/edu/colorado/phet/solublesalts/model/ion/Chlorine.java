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
import edu.colorado.phet.solublesalts.model.ion.Ion;

import java.awt.geom.Point2D;

/**
 * Chloride
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Chlorine extends Ion {
    public static final double RADIUS = 8;
    private static IonProperties ionProperties = new IonProperties( 17, -1, RADIUS );
//    private static IonProperties ionProperties = new IonProperties( 17, -1, 14 );

    public Chlorine() {
        super( ionProperties );
    }

    public Chlorine( Point2D position, Vector2D velocity, Vector2D acceleration ) {
        super( position, velocity, acceleration, ionProperties );
    }
}
