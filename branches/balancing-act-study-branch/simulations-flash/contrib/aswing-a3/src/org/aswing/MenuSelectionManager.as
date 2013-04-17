/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

import flash.display.InteractiveObject;
import flash.events.EventDispatcher;
import flash.events.KeyboardEvent;
import flash.ui.Keyboard;

import org.aswing.event.InteractiveEvent;
import org.aswing.util.ArrayUtils;
import org.aswing.util.ArrayList;
import org.aswing.util.WeakReference;
	
/**
 * Dispatched when the menu selection changed.
 * 
 * @eventType org.aswing.event.InteractiveEvent.SELECTION_CHANGED
 */
[Event(name="selectionChanged", type="org.aswing.event.InteractiveEvent")]

/**
 * A MenuSelectionManager owns the selection in menu hierarchy.
 * 
 * @author iiley
 */
public class MenuSelectionManager extends EventDispatcher{
		 	
	private static var instance:MenuSelectionManager;
	
	protected var selection:ArrayList;
	
	public function MenuSelectionManager(){
		selection = new ArrayList();
	}
	
	public static function defaultManager():MenuSelectionManager{
		if(instance == null){
			instance = new MenuSelectionManager();
		}
		return instance;
	}
	
	/**
	 * Replaces the default manager by yours.
	 */
	public static function setDefaultManager(m:MenuSelectionManager):void{
		instance = m;
	}
	
	protected var lastTriggerRef:WeakReference = new WeakReference();
    /**
     * Changes the selection in the menu hierarchy.  The elements
     * in the array are sorted in order from the root menu
     * element to the currently selected menu element.
     * <p>
     * Note that this method is public but is used by the look and
     * feel engine and should not be called by client applications.
     * </p>
     * @param path  an array of <code>MenuElement</code> objects specifying
     *        the selected path.
     * @param programmatic indicate if this is a programmatic change.
     */
    public function setSelectedPath(trigger:InteractiveObject, path:Array, programmatic:Boolean):void { //MenuElement[] 
        var i:int;
        var c:int;
        var currentSelectionCount:Number = selection.size();
        var firstDifference:int = 0;
				
        if(path == null) {
            path = new Array();
        }

        for(i=0,c=path.length; i<c; i++) {
            if(i < currentSelectionCount && selection.get(i) == path[i]){
                firstDifference++;
            }else{
                break;
            }
        }

        for(i=currentSelectionCount-1 ; i>=firstDifference; i--) {
            var me:MenuElement = MenuElement(selection.get(i));
            selection.removeAt(i);
            me.menuSelectionChanged(false);
        }

        for(i = firstDifference, c = path.length ; i < c ; i++) {
        	var tm:MenuElement = MenuElement(path[i]);
		    if (tm != null) {
				selection.append(tm);
				tm.menuSelectionChanged(true);
		    }
		}
		if(firstDifference < path.length - 1 || currentSelectionCount != path.length){
			fireSelectionChanged(programmatic);
		}
		var lastTrigger:InteractiveObject = lastTriggerRef.value;
		if(selection.size() == 0){
			if(lastTrigger){
				lastTrigger.removeEventListener(KeyboardEvent.KEY_DOWN, __onMSMKeyDown);
				lastTriggerRef.clear();
			}
		}else{
			if(lastTrigger != trigger){
				if(lastTrigger){
					lastTrigger.removeEventListener(KeyboardEvent.KEY_DOWN, __onMSMKeyDown);
				}
				lastTrigger = trigger;
				if(trigger){
					trigger.addEventListener(KeyboardEvent.KEY_DOWN, __onMSMKeyDown, false, 0, true);
				}
				lastTriggerRef.value = trigger;
			}
		}
    }
    
	/**
	 * Adds a listener to listen the menu seletion change event.
	 * @param listener the listener
	 * @param priority the priority
	 * @param useWeakReference Determines whether the reference to the listener is strong or weak.
	 * @see org.aswing.event.InteractiveEvent#STATE_CHANGED
	 */	
	public function addSelectionListener(listener:Function, priority:int=0, useWeakReference:Boolean=false):void{
		addEventListener(InteractiveEvent.SELECTION_CHANGED, listener, false, priority);
	}	
	
