/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;

public class SodiumLeakageChannel extends AbstractMembraneChannel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final double CHANNEL_HEIGHT = AxonMembrane.MEMBRANE_THICKNESS * 1.5; // In nanometers.
	private static final double CHANNEL_WIDTH = AxonMembrane.MEMBRANE_THICKNESS * 0.75; // In nanometers.
	
	private static final double CAPTURE_DISTANCE = 4.5; // In nanometers.
	
	private static final int ATOM_MOTION_UPDATE_PERIOD = 4; // Controls frequency at which motion of atoms inside
	                                                        // this channel is updated.  Lower numbers mean more
	                                                        // frequent updates, 1 is min value.
	
	private static final double MAX_ATOM_VELOCITY = 100; // In nanometers per second, I think.
	
	private static final double PROBABILITY_OF_DOMINANT_MOTION = 0.8;
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	private MembraneChannelFlowDirection flowDirection = MembraneChannelFlowDirection.NONE;
	private int atomMotionUpdateCounter = 0;
	
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
	public SodiumLeakageChannel() {
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
				if ( atom.getType() == AtomType.SODIUM && 
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
		return ColorUtils.darkerColor(Color.red, 0.2);
	}

	@Override
	public Color getEdgeColor() {
		return Color.red;
	}

	@Override
	public void stepInTime(double dt) {
		if (!getOwnedAtomsRef().isEmpty()){
			atomMotionUpdateCounter = (atomMotionUpdateCounter + 1) % ATOM_MOTION_UPDATE_PERIOD;
			if (atomMotionUpdateCounter == 0){
				// Time to update the motion of the atoms in this channel.
				updateMotionOfAtoms();
			}
			
			for (Atom atom : getOwnedAtomsRef()){
				atom.stepInTime(dt);
			}
		}
	}
	
	private void captureAtom(Atom atom, ArrayList<Atom> freeAtoms, ArrayList<Atom> ownedAtoms){
		
		// Transfer the atom to the list of atoms "owned" by this channel.
		freeAtoms.remove(atom);
		ownedAtoms.add(atom);
		
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
			atom.setVelocity(-MAX_ATOM_VELOCITY * Math.cos(getRotationalAngle()),
					-MAX_ATOM_VELOCITY * Math.sin(getRotationalAngle()));
			
			// Set the direction of flow.
			flowDirection = MembraneChannelFlowDirection.IN;
		}
		else{
			// Atom is inside of the membrane, so move it to the inner
			// entrance of the channel.
			double xPos = -CAPTURE_DISTANCE * Math.cos(getRotationalAngle()) + getCenterLocation().getX();
			double yPos = -CAPTURE_DISTANCE * Math.sin(getRotationalAngle()) + getCenterLocation().getY();
			atom.setPosition(xPos, yPos);
			
			// Set the velocity of the atom such that it is moving from the
			// outside to the inside of the membrane.
			atom.setVelocity(MAX_ATOM_VELOCITY * Math.cos(getRotationalAngle()),
					MAX_ATOM_VELOCITY * Math.sin(getRotationalAngle()));

			// Set the direction of flow.
			flowDirection = MembraneChannelFlowDirection.OUT;
		}
	}
	
	@Override
	public MembraneChannelTypes getChannelType() {
		return MembraneChannelTypes.SODIUM_LEAKAGE_CHANNEL;
	}
	
	private void updateMotionOfAtoms(){
		
		if (getOwnedAtoms().size() == 0){
			// No atoms to update, so bail now.
			return;
		}
		
		for (Atom atom : getOwnedAtomsRef()){
			
			// Decide on the direction of motion, which will tend towards the
			// current flow direction, but has a probability to move the opposite
			// way.  This creates an appearance of somewhat random motion, which
			// is what we're after.
			double directionMultiplier;
			if (flowDirection == MembraneChannelFlowDirection.IN){
				if (RAND.nextDouble() < PROBABILITY_OF_DOMINANT_MOTION){
					directionMultiplier = -1;
				}
				else{
					directionMultiplier = 1;
				}
			}
			else if (flowDirection == MembraneChannelFlowDirection.OUT){
				if (RAND.nextDouble() < PROBABILITY_OF_DOMINANT_MOTION){
					directionMultiplier = 1;
				}
				else{
					directionMultiplier = -1;
				}
			}
			else{
				System.err.println(getClass().getName() + " - Error: Unexpected flow direction.");
				assert false;
				directionMultiplier = 1;
			}
			
			// Update the motion of the atom.
			double velocity = MAX_ATOM_VELOCITY * RAND.nextDouble() * directionMultiplier;
			atom.setVelocity(velocity * Math.cos(getRotationalAngle()), velocity * Math.sin(getRotationalAngle()));
		}
	}
}
