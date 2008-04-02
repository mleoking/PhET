/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

public class AtomicNucleus {
    
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
    
    // Radius at which an alpha particle could tunnel out.  This is in
    // femtometers, but is larger than the real value so that users can see
    // particles coming and going in this zone.
    public static final double TUNNEL_OUT_RADIUS = 15; 
    
    // The "agitation factor" for the various types of nucleus.  The amount of
    // agitation controls how dynamic the nucleus looks on the canvas.  Values
    // must be in the range 0-9.
    private static final int POLONIUM_211_AGITATION_FACTOR = 8;
    private static final int URANIUM_235_AGITATION_FACTOR = 6;
    private static final int URANIUM_236_AGITATION_FACTOR = 9;
    private static final int LEAD_207_AGITATION_FACTOR = 3;
    private static final int DEFAULT_AGITATION_FACTOR = 5;
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------

    // The clock that drives the time-based behavior.
    ConstantDtClock _clock;
    
    // List of registered listeners.
    private ArrayList _listeners = new ArrayList();
    
    // Location in space of the center of this nucleus.
    private Point2D _position;
    
    // Velocity of this nucleus.
    private double _xVelocity = 0;
    private double _yVelocity = 0;
    
    // List of the constituent particles that comprise this nucleus.
    private ArrayList _constituents = new ArrayList();
    
    // Original settings, used for resetting this nucleus.
    private int _originalnumProtons;
    private int _originalnumNeutrons;
    
    // Variable for deciding when alpha decay should occur.
    private double _alphaDecayTime = 0;
    
    // Particle that will/is tunneling out of nucleus.
    private AlphaParticle _tunnelingParticle = null;
    
    // Numbers of various particles.
    int _numAlphas;
    int _numProtons;
    int _numNeutrons;
    
    // Used to implement the 'agitation' behavior, i.e. to make the nucleus
    // appear to be in constant dynamic motion.
    int _agitationCount = 0;
    
    // Amount of agitation exhibited by nucleus, from 0 to 9.
    int _agitationFactor = DEFAULT_AGITATION_FACTOR;
    
    // Used for various random calculations.
    Random _rand = new Random();
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public AtomicNucleus(NuclearPhysics2Clock clock, Point2D position, int numProtons, int numNeutrons)
    {
        _clock = clock;

        clock.addClockListener( new ClockAdapter(){
            
            /**
             * Clock tick handler - causes the model to move forward one
             * increment in time.
             */
            public void clockTicked(ClockEvent clockEvent){
                handleClockTicked(clockEvent);
            }
            
            public void simulationTimeReset(ClockEvent clockEvent){
                reset();
            }
        });
        
        
        // Set the initial position for this nucleus.
        _position = position;
        
        // Save the original params so we can reset if need be.
        _originalnumProtons = numProtons;
        _originalnumNeutrons = numNeutrons;
        
        // Figure out the proportions of various particles.
        _numAlphas    = ((numProtons + numNeutrons) / 2) / 4;  // Assume half of all particles are tied up in alphas.
        _numProtons   = numProtons - (_numAlphas * 2);
        _numNeutrons  = numNeutrons - (_numAlphas * 2);

        // Add the particles.  We do this in such a way that the particles
        // are interspersed in the list, particularly towards the end of the
        // list, since this works out better for the view.
        int maxParticles = Math.max( _numProtons, _numNeutrons );
        maxParticles = Math.max( maxParticles, _numAlphas);
        for (int i = (maxParticles - 1); i >= 0; i--){
            if (i < _numAlphas){
                _constituents.add( new AlphaParticle(0, 0) );
            }
            if (i < _numProtons){
                _constituents.add( new Proton(0, 0, true) );
            }
            if (i < _numNeutrons){
                _constituents.add( new Neutron(0, 0, true) );
            }
        }
        
        // If we are being created as a Polonium 211 nucleus, then decide when
        // alpha decay will occur.
        if ((numProtons == 84) && (numNeutrons == 127)){
            _alphaDecayTime = calcPolonium211DecayTime();
        }
        else{
            // Setting the decay time to 0 signifies that no alpha decay
            // should occur.
            _alphaDecayTime = 0;
        }
        
        // Set the initial agitation factor.
        updateAgitationFactor();
    }
    
