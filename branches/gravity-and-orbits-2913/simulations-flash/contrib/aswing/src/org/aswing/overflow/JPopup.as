/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.ASWingUtils;
import org.aswing.BorderLayout;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.FocusManager;
import org.aswing.geom.Point;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.SolidBrush;
import org.aswing.MCPanel;
import org.aswing.UIManager;
import org.aswing.util.Delegate;
import org.aswing.util.DepthManager;
import org.aswing.util.MCUtils;
import org.aswing.util.Vector;
import org.aswing.util.ObjectUtils;

/**
 * JPopup is a component that generally be a base container of a window panel.
 * <p>
 * <b>Note:</b>
 * You should call <code>dispose()</code> instead of <code>destroy()</code> to remove a 
 * JPopup from stage.
 * @see org.aswing.JWindow
 * @author iiley
 */
class org.aswing.overflow.JPopup extends Container {
	
	/**
	 * The window closed event means that When a window was opened.
	 * onWindowOpened(source:JPopup)
	 */	
	public static var ON_WINDOW_OPENED:String = "onWindowOpened";		
	/**
	 * The window opened event means that When a window was disposed or hiden.
	 * onWindowClosed(source:JPopup)
	 */	
	public static var ON_WINDOW_CLOSED:String = "onWindowClosed";	
	/**
	 * The "window is closing" event.
	 * onWindowClosing(source:JPopup)
	 */	
	public static var ON_WINDOW_CLOSING:String = "onWindowClosing";
	
	private static var popups:Vector;
	
	private var ground_mc:MovieClip;
	private var owner:Object;
	private var modal:Boolean;
	private var modalMC:MovieClip;
	
	private var listenerToOwner:Object;
	private var mouseMoveListener:Object;
	private var stageResizeListener:Object;
	private var lastLAF:Object;
	
	/**
	 * Create a JPopup
	 * <br>
	 * JPopup(owner:JPopup, modal:Boolean)<br>
	 * JPopup(owner:MovieClip, modal:Boolean)<br>
	 * JPopup(owner:JPopup)<br>
	 * JPopup(owner:MovieClip)<br>
	 * JPopup()<br>
	 * 
	 * @param owner the owner of this popup, it can be a MovieClip or a JPopup, default it is default 
	 * is <code>ASWingUtils.getRootMovieClip()</code>
	 * @param modal true for a modal dialog, false for one that allows other windows to be active at the same time,
	 *  default is false.
	 * @see org.aswing.ASWingUtils#getRootMovieClip()
	 */	
	public function JPopup(owner, modal:Boolean) {
		super();
		setName("JPopup");
		this.owner = (owner == undefined ? ASWingUtils.getRootMovieClip() : owner);
		this.modal = (modal == undefined ? false : modal);
		visible = false;
		layout = new BorderLayout();
		
		lastLAF = UIManager.getLookAndFeel();
		
		addEventListener(ON_MOVED, __resetModalMCOnMouseMoved, this);
		//This is for subclass
		listenerToOwner = new Object();
		
		stageResizeListener = createEventListener("onResize", onStageResized, this);
	}
		
	/**
	 * @return true always here.
	 */
	public function isValidateRoot():Boolean{
		return (getParent() != null);
	}	
	
	/**
	 * This will return the owner of this JPopup, it maybe a MovieClip maybe a JPopup.
	 */
	public function getOwner():Object{
		return owner;
	}
	
	/**
	 * This will return the owner of this JPopup, it return a JPopup if
	 * this window's owner is a JPopup, else return null;
	 */
	public function getPopupOwner():JPopup{
		return JPopup(owner);
	}
	
	/**
	 * changeOwner(owner:JPopup)<br>
	 * changeOwner(owner:MovieClip)
	 * <p>
	 * Changes the owner. While the popup is displayable, you can't change the owner of it.
	 * @param owner the new owner to apply
	 * @return true if changed successfully, false otherwise
	 */
	public function changeOwner(owner):Boolean{
		if(this.owner != owner){
			if(isDisplayable()){
				return false;
			}
			this.owner = owner;
		}
		return true;
	}
	
