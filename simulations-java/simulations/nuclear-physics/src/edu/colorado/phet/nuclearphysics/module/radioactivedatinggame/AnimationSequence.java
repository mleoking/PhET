package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.util.ArrayList;

/**
 * This interface provides a layer of abstraction through which animation
 * deltas may be obtained.  The idea is that the implementor of this interface
 * may contain a static sequence of events, or could generate them dynamically
 * based on some algorithm, or could use some combination of the two
 * approaches.
 * 
 * @author John Blanco
 */
public interface AnimationSequence {

	/**
	 * Get the sequence of animation deltas for the given time.  Each call to
	 * this method updates the internal time, so that can be thought of as an
	 * iterator of sorts in that it retains internal state that reflects the
	 * previous calls.  
	 * 
	 * @param endTime
	 * @return
	 */
	public ArrayList<ModelAnimationDelta> getNextAnimationDeltas(double endTime);

	/**
	 * Reset time for the animation sequence so that subsequence calls to the
	 * getters will start over at the beginning of the sequence.
	 */
	public void reset();
}
