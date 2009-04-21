/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.model.Neutron;
import edu.colorado.phet.nuclearphysics.common.model.Nucleon;
import edu.colorado.phet.nuclearphysics.common.model.Proton;

/**
 * This class is used to model the atomic nucleus in the "Fission: One
 * Nucleus" tab.
 *
 * @author John Blanco
 */
public class Uranium235CompositeNucleus extends CompositeAtomicNucleus{

    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------

    // Number of neutrons and protons in the nucleus upon construction.  The
    // values below are for Uranium-235.
    public static final int ORIGINAL_NUM_PROTONS = 92;
    public static final int ORIGINAL_NUM_NEUTRONS = 143;

    // The "agitation factor" for the various types of nucleus.  The amount of
    // agitation controls how dynamic the nucleus looks on the canvas.  Values
    // must be in the range 0-9.
    private static final int URANIUM_235_AGITATION_FACTOR = 4;
    private static final int URANIUM_236_AGITATION_FACTOR = 8;
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------

    // Interval from the time a neutron capture occurs until fission occurs.
    private double _fissionInterval;
    
    // Time at which fission will occur.
    private double _fissionTime = 0;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public Uranium235CompositeNucleus(NuclearPhysicsClock clock, Point2D position, double fissionInterval){
        super(clock, position, ORIGINAL_NUM_PROTONS, ORIGINAL_NUM_NEUTRONS);
        
        _fissionInterval = fissionInterval;
        
        // Set the tunneling region to be more confined than in some of the
        // other panels, since having a bunch of alpha particles flying around
        // the nucleus will like be distracting.
        setTunnelingRegionRadius( (getDiameter() / 2) * 1.1 );
    }
    
    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------
    
    public double getFissionTime(){
        return _fissionTime;
    }
    
    public double getFissionInterval(){
        return _fissionInterval;
    }
    
    /**
     * Capture a free particle if the nucleus is able to.
     * 
     * @param freeParticle - The free particle that could potentially be
     * captured.
     * @return true if the particle is captured, false if not.
     */
    public boolean captureParticle(Nucleon freeParticle){

        boolean retval = false;
        
        if (_numNeutrons == ORIGINAL_NUM_NEUTRONS){
            // We can capture this neutron.
            freeParticle.setTunnelingEnabled( true );
            freeParticle.setPosition( getPositionReference() );
            freeParticle.setVelocity( 0, 0 );
            _constituents.add( freeParticle );
            _numNeutrons++;
            updateAgitationFactor();

            // Let the listeners know that the atomic weight has changed.
            notifyAtomicWeightChanged(null);
            
            // Start a timer to kick off fission.
            _fissionTime = _clock.getSimulationTime() + _fissionInterval;
            
            // Indicate that the nucleus was captured.
            retval = true;
        }
        
        return retval;
    }
    
    /**
     * Resets the nucleus to its original state, before any fission has
     * occurred.
     */
    public void reset(ArrayList freeNeutrons, CompositeAtomicNucleus daughterNucleus){
        
        // Reset the fission time to 0, indicating that it shouldn't occur
        // until something changes.
        _fissionTime = 0;

        // Set acceleration, velocity, and position back to 0.
        setPosition( new Point2D.Double(0, 0) );
        setVelocity( 0, 0 );
        setAcceleration( 0, 0 );
        
        if (_numNeutrons < ORIGINAL_NUM_NEUTRONS){
            // Fission has occurred, so we need to reabsorb the daughter
            // nucleus and two of the free neutrons.
            if (freeNeutrons != null){
                for (int i = 0; i < 2; i++){
                    if ( freeNeutrons.size() >= 2 ){
                        Neutron neutron = (Neutron)freeNeutrons.get( freeNeutrons.size() - 1 - i );
                        neutron.setVelocity( 0, 0 );
                        neutron.setPosition( _position );
                        neutron.setTunnelingEnabled( true );
                        _constituents.add(neutron);
                        _numNeutrons++;
                        freeNeutrons.remove( neutron );
                    }
                    else{
                        // This should never occur, debug it if it does.
                        System.out.println("Error: Unexpected number of free neutrons on reset.");
                        assert false;
                    }
                }
            }
            
            if (daughterNucleus != null){
                
                ArrayList daughterConstituents = daughterNucleus.getConstituents();
                
                for (int i = 0; i < daughterConstituents.size(); i++){
                    
                    Object constituent = daughterConstituents.get( i );
                    
                    if (constituent instanceof AlphaParticle){
                        _numAlphas++;
                        _numProtons += 2;
                        _numNeutrons += 2;
                    }
                    else if (constituent instanceof Neutron){
                        _numNeutrons++;
                    }
                    else if (constituent instanceof Proton){
                        _numProtons++;
                    }
                    else{
                        // This should never happen, and needs to be debugged if
                        // it does.
                        assert false;
                    }
                    
                    _constituents.add(constituent);
                }
            }
        }
        else if (_numNeutrons == ORIGINAL_NUM_NEUTRONS + 1){
            // We have been reset after having absorbed a neutron but before
            // fissioning.  Free a neutron to get back to our original state.
            for (int i = 0; i < _constituents.size(); i++){
                if (_constituents.get( i ) instanceof Neutron){
                    // This one will do.
                    freeNeutrons.add( _constituents.get(i) );
                    _constituents.remove(i);
                    _numNeutrons--;
                    break;
                }
            }
        }
        
        // Position all the nucleons near the new center of the nucleus.
        for ( int i = 0; i < _constituents.size(); i++ )
        {
            AtomicNucleusConstituent constituent = (AtomicNucleusConstituent)_constituents.get( i );
            constituent.tunnel( _position, 0, getDiameter()/2, _tunnelingRegionRadius );
        }
        
        // Update our agitation level.
        updateAgitationFactor();
        
        // Notify all listeners of the potential position change.
        notifyPositionChanged();        
        
        // Notify all listeners of the change to our atomic weight.
        notifyAtomicWeightChanged(null);
    }
    
