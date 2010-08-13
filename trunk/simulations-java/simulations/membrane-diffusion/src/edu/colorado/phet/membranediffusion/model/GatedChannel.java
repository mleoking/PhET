package edu.colorado.phet.membranediffusion.model;


/**
 * Base class for gated membrane channels, i.e. channels that can open and
 * close.
 * 
 * @author John Blanco
 */
public abstract class GatedChannel extends MembraneChannel {
	
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	//----------------------------------------------------------------------------
	// Instance Data
	//----------------------------------------------------------------------------
	
	//----------------------------------------------------------------------------
	// Constructor
	//----------------------------------------------------------------------------

	public GatedChannel(double channelWidth, double channelHeight, IParticleCapture modelContainingParticles) {
		super(channelWidth, channelHeight, modelContainingParticles);
		setOpenness(0);  // Gated channels are assumed to be initially closed.
	}
	
	//----------------------------------------------------------------------------
	// Methods
	//----------------------------------------------------------------------------
	
	@Override
	public void reset() {
		setOpenness(0);         // Gated channels are assumed to be initially closed...
	}
}
