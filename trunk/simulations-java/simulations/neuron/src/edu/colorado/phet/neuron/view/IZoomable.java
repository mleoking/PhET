package edu.colorado.phet.neuron.view;

/**
 * This interface can be implemented by anything that needs to be zoomed in
 * and out.
 * 
 * @author John Blanco
 */
public interface IZoomable {

	/**
	 * Set the zoom amount.
	 * 
	 * @param zoomFactor - 1 is the default, unzoomed state.  Larger values
	 * zoom in, smaller values zoom out.
	 */
	public abstract void setZoomFactor(double zoomFactor);

	/**
	 * Get the zoom amount.
	 * 
	 * @return zoomFactor - 1 is the default, unzoomed state.  Larger values
	 * zoom in, smaller values zoom out.
	 */
	public abstract double getZoomFactor();

}