    //------------------------------------------------------------------------
    // Accessor methods
    //------------------------------------------------------------------------

    public Point2D getPosition(){
        return _position;
    }
    
    public ArrayList getConstituents(){
        return _constituents;
    }
    
    public int getAtomicWeight(){
        return _numNeutrons + _numProtons + (_numAlphas * 4);
    }
    
    public int getNumProtons(){
        return _numProtons + (_numAlphas * 2);
    }
    
    public int getNumNeutrons(){
        return _numNeutrons + (_numAlphas * 2);
    }
    
    public double getDiameter(){
        // This calculation is based on an empirically derived function that
        // seems to give pretty reasonable values.
        return (1.6 * Math.pow( (double)getAtomicWeight(), 0.362));
    }
    
    public ConstantDtClock getClock(){
        return _clock;
    }
    
    //------------------------------------------------------------------------
    // Other public methods
    //------------------------------------------------------------------------
    
    /**
     * This method lets this model element know that the clock has ticked.  In
     * response, the nucleus generally 'agitates' a bit, may also perform some
     * sort of decay, and may move.
     */
    public void handleClockTicked(ClockEvent clockEvent)
    {
        // Move if our velocity is non-zero.
        if (!((_xVelocity == 0) && (_yVelocity == 0))){
            _position.setLocation( _position.getX() + _xVelocity, _position.getY() + _yVelocity);
            
            // Notify listeners of the position change.
            for (int i = 0; i < _listeners.size(); i++){
                ((Listener)_listeners.get( i )).positionChanged();
            }
        }
        
        // See if alpha decay should occur.
        if ((_alphaDecayTime != 0) && (clockEvent.getSimulationTime() >= _alphaDecayTime ))
        {
            // Pick an alpha particle to tunnel out and make it happen.
            for (int i = 0; i < _constituents.size(); i++)
            {
                if (_constituents.get( i ) instanceof AlphaParticle){
                    
                    // This one will do.  Make it tunnel.
                    _tunnelingParticle = (AlphaParticle)_constituents.get( i );
                    _constituents.remove( i );
                    _numAlphas--;
                    _tunnelingParticle.tunnelOut( TUNNEL_OUT_RADIUS + 1.0 );
                    
                    // Update our agitation factor.
                    updateAgitationFactor();
                    
                    // Notify listeners of the change of atomic weight.
                    int totalNumProtons = _numProtons + _numAlphas * 2;
                    int totalNumNeutrons= _numNeutrons + _numAlphas * 2;
                    for (int j = 0; j < _listeners.size(); j++){
                        ((Listener)_listeners.get( j )).atomicWeightChanged( totalNumProtons, totalNumNeutrons );
                    }
                    break;
                }
            }
            
            // Set the decay time to 0 to indicate that no more tunneling out
            // should occur.
            _alphaDecayTime = 0;
        }
        
        // Move the constituent particles to create the visual effect of a
        // very dynamic nucleus.  In order to allow different levels of
        // agitation, we don't necessarily move all particles every time.
        if (_agitationFactor > 0){
            
            // Calculate the increment to be used for creating the agitation
            // effect.
            
            int agitationIncrement = 10 - _agitationFactor;
            assert agitationIncrement > 0;
            
            if (agitationIncrement <= 0){
                agitationIncrement = 5;
            }
                
            for (int i = _agitationCount; i < _constituents.size(); i+=agitationIncrement)
            {
                AtomicNucleusConstituent constituent = (AtomicNucleusConstituent)_constituents.get( i );
                constituent.tunnel( _position, 0, getDiameter()/2, TUNNEL_OUT_RADIUS );
            }
            _agitationCount = (_agitationCount + 1) % agitationIncrement;
        }
        
        // If we have a tunneling particle, move it.
        if (_tunnelingParticle != null){
            _tunnelingParticle.moveOut();
        }
    }
    
