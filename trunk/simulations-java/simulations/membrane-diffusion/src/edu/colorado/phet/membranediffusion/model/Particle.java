/* Copyright 2008-2009, University of Colorado */

package edu.colorado.phet.membranediffusion.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Abstract base class for a simulated particle.  It is intended that this be subclassed
 * for each specific particle type used in the simulation.
 *
 * @author John Blanco
 */
public abstract class Particle implements IMovable {
    
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
	
	private double DEFAULT_PARTICLE_RADIUS = 0.75;  // In nanometers.
	
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
	
    protected ArrayList<Listener> listeners = new ArrayList<Listener>();
    
    // Location in space of this particle, units are nano-meters.
    private Point2D.Double position;
    
    // Motion strategy for moving this particle around.
    private MotionStrategy motionStrategy = new StillnessMotionStrategy();
    
    // Opaqueness value, ranges from 0 (completely transparent) to 1 
    // (completely opaque).
    private double opaqueness = 1;
    
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
    	this(0,0);
    }
    
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    
    /**
     * Static factory method for creating a particle of the specified type.
     */
    public static Particle createParticle(ParticleType particleType){
    	
    	Particle newParticle = null;
    	
		switch (particleType){
		case POTASSIUM_ION:
        	newParticle = new PotassiumIon();
        	break;
		case SODIUM_ION:
        	newParticle = new SodiumIon();
        	break;
		default:
			System.err.println("Error: Unrecognized particle type.");
			assert false;
		}
 
		return newParticle;
    }
    
    public abstract ParticleType getType(); 
    
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
    
	public void setOpaqueness(double opaqueness){
		if (this.opaqueness != opaqueness){
			this.opaqueness = opaqueness;
			notifyOpaquenessChanged();
		}
	}
	
	public double getOpaqueness(){
		return opaqueness;
	}
	
    protected boolean isAvailableForCapture() {
    	// If the particle is not in the process of trying to traverse a
    	// membrane channel, then it should be considered to be available for
    	// capture.
		return !(motionStrategy instanceof TraverseChannelMotionStrategy);
	}

    protected void notifyPositionChanged(){
        // Notify all listeners of the position change.
        for (Listener listener : listeners)
        {
            listener.positionChanged(); 
        }        
    }
    
    protected void notifyOpaquenessChanged(){
        // Notify all listeners of the opaqueness change.
        for (Listener listener : listeners)
        {
            listener.opaquenessChanged(); 
        }        
    }
    
    /**
     * This is called to remove this particle from the model.  It simply sends
     * out a notification of removal, and all listeners (including the view)
     * are expected to act appropriately and to remove all references.
     */
    public void removeFromModel(){
    	notifyRemoved();
    }
    
    /**
     * Inform all listeners that this element has been removed from the model.
     */
    private void notifyRemoved(){
    	// Copy the list to avoid concurrent modification exceptions.
    	ArrayList<Listener> listenersCopy = new ArrayList<Listener>(listeners); 
    	// Notify all listeners that this particle was removed from the model.
        for (Listener listener : listenersCopy)
        {
            listener.removedFromModel(); 
        }        
    }
    
    public void setMotionStrategy(MotionStrategy motionStrategy){
    	this.motionStrategy = motionStrategy; 
    }
    
    public MotionStrategy getMotionStrategyRef(){
        return motionStrategy;
    }
    
    /**
     * Get the diameter of this particle in nano meters.  This obviously
     * assumes a round particle.
     */
    public double getDiameter(){
    	return getRadius() * 2;
    }

    public double getRadius(){
    	return DEFAULT_PARTICLE_RADIUS;   // Default value, override if needed to support other particles.
    }
    
    /**
     * Get the base color to be used when representing this particle.
     */
    abstract public Color getRepresentationColor();

    //------------------------------------------------------------------------
    // Behavior methods
    //------------------------------------------------------------------------
    
    /**
     * Execute any time-based behavior.
     * 
     * @param dt - delta time in milliseconds.
     */
    public void stepInTime(double dt){
    	motionStrategy.move(this, dt);
    }
    
    //------------------------------------------------------------------------
    // Listener support
    //------------------------------------------------------------------------

    public void addListener(Listener listener) {
        if (listeners.contains( listener ))
        {
            // Don't bother re-adding.
        	System.err.println(getClass().getName() + "- Warning: Attempting to re-add a listener that is already listening.");
        	assert false;
            return;
        }
        
        listeners.add( listener );
    }
    
    public void removeListener(Listener listener){
    	listeners.remove(listener);
    }

    public interface Listener {
        void positionChanged();
        void opaquenessChanged();
        void removedFromModel();
    }
    
    public static class Adapter implements Listener {
		public void positionChanged() {}
		public void opaquenessChanged() {}
		public void removedFromModel() {}
    }
}
