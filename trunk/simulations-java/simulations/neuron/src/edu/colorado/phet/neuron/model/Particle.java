/* Copyright 2008-2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Abstract base class for a particle.
 *
 * @author John Blanco
 */
public class Particle {
    
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
	
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
	
    protected ArrayList<Listener> _listeners = new ArrayList<Listener>();
    
    // Location in space of this particle.
    private Point2D.Double position;
    
    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------

    /**
     * Construct a particle.
     * 
     * @param xPos - Initial X position of this particle.
     * @param yPos - Initial Y position of this particle.
     */
    public Particle(double xPos, double yPos) {
    	position = new Point2D.Double(xPos, yPos);
    }
    
    public Particle(){
    	
    }
    
    //------------------------------------------------------------------------
    // Accessor methods
    //------------------------------------------------------------------------
    
    public Point2D.Double getPosition() {
        return new Point2D.Double(position.getX(), position.getY());
    }
    
    public Point2D.Double getPositionReference() {
        return position;
    }
    
    public void setPosition(Point2D newPosition) {
        setPosition(newPosition.getX(), newPosition.getY());
    }
    
    public void setPosition(double xPos, double yPos) {
        position.setLocation( xPos, yPos );
        notifyPositionChanged();
    }
    
    protected void notifyPositionChanged(){
        // Notify all listeners of the position change.
        for (Listener listener : _listeners)
        {
            listener.positionChanged(); 
        }        
    }
    
    //------------------------------------------------------------------------
    // Behavior methods
    //------------------------------------------------------------------------
    
    /**
     * Execute any time-based behavior.
     */
    public void stepInTime(double dt){
    	// TODO: Strategy pattern will be here.
    }
    
    //------------------------------------------------------------------------
    // Listener support
    //------------------------------------------------------------------------

    public void addListener(Listener listener) {
        if (_listeners.contains( listener ))
        {
            // Don't bother re-adding.
        	System.err.println(getClass().getName() + "- Warning: Attempting to re-add a listener that is already listening.");
        	assert false;
            return;
        }
        
        _listeners.add( listener );
    }
    
    public void removeListener(Listener listener){
    	_listeners.remove(listener);
    }
    
    public static interface Listener {
        void positionChanged();
    }
}
