// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.nuclearphysics.util;

import java.awt.Image;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * This class provides a Piccolo image node that moves autonomously based on
 * an initial velocity and spin rate.
 *
 * @author John Blanco
 */
public class MovingImageNode extends PImage {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    // Variables that control how this node moves.
    double _xVelocity;
    double _yVelocity;
    double _spin;
    
    // Variables needed to register for and respond to clock ticks.
    SwingClock   _clock;
    ClockAdapter _clockAdapter;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param image - Image to be displayed by this PNode.
     * @param xVel - Velocity in X direction in world units per clock tick.
     * @param yVel - Velocity in Y direction in world units per clock tick.
     * @param spin - Spin rate in radians per clock tick.
     * @param clock - Simulation clock that is providing the ticks.
     */
    public MovingImageNode(Image image, double xVel, double yVel, double spin, SwingClock clock){
        
        super(image);
        
        _xVelocity = xVel;
        _yVelocity = yVel;
        _spin = spin;
        _clock = clock;
        
        // Register as a listener for clock ticks.
        _clockAdapter = new ClockAdapter(){
            public void clockTicked(ClockEvent clockEvent){
                handleClockTicked(clockEvent);
            }
        };
        _clock.addClockListener( _clockAdapter );
    }
    
    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------
    
    /**
     * Perform any cleanup necessary before being garbage collected.
     */
    public void cleanup(){
        // Remove ourself as a listener from any place that we have registered
        // in order to avoid memory leaks.
        _clock.removeClockListener( _clockAdapter );
    }

    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------
    
    private void handleClockTicked( ClockEvent clockEvent ){
        
        rotate(_spin);
        setOffset(getOffset().getX() + _xVelocity, getOffset().getY() + _yVelocity);
    }
}
