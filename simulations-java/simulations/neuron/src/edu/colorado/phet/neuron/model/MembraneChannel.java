/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
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
	
	private static final double DEFAULT_PARTICLE_VELOCITY = 40000; // In nanometers per sec of sim time.

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	// Reference to the model that contains that particles that will be moving
	// through this channel.
	private IParticleCapture modelContainingParticles;
	
	// Member variables that control the size and position of the channel.
	private Point2D centerLocation = new Point2D.Double();
	private double rotationalAngle = 0; // In radians.
	private Dimension2D channelSize = new PDimension(); // Size of channel only, i.e. where the atoms pass through.
	private Dimension2D overallSize = new PDimension(); // Size including edges.
	
	// Variable that defines how open the channel is.
	private double openness = 0;  // Valid range is 0 to 1, 0 means fully closed, 1 is fully open.
	
	// Variable that defines how inactivated the channel is, which is distinct
	// from openness.
	private double inactivationAmt = 0;  // Valid range is 0 to 1, 0 means completely active, 1 is completely inactive.
	
	// Array of listeners.
	private ArrayList<Listener> listeners = new ArrayList<Listener>();
	
	// Capture zones, which is where particles can be captured by this
	// channel.  There are two, one for inside the cell and one for outside.
	// There is generally no enforcement of which is which, so it is the
	// developer's responsibility to position the channel appropriately on the
	// cell membrane.
	private CaptureZone interiorCaptureZone = new NullCaptureZone();
	private CaptureZone exteriorCaptureZone = new NullCaptureZone();
	
	// Time values that control how often this channel requests an ion to move
	// through it.  These are initialized here to values that will cause the
	// channel to never request any ions and must be set by the base classes
	// in order to make capture events occur.
	private double captureCountdownTimer = Double.POSITIVE_INFINITY;
	private double minInterCaptureTime = Double.POSITIVE_INFINITY;
	private double maxInterCaptureTime = Double.POSITIVE_INFINITY;
	
	// Velocity for particles that move through this channel.
	private double particleVelocity = DEFAULT_PARTICLE_VELOCITY;
	
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
    		membraneChannel = new SodiumDualGatedChannel(particleModel, hodgkinHuxleyModel);
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
		return (getOpenness() > 0.3 && getInactivationAmt() < 0.7);
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
		AffineTransform transform = AffineTransform.getRotateInstance(rotationalAngle, centerLocation.getX(), centerLocation.getY());
		Shape rotatedChannelShape = transform.createTransformedShape(channelShape);
		return rotatedChannelShape.contains(pt);
	}
	
	/**
	 * Gets a values that indicates whether this channel has an inactivation
	 * gate.  Most of the channels in this sim do not have these, so the
	 * default is to return false.  This should be overridden in subclasses
	 * that add inactivation gates to the channels.
	 * 
	 * @return
	 */
	public boolean getHasInactivationGate(){
		return false;
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
		AffineTransform transform = AffineTransform.getRotateInstance(rotationalAngle, centerLocation.getX(), centerLocation.getY());
		Shape rotatedChannelShape = transform.createTransformedShape(channelShape);
		return rotatedChannelShape;
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
					modelContainingParticles.requestParticleThroughChannel(getParticleTypeToCapture(), this, particleVelocity, chooseCrossingDirection());
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
	 * behavior for crossing through the neuron membrane.
	 * 
	 * @param particle
	 * @param maxVelocity
	 */
	public void moveParticleThroughNeuronMembrane(Particle particle, double maxVelocity){
		particle.setMotionStrategy(new TraverseChannelAndFadeMotionStrategy(this, particle.getPositionReference(), maxVelocity));
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
	public CaptureZone getInteriorCaptureZone(){
		return interiorCaptureZone;
	}
	
	protected void setInteriorCaptureZone(CaptureZone captureZone){
		this.interiorCaptureZone = captureZone;
	}
	
	public CaptureZone getExteriorCaptureZone(){
		return exteriorCaptureZone;
	}
	
	protected void setExteriorCaptureZone(CaptureZone captureZone){
		this.exteriorCaptureZone = captureZone;
	}
	
	public Dimension2D getChannelSize(){
		return new PDimension(channelSize);
	}
	
	public Point2D getCenterLocation(){
		return new Point2D.Double(centerLocation.getX(), centerLocation.getY());
	}
	
	public void setCenterLocation(Point2D newCenterLocation) {
		if (!newCenterLocation.equals(centerLocation)){
			centerLocation.setLocation(newCenterLocation);
			interiorCaptureZone.setOriginPoint(newCenterLocation);
			exteriorCaptureZone.setOriginPoint(newCenterLocation);
			notifyPositionChanged();
		}
	}

	public void setRotationalAngle(double rotationalAngle){
		this.rotationalAngle = rotationalAngle;
		interiorCaptureZone.setRotationalAngle(rotationalAngle);
		exteriorCaptureZone.setRotationalAngle(rotationalAngle);
	}
	
	public double getRotationalAngle(){
		return rotationalAngle;
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
	
	public double getInactivationAmt(){
		return inactivationAmt;
	}

	protected void setInactivationAmt(double inactivationAmt) {
		if (this.inactivationAmt != inactivationAmt){
			this.inactivationAmt = inactivationAmt;
			notifyInactivationAmtChanged();
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
	 * Choose the direction of crossing for the next particle to cross the
	 * membrane.  If particles only cross in one direction, this will always
	 * return the same thing.  If they can vary, this can return a different
	 * value.
	 */
	protected abstract MembraneCrossingDirection chooseCrossingDirection();
	
    /**
     * This is called to remove this channel from the model.  It simply sends
     * out a notification of removal, and all listeners (including the view)
     * are expected to act appropriately and to remove all references.
     */
	public void removeFromModel(){
		notifyRemoved();
	}
	
	private void notifyRemoved(){
		for (Listener listener : listeners){
			listener.removed();
		}
	}
	
	private void notifyOpennessChanged(){
		for (Listener listener : listeners){
			listener.opennessChanged();
		}
	}
	
	private void notifyInactivationAmtChanged(){
		for (Listener listener : listeners){
			listener.inactivationAmtChanged();
		}
	}
	
	private void notifyPositionChanged(){
		for (Listener listener : listeners){
			listener.positionChanged();
		}
	}
	
	public static interface Listener{
		void removed();
		void opennessChanged();
		void inactivationAmtChanged();
		void positionChanged();
	}
	
	public static class Adapter implements Listener {
		public void removed() {}
		public void opennessChanged() {}
		public void inactivationAmtChanged() {}
		public void positionChanged() {}
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