	/**
	 * Specifies whether this dialog should be modal.
	 */
	public function setModal(m:Boolean):Void{
		if(modal != m){
			modal = m;
			modalMC._visible = modal;
		}
	}
	
	/**
	 * Returns is this dialog modal.
	 */
	public function isModal():Boolean{
		return modal;
	}	
	
			
	/**
	 * Shortcut of <code>setVisible(true)</code>
	 */
	public function show():Void{
		setVisible(true);
	}
	
	/**
	 * Shows or hides the Popup. 
	 * <p>Shows the window when set visible true, If the Popup and/or its owner are not yet displayable(and if Owner is a JPopup),
	 * both are made displayable. The Popup will be made visible and bring to top;
	 * <p>Hides the window when set visible false, just hide the Popup's MCs.
	 * @param v true to show the window, false to hide the window.
	 * @throws Error if the window has not a {@link JPopup} or <code>MovieClip</code> owner currently, 
	 * generally this should be never occur since the default owner is <code>_root</code>.
	 * @see #show()
	 * @see #hide()
	 */	
	public function setVisible(v:Boolean):Void{
		if(v != visible || (v && !MCUtils.isMovieClipExist(root_mc))){
			super.setVisible(v);
			
			if(v){
				if(!isDisplayable()){
					createPopupContents();
				}
				resetModalMC();
				Stage.addListener(stageResizeListener);
				dispatchEvent(createEventObj(ON_WINDOW_OPENED));
			}else{
				Stage.removeListener(stageResizeListener);
				dispatchEvent(createEventObj(ON_WINDOW_CLOSED));
			}
		}
		if(v){
			toFront();
		}
	}
	
	/**
	 * TODO need test
	 * Renders the window at background, for logic it is visible after this call, 
	 * but nothing will appear(assets invisible), and later you can call <code>show()</code> 
	 * or <code>setVisible(true)</code> to show it.
	 * @see #setVisible
	 * @see #show
	 */
	public function renderAtBackground():Void{
		setVisible(true);
		root_mc._visible = false;
	}
	
	/**
	 * Shortcut of <code>setVisible(false)</code>
	 */
	public function hide():Void{
		setVisible(false);
	}
	
	/**
	 * Remove all of this window's source movieclips.(also the components in this window will be removed too)
	 */
	public function dispose():Void{
		if(isDisplayable()){
			visible = false;
			getPopupsVector().remove(this);
			getPopupOwner().removeEventListener(listenerToOwner);
			
			//dispose owned windows
			var owned:Array = getOwnedPopups();
			for(var i:Number=0; i<owned.length; i++){
				var w:JPopup = JPopup(owned[i]);
				w.dispose();
			}
			disposeProcess();
			var mcParent:Container = parent;
			removeFromContainer();
			mcParent.destroy();
			ground_mc.unloadMovie();
			ground_mc.removeMovieClip();
			ground_mc = null;
			Stage.removeListener(stageResizeListener);
			dispatchEvent(createEventObj(ON_WINDOW_CLOSED));
		}
	}
	
	/**
	 * override this method to do process when disposing
	 */
	private function disposeProcess():Void{
	}
	
	/**
	 * Causes this Popup to be sized to fit the preferred size and layouts of its subcomponents.
	 */
	public function pack():Void{
		setSize(getPreferredSize());
	}
	
	/**
	 * If this Popup is visible, sends this Popup to the back and may cause it to lose 
	 * focus or activation if it is the focused or active Popup.
	 * <p>Infact this sends this JPopup to the back of all the MCs in its owner's MC
	 *  except it's owner's root_mc, it's owner is always below it.<br>
	 * @see #toFront()
	 */
	public function toBack():Void{
		if(displayable && visible){
			if(!DepthManager.isBottom(ground_mc, getOwnerRootMC())){
				DepthManager.bringToBottom(ground_mc, getOwnerRootMC());
			}
		}
	}
	
