/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.ASFont;
import org.aswing.ASWingUtils;
import org.aswing.border.Border;
import org.aswing.overflow.ComponentDecorator;
import org.aswing.Container;
import org.aswing.dnd.SourceData;
import org.aswing.ElementCreater;
import org.aswing.EventDispatcher;
import org.aswing.FocusManager;
import org.aswing.FocusTraversalPolicy;
import org.aswing.geom.Dimension;
import org.aswing.geom.Point;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.SolidBrush;
import org.aswing.Icon;
import org.aswing.Insets;
import org.aswing.overflow.JSharedToolTip;
import org.aswing.JWindow;
import org.aswing.MCPanel;
import org.aswing.plaf.ASColorUIResource;
import org.aswing.plaf.ASFontUIResource;
import org.aswing.plaf.ComponentUI;
import org.aswing.RepaintManager;
import org.aswing.util.Delegate;
import org.aswing.util.DepthManager;
import org.aswing.util.HashMap;
import org.aswing.util.MathUtils;
import org.aswing.util.MCUtils;

/**
 * The super class for all UIs.
 * 
 * <p>The maximumSize and minimumSize are the component's represent max or min size.
 * 
 * <p>You can set a Component's size max than its maximumSize, but when it was drawed,
 * it will not max than its maximumSize.Just as its maximumSize and posited itself
 * in that size dimension you just setted. The position is relative to <code>getAlignmentX</code> 
 * and <code>getAlignmentY<code>.
 * 
 * @see #setSize()
 * @see #setMaximuzedSize()
 * @see #getAlignmentX()
 * @see #setActive()
 * 
 * @author iiley
 */
class org.aswing.Component extends EventDispatcher{
	
	/**
	 * When the component was created due to displayable
	 *<br>
	 * onCreated(source:Component)
	 */	
	public static var ON_CREATED:String = "onCreated";	
	/**
	 * When the component was removed due to undisplayable
	 *<br>
	 * onDestroy(source:Component)
	 */	
	public static var ON_DESTROY:String = "onDestroy";
	
	/**
	 * When the component painting.
	 * onPaint(source:Component)
	 */	
	public static var ON_PAINT:String = "onPaint";	
		
	/**
	 * onShown(source:Component)
	 */	
	public static var ON_SHOWN:String = "onShown";	
	
	/**
	 * onHidden(source:Component)
	 */	
	public static var ON_HIDDEN:String = "onHidden";
	
	/**
	 * onMoved(source:Component, oldPos:Point, newPos:Point}
	 */
	public static var ON_MOVED:String = "onMoved";
	
	/**
	 * onResized(source:Component, oldSize:Dimension, newSize:Dimension}
	 */
	public static var ON_RESIZED:String = "onResized";

	
	//---------------------------------------------------
	
	/**
	 * When dragging started if the component is drag enabled.<br>
	 * onDragRecognized(dragInitiator:Component, touchedChild:Component)
	 * <p>
	 * <ul>
	 * <li> dragInitiator the drag initiator component
	 * <li> touchedChild the component that are mouse is pressed and then dragged of the initiator, it maybe the initiator self or its child.
	 * </ul>
	 * @see #isDragEnabled()
	 */
	public static var ON_DRAG_RECOGNIZED:String="onDragRecognized";
		
	/**
	 * onDragEnter(source:Component, dragInitiator:Component, sourceData:SourceData, mousePos:Point)
	 */
	public static var ON_DRAG_ENTER:String="onDragEnter";
		
	/**
	 * onDragOverring(source:Component, dragInitiator:Component, sourceData:SourceData, mousePos:Point)
	 */
	public static var ON_DRAG_OVERRING:String="onDragOverring";
		
	/**
	 * onDragExit(source:Component, dragInitiator:Component, sourceData:SourceData, mousePos:Point)
	 */
	public static var ON_DRAG_EXIT:String="onDragExit";
	
	/**
	 * onDragDrop(source:Component, dragInitiator:Component, sourceData:SourceData, mousePos:Point)
	 */
	public static var ON_DRAG_DROP:String="onDragDrop";
	
	//---------------------------------------------------
	
			
	/**
	 * When the component's state changed.
	 * This is not implemented by every components.
	 * If a component implemented to fire this event, it should 
	 * declare this property again.
	 *<br>
	 * onStateChanged(source:Component)
	 * @see org.aswing.AbstractButton#ON_STATE_CHANGED
	 * @see org.aswing.ButtonModel#addChangeListener()
	 * @see org.aswing.BoundedRangeModel#addChangeListener()
	 */	
	public static var ON_STATE_CHANGED:String = "onStateChanged";	
	/**
	 * When the component was pressed<br>
	 * onPress(source:Component)
	 */	
	public static var ON_PRESS:String = "onPress";
	/**
	 * When the component was released<br>
	 * onRelease(source:Component)
	 */	
	public static var ON_RELEASE:String = "onRelease";	

	/**
	 * When the component was released outside<br>
	 * onReleaseOutSide(source:Component)
	 */	
	public static var ON_RELEASEOUTSIDE:String = "onReleaseOutSide";
	/**
	 * When the component was rollovered<br>
	 * onRollOver(source:Component)
	 */	
	public static var ON_ROLLOVER:String = "onRollOver";
	/**
	 * When the component was rollout<br>
	 * onRollOut(source:Component)
	 */
	public static var ON_ROLLOUT:String = "onRollOut";
	/**
	 * When the component was drag over<br>
	 * onDragOver(source:Component)
	 */
	public static var ON_DRAGOVER:String = "onDragOver";
	/**
	 * When the component was drag out<br>
	 * onDragOut(source:Component)
	 */
	public static var ON_DRAGOUT:String = "onDragOut";
	
	/**
	 * When the component was clicked outside, 
	 * the clickCount was the clicked count of counted continuously<br>
	 * onClicked(source:Component, clickCount:Number)
	 */
	public static var ON_CLICKED:String = "onClicked";
	
	/**
	 * When the component gained the focus from it is not the focus owner<br>
	 * onFocusGained(source:Component)
	 */
	public static var ON_FOCUS_GAINED:String = "onFocusGained";
	/**
	 * When the component lost the focus from it was the focus owner<br>
	 * onFocusLost(source:Component)
	 */
	public static var ON_FOCUS_LOST:String = "onFocusLost";
	
	/**
	 * When the component was focused and <code>FocusManager.isTraversalEnabled()</code> and a keybord key is down<br>
	 * onKeyDown(source:Component)
	 */
	public static var ON_KEY_DOWN:String = "onKeyDown";
	/**
	 * When the component was focused and <code>FocusManager.isTraversalEnabled()</code> and a keybord key is up<br>
	 * onKeyUp(source:Component)
	 */
	public static var ON_KEY_UP:String = "onKeyUp";
	/**
	 * When the component was focused and a mouse wheel rolling<br>
	 * delta as same as Mouse Listener.onMouseWheel in flash api<br>
	 * onMouseWheel(source:Component, delta:Number)
	 */
	public static var ON_MOUSE_WHEEL:String = "onMouseWheel";
	
	/**
	 * The max interval time to judge whether click was continuously.
	 */
	public static var MAX_CLICK_INTERVAL:Number = 400;
	//------------------------------------------------------
 	private static var PATH_TO_OWNER_COMPONENT:String = "_aswing_p_t_o_c_";
 	
	private static var idCounter:Number = 0;
	//NOTE REMOVE THIS 
	//private var tabCounter:Number = 0;
	
	public static function get creater():ElementCreater{
		return ElementCreater.getInstance();
	}
	
	private static var keyListener:Object;
	private static var mouseListener:Object;
	
	private static var defaultAntiBlurring:Boolean = true;
	private static var defaultCashePresSizes:Boolean = true;
	private var antiBlurring:Boolean;
	
	private var ui:ComponentUI;
	private var uiProperties:Object; //a map contains ui properties
	//ui properties
	private var border:Border;
	private var font:ASFont;
	private var background:ASColor;
	private var foreground:ASColor;
	private var opaque:Boolean;
	private var focusable:Boolean;
	//-------------
	
	private var root_mc:MovieClip; //the component mc's root
	private var clip_mc:MovieClip; //the component's clip(mask rect) mc
	private var trigger_mc:MovieClip; //the trigger for the press/roll... events
	private var target_mc:MovieClip; //the component's content primary mc
	
	private var clipBounds:Rectangle;
	
	private var lastClickTime:Number;
	private var clickCount:Number;
	private static var pressingPoint:Point;
	private static var pressingComponent:Component;
    
	private var name:String;
	private var id:Object;
	private var awmlID:String;
	private var awmlIndex:Number;
	private var awmlNamespace:String;
	private var bounds:Rectangle;
	private var enabled:Boolean;
	private var visible:Boolean;
	private var alpha:Number;
	private var displayable:Boolean;
	private var valid:Boolean;
	private var clipMasked:Boolean;
	private var useHandCursor:Boolean;
	private var triggerEnabled:Boolean;
	private var focusTraversalKeys:Array;
	private var clientProperty:HashMap;
	private var constraints:Object;
	private var toolTipText:String;
	private var dragEnabled:Boolean;
	private var dropTrigger:Boolean;
	private var dragAcceptableInitiator:HashMap;
	
	private var alignmentX:Number;
	private var alignmentY:Number;
	
	private var preferredSize:Dimension;
	private var maximumSize:Dimension;
	private var minimumSize:Dimension;
	private var cachePreferSizes:Boolean;
	private var cachedPreferredSize:Dimension;
	private var cachedMinimumSize:Dimension;
	private var cachedMaximumSize:Dimension;
	
	private var fontValidated:Boolean;
	
	private var parent:Container;
	
	/**
	 * It is Abstract, you must call <code>updateUI</code> if your component 
	 * has a UI, at then end of your construction.
	 */
	private function Component(){
		super();
		setName("Component");
		bounds = new Rectangle(0, 0, 0, 0);
		clipBounds = null;
		enabled = true;
		visible = true;
		clipMasked = true;
		displayable = false;
		valid = false;
		alpha = 100;
		useHandCursor = false;
		triggerEnabled = true;
		alignmentX = 0;
		alignmentY = 0;
		lastClickTime = 0;
		clickCount = 0;
		focusTraversalKeys = null; //need create if needed
		fontValidated = false;
		toolTipText = null;
		dragEnabled = false;
		dropTrigger = false;
		antiBlurring = null;
		cachePreferSizes = null;
		
		if(idCounter < MathUtils.STRING_REPRESENTABLE_MAX){
			id = idCounter + "";
			idCounter++;
		}else{
			id = new Object();
		}
		
		initSingletonListeners();
		
		//init for ui properties 
		uiProperties = new Object();
		uiProperties["opaque"] = false;
		uiProperties["focusable"] = true;
		opaque = undefined; //undefined means not defined, so will use property get from UI
		focusable = undefined; //...
		
		//default value is a UIResource means as same as got from UI
		border = undefined;
		font = ASFontUIResource.createResourceFont(ASFont.getASFont());
		background = ASColorUIResource.createResourceColor(ASColor.WHITE);
		foreground = ASColorUIResource.createResourceColor(ASColor.BLACK);
	}
	
