package edu.colorado.phet.genenetwork.model;

public class TimedMotionStrategy extends AbstractMotionStrategy {

	private AbstractMotionStrategy initialMotionStrategy;
	private AbstractMotionStrategy finalMotionStrategy;
	private double crossoverTime;
	private double elapsedTime = 0;
	
	public TimedMotionStrategy(IModelElement modelElement, AbstractMotionStrategy initialMotionStrategy, 
			AbstractMotionStrategy finalMotionStrategy, double crossoverTime) {
		super(modelElement);
		this.initialMotionStrategy = initialMotionStrategy;
		this.finalMotionStrategy = finalMotionStrategy;
		this.crossoverTime = crossoverTime;
	}

	@Override
	public void updatePositionAndMotion(double dt) {
		elapsedTime += dt;
		
		if (elapsedTime < crossoverTime){
			initialMotionStrategy.updatePositionAndMotion(dt);
		}
		else{
			finalMotionStrategy.updatePositionAndMotion(dt);
		}
	}
}
