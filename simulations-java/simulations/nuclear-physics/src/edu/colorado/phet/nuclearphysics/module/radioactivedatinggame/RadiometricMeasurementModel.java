/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;

/**
 * This class defines a model (in the model-view-controller paradigm) that
 * includes a single datable item that contains a single datable item and
 * a meter which can be used to radiometrically date the item.
 *
 * @author John Blanco
 */
public class RadiometricMeasurementModel implements ModelContainingDatableItems {

    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
	
	// Enumerations that defines the main modes for this model.
	public enum SIMULATION_MODE { TREE, ROCK };
	private SIMULATION_MODE DEFAULT_MODE = SIMULATION_MODE.TREE;
	
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------

	private DatableItem _datableItem;
	private RadiometricDatingMeter _meter;
	private SIMULATION_MODE _simulationMode = DEFAULT_MODE;
	private ConstantDtClock _clock;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public RadiometricMeasurementModel(NuclearPhysicsClock clock)
    {
    	_clock = clock;
    	
    	// Add the meter and register for user-initiated movements.
    	_meter = new RadiometricDatingMeter( this, new Point2D.Double(-5, 3) );
    	
    	_meter.getProbeModel().addObserver(new SimpleObserver(){
			public void update() {
				getDatableItemAtLocation( _meter.getProbeModel().getTipLocation() );
			}
    	});
    }

    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------
    
    public RadiometricDatingMeter getMeter(){
    	return _meter;
    }
    
    /**
     * Get the Y location of the ground.  Note that the ground is assumed to
     * extend infinitely in the positive and negative X directions.
     */
    public double getGroundLevelY(){
    	// At least for now, the ground is always assumed to be at Y location
    	// zero.
    	return 0;
    }
    
    public SIMULATION_MODE getSimulationMode() {
		return _simulationMode;
	}

    /**
     * Set the mode (e.g. "TREE") that defines what the model will simulate.
     * 
     * @param mode
     */
	public void setSimulationMode(SIMULATION_MODE mode) {
		_simulationMode = mode;
	}
	
	public ConstantDtClock getClock() {
		return _clock;
	}
	
	public void setClock(ConstantDtClock clock) {
		_clock = clock;
	}
	
    //------------------------------------------------------------------------
    // Other Methods
    //------------------------------------------------------------------------

	/**
     * Get the datable item at the specified model location, or null if there
     * isn't anything there.
     */
    public DatableItem getDatableItemAtLocation( Point2D probeLocation ){

    	DatableItem datableItem = null;
    	
    	if ( (_datableItem != null ) && (_datableItem.contains( probeLocation ) ) ){
    		datableItem = _datableItem;
    	}
    	
    	return datableItem;
    }
}
