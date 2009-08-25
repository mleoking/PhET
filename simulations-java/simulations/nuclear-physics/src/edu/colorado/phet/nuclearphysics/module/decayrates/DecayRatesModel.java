/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.decayrates;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Random;

import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.NucleusType;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.common.model.MultiNucleusDecayModel;
import edu.colorado.phet.nuclearphysics.model.Carbon14Nucleus;
import edu.colorado.phet.nuclearphysics.model.HeavyAdjustableHalfLifeNucleus;
import edu.colorado.phet.nuclearphysics.model.Uranium238Nucleus;
import edu.colorado.phet.nuclearphysics.module.betadecay.LabelVisibilityModel;

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
	
	private static final int MAX_NUCLEI = 1000;
	private static final NucleusType DEFAULT_NUCLEUS_TYPE = NucleusType.CARBON_14;
	private static final double INITIAL_WORLD_WIDTH = 800;  // In femtometers
	private static final double INITIAL_WORLD_HEIGHT = INITIAL_WORLD_WIDTH * 0.4;  // In femtometers
	
	private static final int PLACEMENT_LOCATION_SEARCH_COUNT = 100;
	private static final double DEFAULT_MIN_INTER_NUCLEUS_DISTANCE = 10;
	
	// Define the position in model space of the "holding area", where nuclei
	// are kept while inactive.
	private static final Rectangle2D HOLDING_AREA_RECT = new Rectangle2D.Double(220, 35, 190, 120);
	
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
	
	private static final Random _rand = new Random();
	private double _worldSizeX = INITIAL_WORLD_WIDTH;
	private double _worldSizeY = INITIAL_WORLD_HEIGHT;

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
    
    /**
     * @param clock
     */
    public DecayRatesModel(NuclearPhysicsClock clock) {
    	super( clock, MAX_NUCLEI, DEFAULT_NUCLEUS_TYPE, false, new LabelVisibilityModel() );
    }
    
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    
    public double getPercentageDecayed(){
    	double activeCount = 0;
    	double decayedCount = 0;
    	for (Iterator it = _atomicNuclei.iterator(); it.hasNext(); ){
    		AtomicNucleus nucleus = (AtomicNucleus)it.next();
    		if (nucleus.isDecayActive()){
    			activeCount++;
    		}
    		if (nucleus.hasDecayed()){
    			decayedCount++;
    		}
    	}
    	
    	if (activeCount == 0){
    		return 0;
    	}
    	else{
    		return ( decayedCount / ( activeCount + decayedCount ) ) * 100;
    	}
    }
    
    /**
     * Get a rectangle describing the location of the holding area, which is
     * where nuclei are originally located and where they are kept when they
     * are not active (i.e. moving towards decay).
     */
    public Rectangle2D getHoldingAreaRect(){
    	Rectangle2D.Double rect = new Rectangle2D.Double();
    	rect.setRect(HOLDING_AREA_RECT);
    	return rect;
    }
    
    /**
    /**
     * Get the number of nuclei that are currently in the holding area.
     */
    public int getNumNucleiInHoldingArea(){
    	int numNucleiInHoldingArea = 0;
    	for (AtomicNucleus nucleus : _atomicNuclei){
    		if (isNucleusInHoldingArea(nucleus)){
    			numNucleiInHoldingArea++;
    		}
    	}
    	return numNucleiInHoldingArea;
    }
    
    public void moveNucleusToHoldingArea(AtomicNucleus nucleus){
    	if (!_atomicNuclei.contains(nucleus)){
    		throw new IllegalArgumentException("Specified nucleus is not part of model.");
    	}
    	nucleus.setPosition(HOLDING_AREA_RECT.getCenterX(), HOLDING_AREA_RECT.getCenterY());
    }
    
    /**
     * Return any nucleus that this not currently in the holding area.
     * Returns null if no such nuclei are present.
     * @return
     */
    public AtomicNucleus getAnyNonHeldNucleus(){
    	AtomicNucleus freeNucleus = null;
    	for (AtomicNucleus nucleus : _atomicNuclei){
    		if (!isNucleusInHoldingArea(nucleus)){
    			freeNucleus = nucleus;
    			break;
    		}
    	}
    	return freeNucleus;
    }
    
    /**
     * Determine whether or not the given nucleus is in the holding area. Note
     * that we don't verify that the supplied nucleus is actually contained by
     * the model - that is assumed.
     * 
     * @param nucleus
     * @return
     */
    public boolean isNucleusInHoldingArea(AtomicNucleus nucleus){
    	return HOLDING_AREA_RECT.contains(nucleus.getPositionReference());
    }

	protected void addMaxNuclei() {
		
		AtomicNucleus newNucleus;
		
		for ( int i = 0; i < _maxNuclei; i++ ){
			if ( _currentNucleusType == NucleusType.CARBON_14 ){
				newNucleus = new Carbon14Nucleus( _clock, new Point2D.Double(0,0), true );
			}
			else if ( _currentNucleusType == NucleusType.URANIUM_238 ){
				newNucleus = new Uranium238Nucleus( _clock );
			}
			else{
				newNucleus = new HeavyAdjustableHalfLifeNucleus( _clock );
			}
			_atomicNuclei.add( newNucleus );
			_jitterOffsets[i] = new Point2D.Double();
			
			// Add the nuclei initially to the holding area by positioning
			// them such that they are within the holding area rectangle.
			newNucleus.setPosition( HOLDING_AREA_RECT.getCenterX(), HOLDING_AREA_RECT.getCenterY() );

			// Let any listeners know about the new nucleus.
			notifyModelElementAdded( newNucleus );
	        
			// Register as a listener for the nucleus so we can handle the
	        // particles thrown off by alpha decay.
	        
	        newNucleus.addListener( _nucleusListener );
		}
	}
	
    /**
     * @return
     */
    public Point2D findOpenNucleusLocation(){
    	
    	Point2D.Double openLocation = null;
    	boolean pointAvailable = false;
    	
    	// Determine the minimum placement distance between nuclei.
    	double minInterNucleusDistance = DEFAULT_MIN_INTER_NUCLEUS_DISTANCE;
    	if ( _atomicNuclei.size() > 0 ) {
    		// Calculate a minimum distance between nuclei based on the
    		// diameter of the current nucleus.
    		minInterNucleusDistance = Math.min(_atomicNuclei.get(0).getDiameter() * 1.5,
    				DEFAULT_MIN_INTER_NUCLEUS_DISTANCE);
    	}
    	
    	// Pick random locations until one is found that works or until we've
    	// tried the maximum number of times.
    	for (int i = 0; i < 4; i++){
            for (int j = 0; j < PLACEMENT_LOCATION_SEARCH_COUNT / 4; j++){
                // Randomly select an x & y position
                double xPos = _worldSizeX * (_rand.nextDouble() - 0.5);
                double yPos = _worldSizeY * (_rand.nextDouble() - 0.5);
                openLocation = new Point2D.Double(xPos, yPos);
                
                // Check if this point is available.
                pointAvailable = true;
                for (int k = 0; (k < _atomicNuclei.size()) && (pointAvailable == true); k++){
                	AtomicNucleus nucleus = _atomicNuclei.get(k);
                    if (openLocation.distance(nucleus.getPositionReference()) < minInterNucleusDistance ||
                    	HOLDING_AREA_RECT.contains(openLocation)){
                        // This point is not available.
                        pointAvailable = false;
                    }
                }
                
                if (pointAvailable == true){
                    // We have found a usable location.
                    break;
                }
            }
            if (pointAvailable){
            	break;
            }
            else{
            	// Try again, but lower our standards.
            	minInterNucleusDistance = minInterNucleusDistance / 2;
            }
    	}
    	
        if ( !pointAvailable ){
        	// The random algorithm failed to find an open location, so pick a
        	// random location that is outside of the holding area.

        	System.out.println("Warning: Using arbitrary location because no open location found.");
        	do{
	            double xPos = _worldSizeX * (_rand.nextDouble() - 0.5);
	            double yPos = _worldSizeY * (_rand.nextDouble() - 0.5);
	            openLocation = new Point2D.Double(xPos, yPos);
        	}
            while (HOLDING_AREA_RECT.contains(openLocation));
        }
        
        return openLocation;
    }
    
    /**
     * Convert a value representing simulation time to the corresponding
     * adjusted value according to the nucleus (which may be as much as
     * billions of years).
     * 
     * @param simTime
     */
    public double convertSimTimeToAdjustedTime(double simTime){
    	double conversionFactor = 1;
    	if (_atomicNuclei.size() > 1){
    		conversionFactor = _atomicNuclei.get(0).getDecayTimeScalingFactor();
    	}
    	else{
    		System.err.println(this.getClass().getName() + "Warning: No nuclei available to provide conversion factor.");
    		assert false;
    	}
    	return simTime / conversionFactor;
    }
    
}
