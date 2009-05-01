/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.decayrates;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.model.AbstractDecayNucleus;
import edu.colorado.phet.nuclearphysics.model.AdjustableHalfLifeNucleus;
import edu.colorado.phet.nuclearphysics.model.Carbon14Nucleus;
import edu.colorado.phet.nuclearphysics.model.Uranium238Nucleus;
import edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus.MultiNucleusDecayModel;

/**
 * This class contains the Model portion of the Model-View-Controller 
 * architecture that is used to demonstrate decay rates for various types of
 * atomic nuclei.
 *
 * @author John Blanco
 */
public class DecayRatesModel extends MultiNucleusDecayModel {

    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
	
	private static final int MAX_NUCLEI = 500;
	private static final int NUCLEUS_TYPE = NuclearPhysicsConstants.NUCLEUS_ID_CARBON_14;
	private static final double INITIAL_WORLD_WIDTH = 800;  // Femtometers
	private static final double INITIAL_WORLD_HEIGHT = INITIAL_WORLD_WIDTH * 0.4;  // Femtometers
	
	private static final int PLACEMENT_LOCATION_SEARCH_COUNT = 100;
	private static final double DEFAULT_MIN_INTER_NUCLEUS_DISTANCE = 20;
	
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
	
	private static final Random _rand = new Random();
	private double _worldSizeX = INITIAL_WORLD_WIDTH;
	private double _worldSizeY = INITIAL_WORLD_HEIGHT;

    /**
     * @param clock
     */
    public DecayRatesModel(NuclearPhysicsClock clock) {
    	super( clock, MAX_NUCLEI, NUCLEUS_TYPE );
    }
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------

	protected void addMaxNuclei() {
		
		AbstractDecayNucleus newNucleus;
		
		for ( int i = 0; i < _maxNuclei; i++ ){
			if ( _currentNucleusType == NuclearPhysicsConstants.NUCLEUS_ID_CARBON_14 ){
				newNucleus = new Carbon14Nucleus( _clock );
			}
			else if ( _currentNucleusType == NuclearPhysicsConstants.NUCLEUS_ID_URANIUM_238 ){
				newNucleus = new Uranium238Nucleus( _clock );
			}
			else{
				newNucleus = new AdjustableHalfLifeNucleus( _clock );
			}
			_atomicNuclei.add( newNucleus );
			newNucleus.setPosition( findOpenNucleusLocation() );
			notifyModelElementAdded( newNucleus );
			newNucleus.activateDecay();
			_jitterOffsets[i] = new Point2D.Double();
	        
			// Register as a listener for the nucleus so we can handle the
	        // particles thrown off by alpha decay.
	        
	        newNucleus.addListener( _nucleusListener );
		}
	}
	
	/**
	 * Set the world size.  This will expand or contract the locations of the
	 * particles that exist in the world.
	 */
	public void setWorldSize( double newWorldSizeX, double newWorldSizeY ){
		// Ignore values that are way to small or too large.
		if ( ( newWorldSizeX < INITIAL_WORLD_WIDTH / 10 ) ||
			 ( newWorldSizeY < INITIAL_WORLD_HEIGHT / 10 ) ||
			 ( newWorldSizeY > INITIAL_WORLD_HEIGHT * 10 ) ||
			 ( newWorldSizeY > INITIAL_WORLD_HEIGHT * 10 ) ||
			 ( newWorldSizeX / newWorldSizeY > 5 ) ||
			 ( newWorldSizeY / newWorldSizeX > 5 ) ) {
			
			System.err.println("Warning: Ignoring unreasonable world resizing attempt.");
			return;
		}
		
		double xScaleFactor = newWorldSizeX / _worldSizeX;
		double yScaleFactor = newWorldSizeY / _worldSizeY;
		
        for (int i = 0; i < _atomicNuclei.size(); i++) {
        	AbstractDecayNucleus nucleus = (AbstractDecayNucleus)_atomicNuclei.get(i);
        	double newPosX = nucleus.getPositionReference().getX() * xScaleFactor;
        	double newPosY = nucleus.getPositionReference().getY() * yScaleFactor;
        	nucleus.setPosition( newPosX, newPosY );
        }
        
        _worldSizeX = newWorldSizeX;
        _worldSizeY = newWorldSizeY;
	}
	
    /**
     * Search for a location that is not already occupied by another nucleus.
     * 
     * @return
     */
    private Point2D findOpenNucleusLocation(){
    	
    	Point2D.Double openLocation = null;
    	boolean pointAvailable = false;
    	
    	// Determine the minimum placement distance between nuclei.
    	double minInterNucleusDistance = DEFAULT_MIN_INTER_NUCLEUS_DISTANCE;
    	if ( _atomicNuclei.size() > 0 ) {
    		// Calculate a minimum distance between nuclei based on the
    		// diameter of the current nucleus.
    		minInterNucleusDistance = ((AbstractDecayNucleus)_atomicNuclei.get(0)).getDiameter() * 1.5;
    	}
    	
    	// Pick random locations until one is found that works or until we've
    	// tried the maximum number of times.
        for (int i = 0; i < PLACEMENT_LOCATION_SEARCH_COUNT; i++){
            // Randomly select an x & y position
            double xPos = _worldSizeX * (_rand.nextDouble() - 0.5);
            double yPos = _worldSizeY * (_rand.nextDouble() - 0.5);
            openLocation = new Point2D.Double(xPos, yPos);
            
            // Check if this point is available.
            pointAvailable = true;
            for (int j = 0; (j < _atomicNuclei.size()) && (pointAvailable == true); j++){
            	AbstractDecayNucleus nucleus = (AbstractDecayNucleus)_atomicNuclei.get(j);
                if (openLocation.distance( nucleus.getPositionReference()) < minInterNucleusDistance){
                    // This point is taken.
                    pointAvailable = false;
                }
            }
            
            if (pointAvailable == true){
                // We have found a usable location.
                break;
            }
        }
        
        if ( !pointAvailable ){
        	// The random algorithm failed to find an open location, so try a
        	// more methodical algorithm that starts from the current random spot.

            for (int i = 0; i < PLACEMENT_LOCATION_SEARCH_COUNT; i++){
                
                // Check if this point is available.
                pointAvailable = true;
                for (int j = 0; (j < _atomicNuclei.size()) && (pointAvailable == true); j++){
                	AbstractDecayNucleus nucleus = (AbstractDecayNucleus)_atomicNuclei.get(j);
                    if (openLocation.distance( nucleus.getPositionReference()) < minInterNucleusDistance){
                        // This point is taken.
                        pointAvailable = false;
                    }
                }
                
                if (pointAvailable == true){
                    // We have found a usable location.
                    break;
                }
                else{
                	double newX = openLocation.getX() + minInterNucleusDistance;
                	double newY = openLocation.getY();
                	if (newX > _worldSizeX / 2){
                		newX = -_worldSizeX / 2;
                		newY += minInterNucleusDistance;
                		if (newY > _worldSizeY / 2){
                			newY = -_worldSizeY / 2;
                		}
                	}
                	openLocation.setLocation(newX, newY);
                }
            }
        }
        
        if (!pointAvailable){
        	// TODO: Remove the debug statement below once it is being seen infrequently enough.
        	System.out.println("Warning: Didn't find open location, choosing one arbitrarily.");
        }
        
        return openLocation;
    }
}
