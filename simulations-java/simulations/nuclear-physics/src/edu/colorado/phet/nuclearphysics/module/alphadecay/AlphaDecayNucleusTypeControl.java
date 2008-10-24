package edu.colorado.phet.nuclearphysics.module.alphadecay;

/**
 * This interface is used, generally by a control panel, to set the type of
 * atomic nucleus being used by the model portion of a simulation.  It also
 * includes a query that is used by the control panel to make decisions about
 * its appearance based on the chart types being displayed.
 * 
 * @author John Blanco
 *
 */
public interface AlphaDecayNucleusTypeControl {

	public static final int NUCLEUS_TYPE_POLONIUM = 1;
	public static final int NUCLEUS_TYPE_CUSTOM = 2;
	
	/**
	 * Set the type of nucleus.
	 * 
	 * @param nucleusId - must be one of the values defined in this interface
	 * definition file.
	 */
	public void setNucleusType( int nucleusId );

	/**
	 * Get a boolean value indicating whether the alpha particle energy graph
	 * is being shown.
	 * 
	 * @return - true if the graph is being shown, false if not.
	 */
	public boolean getEnergyChartShowing();
}
