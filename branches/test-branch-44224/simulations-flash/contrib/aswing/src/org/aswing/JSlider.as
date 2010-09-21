/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASWingConstants;
import org.aswing.BoundedRangeModel;
import org.aswing.Component;
import org.aswing.DefaultBoundedRangeModel;
import org.aswing.Event;
import org.aswing.JToolTip;
import org.aswing.plaf.SliderUI;
import org.aswing.UIManager;

/**
 * A component that lets the user graphically select a value by sliding
 * a knob within a bounded interval. The slider can show both 
 * major tick marks and minor tick marks between them. The number of
 * values between the tick marks is controlled with 
 * <code>setMajorTickSpacing</code> and <code>setMinorTickSpacing</code>. 
 * @author iiley
 */
class org.aswing.JSlider extends Component {	
    /** 
     * Horizontal orientation.
     */
    public static var HORIZONTAL:Number = ASWingConstants.HORIZONTAL;
    /** 
     * Vertical orientation.
     */
    public static var VERTICAL:Number   = ASWingConstants.VERTICAL;
	       
	/**
	 * When the slider's state changed.
	 *<br>
	 * onStateChanged(source:JSlider)
	 */	
	public static var ON_STATE_CHANGED:String = Component.ON_STATE_CHANGED;
        
	private var modelListener:Object;
	private var model:BoundedRangeModel;
	private var orientation:Number;
	private var majorTickSpacing:Number;
	private var minorTickSpacing:Number;
	private var isInverted:Boolean;
	private var snapToTicks:Boolean;
	private var paintTrack:Boolean;
	private var paintTicks:Boolean;
	private var valueTip:JToolTip;
	private var showValueTip:Boolean;

	/**
	 * JSlider(orientation:Number, value:Number, extent:Number, min:Number, max:Number)<br>
	 * JSlider(orientation:Number) default to value=0, extent=10, min=0, max=100<br>
	 * JSlider() default orientation to HORIZONTAL.
	 * <p>
	 * Creates a slider with the specified orientation, value, extent, minimum, and maximum. 
	 * The "extent" is the size of the viewable area. It is also known as the "visible amount". 
	 * <p>
	 * 
	 * @param orientation the slider's orientation to either VERTICAL or HORIZONTAL. 
	 * @param min the min value
	 * @param max the max value
	 * @param value the selected value
	 */
	public function JSlider(orientation:Number, min:Number, max:Number, value:Number){
		super();
		if(orientation == undefined) orientation = HORIZONTAL;
		if(value == undefined) value = 50;
		if(min == undefined) min = 0;
		if(max == undefined) max = 100;
		
		isInverted = false;
		majorTickSpacing = 0;
		minorTickSpacing = 0;
		snapToTicks = false;
		paintTrack = true;
		paintTicks = false;
		showValueTip = false;
	
		setOrientation(orientation);
		model = new DefaultBoundedRangeModel(value, 0, min, max);
		addListenerToModel();
		updateUI();
	}

	public function setUI(ui:SliderUI):Void{
		super.setUI(ui);
	}
	
	public function getUI():SliderUI{
		return SliderUI(ui);
	}
	
	public function updateUI():Void{
		setUI(SliderUI(UIManager.getUI(this)));
	}
	
