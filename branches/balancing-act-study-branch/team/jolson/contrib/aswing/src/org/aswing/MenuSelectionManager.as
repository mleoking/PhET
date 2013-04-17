/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.Component;
import org.aswing.EventDispatcher;
import org.aswing.MenuElement;
import org.aswing.util.ArrayUtils;
import org.aswing.util.Delegate;
import org.aswing.util.Vector;

/**
 * A MenuSelectionManager owns the selection in menu hierarchy.
 * 
 * @author iiley
 */
class org.aswing.MenuSelectionManager extends EventDispatcher{
	
	/**
	 * When the state changed.
	 *<br>
	 * onStateChanged(source:MenuSelectionManager)
	 */	
	public static var ON_STATE_CHANGED:String = "onStateChanged";
 	
	private static var instance:MenuSelectionManager;
	
	private var selection:Vector;
	private var keyListener:Object;
	private var keyListenerWorking:Boolean;
	
	private function MenuSelectionManager(){
		selection = new Vector();
		keyListener = new Object();
		keyListener.onKeyDown = Delegate.create(this, __onMSMKeyDown);
		keyListenerWorking = false;
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
	public static function setDefaultManager(m:MenuSelectionManager):Void{
		instance = m;
	}
	
    /**
     * Changes the selection in the menu hierarchy.  The elements
     * in the array are sorted in order from the root menu
     * element to the currently selected menu element.
     * <p>
     * Note that this method is public but is used by the look and
     * feel engine and should not be called by client applications.
     *
     * @param path  an array of <code>MenuElement</code> objects specifying
     *        the selected path
     */
    public function setSelectedPath(path:Array):Void { //MenuElement[] 
        var i:Number;
        var c:Number;
        var currentSelectionCount:Number = selection.size();
        var firstDifference:Number = 0;
				
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
			fireStateChanged();
		}
		if(selection.size() == 0){
			if(keyListenerWorking){
				Key.removeListener(keyListener);
				keyListenerWorking = false;
			}
		}else{
			if(!keyListenerWorking){
				Key.addListener(keyListener);
				keyListenerWorking = true;
			}
		}
    }
    
	public function addChangeListener(func:Function, obj:Object):Object{
		return addEventListener(ON_STATE_CHANGED, func, obj);
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
     * when a choice has been made
     */
    public function clearSelectedPath():Void {
        if (selection.size() > 0) {
            setSelectedPath(null);
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
    
    public function isNavigatingKey(code:Number):Boolean{
    	return isPageNavKey(code) || isItemNavKey(code);
    }
    public function isPageNavKey(code:Number):Boolean{
    	return isPrevPageKey(code) || isNextPageKey(code);
    }
    public function isItemNavKey(code:Number):Boolean{
    	return isPrevItemKey(code) || isNextItemKey(code);
    }
    public function isPrevPageKey(code:Number):Boolean{
    	return code == Key.LEFT;
    }
    public function isPrevItemKey(code:Number):Boolean{
    	return code == Key.UP;
    }
    public function isNextPageKey(code:Number):Boolean{
    	return code == Key.RIGHT;
    }
    public function isNextItemKey(code:Number):Boolean{
    	return code == Key.DOWN;
    }
    public function isEnterKey(code:Number):Boolean{
    	return code == Key.ENTER;
    }
    public function isEscKey(code:Number):Boolean{
    	return code == Key.TAB || code == Key.ESCAPE;
    }
    
    public function nextSubElement(parent:MenuElement, sub:MenuElement):MenuElement{
    	return besideSubElement(parent, sub, 1);
    }
    
    public function prevSubElement(parent:MenuElement, sub:MenuElement):MenuElement{
    	return besideSubElement(parent, sub, -1);
    }
    
    private function besideSubElement(parent:MenuElement, sub:MenuElement, dir:Number):MenuElement{
    	if(parent == null || sub == null){
    		return null;
    	}
    	var subs:Array = parent.getSubElements();
    	var index:Number = ArrayUtils.indexInArray(subs, sub);
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

    private function isComponentPartOfMenu(root:MenuElement, c:Component):Boolean {
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
	
	private function fireStateChanged():Void{
		dispatchEvent(createEventObj(ON_STATE_CHANGED));
	}
	
	private function __onMSMKeyDown():Void{
		if(selection.size() == 0){
			return;
		}
		var code:Number = Key.getCode();
		if(isEscKey(code)){
			setSelectedPath(null);
			return;
		}
		var element:MenuElement = MenuElement(selection.last());
		element.processKeyEvent(Key.getCode());
	}
}