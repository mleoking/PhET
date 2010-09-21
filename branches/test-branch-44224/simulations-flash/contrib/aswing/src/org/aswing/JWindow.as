/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASWingUtils;
import org.aswing.BorderLayout;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.FocusManager;
import org.aswing.geom.Dimension;
import org.aswing.overflow.JPopup;
import org.aswing.KeyboardManager;
import org.aswing.KeyMap;
import org.aswing.LayoutManager;
import org.aswing.plaf.WindowUI;
import org.aswing.UIManager;
import org.aswing.util.Delegate;
import org.aswing.util.Vector;
import org.aswing.WindowLayout;

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
 * 
 * <p>But if you really want to add child to JWindow like how JDialog and JFrame does,
 * just do it, normally if you want to extends JWindow to make a new type Window, you may
 * need to add child to JWindow, example a title bar on top, a menubar on top, a status bar on bottom, etc.
 * 
 * @author iiley
 */
class org.aswing.JWindow extends JPopup{
	/**
	 * The window-activated event type.
	 * onWindowActived(source:JWindow)
	 */	
	public static var ON_WINDOW_ACTIVATED:String = "onWindowActived";
	/**
	 * The window-deactivated event type.
	 * onWindowDeactived(source:JWindow)
	 */
	public static var ON_WINDOW_DEACTIVATED:String = "onWindowDeactived";

	/**
	 * The window iconified event.
	 * onWindowIconified(source:JWindow)
	 * @see org.aswing.JFrame#ICONIFIED
	 * @see org.aswing.JFrame#setState()
	 */	
	public static var ON_WINDOW_ICONIFIED:String = "onWindowIconified";
	/**
	 * The window restored event, (the JFrame normal button pushed).
	 * onWindowRestored(source:JWindow)
	 * @see org.aswing.JFrame#NORMAL
	 * @see org.aswing.JFrame#setState()
	 */
	public static var ON_WINDOW_RESTORED:String = "onWindowRestored";
	/**
	 * The window maximized event.
	 * onWindowMaximized(source:JWindow)
	 * @see org.aswing.JFrame#MAXIMIZED
	 * @see org.aswing.JFrame#setState()
	 */
	public static var ON_WINDOW_MAXIMIZED:String = "onWindowMaximized";	
	
	
	private var contentPane:Container;
	private var actived:Boolean;
	private var keymap:KeyMap;
	
	private var lootActiveFrom:JWindow;
		
	/**
	 * Create a JWindow
	 * <br>
	 * JWindow(owner:JPopup, modal:Boolean)<br>
	 * JWindow(owner:MovieClip, modal:Boolean)<br>
	 * JWindow(owner:JPopup)<br>
	 * JWindow(owner:MovieClip)<br>
	 * JWindow()<br>
	 * 
	 * @param owner the owner of this window, it can be a MovieClip or a JPopup, default it is default 
	 * is <code>ASWingUtils.getRootMovieClip()</code>
	 * @param modal true for a modal dialog, false for one that allows other windows to be active at the same time,
	 *  default is false.
	 * @see org.aswing.ASWingUtils#getRootMovieClip()
	 */
	public function JWindow(owner, modal:Boolean){
		super(owner, modal);
		setName("JWindow");
		actived = false;
		keymap = new KeyMap();
		layout = new WindowLayout();
		listenerToOwner[ON_WINDOW_ICONIFIED] = Delegate.create(this, __ownerIconified);
		listenerToOwner[ON_WINDOW_RESTORED] = Delegate.create(this, __ownerRestored);
		listenerToOwner[ON_WINDOW_MAXIMIZED] = listenerToOwner[ON_WINDOW_RESTORED];
		
		updateUI();
	}
	
    public function updateUI():Void{
    	setUI(WindowUI(UIManager.getUI(this)));
    }
    
    public function setUI(newUI:WindowUI):Void{
    	super.setUI(newUI);
    }
	
