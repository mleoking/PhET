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

import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.ClockListener;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.hydrogenatom.model.Gun.GunFiredEvent;
import edu.colorado.phet.hydrogenatom.model.Gun.GunFiredListener;

/**
 * Space models the space that photons and alpha particles travel through,
 * and where they encounter atoms.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Space implements GunFiredListener, Observer, ClockListener, IModelObject {

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
    // Photon and Alpha Particle management
    //----------------------------------------------------------------------------
    
    public void removeAllPhotons() {
        ArrayList a = new ArrayList( _photons );
        Iterator i = a.iterator();
        while ( i.hasNext() ) {
            removePhoton( (Photon) i.next() );
        }
    }
    
    public void removeAllAlphaParticles() {
        ArrayList a = new ArrayList( _alphaParticles );
        Iterator i = a.iterator();
        while ( i.hasNext() ) {
            removeAlphaParticle( (AlphaParticle) i.next() );
        }
    }
    
    private void addPhoton( Photon photon ) {
        assert( photon != null );
        _photons.add( photon );
        _model.addModelObject( photon );
    }
    
    private void removePhoton( Photon photon ) {
        assert( photon != null );
        _photons.remove( photon );
        _model.removeModelObject( photon );
    }
    
    private void addAlphaParticle( AlphaParticle alphaParticle ) {
        assert( alphaParticle != null );
        _alphaParticles.add( alphaParticle ); 
        _model.addModelObject( alphaParticle );
    }
    
    private void removeAlphaParticle( AlphaParticle alphaParticle ) {
        assert( alphaParticle != null );
        _alphaParticles.remove( alphaParticle );
        _model.removeModelObject( alphaParticle );
    }
    
    // Remove photons that are outside the space.
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
    
    // Remove alpha particles that are outside the space.
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
    // GunFiredListener implementation
    //----------------------------------------------------------------------------
    
    public void photonFired( GunFiredEvent event ) {
        addPhoton( event.getPhoton() );
    }
    
    public void alphaParticleFired( GunFiredEvent event ) {
        addAlphaParticle( event.getAlphaParticle() );
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
    // ClockListener implementation
    //----------------------------------------------------------------------------
    
    public void clockTicked( ClockEvent clockEvent ) {
        updateAlphaParticles();
        updatePhotons();
    }

    public void clockStarted( ClockEvent clockEvent ) {}

    public void clockPaused( ClockEvent clockEvent ) {}

    public void simulationTimeChanged( ClockEvent clockEvent ) {}

    public void simulationTimeReset( ClockEvent clockEvent ) {}
}
