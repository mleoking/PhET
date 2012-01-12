/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.ASWingConstants;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.geom.Dimension;
import org.aswing.geom.Point;
import org.aswing.geom.Rectangle;
import org.aswing.LayoutManager;
import org.aswing.plaf.ViewportUI;
import org.aswing.UIManager;
import org.aswing.Viewportable;
import org.aswing.overflow.ViewportLayout;


/**
 * JViewport is an basic viewport to view normal components. Generally JViewport works 
 * with JScrollPane together, for example:<br>
 * <pre>
 *     var scrollPane:JScrollPane = new JScrollPane();
 *     var viewport:JViewport = new JViewport(yourScrollContentComponent, true, false);
 *     scrollPane.setViewport(viewport);
 * </pre>
 * Then you'll get a scrollpane with scroll content and only vertical scrollbar. And 
 * the scroll content will always tracks the scroll pane width.
 * @author iiley
 */
class org.aswing.overflow.JViewport extends Container implements Viewportable {
				
	/**
	 * When the viewport's state changed.
	 * View position changed, view changed, all related to scroll things changed.
	 *<br>
	 * onStateChanged(source:JViewport)
	 */	
	public static var ON_STATE_CHANGED:String = "onStateChanged";//Component.ON_STATE_CHANGED; 
		
	/**
	 * A fast access to ASWingConstants Constant
	 * @see org.aswing.ASWingConstants
	 */
	public static var CENTER:Number  = ASWingConstants.CENTER;
	/**
	 * A fast access to ASWingConstants Constant
	 * @see org.aswing.ASWingConstants
	 */
	public static var TOP:Number     = ASWingConstants.TOP;
	/**
	 * A fast access to ASWingConstants Constant
	 * @see org.aswing.ASWingConstants
	 */
    public static var LEFT:Number    = ASWingConstants.LEFT;
	/**
	 * A fast access to ASWingConstants Constant
	 * @see org.aswing.ASWingConstants
	 */
    public static var BOTTOM:Number  = ASWingConstants.BOTTOM;
 	/**
	 * A fast access to ASWingConstants Constant
	 * @see org.aswing.ASWingConstants
	 */
    public static var RIGHT:Number   = ASWingConstants.RIGHT;
 	
	private var verticalUnitIncrement:Number;
	private var verticalBlockIncrement:Number;
	private var horizontalUnitIncrement:Number;
	private var horizontalBlockIncrement:Number;
	
    private var verticalAlignment:Number;
    private var horizontalAlignment:Number;
    
	private var tracksHeight:Boolean;
	private var tracksWidth:Boolean;
	
	private var view:Component;
	
	/**
	 * JViewport(view:Component, tracksWidth:Boolean, tracksHeight:Boolean)<br>
	 * JViewport(view:Component) Default tracks width and height to false<br>
	 * JViewport() no view and default tracks width and height to false
	 * <p>
	 * Create a viewport with view and size tracks properties.
	 * @see #setView()
	 * @see #setTracksWidth()
	 * @see #setTracksHeight()
	 */
	public function JViewport(view:Component, tracksWidth:Boolean, tracksHeight:Boolean){
		super();
		setName("JViewport");
		this.tracksWidth = (tracksWidth == undefined ? false : tracksWidth);
		this.tracksHeight = (tracksHeight == undefined ? false : tracksHeight);
		
    	verticalAlignment = CENTER;
    	horizontalAlignment = CENTER;
		
		if(view != undefined) setView(view);
		setLayout(new ViewportLayout());
		updateUI();
	}
    
    public function updateUI():Void{
    	setUI(ViewportUI(UIManager.getUI(this)));
    }
    
    public function setUI(newUI:ViewportUI):Void{
    	super.setUI(newUI);
    }
	