	private static function initSingletonListeners():Void{
		if(keyListener == undefined){
			keyListener = {onKeyDown:Delegate.create(Component, ____onKeyDown__),
						   onKeyUp:Delegate.create(Component, ____onKeyUp__)};
			Key.addListener(keyListener);
		}
		if(mouseListener == undefined){
			mouseListener = {onMouseWheel:Delegate.create(Component, ____onMouseWheel__), 
							 onMouseMove:Delegate.create(Component, ____onMouseMove__), 
							 onMouseUp:Delegate.create(Component, ____onMouseUp__)};
			Mouse.addListener(mouseListener);
		}
	}
	private static function ____onMouseWheel__(delta:Number):Void{
		var focusOwner:Component = FocusManager.getCurrentManager().getFocusOwner();
		if(focusOwner.isShowing()){
			focusOwner.fireMouseWheelEvent(delta);
		}
	}
	private static function ____onMouseMove__():Void{
		if(pressingComponent != null && pressingPoint != null){
			var p:Point = pressingComponent.getMousePosition();
			if(p.distance(pressingPoint) > 1){//draged mouse more than 1 pixel, here we consider to drag
				var dragC:Component = pressingComponent;
				while(dragC != null && !dragC.isDragEnabled()){
					dragC = dragC.getParent();
				}
				if(dragC != null){
					dragC.fireDragRecognizedEvent(pressingComponent);
				}
				pressingComponent = null;
				pressingPoint = null;
			}
		}
	}
	private static function ____onMouseUp__():Void{
		pressingPoint = null;
		pressingComponent = null;
	}
	private static function ____onKeyDown__():Void{
		if(FocusManager.isTraversalEnabled()){
			var focusOwner:Component = FocusManager.getCurrentManager().getFocusOwner();
			if(focusOwner.isShowing()){
				focusOwner.fireKeyDownEvent();
			}
		}
	}
	private static function ____onKeyUp__():Void{
		if(FocusManager.isTraversalEnabled()){
			var focusOwner:Component = FocusManager.getCurrentManager().getFocusOwner();
			if(focusOwner.isShowing()){
				focusOwner.fireKeyUpEvent();
			}
		}
	}

	/**
	 * Returns the component's id.(Type Object(usually a string))
	 * Each component have their owner different id. This id is generated by Component constructor,
	 * you should not to modify its value.
	 */
	public function getID():Object{
		return id;
	}
	    
	/**
	 * Sets ID used to identify components created from AWML. Used to obtain components through 
	 * {@link org.aswing.awml.AwmlManager}. You should never modify this value.
	 * 
	 * @param id the component's AWML ID
	 */
	public function setAwmlID(id:String):Void {
		awmlID = id;	
	}

	/**
	 * Returns ID used to identify components created from AWML.
	 * 
	 * @return the AWML ID
	 */
	public function getAwmlID():String {
		return awmlID;	
	}

	/**
	 * Sets namespace used to identify components created from AWML. 
	 * Used to obtain components through {@link org.aswing.awml.AwmlManager}. 
	 * You should never modify this value.
	 * 
	 * @param namespace the new namespace name
	 */
	public function setAwmlNamespace(namespace:String):Void {
		awmlNamespace = namespace;	
	}

	/**
	 * Returns namespace name used to identify components created from AWML.
	 * 
	 * @return the namespace name
	 */
	public function getAwmlNamespace():String {
		return awmlNamespace;	
	}

	/**
	 * Sets ID used to identify components created from AWML. Used to obtain components through 
	 * {@link org.aswing.awml.AwmlManager}. You should never modify this value.
	 * 
	 * @param index the position index of the component
	 */
	public function setAwmlIndex(index:Number):Void {
		awmlIndex = index;	
	}

	/**
	 * Returns position index of the component inside its AWML container.
	 * 
	 * @return the component index in the AWML
	 */
	public function getAwmlIndex():Number {
		return awmlIndex;	
	}
	    
	/**
     * Returns the <code>UIDefaults</code> key used to
     * look up the name of the <code>org.aswing.plaf.ComponentUI</code>
     * class that defines the look and feel
     * for this component.  Most applications will never need to
     * call this method.  Subclasses of <code>Component</code> that support
     * pluggable look and feel should override this method to
     * return a <code>UIDefaults</code> key that maps to the
     * <code>ComponentUI</code> subclass that defines their look and feel.
     *
     * @return the <code>UIDefaults</code> key for a
     *		<code>ComponentUI</code> subclass
     * @see org.aswing.UIDefaults#getUI()
     */
	public function getUIClassID():String{
		return "ComponentUI";
	}
	
	/**
	 * This just for test, every component's root_mc name start with the name set here.
	 * Different Component can have same name, suggest every type of component have a name same to his class name,
	 * or you want to test, you can set any name to any component, but besure that you'd better set the same before it added to a container.
	 * @see #addTo()
	 */
	public function setName(name:String):Void{
		this.name = name;
	}
	
	/**
	 * @see #setName()
	 */
	public function getName():String{
		return name;
	}
	
    /**
     * Resets the UI property to a value from the current look and feel.
     * <code>Component</code> subclasses must override this method
     * like this:
     * <pre>
     *   public void updateUI() {
     *      setUI((SliderUI)UIManager.getUI(this);
     *   }
     *  </pre>
     *
     * @see #setUI()
     * @see org.aswing.UIManager#getLookAndFeel()
     * @see org.aswing.UIManager#getUI()
     */
    public function updateUI():Void{}


    /**
     * Sets the look and feel delegate for this component.
     * <code>Component</code> subclasses generally override this method
     * to narrow the argument type. For example, in <code>JSlider</code>:
     * <pre>
     * public void setUI(SliderUI newUI) {
     *     super.setUI(newUI);
     * }
     *  </pre>
     * <p>
     * Additionally <code>Component</code> subclasses must provide a
     * <code>getUI</code> method that returns the correct type.  For example:
     * <pre>
     * public SliderUI getUI() {
     *     return (SliderUI)ui;
     * }
     * </pre>
     *
     * @param newUI the new UI delegate
     * @see #updateUI()
     * @see UIManager#getLookAndFeel()
     * @see UIManager#getUI()
     */
    public function setUI(newUI:ComponentUI):Void{
        /* We do not check that the UI instance is different
         * before allowing the switch in order to enable the
         * same UI instance *with different default settings*
         * to be installed.
         */
        if (ui != null) {
            ui.uninstallUI(this);
        }
        ui = newUI;
        if (ui != null) {
            ui.installUI(this);
            if(isDisplayable()){
            	ui.create(this);
            }
        }
        revalidate();
        repaint();
    }
    
    /**
     * Returns the default basic ui class for this component.
     * If there is not a ui class specified in L&F for this component, 
     * this method will be called to return a default one.
     * <p>
     * Any component that have default ui should override this method.
     * 
     * @return the default basic ui class. 
     */
    public function getDefaultBasicUIClass():Function{
    	return null;
    }
	
	/**
	 * Sets the UI delegated property value.<br>
	 * If the property value in component is undefined, then the delegated value 
	 * will be used.
	 * @param name the property name
	 * @param value the value 
	 */
	public function setUIProperty(name:String, value):Void{
		uiProperties[name] = value;
	}
	
	/**
	 * Returns the UI delegated property value.
	 * @param name the property name
	 * @return the value of specified ui property
	 */
	public function getUIProperty(name:String){
		return uiProperties[name];
	}
	
	/**
	 * Sets the default value of <code>antiBlurring</code> to all will created components.
	 * Default value is true.
	 * @see #setAntiBlurring()
	 */
	public static function setDefaultAntiBlurring(b:Boolean):Void{
		defaultAntiBlurring = b;
	}
	
	/**
	 * Returns the default value of <code>antiBlurring</code> to all will created components.
	 * Default value is true.
	 * @see #isAntiBlurring()
	 */
	public static function isDefaultAntiBlurring():Boolean{
		return defaultAntiBlurring;
	}
	
	/**
	 * Sets whether to anti blurring.
	 * <p>
	 * Anti-Blurring is that if your componenet's location is not an integer in global space, 
	 * the graphics maybe blurry.
	 * Sets this value to true, component will location it's root_mc to an integer position in global space and 
	 * round it's paint size to integer values too.
	 * Sets this value to false, component will use the location and size to paint directly 
	 * without any change.
	 * <p>
	 * Sets this value to <code>null</code>, component will use default Anti-Blurring value 
	 * returned by <code>isDefaultAntiBlurring()</code>.
	 * The default value is related to <code>isDefaultAntiBlurring()</code>.
	 * @see #setDefaultAntiBlurring()
	 */
	public function setAntiBlurring(b:Boolean):Void{
		if(antiBlurring != b){
			antiBlurring = b;
			revalidate();
			repaint();
		}
	}
	
	/**
	 * Returns whether to anti blurring.
	 * <p>
	 * Anti-Blurring is that if your componenet's location is not an integer in global space, 
	 * the graphics maybe blurry.
	 * If this value is true, component will location it's root_mc to an integer position in global space and 
	 * round it's paint size to integer values too.
	 * If this value is false, component will use the location and size to paint directly without any change.
	 * If this value is <code>null</code>, component will use default 
	 * Anti-Blurring value returned by <code>isDefaultAntiBlurring()</code>.
	 * <p>
	 * The default value is related to <code>isDefaultAntiBlurring()</code>.
	 * @param #setAntiBlurring()
	 * @see #isDefaultAntiBlurring()
	 */
	public function isAntiBlurring():Boolean{
		return (antiBlurring != null) ? antiBlurring : isDefaultAntiBlurring();
	}
	
	/**
	 * Sends this component as a single page to the print spooler. 
	 * This method will call PrintJob.addPage see {@link http://livedocs.macromedia.com/flash/8/main/00002609.html} 
	 * for details.
	 * @param printJob the printJob used to print the component
	 * @param printArea (optional)the print area in this component coordinates, default is the current component size area
	 * @param options (optional)A parameter that specifies whether to print as vector or bitmap, in the following format:
	 * <pre>
	 * {printAsBitmap:Boolean}
	 * </pre>
	 * @return A Boolean value: true if the page is successfully sent to the print spooler; false otherwise.
	 */
	public function print(printJob:PrintJob, printArea:Object, options:Object):Boolean{
		var target:String = root_mc + "";
		if(printArea == null){
			printArea = {xMin:0, xMax:getWidth(), yMin:0, yMax:getHeight()};
		}
		return printJob.addPage(target, printArea, options);
	}
	
	/**
	 * Create and add this component to a Container.
	 * the method must only can call in a Container's method,
	 * else the Container's layout maybe wrong and Container event will not be called
	 * 
	 * If component was added to a displayable container it could be diplayable.
	 * else it could be not diplayable.
	 * @see #isDiplayable()
	 */
	public function addTo(parent:Container):Void{
		//trace("Component.addTo");
		//trace(parent);
		this.parent = parent;
		root_mc = parent.createChildMC(name);
		//trace(root_mc);
		if(root_mc != undefined){
			root_mc[PATH_TO_OWNER_COMPONENT] = this;
			root_mc._x = bounds.x;
			root_mc._y = bounds.y;
			create();
			initialize();
			displayable = true;
			ASWingUtils.addDisplayableComponent(this);
			repaint();
			revalidate();
			dispatchEvent(createEventObj(ON_CREATED));
		}
	}
	
	/**
	 * Destroy and remove this component from its parent(Container).
	 * If it has not parent, call the destroy method to remove it.
	 * @see #destroy()
	 */
	public function removeFromContainer():Void{
		if(parent != null){
			parent.remove(this);
			parent = null;
		}else{
			destroy();
		}
	}
	
	/**
	 * Destroy(Remove) the component's source movie clips.
	 * After this, this component was undisplayable, has no parent, has no child...<br>
	 * When a displayable component be destroied, the event onDestroy will be generated.
	 * <b>
	 * Note:You'd better not call this method directly, if you want to remove a compoent, call
	 * its method removeFromContainer, it will notify its parent to remove and then destroy it.
	 * </b>
	 * @see #removeFromContainer()
	 * @see #ON_DESTROY
	 */
	public function destroy():Void{
		if(root_mc != null){
			var parent_mc:MovieClip = root_mc._parent;
			root_mc.unloadMovie();
			root_mc.removeMovieClip();
			delete parent_mc[root_mc._name];
			root_mc = null;
			displayable = false;
			dispatchEvent(createEventObj(ON_DESTROY));
		}
		ASWingUtils.removeDisplayableComponent(this);
	}
	
	public function getParent():Container{
		return parent;
	}
	
