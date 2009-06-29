/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus.Listener;

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
	private ArrayList<Listener> _listeners = new ArrayList<Listener>();
	private ArrayList<AnimatedModelElement> _animatedModelElements = new ArrayList<AnimatedModelElement>();

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
    // Methods
    //------------------------------------------------------------------------
    
    public RadiometricDatingMeter getMeter(){
    	return _meter;
    }

    /**
     * Get a collection of all model elements that currently exist in the
     * simulation.
     */
    public ArrayList getModelElements(){
    	return new ArrayList(_animatedModelElements);
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
		if ( _simulationMode != mode){
			_simulationMode = mode;
			notifySimulationModeChanged();
		}
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

    public void addListener(Listener listener){
        if (!_listeners.contains( listener ))
        {
        	_listeners.add( listener );
        }
    }
    
    public boolean removeListener(Listener listener){
    	return _listeners.remove(listener);
    }
    
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
    
    protected void notifySimulationModeChanged(){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get( i )).simulationModeChanged();
        }        
    }
    
    //------------------------------------------------------------------------
    // Inner Interfaces and Classes
    //------------------------------------------------------------------------

    public static interface Listener {
    	public void simulationModeChanged();
    	public void modelElementAdded();
    	public void modelElementRemoved();
    	public void operationalStateChanged();
    }
    
    public static class Adapter implements Listener {
		public void simulationModeChanged() {}
		public void modelElementAdded() {}
		public void modelElementRemoved() {}
		public void operationalStateChanged() {}
    }
}
