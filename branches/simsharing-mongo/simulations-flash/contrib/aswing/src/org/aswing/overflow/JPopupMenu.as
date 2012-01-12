/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASWingUtils;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.DefaultSingleSelectionModel;
import org.aswing.geom.Point;
import org.aswing.geom.Rectangle;
import org.aswing.overflow.JMenu;
import org.aswing.overflow.JMenuItem;
import org.aswing.overflow.JPopup;
import org.aswing.MenuElement;
import org.aswing.MenuSelectionManager;
import org.aswing.plaf.PopupMenuUI;
import org.aswing.SingleSelectionModel;
import org.aswing.UIManager;
import org.aswing.util.ArrayUtils;
import org.aswing.util.Delegate;
import org.aswing.WindowLayout;

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
class org.aswing.overflow.JPopupMenu extends Container implements MenuElement{
	
	private static var popupMenuMouseLis:Object;
	private static var showingMenuPopups:Array;
	private static var popupLis:Object;
	
	private var selectionModel:SingleSelectionModel;
	
	private var invoker:Component;
	private var popup:JPopup;
	private var menuInUse:Boolean;
	private var childLis:Object;
	
	/**
	 * Create a popup menu
	 * @see org.aswing.overflow.JPopup
	 */
	public function JPopupMenu() {
		super();
		setName("JPopupMenu");
		menuInUse = false;
		
		if(popupMenuMouseLis == null){
			popupMenuMouseLis = {onMouseDown:Delegate.create(JPopupMenu, __popupMenuMouseDown)};
			Mouse.addListener(popupMenuMouseLis);
			
			showingMenuPopups = new Array();
			
			popupLis = new Object();
			popupLis[JPopup.ON_WINDOW_OPENED] = Delegate.create(JPopupMenu, __popupShown);
			popupLis[JPopup.ON_WINDOW_CLOSED] = Delegate.create(JPopupMenu, __popupClosed);
		}
		
		layout = undefined;
		setSelectionModel(new DefaultSingleSelectionModel());
		//setFocusTraversalKeysEnabled(false);
		
		popup = new JPopup();
		popup.setLayout(new WindowLayout());
		popup.append(this, WindowLayout.CONTENT);
		popup.addEventListener(popupLis);
		
		childLis = new Object();
		childLis[ON_COM_ADDED] = Delegate.create(this, __popMenuChildAdd);
		childLis[ON_COM_REMOVED] = Delegate.create(this, __popMenuChildRemove);
		addEventListener(childLis);
		
		updateUI();
	}


	public function updateUI():Void{
		setUI(PopupMenuUI(UIManager.getUI(this)));
	}
	
	public function setUI(newUI:PopupMenuUI):Void{
		super.setUI(newUI);
	}
	
	public function getUIClassID():String{
		return "PopupMenuUI";
	}
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.basic.BasicPopupMenuUI;
    }

	public function getUI():PopupMenuUI{
		return PopupMenuUI(ui);
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
	
	/**
	 * Sets the visibility of the popup menu.
	 * 
	 * @param b true to make the popup visible, or false to
	 *		  hide it
	 */
	public function setVisible(b:Boolean):Void {
		if (b == isVisible())
			return;
		// if closing, first close all Submenus
		if (b == false) {
			getSelectionModel().clearSelection();
		} else {
			// This is a popup menu with MenuElement children,
			// set selection path before popping up!
			if (isPopupMenu()) {
				var subEles:Array = getSubElements();
				if (subEles.length > 0) {
					var me:Array = [this, subEles[0]];
					MenuSelectionManager.defaultManager().setSelectedPath(me);
				} else {
					MenuSelectionManager.defaultManager().setSelectedPath([this]);
				}
			}
		}
		//ensure the owner will be applied here
		popup.changeOwner(ASWingUtils.getOwnerAncestor(invoker));
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
	}
	
	public function isVisible():Boolean{
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
	 * the popup menu menu is to be displayed.
	 *
	 * @param invoker the <code>Component</code> in which the popup
	 *		menu is displayed
	 */
	public function setInvoker(invoker:Component):Void {
		var oldInvoker:Component = this.invoker;
		this.invoker = invoker;
		popup.changeOwner(ASWingUtils.getOwnerAncestor(invoker));
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
	public function show(invoker:Component, x:Number, y:Number):Void {
		setInvoker(invoker);

		var gp:Point = invoker.getGlobalLocation();
		if(gp == null){
			gp = new Point(x, y);
		}else{
			gp.move(x, y);
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
	public function pack():Void{
		popup.pack();
	}
	
	public function dispose():Void{
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
				(mp.getInvoker().getParent() instanceof JPopupMenu)
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
		var ncomponents:Number = getComponentCount();
		for (var i:Number = 0 ; i < ncomponents ; i++) {
			var comp:Component = getComponent(i);
			if (comp instanceof JMenu) {
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
		return  ((invoker != null) && !(invoker instanceof JMenu));
	}
	
	private function adjustPopupLocationToFitScreen(gp:Point):Point{
		var globalBounds:Rectangle = ASWingUtils.getVisibleMaximizedBounds();
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
	
	private function __popMenuChildAdd(source:Component, child:Component) : Void {
		if(child instanceof MenuElement){
			MenuElement(child).setInUse(isInUse());
		}
	}

	private function __popMenuChildRemove(source:Component, child:Component) : Void {
		if(child instanceof MenuElement){
			MenuElement(child).setInUse(false);
		}
	}
	
	public function menuSelectionChanged(isIncluded : Boolean) : Void {
		if(invoker instanceof JMenu) {
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
		for(var i:Number=0; i<getComponentCount(); i++){
			var com:Component = getComponent(i);
			if(com instanceof MenuElement){
				arr.push(com);
			}
		}
		return arr;
	}
	
	public function getMenuComponent() : Component {
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
	
	//----------------------
	
	private static function __popupMenuMouseDown():Void{
		var hittedPopupMenu:Boolean = false;
		var ps:Array = showingMenuPopups;
		var hasPopupWindowShown:Boolean = ps.length > 0;
		
		for(var i:Number=0; i<ps.length; i++){
			var pp:JPopup = JPopup(ps[i]);
			if(pp.hitTestMouse()){
				hittedPopupMenu = true;
				break;
			}
		}
		if(hasPopupWindowShown && !hittedPopupMenu){
			MenuSelectionManager.defaultManager().clearSelectedPath();
		}
	}

	private function __popupShown(source:JPopup) : Void {
		showingMenuPopups.push(source);
	}

	private function __popupClosed(source:JPopup) : Void {
		ArrayUtils.removeFromArray(showingMenuPopups, source);
	}
}