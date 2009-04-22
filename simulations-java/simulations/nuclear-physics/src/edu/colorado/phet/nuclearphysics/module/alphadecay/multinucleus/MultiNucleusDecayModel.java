/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.model.AbstractDecayNucleus;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.common.model.NuclearDecayControl;
import edu.colorado.phet.nuclearphysics.common.model.NuclearDecayModelListener;
import edu.colorado.phet.nuclearphysics.model.AdjustableHalfLifeNucleus;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.Polonium211Nucleus;
import edu.colorado.phet.nuclearphysics.module.alphadecay.NucleusTypeControl;

/**
 * Basic "container" sort of model element, meaning that the atomic nuclei
 * model elements are contained by this class.  This class allows nuclei to
 * be added and removed, and listeners can register with it in order to get
 * notifications of such comings and goings.
 * 
 * NOTE: At the time of this writing (April 22, 2009), this class can only
 * handle one type of initial nucleus.  It wouldn't be hard to refactor/
 * restructure so that multiple initial types are supported, but so far it
 * hasn't been needed.
 *  
 * @author John Blanco
 */
public class MultiNucleusDecayModel implements NucleusTypeControl{

    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------

	static final int DEFAULT_NUCLEUS_TYPE = NuclearPhysicsConstants.NUCLEUS_ID_POLONIUM;
	static final double MAX_JITTER_LENGTH = 1;
	static final int CLOCKS_PER_JITTER = 2;
	
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
	
	protected NuclearPhysicsClock _clock;
	ArrayList _listeners = new ArrayList();
	protected AbstractDecayNucleus [] _atomicNuclei;
	protected int _currentNucleusType;
	protected int _initialNucleusType;
	protected AtomicNucleus.Adapter _nucleusListener;
	private final Random _rand = new Random();
	protected Point2D [] _jitterOffsets;
	private int _jitterOffsetCount = 0;
	protected final int _maxNuclei;

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
	
	public MultiNucleusDecayModel( NuclearPhysicsClock clock, int maxNuclei, int initialNucleusType ) {
        _clock = clock;
        _initialNucleusType = initialNucleusType;
        _currentNucleusType = initialNucleusType;
        _maxNuclei = maxNuclei;
        _atomicNuclei = new AbstractDecayNucleus[_maxNuclei];
        _jitterOffsets = new Point2D[_maxNuclei];

        // Register as a listener to the clock.
        clock.addClockListener( new ClockAdapter(){
            
            /**
             * Clock tick handler - causes the model to move forward one
             * increment in time.
             */
            public void clockTicked(ClockEvent clockEvent){
                handleClockTicked(clockEvent);
            }
            
            public void simulationTimeReset(ClockEvent clockEvent){
                // Reset the model.
            	reset();
            }
        });
        
        initializeNucleusListener();
	}

    //------------------------------------------------------------------------
    // Public and protected methods
    //------------------------------------------------------------------------

	public void setNucleusType(int nucleusType) {
		
		if (nucleusType != _currentNucleusType){
			
			removeAllNuclei();
			_currentNucleusType = nucleusType;
			
			// Add all nuclei to the model.  At the time of this writing, this
			// is the desired behavior for all subclasses.  It may need to be
			// modified if a more general approach is needed.
			addMaxNuclei();
			
			notifyNucleusTypeChanged();
		}
	}
	
	public int getNucleusType() {
		return _currentNucleusType;
	}

	/**
	 * This method allows the caller to register for changes in the overall
	 * model, as opposed to changes in the individual model elements.
	 * 
	 * @param listener
	 */
	public void addListener(NuclearDecayModelListener listener) {
	    if ( !_listeners.contains( listener )){
	        _listeners.add( listener );
	    }
	}

	public void setPaused(boolean paused) {
		// Pause the decay and movement of all nuclei.
		for (int i = 0; i < _atomicNuclei.length; i++){
			_atomicNuclei[i].setPaused(paused);
		}
	}

	/**
	 * Reset all nuclei that are either active (meaning that they could decay
	 * at any time) or decayed.
	 * 
	 * @return - The number of nuclei that are reset.
	 */
	public int resetActiveAndDecayedNuclei() {
		int resetCount = 0;
		for (int i = 0; i < _atomicNuclei.length; i++){
			if (_atomicNuclei[i].isDecayActive() || _atomicNuclei[i].hasDecayed()){
				_atomicNuclei[i].reset();
				_atomicNuclei[i].activateDecay();
				resetCount++;
			}
		}
		return resetCount;
	}

	protected void handleClockTicked(ClockEvent clockEvent) {
		// Cause the active nuclei to "jitter".  For efficiency, not every
		// active nucleus is moved every time.
		for (int i = _jitterOffsetCount; i < _atomicNuclei.length; i = i + CLOCKS_PER_JITTER){
			if (_atomicNuclei[i].isDecayActive() && !_atomicNuclei[i].isPaused()){
				// This nucleus is active, so it should be jittered.
				Point2D jitterOffset = _jitterOffsets[i];
				Point2D currentLocation = _atomicNuclei[i].getPositionReference();
				if (jitterOffset.getX() == 0 && jitterOffset.getY() == 0){
					// Move this nucleus away from its center location.
					generateJitterOffset( jitterOffset );
					_atomicNuclei[i].setPosition( currentLocation.getX() + jitterOffset.getX(),
							currentLocation.getY() + jitterOffset.getY());
				}
				else{
					// Move back to original location.
					_atomicNuclei[i].setPosition( currentLocation.getX() - jitterOffset.getX(),
							currentLocation.getY() - jitterOffset.getY());
					_jitterOffsets[i].setLocation(0, 0);
				}
			}
		}
		_jitterOffsetCount = (_jitterOffsetCount + 1) % CLOCKS_PER_JITTER;
	}
	