	/**
	 * If this Popup is visible, brings this Popup to the front and may make it the focused Popup.
	 * <p>Infact this brings this JPopup to the front in his owner, all owner's MovieClips' front.
	 * @see #toBack()
	 */
	public function toFront():Void{
		if(displayable && visible){
			if(!DepthManager.isTop(ground_mc)){
				DepthManager.bringToTop(ground_mc);	
			}
		}
	}	
	
	/**
	 * Return an array containing all the windows this window currently owns.
	 */
	public function getOwnedPopups():Array{
		return getOwnedPopupsWithOwner(this);
	}
	
	private static function getPopupsVector():Vector{
		if(popups == undefined){
			popups = new Vector();
		}
		return popups;
	}
	
	/**
	 * Returns all displable windows currently. A window was disposed or destroied will not 
	 * included by this array.
	 * @return all displable windows currently.
	 */
	public static function getPopups():Array{
		return getPopupsVector().toArray();
	}
	
	/**
	 * getOwnedPopupsWithOwner(owner:JPopup)<br>
	 * getOwnedPopupsWithOwner(owner:MovieClip)
	 * <p>
	 * Returns owned windows of the specifid owner.
	 * @return owned windows of the specifid owner.
	 */
	public static function getOwnedPopupsWithOwner(owner:Object):Array{
		var ws:Array = new Array();
		for(var i:Number=0; i<getPopupsVector().size(); i++){
			var w:JPopup = JPopup(getPopupsVector().get(i));
			if(w.getOwner() === owner){
				ws.push(w);
			}
		}
		return ws;
	}	
	
	/**
	 * Returns the window's ancestor movieclip which it/it's owner is created on.
	 * @return the ancestor movieclip of this window 
	 */
	public function getPopupAncestorMC():MovieClip{
		var ow:JPopup = this;
		while(ow.getPopupOwner() != null){
			ow = ow.getPopupOwner();
		}
		return MovieClip(ow.getOwner());
	}
	
	/**
	 * This is just for PopupUI to draw modalMC face.
	 * @return the modal mc
	 */
	public function getModalMC():MovieClip{
		return modalMC;
	}
	
	/**
	 * Resets the modal mc to cover the hole screen
	 */
	public function resetModalMC():Void{
		if(!isModal()){
			modalMC._y = Number.MAX_VALUE;
			modalMC._visible = false;
			return;
		}
		modalMC._visible = true;
		var globalBounds:Rectangle = ASWingUtils.getVisibleMaximizedBounds(ground_mc);
		modalMC._width = Stage.width+200;
		modalMC._height = Stage.height+200;
		modalMC._x = globalBounds.x - getX() - 100;
		modalMC._y = globalBounds.y - getY() - 100;
	}
	
	private function __resetModalMCOnMouseMoved():Void{
		resetModalMC();
	}
			
	private function initialize():Void{
		super.initialize();
		ground_mc._visible = isVisible();
	}
	
	public function doLayout():Void{
		super.doLayout();
		ground_mc._visible = isVisible();
	}
		
	/**
	 * Returns the component's mc's depth
	 */
	public function getDepth():Number{
		return ground_mc.getDepth();
	}
	
	/**
	 * Swap the component's mc's depth
	 */
	public function swapDepths(target):Void{
		ground_mc.swapDepths(target);
	}
	
	public function startDrag():Void{
		if(mouseMoveListener == null){
			mouseMoveListener = new Object();
			mouseMoveListener.onMouseMove = Delegate.create(this, __onDrag);
		}
		root_mc.startDrag(false);
		Mouse.removeListener(mouseMoveListener);
		Mouse.addListener(mouseMoveListener);
	}
	
	private function __onDrag():Void{
		var oldPos:Point = new Point(bounds.x, bounds.y);
		
		bounds.x = root_mc._x;
		bounds.y = root_mc._y;
		
		var newPos:Point = bounds.getLocation();
		if(!newPos.equals(oldPos)){
			dispatchEvent(createEventObj(ON_MOVED, oldPos, newPos));
			updateAfterEvent();
		}
	}
	
