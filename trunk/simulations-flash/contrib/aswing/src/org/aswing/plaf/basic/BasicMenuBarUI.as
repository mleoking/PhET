/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.overflow.JMenu;
import org.aswing.overflow.JMenuBar;
import org.aswing.LayoutManager;
import org.aswing.LookAndFeel;
import org.aswing.MenuElement;
import org.aswing.MenuSelectionManager;
import org.aswing.plaf.basic.DefaultMenuLayout;
import org.aswing.plaf.MenuBarUI;
import org.aswing.plaf.UIResource;
import org.aswing.util.Delegate;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.BasicMenuBarUI extends MenuBarUI {
	
	private var menuBar:JMenuBar;
	
	private var menuSelectionLis:Object;
	private var menuBarLis:Object;
	
	public function BasicMenuBarUI() {
		super();
	}

    public function installUI(c:Component):Void {
        menuBar = JMenuBar(c);
        installDefaults();
        installListeners();
    }

    public function uninstallUI(c:Component):Void {
        menuBar = JMenuBar(c);
        uninstallDefaults();
        uninstallListeners();
    }

    private function installDefaults():Void {
    	var pp:String = "MenuBar.";
        LookAndFeel.installColorsAndFont(menuBar, pp + "background", pp + "foreground", pp + "font");
        LookAndFeel.installBorder(menuBar, pp+"border");
        LookAndFeel.installBasicProperties(menuBar, pp);
        var layout:LayoutManager = menuBar.getLayout();
        if(layout == null || layout instanceof UIResource){
        	menuBar.setLayout(new DefaultMenuLayout(DefaultMenuLayout.X_AXIS));
        }
    }
    
    private function installListeners():Void{
    	menuSelectionLis = new Object();
    	menuSelectionLis[JMenu.ON_SELECTION_CHANGED] = Delegate.create(this, __menuSelectionChanged);
    	
    	for(var i:Number=0; i<menuBar.getComponentCount(); i++){
    		var menu:JMenu = menuBar.getMenu(i);
    		if(menu != null){
    			menu.addEventListener(menuSelectionLis);
    		}
    	}
    	
    	menuBarLis = new Object();
    	menuBarLis[JMenuBar.ON_COM_ADDED] = Delegate.create(this, __childAdded);
    	menuBarLis[JMenuBar.ON_COM_REMOVED] = Delegate.create(this, __childRemoved);
    	menuBarLis[JMenuBar.ON_FOCUS_GAINED] = Delegate.create(this, __barFocusGained);
    	menuBarLis[JMenuBar.ON_KEY_DOWN] = Delegate.create(this, __barKeyDown);
    	menuBar.addEventListener(menuBarLis);
    }

    private function uninstallDefaults():Void {
        LookAndFeel.uninstallBorder(menuBar);
    }
    
    private function uninstallListeners():Void{
    	menuBar.removeEventListener(menuBarLis);
    	for(var i:Number=0; i<menuBar.getComponentCount(); i++){
    		var menu:JMenu = menuBar.getMenu(i);
    		if(menu != null){
    			menu.removeEventListener(menuSelectionLis);
    		}
    	}
    	menuBarLis = null;
    	menuSelectionLis = null;
    }
    
    //-----------------

	/**
	 * Subclass override this to process key event.
	 */
	public function processKeyEvent(code : Number) : Void {
		var manager:MenuSelectionManager = MenuSelectionManager.defaultManager();
		if(manager.isNavigatingKey(code)){
			var subs:Array = menuBar.getSubElements();
			var path:Array = [menuBar];
			if(subs.length > 0){
				if(manager.isNextItemKey(code) || manager.isNextPageKey(code)){
					path.push(subs[0]);
				}else{//left
					path.push(subs[subs.length-1]);
				}
				var smu:MenuElement = MenuElement(path[1]);
				if(smu.getSubElements().length > 0){
					path.push(smu.getSubElements()[0]);
				}
				manager.setSelectedPath(path);
			}
		}
	}
	
	private function __barKeyDown():Void{
		if(MenuSelectionManager.defaultManager().getSelectedPath().length == 0){
			processKeyEvent(Key.getCode());
		}
	}
    
    private function __menuSelectionChanged():Void{
    	for(var i:Number=0; i<menuBar.getComponentCount(); i++){
    		var menu:JMenu = menuBar.getMenu(i);
    		if(menu != null && menu.isSelected()){
    			menuBar.getSelectionModel().setSelectedIndex(i);
    			break;
    		}
    	}
    }
    
    private function __barFocusGained():Void{
    	MenuSelectionManager.defaultManager().setSelectedPath([menuBar]);
    }
    
    private function __childAdded(source:Component, child:Component):Void{
    	if(child instanceof JMenu){
    		child.addEventListener(menuSelectionLis);
    	}
    }
    
    private function __childRemoved(source:Component, child:Component):Void{
    	if(child instanceof JMenu){
    		child.removeEventListener(menuSelectionLis);
    	}
    }
}