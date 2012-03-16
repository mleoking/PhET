/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.ASWingUtils;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.EmptyLayout;
import org.aswing.geom.Dimension;
import org.aswing.geom.Point;
import org.aswing.geom.Rectangle;
import org.aswing.MCPanel;
import org.aswing.plaf.ToolTipUI;
import org.aswing.UIManager;
import org.aswing.util.Delegate;
import org.aswing.util.DepthManager;
import org.aswing.util.Timer;

/**
 * Used to display a "Tip" for a Component. Typically components provide api
 * to automate the process of using <code>ToolTip</code>s.
 * For example, any AsWing component can use the <code>Component</code>
 * <code>setToolTipText</code> method to specify the text
 * for a standard tooltip. A component that wants to create a custom
 * <code>ToolTip</code>
 * display can create an owner <code>ToolTip</code> and apply it to a component by 
 * <code>JToolTip.setComponent</code> method.
 * @see org.aswing.overflow.JSharedToolTip
 * @author iiley
 */
class org.aswing.JToolTip extends Container {
	
	/**
	 * When the tip text changed.
	 *<br>
	 * onTipTextChanged(source:JTextComponent)
	 */	
	public static var ON_TIP_TEXT_CHANGED :String = "onTipTextChanged";
	/**
	 * When the tip is showing(before showed), with this method you can change the tip text, then the showing text will be the new.
	 *<br>
	 * onTipShowing(source:JTextComponent)
	 */	
	public static var ON_TIP_SHOWING :String = "onTipShowing";
	
	
	//the time waiting after to view tool tip when roll over a component
	private static var WAIT_TIME:Number = 600;
	//when there is one tooltip is just shown, next will shown fast as this time
	private static var FAST_OCCUR_TIME:Number = 50;
	
	private static var last_tip_dropped_time:Number = 0;
	
	private var mcPane:MCPanel;
	
	private var tipText:String;
	private var comp:Component;
	private var offsets:Point;
	private var offsetsRelatedToMouse:Boolean;
	
	private var compListener:Object;
	private var mouseMovedListener:Object;
	private var timer:Timer;
	private var waitThenPopupEnabled:Boolean;
	
	public function JToolTip() {
		super();
		setName("JToolTip");
		offsets = new Point(4, 20);
		offsetsRelatedToMouse = true;
		waitThenPopupEnabled  = true;
		
		compListener = new Object();
		compListener[Component.ON_ROLLOVER] = Delegate.create(this, ____compRollOver);
		compListener[Component.ON_ROLLOUT] = Delegate.create(this, ____compRollOut);
		compListener[Component.ON_HIDDEN] = compListener[Component.ON_ROLLOUT];
		compListener[Component.ON_DESTROY] = compListener[Component.ON_ROLLOUT];
		compListener[Component.ON_PRESS] = compListener[Component.ON_ROLLOUT];
				
		mouseMovedListener = new Object();
		mouseMovedListener.onMouseMove = Delegate.create(this, __onMouseMoved);
		
		timer = new Timer(Number.POSITIVE_INFINITY);
		timer.setRepeats(false);
		timer.setInitialDelay(WAIT_TIME);
		timer.addActionListener(__timeOnAction, this);
				
		setTriggerEnabled(false);
		
		updateUI();
	}
	
	/**
	 * Starts waits to popup, if mouse are not moved for a while, the tool tip will be popuped.
	 */
	public function startWaitToPopup():Void{
		if(getTimer() - last_tip_dropped_time < FAST_OCCUR_TIME){
			timer.setInitialDelay(FAST_OCCUR_TIME);
		}else{
			timer.setInitialDelay(WAIT_TIME);
		}
		timer.restart();
		Mouse.addListener(mouseMovedListener);
	}
	
	/**
	 * Stops waiting.
	 */
	public function stopWaitToPopup():Void{
		timer.stop();
		Mouse.removeListener(mouseMovedListener);
		last_tip_dropped_time = getTimer();
	}
	
	/**
	 * Sets whether the tooltip should wait and then popup automatically when roll over the target component.
	 */
	public function setWaitThenPopupEnabled(b:Boolean):Void{
		waitThenPopupEnabled = b;
	}
	
	/**
	 * Returns whether the tooltip should wait and then popup automatically when roll over the target component.
	 */
	public function isWaitThenPopupEnabled():Boolean{
		return waitThenPopupEnabled;
	}
		
	private function __compRollOver(source:Component):Void{
		if(source == comp && isWaitThenPopupEnabled()){
			startWaitToPopup();
		}
	}
	
	private function __compRollOut(source:Component):Void{
		if(source == comp && isWaitThenPopupEnabled()){
			disposeToolTip();
		}
	}
		
	private function __onMouseMoved():Void{
		if(timer.isRunning()){
			timer.restart();
		}
	}
	
	private function __timeOnAction():Void{
		timer.stop();
		dispatchEvent(createEventObj(ON_TIP_SHOWING));
		disposeToolTip();
		viewToolTip();
	}
	
