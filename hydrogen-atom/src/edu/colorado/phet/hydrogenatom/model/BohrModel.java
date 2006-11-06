/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.hydrogenatom.HAConstants;


public class BohrModel extends AbstractHydrogenAtom {

    public BohrModel( Point2D position ) {
        super( position, 0 /* orientation */ );
    }
    
    /**
     * Move an alpha particle using a Rutherford Scattering algorithm.
     * 
     * @param alphaParticle
     * @param dt
     */
    public void move( AlphaParticle alphaParticle, final double dt ) {
        final double L = HAConstants.ANIMATION_BOX_SIZE.height;
        final double D = L / 4;
        RutherfordScattering.move( this, alphaParticle, dt, D );
    }
}