	/**
	 * Set up the object that will listen to notifications from the nuclei.
	 * It will be common to override this is sub-classes which may need to
	 * handle things like decay events differently.
	 */
	protected void initializeNucleusListener(){

		// Create the object that will listen for nucleus events, such as decays.
        _nucleusListener = new AtomicNucleus.Adapter();
	}

	protected void reset() {
		removeAllNuclei();
		_currentNucleusType = _initialNucleusType;
		addMaxNuclei();  // For now, this is the desired behavior of all subclasses.
		notifyNucleusTypeChanged();
	}

	/**
	 * Remove all the existing nuclei and alpha particles from the model and
	 * send out the appropriate notifications.
	 */
	protected void removeAllNuclei() {
		// Remove any existing nuclei and let the listeners know of their demise.
		for (int i = 0; i < _atomicNuclei.length; i++){
			if (_atomicNuclei[i] != null){
				_atomicNuclei[i].removeListener(_nucleusListener);
				notifyModelElementRemoved( _atomicNuclei[i] );
				_atomicNuclei[i] = null;
			}
		}
	}

	/**
	 * Add the maximum allowed number of nuclei to the model.
	 * 
	 */
	protected void addMaxNuclei() {
	
		AbstractDecayNucleus newNucleus;
			
		for (int i = 0; i < _maxNuclei; i++){
			if (_currentNucleusType == NuclearPhysicsConstants.NUCLEUS_ID_POLONIUM){
				newNucleus = new Polonium211Nucleus(_clock);
			}
			else{
				newNucleus = new AdjustableHalfLifeNucleus(_clock);
			}
			_atomicNuclei[i] = newNucleus;
			_jitterOffsets[i] = new Point2D.Double();
			notifyModelElementAdded(newNucleus);
	        
			// Register as a listener for the nucleus so we can handle the
	        // particles thrown off by alpha decay.
	        
	        newNucleus.addListener( _nucleusListener );
		}
	}

	/**
	 * Notify listeners about the removal of an element from the model.
	 * 
	 * @param removedElement
	 */
	protected void notifyModelElementRemoved(Object removedElement) {
	    for (int i = 0; i < _listeners.size(); i++){
	        ((NuclearDecayModelListener)_listeners.get(i)).modelElementRemoved( removedElement );
	    }
	}

	/**
	 * Notify listeners about the addition of an element to the model.
	 * @param addedElement
	 */
	protected void notifyModelElementAdded(Object addedElement) {
	    for (int i = 0; i < _listeners.size(); i++){
	        ((NuclearDecayModelListener)_listeners.get(i)).modelElementAdded( addedElement );
	    }
	}

	/**
	 * Notify listeners about the change of nucleus type.
	 * @param addedElement
	 */
	private void notifyNucleusTypeChanged() {
	    for (int i = 0; i < _listeners.size(); i++){
	        ((NuclearDecayModelListener)_listeners.get(i)).nucleusTypeChanged();
	    }
	}

	/**
	 * Notify listeners about the change of nucleus type.
	 * @param addedElement
	 */
	protected void notifyHalfLifeChanged() {
	    for (int i = 0; i < _listeners.size(); i++){
	        ((NuclearDecayModelListener)_listeners.get(i)).halfLifeChanged();
	    }
	}

	/**
	 * Generate a random 2-dimensional offset for use in "jittering" a point
	 * according to a Guassian distribution and a predefined length.
	 * 
	 * @param point
	 */
	private void generateJitterOffset(Point2D point) {
		double length = _rand.nextGaussian() * (MAX_JITTER_LENGTH / 2);
		if (length > MAX_JITTER_LENGTH){
			length = MAX_JITTER_LENGTH;
		}
		double angle = _rand.nextDouble() * Math.PI * 2;
		point.setLocation(Math.cos(angle) * length, Math.sin(angle) * length);
	}

	public ConstantDtClock getClock() {
	    return _clock;
	}

	/**
	 * Set the half life for all nuclei in the model.
	 * 
	 * @param halfLife - Half life in milliseconds.
	 */
	public void setHalfLife(double halfLife) {
		
		// Verify that the current nucleus is custom.
		if (_currentNucleusType != NuclearPhysicsConstants.NUCLEUS_ID_CUSTOM){
			System.err.println("Warning: Can only set nucleus type for custom nucleus, ignoring request.");
			return;
		}
		
		// Set the new half life value.
		for (int i = 0; i < _atomicNuclei.length; i++){
			AbstractDecayNucleus nucleus = (AbstractDecayNucleus)_atomicNuclei[i];
			if (nucleus != null){
				nucleus.setHalfLife(halfLife);
			}
		}
		
		// Inform any listeners of the change.
		notifyHalfLifeChanged();
	}

	public double getHalfLife() {
		
		double halfLife = 0;
		
		if (_atomicNuclei.length == 0){
			return 0;
		}
		
		// Get the first nucleus in the list, and assume that all nuclei are
		// the same in terms of half life.
		AtomicNucleus nucleus = _atomicNuclei[0];
		
		if (nucleus instanceof NuclearDecayControl){
			halfLife = ((NuclearDecayControl)nucleus).getHalfLife();
		}
		
		return halfLife;
	}
	
	/**
	 * Get the current total number of nuclei in the model.
	 */
	public int getNumNuclei(){
		int count = 0;
		for (int i = 0; i < _atomicNuclei.length; i++){
			if (_atomicNuclei[i] != null){
				count++;
			}
		}
		return count;
	}
}