/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.Component;
import org.aswing.geom.Dimension;
import org.aswing.geom.Point;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.Pen;
import org.aswing.graphics.SolidBrush;
import org.aswing.JSlider;
import org.aswing.JToolTip;
import org.aswing.LookAndFeel;
import org.aswing.plaf.basic.BasicGraphicsUtils;
import org.aswing.plaf.SliderUI;
import org.aswing.UIManager;
import org.aswing.util.Delegate;
import org.aswing.util.Timer;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.BasicSliderUI extends SliderUI {
	
	private var slider:JSlider;
	private var scrollTimer:Timer;
	private var thumbMC:MovieClip;
	private var progressMC:MovieClip;
    private static var scrollSpeedThrottle:Number = 60; // delay in milli seconds
    private static var initialScrollSpeedThrottle:Number = 500; // first delay in milli seconds
	
	private var contentRect:Rectangle;
	private var trackRect:Rectangle;
	private var tickRect:Rectangle;
	private var thumbRect:Rectangle;
	private var trackBuffer:Number;
	private var sliderListener:Object;
	private var offset:Number;
	private var isDragging:Boolean;
	private var scrollIncrement:Number;
	private var scrollContinueDestination:Number;
	
	private var thumbLightHighlightColor:ASColor;
    private var thumbHighlightColor:ASColor;
    private var thumbLightShadowColor:ASColor;
    private var thumbDarkShadowColor:ASColor;
    private var thumbColor:ASColor;

    private var highlightColor:ASColor;
    private var shadowColor:ASColor;
    private var darkShadowColor:ASColor;
    private var lightColor:ASColor;
    
    private var tickColor:ASColor;
    private var progressColor:ASColor;
	
	public function BasicSliderUI() {
		super();
		contentRect = new Rectangle();
		trackRect   = new Rectangle();
		tickRect    = new Rectangle();
		thumbRect   = new Rectangle();
		trackBuffer = 0;
		offset      = 0;
		isDragging  = false;
		scrollTimer = new Timer(scrollSpeedThrottle);
		scrollTimer.setInitialDelay(initialScrollSpeedThrottle);
		scrollTimer.addActionListener(__scrollTimerPerformed, this);
	}
    	
    public function installUI(c:Component):Void{
		slider = JSlider(c);
		installDefaults();
		installListeners();
    }
    
	public function uninstallUI(c:Component):Void{
		slider = JSlider(c);
		uninstallDefaults();
		uninstallListeners();
		removeMCs();
		scrollTimer.stop();
    }
	
	private function installDefaults():Void{
		var pp:String = "Slider.";
        LookAndFeel.installColorsAndFont(slider, pp + "background", pp + "foreground", pp + "font");
		LookAndFeel.installBasicProperties(slider, pp);
        LookAndFeel.installBorder(slider, pp + "border");
        slider.setAlignmentX(0.5);
        slider.setAlignmentY(0.5);
        configureSliderColors();
	}
    private function configureSliderColors():Void{
    	LookAndFeel.installColorsAndFont(slider, "Slider.background", "Slider.foreground", "Slider.font");
		thumbHighlightColor = UIManager.getColor("Slider.thumbHighlight");
		thumbLightHighlightColor = UIManager.getColor("Slider.thumbLightHighlight");
		thumbLightShadowColor = UIManager.getColor("Slider.thumbShadow");
		thumbDarkShadowColor = UIManager.getColor("Slider.thumbDarkShadow");
		thumbColor = UIManager.getColor("Slider.thumb");
		
		highlightColor = UIManager.getColor("Slider.highlight");
		shadowColor = UIManager.getColor("Slider.shadow");
		darkShadowColor = UIManager.getColor("Slider.darkShadow");
		lightColor = UIManager.getColor("Slider.light");
		
		tickColor = UIManager.getColor("Slider.tickColor");
		progressColor = UIManager.getColor("Slider.progressColor");
    }

    private function uninstallDefaults():Void{
    	LookAndFeel.uninstallBorder(slider);
    }	
    
	private function installListeners():Void{
		sliderListener = new Object();
		sliderListener[JSlider.ON_PRESS] = Delegate.create(this, __onSliderPress);
		sliderListener[JSlider.ON_RELEASE] = Delegate.create(this, __onSliderReleased);
		sliderListener[JSlider.ON_RELEASEOUTSIDE] = sliderListener[JSlider.ON_RELEASE];
		sliderListener[JSlider.ON_MOUSE_WHEEL] = Delegate.create(this, __onSliderMouseWheel);
		sliderListener[JSlider.ON_STATE_CHANGED] = Delegate.create(this, __onSliderStateChanged);
		sliderListener[JSlider.ON_KEY_DOWN] = Delegate.create(this, __onSliderKeyDown);
		slider.addEventListener(sliderListener);
	}
	
    private function uninstallListeners():Void{
    	slider.removeEventListener(sliderListener);
    	scrollTimer.stop();
    }
    
    private function __onSliderStateChanged():Void{
    	if(!isDragging){
			calculateThumbLocation(thumbRect);
			locateThumb(thumbRect);
			progressMC.clear();
			paintTrackProgress(new Graphics(progressMC), getTrackDrawRect(trackRect));
    	}
    }
    
    private function __onSliderKeyDown():Void{
		if(!slider.isEnabled()){
			return;
		}
    	var code:Number = Key.getCode();
    	var unit:Number = getUnitIncrement();
    	var block:Number = slider.getMajorTickSpacing() > 0 ? slider.getMajorTickSpacing() : unit*5;
    	if(isSliderVertical()){
    		unit = -unit;
    		block = -block;
    	}
    	if(slider.getInverted()){
    		unit = -unit;
    		block = -block;
    	}
    	if(code == Key.UP || code == Key.LEFT){
    		scrollByIncrement(-unit);
    	}else if(code == Key.DOWN || code == Key.RIGHT){
    		scrollByIncrement(unit);
    	}else if(code == Key.PGUP){
    		scrollByIncrement(-block);
    	}else if(code == Key.PGDN){
    		scrollByIncrement(block);
    	}else if(code == Key.HOME){
    		slider.setValue(slider.getMinimum());
    	}else if(code == Key.END){
    		slider.setValue(slider.getMaximum() - slider.getExtent());
    	}
    }
    
    private function __onSliderPress():Void{
    	var mousePoint:Point = slider.getMousePosition();
    	if(thumbRect.containsPoint(mousePoint)){
    		__startDragThumb();
    	}else{
    		var inverted:Boolean = slider.getInverted();
	    	var thumbCenterPos:Number;
	    	if(isSliderVertical()){
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
	    	__scrollTimerPerformed();//run one time immediately first
    	}
    }
    private function __onSliderReleased():Void{
    	if(isDragging){
    		__stopDragThumb();
    	}
    	if(scrollTimer.isRunning()){
    		scrollTimer.stop();
    	}
    }
    private function __onSliderMouseWheel(source:JSlider, delta:Number):Void{
		if(!slider.isEnabled()){
			return;
		}
    	if(slider.getInverted()){
    		delta = -delta;
    	}
    	scrollByIncrement(delta*getUnitIncrement());
    }
    
    private function __scrollTimerPerformed():Void{
    	var value:Number = slider.getValue() + scrollIncrement;
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
    private function scrollByIncrement(increment:Number):Void{
    	slider.setValue(slider.getValue() + increment);
    }
    private function getUnitIncrement():Number{
    	var unit:Number = 0;
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
    
    private function __startDragThumb():Void{
    	isDragging = true;
    	slider.setValueIsAdjusting(true);
    	var mp:Point = slider.getMousePosition();
    	var mx:Number = mp.x;
    	var my:Number = mp.y;
    	var tr:Rectangle = thumbRect;
    	if(isSliderVertical()){
    		offset = my - tr.y;
    	}else{
    		offset = mx - tr.x;
    	}
    	__startHandleDrag();
    }
    
    private function __stopDragThumb():Void{
    	__stopHandleDrag();
    	if(isDragging){
    		isDragging = false;
    		calculateThumbLocation(thumbRect);
    	}
    	offset = 0;
    	slider.setValueIsAdjusting(false);
    }
    
    private function __startHandleDrag():Void{
    	thumbMC.onMouseMove = Delegate.create(this, __onMoveThumb);
    	showValueTip();
    }
    private function __stopHandleDrag():Void{
    	thumbMC.onMouseMove = undefined;
    	delete thumbMC.onMouseMove;
    	disposValueTip();
    }
    private function __onMoveThumb():Void{
    	scrollThumbToCurrentMousePosition();
    	showValueTip();
    }
    
    private function showValueTip():Void{
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
    private function disposValueTip():Void{
    	slider.getValueTip().disposeToolTip();
    }
    
    private function scrollThumbToCurrentMousePosition():Void{
    	var mp:Point = slider.getMousePosition();
    	var mx:Number = mp.x;
    	var my:Number = mp.y;
    	var thumbR:Rectangle = thumbRect;
    	
	    var thumbMin:Number, thumbMax:Number, thumbPos:Number;
	    var halfThumbLength:Number;
	    var sliderValue:Number;
	    
		var paintThumbRect:Rectangle = thumbRect.clone();
    	if(isSliderVertical()){
    		halfThumbLength = thumbRect.height / 2;
			thumbPos = my - offset;
			var minPos:Number = yPositionForValue(slider.getMinimum()) - halfThumbLength;
			var maxPos:Number = yPositionForValue(slider.getMaximum() - slider.getExtent()) 
				- halfThumbLength;
			if(minPos > maxPos){
				var t:Number = minPos;
				minPos = maxPos;
				maxPos = t;
			}
			thumbPos = Math.max(minPos, Math.min(maxPos, thumbPos));
			sliderValue = valueForYPosition(thumbPos + halfThumbLength);
    		slider.setValue(sliderValue);
    		thumbRect.y = yPositionForValue(slider.getValue()) - halfThumbLength;
    		paintThumbRect.y = thumbPos;
    	}else{
    		halfThumbLength = thumbRect.width / 2;
			thumbPos = mx - offset;
			var minPos:Number = xPositionForValue(slider.getMinimum()) - halfThumbLength;
			var maxPos:Number = xPositionForValue(slider.getMaximum() - slider.getExtent()) 
				- halfThumbLength;
			if(minPos > maxPos){
				var t:Number = minPos;
				minPos = maxPos;
				maxPos = t;
			}
			thumbPos = Math.max(minPos, Math.min(maxPos, thumbPos));
			sliderValue = valueForXPosition(thumbPos + halfThumbLength);
    		slider.setValue(sliderValue);
    		thumbRect.x = xPositionForValue(slider.getValue()) - halfThumbLength;
    		paintThumbRect.x = thumbPos;
    	}
    	locateThumb(paintThumbRect);
		progressMC.clear();
		paintTrackProgress(new Graphics(progressMC), getTrackDrawRect(trackRect));
    	updateAfterEvent();
    }
    //----------------------------------------------------------------
    
    private function removeMCs(c):Void{
    }
    
    public function create(c:Component):Void{
    	progressMC = c.createMovieClip("ui_progress");
    	thumbMC = c.createMovieClip("ui_thumb");
    }
    
    public function paint(c:Component, g:Graphics, b:Rectangle):Void{
    	super.paint(c, g, b);
    	
    	calculateContentsRect(b);
		
		if ( slider.getPaintTrack()) {
		    paintTrack( g, trackRect );
		}
        if ( slider.getPaintTicks()) {
            paintTicks( g, tickRect );
        }
        thumbMC.clear();
		paintThumb( new Graphics(thumbMC), thumbRect.getSize() );
		locateThumb(thumbRect);
    }
    
    private function isSliderVertical():Boolean{
    	return slider.getOrientation() == JSlider.VERTICAL;
    }
    
    private function locateThumb(thumbRect:Rectangle):Void{
    	thumbMC._x = thumbRect.x;
    	thumbMC._y = thumbRect.y;
    }
    
    public function xPositionForValue( value:Number ):Number{
        var min:Number = slider.getMinimum();
        var max:Number = slider.getMaximum();
        var trackLength:Number = trackRect.width;
        var valueRange:Number = max - min;
        var pixelsPerValue:Number = trackLength / valueRange;
        var trackLeft:Number = trackRect.x;
        var trackRight:Number = trackRect.x + (trackRect.width - 0);//0
        var xPosition:Number;

        if ( !slider.getInverted() ) {
            xPosition = trackLeft;
            xPosition += pixelsPerValue * (value - min);
        }else {
            xPosition = trackRight;
            xPosition -= pixelsPerValue * (value - min);
        }

        xPosition = Math.max( trackLeft, xPosition );
        xPosition = Math.min( trackRight, xPosition );

        return xPosition;
    }

    public function yPositionForValue( value:Number ):Number  {
        var min:Number = slider.getMinimum();
        var max:Number = slider.getMaximum();
        var trackLength:Number = trackRect.height; 
        var valueRange:Number = max - min;
        var pixelsPerValue:Number = trackLength / valueRange;
        var trackTop:Number = trackRect.y;
        var trackBottom:Number = trackRect.y + (trackRect.height - 1);
        var yPosition:Number;

        if ( !slider.getInverted() ) {
            yPosition = trackTop;
            yPosition += pixelsPerValue * (max - value);
        }
        else {
            yPosition = trackTop;
            yPosition += pixelsPerValue * (value - min);
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
    public function valueForYPosition( yPos:Number ):Number {
        var value:Number;
		var minValue:Number = slider.getMinimum();
		var maxValue:Number = slider.getMaximum();
		var trackLength:Number = trackRect.height;
		var trackTop:Number = trackRect.y;
		var trackBottom:Number = trackRect.y + (trackRect.height - 1);
		var inverted:Boolean = slider.getInverted();
		if ( yPos <= trackTop ) {
		    value = inverted ? minValue : maxValue;
		}else if ( yPos >= trackBottom ) {
		    value = inverted ? maxValue : minValue;
		}else {
		    var distanceFromTrackTop:Number = yPos - trackTop;
		    var valueRange:Number = maxValue - minValue;
		    var valuePerPixel:Number = valueRange / trackLength;
		    var valueFromTrackTop:Number = distanceFromTrackTop * valuePerPixel;
	
		    value = inverted ? minValue + valueFromTrackTop : maxValue - valueFromTrackTop;
		}
		return value;
    }
  
    /**
     * Returns a value give an x position.  If xPos is past the track at the left or the
     * right it will set the value to the min or max of the slider, depending if the
     * slider is inverted or not.
     */
    public function valueForXPosition( xPos:Number ):Number {
        var value:Number;
		var minValue:Number = slider.getMinimum();
		var maxValue:Number = slider.getMaximum();
		var trackLength:Number = trackRect.width;
		var trackLeft:Number = trackRect.x; 
		var trackRight:Number = trackRect.x + (trackRect.width - 0);//1
		var inverted:Boolean = slider.getInverted();
		if ( xPos <= trackLeft ) {
		    value = inverted ? maxValue : minValue;
		}else if ( xPos >= trackRight ) {
		    value = inverted ? minValue : maxValue;
		}else {
		    var distanceFromTrackLeft:Number = xPos - trackLeft;
		    var valueRange:Number = maxValue - minValue;
		    var valuePerPixel:Number = valueRange / trackLength;
		    var valueFromTrackLeft:Number = distanceFromTrackLeft * valuePerPixel;
		    
		    value = inverted ? maxValue - valueFromTrackLeft : minValue + valueFromTrackLeft;
		}
		return value;
    }    
    
	//*******************************************************************************
	//              Override these methods to easiy implement different look
	//*******************************************************************************
	    
    //--------------------------------Paints--------------------------------
    
    private function paintTrack(g:Graphics, trackRect:Rectangle):Void{
    	var drawRect:Rectangle = getTrackDrawRect(trackRect);
    	if(slider.isEnabled()){
    		progressMC.clear();
    		paintTrackProgress(new Graphics(progressMC), drawRect);
    		BasicGraphicsUtils.paintLoweredBevel(g, drawRect, shadowColor, darkShadowColor, lightColor, highlightColor);
    	}else{
	    	g.beginDraw(new Pen(lightColor, 1));
	    	//g.beginFill(new SolidBrush(darkShadowColor));
	    	drawRect.grow(-1, -1);
	    	g.rectangle(drawRect.x, drawRect.y, drawRect.width, drawRect.height);
	    	g.endDraw();
	    	//g.endFill();
    	}
    }
    private function getTrackDrawRect(trackRect:Rectangle):Rectangle{
    	var tx:Number = trackRect.x;
    	var ty:Number = trackRect.y;
    	var tw:Number = trackRect.width;
    	var th:Number = trackRect.height;
    	var halfTrackLength:Number = 4;
    	
    	var drawRect:Rectangle;
    	if(isSliderVertical()){
    		tx += Math.floor(tw/2-halfTrackLength);
    		drawRect = new Rectangle(tx, ty, halfTrackLength*2, th);
    	}else{
    		ty += Math.floor(th/2-halfTrackLength);
    		drawRect = new Rectangle(tx, ty, tw, halfTrackLength*2);
    	}
    	return drawRect;
    }
    private function paintTrackProgress(g:Graphics, trackDrawRect:Rectangle):Void{
		var width:Number;
		var height:Number;
		var x:Number;
		var y:Number;
		var inverted:Boolean = slider.getInverted();
		if(isSliderVertical()){
			width = trackDrawRect.width-5;
			height = thumbRect.y + thumbRect.height/2 - trackDrawRect.y - 5;
			x = trackDrawRect.x + 2;
			if(inverted){
				y = trackDrawRect.y + 2;
			}else{
				height = trackDrawRect.y + trackDrawRect.height - thumbRect.y - thumbRect.height/2 - 2;
				y = thumbRect.y + thumbRect.height/2;
			}
		}else{
			height = trackDrawRect.height-5;
			if(inverted){
				width = trackDrawRect.x + trackDrawRect.width - thumbRect.x - thumbRect.width/2 - 2;
				x = thumbRect.x + thumbRect.width/2;
			}else{
				width = thumbRect.x + thumbRect.width/2 - trackDrawRect.x - 5;
				x = trackDrawRect.x + 2;
			}
			y = trackDrawRect.y + 2;
		}
		g.fillRectangle(new SolidBrush(progressColor), x, y, width, height);
    }
    
    private function paintTicks(g:Graphics, tickRect:Rectangle):Void{
        var tickBounds:Rectangle = tickRect;
        var i:Number;
        var w:Number = tickBounds.width;
        var h:Number = tickBounds.height;
        var centerEffect:Number, tickHeight:Number;
        var majT:Number = slider.getMajorTickSpacing();
        var minT:Number = slider.getMinorTickSpacing();
		var max:Number = slider.getMaximum();
		
		g.beginDraw(new Pen(tickColor, 0));
			
        if (isSliderVertical()) {
			var xPos:Number = tickBounds.x;

            var value:Number = slider.getMinimum();
            var yPos:Number = 0;

            if ( minT > 0 ) {
	        	var offset:Number = 0;
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
			var yPos:Number = tickBounds.y;
            var value:Number = slider.getMinimum();
            var xPos:Number = 0;

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
    
    private function paintThumb(g:Graphics, size:Dimension):Void{
    	var x:Number = 2;
    	var y:Number = 2;
    	var rw:Number = size.width - 4;
    	var rh:Number = size.height - 4;
    	var enabled:Boolean = slider.isEnabled();
    	
    	var borderC:ASColor = enabled ? thumbDarkShadowColor : thumbColor;
    	var fillC:ASColor = enabled ? thumbColor : thumbColor;
    	
    	g.beginDraw(new Pen(borderC));
    	g.beginFill(new SolidBrush(fillC));
    	g.rectangle(x, y, rw, rh);
    	g.endFill();
    	g.endDraw();
    }
    
    private function paintMinorTickForHorizSlider( g:Graphics, tickBounds:Rectangle, x:Number, y:Number ):Void {
        g.line( x, y, x, y+tickBounds.height / 2 - 1);
    }

    private function paintMajorTickForHorizSlider( g:Graphics, tickBounds:Rectangle, x:Number, y:Number ):Void {
        g.line( x, y, x, y+tickBounds.height - 2);
    }

    private function paintMinorTickForVertSlider( g:Graphics, tickBounds:Rectangle, x:Number, y:Number ):Void {
        g.line( x, y, x+tickBounds.width / 2 - 1, y );
    }

    private function paintMajorTickForVertSlider( g:Graphics, tickBounds:Rectangle, x:Number, y:Number ):Void {
        g.line( x, y, x+tickBounds.width - 2, y );
    }
    //--------------------------------Ranges--------------------------------
    
    
    private function calculateContentsRect(paintBounds:Rectangle):Void{
    	contentRect.setRect(paintBounds);
    	calculateThumbSize(thumbRect);
		calculateTrackRect(trackRect);
		calculateTickRect(tickRect);
		calculateThumbLocation(thumbRect);
    }
    private function calculateTrackBuffer():Void {
        if (isSliderVertical()) {
            trackBuffer = thumbRect.height / 2;
        }else {
            trackBuffer = thumbRect.width / 2;
        }
    }
    
    private function calculateTrackRect(trackRect:Rectangle):Void{
    	calculateTrackBuffer();
		var centerSpacing:Number = 0; // used to center sliders added using BorderLayout.CENTER (bug 4275631)
        if (isSliderVertical()) {
	    	centerSpacing = thumbRect.width;
			if ( slider.getPaintTicks() ) centerSpacing += getTickLength();
		    trackRect.x = contentRect.x +  Math.floor((contentRect.width - centerSpacing - 1)/2);
		    trackRect.y = contentRect.y + trackBuffer;
		    trackRect.width = thumbRect.width;
		    trackRect.height = contentRect.height - (trackBuffer * 2);
		}else {
		    centerSpacing = thumbRect.height;
		    if ( slider.getPaintTicks() ) centerSpacing += getTickLength();
		    trackRect.x = contentRect.x + trackBuffer;
		    trackRect.y = contentRect.y + Math.floor((contentRect.height - centerSpacing - 1)/2);
		    trackRect.width = contentRect.width - (trackBuffer * 2);
		    trackRect.height = thumbRect.height;
		}
    }
    private function calculateTickRect(tickRect:Rectangle):Void{
		if (isSliderVertical()) {
        	tickRect.x = trackRect.x + trackRect.width;
			tickRect.width = getTickLength();
		    tickRect.y = trackRect.y;
		    tickRect.height = trackRect.height;
		    if ( !slider.getPaintTicks() ) {
		        tickRect.x -= 1;
				tickRect.width = 0;
		    }
		}else {
		    tickRect.x = trackRect.x;
		    tickRect.y = trackRect.y + trackRect.height;
		    tickRect.width = trackRect.width;
		    tickRect.height = getTickLength();
		    
		    if ( !slider.getPaintTicks() ) {
		       	tickRect.y -= 1;
				tickRect.height = 0;
		    }
		}
    }
    private function calculateThumbLocation(thumbRect:Rectangle):Void{
        if ( slider.getSnapToTicks() ) {
		    var sliderValue:Number = slider.getValue();
		    var snappedValue:Number = sliderValue; 
		    var majorTickSpacing:Number = slider.getMajorTickSpacing();
		    var minorTickSpacing:Number = slider.getMinorTickSpacing();
		    var tickSpacing:Number = 0;
	    
		    if ( minorTickSpacing > 0 ) {
		        tickSpacing = minorTickSpacing;
		    }else if ( majorTickSpacing > 0 ) {
		        tickSpacing = majorTickSpacing;
		    }

		    if ( tickSpacing != 0 ) {
		        // If it's not on a tick, change the value
		        if ( (sliderValue - slider.getMinimum()) % tickSpacing != 0 ) {
				    var temp:Number = (sliderValue - slider.getMinimum()) / tickSpacing;
				    var whichTick:Number = Math.round( temp );
				    snappedValue = slider.getMinimum() + (whichTick * tickSpacing);
				}
			
				if( snappedValue != sliderValue ) { 
				    slider.setValue( snappedValue );
				}
	    	}
		}
	
        if (isSliderVertical()) {
            var valuePosition:Number = yPositionForValue(slider.getValue());
	    
		    thumbRect.x = trackRect.x;
		    thumbRect.y = valuePosition - (thumbRect.height / 2);
        }else {
            var valuePosition:Number = xPositionForValue(slider.getValue());

		    thumbRect.x = valuePosition - (thumbRect.width / 2);
		    thumbRect.y = trackRect.y;
        }    	
    }
    private function calculateThumbSize(thumbRect:Rectangle):Void{
        if (isSliderVertical()) {
		    thumbRect.width = 20;
		    thumbRect.height = 10;
		}else{
		    thumbRect.width = 10;
		    thumbRect.height = 20;
		}
    }
    /**
     * Gets the height of the tick area for horizontal sliders and the width of the
     * tick area for vertical sliders.  BasicSliderUI uses the returned value to
     * determine the tick area rectangle.  If you want to give your ticks some room,
     * make this larger than you need and paint your ticks away from the sides in paintTicks().
     */
    private function getTickLength():Number {
        return 10;
    }
    //--------------------------Dimensions----------------------------
    
    private function addTickSize(size:Dimension):Void{
    	if(slider.getPaintTicks()){
    		if(isSliderVertical){
    			size.width += getTickLength();
    		}else{
    			size.height += getTickLength();
    		}
    	}
    }
    
    public function getPreferredSize(c:Component):Dimension{
    	var size:Dimension;
    	if(isSliderVertical()){
    		size = new Dimension(21, 200);
    	}else{
    		size = new Dimension(200, 21);
    	}
    	addTickSize(size);
    	return c.getInsets().getOutsideSize(size);
    }

    public function getMaximumSize(c:Component):Dimension{
		return new Dimension(Number.MAX_VALUE, Number.MAX_VALUE);
    }

    public function getMinimumSize(c:Component):Dimension{
    	var size:Dimension;
    	if(isSliderVertical()){
    		size = new Dimension(21, 36);
    	}else{
    		size = new Dimension(36, 21);
    	}
    	addTickSize(size);
    	return c.getInsets().getOutsideSize(size);
    }    
}