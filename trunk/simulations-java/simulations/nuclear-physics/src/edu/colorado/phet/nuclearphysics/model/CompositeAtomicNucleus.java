/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;

/**
 * This class represents an atomic nucleus model element that is composed of a
 * bunch of smaller particles (i.e. nucleons and alpha particles), hence the
 * "Composite" portion of the name.
 *
 * @author John Blanco
 */
public abstract class CompositeAtomicNucleus extends AtomicNucleus {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    // Default value for agitation.
    protected static final int DEFAULT_AGITATION_FACTOR = 5;
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    // List of the constituent particles that comprise this nucleus.
    protected ArrayList _constituents;
    
    // Used to implement the 'agitation' behavior, i.e. to make the nucleus
    // appear to be in constant dynamic motion.
    private int _agitationCount = 0;
    
    // Amount of agitation exhibited by nucleus, from 0 to 9.
    protected int _agitationFactor = DEFAULT_AGITATION_FACTOR;
    
    // Used for various random calculations.
    protected Random _rand = new Random();
    
    // The number of alpha particles that will be part of the constituents
    // of this nucleus.
    protected int _numAlphas = 0;
    
    // Paused state variable.
    protected boolean _paused = false;
    
    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

    public CompositeAtomicNucleus(NuclearPhysicsClock clock, Point2D position, int numProtons, int numNeutrons){
        
        super(clock, position, numProtons, numNeutrons);
        
        // Figure out the proportion of the protons and neutrons that will be
        // tied up in alpha particles.  This is not based on any formula, just
        // worked out with the educators as something that looks good and is
        // representative enough of reality to work for this sim.
        _numAlphas = ((numProtons + numNeutrons) / 2) / 4;  // Assume half of all particles are tied up in alphas.
        
        // Add the constituent particles that make up this nucleus.  We do
        // this in such a way that the particles are interspersed in the list,
        // particularly towards the end of the list, since this works out
        // better for the view.
        _constituents = new ArrayList();
        int numFreeProtons = _numProtons - (_numAlphas * 2);
        int numFreeNeutrons = _numNeutrons - (_numAlphas * 2);
        int maxParticles = Math.max( numFreeProtons, numFreeNeutrons );
        maxParticles = Math.max( maxParticles, _numAlphas);
        for (int i = (maxParticles - 1); i >= 0; i--){
            if (i < _numAlphas){
                _constituents.add( new AlphaParticle(0, 0) );
            }
            if (i < numFreeProtons){
                _constituents.add( new Proton(0, 0, true) );
            }
            if (i < numFreeNeutrons){
                _constituents.add( new Neutron(0, 0, true) );
            }
        }
        
        // Set the initial agitation factor.
        updateAgitationFactor();
    }
    
    /**
     * This constructor is used to create a nucleus when the constituents of
     * the nucleus (i.e. protons, neutrons, and alpha particles) already exist.
     * This is generally used to create a "daughter nucleus" when an existing
     * nucleus splits.
     */
    public CompositeAtomicNucleus(NuclearPhysicsClock clock, Point2D position, ArrayList constituents)
    {
        // Create an empty nucleus.
        this(clock, position, 0, 0);

        // Figure out the makeup of the constituents.
        _numAlphas = 0;
        _numProtons = 0;
        _numNeutrons = 0;
        for (int i = 0; i < constituents.size(); i++){
            if (constituents.get( i ) instanceof AlphaParticle){
                _numAlphas++;
                _numNeutrons += 2;
                _numProtons += 2;
            }
            else if (constituents.get( i ) instanceof Neutron){
                _numNeutrons++;
            }
            else if (constituents.get( i ) instanceof Proton){
                _numProtons++;
            }
            else{
                // Should never happen, debug if it does.
                assert false;
                System.err.println("Error: Unexpected nucleus constituent type.");
            }
        }
        
        // Keep the array of constituents.
        _constituents = constituents;
        
        // recalculate our diameter.
        updateDiameter();
        
        // Update our agitation factor.
        updateAgitationFactor();
    }

    //------------------------------------------------------------------------
    // Accessor methods
    //------------------------------------------------------------------------

    public ArrayList getConstituents(){
        return _constituents;
    }

    public void setPaused(boolean paused){
    	_paused = paused;
    }
    
    public boolean getPaused(){
    	return _paused;
    }

    //------------------------------------------------------------------------
    // Private and Protected Methods
    //------------------------------------------------------------------------

    /**
     * Update the agitation factor for this nucleus.
     */
    abstract protected void updateAgitationFactor();

    /**
     * Handle a clock tick event.
     */
    protected void handleClockTicked(ClockEvent clockEvent){
        
    	if (!_paused){
            super.handleClockTicked(clockEvent);

            if ((_xVelocity != 0) || (_yVelocity != 0)){
                // Move the constituent particles by the velocity amount.
                int numConstituents = _constituents.size();
                AtomicNucleusConstituent constituent = null;
                for (int i = 0; i < numConstituents; i++){
                    constituent = (AtomicNucleusConstituent)_constituents.get(i);
                    double newPosX = constituent.getPositionReference().x + _xVelocity; 
                    double newPosY = constituent.getPositionReference().y + _yVelocity;
                    constituent.setPosition( newPosX, newPosY );
                }
            }
            
            // Move the constituent particles to create the visual effect of a
            // very dynamic nucleus.  In order to allow different levels of
            // agitation, we don't necessarily move all particles every time.
            if (_agitationFactor > 0){
                
                // Calculate the increment to be used for creating the agitation
                // effect.
                
                int agitationIncrement = 20 - (2 * _agitationFactor);
                assert agitationIncrement > 0;
                
                if (agitationIncrement <= 0){
                    agitationIncrement = 5;
                }
                    
                // Limit the tunneling distance, because otherwise it can
                // look like alpha particles are leaving the nucleus when
                // they aren't.
                double tunnelingRegion = Math.min(_tunnelingRegionRadius, getDiameter() * 1.5);
                for (int i = _agitationCount; i < _constituents.size(); i+=agitationIncrement)
                {
                    AtomicNucleusConstituent constituent = (AtomicNucleusConstituent)_constituents.get( i );
                    constituent.tunnel( _position, 0, getDiameter()/2, tunnelingRegion );
                }
                _agitationCount = (_agitationCount + 1) % agitationIncrement;
            }
    	}
    }
}
