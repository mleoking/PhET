/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.ASColor;
import org.aswing.Component;
import org.aswing.geom.Point;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.Pen;
import org.aswing.JFrame;
import org.aswing.LookAndFeel;
import org.aswing.plaf.basic.BasicWindowUI;
import org.aswing.plaf.basic.frame.FrameTitleBar;
import org.aswing.plaf.UIResource;
import org.aswing.resizer.Resizer;
import org.aswing.UIManager;
import org.aswing.util.Delegate;
import org.aswing.WindowLayout;

/**
 *
 * @author iiley
 */
class org.aswing.plaf.basic.BasicFrameUI extends BasicWindowUI {
	
	private var frame:JFrame;
	private var titleBar:FrameTitleBar;
	private var titleBarListener:Object;
	private var frameListener:Object;
	
	private var resizeArrowColor:ASColor;
	private var resizeArrowLightColor:ASColor;
	private var resizeArrowDarkColor:ASColor;
	
	private var mouseMoveListener:Object;
	private var boundsMC:MovieClip;
	
	public function BasicFrameUI() {
		super();
	}

    // Do not Shared UI object
    //public static function createInstance(c:Component):ComponentUI {
    //    return new BasicFrameUI();
    //}

    public function installUI(c:Component):Void {
        frame = JFrame(c);
        installDefaults();
		installComponents();
		installListeners();
    }

    private function installDefaults():Void {
    	super.installDefaults(frame);
    	var pp:String = "Frame.";
        LookAndFeel.installColorsAndFont(frame, pp + "background", pp + "foreground", pp + "font");
        LookAndFeel.installBorder(frame, pp + "border");
	    resizeArrowColor = UIManager.getColor("Frame.resizeArrow");
	    resizeArrowLightColor = UIManager.getColor("Frame.resizeArrowLight");
	    resizeArrowDarkColor = UIManager.getColor("Frame.resizeArrowDark");
    }
    
    private function installComponents():Void {
    	titleBar = createTitleBar();
    	frame.insert(0, titleBar, WindowLayout.TITLE);
    	
    	if(frame.getResizer() == null || frame.getResizer() instanceof UIResource){
	    	var resizer:Resizer = Resizer(UIManager.getInstance("Frame.resizer"));
	    	frame.setResizer(resizer);
    	}
	}
	private function installListeners():Void{
		var titleLis:Object = new Object();
		titleLis[Component.ON_PRESS] = Delegate.create(this, __onTitleBarPress);
		titleLis[Component.ON_RELEASE] = Delegate.create(this, __onTitleBarRelease);
		titleLis[Component.ON_RELEASEOUTSIDE] = titleLis[Component.ON_RELEASE];
		titleLis[Component.ON_CLICKED] = Delegate.create(this, __onTitleBarClick);
		titleBarListener = titleLis;
		titleBar.addEventListener(titleBarListener);
		mouseMoveListener = new Object();
		mouseMoveListener.onMouseMove = Delegate.create(this, __onMouseMove);
		frameListener = new Object();
		frameListener[JFrame.ON_WINDOW_ACTIVATED] = Delegate.create(frame, frame.repaint);
		frameListener[JFrame.ON_WINDOW_DEACTIVATED] = frameListener[JFrame.ON_WINDOW_ACTIVATED];
		frame.addEventListener(frameListener);
	}

    public function uninstallUI(c:Component):Void {
        var frame:JFrame = JFrame(c);
        uninstallDefaults();
		uninstallComponents();
		uninstallListeners();
		boundsMC.unloadMovie();
		boundsMC.removeMovieClip();
    }
    
    private function uninstallDefaults():Void {
        LookAndFeel.uninstallBorder(frame);
    }
	private function uninstallComponents():Void{
		titleBar.setUI(null); //uninstall its listeners
		frame.remove(titleBar);
	}
	private function uninstallListeners():Void{
		titleBar.removeEventListener(titleBarListener);
		frame.removeEventListener(frameListener);
		titleBarListener = null;
		frameListener = null;
	}
    
