/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic{

import org.aswing.plaf.*;
import org.aswing.*;
import org.aswing.event.*;
import flash.utils.Timer;
import flash.events.*;

/**
 * @private
 * @author iiley
 */
public class BasicMenuUI extends BasicMenuItemUI{
	
	protected var postTimer:Timer;
	
	public function BasicMenuUI(){
		super();
	}

	override protected function getPropertyPrefix():String {
		return "Menu.";
	}

	override protected function installDefaults():void {
		super.installDefaults();
		updateDefaultBackgroundColor();
	}	
	
	override protected function uninstallDefaults():void {
		menuItem.getModel().setRollOver(false);
		menuItem.setSelected(false);
		super.uninstallDefaults();
	}

	override protected function installListeners():void{
		super.installListeners();
		menuItem.addSelectionListener(__menuSelectionChanged);
	}
	
	override protected function uninstallListeners():void{
		super.uninstallListeners();
		menuItem.removeSelectionListener(__menuSelectionChanged);
	}		
	
	protected function getMenu():JMenu{
		return JMenu(menuItem);
	}
	
	/*
	 * Set the background color depending on whether this is a toplevel menu
	 * in a menubar or a submenu of another menu.
	 */
	protected function updateDefaultBackgroundColor():void{
		if (!getBoolean("Menu.useMenuBarBackgroundForTopLevel")) {
			return;
		}
		var menu:JMenu = getMenu();
		if (menu.getBackground() is UIResource) {
			if (menu.isTopLevelMenu()) {
				menu.setBackground(getColor("MenuBar.background"));
			} else {
				menu.setBackground(getColor(getPropertyPrefix() + ".background"));
			}
		}
	}
	
	/**
	 * SubUI override this to do different
	 */
	override protected function isMenu():Boolean{
		return true;
	}
	
	/**
	 * SubUI override this to do different
	 */
	override protected function isTopMenu():Boolean{
		return getMenu().isTopLevelMenu();
	}
	
	/**
	 * SubUI override this to do different
	 */
	override protected function shouldPaintSelected():Boolean{
		return menuItem.getModel().isRollOver() || menuItem.isSelected();
	}
	
	//---------------------
	
	override public function processKeyEvent(code : uint) : void {
		var manager:MenuSelectionManager = MenuSelectionManager.defaultManager();
		if(manager.isNextPageKey(code)){
			var path:Array = manager.getSelectedPath();
			if(path[path.length-1] == menuItem){
				var popElement:MenuElement = getMenu().getPopupMenu();
				path.push(popElement);
				if(popElement.getSubElements().length > 0){
					path.push(popElement.getSubElements()[0]);
				}
				manager.setSelectedPath(menuItem.stage, path, false);
			}
		}else{
			super.processKeyEvent(code);
		}
	}	
	
	protected function __menuSelectionChanged(e:InteractiveEvent):void{
		menuItem.repaint();
	}
	
	override protected function __menuItemRollOver(e:MouseEvent):void{
		var menu:JMenu = getMenu();
		var manager:MenuSelectionManager = MenuSelectionManager.defaultManager();
		var selectedPath:Array = manager.getSelectedPath();		
		if (!menu.isTopLevelMenu()) {
			if(!(selectedPath.length>0 && selectedPath[selectedPath.length-1]==menu.getPopupMenu())){
				if(menu.getDelay() <= 0) {
					appendPath(getPath(), menu.getPopupMenu());
				} else {
					manager.setSelectedPath(menuItem.stage, getPath(), false);
					setupPostTimer(menu);
				}
			}
		} else {
			if(selectedPath.length > 0 && selectedPath[0] == menu.getParent()) {
				// A top level menu's parent is by definition a JMenuBar
				manager.setSelectedPath(menuItem.stage, [menu.getParent(), menu, menu.getPopupMenu()], false);
			}
		}
		menuItem.repaint();
	}
	
	override protected function __menuItemAct(e:AWEvent):void{
		var menu:JMenu = getMenu();
		var cnt:Container = menu.getParent();
		if(cnt != null && cnt is JMenuBar) {
			var me:Array = [cnt, menu, menu.getPopupMenu()];
			MenuSelectionManager.defaultManager().setSelectedPath(menuItem.stage, me, false);
		}
		menuItem.repaint();
	}
	
	protected function __postTimerAct(e:Event):void{
		var menu:JMenu = getMenu();
		var path:Array = MenuSelectionManager.defaultManager().getSelectedPath();
		if(path.length > 0 && path[path.length-1] == menu) {
			appendPath(path, menu.getPopupMenu());
		}
	}
	
	//---------------------
	protected function appendPath(path:Array, end:Object):void{
		path.push(end);
		MenuSelectionManager.defaultManager().setSelectedPath(menuItem.stage, path, false);
	}

	protected function setupPostTimer(menu:JMenu):void {
		if(postTimer == null){
			postTimer = new Timer(menu.getDelay(), 1);
			postTimer.addEventListener(TimerEvent.TIMER, __postTimerAct);
		}
		postTimer.reset();
		postTimer.start();
	}	
}
}