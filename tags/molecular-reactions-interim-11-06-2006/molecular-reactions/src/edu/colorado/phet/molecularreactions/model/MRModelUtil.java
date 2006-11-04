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

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.math.MathUtil;

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
     * @param sm    The simple molecule involved in the collision
     * @param comp  The composite molecule in the collision
     * @return the collision energy
     */
    public static double getCollisionEnergy( SimpleMolecule sm, CompositeMolecule comp ) {

        MoleculeB mB = comp.getComponentMolecules()[0] instanceof MoleculeB
                       ? (MoleculeB)comp.getComponentMolecules()[0]
                       : (MoleculeB)comp.getComponentMolecules()[1];

        // Move the molecules apart so they won't react on the next time step
        Vector2D sep = new Vector2D.Double( mB.getPosition(), sm.getPosition() );
        sep.normalize().scale( mB.getRadius() + ( (SimpleMolecule)sm ).getRadius() + 2 );

        // Get the speeds at which the molecules are moving toward their CM, and add that KE to the PE passed in
        CompositeBody cb = new CompositeBody();
        cb.addBody( sm );
        cb.addBody( comp );

        Vector2D v1 = cb.getVelocity();
        Vector2D v2 = comp.getVelocity();

        Vector2D vFreeMoleculeRelCM = new Vector2D.Double( sm.getVelocity() ).subtract( cb.getVelocity() );
        double sFree = MathUtil.getProjection( vFreeMoleculeRelCM, sep ).getMagnitude();
        Vector2D vCompositeMoleculeRelCM = new Vector2D.Double( comp.getVelocity() ).subtract( cb.getVelocity() );
        double sComposite = MathUtil.getProjection( vCompositeMoleculeRelCM, sep ).getMagnitude();

        // Compute the *collision energy*. This is the combined kinetic energy of the colliding
        // bodies relative to their combined center of mass, if they are moving toward
        // each other, and 0 if they are not moving toward each other
        double s1 = -MathUtil.getSign( vFreeMoleculeRelCM.dot( sep ));
        double s2 = MathUtil.getSign( vCompositeMoleculeRelCM.dot( sep ));
        double ce = ( s1* sm.getMass() * sFree * sFree + s2 * comp.getMass() * sComposite * sComposite ) / 2;
        ce = Math.max( ce, 0 );
        return ce;
    }
}