	public function getUIClassID():String{
		return "ViewportUI";
	}	
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.basic.BasicViewportUI;
    }

	/**
	 * @throws Error if the layout is not a ViewportLayout
	 */
	public function setLayout(layout:LayoutManager):Void{
		if(layout instanceof ViewportLayout){
			super.setLayout(layout);
		}else{
			trace(this + " Only on set ViewportLayout to JViewport");
			throw new Error(this + " Only on set ViewportLayout to JViewport");
		}
	}
	
	/**
	 * Sets whether the view tracks viewport width. Default is false<br>
	 * If true, the view will always be set to the same size as the viewport.<br>
	 * If false, the view will be set to it's preffered size.
	 * @param b tracks width
	 */
	public function setTracksWidth(b:Boolean):Void{
		if(b != tracksWidth){
			tracksWidth = b;
			revalidate();
		}
	}
	
	/**
	 * Returns whether the view tracks viewport width. Default is false<br>
	 * If true, the view will always be set to the same width as the viewport.<br>
	 * If false, the view will be set to it's preffered width.
	 * @return whether tracks width
	 */
	public function isTracksWidth():Boolean{
		//TODO remove this Backward compatible 
		var o:Object = view;
		if(o.isTracksViewportWidth != undefined){
			return o.isTracksViewportWidth();
		}
		
		return tracksWidth;
	}
	
	/**
	 * Sets whether the view tracks viewport height. Default is false<br>
	 * If true, the view will always be set to the same height as the viewport.<br>
	 * If false, the view will be set to it's preffered height.
	 * @param b tracks height
	 */
	public function setTracksHeight(b:Boolean):Void{
		if(tracksHeight != b){
			tracksHeight = b;
			revalidate();
		}
	}
	
	/**
	 * Returns whether the view tracks viewport height. Default is false<br>
	 * If true, the view will always be set to the same height as the viewport.<br>
	 * If false, the view will be set to it's preffered height.
	 * @return whether tracks height
	 */
	public function isTracksHeight():Boolean{
		//TODO remove this Backward compatible 
		var o:Object = view;
		if(o.isTracksViewportHeight != undefined){
			return o.isTracksViewportHeight();
		}
		
		return tracksHeight;
	}

    /**
     * Returns the vertical alignment of the view if the view is lower than extent height.
     *
     * @return the <code>verticalAlignment</code> property, one of the
     *		following values: 
     * <ul>
     * <li>ASWingConstants.CENTER (the default)
     * <li>ASWingConstants.TOP
     * <li>ASWingConstants.BOTTOM
     * </ul>
     */
    public function getVerticalAlignment():Number {
        return verticalAlignment;
    }
    
    /**
     * Sets the vertical alignment of the view if the view is lower than extent height.
     * @param alignment  one of the following values:
     * <ul>
     * <li>ASWingConstants.CENTER (the default)
     * <li>ASWingConstants.TOP
     * <li>ASWingConstants.BOTTOM
     * </ul>
     */
    public function setVerticalAlignment(alignment:Number):Void {
        if (alignment == verticalAlignment){
        	return;
        }else{
        	verticalAlignment = alignment;
        	setViewPosition(getViewPosition());//make it to be restricted
        }
    }
    
    /**
     * Returns the horizontal alignment of the view if the view is narrower than extent width.
     * @return the <code>horizontalAlignment</code> property,
     *		one of the following values:
     * <ul>
     * <li>ASWingConstants.RIGHT (the default)
     * <li>ASWingConstants.LEFT
     * <li>ASWingConstants.CENTER
     * </ul>
     */
    public function getHorizontalAlignment():Number{
        return horizontalAlignment;
    }
    
    /**
     * Sets the horizontal alignment of the view if the view is narrower than extent width.
     * @param alignment  one of the following values:
     * <ul>
     * <li>ASWingConstants.RIGHT (the default)
     * <li>ASWingConstants.LEFT
     * <li>ASWingConstants.CENTER
     * </ul>
     */
    public function setHorizontalAlignment(alignment:Number):Void {
        if (alignment == horizontalAlignment){
        	return;
        }else{
        	horizontalAlignment = alignment;
        	setViewPosition(getViewPosition());//make it to be restricted
        }
    }
	
	/**
	 * Sets the view component.<br>
	 * 
	 * <p>The view is the visible content of the JViewPort.
	 * 
	 * <p>JViewport use to manage the scroll view of a component.
	 * the component will be set size to its preferred size, then scroll in the viewport.<br>
	 * 
	 * <p>If the component's isTracksViewportWidth method is defined and return true,
	 * when the viewport's show size is larger than the component's,
	 * the component will be widen to the show size, otherwise, not widen.
	 * Same as isTracksViewportHeight method.
	 */
	public function setView(view:Component):Void{
		if(this.view != view){
			this.view = view;
			removeAll();
			
			if(view != null){
				super.insert(-1, view);
			}
			fireStateChanged();
		}
	}
	
	public function getView():Component{
		return view;
	}
		
	/**
	 * Sets the unit value for the Vertical scrolling.
	 */
    public function setVerticalUnitIncrement(increment:Number):Void{
    	if(verticalUnitIncrement != increment){
    		verticalUnitIncrement = increment;
			fireStateChanged();
    	}
    }
    
    /**
     * Sets the block value for the Vertical scrolling.
     */
    public function setVerticalBlockIncrement(increment:Number):Void{
    	if(verticalBlockIncrement != increment){
    		verticalBlockIncrement = increment;
			fireStateChanged();
    	}
    }
    
	/**
	 * Sets the unit value for the Horizontal scrolling.
	 */
    public function setHorizontalUnitIncrement(increment:Number):Void{
    	if(horizontalUnitIncrement != increment){
    		horizontalUnitIncrement = increment;
			fireStateChanged();
    	}
    }
    
    /**
     * Sets the block value for the Horizontal scrolling.
     */
    public function setHorizontalBlockIncrement(increment:Number):Void{
    	if(horizontalBlockIncrement != increment){
    		horizontalBlockIncrement = increment;
			fireStateChanged();
    	}
    }		
			
	
	/**
	 * In fact just call setView(com) in this method
	 * @see #setView()
	 */
	public function append(com:Component, constraints:Object):Void{
		setView(com);
	}
	
	/**
	 * In fact just call setView(com) in this method
	 * @see #setView()
	 */	
	public function insert(i:Number, com:Component, constraints:Object):Void{
		setView(com);
	}	
	
	//--------------------implementatcion of Viewportable---------------

	/**
	 * Returns the unit value for the Vertical scrolling.
	 */
    public function getVerticalUnitIncrement():Number{
    	if(verticalUnitIncrement != undefined){
    		return verticalUnitIncrement;
    	}else{
    		return 1;
    	}
    }
    
    /**
     * Return the block value for the Vertical scrolling.
     */
    public function getVerticalBlockIncrement():Number{
    	if(verticalBlockIncrement != undefined){
    		return verticalBlockIncrement;
    	}else{
    		return getExtentSize().height-1;
    	}
    }
    
	/**
	 * Returns the unit value for the Horizontal scrolling.
	 */
    public function getHorizontalUnitIncrement():Number{
    	if(horizontalUnitIncrement != undefined){
    		return horizontalUnitIncrement;
    	}else{
    		return 1;
    	}
    }
    
    /**
     * Return the block value for the Horizontal scrolling.
     */
    public function getHorizontalBlockIncrement():Number{
    	if(horizontalBlockIncrement != undefined){
    		return horizontalBlockIncrement;
    	}else{
    		return getExtentSize().width - 1;
    	}
    }
    
    public function setViewportTestSize(s:Dimension):Void{
    	setSize(s);
    }

	public function getExtentSize() : Dimension {
		return getInsets().getInsideSize(getSize());
	}
	
	/**
     * Usually the view's preffered size.
     * @return the view's size, (0, 0) if view is null.
	 */
	public function getViewSize() : Dimension {
		if(view == null){
			return new Dimension();
		}else{
			if(isTracksWidth() && isTracksHeight()){
				return getExtentSize();
			}else{
				var viewSize:Dimension = view.getPreferredSize();
				var extentSize:Dimension = getExtentSize();
				if(isTracksWidth()){
					viewSize.width = extentSize.width;
				}else if(isTracksHeight()){
					viewSize.height = extentSize.height;
				}
				return viewSize;
			}
		}
	}
	
	/**
	 * Returns the view's position, if there is not any view, return null.
	 * @return the view's position, null if view is null.
	 */
	public function getViewPosition() : Point {
		if(view != null){
			var p:Point = view.getLocation();
			var ir:Rectangle = getInsets().getInsideBounds(getSize().getBounds());
			p.x = ir.x - p.x;
			p.y = ir.y - p.y;
			return p;
		}else{
			return null;
		}
	}

	public function setViewPosition(p : Point) : Void {
		restrictionViewPos(p);
		if(!p.equals(getViewPosition())){
			var ir:Rectangle = getInsets().getInsideBounds(getSize().getBounds());
			view.setLocationImmediately(ir.x-p.x, ir.y-p.y);
			fireStateChanged();
		}
	}

	public function scrollRectToVisible(contentRect : Rectangle) : Void {
		setViewPosition(new Point(contentRect.x, contentRect.y));
	}
	
	/**
	 * Scrolls view vertical with delta pixels.
	 */
	public function scrollVertical(delta:Number):Void{
		setViewPosition(getViewPosition().move(0, delta));
	}
	
	/**
	 * Scrolls view horizontal with delta pixels.
	 */
	public function scrollHorizontal(delta:Number):Void{
		setViewPosition(getViewPosition().move(delta, 0));
	}
	
	/**
	 * Scrolls to view bottom left content. 
	 * This will make the scrollbars of <code>JScrollPane</code> scrolled automatically, 
	 * if it is located in a <code>JScrollPane</code>.
	 */
	public function scrollToBottomLeft():Void{
		setViewPosition(new Point(0, Number.MAX_VALUE));
	}
	/**
	 * Scrolls to view bottom right content. 
	 * This will make the scrollbars of <code>JScrollPane</code> scrolled automatically, 
	 * if it is located in a <code>JScrollPane</code>.
	 */	
	public function scrollToBottomRight():Void{
		setViewPosition(new Point(Number.MAX_VALUE, Number.MAX_VALUE));
	}
	/**
	 * Scrolls to view top left content. 
	 * This will make the scrollbars of <code>JScrollPane</code> scrolled automatically, 
	 * if it is located in a <code>JScrollPane</code>.
	 */	
	public function scrollToTopLeft():Void{
		setViewPosition(new Point(0, 0));
	}
	/**
	 * Scrolls to view to right content. 
	 * This will make the scrollbars of <code>JScrollPane</code> scrolled automatically, 
	 * if it is located in a <code>JScrollPane</code>.
	 */	
	public function scrollToTopRight():Void{
		setViewPosition(new Point(Number.MAX_VALUE, 0));
	}
	
	public function restrictionViewPos(p:Point):Point{
		var showSize:Dimension = getExtentSize();
		var viewSize:Dimension = getViewSize();
		if(showSize.width < viewSize.width){
			p.x = Math.max(0, Math.min(viewSize.width-showSize.width, p.x));
		}else if(showSize.width > viewSize.width){
			if(horizontalAlignment == CENTER){
				p.x = -(showSize.width - viewSize.width)/2;
			}else if(horizontalAlignment == RIGHT){
				p.x = -(showSize.width - viewSize.width);
			}else{
				p.x = 0;
			}
		}else{//equals
			p.x = 0;
		}
		
		if(showSize.height < viewSize.height){
			p.y = Math.max(0, Math.min(viewSize.height-showSize.height, p.y));
		}else if(showSize.height > viewSize.height){
			if(verticalAlignment == CENTER){
				p.y = -(showSize.height - viewSize.height)/2;
			}else if(verticalAlignment == BOTTOM){
				p.y = -(showSize.height - viewSize.height);
			}else{
				p.y = 0;
			}
		}else{//equals
			p.y = 0;
		}
		return p;
	}
	
    /**
     * Converts a size in screen pixel coordinates to view ligic coordinates.
     * Subclasses of viewport that support "logical coordinates" will override this method. 
     * 
     * @param size  a <code>Dimension</code> object using screen pixel coordinates
     * @return a <code>Dimension</code> object converted to view logic coordinates
     */
	public function toViewCoordinatesSize(size : Dimension) : Dimension {
		return new Dimension(size.width, size.height);
	}
	
    /**
     * Converts a point in screen pixel coordinates to view coordinates.
     * Subclasses of viewport that support "logical coordinates" will override this method. 
     *
     * @param p  a <code>Point</code> object using screen pixel coordinates
     * @return a <code>Point</code> object converted to view coordinates
     */
	public function toViewCoordinatesLocation(p : Point) : Point {
		return new Point(p.x, p.y);
	}
	
    /**
     * Converts a size in view logic coordinates to screen pixel coordinates.
     * Subclasses of viewport that support "logical coordinates" will override this method. 
     * 
     * @param size  a <code>Dimension</code> object using view logic coordinates
     * @return a <code>Dimension</code> object converted to screen pixel coordinates
     */
    public function toScreenCoordinatesSize(size:Dimension):Dimension{
    	return new Dimension(size.width, size.height);
    }

    /**
     * Converts a point in view logic coordinates to screen pixel coordinates.
     * Subclasses of viewport that support "logical coordinates" will override this method. 
     * 
     * @param p  a <code>Point</code> object using view logic coordinates
     * @return a <code>Point</code> object converted to screen pixel coordinates
     */
    public function toScreenCoordinatesLocation(p:Point):Point{
    	return new Point(p.x, p.y);
    }
    	
	public function addChangeListener(func:Function, obj:Object):Object{
		return addEventListener(Component.ON_STATE_CHANGED, func, obj);
	}
	
	public function getViewportPane() : Component {
		return this;
	}

}
