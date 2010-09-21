/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas.collision;

/**
 * FloorFixupStrategy
 * <p/>
 * Keeps spheres from getting through a wall from top to bottom
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class FloorFixupStrategy implements WallFixupStrategy {

    public void fixup( Wall wall, SphericalBody sphere ) {
        sphere.setVelocity( sphere.getVelocity().getX(), -Math.abs( sphere.getVelocity().getY() ) );
        sphere.setPosition( sphere.getPosition().getX(), wall.getBounds().getMinY() - sphere.getRadius() );
    }
}