	/**
	 * Set this component to have press/releaseXXx/rollXXx...event functions.
	 */
	private function initActive():Void{
		var detect_mc:MovieClip = this.trigger_mc;
		if(detect_mc != null){
			detect_mc.onRollOver = Delegate.create(this, ____onRollOver);
			detect_mc.onRollOut = Delegate.create(this, ____onRollOut);
			detect_mc.onPress = Delegate.create(this, ____onPress);
			detect_mc.onRelease = Delegate.create(this, ____onRelease);
			detect_mc.onReleaseOutside = Delegate.create(this, ____onReleaseOutside);
			detect_mc.onDragOver = Delegate.create(this, ____onDragOver);
			detect_mc.onDragOut = Delegate.create(this, ____onDragOut);
			detect_mc.enabled = enabled;
		}
		detect_mc._visible = triggerEnabled;
		detect_mc.useHandCursor = useHandCursor;
	}
		
	public function setBorder(b:Border):Void{
		if(border !== b){
			uninstallBorderWhenNextPaint(border);
			border = b;
			repaint();
			revalidate();
		}
	}
	
	public function getBorder():Border{
		return border;
	}

	public function getInsets():Insets{
		if(border == null){
			return new Insets();
		}else{
			return border.getBorderInsets(this, getSize().getBounds());
		}
	}
		
	
	///////
	/**
	 * create others after target_mc created.
	 */
	private function create():Void{
		trigger_mc = creater.createMC(root_mc, "trigger_mc");
		target_mc = creater.createMC(root_mc, "target_mc");
		clip_mc = creater.createMC(root_mc, "clip_mc");
		//focus_mc = creater.createMC(target_mc, "focus_mc");
		ui.create(this);
	}
	
	///////////
	/**
	 * initialize after create
	 */
	private function initialize():Void{
		//paint the clip rect content
		var g:Graphics = new Graphics(trigger_mc);
		g.fillRectangle(new SolidBrush(0, 0), 0, 0, 1, 1);
		g = new Graphics(clip_mc);
		g.fillRectangle(new SolidBrush(0, 100), 0, 0, 1, 1);
		target_mc.setMask(clip_mc);
		
		initActive();
		setEnabled(enabled);
		setClipMasked(clipMasked);
		root_mc._visible = isVisible();
		root_mc._alpha = getAlpha();
	}
	
	/////////
	/**
	 * draw the component interface in specified bounds.
	 * Sub class should override this method if you want to draw your component's face.
	 * @param b this paiting bounds, it is opposite on the component's target_mc,
	 * that mean the bounds.x mean in target_mc's x, not the root_mc's or component's parent's x.
	 */
	private function paint(b:Rectangle):Void{
		target_mc.clear();
		var g:Graphics = new Graphics(target_mc);
		
		ui.paint(this, g, b);
		//paint border at last to make it at the top depth
		if(border != null){
			// not that border is not painted in b, is painted in component's full size bounds
			// because border are the rounds, others will painted in the border's bounds.
			border.paintBorder(this, g, getInsets().getOutsideBounds(b));
		}
		dispatchEvent(createEventObj(ON_PAINT));
	}
	
	/**
	 * Returns a graphics of the Component.
	 */
	public function getGraphics():Graphics{
		return new Graphics(target_mc);
	}
	
	/**
	 * Returns a graphics to draw focus representation. Every time 
	 * this call will clear representation then return a graphics to draw.
	 * <p>
	 * This is not for user use, so do not call this method at you application. 
	 */
	public function getFocusGraphics():Graphics{
		var focusMC:MovieClip = getFocusMC();
		if(focusMC == undefined){
			focusMC = creater.createMCWithName(root_mc, FOCUS_MC_NAME);
		}
		focusMC.clear();
		return new Graphics(focusMC);
	}
	
	/**
	 * Call UI to clear the focus representation of this method.
	 * Some times UI will draw representation at other where, so to ensure 
	 * all will be cleared, call this method instead of <code>clearFocusGraphics</code>.
	 * <p>
	 * This is not for user use, so do not call this method at you application. 
	 */	
	public function clearFocusGraphicsByUI():Void{
		ui.clearFocus(this);
	}
	
	/**
	 * Clear the focus grapics of this component.
	 * <p>
	 * This is not for user use, so do not call this method at you application. 
	 */
	public function clearFocusGraphics():Void{
		getFocusMC().removeMovieClip();
	}
	private static var FOCUS_MC_NAME:String = "asw_focus_des_mc";
	private function getFocusMC():MovieClip{
		return root_mc[FOCUS_MC_NAME];
	}
	
	/**
	 * Redraws the component face next frame.This method can
     * be called often, so it needs to execute quickly.
	 * @see org.aswing.RepaintManager
	 */
	public function repaint():Void{
		RepaintManager.getInstance().addRepaintComponent(this);
	}
	
	/**
	 * Returns the bounds that component should paint in.
	 * <p>
	 * This is same to some paint method param b:Rectangle.
	 * So if you want to paint outside those method, you can get the 
	 * rectangle from here.
	 * 
	 * If this component has a little maximum size, and then current 
	 * size is larger, the bounds return from this method will be related 
	 * to <code>getAlignmentX<code>, <code>getAlignmentY<code> and <code>getMaximumSize<code>.
	 * @return return the rectangle that component should paint in.
	 * @see #getAlignmentX()
	 * @see #getAlignmentY()
	 * @see #getMaximumSize()
	 */
	public function getPaintBounds():Rectangle{
		return getInsets().getInsideBounds(getPaintBoundsInRoot());
	}
	
	/**
	 * Redraw the component face immediately.
	 * @see #repaint()
	 */
	public function paintImmediately():Void{
		if(isDisplayable() && isVisible()){
			var paintBounds:Rectangle = getPaintBoundsInRoot();
			layoutClipAndTrigger(paintBounds);
			paint(getInsets().getInsideBounds(paintBounds));
		}
	}
	
	private function layoutClipAndTrigger(paintBounds:Rectangle):Void{
		if(paintBounds == undefined){
			paintBounds = getPaintBoundsInRoot();
		}else{
			paintBounds = paintBounds.clone();
		}
		if(clipBounds != null){
			paintBounds.x = Math.max(paintBounds.x, clipBounds.x);
			paintBounds.y = Math.max(paintBounds.y, clipBounds.y);
			paintBounds.width = Math.min(paintBounds.width, clipBounds.width);
			paintBounds.height = Math.min(paintBounds.height, clipBounds.height);
		}
		//locate and scale the clip mask
		clip_mc._x = paintBounds.x;
		clip_mc._y = paintBounds.y;
		//paint size maybe larger than component size, if that just mask component size
		clip_mc._width = Math.min(getWidth(), paintBounds.width);
		clip_mc._height = Math.min(getHeight(), paintBounds.height);
		
		trigger_mc._x = paintBounds.x;
		trigger_mc._y = paintBounds.y;
		trigger_mc._width = clip_mc._width;
		trigger_mc._height = clip_mc._height;
	}
	
	/**
	 * setClipBounds(x:Number, y:Number, width:Number, height:Number):Void<br>
	 * setClipBounds(b:Rectangle):Void<br>
	 * setClipBounds(null):Void<br>
	 * Sets the clip bounds, a rectangle mask to make specified bounds visible.
	 * Null to make the componet mask whole rectangle(show all).
	 */
	public function setClipBounds(x, y:Number, width:Number, height:Number):Void{
		var changed:Boolean = false;
		if(x == null && clipBounds != null){
			clipBounds = null;
			changed = true;
		}else{
			var b:Rectangle = new Rectangle(x, y, width, height);
			if(!b.equals(clipBounds)){
				clipBounds = b;
				changed = true;
			}
		}
		if(changed){
			layoutClipAndTrigger();
		}
	}
	
	/**
	 * Returns the clip bounds.
	 * @see #setClipBounds()
	 */
	public function getClipBounds():Rectangle{
		if(clipBounds == null){
			return null;
		}
		return clipBounds.clone();
	}
	
	/**
	 * setClipSize(width:Number, height:Number):Void<br>
	 * setClipSize(b:Dimension):Void<br>
	 * Sets the clip size, a rectangle mask to make specified bounds visible.
	 * This will be only in effect after component created and before next layout time.
	 * @see #setClipBounds()
	 */	
	public function setClipSize(width, height:Number):Void{
		var b:Dimension = new Dimension(width, height);
		clip_mc._width = Math.min(getWidth(), b.width);
		clip_mc._height = Math.min(getHeight(), b.height);
	}
	
	/**
	 * get the simon-pure component paint bounds.
	 * This is include insets range.
	 * @see #getPaintBounds()
	 */
	private function getPaintBoundsInRoot():Rectangle{
		var minSize:Dimension = getMinimumSize();
		var maxSize:Dimension = getMaximumSize();
		var size:Dimension = getSize();
		var paintBounds:Rectangle = new Rectangle(0, 0, size.width, size.height);
		//if it size max than maxsize, draw it as maxsize and then locate it in it size(the size max than maxsize)
		if(size.width > maxSize.width){
			paintBounds.width = maxSize.width;
			paintBounds.x = (size.width-paintBounds.width)*getAlignmentX();
		}
		if(size.height > maxSize.height){
			paintBounds.height = maxSize.height;
			paintBounds.y = (size.height-paintBounds.height)*getAlignmentY();
		}
		//cannot paint its min than minsize
		if(paintBounds.width < minSize.width) paintBounds.width = minSize.width;
		if(paintBounds.height < minSize.height) paintBounds.height = minSize.height;
		
		if(isAntiBlurring()){
			MathUtils.roundRectangle(paintBounds);
		}
		return paintBounds;
	}
	
	/////////
	/**
	 * the Component changed size.
	 * <br>default operation here is : if current size not min than getMinimumSize
	 * call repaint else set target_mc to hide.
	 */
	private function size():Void{
		repaint();
		invalidate();
	}
	
    /**
     * Supports deferred automatic layout.  
     * <p> 
     * Calls <code>invalidate</code> and then adds this component's
     * <code>validateRoot</code> to a list of components that need to be
     * validated.  Validation will occur after all currently pending
     * events have been dispatched.  In other words after this method
     * is called,  the first validateRoot (if any) found when walking
     * up the containment hierarchy of this component will be validated.
     * By default, <code>JPopup</code>, <code>JScrollPane</code>,
     * and <code>JTextField</code> return true 
     * from <code>isValidateRoot</code>.
     * <p>
     * This method will or will not automatically be called on this component 
     * when a property value changes such that size, location, or 
     * internal layout of this component has been affected.But invalidate
     * will do called after thats method, so you want to get the contents of 
     * the GUI to update you should call this method.
     * <p>
     *
     * @see #invalidate()
     * @see #validate()
     * @see #isValidateRoot()
     * @see RepaintManager#addInvalidComponent()
     */
    public function revalidate():Void {
    	invalidate();
    	RepaintManager.getInstance().addInvalidComponent(this);
    }
        
    public function revalidateIfNecessary():Void{
    	RepaintManager.getInstance().addInvalidComponent(this);
    }
    
    /**
     * Invalidates this component.  This component and all parents
     * above it are marked as needing to be laid out.  This method can
     * be called often, so it needs to execute quickly.
     * @see       #validate()
     * @see       #doLayout()
     * @see       org.aswing.LayoutManager
     */
    public function invalidate():Void {
    	valid = false;
    	cachedMaximumSize = null;
    	cachedMinimumSize = null;
    	cachedPreferredSize = null;
    	if(parent != null && parent.isValid()){
    		parent.invalidate();
    	}
    }
        
    /**
     * Ensures that this component has a valid layout.  This method is
     * primarily intended to operate on instances of <code>Container</code>.
     * @see       #invalidate()
     * @see       #doLayout()
     * @see       org.aswing.LayoutManager
     * @see       org.aswing.Container#validate()
     */
    public function validate():Void {
    	if(!valid){
    		doLayout();
    		valid = true;
    	}
    }
	
	/**
	 * If this method returns true, revalidate calls by descendants of this 
	 * component will cause the entire tree beginning with this root to be validated. 
	 * Returns false by default. 
	 * JScrollPane overrides this method and returns true. 
	 * @return always returns false
	 */
	public function isValidateRoot():Boolean{
		return false;
	}
    
