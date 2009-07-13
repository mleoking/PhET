package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * An animated datable item that has a static animation sequence, meaning
 * one that doesn't change regardless of whatever else is happening within the
 * model or is commanded by the user.
 * 
 * @author John Blanco
 */
public class StaticAnimatedDatableItem extends AnimatedDatableItem {

	public StaticAnimatedDatableItem(String name, List<String> resourceImageNames, Point2D center, double width,
			double rotationAngle, double age, ConstantDtClock clock, double ageAdjustmentFactor) {
		super(name, resourceImageNames, center, width, rotationAngle, age, clock,
				ageAdjustmentFactor);
	}

	@Override
	protected AnimationSequence getAnimationSequence() {
		// TODO Auto-generated method stub
		return null;
	}

}
