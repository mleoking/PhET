/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

import org.aswing.plaf.*;
import org.aswing.plaf.basic.BasicMenuItemUI;

/**
 * An implementation of an item in a menu. A menu item is essentially a button
 * sitting in a list. When the user selects the "button", the action
 * associated with the menu item is performed. A <code>JMenuItem</code>
 * contained in a <code>JPopupMenu</code> performs exactly that function.
 * 
 * @author iiley
 */
public class JMenuItem extends AbstractButton implements MenuElement{
	
	protected var menuInUse:Boolean;
	protected var accelerator:KeyType;
	
	public function JMenuItem(text:String="", icon:Icon=null){
		super(text, icon);
		setName("JMenuItem");
		setModel(new DefaultButtonModel());
		initFocusability();
		menuInUse = false;
		accelerator = null;
	}
	
	override public function updateUI():void{
		setUI(UIManager.getUI(this));
	}
	
	override public function getDefaultBasicUIClass():Class{
    	return org.aswing.plaf.basic.BasicMenuItemUI;
    }
	
	/**
	 * Sets the ui.
	 * <p>
	 * The ui should implemented <code>MenuElementUI</code> interface!
	 * </p>
	 * @param newUI the newUI
	 * @throws ArgumentError when the newUI is not an <code>MenuElementUI</code> instance.
	 */
    override public function setUI(newUI:ComponentUI):void{
    	if(newUI is MenuElementUI){
    		super.setUI(newUI);
    	}else{
    		throw new ArgumentError("JMenuItem just accept MenuElementUI instance!!!");
    	}
    }
    
    /**
     * Returns the ui for this frame with <code>MenuElementUI</code> instance
     * @return the menu element ui.
     */
    public function getMenuElementUI():MenuElementUI{
    	return getUI() as MenuElementUI;
    }
	
	override public function getUIClassID():String{
		return "MenuItemUI";
	}
	
    /**
     * Sets the key combination which invokes the menu item's
     * action listeners without navigating the menu hierarchy. It is the
     * UI's responsibility to install the correct action.  Note that 
     * when the keyboard accelerator is typed, it will work whether or
     * not the menu is currently displayed.
     *
     * @param keyStroke the <code>KeyType</code> which will
     *		serve as an accelerator 
     */
	public function setAccelerator(acc:KeyType):void{
		if(accelerator != acc){
			accelerator = acc;
			revalidate();
			repaint();
		}
	}
	
    /**
     * Returns the <code>KeyType</code> which serves as an accelerator 
     * for the menu item.
     * @return a <code>KeyType</code> object identifying the
     *		accelerator key
     */	
	public function getAccelerator():KeyType{
		return accelerator;
	}
	
	/**
	 * Inititalizes the focusability of the the <code>JMenuItem</code>.
	 * <code>JMenuItem</code>'s are focusable, but subclasses may
	 * want to be, this provides them the opportunity to override this
	 * and invoke something else, or nothing at all.
	 */
	protected function initFocusability():void {
		setFocusable(false);
	}
	
	/**
	 * Returns the window that owned this menu.
	 * @return window that owned this menu, or null no window owned this menu yet.
	 */
	public function getRootPaneOwner():JRootPane{
		var pp:Component = this;
		do{
			pp = pp.getParent();
			if(pp is JPopupMenu){
				pp = JPopupMenu(pp).getInvoker();
			}
			if(pp is JRootPane){
				return JRootPane(pp);
			}
		}while(pp != null);
		return null;
	}
	
	protected function inUseChanged():void{
		var acc:KeyType = getAccelerator();
		if(acc != null){
			var rOwner:JRootPane = getRootPaneOwner();
			if(rOwner == null){
				throw new Error("The menu item has accelerator, " + 
						"it or it's popupMenu must be in a JRootPane(or it's subclass).");
				return;
			}
			var keyMap:KeyMap = rOwner.getKeyMap();
			if(keyMap != null){
				if(isInUse()){
					keyMap.registerKeyAction(acc, __acceleratorAction);
				}else{
					keyMap.unregisterKeyAction(acc);
				}
			}
		}
	}
	
	protected function __acceleratorAction():void{
		doClick();
	}
	
	//--------------------------------
	
    public function setInUse(b:Boolean):void{
    	if(menuInUse != b){
	    	menuInUse = b;
	    	inUseChanged();
    	}
    }
    
    public function isInUse():Boolean{
    	return menuInUse;
    }
    
	public function menuSelectionChanged(isIncluded : Boolean) : void {
		getModel().setRollOver(isIncluded);
	}
	
	public function getSubElements() : Array {
		return [];
	}
	
	public function getMenuComponent() : Component {
		return this;
	}
	
	public function processKeyEvent(code : uint) : void {
		getMenuElementUI().processKeyEvent(code);
	}	
}
}