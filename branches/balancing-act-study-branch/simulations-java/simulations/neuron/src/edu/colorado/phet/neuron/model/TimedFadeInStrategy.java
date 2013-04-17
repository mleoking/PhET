// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.neuron.model;

public class TimedFadeInStrategy extends FadeStrategy {

	public final double fadeTime;
	public double opaquenessTarget = 1;
	public double fadeCountdownTimer;
	
	/**
	 * Constructor that assumes full fade in.
	 * 
	 * @param fadeTime - time, in seconds of sim time, for this to fade in.
	 */
	public TimedFadeInStrategy(double fadeTime){
		this.fadeTime = fadeTime;
		this.fadeCountdownTimer = fadeTime;
	}
	
	/**
	 * Constructor where the final opaqueness value is explicitly specified.
	 * 
	 * @param fadeTime - time, in seconds of sim time, for this to fade in.
	 * @param 
	 */
	public TimedFadeInStrategy(double fadeTime, double opaquenessTarget){
		this.fadeTime = fadeTime;
		this.opaquenessTarget = opaquenessTarget;
		this.fadeCountdownTimer = fadeTime;
	}
	
	@Override
	public void updateOpaqueness(IFadable fadableModelElement, double dt) {
		fadableModelElement.setOpaqueness( Math.min( (1 - fadeCountdownTimer / fadeTime) * opaquenessTarget, 1) );
		fadeCountdownTimer -= dt;
		if (fadeCountdownTimer < 0){
			fadeCountdownTimer = 0;
			// Done with the fade in, so set a null fade strategy.
			fadableModelElement.setFadeStrategy(new NullFadeStrategy());
		}
	}
}
