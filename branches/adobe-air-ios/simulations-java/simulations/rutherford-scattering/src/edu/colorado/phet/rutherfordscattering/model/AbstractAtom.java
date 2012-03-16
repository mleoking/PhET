// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.rutherfordscattering.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.ModelElement;

/**
 * AbstractAtom is the base class for all atom models.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractAtom extends FixedObject implements ModelElement {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     * 
     * @param position
     * @param orientation
     */
    public AbstractAtom( Point2D position, double orientation ) {
        super( position, orientation );
    }
    
    //----------------------------------------------------------------------------
    // Particle motion
    //----------------------------------------------------------------------------

    /**
     * Moves an alpha particle.
     * 
     * @param dt
     * @param alphaParticle
     */
    public abstract void moveAlphaParticle( double dt, AlphaParticle alphaParticle );
}
