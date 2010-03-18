package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;

/**
 * Utility class used to trim down motion bounds so that the edges of an
 * object will stay within them.
 * 
 * @author John Blanco
 */
public class MotionBoundsTrimmer {

	static Rectangle2D trimMotionBounds(Rectangle2D originalBounds, SimpleModelElement sme){
		return trimMotionBounds(originalBounds, sme.getShape().getBounds2D());
	}

	static Rectangle2D trimMotionBounds(Rectangle2D originalBounds, Rectangle2D boundsOfShape){
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
