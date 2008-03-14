/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * This class is used to represent an alpha particle in a model of alpha
 * radiation behavior.
 *
 * @author John Blanco
 */
public class AlphaParticle {
    
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
    static final double MAX_AUTO_TRANSLATE_AMT = 0.5;
    
    // JPB TBD - This is for an experiment, and I should make this come from
    // a better place or be controlled elsewhere at some point.
    static final double MAX_DISTANCE = 7.0;
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    private ArrayList _listeners = new ArrayList();
    
    // Location in space of this particle.
    private Point2D.Double _position;
    
    // Values used for autonomous translation.
    private double _xAutoDelta;
    private double _yAutoDelta;
    private int _changeHysteresis = 3;
    
    // Random number generator, used for creating some random behavior.
    Random _rand = new Random();
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public AlphaParticle(double xPos, double yPos)
    {
        _position = new Point2D.Double(xPos, yPos);

        _xAutoDelta = MAX_AUTO_TRANSLATE_AMT *((_rand.nextDouble() * 2.0) - 1.0); 
        _yAutoDelta = MAX_AUTO_TRANSLATE_AMT * ((_rand.nextDouble() * 2.0) - 1.0); 
    }
    
    //------------------------------------------------------------------------
    // Accessor methods
    //------------------------------------------------------------------------
    
    public Point2D getPosition()
    {
        return new Point2D.Double(_position.getX(), _position.getY());
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
            ((Listener)_listeners.get( i )).positionChanged(); 
        }
    }
    
    /**
     * Tell the particle to move itself based on its current velocity.
     */
    public void autoTranslate()
    {
        if (_changeHysteresis > 0)
        {
            _changeHysteresis--;
        }
        else if (Point2D.distance( _position.x, _position.y, 0, 0 ) > MAX_DISTANCE)
        {
            // Time to "bounce".  This is a simple bouncing algorithm used to
            // minimize computation.
            if (Math.abs( _position.x ) > 3 * Math.abs( _position.y ))
            {
                // Bounce only in x direction.
                _xAutoDelta = - _xAutoDelta;
            }
            else if (Math.abs( _position.y ) > 3 * Math.abs( _position.x ))
            {
                // Bounce only in y direction.
                _yAutoDelta = - _yAutoDelta;
            }
            else
            {
                // Bounce in both directions.
                _xAutoDelta = - _xAutoDelta;
                _yAutoDelta = - _yAutoDelta;
            }
            
            // Reset the hysteresis counter.
            _changeHysteresis = 3;
        }
        else if (_rand.nextDouble() > 0.9)
        {
            // Every once in a while we just randomly change our velocity
            // vector in order to simulate a collision.
            _xAutoDelta = MAX_AUTO_TRANSLATE_AMT *((_rand.nextDouble() * 2.0) - 1.0); 
            _yAutoDelta = MAX_AUTO_TRANSLATE_AMT * ((_rand.nextDouble() * 2.0) - 1.0);             
        }
        
        // Update our position based on our current delta (i.e. velocity).
        _position.x += _xAutoDelta;
        _position.y += _yAutoDelta;
        
        // Notify all listeners of the position change.
        for (int i = 0; i < _listeners.size(); i++)
        {
            ((Listener)_listeners.get( i )).positionChanged(); 
        }        
    }
    
    /**
     * This method simulates the quantum tunneling behavior, which means that
     * it causes the particle to move to some new random location within the
     * confines of the supplied parameters.
     * 
     * @param minDistance - Minimum distance from origin (0,0).  This is
     * generally the radius of the nucleus.
     * @param maxDistance - Maximum distance from origin (0,0).
     * @return New location.
     */
    public Point2D tunnel(double minDistance, double maxDistance)
    {
        double guassian = Math.abs( _rand.nextGaussian() / 5.0 );
        if (guassian > 1.0){
            // Hard limit guassian to a max of 1.0.
            guassian = 1.0;
        }
        double newRadius = minDistance + (guassian * (maxDistance - minDistance));
        
        // Calculate the new angle, in radians, from the origin.
        double newAngle = _rand.nextDouble() * 2 * Math.PI;
        
        // Convert from polar to Cartesian coordinates.
        double xPos = Math.cos( newAngle ) * newRadius;
        double yPos = Math.sin( newAngle ) * newRadius;
        
        // Save the new position.
        _position.setLocation( xPos, yPos );

        // Notify listeners of the change.
        for (int i = 0; i < _listeners.size(); i++)
        {
            ((Listener)_listeners.get( i )).positionChanged(); 
        }
        
        // Return the new position to the caller.
        return new Point2D.Double(_position.getX(), _position.getY());
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
    
    public static interface Listener {
        void positionChanged();
    }
}