	public function getUIClassID():String{
		return "WindowUI";
	}
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.basic.BasicWindowUI;
    }
	
	/**
	 * Sets the layout for the window.
	 * @throws Error when you try to set a non-WindowLayout instance.
	 */
	public function setLayout(layout:LayoutManager):Void{
		if(layout instanceof WindowLayout){
			var oldLayout:WindowLayout = WindowLayout(this.layout);
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
			trace(this + " Can not set a non-WindowLayout Layout to JWindow");
			throw new Error(this + " Can not set a non-WindowLayout Layout to JWindow");
		}
	}
		
	/**
	 * Check size first to make sure current size is not min than <code>getMinimumSize</code>, 
	 */
	public function paintImmediately():Void{
		if(displayable && isVisible()){
			var minimizSize:Dimension = getMinimumSize();
			var needSize:Dimension = new Dimension(Math.max(getWidth(), minimizSize.width),
													Math.max(getHeight(), minimizSize.height));
			this.setSize(needSize);
			super.paintImmediately();
			revalidate();
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
			p.setFocusable(false);
			p.setLayout(new BorderLayout());
			setContentPaneImp(p);
		}
		return contentPane;
	}
	
	/**
	 * Sets the window's content pane.
	 * @param cp the content pane you want to set to the window.
	 * @throws Error when cp is null or undefined
	 */
	public function setContentPane(cp:Container):Void{
		if(cp != contentPane){
			if(cp == null){
				trace(this + " Can not set null to be JWindow's contentPane!");
				throw new Error(this + " Can not set null to be JWindow's contentPane!");
			}else{
				setContentPaneImp(cp);
			}
		}
	}
	
	private function setContentPaneImp(cp:Container):Void{
		contentPane.removeFromContainer();
		contentPane = cp;
		append(contentPane, WindowLayout.CONTENT);
	}
	
	/**
	 * Returns the key -> action map of this window.
	 * When a window is actived, it's keymap will be in working, or it is out of working.
	 * @see org.aswing.KeyMap
	 * @see org.aswing.KeyboardController
	 */
	public function getKeyMap():KeyMap{
		return keymap;
	}		
		
	/**
	 * This will return the owner of this JWindow, it return a JWindow if
	 * this window's owner is a JWindow, else return null;
	 */
	public function getWindowOwner():JWindow{
		return JWindow(owner);
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
	public function setVisible(v:Boolean):Void{
		super.setVisible(v);
		if(v){
			setActive(true);
		}else{
			lostActiveAction();
		}
	}
	
	private function disposeProcess():Void{
		lostActiveAction();
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
	public function setActive(b:Boolean):Void{
		if(actived != b){
			if(b){
				active();
			}else{
				deactive();
			}
		}
	}
	
	/**
	 * Returns the window's ancestor movieclip which it/it's owner is created on.
	 * @return the ancestor movieclip of this window 
	 * @see JPopup#getPopupAncestorMC()
	 */
	public function getWindowAncestorMC():MovieClip{
		return getPopupAncestorMC();
	}
		
	/**
	 * Returns all displable windows currently. A window was disposed or destroied will not 
	 * included by this array.
	 * @return all displable windows currently.
	 * @see JPopup#getPopups()
	 */
	public static function getWindows():Array{
		var vec:Vector = getPopupsVector();
		var arr:Array = new Array();
		for(var i:Number=0; i<vec.size(); i++){
			var win:Object = vec.get(i);
			if(win instanceof JWindow){
				arr.push(win);
			}
		}
		return arr;
	}
	
	/**
	 * getOwnedWindowsWithOwner(owner:JWindow)<br>
	 * getOwnedWindowsWithOwner(owner:MovieClip)
	 * <p>
	 * Returns owned windows of the specifid owner.
	 * @return owned windows of the specifid owner.
	 * @see JPopup#getOwnedPopupsWithOwner()
	 */
	public static function getOwnedWindowsWithOwner(owner:Object):Array{
		var ws:Array = new Array();
		for(var i:Number=0; i<getPopupsVector().size(); i++){
			var w:JPopup = JPopup(getPopupsVector().get(i));
			if(w instanceof JWindow && w.getOwner() === owner){
				ws.push(w);
			}
		}
		return ws;
	}
	
	//--------------------------------------------------------
	private var visibleWhenOwnerIconing:Boolean;
	private function __ownerIconified():Void{
		visibleWhenOwnerIconing = isVisible();
		if(visibleWhenOwnerIconing){
			lostActiveAction();
			ground_mc._visible = false;
		}
	}
	private function __ownerRestored():Void{
		if(visibleWhenOwnerIconing){
			ground_mc._visible = true;
		}
	}
		
	private function lostActiveAction():Void{
		if(isActive()){
			deactive();
			if(getLootActiveFrom() != null && getLootActiveFrom().isShowing()){
				getLootActiveFrom().active();
			}
		}
		setLootActiveFrom(null);
	}
		
	private function getLootActiveFrom():JWindow{
		return lootActiveFrom;
	}
	private function setLootActiveFrom(activeOwner:JWindow):Void{
		if(activeOwner.getLootActiveFrom() == this){
			activeOwner.lootActiveFrom = lootActiveFrom;
		}
		lootActiveFrom = activeOwner;
	}
	
	private function active():Void{
		actived = true;
		KeyboardManager.getInstance().registerKeyMap(getKeyMap());
		var vec:Vector = getPopupsVector();
		for(var i:Number=0; i<vec.size(); i++){
			var w:JWindow = JWindow(vec.get(i));
			if(w != null && w != this){
				if(w.isActive()){
					w.deactive();
					setLootActiveFrom(w);
				}
			}

		}
		FocusManager.getCurrentManager().setActiveWindow(this);
		focusAtThisWindow();

		dispatchEvent(createEventObj(ON_WINDOW_ACTIVATED));
	}
	
	private function deactive():Void{
		actived = false;
		KeyboardManager.getInstance().unregisterKeyMap(getKeyMap());
		FocusManager.getCurrentManager().setActiveWindow(null);
		dispatchEvent(createEventObj(ON_WINDOW_DEACTIVATED));
	}
	
	private function focusAtThisWindow():Void{
		var focusOwner:Component = FocusManager.getCurrentManager().getFocusOwner();
		var currentFocusWindow:JWindow = ASWingUtils.getWindowAncestor(focusOwner);
		if(currentFocusWindow != this){
			var newFocusOwner:Component = getFocusTraversalPolicy().getInitialComponent(this);
			if(newFocusOwner != null){
				newFocusOwner.requestFocus();
			}
		}
	}
		
	private function __onPress():Void{
		super.__onPress();
		__activeWhenClicked();
	}
		
	/**
	 * Active and make this window to front.
	 */
	public function __onChildPressed(child:Component):Void{
		super.__onChildPressed(child);
		__activeWhenClicked();
	}
	
	private function __activeWhenClicked():Void{
		//getWindowOwner().__activeWhenClicked();
		getWindowOwner().toFront();
		if(!isActive()){
			toFront();
			active();
		}
	}
}

