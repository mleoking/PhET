/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
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
	
	private static final double SIDE_HEIGHT_TO_CHANNEL_HEIGHT_RATIO = 1.6;
	protected static final Random RAND = new Random();

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	/**
	 * List of the atoms "owned" (meaning that their motion is controlled by)
	 * this channel.
	 */
	private ArrayList<Particle> ownedAtoms = new ArrayList<Particle>();
	
	// Member variables that control the size and position of the channel.
	private Point2D centerLocation = new Point2D.Double();
	private double rotationalAngle = 0; // In radians.
	private Dimension2D channelSize = new PDimension(); // Size of channel only, i.e. where the atoms pass through.
	private Dimension2D overallSize = new PDimension(); // Size including edges.
	
	// Variable that defines how open the channel is.
	private double openness = 0;  // Valid range is 0 to 1, 0 means fully closed, 1 is fully open.
	
	// Array of listeners.
	private ArrayList<Listener> listeners = new ArrayList<Listener>();
	
	// Capture zone, which is where particles can be captured by this channel.
	private CaptureZone captureZone = new NullCaptureZone();
	
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
	
	public MembraneChannel(double channelWidth, double channelHeight){
		channelSize.setSize(channelWidth, channelHeight);
		overallSize.setSize(channelWidth * 2.4, channelHeight * SIDE_HEIGHT_TO_CHANNEL_HEIGHT_RATIO);
	}
	
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
	
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

	/**
	 * Add any atoms that this channel no longer wants to control to the
	 * provided list.
	 * 
	 * @param freeAtoms - List of atoms that are "free", meaning that they
	 * are not controlled by this channel (nor probably any other).  Atoms
	 * that this membrane no longer wants to control will be added to this
	 * list.
	 * @return List of atoms that this channel is releasing.
	 */
	abstract public ArrayList<Particle> checkReleaseControlParticles(final ArrayList<Particle> freeAtoms);
	
	/**
	 * Implements the time-dependent behavior of the gate.
	 * 
	 * @param dt - Amount of time step, in milliseconds.
	 */
	abstract public void stepInTime(double dt);
	
	/**
	 * Get the identifier for this channel type.
	 */
	abstract public MembraneChannelTypes getChannelType();
	
	/**
	 * Get the "capture zone", which is a shape that represents the space
	 * from which particles may be captured.  If null is returned, this
	 * channel has no capture zone.
	 */
	public CaptureZone getCaptureZone(){
		return captureZone;
	}
	
	protected void setCaptureZone(CaptureZone captureZone){
		this.captureZone = captureZone;
	}
	
	/**
	 * Return a list of the atoms "owned" (meaning that their motion is
	 * controlled by) this channel.  Getting this list does NOT cause the
	 * atoms to be released by the channel.
	 * 
	 * @return a copy of the list of owned atoms.
	 */
	public ArrayList<Particle> getOwnedParticles(){
		return new ArrayList<Particle>(ownedAtoms);
	}
	
	public ArrayList<Particle> forceReleaseAllParticles(final ArrayList<Particle> freeAtoms){
		ArrayList<Particle> releasedAtoms = null;
		if (ownedAtoms.size() > 0){
			releasedAtoms = new ArrayList<Particle>(ownedAtoms);
		}
		ownedAtoms.clear();
		return releasedAtoms;
	}
	
	public Dimension2D getChannelSize(){
		return new PDimension(channelSize);
	}
	
	public Point2D getCenterLocation(){
		return new Point2D.Double(centerLocation.getX(), centerLocation.getY());
	}
	
	public void setCenterLocation(Point2D newCenterLocation) {
		centerLocation.setLocation(newCenterLocation);
		captureZone.setCenterPoint(newCenterLocation);
	}

	public void setRotationalAngle(double rotationalAngle){
		this.rotationalAngle = rotationalAngle;
		captureZone.setRotationalAngle(rotationalAngle);
	}
	
	public double getRotationalAngle(){
		return rotationalAngle;
	}
	
	/**
	 * Get the 2D size of the edges of the channel.  Even though there are
	 * two sides 
	 * Even though there are two sides, the channel is assumed to be
	 * symmetric, so there is only one side rectangle supplied, and callers
	 * are expected to use it twice.
	 * 
	 * @return
	 */
	public Dimension2D getOverallSize(){
		return overallSize;
	}
	
	public void setDimensions( Dimension2D overallSize, Dimension2D channelSize ){
		this.overallSize.setSize(overallSize);
		this.channelSize.setSize(channelSize);
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
	
	/**
	 * Get a reference to the list of owned atoms.
	 * @return
	 */
	protected ArrayList<Particle> getOwnedAtomsRef(){
		return ownedAtoms;
	}
	
	public void addListener(Listener listener){
		listeners.add(listener);
	}
	
	public void removeListener(Listener listener){
		listeners.remove(listener);
	}
	
	public void remove(){
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
	
	public static interface Listener{
		void removed();
		void opennessChanged();
	}
	
	public static class Adapter implements Listener {
		public void opennessChanged() {}
		public void removed() {}
	}
}