    /**
     * Determines whether this component is valid. A component is valid
     * when it is correctly sized and positioned within its parent
     * container and all its children are also valid. 
     * In order to account for peers' size requirements, components are invalidated
     * before they are first shown on the screen. By the time the parent container 
     * is fully realized, all its components will be valid.
     * @return <code>true</code> if the component is valid, <code>false</code>
     * otherwise
     * @see #validate()
     * @see #invalidate()
     */
    public function isValid():Boolean{
    	return valid;
    }
    
    /**
     * layout this component. Locate this component to its location and visible its visible.
     */
    public function doLayout():Void{
    	if(isDisplayable()){
    		validateLocation();
			root_mc._visible = visible;
    	}
    }
	
	/**
	 * Sets whether the component clip should be masked by its bounds. By default it is true.
	 * @param m whether the component clip should be masked.
	 * @see #isClipMasked()
	 */
	public function setClipMasked(m:Boolean):Void{
		clipMasked = m;
		clip_mc._visible = clipMasked;
		if(clipMasked){
			target_mc.setMask(clip_mc);
		}else{
			target_mc.setMask(null);
		}
	}
	
	/**
	 * Returns whether the component clip should be masked by its bounds. By default it is true.
	 * @return whether the component clip should be masked.
	 * @see #setClipMasked()
	 */
	public function isClipMasked():Boolean{
		return clipMasked;
	}
	
	/**
	 * Set a component to be hide or shown.
	 * If a component was hide, some laterly operation may not be done,
	 * they will be done when next shown, ex: repaint, doLayout ....
	 * So suggest you dont changed a component's visible frequently.
	 */
	public function setVisible(v:Boolean):Void{
		if(v != visible){
			visible = v;
			if(v){
				dispatchEvent(createEventObj(ON_SHOWN));
			}else{
				dispatchEvent(createEventObj(ON_HIDDEN));
			}
			//because the repaint and some other operating only do when visible
			//so when change to visible, must call repaint to do the operatings they had not done when invisible
			if(visible){
				repaint();
			}
			revalidate();
		}
	}
	
	public function isVisible():Boolean{
		return visible;
	}
		
	/**
	 * Determines whether this component is displayable. 
	 * <br>
	 * A component is displayable when it is connected to a native screen resource. 
	 * 
	 * <br>
	 * A component is made displayable either when it is added to a displayable containment hierarchy or when its containment hierarchy is made displayable. 
	 * A component is made undisplayable either when it is removed from a displayable containment hierarchy or when its containment hierarchy is made undisplayable.
	 */
	public function isDisplayable():Boolean{
		if(displayable && !MCUtils.isMovieClipExist(root_mc)){
			trace("/w/ " + this + " Logic Error, root_mc is not exist but the component is still displayable, you forgot call destroy method!");
			return false;
		}
		return displayable;
	}
	
	/**
	 * Sets the text font for this component.<br>
	 * this method will cause a repaint and revalidate method call.<br>
	 * @param newFont the font to set for this component.
	 */
	public function setFont(newFont:ASFont):Void{
		if(font != newFont){
			font = newFont;
			setFontValidated(false);
			repaint();
			revalidate();
		}
	}
	
	/**
	 * Returns whether the new font are applied and taked effect.
	 * <p>
	 * Some UI can just apply font to text when this method returned false 
	 * to avoid wasteful time for font applying.
	 * @return true if currently font are applied to texts, otherwish false.
	 * @see #setFontValidated()
	 */
	public function isFontValidated():Boolean{
		return fontValidated;
	}
	
	/**
	 * Sets whether the new font are applied and taked effect.
	 * <p>
	 * Once the UI applied the font, it can call this method to set the value 
	 * to be true, to avoid next wasteful applying.
	 * @return true set font are applied, otherwish false.
	 * @see #isFontValidated()
	 */
	public function setFontValidated(b:Boolean):Void{
		fontValidated = b;
	}
	
	/**
     * Gets the font of this component.
     * @return this component's font; if a font has not been set
     * for this component and it has parent, the font of its parent is returned
     * @see #setFont()
     */
	public function getFont():ASFont{
        if (font != null) {
            return font;
        }
        return (parent != null) ? parent.getFont() : font;
	}
	
	/**
     * Sets the background color of this component.
     * <p>
     * The background color affects each component differently.
     *
     * @param c the color to become this component's color;
     *          if this parameter is <code>null/undefined</code> and it has parent, then this
     *          component will inherit the background color of its parent
     * @see #getBackground()
	 */
	public function setBackground(c:ASColor):Void{
		if(background != c){
			background = c;
			repaint();
		}
	}
	
	/**
     * Gets the background color of this component.
     * @return this component's background color; if this component does
     *          not have a background color and it has parent,
     *          the background color of its parent is returned
     * @see #setBackground()
	 */
	public function getBackground():ASColor{
		if(background != null){
			return background;
		}
		return (parent != null) ? parent.getBackground() : background;
	}
	
	/**
     * Sets the foreground color of this component.
     * <p>
     * The foreground color affects each component differently.
     *
     * @param c the color to become this component's color;
     *          if this parameter is <code>null/undefined</code> and it has parent, then this
     *          component will inherit the foreground color of its parent
     * @see #getForeground()
	 */
	public function setForeground(c:ASColor):Void{
		if(foreground != c){
			foreground = c;
			repaint();
		}
	}
	
	/**
     * Gets the foreground color of this component.
     * @return this component's foreground color; if this component does
     *          not have a foreground color and it has parent,
     *          the foreground color of its parent is returned
     * @see #setForeground()
	 */
	public function getForeground():ASColor{
		if(foreground != null){
			return foreground;
		}
		return (parent != null) ? parent.getForeground() : foreground;
	}
		
    /**
     * If true the component paints every pixel within its bounds. 
     * Otherwise, the component may not paint some or all of its
     * pixels, allowing the underlying pixels to show through.
     * <p>
     * The default value of this property is false for <code>JComponent</code>.
     * However, the default value for this property on most standard
     * <code>Component</code> subclasses (such as <code>JButton</code> and
     * <code>JTree</code>) is look-and-feel dependent.
     *
     * @param b  true if this component should be opaque
     * @see #isOpaque()
     */
    public function setOpaque(b:Boolean):Void {
    	if(opaque != b){
    		opaque = b;
    		repaint();
    	}
    }
    
    /**
     * Returns true if this component is completely opaque.
     * <p>
     * An opaque component paints every pixel within its
     * rectangular bounds. A non-opaque component paints only a subset of
     * its pixels or none at all, allowing the pixels underneath it to
     * "show through".  Therefore, a component that does not fully paint
     * its pixels provides a degree of transparency.
     * <p>
     * Subclasses that guarantee to always completely paint their contents
     * should override this method and return true.
     *
     * @return true if this component is completely opaque
     * @see #setOpaque()
     */
    public function isOpaque():Boolean{
    	if(opaque === undefined){
    		return (uiProperties["opaque"] == true);
    	}else{
    		return opaque;
    	}
    }	
    
    /**
     * Sets the alpha for this component.
     * @param alpha the alpha for this component, between 0 and 100. default is 100.
     */
    public function setAlpha(alpha:Number):Void{
    	this.alpha = alpha;
    	this.root_mc._alpha = alpha;
    }
    
    /**
     * Returns the alpha of this component.
     * @return the alpha of this component. default is 100.
     */
    public function getAlpha():Number{
    	return alpha;
    }
		
	/**
	 * setBounds(bounds:Rectangle)<br>
	 * setBounds(x:Number, y:Number, width:Number, height:Number)
	 * <p>
	 * Sets the location and size.
	 */
	public function setBounds():Void{
		var newBounds:Rectangle = new Rectangle(arguments[0], arguments[1], arguments[2], arguments[3]);
		setLocation(newBounds.x, newBounds.y);
		setSize(newBounds.width, newBounds.height);
	}
	
	/**
	 * setBounds(bounds:Rectangle)<br>
	 * setBounds(x:Number, y:Number, width:Number, height:Number)
	 * <p>
	 * Sets the location immediately if the size is not changed or set the location and size and then 
	 * <code>invalidate</code>.
	 * See {@link #setLocationImmediately()} for more infomation.
	 * @see #setLocationImmediately()
	 * @see #setBounds()
	 * @see #validateLocation()
	 */
	public function setBoundsImmediately():Void{
		var newBounds:Rectangle = new Rectangle(arguments[0], arguments[1], arguments[2], arguments[3]);
		if(newBounds.width != bounds.width || newBounds.height != bounds.height){
			setLocation(newBounds.x, newBounds.y);
			setSize(newBounds.width, newBounds.height);
		}else{
			setLocationImmediately(newBounds.x, newBounds.y);
		}
	}
	
	/**
	 * Moves and resizes this component. The new location of the top-left corner is specified by x and y, and the new size is specified by width and height. 
	 * 
	 * <p>Stores the bounds value of this component into "return value" b and returns b. 
	 * If b is null or undefined a new Rectangle object is allocated. 
	 * 
	 * @param b the return value, modified to the component's bounds.
	 * 
	 * @see #setSize()
	 * @see #setLocation()
	 */
	public function getBounds(b:Rectangle):Rectangle{
		if(b != undefined){
			b.setRect(bounds);
			return b;
		}else{
			return new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
		}
	}
	
	/**
	 * setLocation(x:Number, y:Number)<br>
	 * setLocation(p:Point)
	 * <p>
	 * Set the component's location, if it is diffs from old location, invalidate it to wait validate.
	 * The top-left corner of the new location is specified by the x and y parameters 
	 * in the coordinate space of this component's parent.
	 */
	public function setLocation():Void{
		var newPos:Point = new Point(arguments[0], arguments[1]);
		var oldPos:Point = new Point(bounds.x, bounds.y);
		if(!newPos.equals(oldPos)){
			bounds.setLocation(newPos);
			dispatchEvent(createEventObj(ON_MOVED, oldPos, newPos));
			invalidate();
		}
	}
	
	/**
	 * setLocationImmediately(x:Number, y:Number)<br>
	 * setLocationImmediately(p:Point)
	 * <p>
	 * Set the component's location and move it's assets immediately.<br>
	 * <b>Note:</b>
	 * The method may be fast(cool thing:)), but take care to use it, because it will not 
	 * call <code>invalidate()</code>, it validate the new location immediately(
	 * <code>setLocation</code> method will <code>invalidate</code> the component and 
	 * then may validate the move at next frame, it is time different), it is just move 
	 * the component location, will not cause it's container relayouting, so it is 
	 * <b>faster</b> than <code>setLocation</code> generally.  However, generally the 
	 * layout managers do not care the old location of it's children, but you must 
	 * ensure it really do not, then call this method to move the component.
	 * @see #setLocation()
	 * @see #setBoundsImmediately()
	 * @see #validateLocation()
	 */
	public function setLocationImmediately():Void{
		var newPos:Point = new Point(arguments[0], arguments[1]);
		var oldPos:Point = new Point(bounds.x, bounds.y);
		if(!newPos.equals(oldPos)){
			bounds.setLocation(newPos);
			dispatchEvent(createEventObj(ON_MOVED, oldPos, newPos));
			validateLocation();
		}
	}
    
    /**
     * Locate this component assets to its location.
     */
    public function validateLocation():Void{
    	if(isDisplayable()){
    		if(isAntiBlurring()){
    			if(getParent() instanceof MCPanel){
    				MCUtils.pixelSnapMovieClip(root_mc, bounds.x, bounds.y);
    			}else{
					root_mc._x = Math.round(bounds.x);
					root_mc._y = Math.round(bounds.y);
    			}
    		}else{
				root_mc._x = bounds.x;
				root_mc._y = bounds.y;
    		}
    	}
    }	
	
