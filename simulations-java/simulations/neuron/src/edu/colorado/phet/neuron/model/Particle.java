/* Copyright 2008-2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Abstract base class for a simulated particle.  It is intended that this be subclassed
 * for each specific particle type used in the simulation.
 *
 * @author John Blanco
 */
public abstract class Particle implements IMovable, IFadable, IViewableParticle {
    
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
	
	private static final double DEFAULT_PARTICLE_RADIUS = 0.75;  // In nanometers.
	
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
	
    protected ArrayList<IParticleListener> listeners = new ArrayList<IParticleListener>();
    
    // Location in space of this particle, units are nano-meters.
    private Point2D.Double position;
    
    // Motion strategy for moving this particle around.
    private MotionStrategy motionStrategy = new StillnessMotionStrategy();
    
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
		default:
			System.err.println("Error: Unrecognized particle type.");
			assert false;
		}
 
		return newParticle;
    }
    
    /* (non-Javadoc)
     * @see edu.colorado.phet.neuron.model.IViewableParticle#getType()
     */
    public abstract ParticleType getType(); 
    
    /* (non-Javadoc)
     * @see edu.colorado.phet.neuron.model.IViewableParticle#getPosition()
     */
    public Point2D getPosition() {
        return new Point2D.Double(position.getX(), position.getY());
    }
    
    /* (non-Javadoc)
     * @see edu.colorado.phet.neuron.model.IViewableParticle#getPositionReference()
     */
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
			notifyAppearanceChanged();
		}
	}
	
	/* (non-Javadoc)
     * @see edu.colorado.phet.neuron.model.IViewableParticle#getOpaqueness()
     */
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
    	// If the particle is not in the process of trying to traverse a
    	// membrane channel, then it should be considered to be available for
    	// capture.
		return !(motionStrategy instanceof MembraneTraversalMotionStrategy);
	}
    
    /**
     * Get a playback memento, which can be used when doing playback of
     * previous model states.  Note that the memento does not capture all of
     * the particle's state, just enough to support playback.
     */
    public ParticlePlaybackMemento getPlaybackMemento(){
        return new ParticlePlaybackMemento( this );
    }

    protected void notifyPositionChanged(){
        // Notify all listeners of the position change.
        for (IParticleListener listener : listeners)
        {
            listener.positionChanged(); 
        }        
    }
    
    protected void notifyAppearanceChanged(){
        // Notify all listeners of the opaqueness change.
        for (IParticleListener listener : listeners)
        {
            listener.appearanceChanged(); 
        }        
    }
    
    /* (non-Javadoc)
     * @see edu.colorado.phet.neuron.model.IViewableParticle#removeFromModel()
     */
    public void removeFromModel(){
    	notifyRemoved();
    }
    
    /**
     * Inform all listeners that this element has been removed from the model.
     */
    private void notifyRemoved(){
    	// Copy the list to avoid concurrent modification exceptions.
    	ArrayList<IParticleListener> listenersCopy = new ArrayList<IParticleListener>(listeners); 
    	// Notify all listeners that this particle was removed from the model.
        for (IParticleListener listener : listenersCopy)
        {
            listener.removedFromModel(); 
        }        
    }
    
    public void setMotionStrategy(MotionStrategy motionStrategy){
    	this.motionStrategy = motionStrategy; 
    }
    
    /* (non-Javadoc)
     * @see edu.colorado.phet.neuron.model.IViewableParticle#getRadius()
     */
    public double getRadius(){
    	return DEFAULT_PARTICLE_RADIUS;   // Default value, override if needed to support other particles.
    }
    
    /* (non-Javadoc)
     * @see edu.colorado.phet.neuron.model.IViewableParticle#getRepresentationColor()
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
    
    /**
     * Get a "memento", which is an object that is not a full-blown particle
     * but that contains enough information for the playback feature to
     * present a particle in the view.
     */
    public PlaybackParticle getMemento() {
        return new PlaybackParticle( this );
    }
    
    //------------------------------------------------------------------------
    // Listener support
    //------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see edu.colorado.phet.neuron.model.IViewableParticle#addListener(edu.colorado.phet.neuron.model.IParticleListener)
     */
    public void addListener(IParticleListener listener) {
        if (listeners.contains( listener ))
        {
            // Don't bother re-adding.
        	System.err.println(getClass().getName() + "- Warning: Attempting to re-add a listener that is already listening.");
        	assert false;
            return;
        }
        
        listeners.add( listener );
    }
    
    /* (non-Javadoc)
     * @see edu.colorado.phet.neuron.model.IViewableParticle#removeListener(edu.colorado.phet.neuron.model.IParticleListener)
     */
    public void removeListener(IParticleListener listener){
    	listeners.remove(listener);
    }
}
