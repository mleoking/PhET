/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.Component;
import org.aswing.dnd.DefaultDragImage;
import org.aswing.dnd.DirectlyRemoveMotion;
import org.aswing.dnd.DraggingImage;
import org.aswing.dnd.DragListener;
import org.aswing.dnd.DropMotion;
import org.aswing.dnd.RejectedMotion;
import org.aswing.dnd.SourceData;
import org.aswing.ElementCreater;
import org.aswing.geom.Point;
import org.aswing.util.ArrayUtils;
import org.aswing.util.ObjectUtils;

/**
 * Drag and Drop Manager.
 * <p>
 * Thanks Bill Lee for the original DnD implementation for AsWing.
 * @author iiley
 */
class org.aswing.dnd.DragManager {
	
	public static var TYPE_NONE:Number = 0;
	public static var TYPE_MOVE:Number = 1;
	public static var TYPE_COPY:Number = 2;
	
	public static var DEFAULT_DROP_MOTION:DropMotion = new DirectlyRemoveMotion();
	public static var DEFAULT_REJECT_DROP_MOTION:DropMotion = new RejectedMotion();
	
	private static var s_isDragging:Boolean = false;
	
	private static var s_dragListener:DragListener;
	private static var s_dragInitiator:Component;
	private static var s_sourceData:SourceData;
	private static var s_dragImage:DraggingImage;
	
	private static var dropMotion:DropMotion;
	private static var dragProxyMC:MovieClip;
	private static var mouseOffset:Point;
	private static var enteredComponent:Component;
	
	private static var listeners:Array = new Array();
	
	/**
	 * startDrag(dragInitiator:Component, dragSource, dragImage:MovieClip, lis:DragListener)<br>
	 * startDrag(dragInitiator:Component, dragSource, dragImage:MovieClip)<br>
	 * startDrag(dragInitiator:Component, dragSource)<br>
	 * <p>
	 * Starts dragging a initiator, with dragSource, a dragging Image, and a listener. The drag action will be finished 
	 * at next Mouse UP/Down Event(Mouse UP or Mouse Down, generally you start a drag when mouse down, then it will be finished 
	 * when mouse up, if you start a drag when mouse up, it will be finished when mouse down).
	 * 
	 * @param dragInitiator the dragging initiator
	 * @param sourceData the data source will pass to the listeners and target components
	 * @param dragImage (optional)the image to drag, default is a rectangle image.
	 * @param dragListener (optional)the listener added to just for this time dragging action, default is null(no listener)
	 */
	public static function startDrag(dragInitiator:Component, sourceData:SourceData, dragImage:DraggingImage, dragListener:DragListener):Void{
		if(s_isDragging){
			trace("/e/The last dragging action is not finished, can't start a new one!");
			throw new Error("/The last dragging action is not finished, can't start a new one!");
			return;
		}
		
		if(dragImage == undefined){
			dragImage = new DefaultDragImage(dragInitiator);
		}
		
		s_isDragging = true;
		s_dragInitiator = dragInitiator;
		s_sourceData = sourceData;
		s_dragImage = dragImage;
		s_dragListener = dragListener;
		if(s_dragListener != null){
			addDragListener(s_dragListener);
		}
		
		var root:MovieClip = dragInitiator.getRootAncestorMovieClip();
		dragProxyMC.removeMovieClip();
		dragProxyMC = ElementCreater.getInstance().createMCWithName(root, "_aswing_drag_image_");
		var globalPos:Point = dragInitiator.getGlobalLocation();;
		var dp:Point = globalPos.clone();
		root.globalToLocal(dp);
		dragProxyMC._x = dp.x;
		dragProxyMC._y = dp.y;
		
		dragImage.setCanvas(dragProxyMC);
		
		mouseOffset = new Point(root._xmouse - dp.x, root._ymouse - dp.y);
		fireDragStartEvent(s_dragInitiator, s_sourceData, globalPos);
		
		enteredComponent = null;
		//call it first once to avoid the _dropTarget bug
		dragProxyMC.startDrag(false);
		dragProxyMC.stopDrag();
		//initial image
		s_dragImage.switchToRejectImage();
		onMouseMove();
		Mouse.removeListener(DragManager);
		Mouse.addListener(DragManager);
	}
		
	/**
	 * Adds a drag listener to listener list.
	 * @param lis the listener to be add
	 */
	public static function addDragListener(lis:DragListener):Void{
		listeners.push(lis);
	}
	
	/**
	 * Removes the specified listener from listener list.
	 * @param lis the listener to be removed
	 */
	public static function removeDragListener(lis:DragListener):Void{
		ArrayUtils.removeFromArray(listeners, lis);
	}
	