	/**
	 * setGlobalLocation(x:Number, y:Number)<br>
	 * setGlobalLocation(p:Point)
	 * <p>
	 * Set the component's location in global coordinate. This method should only be called when the component 
	 * is created(That mean the MCs for this component are created).
	 * @see setLocation()
	 * @see MovieClip.localToGlobal()
	 * @see MovieClip.globalToLocal()
	 */
	public function setGlobalLocation():Void{
		var newGlobalPos:Point = new Point(arguments[0], arguments[1]);
		root_mc._parent.globalToLocal(newGlobalPos);
		if(root_mc._parent == null){
			trace("root_mc._parent == null : " + this);
		}
		setLocation(newGlobalPos);
	}
	
	/**
	 * setGlobalLocation(x:Number, y:Number)<br>
	 * setGlobalLocation(p:Point)
	 * <p>
	 * Sets the component's location in global coordinate immediately. 
	 * See {@link #setLocationImmediately()} for more infomation.
	 * @see #setLocationImmediately()
	 * @see #validateLocation()
	 */
	public function setGlobalLocationImmediately():Void{
		var newGlobalPos:Point = new Point(arguments[0], arguments[1]);
		root_mc._parent.globalToLocal(newGlobalPos);
		if(root_mc._parent == null){
			trace("root_mc._parent == null : " + this);
		}
		setLocationImmediately(newGlobalPos);
	}	
	
	/**
	 * getLocation(p:Point)<br>
	 * getLocation()
	 * <p>
	 * Stores the location value of this component into "return value" p and returns p. 
	 * If p is null or undefined a new Point object is allocated. 
	 * @param p the return value, modified to the component's location.
	 */
	public function getLocation(p:Point):Point{
		if(p != undefined){
			p.setLocation(bounds.x, bounds.y);
			return p;
		}else{
			return new Point(bounds.x, bounds.y);
		}
	}
	
	/**
	 * getGlobalLocation(p:Point)<br>
	 * getGlobalLocation()
	 * <p>
	 * Stores the global location value of this component into "return value" p and returns p. 
	 * If p is null or undefined a new Point object is allocated. 
	 * @param p the return value, modified to the component's global location.
	 * @see #getLocation()
	 * @see #setGlobalLocation()
	 * @see MovieClip.localToGlobal()
	 * @see MovieClip.globalToLocal()
	 */
	public function getGlobalLocation(p:Point):Point{
		var gp:Point = new Point(bounds.x, bounds.y); 
		root_mc._parent.localToGlobal(gp);
		if(p != undefined){
			p.setLocation(gp);
			return p;
		}else{
			return gp;
		}
	}
	
	/**
	 * setSize(width:Number, height:Number)<br>
	 * setSize(dim:Dimension)
	 * <p>
	 * Set the component's size, the width and height all will be setted to not less than zero, 
	 * then set the size.
	 * You can set a Component's size max than its maximumSize, but when it was drawed,
 	 * it will not max than its maximumSize.Just as its maximumSize and posited itself
 	 * in that size dimension you just setted. The position is relative to <code>getAlignmentX</code> 
	 * @see #getAlignmentX()
	 * @see #getAlignmentY()
	 * @see #getMinimumSize()
	 * @see #countMaximumSize()
	 * @see #getPreferredSize()
	 */
	public function setSize():Void{
		var newSize:Dimension = new Dimension(arguments[0], arguments[1]);
		newSize.width = Math.max(0, newSize.width);
		newSize.height = Math.max(0, newSize.height);
		var oldSize:Dimension = new Dimension(bounds.width, bounds.height);
		if(!newSize.equals(oldSize)){
			bounds.setSize(newSize);
			size();
			dispatchEvent(createEventObj(ON_RESIZED, oldSize, newSize));
		}
	}
	
	/**
	 * getSize(s:Dimension)<br>
	 * getSize()
	 * <p>
	 * Stores the size value of this component into "return value" s and returns s. 
	 * If s is null or undefined a new Dimension object is allocated. 
	 * @param p the return value, modified to the component's size.
	 */	
	public function getSize(s:Dimension):Dimension{
		if(s != undefined){
			s.setSize(bounds.width, bounds.height);
			return s;
		}else{
			return new Dimension(bounds.width, bounds.height);
		}
	}
	/**
	 * Sets the component's width.
	 * @param width the width of component to set
	 * @see  #setSize()
	 */
	public function setWidth(width:Number):Void{
		setSize(width, getHeight());
	}
	/**
	 * Sets the component's height.
	 * @param height the height of component to set
	 * @see  #setSize()
	 */	
	public function setHeight(height:Number):Void{
		setSize(getWidth(), height);
	}
	/**
	 * Returns the current width of this component
	 * @return the width of the component
	 */
	public function getWidth():Number{
		return bounds.width;
	}
	/**
	 * Returns the current height of this component
	 * @return the height of the component
	 */	
	public function getHeight():Number{
		return bounds.height;
	}
	/**
	 * Sets the x coordinate of the components.
	 * @return the x coordinate
	 * @see #setLocation()
	 */
	public function setX(x:Number):Void{
		setLocation(x, getY());
	}
	/**
	 * Sets the y coordinate of the components.
	 * @return the y coordinate
	 * @see #setLocation()
	 */
	public function setY(y:Number):Void{
		setLocation(getX(), y);
	}
	/**
	 * Returns the current x coordinate of the components.
	 * @return the current x coordinate of the components
	 * @see #getLocation()
	 */
	public function getX():Number{
		return bounds.x;
	}
	/**
	 * Returns the current y coordinate of the components.
	 * @return the current y coordinate of the components
	 * @see #getLocation()
	 */
	public function getY():Number{
		return bounds.y;
	}
	
	/**
	 * Registers the text to display in a tool tip. 
	 * The text displays when the cursor lingers over the component. 
	 * @param t the string to display; if the text is null, 
	 * the tool tip is turned off for this component
	 */
	public function setToolTipText(t:String):Void{
		toolTipText = t;
		if(t == null){
			JSharedToolTip.getSharedInstance().unregisterComponent(this);
		}else{
			JSharedToolTip.getSharedInstance().registerComponent(this);
		}
	}
	
	/**
	 * Returns the tooltip string that has been set with setToolTipText. 
	 * @return the text of the tool tip
	 * @see #setToolTipText()
	 */
	public function getToolTipText():String{
		return toolTipText;
	}
	
	/**
	 * @param ax
	 * @see #getAlignmentX()
	 */
    public function setAlignmentX(ax:Number):Void{
    	if(alignmentX != ax){
    		alignmentX = ax;
    		repaint();
    	}
    }
    
    /**
	 * @param ay
	 * @see #getAlignmentY()
     */
    public function setAlignmentY(ay:Number):Void{
    	if(alignmentY != ay){
    		alignmentY = ay;
    		repaint();
    	}
    }		
	
	/**
	 * Returns the alignment along the x axis. 
	 * This specifies how the component would like to be aligned relative 
	 * to its size when its size is maxer than its maximumSize. 
	 * The value should be a number between 0 and 1 where 0 
	 * represents alignment start from left, 1 is aligned the furthest 
	 * away from the left, 0.5 is centered, etc. 
	 * @return the alignment along the x axis, 0 by default
	 */
    public function getAlignmentX():Number{
    	return alignmentX;
    }

	/**
	 * Returns the alignment along the y axis. 
	 * This specifies how the component would like to be aligned relative 
	 * to its size when its size is maxer than its maximumSize. 
	 * The value should be a number between 0 and 1 where 0 
	 * represents alignment start from top, 1 is aligned the furthest 
	 * away from the top, 0.5 is centered, etc. 
	 * @return the alignment along the y axis, 0 by default
	 */
    public function getAlignmentY():Number{
    	return alignmentY;
    }
    
    /**
     * Returns the value of the property with the specified key. 
     * Only properties added with putClientProperty will return a non-null value.
     * @param key the being queried
     * @return the value of this property or null
     * @see #putClientProperty()
     */
    public function getClientProperty(key){
    	return clientProperty.get(key);
    }
    
    /**
     * Adds an arbitrary key/value "client property" to this component.
     * <p>
     * The <code>get/putClientProperty</code> methods provide access to 
     * a small per-instance hashtable. Callers can use get/putClientProperty
     * to annotate components that were created by another module.
     * For example, a
     * layout manager might store per child constraints this way. For example:
     * <pre>
     * componentA.putClientProperty("to the left of", componentB);
     * </pre>
     * @param key the new client property key
     * @param value the new client property value
     * @see #getClientProperty()
     */    
    public function putClientProperty(key, value):Void{
    	//Lazy initialization
    	if(clientProperty == undefined){
    		clientProperty = new HashMap();
    	}
    	clientProperty.put(key, value);
    }
	
	/**
	 * get the minimumSize from ui, if ui is null then Returns getInsets().roundsSize(new Dimension(0, 0)).
	 */
	private function countMinimumSize():Dimension{		
		if(ui != null){
			return ui.getMinimumSize(this);
		}else{
			return getInsets().getOutsideSize(new Dimension(0, 0));
		}
	}
	
	/**
	 * get the maximumSize from ui, if ui is null then new Dimension(Number.MAX_VALUE, Number.MAX_VALUE);
	 */
	private function countMaximumSize():Dimension{		
		if(ui != null){
			return ui.getMaximumSize(this);
		}else{
			return new Dimension(Number.MAX_VALUE, Number.MAX_VALUE);
		}
	}
	
	/**
	 * get the preferredSize from ui, if ui is null then just return the current size
	 */
	private function countPreferredSize():Dimension{
		if(ui != null){
			return ui.getPreferredSize(this);
		}else{
			return getSize();
		}
	}

	/**
	 * Sets the default value of <code>cachePrefSizes</code> to all will created components.
	 * Default value is true.
	 * @see #setCachePreferSizes()
	 */
	public static function setDefaultCachePreferSizes(b:Boolean):Void{
		defaultCashePresSizes = b;
	}
	
	/**
	 * Returns the default value of <code>cachePrefSizes</code> to all will created components.
	 * Default value is true.
	 * @see #isCachePreferSizes()
	 */
	public static function isDefaultCachePreferSizes():Boolean{
		return defaultCashePresSizes;
	}
	
	/**
	 * Sets whether or not turn on the preferred size, minimum size and 
	 * max size cache. By default, this is true(means turned on).
	 * <p>
	 * If this is turned on, the size count will be very fast as most time. 
	 * So suggest you that do not turn off it unless you have your personal reason.
	 * </p>
	 * @param b true to turn on it, false trun off it.
	 */
	public function setCachePreferSizes(b:Boolean):Void{
		cachePreferSizes = b;
		if(!isCachePreferSizes()){
	    	cachedMaximumSize = null;
	    	cachedMinimumSize = null;
	    	cachedPreferredSize = null;			
		}
	}
	
	/**
	 * Returns whether or not the preferred size, minimum size and 
	 * max size cache is turned on.
	 * @return whether or not the preferred size, minimum size and 
	 * max size cache is turned on.
	 */
	public function isCachePreferSizes():Boolean{
		return (cachePreferSizes != null) ? cachePreferSizes : isDefaultCachePreferSizes();
	}	
	
	/**
	 * @see #setMinimumSize()
	 */
	public function getMinimumSize():Dimension{
		if(minimumSize != null){
			return minimumSize.clone();
		}else if(isCachePreferSizes() && cachedMinimumSize != null){
			return cachedMinimumSize.clone();
		}else{
			if(isCachePreferSizes()){
				cachedMinimumSize = countMinimumSize();
				return cachedMinimumSize.clone();
			}else{
				return countMinimumSize();
			}
		}
	}
	
	/**
	 * @see #setMaximumSize()
	 */	
	public function getMaximumSize():Dimension{
		if(maximumSize != null){
			return maximumSize.clone();
		}else if(isCachePreferSizes() && cachedMaximumSize != null){
			return cachedMaximumSize.clone();
		}else{
			if(isCachePreferSizes()){
				cachedMaximumSize = countMaximumSize();
				return cachedMaximumSize.clone();
			}else{
				return countMaximumSize();
			}
		}
	}
	
