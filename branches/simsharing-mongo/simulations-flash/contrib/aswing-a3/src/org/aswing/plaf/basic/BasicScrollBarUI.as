/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic{
	
import org.aswing.plaf.BaseComponentUI;
import org.aswing.*;
import org.aswing.geom.*;
import org.aswing.graphics.*;
import org.aswing.util.*;
import org.aswing.event.*;
import flash.events.*;
import org.aswing.plaf.basic.icon.ArrowIcon;
import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.ui.Keyboard;

/**
 * The basic scrollbar ui.
 * @author iiley
 * @private
 */
public class BasicScrollBarUI extends BaseComponentUI{
	
	protected var scrollBarWidth:int;
	protected var minimumThumbLength:int;
	protected var thumbRect:IntRectangle;
	protected var isDragging:Boolean;
	protected var offset:int;
	
    private var arrowShadowColor:ASColor;
    private var arrowLightColor:ASColor;
    
    protected var scrollbar:JScrollBar;
    protected var thumMC:AWSprite;
	protected var thumbDecorator:GroundDecorator;
    protected var incrButton:JButton;
    protected var decrButton:JButton;
    protected var leftIcon:Icon;
    protected var rightIcon:Icon;
    protected var upIcon:Icon;
    protected var downIcon:Icon;
        
    private static var scrollSpeedThrottle:int = 60; // delay in milli seconds
    private static var initialScrollSpeedThrottle:int = 500; // first delay in milli seconds
    private var scrollTimer:Timer;
    private var scrollIncrement:int;
    private var scrollContinueDestination:int;
	
	public function BasicScrollBarUI(){
		scrollBarWidth = 16;
		minimumThumbLength = 9;
		thumbRect = new IntRectangle();
		isDragging = false;
		offset = 0;
		scrollIncrement = 0;
	}
    	
    protected function getPropertyPrefix():String {
        return "ScrollBar.";
    }    	
    	
    override public function installUI(c:Component):void{
		scrollbar = JScrollBar(c);
		installDefaults();
		installComponents();
		installListeners();
    }
    
	override public function uninstallUI(c:Component):void{
		scrollbar = JScrollBar(c);
		uninstallDefaults();
		uninstallComponents();
		uninstallListeners();
    }
	
	protected function installDefaults():void{
		configureScrollBarColors();
		var pp:String = getPropertyPrefix();
		if(containsKey(pp+"barWidth")){
			scrollBarWidth = getInt(pp+"barWidth");
		}
		if(containsKey(pp+"minimumThumbLength")){
			minimumThumbLength = getInt(pp+"minimumThumbLength");
		}
		LookAndFeel.installBasicProperties(scrollbar, pp);
        LookAndFeel.installBorderAndBFDecorators(scrollbar, pp);
	}
	
    private function configureScrollBarColors():void{
		var pp:String = getPropertyPrefix();
    	LookAndFeel.installColorsAndFont(scrollbar, pp);
		arrowShadowColor = getColor(pp + "arrowShadowColor");
		arrowLightColor = getColor(pp + "arrowLightColor");
    }
    
    protected function uninstallDefaults():void{
    	LookAndFeel.uninstallBorderAndBFDecorators(scrollbar);
    }
    
	protected function installComponents():void{
		thumMC = new AWSprite();
		var pp:String = getPropertyPrefix();    			
		thumbDecorator = getGroundDecorator(pp + "thumbDecorator");
		if(thumbDecorator != null){
			if(thumbDecorator.getDisplay(scrollbar) != null){
				thumMC.addChild(thumbDecorator.getDisplay(scrollbar));
			}
		}
		scrollbar.addChild(thumMC);
		thumMC.addEventListener(MouseEvent.MOUSE_DOWN, __startDragThumb);
		thumMC.addEventListener(ReleaseEvent.RELEASE, __stopDragThumb);
		createIcons();
    	incrButton = createArrowButton();
    	incrButton.setName("JScrollbar_incrButton");
    	decrButton = createArrowButton();
    	decrButton.setName("JScrollbar_decrButton");
    	setButtonIcons();
        incrButton.setUIElement(true);
		decrButton.setUIElement(true);
        scrollbar.addChild(incrButton);
        scrollbar.addChild(decrButton);
		scrollbar.setEnabled(scrollbar.isEnabled());
    }
	protected function uninstallComponents():void{
		scrollbar.removeChild(incrButton);
		scrollbar.removeChild(decrButton);
		scrollbar.removeChild(thumMC);
		thumMC.removeEventListener(MouseEvent.MOUSE_DOWN, __startDragThumb);
		thumMC.removeEventListener(ReleaseEvent.RELEASE, __stopDragThumb);
		thumbDecorator = null;
    }
	
	protected function installListeners():void{
		scrollbar.addStateListener(__adjustChanged);
		
		incrButton.addEventListener(MouseEvent.MOUSE_DOWN, __incrButtonPress);
		incrButton.addEventListener(ReleaseEvent.RELEASE, __incrButtonReleased);
		
		decrButton.addEventListener(MouseEvent.MOUSE_DOWN, __decrButtonPress);
		decrButton.addEventListener(ReleaseEvent.RELEASE, __decrButtonReleased);
				
		scrollbar.addEventListener(MouseEvent.MOUSE_DOWN, __trackPress);
		scrollbar.addEventListener(ReleaseEvent.RELEASE, __trackReleased);
		
		scrollbar.addEventListener(MouseEvent.MOUSE_WHEEL, __onMouseWheel);
		scrollbar.addEventListener(FocusKeyEvent.FOCUS_KEY_DOWN, __onKeyDown);
		
		scrollbar.addEventListener(Event.REMOVED_FROM_STAGE, __destroy);
		
		scrollTimer = new Timer(scrollSpeedThrottle);
		scrollTimer.setInitialDelay(initialScrollSpeedThrottle);
		scrollTimer.addActionListener(__scrollTimerPerformed);
	}
    
    protected function uninstallListeners():void{
		scrollbar.removeStateListener(__adjustChanged);
		
		incrButton.removeEventListener(MouseEvent.MOUSE_DOWN, __incrButtonPress);
		incrButton.removeEventListener(ReleaseEvent.RELEASE, __incrButtonReleased);
		
		decrButton.removeEventListener(MouseEvent.MOUSE_DOWN, __decrButtonPress);
		decrButton.removeEventListener(ReleaseEvent.RELEASE, __decrButtonReleased);
				
		scrollbar.removeEventListener(MouseEvent.MOUSE_DOWN, __trackPress);
		scrollbar.removeEventListener(ReleaseEvent.RELEASE, __trackReleased);
		
		scrollbar.removeEventListener(MouseEvent.MOUSE_WHEEL, __onMouseWheel);
		scrollbar.removeEventListener(FocusKeyEvent.FOCUS_KEY_DOWN, __onKeyDown);
		scrollbar.removeEventListener(Event.REMOVED_FROM_STAGE, __destroy);
		scrollTimer.stop();
		scrollTimer = null;
    }
	    
    protected function isVertical():Boolean{
    	return scrollbar.getOrientation() == JScrollBar.VERTICAL;
    }
    
    protected function getThumbRect():IntRectangle{
    	return thumbRect.clone();
    }
    
    //-------------------------listeners--------------------------
    
    private function __destroy(e:Event):void{
    	scrollTimer.stop();
    	if(isDragging){
    		scrollbar.stage.removeEventListener(MouseEvent.MOUSE_MOVE, __onMoveThumb);
    	}
    }
    
    private function __onMouseWheel(e:MouseEvent):void{
		if(!scrollbar.isEnabled()){
			return;
		}
    	scrollByIncrement(-e.delta * scrollbar.getUnitIncrement());
    }
    
    private function __onKeyDown(e:FocusKeyEvent):void{
		if(!(scrollbar.isEnabled() && scrollbar.isShowing())){
			return;
		}
    	var code:uint = e.keyCode;
    	if(code == Keyboard.UP || code == Keyboard.LEFT){
    		scrollByIncrement(-scrollbar.getUnitIncrement());
    	}else if(code == Keyboard.DOWN || code == Keyboard.RIGHT){
    		scrollByIncrement(scrollbar.getUnitIncrement());
    	}else if(code == Keyboard.PAGE_UP){
    		scrollByIncrement(-scrollbar.getBlockIncrement());
    	}else if(code == Keyboard.PAGE_DOWN){
    		scrollByIncrement(scrollbar.getBlockIncrement());
    	}else if(code == Keyboard.HOME){
    		scrollbar.setValue(scrollbar.getMinimum());
    	}else if(code == Keyboard.END){
    		scrollbar.setValue(scrollbar.getMaximum() - scrollbar.getVisibleAmount());
    	}
    }
    
    private function __scrollTimerPerformed(e:AWEvent):void{
    	var value:int = scrollbar.getValue() + scrollIncrement;
    	var finished:Boolean = false;
    	if(scrollIncrement > 0){
    		if(value >= scrollContinueDestination){
    			finished = true;
    		}
    	}else{
    		if(value <= scrollContinueDestination){
    			finished = true;
    		}
    	}
    	if(finished){
    		scrollbar.setValue(scrollContinueDestination, false);
    		scrollTimer.stop();
    	}else{
    		scrollByIncrement(scrollIncrement);
    	}
    }
    
    private function __adjustChanged(e:Event):void{
    	if(scrollbar.isVisible() && !isDragging)
    		paintAndLocateThumb(scrollbar.getPaintBounds());
    }
    
    private function __incrButtonPress(e:Event):void{
    	scrollIncrement = scrollbar.getUnitIncrement();
    	scrollByIncrement(scrollIncrement);
    	scrollContinueDestination = scrollbar.getMaximum() - scrollbar.getVisibleAmount();
    	scrollTimer.restart();
    }
    
    private function __incrButtonReleased(e:Event):void{
    	scrollTimer.stop();
    }
    
    private function __decrButtonPress(e:Event):void{
    	scrollIncrement = -scrollbar.getUnitIncrement();
    	scrollByIncrement(scrollIncrement);
    	scrollContinueDestination = scrollbar.getMinimum();
    	scrollTimer.restart();
    }
    
    private function __decrButtonReleased(e:Event):void{
    	scrollTimer.stop();
    }
    
    private function __trackPress(e:MouseEvent):void{
    	var aimPoint:IntPoint = scrollbar.getMousePosition();
    	var isPressedInRange:Boolean = false;
    	var tr:IntRectangle = getThumbRect();
    	var mousePos:int;
    	if(isVertical()){
    		mousePos = aimPoint.y;
    		aimPoint.y -= tr.height/2;
    		if(mousePos < tr.y && mousePos > decrButton.y + decrButton.height){
    			isPressedInRange = true;
    		}else if(mousePos > tr.y + tr.height && mousePos < incrButton.y){
    			isPressedInRange = true;
    		}
    	}else{
    		mousePos = aimPoint.x;
    		aimPoint.x -= tr.width/2;
    		if(mousePos < tr.x && mousePos > decrButton.x + decrButton.width){
    			isPressedInRange = true;
    		}else if(mousePos > tr.x + tr.width && mousePos < incrButton.x){
    			isPressedInRange = true;
    		}
    	}
    	
    	if(isPressedInRange){
    		scrollContinueDestination = getValueWithPosition(aimPoint);
    		if(scrollContinueDestination > scrollbar.getValue()){
    			scrollIncrement = scrollbar.getBlockIncrement();
    		}else{
    			scrollIncrement = -scrollbar.getBlockIncrement();
    		}
    		scrollByIncrement(scrollIncrement);
    		scrollTimer.restart();
    	}
    }
    
    private function __trackReleased(e:Event):void{
    	scrollTimer.stop();
    }
        
    private function scrollByIncrement(increment:int):void{
    	scrollbar.setValue(scrollbar.getValue() + increment, false);
    }
    
    private function __startDragThumb(e:Event):void{
    	if(!scrollbar.isEnabled()){
    		return;
    	}
    	scrollbar.setValueIsAdjusting(true);
    	var mp:IntPoint = scrollbar.getMousePosition();
    	var mx:int = mp.x;
    	var my:int = mp.y;
    	var tr:IntRectangle = getThumbRect();
    	if(isVertical()){
    		offset = my - tr.y;
    	}else{
    		offset = mx - tr.x;
    	}
    	isDragging = true;
    	__startHandleDrag();
    }
    
    private function __stopDragThumb(e:Event):void{
    	__stopHandleDrag();
    	if(!scrollbar.isEnabled()){
    		return;
    	}
    	if(isDragging){
    		scrollThumbToCurrentMousePosition();
    	}
    	offset = 0;
    	isDragging = false;
    	scrollbar.setValueIsAdjusting(false);
    }
    
    private function __startHandleDrag():void{
    	scrollbar.stage.addEventListener(MouseEvent.MOUSE_MOVE, __onMoveThumb, false, 0, true);
    }
    private function __stopHandleDrag():void{
    	scrollbar.stage.removeEventListener(MouseEvent.MOUSE_MOVE, __onMoveThumb);
    }
    
    private function __onMoveThumb(e:MouseEvent):void{
    	if(!scrollbar.isEnabled()){
    		return;
    	}
    	scrollThumbToCurrentMousePosition();
    	e.updateAfterEvent();
    }
    
    private function scrollThumbToCurrentMousePosition():void{
    	var mp:IntPoint = scrollbar.getMousePosition();
    	var mx:int = mp.x;
    	var my:int = mp.y;
    	var thumbR:IntRectangle = getThumbRect();
    	
	    var thumbMin:int, thumbMax:int, thumbPos:int;
	    
    	if(isVertical()){
			thumbMin = decrButton.getY() + decrButton.getHeight();
			thumbMax = incrButton.getY() - thumbR.height;
			thumbPos = Math.min(thumbMax, Math.max(thumbMin, (my - offset)));
			setThumbRect(thumbR.x, thumbPos, thumbR.width, thumbR.height);	
    	}else{
		    thumbMin = decrButton.getX() + decrButton.getWidth();
		    thumbMax = incrButton.getX() - thumbR.width;
			thumbPos = Math.min(thumbMax, Math.max(thumbMin, (mx - offset)));
			setThumbRect(thumbPos, thumbR.y, thumbR.width, thumbR.height);
    	}
    	
    	var scrollBarValue:int = getValueWithThumbMaxMinPos(thumbMin, thumbMax, thumbPos);
    	scrollbar.setValue(scrollBarValue, false);
    }
    
    private function getValueWithPosition(point:IntPoint):int{
    	var mx:int = point.x;
    	var my:int = point.y;
    	var thumbR:IntRectangle = getThumbRect();
    	
	    var thumbMin:int, thumbMax:int, thumbPos:int;
	    
    	if(isVertical()){
			thumbMin = decrButton.getY() + decrButton.getHeight();
			thumbMax = incrButton.getY() - thumbR.height;
			thumbPos = my;
    	}else{
		    thumbMin = decrButton.getX() + decrButton.getWidth();
		    thumbMax = incrButton.getX() - thumbR.width;
		    thumbPos = mx;
    	}
    	return getValueWithThumbMaxMinPos(thumbMin, thumbMax, thumbPos);
    }
    
    private function getValueWithThumbMaxMinPos(thumbMin:int, thumbMax:int, thumbPos:int):int{
    	var model:BoundedRangeModel = scrollbar.getModel();
    	var scrollBarValue:int;
    	if (thumbPos >= thumbMax) {
    		scrollBarValue = model.getMaximum() - model.getExtent();
    	}else{
			var valueMax:int = model.getMaximum() - model.getExtent();
			var valueRange:int = valueMax - model.getMinimum();
			var thumbValue:int = thumbPos - thumbMin;
			var thumbRange:int = thumbMax - thumbMin;
			var value:int = Math.round((thumbValue / thumbRange) * valueRange);
			scrollBarValue = value + model.getMinimum();
    	}
    	return scrollBarValue;    	
    }
    
    //--------------------------paints----------------------------
    override public function paint(c:Component, g:Graphics2D, b:IntRectangle):void{
    	super.paint(c, g, b);
    	layoutScrollBar();
    	paintAndLocateThumb(b);
    }
    
    private function paintAndLocateThumb(b:IntRectangle):void{
     	if(!scrollbar.isEnabled()){
    		if(isVertical()){
    			if(scrollbar.mouseChildren){
    				trace("Logic Wrong : Scrollbar is not enabled, but its children enabled ");
    			}
    		}
    		thumMC.visible = false;
    		return;
    	}
    	thumMC.visible = true;
    	var min:int = scrollbar.getMinimum();
    	var extent:int = scrollbar.getVisibleAmount();
    	var range:int = scrollbar.getMaximum() - min;
    	var value:int = scrollbar.getValue();
    	
    	if(range <= 0){
    		if(range < 0)
    			trace("Logic Wrong : Scrollbar range = " + range + ", max="+scrollbar.getMaximum()+", min="+min);
    		thumMC.visible = false;
    		return;
    	}
    	
    	var trackLength:int;
    	var thumbLength:int;
    	if(isVertical()){
    		trackLength = b.height - incrButton.getHeight() - decrButton.getHeight();
    		thumbLength = Math.floor(trackLength*(extent/range));
    	}else{
    		trackLength = b.width - incrButton.getWidth() - decrButton.getWidth();
    		thumbLength = Math.floor(trackLength*(extent/range));
    	}
    	if(trackLength > minimumThumbLength){
    		thumbLength = Math.max(thumbLength, minimumThumbLength);
    	}else{
			//trace("The visible range is so short can't view thumb now!");
    		thumMC.visible = false;
    		return;
    	}
    	
		var thumbRange:int = trackLength - thumbLength;
		var thumbPos:int;
		if((range - extent) == 0){
			thumbPos = 0;
		}else{
			thumbPos = Math.round(thumbRange * ((value - min) / (range - extent)));
		}
		if(isVertical()){
			setThumbRect(b.x, thumbPos + b.y + decrButton.getHeight(), 
						scrollBarWidth, thumbLength);
		}else{
			setThumbRect(thumbPos + b.x + decrButton.getWidth(), b.y, 
						thumbLength, scrollBarWidth);
		}
    }
    
    private function setThumbRect(x:int, y:int, w:int, h:int):void{
    	var oldW:int = thumbRect.width;
    	var oldH:int = thumbRect.height;
    	
    	thumbRect.setRectXYWH(x, y, w, h);
    	
    	if(w != oldW || h != oldH){
    		paintThumb(thumMC, thumbRect.getSize(), isDragging);
    	}
    	thumMC.x = x;
    	thumMC.y = y;
    }
    
    /**
     * LAF notice.
     * 
     * Override this method to paint diff thumb in your LAF.
     */
    private function paintThumb(thumMC:Sprite, size:IntDimension, isPressed:Boolean):void{
    	thumMC.graphics.clear();
    	var g:Graphics2D = new Graphics2D(thumMC.graphics);
    	if(thumbDecorator != null){
    		thumbDecorator.updateDecorator(scrollbar, g, size.getBounds());
    	}
    }
    /**
     * LAF notice.
     * 
     * Override this method to paint diff thumb in your LAF.
     */    
    protected function createArrowIcon(direction:Number):Icon{
    	var icon:Icon = new ArrowIcon(direction, scrollBarWidth/2,
				    arrowLightColor,
				    arrowShadowColor);
		return icon;
    }
    
    /**
     * LAF notice.
     * 
     * Override this method to paint diff thumb in your LAF.
     */    
    protected function createArrowButton():JButton{
		var b:JButton = new JButton();
		b.setFocusable(false);
		return b;
    }
        
    protected function createIcons():void{
    	leftIcon = createArrowIcon(Math.PI);
    	rightIcon = createArrowIcon(0);
    	upIcon = createArrowIcon(-Math.PI/2);
    	downIcon = createArrowIcon(Math.PI/2);
    }
    
    protected function setButtonIcons():void{
    	if(isVertical()){
    		incrButton.setIcon(downIcon);
    		decrButton.setIcon(upIcon);
    	}else{
    		incrButton.setIcon(rightIcon);
    		decrButton.setIcon(leftIcon);
    	}
    }     
    //--------------------------Dimensions----------------------------
    
    override public function getPreferredSize(c:Component):IntDimension{
		var w:int, h:int;
		if(isVertical()){
			w = scrollBarWidth;
			h = scrollBarWidth*2;
		}else{
			w = scrollBarWidth*2;
			h = scrollBarWidth;
		}
		return scrollbar.getInsets().getOutsideSize(new IntDimension(w, h));
    }

    override public function getMaximumSize(c:Component):IntDimension{
		var w:int, h:int;
		if(isVertical()){
			w = scrollBarWidth;
			h = 100000;
		}else{
			w = 100000;
			h = scrollBarWidth;
		}
		return scrollbar.getInsets().getOutsideSize(new IntDimension(w, h));
    }

    override public function getMinimumSize(c:Component):IntDimension{
		return getPreferredSize(c);
    }
	
	//--------------------------Layout----------------------------
	protected function layoutVScrollbar(sb:JScrollBar):void{
    	var rd:IntRectangle = sb.getPaintBounds();
    	var w:int = scrollBarWidth;
    	decrButton.setComBoundsXYWH(rd.x, rd.y, w, w);
    	incrButton.setComBoundsXYWH(rd.x, rd.y + rd.height - w, w, w);
	}
	
	protected function layoutHScrollbar(sb:JScrollBar):void{
    	var rd:IntRectangle = sb.getPaintBounds();
    	var w:int = scrollBarWidth;
    	decrButton.setComBoundsXYWH(rd.x, rd.y, w, w);
    	incrButton.setComBoundsXYWH(rd.x + rd.width - w, rd.y, w, w);
	}
	    
	public function layoutScrollBar():void{
		if(isDragging){
			return;
		}
		setButtonIcons();
		if(isVertical()){
			layoutVScrollbar(scrollbar);
		}else{
			layoutHScrollbar(scrollbar);
		}
    }
	
}

}