/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;

public class PotassiumLeakageChannel extends AbstractMembraneChannel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final double CHANNEL_HEIGHT = AxonMembrane.MEMBRANE_THICKNESS * 1.5; // In nanometers.
	private static final double CHANNEL_WIDTH = AxonMembrane.MEMBRANE_THICKNESS * 0.75; // In nanometers.
	
	private static final double CAPTURE_DISTANCE = 4.5; // In nanometers.
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
	public PotassiumLeakageChannel() {
		super(CHANNEL_WIDTH, CHANNEL_HEIGHT);
	}

	@Override
	public void checkReleaseControlAtoms(ArrayList<Atom> freeAtoms) {
		Atom atom = null;
		boolean release = false;
		for (int i = 0; i < getOwnedAtomsRef().size(); i++){
			atom = getOwnedAtomsRef().get(i);
			if (atom.getPositionReference().distance(getCenterLocation()) > CAPTURE_DISTANCE * 1.3){
				// Atom is far enough away that it can be released.
				release = true;
				break;
			}
		}
		if (release){
			getOwnedAtomsRef().remove(atom);
			freeAtoms.add(atom);
		}
	}

	@Override
	public void checkTakeControlAtoms(ArrayList<Atom> freeAtoms) {
		
		ArrayList<Atom> ownedAtoms = getOwnedAtomsRef();
		
		// Only move one atom at a time.
		if (ownedAtoms.size() == 0){
			boolean capture = false;
			Atom atom = null;
			for (int i = 0; i < freeAtoms.size(); i++){
				// See if this atom is in the right place to be captured.
				atom = freeAtoms.get(i);
				if ( atom.getType() == AtomType.POTASSIUM && 
					 atom.getPosition().distance(getCenterLocation()) < CAPTURE_DISTANCE ){
					// Capture this guy.
					capture = true;
					break;
				}
			}
			if (capture && atom != null){
				captureAtom(atom, freeAtoms, ownedAtoms);
			}
		}
	}

	@Override
	public Color getChannelColor() {
		return ColorUtils.darkerColor(Color.green, 0.2);
	}

	@Override
	public Color getEdgeColor() {
		return Color.green;
	}

	@Override
	public void stepInTime(double dt) {
		if (!getOwnedAtomsRef().isEmpty()){
			for (Atom atom : getOwnedAtomsRef()){
				atom.stepInTime(dt);
			}
		}
	}
	
	private void captureAtom(Atom atom, ArrayList<Atom> freeAtoms, ArrayList<Atom> ownedAtoms){
		
		// Transfer the atom to the list of atoms "owned" by this channel.
		freeAtoms.remove(atom);
		ownedAtoms.add(atom);
		
		double velocity = 100; // Arbitrary - working on this.
		
		// Move the atom so that it is exactly at the entrance of the channel.
		if ( atom.getPositionReference().distance(new Point2D.Double(0, 0)) >
		     getCenterLocation().distance(new Point2D.Double(0, 0)))
		{
			// Atom is outside of the membrane, so move it to the outer
			// entrance of the channel.
			double xPos = CAPTURE_DISTANCE * Math.cos(getRotationalAngle()) + getCenterLocation().getX();
			double yPos = CAPTURE_DISTANCE * Math.sin(getRotationalAngle()) + getCenterLocation().getY();
			atom.setPosition(xPos, yPos);
			
			// Set the velocity of the atom such that it is moving from the
			// outside to the inside of the membrane.
			atom.setVelocity(-velocity * Math.cos(getRotationalAngle()), -velocity * Math.sin(getRotationalAngle()));
		}
		else{
			// Atom is inside of the membrane, so move it to the inner
			// entrance of the channel.
			double xPos = -CAPTURE_DISTANCE * Math.cos(getRotationalAngle()) + getCenterLocation().getX();
			double yPos = -CAPTURE_DISTANCE * Math.sin(getRotationalAngle()) + getCenterLocation().getY();
			atom.setPosition(xPos, yPos);
			
			// Set the velocity of the atom such that it is moving from the
			// outside to the inside of the membrane.
			atom.setVelocity(velocity * Math.cos(getRotationalAngle()), velocity * Math.sin(getRotationalAngle()));
		}
	}

	@Override
	public MembraneChannelTypes getChannelType() {
		return MembraneChannelTypes.POTASSIUM_LEAKAGE_CHANNEL;
	}
}