	/**
	 * view the tool tip on stage
	 */
	private function viewToolTip():Void{
		if(tipText == null){
			return;
		}
		if(mcPane == null || !mcPane.contains(this)){
			mcPane = new MCPanel(getTargetComponent().getRootAncestorMovieClip(), 10000, 10000);
			mcPane.setLayout(new EmptyLayout());
			mcPane.append(this);
		}
		var paneMC:MovieClip = mcPane.getPanelMC();
		var relatePoint:Point = new Point();
		if(offsetsRelatedToMouse){
			relatePoint.setLocation(paneMC._xmouse, paneMC._ymouse);
			paneMC.localToGlobal(relatePoint);
		}else{
			relatePoint = getTargetComponent().getGlobalLocation();
		}
		moveLocationRelatedTo(relatePoint);
		DepthManager.bringToTop(root_mc);
	}
	
	/**
	 * Moves tool tip to new location related to the specified pos(global).
	 */
	public function moveLocationRelatedTo(globalPos:Point):Void{
		if(!isShowing()){
			//not created, so can't move
			return;
		}
		globalPos = new Point(globalPos);
		globalPos.move(offsets.x, offsets.y);
		var viewSize:Dimension = getPreferredSize();
		var visibleBounds:Rectangle = ASWingUtils.getVisibleMaximizedBounds(root_mc._parent);
		
		if(globalPos.x + viewSize.width > visibleBounds.x + visibleBounds.width){
			globalPos.x = visibleBounds.x + visibleBounds.width - viewSize.width;
		}
		if(globalPos.y + viewSize.height > visibleBounds.y + visibleBounds.height){
			globalPos.y = visibleBounds.y + visibleBounds.height - viewSize.height;
		}
		if(globalPos.x < visibleBounds.x){
			globalPos.x = visibleBounds.x;
		}
		if(globalPos.y < visibleBounds.y){
			globalPos.y = visibleBounds.y;
		}
		globalPos.x = Math.round(globalPos.x);
		globalPos.y = Math.round(globalPos.y);
		setGlobalLocation(globalPos);
		setSize(viewSize);
	}
	
	/**
	 * Shows tooltip directly.
	 */
	public function showToolTip():Void{
		viewToolTip();
	}
	
	/**
	 * Disposes the tool tip.
	 */
	public function disposeToolTip():Void{
		stopWaitToPopup();
		removeFromContainer();
	}
	
    public function updateUI():Void{
    	setUI(ToolTipUI(UIManager.getUI(this)));
    }
    
    public function setUI(newUI:ToolTipUI):Void{
    	super.setUI(newUI);
    }
	
	public function getUIClassID():String{
		return "ToolTipUI";
	}
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.basic.BasicToolTipUI;
    }
		
	/**
	 * Sets the text to show when the tool tip is displayed. 
	 * null to set this tooltip will not be displayed.
	 * @param t the String to display, or null not display tool tip.
	 */
	public function setTipText(t:String):Void{
		if(t != tipText){
			tipText = t;
			dispatchEvent(createEventObj(ON_TIP_TEXT_CHANGED));
			if(t == null){
				disposeToolTip();
			}else{
				if(isShowing()){
					setSize(getPreferredSize());
					repaint();
					revalidate();
				}
			}
		}
	}
	
	/**
	 * Returns the text that is shown when the tool tip is displayed. 
	 * The returned value may be null. 
	 * @return the string that displayed.
	 */
	public function getTipText():String{
		return tipText;
	}
	
	/**
	 * Specifies the component that the tooltip describes. 
	 * The component c may be null and will have no effect. 
	 * @param the JComponent being described
	 */
	public function setTargetComponent(c:Component):Void{
		if(c != comp){
			comp.removeEventListener(compListener);
			comp = c;
			comp.addEventListener(compListener);
		}
	}
	
	/**
	 * Returns the component the tooltip applies to. 
	 * The returned value may be null. 
	 * @return the component that the tooltip describes
	 */
	public function getTargetComponent():Component{
		return comp;
	}
	
	/**
	 * Sets the offsets of the tooltip related the described component.
	 * @param o the offsets point, delta x is o.x, delta y is o.y
	 */
	public function setOffsets(o:Point):Void{
		offsets.setLocation(o);
	}
	
	/**
	 * Returns the offsets of the tooltip related the described component.
	 * @return the offsets point, delta x is o.x, delta y is o.y
	 */	
	public function getOffsets():Point{
		return new Point(offsets);
	}
	
	/**
	 * Sets whether the <code>offsets</code> is related the mouse position, otherwise 
	 * it will be related the described component position.
	 * <p>
	 * This change will be taked effect at the next showing, current showing will no be changed.
	 * @param b whether the <code>offsets</code> is related the mouse position
	 */
	public function setOffsetsRelatedToMouse(b:Boolean):Void{
		offsetsRelatedToMouse = b;
	}
	
	/**
	 * Returns whether the <code>offsets</code> is related the mouse position.
	 * @return whether the <code>offsets</code> is related the mouse position
	 * @see #setOffsetsRelatedToMouse()
	 */
	public function isOffsetsRelatedToMouse():Boolean{
		return offsetsRelatedToMouse;
	}
	//-----------can't override these------
	private function ____compRollOver(source:Component):Void{
		__compRollOver(source);
	}
	
	private function ____compRollOut(source:Component):Void{
		__compRollOut(source);
	}	
}
