// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model.ion;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * Chromium
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Thallium extends Ion {

    public static final double RADIUS = 4;
    private static IonProperties ionProperties = new IonProperties( 204, 1, Thallium.RADIUS );

    public Thallium() {
        super( Thallium.ionProperties );
    }

    public Thallium( Point2D position, Vector2D velocity, Vector2D acceleration ) {
        super( position,
               velocity,
               acceleration,
               Thallium.ionProperties );
    }
}
