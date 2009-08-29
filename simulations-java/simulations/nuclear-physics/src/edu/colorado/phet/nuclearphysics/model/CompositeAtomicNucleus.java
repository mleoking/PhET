/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.common.model.Nucleon;
import edu.colorado.phet.nuclearphysics.common.model.SubatomicParticle;
import edu.colorado.phet.nuclearphysics.common.model.Nucleon.NucleonType;

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
    
    // Maximum value for agitation.
    protected static final int MAX_AGITATION_FACTOR = 9;
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    // List of the constituent particles that comprise this nucleus.
    protected ArrayList<SubatomicParticle> _constituents;
    
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

    public CompositeAtomicNucleus(NuclearPhysicsClock clock, Point2D position, int numProtons, int numNeutrons, 
    		double decayTimeScalingFactor){
        
        super(clock, position, numProtons, numNeutrons, decayTimeScalingFactor);
        
        // Figure out the proportion of the protons and neutrons that will be
        // tied up in alpha particles.  This is not based on any formula, just
        // worked out with the educators as something that looks good and is
        // representative enough of reality to work for this sim.  Note that
        // below a certain atomic weight so don't put ANY nucleons into alpha
        // particles.
        if (_numProtons + _numNeutrons > 50){
        	_numAlphas = ((numProtons + numNeutrons) / 2) / 4;  // Assume half of all particles are tied up in alphas.
        }
        
        // Add the constituent particles that make up this nucleus.  We do
        // this in such a way that the particles are interspersed in the list,
        // particularly towards the end of the list, since this works out
        // better for the view.
        _constituents = new ArrayList<SubatomicParticle>();
        int numFreeProtons = _numProtons - (_numAlphas * 2);
        int numFreeNeutrons = _numNeutrons - (_numAlphas * 2);
        int maxParticles = Math.max( numFreeProtons, numFreeNeutrons );
        maxParticles = Math.max( maxParticles, _numAlphas);
        for (int i = (maxParticles - 1); i >= 0; i--){
            if (i < _numAlphas){
                _constituents.add( new AlphaParticle(0, 0) );
            }
            if (i < numFreeProtons){
                _constituents.add( new Nucleon(NucleonType.PROTON, 0, 0, true) );
            }
            if (i < numFreeNeutrons){
                _constituents.add( new Nucleon(NucleonType.NEUTRON, 0, 0, true) );
            }
        }
        
        // Set initial positions of all nucleons.
        setInitialNucleonPositions();

        // Set the initial agitation factor.
        updateAgitationFactor();
    }
    
    /**
     * This constructor is used to create a nucleus when the constituents of
     * the nucleus (i.e. protons, neutrons, and alpha particles) already exist.
     * This is generally used to create a "daughter nucleus" when an existing
     * nucleus splits.
     */
    public CompositeAtomicNucleus(NuclearPhysicsClock clock, Point2D position, 
    		ArrayList<SubatomicParticle> constituents)
    {
        // Create an empty nucleus.
        this(clock, position, 0, 0, 1);

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
            else if (constituents.get( i ) instanceof Nucleon){
            	Nucleon nucleon = (Nucleon)constituents.get( i );
            	if (nucleon.getNucleonType() == NucleonType.PROTON){
            		_numNeutrons++;
            	}
            	else{
            		_numProtons++;
            	}
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
                SubatomicParticle constituent = null;
                for (int i = 0; i < numConstituents; i++){
                    constituent = (SubatomicParticle)_constituents.get(i);
                    double newPosX = constituent.getPositionReference().x + _xVelocity; 
                    double newPosY = constituent.getPositionReference().y + _yVelocity;
                    constituent.setPosition( newPosX, newPosY );
                }
            }
            
            // Move the constituent particles to create the visual effect of a
            // very dynamic nucleus.  In order to allow different levels of
            // agitation, we don't necessarily move all particles every time.
            if (_agitationFactor > 0){
                
                if (_numNeutrons + _numProtons > 20){
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
	                    SubatomicParticle constituent = (SubatomicParticle)_constituents.get( i );
	                   	constituent.tunnel( _position, 0, getDiameter()/2, tunnelingRegion );
	                }
	                _agitationCount = (_agitationCount + 1) % agitationIncrement;
                }
                else{
                	// Having a small number of nucleons tunneling looks
                	// too weird, so these nucleons just vibrate a little
                	// instead.
                	if (_agitationFactor > 0){
		                int agitationIncrement = MAX_AGITATION_FACTOR - _agitationFactor + 1;
		                
		                for (int i = _agitationCount; i < _constituents.size(); i+=agitationIncrement)
		                {
		                    SubatomicParticle constituent = (SubatomicParticle)_constituents.get( i );
		                   	constituent.jitter();
		                }
		                _agitationCount = (_agitationCount + 1) % agitationIncrement;
                	}
                }
            }
    	}
    }
    
    private void setInitialNucleonPositions(){
    	if (_constituents.size() == 3){
    		// This is a special case of a 3-neucleon nucleus.  Position all
    		// nucleons to be initially visible.
        	double rotationOffset = Math.PI;  // In radians, arbitrary and just for looks.
        	double distanceFromCenter = NuclearPhysicsConstants.NUCLEON_DIAMETER / 2 / Math.cos(Math.PI / 6);
        	for (int i = 0; i < 3; i++){
        		double angle = ( Math.PI * 2 / 3 ) * i + rotationOffset;
                double xOffset = Math.sin( angle ) * distanceFromCenter;
                double yOffset = Math.cos( angle ) * distanceFromCenter;
        		_constituents.get(i).setPosition( getPositionReference().getX() + xOffset,
        				getPositionReference().getY() + yOffset );
        	}
    	}
    	else if (_constituents.size() < 28){
    		// Arrange the nuclei in such a way that the nucleus as a whole
    		// ends up looking pretty round.
    		double minDistance = NuclearPhysicsConstants.NUCLEON_DIAMETER / 4;
    		double maxDistance = NuclearPhysicsConstants.NUCLEON_DIAMETER / 2;
    		double distanceIncrement = NuclearPhysicsConstants.NUCLEON_DIAMETER / 5;
    		int numberToPlacePerCycle = 2;
    		int numberOfNucleiPlaced = 0;
    		while (numberOfNucleiPlaced < _constituents.size()){
    			for (int i = 0; i < numberToPlacePerCycle; i++){
    				SubatomicParticle particle = _constituents.get(_constituents.size() - 1 - numberOfNucleiPlaced);
    				placeNucleon(particle, _position, minDistance, maxDistance, 
    						_placementZoneAngles[chooseNextPlacementZoneIndex()]);
                	numberOfNucleiPlaced++;
                	if (numberOfNucleiPlaced >= _constituents.size()){
                		break;
                	}
    			}
    			minDistance += distanceIncrement;
    			maxDistance += distanceIncrement;
    			numberToPlacePerCycle += 6;
    		}
    	}
    	else{
    		// Have each particle place itself randomly somewhere within the
    		// radius of the nucleus.
            double tunnelingRegion = Math.min(_tunnelingRegionRadius, getDiameter() * 1.5);
    		for (SubatomicParticle particle : _constituents){
            	particle.tunnel( _position, 0, getDiameter()/2, tunnelingRegion );
    		}
    	}
    }
    
    private void placeNucleon(SubatomicParticle nucleon, Point2D centerPos, double minDistance, double maxDistance,
    		PlacementZoneAngle placementZoneAngle ){
    	
    	double distance = minDistance + RAND.nextDouble() * (maxDistance - minDistance);
    	double angle = placementZoneAngle.getMinAngle() + RAND.nextDouble() * 
    		(placementZoneAngle.getMaxAngle() - placementZoneAngle.getMinAngle());
    	double xPos = centerPos.getX() + Math.cos(angle) * distance;
    	double yPos = centerPos.getY() + Math.sin(angle) * distance;
    	nucleon.setPosition(xPos, yPos);
    }
    
    /**
     * Class used to define min and max angle for placing nuclei, used for
     * initializing nucleus positions to a random yet structured
     * configuration.
     */
    private static class PlacementZoneAngle {
    	private final double minAngle;
    	private final double maxAngle;
    	
		public PlacementZoneAngle(double minAngle, double maxAngle) {
			this.minAngle = minAngle;
			this.maxAngle = maxAngle;
		}

		public double getMinAngle() {
			return minAngle;
		}

		public double getMaxAngle() {
			return maxAngle;
		}
		
		public String toString(){
			return ("min = " + minAngle + ", max = " + maxAngle );
		}
    }
    
    private int currentPlacementZoneIndex = 0;
    
    private int chooseNextPlacementZoneIndex(){
    	currentPlacementZoneIndex = (currentPlacementZoneIndex + 1 + _placementZoneAngles.length / 2) % _placementZoneAngles.length;
    	return currentPlacementZoneIndex;
    }
    
    private static PlacementZoneAngle [] _placementZoneAngles;
    
    static {
    	// Initialize the placement zones to be on opposite sides of one
    	// another.
    	int numZones = 8;
    	assert numZones % 2 == 0;
    	_placementZoneAngles = new PlacementZoneAngle[numZones];
    	double angleIncrement = 2 * Math.PI / (double)numZones;
    	for (int i = 0; i < numZones; i++){
    		_placementZoneAngles[i] = new PlacementZoneAngle(i * angleIncrement, (i+1) * angleIncrement);
    	}
    }
}
