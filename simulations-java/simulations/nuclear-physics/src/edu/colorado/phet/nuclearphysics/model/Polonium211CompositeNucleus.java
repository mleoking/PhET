/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;

/**
 * This class defines the behavior of the nucleus of Polonium 211, which
 * exhibits alpha decay behavior.
 *
 * @author John Blanco
 */
public class Polonium211CompositeNucleus extends CompositeAtomicNucleus {
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
    
    // Number of neutrons and protons in the nucleus upon construction.  The
    // values below are for Polonium-211.
    public static final int ORIGINAL_NUM_PROTONS = 84;
    public static final int ORIGINAL_NUM_NEUTRONS = 127;

    // The "agitation factor" for the various types of nucleus.  The amount of
    // agitation controls how dynamic the nucleus looks on the canvas.  Values
    // must be in the range 0-9.
    private static final int POLONIUM_211_AGITATION_FACTOR = 5;
    private static final int LEAD_207_AGITATION_FACTOR = 1;

    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------

    // Variable for deciding when alpha decay should occur.
    private double _alphaDecayTime = 0;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public Polonium211CompositeNucleus(NuclearPhysicsClock clock, Point2D position){
        super(clock, position, ORIGINAL_NUM_PROTONS, ORIGINAL_NUM_NEUTRONS);
        
        // Decide when alpha decay will occur.
        _alphaDecayTime = calcPolonium211DecayTime();
    }
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------
    
    public double getDecayTime(){
        return _alphaDecayTime;
    }
    
    //------------------------------------------------------------------------
    // Other Public Methods
    //------------------------------------------------------------------------
    
    /**
     * Resets the nucleus to its original state, before any alpha decay has
     * occurred.
     * 
     * @param alpha - Particle that had previously tunneled out of this nucleus.
     */
    public void reset(AlphaParticle alpha){
        
        // Reset the decay time.
        _alphaDecayTime = calcPolonium211DecayTime();
        
        if (alpha != null){
            // Add the tunneled particle back to our list.
            _constituents.add( 0, alpha );
            _numAlphas++;
            _numNeutrons += 2;
            _numProtons += 2;
            alpha.resetTunneling();

            // Update our agitation level.
            updateAgitationFactor();
            
            // Let the listeners know that the atomic weight has changed.
            notifyAtomicWeightChanged(null);
        }
    }

    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------
    
    /**
     * This method lets this model element know that the clock has ticked.  In
     * response, the nucleus generally 'agitates' a bit, may also perform some
     * sort of decay, and may move.
     */
    protected void handleClockTicked(ClockEvent clockEvent)
    {
        super.handleClockTicked( clockEvent );
        
        // See if alpha decay should occur.
        if ((_alphaDecayTime != 0) && (clockEvent.getSimulationTime() >= _alphaDecayTime ))
        {
            // Pick an alpha particle to tunnel out and make it happen.
            for (int i = 0; i < _constituents.size(); i++)
            {
                if (_constituents.get( i ) instanceof AlphaParticle){
                    
                    // This one will do.  Make it tunnel.
                    AlphaParticle tunnelingParticle = (AlphaParticle)_constituents.get( i );
                    _constituents.remove( i );
                    _numAlphas--;
                    _numProtons -= 2;
                    _numNeutrons -= 2;
                    tunnelingParticle.tunnelOut( _position, _tunnelingRegionRadius + 1.0 );
                    
                    // Update our agitation factor.
                    updateAgitationFactor();
                    
                    // Notify listeners of the change of atomic weight.
                    ArrayList byProducts = new ArrayList(1);
                    byProducts.add( tunnelingParticle );
                    notifyAtomicWeightChanged(byProducts);
                    break;
                }
            }
            
            // Set the decay time to 0 to indicate that no more tunneling out
            // should occur.
            _alphaDecayTime = 0;
        }
    }
    
    protected void updateAgitationFactor() {
        // Determine the amount of agitation that should be exhibited by this
        // particular nucleus.  This obviously doesn't handle every possible
        // nucleus, so add more if and when they are needed.
        
        switch (_numProtons){
        
        case 84:
            // Polonium.
            if (_numNeutrons == 127){
                // Polonium 211.
                _agitationFactor = POLONIUM_211_AGITATION_FACTOR;
            }
            break;
            
        case 82:
            // Lead
            if (_numNeutrons == 125){
                // Lead 207
                _agitationFactor = LEAD_207_AGITATION_FACTOR;
            }
            break;
            
        default:
            // If we reach this point in the code, there is a problem
            // somewhere that should be debugged.
            System.err.println("Error: Unexpected atomic weight in alpha decay nucleus.");
            assert(false);
        }        
    }
    
    /**
     * This method generates a value indicating the number of milliseconds for
     *  a Polonium 211 nucleus to decay.  This calculation is based on the 
     * exponential decay formula and uses the decay constant for Polonium 211.
     * 
     * @return
     */
    private double calcPolonium211DecayTime(){
        double randomValue = _rand.nextDouble();
        if (randomValue > 0.999){
            // Limit the maximum time for decay so that the user isn't waiting
            // around forever.
            randomValue = 0.999;
        }
        double tunnelOutMilliseconds = (-(Math.log( 1 - randomValue ) / 1.343)) * 1000;
        return tunnelOutMilliseconds;
    }
}
