/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic{

import flash.display.Shape;
import flash.events.*;
import flash.ui.Keyboard;

import org.aswing.*;
import org.aswing.event.*;
import org.aswing.geom.*;
import org.aswing.graphics.*;
import org.aswing.plaf.*;
import org.aswing.util.*;

/**
 * Basic slider ui imp.
 * @author iiley
 * @private
 */
public class BasicSliderUI extends BaseComponentUI implements SliderUI{
	
	protected var slider:JSlider;
	protected var thumbIcon:Icon;

	protected var highlightColor:ASColor;
	protected var shadowColor:ASColor;
	protected var darkShadowColor:ASColor;
	protected var lightColor:ASColor;
	protected var tickColor:ASColor;
	protected var progressColor:ASColor;
	
	protected var trackRect:IntRectangle;
	protected var trackDrawRect:IntRectangle;
	protected var tickRect:IntRectangle;
	protected var thumbRect:IntRectangle;
	
	private var offset:int;
	private var isDragging:Boolean;
	private var scrollIncrement:int;
	private var scrollContinueDestination:int;
	private var scrollTimer:Timer;
	private static var scrollSpeedThrottle:Number = 60; // delay in milli seconds
	private static var initialScrollSpeedThrottle:Number = 500; // first delay in milli seconds
	
	protected var progressCanvas:Shape;
	
	public function BasicSliderUI(){
		super();
		trackRect   = new IntRectangle();
		tickRect	= new IntRectangle();
		thumbRect   = new IntRectangle();
		trackDrawRect = new IntRectangle();
		offset	  = 0;
		isDragging  = false;
	}
		
	protected function getPropertyPrefix():String {
		return "Slider.";
	}
		
	override public function installUI(c:Component):void{
		slider = JSlider(c);
		installDefaults();
		installComponents();
		installListeners();
	}
	
	override public function uninstallUI(c:Component):void{
		slider = JSlider(c);
		uninstallDefaults();
		uninstallComponents();
		uninstallListeners();
	}
	
	protected function installDefaults():void{
		var pp:String = getPropertyPrefix();
		LookAndFeel.installColorsAndFont(slider, pp);
		LookAndFeel.installBasicProperties(slider, pp);
		LookAndFeel.installBorderAndBFDecorators(slider, pp);
		configureSliderColors();
	}
	
	protected function configureSliderColors():void{
		var pp:String = getPropertyPrefix();
		highlightColor = getColor(pp+"highlight");
		shadowColor = getColor(pp+"shadow");
		darkShadowColor = getColor(pp+"darkShadow");
		lightColor = getColor(pp+"light");
		
		tickColor = getColor(pp+"tickColor");
		progressColor = getColor(pp+"progressColor");
	}
	
	protected function uninstallDefaults():void{
		LookAndFeel.uninstallBorderAndBFDecorators(slider);
	}
	
	protected function installComponents():void{
		var pp:String = getPropertyPrefix();
		thumbIcon = getIcon(pp+"thumbIcon");
		if(thumbIcon.getDisplay(slider)==null){
			throw new Error("Slider thumb icon must has its own display object(getDisplay()!=null)!");
		}
		progressCanvas = new Shape();
		slider.addChild(progressCanvas);
		slider.addChild(thumbIcon.getDisplay(slider));
	}
	
	protected function uninstallComponents():void{
		slider.removeChild(progressCanvas);
		slider.removeChild(thumbIcon.getDisplay(slider));
		thumbIcon = null;
		progressCanvas = null;
	}
	
	protected function installListeners():void{
		slider.addEventListener(MouseEvent.MOUSE_DOWN, __onSliderPress);
		slider.addEventListener(ReleaseEvent.RELEASE, __onSliderReleased);
		slider.addEventListener(MouseEvent.MOUSE_WHEEL, __onSliderMouseWheel);
		slider.addStateListener(__onSliderStateChanged);
		slider.addEventListener(FocusKeyEvent.FOCUS_KEY_DOWN, __onSliderKeyDown);
		scrollTimer = new Timer(scrollSpeedThrottle);
		scrollTimer.setInitialDelay(initialScrollSpeedThrottle);
		scrollTimer.addActionListener(__scrollTimerPerformed);
	}
	
	protected function uninstallListeners():void{
		slider.removeEventListener(MouseEvent.MOUSE_DOWN, __onSliderPress);
		slider.removeEventListener(ReleaseEvent.RELEASE, __onSliderReleased);
		slider.removeEventListener(MouseEvent.MOUSE_WHEEL, __onSliderMouseWheel);
		slider.removeStateListener(__onSliderStateChanged);
		slider.removeEventListener(FocusKeyEvent.FOCUS_KEY_DOWN, __onSliderKeyDown);
		scrollTimer.stop();
		scrollTimer = null;
	}
	
	protected function isVertical():Boolean{
		return slider.getOrientation() == JSlider.VERTICAL;
	}
	
	override public function paint(c:Component, g:Graphics2D, b:IntRectangle):void{
		super.paint(c, g, b);
		countTrackRect(b);
		countThumbRect();
		countTickRect(b);
		paintTrack(g, trackDrawRect);
		paintThumb(g, thumbRect);
		paintTick(g, tickRect);
	}
	
	protected function countTrackRect(b:IntRectangle):void{
		var thumbSize:IntDimension = getThumbSize();
		var h_margin:int, v_margin:int;
		if(isVertical()){
			v_margin = Math.ceil(thumbSize.height/2.0);
			h_margin = thumbSize.width/3-1;
			trackDrawRect.setRectXYWH(b.x+h_margin, b.y+v_margin, 
				h_margin+2, b.height-v_margin*2);
			trackRect.setRectXYWH(b.x, b.y+v_margin, 
				thumbSize.width, b.height-v_margin*2);
		}else{
			h_margin = Math.ceil(thumbSize.width/2.0);
			v_margin = thumbSize.height/3-1;
			trackDrawRect.setRectXYWH(b.x+h_margin, b.y+v_margin, 
				b.width-h_margin*2, v_margin+2);
			trackRect.setRectXYWH(b.x+h_margin, b.y, 
				b.width-h_margin*2, thumbSize.height);
		}
	}
	
	protected function countTickRect(b:IntRectangle):void{
		if(isVertical()){
			tickRect.y = trackRect.y;
			tickRect.x = trackRect.x+trackRect.width+getTickTrackGap();
			tickRect.height = trackRect.height;
			tickRect.width = b.width-trackRect.width-getTickTrackGap();
		}else{
			tickRect.x = trackRect.x;
			tickRect.y = trackRect.y+trackRect.height+getTickTrackGap();
			tickRect.width = trackRect.width;
			tickRect.height = b.height-trackRect.height-getTickTrackGap();
		}
	}
	
	protected function countThumbRect():void{
		thumbRect.setSize(getThumbSize());
		if (slider.getSnapToTicks()){
			var sliderValue:int = slider.getValue();
			var snappedValue:int = sliderValue; 
			var majorTickSpacing:int = slider.getMajorTickSpacing();
			var minorTickSpacing:int = slider.getMinorTickSpacing();
			var tickSpacing:int = 0;
			if (minorTickSpacing > 0){
				tickSpacing = minorTickSpacing;
			}else if (majorTickSpacing > 0){
				tickSpacing = majorTickSpacing;
			}
			if (tickSpacing != 0){
				// If it's not on a tick, change the value
				if ((sliderValue - slider.getMinimum()) % tickSpacing != 0){
					var temp:Number = (sliderValue - slider.getMinimum()) / tickSpacing;
					var whichTick:int = Math.round( temp );
					snappedValue = slider.getMinimum() + (whichTick * tickSpacing);
				}
				if(snappedValue != sliderValue){ 
					slider.setValue(snappedValue);
				}
			}
		}
		var valuePosition:int;
		if (isVertical()) {
			valuePosition = yPositionForValue(slider.getValue());
			thumbRect.x = trackRect.x;
			thumbRect.y = valuePosition - (thumbRect.height / 2);
		}else {
			valuePosition = xPositionForValue(slider.getValue());
			thumbRect.x = valuePosition - (thumbRect.width / 2);
			thumbRect.y = trackRect.y;
		}
	}
	
	protected function getThumbSize():IntDimension{
		if(isVertical()){
			return new IntDimension(thumbIcon.getIconHeight(slider), thumbIcon.getIconWidth(slider));
		}else{
			return new IntDimension(thumbIcon.getIconWidth(slider), thumbIcon.getIconHeight(slider));
		}
	}
	
	protected function countTickSize(sliderRect:IntRectangle):IntDimension{
		if(isVertical()){
			return new IntDimension(getTickLength(), sliderRect.height);
		}else{
			return new IntDimension(sliderRect.width, getTickLength());
		}
	}
	
	/**
	 * Gets the height of the tick area for horizontal sliders and the width of the
	 * tick area for vertical sliders.  BasicSliderUI uses the returned value to
	 * determine the tick area rectangle.  If you want to give your ticks some room,
	 * make this larger than you need and paint your ticks away from the sides in paintTicks().
	 */
	protected function getTickLength():Number {
		return 10;
	}	
	
	protected function countTrackAndThumbSize(sliderRect:IntRectangle):IntDimension{
		if(isVertical()){
			return new IntDimension(getThumbSize().width, sliderRect.height);
		}else{
			return new IntDimension(sliderRect.width, getThumbSize().height);
		}
	}
	
	protected function getTickTrackGap():int{
		return 2;
	}
	
	public function xPositionForValue(value:int):Number{
		var min:int = slider.getMinimum();
		var max:int = slider.getMaximum();
		var trackLength:int = trackRect.width;
		var valueRange:int = max - min;
		var pixelsPerValue:Number = trackLength / valueRange;
		var trackLeft:int = trackRect.x;
		var trackRight:int = trackRect.x + (trackRect.width - 0);//0
		var xPosition:int;

		if ( !slider.getInverted() ) {
			xPosition = trackLeft;
			xPosition += Math.round(pixelsPerValue * (value - min));
		}else {
			xPosition = trackRight;
			xPosition -= Math.round(pixelsPerValue * (value - min));
		}

		xPosition = Math.max( trackLeft, xPosition );
		xPosition = Math.min( trackRight, xPosition );

		return xPosition;
	}

	public function yPositionForValue( value:int ):int  {
		var min:int = slider.getMinimum();
		var max:int = slider.getMaximum();
		var trackLength:int = trackRect.height; 
		var valueRange:int = max - min;
		var pixelsPerValue:Number = trackLength / valueRange;
		var trackTop:int = trackRect.y;
		var trackBottom:int = trackRect.y + (trackRect.height - 1);
		var yPosition:int;

		if ( !slider.getInverted() ) {
			yPosition = trackTop;
			yPosition += Math.round(pixelsPerValue * (max - value));
		}
		else {
			yPosition = trackTop;
			yPosition += Math.round(pixelsPerValue * (value - min));
		}

		yPosition = Math.max( trackTop, yPosition );
		yPosition = Math.min( trackBottom, yPosition );

		return yPosition;
	}	
	
	/**
	 * Returns a value give a y position.  If yPos is past the track at the top or the
	 * bottom it will set the value to the min or max of the slider, depending if the
	 * slider is inverted or not.
	 */
	public function valueForYPosition( yPos:int ):int {
		var value:int;
		var minValue:int = slider.getMinimum();
		var maxValue:int = slider.getMaximum();
		var trackLength:int = trackRect.height;
		var trackTop:int = trackRect.y;
		var trackBottom:int = trackRect.y + (trackRect.height - 1);
		var inverted:Boolean = slider.getInverted();
		if ( yPos <= trackTop ) {
			value = inverted ? minValue : maxValue;
		}else if ( yPos >= trackBottom ) {
			value = inverted ? maxValue : minValue;
		}else {
			var distanceFromTrackTop:int = yPos - trackTop;
			var valueRange:int = maxValue - minValue;
			var valuePerPixel:Number = valueRange / trackLength;
			var valueFromTrackTop:int = Math.round(distanceFromTrackTop * valuePerPixel);
	
			value = inverted ? minValue + valueFromTrackTop : maxValue - valueFromTrackTop;
		}
		return value;
	}
  
	/**
	 * Returns a value give an x position.  If xPos is past the track at the left or the
	 * right it will set the value to the min or max of the slider, depending if the
	 * slider is inverted or not.
	 */
	public function valueForXPosition( xPos:int ):int {
		var value:int;
		var minValue:int = slider.getMinimum();
		var maxValue:int = slider.getMaximum();
		var trackLength:int = trackRect.width;
		var trackLeft:int = trackRect.x; 
		var trackRight:int = trackRect.x + (trackRect.width - 0);//1
		var inverted:Boolean = slider.getInverted();
		if ( xPos <= trackLeft ) {
			value = inverted ? maxValue : minValue;
		}else if ( xPos >= trackRight ) {
			value = inverted ? minValue : maxValue;
		}else {
			var distanceFromTrackLeft:int = xPos - trackLeft;
			var valueRange:int = maxValue - minValue;
			var valuePerPixel:Number = valueRange / trackLength;
			var valueFromTrackLeft:int = Math.round(distanceFromTrackLeft * valuePerPixel);
			
			value = inverted ? maxValue - valueFromTrackLeft : minValue + valueFromTrackLeft;
		}
		return value;
	}
	
	//-------------------------
	
	protected function paintTrack(g:Graphics2D, drawRect:IntRectangle):void{
		if(!slider.getPaintTrack()){
			return;
		}
		if(slider.isEnabled()){
			BasicGraphicsUtils.paintLoweredBevel(g, drawRect, shadowColor, darkShadowColor, lightColor, highlightColor);
			paintTrackProgress(new Graphics2D(progressCanvas.graphics), drawRect);
		}else{
			g.beginDraw(new Pen(lightColor, 1));
			drawRect.grow(-1, -1);
			g.rectangle(drawRect.x, drawRect.y, drawRect.width, drawRect.height);
			g.endDraw();
		}
	}
	protected function paintTrackProgress(g:Graphics2D, trackDrawRect:IntRectangle):void{
		if(!slider.getPaintTrack()){
			return;
		}		
		var rect:IntRectangle = trackDrawRect.clone();
		var width:int;
		var height:int;
		var x:int;
		var y:int;
		var inverted:Boolean = slider.getInverted();
		if(isVertical()){
			width = rect.width-5;
			height = thumbRect.y + thumbRect.height/2 - rect.y - 5;
			x = rect.x + 2;
			if(inverted){
				y = rect.y + 2;
			}else{
				height = rect.y + rect.height - thumbRect.y - thumbRect.height/2 - 2;
				y = thumbRect.y + thumbRect.height/2;
			}
		}else{
			height = rect.height-5;
			if(inverted){
				width = rect.x + rect.width - thumbRect.x - thumbRect.width/2 - 2;
				x = thumbRect.x + thumbRect.width/2;
			}else{
				width = thumbRect.x + thumbRect.width/2 - rect.x - 5;
				x = rect.x + 2;
			}
			y = rect.y + 2;
		}
		g.fillRectangle(new SolidBrush(progressColor), x, y, width, height);		
	}
	
	protected function paintThumb(g:Graphics2D, drawRect:IntRectangle):void{
		if(isVertical()){
			thumbIcon.getDisplay(slider).rotation = 90;
			thumbIcon.updateIcon(slider, g, drawRect.x+thumbIcon.getIconHeight(slider), drawRect.y);
		}else{
			thumbIcon.getDisplay(slider).rotation = 0;
			thumbIcon.updateIcon(slider, g, drawRect.x, drawRect.y);
		}
	}
	
	protected function paintTick(g:Graphics2D, drawRect:IntRectangle):void{
		if(!slider.getPaintTicks()){
			return;
		}		
		var tickBounds:IntRectangle = drawRect;
		var majT:int = slider.getMajorTickSpacing();
		var minT:int = slider.getMinorTickSpacing();
		var max:int = slider.getMaximum();
		
		g.beginDraw(new Pen(tickColor, 0));
			
		var yPos:int = 0;
		var value:int = 0;
		var xPos:int = 0;
			
		if (isVertical()) {
			xPos = tickBounds.x;
			value = slider.getMinimum();
			yPos = 0;

			if ( minT > 0 ) {
				while ( value <= max ) {
					yPos = yPositionForValue( value );
					paintMinorTickForVertSlider( g, tickBounds, xPos, yPos );
					value += minT;
				}
			}

			if ( majT > 0 ) {
				value = slider.getMinimum();
				while ( value <= max ) {
					yPos = yPositionForValue( value );
					paintMajorTickForVertSlider( g, tickBounds, xPos, yPos );
					value += majT;
				}
			}
		}else {
			yPos = tickBounds.y;
			value = slider.getMinimum();
			xPos = 0;

			if ( minT > 0 ) {
				while ( value <= max ) {
					xPos = xPositionForValue( value );
					paintMinorTickForHorizSlider( g, tickBounds, xPos, yPos );
					value += minT;
				}
			}

			if ( majT > 0 ) {
				value = slider.getMinimum();

				while ( value <= max ) {
					xPos = xPositionForValue( value );
					paintMajorTickForHorizSlider( g, tickBounds, xPos, yPos );
					value += majT;
				}
			}
		}
		g.endDraw();		
	}

	private function paintMinorTickForHorizSlider( g:Graphics2D, tickBounds:IntRectangle, x:int, y:int ):void {
		g.line( x, y, x, y+tickBounds.height / 2 - 1);
	}

	private function paintMajorTickForHorizSlider( g:Graphics2D, tickBounds:IntRectangle, x:int, y:int ):void {
		g.line( x, y, x, y+tickBounds.height - 2);
	}

	private function paintMinorTickForVertSlider( g:Graphics2D, tickBounds:IntRectangle, x:int, y:int ):void {
		g.line( x, y, x+tickBounds.width / 2 - 1, y );
	}

	private function paintMajorTickForVertSlider( g:Graphics2D, tickBounds:IntRectangle, x:int, y:int ):void {
		g.line( x, y, x+tickBounds.width - 2, y );
	}
	
	//----------------------------
	
	private function __onSliderStateChanged(e:Event):void{
		if(!isDragging){
			countThumbRect();
			paintThumb(null, thumbRect);
			progressCanvas.graphics.clear();
			paintTrackProgress(new Graphics2D(progressCanvas.graphics), trackDrawRect);
		}
	}
	
	private function __onSliderKeyDown(e:FocusKeyEvent):void{
		if(!slider.isEnabled()){
			return;
		}
		var code:uint = e.keyCode;
		var unit:int = getUnitIncrement();
		var block:int = slider.getMajorTickSpacing() > 0 ? slider.getMajorTickSpacing() : unit*5;
		if(isVertical()){
			unit = -unit;
			block = -block;
		}
		if(slider.getInverted()){
			unit = -unit;
			block = -block;
		}
		if(code == Keyboard.UP || code == Keyboard.LEFT){
			scrollByIncrement(-unit);
		}else if(code == Keyboard.DOWN || code == Keyboard.RIGHT){
			scrollByIncrement(unit);
		}else if(code == Keyboard.PAGE_UP){
			scrollByIncrement(-block);
		}else if(code == Keyboard.PAGE_DOWN){
			scrollByIncrement(block);
		}else if(code == Keyboard.HOME){
			slider.setValue(slider.getMinimum());
		}else if(code == Keyboard.END){
			slider.setValue(slider.getMaximum() - slider.getExtent());
		}
	}
	
	private function __onSliderPress(e:Event):void{
		var mousePoint:IntPoint = slider.getMousePosition();
		if(thumbRect.containsPoint(mousePoint)){
			__startDragThumb();
		}else{
			var inverted:Boolean = slider.getInverted();
			var thumbCenterPos:Number;
			if(isVertical()){
				thumbCenterPos = thumbRect.y + thumbRect.height/2;
				if(mousePoint.y > thumbCenterPos){
					scrollIncrement = inverted ? getUnitIncrement() : -getUnitIncrement();
				}else{
					scrollIncrement = inverted ? -getUnitIncrement() : getUnitIncrement();
				}
				scrollContinueDestination = valueForYPosition(mousePoint.y);
			}else{
				thumbCenterPos = thumbRect.x + thumbRect.width/2;
				if(mousePoint.x > thumbCenterPos){
					scrollIncrement = inverted ? -getUnitIncrement() : getUnitIncrement();
				}else{
					scrollIncrement = inverted ? getUnitIncrement() : -getUnitIncrement();
				}
				scrollContinueDestination = valueForXPosition(mousePoint.x);
			}
			scrollTimer.restart();
			__scrollTimerPerformed(null);//run one time immediately first
		}
	}
	private function __onSliderReleased(e:Event):void{
		if(isDragging){
			__stopDragThumb();
		}
		if(scrollTimer.isRunning()){
			scrollTimer.stop();
		}
	}
	private function __onSliderMouseWheel(e:MouseEvent):void{
		if(!slider.isEnabled()){
			return;
		}
		var delta:int = e.delta;
		if(slider.getInverted()){
			delta = -delta;
		}
		scrollByIncrement(delta*getUnitIncrement());
	}
	
	private function __scrollTimerPerformed(e:Event):void{
		var value:int = slider.getValue() + scrollIncrement;
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
			slider.setValue(scrollContinueDestination);
			scrollTimer.stop();
		}else{
			scrollByIncrement(scrollIncrement);
		}
	}	
	
	private function scrollByIncrement(increment:int):void{
		slider.setValue(slider.getValue() + increment);
	}
	
	private function getUnitIncrement():int{
		var unit:int = 0;
		if(slider.getMinorTickSpacing() >0 ){
			unit = slider.getMinorTickSpacing();
		}else if(slider.getMajorTickSpacing() > 0){
			unit = slider.getMajorTickSpacing();
		}else{
			var range:Number = slider.getMaximum() - slider.getMinimum();
			if(range > 2){
				unit = Math.max(1, Math.round(range/500));
			}else{
				unit = range/100;
			}
		}
		return unit;
	}
	
	private function __startDragThumb():void{
		isDragging = true;
		slider.setValueIsAdjusting(true);
		var mp:IntPoint = slider.getMousePosition();
		var mx:int = mp.x;
		var my:int = mp.y;
		var tr:IntRectangle = thumbRect;
		if(isVertical()){
			offset = my - tr.y;
		}else{
			offset = mx - tr.x;
		}
		__startHandleDrag();
	}
	
	private function __stopDragThumb():void{
		__stopHandleDrag();
		if(isDragging){
			isDragging = false;
			countThumbRect();
		}
		slider.setValueIsAdjusting(false);
		offset = 0;
	}
	
	private function __startHandleDrag():void{
		if(slider.stage){
			slider.stage.addEventListener(MouseEvent.MOUSE_MOVE, __onMoveThumb, false, 0, true);
			slider.addEventListener(Event.REMOVED_FROM_STAGE, __onMoveThumbRFS, false, 0, true);
			showValueTip();
		}
	}
	private function __onMoveThumbRFS(e:Event):void{
		slider.stage.removeEventListener(MouseEvent.MOUSE_MOVE, __onMoveThumb);
		slider.removeEventListener(Event.REMOVED_FROM_STAGE, __onMoveThumbRFS);
	}
	private function __stopHandleDrag():void{
		if(slider.stage){
			__onMoveThumbRFS(null);
		}
		disposValueTip();
	}
	private function __onMoveThumb(e:MouseEvent):void{
		scrollThumbToCurrentMousePosition();
		showValueTip();
		e.updateAfterEvent();
	}
	
	protected function showValueTip():void{
		if(slider.getShowValueTip()){
			var tip:JToolTip = slider.getValueTip();
			tip.setWaitThenPopupEnabled(false);
			tip.setTipText(slider.getValue()+"");
			if(!tip.isShowing()){
				tip.showToolTip();
			}
			tip.moveLocationRelatedTo(slider.componentToGlobal(slider.getMousePosition()));
		}
	}
	
	protected function disposValueTip():void{
		if(slider.getValueTip() != null){
			slider.getValueTip().disposeToolTip();
		}
	}
	
	protected function scrollThumbToCurrentMousePosition():void{
		var mp:IntPoint = slider.getMousePosition();
		var mx:int = mp.x;
		var my:int = mp.y;
		
		var thumbPos:int, minPos:int, maxPos:int;
		var halfThumbLength:int;
		var sliderValue:int;
		var paintThumbRect:IntRectangle = thumbRect.clone();
		if(isVertical()){
			halfThumbLength = thumbRect.height / 2;
			thumbPos = my - offset;
			if(!slider.getInverted()){
				maxPos = yPositionForValue(slider.getMinimum()) - halfThumbLength;
				minPos = yPositionForValue(slider.getMaximum() - slider.getExtent()) - halfThumbLength;
			}else{
				minPos = yPositionForValue(slider.getMinimum()) - halfThumbLength;
				maxPos = yPositionForValue(slider.getMaximum() - slider.getExtent()) - halfThumbLength;
			}
			thumbPos = Math.max(minPos, Math.min(maxPos, thumbPos));
			sliderValue = valueForYPosition(thumbPos + halfThumbLength);
			slider.setValue(sliderValue);
			thumbRect.y = yPositionForValue(slider.getValue()) - halfThumbLength;
			paintThumbRect.y = thumbPos;
		}else{
			halfThumbLength = thumbRect.width / 2;
			thumbPos = mx - offset;
			if(slider.getInverted()){
				maxPos = xPositionForValue(slider.getMinimum()) - halfThumbLength;
				minPos = xPositionForValue(slider.getMaximum() - slider.getExtent()) - halfThumbLength;
			}else{
				minPos = xPositionForValue(slider.getMinimum()) - halfThumbLength;
				maxPos = xPositionForValue(slider.getMaximum() - slider.getExtent()) - halfThumbLength;
			}
			thumbPos = Math.max(minPos, Math.min(maxPos, thumbPos));
			sliderValue = valueForXPosition(thumbPos + halfThumbLength);
			slider.setValue(sliderValue);
			thumbRect.x = xPositionForValue(slider.getValue()) - halfThumbLength;
			paintThumbRect.x = thumbPos;
		}
		paintThumb(null, paintThumbRect);
		progressCanvas.graphics.clear();
		paintTrackProgress(new Graphics2D(progressCanvas.graphics), trackDrawRect);
	}

    public function getTrackMargin():Insets{
    	var b:IntRectangle = slider.getPaintBounds();
    	countTrackRect(b);
    	
    	var insets:Insets = new Insets();
    	insets.top = trackRect.y - b.y;
    	insets.bottom = b.y + b.height - trackRect.y - trackRect.height;
    	insets.left = trackRect.x - b.x;
    	insets.right = b.x + b.width - trackRect.x - trackRect.width;
    	return insets;
    }	
	
	//---------------------
	
	protected function getPrefferedLength():int{
		return 200;
	}
		
    override public function getPreferredSize(c:Component):IntDimension{
    	var size:IntDimension;
    	var thumbSize:IntDimension = getThumbSize();
    	var tickLength:int = this.getTickLength();
    	var gap:int = this.getTickTrackGap();
    	var wide:int = slider.getPaintTicks() ? gap+tickLength : 0;
    	if(isVertical()){
    		wide += thumbSize.width;
    		size = new IntDimension(wide, Math.max(wide, getPrefferedLength()));
    	}else{
    		wide += thumbSize.height;
    		size = new IntDimension(Math.max(wide, getPrefferedLength()), wide);
    	}
    	return c.getInsets().getOutsideSize(size);
    }

    override public function getMaximumSize(c:Component):IntDimension{
		return IntDimension.createBigDimension();
    }

	override public function getMinimumSize(c:Component):IntDimension{
    	var size:IntDimension;
    	var thumbSize:IntDimension = getThumbSize();
    	var tickLength:int = this.getTickLength();
    	var gap:int = this.getTickTrackGap();
    	var wide:int = slider.getPaintTicks() ? gap+tickLength : 0;
    	if(isVertical()){
    		wide += thumbSize.width;
    		size = new IntDimension(wide, thumbSize.height);
    	}else{
    		wide += thumbSize.height;
    		size = new IntDimension(thumbSize.width, wide);
    	}
    	return c.getInsets().getOutsideSize(size);
    }    	
}
}