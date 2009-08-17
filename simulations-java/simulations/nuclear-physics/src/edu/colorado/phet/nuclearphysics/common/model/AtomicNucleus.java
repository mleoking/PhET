/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.common.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.NucleusDisplayInfo;
import edu.colorado.phet.nuclearphysics.common.NucleusType;
import edu.colorado.phet.nuclearphysics.model.HalfLifeInfo;

public abstract class AtomicNucleus implements NuclearDecayControl {
    
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
    
    // Radius at which the repulsive electrical force overwhelms the strong
    // force.
    public static final double DEFAULT_TUNNELING_REGION_RADIUS = 15;
    public static final double MAX_TUNNELING_REGION_RADIUS = 200;
    public static final Random RAND = new Random();
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------

    // The clock that drives the time-based behavior.
    protected NuclearPhysicsClock _clock;
    
    // List of registered listeners.
    protected ArrayList<Listener> _listeners = new ArrayList<Listener>();
    
    // Location in space of the center of this nucleus.
    protected Point2D _position = new Point2D.Double();
    
    // Original position location.
    protected Point2D _origPosition = new Point2D.Double();
    
    // Velocity of this nucleus.
    protected double _xVelocity = 0;
    protected double _yVelocity = 0;
    
    // Acceleration of this nucleus.
    protected double _xAcceleration = 0;
    protected double _yAcceleration = 0;

    // Number of neutrons and protons in this nucleus.
    protected int _numNeutrons;
    protected int _numProtons;
    
    // Original number of neutrons and protons, needed for resets and possibly
    // for determining whether decay has occurred.
    protected final int _origNumNeutrons;
    protected final int _origNumProtons;
    
    // Used for deciding where particles tunnel to and how far they need
    // to go to tunnel out.
    protected double _tunnelingRegionRadius = DEFAULT_TUNNELING_REGION_RADIUS;
    
    // Diameter of the atom, calculated at init and when changes occur. 
    private double _diameter;

    // Variables that describe and control the decay of the nucleus.
	protected double _decayTime = 0;         // Time at which nucleus should decay - in sim time.
	protected double _activatedLifetime = 0; // Duration of activation (moving towards decay) in sim time.
	protected double _totalUndecayedLifetime = 0; // Time, in real time, that this nucleus will live or did live.
	protected double _halfLife = 0;
	protected boolean _paused = false;
	protected final double _decayTimeScalingFactor;

	// Clock adapter for listening to the simulation clock.
    private ClockAdapter _ca = new ClockAdapter(){
        
        /**
         * Clock tick handler - causes the model to move forward one
         * increment in time.
         */
        public void clockTicked(ClockEvent clockEvent){
            handleClockTicked(clockEvent);
        }
    };
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public AtomicNucleus(NuclearPhysicsClock clock, Point2D position, int numProtons, int numNeutrons, 
    		double decayTimeScalingFactor)
    {
        _clock = clock;
        _numProtons = numProtons;
        _numNeutrons = numNeutrons;
        _origNumProtons = numProtons;
        _origNumNeutrons = numNeutrons;
        _decayTimeScalingFactor = decayTimeScalingFactor;

        addClockListener();
        
        // Set the initial position for this nucleus.
        _origPosition.setLocation( position );
        _position.setLocation( position );
        
        // Calculate our diameter.
        updateDiameter();
        
        // Set the initial half life based on the nucleus' configuration.  It
        // can be changed through a setter method if needed.
      	_halfLife = HalfLifeInfo.getHalfLifeForNucleusConfig( _numProtons, _numNeutrons );
    }
    
    public AtomicNucleus(NuclearPhysicsClock clock, Point2D position, int numProtons, int numNeutrons){
    	this(clock, position, numProtons, numNeutrons, 1);
    }
    
    //------------------------------------------------------------------------
    // Accessor methods
    //------------------------------------------------------------------------

    public Point2D getPositionReference(){
        return _position;
    }
    
    public void setPosition(Point2D newPosition){
        setPosition( newPosition.getX(), newPosition.getY() );
    }
    
