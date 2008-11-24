/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.DefaultFocusTraversalPolicy;
import org.aswing.FocusTraversalPolicy;
import org.aswing.JWindow;
import org.aswing.util.Delegate;
import org.aswing.util.DepthManager;

/**
 * FocusManager manages all the when a component should receive focus, i.e if it
 * can.
 * @author firdosh
 * @author iiley
 */
class org.aswing.FocusManager{
    /**
     * The identifier for the Forward focus traversal keys.
     *
     * @see #setDefaultFocusTraversalKeys()
     * @see #getDefaultFocusTraversalKeys()
     * @see Component#setFocusTraversalKeys()
     * @see Component#getFocusTraversalKeys()
     */
	public static var FORWARD_TRAVERSAL_KEYS:Number = 0;
    /**
     * The identifier for the Backward focus traversal keys.
     *
     * @see #setDefaultFocusTraversalKeys()
     * @see #getDefaultFocusTraversalKeys()
     * @see Component#setFocusTraversalKeys()
     * @see Component#getFocusTraversalKeys()
     */
	public static var BACKWARD_TRAVERSAL_KEYS:Number = 1;
    /**
     * The identifier for the Up Cycle focus traversal keys.
     *
     * @see #setDefaultFocusTraversalKeys()
     * @see #getDefaultFocusTraversalKeys()
     * @see Component#setFocusTraversalKeys()
     * @see Component#getFocusTraversalKeys()
     */
	public static var UP_CYCLE_TRAVERSAL_KEYS:Number = 2;
    /**
     * The identifier for the Down Cycle focus traversal keys.
     *
     * @see #setDefaultFocusTraversalKeys()
     * @see #getDefaultFocusTraversalKeys()
     * @see Component#setFocusTraversalKeys()
     * @see Component#getFocusTraversalKeys()
     */
	public static var DOWN_CYCLE_TRAVERSAL_KEYS:Number = 3;
    /**
     * The identifiers length.
     */
	public static var TRAVERSAL_KEY_LENGTH:Number = (DOWN_CYCLE_TRAVERSAL_KEYS + 1);
	
	/**
	 * Then tab index range(start index, inclusive) of AsWing component using
	 */
	public static var ASWING_TAB_INDEX_START:Number = 10000;
	/**
	 * Then tab index range(end inde, inclusive) of AsWing component using
	 */	
	public static var ASWING_TAB_INDEX_END:Number   = 20000;
	
	private static var instance:FocusManager;
	private static var traversalEnabled:Boolean = true;
	
	private static var oldFocusOwner:Component;
	private static var focusOwner:Component;
	private static var activeWindow:JWindow;
	private static var currentFocusCycleRoot:Container;
	private static var keyListener:Object;
	private static var mouseListener:Object;
	
	private static var lastFocusRecivedComponentObject:Object;
	private static var justSystemTabDirection:Number = 0;
	
	private var defaultPolicy:FocusTraversalPolicy;
	private var defaultFocusTraversalKeys:Array;
	private var traversing:Boolean;
	
	private var focusFrontHolderMC:MovieClip;
	private var focusBackHolderMC:MovieClip;
	
	
	public function FocusManager(){
		traversing = false;
		defaultPolicy = new DefaultFocusTraversalPolicy();
		defaultFocusTraversalKeys = [[Key.TAB], [Key.SHIFT, Key.TAB], [], []];
		
		if(keyListener == undefined){
			keyListener = {onKeyDown:Delegate.create(FocusManager, __onKeyDown)};
			if(traversalEnabled){
				Key.addListener(keyListener);
			}
		}
		if(mouseListener == undefined){
			mouseListener = {onMouseDown:Delegate.create(FocusManager, __onMouseDown)};
			Mouse.addListener(mouseListener);
		}
		
		focusFrontHolderMC = _root.createEmptyMovieClip("aswing_focusFrontHolderMC", DepthManager.getNextAvailableDepth(_root));
		focusFrontHolderMC.onPress = function(){};
		focusFrontHolderMC.tabEnabled = true;
		focusFrontHolderMC.tabIndex = ASWING_TAB_INDEX_START;
		focusBackHolderMC = _root.createEmptyMovieClip("aswing_focusBackHolderMC", DepthManager.getNextAvailableDepth(_root));
		focusBackHolderMC.onPress = function(){};
		focusBackHolderMC.tabEnabled = true;
		focusBackHolderMC.tabIndex = ASWING_TAB_INDEX_END;
	}
	
