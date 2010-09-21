/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 * This interface allows callers to obtain information about an animated
 * model element, such as its position, its rotational angle, the image that
 * should be used to portray the element, etc.
 * 
 * @author John Blanco
 *
 */
public interface AnimatedModelElement {
	
	public static final int NO_IMAGE = Integer.MAX_VALUE;

	/**
	 * Get the position in 2D space of the center of the element.
	 */
	public Point2D getPosition();
	
	/**
	 * Set the position in 2D space of the center of the element.
	 */
	public void setPosition(Point2D center);
	
	/**
	 * Get the rotational angle in radians.
	 */
	public double getRotationalAngle();
	
	/**
	 * Set the rotational angle in radians.
	 */
	public void setRotationalAngle(double rotationalAngle);
	
	/**
	 * Get the 2D size of the model element.
	 */
	public Dimension2D getSize();
	
	/**
	 * Set the 2D size of the model element.
	 */
	public void setSize(Dimension2D size);
	
	/**
	 * Get the unscaled image that should be used to represent this model
	 * element.  It is a little unusual to have the model be responsible for
	 * the image and not the view, but since the image can change based on the
	 * state of the model, it makes sense in this case for the model to keep
	 * track of the corresponding image.
	 * @return
	 */
	public BufferedImage getImage();
	
	/**
	 * Get the number of images that are available for use.
	 * 
	 * @return
	 */
	public int getNumberImages();
	
	/**
	 * Set the index for the primary image.
	 */
	public void setPrimaryImageIndex( int imageIndex );
	
	/**
	 * Get the index for the primary image.
	 */
	public int getPrimaryImageIndex();
	
	/**
	 * Set the index for the secondary image.
	 */
	public void setSecondaryImageIndex( int imageIndex );
	
	/**
	 * Get the index for the secondary image.
	 */
	public int getSecondaryImageIndex();
	
	/**
	 * Set the amount of fade between the primary and secondary images.
	 * 
	 * @param fadeFactor - 0 for 100% primary, 1 for 100% secondary, values
	 * in between for corresponding amount of fade.
	 */
	public void setFadeFactor(double fadeFactor);
	
	/**
	 * The the fade factor, which defines the relative amounts of the primary
	 * and secondary images that are shown.
	 * 
	 * @return fadeFactor - 0 for 100% primary, 1 for 100% secondary, values
	 * in between for corresponding amount of fade.
	 */
	public double getFadeFactor();
	
	/**
	 * Register for notifications of animation changes.
	 * 
	 * @param listener
	 */
	public void addAnimationListener( ModelAnimationListener listener );
	
	/**
	 * Remove the designated listener from the list of listeners.
	 * @param listener
	 * @return
	 */
	public boolean removeAnimationListener( ModelAnimationListener listener );
	
	/**
	 * Remove all animation listeners for this animated element.
	 */
	public void removeAllAnimationListeners();
}
