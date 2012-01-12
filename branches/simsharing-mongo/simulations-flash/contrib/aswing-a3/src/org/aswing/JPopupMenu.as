/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

import flash.events.Event;
import flash.events.MouseEvent;

import org.aswing.event.ContainerEvent;
import org.aswing.event.PopupEvent;
import org.aswing.geom.*;
import org.aswing.plaf.*;
import org.aswing.plaf.basic.BasicPopupMenuUI;
import org.aswing.util.*;

/**
 * An implementation of a popup menu -- a small window that pops up
 * and displays a series of choices. A <code>JPopupMenu</code> is used for the
 * menu that appears when the user selects an item on the menu bar.
 * It is also used for "pull-right" menu that appears when the
 * selects a menu item that activates it. Finally, a <code>JPopupMenu</code>
 * can also be used anywhere else you want a menu to appear.  For
 * example, when the user right-clicks in a specified area.
 * 
 * @author iiley
 */
public class JPopupMenu extends Container implements MenuElement{
		
	private static var popupMenuMouseDownListening:Boolean = false;
	private static var showingMenuPopups:HashSet = new HashSet();
	
	protected var selectionModel:SingleSelectionModel;
	
	protected var invoker:Component;
	protected var popup:JPopup;
	protected var menuInUse:Boolean;
	
	/**
	 * Create a popup menu
	 * @see org.aswing.JPopup
	 */
	public function JPopupMenu() {
		super();
		setName("JPopupMenu");
		menuInUse = false;
		
		layout = new EmptyLayoutUIResourse();
		setSelectionModel(new DefaultSingleSelectionModel());
		//setFocusTraversalKeysEnabled(false);
		
		popup = new JPopup();
		popup.setLayout(new WindowLayout());
		popup.append(this, WindowLayout.CONTENT);
		popup.addEventListener(PopupEvent.POPUP_OPENED, __popupShown);
		popup.addEventListener(PopupEvent.POPUP_CLOSED, __popupClosed);
		
		popup.addEventListener(ContainerEvent.COM_ADDED, __popMenuChildAdd);
		popup.addEventListener(ContainerEvent.COM_REMOVED, __popMenuChildRemove);
		
		updateUI();
	}


	override public function updateUI():void{
		setUI(UIManager.getUI(this));
	}
	
