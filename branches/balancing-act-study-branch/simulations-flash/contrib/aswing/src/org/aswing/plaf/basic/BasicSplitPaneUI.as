/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.Cursor;
import org.aswing.CursorManager;
import org.aswing.ElementCreater;
import org.aswing.geom.Dimension;
import org.aswing.geom.Point;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.SolidBrush;
import org.aswing.Insets;
import org.aswing.overflow.JSplitPane;
import org.aswing.LayoutManager;
import org.aswing.LookAndFeel;
import org.aswing.MouseManager;
import org.aswing.plaf.basic.splitpane.Divider;
import org.aswing.plaf.SplitPaneUI;
import org.aswing.UIManager;
import org.aswing.util.Delegate;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.BasicSplitPaneUI extends SplitPaneUI implements LayoutManager{
	
	private var sp:JSplitPane;
	private var divider:Divider;
	private var lastContentSize:Dimension;
	private var spLis:Object;
	private var divLis:Object;
	private var mouseLis:Object;
	private var vSplitCursor:Cursor;
	private var hSplitCursor:Cursor;
	private var presentDragColor:ASColor;
	
	public function BasicSplitPaneUI() {
		super();
	}
	    
    public function installUI(c:Component):Void {
        sp = JSplitPane(c);
        installDefaults();
        installComponents();
        installListeners();
    }

    public function uninstallUI(c:Component):Void {
        sp = JSplitPane(c);
        uninstallDefaults();
        uninstallComponents();
        uninstallListeners();
    }

    private function installDefaults():Void {
    	var pp:String = "SplitPane.";
        LookAndFeel.installColorsAndFont(sp, pp + "background", pp + "foreground", pp + "font");
        LookAndFeel.installBorder(sp, pp+"border");
        LookAndFeel.installBasicProperties(sp, pp);
        presentDragColor = UIManager.getColor(pp+"presentDragColor");
        lastContentSize = new Dimension();
        sp.setLayout(this);
    }

    private function uninstallDefaults():Void {
        LookAndFeel.uninstallBorder(sp);
    }
	
	private function installComponents():Void{
		vSplitCursor = createSplitCursor(true);
		hSplitCursor = createSplitCursor(false);
		divider = createDivider();
		sp.append(divider, JSplitPane.DIVIDER);
		
		divLis = new Object();
		divLis[Divider.ON_PRESS] = Delegate.create(this, __div_pressed);
		divLis[Divider.ON_RELEASE] = Delegate.create(this, __div_released);
		divLis[Divider.ON_RELEASEOUTSIDE] = divLis[Divider.ON_RELEASE];
		divLis[Divider.ON_ROLLOVER] = Delegate.create(this, __div_rollover);
		divLis[Divider.ON_ROLLOUT] = Delegate.create(this, __div_rollout);
		divider.addEventListener(divLis);
		divider.getCollapseLeftButton().addActionListener(__collapseLeft, this);
		divider.getCollapseRightButton().addActionListener(__collapseRight, this);
	}
	
	private function uninstallComponents():Void{
		sp.remove(divider);
		divider = null;
		divLis = null;
	}
	
	private function installListeners():Void{
		spLis = new Object();
		spLis[JSplitPane.ON_KEY_DOWN] = Delegate.create(this, __on_splitpane_key_down);
		spLis[JSplitPane.ON_DIVIDER_MOVED] = Delegate.create(this, __div_location_changed);
		sp.addEventListener(spLis);
		
		mouseLis = new Object();
		mouseLis[MouseManager.ON_MOUSE_MOVE] = Delegate.create(this, __div_mouse_moving);
		//add it when start drag
	}
	
	private function uninstallListeners():Void{
		sp.removeEventListener(spLis);
		spLis = null;
	}
	
	/**
	 * Override this method to return a different splitCursor for your UI<br>
	 * Credit to Kristof Neirynck for added this.
	 */
	private function createSplitCursor(vertical:Boolean):Cursor{
		var result:Cursor;
		if(vertical){
			result = new Cursor(Cursor.V_RESIZE_CURSOR);
		}else{
			result = new Cursor(Cursor.H_RESIZE_CURSOR);
		}
		return result;
	}
	
	/**
	 * Override this method to return a different divider for your UI
	 */
	private function createDivider():Divider{
		return new Divider(sp);
	}
    
	/**
	 * Override this method to return a different default divider size for your UI
	 */
    private function getDefaultDividerSize():Number{
    	return 10;
    }
    /**
	 * Override this method to return a different default DividerDragingRepresention for your UI
	 */
    private function paintDividerDragingRepresention(g:Graphics):Void{
		g.fillRectangle(new SolidBrush(presentDragColor), 0, 0, divider.getWidth(), divider.getHeight());
    }
	
    /**
     * Messaged to relayout the JSplitPane based on the preferred size
     * of the children components.
     */
    public function resetToPreferredSizes(jc:JSplitPane):Void{
    	var loc:Number = jc.getDividerLocation();
    	if(isVertical()){
    		if(jc.getLeftComponent() == null){
    			loc = 0;
    		}else{
    			loc = jc.getLeftComponent().getPreferredHeight();
    		}
    	}else{
    		if(jc.getLeftComponent() == null){
    			loc = 0;
    		}else{
    			loc = jc.getLeftComponent().getPreferredWidth();
    		}
    	}
		loc = Math.max(
			getMinimumDividerLocation(), 
			Math.min(loc, getMaximumDividerLocation()));
		jc.setDividerLocation(loc);
    }
    
	public function paint(c:Component, g:Graphics, b:Rectangle):Void{
		super.paint(c, g, b);
		divider.paintImmediately();
	}    
    
    public function layoutWithLocation(location:Number):Void{
    	var rect:Rectangle = sp.getSize().getBounds(0, 0);
    	rect = sp.getInsets().getInsideBounds(rect);
    	var lc:Component = sp.getLeftComponent();
    	var rc:Component = sp.getRightComponent();
    	var dvSize:Number = getDividerSize();
    	var lcSize:Number = 0;
    	var rcSize:Number = 0;
    	location = Math.floor(location);
    	if(location < 0){
    		//collapse left
			if(isVertical()){
				divider.setBounds(rect.x, rect.y, rect.width, dvSize);
				rc.setBounds(rect.x, rect.y+dvSize, rect.width, rect.height-dvSize);
			}else{
				divider.setBounds(rect.x, rect.y, dvSize, rect.height);
				rc.setBounds(rect.x+dvSize, rect.y, rect.width-dvSize, rect.height);
			}
    		lc.setVisible(false);
    		rc.setVisible(true);
    	}else if(location == Number.MAX_VALUE){
    		//collapse right
			if(isVertical()){
				divider.setBounds(
					rect.x, 
					rect.y+rect.height-dvSize, 
					rect.width, 
					dvSize);
				lc.setBounds(
					rect.x, 
					rect.y,
					rect.width, 
					rect.height-dvSize);
			}else{
				divider.setBounds(
					rect.x+rect.width-dvSize, 
					rect.y, 
					dvSize, 
					rect.height);
				lc.setBounds(
					rect.x, 
					rect.y,
					rect.width-dvSize, 
					rect.height);
			}
    		lc.setVisible(true);
    		rc.setVisible(false);
    	}else{
    		//both visible
			if(isVertical()){
				divider.setBounds(
					rect.x, 
					rect.y+location, 
					rect.width, 
					dvSize);
				lc.setBounds(
					rect.x, 
					rect.y,
					rect.width, 
					location);
				rc.setBounds(
					rect.x, 
					rect.y+location+dvSize, 
					rect.width, 
					rect.height-location-dvSize
				);
			}else{
				divider.setBounds(
					rect.x+location, 
					rect.y, 
					dvSize, 
					rect.height);
				lc.setBounds(
					rect.x, 
					rect.y,
					location, 
					rect.height);
				rc.setBounds(
					rect.x+location+dvSize, 
					rect.y, 
					rect.width-location-dvSize, 
					rect.height
				);
			}
    		lc.setVisible(true);
    		rc.setVisible(true);
    	}
    	lc.revalidateIfNecessary();
    	rc.revalidateIfNecessary();
    	divider.revalidateIfNecessary();
    }
    
    public function getMinimumDividerLocation():Number{
    	var leftCom:Component = sp.getLeftComponent();
    	if(leftCom == null){
    		return 0;
    	}else{
    		if(isVertical()){
    			return leftCom.getMinimumHeight();
    		}else{
    			return leftCom.getMinimumWidth();
    		}
    	}
    }
    
    public function getMaximumDividerLocation():Number{
    	var rightCom:Component = sp.getRightComponent();
    	var insets:Insets = sp.getInsets();
    	var rightComSize:Number = 0;
    	if(rightCom != null){
    		rightComSize = isVertical() ? rightCom.getMinimumHeight() : rightCom.getMinimumWidth();
    	}
		if(isVertical()){
			return sp.getHeight() - insets.top - insets.bottom - getDividerSize() - rightComSize;
		}else{
			return sp.getWidth() - insets.left - insets.right - getDividerSize() - rightComSize;
		}
    }
    
    private function isVertical():Boolean{
    	return sp.getOrientation() == JSplitPane.VERTICAL_SPLIT;
    }
    
    private function getDividerSize():Number{
    	var si:Number = sp.getDividerSize();
    	if(si == null){
    		return getDefaultDividerSize();
    	}else{
    		return si;
    	}
    }
    
    private function restrictDividerLocation(loc:Number):Number{
    	return Math.max(
				getMinimumDividerLocation(), 
				Math.min(loc, getMaximumDividerLocation()));
    }
    //-----------------------------------------------------------------------
    
	private function __collapseLeft() : Void {
		if(sp.getDividerLocation() == Number.MAX_VALUE){
			sp.setDividerLocation(sp.getLastDividerLocation());
			divider.getCollapseLeftButton().setEnabled(true);
			divider.getCollapseRightButton().setEnabled(true);
		}else if(sp.getDividerLocation() >= 0){
			sp.setDividerLocation(-1);
			divider.getCollapseLeftButton().setEnabled(false);
		}else{
			divider.getCollapseLeftButton().setEnabled(true);
		}
	}

	private function __collapseRight() : Void {
		if(sp.getDividerLocation() < 0){
			sp.setDividerLocation(sp.getLastDividerLocation());
			divider.getCollapseRightButton().setEnabled(true);
			divider.getCollapseLeftButton().setEnabled(true);
		}else if(sp.getDividerLocation() != Number.MAX_VALUE){
			sp.setDividerLocation(Number.MAX_VALUE);
			divider.getCollapseRightButton().setEnabled(false);
		}else{
			divider.getCollapseRightButton().setEnabled(false);
		}
	}
	
	private function __on_splitpane_key_down():Void{
		var code:Number = Key.getCode();
		var dir:Number = 0;
		if(code == Key.HOME){
			if(sp.getDividerLocation() < 0){
				sp.setDividerLocation(sp.getLastDividerLocation());
			}else{
				sp.setDividerLocation(-1);
			}
			return;
		}else if(code == Key.END){
			if(sp.getDividerLocation() == Number.MAX_VALUE){
				sp.setDividerLocation(sp.getLastDividerLocation());
			}else{
				sp.setDividerLocation(Number.MAX_VALUE);
			}
			return;
		}
		if(code == Key.LEFT || code == Key.UP){
			dir = -1;
		}else if(code == Key.RIGHT || code == Key.DOWN){
			dir = 1;
		}
		if(Key.isDown(Key.SHIFT)){
			dir *= 10;
		}
		sp.setDividerLocation(restrictDividerLocation(sp.getDividerLocation() + dir));
	}
    
    private function __div_location_changed():Void{
    	layoutWithLocation(sp.getDividerLocation());
        if(sp.getDividerLocation() >= 0 && sp.getDividerLocation() != Number.MAX_VALUE){
        	divider.setEnabled(true);
        }else{
        	divider.setEnabled(false);
        }
    }
	
	private var startDragPos:Point;
	private var startLocation:Number;
	private var startDividerPos:Point;
	private var dragRepresentationMC:MovieClip;
	
	private function __div_pressed() : Void {
		startDragPos = sp.getMousePosition();
		startLocation = sp.getDividerLocation();
		startDividerPos = divider.getGlobalLocation();
		MouseManager.removeEventListener(mouseLis);
		MouseManager.addEventListener(mouseLis);
	}

	private function __div_released() : Void {
		if(!divider.hitTestMouse()){
			__div_rollout();
		}
		if(dragRepresentationMC != null){
			dragRepresentationMC.removeMovieClip();
			dragRepresentationMC = null;
		}
		validateDivMoveWithCurrentMousePos();
		MouseManager.removeEventListener(mouseLis);
	}

	private function __div_mouse_moving() : Void {
		if(!sp.isContinuousLayout()){
			if(dragRepresentationMC == null){
				dragRepresentationMC = ElementCreater.getInstance().createMC(sp.getRootAncestorMovieClip(), "split");
				var g:Graphics = new Graphics(dragRepresentationMC);
				paintDividerDragingRepresention(g);
			}
			var mouseP:Point = sp.getMousePosition();
			var newGlobalPos:Point = startDividerPos.clone();
			if(isVertical()){
				newGlobalPos.y += getCurrentMovedDistance();
			}else{
				newGlobalPos.x += getCurrentMovedDistance();
			}
			dragRepresentationMC._parent.globalToLocal(newGlobalPos);
			dragRepresentationMC._x = Math.round(newGlobalPos.x);
			dragRepresentationMC._y = Math.round(newGlobalPos.y);
		}else{
			validateDivMoveWithCurrentMousePos();
		}
	}
	
	private function validateDivMoveWithCurrentMousePos():Void{
		var newLocation:Number = startLocation + getCurrentMovedDistance();
		sp.setDividerLocation(newLocation);
	}
	
	private function getCurrentMovedDistance():Number{
		var mouseP:Point = sp.getMousePosition();
		var delta:Number = 0;
		if(isVertical()){
			delta = mouseP.y - startDragPos.y;
		}else{
			delta = mouseP.x - startDragPos.x;
		}
		var newLocation:Number = startLocation + delta;
		newLocation = Math.max(
			getMinimumDividerLocation(), 
			Math.min(newLocation, getMaximumDividerLocation()));
		return newLocation - startLocation;
	}

	private function __div_rollover() : Void {
		CursorManager.hideSystemCursor();
		if(isVertical()){
			CursorManager.showCustomCursor(vSplitCursor, sp);
		}else{
			CursorManager.showCustomCursor(hSplitCursor, sp);
		}
	}

	private function __div_rollout() : Void {
		CursorManager.hideCustomCursor();
		CursorManager.showSystemCursor();
	}
    
    //-----------------------------------------------------------------------
    //                     Layout implementation
    //-----------------------------------------------------------------------
	public function addLayoutComponent(comp : Component, constraints : Object) : Void {
	}

	public function removeLayoutComponent(comp : Component) : Void {
	}

	public function preferredLayoutSize(target : Container) : Dimension {
		var insets:Insets = sp.getInsets();
    	var lc:Component = sp.getLeftComponent();
    	var rc:Component = sp.getRightComponent();
    	var lcSize:Dimension = (lc == null ? new Dimension() : lc.getPreferredSize());
    	var rcSize:Dimension = (rc == null ? new Dimension() : rc.getPreferredSize());
    	var size:Dimension;
    	if(isVertical()){
    		size = new Dimension(
    			Math.max(lcSize.width, rcSize.width), 
    			lcSize.height + rcSize.height + getDividerSize()
    		);
    	}else{
    		size = new Dimension(
    			lcSize.width + rcSize.width + getDividerSize(), 
    			Math.max(lcSize.height, rcSize.height)
    		);
    	}
    	return insets.getOutsideSize(size);
	}

	public function minimumLayoutSize(target : Container) : Dimension {
		return target.getInsets().getOutsideSize();
	}

	public function maximumLayoutSize(target : Container) : Dimension {
		return new Dimension(Number.MAX_VALUE, Number.MAX_VALUE);
	}
	
	public function layoutContainer(target : Container) : Void {
		var size:Dimension = sp.getSize();
		size = sp.getInsets().getInsideSize(size);
		var layouted:Boolean = false;
		if(!size.equals(lastContentSize)){
			//re weight the split
			var deltaSize:Number = 0;
			if(isVertical()){
				deltaSize = size.height - lastContentSize.height;
			}else{
				deltaSize = size.width - lastContentSize.width;
			}
			lastContentSize = size.clone();
			var locationDelta:Number = deltaSize*sp.getResizeWeight();
			layouted = (locationDelta != 0);
			var newLocation:Number = sp.getDividerLocation()+locationDelta;
			
			newLocation = Math.max(
				getMinimumDividerLocation(), 
				Math.min(newLocation, getMaximumDividerLocation()));
			
			sp.setDividerLocation(newLocation);
		}
		if(!layouted){
			layoutWithLocation(sp.getDividerLocation());
		}
	}

	public function getLayoutAlignmentX(target : Container) : Number {
		return 0;
	}

	public function getLayoutAlignmentY(target : Container) : Number {
		return 0;
	}

	public function invalidateLayout(target : Container) : Void {
	}
}