    /**
     * Resets the nucleus to its original state, before any fission has
     * occurred.
     */
    public void reset(){
        
        // If we started as Polonium 211, reset the alpha decay timer.
        if ((_originalnumProtons == 84) && (_originalnumNeutrons == 127)){
            _alphaDecayTime = calcPolonium211DecayTime();
        }
        else {
            _alphaDecayTime = 0;            
        }

        // See if a particle is tunneling or has tunneled.
        if (_tunnelingParticle != null){
            // Reset the tunneling, i.e. return the particle to the nucleus.
            _tunnelingParticle.resetTunneling();
            
            // Add this particle back to our list of constituents.
            _constituents.add( _tunnelingParticle );
            _tunnelingParticle = null;
            _numAlphas++;
            
            // Update our agitation level.
            updateAgitationFactor();
            
            // Let the listeners know that the atomic weight has changed.
            int totalNumProtons = _numProtons + _numAlphas * 2;
            int totalNumNeutrons= _numNeutrons + _numAlphas * 2;
            for (int i = 0; i < _listeners.size(); i++){
                ((Listener)_listeners.get( i )).atomicWeightChanged( totalNumProtons, totalNumNeutrons );
            }
        }
    }
    
    /**
     * Capture a free particle if the nucleus is able to.
     * 
     * @param freeParticle - A particle that is currently free, i.e. not a 
     * part of another nucleus.
     */
    public void captureNeutron(Nucleon freeParticle){
        
        freeParticle.setTunnelingEnabled( true );
        freeParticle.setVelocity( 0, 0 );
        _constituents.add( freeParticle );
        _numNeutrons++;
        updateAgitationFactor();

        // Let the listeners know that the atomic weight has changed.
        int totalNumProtons = _numProtons + _numAlphas * 2;
        int totalNumNeutrons= _numNeutrons + _numAlphas * 2;
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get( i )).atomicWeightChanged( totalNumProtons, totalNumNeutrons );
        }
    }

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
    
    //------------------------------------------------------------------------
    // Private methods
    //------------------------------------------------------------------------
    
    /**
     * This method generates a value indicating the number of milliseconds for
     *  a Polonium 211 nucleus to decay.  This calculation is based on the 
     * exponential decay formula and uses the decay constant for Polonium 211.
     * 
     * @return
     */
    private double calcPolonium211DecayTime(){
        double randomValue = _rand.nextDouble();
        if (randomValue > 0.999){
            // Limit the maximum time for decay so that the user isn't waiting
            // around forever.
            randomValue = 0.999;
        }
        double tunnelOutMilliseconds = (-(Math.log( 1 - randomValue ) / 1.343)) * 1000;
        System.out.println("randomValue = " + randomValue + ", tunnelOutMilliseconds = " + tunnelOutMilliseconds);
        return tunnelOutMilliseconds;
    }
    
    /**
     * Updates the "agitation factor", which controls how agitated and dynamic
     * the nucleus will appear to be.  This is generally called at construction
     * or when something changes about the nucleus, such as a decay event.
     */
    private void updateAgitationFactor(){
        
        // Determine the amount of agitation that should be exhibited by this
        // particular nucleus.  This obviously doesn't handle every possible
        // nucleus, so add more if and when they are needed.
        
        int _totalNumProtons = _numProtons + (_numAlphas * 2);
        int _totalNumNeutrons = _numNeutrons + (_numAlphas * 2);
        
        switch (_totalNumProtons){
        
        case 84:
            // Polonium.
            if (_totalNumNeutrons == 127){
                // Polonium 211.
                _agitationFactor = POLONIUM_211_AGITATION_FACTOR;
            }
            break;
            
        case 92:
            // Uranium
            if (_totalNumNeutrons == 143){
                // Uranium 235
                _agitationFactor = URANIUM_235_AGITATION_FACTOR;
            }
            else if (_totalNumNeutrons == 144){
                // Uranium 236
                _agitationFactor = URANIUM_236_AGITATION_FACTOR;                
            }
            break;

        case 82:
            // Lead
            if (_totalNumNeutrons == 125){
                // Lead 207
                _agitationFactor = LEAD_207_AGITATION_FACTOR;
            }
            break;

        }
    }
    
    //------------------------------------------------------------------------
    // Inner interfaces
    //------------------------------------------------------------------------
    
    public static interface Listener {
        void positionChanged();
        void atomicWeightChanged(int numProtons, int numNeutrons);
    }
}
