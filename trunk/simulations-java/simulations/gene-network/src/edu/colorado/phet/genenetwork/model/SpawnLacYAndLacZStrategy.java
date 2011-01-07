// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * Spawning strategy that creates LacI.
 * 
 * @author John Blanco
 */
public class SpawnLacYAndLacZStrategy extends MessengerRnaSpawningStrategy {
	
	private static final double PRE_SPAWN_TIME = 1;          // In seconds of sim time.
	private static final double TIME_BETWEEN_SPAWNINGS = 1.0;  // In seconds of sim time.
	
	private static final Random RAND = new Random();

	private double spawnCountdownTimer = PRE_SPAWN_TIME;
	private int spawnLacZCount;
	private int spawnLacYCount;
	private boolean spawnLacZNext = true;
	
	/**
	 * Constructor that will choose the number to spawn.
	 */
	public SpawnLacYAndLacZStrategy(){
		// Initialize the spawn counts.  Based on feedback from George
		// Spiegelman of UBC, there should be more LacZ created than LacY.
		spawnLacZCount = RAND.nextInt(3) + 2;
		spawnLacYCount = Math.max(spawnLacZCount / 2, 2);
	}

	@Override
	public boolean isSpawningComplete() {
		// True if all the progeny has been created.
		return (spawnLacZCount == 0 && spawnLacYCount == 0);
	}

	@Override
	public void stepInTime(double dt, SimpleModelElement parentModelElement) {
		if (spawnCountdownTimer != Double.POSITIVE_INFINITY){
			spawnCountdownTimer -= dt;
			if (spawnCountdownTimer <= 0){
				// Time to spawn.
				assert spawnLacZCount > 0 || spawnLacYCount > 0;
				if (spawnLacZNext){
					spawnLacZ(parentModelElement);
					spawnLacZCount--;
					if (spawnLacYCount > 0){
						spawnLacZNext = false;
					}
				}
				else{
					spawnLacY(parentModelElement);
					spawnLacYCount--;
					spawnLacZNext = true;
				}
				if (spawnLacZCount > 0 || spawnLacYCount > 0){
					// Set the timer for the next spawning.
					spawnCountdownTimer = TIME_BETWEEN_SPAWNINGS;
				}
				else{
					// No more spawning to be done.
					spawnCountdownTimer = Double.POSITIVE_INFINITY;
				}
			}
		}
	}

	protected void spawnLacY(SimpleModelElement parentModelElement) {
		// Create and position the transformation arrow, which will in turn
		// create the LacY.
		Rectangle2D bounds = parentModelElement.getShape().getBounds2D();
		Point2D transformationArrowPos = new Point2D.Double(
				bounds.getX() + parentModelElement.getPositionRef().getX() + bounds.getWidth() * 0.7 + 3,
				bounds.getY() + parentModelElement.getPositionRef().getY() + bounds.getHeight() * 0.3 + 3);
		LacYTransformationArrow transformationArrow = new LacYTransformationArrow(parentModelElement.getModel(),
				transformationArrowPos, new LacY(parentModelElement.getModel(), true), Math.PI/4);
		transformationArrow.setMotionStrategy(new LinearMotionStrategy(
				parentModelElement.getModel().getInteriorMotionBoundsAboveDna(), transformationArrowPos,
				new Vector2D(parentModelElement.getVelocityRef()), 5.0));
		parentModelElement.getModel().addTransformationArrow(transformationArrow);
	}
	
	protected void spawnLacZ(SimpleModelElement parentModelElement) {
		// Create and position the transformation arrow, which will in turn
		// create the LacZ.
		Rectangle2D bounds = parentModelElement.getShape().getBounds2D();
		Point2D transformationArrowPos = new Point2D.Double(
				bounds.getX() + parentModelElement.getPositionRef().getX() + bounds.getWidth() / 3 + 3,
				bounds.getMaxY() + parentModelElement.getPositionRef().getY() + 1);
		LacZTransformationArrow transformationArrow = new LacZTransformationArrow(parentModelElement.getModel(),
				transformationArrowPos, new LacZ(parentModelElement.getModel(), true), Math.PI/4);
		transformationArrow.setMotionStrategy(new LinearMotionStrategy(
				parentModelElement.getModel().getInteriorMotionBoundsAboveDna(), transformationArrowPos,
				new Vector2D(parentModelElement.getVelocityRef()), 5.0));
		parentModelElement.getModel().addTransformationArrow(transformationArrow);
	}	
}
