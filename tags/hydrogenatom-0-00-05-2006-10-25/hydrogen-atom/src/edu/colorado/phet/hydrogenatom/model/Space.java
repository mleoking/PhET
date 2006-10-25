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
 * and where they encounter atoms.  Space is responsible for adding and 
 * removing photons and alpha particles to/from the model. Photons and
 * alpha particles are added when the gun fires them; they are removed 
 * when they travel outside the bounds of Space.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Space implements ModelElement, Observer, GunFiredListener {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Rectangle2D _bounds;
    private Gun _gun;
    private Model _model;
    private ArrayList _photons; // array of Photon
    private ArrayList _alphaParticles; // array of AlphaParticle
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param bounds
     * @param gun
     * @param model
     */
    public Space( Rectangle2D bounds, Gun gun, Model model ) {
        
        _bounds = new Rectangle2D.Double();
        _gun = gun;
        _model = model;
        _bounds.setRect( bounds );
        _photons = new ArrayList();
        _alphaParticles = new ArrayList();
        
        _gun.addObserver( this );
        _gun.addGunFiredListener( this );
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
    
    /**
     * Removes all photons from the model.
     */
    public void removeAllPhotons() {
        ArrayList a = new ArrayList( _photons );
        Iterator i = a.iterator();
        while ( i.hasNext() ) {
            removePhoton( (Photon) i.next() );
        }
    }
    
    /**
     * Removes all alpha particles from the model.
     */
    public void removeAllAlphaParticles() {
        ArrayList a = new ArrayList( _alphaParticles );
        Iterator i = a.iterator();
        while ( i.hasNext() ) {
            removeAlphaParticle( (AlphaParticle) i.next() );
        }
    }
    
    /**
     * Adds a specific photon to the model.
     * @param photon
     */
    private void addPhoton( Photon photon ) {
        assert( photon != null );
        _photons.add( photon );
        _model.addModelElement( photon );
    }
    
    /**
     * Removes a specific photon from the model.
     * @param photon
     */
    private void removePhoton( Photon photon ) {
        assert( photon != null );
        _photons.remove( photon );
        _model.removeModelElement( photon );
    }
    
    /**
     * Adds a specific alpha particle to the model.
     * @param alphaParticle
     */
    private void addAlphaParticle( AlphaParticle alphaParticle ) {
        assert( alphaParticle != null );
        _alphaParticles.add( alphaParticle ); 
        _model.addModelElement( alphaParticle );
    }
    
    /**
     * Removes a specific alpha particle from the model.
     * @param alphaParticle
     */
    private void removeAlphaParticle( AlphaParticle alphaParticle ) {
        assert( alphaParticle != null );
        _alphaParticles.remove( alphaParticle );
        _model.removeModelElement( alphaParticle );
    }
    
    /* 
     * Removes photons that are outside the bounds of space.
     */
    private void updatePhotons() {
            ArrayList a = new ArrayList( _photons );
            Iterator i = a.iterator();
            while ( i.hasNext() ) {
                Photon photon = (Photon) i.next();
                Point2D position = photon.getPositionRef();
                if ( !_bounds.contains( position ) ) {
                    removePhoton( photon );
                }
            }
    }
    
    /* 
     * Removes alpha particles that are outside the bounds of space.
     */
    private void updateAlphaParticles() {
        ArrayList a = new ArrayList( _alphaParticles );
        Iterator i = a.iterator();
        while ( i.hasNext() ) {
            AlphaParticle alphaParticle = (AlphaParticle) i.next();
            Point2D position = alphaParticle.getPositionRef();
            if ( !_bounds.contains( position ) ) {
                removeAlphaParticle( alphaParticle );
            }
        }
    }

    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /**
     * Whenever time advances, look for photons and alpha particles
     * that have moved outside the bounds of space.
     * 
     * @param dt
     */
    public void stepInTime( double dt ) {
        updateAlphaParticles();
        updatePhotons();
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Remove all photons and alpha particles when the gun is switched between 
     * firing photons and alpha particles.
     */
    public void update( Observable o, Object arg ) {
        if ( o == _gun ) {
            if ( arg == Gun.PROPERTY_MODE ) {
                removeAllAlphaParticles();
                removeAllPhotons(); 
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // GunFiredListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * When the gun fires a photon, add the photon to the model.
     * 
     * @param event
     */
    public void photonFired( GunFiredEvent event ) {
        addPhoton( event.getPhoton() );
    }
    
    /**
     * When the gun fires an alpha particle, add the alpha particle to the model.
     * 
     * @param event
     */
    public void alphaParticleFired( GunFiredEvent event ) {
        addAlphaParticle( event.getAlphaParticle() );
    }
}