	/**
	 * Sets the motion of drag movie clip when a drop acted.
	 * <p>
	 * Generally if you want to do a custom motion of the dragging movie clip when dropped, you may 
	 * call this method in the listener's <code>onDragDrop()</code> method.
	 * <p>
	 * Every drop acted, the default motion will be set to <code>DirectlyRemoveMotion</code> 
	 * so you need to set to yours every drop time if you want.
	 * @param motion the motion
	 * @see org.aswing.dnd.DropMotion
	 * @see org.aswing.dnd.DirectlyRemoveMotion
	 * @see org.aswing.dnd.RejectedMotion
	 * @see org.aswing.dnd.DragListener#onDragDrop()
	 */
	public static function setDropMotion(motion:DropMotion):Void{
		if(motion == null) motion = DEFAULT_DROP_MOTION;
		dropMotion = motion;
	}
	
	/**
	 * Returns the drag image.
	 */
	public static function getCurrentDragImage():DraggingImage{
		return s_dragImage;
	}
	
	/**
	 * Returns current drop target movie clip / textfield of dragging components by startDrag method.
	 * @return the drop target movie clip / textfield
	 * @see #startDrag()
	 * @see #getDropTarget()
	 */
	public static function getCurrentDropTarget():MovieClip{
		return getDropTarget(dragProxyMC);
	}	
	
	/**
	 * Returns current drop target movie clip of dragging components by startDrag method.
	 * @return the drop target movie clip
	 * @see #startDrag()
	 * @see #getDropTargetMovieClip()
	 */
	public static function getCurrentDropTargetMovieClip():MovieClip{
		return getDropTargetMovieClip(dragProxyMC);
	}
	
	/**
	 * Returns current drop target component of dragging components by startDrag method.
	 * @return the drop target component
	 * @see #startDrag()
	 * @see #getDropTargetComponent()
	 */
	public static function getCurrentDropTargetComponent():Component{
		return getDropTargetComponent(dragProxyMC);
	}
	
	/**
	 * Returns current drop target drop trigger component of dragging components by startDrag method.
	 * @return the drop target drop trigger component
	 * @see #startDrag()
	 * @see #getDropTargetDropTriggerComponent()
	 */
	public static function getCurrentDropTargetDropTriggerComponent():Component{
		return getDropTargetDropTriggerComponent(dragProxyMC);
	}
	
	/**
	 * Returns the drop target movie clip /textfield of specified dragging movie clip.
	 * @param draggingMC the dragging movie clip
	 * @return drop target movie clip / textfield
	 */
	public static function getDropTarget(draggingMC:MovieClip){
		if(draggingMC == null){
			return null;
		}else{
			draggingMC.startDrag(false);
			var target = eval(draggingMC._droptarget);
			draggingMC.stopDrag();
			return target;
		}
	}
	
	/**
	 * Returns the drop target movie clip of specified dragging movie clip.
	 * @param draggingMC the dragging movie clip
	 * @return drop target movie clip
	 */
	public static function getDropTargetMovieClip(draggingMC:MovieClip):MovieClip{
		var target = getDropTarget(draggingMC);
		if(target == null){
			return null;
		}else if(ObjectUtils.isMovieClip(target)){
			return target;
		}else{
			return target._parent;
		}
	}
	
	/**
	 * Returns the drop target component of specified dragging movie clip.
	 * @param draggingMC the dragging movie clip
	 * @see #Component#getOwnerComponent()
	 * @see #getDropTargetMovieClip()
	 * @return drop target component, or null there is not a component triggered as drop target.
	 */
	public static function getDropTargetComponent(draggingMC:MovieClip):Component{
		var mc:MovieClip = getDropTargetMovieClip(draggingMC);
		if(mc != null){
			return Component.getOwnerComponent(mc);
		}
		return null;
	}
	
	/**
	 * Returns the drop target component drop trigger of specified dragging movie clip.<p>
	 * The drop trigger is a component which it is a drop trigger and itself or its child is drop targeted.
	 * @param draggingMC the dragging movie clip
	 * @see Component#isDropTrigger()
	 * @see #getDropTargetComponent()
	 * @return drop trigger target component, or null there is not a trigger component triggered as drop target.
	 */
	public static function getDropTargetDropTriggerComponent(draggingMC:MovieClip):Component{
		var c:Component = getDropTargetComponent(draggingMC);
		while(c != null && !c.isDropTrigger()){
			c = c.getParent();
		}
		return c;
	}
	
	//---------------------------------------------------------------------------------
	