	/**
	 * @see #setPreferredSize()
	 */	
	public function getPreferredSize():Dimension{
		if(preferredSize != null){
			return preferredSize.clone();
		}else if(isCachePreferSizes() && cachedPreferredSize != null){
			return cachedPreferredSize.clone();
		}else{
			if(isCachePreferSizes()){
				cachedPreferredSize = countPreferredSize();
				return cachedPreferredSize.clone();
			}else{
				return countPreferredSize();
			}
		}
	}
	
	/**
	 * setMinimumSize(d:Dimension)<br>
	 * setMinimumSize(width:Number, height:Number)
	 * <p>
	 * Set the minimumSize, then the component's minimumSize is
	 * specified. otherwish getMinimumSize will can the count method.
	 * @param arguments null to set minimumSize null then getMinimumSize will can the layout.
	 * others set the minimumSize to be a specified size.
	 * @see #getMinimumSize()
	 */
	public function setMinimumSize():Void{
		if(arguments[0] == null){
			 minimumSize = null;
		}
		else if(arguments[1]!=null){
			minimumSize = new Dimension(arguments[0], arguments[1]);
		}
		else{
			minimumSize=new Dimension(arguments[0].width, arguments[0].height);
		}
	}
	
	/**
	 * setMaximumSize(d:Dimension)<br>
	 * setMaximumSize(width:Number, height:Number)<br>
	 * <p>
	 * Set the maximumSize, then the component's maximumSize is
	 * specified. otherwish getMaximumSize will can count method.
	 * 
	 * @param arguments null to set maximumSize null to make getMaximumSize will can the layout.
	 * others set the maximumSize to be a specified size.
	 * @see #getMaximumSize()
	 * @see #MaximumSize()
	 */	
	public function setMaximumSize():Void{
		if(arguments[0] == null){
			 maximumSize = null;
		}
		else if(arguments[1]!=null){
			maximumSize = new Dimension(arguments[0], arguments[1]);
		}
		else{
			maximumSize=new Dimension(arguments[0].width,arguments[0].height);
		}		
	}
	
	/**
	 * setPreferredSize(d:Dimension)<br>
	 * setPreferredSize(width:Number, height:Number)<br>
	 * <p>
	 * Set the preferredSize, then the component's preferredSize is
	 * specified. otherwish getPreferredSize will count method.
	 * 
	 * @param arguments null to set preferredSize null to make getPreferredSize will call the layout,
	 * others set the preferredSize to be a specified size.
	 * @see #getPreferredSize()
	 */	
	public function setPreferredSize():Void{
		if(arguments[0] == null){
			 preferredSize = null;
		}
		else if(arguments[1]!=null){
			preferredSize = new Dimension(arguments[0], arguments[1]);
		}
		else{
			preferredSize=new Dimension(arguments[0].width,arguments[0].height);
		}		
	}	
	
	/**
	 * Returns <code>getPreferredSize().width</code>
	 * @see #getPreferredSize()
	 */
	public function getPreferredWidth():Number {
		return getPreferredSize().width;
	}
	/**
	 * Calls <code>setPreferredSize(preferredWidth, getPreferredHeight())</code>
	 * @see #setPreferredSize()
	 */
	public function setPreferredWidth(preferredWidth:Number):Void {
		setPreferredSize(preferredWidth, getPreferredHeight());
	}
	/**
	 * Returns <code>getPreferredSize().height</code>
	 * @see #getPreferredSize()
	 */
	public function getPreferredHeight():Number {
		return getPreferredSize().height;
	}
	/**
	 * Calls <code>setPreferredSize(getPreferredWidth(), preferredHeight)</code>
	 * @see #setPreferredSize()
	 */
	public function setPreferredHeight(preferredHeight:Number):Void {
		setPreferredSize(getPreferredWidth(), preferredHeight);
	}
	/**
	 * Returns <code>getMaximumSize().width</code>
	 * @see #getMaximumSize()
	 */
	public function getMaximumWidth():Number {
		return getMaximumSize().width;
	}
	/**
	 * Calls <code>setMaximumSize(maximumWidth, getMaximumHeight())</code>
	 * @see #setMaximumSize()
	 */
	public function setMaximumWidth(maximumWidth:Number):Void {
		setMaximumSize(maximumWidth, getMaximumHeight());
	}
	/**
	 * Returns <code>getMaximumSize().height</code>
	 * @see #getMaximumSize()
	 */
	public function getMaximumHeight():Number {
		return getMaximumSize().height;
	}
	/**
	 * Calls <code>setMaximumSize(getMaximumWidth(), maximumHeight)</code>
	 * @see #setMaximumSize()
	 */
	public function setMaximumHeight(maximumHeight:Number):Void {
		setMaximumSize(getMaximumWidth(), maximumHeight);
	}
	/**
	 * Returns <code>getMinimumSize().width</code>
	 * @see #getMinimumSize()
	 */
	public function getMinimumWidth():Number {
		return getMinimumSize().width;
	}
	/**
	 * Calls <code>setMinimumSize(minimumWidth, getMinimumHeight())</code>
	 * @see #setMinimumSize()
	 */
	public function setMinimumWidth(minimumWidth:Number):Void {
		setMinimumSize(minimumWidth, getMinimumHeight());
	}	
	/**
	 * Returns <code>getMinimumSize().height</code>
	 * @see #getMinimumSize()
	 */
	public function getMinimumHeight():Number {
		return getMinimumSize().height;
	}
	/**
	 * Calls <code>setMinimumSize(getMinimumWidth(), minimumHeight)</code>
	 * @see #setMinimumSize()
	 */
	public function setMinimumHeight(minimumHeight:Number):Void {
		setMinimumSize(getMinimumWidth(), minimumHeight);
	}
	
	/**
	 * Enable or disable the component.
	 * <p>
	 * If a component is disabled, it will not fire mouse events. 
	 * And some component will has different interface when enabled or disabled.
	 * But it will also eat mouse clicks when disable even it will not fire mouse events.
	 * @param b true to enable the component, false to disable it.
	 * @see #setTriggerEnabled()
	 * @see #ON_ROLLOVER
	 * @see #ON_ROLLOUT
	 * @see #ON_PRESS
	 * @see #ON_RELEASE
	 * @see #ON_RELEASEOUTSIDE
	 */
	public function setEnabled(b:Boolean):Void{
		trigger_mc.enabled = b;
		if(enabled != b){
			enabled = b;
			repaint();
		}
	}
	
	/**
	 * Returns whether the component is enabled.
	 * @see #setEnabled()
	 */
	public function isEnabled():Boolean{
		return enabled;
	}
	
	/**
	 * Sets whether the component trigger be enabled.
	 * <P>
	 * If it is enabled, it will fire events when mouse roll, press on the component, of course 
	 * it eats the mouse clicks so its container may not got clicks.
	 * If it is disabled, it will not fire events when mouse roll press on, 
	 * And the importance is that it will not eat the mouse clicks.
	 * <P>
	 * Default value is true.
	 * @param b true to enable trigger, false to disable it.
	 * @see #ON_ROLLOVER
	 * @see #ON_ROLLOUT
	 * @see #ON_PRESS
	 * @see #ON_RELEASE
	 * @see #ON_RELEASEOUTSIDE
	 */
	public function setTriggerEnabled(b:Boolean):Void{
		triggerEnabled = b;
		trigger_mc._visible = b;
	}
	
	/**
	 * Returns is trigger enabled.
	 * @see #setTriggerEnabled()
	 */
	public function isTriggerEnabled():Boolean{
		return triggerEnabled;
	}
	
	/**
	 * Sets whether use hand cursor when mouse move on this component.
	 * Default is false.
	 * @param b true to use hand cursor when mouse move on this component, false not.
	 */
	public function setUseHandCursor(b:Boolean):Void{
		useHandCursor = b;
		trigger_mc.useHandCursor = b;
	}
	
	/**
	 * @return whether use hand cursor when mouse move on this component.
	 * @see #setUseHandCursor()
	 */
	public function isUseHandCursor():Boolean{
		return useHandCursor;
	}
	
	/**
	 * Sets whether this component can fire ON_DRAG_RECOGNIZED event.
	 * @see #ON_DRAG_RECOGNIZED 
	 * @see #isDragEnabled()
	 */
	public function setDragEnabled(b:Boolean):Void{
		dragEnabled = b;
	}
	
	/**
	 * Returns whether this component can fire ON_DRAG_RECOGNIZED event. (Default value is false)
	 * @see #ON_DRAG_RECOGNIZED
	 * @see #setDragEnabled()
	 */
	public function isDragEnabled():Boolean{
		return dragEnabled;
	}
	
	/**
	 * Sets whether this component can trigger dragging component to fire drag events 
	 * when dragging over to this component.
	 * @param b true to make this component to be a trigger that trigger drag and drop 
	 * action to fire events, false not to do that things.
	 * @see #ON_DRAG_ENTER
	 * @see #ON_DRAG_OVER
	 * @see #ON_DRAG_EXIT
	 * @see #ON_DRAG_DROP
	 * @see #isDropTrigger()
	 */
	public function setDropTrigger(b:Boolean):Void{
		dropTrigger = b;
	}
	
	/**
	 * Returns whether this component can trigger dragging component to fire drag events 
	 * when dragging over to this component.(Default value is false)
	 * @return true if this component is a trigger that can trigger drag and drop action to 
	 * fire events, false it is not.
	 * @see #ON_DRAG_ENTER
	 * @see #ON_DRAG_OVER
	 * @see #ON_DRAG_EXIT
	 * @see #ON_DRAG_DROP
	 * @see #setDropTrigger()
	 */
	public function isDropTrigger():Boolean{
		return dropTrigger;
	}
	
	/**
	 * Creates and returns an empty MovieClip from the Component's root MovieClip.
	 * This method will only return non-null when the component is displayable, a normal 
	 * way to ensure this method will really create context, you can add a ON_CREATED even 
	 * handler to the component, whan handler executed, then you can create real contexts.
	 * <p>
	 * Note that the mc created is not cliped by component size, it is different from 
	 * <code>createMovieClip()</code>.
	 * @param nameStart (optional)the prefix of the created MovieClip's name.
	 * @param depth (optional)the depth of the created MovieClip.
	 * @see #isDisplayable()
	 * @see #createMovieClip()
	 */
	public function createMovieClipOnRoot(nameStart:String, depth:Number):MovieClip{
		return creater.createMC(root_mc, nameStart, depth);
	}
	
	/**
	 * Creates and returns an empty MovieClip from the Component's target MovieClip.
	 * This method will only return non-null when the component is displayable, a normal 
	 * way to ensure this method will really create context, you can add a ON_CREATED even 
	 * handler to the component, whan handler executed, then you can create real contexts.
	 * <p>
	 * Note that if you create an new MC for a border or icon you should remove them in your 
	 * uninstallXXx method of them.
	 * @param nameStart (optional)the prefix of the created MovieClip's name.
	 * @param depth (optional)the depth of the created MovieClip.
	 * @see #isDisplayable()
	 */
	public function createMovieClip(nameStart:String, depth:Number):MovieClip{
		return creater.createMC(target_mc, nameStart, depth);
	}
	
	/**
	 * Attaches and returns a MovieClip from the Component's target MovieClip.
	 * This method will only return non-null when the component is displayable, a normal 
	 * way to ensure this method will really create context, you can add a ON_CREATED even 
	 * handler to the component, whan handler executed, then you can create real contexts.
	 * <p>
	 * Note that if you attach an new MC for a border or icon you should remove them in your 
	 * uninstallXXx method of them.
	 * @param linkage the linkage id of the MovieClip in your Symbol Library.
	 * @param nameStart (optional)the prefix of the attached MovieClip's name.
	 * @param depth (optional)the depth of the attached MovieClip.
	 * @see #isDisplayable()
	 */	
	public function attachMovieClip(linkage:String, nameStart:String, depth:Number):MovieClip{
		return creater.attachMC(target_mc, linkage, nameStart, depth);
	}
	
