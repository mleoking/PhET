/* Copyright 2008-2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.common.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Abstract base class for subatomic particles.
 *
 * @author John Blanco
 */
public abstract class SubatomicParticle {
    
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
	
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
	
    protected ArrayList<Listener> _listeners = new ArrayList<Listener>();
    
    // Location in space of this particle.
    private Point2D.Double _position;
    
    // Velocity of this particle.
    private double _xVelocity;
    private double _yVelocity;
    
    // Acceleration of this particle.
    private double _xAcceleration;
    private double _yAcceleration;
    
    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------

    /**
     * Construct a particle.
     * 
     * @param xPos - Initial X position of this particle.
     * @param yPos - Initial Y position of this particle.
     * @param xVel - Initial velocity in the X direction.
     * @param yVel - Initial velocity in the Y direction.
     * @param tunnelingEnabled - Controls whether this particle should exhibit
     */
    public SubatomicParticle(double xPos, double yPos, double xVel, double yVel){
    	_position = new Point2D.Double(xPos, yPos);
    	_xVelocity = xVel;
    	_yVelocity = yVel;
    }
    
    /**
     * Construct a particle that is not moving.
     * 
     * @param xPos - Initial X position of this particle.
     * @param yPos - Initial Y position of this particle.
     * @param tunnelingEnabled - Controls whether this particle should exhibit
     * quantum tunneling behavior. 
     */
    public SubatomicParticle(double xPos, double yPos)
    {
        this(xPos, yPos, 0, 0);
    }
    
    //------------------------------------------------------------------------
    // Accessor methods
    //------------------------------------------------------------------------
    
    public Point2D.Double getPosition()
    {
        return new Point2D.Double(_position.getX(), _position.getY());
    }
    
    public Point2D.Double getPositionReference()
    {
        return _position;
    }
    
    public void setPosition(Point2D newPosition)
    {
        setPosition(newPosition.getX(), newPosition.getY());
    }
    
    public void setPosition(double xPos, double yPos)
    {
        _position.setLocation( xPos, yPos );
        notifyPositionChanged();
    }
    
    public void setVelocity(double xVel, double yVel){
        _xVelocity = xVel;
        _yVelocity = yVel;
    }
    
    public void setAcceleration(double xAcc, double yAcc){
        _xAcceleration = xAcc;
        _yAcceleration = yAcc;
    }
    
    protected void notifyPositionChanged(){
        // Notify all listeners of the position change.
        for (Listener listener : _listeners)
        {
            listener.positionChanged(this); 
        }        
    }
    
    //------------------------------------------------------------------------
    // Behavior methods
    //------------------------------------------------------------------------
    
    /**
     * Moves this particle based on its current velocity and acceleration.
     */
    public void translate(){
        
        // Update the position.
        _position.x += _xVelocity;
        _position.y += _yVelocity;
        notifyPositionChanged();
        
        // Update the velocity.
        _xVelocity += _xAcceleration;
        _yVelocity += _yAcceleration;
    }
    
    /**
     * Tunnel to another location.
     * 
     * @param center - Location from which to tunnel.
     * @param minDistance - Minimum tunneling distance, often zero.
     * @param maxDistance1 - The usual value used for the max tunneling distance.
     * @param maxDistance2 - A value that is more rarely used and is usually
     * equal to or bigger than the other max distance.  Originally added to
     * allow alpha particles to sometimes tunnel to the area between the edge
     * of the nucleus to the tunneling radius.
     */
    public void tunnel(Point2D center, double minDistance, double maxDistance1, double maxDistance2){
    	// Does nothing in base class.
    }
    
    /**
     * Jitter a little, meaning move a small amount from the current position
     * then back.
     */
    public void jitter(){
    	// Does nothing in the base class.
    }
    
    //------------------------------------------------------------------------
    // Listener support
    //------------------------------------------------------------------------

    public void addListener(Listener listener)
    {
        if (_listeners.contains( listener ))
        {
            // Don't bother re-adding.
            return;
        }
        
        _listeners.add( listener );
    }
    
    public void removeListener(Listener listener){
    	_listeners.remove(listener);
    }
    
    public static interface Listener {
        void positionChanged(SubatomicParticle particle);
    }
}