	/**
	 * Makes specified component obj(a movie clip or a text field) to receive this flash internal focus.
	 * @return the flash system tab navigating direction, 0 if it is not navigated by system tab.
	 */
	public function receiveFocus(obj:Object):Number{
		lastFocusRecivedComponentObject.tabIndex = undefined;
		lastFocusRecivedComponentObject.tabEnabled = false;
		obj.tabEnabled = true;
		obj.tabIndex = ASWING_TAB_INDEX_START + Math.floor((ASWING_TAB_INDEX_END - ASWING_TAB_INDEX_START)/2);
		if(justSystemTabDirection == 0){
			if(eval(Selection.getFocus()) != obj){
				Selection.setFocus(obj);
			}
		}else if(justSystemTabDirection < 0){
			Selection.setFocus(focusBackHolderMC);
		}else{
			Selection.setFocus(focusFrontHolderMC);
		}
		lastFocusRecivedComponentObject = obj;
		var dir:Number = justSystemTabDirection;
		justSystemTabDirection = 0;
		return dir;
	}
	
	private static function __onMouseDown():Void{
		instance.__noticeWhenMouseDown();
	}
	
	private static function __onKeyDown():Void{
		if(Key.isDown(Key.TAB)){
			if(Key.isDown(Key.SHIFT)){
				justSystemTabDirection = -1;
			}else{
				justSystemTabDirection = 1;
			}
		}else{
			justSystemTabDirection = 0;
		}
		var fonwer:Component = instance.getFocusOwner();
		var forwardKeys:Array = fonwer.getFocusTraversalKeys(FORWARD_TRAVERSAL_KEYS);
		var backwardKeys:Array = fonwer.getFocusTraversalKeys(BACKWARD_TRAVERSAL_KEYS);
		var upCycleKeys:Array = fonwer.getFocusTraversalKeys(UP_CYCLE_TRAVERSAL_KEYS);
		var downCycleKeys:Array = fonwer.getFocusTraversalKeys(DOWN_CYCLE_TRAVERSAL_KEYS);
		if(isDownAll(upCycleKeys)){
			instance.setTraversing(true);
			instance.upFocusCycle();
		}else if(isDownAll(downCycleKeys)){
			instance.setTraversing(true);
			instance.downFocusCycle();
		}else if(isDownAll(backwardKeys)){
			instance.setTraversing(true);
			instance.focusPrevious();
		}else if(isDownAll(forwardKeys)){
			instance.setTraversing(true);
			instance.focusNext();
		}
	}
	private static function isDownAll(keys:Array):Boolean{
		if(keys.length <= 0 || keys == null){
			return false;
		}
		for(var i:Number=0; i<keys.length; i++){
			if(!Key.isDown(keys[i])){
				return false;
			}
		}
		return true;
	}
	
	private function __noticeWhenMouseDown():Void{
		traversing = false;
		getFocusOwner().clearFocusGraphicsByUI();
	}
	
	/**
	 * Returns if the focus is traversing by keys.
	 * <p>
	 * Once when focus traversed by FocusTraversalKeys this is turned on, 
	 * true will be returned. Once when Mouse is down, this will be turned off, 
	 * false will be returned.
	 * @return true if the focus is traversing by FocusTraversalKeys, otherwise returns false.
	 * @see #getDefaultFocusTraversalKeys()
	 * @see Component#getFocusTraversalKeys()
	 */
	public function isTraversing():Boolean{
		return traversing;
	}
	
