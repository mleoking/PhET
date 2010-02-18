package edu.colorado.phet.neuron.model;

public class TimedFadeAwayStrategy extends FadeStrategy {

	public final double fadeTime;
	public double fadeCountdownTimer;
	
	/**
	 * Constructor
	 * 
	 * @param fadeTime - time, in seconds of sim time, for this to fade away.
	 */
	public TimedFadeAwayStrategy(double fadeTime){
		this.fadeTime = fadeTime;
		this.fadeCountdownTimer = fadeTime;
	}
	
	@Override
	public void updateOpaqueness(IFadable fadableModelElement, double dt) {
		fadableModelElement.setOpaqueness( Math.max( fadeCountdownTimer / fadeTime, 0) );
		fadeCountdownTimer -= dt;
	}
	
	@Override
	public boolean shouldContinueExisting(IFadable fadeableModelElement) {
		return fadeableModelElement.getOpaqueness() > 0;
	}
}