	override public function getDefaultBasicUIClass():Class{
    	return org.aswing.plaf.basic.BasicPopupMenuUI;
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
    		throw new ArgumentError("JPopupMenu just accept MenuElementUI instance!!!");
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
		return "PopupMenuUI";
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
	 * Returns the model object that handles single selections.
	 *
	 * @return the <code>SingleSelectionModel</code> property
	 * @see SingleSelectionModel
	 */
	public function getSelectionModel():SingleSelectionModel {
		return selectionModel;
	}

	/**
	 * Sets the model object to handle single selections.
	 *
	 * @param model the <code>SingleSelectionModel</code> to use
	 * @see SingleSelectionModel
	 */
	public function setSelectionModel(model:SingleSelectionModel):void {
		selectionModel = model;
	}	

	/**
	 * Sets the currently selected component, producing a
	 * a change to the selection model.
	 *
	 * @param sel the <code>Component</code> to select
	 */
	public function setSelected(sel:Component):void {	
		var model:SingleSelectionModel = getSelectionModel();
		var index:int = getIndex(sel);
		model.setSelectedIndex(index);
	}

	/**
	 * Returns true if the menu bar currently has a component selected.
	 *
	 * @return true if a selection has been made, else false
	 */
	public function isSelected():Boolean {	   
		return selectionModel.isSelected();
	}	
	
	/**
	 * Sets the visibility of the popup menu.
	 * 
	 * @param b true to make the popup visible, or false to
	 *		  hide it
	 */
	override public function setVisible(b:Boolean):void {
		if (b == isVisible())
			return;
		//ensure the owner will be applied here
		var owner:* = AsWingUtils.getOwnerAncestor(invoker);
		popup.changeOwner(owner);
		if(b){
			popup.setVisible(true);
			if(isPopupMenu()){
				setInUse(true);
			}
		}else{
			popup.dispose();
			if(isPopupMenu()){
				setInUse(false);
			}
		}
		// if closing, first close all Submenus
		if (b == false) {
			getSelectionModel().clearSelection();
		} else {
			// This is a popup menu with MenuElement children,
			// set selection path before popping up!
			if (isPopupMenu()) {
				MenuSelectionManager.defaultManager().setSelectedPath(stage, [this], true);
			}
		}
		if(b){
			popup.setMnemonicTriggerProxy(stage);
			//if the size is not changed, but children changed, this call make it fresh
			revalidate();
		}else{
			popup.setMnemonicTriggerProxy(null);
		}
	}
	
	override public function isVisible():Boolean{
		return popup.isVisible();
	}
	
	/**
	 * Returns the component which is the 'invoker' of this 
	 * popup menu.
	 *
	 * @return the <code>Component</code> in which the popup menu is displayed
	 */
	public function getInvoker():Component {
		return invoker;
	}

	/**
	 * Sets the invoker of this popup menu -- the component in which
	 * the popup menu is to be displayed.
	 *
	 * @param invoker the <code>Component</code> in which the popup
	 *		menu is displayed
	 */
	public function setInvoker(invoker:Component):void {
		//var oldInvoker:Component = this.invoker;
		this.invoker = invoker;
		popup.changeOwner(AsWingUtils.getOwnerAncestor(invoker));
//		if ((oldInvoker != this.invoker) && (ui != null)) {
//			ui.uninstallUI(this);
//			ui.installUI(this);
//		}
//		invalidate();
	}	
	
	/**
	 * Displays the popup menu at the position x,y in the coordinate
	 * space of the component invoker.
	 *
	 * @param invoker the component in whose space the popup menu is to appear
	 * @param x the x coordinate in invoker's coordinate space at which 
	 * the popup menu is to be displayed
	 * @param y the y coordinate in invoker's coordinate space at which 
	 * the popup menu is to be displayed
	 */
	public function show(invoker:Component, x:int, y:int):void {
		setInvoker(invoker);
		if(invoker){
			var gp:IntPoint = invoker.getGlobalLocation();
			if(gp == null){
				gp = new IntPoint(x, y);
			}else{
				gp.move(x, y);
			}
		}else{
			gp = new IntPoint(x, y);
		}
		pack();
		setVisible(true);
		//ensure viewing in the eyeable area
		adjustPopupLocationToFitScreen(gp);
		popup.setGlobalLocation(gp);
	}
	
	/**
	 * Causes this Popup Menu to be sized to fit the preferred size.
	 */
	override public function pack():void{
		popup.pack();
	}
	
	/**
	 * Dispose(close) the popup.
	 */
	public function dispose():void{
		popup.dispose();
		if(isPopupMenu()){
			setInUse(false);
		}
	}
	
	/**
	 * Returns the popup menu which is at the root of the menu system
	 * for this popup menu.
	 *
	 * @return the topmost grandparent <code>JPopupMenu</code>
	 */
	public function getRootPopupMenu():JPopupMenu {
		var mp:JPopupMenu = this;
		while((mp != null) && 
				(mp.isPopupMenu() != true) &&
				(mp.getInvoker() != null) &&
				(mp.getInvoker().getParent() != null) &&
				(mp.getInvoker().getParent() is JPopupMenu)
			  ) {
			mp = JPopupMenu(mp.getInvoker().getParent());
		}
		return mp;
	}
	
	/**
	 * Examines the list of menu items to determine whether
	 * <code>popupMenu</code> is a popup menu.
	 * 
	 * @param popup  a <code>JPopupMenu</code>
	 * @return true if <code>popupMenu</code>
	 */
	public function isSubPopupMenu(popupMenu:JPopupMenu):Boolean {
		var ncomponents:int = getComponentCount();
		for (var i:int = 0 ; i < ncomponents ; i++) {
			var comp:Component = getComponent(i);
			if (comp is JMenu) {
				var menu:JMenu = JMenu(comp);
				var subPopup:JPopupMenu = menu.getPopupMenu();
				if (subPopup == popupMenu){
					return true;
				}
				if (subPopup.isSubPopupMenu(popupMenu)){
					return true;
				}
			}
		}
		return false;
	}	
		
	/**
	 * Returns true if the popup menu is a standalone popup menu
	 * rather than the submenu of a <code>JMenu</code>.
	 *
	 * @return true if this menu is a standalone popup menu, otherwise false
	 */	
	private function isPopupMenu():Boolean{
		return (!(invoker is JMenu));
	}
	
	private function adjustPopupLocationToFitScreen(gp:IntPoint):IntPoint{
		var globalBounds:IntRectangle = AsWingUtils.getVisibleMaximizedBounds(popup.parent);
		if(gp.x + popup.getWidth() > globalBounds.x + globalBounds.width){
			gp.x = gp.x - popup.getWidth();
		}
		if(gp.x < globalBounds.x){
			gp.x = globalBounds.x;
		}
		if(gp.y + popup.getHeight() > globalBounds.y + globalBounds.height){
			gp.y = gp.y - popup.getHeight();
		}
		if(gp.y < globalBounds.y){
			gp.y = globalBounds.y;
		}
		return gp;
	}
	
	//---------------------------------------------------------------------
	//--   MenuElement implementation   --
	//---------------------------------------------------------------------
	
	private function __popMenuChildAdd(e:ContainerEvent) : void {
		var child:Component = e.getChild();
		if(child is MenuElement){
			MenuElement(child).setInUse(isInUse());
		}
	}

	private function __popMenuChildRemove(e:ContainerEvent) : void {
		var child:Component = e.getChild();
		if(child is MenuElement){
			MenuElement(child).setInUse(false);
		}
	}
	
	public function menuSelectionChanged(isIncluded : Boolean) : void {
		if(invoker is JMenu) {
			var m:JMenu = JMenu(invoker);
			if(isIncluded){
				m.setPopupMenuVisible(true);
			}else{
				m.setPopupMenuVisible(false);
			}
		}
		if (isPopupMenu() && !isIncluded){
			setVisible(false);
		}
	}

	public function getSubElements() : Array {
		var arr:Array = new Array();
		for(var i:int=0; i<getComponentCount(); i++){
			var com:Component = getComponent(i);
			if(com is MenuElement){
				arr.push(com);
			}
		}
		return arr;
	}
	
	public function getMenuComponent() : Component {
		return this;
	}
	
	public function processKeyEvent(code : uint) : void {
		getMenuElementUI().processKeyEvent(code);
	}

    public function setInUse(b:Boolean):void{
    	if(menuInUse != b){
	    	menuInUse = b;
	    	var subs:Array = getSubElements();
	    	for(var i:int=0; i<subs.length; i++){
	    		var ele:MenuElement = MenuElement(subs[i]);
	    		ele.setInUse(b);
	    	}
    	}
    }
    
    public function isInUse():Boolean{
    	return menuInUse;
    }
	
	//----------------------
	
	private static function __popupMenuMouseDown(e:Event):void{
		var hittedPopupMenu:Boolean = false;
		var ps:Array = showingMenuPopups.toArray();
		var hasPopupWindowShown:Boolean = ps.length > 0;
		
		for(var i:int=0; i<ps.length; i++){
			var pp:JPopup = JPopup(ps[i]);
			if(pp.hitTestMouse()){
				hittedPopupMenu = true;
				break;
			}
		}
		if(hasPopupWindowShown && !hittedPopupMenu){
			MenuSelectionManager.defaultManager().clearSelectedPath(false);
		}
	}

	private function __popupShown(e:PopupEvent) : void {
		var source:* = e.target;
		showingMenuPopups.add(source);
		//to delay to next frame to add the listener to avoid listening in a mouse down event
		AsWingManager.callNextFrame(__addMouseDownListenerToStage);
	}
	
	private function __addMouseDownListenerToStage():void{
		if(showingMenuPopups.size()>0 && !popupMenuMouseDownListening && stage != null){
			stage.addEventListener(MouseEvent.MOUSE_DOWN, __popupMenuMouseDown, false, 0, true);
			popupMenuMouseDownListening = true;
		}
	}

	private function __popupClosed(e:PopupEvent) : void {
		var source:* = e.target;
		showingMenuPopups.remove(source);
		if(showingMenuPopups.size() == 0 && popupMenuMouseDownListening && stage != null){
			stage.removeEventListener(MouseEvent.MOUSE_DOWN, __popupMenuMouseDown);
			popupMenuMouseDownListening = false;
		}
	}
}
}