	/**
	 * Creates and returns an textfiled from the Component's target MovieClip.
	 * This method will only return non-null when the component is displayable, a normal 
	 * way to ensure this method will really create context, you can add a ON_CREATED even 
	 * handler to the component, whan handler executed, then you can create real contexts.
	 * <p>
	 * Note that if you create an new textfiled for a border or icon you should remove them in your 
	 * uninstallXXx method of them.
	 * @param nameStart (optional)the prefix of the created textfiled's name.
	 * @param depth (optional)the depth of the created textfiled.
	 * @see #isDisplayable()
	 */	
	public function createTextField(nameStart:String, depth:Number):TextField{
		return creater.createTF(target_mc, nameStart, depth);
	}
	
	/**
	 * Returns the component based root movie clip's ancestor movie clip.<br>
	 * Generally it is _root.
	 * @return the root movie clip of the component rootest based.
	 */
	public function getRootAncestorMovieClip():MovieClip{
		var root:MovieClip = root_mc;
		var aswing_root:MovieClip = ASWingUtils.getRootMovieClip();
		while(root._parent != null && root != aswing_root){
			root = root._parent;
		}
		return root;
	}
	
	/**
	 * Returns the root ancestor component(a jwindow or a mcpanel)'s movie clip.<br>
	 * @return the root ancestor component movie clip.
	 */	
	public function getComponentRootAncestorMovieClip():MovieClip{
		var rootCon:Component = ASWingUtils.getAncestorComponent(this);
		
		var root:MovieClip = rootCon.root_mc;
		return root;
	}	
		
	/**
	 * Returns the component's mc's depth.  
	 * It will return undefined when this component is displayable.
	 */
	public function getDepth():Number{
		return root_mc.getDepth();
	}
	
	/**
	 * Swap the component's mc's depth. This is only effective when this component is displayable.
	 */
	public function swapDepths(target):Void{
		root_mc.swapDepths(target);
	}
	
	/**
	 * Brings this component at top depth of all brothers. 
	 * This is only effective when this component is displayable.
	 */	
	public function bringToTopDepth():Void{
		DepthManager.bringToTop(root_mc);
	}
	
	/**
	 * Brings this component at bottom depth of all brothers. 
	 * This is only effective when this component is displayable.
	 */	
	public function bringToBottomDepth():Void{
		DepthManager.bringToBottom(root_mc);
	}
	
	/**
	 * hitTest(target:MovieClip):Boolean<br>
	 * hitTest(x:Number, y:Number, [shapeFlag:Boolean]):Boolean
	 * <p>
	 * Returns the component's clip mc hitTest()
	 */
	public function hitTest(x, y, shapeFlag):Boolean{
		return clip_mc.hitTest(x, y, shapeFlag);
	}
	
	/**
	 * Returns whether the component hit the mouse.
	 */
	public function hitTestMouse():Boolean{
		//Does this way always right? you know hitTest is not testing the point at global corrdinates as the liveDocs says.
		var root:MovieClip = _level0;
		if(!MCUtils.isMovieClipExist(root)){
			trace("level0 not exists");
			root = _root;
		}
		return clip_mc.hitTest(_level0._xmouse, _level0._ymouse);
	}
	
	/**
	 * Returns the position of the mouse in the Component area.
	 * (0, 0) is the top left of the component bounds.
	 * @return the position of the mouse in the Component area.
	 */
	public function getMousePosition():Point{
		return new Point(root_mc._xmouse, root_mc._ymouse);
	}
	
	public function globalToComponent(p:Point):Point{
		var np:Point = new Point(p.x, p.y);
		root_mc.globalToLocal(np);
		return np;
	}
	
	public function componentToGlobal(p:Point):Point{
		var np:Point = new Point(p.x, p.y);
		root_mc.localToGlobal(np);
		return np;
	}
	
    /**
     * Returns the Container which is the focus cycle root of this Component's
     * focus traversal cycle. Each focus traversal cycle has only a single
     * focus cycle root and each Component which is not a Container belongs to
     * only a single focus traversal cycle. Containers which are focus cycle
     * roots belong to two cycles: one rooted at the Container itself, and one
     * rooted at the Container's nearest focus-cycle-root ancestor. For such
     * Containers, this method will return the Container's nearest focus-cycle-
     * root ancestor.
     *
     * @return this Component's nearest focus-cycle-root ancestor
     * @see Container#isFocusCycleRoot()
     */
    public function getFocusCycleRootAncestor():Container {
        var rootAncestor:Container = getParent();
        while (rootAncestor != null && !rootAncestor.isFocusCycleRoot()) {
            rootAncestor = rootAncestor.getParent();
        }
        return rootAncestor;
    }
    
    /**
     * Returns whether the specified Container is the focus cycle root of this
     * Component's focus traversal cycle. Each focus traversal cycle has only
     * a single focus cycle root and each Component which is not a Container
     * belongs to only a single focus traversal cycle.
     *
     * @param container the Container to be tested
     * @return <code>true</code> if the specified Container is a focus-cycle-
     *         root of this Component; <code>false</code> otherwise
     * @see Container#isFocusCycleRoot()
     */
    public function isFocusCycleRootOfContainer(container:Container):Boolean{
        var rootAncestor:Container = getFocusCycleRootAncestor();
        return (rootAncestor == container);
    }
    
    /**
     * Determines whether this component is showing on screen. This means
     * that the component must be visible, and it must be in a container
     * that is visible and showing.
     * @return <code>true</code> if the component is showing,
     *          <code>false</code> otherwise
     * @see #setVisible()
     */    
    public function isShowing():Boolean{
    	if(isDisplayable() && isVisible()){
    		//here, parent is null means this is the top component(like MCPanel)
    		if(parent == null){
    			return true;
    		}else{
    			return parent.isShowing();
    		}
    	}
    	return false;
    }
    
    /**
     * Returns <code>true</code> if this <code>Component</code> is the 
     *    focus owner.
     *
     * @return <code>true</code> if this <code>Component</code> is the 
     *     focus owner; <code>false</code> otherwise
     */
    public function isFocusOwner():Boolean {
        return (FocusManager.getCurrentManager().getFocusOwner() == this);
    }    
    
    /**
     * Requests that this Component get the input focus, and that this
     * Component's top-level ancestor become the focused Window. This component
     * must be displayable, visible, and focusable for the request to be
     * granted. Every effort will be made to honor the request; however, in
     * some cases it may be impossible to do so. Developers must never assume
     * that this Component is the focus owner until this Component receives a
     * ON_FOCUS_GAINED event.
     *
     * @return true if the request is made successful, false if the request is denied.
     * @see #isFocusable()
     * @see #isDisplayable()
     * @see #ON_FOCUS_GAINED
     */
    public function requestFocus():Boolean {
    	//trace("-------requestFocus of " + this);
    	if(isShowing() && isFocusable()){
    		var currentFocusOwner:Component = FocusManager.getCurrentManager().getFocusOwner();
    		if(currentFocusOwner != this){
    			if(canChangeFocusFromComponent(currentFocusOwner)){
		    		FocusManager.getCurrentManager().setFocusOwner(this);
		    		currentFocusOwner.__onFocusLost();
		    		__onFocusGained();
		    		activeWindowOwner();
    				//trace("requestFocused ok of " + this);
		    		return true;
    			}
    		}
    	}
    	//trace("requestFocused failed of " + this);
        return false;
    }
    private function canChangeFocusFromComponent(currentFocusOwner:Component):Boolean{
    	var currentActivedWindow:JWindow = FocusManager.getCurrentManager().getActiveWindow();
		var window:JWindow = ASWingUtils.getWindowAncestor(this);
		if(window != currentActivedWindow && (this != window) && (currentActivedWindow != null && currentActivedWindow.isModal())){
//			trace("getWindowAncestor( this ) : " + window);
//			trace("title : " + (JFrame(window)).getTitle());
//			trace("currentActivedWindow : " + currentActivedWindow);
//			trace("currentActivedWindow : " + (JFrame(currentActivedWindow)).getTitle());
			//TODO fix this jude when will be false
			return true;
		}
		return true;
    }
	private function activeWindowOwner():Void{
		var window:JWindow = ASWingUtils.getWindowAncestor(this);
		if(window != null){
			window.setActive(true);
		}
	}
	
    /**
     * Transfers the focus to the next component, as though this Component were
     * the focus owner.
     * 
     * @return true if transfered, false otherwise
     * @see       #requestFocus()
     */
    public function transferFocus():Boolean {
    	return transferFocusWithDirection(1);
    }
    
    /**
     * Transfers the focus to the previous component, as though this Component
     * were the focus owner.
     * 
     * @return true if transfered, false otherwise
     * @see       #requestFocus()
     */
    public function transferFocusBackward():Boolean{
    	return transferFocusWithDirection(-1);
    }
    /**
     * dir > 0 transferFocus, dir <= 0 transferFocusBackward
     */
    private function transferFocusWithDirection(dir:Number):Boolean{
        var rootAncestor:Container = getFocusCycleRootAncestor();
        var comp:Component = this;
        while (rootAncestor != null && 
               !(rootAncestor.isShowing() && 
                 rootAncestor.isFocusable() && 
                 rootAncestor.isEnabled())) 
        {
            comp = rootAncestor;
            rootAncestor = comp.getFocusCycleRootAncestor();
        }
        if (rootAncestor != null) {
        	//trace("About " + this + " : ");
        	//trace("rootAncestor = " + rootAncestor);
            var policy:FocusTraversalPolicy = rootAncestor.getFocusTraversalPolicy();
            var toFocus:Component;
            if(dir < 0){
            	toFocus = policy.getComponentBefore(rootAncestor, comp);
            }else{
            	toFocus = policy.getComponentAfter(rootAncestor, comp);
            }
            if (toFocus == null) {
                toFocus = policy.getDefaultComponent(rootAncestor);
            }
            if (toFocus != null) {
                var res:Boolean = toFocus.requestFocus();
                return res;
            }
        }
        return false;
    }
    

    /**
     * Transfers the focus up one focus traversal cycle. Typically, the focus
     * owner is set to this Component's focus cycle root, and the current focus
     * cycle root is set to the new focus owner's focus cycle root. If,
     * however, this Component's focus cycle root is a Window, then the focus
     * owner is set to the focus cycle root's default Component to focus, and
     * the current focus cycle root is unchanged.
     *
     * @see       #requestFocus()
     * @see       Container#isFocusCycleRoot()
     * @see       Container#setFocusCycleRoot()
     */

	public function transferFocusUpCycle():Void{
		var rootAncestor:Container;
		for (rootAncestor = getFocusCycleRootAncestor(); 
			(rootAncestor != null) && 
				(! ((rootAncestor.isShowing() 
					&& rootAncestor.isFocusable()) 
					&& rootAncestor.isEnabled())); 
			rootAncestor = rootAncestor.getFocusCycleRootAncestor()){}
		if (rootAncestor != null){
			var rootAncestorRootAncestor:Container = rootAncestor.getFocusCycleRootAncestor();
			FocusManager.getCurrentManager().setCurrentFocusCycleRoot(((rootAncestorRootAncestor != null) ? rootAncestorRootAncestor : rootAncestor));
			rootAncestor.requestFocus();
		}else{
			var window:Container = (((this instanceof Container) ? Container(this) : getParent()));
			while ((window != null) && (!(window instanceof JWindow))){
				window = window.getParent();
			}
			if (window != null){
				var toFocus:Component = window.getFocusTraversalPolicy().getDefaultComponent(window);
				if (toFocus != null){
					FocusManager.getCurrentManager().setCurrentFocusCycleRoot(window);
					toFocus.requestFocus();
				}
			}
		}
	}	
	

