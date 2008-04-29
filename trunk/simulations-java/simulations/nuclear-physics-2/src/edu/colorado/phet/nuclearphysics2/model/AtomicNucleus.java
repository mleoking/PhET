/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

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
    
    // Default value for agitation.
    protected static final int DEFAULT_AGITATION_FACTOR = 5;
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------

    // The clock that drives the time-based behavior.
    protected NuclearPhysics2Clock _clock;
    
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
    
    // List of the constituent particles that comprise this nucleus.
    protected ArrayList _constituents;
    
    // Numbers of various particles.  The "free" neutrons and protons are not
    // bound up in an alpha particle.
    protected int _numAlphas;
    protected int _numFreeProtons;
    protected int _numFreeNeutrons;
    
    // Boolean value that controls whether the nucleus is dynamic, meaning
    // that it moves the constituent particles around ("agitates") or is
    // static and does not move the particles around.
    boolean _dynamic = true;
    
    // Used to implement the 'agitation' behavior, i.e. to make the nucleus
    // appear to be in constant dynamic motion.
    private int _agitationCount = 0;
    
    // Amount of agitation exhibited by nucleus, from 0 to 9.
    protected int _agitationFactor = DEFAULT_AGITATION_FACTOR;
    
    // Used for various random calculations.
    protected Random _rand = new Random();
    
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
    public AtomicNucleus(NuclearPhysics2Clock clock, Point2D position, int numProtons, int numNeutrons)
    {
        _clock = clock;

        addClockListener();
        
        // Set the initial position for this nucleus.
        _origPosition.setLocation( position );
        _position.setLocation( position );
        
        // Figure out the proportions of various particles.
        _numAlphas    = ((numProtons + numNeutrons) / 2) / 4;  // Assume half of all particles are tied up in alphas.
        _numFreeProtons   = numProtons - (_numAlphas * 2);
        _numFreeNeutrons  = numNeutrons - (_numAlphas * 2);

        // Add the particles.  We do this in such a way that the particles
        // are interspersed in the list, particularly towards the end of the
        // list, since this works out better for the view.
        _constituents = new ArrayList();
        int maxParticles = Math.max( _numFreeProtons, _numFreeNeutrons );
        maxParticles = Math.max( maxParticles, _numAlphas);
        for (int i = (maxParticles - 1); i >= 0; i--){
            if (i < _numAlphas){
                _constituents.add( new AlphaParticle(0, 0) );
            }
            if (i < _numFreeProtons){
                _constituents.add( new Proton(0, 0, true) );
            }
            if (i < _numFreeNeutrons){
                _constituents.add( new Neutron(0, 0, true) );
            }
        }
        
        // Calculate our diameter.
        updateDiameter();
        
        // Set the initial agitation factor.
        updateAgitationFactor();
    }
    
    
    /**
     * This constructor is used to create a nucleus when the constituents of
     * the nucleus (i.e. protons, neutrons, and alpha particles) already exist.
     * This is generally used to create a "daughter nucleus" when an existing
     * nucleus splits.
     */
    public AtomicNucleus(NuclearPhysics2Clock clock, Point2D position, ArrayList constituents)
    {
        // Create an empty nucleus.
        this(clock, position, 0, 0);

        // Figure out the makeup of the constituents.
        _numAlphas = 0;
        _numFreeNeutrons = 0;
        _numFreeProtons = 0;
        for (int i = 0; i < constituents.size(); i++){
            if (constituents.get( i ) instanceof AlphaParticle){
                _numAlphas++;
            }
            else if (constituents.get( i ) instanceof Neutron){
                _numFreeNeutrons++;
            }
            else if (constituents.get( i ) instanceof Proton){
                _numFreeProtons++;
            }
            else{
                // Should never happen, debug if it does.
                assert false;
                System.err.println("Error: Unexpected nucleus constituent type.");
            }
        }
        
        // Keep the array of constituents.
        _constituents = constituents;
        
        // recalculate our diameter.
        updateDiameter();
        
        // Update our agitation factor.
        updateAgitationFactor();
    }
    
    //------------------------------------------------------------------------
    // Accessor methods
    //------------------------------------------------------------------------

    public Point2D getPositionRef(){
        return _position;
    }
    
    public void setPosition(Point2D newPosition){
        _position.setLocation( newPosition );
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
    
    public ArrayList getConstituents(){
        return _constituents;
    }
    
    public int getAtomicWeight(){
        return _numFreeNeutrons + _numFreeProtons + (_numAlphas * 4);
    }
    
    public int getNumProtons(){
        return _numFreeProtons + (_numAlphas * 2);
    }
    
    public int getNumNeutrons(){
        return _numFreeNeutrons + (_numAlphas * 2);
    }
    
    /**
     * Return the diameter of the nucleus in femtometers.
     * 
     * @return
     */
    public double getDiameter(){
        return _diameter;
    }
    
    private void updateDiameter(){
        // This calculation is based on an empirically derived function that
        // seems to give pretty reasonable values.
        _diameter = (1.6 * Math.pow( (double)getAtomicWeight(), 0.362));        
    }
    
    public ConstantDtClock getClock(){
        return _clock;
    }
    
    public void setTunnelingRegionRadius(double tunnelingRegionRadius){
        _tunnelingRegionRadius = tunnelingRegionRadius;
    }
    
    public double getTunnelingRegionRadius(){
        return _tunnelingRegionRadius;
    }
    
    public void setDynamic(boolean dynamic){
        if ((_dynamic == true)  && (dynamic == false)){
            // Randomize the locations of the constituent particles so that 
            // they are not all in once place, which would look lame.
            double nucleusRadius = getDiameter() / 2;
            for (int i = 0; i < _constituents.size(); i++){
                double angle = (_rand.nextDouble() * Math.PI * 2);
                double multiplier = _rand.nextDouble();
                if (multiplier > 0.8){
                    // Cause the distribution to tail off in the outer regions
                    // of the nucleus.  This makes the center of the nucleus
                    // look more concentrated.
                    multiplier = _rand.nextDouble() * _rand.nextDouble();
                }
                double radius = nucleusRadius * multiplier;
                double xPos = Math.sin( angle ) * radius + _position.getX();
                double yPos = Math.cos( angle ) * radius + _position.getY();
                AtomicNucleusConstituent constituent = (AtomicNucleusConstituent)_constituents.get( i );
                constituent.setPosition( new Point2D.Double(xPos, yPos) );
            }
        }
        _dynamic = dynamic;
    }
    
    public boolean getDynamic(){
        return _dynamic;
    }
    
    //------------------------------------------------------------------------
    // Other public methods
    //------------------------------------------------------------------------
    
    /**
     * Method to add listeners.
     * 
     * @param listener
     */
    public void addListener(Listener listener)
    {
        if (_listeners.contains( listener ))
        {
            // Don't bother re-adding.
            return;
        }
        
        _listeners.add( listener );
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
     * This method lets this model element know that the clock has ticked.  In
     * response, the nucleus generally 'agitates' a bit, may also perform some
     * sort of decay, and may move.
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
            
            // Move the constituent particles by the velocity amount.
            int numConstituents = _constituents.size();
            AtomicNucleusConstituent constituent = null;
            for (int i = 0; i < numConstituents; i++){
                constituent = (AtomicNucleusConstituent)_constituents.get(i);
                newPosX = constituent.getPosition().x + _xVelocity; 
                newPosY = constituent.getPosition().y + _yVelocity;
                constituent.setPosition( newPosX, newPosY );
            }
        }
        
        // Move the constituent particles to create the visual effect of a
        // very dynamic nucleus.  In order to allow different levels of
        // agitation, we don't necessarily move all particles every time.
        if ((_agitationFactor > 0) && (_dynamic)){
            
            // Calculate the increment to be used for creating the agitation
            // effect.
            
            int agitationIncrement = 20 - (2 * _agitationFactor);
            assert agitationIncrement > 0;
            
            if (agitationIncrement <= 0){
                agitationIncrement = 5;
            }
                
            for (int i = _agitationCount; i < _constituents.size(); i+=agitationIncrement)
            {
                AtomicNucleusConstituent constituent = (AtomicNucleusConstituent)_constituents.get( i );
                constituent.tunnel( _position, 0, getDiameter()/2, _tunnelingRegionRadius );
            }
            _agitationCount = (_agitationCount + 1) % agitationIncrement;
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
        
        public void simulationTimeReset(ClockEvent clockEvent){
            // Ignore this reset and count on the main model to reset us.
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
     * Updates the "agitation factor", which controls how agitated and dynamic
     * the nucleus will appear to be.  This is generally called at construction
     * or when something changes about the nucleus, such as a decay event.
     */
    protected abstract void updateAgitationFactor();
    
    /**
     * Notify all listeners that our atomic weight has changed.
     */
    protected void notifyAtomicWeightChanged(ArrayList byProducts){
        
        // First recalculate the diameter, since it likely has changed.
        updateDiameter();
        
        // Do the notification.
        int totalNumProtons = _numFreeProtons + _numAlphas * 2;
        int totalNumNeutrons= _numFreeNeutrons + _numAlphas * 2;
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get( i )).atomicWeightChanged( this, totalNumProtons, totalNumNeutrons, byProducts);
        }
    }
    
    protected void notifyPositionChanged(){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get( i )).positionChanged();
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
         * Inform listeners that the atomic weight (i.e. the total number of
         * protons and neutrons that comprise this nucleus) has changed.
         * 
         * @param numProtons - New number of protons in the nucleus.
         * @param numNeutrons - New number of neutrons in the nucleus.
         * @param byProducts - By products of the change, which may include
         * protons, neutrons, alpha particles, or daughter nuclei.  May be
         * null if no byproducts were produced.
         * @param atomicNucleus TODO
         * @param newParam TODO
         */
        void atomicWeightChanged(AtomicNucleus atomicNucleus, int numProtons, int numNeutrons, ArrayList byProducts);
    }
    
    public static class Adapter implements Listener {
        public void positionChanged(){}
        public void atomicWeightChanged(AtomicNucleus atomicNucleus, int numProtons, int numNeutrons, 
                ArrayList byProducts){}
    }
}
