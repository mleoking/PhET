/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * This class is used to represent an alpha particle in a model of alpha
 * radiation behavior.
 *
 * @author John Blanco
 */
public class AlphaParticle implements SubatomicParticle {
    
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
    private static Random _rand = new Random();
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    private ArrayList _listeners = new ArrayList();
    
    // Location in space of this particle.
    private Point2D.Double _position;
    
    // Values used for autonomous translation.
    private double _xVelocity;
    private double _yVelocity;
    private double _xAcceleration;
    private double _yAcceleration;
    
    // State of this particle with respect to tunneling out.
    private int _tunnelingState = IN_NUCLEUS;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public AlphaParticle(double xPos, double yPos)
    {
        _position = new Point2D.Double(xPos, yPos);

        _xVelocity = MAX_AUTO_TRANSLATE_AMT *((_rand.nextDouble() * 2.0) - 1.0); 
        _yVelocity = MAX_AUTO_TRANSLATE_AMT * ((_rand.nextDouble() * 2.0) - 1.0); 
    }
    
    //------------------------------------------------------------------------
    // Accessor methods
    //------------------------------------------------------------------------
    
    public void setPosition( Point2D position ){
        
        _position.setLocation( position );

        // Notify all listeners of the position change.
        for (int i = 0; i < _listeners.size(); i++)
        {
            ((Listener)_listeners.get( i )).positionChanged(this); 
        }        
    }
    
    public void setPosition( double xPos, double yPos ){
        _position.setLocation(xPos, yPos);

        // Notify all listeners of the position change.
        for (int i = 0; i < _listeners.size(); i++)
        {
            ((Listener)_listeners.get( i )).positionChanged(this); 
        }        
    }
    
    public Point2D.Double getPosition(){
        return new Point2D.Double(_position.getX(), _position.getY());
    }
    
    public Point2D.Double getPositionReference(){
        return _position;
    }
    
    public int getTunnelingState(){
        return _tunnelingState;
    }
    
    //------------------------------------------------------------------------
    // Behavior methods
    //------------------------------------------------------------------------

    /**
     * Move the particle by some amount.
     */
    public void translate(double dx, double dy)
    {
        _position.x += dx;
        _position.y += dy;
        
        // Notify all listeners of the position change.
        for (int i = 0; i < _listeners.size(); i++)
        {
            ((Listener)_listeners.get( i )).positionChanged(this); 
        }
    }
    
    /**
     * @see SubatomicParticle
     */
    public void tunnel(Point2D center, double minDistance, double nucleusRadius, double tunnelRadius)
    {
        double maxDistance = nucleusRadius;
        
        if (_rand.nextDouble() > 0.98)
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
        
        double multiplier = _rand.nextDouble();
        
        if (multiplier > 0.8)
        {
            // Cause the distribution to tail off in the outer regions of the
            // nucleus.
            multiplier = _rand.nextDouble() * _rand.nextDouble();
        }
        
        double newRadius = minDistance + (multiplier * (maxDistance - minDistance));
       
        // Calculate the new angle, in radians, from the origin.
        double newAngle = _rand.nextDouble() * 2 * Math.PI;
        
        // Convert from polar to Cartesian coordinates.
        double xPos = Math.cos( newAngle ) * newRadius;
        double yPos = Math.sin( newAngle ) * newRadius;
        
        // Save the new position.
        _position.setLocation( xPos + center.getX(), yPos + center.getY());

        // Notify all listeners of the position change.
        for (int i = 0; i < _listeners.size(); i++)
        {
            ((Listener)_listeners.get( i )).positionChanged(this); 
        }        
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
        
        if (_rand.nextBoolean()){
            // Go out on the right side.
            newAngle = Math.PI / 3 + (_rand.nextDouble() * Math.PI / 3);
        }
        else {
            // Go out on left side.
            newAngle = Math.PI + (Math.PI / 3) + (_rand.nextDouble() * Math.PI / 3);
        }
        
        double xPos = Math.sin( newAngle ) * radius;
        double yPos = Math.cos( newAngle ) * radius;
        
        // Save the new position.
        _position.setLocation( xPos + center.getX(), yPos + center.getY() );

        // Notify all listeners of the position change.
        for (int i = 0; i < _listeners.size(); i++)
        {
            ((Listener)_listeners.get( i )).positionChanged(this); 
        }
        
        // Set our initial values for translating out of the nucleus.
        _xVelocity = 0.75 * Math.sin( newAngle );
        _yVelocity = 0.75 * Math.cos( newAngle );
        _xAcceleration = 0.3 * _xVelocity;
        _yAcceleration = 0.3 * _yVelocity;
        
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
        
        if (Point2D.distance( 0, 0, _position.x, _position.y ) > MAX_TUNNELING_DISTANCE){
            // This is far enough away that we don't need to bother moving it any more.
            _tunnelingState = TUNNELED_OUT_OF_NUCLEUS;
            return;
        }
        
        // Move some amount.
        _position.x += _xVelocity;
        _position.y += _yVelocity;
        
        // Notify all listeners of the position change.
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get( i )).positionChanged(this); 
        }
        
        // Accelerate.
        _xVelocity += _xAcceleration;
        _yVelocity += _yAcceleration;
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
        _position.x = 0;
        _position.y = 0;
        
        // Reset the tunneling state.
        _tunnelingState = IN_NUCLEUS;
    }
    
    
    //------------------------------------------------------------------------
    // Listener support
    //------------------------------------------------------------------------

    public void addListener(Listener listener){

        if (_listeners.contains( listener ))
        {
            // Don't bother re-adding.
            return;
        }
        
        _listeners.add( listener );
    }
    
    public void removeListener(Listener listener){
        _listeners.remove( listener );
    }
    
    public static interface Listener {
        void positionChanged(AlphaParticle alpha);
    }
}
