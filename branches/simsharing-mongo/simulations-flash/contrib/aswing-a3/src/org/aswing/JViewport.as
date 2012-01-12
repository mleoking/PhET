/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

import org.aswing.event.InteractiveEvent;
import org.aswing.geom.IntDimension;
import org.aswing.geom.IntPoint;
import org.aswing.geom.IntRectangle;
import org.aswing.plaf.basic.BasicViewportUI;

/**
 * Dispatched when the viewport's state changed. the state is all about:
 * <ul>
 * <li>view position</li>
 * <li>verticalUnitIncrement</li>
 * <li>verticalBlockIncrement</li>
 * <li>horizontalUnitIncrement</li>
 * <li>horizontalBlockIncrement</li>
 * </ul>
 * </p>
 * 
 * @eventType org.aswing.event.InteractiveEvent.STATE_CHANGED
 */
[Event(name="stateChanged", type="org.aswing.event.InteractiveEvent")]

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
public class JViewport extends Container implements Viewportable{
 	
 	/**
 	 * The default unit/block increment, it means auto count a value.
 	 */
 	public static const AUTO_INCREMENT:int = int.MIN_VALUE;
 	
	/**
	 * A fast access to AsWingConstants Constant
	 * @see org.aswing.AsWingConstants
	 */
	public static var CENTER:int  = AsWingConstants.CENTER;
	/**
	 * A fast access to AsWingConstants Constant
	 * @see org.aswing.AsWingConstants
	 */
	public static var TOP:int     = AsWingConstants.TOP;
	/**
	 * A fast access to AsWingConstants Constant
	 * @see org.aswing.AsWingConstants
	 */
    public static var LEFT:int    = AsWingConstants.LEFT;
	/**
	 * A fast access to AsWingConstants Constant
	 * @see org.aswing.AsWingConstants
	 */
    public static var BOTTOM:int  = AsWingConstants.BOTTOM;
 	/**
	 * A fast access to AsWingConstants Constant
	 * @see org.aswing.AsWingConstants
	 */
    public static var RIGHT:int   = AsWingConstants.RIGHT;
 	
	private var verticalUnitIncrement:int;
	private var verticalBlockIncrement:int;
	private var horizontalUnitIncrement:int;
	private var horizontalBlockIncrement:int;
	
	private var tracksHeight:Boolean;
	private var tracksWidth:Boolean;
	
    private var verticalAlignment:int;
    private var horizontalAlignment:int;
	
	private var view:Component;
	
	/**
	 * Create a viewport with view and size tracks properties.
	 * @see #setView()
	 * @see #setTracksWidth()
	 * @see #setTracksHeight()
	 */
	public function JViewport(view:Component=null, tracksWidth:Boolean=false, tracksHeight:Boolean=false){
		super();
		setName("JViewport");
		this.tracksWidth = tracksWidth;
		this.tracksHeight = tracksHeight;
		verticalUnitIncrement = AUTO_INCREMENT;
		verticalBlockIncrement = AUTO_INCREMENT;
		horizontalUnitIncrement = AUTO_INCREMENT;
		horizontalBlockIncrement = AUTO_INCREMENT;
		
    	verticalAlignment = CENTER;
    	horizontalAlignment = CENTER;
    	
		if(view != null) setView(view);
		setLayout(new ViewportLayout());
		updateUI();
	}
    
	override public function updateUI():void{
    	setUI(UIManager.getUI(this));
    }
	
    override public function getDefaultBasicUIClass():Class{
    	return org.aswing.plaf.basic.BasicViewportUI;
    }
	
	override public function getUIClassID():String{
		return "ViewportUI";
	}	

	/**
	 * @throws Error if the layout is not a ViewportLayout
	 */
	override public function setLayout(layout:LayoutManager):void{
		if(layout is ViewportLayout){
			super.setLayout(layout);
		}else{
			throw new Error("Only on set ViewportLayout to JViewport");
		}
	}
	