    //------------------------------------------------------------------------
    // Private and Protected Methods
    //------------------------------------------------------------------------
    
    /**
     * This method lets this model element know that the clock has ticked.  In
     * response, the nucleus generally 'agitates' a bit, may also perform some
     * sort of decay, and may move.
     */
    protected void handleClockTicked(ClockEvent clockEvent)
    {
        super.handleClockTicked( clockEvent );
        
        // See if fission should occur.
        if ((_fissionTime != 0) && (clockEvent.getSimulationTime() >= _fissionTime ))
        {
            // Fission the nucleus.  First pull out three neutrons as 
            // byproducts of this decay event.
            ArrayList byProducts = new ArrayList();
            int neutronByProductCount = 0;
            for (int i = 0; i < _constituents.size() && neutronByProductCount < 3; i++){
                if (_constituents.get( i ) instanceof Neutron){
                    Object newlyFreedNeutron = _constituents.get( i );
                    byProducts.add( newlyFreedNeutron );
                    neutronByProductCount++;
                    _numNeutrons--;
                }
            }
            
            _constituents.removeAll( byProducts );
            
            // Now pull out the needed number of protons, neutrons, and alphas
            // to create the appropriate daughter nucleus.  The daughter
            // nucleus created is Krypton-92, so the number of particles
            // needed is calculated for this particular isotope.
            int numAlphasNeeded = 12;
            int numProtonsNeeded = 12;
            int numNeutronsNeeded = 32;
                
            ArrayList daughterNucleusConstituents = new ArrayList(numAlphasNeeded + numProtonsNeeded + numNeutronsNeeded);
            
            for (int i = 0; i < _constituents.size(); i++){
                Object constituent = _constituents.get( i );
                
                if ((numNeutronsNeeded > 0) && (constituent instanceof Neutron)){
                    daughterNucleusConstituents.add( constituent );
                    numNeutronsNeeded--;
                    _numNeutrons--;
                }
                else if ((numProtonsNeeded > 0) && (constituent instanceof Proton)){
                    daughterNucleusConstituents.add( constituent );
                    numProtonsNeeded--;
                    _numProtons--;
                }
                if ((numAlphasNeeded > 0) && (constituent instanceof AlphaParticle)){
                    daughterNucleusConstituents.add( constituent );
                    numAlphasNeeded--;
                    _numAlphas--;
                    _numNeutrons -= 2;
                    _numProtons -= 2;
                }
                
                if ((numNeutronsNeeded == 0) && (numProtonsNeeded == 0) && (numAlphasNeeded == 0)){
                    // We've got all that we need.
                    break;
                }
            }
            
            _constituents.removeAll( daughterNucleusConstituents );
            
            Point2D location = new Point2D.Double();
            location.setLocation( _position );
            DaughterCompositeNucleus daughterNucleus = new DaughterCompositeNucleus(_clock, location, daughterNucleusConstituents);
            
            // Consolidate all of the byproducts.
            byProducts.add( daughterNucleus );
            
            // Send out the decay event to all listeners.
            notifyAtomicWeightChanged(byProducts);
            
            // Set the fission time to 0 to indicate that no more fissioning
            // should occur.
            _fissionTime = 0;
        }
    }
    
    protected void updateAgitationFactor() {
        
        // Determine the amount of agitation that should be exhibited by this
        // particular nucleus.  This obviously doesn't handle every possible
        // nucleus, so add more if and when they are needed.
        
        switch (_numProtons){
        
        case 92:
            // Uranium.
            if (_numNeutrons == 143){
                // Uranium 235.
                _agitationFactor = URANIUM_235_AGITATION_FACTOR;
            }
            else if (_numNeutrons == 144){
                // Uranium 236.
                _agitationFactor = URANIUM_236_AGITATION_FACTOR;
            }
            break;
            
        default:
            _agitationFactor = DEFAULT_AGITATION_FACTOR;
            break;
        }        
    }
}
