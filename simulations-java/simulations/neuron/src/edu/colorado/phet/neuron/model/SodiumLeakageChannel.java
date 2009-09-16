/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.util.ArrayList;

public class SodiumLeakageChannel extends AbstractMembraneChannel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final double CHANNEL_HEIGHT = AxonMembrane.MEMBRANE_THICKNESS * 1.5; // In nanometers.
	private static final double CHANNEL_WIDTH = AxonMembrane.MEMBRANE_THICKNESS * 0.75; // In nanometers.
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
	public SodiumLeakageChannel() {
		super(CHANNEL_WIDTH, CHANNEL_HEIGHT);
	}

	@Override
	public void checkReleaseControlAtoms(ArrayList<Atom> freeAtoms) {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkTakeControlAtoms(ArrayList<Atom> freeAtoms) {
		// TODO Auto-generated method stub

	}
}
