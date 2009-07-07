/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.EventObject;

import edu.colorado.phet.common.phetcommon.math.MathUtil;

/**
 * This class interprets model change descriptions (or "deltas") and executes
 * them on its model element.  The general idea is that the user provides an
 * animatable model element and a sequence of deltas, and then updates the
 * time periodically, and this class executes the event sequence base on the
 * provided time value.
 * 
 * @author John Blanco
 */
public class ModelAnimationInterpreter {

	private final AnimatedModelElement modelElement;
	private final AnimationSequence animationSequence;
	private int maxEventsPerUpdate = Integer.MAX_VALUE;
	private ArrayList<Listener> _listeners = new ArrayList<Listener>();
	
	/**
	 * Constructor.
	 */
	public ModelAnimationInterpreter(AnimatedModelElement modelElement, AnimationSequence animationSequence,
			int maxEventsPerUpdate) {
		
		this.modelElement = modelElement;
		this.animationSequence = animationSequence;
		this.maxEventsPerUpdate = maxEventsPerUpdate;
	}

	/**
	 * Alternate constructor.
	 * 
	 * @param modelElement
	 * @param animationSequence
	 */
	public ModelAnimationInterpreter(AnimatedModelElement modelElement, AnimationSequence animationSequence) {
		
		this(modelElement, animationSequence, Integer.MAX_VALUE);
	}
	
	/**
	 * Set the current time, which will cause all commands up to the maximum
	 * allowed per update that are specified to occur at or before the
	 * specified time value to be interpreted.
	 * 
	 * @param time
	 */
	public void setTime(double time){
		ArrayList<ModelAnimationDelta> animationDeltas = animationSequence.getNextAnimationDeltas(time);
		
		for ( int i = 0; i < animationDeltas.size() &&  i < maxEventsPerUpdate; i++){
			interpret(animationDeltas.get(i));
		}
	}
	
    public void addListener(Listener listener){
        if (!_listeners.contains( listener ))
        {
        	_listeners.add( listener );
        }
    }
    
    public boolean removeListener(Listener listener){
    	return _listeners.remove(listener);
    }
	
    protected void notifySimulationModeChanged(EventObject event) {
        for (int i = 0; i < _listeners.size(); i++){
            _listeners.get( i ).animationEventOccurred(event);
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
			double fadeFactor = modelElement.getFadeFactor() + delta.getFadeFactorDelta();
			fadeFactor = MathUtil.clamp(0, fadeFactor, 1);
			modelElement.setFadeFactor(fadeFactor);
		}
		if ( delta.getPositionDelta() != null  ){
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
					size.getHeight() * delta.getSizeChangeFactor() );
			modelElement.setSize(size);
		}
	}
	
	public static interface Listener {
		public void animationEventOccurred(EventObject event);
	}
}
