/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

import org.aswing.plaf.*;
import org.aswing.plaf.basic.BasicMenuUI;
import org.aswing.geom.*;
import flash.events.Event;

/**
 * An implementation of a menu -- a popup window containing
 * <code>JMenuItem</code>s that
 * is displayed when the user selects an item on the <code>JMenuBar</code>.
 * In addition to <code>JMenuItem</code>s, a <code>JMenu</code> can
 * also contain <code>JSeparator</code>s. 
 * <p>
 * In essence, a menu is a button with an associated <code>JPopupMenu</code>.
 * When the "button" is pressed, the <code>JPopupMenu</code> appears. If the
 * "button" is on the <code>JMenuBar</code>, the menu is a top-level window.
 * If the "button" is another menu item, then the <code>JPopupMenu</code> is
 * "pull-right" menu.
 * </p>
 * @author iiley
 */
public class JMenu extends JMenuItem implements MenuElement{
	
	/*
	 * The popup menu portion of the menu.
	 */
	protected var popupMenu:JPopupMenu;
	
	protected var delay:int;
		
	public function JMenu(text:String="", icon:Icon=null){
		super(text, icon);
		setName("JMenu");
		delay = 200;
		menuInUse = false;
		addEventListener(Event.REMOVED_FROM_STAGE, __menuDestroied);
	}

	override public function updateUI():void{
		setUI(UIManager.getUI(this));
		if(popupMenu != null){
			popupMenu.updateUI();
		}
	}
	
	override public function getUIClassID():String{
		return "MenuUI";
	}
	
	override public function getDefaultBasicUIClass():Class{
    	return org.aswing.plaf.basic.BasicMenuUI;
    }
	
    /**
     * Returns true if the menu is a 'top-level menu', that is, if it is
     * the direct child of a menubar.
     *
     * @return true if the menu is activated from the menu bar;
     *         false if the menu is activated from a menu item
     *         on another menu
     */	
	public function isTopLevelMenu():Boolean{
        if (!(getParent() is JPopupMenu)){
            return true;
        }
        return false;
	}
	

    /**
     * Returns true if the specified component exists in the 
     * submenu hierarchy.
     *
     * @param c the <code>Component</code> to be tested
     * @return true if the <code>Component</code> exists, false otherwise
     */
    public function isMenuComponent(c:Component):Boolean {
    	if(c == null){
    		return false;
    	}
        if (c == this){
            return true;
        }
        if (c == popupMenu) {
        	return true;
        }
        var ncomponents:int = getComponentCount();
        for (var i:int = 0 ; i < ncomponents ; i++) {
            var comp:Component = getComponent(i);
            if (comp == c){
                return true;
            }
            // Recursive call for the Menu case
            if (comp is JMenu) {
                var subMenu:JMenu = JMenu(comp);
                if (subMenu.isMenuComponent(c)){
                    return true;
                }
            }
        }
        return false;
    }	
	
	/**
	 * Returns the popupMenu for the Menu
	 */
	public function getPopupMenu():JPopupMenu{
		ensurePopupMenuCreated();
		return popupMenu;
	}
	
	/**
	 * Creates a new menu item with the specified text and appends
	 * it to the end of this menu.
	 *  
	 * @param s the string for the menu item to be added
	 */
	public function addMenuItem(s:String):JMenuItem {
		var mi:JMenuItem = new JMenuItem(s);
		append(mi);
		return mi;
	}
	
	/**
	 * Adds a component(generally JMenuItem or JSeparator) to this menu.
	 */
	public function append(c:Component):void{
		getPopupMenu().append(c);
	}
	
	/**
	 * Inserts a component(generally JMenuItem or JSeparator) to this menu.
	 */
	public function insert(i:int, c:Component):void{
		getPopupMenu().insert(i, c);
	}
	
    /**
     * Returns the number of components on the menu.
     *
     * @return an integer containing the number of components on the menu
     */
    public function getComponentCount():int {
        if (popupMenu != null){
            return popupMenu.getComponentCount();
        }else{
			return 0;
        }
    }
    
    /**
     * Returns the component at position <code>index</code>.
     *
     * @param n the position of the component to be returned
     * @return the component requested, or <code>null</code>
     *			if there is no popup menu or no component at the position.
     *
     */
    public function getComponent(index:int):Component{
        if (popupMenu != null){
            return popupMenu.getComponent(index);
        }else{
			return null;
        }
    }
    
    /**
	 * Remove the specified component.
	 * @return the component just removed, null if the component is not in this menu.
	 */
    public function remove(c:Component):Component{
		if (popupMenu != null){
			return popupMenu.remove(c);
		}
		return null;
    }
    
	/**
	 * Remove the specified index component.
	 * @param i the index of component.
	 * @return the component just removed. or null there is not component at this position.
	 */	
    public function removeAt(i:int):Component{
		if (popupMenu != null){
			return popupMenu.removeAt(i);
		}
		return null;
    }
    
