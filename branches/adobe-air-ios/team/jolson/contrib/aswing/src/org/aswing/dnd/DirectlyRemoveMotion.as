/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.Component;
import org.aswing.dnd.DropMotion;

/**
 * Remove the dragging movieclip directly.
 * @author iiley
 */
class org.aswing.dnd.DirectlyRemoveMotion implements DropMotion{
	
	public function DirectlyRemoveMotion(){
	}
	
	public function startMotionAndLaterRemove(dragInitiator:Component, dragMC : MovieClip) : Void {
		dragMC.removeMovieClip();
	}

}