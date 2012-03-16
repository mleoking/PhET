/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.geom.Point;
import org.aswing.Icon;
import org.aswing.overflow.JMenuItem;
import org.aswing.overflow.JPopupMenu;
import org.aswing.MenuElement;
import org.aswing.plaf.MenuItemUI;
import org.aswing.UIManager;
import org.aswing.util.Delegate;

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
 * @author iiley
 */
class org.aswing.overflow.JMenu extends JMenuItem {
	
	/*
	 * The popup menu portion of the menu.
	 */
	private var popupMenu:JPopupMenu;
	
	private var delay:Number;
	
	private var menuLis:Object;
	
	public function JMenu(text, icon : Icon) {
		super(text, icon);
		setName("JMenu");
		delay = 200;
		menuInUse = false;
		menuLis = new Object();
		menuLis[ON_DESTROY] = Delegate.create(this, __menuDestroied);
		menuLis[ON_CREATED] = Delegate.create(this, __menuCreated);
		addEventListener(menuLis);
	}

	public function updateUI():Void{
		setUI(MenuItemUI(UIManager.getUI(this)));
		if(popupMenu != null){
			popupMenu.updateUI();
		}
	}
	
	public function setUI(newUI:MenuItemUI):Void{
		super.setUI(newUI);
	}
	
	public function getUIClassID():String{
		return "MenuUI";
	}
	
	public function getDefaultBasicUIClass():Function{
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
        if (!(getParent() instanceof JPopupMenu)){
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
        var ncomponents:Number = getComponentCount();
        for (var i:Number = 0 ; i < ncomponents ; i++) {
            var comp:Component = getComponent(i);
            if (comp == c){
                return true;
            }
            // Recursive call for the Menu case
            if (comp instanceof JMenu) {
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
	public function append(c:Component):Void{
		getPopupMenu().append(c, undefined);
	}
	
	/**
	 * Inserts a component(generally JMenuItem or JSeparator) to this menu.
	 */
	public function insert(i:Number, c:Component):Void{
		getPopupMenu().insert(i, c, undefined);
	}
	
    /**
     * Returns the number of components on the menu.
     *
     * @return an integer containing the number of components on the menu
     */
    public function getComponentCount():Number {
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
    public function getComponent(index:Number):Component{
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
    public function removeAt(i:Number):Component{
		if (popupMenu != null){
			return popupMenu.removeAt(i);
		}
		return null;
    }
    
    /**
	 * Remove all components in the menu.
	 */
    public function removeAll():Void{
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
	public function getDelay():Number {
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
	public function setDelay(d:Number):Void {
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
	public function setPopupMenuVisible(b:Boolean):Void {
		var isVisible:Boolean = isPopupMenuVisible();
		if (b != isVisible && (isEnabled() || !b)) {
			ensurePopupMenuCreated();
			if ((b==true) && isShowing()) {
				// Set location of popupMenu (pulldown or pullright)
		 		var p:Point = getPopupMenuOrigin();
				getPopupMenu().show(this, p.x, p.y);
			} else {
				getPopupMenu().setVisible(false);
			}
		}
	}
	private function ensurePopupMenuCreated() : Void {
        if (popupMenu == null) {
            popupMenu = new JPopupMenu();
            popupMenu.setInvoker(this);
        }
	}

	private function getPopupMenuOrigin() : Point {
		var p:Point;
		if(getParent() instanceof JPopupMenu){
			p = new Point(getWidth(), 0);
			p.x += getUIPropertyNumber("Menu.submenuPopupOffsetX");
			p.y += getUIPropertyNumber("Menu.submenuPopupOffsetY");
		}else{
			p = new Point(0, getHeight());
			p.x += getUIPropertyNumber("Menu.menuPopupOffsetX");
			p.y += getUIPropertyNumber("Menu.menuPopupOffsetY");
		}
		return p;
	}
	
	private function getUIPropertyNumber(name:String):Number{
		var n:Number = UIManager.getNumber(name);
		if(n == null){
			n = 0;
		}
		return n;
	}
	
	private function __menuDestroied():Void{
		if(popupMenu != null && popupMenu.isVisible()){
			popupMenu.dispose();
		}
	}
	
	private function __menuCreated():Void{
	}
	
	//--------------------------------
	
    public function setInUse(b:Boolean):Void{
    	if(menuInUse != b){
	    	menuInUse = b;
	    	if(b){
	    		ensurePopupMenuCreated();
	    	}
	    	var subs:Array = getSubElements();
	    	for(var i:Number=0; i<subs.length; i++){
	    		var ele:MenuElement = MenuElement(subs[i]);
	    		ele.setInUse(b);
	    	}
	    	inUseChanged();
    	}
    }
    	
	public function menuSelectionChanged(isIncluded : Boolean) : Void {
		setSelected(isIncluded);
	}

	public function getSubElements() : Array {
        if(popupMenu == null){
            return [];
        }else{
            return [popupMenu];
        }
	}

	public function getMenuComponent() : Component {
		return this;
	}
}