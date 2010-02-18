/* Copyright 2008-2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.neuron.NeuronStrings;

/**
 * Abstract base class for a simulated particle.  It is intended that this be subclassed
 * for each specific particle type used in the simulation.
 *
 * @author John Blanco
 */
public abstract class Particle implements IMovable, IFadable {
    
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
	
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
	
    protected ArrayList<Listener> listeners = new ArrayList<Listener>();
    
    // Location in space of this particle, units are nano-meters.
    private Point2D.Double position;
    
    // Motion strategy for moving this particle around.
    private MotionStrategy motionStrategy = new StillnessMotionStrategy();
    
    // Availability for capture by a membrane channel.
    private boolean availableForCapture = true;
    
    // Opaqueness value, ranges from 0 (completely transparent) to 1 
    // (completely opaque).
    private double opaqueness = 1;
    
    // Fade strategy for fading in and out.
    private FadeStrategy fadeStrategy = new NullFadeStrategy();
    
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
		case PROTEIN_ION:
        	newParticle = new ProteinIon();
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
	
	/**
	 * Set the fade strategy for the element.
	 */
	public void setFadeStrategy(FadeStrategy fadeStrategy){
		this.fadeStrategy = fadeStrategy;
	}
    
    protected boolean isAvailableForCapture() {
		return availableForCapture;
	}

	protected void setAvailableForCapture(boolean availableForCapture) {
		this.availableForCapture = availableForCapture;
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
    
    protected void notifyRemoved(){
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
    
    /**
     * Get the diameter of this particle in nano meters.  This obviously
     * assumes a round particle.
     */
    public double getDiameter(){
    	return 2;  // Default value, override if needed to support other particles.
    }
    
	/**
     * Text to use for labeling this particle.  Often this will be a chemical
     * symbol, but this is not always the case.
     */
    abstract public String getLabelText();
    
    /**
     * Get the base color to be used when representing this particle.
     */
    abstract public Color getRepresentationColor();

    /**
     * Get the color to be used for the textual labels of this particle.
     */
    public Color getLabelColor(){
    	return Color.BLACK;
    }
    
    /**
     * Ionic charge for this particle, in terms of electrons.
     * 
     * @return 0 for neutral particle, 1 for a 1-electron deficit, -1 for a
     * one-electron surplus, etc.
     * 
     * Note: At the time of this writing, there is no requirement for a particle
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
     * Get the string representation for this particle's charge.
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
    	motionStrategy.move(this, this, dt);
    	fadeStrategy.updateOpaqueness(this, dt);
    	if (!fadeStrategy.shouldContinueExisting(this)){
    		// This particle has faded out of existence, so send out a
    		// notification that indicates that it is being removed from the
    		// model.  The thinking here is that everyone with a reference to
    		// this particle should listen for this notification and do any
    		// cleanup and removal of references needed.  If they don't, there
    		// will be memory leaks.
    		notifyRemoved();
    	}
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

    public double getX() {
        return position.x;
    }

    public double getY(){
        return position.y;
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
