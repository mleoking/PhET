/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.model;

import java.util.ArrayList;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.rutherfordscattering.event.GunFiredEvent;
import edu.colorado.phet.rutherfordscattering.event.GunFiredListener;

/**
 * RSModel is the model for this simulation.
 * The model consists of "space" that contain:
 * - 1 gun
 * - 1 atom
 * - N alpha particles
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RSModel extends Model implements GunFiredListener {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Gun _gun;
    private Space _space;
    private AbstractAtom _atom;
    private ArrayList _alphaParticles; // array of AlphaParticle
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public RSModel( IClock clock, Gun gun, Space space, AbstractAtom atom ) {
        super( clock );
        
        _gun = gun;
        _gun.addGunFiredListener( this );
        super.addModelElement( _gun );
        
        _space = space;
        super.addModelElement( _space );
        
        _atom = atom;
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
    
    public AbstractAtom getAtom() {
        return _atom;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement management
    //----------------------------------------------------------------------------
    
    /**
     * Dynamically adds a model element.
     * 
     * @param modelElement
     */
    public void addModelElement( ModelElement modelElement ) {
        if ( modelElement instanceof AlphaParticle ) {
            _alphaParticles.add( modelElement );
        }
        else if ( modelElement instanceof AbstractAtom ) {
            throw new IllegalArgumentException( "Atom must be added in constructor" );
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
     * Dynamically removes a model element.
     * 
     * @param modelElement
     */
    public void removeModelElement( ModelElement modelElement ) {
        if ( modelElement instanceof AlphaParticle ) {
            _alphaParticles.remove( modelElement );
        }
        else if ( modelElement == _atom ) {
            throw new IllegalArgumentException( "Atom cannot be removed" );
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
                    removeModelElement( alphaParticle );
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
        addModelElement( event.getAlphaParticle() );
    }
}
