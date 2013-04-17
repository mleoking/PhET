/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.overflow.JPopupMenu;
import org.aswing.LayoutManager;
import org.aswing.LookAndFeel;
import org.aswing.MenuElement;
import org.aswing.MenuSelectionManager;
import org.aswing.plaf.basic.DefaultMenuLayout;
import org.aswing.plaf.PopupMenuUI;
import org.aswing.plaf.UIResource;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.BasicPopupMenuUI extends PopupMenuUI {
	
	private var popupMenu:JPopupMenu;
	
	public function BasicPopupMenuUI() {
		super();
	}
	
    public function installUI(c:Component):Void {
        popupMenu = JPopupMenu(c);
        installDefaults();
        installListeners();
    }

    public function uninstallUI(c:Component):Void {
        popupMenu = JPopupMenu(c);
        uninstallDefaults();
        uninstallListeners();
    }

    private function installDefaults():Void {
    	var pp:String = "PopupMenu.";
        LookAndFeel.installColorsAndFont(popupMenu, pp + "background", pp + "foreground", pp + "font");
        LookAndFeel.installBorder(popupMenu, pp+"border");
        LookAndFeel.installBasicProperties(popupMenu, pp);
        var layout:LayoutManager = popupMenu.getLayout();
        if(layout == null || layout instanceof UIResource){
        	popupMenu.setLayout(new DefaultMenuLayout(DefaultMenuLayout.Y_AXIS));
        }
    }
    
    private function installListeners():Void{
    }

    private function uninstallDefaults():Void {
        LookAndFeel.uninstallBorder(popupMenu);
    }
    
    private function uninstallListeners():Void{
    }
    
    //-----------------
    
	/**
	 * Subclass override this to process key event.
	 */
	public function processKeyEvent(code : Number) : Void {
		var manager:MenuSelectionManager = MenuSelectionManager.defaultManager();
		var path:Array = manager.getSelectedPath();
		if(path[path.length-1] != popupMenu){
			return;
		}
		if(manager.isPrevPageKey(code)){
			if(path.length > 2){
				path.pop();
			}
			if(path.length == 2 && !(path[0] instanceof JPopupMenu)){ //generally means jmenubar here
				var root:MenuElement = MenuElement(path[0]);
				var prev:MenuElement = manager.prevSubElement(root, MenuElement(path[1]));
				path.pop();
				path.push(prev);
				if(prev.getSubElements().length > 0){
					var prevPop:MenuElement = MenuElement(prev.getSubElements()[0]);
					path.push(prevPop);
					if(prevPop.getSubElements().length > 0){
						path.push(prevPop.getSubElements()[0]);
					}
				}
			}else{
				var subs:Array = popupMenu.getSubElements();
				if(subs.length > 0){
					path.push(subs[subs.length-1]);
				}
			}
			manager.setSelectedPath(path);
		}else if(manager.isNextPageKey(code)){
			var root:MenuElement = MenuElement(path[0]);
			if(root.getSubElements().length > 1 && !(root instanceof JPopupMenu)){
				var next:MenuElement = manager.nextSubElement(root, MenuElement(path[1]));
				path = [root];
				path.push(next);
				if(next.getSubElements().length > 0){
					var nextPop:MenuElement = MenuElement(next.getSubElements()[0]);
					path.push(nextPop);
					if(nextPop.getSubElements().length > 0){
						path.push(nextPop.getSubElements()[0]);
					}
				}
			}else{
				var subs:Array = popupMenu.getSubElements();
				if(subs.length > 0){
					path.push(subs[0]);
				}
			}
			manager.setSelectedPath(path);
		}else if(manager.isNextItemKey(code)){
			var subs:Array = popupMenu.getSubElements();
			if(subs.length > 0){
				if(manager.isPrevItemKey(code)){
					path.push(subs[subs.length-1]);
				}else{
					path.push(subs[0]);
				}
			}
			manager.setSelectedPath(path);
		}
	}       
    
    //-----------------
		
	public static function getFirstPopup():MenuElement {
		var msm:MenuSelectionManager = MenuSelectionManager.defaultManager();
		var p:Array = msm.getSelectedPath();
		var me:MenuElement = null;		
	
		for(var i:Number = 0 ; me == null && i < p.length ; i++) {
			if (p[i] instanceof JPopupMenu){
				me = p[i];
			}
		}
	
		return me;
	}
	
	public static function getLastPopup():MenuElement {
		var msm:MenuSelectionManager = MenuSelectionManager.defaultManager();
		var p:Array = msm.getSelectedPath();
		var me:MenuElement = null;		
	
		for(var i:Number = p.length-1 ; me == null && i >= 0 ; i--) {
			if (p[i] instanceof JPopupMenu){
				me = p[i];
			}
		}
	
		return me;
	}
}