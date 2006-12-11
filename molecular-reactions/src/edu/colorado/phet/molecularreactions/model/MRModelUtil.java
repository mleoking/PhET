/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;

/**
 * MRModelUtil
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MRModelUtil {

    /**
     * Determines the *collision energy* with which two molecules collide.
     * This is the combined kinetic energy of the colliding
     * bodies relative to their combined center of mass, if they are moving toward
     * each other, and 0 if they are not moving toward each other
     *
     * @param sm   The simple molecule involved in the collision
     * @param comp The composite molecule in the collision
     * @return the collision energy
     */
    public static double getCollisionEnergy( SimpleMolecule sm, CompositeMolecule comp ) {

        CollisionParams params = new CollisionParams( sm, comp );
        Vector2D cv = new Vector2D.Double( params.getbMolecule().getPosition(), params.getFreeMolecule().getPosition() );
        if( params.getFreeMolecule() instanceof MoleculeB ) {
new CollisionParams( sm, comp );            
        }
        cv.normalize();

        // Get the speed of each molecule along the vector connecting them
        double speedF = params.getFreeMolecule().getVelocity().dot( cv );
        double speedC = params.getCompositeMolecule().getVelocity().dot( cv );
        double signF = -MathUtil.getSign( speedF );
        double signC = MathUtil.getSign( speedC );
        double ce = ( signF * sm.getMass() * speedF * speedF
                      + signC * comp.getMass() * speedC * speedC ) / 2;
        ce = Math.max( ce, 0 );
        return ce;
    }
}
