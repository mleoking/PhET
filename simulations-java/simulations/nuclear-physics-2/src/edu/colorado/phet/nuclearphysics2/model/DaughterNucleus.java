package edu.colorado.phet.nuclearphysics2.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.nuclearphysics2.model.AtomicNucleus.Listener;


public class DaughterNucleus extends AtomicNucleus {

    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------

    // The "agitation factor" for the various types of nucleus.  The amount of
    // agitation controls how dynamic the nucleus looks on the canvas.  Values
    // must be in the range 0-9.
    private static final int KRYPTON_92_AGITATION_FACTOR = 8;
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public DaughterNucleus(NuclearPhysics2Clock clock, Point2D position, ArrayList constituents){
        super(clock, position, constituents);
        
        // Set the tunneling region to be more confined than in some of the
        // other panels, since having a bunch of alpha particles flying around
        // the nucleus will like be distracting.
        setTunnelingRegionRadius( (getDiameter() / 2) * 1.1 );
    }
    
    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------
    
    /**
     * Reset the daughter nucleus, which means it should surrender all its
     * constituent particles.
     */
    public void reset(){

        _constituents.removeAll( _constituents );
        _numAlphas = 0;
        _numFreeNeutrons = 0;
        _numFreeProtons = 0;
        
        for (int i = 0; i < _listeners.size(); i++){
            // Let the listeners know about the change.
            ((Listener)_listeners.get( i )).atomicWeightChanged( this, 0, 0, null );
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
        
        case 36:
            // Krypton.
            if (_totalNumNeutrons == 56){
                // Krypton 92.
                _agitationFactor = KRYPTON_92_AGITATION_FACTOR;
            }
            else{
                // Some other Krypton isotope.
                _agitationFactor = DEFAULT_AGITATION_FACTOR;
            }
            break;
            
        default:
            _agitationFactor = DEFAULT_AGITATION_FACTOR;
            break;
        }        
    }
}
