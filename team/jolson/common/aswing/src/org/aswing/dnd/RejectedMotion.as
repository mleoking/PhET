/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.Component;
import org.aswing.dnd.DropMotion;
import org.aswing.geom.Point;
import org.aswing.util.EnterFrameImpulser;
import org.aswing.util.Impulser;
import org.aswing.util.MCUtils;

/**
 * The motion of the drop target does not accept the dropped initiator. 
 * @author iiley
 */
class org.aswing.dnd.RejectedMotion implements DropMotion{
	
	private var timer:Impulser;
	private var initiatorPos:Point;
	private var dragMC:MovieClip;
	
	public function RejectedMotion(){
		timer = new EnterFrameImpulser(1);
		timer.addActionListener(__enterFrame, this);
	}
	
	private function startNewMotion(dragInitiator:Component, dragMC : MovieClip):Void{
		this.dragMC = dragMC;
		initiatorPos = dragInitiator.getGlobalLocation();
		if(initiatorPos == null){
			initiatorPos = new Point();
		}
		timer.start();
	}
	
	public function startMotionAndLaterRemove(dragInitiator:Component, dragMC : MovieClip) : Void {
		//create a new instance to do motion, avoid muiltiple motion shared instance
		var motion:RejectedMotion = new RejectedMotion();
		motion.startNewMotion(dragInitiator, dragMC);
	}
	
	private function __enterFrame():Void{
		//check first
		if(!MCUtils.isMovieClipExist(dragMC)){
			timer.stop();
			return;
		}
		var speed:Number = 0.25;
		
		var p:Point = new Point(dragMC._x, dragMC._y);
		dragMC._parent.localToGlobal(p);
		p.x += (initiatorPos.x - p.x) * speed;
		p.y += (initiatorPos.y - p.y) * speed;
		if(p.distance(initiatorPos) < 1){
			dragMC.removeMovieClip();
			timer.stop();
			return;
		}
		dragMC._parent.globalToLocal(p);
		dragMC._alpha += (4 - dragMC._alpha) * speed;
		dragMC._x = p.x;
		dragMC._y = p.y;
	}
}