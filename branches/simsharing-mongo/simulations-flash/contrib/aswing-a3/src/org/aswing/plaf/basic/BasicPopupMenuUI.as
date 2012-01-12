package org.aswing.plaf.basic{

import org.aswing.*;
import flash.display.*;
import org.aswing.geom.*;
import org.aswing.plaf.*;

/**
 * @private
 * @author iiley
 */
public class BasicPopupMenuUI extends BaseComponentUI implements MenuElementUI{

	protected var popupMenu:JPopupMenu;
	
	public function BasicPopupMenuUI() {
		super();
	}
	
	override public function installUI(c:Component):void {
		popupMenu = JPopupMenu(c);
		installDefaults();
		installListeners();
	}

	override public function uninstallUI(c:Component):void {
		popupMenu = JPopupMenu(c);
		uninstallDefaults();
		uninstallListeners();
	}
	
	protected function getPropertyPrefix():String {
		return "PopupMenu.";
	}

	protected function installDefaults():void {
		var pp:String = getPropertyPrefix();
        LookAndFeel.installColorsAndFont(popupMenu, pp);
        LookAndFeel.installBorderAndBFDecorators(popupMenu, pp);
        LookAndFeel.installBasicProperties(popupMenu, pp);
		var layout:LayoutManager = popupMenu.getLayout();
		if(layout == null || layout is UIResource){
			popupMenu.setLayout(new DefaultMenuLayout(DefaultMenuLayout.Y_AXIS));
		}
	}
	
	protected function installListeners():void{
	}

	protected function uninstallDefaults():void {
		LookAndFeel.uninstallBorderAndBFDecorators(popupMenu);
	}
	
	protected function uninstallListeners():void{
	}
	
	//-----------------
	
	/**
	 * Subclass override this to process key event.
	 */
	public function processKeyEvent(code : uint) : void {
		var manager:MenuSelectionManager = MenuSelectionManager.defaultManager();
		var path:Array = manager.getSelectedPath();
		if(path[path.length-1] != popupMenu){
			return;
		}
		var root:MenuElement;
		var prev:MenuElement;
		var subs:Array;
		if(manager.isPrevPageKey(code)){
			if(path.length > 2){
				path.pop();
			}
			if(path.length == 2 && !(path[0] is JPopupMenu)){ //generally means jmenubar here
				root = MenuElement(path[0]);
				prev = manager.prevSubElement(root, MenuElement(path[1]));
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
				subs = popupMenu.getSubElements();
				if(subs.length > 0){
					path.push(subs[subs.length-1]);
				}
			}
			manager.setSelectedPath(popupMenu.stage, path, false);
		}else if(manager.isNextPageKey(code)){
			root = MenuElement(path[0]);
			if(root.getSubElements().length > 1 && !(root is JPopupMenu)){
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
				subs = popupMenu.getSubElements();
				if(subs.length > 0){
					path.push(subs[0]);
				}
			}
			manager.setSelectedPath(popupMenu.stage, path, false);
		}else if(manager.isNextItemKey(code)){
			subs = popupMenu.getSubElements();
			if(subs.length > 0){
				if(manager.isPrevItemKey(code)){
					path.push(subs[subs.length-1]);
				}else{
					path.push(subs[0]);
				}
			}
			manager.setSelectedPath(popupMenu.stage, path, false);
		}
	}	   
	
	//-----------------
		
	public static function getFirstPopup():MenuElement {
		var msm:MenuSelectionManager = MenuSelectionManager.defaultManager();
		var p:Array = msm.getSelectedPath();
		var me:MenuElement = null;		
	
		for(var i:Number = 0 ; me == null && i < p.length ; i++) {
			if (p[i] is JPopupMenu){
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
			if (p[i] is JPopupMenu){
				me = p[i];
			}
		}
	
		return me;
	}
	
}
}