	/**
	 * Sets if the focus is traversing by keys.
	 * <p>
	 * By default, the traversing property will only be set true when TRAVERSAL_KEYS down.
	 * If your component need view focus rect, you should set it to true when your component's 
	 * managed key down. 
	 * @param b true tag traversing to be true, false tag traversing to be false
	 * @see #isTraversing()
	 */
	public function setTraversing(b:Boolean):Void{
		traversing = b;
	}
		
	/**
	 * Disables the traversal by keys pressing.
	 * If this method called, TAB... keys will not effect the focus traverse. And component will not fire 
	 * any Key events when there are focused and key pressed.
	 */
	public static function disableTraversal():Void{
		Key.removeListener(keyListener);
		traversalEnabled = false;
	}
	
	/**
	 * Enables the traversal by keys pressing.
	 * If this method called, TAB... keys will effect the focus traverse. And component will fire 
	 * Key events when there are focused and key pressed.
	 */
	public static function enableTraversal():Void{
		Key.removeListener(keyListener);
		Key.addListener(keyListener);
		traversalEnabled = true;
	}
	
	/**
	 * Returns whether the traversal enabled.
	 * @return whether the traversal enabled.
	 * @see #disableTraversal()
	 * @see #enableTraversal()
	 */
	public static function isTraversalEnabled():Boolean{
		return traversalEnabled;
	}
	
    /**
     * Returns the current FocusManager instance
     *
     * @return this the current FocusManager instance
     * @see #setCurrentManager
     */
	public static function getCurrentManager():FocusManager{		
		if(instance == null){
			instance = new FocusManager();	
		}		
		return instance;
	}
	
	/**
     * Sets the current FocusManager instance. If null is specified, 
     * then the current FocusManager is replaced with a new instance of FocusManager.
     * 
     * @param newManager the new FocusManager
     * @see #getCurrentManager
     * @see org.aswing.FocusManager
	 */
	public static function setCurrentManager(newManager:FocusManager):Void{
		if(newManager == null){
			newManager = new FocusManager();
		}
		instance = newManager;
	}

	/**
     * Returns the focused component.
     *
     * @return the focused component.
     */
	public static function getFocusedComponent():Component{
		return focusOwner;
	}

	/**
     * Returns the previous focused component.
     *
     * @return the previous focused component.
     */
	public static function getPreviousFocusedComponent():Component{
		return oldFocusOwner;
	}

	/**
     * Returns the focus owner.
     *
     * @return the focus owner.
     * @see #setFocusOwner()
     */
	public function getFocusOwner():Component{
		return focusOwner;
	}
	
	/**
     * Sets the focus owner. The operation will be cancelled if the Component
     * is not focusable.
     * <p>
     * This method does not actually set the focus to the specified Component.
     * It merely stores the value to be subsequently returned by
     * <code>getFocusOwner()</code>. Use <code>Component.requestFocus()</code>
     * or <code>Component.requestFocusInWindow()</code> to change the focus
     * owner.
     *
     * @param focusOwner the focus owner
     * @see #getFocusOwner
     * @see Component#requestFocus()
     * @see Component#requestFocusInWindow()
     * @see Component#isFocusable
	 */
	public function setFocusOwner(newFocusOwner:Component):Void{
		//var oldFocusOwner:Component = null;
		var shouldFire:Boolean = false;
		if ((newFocusOwner == null) || newFocusOwner.isFocusable()){
			oldFocusOwner = getFocusOwner();
			focusOwner = newFocusOwner;
			if (((newFocusOwner != null) && ((getCurrentFocusCycleRoot() == null) || (! newFocusOwner.isFocusCycleRootOfContainer(getCurrentFocusCycleRoot()))))){
				var rootAncestor:Container = newFocusOwner.getFocusCycleRootAncestor();
				if (((rootAncestor == null) && (newFocusOwner instanceof JWindow))){
					rootAncestor = Container(newFocusOwner);
				}
				if ((rootAncestor != null)){
					setCurrentFocusCycleRoot(rootAncestor);
				}
			}
		}
	}	
	
