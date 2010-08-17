/* Copyright 2009, University of Colorado */

package edu.colorado.phet.membranediffusion.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import edu.umd.cs.piccolo.util.PDimension;


/**
 * Abstract base class for membrane channels, which represent any channel
 * through which atoms can go through to cross a membrane.
 * 
 * @author John Blanco
 */
public abstract class MembraneChannel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final double SIDE_HEIGHT_TO_CHANNEL_HEIGHT_RATIO = 1.3;
	protected static final Random RAND = new Random();
	
	private static final double DEFAULT_PARTICLE_VELOCITY = 100000; // In nanometers per sec of sim time.

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	// Reference to the model that contains that particles that will be moving
	// through this channel.
	private IParticleCapture modelContainingParticles;
	
	// Member variables that control the size and position of the channel.
	private Point2D centerLocation = new Point2D.Double();
	private Dimension2D channelSize = new PDimension(); // Size of channel only, i.e. where the atoms pass through.
	private Dimension2D overallSize = new PDimension(); // Size including edges.
	
	// Variable that defines how open the channel is.
	private double openness = 0;  // Valid range is 0 to 1, 0 means fully closed, 1 is fully open.
	
	// Array of listeners.
	private ArrayList<Listener> listeners = new ArrayList<Listener>();
	
	// Capture zones, which is where particles can be captured by this
	// channel.  There are two, one for the top of the channel and one for
	// the bottom.  There is generally no enforcement of which is which, so it
	// is the developer's responsibility to position the channel appropriately
	// on the cell membrane.
	private CaptureZone uppperCaptureZone = new NullCaptureZone();
	private CaptureZone lowerCaptureZone = new NullCaptureZone();
	
	// Time values that control how often this channel requests an ion to move
	// through it.  These are initialized here to values that will cause the
	// channel to never request any ions and must be set by the base classes
	// in order to make capture events occur.
	private double captureCountdownTimer = Double.POSITIVE_INFINITY;
	private double minInterCaptureTime = Double.POSITIVE_INFINITY;
	private double maxInterCaptureTime = Double.POSITIVE_INFINITY;
	
	// Velocity for particles that move through this channel.
	private double particleVelocity = DEFAULT_PARTICLE_VELOCITY;
	
	// Flag for whether this channel is "user controlled", meaning that the
	// user is moving it around in the play area.  It is initially set to be
	// true because in this sim, all channels are added by the user dragging
	// them into the play area.
	private boolean userControlled = true;
	
	// List of all particles currently traversing this channel.  This is
	// needed for handling collisions and for aborting traversals if the user
	// grabs the channel.
	private ArrayList<Particle> particlesTraversingChannel = new ArrayList<Particle>();
	
	// Handler for notifications of particles that have completed the traversal
	// of this membrane channel.
	private MotionStrategy.Listener channelTraversalListener = new MotionStrategy.Adapter(){
        @Override
        public void strategyComplete( IMovable movableElement ) {
            assert particlesTraversingChannel.contains( movableElement );
            // This particle has completed traversing the channel, so remove
            // if from our list.
            particlesTraversingChannel.remove( movableElement );
            // Don't need to listen anymore.
            movableElement.getMotionStrategy().removeListener( channelTraversalListener );
        }
	};
	
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

	public MembraneChannel(double channelWidth, double channelHeight, IParticleCapture modelContainingParticles){
		channelSize.setSize(channelWidth, channelHeight);
		overallSize.setSize(channelWidth * 2.1, channelHeight * SIDE_HEIGHT_TO_CHANNEL_HEIGHT_RATIO);
		this.modelContainingParticles = modelContainingParticles;
	}
	
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
	
	/**
	 * Static factory method for creating a membrane channel of the specified
	 * type.
	 */
	public static MembraneChannel createMembraneChannel(MembraneChannelTypes channelType, IParticleCapture particleModel,
			IHodgkinHuxleyModel hodgkinHuxleyModel){
		
		MembraneChannel membraneChannel = null;
		
    	switch (channelType){
    	case SODIUM_LEAKAGE_CHANNEL:
    		membraneChannel = new SodiumLeakageChannel(particleModel, hodgkinHuxleyModel);
    		break;
    		
    	case SODIUM_GATED_CHANNEL:
    		membraneChannel = new SodiumGatedChannel(particleModel, hodgkinHuxleyModel);
    		break;
    		
    	case POTASSIUM_LEAKAGE_CHANNEL:
    		membraneChannel = new PotassiumLeakageChannel(particleModel, hodgkinHuxleyModel);
    		break;
    		
		case POTASSIUM_GATED_CHANNEL:
			membraneChannel = new PotassiumGatedChannel(particleModel, hodgkinHuxleyModel);
			break;
    	}
    	
    	assert membraneChannel != null; // Should be able to create all types of channels.
    	return membraneChannel;
	}
	
	/**
	 * Check the supplied list of atoms to see if any are in a location where
	 * this channel wants to take control of them.
	 * 
	 * @param freeAtoms - List of atoms that can be potentially taken.  Any
	 * atoms that are taken are removed from the list.
	 * @return List of atoms for which this channel is taking control.
	 */
	/*
	 * TODO: Feb 12 2010 - The paradigm for moving particles around is changing from having
	 * them controlled by the AxonModel and the channels to having a motion strategy set on
	 * them and have them move themselves.  This routine is being removed as part of that
	 * effort, and should be deleted or reinstated at some point in time.

	abstract public ArrayList<Particle> checkTakeControlParticles(final ArrayList<Particle> freeAtoms);
	*/

	abstract protected ParticleType getParticleTypeToCapture();
	
	/**
	 * Reset the channel.
	 */
	public void reset(){
		captureCountdownTimer = Double.POSITIVE_INFINITY;
	}
	
	/**
	 * Returns a boolean value that says whether or not the channel should be
	 * considered open.
	 * 
	 * @return
	 */
	protected boolean isOpen(){
		// The threshold values used here are arbitrary, and can be changed if
		// necessary.
		return (getOpenness() > 0.3);
	}
	
	/**
	 * Determine whether the provided point is inside the channel.
	 * 
	 * @param pt
	 * @return
	 */
	public boolean isPointInChannel(Point2D pt){
		// Note: A rotational angle of zero is considered to be lying on the
		// side.  Hence the somewhat odd-looking use of height and width in
		// the determination of the channel shape.
		Shape channelShape = new Rectangle2D.Double(
				centerLocation.getX() - channelSize.getHeight() / 2,
				centerLocation.getY() - channelSize.getWidth() / 2,
				channelSize.getHeight(),
				channelSize.getWidth());
		return channelShape.contains(pt);
	}
	
	/**
	 * TODO: This is a temp routine for debug.
	 */
	public Shape getChannelTestShape(){
		Shape channelShape = new Rectangle2D.Double(
				centerLocation.getX() - channelSize.getHeight() / 2,
				centerLocation.getY() - channelSize.getWidth() / 2,
				channelSize.getHeight(),
				channelSize.getWidth());
		return channelShape;
	}
	
	/**
	 * Implements the time-dependent behavior of the gate.
	 * 
	 * @param dt - Amount of time step, in milliseconds.
	 */
	public void stepInTime(double dt){
		if (captureCountdownTimer != Double.POSITIVE_INFINITY){
			if (isOpen()){
				captureCountdownTimer -= dt;
				if (captureCountdownTimer <= 0){
					Particle particle = modelContainingParticles.requestParticleThroughChannel(getParticleTypeToCapture(), this);
					if (particle != null){
					    particlesTraversingChannel.add( particle );
					}
					restartCaptureCountdownTimer();
				}
			}
			else{
				// If the channel is closed, the countdown timer shouldn't be
				// running, so this code is generally hit when the membrane
				// just became closed.  Turn off the countdown timer by
				// setting it to infinity.
				captureCountdownTimer = Double.POSITIVE_INFINITY;
			}
		}
	}
	
	/**
	 * Set the motion strategy for a particle that will cause the particle to
	 * traverse the channel.  This version is the one that implements the
	 * behavior for crossing through the generic membrane.
	 * 
	 * @param particle
	 * @param maxVelocity
	 */
	public void moveParticleThroughChannel(Particle particle, Rectangle2D preTraversalBounds, 
	        Rectangle2D postTraversalBounds, double maxVelocity){
	    
		particle.setMotionStrategy(new TraverseChannelMotionStrategy(this, particle.getPosition(), 
		        preTraversalBounds, postTraversalBounds, maxVelocity));
		// Add a listener so that we get a notification when the particle has
		// completed the traversal.
		particle.getMotionStrategy().addListener( channelTraversalListener );
	}
	
	protected double getParticleVelocity() {
		return particleVelocity;
	}

	protected void setParticleVelocity(double particleVelocity) {
		this.particleVelocity = particleVelocity;
	}
	
	/**
	 * Start or restart the countdown timer which is used to time the event
	 * where a particle is captured for movement across the membrane.
	 */
	protected void restartCaptureCountdownTimer(){
		if (minInterCaptureTime != Double.POSITIVE_INFINITY && maxInterCaptureTime != Double.POSITIVE_INFINITY){
			assert maxInterCaptureTime >= minInterCaptureTime;
			captureCountdownTimer = minInterCaptureTime + RAND.nextDouble() * (maxInterCaptureTime - minInterCaptureTime);
		}
		else{
			captureCountdownTimer = Double.POSITIVE_INFINITY;
		}
	}
	
	/**
	 * Get the identifier for this channel type.
	 */
	abstract public MembraneChannelTypes getChannelType();
	
	/**
	 * Set the "capture zone", which is a shape that represents the space
	 * from which particles may be captured.  If null is returned, this
	 * channel has no capture zone.
	 */
	public CaptureZone getUpperCaptureZone(){
		return uppperCaptureZone;
	}
	
	protected void setUpperCaptureZone(CaptureZone captureZone){
		this.uppperCaptureZone = captureZone;
	}
	
	public CaptureZone getLowerCaptureZone(){
		return lowerCaptureZone;
	}
	
	protected void setLowerCaptureZone(CaptureZone captureZone){
		this.lowerCaptureZone = captureZone;
	}
	
	public Dimension2D getChannelSize(){
		return new PDimension(channelSize);
	}
	
	public Point2D getCenterLocation(){
		return new Point2D.Double(centerLocation.getX(), centerLocation.getY());
	}
	
	public void setCenterLocation(Point2D newCenterLocation) {
	    setCenterLocation( newCenterLocation.getX(), newCenterLocation.getY() );
	}
	
	public void setCenterLocation(double x, double y){
	    if (centerLocation.getX() != x || centerLocation.getY() != y){
	        centerLocation.setLocation( x, y );
	        uppperCaptureZone.setOriginPoint( x, y );
	        uppperCaptureZone.setOriginPoint( x, y );
	        notifyPositionChanged();
	    }
	}
	
	public boolean isUserControlled(){
	    return userControlled;
	}
	
	public void setUserControlled(boolean userControlled){
	    if (this.userControlled != userControlled){
	        this.userControlled = userControlled;
	        
	        if (this.userControlled){
	            // If there are any particles that are in the process of
	            // traversing the channel, their traversal must be aborted.
	            for (Particle particle : particlesTraversingChannel){
	                MotionStrategy motionStrategy = particle.getMotionStrategy();
//	                assert motionStrategy instanceof TraverseChannelMotionStrategy; // See below for why this assert is here.
	                if (motionStrategy instanceof TraverseChannelMotionStrategy){
	                    ((TraverseChannelMotionStrategy)motionStrategy).abortTraversal( particle );
	                }
	                else{
	                    // This particle is traversing the channel, but
	                    // doesn't have a traversal motion strategy.  Weird.
	                    // Not much we can do.
	                    System.err.println(getClass().getName() + " Error - Particle doesn't appear to have correct motion strategy.");
	                }
	            }
	            particlesTraversingChannel.clear();
	        }
	        
	        // Notify listeners of the state change.
	        notifyUserControlledStateChanged();
	    }
	}

    /**
	 * Get the overall 2D size of the channel, which includes both the part
	 * that the particles travel through as well as the edges.
	 * 
	 * @return
	 */
	public Dimension2D getOverallSize(){
		return overallSize;
	}
	
	public double getOpenness() {
		return openness;
	}
	
	protected void setOpenness(double openness) {
		if (this.openness != openness){
			this.openness = openness;
			notifyOpennessChanged();
		}
	}
	
	public Color getChannelColor(){
		return Color.MAGENTA;
	}
	
	public Color getEdgeColor(){
		return Color.RED;
	}
	
	public void addListener(Listener listener){
		listeners.add(listener);
	}
	
	public void removeListener(Listener listener){
		listeners.remove(listener);
	}
	
    /**
     * This is called to remove this channel from the model.  It simply sends
     * out a notification of removal, and all listeners (including the view)
     * are expected to act appropriately and to remove all references.
     */
	public void removeFromModel(){
		notifyRemoved();
		// Clear our listener list to avoid memory leaks.
		listeners.clear();
	}
	
	private void notifyRemoved(){
        // Make a copy of the list before sending out notifications, since
        // these notifications may cause others to deregister as listeners,
        // which would lead to concurrent modification exceptions.
        ArrayList<Listener> listenerListCopy = new ArrayList<Listener>( listeners );
        for (Listener listener : listenerListCopy){
			listener.removed();
		}
	}
	
	private void notifyOpennessChanged(){
		for (Listener listener : listeners){
			listener.opennessChanged();
		}
	}
	
	private void notifyPositionChanged(){
		for (Listener listener : listeners){
			listener.positionChanged();
		}
	}
	
    private void notifyUserControlledStateChanged() {
        // Make a copy of the list before sending out notifications, since
        // these notifications may cause others to deregister as listeners,
        // which would lead to concurrent modification exceptions.
        ArrayList<Listener> listenerListCopy = new ArrayList<Listener>( listeners );
        for (Listener listener : listenerListCopy){
            listener.userControlledStateChanged();
        }
    }
	
	public static interface Listener{
		void removed();
		void opennessChanged();
		void positionChanged();
		void userControlledStateChanged();
	}
	
	public static class Adapter implements Listener {
		public void removed() {}
		public void opennessChanged() {}
		public void positionChanged() {}
        public void userControlledStateChanged() {}
	}
	
	protected double getMaxInterCaptureTime() {
		return maxInterCaptureTime;
	}

	protected void setMaxInterCaptureTime(double maxInterCaptureTime) {
		this.maxInterCaptureTime = maxInterCaptureTime;
	}

	protected double getMinInterCaptureTime() {
		return minInterCaptureTime;
	}

	protected void setMinInterCaptureTime(double minInterCaptureTime) {
		this.minInterCaptureTime = minInterCaptureTime;
	}
	
	protected double getCaptureCountdownTimer() {
		return captureCountdownTimer;
	}
}
