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

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.hydrogenatom.event.*;

/**
 * HAModel is the model for this simulation.
 * The model consists of "space" that contain:
 * - 1 gun
 * - 1 hydrogen atom
 * - N photons
 * - N electrons
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HAModel extends Model implements GunFiredListener, PhotonAbsorbedListener, PhotonEmittedListener {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Gun _gun;
    private Space _space;
    private AbstractHydrogenAtom _atom;
    private ArrayList _photons; // array of Photon
    private ArrayList _alphaParticles; // array of AlphaParticle
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public HAModel( IClock clock, Gun gun, Space space ) {
        super( clock );
        
        _gun = gun;
        _gun.addGunFiredListener( this );
        super.addModelElement( _gun );
        
        _space = space;
        super.addModelElement( _space );
        
        _atom = null;
        _photons = new ArrayList();
        _alphaParticles = new ArrayList();
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
    
    public AbstractHydrogenAtom getAtom() {
        return _atom;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement management
    //----------------------------------------------------------------------------
    
    /**
     * When a model element is added, also add it to one of 
     * the lists used for collision detection.
     * 
     * @param modelElement
     */
    public void addModelElement( ModelElement modelElement ) {
        if ( modelElement instanceof Photon ) {
            _photons.add( modelElement );
        }
        else if ( modelElement instanceof AlphaParticle ) {
            _alphaParticles.add( modelElement );
        }
        else if ( modelElement instanceof AbstractHydrogenAtom ) {
            if ( _atom != null ) {
                throw new IllegalArgumentException( "model already contains an AbstractHydrogenAtom" );
            }
            _atom = (AbstractHydrogenAtom) modelElement;
            _atom.addPhotonAbsorbedListener( this );
            _atom.addPhotonEmittedListener( this );
        }
        else if ( modelElement instanceof Gun ) {
            throw new IllegalArgumentException( "Gun must be added in constructor" );
        }
        else if ( modelElement instanceof Space ) {
            throw new IllegalArgumentException( "Space must be added in constructor" );
        }
        else {
            throw new IllegalArgumentException( "unsupported modelElement: " + modelElement.getClass().getName() );
        }
        super.addModelElement( modelElement );
    }

    /**
     * When a model element is removed, also remove it from one of 
     * the lists used for collision detection.
     * 
     * @param modelElement
     */
    public void removeModelElement( ModelElement modelElement ) {
        if ( modelElement instanceof Photon ) {
            _photons.remove( modelElement );
        }
        else if ( modelElement instanceof AlphaParticle ) {
            _alphaParticles.remove( modelElement );
        }
        else if ( modelElement == _atom ) {
            _atom.removePhotonAbsorbedListener( this );
            _atom.removePhotonEmittedListener( this );
            _atom = null;
        }
        else if ( modelElement == _gun ) {
            throw new IllegalArgumentException( "Gun cannot be removed" );
        }
        else if ( modelElement == _space ) {
            throw new IllegalArgumentException( "Space cannot be removed" );
        }
        else {
            throw new IllegalArgumentException( "unsupported modelElement: " + modelElement.getClass().getName() );
        }
        super.removeModelElement( modelElement );
    }
    
    /**
     * Removes all photons from the model.
     */
    public void removeAllPhotons() {
        if ( _photons.size() > 0 ) {
            Object[] photons = _photons.toArray(); // copy, this operation deletes from list
            for ( int i = 0; i < photons.length; i++ ) {
                removeModelElement( (Photon) photons[i] );
            }
        }
    }
    
    /**
     * Removes all alpha particles from the model.
     */
    public void removeAllAlphaParticles() {
        if ( _alphaParticles.size() > 0 ) {
            Object[] alphaParticles = _alphaParticles.toArray(); // copy, this operation deletes from list
            for ( int i = 0; i < alphaParticles.length; i++ ) {
                removeModelElement( (AlphaParticle) alphaParticles[i] );
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // ClockListener overrides
    //----------------------------------------------------------------------------
    
    /**
     * Detect collisions whenever the clock ticks.
     * 
     * @param event
     */
    public void clockTicked( ClockEvent event ) {
        final double dt = event.getSimulationTimeChange();
        _gun.stepInTime( dt );
        _atom.stepInTime( dt );
        moveParticles( dt );
        cullParticles();
//        System.out.println( "photons=" + _photons.size() + " alphaParticles=" + _alphaParticles.size() );//XXX
    }
    
    /*
     * Moves photons and alpha particles.
     */
    private void moveParticles( double dt ) {
        
        if ( _atom != null ) {
            
            // Photon collisions
            if ( _photons.size() > 0 ) {
                Object[] photons = _photons.toArray(); // copy, this operation may delete from list
                for ( int i = 0; i < photons.length; i++ ) {
                    Photon photon = (Photon) photons[i];
                    _atom.movePhoton( photon, dt );
                }
            }

            // Alpha Particle collisions
            if ( _alphaParticles.size() > 0 ) {
                Object[] alphaParticles = _alphaParticles.toArray(); // copy, this operation may delete from list
                for ( int i = 0; i < alphaParticles.length; i++ ) {
                    AlphaParticle alphaParticle = (AlphaParticle) alphaParticles[i];
                    _atom.moveAlphaParticle( alphaParticle, dt );
                }
            }
        }
    }
    
    /*
     * Culls photons and alpha particles that have left the bounds of space.
     */
    private void cullParticles() {
        
        if ( _photons.size() > 0 ) {
            Object[] photons = _photons.toArray(); // copy, this operation may delete from list
            for ( int i = 0; i < photons.length; i++ ) {
                Photon photon = (Photon) photons[i];
                if ( !_space.contains( photon ) ) {
                    removeModelElement( photon );
                }
            }
        }
        
        if ( _alphaParticles.size() > 0 ) {
            Object[] alphaParticles = _alphaParticles.toArray(); // copy, this operation may delete from list
            for ( int i = 0; i < alphaParticles.length; i++ ) {
                AlphaParticle alphaParticle = (AlphaParticle) alphaParticles[i];
                if ( !_space.contains( alphaParticle ) ) {
                    removeModelElement( alphaParticle );
                }
            }
        }
    } 
    
    //----------------------------------------------------------------------------
    // GunFiredListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * When the gun fires a photon, add the photon to the model.
     * @param event
     */
    public void photonFired( GunFiredEvent event ) {
        addModelElement( event.getPhoton() );
    }
    
    /**
     * When the gun fires an alpha particle, add the alpha particle to the model.
     * @param event
     */
    public void alphaParticleFired( GunFiredEvent event ) {
        addModelElement( event.getAlphaParticle() );
    }
    
    //----------------------------------------------------------------------------
    // PhotonAbsorbedListener
    //----------------------------------------------------------------------------

    /**
     * When a photon is absorbed, remove it from the model.
     * @param event
     */
    public void photonAbsorbed( PhotonAbsorbedEvent event ) {
        removeModelElement( event.getPhoton() );
    }

    //----------------------------------------------------------------------------
    // PhotonEmittedListener
    //----------------------------------------------------------------------------

    /**
     * When a photon is emitted, add it to the model.
     * @param event
     */
    public void photonEmitted( PhotonEmittedEvent event ) {
        addModelElement( event.getPhoton() );
    }
}