	public function getUIClassID():String{
		return "SliderUI";
	}
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.asw.ASWingSliderUI;
    }
	
    /**
     * Return this slider's vertical or horizontal orientation.
     * @return VERTICAL or HORIZONTAL
     * @see #setOrientation()
     */
	public function getOrientation():Number{
		return orientation;
	}
	
	
	/**
     * Set the slider's orientation to either VERTICAL or HORIZONTAL.
     * @param orientation the orientation to either VERTICAL orf HORIZONTAL
	 */
	public function setOrientation(orientation:Number):Void{
		var oldValue:Number = this.orientation;
		this.orientation = orientation;
		if (orientation != oldValue){
			repaint();
			revalidate();
		}
	}
	
	/**
	 * Returns data model that handles the slider's four fundamental properties: minimum, maximum, value, extent.
	 * @return the data model
	 */
	public function getModel():BoundedRangeModel{
		return model;
	}
	
	/**
	 * Sets the model that handles the slider's four fundamental properties: minimum, maximum, value, extent. 
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
		fireStateChanged();
	}
	
	/**
	 * Returns the slider's value.
	 * @return the slider's value property.
	 * @see #setValue()
	 * @see BoundedRangeModel#getValue()
	 */
	public function getValue():Number{
		return getModel().getValue();
	}
	
	/**
	 * Sets the slider's value. This method just forwards the value to the model.
	 * @param value the value to set.
	 * @see #getValue()
	 * @see BoundedRangeModel#setValue()
	 */
	public function setValue(value:Number):Void{
		var m:BoundedRangeModel = getModel();
		m.setValue(value);
	}
	
	/**
     * Returns the "extent" -- the range of values "covered" by the knob.
     * @return an int representing the extent
     * @see #setExtent()
     * @see BoundedRangeModel#getExtent()
	 */
	public function getExtent():Number{
		return getModel().getExtent();
	}
	
	/**
     * Sets the size of the range "covered" by the knob.  Most look
     * and feel implementations will change the value by this amount
     * if the user clicks on either side of the knob.
     * 
     * @see #getExtent()
     * @see BoundedRangeModel#setExtent()
	 */
	public function setExtent(extent:Number):Void{
		getModel().setExtent(extent);
	}
	
	/**
	 * Returns the minimum value supported by the slider (usually zero). 
	 * @return the minimum value supported by the slider
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
	 * Returns the maximum value supported by the slider.
	 * @return the maximum value supported by the slider
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
	 * True if the slider knob is being dragged. 
	 * @return the value of the model's valueIsAdjusting property
	 */
	public function getValueIsAdjusting():Boolean{
		return getModel().getValueIsAdjusting();
	}
	
	/**
	 * Sets the model's valueIsAdjusting property. Slider look and feel 
	 * implementations should set this property to true when a knob drag begins, 
	 * and to false when the drag ends. The slider model will not generate 
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
     * Returns true if the value-range shown for the slider is reversed,
     *
     * @return true if the slider values are reversed from their normal order
     * @see #setInverted
     */
    public function getInverted():Boolean { 
        return isInverted; 
    }

    /**
     * Specify true to reverse the value-range shown for the slider and false to
     * put the value range in the normal order.  The order depends on the
     * slider's <code>ComponentOrientation</code> property.  Normal (non-inverted)
     * horizontal sliders with a <code>ComponentOrientation</code> value of 
     * <code>LEFT_TO_RIGHT</code> have their maximum on the right.  
     * Normal horizontal sliders with a <code>ComponentOrientation</code> value of
     * <code>RIGHT_TO_LEFT</code> have their maximum on the left.  Normal vertical 
     * sliders have their maximum on the top.  These labels are reversed when the 
     * slider is inverted.
     *
     * @param b  true to reverse the slider values from their normal order
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: If true reverses the slider values from their normal order 
     * 
     */
    public function setInverted( b:Boolean ):Void { 
        if (b != isInverted) {
        	isInverted = b;
            repaint();
        }
    }	
	
    /**
     * This method returns the major tick spacing.  The number that is returned
     * represents the distance, measured in values, between each major tick mark.
     * If you have a slider with a range from 0 to 50 and the major tick spacing
     * is set to 10, you will get major ticks next to the following values:
     * 0, 10, 20, 30, 40, 50.
     *
     * @return the number of values between major ticks
     * @see #setMajorTickSpacing()
     */
    public function getMajorTickSpacing():Number { 
        return majorTickSpacing; 
    }
    
    /**
     * This method sets the major tick spacing.  The number that is passed-in
     * represents the distance, measured in values, between each major tick mark.
     * If you have a slider with a range from 0 to 50 and the major tick spacing
     * is set to 10, you will get major ticks next to the following values:
     * 0, 10, 20, 30, 40, 50.
     *
     * @see #getMajorTickSpacing()
     */    
    public function setMajorTickSpacing(n:Number):Void{
    	if(n != majorTickSpacing){
    		majorTickSpacing = n;
    		if(getPaintTicks()){
    			repaint();
    		}
    	}
    }
	
    /**
     * This method returns the minor tick spacing.  The number that is returned
     * represents the distance, measured in values, between each minor tick mark.
     * If you have a slider with a range from 0 to 50 and the minor tick spacing
     * is set to 10, you will get minor ticks next to the following values:
     * 0, 10, 20, 30, 40, 50.
     *
     * @return the number of values between minor ticks
     * @see #getMinorTickSpacing
     */
    public function getMinorTickSpacing():Number { 
        return minorTickSpacing; 
    }


    /**
     * This method sets the minor tick spacing.  The number that is passed-in
     * represents the distance, measured in values, between each minor tick mark.
     * If you have a slider with a range from 0 to 50 and the minor tick spacing
     * is set to 10, you will get minor ticks next to the following values:
     * 0, 10, 20, 30, 40, 50.
     *
     * @see #getMinorTickSpacing()
     */
    public function setMinorTickSpacing(n:Number):Void { 
        if (minorTickSpacing != n) {
        	minorTickSpacing = n;
    		if(getPaintTicks()){
    			repaint();
    		}
        }
    }	
    /**
     * Specifying true makes the knob (and the data value it represents) 
     * resolve to the closest tick mark next to where the user
     * positioned the knob.
     *
     * @param b  true to snap the knob to the nearest tick mark
     * @see #getSnapToTicks()
     */
    public function setSnapToTicks(b:Boolean):Void { 
        if(b != snapToTicks){
        	snapToTicks = b;
        	repaint();
        }
    }    

    /**
     * Returns true if the knob (and the data value it represents) 
     * resolve to the closest tick mark next to where the user
     * positioned the knob.
     *
     * @return true if the value snaps to the nearest tick mark, else false
     * @see #setSnapToTicks()
     */
    public function getSnapToTicks():Boolean { 
        return snapToTicks; 
    }    
    
    /**
     * Tells if tick marks are to be painted.
     * @return true if tick marks are painted, else false
     * @see #setPaintTicks()
     */
    public function getPaintTicks():Boolean { 
        return paintTicks; 
    }


    /**
     * Determines whether tick marks are painted on the slider.
     * @see #getPaintTicks()
     */
    public function setPaintTicks(b:Boolean):Void { 
        if (paintTicks != b) {
			paintTicks = b;
            revalidate();
            repaint();
        }
    }

    /**
     * Tells if the track (area the slider slides in) is to be painted.
     * @return true if track is painted, else false
     * @see #setPaintTrack()
     */
    public function getPaintTrack():Boolean { 
        return paintTrack; 
    }


    /**
     * Determines whether the track is painted on the slider.
     * @see #getPaintTrack()
     */
    public function setPaintTrack(b:Boolean):Void { 
        if (paintTrack != b) {
        	paintTrack = b;
            repaint();
        }
    }
    
    /**
     * Sets whether show a tip for the value when user slide the thumb.
     * This is related to the LAF, different LAF may have different display for this.
     * Default value is false.
     * @param b true to set to show tip for value, false indicate not show tip.
     * @see #getShowValueTip()
     */
    public function setShowValueTip(b:Boolean):Void{
    	if(showValueTip != b){
    		showValueTip = b;
    		if(showValueTip){
    			if(valueTip == null){
    				createDefaultValueTip();
    			}
    		}else{
    			if(valueTip != null && valueTip.isShowing()){
    				valueTip.disposeToolTip();
    			}
    		}
    	}
    }
    
    /**
     * Returns whether show a tip for the value when user slide the thumb.
     * This is related to the LAF, different LAF may have different display for this.
     * Default value is false.
     * @return whether show a tip for the value when user slide the thumb.
     * @see #setShowValueTip()
     */
    public function getShowValueTip():Boolean{
    	if(showValueTip && valueTip == null){
    		createDefaultValueTip();
    	}
    	return showValueTip;
    }
    
    /**
     * Returns the <code>JToolTip</code> which is used to show the value tip.
     * This may return null if the slider had never set to showValueTip.
     * @return the <code>JToolTip</code> which is used to show the value tip.
     * @see #setShowValueTip()
     * @see #setValueTip()
     */
    public function getValueTip():JToolTip{
    	return valueTip;
    }
    
    /**
     * Sets a Tip component to show the value tip. By default there will be a <code>JToolTip</code> 
     * instance created for this.
     * @param valueTip  the Tip component to show the value tip
     * @see #getValueTip()
     * @see #getValueTip()
     */
    public function setValueTip(valueTip:JToolTip):Void{
    	if(valueTip != null){
    		valueTip.setTargetComponent(this);
    	}
    	this.valueTip = valueTip;
    	if(valueTip == null && getShowValueTip()){
    		createDefaultValueTip();
    	}
    }
	
	private function createDefaultValueTip():Void{
    	valueTip = new JToolTip();
    	valueTip.setTargetComponent(this);
	}
	
	/**
	 * Shortcut to and ON_STATE_CHANGED listener.
	 * <p>
	 * addChangeListener(func:Function)<br>
	 * addChangeListener(func:Function, obj:Object)<br>
	 * @param func the function which want to handler the event.
	 * @param obj context in which to run the function of param func.
	 * @see #ON_STATE_CHANGED
	 */	
	public function addChangeListener(func:Function, obj:Object):Object{
		return addEventListener(ON_STATE_CHANGED, func, obj);
	}
}