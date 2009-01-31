/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ASWingConstants;
import org.aswing.BoundedRangeModel;
import org.aswing.Container;
import org.aswing.DefaultBoundedRangeModel;
import org.aswing.Event;
import org.aswing.plaf.ScrollBarUI;
import org.aswing.UIManager;
 
/**
 * An implementation of a scrollbar. The user positions the knob in the scrollbar to determine the contents of 
 * the viewing area. The program typically adjusts the display so that the end of the scrollbar represents the 
 * end of the displayable contents, or 100% of the contents. The start of the scrollbar is the beginning of the 
 * displayable contents, or 0%. The position of the knob within those bounds then translates to the corresponding 
 * percentage of the displayable contents. 
 * <p>
 * Typically, as the position of the knob in the scrollbar changes a corresponding change is 
 * made to the position of the JViewport on the underlying view, changing the contents of the 
 * JViewport.
 * </p>
 * @author iiley
 */
class org.aswing.JScrollBar extends Container{
    /** 
     * Horizontal orientation.
     */
    public static var HORIZONTAL:Number = ASWingConstants.HORIZONTAL;
    /** 
     * Vertical orientation.
     */
    public static var VERTICAL:Number   = ASWingConstants.VERTICAL;
	       

	/**
	 * When the scrollbar's Adjustment Value Changed.
	 *<br>
	 * onAdjustmentValueChanged(source:JScrollBar)
	 */	
	public static var ON_ADJUSTMENT_VALUE_CHANGED:String = "onAdjustmentValueChanged"; 		       
        
	private var modelListener:Object;
	
	private var model:BoundedRangeModel;
	private var orientation:Number;
	private var unitIncrement:Number;
	private var blockIncrement:Number;

	/**
	 * JScrollBar(orientation:Number, value:Number, extent:Number, min:Number, max:Number)<br>
	 * JScrollBar(orientation:Number) default to value=0, extent=10, min=0, max=100<br>
	 * <p>
	 * Creates a scrollbar with the specified orientation, value, extent, minimum, and maximum. 
	 * The "extent" is the size of the viewable area. It is also known as the "visible amount". 
	 * <p>
	 * Note: Use setBlockIncrement to set the block increment to a size slightly smaller than 
	 * the view's extent. That way, when the user jumps the knob to an adjacent position, one 
	 * or two lines of the original contents remain in view. 
	 * 
	 * @param orientation the scrollbar's orientation to either VERTICAL or HORIZONTAL. 
	 * @param value
	 * @param extent
	 * @param min
	 * @param max
	 */
	public function JScrollBar(orientation:Number, value:Number, extent:Number, min:Number, max:Number){
		super();
		if(value == undefined) value = 0;
		if(extent == undefined) extent = 10;
		if(min == undefined) min = 0;
		if(max == undefined) max = 100;
		if(orientation == undefined) orientation = VERTICAL;
		unitIncrement = 1;
		blockIncrement = (extent == 0 ? 10 : extent);
		setOrientation(orientation);
		model = new DefaultBoundedRangeModel(value, extent, min, max);
		addListenerToModel();
		updateUI();
	}

	public function setUI(ui:ScrollBarUI):Void{
		super.setUI(ui);
	}
	
	public function getUI():ScrollBarUI{
		return ScrollBarUI(ui);
	}
	
	public function updateUI():Void{
		setUI(ScrollBarUI(UIManager.getUI(this)));
	}
	
