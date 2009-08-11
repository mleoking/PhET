/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.nuclearphysics.common.model.SubatomicParticle;

/**
 * This class is used to represent an alpha particle in a model of alpha
 * radiation behavior.
 *
 * @author John Blanco
 */
public class AlphaParticle extends SubatomicParticle {
    
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
    private static final double MAX_AUTO_TRANSLATE_AMT = 0.75;
    
    // Possible states for tunneling.
    public static final int IN_NUCLEUS                 = 0;
    public static final int TUNNELING_OUT_OF_NUCLEUS   = 1;
    public static final int TUNNELED_OUT_OF_NUCLEUS    = 2;
    
    // Distance at which we consider the particle done tunneling, in fm.
    private static final double MAX_TUNNELING_DISTANCE = 1000;
    
    // Random number generator, used for creating some random behavior.
    private static Random RAND = new Random();
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    
    // State of this particle with respect to tunneling out.
    private int _tunnelingState = IN_NUCLEUS;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public AlphaParticle(double xPos, double yPos)
    {
    	super(xPos, yPos, MAX_AUTO_TRANSLATE_AMT *((RAND.nextDouble() * 2.0) - 1.0), 
    			MAX_AUTO_TRANSLATE_AMT * ((RAND.nextDouble() * 2.0) - 1.0));
    }
    
    public int getTunnelingState(){
        return _tunnelingState;
    }
    
    /**
     * @see SubatomicParticle
     */
    public void tunnel(Point2D center, double minDistance, double nucleusRadius, double tunnelRadius)
    {
        double maxDistance = nucleusRadius;
        
        if (RAND.nextDouble() > 0.98)
        {
            // Every once in a while use the tunnel radius as the max distance
            // to which this particle might tunnel.  This creates the effect of
            // having particles occasionally appear as though they are almost
            // tunneling out.
            maxDistance = tunnelRadius;    
        }
        
        // Create a probability distribution that will cause the particles to
        // be fairly evenly spread around the core of the nucleus and appear
        // occasionally at the outer reaches.
        
        double multiplier = RAND.nextDouble();
        
        if (multiplier > 0.8)
        {
            // Cause the distribution to tail off in the outer regions of the
            // nucleus.
            multiplier = RAND.nextDouble() * RAND.nextDouble();
        }
        
        double newRadius = minDistance + (multiplier * (maxDistance - minDistance));
       
        // Calculate the new angle, in radians, from the origin.
        double newAngle = RAND.nextDouble() * 2 * Math.PI;
        
        // Convert from polar to Cartesian coordinates.
        double xPos = Math.cos( newAngle ) * newRadius;
        double yPos = Math.sin( newAngle ) * newRadius;
        
        // Save the new position.
        setPosition( xPos + center.getX(), yPos + center.getY());
    }
    
    /**
     * This method forces the particle to tunnel out of the nucleus.
     * 
     * @param center - Center point from which tunnel out should occur.
     * @param radius - Radius at which it should tunnel out too.
     */
    public void tunnelOut(Point2D center, double radius){
        
        // Make sure we are in the expected state.
        assert (_tunnelingState == IN_NUCLEUS);
        
        // Choose the angle at which to tunnel out.  To assure that it is
        // clear to the user, we only tunnel out at the sides of the
        // nucleus, otherwise the particle tends to disappear too quickly.
        
        double newAngle;
        
        if (RAND.nextBoolean()){
            // Go out on the right side.
            newAngle = Math.PI / 3 + (RAND.nextDouble() * Math.PI / 3);
        }
        else {
            // Go out on left side.
            newAngle = Math.PI + (Math.PI / 3) + (RAND.nextDouble() * Math.PI / 3);
        }
        
        double xPos = Math.sin( newAngle ) * radius;
        double yPos = Math.cos( newAngle ) * radius;
        
        // Save the new position.
        setPosition( xPos + center.getX(), yPos + center.getY() );
        
        // Set our initial values for translating out of the nucleus.
        double xVel = 0.75 * Math.sin( newAngle );
        double yVel = 0.75 * Math.cos( newAngle );
        setVelocity(xVel, yVel);
        setAcceleration(0.3 * xVel, 0.3 * yVel);
        
        // Change our tunneling state.
        _tunnelingState = TUNNELING_OUT_OF_NUCLEUS;
    }
    
    /**
     * This method tells the particle to take its next step in moving away
     * from the nucleus, and is only applicable when the particle is in the
     * process of tunneling out of the nucleus.
     */
    public void moveOut(){
        
        if (_tunnelingState != TUNNELING_OUT_OF_NUCLEUS){
            return;
        }
        
        if (Point2D.distance( 0, 0, getPosition().x, getPosition().y ) > MAX_TUNNELING_DISTANCE){
            // This is far enough away that we don't need to bother moving it any more.
            _tunnelingState = TUNNELED_OUT_OF_NUCLEUS;
            return;
        }
        
        // Move based on current pos/vel/acc settings.
        translate();
    }
    
    /**
     * This method returns to the nucleus a particle that is in the process
     * of tunneling or that has fully tunneled away.
     */
    public void resetTunneling(){
        
        if (_tunnelingState == IN_NUCLEUS){
            // We are currently in the nucleus, so no changes are required.
            return;
        }
        
        // Return our position to the origin.
        setPosition(0, 0);
        
        // Reset the tunneling state.
        _tunnelingState = IN_NUCLEUS;
    }
}
