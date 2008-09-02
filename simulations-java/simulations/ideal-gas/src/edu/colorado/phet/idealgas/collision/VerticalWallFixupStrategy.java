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
 * VerticalWallFixupStrategy
 * <p/>
 * Keeps spheres from getting through a wall from left to right or right to left
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class VerticalWallFixupStrategy implements WallFixupStrategy {
    public void fixup( Wall wall, SphericalBody sphere ) {
        WallDescriptor wallDesc = new WallDescriptor( wall, sphere.getRadius() );
        double dAB = wallDesc.AB.ptLineDistSq( sphere.getPosition() );
        double dBC = wallDesc.BC.ptLineDistSq( sphere.getPosition() );
        double dCD = wallDesc.CD.ptLineDistSq( sphere.getPosition() );
        double dAD = wallDesc.AD.ptLineDistSq( sphere.getPosition() );

        if( dBC < dAD ) {
            sphere.setPosition( wallDesc.BC.getX1(), sphere.getPosition().getY() );
            sphere.setVelocity( Math.abs( sphere.getVelocity().getX() ), sphere.getVelocity().getY() );
        }
        else {
            sphere.setPosition( wallDesc.AD.getX1(), sphere.getPosition().getY() );
            sphere.setVelocity( -Math.abs( sphere.getVelocity().getX() ), sphere.getVelocity().getY() );
        }
    }
}
