/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.umd.cs.piccolo.util.PDimension;


/**
 * Abstract base class for membrane channels, which represent any channel
 * through which atoms can go through to cross a membrane.
 * 
 * @author John Blanco
 */
public abstract class AbstractMembraneChannel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final double SIDE_HEIGHT_TO_CHANNEL_HEIGHT_RATIO = 1.6;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	/**
	 * List of the atoms "owned" (meaning that their motion is controlled by)
	 * this channel.
	 */
	private ArrayList<Atom> ownedAtoms = new ArrayList<Atom>();
	
	// Member variables that control the size and position of the channel.
	private Point2D centerLocation = new Point2D.Double();
	private double rotationalAngle; // In radians.
	private Dimension2D channelSize = new PDimension(); // Size of channel only, i.e. where the atoms pass through.
	private Dimension2D overallSize = new PDimension(); // Size including edges.
	
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
	
	public AbstractMembraneChannel(double channelWidth, double channelHeight){
		channelSize.setSize(channelWidth, channelHeight);
		overallSize.setSize(channelWidth * 2.5, channelHeight * SIDE_HEIGHT_TO_CHANNEL_HEIGHT_RATIO);
	}
	
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
	
	/**
	 * Check the supplied list of atoms to see if any are in a location where
	 * this membrane wants to take control of them.
	 * 
	 * @param freeAtoms - List of atoms that can be potentially taken.  Any
	 * atoms that are taken are removed from the list.
	 */
	abstract public void checkTakeControlAtoms(ArrayList<Atom> freeAtoms);

	/**
	 * Add any atoms that this channel no longer wants to control to the
	 * provided list.
	 * 
	 * @param freeAtoms - List of atoms that are "free", meaning that they
	 * are not controlled by this channel (nor probably any other).  Atoms
	 * that this membrane no longer wants to control will be added to this
	 * list.
	 */
	abstract public void checkReleaseControlAtoms(ArrayList<Atom> freeAtoms);
	
	/**
	 * Return a list of the atoms "owned" (meaning that their motion is
	 * controlled by) this channel.  Getting this list does NOT cause the
	 * atoms to be released by the channel.
	 * 
	 * @return a copy of the list of owned atoms.
	 */
	public ArrayList<Atom> getOwnedAtoms(){
		return new ArrayList<Atom>(ownedAtoms);
	}
	
	public Dimension2D getChannelSize(){
		return new PDimension(channelSize);
	}
	
	public Point2D getCenterLocation(){
		return new Point2D.Double(centerLocation.getX(), centerLocation.getY());
	}
	
	public void setCenterLocation(Point2D newCenterLocation) {
		centerLocation.setLocation(newCenterLocation);
	}

	public void setRotationalAngle(double rotationalAngle){
		this.rotationalAngle = rotationalAngle;
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
	
	public Color getChannelColor(){
		return Color.MAGENTA;
	}
	
	public Color getEdgeColor(){
		return Color.RED;
	}
}