	/**
	 * Sets whether the view tracks viewport width. Default is false<br>
	 * If true, the view will always be set to the same size as the viewport.<br>
	 * If false, the view will be set to it's preffered size.
	 * @param b tracks width
	 */
	public function setTracksWidth(b:Boolean):void{
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
		return tracksWidth;
	}
	
	/**
	 * Sets whether the view tracks viewport height. Default is false<br>
	 * If true, the view will always be set to the same height as the viewport.<br>
	 * If false, the view will be set to it's preffered height.
	 * @param b tracks height
	 */
	public function setTracksHeight(b:Boolean):void{
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
		return tracksHeight;
	}

    /**
     * Returns the vertical alignment of the view if the view is lower than extent height.
     *
     * @return the <code>verticalAlignment</code> property, one of the
     *		following values: 
     * <ul>
     * <li>AsWingConstants.CENTER (the default)
     * <li>AsWingConstants.TOP
     * <li>AsWingConstants.BOTTOM
     * </ul>
     */
    public function getVerticalAlignment():int {
        return verticalAlignment;
    }
    
    /**
     * Sets the vertical alignment of the view if the view is lower than extent height.
     * @param alignment  one of the following values:
     * <ul>
     * <li>AsWingConstants.CENTER (the default)
     * <li>AsWingConstants.TOP
     * <li>AsWingConstants.BOTTOM
     * </ul>
     */
    public function setVerticalAlignment(alignment:int):void {
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
     * <li>AsWingConstants.RIGHT (the default)
     * <li>AsWingConstants.LEFT
     * <li>AsWingConstants.CENTER
     * </ul>
     */
    public function getHorizontalAlignment():int{
        return horizontalAlignment;
    }
    
    /**
     * Sets the horizontal alignment of the view if the view is narrower than extent width.
     * @param alignment  one of the following values:
     * <ul>
     * <li>AsWingConstants.RIGHT (the default)
     * <li>AsWingConstants.LEFT
     * <li>AsWingConstants.CENTER
     * </ul>
     */
    public function setHorizontalAlignment(alignment:int):void {
        if (alignment == horizontalAlignment){
        	return;
        }else{
        	horizontalAlignment = alignment;
        	setViewPosition(getViewPosition());//make it to be restricted
        }
    }
		
	/**
	 * Sets the view component.
	 * 
	 * <p>The view is the visible content of the JViewPort.</p>
	 * 
	 * <p>JViewport use to manage the scroll view of a component.
	 * the component will be set size to its preferred size, then scroll in the viewport.<br>
	 * </p>
	 * <p>If the component's isTracksViewportWidth method is defined and return true,
	 * when the viewport's show size is larger than the component's,
	 * the component will be widen to the show size, otherwise, not widen.
	 * Same as isTracksViewportHeight method.
	 * </p>
	 */
	public function setView(view:Component):void{
		if(this.view != view){
			this.view = view;
			removeAll();
			
			if(view != null){
				insertImp(-1, view);
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
    public function setVerticalUnitIncrement(increment:int):void{
    	if(verticalUnitIncrement != increment){
    		verticalUnitIncrement = increment;
			fireStateChanged();
    	}
    }
    
    /**
     * Sets the block value for the Vertical scrolling.
     */
    public function setVerticalBlockIncrement(increment:int):void{
    	if(verticalBlockIncrement != increment){
    		verticalBlockIncrement = increment;
			fireStateChanged();
    	}
    }
    
	/**
	 * Sets the unit value for the Horizontal scrolling.
	 */
    public function setHorizontalUnitIncrement(increment:int):void{
    	if(horizontalUnitIncrement != increment){
    		horizontalUnitIncrement = increment;
			fireStateChanged();
    	}
    }
    
    /**
     * Sets the block value for the Horizontal scrolling.
     */
    public function setHorizontalBlockIncrement(increment:int):void{
    	if(horizontalBlockIncrement != increment){
    		horizontalBlockIncrement = increment;
			fireStateChanged();
    	}
    }		
			
	
	/**
	 * In fact just call setView(com) in this method
	 * @see #setView()
	 */
	override public function append(com:Component, constraints:Object=null):void{
		setView(com);
	}
	
	/**
	 * In fact just call setView(com) in this method
	 * @see #setView()
	 */	
	override public function insert(i:int, com:Component, constraints:Object=null):void{
		setView(com);
	}
	
	//--------------------implementatcion of Viewportable---------------

	/**
	 * Returns the unit value for the Vertical scrolling.
	 */
    public function getVerticalUnitIncrement():int{
    	if(verticalUnitIncrement != AUTO_INCREMENT){
    		return verticalUnitIncrement;
    	}else{
    		return Math.max(getExtentSize().height/40, 1);
    	}
    }
    
    /**
     * Return the block value for the Vertical scrolling.
     */
    public function getVerticalBlockIncrement():int{
    	if(verticalBlockIncrement != AUTO_INCREMENT){
    		return verticalBlockIncrement;
    	}else{
    		return getExtentSize().height-1;
    	}
    }
    
	/**
	 * Returns the unit value for the Horizontal scrolling.
	 */
    public function getHorizontalUnitIncrement():int{
    	if(horizontalUnitIncrement != AUTO_INCREMENT){
    		return horizontalUnitIncrement;
    	}else{
    		return Math.max(getExtentSize().width/40, 1);
    	}
    }
    
    /**
     * Return the block value for the Horizontal scrolling.
     */
    public function getHorizontalBlockIncrement():int{
    	if(horizontalBlockIncrement != AUTO_INCREMENT){
    		return horizontalBlockIncrement;
    	}else{
    		return getExtentSize().width - 1;
    	}
    }
    
    public function setViewportTestSize(s:IntDimension):void{
    	setSize(s);
    }

	public function getExtentSize() : IntDimension {
		return getInsets().getInsideSize(getSize());
	}
	
	/**
     * Usually the view's preffered size.
     * @return the view's size, (0, 0) if view is null.
	 */
	public function getViewSize() : IntDimension {
		if(view == null){
			return new IntDimension();
		}else{
			if(isTracksWidth() && isTracksHeight()){
				return getExtentSize();
			}else{
				var viewSize:IntDimension = view.getPreferredSize();
				var extentSize:IntDimension = getExtentSize();
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
	 * Returns the view's position, if there is not any view, return (0,0).
	 * @return the view's position, (0,0) if view is null.
	 */
	public function getViewPosition() : IntPoint {
		if(view != null){
			var p:IntPoint = view.getLocation();
			var ir:IntRectangle = getInsets().getInsideBounds(getSize().getBounds());
			p.x = ir.x - p.x;
			p.y = ir.y - p.y;
			return p;
		}else{
			return new IntPoint(0, 0);
		}
	}

	public function setViewPosition(p : IntPoint, programmatic:Boolean=true) : void {
		restrictionViewPos(p);
		if(!p.equals(getViewPosition())){
			var ir:IntRectangle = getInsets().getInsideBounds(getSize().getBounds());
			if(view){
				view.setLocationXY(ir.x-p.x, ir.y-p.y);
			}
			fireStateChanged(programmatic);
		}
	}

	public function scrollRectToVisible(contentRect:IntRectangle, programmatic:Boolean=true):void{
		setViewPosition(new IntPoint(contentRect.x, contentRect.y), programmatic);
	}
	
	/**
	 * Make a scroll or not to ensure specified rect will be visible.
	 * @param contentRect the rect to be ensure visible
	 * @programmatic whether or not a programmatic call
	 */
	public function ensureRectVisible(contentRect:IntRectangle, programmatic:Boolean=true):void{
		contentRect = contentRect.clone();
		var vp:IntPoint = getViewPosition();
		var es:IntDimension = getExtentSize();
		var vs:IntDimension = getViewSize();
		var range:IntRectangle = new IntRectangle(vp.x, vp.y, es.width, es.height);
		if(contentRect.x < 0){
			contentRect.width += contentRect.x;
			contentRect.x = 0;
		}
		if(contentRect.y < 0){
			contentRect.height += contentRect.y;
			contentRect.y = 0;
		}
		if(contentRect.x + contentRect.width > vs.width){
			contentRect.width = vs.width - contentRect.x;
		}
		if(contentRect.y + contentRect.height > vs.height){
			contentRect.height = vs.height - contentRect.y;
		}
		var newVP:IntPoint = vp.clone();
		if(contentRect.x + contentRect.width > range.x + range.width){
			newVP.x = contentRect.x + contentRect.width - es.width;
		}
		if(contentRect.y + contentRect.height > range.y + range.height){
			newVP.y = contentRect.y + contentRect.height - es.height;
		}
		if(contentRect.x < range.x){
			newVP.x = contentRect.x;
		}
		if(contentRect.y < range.y){
			newVP.y = contentRect.y;
		}
		setViewPosition(newVP, programmatic);
	}
	
	/**
	 * Scrolls view vertical with delta pixels.
	 */
	public function scrollVertical(delta:int):void{
		setViewPosition(getViewPosition().move(0, delta));
	}
	
	/**
	 * Scrolls view horizontal with delta pixels.
	 */
	public function scrollHorizontal(delta:int):void{
		setViewPosition(getViewPosition().move(delta, 0));
	}
	
	/**
	 * Scrolls to view bottom left content. 
	 * This will make the scrollbars of <code>JScrollPane</code> scrolled automatically, 
	 * if it is located in a <code>JScrollPane</code>.
	 */
	public function scrollToBottomLeft():void{
		setViewPosition(new IntPoint(0, int.MAX_VALUE));
	}
	/**
	 * Scrolls to view bottom right content. 
	 * This will make the scrollbars of <code>JScrollPane</code> scrolled automatically, 
	 * if it is located in a <code>JScrollPane</code>.
	 */	
	public function scrollToBottomRight():void{
		setViewPosition(new IntPoint(int.MAX_VALUE, int.MAX_VALUE));
	}
	/**
	 * Scrolls to view top left content. 
	 * This will make the scrollbars of <code>JScrollPane</code> scrolled automatically, 
	 * if it is located in a <code>JScrollPane</code>.
	 */	
	public function scrollToTopLeft():void{
		setViewPosition(new IntPoint(0, 0));
	}
	/**
	 * Scrolls to view to right content. 
	 * This will make the scrollbars of <code>JScrollPane</code> scrolled automatically, 
	 * if it is located in a <code>JScrollPane</code>.
	 */	
	public function scrollToTopRight():void{
		setViewPosition(new IntPoint(int.MAX_VALUE, 0));
	}
	
	/**
	 * Restrict the view pos in valid range based on the align.
	 */	
	protected function restrictionViewPos(p:IntPoint):IntPoint{
		var showSize:IntDimension = getExtentSize();
		var viewSize:IntDimension = getViewSize();
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
	 * Add a listener to listen the viewpoat state change event.
	 * <p>
	 * When the viewpoat's state changed, the state is all about:
	 * <ul>
	 * <li>viewPosition</li>
	 * <li>verticalUnitIncrement</li>
	 * <li>verticalBlockIncrement</li>
	 * <li>horizontalUnitIncrement</li>
	 * <li>horizontalBlockIncrement</li>
	 * </ul>
	 * @param listener the listener
	 * @param priority the priority
	 * @param useWeakReference Determines whether the reference to the listener is strong or weak.
	 * @see org.aswing.event.InteractiveEvent#STATE_CHANGED
	 */
	public function addStateListener(listener:Function, priority:int=0, useWeakReference:Boolean=false):void{
		addEventListener(InteractiveEvent.STATE_CHANGED, listener, false, priority);
	}	
	
	/**
	 * Removes a state listener.
	 * @param listener the listener to be removed.
	 * @see org.aswing.event.InteractiveEvent#STATE_CHANGED
	 */	
	public function removeStateListener(listener:Function):void{
		removeEventListener(InteractiveEvent.STATE_CHANGED, listener);
	}
	
	protected function fireStateChanged(programmatic:Boolean=true):void{
		dispatchEvent(new InteractiveEvent(InteractiveEvent.STATE_CHANGED, programmatic));
	}
	
	public function getViewportPane() : Component {
		return this;
	}
}
}