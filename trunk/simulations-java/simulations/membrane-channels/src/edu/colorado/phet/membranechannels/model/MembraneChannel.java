/* Copyright 2009, University of Colorado */

package edu.colorado.phet.membranechannels.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.membranechannels.MembraneChannelsConstants;
import edu.umd.cs.piccolo.util.PDimension;


/**
 * Class for membrane channels, which represent any channel through which
 * atoms can go through to cross a membrane.
 * 
 * @author John Blanco
 */
public class MembraneChannel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final double SIDE_HEIGHT_TO_CHANNEL_HEIGHT_RATIO = 1.3;
	protected static final Random RAND = new Random();
	
	private static final double DEFAULT_PARTICLE_VELOCITY = 100; // In nanometers per sec of sim time.
    protected static final double CHANNEL_HEIGHT = MembraneChannelsModel.getMembraneThickness() * 1.2;
    protected static final double CHANNEL_WIDTH = MembraneChannelsModel.getMembraneThickness() * 0.50;

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
    protected final Color channelColor;
    protected final Color edgeColor;
    protected final ParticleType particleTypeToCapture;
    protected final MembraneChannelOpennessStrategy opennessStrategy;
	
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

    public MembraneChannel(IParticleCapture modelContainingParticles, ParticleType particleTypeToCapture,
            Color channelColor, Color edgeColor, MembraneChannelOpennessStrategy opennessStrategy,
            Point2D initialPositon) {

        this.modelContainingParticles = modelContainingParticles;
        this.particleTypeToCapture = particleTypeToCapture;
        this.channelColor = channelColor;
        this.edgeColor = edgeColor;
        this.opennessStrategy = opennessStrategy;

        // Set the channel size.
        channelSize.setSize(CHANNEL_WIDTH, CHANNEL_HEIGHT);
        overallSize.setSize(CHANNEL_WIDTH * 2.1, CHANNEL_HEIGHT * SIDE_HEIGHT_TO_CHANNEL_HEIGHT_RATIO);

        // Set up the capture zones that define where particles may be
        // captured in order to move them across the membrane.
        setUpperCaptureZone(new PieSliceShapedCaptureZone(getCenterLocation(), CHANNEL_WIDTH * 4, -Math.PI / 2, Math.PI * 0.3));
        setLowerCaptureZone(new PieSliceShapedCaptureZone(getCenterLocation(), CHANNEL_WIDTH * 4, Math.PI / 2, Math.PI * 0.3));
        
        // Initialize the openness level.
        setOpenness( opennessStrategy.getOpenness() );
        
        // Initialize the position.
        setCenterLocation( initialPositon );
        
        // Listen for updates to the openness.
        opennessStrategy.addListener( new MembraneChannelOpennessStrategy.Listener() {
            public void opennessChanged() {
                setOpenness( MembraneChannel.this.opennessStrategy.getOpenness() );
            }
        });
    }
    
    public MembraneChannel(IParticleCapture modelContainingParticles, ParticleType particleTypeToCapture,
            Color channelColor, Color edgeColor, MembraneChannelOpennessStrategy opennessStrategy) {

        this(modelContainingParticles, particleTypeToCapture, channelColor, edgeColor, opennessStrategy, 
                new Point2D.Double(0, 0));
    }
	
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
	
    /**
     * Static factory method for creating a channel when given a channel type ID.
     */
    public static MembraneChannel createChannel( MembraneChannelTypes channelType, IParticleCapture modelContainingParticles, MembraneChannelOpennessStrategy opennessStrategy ) {
    
        Color channelColor, edgeColor;
        ParticleType particleType;
    
        switch ( channelType ){
        case POTASSIUM_GATED_CHANNEL:
            channelColor = MembraneChannelsConstants.POTASSIUM_GATED_CHANNEL_COLOR;
            edgeColor = MembraneChannelsConstants.POTASSIUM_GATED_EDGE_COLOR;
            particleType = ParticleType.POTASSIUM_ION;
            break;
    
        case POTASSIUM_LEAKAGE_CHANNEL:
            channelColor = MembraneChannelsConstants.POTASSIUM_LEAKAGE_CHANNEL_COLOR;
            edgeColor = MembraneChannelsConstants.POTASSIUM_LEAKAGE_EDGE_COLOR;
            particleType = ParticleType.POTASSIUM_ION;
            break;
    
        case SODIUM_GATED_CHANNEL:
            channelColor = MembraneChannelsConstants.SODIUM_GATED_CHANNEL_COLOR;
            edgeColor = MembraneChannelsConstants.SODIUM_GATED_EDGE_COLOR;
            particleType = ParticleType.SODIUM_ION;
            break;
    
        case SODIUM_LEAKAGE_CHANNEL:
            channelColor = MembraneChannelsConstants.SODIUM_LEAKAGE_CHANNEL_COLOR;
            edgeColor = MembraneChannelsConstants.SODIUM_LEAKAGE_EDGE_COLOR;
            particleType = ParticleType.SODIUM_ION;
            break;
    
        default:
            System.err.println( MembraneChannel.class.getCanonicalName() + " - Error: Unknown channel type." );
            assert false;
            channelColor = Color.white;;
            edgeColor = Color.pink;
            particleType = ParticleType.POTASSIUM_ION;
            break;
        }
        
        return new MembraneChannel( modelContainingParticles, particleType, channelColor, edgeColor, opennessStrategy );
    }

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
	 * Implements the time-dependent behavior of the gate.
	 * 
	 * @param dt - Amount of time step, in milliseconds.
	 */
	public void stepInTime(double dt){
		if (isOpen() && particlesTraversingChannel.size() == 0){
		    // Ask the model to scan our capture zones for any particles.
		    // Decide randomly whether to scan the upper or lower zone first.
	        CaptureZone firstZone, secondZone;
	        if (RAND.nextBoolean()){
	            firstZone = getUpperCaptureZone();
	            secondZone = getLowerCaptureZone();
	        }
	        else{
	            firstZone = getLowerCaptureZone();
	            secondZone = getUpperCaptureZone();
	        }
	        
	        CaptureZoneScanResult czsr = modelContainingParticles.scanCaptureZoneForFreeParticles( firstZone, getParticleTypeToCapture() );
	        Particle particleToCapture = czsr.getClosestFreeParticle();
	        if (particleToCapture == null){
	            // Try the other zone.
	            czsr = modelContainingParticles.scanCaptureZoneForFreeParticles( secondZone, getParticleTypeToCapture() );
	            particleToCapture = czsr.getClosestFreeParticle();
	        }
	        
	        if (particleToCapture != null){
	            // Set a motion strategy for the particle that will cause it
	            // to traverse this channel.
	            Rectangle2D preTraversalMotionBounds;
	            Rectangle2D postTraversalMotionBounds;
	            if (modelContainingParticles.getUpperParticleChamberRect().contains( particleToCapture.getPositionReference())){
	                // In the upper sub-chamber now, so will be in the lower one
	                // after traversing.
	                preTraversalMotionBounds = modelContainingParticles.getUpperParticleChamberRect();
	                postTraversalMotionBounds = modelContainingParticles.getLowerParticleChamberRect();
	            }
	            else{
	                // In the lower sub-chamber now, so will be in the upper one
	                // after traversing.
                    preTraversalMotionBounds = modelContainingParticles.getLowerParticleChamberRect();
                    postTraversalMotionBounds = modelContainingParticles.getUpperParticleChamberRect();
	            }
	            particleToCapture.setMotionStrategy(new TraverseChannelMotionStrategy(this, 
	                    particleToCapture.getPosition(), preTraversalMotionBounds, postTraversalMotionBounds,
	                    particleToCapture.getVelocity().getMagnitude()));
	            
	            // Add a listener so that we get a notification when the particle has
	            // completed the traversal.
	            particleToCapture.getMotionStrategy().addListener( channelTraversalListener );

	            // Keep a reference to this particle.
	            particlesTraversingChannel.add( particleToCapture );
	            
	            final Particle particleToCaptureFinal = particleToCapture;
	            
	            particleToCapture.addListener( new Particle.Adapter(){
	                public void removedFromModel() {
	                    // The particle has been removed, so remove it from our list.
	                    particlesTraversingChannel.remove( particleToCaptureFinal );
	                }
	            });
	        }
		}
	}
	
	protected double getParticleVelocity() {
		return particleVelocity;
	}

	protected void setParticleVelocity(double particleVelocity) {
		this.particleVelocity = particleVelocity;
	}
	
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
	        lowerCaptureZone.setOriginPoint( x, y );
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
	                assert motionStrategy instanceof TraverseChannelMotionStrategy; // See below for why this assert is here.
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
	
	/**
         * Returns a boolean value indicating whether the channels is gated,
         * meaning that it can open and close.  In general, a non-gated channel
         * is a leakage channel.
         */
    public boolean isGated() {
        return opennessStrategy.isDynamic();
    }

    public Color getChannelColor() {
    	return channelColor;
    }

    public Color getEdgeColor() {
    	return edgeColor;
    }

    protected ParticleType getParticleTypeToCapture() {
    	return particleTypeToCapture;
    }

    public MembraneChannelOpennessStrategy getOpennessStrategy() {
        return opennessStrategy;
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
}
