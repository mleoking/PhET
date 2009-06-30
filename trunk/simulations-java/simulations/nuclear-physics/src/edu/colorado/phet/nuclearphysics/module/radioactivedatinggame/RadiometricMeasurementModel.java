/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

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
	
	// Enumeration that defines the main modes for this model.
	public enum SIMULATION_MODE { TREE, ROCK };
	private SIMULATION_MODE DEFAULT_MODE = SIMULATION_MODE.TREE;
	
	// Constants that control the initial size and position of some of the
	// model elements.
	private static final Point2D INITIAL_TREE_POSITION = new Point2D.Double(-12, -2);
	private static final double  INITIAL_TREE_WIDTH = 7;
	private static final Point2D INITIAL_VOLCANO_POSITION = new Point2D.Double(13, 0);
	private static final double  INITIAL_VOLCANO_WIDTH = 12;
	private static final Point2D INITIAL_PROBE_TIP_POSITION = new Point2D.Double(0, 10);
	
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------

	private RadiometricDatingMeter _meter;
	private SIMULATION_MODE _simulationMode;
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
    	_meter = new RadiometricDatingMeter( this, INITIAL_PROBE_TIP_POSITION );
    	
    	_meter.getProbeModel().addObserver(new SimpleObserver(){
			public void update() {
				getDatableItemAtLocation( _meter.getProbeModel().getTipLocation() );
			}
    	});
    	
    	// Set the initial simulation mode, which will add the initial
    	// model element(s).
    	setSimulationMode(DEFAULT_MODE);
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
    
    /**
     * Get the mode value that indicates what is being simulated.
     */
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
			
			// Stop and reset the clock.
			_clock.stop();
			_clock.resetSimulationTime();
			
			// Remove all existing model elements.
			Iterator<AnimatedModelElement> itr = _animatedModelElements.iterator();
			while (itr.hasNext()){
				itr.next();
				itr.remove();
				notifyModelElementRemoved();
			}
			
			// Add the appropriate model elements based on the simulation mode.
			switch( _simulationMode ){
			case TREE:
				// No model elements added until the tree is planted.
				break;
			case ROCK:
				_animatedModelElements.add(new EruptingVolcano(_clock, INITIAL_VOLCANO_POSITION, INITIAL_VOLCANO_WIDTH));
				notifyModelElementAdded();
				break;
			}
			
			notifySimulationModeChanged();
		}
	}
	
	/**
	 * Start simulating based on the currently selected mode.  This is ignored
	 * if a simulation is already in progress.
	 */
	public void startOperation(){
		
		if ( _clock.getSimulationTime() != 0 ){
			// The simulation has already been started, so print an error and
			// exit.
			System.err.println(getClass().getName() + " - Warning: Command received to start operation when already started.");
			return;
		}
		
		switch (_simulationMode){
		case TREE:
			plantTree();
			break;
		case ROCK:
			break;
		}
		
		// Start the clock, which the model elements will listen to and do
		// their thing.
		_clock.start();
	}
	
	/**
	 * Start simulating the life of a tree be creating a tree and adding it
	 * to the model.
	 */
	private void plantTree(){
		// At the time of this writing, no other animated model elements
		// should exist when the tree is planted.
		assert _animatedModelElements.size() == 0;
		
		// Add the tree.
		_animatedModelElements.add(new AgingTree(_clock, INITIAL_TREE_POSITION, INITIAL_TREE_WIDTH));
		notifyModelElementAdded();
	}
	
	/**
	 * Returns a value indicating whether "closure" has occurred, meaning that
	 * the datable item(s) have started to age radiometrically.  For a tree,
	 * this occurs when the tree dies.  For a rock, when it cools.  And so on.
	 */
	public boolean hasClosureOccurred(){
		// TODO: Needs implementation.
		return false;
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
    	
    	for ( AnimatedModelElement modelElement : _animatedModelElements ){
    		if ( modelElement instanceof DatableItem ){
    			if ( ((DatableItem)modelElement).contains(probeLocation) ){
    				datableItem = (DatableItem)modelElement;
    			}
    		}
    	}
    	
    	return datableItem;
    }
    
    protected void notifySimulationModeChanged() {
        for (int i = 0; i < _listeners.size(); i++){
            _listeners.get( i ).simulationModeChanged();
        }
    }
    
    protected void notifyModelElementAdded() {
        for (int i = 0; i < _listeners.size(); i++){
            _listeners.get( i ).modelElementAdded();
        }        
    }

    protected void notifyModelElementRemoved() {
        for (int i = 0; i < _listeners.size(); i++){
            _listeners.get( i ).modelElementRemoved();
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
