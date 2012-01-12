/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{
	
import flash.display.DisplayObject;
import flash.display.DisplayObjectContainer;
import flash.display.InteractiveObject;
import flash.display.Sprite;
import flash.display.Stage;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.ui.Mouse;
import flash.utils.Dictionary;

import org.aswing.util.DepthManager;
import org.aswing.util.WeakMap;
	
/**
 * The CursorManager, manage the cursor, hide system mouse cursor, show custom cursor, 
 * etc.
 * @author iiley
 */
public class CursorManager{
	
	protected var root:DisplayObjectContainer = null;
	protected var cursorHolder:DisplayObjectContainer = null;
	protected var currentCursor:DisplayObject = null;
	
	/**
	 * Create a CursorManage
	 * @param cursorRoot the container to hold the cursors
	 * @see #getManager()
	 */
	public function CursorManager(cursorRoot:DisplayObjectContainer){
		setCursorContainerRoot(cursorRoot);
	}
	
	private static var managers:WeakMap = new WeakMap();
	
	/**
	 * Returns the default cursor manager for specified stage.
	 * <p>
	 * Generally, you should call this method to get cursor manager, 
	 * it will create one manager for each stage.
	 * </p>
	 * @param stage the stage, if pass null, the inital Stage of <code>AsWingManager.getStage()</code> 
	 * 			will be used.
	 */
	public static function getManager(stage:Stage=null):CursorManager{
		if(stage == null){
			stage = AsWingManager.getStage();
		}
		if(stage == null){
			return null;
		}
		var manager:CursorManager = managers.getValue(stage);
		if(manager == null){
			manager = new CursorManager(stage);
			managers.put(stage, manager);
		}
		return manager;
	}
	
	/**
	 * Sets the container to hold the cursors(in fact it will hold the cursor's parent--a sprite).
	 * By default(if you have not set one), it is the stage if <code>AsWingManager</code> is inited.
	 * @param theRoot the container to hold the cursors.
	 */
	protected function setCursorContainerRoot(theRoot:DisplayObjectContainer):void{
		if(theRoot != root){
			if(root){
				root.removeEventListener(Event.DEACTIVATE, __referenceEvent);
			}
			root = theRoot;
			//Make root reference this manager to keep manager will not be GC until root be GC.
			root.addEventListener(Event.DEACTIVATE, __referenceEvent);
			if(cursorHolder != null && cursorHolder.parent != root){
				root.addChild(cursorHolder);
			}
		}
	}
	private function __referenceEvent(e:Event):void{//just for keep stage reference this manager
	}
	
	protected function getCursorContainerRoot():DisplayObjectContainer{
		return root;
	}
	
	/**
	 * Shows your display object as the cursor and/or not hide the system cursor. 
	 * If current another custom cursor is showing, the current one will be removed 
	 * and then the new one is shown.
	 * @param cursor the display object to be add to the cursor container to be the cursor
	 * @param hideSystemCursor whether or not hide the system cursor when custom cursor shows.
	 */
	public function showCustomCursor(cursor:DisplayObject, hideSystemCursor:Boolean=true):void{
		if(hideSystemCursor){
			Mouse.hide();
		}else{
			Mouse.show();
		}
		if(cursor == currentCursor){
			return;
		}
		
		var ro:DisplayObjectContainer = getCursorContainerRoot();
		if(cursorHolder == null){
			if(ro != null){
				cursorHolder = new Sprite();
				cursorHolder.mouseEnabled = false;
				cursorHolder.tabEnabled = false;
				cursorHolder.mouseChildren = false;
				ro.addChild(cursorHolder);
			}
		}
		if(cursorHolder != null){
			if(currentCursor != cursor){
				if(currentCursor != null){
					cursorHolder.removeChild(currentCursor);
				}
				currentCursor = cursor;
				cursorHolder.addChild(currentCursor);
			}
			DepthManager.bringToTop(cursorHolder);
			ro.stage.addEventListener(MouseEvent.MOUSE_MOVE, __mouseMove, false, 0, true);
			__mouseMove(null);
		}
	}
	
	private function __mouseMove(e:MouseEvent):void{
		cursorHolder.x = cursorHolder.parent.mouseX;
		cursorHolder.y = cursorHolder.parent.mouseY;
		DepthManager.bringToTop(cursorHolder);
	}
	
	/**
	 * Hides the custom cursor which is showing and show the system cursor.
	 * @param cursor the showing cursor, if it is not the showing cursor, nothing 
	 * will happen
	 */
	public function hideCustomCursor(cursor:DisplayObject):void{
		if(cursor != currentCursor){
			return;
		}
		if(cursorHolder != null){
			if(currentCursor != null){
				cursorHolder.removeChild(currentCursor);
			}
		}
		currentCursor = null;
		Mouse.show();
		var ro:DisplayObjectContainer = getCursorContainerRoot();
		if(ro != null){
			ro.stage.removeEventListener(MouseEvent.MOUSE_MOVE, __mouseMove);
		}
	}
	
	private var tiggerCursorMap:Dictionary = new Dictionary(true);
	
	/**
	 * Sets the cursor when mouse on the specified trigger. null to remove cursor for that trigger.
	 * @param trigger where the cursor will shown when the mouse on the trigger
	 * @param cursor the cursor object, if cursor is null, the trigger's current cursor will be removed
	 */
	public function setCursor(trigger:InteractiveObject, cursor:DisplayObject):void{
		tiggerCursorMap[trigger] = cursor;
		if(cursor != null){
			trigger.addEventListener(MouseEvent.ROLL_OVER, __triggerOver, false, 0, true);
			trigger.addEventListener(MouseEvent.ROLL_OUT, __triggerOut, false, 0, true);
			trigger.addEventListener(MouseEvent.MOUSE_UP, __triggerUp, false, 0, true);
		}else{
			trigger.removeEventListener(MouseEvent.ROLL_OVER, __triggerOver, false);
			trigger.removeEventListener(MouseEvent.ROLL_OUT, __triggerOut, false);
			trigger.removeEventListener(MouseEvent.MOUSE_UP, __triggerUp, false);
			delete tiggerCursorMap[trigger];
		}
	}
		
	private function __triggerOver(e:MouseEvent):void{
		var trigger:Object = e.currentTarget;
		var cursor:DisplayObject = tiggerCursorMap[trigger] as DisplayObject;
		if(cursor && !e.buttonDown){
			showCustomCursor(cursor);
		}
	}
	
	private function __triggerOut(e:MouseEvent):void{
		var trigger:Object = e.currentTarget;
		var cursor:DisplayObject = tiggerCursorMap[trigger] as DisplayObject;
		if(cursor){
			hideCustomCursor(cursor);
		}
	}
	
	private function __triggerUp(e:MouseEvent):void{
		var trigger:InteractiveObject = e.currentTarget as InteractiveObject;
		var cursor:DisplayObject = tiggerCursorMap[trigger] as DisplayObject;
		if(cursor && trigger.hitTestPoint(e.stageX, e.stageY, true)){
			showCustomCursor(cursor);
		}
	}
}
}