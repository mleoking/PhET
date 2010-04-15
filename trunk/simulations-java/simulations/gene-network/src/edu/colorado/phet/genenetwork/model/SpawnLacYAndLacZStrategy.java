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
	
	private static final double PRE_SPAWN_TIME = 2;          // In seconds of sim time.
	private static final double TIME_BETWEEN_SPAWNINGS = 1.5;  // In seconds of sim time.
	private static final Random RAND = new Random(2211934);

	private double spawnCountdownTimer = PRE_SPAWN_TIME;
	private int spawnCount = Integer.MAX_VALUE;
	private boolean spawnLacZNext = true;
	
	/**
	 * Constructor where the number of elements to spawn is specified.
	 * 
	 * @param numberToSpawn
	 */
	public SpawnLacYAndLacZStrategy(int numberToSpawn) {
		super();
		spawnCount = numberToSpawn;
	}
	
	/**
	 * Constructor that will choose the number to spawn.
	 */
	public SpawnLacYAndLacZStrategy(){
		// Choose a random number of spawns.  This choses only even numbers
		// so that equal amounts of LacZ and LacY are spawned.
		this(autoGenSpawnCount());
	}

	@Override
	public boolean isSpawningComplete() {
		// True if all the progeny has been created.
		return spawnCount == 0;
	}

	@Override
	public void stepInTime(double dt, SimpleModelElement parentModelElement) {
		if (spawnCountdownTimer != Double.POSITIVE_INFINITY){
			spawnCountdownTimer -= dt;
			if (spawnCountdownTimer <= 0){
				// Time to spawn.
				assert spawnCount > 0;
				if (spawnLacZNext){
					spawnLacZ(parentModelElement);
					spawnLacZNext = false;
				}
				else{
					spawnLacY(parentModelElement);
					spawnLacZNext = true;
				}
				spawnCount--;
				if (spawnCount > 0){
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
		// create the LacI.
		Rectangle2D bounds = parentModelElement.getShape().getBounds2D();
		Point2D transformationArrowPos = new Point2D.Double(
				bounds.getX() + parentModelElement.getPositionRef().getX() + bounds.getWidth() * 0.7 + 3,
				bounds.getY() + parentModelElement.getPositionRef().getY() + bounds.getHeight() * 0.3 + 3);
		LacYTransformationArrow transformationArrow = new LacYTransformationArrow(parentModelElement.getModel(),
				transformationArrowPos, new LacY(parentModelElement.getModel(), true), Math.PI/4);
		transformationArrow.setMotionStrategy(new LinearMotionStrategy(
				parentModelElement.getModel().getInteriorMotionBoundsAboveDna(), transformationArrowPos,
				new Vector2D.Double(parentModelElement.getVelocityRef()), 5.0));
		parentModelElement.getModel().addTransformationArrow(transformationArrow);
	}
	
	protected void spawnLacZ(SimpleModelElement parentModelElement) {
		// Create and position the transformation arrow, which will in turn
		// create the LacI.
		Rectangle2D bounds = parentModelElement.getShape().getBounds2D();
		Point2D transformationArrowPos = new Point2D.Double(
				bounds.getX() + parentModelElement.getPositionRef().getX() + bounds.getWidth() / 3 + 3,
				bounds.getMaxY() + parentModelElement.getPositionRef().getY() + 1);
		LacZTransformationArrow transformationArrow = new LacZTransformationArrow(parentModelElement.getModel(),
				transformationArrowPos, new LacZ(parentModelElement.getModel(), true), Math.PI/4);
		transformationArrow.setMotionStrategy(new LinearMotionStrategy(
				parentModelElement.getModel().getInteriorMotionBoundsAboveDna(), transformationArrowPos,
				new Vector2D.Double(parentModelElement.getVelocityRef()), 5.0));
		parentModelElement.getModel().addTransformationArrow(transformationArrow);
	}
	
	/**
	 * Automatically generate a pseudo-random number of items to spawn.
	 * 
	 * @return
	 */
	private static int autoGenSpawnCount(){
		// Notes: This version will only generate even numbers, since my
		// (jblanco) thinking as of April 8 2010 is that this is how it works
		// in real life - the mRNA is fully transcribed, and each time both
		// LacZ and LacY are produced.  I am not certain if this is the case,
		// it just stands to reason.
		//
		// Second, this function includes a "tweak factor" the influences the
		// probability of spawning 2 items (1 transcription) or 4 items (2
		// transcriptions).  This can be used to make there be on average more
		// or less LacZ and LacY present in the sim.  Right now this is only
		// adjustable at compile time, but could be hooked into the reading of
		// the lactose level if desired (and if we're sure it won't mislead
		// users).
		
		// Tweak factor - higher values make it more likely that more LacZ & 
		// LacY will be spawned.  1 is the max value.
		double makeMoreTweakFactor = 0.75;
		
		int spawnCount;
		if (RAND.nextDouble() > makeMoreTweakFactor){
			// Spawn 2.
			spawnCount = 4;
		}
		else{
			// Spawn 4.
			spawnCount = 6;
		}
		
		return spawnCount;
	}
}
