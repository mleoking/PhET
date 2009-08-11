/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.common.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.NucleusDisplayInfo;
import edu.colorado.phet.nuclearphysics.common.NucleusType;
import edu.colorado.phet.nuclearphysics.model.HalfLifeInfo;

public abstract class AtomicNucleus {
    
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
    
    // Radius at which the repulsive electrical force overwhelms the strong
    // force.
    public static final double DEFAULT_TUNNELING_REGION_RADIUS = 15;
    public static final double MAX_TUNNELING_REGION_RADIUS = 200;
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------

    // The clock that drives the time-based behavior.
    protected NuclearPhysicsClock _clock;
    
    // List of registered listeners.
    protected ArrayList _listeners = new ArrayList();
    
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
    
    // Used for deciding where particles tunnel to and how far they need
    // to go to tunnel out.
    protected double _tunnelingRegionRadius = DEFAULT_TUNNELING_REGION_RADIUS;
    
    // Diameter of the atom, calculated at init and when changes occur. 
    private double _diameter;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    /**
     * This constructor creates the constituent particles, i.e. the protons,
     * neutrons, and alpha particles that will comprise the nucleus.  It is
     * generally used when create a nucleus "from scratch".
     */
    public AtomicNucleus(NuclearPhysicsClock clock, Point2D position, int numProtons, int numNeutrons)
    {
        _clock = clock;
        _numProtons = numProtons;
        _numNeutrons = numNeutrons;

        addClockListener();
        
        // Set the initial position for this nucleus.
        _origPosition.setLocation( position );
        _position.setLocation( position );
        
        // Calculate our diameter.
        updateDiameter();
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
    
    /**
     * Get the half life for this nucleus.
     */
    public double getHalfLife(){
    	return HalfLifeInfo.getHalfLifeForNucleusConfig(_numProtons, _numNeutrons);
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
    public void reset(){
        // Stubbed in base class.
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
    }
    
    ClockAdapter _ca = new ClockAdapter(){
        
        /**
         * Clock tick handler - causes the model to move forward one
         * increment in time.
         */
        public void clockTicked(ClockEvent clockEvent){
            handleClockTicked(clockEvent);
        }
    };
    
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
