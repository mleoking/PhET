/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.Component;

/**
 * The motion when a drag action dropped.
 * The motion must remove the drag movie clip when motion is completed.
 * @author iiley
 */
interface org.aswing.dnd.DropMotion {
	
	/**
	 * Starts the drop motion and remove the movieclip when motion is completed.
	 * @param dragInitiator the drag initiator
	 * @param dragMC the movie clip to do motion
	 */
	public function startMotionAndLaterRemove(dragInitiator:Component, dragMC:MovieClip):Void;
	
}