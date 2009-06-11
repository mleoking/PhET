/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.NucleusType;
import edu.colorado.phet.nuclearphysics.common.model.AbstractDecayNucleus;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.common.model.NuclearDecayModelListener;
import edu.colorado.phet.nuclearphysics.model.AdjustableHalfLifeNucleus;
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
public class MultiNucleusDecayModel implements NucleusTypeControl {

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
	protected ArrayList _listeners = new ArrayList();
	protected ArrayList<AbstractDecayNucleus> _atomicNuclei;
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
        _atomicNuclei = new ArrayList();
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
        });
        
        initializeNucleusListener();
	}

    //------------------------------------------------------------------------
    // Public and protected methods
    //------------------------------------------------------------------------

	// TODO: Finish refactoring to use enum and get rid of this method.
	public void setNucleusTypeOldStyle(int nucleusType) {
		
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
	
	// TODO: Finish refactoring to use enum and get rid of this method.
	public int getNucleusTypeOldStyle() {
		return _currentNucleusType;
	}
	
	public NucleusType getNucleusType() {
		switch (_currentNucleusType){
		case NuclearPhysicsConstants.NUCLEUS_ID_CARBON_14:
			return NucleusType.CARBON_14;
		case NuclearPhysicsConstants.NUCLEUS_ID_URANIUM_238:
			return NucleusType.URANIUM_238;
		case NuclearPhysicsConstants.NUCLEUS_ID_CUSTOM:
			return NucleusType.CUSTOM;
		default:
			assert false;
		    return null;
		}
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

	/**
	 * Reset all nuclei that are either active (meaning that they could decay
	 * at any time) or decayed.
	 * 
	 * @return - The number of nuclei that are reset.
	 */
	public int resetActiveAndDecayedNuclei() {
		int resetCount = 0;
		for (int i = 0; i < _atomicNuclei.size(); i++){
			AbstractDecayNucleus nucleus = (AbstractDecayNucleus)_atomicNuclei.get(i);
			if (nucleus.isDecayActive() || nucleus.hasDecayed()){
				nucleus.reset();
				nucleus.activateDecay();
				resetCount++;
			}
		}
		return resetCount;
	}

	protected void handleClockTicked(ClockEvent clockEvent) {
		// Cause the active nuclei to "jitter".  For efficiency, not every
		// active nucleus is moved every time.
		for (int i = _jitterOffsetCount; i < _atomicNuclei.size(); i = i + CLOCKS_PER_JITTER){
			AbstractDecayNucleus nucleus = (AbstractDecayNucleus)_atomicNuclei.get(i);
			if (nucleus.isDecayActive() && !nucleus.isPaused()){
				// This nucleus is active, so it should be jittered.
				Point2D jitterOffset = _jitterOffsets[i];
				Point2D currentLocation = nucleus.getPositionReference();
				if (jitterOffset.getX() == 0 && jitterOffset.getY() == 0){
					// Move this nucleus away from its center location.
					generateJitterOffset( jitterOffset );
					nucleus.setPosition( currentLocation.getX() + jitterOffset.getX(),
							currentLocation.getY() + jitterOffset.getY());
				}
				else{
					// Move back to original location.
					nucleus.setPosition( currentLocation.getX() - jitterOffset.getX(),
							currentLocation.getY() - jitterOffset.getY());
					_jitterOffsets[i].setLocation(0, 0);
				}
			}
		}
		_jitterOffsetCount = (_jitterOffsetCount + 1) % CLOCKS_PER_JITTER;
	}
	
	/**
	 * Convenience method for converting years to milliseconds, since
	 * milliseconds is used throughout the simulation for timing.
	 */
	static public double convertYearsToMs( double years ){
		return years * 3.1556926E10;
	}
	
	/**
	 * Convenience method for converting milliseconds to years, since
	 * milliseconds is used throughout the simulation for timing.
	 */
	static public double convertMsToYears( double milliseconds ){
		return milliseconds * 3.16887646E-11;
	}
	
	/**
	 * Convenience method for obtaining the decay product(s) for a given
	 * nucleus.  Note that the return values are NOT NECESSARILY what always
	 * happens in the real world - they represent the way this simulation
	 * behaves, which is a simplification of real-world behavior.
	 */
	public static ArrayList<NucleusType> getDecayProduct(NucleusType preDecayNucleus){
		
		ArrayList<NucleusType> decayProducts = new ArrayList<NucleusType>();
		
		switch (preDecayNucleus){
		
		case CARBON_14:
			decayProducts.add(NucleusType.NITROGEN_14);
			break;
			
		case URANIUM_238:
			decayProducts.add(NucleusType.LEAD_206);
			break;

		default:
			System.out.println("Warning: No decay product information available for requested nucleus, returning original value, nucleus = " + preDecayNucleus);
			decayProducts.add(preDecayNucleus);
			break;
		}
		
		return decayProducts;
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

	public void reset() {
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
		for (Iterator it = _atomicNuclei.iterator(); it.hasNext(); ){
			AbstractDecayNucleus nucleus = (AbstractDecayNucleus)it.next();
			nucleus.removeListener(_nucleusListener);
			nucleus.removedFromModel();
			notifyModelElementRemoved( nucleus );
			it.remove();
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
			_atomicNuclei.add( newNucleus );
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
		for (int i = 0; i < _atomicNuclei.size(); i++){
			(_atomicNuclei.get(i)).setHalfLife(halfLife);
		}
		
		// Inform any listeners of the change.
		notifyHalfLifeChanged();
	}

	public double getHalfLife() {
		
		if (_atomicNuclei.size() == 0){
			return 0;
		}
		
		// Get the half life from the first nucleus in the list, and assume
		// that all nuclei are the same.
		return (_atomicNuclei.get(0)).getHalfLife();
	}
	
	/**
	 * Get the current total number of nuclei in the model.
	 */
	public int getTotalNumNuclei(){
		return _atomicNuclei.size();
	}
	
	/**
	 * Get the number of decayed nuclei in the model.
	 */
	public int getNumDecayedNuclei(){
		int decayCount = 0;
		for (AbstractDecayNucleus nucleus : _atomicNuclei){
			if (nucleus.hasDecayed()){
				decayCount++;
			}
		}
		return decayCount;
	}
	
	/**
	 * Get the number of active nuclei, meaning nuclei that are being clocked
	 * and are progressing towards decay.
	 */
	public int getNumActiveNuclei(){
		int activeCount = 0;
		for (AbstractDecayNucleus nucleus : _atomicNuclei){
			if (nucleus.isDecayActive()){
				activeCount++;
			}
		}
		return activeCount;
	}
}