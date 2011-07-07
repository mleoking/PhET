// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.hydrogenatom.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom.AtomListener;
import edu.colorado.phet.hydrogenatom.model.Gun.GunListener;

/**
 * HAModel is the model for this simulation.
 * The model consists of "space" that contain:
 * - 1 gun
 * - 1 hydrogen atom
 * - N photons
 * - N alpha particles
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HAModel extends Model implements GunListener, AtomListener {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Gun _gun;
    private Space _space;
    private AbstractHydrogenAtom _atom;
    private ArrayList<Photon> _photons;
    private ArrayList<AlphaParticle> _alphaParticles;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public HAModel( IClock clock, Gun gun, Space space ) {
        super( clock );
        
        _gun = gun;
        _gun.addGunFiredListener( this );
        addModelElement( _gun );
        
        _space = space;
        addModelElement( _space );
        
        _atom = null;
        _photons = new ArrayList<Photon>();
        _alphaParticles = new ArrayList<AlphaParticle>();
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

    public void setAtom( AbstractHydrogenAtom atom ) {
        if ( _atom != null ) {
            _atom.cleanup();
            removeModelElement( _atom );
        }
        _atom = atom;
        _atom.addAtomListener( this );
        addModelElement( atom );
    }

    //----------------------------------------------------------------------------
    // ModelElement management
    //----------------------------------------------------------------------------

    public void addPhoton( Photon photon ) {
        _photons.add( photon );
        addModelElement( photon );
    }

    public void removePhoton( Photon photon ) {
        _photons.remove( photon );
        removeModelElement( photon );
    }

    public void removeAllPhotons() {
        for ( Photon photon : new ArrayList<Photon>( _photons ) ) {
            removePhoton( photon );
        }
    }

    public void addAlphaParticle( AlphaParticle alphaParticle ) {
        _alphaParticles.add( alphaParticle );
        addModelElement( alphaParticle );
    }

    public void removeAlphaParticle( AlphaParticle alphaParticle ) {
        _photons.remove( alphaParticle );
        removeModelElement( alphaParticle );
    }

    public void removeAllAlphaParticles() {
        for ( AlphaParticle alphaParticle : new ArrayList<AlphaParticle>( _alphaParticles ) ) {
            removeAlphaParticle( alphaParticle );
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
    @Override public void clockTicked( ClockEvent event ) {
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

        for ( Photon photon : new ArrayList<Photon>( _photons ) ) {
            if ( !_space.contains( photon ) ) {
                removePhoton( photon );
            }
        }

        for ( AlphaParticle alphaParticle : new ArrayList<AlphaParticle>( _alphaParticles ) ) {
            if ( !_space.contains( alphaParticle ) ) {
                removeAlphaParticle( alphaParticle );
            }
        }
    }

    //----------------------------------------------------------------------------
    // GunListener implementation
    //----------------------------------------------------------------------------
    
    // When the gun fires a photon, add the photon to the model.
    public void photonFired( Photon photon ) {
        addPhoton( photon );
    }
    
    // When the gun fires an alpha particle, add the alpha particle to the model.
    public void alphaParticleFired( AlphaParticle alphaParticle ) {
        addAlphaParticle( alphaParticle );
    }
    
    //----------------------------------------------------------------------------
    // AtomListener
    //----------------------------------------------------------------------------

    // When a photon is absorbed by the atom, remove it from the model.
    public void photonAbsorbed( Photon photon ) {
        removePhoton( photon );
    }

    // When a photon is emitted by the atom, add it to the model.
    public void photonEmitted( Photon photon ) {
        addPhoton( photon );
    }
}
