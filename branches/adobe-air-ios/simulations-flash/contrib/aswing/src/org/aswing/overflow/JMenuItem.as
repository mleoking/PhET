/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.AbstractButton;
import org.aswing.Component;
import org.aswing.DefaultButtonModel;
import org.aswing.Icon;
import org.aswing.overflow.JPopupMenu;
import org.aswing.JWindow;
import org.aswing.KeyType;
import org.aswing.KeyMap;
import org.aswing.MenuElement;
import org.aswing.plaf.MenuItemUI;
import org.aswing.UIManager;

/**
 * An implementation of an item in a menu. A menu item is essentially a button
 * sitting in a list. When the user selects the "button", the action
 * associated with the menu item is performed. A <code>JMenuItem</code>
 * contained in a <code>JPopupMenu</code> performs exactly that function.
 * 
 * @author iiley
 */
class org.aswing.overflow.JMenuItem extends AbstractButton implements MenuElement{
	
	private var menuInUse:Boolean;
	private var accelerator:KeyType;
	
	public function JMenuItem(text, icon : Icon) {
		super(text, icon);
		setName("JMenuItem");
		setModel(new DefaultButtonModel());
		initFocusability();
		menuInUse = false;
		accelerator = null;
		
		updateUI();
	}
	
	public function updateUI():Void{
		setUI(MenuItemUI(UIManager.getUI(this)));
	}
	
	public function setUI(newUI:MenuItemUI):Void{
		super.setUI(newUI);
	}
	
	public function getUIClassID():String{
		return "MenuItemUI";
	}
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.basic.BasicMenuItemUI;
    }
	
	public function getUI():MenuItemUI{
		return MenuItemUI(ui);
	}
	
	public function setAccelerator(acc:KeyType):Void{
		if(accelerator != acc){
			accelerator = acc;
			revalidate();
			repaint();
		}
	}
	
	public function getAccelerator():KeyType{
		return accelerator;
	}
	
	/**
	 * Inititalizes the focusability of the the <code>JMenuItem</code>.
	 * <code>JMenuItem</code>'s are focusable, but subclasses may
	 * want to be, this provides them the opportunity to override this
	 * and invoke something else, or nothing at all.
	 */
	private function initFocusability():Void {
		setFocusable(false);
	}
	
	/**
	 * Returns the window that owned this menu.
	 * @return window that owned this menu, or null no window owned this menu yet.
	 */
	public function getWindowOwner():JWindow{
		var pp:Component = this;
		do{
			pp = pp.getParent();
			if(pp instanceof JPopupMenu){
				pp = JPopupMenu(pp).getInvoker();
			}
			if(pp instanceof JWindow){
				return JWindow(pp);
			}
		}while(pp != null);
		return null;
	}
	
	private function inUseChanged():Void{
		var acc:KeyType = getAccelerator();
		if(acc != null){
			var keyMap:KeyMap = getWindowOwner().getKeyMap();
			if(keyMap != null){
				if(isInUse()){
					keyMap.registerKeyAction(acc, __acceleratorAction, this);
				}else{
					keyMap.unregisterKeyAction(acc);
				}
			}
		}
	}
	
	private function __acceleratorAction():Void{
		click();
	}
	
	//--------------------------------
		
    public function setInUse(b:Boolean):Void{
    	if(menuInUse != b){
	    	menuInUse = b;
	    	inUseChanged();
    	}
    }
    
    public function isInUse():Boolean{
    	return menuInUse;
    }
    
	public function menuSelectionChanged(isIncluded : Boolean) : Void {
		getModel().setRollOver(isIncluded);
	}

	public function getSubElements() : Array {
		return [];
	}

	public function getMenuComponent() : Component {
		return this;
	}

	public function processKeyEvent(code : Number) : Void {
		getUI().processKeyEvent(code);
	}

}