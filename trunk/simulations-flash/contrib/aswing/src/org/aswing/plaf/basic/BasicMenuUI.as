/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Container;
import org.aswing.overflow.JMenu;
import org.aswing.overflow.JMenuBar;
import org.aswing.MenuElement;
import org.aswing.MenuSelectionManager;
import org.aswing.plaf.basic.BasicMenuItemUI;
import org.aswing.plaf.UIResource;
import org.aswing.UIManager;
import org.aswing.util.Delegate;
import org.aswing.util.Timer;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.BasicMenuUI extends BasicMenuItemUI {
	
	private var postTimer:Timer;
	private var menuLis:Object;
	
	public function BasicMenuUI() {
		super();
	}
	
	private function getPropertyPrefix():String {
		return "Menu";
	}

	private function installDefaults():Void {
		super.installDefaults();
		updateDefaultBackgroundColor();
	}	
	
	private function uninstallDefaults():Void {
		menuItem.getModel().setRollOver(false);
		menuItem.setSelected(false);
		super.uninstallDefaults();
	}

	private function installListeners():Void{
		super.installListeners();
		menuLis = new Object();
		menuLis[JMenu.ON_SELECTION_CHANGED] = Delegate.create(this, __menuSelectionChanged);
		menuItem.addEventListener(menuLis);
	}
	
	private function uninstallListeners():Void{
		super.uninstallListeners();
		menuItem.removeEventListener(menuLis);
		menuLis = null;
	}		
	
	private function getMenu():JMenu{
		return JMenu(menuItem);
	}
	
	/*
	 * Set the background color depending on whether this is a toplevel menu
	 * in a menubar or a submenu of another menu.
	 */
	private function updateDefaultBackgroundColor():Void{
		if (!UIManager.getBoolean("Menu.useMenuBarBackgroundForTopLevel")) {
			return;
		}
		var menu:JMenu = getMenu();
		if (menu.getBackground() instanceof UIResource) {
			if (menu.isTopLevelMenu()) {
				menu.setBackground(UIManager.getColor("MenuBar.background"));
			} else {
				menu.setBackground(UIManager.getColor(getPropertyPrefix() + ".background"));
			}
		}
	}
	
	/**
	 * SubUI override this to do different
	 */
	private function isMenu():Boolean{
		return true;
	}
	
	/**
	 * SubUI override this to do different
	 */
	private function isTopMenu():Boolean{
		return getMenu().isTopLevelMenu();
	}
	
	/**
	 * SubUI override this to do different
	 */
	private function shouldPaintSelected():Boolean{
		return menuItem.getModel().isRollOver() || menuItem.isSelected();
	}
	
	//---------------------
	
	public function processKeyEvent(code : Number) : Void {
		var manager:MenuSelectionManager = MenuSelectionManager.defaultManager();
		if(manager.isNextPageKey(code)){
			var path:Array = manager.getSelectedPath();
			if(path[path.length-1] == menuItem){
				var popElement:MenuElement = getMenu().getPopupMenu();
				path.push(popElement);
				if(popElement.getSubElements().length > 0){
					path.push(popElement.getSubElements()[0]);
				}
				manager.setSelectedPath(path);
			}
		}else{
			super.processKeyEvent(code);
		}
	}	
	
	private function __menuSelectionChanged():Void{
		menuItem.repaint();
	}
	
	private function __menuItemRollOver():Void{
		var menu:JMenu = getMenu();
		var manager:MenuSelectionManager = MenuSelectionManager.defaultManager();
		var selectedPath:Array = manager.getSelectedPath();		
		if (!menu.isTopLevelMenu()) {
			if(!(selectedPath.length>0 && selectedPath[selectedPath.length-1]==menu.getPopupMenu())){
				if(menu.getDelay() <= 0) {
					appendPath(getPath(), menu.getPopupMenu());
				} else {
					manager.setSelectedPath(getPath());
					setupPostTimer(menu);
				}
			}
		} else {
			if(selectedPath.length > 0 && selectedPath[0] == menu.getParent()) {
				// A top level menu's parent is by definition a JMenuBar
				manager.setSelectedPath([menu.getParent(), menu, menu.getPopupMenu()]);
			}
		}
		menuItem.repaint();
	}
	
	private function __menuItemRollOut():Void{
		super.__menuItemRollOut();
	}
	
	private function __menuItemAct():Void{
		var menu:JMenu = getMenu();
		var cnt:Container = menu.getParent();
		if(cnt != null && cnt instanceof JMenuBar) {
			var me:Array = [cnt, menu, menu.getPopupMenu()];
			MenuSelectionManager.defaultManager().setSelectedPath(me);
		}
		menuItem.repaint();
	}
	
	private function __postTimerAct():Void{
		var menu:JMenu = getMenu();
		var path:Array = MenuSelectionManager.defaultManager().getSelectedPath();
		if(path.length > 0 && path[path.length-1] == menu) {
			appendPath(path, menu.getPopupMenu());
		}
	}
	
	//---------------------
	private function appendPath(path:Array, end:Object):Void{
		path.push(end);
		MenuSelectionManager.defaultManager().setSelectedPath(path);
	}

	private function setupPostTimer(menu:JMenu):Void {
		if(postTimer == null){
			postTimer = new Timer(menu.getDelay(), false);
			postTimer.addActionListener(__postTimerAct, this);
		}
		postTimer.restart();
	}
}