	/**
	 * Removes a menu seletion change listener.
	 * @param listener the listener to be removed.
	 * @see org.aswing.event.InteractiveEvent#STATE_CHANGED
	 */	
	public function removeSelectionListener(listener:Function):void{
		removeEventListener(InteractiveEvent.SELECTION_CHANGED, listener);
	}
	
    /**
     * Returns the path to the currently selected menu item
     *
     * @return an array of MenuElement objects representing the selected path
     */
    public function getSelectedPath():Array { //MenuElement[]
        return selection.toArray();
    }

    /**
     * Tell the menu selection to close and unselect all the menu components. Call this method
     * when a choice has been made.
     * @param programmatic indicate if this is a programmatic change.
     */
    public function clearSelectedPath(programmatic:Boolean):void {
        if (selection.size() > 0) {
            setSelectedPath(null, null, true);
        }
    }
    
    /** 
     * Return true if c is part of the currently used menu
     */
    public function isComponentPartOfCurrentMenu(c:Component):Boolean {
        if(selection.size() > 0) {
            var me:MenuElement = MenuElement(selection.get(0));
            return isComponentPartOfMenu(me, c);
        }else{
            return false;
        }
    }
    
    public function isNavigatingKey(code:uint):Boolean{
    	return isPageNavKey(code) || isItemNavKey(code);
    }
    public function isPageNavKey(code:uint):Boolean{
    	return isPrevPageKey(code) || isNextPageKey(code);
    }
    public function isItemNavKey(code:uint):Boolean{
    	return isPrevItemKey(code) || isNextItemKey(code);
    }
    public function isPrevPageKey(code:uint):Boolean{
    	return code == Keyboard.LEFT;
    }
    public function isPrevItemKey(code:uint):Boolean{
    	return code == Keyboard.UP;
    }
    public function isNextPageKey(code:uint):Boolean{
    	return code == Keyboard.RIGHT;
    }
    public function isNextItemKey(code:uint):Boolean{
    	return code == Keyboard.DOWN;
    }
    public function isEnterKey(code:uint):Boolean{
    	return code == Keyboard.ENTER;
    }
    public function isEscKey(code:uint):Boolean{
    	return code == Keyboard.TAB || code == Keyboard.ESCAPE;
    }
    
    public function nextSubElement(parent:MenuElement, sub:MenuElement):MenuElement{
    	return besideSubElement(parent, sub, 1);
    }
    
    public function prevSubElement(parent:MenuElement, sub:MenuElement):MenuElement{
    	return besideSubElement(parent, sub, -1);
    }
    
    protected function besideSubElement(parent:MenuElement, sub:MenuElement, dir:Number):MenuElement{
    	if(parent == null || sub == null){
    		return null;
    	}
    	var subs:Array = parent.getSubElements();
    	var index:int = ArrayUtils.indexInArray(subs, sub);
    	if(index < 0){
    		return null;
    	}
    	index += dir;
    	if(index >= subs.length){
    		index = 0;
    	}else if(index < 0){
    		index = subs.length - 1;
    	}
    	return MenuElement(subs[index]);
    }

    protected function isComponentPartOfMenu(root:MenuElement, c:Component):Boolean {
        var children:Array;
        var i:Number;
        var d:Number;
	
		if (root == null){
		    return false;
		}
	
        if(root.getMenuComponent() == c){
            return true;
        }else {
            children = root.getSubElements();
            for(i=0,d=children.length; i<d; i++) {
            	var me:MenuElement = MenuElement(children[i]);
                if(me != null && isComponentPartOfMenu(me, c)){
                    return true;
                }
            }
        }
        return false;
	}
	
	protected function fireSelectionChanged(programmatic:Boolean):void{
		dispatchEvent(new InteractiveEvent(InteractiveEvent.SELECTION_CHANGED, programmatic));
	}
	
	protected function __onMSMKeyDown(e:KeyboardEvent):void{
		if(selection.size() == 0){
			return;
		}
		var code:uint = e.keyCode;
		if(isEscKey(code)){
			setSelectedPath(null, null, true);
			return;
		}
		var element:MenuElement = MenuElement(selection.last());
		element.processKeyEvent(code);
	}
	
}
}