	private static function onMouseMove():Void{
		var globalPos:Point = moveDragingImageAndReturnGlobalPos();
		
		var dropC:Component = getCurrentDropTargetDropTriggerComponent();
		if(dropC != enteredComponent){
			if(enteredComponent != null){
				s_dragImage.switchToRejectImage();
				fireDragExitEvent(s_dragInitiator, s_sourceData, globalPos, enteredComponent);
				enteredComponent.fireDragExitEvent(s_dragInitiator, s_sourceData, globalPos);
			}
			if(dropC != null){
				if(dropC.isDragAcceptableInitiator(s_dragInitiator)){
					s_dragImage.switchToAcceptImage();
				}
				fireDragEnterEvent(s_dragInitiator, s_sourceData, globalPos, dropC);
				dropC.fireDragEnterEvent(s_dragInitiator, s_sourceData, globalPos);
			}
			enteredComponent = dropC;
		}else{
			if(enteredComponent != null){
				fireDragOverringEvent(s_dragInitiator, s_sourceData, globalPos, enteredComponent);
				enteredComponent.fireDragOverringEvent(s_dragInitiator, s_sourceData, globalPos);
			}
		}
	}
	
	private static function onMouseUp():Void{
		drop();
	}
	
	private static function onMouseDown():Void{
		drop(); //why call drop again when mouse down?
		//just because if you released mouse outside of flash movie, then the mouse up
		//was not triggered. So if that happened, when user reclick mouse, the drop will 
		//fire (the right behavor to ensure dragging thing was dropped).
		//Well, if user released mouse rightly in the flash movie, then the drop be called, 
		//and in drop method, the mouse listener was removed, so it will not be called drop 
		//again when next mouse down. :)
	}
	
	private static function drop():Void{
		var globalPos:Point = moveDragingImageAndReturnGlobalPos();
		var dropC:Component = getDropTargetDropTriggerComponent(dragProxyMC);
		Mouse.removeListener(DragManager);
		s_isDragging = false;
		
		if(enteredComponent != null){
			setDropMotion(DEFAULT_DROP_MOTION);
		}else{
			setDropMotion(DEFAULT_REJECT_DROP_MOTION);
		}
		fireDragDropEvent(s_dragInitiator, s_sourceData, globalPos, enteredComponent);
		dropMotion.startMotionAndLaterRemove(s_dragInitiator, dragProxyMC);
		if(enteredComponent != null){
			enteredComponent.fireDragDropEvent(s_dragInitiator, s_sourceData, globalPos);
		}
		
		if(s_dragListener != null){
			removeDragListener(s_dragListener);
		}
		s_dragImage = null;
		dragProxyMC = null;
		s_dragListener = null;
		s_sourceData = null;
		enteredComponent = null;
	}
	
	private static function moveDragingImageAndReturnGlobalPos():Point{
		var p:Point = new Point(dragProxyMC._parent._xmouse - mouseOffset.x, dragProxyMC._parent._ymouse - mouseOffset.y);
		dragProxyMC._x = p.x;
		dragProxyMC._y = p.y;
		dragProxyMC._parent.localToGlobal(p);
		return p;
	}
	
	private static function fireDragStartEvent(dragInitiator:Component, sourceData:SourceData, pos:Point):Void{
		for(var i:Number=0; i<listeners.length; i++){
			var lis:DragListener = DragListener(listeners[i]);
			lis.onDragStart(dragInitiator, sourceData, pos);
		}
	}
	
	private static function fireDragEnterEvent(dragInitiator:Component, sourceData:SourceData, pos:Point, targetComponent:Component):Void{
		for(var i:Number=0; i<listeners.length; i++){
			var lis:DragListener = DragListener(listeners[i]);
			lis.onDragEnter(dragInitiator, sourceData, pos, targetComponent);
		}
	}
	
	private static function fireDragOverringEvent(dragInitiator:Component, sourceData:SourceData, pos:Point, targetComponent:Component):Void{
		for(var i:Number=0; i<listeners.length; i++){
			var lis:DragListener = DragListener(listeners[i]);
			lis.onDragOverring(dragInitiator, sourceData, pos, targetComponent);
		}
	}
	
	private static function fireDragExitEvent(dragInitiator:Component, sourceData:SourceData, pos:Point, targetComponent:Component):Void{
		for(var i:Number=0; i<listeners.length; i++){
			var lis:DragListener = DragListener(listeners[i]);
			lis.onDragExit(dragInitiator, sourceData, pos, targetComponent);
		}
	}
	
	private static function fireDragDropEvent(dragInitiator:Component, sourceData:SourceData, pos:Point, targetComponent:Component):Void{
		for(var i:Number=0; i<listeners.length; i++){
			var lis:DragListener = DragListener(listeners[i]);
			lis.onDragDrop(dragInitiator, sourceData, pos, targetComponent);
		}
	}
}