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
    private static final int MAX_TUNNELING_TICKS = 50;
    
    // This controls the amount of agitation that the nucleus exhibits.
    // One is the lowest allowable value and represents the most agitated
    // state.
    private static final int INVERSE_AGITATION_FACTOR = 8;
    
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
    
    // JPB TBD
    int _churnCount = 0;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public AtomicNucleus(double xPos, double yPos, int atomicNumber)
    {
        // Set the initial position for this nucleus.
        position = new Point2D.Double(xPos, yPos);
        
        // Figure out the proportions of various particles based on the atomic number.
        _numAlphas   = (atomicNumber / 2) / 4;  // Assume half of all particles are tied up in alphas.
        _numProtons = (atomicNumber / 4);
        _numNeutrons  = atomicNumber - _numProtons - (_numAlphas * 4);

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
        
        // Decide when tunneling should occur.
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
    
    //------------------------------------------------------------------------
    // Other methods
    //------------------------------------------------------------------------
    
    /**
     * This method lets this model element know that the clock has ticked.  In
     * response, the nucleus generally 'agitates' a bit.
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
                    // This one will do.
                    _tunnelingParticle = (AlphaParticle)_constituents.get( i );
                    _constituents.remove( i );
                    _numAlphas--;
                    _tunnelingParticle.tunnelOut( TUNNEL_OUT_RADIUS + 1.0 );
                    break;
                }
            }
        }
        
        // Move the constituent particles to create the visual effect of a
        // very dynamic nucleus.  In order to allow different levels of
        // agitation, we don't necessarily move all particles every time.
        for (int i = _churnCount; i < _constituents.size(); i+=INVERSE_AGITATION_FACTOR)
        {
            AtomicNucleusConstituent constituent = (AtomicNucleusConstituent)_constituents.get( i );
            constituent.tunnel( 0, CORE_RADIUS, TUNNEL_OUT_RADIUS );
        }
        _churnCount = (_churnCount + 1) % INVERSE_AGITATION_FACTOR;
        
        // If we have a tunneling particle, move it.
        if (_tunnelingParticle != null){
            _tunnelingParticle.moveOut();
        }
    }
    
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
    }
}
