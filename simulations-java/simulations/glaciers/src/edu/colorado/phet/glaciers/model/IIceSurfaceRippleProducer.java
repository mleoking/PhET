/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.model;



/**
 * IIceSurfaceRippleProducer is the interface implemented by objects that create IceSurfaceRipple model elements.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface IIceSurfaceRippleProducer {

    /**
     * Adds ripple as a specified x coordinate.
     * The 2D position will always be on the surface of the ice, so we only specify x.
     * @param x
     * @return
     */
    public IceSurfaceRipple addIceSurfaceRipple( double x );
    
    /**
     * Removes the specified ripple.
     * @param ripple
     */
    public void removeIceSurfaceRipple( IceSurfaceRipple ripple );
    
    /**
     * Removes all ripples.
     */
    public void removeAllIceSurfaceRipples();
    
    /**
     * IIceSurfaceRippleProducerListener is the interface implemented by all parties
     * that are interested in ripple creation and deletion.
     */
    public interface IIceSurfaceRippleProducerListener {
        public void rippleAdded( IceSurfaceRipple ripple );
        public void rippleRemoved( IceSurfaceRipple ripple );
    }
    
    /**
     * Adds an IIceSurfaceRippleProducerListener.
     * @param listener
     */
    public void addIceSurfaceRippleProducerListener( IIceSurfaceRippleProducerListener listener );
    
    /**
     * Removes an IIceSurfaceRippleProducerListener.
     * @param listener
     */
    public void removeIceSurfaceRippleProducerListener( IIceSurfaceRippleProducerListener listener );
}