	/**
     * Returns the active Window.
     * The active Window is always either the focused Window, or the first
     * Window that is an owner of the focused Window.
     *
     * @return the active Window
     * @see #setActiveWindow()
	 */
	public function getActiveWindow():JWindow{
		return activeWindow;
	}
	
	/**
     * Sets the active Window. The active Window is always either the 
     * focused Window, or the first Window that is an owner of the focused Window.
     * <p>
     * This method does not actually change the active Window . 
     * It merely stores the value to be
     * subsequently returned by <code>getActiveWindow()</code>. Use
     * <code>Component.requestFocus()</code> or
     * <code>Component.requestFocusInWindow()</code> or
     * <code>JWindow.setActive()</code>to change the active
     * Window.
     *
     * @param activeWindow the active Window
     * @see #getActiveWindow()
     * @see Component#requestFocus()
     * @see Component#requestFocusInWindow()
     * @see JWindow#setActive()
	 */
	public function setActiveWindow(newActiveWindow:JWindow):Void{
		activeWindow = newActiveWindow;
	}	
	
	/**
     * Returns the current focus cycle root. If the focus owner is itself
     * a focus cycle root, then it may be ambiguous as to which Components
     * represent the next and previous Components to focus during normal focus
     * traversal. In that case, the current focus cycle root is used to
     * differentiate among the possibilities.
     * <p>
     * This method is intended to be used only by FocusManagers and
     * focus implementations. It is not for general client use.
     *
     * @return the current focus cycle root.
     * @see #setCurrentFocusCycleRoot()
	 */
	public function getCurrentFocusCycleRoot():Container{
		return currentFocusCycleRoot;
	}
	
	/**
     * Sets the current focus cycle root. If the focus owner is itself a focus
     * cycle root, then it may be ambiguous as to which Components represent
     * the next and previous Components to focus during normal focus traversal.
     * In that case, the current focus cycle root is used to differentiate
     * among the possibilities.
     * <p>
     * This method is intended to be used only by FocusManagers and
     * focus implementations. It is not for general client use.
     *
     * @param newFocusCycleRoot the new focus cycle root
     * @see #getCurrentFocusCycleRoot()
     */
	public function setCurrentFocusCycleRoot(root:Container):Void{
		currentFocusCycleRoot = root;
	}
	
