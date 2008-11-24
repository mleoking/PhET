/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.geom.Dimension;
import org.aswing.geom.Point;
import org.aswing.geom.Rectangle;
import org.aswing.IEventDispatcher;

/**
 * A viewportable object can scrolled by JScrollPane, JScrollBar to view 
 * its viewed content in a visible area.
 * 
 * @see JScrollPane
 * @see JViewport
 * @see JList
 * @see JTextArea
 * 
 * @author iiley
 */
interface org.aswing.Viewportable extends IEventDispatcher{
	/**
	 * Returns the unit value for the Vertical scrolling.
	 */
    public function getVerticalUnitIncrement():Number;
    
    /**
     * Return the block value for the Vertical scrolling.
     */
    public function getVerticalBlockIncrement():Number;
    
	/**
	 * Returns the unit value for the Horizontal scrolling.
	 */
    public function getHorizontalUnitIncrement():Number;
    
    /**
     * Return the block value for the Horizontal scrolling.
     */
    public function getHorizontalBlockIncrement():Number;    
    
	/**
	 * Sets the unit value for the Vertical scrolling.
	 */
    public function setVerticalUnitIncrement(increment:Number):Void;
    
    /**
     * Sets the block value for the Vertical scrolling.
     */
    public function setVerticalBlockIncrement(increment:Number):Void;
    
	/**
	 * Sets the unit value for the Horizontal scrolling.
	 */
    public function setHorizontalUnitIncrement(increment:Number):Void;
    
    /**
     * Sets the block value for the Horizontal scrolling.
     */
    public function setHorizontalBlockIncrement(increment:Number):Void;       
    
    /**
     * Before JScrollPane analyse the scroll properties(call getExtentSize and getViewSize), 
     * it will call this method to set the size of viewport will be to test.
     */
    public function setViewportTestSize(s:Dimension):Void;
    
    /**
     * Returns the size of the visible part of the view in view logic coordinates.
     *
     * @return a <code>Dimension</code> object giving the size of the view
     */
    public function getExtentSize():Dimension;
    
    /**
     * Returns the viewportable view's amount size if view all content in view logic coordinates.
     * Usually the view's preffered size.
     * @return the view's size.
     */
    public function getViewSize():Dimension;
    
    /**
     * Returns the view coordinates that appear in the upper left
     * hand corner of the viewport, or 0,0 if there's no view. in view logic coordinates.
     *
     * @return a <code>Point</code> object giving the upper left coordinates
     */
    public function getViewPosition():Point;
    
    /**
     * Sets the view coordinates that appear in the upper left
     * hand corner of the viewport. in view logic coordinates.
     *
     * @param p  a <code>Point</code> object giving the upper left coordinates
     */
    public function setViewPosition(p:Point):Void;
    
    /**
     * Scrolls the view so that <code>Rectangle</code>
     * within the view becomes visible. in view logic coordinates.
     * <p>
     * Note that this method will not scroll outside of the
     * valid viewport; for example, if <code>contentRect</code> is larger
     * than the viewport, scrolling will be confined to the viewport's
     * bounds.
     * @param contentRect the <code>Rectangle</code> to display
     */
    public function scrollRectToVisible(contentRect:Rectangle):Void;
    
    /**
     * Converts a size in screen pixel coordinates to view logic coordinates.
     * 
     * @param size  a <code>Dimension</code> object using screen pixel coordinates
     * @return a <code>Dimension</code> object converted to view logic coordinates
     */
   // public function toViewCoordinatesSize(size:Dimension):Dimension;

    /**
     * Converts a point in screen pixel coordinates to view coordinates.
     *
     * @param p  a <code>Point</code> object using screen pixel coordinates
     * @return a <code>Point</code> object converted to view coordinates
     */
    //public function toViewCoordinatesLocation(p:Point):Point;
    
    /**
     * Converts a size in view logic coordinates to screen pixel coordinates.
     * 
     * @param size  a <code>Dimension</code> object using view logic coordinates
     * @return a <code>Dimension</code> object converted to screen pixel coordinates
     */
    //public function toScreenCoordinatesSize(size:Dimension):Dimension;

    /**
     * Converts a point in view logic coordinates to screen pixel coordinates.
     *
     * @param p  a <code>Point</code> object using view logic coordinates
     * @return a <code>Point</code> object converted to screen pixel coordinates
     */
    //public function toScreenCoordinatesLocation(p:Point):Point;    
    
	/**
	 * Add a listener to listen the viewpoat state change event.
	 * <p>
	 * When the viewpoat's state changed, the state is all about:
	 * <ul>
	 * <li>viewPosition
	 * </ul>
	 *<br>
	 * onStateChanged(source:Viewportable)
	 * @see org.aswing.Component#ON_STATE_CHANGED
	 * @return the listener added.
	 * @see EventDispatcher#addEventListener()
	 */
	public function addChangeListener(func:Function, obj:Object):Object;
    
    /**
     * Return the component of the viewportable's pane which would added to displayed on the stage.
     * 
     * @return the component of the viewportable pane.
     */
    public function getViewportPane():Component;
}
