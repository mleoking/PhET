/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class interprets model change descriptions (or "deltas") and executes
 * them on its model element.  The general idea is that the user provides an
 * animatable model element and a sequence of deltas, and then updates the
 * time periodically, and this class executes the event sequence base on the
 * provided time value.
 * 
 * @author John Blanco
 */
public class ModelAnimationDeltaInterpreter {

	private final AnimatedModelElement modelElement;
	private final ArrayList<ModelAnimationDelta> animationDeltas;
	private int currentIndex = 0;
	private int maxEventsPerUpdate = Integer.MAX_VALUE;
	
	/**
	 * Constructor.
	 */
	public ModelAnimationDeltaInterpreter(AnimatedModelElement modelElement,
			ArrayList<ModelAnimationDelta> animationDeltas, int maxEventsPerUpdate) {
		
		this.modelElement = modelElement;
		this.animationDeltas = animationDeltas;
		this.maxEventsPerUpdate = maxEventsPerUpdate;
	}

	/**
	 * Alternate constructor.
	 * 
	 * @param modelElement
	 * @param animationDeltas
	 */
	public ModelAnimationDeltaInterpreter(AnimatedModelElement modelElement,
			ArrayList<ModelAnimationDelta> animationDeltas) {
		
		this(modelElement, animationDeltas, Integer.MAX_VALUE);
	}
	
	/**
	 * Set the current time, which will cause all commands up to the maximum
	 * allowed per update that are specified to occur at or before the
	 * specified time value to be interpreted.
	 * 
	 * @param time
	 */
	public void setTime(double time){
		for ( int i = 0; i < maxEventsPerUpdate; i++){
			if (time > animationDeltas.get(i).getTime()){
				// Execute this animation delta.
				interpret(animationDeltas.get(i));
			}
		}
	}
	
	/**
	 * Interpret the animation delta by looking at each field, deciding if
	 * its value implies that something needs to change for the model element
	 * and, if so, executing the change.
	 * 
	 * @param delta
	 */
	private void interpret(ModelAnimationDelta delta){
		if ( delta.getPrimaryImageIndexDelta() != 0 ){
			// The image needs to be updated.
			int maxImages = modelElement.getNumberImages();
			modelElement.setPrimaryImageIndex(
				( delta.getPrimaryImageIndexDelta() + modelElement.getPrimaryImageIndex() ) % maxImages );
		}
		if ( delta.getSecondaryImageIndexDelta() != 0 ){
			// The image needs to be updated.
			int maxImages = modelElement.getNumberImages();
			modelElement.setSecondaryImageIndex(
				( delta.getSecondaryImageIndexDelta() + modelElement.getSecondaryImageIndex() ) % maxImages );
		}
		if ( delta.getFadeFactorDelta() != 0 ){
			// The fade amount needs to be updated.
			modelElement.setFadeFactor(modelElement.getFadeFactor() + delta.getFadeFactorDelta());
		}
		if ( delta.getPositionDelta().getX() != 0 || delta.getPositionDelta().getY() != 0 ){
			// The position needs to be updated.
			double xPos = modelElement.getPosition().getX() + delta.getPositionDelta().getX();
			double yPos = modelElement.getPosition().getY() + delta.getPositionDelta().getY();
			modelElement.setPosition(new Point2D.Double(xPos, yPos));
		}
		if ( delta.getRotationDelta() != 0 ){
			// The rotational angle needs to be updated.
			modelElement.setRotationalAngle(modelElement.getRotationalAngle() + delta.getRotationDelta());
		}
		if ( delta.getSizeChangeFactor() != 1 ){
			// The size needs to be changed.
			Dimension2D size = modelElement.getSize();
			size.setSize(size.getWidth() * delta.getSizeChangeFactor(),
					size.getWidth() * delta.getSizeChangeFactor() );
			modelElement.setSize(size);
		}
	}
}
