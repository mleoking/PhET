package edu.colorado.phet.nuclearphysics2.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;


public class DaughterNucleus extends AtomicNucleus {

    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------

    // The "agitation factor" for the various types of nucleus.  The amount of
    // agitation controls how dynamic the nucleus looks on the canvas.  Values
    // must be in the range 0-9.
    private static final int KRYPTON_92_AGITATION_FACTOR = 7;
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------

    // Variable for deciding when alpha decay should occur.
    private double _fissionTime = 0;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public DaughterNucleus(NuclearPhysics2Clock clock, Point2D position, ArrayList constituents){
        super(clock, position, constituents);
    }
    
    //------------------------------------------------------------------------
    // Private and Protected Methods
    //------------------------------------------------------------------------

    @Override
    protected void updateAgitationFactor() {
        
        // Determine the amount of agitation that should be exhibited by this
        // particular nucleus.  This obviously doesn't handle every possible
        // nucleus, so add more if and when they are needed.
        
        int _totalNumProtons = _numProtons + (_numAlphas * 2);
        int _totalNumNeutrons = _numNeutrons + (_numAlphas * 2);
        
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
