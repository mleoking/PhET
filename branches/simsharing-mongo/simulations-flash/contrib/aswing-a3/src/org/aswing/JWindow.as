/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{
	
import flash.display.*;
import flash.events.MouseEvent;

import org.aswing.event.PopupEvent;
import org.aswing.event.WindowEvent;
import org.aswing.geom.*;
import org.aswing.util.ArrayList;	

/**
 * Dispatched when the window be set actived from not being actived.
 * @eventType org.aswing.event.WindowEvent.WINDOW_ACTIVATED
 */
[Event(name="windowActived", type="org.aswing.event.WindowEvent")]
	
/**
 *  Dispatched when the window be set not actived from being actived.
 *  @eventType org.aswing.event.WindowEvent.WINDOW_DEACTIVATED
 */
[Event(name="windowDeactived", type="org.aswing.event.WindowEvent")]

/**
 * JWindow is a Container, but you should not add component to JWindow directly,
 * you should add component like this:<br>
 * <pre>
 * 		jwindow.getContentPane().append(child);
 * </pre>
 * <p>The same is true of setting LayoutManagers, removing components, listing children, etc.
 * All these methods should normally be sent to the contentPane instead of the JWindow itself. 
 * The contentPane will always be non-null. Attempting to set it to null will cause the JWindow to throw an Error. 
 * The default contentPane will have a BorderLayout manager set on it. 
 * </p>
 * <p>But if you really want to add child to JWindow like how JDialog and JFrame does,
 * just do it with setting a new <code>LayoutManager</code> to layout them, normally if you want to 
 * extends JWindow to make a new type Window, you may need to add child to JWindow, example a title 
 * bar on top, a menubar on top, a status bar on bottom, etc.
 * </p>
 * @author iiley
 */
public class JWindow extends JPopup{

	private var contentPane:Container;
	private var actived:Boolean;
	
	private var focusWhenDeactive:Component;
	private var lootActiveFrom:JWindow;
	private var focusObject:InteractiveObject;
	private var activable:Boolean;
	
	/**
	 * Create a JWindow
	 * @param owner the owner of this popup, it can be a DisplayObjectContainer or a JPopup, default it is default 
	 * is <code>AsWingManager.getRoot()</code>
	 * @param modal true for a modal dialog, false for one that allows other windows to be active at the same time,
	 *  default is false.
	 * @see org.aswing.AsWingManager#getRoot()
	 * @throw AsWingManagerNotInited if not specified the owner, and aswing default root is not specified either.
	 * @throw TypeError if the owner is not a JPopup nor DisplayObjectContainer
	 */	
	public function JWindow(owner:*=null, modal:Boolean=false){
		super(owner, modal);
		setName("JWindow");
		actived = false;
		focusObject = new Sprite();
		focusObject.name = "hidden_focus_obj";
		focusObject.visible = false;
		activable = true;
		addChild(focusObject);
		
		layout = new WindowLayout();
		setFocusTraversalPolicy(new WindowOrderFocusTraversalPolicy());
		
		addEventListener(MouseEvent.MOUSE_DOWN, __activeWhenPress, true);
		addEventListener(MouseEvent.MOUSE_DOWN, __activeWhenPressWindowSelf);
	}
	
	/**
	 * Sets the layout for the window.
	 * @throws ArgumentError when you try to set a non-WindowLayout instance.
	 */
	override public function setLayout(layout:LayoutManager):void{
		if(layout is WindowLayout){
			var oldLayout:WindowLayout = this.layout as WindowLayout;
			super.setLayout(layout);
			if(oldLayout != null){
				if(oldLayout.getTitleBar() != null){
					layout.addLayoutComponent(oldLayout.getTitleBar(), WindowLayout.TITLE);
				}
				if(oldLayout.getContentPane() != null){
					layout.addLayoutComponent(oldLayout.getContentPane(), WindowLayout.CONTENT);
				}
			}
		}else{
			throw new ArgumentError("Can not set a non-WindowLayout Layout to JWindow");
		}
	}
		
	/**
	 * Check size first to make sure current size is not min than <code>getMinimumSize</code>, 
	 */
	override public function paintImmediately():void{
		if(isVisible()){
			var minimizSize:IntDimension = getMinimumSize();
			var needSize:IntDimension = new IntDimension(Math.max(getWidth(), minimizSize.width),
													Math.max(getHeight(), minimizSize.height));
			setSize(needSize);
			super.paintImmediately();
			revalidateIfNecessary();
		}else{
			super.paintImmediately();
		}
	}
			
	/**
	 * Returns the content pane of this window.
	 * @return the content pane
	 */
	public function getContentPane():Container{
		if(contentPane == null){
			var p:Container = new Container();
			p.setLayout(new BorderLayout());
			setContentPaneImp(p);
		}
		return contentPane;
	}
	
	/**
	 * Sets the window's content pane.
	 * @param cp the content pane you want to set to the window.
	 * @throws Error when cp is null
	 */
	public function setContentPane(cp:Container):void{
		if(cp != contentPane){
			if(cp == null){
				throw new Error(this + " Can not set null to be JWindow's contentPane!");
			}else{
				setContentPaneImp(cp);
			}
		}
	}
	
	private function setContentPaneImp(cp:Container):void{
		if(contentPane != null){
			contentPane.removeFromContainer();
		}
		contentPane = cp;
		append(contentPane, WindowLayout.CONTENT);
	}
			
	/**
	 * This will return the owner of this JWindow, it return a JWindow if
	 * this window's owner is a JWindow, else return null;
	 */
	public function getWindowOwner():JWindow{
		return owner as JWindow;
	}
	
	/**
	 * Return an array containing all the windows this window currently owns.
	 */
	public function getOwnedWindows():Array{
		return getOwnedWindowsWithOwner(this);
	}
	
	/**
	 * Shows or hides the Window. 
	 * <p>Shows the window when set visible true, If the Window and/or its owner are not yet displayable(and if Owner is a JWindow),
	 * both are made displayable. The Window will be made visible and bring to top;
	 * <p>Hides the window when set visible false, just hide the Window's MCs.
	 * @param v true to show the window, false to hide the window.
	 * @throws Error if the window has not a {@link JWindow} or <code>MovieClip</code> owner currently, 
	 * generally this should be never occur since the default owner is <code>_root</code>.
	 * @see #show()
	 * @see #hide()
	 */	
	override public function setVisible(v:Boolean):void{
		super.setVisible(v);
		if(v && isActivable()){
			setActive(true);
		}else{
			lostActiveAction(stage);
		}
	}
	
	/**
	 * Sets whether or not this window will be set to active when user interactive 
	 * the UI. Default value is true.<br/>
	 * If ture, the window will try to active when shown or user pressed the window.<br/>
	 * If false, then window will not do active when visible or user pressed the window.
	 * @param b whether or not the window is activable.
	 */
	public function setActivable(b:Boolean):void{
		activable = b;
	}
	
	/**
	 * Returns whether or not the window is activable.
	 * @return whether or not the window is activable.
	 * @see #setActivable()
	 */
	public function isActivable():Boolean{
		return activable;
	}
	
	override protected function disposeProcess(st:Stage):void{
		lostActiveAction(st);
	}	
		
	/**
	 * Returns whether this Window is active. 
	 * The active Window is always either the focused Window, 
	 * or the first Frame or Dialog that is an owner of the focused Window. 
	 */
	public function isActive():Boolean{
		return actived;
	}
	
	/**
	 * Sets the window to be actived or unactived.
	 */
	public function setActive(b:Boolean):void{
		if(actived != b){
			if(b){
				active();
			}else{
				deactive(stage);
			}
		}
	}
	
	/**
	 * Request focus to this window's default focus component or last focused component when 
	 * last deactived.
	 */
	public function focusAtThisWindow():void{
		var defaultFocus:Component = focusWhenDeactive;
		if(defaultFocus == null || 
			!(AsWingUtils.isAncestor(this, defaultFocus) 
				&& defaultFocus.isShowing() 
				&& defaultFocus.isFocusable() 
				&& defaultFocus.isEnabled())){
			defaultFocus = getFocusTraversalPolicy().getDefaultComponent(this);
		}
		if(defaultFocus == null){
			defaultFocus = this;
		}
		focusWhenDeactive = null;
		defaultFocus.requestFocus();
	}
			
	/**
	 * Returns all displable windows currently on specified stage. A window was disposed or destroied will not 
	 * included by this array.
	 * @param st the stage, if it is null, the <code>AsWingManager.getStage()</code> will be called.
	 * @return all displable windows currently.
	 * @see JPopup#getPopups()
	 */
	public static function getWindows(st:Stage=null):Array{		
		if(st == null){
			st = AsWingManager.getStage();
		}
		var fm:FocusManager = FocusManager.getManager(st);
		var vec:ArrayList = fm.getPopupsVector();
		var arr:Array = new Array();
		for(var i:int=0; i<vec.size(); i++){
			var win:* = vec.get(i);
			if(win is JWindow){
				arr.push(win);
			}
		}
		return arr;
	}
	
	/**
	 * getOwnedWindowsWithOwner(owner:JWindow)<br>
	 * getOwnedWindowsWithOwner(owner:DisplayObjectContainer)
	 * <p>
	 * Returns owned windows of the specifid owner.
	 * @return owned windows of the specifid owner.
	 * @see JPopup#getOwnedPopupsWithOwner()
	 */
	public static function getOwnedWindowsWithOwner(owner:DisplayObjectContainer):Array{
		var fm:FocusManager = FocusManager.getManager(owner.stage);
		if(fm){
			var ws:Array = new Array();
			var vec:ArrayList = fm.getPopupsVector();
			var n:int = vec.size();
			for(var i:int=0; i<n; i++){
				var w:JPopup = vec.get(i);
				if(w is JWindow && w.getOwner() === owner){
					ws.push(w);
				}
			}
			return ws;
		}else{
			return [];
		}
	}
	
	//--------------------------------------------------------
	/*private var visibleWhenOwnerIconing:Boolean;
	private function __ownerIconified():void{
		visibleWhenOwnerIconing = isVisible();
		if(visibleWhenOwnerIconing){
			lostActiveAction();
			ground_mc._visible = false;
		}
	}
	private function __ownerRestored():void{
		if(visibleWhenOwnerIconing){
			ground_mc._visible = true;
		}
	}
	*/
	
	private function lostActiveAction(st:Stage):void{
		if(isActive()){
			deactive(st);
			if(getLootActiveFrom() != null && getLootActiveFrom().isShowing()){
				getLootActiveFrom().active();
			}
		}
		setLootActiveFrom(null);
	}
		
	private function getLootActiveFrom():JWindow{
		return lootActiveFrom;
	}
	private function setLootActiveFrom(activeOwner:JWindow):void{
		if(lootActiveFrom != null){
			lootActiveFrom.removeEventListener(PopupEvent.POPUP_CLOSED, __lootActiveFromHide);
		}
		var oldLookActiveFrom:JWindow = lootActiveFrom;
		lootActiveFrom = activeOwner;
		if(lootActiveFrom != null){
			lootActiveFrom.addEventListener(PopupEvent.POPUP_CLOSED, __lootActiveFromHide, false, 0, true);
		}
		if(activeOwner != null && activeOwner.getLootActiveFrom() == this){
			activeOwner.setLootActiveFrom(oldLookActiveFrom);
		}
	}
	
	private function __lootActiveFromHide(e:PopupEvent):void{
		if(lootActiveFrom != null){
			setLootActiveFrom(lootActiveFrom.lootActiveFrom);
		}
	}
	
	private function active(programmatic:Boolean=true):void{
		var fm:FocusManager = FocusManager.getManager(stage);
		if(fm == null){
			return;
		}
		actived = true;
		var vec:ArrayList = fm.getPopupsVector();
		for(var i:int=0; i<vec.size(); i++){
			var w:JWindow = vec.get(i) as JWindow;
			if(w != null && w != this){
				if(w.isActive()){
					w.deactive(w.stage, programmatic);
					if(w.isShowing()){
						setLootActiveFrom(w);
					}
				}
			}
		}
		fm.setActiveWindow(this);
		focusAtThisWindow();
		dispatchEvent(new WindowEvent(WindowEvent.WINDOW_ACTIVATED, programmatic));
	}
	
	private function deactive(st:Stage, programmatic:Boolean=true):void{
		actived = false;
		//recored this last focus component
		var fm:FocusManager = FocusManager.getManager(st);
		if(fm == null){
			return;
		}
		focusWhenDeactive = fm.getFocusOwner();
		if(!AsWingUtils.isAncestor(this, focusWhenDeactive)){
			focusWhenDeactive = null;
		}
		fm.setActiveWindow(null);
		dispatchEvent(new WindowEvent(WindowEvent.WINDOW_DEACTIVATED, programmatic));
	}
	
	//---------------------------------------------------------------------
    /**
     * Window will return a empty sprite to receive the focus, this makes window 
     * can only get focus with key navigation, not mouse.
     * @return the object to receive the focus.
     */
	override public function getInternalFocusObject():InteractiveObject{
		return focusObject;
	}
    	
	private function __activeWhenPress(e:MouseEvent):void{
		if(getWindowOwner() != null){
			getWindowOwner().toFront();
		}
		toFront();
		if(isActivable() && !isActive()){
			active(false);
		}
	}
	
	private function __activeWhenPressWindowSelf(e:MouseEvent):void{
		if(e.target == this){
			__activeWhenPress(e);
		}
	}
}
}