	public function getUIClassID():String{
		return "ScrollBarUI";
	}
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.asw.ASWingScrollBarUI;
    }
	
	/**
	 * @return the orientation.
	 */
	public function getOrientation():Number{
		return orientation;
	}
	
	
	public function setOrientation(orientation:Number):Void{
		var oldValue:Number = this.orientation;
		this.orientation = orientation;
		if (orientation != oldValue){
			revalidate();
		}
	}
	
	/**
	 * Returns data model that handles the scrollbar's four fundamental properties: minimum, maximum, value, extent.
	 * @return the data model
	 */
	public function getModel():BoundedRangeModel{
		return model;
	}
	
	/**
	 * Sets the model that handles the scrollbar's four fundamental properties: minimum, maximum, value, extent. 
	 * @param the data model
	 */
	public function setModel(newModel:BoundedRangeModel):Void{
		var oldModel:BoundedRangeModel = model;
		if (model != null){
			model.removeEventListener(modelListener);
		}
		model = newModel;
		if (model != null){
			addListenerToModel();
		}
	}
	
	private function addListenerToModel():Void{
		modelListener = model.addChangeListener(__onModelStateChanged, this);		
	}
	
	private function __onModelStateChanged(event:Event):Void{
		fireAdjustmentValueChanged();
	}
	
	/**
	 * Sets the unit increment
	 * @param unitIncrement the unit increment
	 * @see #getUnitIncrement()
	 */
	public function setUnitIncrement(unitIncrement:Number):Void{
		this.unitIncrement = unitIncrement;
	}
	
	/**
	 * Returns the amount to change the scrollbar's value by, given a unit up/down request. 
	 * A ScrollBarUI implementation typically calls this method when the user clicks on a 
	 * scrollbar up/down arrow and uses the result to update the scrollbar's value. 
	 * Subclasses my override this method to compute a value, e.g. the change required 
	 * to scroll up or down one (variable height) line text or one row in a table.
	 * <p>
	 * The JScrollPane component creates scrollbars (by default) that then
	 * set the unit increment by the viewport, if it has one. The {@link Viewportable} interface 
	 * provides a method to return the unit increment.
	 * 
	 * @return the unit increment
	 * @see org.aswing.JScrollPane
	 * @see org.aswing.Viewportable
	 */
	public function getUnitIncrement():Number{
		return unitIncrement;
	}
	
	/**
	 * Sets the block increment.
	 * @param blockIncrement the block increment.
	 * @see #getBlockIncrement()
	 */
	public function setBlockIncrement(blockIncrement:Number):Void{
		this.blockIncrement = blockIncrement;
	}
	
	/**
	 * Returns the amount to change the scrollbar's value by, given a block (usually "page") 
	 * up/down request. A ScrollBarUI implementation typically calls this method when the 
	 * user clicks above or below the scrollbar "knob" to change the value up or down by 
	 * large amount. Subclasses my override this method to compute a value, e.g. the change 
	 * required to scroll up or down one paragraph in a text document. 
	 * <p>
	 * The JScrollPane component creates scrollbars (by default) that then
	 * set the block increment by the viewport, if it has one. The {@link Viewportable} interface 
	 * provides a method to return the block increment.
	 * 
	 * @return the block increment
	 * @see JScrollPane
	 * @see Viewportable
	 */
	public function getBlockIncrement():Number{
		return blockIncrement;
	}
	
	/**
	 * Returns the scrollbar's value.
	 * @return the scrollbar's value property.
	 * @see #setValue()
	 * @see BoundedRangeModel#getValue()
	 */
	public function getValue():Number{
		return getModel().getValue();
	}
	
	/**
	 * Sets the scrollbar's value. This method just forwards the value to the model.
	 * @param value the value to set.
	 * @see #getValue()
	 * @see BoundedRangeModel#setValue()
	 */
	public function setValue(value:Number):Void{
		var m:BoundedRangeModel = getModel();
		m.setValue(value);
	}
	
	/**
	 * Returns the scrollbar's extent. In many scrollbar look and feel 
	 * implementations the size of the scrollbar "knob" or "thumb" is proportional to the extent. 
	 * @return the scrollbar's extent.
	 * @see #setVisibleAmount()
	 * @see BoundedRangeModel#getExtent()
	 */
	public function getVisibleAmount():Number{
		return getModel().getExtent();
	}
	
	/**
	 * Set the model's extent property.
	 * @param extent the extent to set
	 * @see #getVisibleAmount()
	 * @see BoundedRangeModel#setExtent()
	 */
	public function setVisibleAmount(extent:Number):Void{
		getModel().setExtent(extent);
	}
	
	/**
	 * Returns the minimum value supported by the scrollbar (usually zero). 
	 * @return the minimum value supported by the scrollbar
	 * @see #setMinimum()
	 * @see BoundedRangeModel#getMinimum()
	 */
	public function getMinimum():Number{
		return getModel().getMinimum();
	}
	
	/**
	 * Sets the model's minimum property. 
	 * @param minimum the minimum to set.
	 * @see #getMinimum()
	 * @see BoundedRangeModel#setMinimum()
	 */
	public function setMinimum(minimum:Number):Void{
		getModel().setMinimum(minimum);
	}
	
	/**
	 * Returns the maximum value supported by the scrollbar.
	 * @return the maximum value supported by the scrollbar
	 * @see #setMaximum()
	 * @see BoundedRangeModel#getMaximum()
	 */
	public function getMaximum():Number{
		return getModel().getMaximum();
	}
	
	/**
	 * Sets the model's maximum property.
	 * @param maximum the maximum to set.
	 * @see #getMaximum()
	 * @see BoundedRangeModel#setMaximum()
	 */	
	public function setMaximum(maximum:Number):Void{
		getModel().setMaximum(maximum);
	}
	
	/**
	 * True if the scrollbar knob is being dragged. 
	 * @return the value of the model's valueIsAdjusting property
	 */
	public function getValueIsAdjusting():Boolean{
		return getModel().getValueIsAdjusting();
	}
	
	/**
	 * Sets the model's valueIsAdjusting property. Scrollbar look and feel 
	 * implementations should set this property to true when a knob drag begins, 
	 * and to false when the drag ends. The scrollbar model will not generate 
	 * ChangeEvents while valueIsAdjusting is true. 
	 * @see BoundedRangeModel#setValueIsAdjusting()
	 */
	public function setValueIsAdjusting(b:Boolean):Void{
		var m:BoundedRangeModel = getModel();
		m.setValueIsAdjusting(b);
	}
	
	/**
	 * Sets the four BoundedRangeModel properties after forcing the arguments to 
	 * obey the usual constraints: "minimum le value le value+extent le maximum" 
	 * ("le" means less or equals)
	 */
	public function setValues(newValue:Number, newExtent:Number, newMin:Number, newMax:Number):Void{
		var m:BoundedRangeModel = getModel();
		m.setRangeProperties(newValue, newExtent, newMin, newMax, m.getValueIsAdjusting());
	}
	
	/**
	 * Shortcut to and ON_ADJUSTMENT_VALUE_CHANGED listener.
	 * <p>
	 * addAdjustmentListener(func:Function)<br>
	 * addAdjustmentListener(func:Function, obj:Object)<br>
	 * @param func the function which want to handler the event.
	 * @param obj context in which to run the function of param func.
	 * @see #ON_ADJUSTMENT_VALUE_CHANGED
	 */	
	public function addAdjustmentListener(func:Function, obj:Object):Object{
		return addEventListener(ON_ADJUSTMENT_VALUE_CHANGED, func, obj);
	}
	
	private function fireAdjustmentValueChanged():Void{
		dispatchEvent(createEventObj(ON_ADJUSTMENT_VALUE_CHANGED));
	}
	
	public function setEnabled(b:Boolean):Void{
		if(b != isEnabled()){
			repaint();
		}
		super.setEnabled(b);
		for (var i:Number = 0; i < children.length; i++){
			children[i].setEnabled(b);
		}
	}	
}