    public function create(c:Component):Void{
    	super.create(c);
    	boundsMC = frame.createMovieClipOnRoot("drag_mc");
    }
    //----------------------------------------------------------
    /**
     * Override this method to create different title bar
     */
    private function createTitleBar():FrameTitleBar{
    	var tb:FrameTitleBar = new FrameTitleBar(frame);
    	//To get default UI from Defauls property
    	tb.updateUI();
    	return tb;
    }
    //----------------------------------------------------------
	
	private function isMaximizedFrame():Boolean{
		var state:Number = frame.getState();
		return ((state & JFrame.MAXIMIZED_HORIZ) == JFrame.MAXIMIZED_HORIZ)
				|| ((state & JFrame.MAXIMIZED_VERT) == JFrame.MAXIMIZED_VERT);
	}
	
	private var startPos:Point;
    private function __onTitleBarPress():Void{
    	if(frame.isDragable() && !isMaximizedFrame()){
    		if(frame.isDragDirectly()){
    			frame.startDrag();
    		}else{
    			startPos = frame.getMousePosition();
    			Mouse.addListener(mouseMoveListener);
    		}
    	}
    }
    
    private function __onTitleBarRelease():Void{
    	frame.stopDrag();
    	Mouse.removeListener(mouseMoveListener);
    	if(frame.isDragable() && !isMaximizedFrame()){
	    	var delta:Point = representMoveBounds();
	    	boundsMC.clear();
	    	boundsMC._visible = false;
	    	var loc:Point = frame.getLocation();
	    	loc.move(delta.x, delta.y);
	    	loc.x = Math.round(loc.x);
	    	loc.y = Math.round(loc.y);
	    	frame.setLocation(loc);
	    	frame.validate();
    	}
    }
    
    private function __onTitleBarClick(source:Component, clickCount:Number):Void{
    	if(clickCount == 2){
    		if(frame.isResizable()){
    			var state:Number = frame.getState();
    			
    			if(state & JFrame.MAXIMIZED_HORIZ == JFrame.MAXIMIZED_HORIZ
    				|| state & JFrame.MAXIMIZED_VERT == JFrame.MAXIMIZED_VERT
    				|| state & JFrame.ICONIFIED == JFrame.ICONIFIED){
    					frame.setState(JFrame.NORMAL);
    			}else{
    				frame.setState(JFrame.MAXIMIZED);
    			}
    		}
    	}
    }
    
    private function representMoveBounds():Point{
    	boundsMC._visible = true;
    	var currentPos:Point = frame.getMousePosition();
    	var bounds:Rectangle = frame.getBounds();
    	bounds.x = currentPos.x - startPos.x;
    	bounds.y = currentPos.y - startPos.y;
    	
    	//these make user can't drag frames out the stage
    	var gap:Number = titleBar.getHeight();
    	var frameMaxBounds:Rectangle = frame.getMaximizedBounds();
    	frameMaxBounds.x -= frame.getX();
    	frameMaxBounds.y -= frame.getY();
    	
    	var topLeft:Point = frameMaxBounds.leftTop();
    	var topRight:Point = frameMaxBounds.rightTop();
    	var bottomLeft:Point = frameMaxBounds.leftBottom();
    	if(bounds.x < topLeft.x - bounds.width + gap){
    		bounds.x = topLeft.x - bounds.width + gap;
    	}
    	if(bounds.x > topRight.x - gap){
    		bounds.x = topRight.x - gap;
    	}
    	if(bounds.y < topLeft.y){
    		bounds.y = topLeft.y;
    	}
    	if(bounds.y > bottomLeft.y - gap){
    		bounds.y = bottomLeft.y - gap;
    	}
    	
		var x:Number = bounds.x;
		var y:Number = bounds.y;
		var w:Number = bounds.width;
		var h:Number = bounds.height;
		var g:Graphics = new Graphics(boundsMC);
		boundsMC.clear();
		g.drawRectangle(new Pen(resizeArrowLightColor, 1), x-1,y-1,w+2,h+2);
		g.drawRectangle(new Pen(resizeArrowColor, 1), x,y,w,h);
		g.drawRectangle(new Pen(resizeArrowDarkColor, 1), x+1,y+1,w-2,h-2);
		updateAfterEvent();
		return bounds.leftTop();
    }
    private function __onMouseMove():Void{
    	representMoveBounds();
    }
}
