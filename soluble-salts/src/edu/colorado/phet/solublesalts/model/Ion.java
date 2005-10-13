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

import edu.colorado.phet.collision.SphericalBody;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;

/**
 * Ion
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Ion extends SphericalBody {

    private IonProperties ionProperties;

    public Ion( IonProperties ionProperties ) {
        this( new Point2D.Double(),
              new Vector2D.Double(),
              new Vector2D.Double(),
              ionProperties );
    }

    public Ion( Point2D position, Vector2D velocity, Vector2D acceleration, IonProperties ionProperties ) {
        super( position, velocity, acceleration, ionProperties.getMass(), ionProperties.getRadius() );
        this.ionProperties = ionProperties;
    }

    public double getCharge() {
        return ionProperties.getCharge();
    }
}
