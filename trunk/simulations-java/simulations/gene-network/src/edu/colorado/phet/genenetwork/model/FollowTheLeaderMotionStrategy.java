/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Dimension2D;

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
	private ModelElementListenerAdapter leaderMotionListener = new ModelElementListenerAdapter() {
		@Override
		public void positionChanged() {
			updateFollowerPosition();
		}
	};
	
	public FollowTheLeaderMotionStrategy(IModelElement follower, IModelElement leader, Dimension2D offset) {
		super(follower);
		this.leaderToFollowerOffset = offset;
		
		// Register for updates from the "leader".
		leader.addListener(leaderMotionListener);
	}

	@Override
	public void updatePositionAndMotion(double dt) {
		// Does nothing, because it is assumed that the model element is
		// monitoring the motion of the leader and following that.
	}
	
	/**
	 * Update the position of the model element for which this motion strategy
	 * is responsible. 
	 */
	private void updateFollowerPosition(){
		getModelElement().setPosition(leader.getPositionRef().getX() + leaderToFollowerOffset.getWidth(),
				leader.getPositionRef().getY() + leaderToFollowerOffset.getHeight());
	}

	@Override
	public void cleanup() {
		leader.removeListener(leaderMotionListener);
	}
}
