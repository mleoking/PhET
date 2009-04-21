/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

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
    private ArrayList _listeners = new ArrayList();
    
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
     * Recalculate the diameter of this nucleus based on the number of protons
     * and neutrons that comprise it.
     */
    protected void updateDiameter(){
        // This calculation is based on an empirically derived formulat that
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
    protected void notifyAtomicWeightChanged(ArrayList byProducts){
        
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
    
    //------------------------------------------------------------------------
    // Inner interfaces
    //------------------------------------------------------------------------
    
    public static interface Listener {
        
        /**
         * Inform listeners that the position of the nucleus has changed.
         */
        void positionChanged();
        
        /**
         * Inform listeners that the nucleus has changed in some why, which
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
