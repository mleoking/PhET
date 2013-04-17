/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic{

import org.aswing.plaf.*;
import org.aswing.*;
import org.aswing.event.ContainerEvent;
import org.aswing.event.AWEvent;
import org.aswing.event.FocusKeyEvent;
import org.aswing.event.InteractiveEvent;

/**
 * @private
 */
public class BasicMenuBarUI extends BaseComponentUI implements MenuElementUI{
	
	protected var menuBar:JMenuBar;
	
	public function BasicMenuBarUI() {
		super();
	}

	override public function installUI(c:Component):void {
		menuBar = JMenuBar(c);
		installDefaults();
		installListeners();
	}

	override public function uninstallUI(c:Component):void {
		menuBar = JMenuBar(c);
		uninstallDefaults();
		uninstallListeners();
	}
	
	protected function getPropertyPrefix():String {
		return "MenuBar.";
	}

	protected function installDefaults():void {
		var pp:String = getPropertyPrefix();
		LookAndFeel.installColorsAndFont(menuBar, pp);
		LookAndFeel.installBorderAndBFDecorators(menuBar, pp);
		LookAndFeel.installBasicProperties(menuBar, pp);
		var layout:LayoutManager = menuBar.getLayout();
		if(layout == null || layout is UIResource){
			menuBar.setLayout(new DefaultMenuLayout(DefaultMenuLayout.X_AXIS));
		}
	}
	
	protected function installListeners():void{
		for(var i:int=0; i<menuBar.getComponentCount(); i++){
			var menu:JMenu = menuBar.getMenu(i);
			if(menu != null){
				menu.addSelectionListener(__menuSelectionChanged);
			}
		}
		
		menuBar.addEventListener(ContainerEvent.COM_ADDED, __childAdded);
		menuBar.addEventListener(ContainerEvent.COM_REMOVED, __childRemoved);
		menuBar.addEventListener(AWEvent.FOCUS_GAINED, __barFocusGained);
		menuBar.addEventListener(FocusKeyEvent.FOCUS_KEY_DOWN, __barKeyDown);
	}

	protected function uninstallDefaults():void {
		LookAndFeel.uninstallBorderAndBFDecorators(menuBar);
	}
	
	protected function uninstallListeners():void{
		for(var i:int=0; i<menuBar.getComponentCount(); i++){
			var menu:JMenu = menuBar.getMenu(i);
			if(menu != null){
				menu.removeSelectionListener(__menuSelectionChanged);
			}
		}
		
		menuBar.removeEventListener(ContainerEvent.COM_ADDED, __childAdded);
		menuBar.removeEventListener(ContainerEvent.COM_REMOVED, __childRemoved);
		menuBar.removeEventListener(AWEvent.FOCUS_GAINED, __barFocusGained);
		menuBar.removeEventListener(FocusKeyEvent.FOCUS_KEY_DOWN, __barKeyDown);
	}
	
	//-----------------

	/**
	 * Subclass override this to process key event.
	 */
	public function processKeyEvent(code : uint) : void {
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
				manager.setSelectedPath(menuBar.stage, path, false);
			}
		}
	}
	
	protected function __barKeyDown(e:FocusKeyEvent):void{
		if(MenuSelectionManager.defaultManager().getSelectedPath().length == 0){
			processKeyEvent(e.keyCode);
		}
	}
	
	protected function __menuSelectionChanged(e:InteractiveEvent):void{
		for(var i:int=0; i<menuBar.getComponentCount(); i++){
			var menu:JMenu = menuBar.getMenu(i);
			if(menu != null && menu.isSelected()){
				menuBar.getSelectionModel().setSelectedIndex(i, e.isProgrammatic());
				break;
			}
		}
	}
	
	protected function __barFocusGained(e:AWEvent):void{
		MenuSelectionManager.defaultManager().setSelectedPath(menuBar.stage, [menuBar], false);
	}
	
	protected function __childAdded(e:ContainerEvent):void{
		if(e.getChild() is JMenu){
			JMenu(e.getChild()).addSelectionListener(__menuSelectionChanged);
		}
	}
	
	protected function __childRemoved(e:ContainerEvent):void{
		if(e.getChild() is JMenu){
			JMenu(e.getChild()).removeSelectionListener(__menuSelectionChanged);
		}
	}
}
}