    /**
	 * Remove all components in the menu.
	 */
    public function removeAll():void{
		if (popupMenu != null){
			popupMenu.removeAll();
		}
    }
    
	/**
	 * Returns the suggested delay, in milliseconds, before submenus
	 * are popped up or down.  
	 * Each look and feel (L&F) may determine its own policy for
	 * observing the <code>delay</code> property.
	 * In most cases, the delay is not observed for top level menus
	 * or while dragging.  The default for <code>delay</code> is 0.
	 * This method is a property of the look and feel code and is used
	 * to manage the idiosyncracies of the various UI implementations.
	 * 
	 * @return the <code>delay</code> property
	 */
	public function getDelay():int {
		return delay;
	}
	
	/**
	 * Sets the suggested delay before the menu's <code>PopupMenu</code>
	 * is popped up or down.  Each look and feel (L&F) may determine
	 * it's own policy for observing the delay property.  In most cases,
	 * the delay is not observed for top level menus or while dragging.
	 * This method is a property of the look and feel code and is used
	 * to manage the idiosyncracies of the various UI implementations.
	 *
	 * @param d the number of milliseconds to delay
	 */
	public function setDelay(d:int):void {
		if (d < 0){
			trace("/e/Delay must be a positive integer, ignored.");
			return;
		}
		delay = d;
	}
			
	/**
	 * Returns true if the menu's popup window is visible.
	 *
	 * @return true if the menu is visible, else false
	 */
	public function isPopupMenuVisible():Boolean {
		return popupMenu != null && popupMenu.isVisible();
	}

	/**
	 * Sets the visibility of the menu's popup.  If the menu is
	 * not enabled, this method will have no effect.
	 *
	 * @param b  a boolean value -- true to make the menu visible,
	 *		   false to hide it
	 */
	public function setPopupMenuVisible(b:Boolean):void {
		var isVisible:Boolean = isPopupMenuVisible();
		if (b != isVisible && (isEnabled() || !b)) {
			ensurePopupMenuCreated();
			if ((b==true) && isShowing()) {
				// Set location of popupMenu (pulldown or pullright)
		 		var p:IntPoint = getPopupMenuOrigin();
				getPopupMenu().show(this, p.x, p.y);
			} else {
				getPopupMenu().setVisible(false);
			}
		}
	}
	private function ensurePopupMenuCreated() : void {
        if (popupMenu == null) {
            popupMenu = new JPopupMenu();
            popupMenu.setInvoker(this);
        }
	}

	private function getPopupMenuOrigin() : IntPoint {
		var p:IntPoint;
		if(getParent() is JPopupMenu){
			p = new IntPoint(getWidth(), 0);
			var ofx:int = getUIPropertyNumber("Menu.submenuPopupOffsetX");
			var ofy:int = getUIPropertyNumber("Menu.submenuPopupOffsetY");
			p.x += ofx;
			p.y += ofy;
			if(stage){
				var rect:IntRectangle = AsWingUtils.getVisibleMaximizedBounds(this);
				var popupSize:IntDimension = getPopupMenu().getPreferredSize();
				if(p.x + popupSize.width > rect.x + rect.width){
					p.x = -ofx - popupSize.width;
				}
				if(p.y + popupSize.height > rect.y + rect.height){
					p.y = -ofy - popupSize.height + getHeight();
				}
			}
		}else{
			p = new IntPoint(0, getHeight());
			p.x += getUIPropertyNumber("Menu.menuPopupOffsetX");
			p.y += getUIPropertyNumber("Menu.menuPopupOffsetY");
		}
		return p;
	}
	
	private function getUIPropertyNumber(name:String):int{
		var n:int = getUI().getInt(name);
		return n;
	}
	
	private function __menuDestroied(e:Event):void{
		if(popupMenu != null && popupMenu.isVisible()){
			popupMenu.dispose();
		}
	}
	
	//--------------------------------
	
    override public function setInUse(b:Boolean):void{
    	if(menuInUse != b){
	    	menuInUse = b;
	    	if(b){
	    		ensurePopupMenuCreated();
	    	}
	    	var subs:Array = getSubElements();
	    	for(var i:int=0; i<subs.length; i++){
	    		var ele:MenuElement = MenuElement(subs[i]);
	    		ele.setInUse(b);
	    	}
	    	inUseChanged();
    	}
    }
    	
	override public function menuSelectionChanged(isIncluded : Boolean) : void {
		setSelected(isIncluded);
	}

	override public function getSubElements() : Array {
        if(popupMenu == null){
            return [];
        }else{
            return [popupMenu];
        }
	}

	override public function getMenuComponent() : Component {
		return this;
	}	
}
}