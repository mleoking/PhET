// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.rutherfordscattering.model;

import java.util.ArrayList;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.rutherfordscattering.event.GunFiredEvent;
import edu.colorado.phet.rutherfordscattering.event.GunFiredListener;
import edu.colorado.phet.rutherfordscattering.event.ParticleEvent;
import edu.colorado.phet.rutherfordscattering.event.ParticleListener;

/**
 * RSModel is the model for this simulation.
 * The model consists of "space" that contain:
 * - 1 gun
 * - 1 atom
 * - N alpha particles
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RSModel extends ClockAdapter implements GunFiredListener {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Gun _gun;
    private Space _space;
    private AbstractAtom _atom;
    private ArrayList _alphaParticles; // array of AlphaParticle
    private EventListenerList _listenerList;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public RSModel( IClock clock, Gun gun, Space space, AbstractAtom atom ) {
        super();
        
        clock.addClockListener( this );
        
        _gun = gun;
        _gun.addGunFiredListener( this );
        
        _space = space;
        
        _atom = atom;
        
        _alphaParticles = new ArrayList();
        _listenerList = new EventListenerList();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public Space getSpace() {
        return _space;
    }
    
    public Gun getGun() {
        return _gun;
    }
    
    public AbstractAtom getAtom() {
        return _atom;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement management
    //----------------------------------------------------------------------------
    
    /*
     * Adds a particle.
     * 
     * @param particle
     */
    private void addParticle( AlphaParticle particle ) {
        _alphaParticles.add( particle );
        fireParticleAdded( new ParticleEvent( this, particle ) );
    }

    /*
     * Removes a particle.
     * 
     * @param particle
     */
    private void removeParticle( AlphaParticle particle ) {
        _alphaParticles.remove( particle );
        fireParticleRemoved( new ParticleEvent( this, particle ) );
    }
    
    /**
     * Removes all alpha particles from the model.
     */
    public void removeAllAlphaParticles() {
        if ( _alphaParticles.size() > 0 ) {
            Object[] alphaParticles = _alphaParticles.toArray(); // copy, this operation deletes from list
            for ( int i = 0; i < alphaParticles.length; i++ ) {
                removeParticle( (AlphaParticle) alphaParticles[i] );
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // ClockListener overrides
    //----------------------------------------------------------------------------
    
    /**
     * Advances the simulation when the clock ticks.
     * 
     * @param event
     */
    public void clockTicked( ClockEvent event ) {
        final double dt = event.getSimulationTimeChange();
        _gun.stepInTime( dt );
        _atom.stepInTime( dt );
        moveParticles( dt );
        cullParticles();
    }
    
    /*
     * Moves alpha particles.
     */
    private void moveParticles( double dt ) {
        if ( _alphaParticles.size() > 0 ) {
            Object[] alphaParticles = _alphaParticles.toArray(); // copy, this operation may delete from list
            for ( int i = 0; i < alphaParticles.length; i++ ) {
                AlphaParticle alphaParticle = (AlphaParticle) alphaParticles[i];
                _atom.moveAlphaParticle( dt, alphaParticle );
            }
        }
    }
    
    /*
     * Culls alpha particles that have left the bounds of space.
     */
    private void cullParticles() {
        if ( _alphaParticles.size() > 0 ) {
            Object[] alphaParticles = _alphaParticles.toArray(); // copy, this operation may delete from list
            for ( int i = 0; i < alphaParticles.length; i++ ) {
                AlphaParticle alphaParticle = (AlphaParticle) alphaParticles[i];
                if ( !_space.contains( alphaParticle ) ) {
                    removeParticle( alphaParticle );
                }
            }
        }
    } 
    
    //----------------------------------------------------------------------------
    // GunFiredListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * When the gun fires an alpha particle, adds the alpha particle to the model.
     * @param event
     */
    public void alphaParticleFired( GunFiredEvent event ) {
        addParticle( event.getAlphaParticle() );
    }
    
    //----------------------------------------------------------------------------
    // ParticleEvent notification
    //----------------------------------------------------------------------------
    
    /**
     * Adds a ParticleListener.
     * @param listener
     */
    public void addParticleListener( ParticleListener listener ) {
        _listenerList.add( ParticleListener.class, listener );
    }

    /**
     * Removes a ParticleListener.
     * @param listener
     */
    public void removeParticleListener( ParticleListener listener ) {
        _listenerList.remove( ParticleListener.class, listener );
    }

    /*
     * Calls particleAdded for all ParticleListeners.
     */
    private void fireParticleAdded( ParticleEvent event ) {
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ParticleListener.class ) {
                ( (ParticleListener)listeners[i + 1] ).particleAdded( event );
            }
        }
    }
    
    /*
     * Calls particleRemoved for all ParticleListeners.
     */
    private void fireParticleRemoved( ParticleEvent event ) {
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ParticleListener.class ) {
                ( (ParticleListener)listeners[i + 1] ).particleRemoved( event );
            }
        }
    }
}
