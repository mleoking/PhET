/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.Container;
import org.aswing.DefaultSingleSelectionModel;
import org.aswing.overflow.JMenu;
import org.aswing.MenuElement;
import org.aswing.plaf.MenuBarUI;
import org.aswing.SingleSelectionModel;
import org.aswing.UIManager;
import org.aswing.util.Delegate;

/**
 * @author iiley
 */
class org.aswing.overflow.JMenuBar extends Container implements MenuElement{
	
	private var selectionModel:SingleSelectionModel;
	private var menuInUse:Boolean;
	private var menuBarLis;
	
	public function JMenuBar() {
		super();
		setSelectionModel(new DefaultSingleSelectionModel());
		layout = undefined;
		menuInUse = false;
		
		menuBarLis = new Object();
		menuBarLis[ON_DESTROY] = Delegate.create(this, __menuBarDestroied);
		menuBarLis[ON_CREATED] = Delegate.create(this, __menuBarCreated);
		menuBarLis[ON_COM_ADDED] = Delegate.create(this, __menuBarChildAdd);
		menuBarLis[ON_COM_REMOVED] = Delegate.create(this, __menuBarChildRemove);
		addEventListener(menuBarLis);
		
		updateUI();
	}

	public function updateUI():Void{
		setUI(MenuBarUI(UIManager.getUI(this)));
	}
	
	public function setUI(newUI:MenuBarUI):Void{
		super.setUI(newUI);
	}
	
	public function getUIClassID():String{
		return "MenuBarUI";
	}
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.basic.BasicMenuBarUI;
    }
	
	public function getUI():MenuBarUI{
		return MenuBarUI(ui);
	}
	
	public function addMenu(menu:JMenu):JMenu{
		append(menu);
		return menu;
	}
	
	/**
	 * Returns the menu component at index, if it is not a menu component at that index, null will be returned.
	 */
	public function getMenu(index:Number):JMenu{
		var com:Component = getComponent(index);
		if(com instanceof JMenu){
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
	public function setSelectionModel(model:SingleSelectionModel):Void {
		selectionModel = model;
	}	

	/**
	 * Sets the currently selected component, producing a
	 * a change to the selection model.
	 *
	 * @param sel the <code>Component</code> to select
	 */
	public function setSelected(sel:Component):Void {	
		var model:SingleSelectionModel = getSelectionModel();
		var index:Number = getIndex(sel);
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
		
	public function menuSelectionChanged(isIncluded : Boolean) : Void {
	}

	private function __menuBarDestroied():Void{
		setInUse(false);
	}
	
	private function __menuBarCreated():Void{
		setInUse(true);
	}
	
	private function __menuBarChildAdd(source:Component, child:Component) : Void {
		if(child instanceof MenuElement){
			MenuElement(child).setInUse(isInUse());
		}
	}

	private function __menuBarChildRemove(source:Component, child:Component) : Void {
		if(child instanceof MenuElement){
			MenuElement(child).setInUse(false);
		}
	}
	
	public function getSubElements() : Array {
		var arr:Array = new Array();
		for(var i:Number=0; i<getComponentCount(); i++){
			var com:Component = getComponent(i);
			if(com instanceof MenuElement){
				arr.push(com);
			}
		}
		return arr;
	}
		
	public function getMenuComponent():Component{
		return this;
	}
	
	public function processKeyEvent(code : Number) : Void {
		getUI().processKeyEvent(code);
	}
	
    public function setInUse(b:Boolean):Void{
    	if(menuInUse != b){
	    	menuInUse = b;
	    	var subs:Array = getSubElements();
	    	for(var i:Number=0; i<subs.length; i++){
	    		var ele:MenuElement = MenuElement(subs[i]);
	    		ele.setInUse(b);
	    	}
    	}
    }
    
    public function isInUse():Boolean{
    	return menuInUse;
    }
}