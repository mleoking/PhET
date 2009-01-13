/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ASColor;
import org.aswing.BoundedRangeModel;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.geom.Dimension;
import org.aswing.geom.Point;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.Pen;
import org.aswing.graphics.SolidBrush;
import org.aswing.Icon;
import org.aswing.JButton;
import org.aswing.JScrollBar;
import org.aswing.LayoutManager;
import org.aswing.LookAndFeel;
import org.aswing.plaf.basic.icon.ArrowIcon;
import org.aswing.plaf.ScrollBarUI;
import org.aswing.UIManager;
import org.aswing.util.Delegate;
import org.aswing.util.MCUtils;
import org.aswing.util.Timer;
 
/**
 *
 * @author iiley
 */
class org.aswing.plaf.basic.BasicScrollBarUI extends ScrollBarUI
	implements LayoutManager{
	
	private var scrollBarWidth:Number;
	private var minimumThumbLength:Number;
	private var thumbRect:Rectangle;
	private var isDragging:Boolean;
	private var offset:Number;
	
    private var thumbHighlightColor:ASColor;
    private var thumbLightHighlightColor:ASColor;
    private var thumbLightShadowColor:ASColor;
    private var thumbDarkShadowColor:ASColor;
    private var thumbColor:ASColor;
    private var arrowShadowColor:ASColor;
    private var arrowLightColor:ASColor;
    
    private var scrollbar:JScrollBar;
    private var incrButton:JButton;
    private var decrButton:JButton;
    private var leftIcon:Icon;
    private var rightIcon:Icon;
    private var upIcon:Icon;
    private var downIcon:Icon;
    
    private var adjusterListener:Object;
    private var incrButtonListener:Object;
    private var decrButtonListener:Object;
    private var blockListener:Object;
    private var keyListener:Object;
    private var mouseListener:Object;
    
    private static var scrollSpeedThrottle:Number = 60; // delay in milli seconds
    private static var initialScrollSpeedThrottle:Number = 500; // first delay in milli seconds
    private var scrollTimer:Timer;
    private var scrollIncrement:Number;
    private var scrollContinueDestination:Number;
	
	public function BasicScrollBarUI(){
		scrollBarWidth = 16;
		minimumThumbLength = 9;
		thumbRect = new Rectangle();
		isDragging = false;
		offset = 0;
		scrollIncrement = 0;
		scrollTimer = new Timer(scrollSpeedThrottle);
		scrollTimer.setInitialDelay(initialScrollSpeedThrottle);
		scrollTimer.addActionListener(__scrollTimerPerformed, this);
	}
    	
    public function installUI(c:Component):Void{
		scrollbar = JScrollBar(c);
		installDefaults();
		installComponents();
		installListeners();
    }
    
	public function uninstallUI(c:Component):Void{
		scrollbar = JScrollBar(c);
		uninstallDefaults();
		uninstallComponents();
		uninstallListeners();
		removeMCs(c);
		scrollTimer.stop();
    }
	
	private function installDefaults():Void{
		configureScrollBarColors();
        scrollbar.setLayout(this);
		LookAndFeel.installBasicProperties(scrollbar, "ScrollBar.");
        LookAndFeel.installBorder(scrollbar, "ScrollBar.border");
	}
    private function configureScrollBarColors():Void{
    	LookAndFeel.installColorsAndFont(scrollbar, "ScrollBar.background", "ScrollBar.foreground", "ScrollBar.font");
		thumbHighlightColor = UIManager.getColor("ScrollBar.thumbHighlight");
		thumbLightHighlightColor = UIManager.getColor("ScrollBar.thumbLightHighlight");
		thumbLightShadowColor = UIManager.getColor("ScrollBar.thumbShadow");
		thumbDarkShadowColor = UIManager.getColor("ScrollBar.thumbDarkShadow");
		thumbColor = UIManager.getColor("ScrollBar.thumb");
		arrowShadowColor = UIManager.getColor("ScrollBar.arrowShadowColor");
		arrowLightColor = UIManager.getColor("ScrollBar.arrowLightColor");
    }
    
    private function createArrowIcon(direction:Number):Icon{
    	var icon:Icon = new ArrowIcon(direction, scrollBarWidth/2,
				    thumbColor,
				    arrowLightColor,
				    arrowShadowColor,
				    thumbHighlightColor);
		return icon;
    }
    
    private function createIcons():Void{
    	leftIcon = createArrowIcon(Math.PI);
    	rightIcon = createArrowIcon(0);
    	upIcon = createArrowIcon(-Math.PI/2);
    	downIcon = createArrowIcon(Math.PI/2);
    }
    
    private function createArrowButton():JButton{
		var b:JButton = new JButton();
		return b;
    }
    
    private function setButtonIcons():Void{
    	if(isVertical()){
    		incrButton.setIcon(downIcon);
    		decrButton.setIcon(upIcon);
    	}else{
    		incrButton.setIcon(rightIcon);
    		decrButton.setIcon(leftIcon);
    	}
    }
    
    private function uninstallDefaults():Void{
    	LookAndFeel.uninstallBorder(scrollbar);
		scrollbar.setLayout(null);
    }
    
	private function installComponents():Void{
		createIcons();
    	incrButton = createArrowButton();
    	decrButton = createArrowButton();
    	setButtonIcons();
        incrButton.setFocusable(false);
        decrButton.setFocusable(false);
        scrollbar.append(incrButton);
        scrollbar.append(decrButton);
		scrollbar.setEnabled(scrollbar.isEnabled());
    }
	private function uninstallComponents():Void{
		scrollbar.remove(incrButton);
		scrollbar.remove(decrButton);
    }
	
	private function installListeners():Void{
		adjusterListener = scrollbar.addAdjustmentListener(__adjustChanged, this);
		
		incrButtonListener = new Object();
		incrButtonListener[Component.ON_PRESS] = Delegate.create(this, __incrButtonPress);
		incrButtonListener[Component.ON_RELEASE] = Delegate.create(this, __incrButtonReleased);
		incrButtonListener[Component.ON_RELEASEOUTSIDE] = incrButtonListener[Component.ON_RELEASE];
		incrButton.addEventListener(incrButtonListener);
		
		decrButtonListener = new Object();
		decrButtonListener[Component.ON_PRESS] = Delegate.create(this, __decrButtonPress);
		decrButtonListener[Component.ON_RELEASE] = Delegate.create(this, __decrButtonReleased);
		decrButtonListener[Component.ON_RELEASEOUTSIDE] = decrButtonListener[Component.ON_RELEASE];
		decrButton.addEventListener(decrButtonListener);
		
		blockListener = new Object();
		blockListener[Component.ON_PRESS] = Delegate.create(this, __trackPress);
		blockListener[Component.ON_RELEASE] = Delegate.create(this, __trackReleased);
		blockListener[Component.ON_RELEASEOUTSIDE] = blockListener[Component.ON_RELEASE];
		scrollbar.addEventListener(blockListener);
		
		keyListener = scrollbar.addEventListener(Component.ON_KEY_DOWN, __onKeyDown, this);
		mouseListener = scrollbar.addEventListener(Component.ON_MOUSE_WHEEL, __onMouseWheel, this);
	}
    
    private function uninstallListeners():Void{
    	scrollbar.removeEventListener(adjusterListener);
    	scrollbar.removeEventListener(incrButtonListener);
    	scrollbar.removeEventListener(decrButtonListener);
    	scrollbar.removeEventListener(blockListener);
    	scrollbar.removeEventListener(keyListener);
    	scrollbar.removeEventListener(mouseListener);
    }
	
	private function createButtonListener():Object{
		var l:Object = new Object();
		return l;
	}
	
    private var thumMC:MovieClip;
    
    private function removeMCs(c:Component):Void{
    	thumMC.removeMovieClip();
    }
    
    public function create(c:Component):Void{
    	thumMC = c.createMovieClip("ui");
    	thumMC.onPress = Delegate.create(this, __startDragThumb);
    	thumMC.onRelease = thumMC.onReleaseOutside = Delegate.create(this, __stopDragThumb);
    	thumbRect.setRect(0,0,0,0);//clear it make sure when draw it is diff, so will redraw
    }
    
    private function isVertical():Boolean{
    	return scrollbar.getOrientation() == JScrollBar.VERTICAL;
    }
    
    private function getThumbRect():Rectangle{
    	return new Rectangle(thumbRect);
    }
    
    //-------------------------listeners--------------------------
    private function __onKeyDown():Void{
		if(!scrollbar.isEnabled()){
			return;
		}
    	var code:Number = Key.getCode();
    	if(code == Key.UP || code == Key.LEFT){
    		scrollByIncrement(-scrollbar.getUnitIncrement());
    	}else if(code == Key.DOWN || code == Key.RIGHT){
    		scrollByIncrement(scrollbar.getUnitIncrement());
    	}else if(code == Key.PGUP){
    		scrollByIncrement(-scrollbar.getBlockIncrement());
    	}else if(code == Key.PGDN){
    		scrollByIncrement(scrollbar.getBlockIncrement());
    	}else if(code == Key.HOME){
    		scrollbar.setValue(scrollbar.getMinimum());
    	}else if(code == Key.END){
    		scrollbar.setValue(scrollbar.getMaximum() - scrollbar.getVisibleAmount());
    	}
    }
    
    private function __onMouseWheel(source:Component, delta:Number):Void{
		if(!scrollbar.isEnabled()){
			return;
		}
    	scrollByIncrement(-delta * scrollbar.getUnitIncrement());
    }
    
    private function __scrollTimerPerformed():Void{
    	var value:Number = scrollbar.getValue() + scrollIncrement;
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
    		scrollbar.setValue(scrollContinueDestination);
    		scrollTimer.stop();
    	}else{
    		scrollByIncrement(scrollIncrement);
    	}
    }
    
    private function __adjustChanged():Void{
    	if(!isDragging)
    		paintAndLocateThumb(scrollbar.getPaintBounds());
    }
    
    private function __incrButtonPress():Void{
    	scrollbar.requestFocus();
    	scrollIncrement = scrollbar.getUnitIncrement();
    	scrollByIncrement(scrollIncrement);
    	scrollContinueDestination = scrollbar.getMaximum() - scrollbar.getVisibleAmount();
    	scrollTimer.restart();
    }
    
    private function __incrButtonReleased():Void{
    	scrollTimer.stop();
    }
    
    private function __decrButtonPress():Void{
    	scrollbar.requestFocus();
    	scrollIncrement = -scrollbar.getUnitIncrement();
    	scrollByIncrement(scrollIncrement);
    	scrollContinueDestination = scrollbar.getMinimum();
    	scrollTimer.restart();
    }
    
    private function __decrButtonReleased():Void{
    	scrollTimer.stop();
    }
    
    private function __trackPress():Void{
    	var aimPoint:Point = scrollbar.getMousePosition();
    	var isPressedInRange:Boolean = false;
    	var tr:Rectangle = getThumbRect();
    	if(isVertical()){
    		var mousePos:Number = aimPoint.y;
    		aimPoint.y -= tr.height/2;
    		if(mousePos < tr.y){
    			isPressedInRange = true;
    		}else if(mousePos > tr.y + tr.height){
    			isPressedInRange = true;
    		}
    	}else{
    		var mousePos:Number = aimPoint.x;
    		aimPoint.x -= tr.width/2;
    		if(mousePos < tr.x){
    			isPressedInRange = true;
    		}else if(mousePos > tr.x + tr.width){
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
    
    private function __trackReleased():Void{
    	scrollTimer.stop();
    }
        
    private function scrollByIncrement(increment:Number):Void{
    	scrollbar.setValue(scrollbar.getValue() + increment);
    }
    
    private function __startDragThumb():Void{
    	//pass a on pressed event to make sure when thumb mc pressed
    	//there will be a event passed to the root of scrollbar's parents
    	scrollbar.supplyOnPress();
    	
    	if(!scrollbar.isEnabled()){
    		return;
    	}
    	scrollbar.setValueIsAdjusting(true);
    	var mp:Point = scrollbar.getMousePosition();
    	var mx:Number = mp.x;
    	var my:Number = mp.y;
    	var tr:Rectangle = getThumbRect();
    	if(isVertical()){
    		offset = my - tr.y;
    	}else{
    		offset = mx - tr.x;
    	}
    	isDragging = true;
    	__startHandleDrag();
    	paintThumb(thumMC, thumbRect.getSize(), isDragging);
    }
    
    private function __stopDragThumb():Void{
    	if(!scrollbar.isEnabled()){
    		return;
    	}
    	if(isDragging){
    		scrollThumbToCurrentMousePosition();
    	}
    	offset = 0;
    	scrollbar.setValueIsAdjusting(false);
    	isDragging = false;
    	__stopHandleDrag();
    	paintThumb(thumMC, thumbRect.getSize(), isDragging);
    }
    
    private function __startHandleDrag():Void{
    	thumMC.onMouseMove = Delegate.create(this, __onMoveThumb);
    }
    private function __stopHandleDrag():Void{
    	thumMC.onMouseMove = undefined;
    	delete thumMC.onMouseMove;
    }
    
    private function __onMoveThumb():Void{
    	if(!scrollbar.isEnabled()){
    		return;
    	}
    	scrollThumbToCurrentMousePosition();
    }
    
    private function scrollThumbToCurrentMousePosition():Void{
    	var mp:Point = scrollbar.getMousePosition();
    	var mx:Number = mp.x;
    	var my:Number = mp.y;
    	var thumbR:Rectangle = getThumbRect();
    	
	    var thumbMin:Number, thumbMax:Number, thumbPos:Number;
	    
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
    	
    	var scrollBarValue:Number = getValueWithThumbMaxMinPos(thumbMin, thumbMax, thumbPos);
    	scrollbar.setValue(scrollBarValue);
    	updateAfterEvent();
    }
    
    private function getValueWithPosition(point:Point):Number{
    	var mx:Number = point.x;
    	var my:Number = point.y;
    	var thumbR:Rectangle = getThumbRect();
    	
	    var thumbMin:Number, thumbMax:Number, thumbPos:Number;
	    
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
    
    private function getValueWithThumbMaxMinPos(thumbMin:Number, thumbMax:Number, thumbPos:Number):Number{
    	var model:BoundedRangeModel = scrollbar.getModel();
    	var scrollBarValue:Number;
    	if (thumbPos >= thumbMax) {
    		scrollBarValue = model.getMaximum() - model.getExtent();
    	}else{
			var valueMax:Number = model.getMaximum() - model.getExtent();
			var valueRange:Number = valueMax - model.getMinimum();
			var thumbValue:Number = thumbPos - thumbMin;
			var thumbRange:Number = thumbMax - thumbMin;
			var value:Number = (thumbValue / thumbRange) * valueRange;
			scrollBarValue = value + model.getMinimum();
    	}
    	return scrollBarValue;    	
    }
    
    //--------------------------paints----------------------------
    
    public function paint(c:Component, g:Graphics, b:Rectangle):Void{
    	super.paint(c, g, b);
    	paintAndLocateThumb(b);
    }
    
    private function paintAndLocateThumb(b:Rectangle):Void{
     	if(!scrollbar.isEnabled()){
    		if(isVertical()){
    			if(incrButton.isEnabled()){
    				trace("Logic Wrong : Scrollbar range is not enabled, but its button enabled ");
    			}
    		}
    		thumMC._visible = false;
    		return;
    	}
    	thumMC._visible = true;
    	var min:Number = scrollbar.getMinimum();
    	var extent:Number = scrollbar.getVisibleAmount();
    	var range:Number = scrollbar.getMaximum() - min;
    	var value:Number = scrollbar.getValue();
    	
    	if(range <= 0){
    		if(range < 0)
    			trace("Logic Wrong : Scrollbar range = " + range + ", max="+scrollbar.getMaximum()+", min="+min);
    		thumMC._visible = false;
    		return;
    	}
    	
    	var trackLength:Number;
    	var thumbLength:Number;
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
    		thumMC._visible = false;
    		return;
    	}
    	
		var thumbRange:Number = trackLength - thumbLength;
		var thumbPos:Number;
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
    
    private function setThumbRect(x:Number, y:Number, w:Number, h:Number):Void{
    	if(!MCUtils.isMovieClipExist(thumMC)){
    		return;
    	}
    	
    	var oldW:Number = thumbRect.width;
    	var oldH:Number = thumbRect.height;
    	
    	thumbRect.setRect(x, y, w, h);
    	
    	if(w != oldW || h != oldH){
    		paintThumb(thumMC, thumbRect.getSize(), isDragging);
    	}
    	thumMC._x = x;
    	thumMC._y = y;
    }
    
    /**
     * LAF notice.
     * 
     * Override this method to paint diff thumb in your LAF.
     */
    private function paintThumb(thumMC:MovieClip, size:Dimension, isPressed:Boolean):Void{
    	var w:Number = size.width;
    	var h:Number = size.height;
    	thumMC.clear();
    	var g:Graphics = new Graphics(thumMC);
    	var b:SolidBrush = new SolidBrush(thumbDarkShadowColor);
    	g.fillRectangle(b, 0, 0, w, h);
    	b.setASColor(this.thumbColor);
    	g.fillRectangle(b, 1, 1, w-2, h-2);
    	
    	var p:Pen = new Pen(thumbDarkShadowColor, 0);
    	if(isVertical()){
	    	var ch:Number = h/2;
	    	g.drawLine(p, 3, ch, w-3, ch);
	    	g.drawLine(p, 3, ch+2, w-3, ch+2);
	    	g.drawLine(p, 3, ch-2, w-3, ch-2);
    	}else{
	    	var cw:Number = w/2;
	    	g.drawLine(p, cw, 3, cw, h-3);
	    	g.drawLine(p, cw+2, 3, cw+2, h-3);
	    	g.drawLine(p, cw-2, 3, cw-2, h-3);
    	}
    }
    
    //--------------------------Dimensions----------------------------
    
    public function getPreferredSize(c:Component):Dimension{
    	return preferredLayoutSize(Container(c));
    }

    public function getMaximumSize(c:Component):Dimension{
		return maximumLayoutSize(Container(c));
    }

    public function getMinimumSize(c:Component):Dimension{
		return getPreferredSize(c);
    }
	
	//--------------------------Layout----------------------------
	private function layoutVScrollbar(sb:JScrollBar):Void{
    	var rd:Rectangle = sb.getPaintBounds();
    	var w:Number = scrollBarWidth;
    	decrButton.setBounds(rd.x, rd.y, w, w);
    	incrButton.setBounds(rd.x, rd.y + rd.height - w, w, w);
	}
	
	private function layoutHScrollbar(sb:JScrollBar):Void{
    	var rd:Rectangle = sb.getPaintBounds();
    	var w:Number = scrollBarWidth;
    	decrButton.setBounds(rd.x, rd.y, w, w);
    	incrButton.setBounds(rd.x + rd.width - w, rd.y, w, w);
	}
	    
	public function layoutContainer(target:Container):Void{
		if(isDragging){
			return;
		}
    	if(target == scrollbar){
    		setButtonIcons();
    		if(isVertical()){
    			layoutVScrollbar(scrollbar);
    		}else{
    			layoutHScrollbar(scrollbar);
    		}
    	}else{
    		trace("BasicScrollBarUI just can be JScrollBar's Layout : " + target);
    	}
    }
	
	/**
	 * @param target
	 * @throws Error when the target is not a JScrollBar
	 */
    public function preferredLayoutSize(target:Container):Dimension{
    	if(target == scrollbar){
    		var w:Number, h:Number;
    		if(isVertical()){
    			w = scrollBarWidth;
    			h = scrollBarWidth*2;
    		}else{
    			w = scrollBarWidth*2;
    			h = scrollBarWidth;
    		}
    		return scrollbar.getInsets().getOutsideSize(new Dimension(w, h));
    	}else{
    		trace("BasicScrollBarUI just can be JScrollBar's Layout : " + target);
    		throw new Error("BasicScrollBarUI just can be JScrollBar's Layout : " + target);
    	}
    }
    
	/**
	 * @param target
	 * @throws Error when the target is not a JScrollBar
	 */    
    public function maximumLayoutSize(target:Container):Dimension{
    	if(target == scrollbar){
    		var w:Number, h:Number;
    		if(isVertical()){
    			w = scrollBarWidth;
    			h = Number.MAX_VALUE;
    		}else{
    			w = Number.MAX_VALUE;
    			h = scrollBarWidth;
    		}
    		return scrollbar.getInsets().getOutsideSize(new Dimension(w, h));
    	}else{
    		trace("BasicScrollBarUI just can be JScrollBar's Layout : " + target);
    		throw new Error("BasicScrollBarUI just can be JScrollBar's Layout : " + target);
    	}
    }
    
    public function minimumLayoutSize(target:Container):Dimension{
    	return preferredLayoutSize(target);
    }
    public function getLayoutAlignmentX(target:Container):Number{
    	return 0;
    }
    public function getLayoutAlignmentY(target:Container):Number{
    	return 0;
    }
	
    public function addLayoutComponent(comp:Component, constraints:Object):Void{}
    public function removeLayoutComponent(comp:Component):Void{}
    public function invalidateLayout(target:Container):Void{}
    
}
