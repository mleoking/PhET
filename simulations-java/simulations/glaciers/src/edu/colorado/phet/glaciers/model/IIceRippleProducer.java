/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.Dimension;



/**
 * IIceRippleProducer is the interface implemented by objects that create IceRipple model elements.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface IIceRippleProducer {

    /**
     * Adds ripple at a specified x coordinate.
     * The 2D position will always be on the surface of the ice, so we only specify x.
     * @param x initial x position of the ripple (meters)
     * @param size (x and z dimensions, in meters)
     * @param zOffset offset of the ripple from the far wall of the valley (z dimension, i meters)
     * @return
     */
    public IceRipple addIceRipple( double x, Dimension size, double zOffset );
    
    /**
     * Removes the specified ripple.
     * @param ripple
     */
    public void removeIceRipple( IceRipple ripple );
    
    /**
     * Removes all ripples.
     */
    public void removeAllIceRipples();
    
    /**
     * Interface implemented by all parties that are interested in ripple creation and deletion.
     */
    public interface IIceRippleProducerListener {
        public void rippleAdded( IceRipple ripple );
        public void rippleRemoved( IceRipple ripple );
    }
    
    /**
     * Adds an IIceRippleProducerListener.
     * @param listener
     */
    public void addIceRippleProducerListener( IIceRippleProducerListener listener );
    
    /**
     * Removes an IIceRippleProducerListener.
     * @param listener
     */
    public void removeIceRippleProducerListener( IIceRippleProducerListener listener );
}
