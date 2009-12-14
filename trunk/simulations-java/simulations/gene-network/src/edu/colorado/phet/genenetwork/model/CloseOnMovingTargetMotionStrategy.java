package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * This class represents a motion strategy for a simple model element that
 * causes it to move in a somewhat random fashion but also move towards an
 * attachment point on another simple model element that is also moving.
 * 
 * @author John Blanco
 */
public class CloseOnMovingTargetMotionStrategy extends
		LinearMotionStrategy {
	
	private Dimension2D offsetFromTarget;
	private final IModelElement targetElement;
	
	private ModelElementListenerAdapter targetListener = new ModelElementListenerAdapter(){
		public void positionChanged(){
			updateDestination();
		};
	};

	public CloseOnMovingTargetMotionStrategy(IModelElement modelElement, IModelElement targetElement, 
			Dimension2D offsetFromTarget, Rectangle2D bounds) {
		super(modelElement, bounds, new Point2D.Double(), 5);
		this.targetElement = targetElement;
		this.offsetFromTarget = offsetFromTarget;
		targetElement.addListener(targetListener);
		updateDestination();
	}
	
	private void updateDestination(){
		setDestination(targetElement.getPositionRef().getX() + offsetFromTarget.getHeight(),
				targetElement.getPositionRef().getY() + offsetFromTarget.getWidth());
		System.out.println(getDestination());
	}
}