    /**
     * Focuses the Component after aComponent, typically based on a
     * FocusTraversalPolicy.
     *
     * @param aComponent the Component that is the basis for the focus
     *        traversal operation
     * @see FocusTraversalPolicy
     */
	public function focusNextOfComponent(aComponent:Component):Void{
        if (aComponent != null) {
            aComponent.transferFocus();
        }
	}
    /**
     * Focuses the Component before aComponent, typically based on a
     * FocusTraversalPolicy.
     *
     * @param aComponent the Component that is the basis for the focus
     *        traversal operation
     * @see FocusTraversalPolicy
     */	
	public function focusPreviousOfComponent(aComponent:Component):Void{ 
        if (aComponent != null) {
            aComponent.transferFocusBackward();
        }
	}
    /**
     * Moves the focus up one focus traversal cycle. Typically, the focus owner
     * is set to aComponent's focus cycle root, and the current focus cycle
     * root is set to the new focus owner's focus cycle root. If, however,
     * aComponent's focus cycle root is a Window, then typically the focus
     * owner is set to the Window's default Component to focus, and the current
     * focus cycle root is unchanged.
     *
     * @param aComponent the Component that is the basis for the focus
     *        traversal operation
     */	
	public function upFocusCycleOfComponent(aComponent:Component):Void{ 
        if (aComponent != null) {
            aComponent.transferFocusUpCycle();
        }
	}
    /**
     * Moves the focus down one focus traversal cycle. Typically, if
     * aContainer is a focus cycle root, then the focus owner is set to
     * aContainer's default Component to focus, and the current focus cycle
     * root is set to aContainer. If aContainer is not a focus cycle root, then
     * no focus traversal operation occurs.
     *
     * @param aContainer the Container that is the basis for the focus
     *        traversal operation
     */	
	public function downFocusCycleOfContainer(aContainer:Container):Void{ 
        if (aContainer != null && aContainer.isFocusCycleRoot()) {
            aContainer.transferFocusDownCycle();
        }
	}
    /**
     * Focuses the Component after the current focus owner.
     * @see #focusNextOfComponent()
     */	
	public function focusNext():Void{
		focusNextOfComponent(getFocusOwner());
	}
    /**
     * Focuses the Component before the current focus owner.
     * @see #focusPreviousOfComponent()
     */	
	public function focusPrevious():Void{
		focusPreviousOfComponent(getFocusOwner());
	}
    /**
     * Moves the focus up one focus traversal cycle from the current focus
     * owner. Typically, the new focus owner is set to the current focus
     * owner's focus cycle root, and the current focus cycle root is set to the
     * new focus owner's focus cycle root. If, however, the current focus
     * owner's focus cycle root is a Window, then typically the focus owner is
     * set to the focus cycle root's default Component to focus, and the
     * current focus cycle root is unchanged.
     * @see #upFocusCycleOfComponent()
     */	
	public function upFocusCycle():Void{
		upFocusCycleOfComponent(getFocusOwner());
	}
    /**
     * Moves the focus down one focus traversal cycle from the current focus
     * owner, if and only if the current focus owner is a Container that is a
     * focus cycle root. Typically, the focus owner is set to the current focus
     * owner's default Component to focus, and the current focus cycle root is
     * set to the current focus owner. If the current focus owner is not a
     * Container that is a focus cycle root, then no focus traversal operation
     * occurs.
     * @see #downFocusCycleOfComponent()
     */	
	public function downFocusCycle():Void{
		var focusOwner:Component = getFocusOwner();
		if (focusOwner instanceof Container){
			downFocusCycleOfContainer(Container(focusOwner));
		}
	}
	
	/**
     * Returns the default FocusTraversalPolicy. Top-level components 
     * use this value on their creation to initialize their own focus traversal
     * policy by explicit call to Container.setFocusTraversalPolicy.
     *
     * @return the default FocusTraversalPolicy. null will never be returned.
     * @see #setDefaultFocusTraversalPolicy()
     * @see Container#setFocusTraversalPolicy()
     * @see Container#getFocusTraversalPolicy()
     */
	public function getDefaultFocusTraversalPolicy():FocusTraversalPolicy{
		return defaultPolicy;
	}

    /**
     * Sets the default FocusTraversalPolicy. Top-level components 
     * use this value on their creation to initialize their own focus traversal
     * policy by explicit call to Container.setFocusTraversalPolicy.
     * Note: this call doesn't affect already created components as they have 
     * their policy initialized. Only new components will use this policy as
     * their default policy.
     *
     * @param defaultPolicy the new, default FocusTraversalPolicy, if it is null, nothing will be done
     * @see #getDefaultFocusTraversalPolicy()
     * @see Container#setFocusTraversalPolicy()
     * @see Container#getFocusTraversalPolicy()
     */	
	public function setDefaultFocusTraversalPolicy(newDefaultPolicy:FocusTraversalPolicy):Void{
		if (newDefaultPolicy != null){
			defaultPolicy = newDefaultPolicy;
		}
	}
	
