/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.nuclearphysics2.model.AtomicNucleus.Listener;


public class Uranium238Nucleus extends AtomicNucleus {
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------

    // Number of neutrons and protons in the nucleus upon construction.  The
    // values below are for Uranium-238.
    public static final int ORIGINAL_NUM_PROTONS = 92;
    public static final int ORIGINAL_NUM_NEUTRONS = 146;

    // The "agitation factor" for the various types of nucleus.  The amount of
    // agitation controls how dynamic the nucleus looks on the canvas.  Values
    // must be in the range 0-9.
    private static final int URANIUM_238_AGITATION_FACTOR = 6;
    private static final int URANIUM_239_AGITATION_FACTOR = 6;
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public Uranium238Nucleus(NuclearPhysics2Clock clock, Point2D position){
        super(clock, position, ORIGINAL_NUM_PROTONS, ORIGINAL_NUM_NEUTRONS);
    }
    
    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------
    
    /**
     * Capture a free particle if the nucleus is able to.
     * 
     * @param freeParticle - The free particle that could potentially be
     * captured.
     * @return true if the particle is captured, false if not.
     */
    public boolean captureParticle(Nucleon freeParticle){

        boolean retval = false;

        if (_position.distance( freeParticle.getPosition() ) <= getDiameter() / 2){
            
            int totalNumNeutrons = _numFreeNeutrons + (_numAlphas * 2);
            if (totalNumNeutrons == ORIGINAL_NUM_NEUTRONS){
                // We can capture this neutron.
                freeParticle.setPosition( _position );
                if (_dynamic == true){
                    freeParticle.setTunnelingEnabled( true );
                }
                freeParticle.setVelocity( _xVelocity, _yVelocity );
                _constituents.add( freeParticle );
                _numFreeNeutrons++;
                updateAgitationFactor();
    
                // Let the listeners know that the atomic weight has changed.
                int totalNumProtons = _numFreeProtons + _numAlphas * 2;
                totalNumNeutrons = _numFreeNeutrons + (_numAlphas * 2);
                for (int i = 0; i < _listeners.size(); i++){
                    ((Listener)_listeners.get( i )).atomicWeightChanged( this, totalNumProtons, totalNumNeutrons, null );
                }
                
                // Indicate that the nucleus was captured.
                retval = true;
            }
        }
        
        return retval;
    }
    
    /**
     * Resets the nucleus to its original state, before any fission has
     * occurred.
     */
    public void reset(ArrayList freeNeutrons){
        
        // Set acceleration, velocity, and position back to 0.
        setPosition( _origPosition );
        setVelocity( 0, 0 );
        setAcceleration( 0, 0 );
        
        int totalNumNeutrons = _numFreeNeutrons + _numAlphas * 2;
        
        if (totalNumNeutrons == ORIGINAL_NUM_NEUTRONS + 1){
            // We absorbed a neutron, so we need to release it to get back to
            // our original state.
            for (int i = _constituents.size(); i >= 0; i++){
                if (_constituents.get( i ) instanceof Neutron){
                    // This one will do.
                    freeNeutrons.add( _constituents.get(i) );
                    _constituents.remove(i);
                    _numFreeNeutrons--;
                    break;
                }
            }
        }
        
        // Update our agitation level.
        updateAgitationFactor();
        
        // Notify all listeners of the potential position change.
        for (int i = 0; i < _listeners.size(); i++)
        {
            ((Listener)_listeners.get( i )).positionChanged();
        }        
        
        // Notify all listeners of the change to our atomic weight.
        totalNumNeutrons= _numFreeNeutrons + _numAlphas * 2;
        int totalNumProtons = _numFreeProtons + _numAlphas * 2;
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get( i )).atomicWeightChanged( this, totalNumProtons, totalNumNeutrons, null);
        }
    }
    
    //------------------------------------------------------------------------
    // Private and Protected Methods
    //------------------------------------------------------------------------
    
    protected void updateAgitationFactor() {
        
        // Determine the amount of agitation that should be exhibited by this
        // particular nucleus.  This obviously doesn't handle every possible
        // nucleus, so add more if and when they are needed.
        
        int _totalNumProtons = _numFreeProtons + (_numAlphas * 2);
        int _totalNumNeutrons = _numFreeNeutrons + (_numAlphas * 2);
        
        switch (_totalNumProtons){
        
        case 92:
            // Uranium.
            if (_totalNumNeutrons == 146){
                // Uranium 238.
                _agitationFactor = URANIUM_238_AGITATION_FACTOR;
            }
            else if (_totalNumNeutrons == 147){
                // Uranium 236.
                _agitationFactor = URANIUM_239_AGITATION_FACTOR;
            }
            break;
            
        default:
            _agitationFactor = DEFAULT_AGITATION_FACTOR;
            break;
        }        
    }
}
