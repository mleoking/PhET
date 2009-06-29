/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 * This interface allows callers to obtain information about an animated
 * element, such as its position, its rotational angle, the image that should
 * be used to portray the element, etc.
 * 
 * @author John
 *
 */
public interface AnimatedModelElement {

	/**
	 * Get the position in 2D space of the center of the element.
	 */
	public Point2D getPosition();
	
	/**
	 * Get the rotational angle in radians.
	 * @return
	 */
	public double getRotationalAngle();
	
	/**
	 * Get the 2D size of the model element.
	 */
	public Dimension2D getSize();
	
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
	 * Register for notifications of animation changes.
	 * 
	 * @param listener
	 */
	public void addAnimationListener( AnimationListener listener );
	
	/**
	 * Remove the designated listener from the list of listeners.
	 * @param listener
	 * @return
	 */
	public boolean removeAnimationListener( AnimationListener listener );
	
	/**
	 * Remove all animation listeners for this animated element.
	 */
	public void removeAllAnimationListeners();
}
