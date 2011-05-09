/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASWingConstants;
import org.aswing.BoundedRangeModel;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.DefaultIntegerBoundedRangeModel;
import org.aswing.Event;
import org.aswing.JSlider;
import org.aswing.JTextField;
import org.aswing.plaf.AdjusterUI;
import org.aswing.UIManager;

/**
 * A component that combine a input text and a button to drop-down a popup slider to let 
 * user input/select a value.
 * 
 * @author iiley
 */
class org.aswing.overflow.JAdjuster extends Container {

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
        
    /**
     * This translator translate a float value to a float string representation.
     */    
	public static var FLOAT_VALUE_TRANSLATOR:Function = function(value:Number):String{
		return value + "";
	};
	
    /**
     * This translator translate a float value to a integer string representation by round.
     */  	
	public static var INT_RESTRICT_VALUE_TRANSLATOR:Function = function(value:Number):String{
		return Math.round(value) + "";
	};	
	
    /**
     * This parser parse a float value from the string.
     */  	
	public static var FLOAT_VALUE_PARSER:Function = function(text:String):Number{
		var value:Number = parseFloat(text);
		if(isNaN(value)){
			value = 0;
		}
		return value;
	};
	
    /**
     * This parser parse a int value from the string.
     */  		
	public static var INT_RESTRICT_VALUE_PARSER:Function = function(text:String):Number{
		var value:Number = parseInt(text);
		if(isNaN(value)){
			value = 0;
		}
		return value;
	};
        
	private var modelListener:Object;
	private var model:BoundedRangeModel;
	private var columns:Number;
	private var orientation:Number;
	private var editable:Boolean;
	private var valueTranslator:Function;
	private var valueParser:Function;

	/**
	 * JAdjuster(columns:Number, orientation:Number)<br>
	 * JAdjuster(columns:Number) default orientation to VERTICAL<br>
	 * JAdjuster()  defalut columns to 3
	 * <p>
	 * Creates a adjuster with the specified columns input text and orientation<p>
	 * Defalut model is a instance of <code>DefaultIntegerBoundedRangeModel</code>, if 
	 * you need float value, you can create a <code>DefaultBoundedRangeModel</code> to it.
	 * @param columns (optional)the number of columns to use to calculate the input text preferred width
	 * @param orientation (optional)the pop-up slider's orientation to either VERTICAL or HORIZONTAL.
	 * @see org.aswing.DefaultIntegerBoundedRangeModel 
	 */
	public function JAdjuster(columns:Number, orientation:Number){
		super();
		if(columns == undefined) columns = 3;
		if(orientation == undefined) orientation = VERTICAL;
			
		setColumns(columns);
		setOrientation(orientation);
		
		
		editable = true;
		valueTranslator = INT_RESTRICT_VALUE_TRANSLATOR;
		valueParser     = INT_RESTRICT_VALUE_PARSER;
		
		model = new DefaultIntegerBoundedRangeModel(50, 0, 0, 100);
		addListenerToModel();
		updateUI();
	}

	public function setUI(ui:AdjusterUI):Void{
		super.setUI(ui);
	}
	
	public function getUI():AdjusterUI{
		return AdjusterUI(ui);
	}
	
	public function updateUI():Void{
		setUI(AdjusterUI(UIManager.getUI(this)));
	}
	
	public function getUIClassID():String{
		return "AdjusterUI";
	}
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.basic.BasicAdjusterUI;
    }
	
	/**
	 * Sets the number of columns for the input text. 
	 * @param columns the number of columns to use to calculate the preferred width.
	 */
	public function setColumns(columns:Number):Void{
		if(columns == undefined) columns = 3;
		if(columns < 0) columns = 0;
		if(this.columns != columns){
			this.columns = columns;
			getInputText().setColumns(columns);
		}
	}
	
	/**
	 * @see #setColumns
	 */
	public function getColumns():Number{
		return columns;
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
			getPopupSlider().setOrientation(orientation);
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
			getPopupSlider().setModel(model);
		}
	}
	
	private function addListenerToModel():Void{
		modelListener = model.addChangeListener(__onModelStateChanged, this);		
	}
	
	private function __onModelStateChanged(event:Event):Void{
		fireStateChanged();
	}
	
	/**
	 * Returns the input text component, based on the LAF, the AdjusterUI should 
	 * return the right text component to this. 
	 * @return the input text component.
	 * @see org.aswing.plaf.AdjusterUI
	 */
	public function getInputText():JTextField{
		return getUI().getInputText();
	}
	
	/**
	 * Returns the pop-up slider component, based on the LAF, the AdjusterUI should 
	 * return the right slider component to this. 
	 * @return the input text component.
	 * @see org.aswing.plaf.AdjusterUI
	 */	
	public function getPopupSlider():JSlider{
		return getUI().getPopupSlider();
	}
	
	/**
	 * Sets a function(Number):String to translator the value to the string representation in 
	 * the input text.
	 * <p>
	 * Generally, if you changed translator, you should change a right valueParser to suit it. 
	 * @param translator a function(Number):String.
	 * @see #getValueTranslator()
	 * @see #setValueParser()
	 */
	public function setValueTranslator(translator:Function):Void{
		if(valueTranslator != translator){
			valueTranslator = translator;
			repaint();
		}
	}
	
	/**
	 * Returns the value translator function(Number):String.
	 * @see #setValueTranslator()
	 */
	public function getValueTranslator():Function{
		return valueTranslator;
	}
	
	/**
	 * Sets a function(String):Number to parse the value from the string in 
	 * the input text.
	 * <p>
	 * Generally, if you changed parser, you should change a right valueTranslator to suit it. 
	 * @param parser a function(String):Number.
	 * @see #getValueParser()
	 * @see #setValueTranslator()
	 */
	public function setValueParser(parser:Function):Void{
		if(valueParser != parser){
			valueParser = parser;
			repaint();
		}
	}
	
	/**
	 * Returns the value parser function(String):Number.
	 * @see #setValueParser()
	 * @see #getValueTranslator()
	 */
	public function getValueParser():Function{
		return valueParser;
	}
	
	/**
	 * Sets whether the adjuster is editable to adjust, both the input text and pop-up slider.
	 * @param b true to make the adjuster can be edited the value, no to not.
	 * @see #isEditable()
	 */
	public function setEditable(b:Boolean):Void{
		if(editable != b){
			editable = b;
			repaint();
			revalidate();
		}
	}
	
	/**
	 * Returns whether the adjuster is editable.
	 * @return whether the adjuster is editable.
	 * @see #setEditable()
	 */
	public function isEditable():Boolean{
		return editable;
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
		
    /**
     * addActionListener(fuc:Function, obj:Object)<br>
     * addActionListener(fuc:Function)<br>
     * Adds a action listener to this <code>JAdjuster</code>. <code>JAdjuster</code> 
     * fire a action event when user finish a adjusting.
     * <p>
     * This event will fired though AdjusterUI implementation.
     * @param fuc the listener function.
     * @param obj which context to run in by the func.
     * @return the listener just added.
     * @see EventDispatcher#ON_ACT
     */
	public function addActionListener(func:Function, obj:Object):Object{
		return addEventListener(ON_ACT, func, obj);
	}
}