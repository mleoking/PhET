package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.util.ArrayList;

/**
 * This class implements a static sequence of animation deltas, meaning that
 * the animation sequence is fixed and will be the same every time it is used
 * from beginning to end.
 * 
 * @author John Blanco
 */
public class StaticAnimationSequence implements AnimationSequence {

	private ArrayList<ModelAnimationDelta> animationDeltas;
	private int currentIndex = 0; 

	/**
	 * Constructor.
	 * 
	 * @param animationDeltas
	 */
	public StaticAnimationSequence( ArrayList<ModelAnimationDelta> animationDeltas) {
		this.animationDeltas = animationDeltas;
	}

	public ArrayList<ModelAnimationDelta> getNextAnimationDeltas(double endTime) {
		
		ArrayList<ModelAnimationDelta> animationDeltaSubset = new ArrayList<ModelAnimationDelta>();
		
		while ((currentIndex < animationDeltas.size()) && (animationDeltas.get(currentIndex).getTime() <= endTime)){
			animationDeltaSubset.add(animationDeltas.get(currentIndex));
			currentIndex++;
		}
		return animationDeltaSubset;
	}

	public void reset() {
		currentIndex = 0;
	}
}