	public function stopDrag():Void{
		root_mc.stopDrag();
		Mouse.removeListener(mouseMoveListener);
		__onDrag();
	}
	
    /**
     * Does nothing because Popups must always be roots of a focus traversal
     * cycle. The passed-in value is ignored.
     *
     * @param focusCycleRoot this value is ignored
     * @see #isFocusCycleRoot()
     * @see Container#setFocusTraversalPolicy()
     * @see Container#getFocusTraversalPolicy()
     */
    public function setFocusCycleRoot(focusCycleRoot:Boolean):Void {
    	this.focusCycleRoot = true;
    }
  
    /**
     * Always returns <code>true</code> because all Popups must be roots of a
     * focus traversal cycle.
     *
     * @return <code>true</code>
     * @see #setFocusCycleRoot()
     * @see Container#setFocusTraversalPolicy()
     * @see Container#getFocusTraversalPolicy()
     */
    public function isFocusCycleRoot():Boolean {
		return true;
    }
  
    /**
     * Always returns <code>null</code> because Popups have no ancestors; they
     * represent the top of the Component hierarchy.
     *
     * @return <code>null</code>
     * @see Container#isFocusCycleRoot()
     */
    public function getFocusCycleRootAncestor():Container {
		return null;
    }
	
	/**
	 * Makes the focus tranfer into this popup
	 */
	public function focusAtThisPopup():Void{
		var focusOwner:Component = FocusManager.getCurrentManager().getFocusOwner();
		var currentFocusPopup:JPopup = ASWingUtils.getPopupAncestor(focusOwner);
		if(currentFocusPopup != this){
			var newFocusOwner:Component = getFocusTraversalPolicy().getDefaultComponent(this);
			if(newFocusOwner != null){
				newFocusOwner.requestFocus();
			}
		}
	}
	
	//--------------------------------------------------------
	
	private function createMCForOwnedPopup():MovieClip{
		return creater.createMC(ground_mc, "ground_mc");
	}
	
	/**
	 * Return the root_mc of the window's owner window.
	 * @return the root_mc of the window's owner window, undefined if 
	 * it has not a window owner.
	 */
	private function getOwnerRootMC():MovieClip{
		return getPopupOwner().root_mc;
	}
	
	private function createPopupContents():Void{
		if(ObjectUtils.isMovieClip(owner)){
			var ownerMC:MovieClip = MovieClip(owner);
			ground_mc = creater.createMC(ownerMC, "ground_mc");
		}else if(owner instanceof JPopup){
			var jwo:JPopup = JPopup(owner);
			jwo.show();
			ground_mc = jwo.createMCForOwnedPopup();
			jwo.addEventListener(listenerToOwner);
		}else{
			trace(this + " JPopup's owner is not a mc or JPopup, owner is : " + owner);
			throw new Error(this + " JPopup's owner is not a mc or JPopup, owner is : " + owner);
		}
		if(lastLAF != UIManager.getLookAndFeel()){
			ASWingUtils.updateComponentTreeUI(this);
			lastLAF = UIManager.getLookAndFeel();
		}
		var groundPanel:MCPanel = new MCPanel(ground_mc, 10000, 10000);
		groundPanel.append(this); //MCPanel is just a tool to make JPopup created
	}
	
	private function create():Void{
		if(getPopupsVector().contains(this)){
			getPopupsVector().remove(this);
		}
		getPopupsVector().append(this);
		createModalMC();
		super.create();
	}
	
	private function createModalMC():Void{
		modalMC = creater.createMC(root_mc, "modal_mc");
		modalMC.tabEnabled = false;
		modalMC.onPress = null;
		modalMC.onRelease = null;
		modalMC._visible = modal;
    	modalMC.clear();
    	var modalColor:ASColor = new ASColor(0, 0);
		var g:Graphics = new Graphics(modalMC);
		g.fillRectangle(new SolidBrush(modalColor), 0, 0, 1, 1);
	}	
	
	private function onStageResized():Void
	{
		resetModalMC();
	}
}