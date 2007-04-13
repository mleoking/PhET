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
 * Strontium
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Sulfur extends Ion {

    public static final double RADIUS = 6;
    private static IonProperties ionProperties = new IonProperties( 32, -2, Sulfur.RADIUS );

    public Sulfur() {
        super( Sulfur.ionProperties );
    }

    public Sulfur( Point2D position, Vector2D velocity, Vector2D acceleration ) {
        super( position,
               velocity,
               acceleration,
               Sulfur.ionProperties );
    }
}
