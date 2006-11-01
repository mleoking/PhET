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
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom.PhotonAbsorbedEvent;
import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom.PhotonAbsorbedListener;
import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom.PhotonEmittedEvent;
import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom.PhotonEmittedListener;
import edu.colorado.phet.hydrogenatom.model.Gun.GunFiredEvent;
import edu.colorado.phet.hydrogenatom.model.Gun.GunFiredListener;

/**
 * HAModel is the model for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HAModel extends Model implements Observer, GunFiredListener, PhotonAbsorbedListener, PhotonEmittedListener {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Gun _gun;
    private Space _space;

    private ArrayList _atoms; // array of AbstractHydrogenAtom
    private ArrayList _photons; // array of Photon
    private ArrayList _alphaParticles; // array of AlphaParticle
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public HAModel( IClock clock, Gun gun, Space space ) {
        super( clock );
        
        _gun = gun;
        _gun.addGunFiredListener( this );
        _gun.addObserver( this );
        super.addModelElement( _gun );
        
        _space = space;
        super.addModelElement( _space );
        
        _atoms = new ArrayList();
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
        if ( modelElement instanceof AbstractHydrogenAtom ) {
            AbstractHydrogenAtom atom = (AbstractHydrogenAtom) modelElement;
            _atoms.add( atom );
            atom.addPhotonAbsorbedListener( this );
            atom.addPhotonEmittedListener( this );
        }
        else if ( modelElement instanceof Photon ) {
            _photons.add( modelElement );
        }
        else if ( modelElement instanceof AlphaParticle ) {
            _alphaParticles.add( modelElement );
        }
        else if ( modelElement instanceof Gun ) {
            throw new IllegalArgumentException( "Gun must be added in constructor" );
        }
        else if ( modelElement instanceof Space ) {
            throw new IllegalArgumentException( "Space must be added in constructor" );
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
        if ( modelElement instanceof AbstractHydrogenAtom ) {
            AbstractHydrogenAtom atom = (AbstractHydrogenAtom) modelElement;
            _atoms.remove( atom );
            atom.removePhotonAbsorbedListener( this );
            atom.removePhotonEmittedListener( this );
        }
        else if ( modelElement instanceof Photon ) {
            _photons.remove( modelElement );
        }
        else if ( modelElement instanceof AlphaParticle ) {
            _alphaParticles.remove( modelElement );
        }
        else if ( modelElement instanceof Gun ) {
            throw new IllegalArgumentException( "Gun cannot be removed" );
        }
        else if ( modelElement instanceof Space ) {
            throw new IllegalArgumentException( "Space cannot be removed" );
        }
        super.removeModelElement( modelElement );
    }
    
    /**
     * Removes all photons from the model.
     */
    public void removeAllPhotons() {
        ArrayList photonsCopy = new ArrayList( _photons ); // copy
        Iterator i = photonsCopy.iterator();
        while ( i.hasNext() ) {
            removeModelElement( (Photon) i.next() );
        }
    }
    
    /**
     * Removes all alpha particles from the model.
     */
    public void removeAllAlphaParticles() {
        ArrayList alphaParticlesCopy = new ArrayList( _alphaParticles ); // copy
        Iterator i = alphaParticlesCopy.iterator();
        while ( i.hasNext() ) {
            removeModelElement( (AlphaParticle) i.next() );
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
        super.clockTicked( event );
        detectCollisions();
        detectSpaceBounds();
//        System.out.println( "photons=" + _photons.size() + " alphaParticles=" + _alphaParticles.size() );//XXX
    }
    
    /*
     * Detect collisions by iterating through each atom-photon
     * and atom-alphaParticle pair. The atom is responsible for
     * detecting and handling any collisions.
     */
    private void detectCollisions() {
        Iterator i = _atoms.iterator();
        while ( i.hasNext() ) {
            
            AbstractHydrogenAtom atom = (AbstractHydrogenAtom)i.next();
            
            ArrayList photonsCopy = new ArrayList( _photons ); // copy
            Iterator p = photonsCopy.iterator();
            while ( p.hasNext() ) {
                Photon photon = (Photon)p.next();
                atom.detectCollision( photon );
            }
            
            ArrayList alphaParticlesCopy = new ArrayList( _alphaParticles ); // copy
            Iterator a = alphaParticlesCopy.iterator();
            while ( a.hasNext() ) {
                AlphaParticle alphaParticle = (AlphaParticle)a.next();
                atom.detectCollision( alphaParticle );
            }
        }
    }
    
    /*
     * Detect and remove photons and alpha particles that have left 
     * the bounds of space.
     */
    private void detectSpaceBounds() {
        
        ArrayList photonsCopy = new ArrayList( _photons ); // copy
        Iterator p = photonsCopy.iterator();
        while ( p.hasNext() ) {
            Photon photon = (Photon)p.next();
            if ( !_space.contains( photon ) ) {
                removeModelElement( photon );
            }
        }
        
        ArrayList alphaParticlesCopy = new ArrayList( _alphaParticles ); // copy
        Iterator a = alphaParticlesCopy.iterator();
        while ( a.hasNext() ) {
            AlphaParticle alphaParticle = (AlphaParticle)a.next();
            if ( !_space.contains( alphaParticle ) ) {
                removeModelElement( alphaParticle );
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Remove all photons and alpha particles when the gun is
     * switched between firing photons and alpha particles.
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