    public void setPosition(double x, double y){
        _position.setLocation( x, y );
        notifyPositionChanged();
    }
    
    public void setVelocity( double xVel, double yVel){
        _xVelocity = xVel;
        _yVelocity = yVel;
    }
    
    public void setAcceleration( double xAcc, double yAcc ){
        _xAcceleration = xAcc;
        _yAcceleration = yAcc;
    }
    
    public void setAcceleration( Vector2D accelerationVector ){
        _xAcceleration = accelerationVector.getX();
        _yAcceleration = accelerationVector.getY();
    }

    public Vector2D.Double getAcceleration(){
        return new Vector2D.Double(_xAcceleration, _yAcceleration);
    }
    
    public Vector2D.Double getVelocity(){
        return new Vector2D.Double(_xVelocity, _yVelocity);
    }
    
    public int getAtomicWeight(){
        return _numNeutrons + _numProtons;
    }
    
    public int getNumProtons(){
        return _numProtons;
    }
    
    public int getNumNeutrons(){
        return _numNeutrons;
    }
    
    /**
     * Return the diameter of the nucleus in femtometers.
     * 
     * @return
     */
    public double getDiameter(){
        return _diameter;
    }
    
	public double getDecayTime() {
	    return _decayTime;
	}

	public double getHalfLife() {
	    return _halfLife;
	}
	
	public double getDecayTimeScalingFactor() {
		return _decayTimeScalingFactor;
	}

	/**
	 * Set the half life for this nucleus.
	 * 
	 * @param halfLife - Half life in milliseconds.
	 */
	public void setHalfLife(double halfLife) {
	    _halfLife = halfLife;
	}

    /**
     * Recalculate the diameter of this nucleus based on the number of protons
     * and neutrons that comprise it.
     */
    protected void updateDiameter(){
        // This calculation is based on an empirically derived formula that
        // seems to give pretty reasonable values.
        _diameter = (1.6 * Math.pow( (double)getAtomicWeight(), 0.362));        
    }
    
    public ConstantDtClock getClock(){
        return _clock;
    }
    
    public void setTunnelingRegionRadius(double tunnelingRegionRadius){
    	if (tunnelingRegionRadius != _tunnelingRegionRadius){
    		if (tunnelingRegionRadius >= getDiameter()/2){
    			tunnelingRegionRadius = Math.min(tunnelingRegionRadius, MAX_TUNNELING_REGION_RADIUS);
    	        _tunnelingRegionRadius = tunnelingRegionRadius;
    	        notifyTunnelingRadiusChanged();
    		}
    		else{
    			// Ignore attempts to set invalid values.
    		}
    	}
    }
    
    public double getTunnelingRegionRadius(){
        return _tunnelingRegionRadius;
    }
    
	public boolean isPaused() {
		return _paused;
	}

	public void setPaused(boolean paused) {
		_paused = paused;
	}

	/**
	 * This method starts the nucleus moving towards decay.
	 */
    public void activateDecay(){
    	// Only allow activation if the nucleus hasn't already decayed.
    	if (_numNeutrons == _origNumNeutrons){
    		_totalUndecayedLifetime = calcDecayTime();
    		_decayTime = _clock.getSimulationTime() + (_totalUndecayedLifetime * _decayTimeScalingFactor);
    	}
    }

