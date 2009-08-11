/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.common.model;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.nuclearphysics.model.SubatomicParticle;

/**
 * Class the implements the behavior of nucleon (i.e. proton and neutron)
 * model elements.
 *
 * @author John Blanco
 */
public class Nucleon extends SubatomicParticle {
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------

    // Random number generator, used for creating some random behavior.
    private static Random _rand = new Random();

    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    
    // Boolean that controls whether this particle should exhibit quantum
    // tunneling behavior.
    private boolean _tunnelingEnabled;
    
    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------

    /**
     * Construct a nucleon that is not moving.
     * 
     * @param xPos - Initial X position of this particle.
     * @param yPos - Initial Y position of this particle.
     * @param tunnelingEnabled - Controls whether this particle should exhibit
     * quantum tunneling behavior. 
     */
    public Nucleon(double xPos, double yPos, boolean tunnelingEnabled)
    {
        this(xPos, yPos, 0, 0, tunnelingEnabled);
    }
    
    /**
     * Construct a nucleon.
     * 
     * @param xPos - Initial X position of this particle.
     * @param yPos - Initial Y position of this particle.
     * @param xVel - Initial velocity in the X direction.
     * @param yVel - Initial velocity in the Y direction.
     * @param tunnelingEnabled - Controls whether this particle should exhibit
     */
    public Nucleon(double xPos, double yPos, double xVel, double yVel, boolean tunnelingEnabled){
    	super(xPos, yPos, xVel, yVel);
        _tunnelingEnabled = tunnelingEnabled;
    }
    
    public void setTunnelingEnabled(boolean tunnelingEnabled){
        _tunnelingEnabled = tunnelingEnabled;
    }
    
    public boolean getTunnelingEnabled(){
        return _tunnelingEnabled;
    }
    
    //------------------------------------------------------------------------
    // Behavior methods
    //------------------------------------------------------------------------
    
    /**
     * This method simulates the quantum tunneling behavior, which means that
     * it causes the particle to move to some new random location within the
     * confines of the supplied parameters.
     * 
     * @param minDistance - Minimum distance from origin (0,0).  This is
     * generally 0.
     * @param nucleusRadius - Radius of the nucleus where this particle resides.
     * @param tunnelRadius - Radius at which this particle could tunnel out of nucleus.
     */
    public void tunnel(Point2D center, double minDistance, double nucleusRadius, double tunnelRadius)
    {
        if (_tunnelingEnabled){
            
            // Create a probability distribution that will cause the particles to
            // be fairly evenly spread around the core of the nucleus and appear
            // occasionally at the outer reaches.
    
            double multiplier = _rand.nextDouble();
            
            if (multiplier > 0.8){
                // Cause the distribution to tail off in the outer regions of the
                // nucleus.
                multiplier = _rand.nextDouble() * _rand.nextDouble();
            }
            
            double newRadius = minDistance + (multiplier * (nucleusRadius - minDistance));
            
            // Calculate the new angle, in radians, from the origin.
            double newAngle = _rand.nextDouble() * 2 * Math.PI;
            
            // Convert from polar to Cartesian coordinates.
            double xPos = Math.cos( newAngle ) * newRadius;
            double yPos = Math.sin( newAngle ) * newRadius;
            
            // Save the new position.
            setPosition( xPos + center.getX(), yPos + center.getY());
        }
    }
}
