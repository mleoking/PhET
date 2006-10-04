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

import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.hydrogenatom.model.Gun.AlphaParticleFiredEvent;
import edu.colorado.phet.hydrogenatom.model.Gun.AlphaParticleFiredListener;
import edu.colorado.phet.hydrogenatom.model.Gun.PhotonFiredEvent;
import edu.colorado.phet.hydrogenatom.model.Gun.PhotonFiredListener;

/**
 * Space models the space that photons and alpha particles travel through,
 * and where they encounter atoms.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Space implements PhotonFiredListener, AlphaParticleFiredListener, Observer {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Rectangle2D _bounds;
    private IClock _clock;
    private Gun _gun;
    private ArrayList _photons; // array of Photon
    private ArrayList _alphaParticles; // array of AlphaParticle
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Space( Rectangle2D bounds, IClock clock, Gun gun ) {
        
        _bounds = new Rectangle2D.Double();
        _gun = gun;
        _clock = clock;
        _bounds.setRect( bounds );
        _photons = new ArrayList();
        _alphaParticles = new ArrayList();
        
        _clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                updatePhotons();
                updateAlphaParticles();
            }
        });
        
        _gun.addObserver( this );
        _gun.addPhotonFiredListener( this );
        _gun.addAlphaParticleFiredListener( this );
    }
    
    //----------------------------------------------------------------------------
    // Photon and Alpha Particle management
    //----------------------------------------------------------------------------
    
    public void removeAllPhotons() {
        System.out.println( "Space.removeAllPhotons" );//XXX
        ArrayList a = new ArrayList( _photons );
        Iterator i = a.iterator();
        while ( i.hasNext() ) {
            removePhoton( (Photon) i.next() );
        }
    }
    
    public void removeAllAlphaParticles() {
        System.out.println( "Space.removeAllAlphaParticles" );//XXX
        ArrayList a = new ArrayList( _alphaParticles );
        Iterator i = a.iterator();
        while ( i.hasNext() ) {
            removeAlphaParticle( (AlphaParticle) i.next() );
        }
    }
    
    private void addPhoton( Photon photon ) {
        System.out.println( "Space.addPhoton " + photon );//XXX
        _photons.add( photon );
        _clock.addClockListener( photon );
        //TODO add to view
    }
    
    private void removePhoton( Photon photon ) {
        System.out.println( "Space.removePhoton " + photon );//XXX
        _photons.remove( photon );
        _clock.removeClockListener( photon );
        //TODO remove from view
    }
    
    private void addAlphaParticle( AlphaParticle alphaParticle ) {
        _alphaParticles.add( alphaParticle ); 
        _clock.addClockListener( alphaParticle );
        //TODO add to view
    }
    
    private void removeAlphaParticle( AlphaParticle alphaParticle ) {
        _alphaParticles.remove( alphaParticle );
        _clock.removeClockListener( alphaParticle );
        //TODO remove from view
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
    // PhotonFiredListener implementation
    //----------------------------------------------------------------------------
    
    public void photonFired( PhotonFiredEvent event ) {
        addPhoton( event.getPhoton() );
    }

    //----------------------------------------------------------------------------
    // AlphaParticleFiredListener implementation
    //----------------------------------------------------------------------------
    
    public void alphaParticleFired( AlphaParticleFiredEvent event ) {
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
}
