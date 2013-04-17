/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

import org.aswing.plaf.*;
import org.aswing.plaf.basic.BasicMenuBarUI;
import flash.events.Event;
import org.aswing.event.ContainerEvent;

/**
 * An implementation of a menu bar. You add <code>JMenu</code> objects to the
 * menu bar to construct a menu. When the user selects a <code>JMenu</code>
 * object, its associated <code>JPopupMenu</code> is displayed, allowing the
 * user to select one of the <code>JMenuItems</code> on it.
 * @author iiley
 */
public class JMenuBar extends Container implements MenuElement{
	
	private var selectionModel:SingleSelectionModel;
	private var menuInUse:Boolean;
	
	public function JMenuBar() {
		super();
		setSelectionModel(new DefaultSingleSelectionModel());
		layout = new EmptyLayoutUIResourse();
		menuInUse = false;
		
		addEventListener(Event.REMOVED_FROM_STAGE, __menuBarDestroied);
		addEventListener(Event.ADDED_TO_STAGE, __menuBarCreated);
		addEventListener(ContainerEvent.COM_ADDED, __menuBarChildAdd);
		addEventListener(ContainerEvent.COM_REMOVED, __menuBarChildRemove);
		
		updateUI();
	}

	override public function updateUI():void{
		setUI(UIManager.getUI(this));
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
    		throw new ArgumentError("JMenuBar just accept MenuElementUI instance!!!");
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
		return "MenuBarUI";
	}
	
	override public function getDefaultBasicUIClass():Class{
    	return org.aswing.plaf.basic.BasicMenuBarUI;
    }
	
	/**
	 * Adds a menu to the menu bar.
	 * @param menu the menu to be added
	 * @return the menu be added
	 */
	public function addMenu(menu:JMenu):JMenu{
		append(menu);
		return menu;
	}
	
	/**
	 * Returns the menu component at index, if it is not a menu component at that index, null will be returned.
	 * @return a menu instance or null
	 */
	public function getMenu(index:int):JMenu{
		var com:Component = getComponent(index);
		if(com is JMenu){
			return JMenu(com);
		}else{
			return null;
		}
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
		
	//--------------------------------------------------------------
	//					MenuElement imp
	//--------------------------------------------------------------
		
	public function menuSelectionChanged(isIncluded : Boolean) : void {
	}

	private function __menuBarDestroied(e:Event):void{
		setInUse(false);
	}
	
	private function __menuBarCreated(e:Event):void{
		setInUse(true);
	}
	
	private function __menuBarChildAdd(e:ContainerEvent) : void {
		if(e.getChild() is MenuElement){
			MenuElement(e.getChild()).setInUse(isInUse());
		}
	}

	private function __menuBarChildRemove(e:ContainerEvent) : void {
		if(e.getChild() is MenuElement){
			MenuElement(e.getChild()).setInUse(false);
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
		
	public function getMenuComponent():Component{
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
}
}