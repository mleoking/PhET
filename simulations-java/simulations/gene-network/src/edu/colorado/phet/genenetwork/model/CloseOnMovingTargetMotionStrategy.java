package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents a motion strategy for a simple model element that
 * causes it to move in a somewhat random fashion but also move towards an
 * attachment point on another simple model element that is also moving.
 * 
 * @author John Blanco
 */
public class CloseOnMovingTargetMotionStrategy extends
		DirectedRandomWalkMotionStrategy {
	
	private PDimension offsetFromTarget;
	private final IModelElement targetElement;
	private ModelElementListenerAdapter targetListener = new ModelElementListenerAdapter(){
		public void positionChanged(){
			updateDestination();
		};
		public void removedFromModel() {
			targetElement.removeListener(targetListener);
		};
	};

	public CloseOnMovingTargetMotionStrategy(IModelElement modelElement, IModelElement targetElement, 
			Dimension2D attachmentPointOffset, Rectangle2D bounds) {
		super(modelElement, bounds);
		this.targetElement = targetElement;
		targetElement.addListener(targetListener);
		updateDestination();
	}
	
	private void updateDestination(){
		setDestination(getModelElement().getPositionRef().getX() + offsetFromTarget.getHeight(),
				getModelElement().getPositionRef().getX() + offsetFromTarget.getWidth());
	}
}
