/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.collision;

/**
 * VerticalWallFixupStrategy
 * <p>
 * Keeps spheres from getting through a wall from left to right or right to left
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class VerticalWallFixupStrategy implements WallFixupStrategy {
    public void fixup( Wall wall, SphericalBody sphere ) {
        if( sphere.getVelocity().getX() > 0 ) {
            sphere.setPosition( wall.getBounds().getMinX() - sphere.getRadius(), sphere.getPosition().getY() );
        }
        else {
            sphere.setPosition( wall.getBounds().getMaxX() + sphere.getRadius(), sphere.getPosition().getY() );
        }
        sphere.setVelocity( -sphere.getVelocity().getX(), sphere.getVelocity().getY() );
    }
}
