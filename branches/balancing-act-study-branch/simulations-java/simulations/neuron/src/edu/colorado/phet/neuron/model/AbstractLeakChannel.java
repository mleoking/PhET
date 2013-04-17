// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.neuron.model;


public abstract class AbstractLeakChannel extends MembraneChannel {
	
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

	//----------------------------------------------------------------------------
	// Instance Data
	//----------------------------------------------------------------------------
	
	//----------------------------------------------------------------------------
	// Constructor
	//----------------------------------------------------------------------------

	public AbstractLeakChannel(double channelWidth, double channelHeight, IParticleCapture modelContainingParticles) {
		super(channelWidth, channelHeight, modelContainingParticles);
		reset();
	}
	
	//----------------------------------------------------------------------------
	// Methods
	//----------------------------------------------------------------------------
	
	@Override
	public void reset() {
		setOpenness(1);  // Leak channels are always fully open.
	}
}