	/**
     * Returns a Array of default focus traversal keys for a given traversal
     * operation. This traversal key Set will be in effect on all Windows that
     * have no such Set of their own explicitly defined. This Set will also be
     * inherited, recursively, by any child Component of those Windows that has
     * no such Set of its own explicitly defined. (See
     * <code>setDefaultFocusTraversalKeys</code> for a full description of each
     * operation.)
     *
     * @param id one of KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
     *        KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
     *        KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS, or
     *        KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS
     * @return the <code>Array</code> of <code>Number(Key.getCode())</code>s
     *         for the specified operation; <code>null</code> 
     *         will be returned if the id is not defineded TRAVERSAL_KEYS.
     * @see #setDefaultFocusTraversalKeys()
     * @see Component#setFocusTraversalKeys()
     * @see Component#getFocusTraversalKeys()
     */
	public function getDefaultFocusTraversalKeys(id:Number):Array{
		return defaultFocusTraversalKeys[id].concat();
	}
	
    /**
     * Sets the default focus traversal keys for a given traversal operation.
     * This traversal key <code>Array</code> will be in effect on all
     * <code>JWindow</code>s that have no such <code>Array</code> of
     * their own explicitly defined. This <code>Array</code> will also be
     * inherited, recursively, by any child <code>Component</code> of
     * those <code>JWindows</code> that has
     * no such <code>Array</code> of its own explicitly defined.
     * <p>
     * The default values for the default focus traversal keys are
     * implementation-dependent. We recommends that all implementations for a
     * particular native platform use the same default values. The
     * recommendations for Windows and Unix are listed below. These
     * recommendations are used in the default implementations.
     *
     * <table border=1 summary="Recommended default values for focus traversal keys">
     * <tr>
     *    <th>Identifier</th>
     *    <th>Meaning</th>
     *    <th>Default</th>
     * </tr>
     * <tr>
     *    <td><code>KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS</code></td>
     *    <td>Normal forward keyboard traversal</td>
     *    <td><code>TAB</code> on <code>KEY_PRESSED</code></td>
     * </tr>
     * <tr>
     *    <td><code>KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS</code></td>
     *    <td>Normal reverse keyboard traversal</td>
     *    <td><code>SHIFT-TAB</code> on <code>KEY_PRESSED</code></td>
     * </tr>
     * <tr>
     *    <td><code>KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS</code></td>
     *    <td>Go up one focus traversal cycle</td>
     *    <td>none</td>
     * </tr>
     * <tr>
     *    <td><code>KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS</code></td>
     *    <td>Go down one focus traversal cycle</td>
     *    <td>none</td>
     * </tr>
     * </table>
     *
     * To disable a traversal key, use an empty <code>Array</code>;
     * <code>[]</code> is recommended.
     * The priority of traversal keys check is first UP_CYCLE_TRAVERSAL_KEYS,
     * then DOWN_CYCLE_TRAVERSAL_KEYS,BACKWARD_TRAVERSAL_KEYS last FORWARD_TRAVERSAL_KEYS.
     * So, if there's same keys the high priority TRAVERSAL will be considered. 
     * @param id one of
     *        <code>FocusManager.FORWARD_TRAVERSAL_KEYS</code>,
     *        <code>FocusManager.BACKWARD_TRAVERSAL_KEYS</code>,
     *        <code>FocusManager.UP_CYCLE_TRAVERSAL_KEYS</code>, or
     *        <code>FocusManager.DOWN_CYCLE_TRAVERSAL_KEYS</code>.
     *        If it is not one of them, nothing will be done.
     * @param keystrokes the Array of <code>Number(Key.getCode())</code>s for the
     *        specified operation
     * @see #getDefaultFocusTraversalKeys()
     * @see Component#setFocusTraversalKeys()
     * @see Component#getFocusTraversalKeys()
     */	
	public function setDefaultFocusTraversalKeys(id:Number, keys:Array):Void{
		if(id >=0 && id < TRAVERSAL_KEY_LENGTH){
			defaultFocusTraversalKeys[id] = keys.concat();
		}
	}
}

