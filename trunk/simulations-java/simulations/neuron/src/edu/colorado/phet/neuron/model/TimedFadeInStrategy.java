package edu.colorado.phet.neuron.model;

public class TimedFadeInStrategy extends FadeStrategy {

	public final double fadeTime;
	public double fadeCountdownTimer;
	
	/**
	 * Constructor
	 * 
	 * @param fadeTime - time, in seconds of sim time, for this to fade in.
	 */
	public TimedFadeInStrategy(double fadeTime){
		this.fadeTime = fadeTime;
		this.fadeCountdownTimer = fadeTime;
	}
	
	@Override
	public void updateOpaqueness(IFadable fadableModelElement, double dt) {
		fadableModelElement.setOpaqueness( Math.min( 1 - fadeCountdownTimer / fadeTime, 1) );
		fadeCountdownTimer -= dt;
	}
}
