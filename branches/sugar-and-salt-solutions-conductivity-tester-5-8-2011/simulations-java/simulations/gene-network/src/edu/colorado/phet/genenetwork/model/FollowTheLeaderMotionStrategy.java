// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Dimension2D;

import edu.umd.cs.piccolo.util.PDimension;

/**
 * This is a motion strategy that defers to a leader, meaning that it never
 * decides to move on its own, but instead watches the motion of the leader
 * and updates the position every time the leader moves.
 * 
 * @author John Blanco
 */
public class FollowTheLeaderMotionStrategy extends AbstractMotionStrategy {

	private IModelElement leader;
	private Dimension2D leaderToFollowerOffset;
	private IModelElement follower;
	private ModelElementListenerAdapter leaderMotionListener = new ModelElementListenerAdapter() {
		@Override
		public void positionChanged() {
			// The leader's position has changed, so the follower should,
			// well, follow, but only if it is not user controlled.
			if (!follower.isUserControlled()){
				updateFollowerPosition(follower);
			}
		}
	};
	
	public FollowTheLeaderMotionStrategy(IModelElement follower, IModelElement leader, Dimension2D offset) {
		super();
		this.leader = leader;
		this.follower = follower;
		this.leaderToFollowerOffset = new PDimension(offset);
		
		
		// Register for updates from the "leader".
		leader.addListener(leaderMotionListener);
	}

	@Override
	public void updatePositionAndMotion(double dt, SimpleModelElement modelElement) {
		// Does nothing, because it is assumed that the model element is
		// monitoring the motion of the leader and following that.
	}
	
	/**
	 * Update the position of the model element for which this motion strategy
	 * is responsible. 
	 * @param modelElement
	 */
	private void updateFollowerPosition(IModelElement modelElement){
		modelElement.setPosition(leader.getPositionRef().getX() + leaderToFollowerOffset.getWidth(),
				leader.getPositionRef().getY() + leaderToFollowerOffset.getHeight());
	}

	@Override
	public void cleanup() {
		leader.removeListener(leaderMotionListener);
	}
}