	/**
	 * Returns a boolean value indicating whether the nucleus has decayed.
	 * This will return false if the nucleus has not been activated.
	 */
	public boolean hasDecayed(){
		// Not sure if this default implementation will apply to all types of
		// decay, but it works for those implemented by the sim as of this
		// writing.
		if (_numProtons < _origNumProtons){
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * Return true if decay is currently active and false if not.  Note that
	 * this will return false if the nucleus has already decayed.
	 */
	public boolean isDecayActive() {
		if (_decayTime != 0){
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * Returns a value indicating how long in terms of simulation time the
	 * nucleus has been active without having decayed.
	 * 
	 * @return Simulation time for which this nucleus has been activated, i.e.
	 * progressing towards decay.
	 */
	public double getActivatedSimTime() {
		return _activatedLifetime;
	}
	
	/**
	 * Returns a value indicating the amount of adjusted time that the nucleus
	 * has been active without decaying.  Adjusted time is based on the time
	 * adjustment factor that is used to scale the amount of time that a model
	 * element has experienced such that it will generally decay in a
	 * reasonable time frame (so that users aren't waiting around for
	 * thousands of years for decay to occur).
	 * 
	 * @return Adjusted time in milliseconds for which this nucleus has been
	 * activated, i.e. progressing towards decay.
	 */
	public double getAdjustedActivatedTime() {
		return _activatedLifetime / _decayTimeScalingFactor;
	}

    //------------------------------------------------------------------------
    // Other public methods
    //------------------------------------------------------------------------
    
    public void addListener(Listener listener){
        
        if (_listeners.contains( listener ))
        {
            // Don't bother re-adding.
            return;
        }
        
        _listeners.add( listener );
    }
    
    public boolean removeListener(Listener listener){
        return _listeners.remove( listener );
    }
    
    /**
     * Reset the nucleus to its original state.
     */
	public void reset() {
	    // Reset the decay time to 0, indicating that it shouldn't occur
	    // until something changes.
	    _decayTime = 0;
	    _activatedLifetime = 0;
	
	    // Make sure we are not paused.
		_paused = false;
	}
    
    /**
     * Give the nucleus a chance to capture a (presumably) free particle.
     * 
     * @param particle - The particle that could potentially be captured.
     * @return true if particle captured, false if not.
     */
    public boolean captureParticle(Nucleon particle){
        // Does nothing in base class.
        return false;
    };
    
    /**
     * Convenience method for obtaining the display information for this
     * configuration of atomic nucleus.
     * @return
     */
    public NucleusDisplayInfo getDisplayInfo(){
    	return NucleusDisplayInfo.getDisplayInfoForNucleusConfig(_numProtons, _numNeutrons);
    }
    
    /**
	 * Convenience method for obtaining the nucleus or nuclei that the
	 * specified nucleus type will decay into.  Note that the return values
	 * are NOT NECESSARILY what always happens in the real world - they
	 * represent the way this simulation behaves, which is a simplification of
	 * real-world behavior.  Also note that this method may sometimes consider
	 * something like an alpha particle as a helium nucleus and list it here,
	 * or sometimes as and emitted particle, and thus NOT list it here.  It
	 * all depends on the needs of the other portions of the sim.
	 */
	public static ArrayList<NucleusType> getPostDecayNuclei(NucleusType preDecayNucleus){
		
		ArrayList<NucleusType> decayProducts = new ArrayList<NucleusType>();
		
		switch (preDecayNucleus){
		
		case HYDROGEN_3:
			decayProducts.add(NucleusType.HELIUM_3);
			break;
		
		case CARBON_14:
			decayProducts.add(NucleusType.NITROGEN_14);
			break;
			
		case URANIUM_238:
			decayProducts.add(NucleusType.LEAD_206);
			break;
			
		case POLONIUM_211:
			decayProducts.add(NucleusType.LEAD_207);
			break;
	
		case LIGHT_CUSTOM:
			decayProducts.add(NucleusType.LIGHT_CUSTOM_POST_DECAY);
			break;
	
		case HEAVY_CUSTOM:
			decayProducts.add(NucleusType.HEAVY_CUSTOM_POST_DECAY);
			break;
	
		default:
			System.out.println("Warning: No decay product information available for requested nucleus, returning original value, nucleus = " + preDecayNucleus);
			decayProducts.add(preDecayNucleus);
			break;
		}
		
		return decayProducts;
	}
	
	/**
	 * Convenience method for identifying a nucleus based on its configuration.
	 *  
	 * @param numProtons
	 * @param numNeutrons
	 * @return
	 */
	public static NucleusType identifyNucleus(int numProtons, int numNeutrons){
		
		NucleusType nucleusType = null;
		
		// Note that (obviously) not every nucleus that exists in nature is
		// handled here - just those needed by the sim.  Feel free to add more
		// if needed.
		switch (numProtons){
		case 1:
			// Hydrogen.
			nucleusType = NucleusType.HYDROGEN_3;
			break;
			
		case 2:
			// Helium.
			nucleusType = NucleusType.HELIUM_3;
			break;
		
		case 6:
			// Carbon 14.
			nucleusType = NucleusType.CARBON_14;
			break;
			
		case 7:
			// Nitrogen 14.
			nucleusType = NucleusType.NITROGEN_14;
			break;
			
		case 8:
			// Oxygen, which is used in this sim as the light custom nucleus.
			nucleusType = NucleusType.LIGHT_CUSTOM;
			break;
			
		case 81:
			// This is thallium, which we use as the post-decay custom nucleus.
			nucleusType = NucleusType.HEAVY_CUSTOM_POST_DECAY;
			break;
			
		case 82:
			// Lead.
			if ( numNeutrons == 124 ){
				// Lead 206
	    		nucleusType = NucleusType.LEAD_206;
			}
			else if ( numNeutrons == 125 ) {
				// Lead 207
	    		nucleusType = NucleusType.LEAD_207;
			}
			else {
				System.err.println("Error: Unrecognized isotope for Lead, using Lead 207.");
				assert false;
	    		nucleusType = NucleusType.LEAD_207;
			}
			break;
			
		case 83:
			// This nucleus is bismuth, which we use as the pre-decay custom
			// nucleus.
			nucleusType = NucleusType.HEAVY_CUSTOM;
			break;
			
		case 84:
			// Polonium.
			nucleusType = NucleusType.POLONIUM_211;
			break;
			
		case 92:
			switch (numNeutrons){
			case 143:
				// U235.
	    		nucleusType = NucleusType.URANIUM_235;
	    		break;
	    		
			case 144:
				// U236.
	    		nucleusType = NucleusType.URANIUM_236;
	    		break;
	    		
			case 146:
				// U238.
	    		nucleusType = NucleusType.URANIUM_238;
	    		break;
	    		
			case 147:
				// U239.
	    		nucleusType = NucleusType.URANIUM_239;
	    		break;
	    		
			default:
				// Unrecognized.
				System.err.println("Error: Unrecognized uranium isotop, using U238.");
				assert false;
	    		nucleusType = NucleusType.URANIUM_238;
	    		break;
			}
			break;
			
		default:
			// This is not a nucleus type that we are familiar with.  This is
			// okay, we just return null.
			nucleusType = null;
			break;
		}
		
		return nucleusType;
	}
    
    //------------------------------------------------------------------------
    // Private and Protected Methods
    //------------------------------------------------------------------------

    /**
     * This method lets this model element know that the clock has ticked, and
     * it should move or do whatever other changes are appropriate.
     */
    protected void handleClockTicked(ClockEvent clockEvent)
    {
        // Move if our velocity is non-zero.
        _xVelocity += _xAcceleration;
        _yVelocity += _yAcceleration;
        if (!((_xVelocity == 0) && (_yVelocity == 0))){
            double newPosX = _position.getX() + _xVelocity;
            double newPosY = _position.getY() + _yVelocity;
            _position.setLocation( newPosX, newPosY);
            
            // Notify listeners of the position change.
            notifyPositionChanged();
        }
        
        // Take any action necessary related to decay.
	    if (_decayTime != 0){
		     
	    	if (!_paused){
	        	// See if decay should occur.
		        if ( isTimeToDecay(clockEvent) ) {
		            // It is time to decay.
		        	decay( clockEvent );
		        }
		        else{
		        	// Not decaying yet, so updated the activated lifetime.
		        	_activatedLifetime += clockEvent.getSimulationTimeChange();
		        }
	    	}
	    	else{
	    		// This atom is currently paused, so extend the decay time.
	    		_decayTime += clockEvent.getSimulationTimeChange();
	    	}
	    }
    }
    
	/**
	 * This method is called when decay occurs, and it defines the behavior
	 * exhibited by the nucleus when it decays.  This method should be
	 * implemented by all subclasses that exhibit decay behavior..
	 */
	protected void decay( ClockEvent clockEvent ){

        // Set the final value of the time that this nucleus existed prior to
        // decaying.
        _activatedLifetime += clockEvent.getSimulationTimeChange();
        
        // Set the decay time to 0 to indicate that decay has occurred and
        // should not occur again.
        _decayTime = 0;
	}
	
    /**
     * This method generates a value indicating the number of milliseconds for
     * a nucleus decay based on the half life.  This calculation is based on the 
     * exponential decay formula.
     * 
     * @return - a time value in milliseconds
     */
    protected double calcDecayTime(){
    	
    	double decayTime;
    	
    	if (_halfLife <= 0){
    		decayTime = 0;
    	}
    	else if (_halfLife == Double.POSITIVE_INFINITY){
    		decayTime =  Double.POSITIVE_INFINITY;
    	}
    	else{
    		double decayConstant = 0.693/_halfLife;
    		double randomValue = RAND.nextDouble();
    		if (randomValue > 0.999){
    			// Limit the maximum time for decay so that the user isn't waiting
    			// around forever.
    			randomValue = 0.999;
    		}
    		decayTime =  (-(Math.log( 1 - randomValue ) / decayConstant));
    	}
    	
    	return decayTime;
    }
    
    /**
     * Get the amount of time that this nucleus will or did exist without
     * decaying in real time (NOT in simulation time).
     * 
     * @return
     */
    protected double getTotalUndecayedLifetime(){
    	return _totalUndecayedLifetime;
    }
    
    /**
     * Returns true if it is time to decay, false if not.  Generally, this is
     * true if the nucleus has existed longer than the decay time.  It may
     * need to be overridden in cases where simulation time is not linear.
     */
    protected boolean isTimeToDecay(ClockEvent clockEvent){
    	return clockEvent.getSimulationTime() >= _decayTime;
    }
	
    /**
     * Set ourself up to listen to the simulation clock.
     */
    private void addClockListener(){
        _clock.addClockListener( _ca );
    }
    
    public void removedFromModel(){
        _clock.removeClockListener( _ca );
    }

    /**
     * Notify all listeners that our atomic weight has changed.
     */
    protected void notifyNucleusChangeEvent(ArrayList byProducts){
        
        // First recalculate the diameter, since it likely has changed.
        updateDiameter();
        
        // Do the notification.
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get( i )).nucleusChangeEvent( this, _numProtons, _numNeutrons, byProducts);
        }
    }
    
    protected void notifyPositionChanged(){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get( i )).positionChanged();
        }        
    }
    
    protected void notifyTunnelingRadiusChanged(){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get( i )).tunnelingRadiusChanged();
        }        
    }
    
	public static interface Listener {
        
        /**
         * Inform listeners that the position of the nucleus has changed.
         */
        void positionChanged();
        
        /**
         * Inform listeners that the nucleus has changed in some way, which
         * generally indicates a decay event of some kind.
         * 
         * @param numProtons - New number of protons in the nucleus.
         * @param numNeutrons - New number of neutrons in the nucleus.
         * @param byProducts - By products of the change, which may include
         * protons, neutrons, alpha particles, or daughter nuclei.  May be
         * null if no byproducts were produced.
         */
        void nucleusChangeEvent(AtomicNucleus atomicNucleus, int numProtons, int numNeutrons, ArrayList byProducts);
        
        /**
         * Inform listeners that the tunneling region radius had changed.
         */
        void tunnelingRadiusChanged();
    }
    
    public static class Adapter implements Listener {
        public void positionChanged(){}
        public void nucleusChangeEvent(AtomicNucleus atomicNucleus, int numProtons, int numNeutrons, 
                ArrayList byProducts){}
        public void tunnelingRadiusChanged(){};
    }
}
