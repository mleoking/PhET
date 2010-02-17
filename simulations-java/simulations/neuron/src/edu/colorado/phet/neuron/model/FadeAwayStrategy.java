package edu.colorado.phet.neuron.model;

public class FadeAwayStrategy extends FadeStrategy {

	public final double fadeRate;
	
	/**
	 * Constructor
	 * 
	 * @param fadeRate - rate, in proportion per second of sim time, at which
	 * fadeout should progress.
	 */
	public FadeAwayStrategy(double fadeRate){
		this.fadeRate = fadeRate;
	}
	
	@Override
	public void updateOpaqueness(IFadable fadableModelElement, double dt) {
		fadableModelElement.setOpaqueness( Math.max( fadableModelElement.getOpaqueness() - ( fadeRate * dt ), 0) );
	}
	
	@Override
	public boolean shouldContinueExisting(IFadable fadeableModelElement) {
		return fadeableModelElement.getOpaqueness() > 0;
	}
}
