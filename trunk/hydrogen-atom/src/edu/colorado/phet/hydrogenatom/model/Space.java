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
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.hydrogenatom.model.Gun.GunFiredEvent;
import edu.colorado.phet.hydrogenatom.model.Gun.GunFiredListener;

/**
 * Space models the space that photons and alpha particles travel through,
 * and where they encounter atoms.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Space implements ModelElement {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Rectangle2D _bounds;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param bounds
     * @param gun
     * @param model
     */
    public Space( Rectangle2D bounds ) {
        _bounds = new Rectangle2D.Double();
        _bounds.setRect( bounds );
    }
    
    //----------------------------------------------------------------------------
    // Accesors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the point that is at the center of space.
     * The origin (0,0) is at the bottom-center of space.
     * 
     * @return Point2D
     */
    public Point2D getCenter() {
        return new Point2D.Double( 0, -_bounds.getHeight() / 2 );
    }
    
    //----------------------------------------------------------------------------
    // Photon and Alpha Particle management
    //----------------------------------------------------------------------------

    public boolean contains( Photon photon ) {
        Point2D position = photon.getPositionRef();
        return _bounds.contains( position );
    }
    
    public boolean contains( AlphaParticle alphaParticle ) {
        Point2D position = alphaParticle.getPositionRef();
        return _bounds.contains( position );
    }
   
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /** Do nothing. */
    public void stepInTime( double dt ) {}
}
