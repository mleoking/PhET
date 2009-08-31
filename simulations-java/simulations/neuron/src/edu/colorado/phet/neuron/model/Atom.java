/* Copyright 2008-2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.neuron.NeuronStrings;

/**
 * Abstract base class for an atom.  It is intended that this be subclassed
 * for each specific atom type used in the simulation.
 *
 * @author John Blanco
 */
public abstract class Atom {
    
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
	
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
	
    protected ArrayList<Listener> _listeners = new ArrayList<Listener>();
    
    // Location in space of this particle, units are nano-meters.
    private Point2D.Double position;
    
    // Velocity of this particle in nm/sec.
    private Vector2D velocity = new Vector2D.Double(0, 0);
    
    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------

    /**
     * Construct a particle.
     * 
     * @param xPos - Initial X position of this particle.
     * @param yPos - Initial Y position of this particle.
     */
    public Atom(double xPos, double yPos) {
    	position = new Point2D.Double(xPos, yPos);
    }
    
    public Atom(){
    	this(0,0);
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
    
    /**
     * Get the diameter of this atom in nano meters.
     */
    public double getDiameter(){
    	return 5;  // Default value, override if needed to support other atoms.
    }
    
    public Vector2D getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2D velocity) {
		setVelocity(velocity.getX(), velocity.getY());
	}
	
	public void setVelocity(double xVel, double yVel) {
		velocity.setComponents(xVel, yVel);
	}

	/**
     * Chemical symbol for this atom, e.g. Pb or K.
     */
    abstract public String getChemicalSymbol();
    
    /**
     * Get the base color to be used when representing this atom.
     */
    abstract public Color getRepresentationColor();

    /**
     * Ionic charge for this atom, in terms of electrons.
     * 
     * @return 0 for neutral atom, 1 for a 1-electron deficit, -1 for a one-
     * electron surplus, etc.
     * 
     * Note: At the time of this writing, there is no requirement for an atom
     * to ever change its charge within the sim, so it is assumed that for our
     * purposes, this is essentially a permanent characteristic.  This is
     * obviously not true in nature, and will need to change if the
     * requirements of the simulation change.  
     */
    public int getCharge(){
    	// Assume uncharged unless overridden.
    	return 0;
    }
    
    /**
     * Get the string representation for this atom's charge.
     */
    public String getChargeString(){
    	
    	String retVal = null;
    	
    	if (getCharge() == 0){
    		retVal = "";
    	}
    	else if (getCharge() > 0){
    		retVal = NeuronStrings.POSITIVE_ION_SYMBOL;
    		if (getCharge() > 1){
    			retVal += Integer.toString(getCharge());
    		}
    	}
    	else if (getCharge() < 0){
    		retVal = NeuronStrings.NEGATIVE_ION_SYMBOL;
			if (getCharge() < -1){
				retVal += Integer.toString(Math.abs(getCharge()));
			}
    	}
    	
    	return retVal;
    }
    
    //------------------------------------------------------------------------
    // Behavior methods
    //------------------------------------------------------------------------
    
    /**
     * Execute any time-based behavior.
     * 
     * @param dt - delta time in milliseconds.
     */
    public void stepInTime(double dt){
    	// Update position based on velocity.
    	double newPosX = position.getX() + (velocity.getX() * dt / 1000);
    	double newPosY = position.getY() + (velocity.getY() * dt / 1000);
    	setPosition(newPosX, newPosY);
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

    public double getX() {
        return position.x;
    }

    public double getY(){
        return position.y;
    }

    public static interface Listener {
        void positionChanged();
    }
}
