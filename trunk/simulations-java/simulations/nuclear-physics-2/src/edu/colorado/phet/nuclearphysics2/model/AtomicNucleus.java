/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

public class AtomicNucleus {
    
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
    
    // Radius of the core of the nucleus, in femtometers.
    public static final double CORE_RADIUS = 5.5; // In femtometers
    
    // Radius at which an alpha particle could tunnel out.  This is in
    // femtometers, but is larger than the real value so that users can see
    // particles coming and going in this zone.
    public static final double TUNNEL_OUT_RADIUS = 15; 
    
    // Parameters that control when tunneling will occur.
    private static final int MIN_TUNNELING_TICKS = 20;
    private static final int MAX_TUNNELING_TICKS = 100;
    
    // Initial amount of agitation exhibited by the nucleus.  Range is 0-9.
    private static final int INITIAL_AGITATION_FACTOR = 8;
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------

    // List of registered listeners.
    private ArrayList _listeners = new ArrayList();
    
    // Location in space of the center of this nucleus.
    private Point2D.Double position;
    
    // List of the constituent particles that comprise this nucleus.
    private ArrayList _constituents = new ArrayList();
    
    // Vars for deciding when particle should tunnel out.
    private long _tickCount = 0;
    private long _tunnelOutTicks = MAX_TUNNELING_TICKS;
    
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
    int _agitationFactor = INITIAL_AGITATION_FACTOR;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public AtomicNucleus(double xPos, double yPos, int atomicWeight)
    {
        // Set the initial position for this nucleus.
        position = new Point2D.Double(xPos, yPos);
        
        // Figure out the proportions of various particles based on the atomic number.
        _numAlphas    = (atomicWeight / 2) / 4;  // Assume half of all particles are tied up in alphas.
        _numProtons   = (atomicWeight / 4);
        _numNeutrons  = atomicWeight - _numProtons - (_numAlphas * 4);

        // Add the alpha particles.
        for (int i = 0; i < _numAlphas; i++){
            _constituents.add( new AlphaParticle(0, 0) );
        }
        
        // Add the neutrons.
        for (int i = 0; i < _numNeutrons; i++){
            _constituents.add( new Neutron(0, 0) );
        }

        // Add the protons.
        for (int i = 0; i < _numProtons; i++){
            _constituents.add( new Proton(0, 0) );
        }
        
        // Decide when tunneling will occur.
        Random rand = new Random();
        _tunnelOutTicks = MIN_TUNNELING_TICKS + rand.nextInt( MAX_TUNNELING_TICKS - MIN_TUNNELING_TICKS );
    }
    
    //------------------------------------------------------------------------
    // Accessor methods
    //------------------------------------------------------------------------

    public Point2D getPosition(){
        return new Point2D.Double(position.getX(), position.getY());
    }
    
    public ArrayList getConstituents(){
        return _constituents;
    }
    
    public int getAtomicWeight(){
        return _numNeutrons + _numProtons + (_numAlphas * 4);
    }
    
    public double getDiameter(){
        return 2 * CORE_RADIUS;
    }
    
    //------------------------------------------------------------------------
    // Other methods
    //------------------------------------------------------------------------
    
    /**
     * This method lets this model element know that the clock has ticked.  In
     * response, the nucleus generally 'agitates' a bit, and may also perform
     * some sort of decay.
     */
    public void clockTicked()
    {
        _tickCount++;
        
        if (_tickCount == _tunnelOutTicks)
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
                    
                    // Reduce our agitation factor.
                    _agitationFactor = _agitationFactor / 2;
                    
                    // Notify listeners of the change of atomic weight.
                    int newAtomicWeight = getAtomicWeight();
                    for (int j = 0; j < _listeners.size(); j++){
                        ((Listener)_listeners.get( i )).atomicWeightChanged(newAtomicWeight);
                    }
                    break;
                }
            }
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
                constituent.tunnel( 0, CORE_RADIUS, TUNNEL_OUT_RADIUS );
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
        
        _tickCount = 0;
        
        // Reset the tunnel out counter.
        Random rand = new Random();
        _tunnelOutTicks = MIN_TUNNELING_TICKS + rand.nextInt( MAX_TUNNELING_TICKS - MIN_TUNNELING_TICKS );

        // See if a particle is tunneling or has tunneled.
        if (_tunnelingParticle != null){
            // Reset the tunneling, i.e. return the particle to the nucleus.
            _tunnelingParticle.resetTunneling();
            
            // Add this particle back to our list of constituents.
            _constituents.add( _tunnelingParticle );
            _tunnelingParticle = null;
            _numAlphas++;
            
            // Become more agitated, since we are now less stable.
            _agitationFactor = _agitationFactor * 2;
            
            // Let the listeners know that the atomic weight has changed.
            for (int i = 0; i < _listeners.size(); i++){
                ((Listener)_listeners.get( i )).atomicWeightChanged( getAtomicWeight() );
            }
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
    
    public static interface Listener {
        void positionChanged();
        void atomicWeightChanged(int newAtomicWeight);
    }
}