    /**
     * Returns the Set of focus traversal keys for a given traversal operation
     * for this Component. (See
     * <code>setFocusTraversalKeys</code> for a full description of each key.)
     * <p>
     * If a Set of traversal keys has not been explicitly defined for this
     * Component, then this Component's parent's Set is returned. If no Set
     * has been explicitly defined for any of this Component's ancestors, then
     * the current FocusManager's default Set is returned.
     *
     * @param id one of FocusManager.FORWARD_TRAVERSAL_KEYS,
     *        FocusManager.BACKWARD_TRAVERSAL_KEYS, or
     *        FocusManager.UP_CYCLE_TRAVERSAL_KEYS.
     *        FocusManager.DOWN_CYCLE_TRAVERSAL_KEYS is not useful for a component, 
     *        it only be useful if this component is a instance of Container.
     * @return the Set of Key codes(contains in a Array) for the specified operation. The Set
     *         will be unmodifiable, and may be empty. null will never be
     *         returned.
     * @see #setFocusTraversalKeys()
     * @see FocusManager#getDefaultFocusTraversalKeys()
     * @see FocusManager#FORWARD_TRAVERSAL_KEYS
     * @see FocusManager#BACKWARD_TRAVERSAL_KEYS
     * @see FocusManager#UP_CYCLE_TRAVERSAL_KEYS
     * @see FocusManager#DOWN_CYCLE_TRAVERSAL_KEYS
     */	
	public function getFocusTraversalKeys(id:Number):Array{
		var keys:Array = focusTraversalKeys[id];
		if(keys == null){
			var theParent:Component = getParent();
			if(theParent != null){
				keys = theParent.getFocusTraversalKeys(id);
			}else{
				keys = FocusManager.getCurrentManager().getDefaultFocusTraversalKeys(id);
			}
		}else{
			keys = keys.concat();
		}
		return keys;
	}
	

    /**
     * Sets the focus traversal keys for a given traversal operation for this
     * Component.
     * <p>
     * To disable a traversal key, use an empty Array; [] is
     * recommended.
     * <p>
     * If a value of null is specified for the Set, this Component inherits the
     * Set from its parent. If all ancestors of this Component have null
     * specified for the Set, then the current FocusManager's default
     * Set is used.
     *
     * @param id one of FocusManager.FORWARD_TRAVERSAL_KEYS,
     *        FocusManager.BACKWARD_TRAVERSAL_KEYS, or
     *        FocusManager.UP_CYCLE_TRAVERSAL_KEYS
     * @param keystrokes the Set of Key codes(contained a Array) for the specified operation
     * @see #getFocusTraversalKeys()
     * @see FocusManager#setDefaultFocusTraversalKeys()
     * @see FocusManager#FORWARD_TRAVERSAL_KEYS
     * @see FocusManager#BACKWARD_TRAVERSAL_KEYS
     * @see FocusManager#UP_CYCLE_TRAVERSAL_KEYS
     * @see FocusManager#DOWN_CYCLE_TRAVERSAL_KEYS
     */	
	public function setFocusTraversalKeys(id:Number, keys:Array):Void{
		if(focusTraversalKeys == null){
			focusTraversalKeys = new Array();
			for(var i:Number=0; i<FocusManager.TRAVERSAL_KEY_LENGTH; i++){
				focusTraversalKeys.push(null);
			}
		}
		if(id >=0 && id < FocusManager.TRAVERSAL_KEY_LENGTH){
			focusTraversalKeys[id] = keys.concat();
		}
	}    
    
    /**
     * Returns whether this Component can be focused.
     *
     * @return <code>true</code> if this Component is focusable;
     *         <code>false</code> otherwise.
     * @see #setFocusable()
     */	
   	public function isFocusable():Boolean{
   		if(focusable === undefined){
   			return (uiProperties["focusable"] == true);
   		}else{
			return focusable;
   		}
	}
	
    /**
     * Sets the focusable state of this Component to the specified value. This
     * value overrides the Component's default focusability.
     *
     * @param focusable indicates whether this Component is focusable
     * @see #isFocusable()
     */	
	public function setFocusable(focusable:Boolean):Void{
		this.focusable = focusable;
	}

	/**
	 * Adds a component to be the acceptable drag initiator to this component.
	 * <p>
	 * It is not meanning that the DnD events will not be fired when the initiator 
	 * is dragging enter/over/exit/drop on this component.
	 * It is meanning that you can have a convenient way to proccess that events from 
	 * the method <code>isDragAcceptableInitiator</code> later, and the default dragging 
	 * image will take advantage to present a better picture when painting.
	 * <P>
	 * @param com the acceptable drag initiator
	 * @see #isDragAcceptableInitiator()
	 */
	public function addDragAcceptableInitiator(com:Component):Void{
		if(dragAcceptableInitiator == null){
			dragAcceptableInitiator = new HashMap();
		}
		dragAcceptableInitiator.put(com.getID(), true);
	}
	
	/**
	 * Removes a component to be the acceptable drag initiator to this component.
	 * @param com the acceptable drag initiator
	 * @see #addDragAcceptableInitiator()
	 */
	public function removeDragAcceptableInitiator(com:Component):Void{
		if(dragAcceptableInitiator != null)
			dragAcceptableInitiator.remove(com.getID());
	}
	
	/**
	 * Returns whether the component is acceptable drag initiator for this component.
	 * @param com the maybe acceptable drag initiator
	 * @return true if it is acceptable drag initiator, false not
	 */
	public function isDragAcceptableInitiator(com:Component):Boolean{
		if(dragAcceptableInitiator != null){
			return dragAcceptableInitiator.containsKey(com.getID());
		}else{
			return false;
		}
	}	
			
	public function toString():String{
		return "Component - " + name + " mc:" + root_mc;
	}	
	
	/**
	 * @see ComponentDecorator#removeBorderWhenNextPaint()
	 */
	private function uninstallBorderWhenNextPaint(border:Border):Void{
		ComponentDecorator.removeBorderWhenNextPaint(this, border);
	}
	/**
	 * @see ComponentDecorator#removeIconWhenNextPaint()
	 */
	private function uninstallIconWhenNextPaint(icon:Icon):Void{
		ComponentDecorator.removeIconWhenNextPaint(this, icon);
	}
	
	/**
	 * Sets component's constraints.
	 * @param constraints the constraints to set
	 */
	public function setConstraints(constraints:Object):Void {
		this.constraints = constraints;	
	}
	
	/**
	 * Gets cpmponent's constraints.
	 * @return component's constraints
	 */
	public function getConstraints():Object {
		return constraints;	
	}
	
	/**
	 * Returns a component that is the specified movie clip's owner.
	 * @param mc the movie clip
	 * @return the owner of the movie clip, or null if it is not owned by any component.
	 */
	public static function getOwnerComponent(mc:MovieClip):Component{
		do{
			var c:Component = Component(mc[PATH_TO_OWNER_COMPONENT]);
			if(c != null){
				return c;
			}
			mc = mc._parent;
		}while(mc != null);
		return null;
	}
	
	/**
	 * Notifies all listeners that have registered interest for
     * notification on this event type.
	 */
	private function fireStateChanged():Void{
		dispatchEvent(createEventObj(ON_STATE_CHANGED));
	}
	
	private function fireKeyDownEvent():Void{
		dispatchEvent(createEventObj(ON_KEY_DOWN));
	}
	
	private function fireKeyUpEvent():Void{
		dispatchEvent(createEventObj(ON_KEY_UP));
	}
	
	private function fireMouseWheelEvent(delta:Number):Void{
		dispatchEvent(createEventObj(ON_MOUSE_WHEEL, delta));
	}
	
	private function fireDragRecognizedEvent(touchedChild:Component):Void{
		dispatchEvent(createEventObj(ON_DRAG_RECOGNIZED, touchedChild));
	}
	
	/**
	 * Fires ON_DRAG_ENTER event.(Note, this method is only for DragManager use)
	 */
	public function fireDragEnterEvent(dragInitiator:Component, sourceData:SourceData, mousePos:Point):Void{
		dispatchEvent(createEventObj(ON_DRAG_ENTER, dragInitiator, sourceData, mousePos));
	}
	/**
	 * Fires ON_DRAG_ENTER event.(Note, this method is only for DragManager use)
	 */
	public function fireDragOverringEvent(dragInitiator:Component, sourceData:SourceData, mousePos:Point):Void{
		dispatchEvent(createEventObj(ON_DRAG_OVERRING, dragInitiator, sourceData, mousePos));
	}
	/**
	 * Fires ON_DRAG_ENTER event.(Note, this method is only for DragManager use)
	 */
	public function fireDragExitEvent(dragInitiator:Component, sourceData:SourceData, mousePos:Point):Void{
		dispatchEvent(createEventObj(ON_DRAG_EXIT, dragInitiator, sourceData, mousePos));
	}
	/**
	 * Fires ON_DRAG_ENTER event.(Note, this method is only for DragManager use)
	 */
	public function fireDragDropEvent(dragInitiator:Component, sourceData:SourceData, mousePos:Point):Void{
		dispatchEvent(createEventObj(ON_DRAG_DROP, dragInitiator, sourceData, mousePos));
	}
	
	/**
	 * Supply On press action, some times the component trigger are not cover 
	 * the whole space, so if some other space of this component on press, the 
	 * on press action will not fired, then call this method to supply.
	 * @see #supplyOnRelease()
	 */
	public function supplyOnPress():Void{
		__onPress();
		//TODO check if below is more suitable
//		parent.__onChildPressed(this);
//		requestFocus();
	}
	
	/**
	 * Simulates click on the component. Performs press and then release actions.
	 */
	public function click():Void {
		____onRollOver();
		____onPress();
		____onRelease();
		____onRollOut();
	}
	
	//-----------------------component's event method-------------
	private function __onPress():Void{
		pressingPoint = getMousePosition();
		pressingComponent = this;
		parent.__onChildPressed(this);
		dispatchEvent(createEventObj(ON_PRESS));
		requestFocus();
	}
	private function __onRelease():Void{
		dispatchEvent(createEventObj(ON_RELEASE));
	}
	private function __onReleaseOutside():Void{
		dispatchEvent(createEventObj(ON_RELEASEOUTSIDE));
	}
	private function __onRollOver():Void{
		dispatchEvent(createEventObj(ON_ROLLOVER));
	}
	private function __onRollOut():Void{
		dispatchEvent(createEventObj(ON_ROLLOUT));
	}
	private function __onDragOver():Void{
		dispatchEvent(createEventObj(ON_DRAGOVER));
	}
	private function __onDragOut():Void{
		dispatchEvent(createEventObj(ON_DRAGOUT));
	}
	
	private function __onClick():Void{
		var time:Number = getTimer();
		var mousePoint:Point = getMousePosition();
		if(mousePoint.equals(_lastClickPoint) && time - lastClickTime < MAX_CLICK_INTERVAL){
			clickCount++;
		}else{
			clickCount = 1;
		}
		lastClickTime = time;
		dispatchEvent(createEventObj(ON_CLICKED, clickCount));
		_lastClickPoint = mousePoint;
	}
	
	private var _lastClickPoint:Point;
	
	private function __onFocusGained():Void{
		if(isFocusOwner() && FocusManager.getCurrentManager().isTraversing()){
			ui.paintFocus(this, getFocusGraphics());
		}
		__onFocusGainedExtraFix();
		dispatchEvent(createEventObj(ON_FOCUS_GAINED));
	}
	private function __onFocusLost():Void{
		clearFocusGraphicsByUI();
		dispatchEvent(createEventObj(ON_FOCUS_LOST));
	}
	private function getInternalFocusObj():Object{
		return clip_mc;
	}
	private function __onFocusGainedExtraFix():Void{
		FocusManager.getCurrentManager().receiveFocus(getInternalFocusObj());
	}
	//-----------------------mc events deletage, can't override these method-------------
	private function ____onPress():Void{
		__onPress();
	}
	
	private function ____onRelease():Void{
		__onRelease();
		__onClick();
	}
	
	private function ____onReleaseOutside():Void{
		__onReleaseOutside();
	}
	
	private function ____onRollOver():Void{
		__onRollOver();
	}
	
	private function ____onRollOut():Void{
		__onRollOut();
	}
	
	private function ____onDragOver():Void{
		__onDragOver();
	}
	
	private function ____onDragOut():Void{
		__onDragOut();
	}

}
