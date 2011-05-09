// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Rectangle2D;

/**
 * Utility class used to trim down motion bounds so that the edges of an
 * object will stay within them.
 * 
 * @author John Blanco
 */
public class MotionBoundsTrimmer {

	/**
	 * Trim the motion bounds such that the provided simple model element will
	 * stay completely inside them.
	 * 
	 * @param originalBounds
	 * @param sme
	 * @return
	 */
	static Rectangle2D trim(Rectangle2D originalBounds, SimpleModelElement sme){
		return trim(originalBounds, sme.getShape().getBounds2D());
	}

	/**
	 * Trim the motion bounds such that the provided shape would stay
	 * completely inside the bounds.
	 * 
	 * @param originalBounds
	 * @param boundsOfShape
	 * @return
	 */
	static Rectangle2D trim(Rectangle2D originalBounds, Rectangle2D boundsOfShape){
		// Make sure that this shape is not something unexpected.
		assert boundsOfShape.getMinX() <= 0 && boundsOfShape.getMinY() <= 0 && 
			   boundsOfShape.getMaxX() >= 0 && boundsOfShape.getMaxY() >= 0;
			
		double xAdder = -boundsOfShape.getMinX();
		double yAdder = -boundsOfShape.getMinY();
		double widthReduction = boundsOfShape.getWidth();
		double heightReduction = boundsOfShape.getHeight();
		
		// Return a rectangle representing the reduced bounds.
		return new Rectangle2D.Double(originalBounds.getX() + xAdder, originalBounds.getY() + yAdder,
				originalBounds.getWidth() - widthReduction, originalBounds.getHeight() - heightReduction);
	}
}
