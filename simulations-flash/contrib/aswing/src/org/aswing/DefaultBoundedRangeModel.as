/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.BoundedRangeModel;
import org.aswing.Component;
import org.aswing.EventDispatcher;
 
/**
 * 
 * @author iiley
 */
class org.aswing.DefaultBoundedRangeModel extends EventDispatcher implements BoundedRangeModel{

	private var value:Number;
	private var extent:Number;
	private var min:Number;
	private var max:Number;
	private var isAdjusting:Boolean;
	
	/**
	 * @throws Error when invalid range properties
	 */
	public function DefaultBoundedRangeModel(value:Number, extent:Number, min:Number, max:Number){
		if(value == undefined) value = 0;
		if(extent == undefined) extent = 0;
		if(min == undefined) min = 0;
		if(max == undefined) max = 100;
		
		this.isAdjusting = false;
		
		if (max >= min && value >= min && value + extent >= value && value + extent <= max){
			this.value = value;
			this.extent = extent;
			this.min = min;
			this.max = max;
		}else{
			trace("invalid range properties when construct ort.aswing.DefaultBoundedRangeModel");
			throw new Error("invalid range properties");
		}
	}
	
	public function getValue():Number{
		return value;
	}
	
	public function getExtent():Number{
		return extent;
	}
	
	public function getMinimum():Number{
		return min;
	}
	
	public function getMaximum():Number{
		return max;
	}
	
	public function setValue(n:Number):Void{
		n = Math.min(n, max - extent);
		var newValue:Number = Math.max(n, min);
		setRangeProperties(newValue, extent, min, max, isAdjusting);
	}
	
	public function setExtent(n:Number):Void{
		var newExtent:Number = Math.max(0, n);
		if (value + newExtent > max){
			newExtent = max - value;
		}
		setRangeProperties(value, newExtent, min, max, isAdjusting);
	}
	
	public function setMinimum(n:Number):Void{
		var newMax:Number = Math.max(n, max);
		var newValue:Number = Math.max(n, value);
		var newExtent:Number = Math.min(newMax - newValue, extent);
		setRangeProperties(newValue, newExtent, n, newMax, isAdjusting);
	}
	
	public function setMaximum(n:Number):Void{
		var newMin:Number = Math.min(n, min);
		var newExtent:Number = Math.min(n - newMin, extent);
		var newValue:Number = Math.min(n - newExtent, value);
		setRangeProperties(newValue, newExtent, newMin, n, isAdjusting);
	}
	
	public function setValueIsAdjusting(b:Boolean):Void{
		setRangeProperties(value, extent, min, max, b);
	}
	
	public function getValueIsAdjusting():Boolean{
		return isAdjusting;
	}
	
	public function setRangeProperties(newValue:Number, newExtent:Number, newMin:Number, newMax:Number, adjusting:Boolean):Void{
		if (newMin > newMax){
			newMin = newMax;
		}
		if (newValue > newMax){
			newMax = newValue;
		}
		if (newValue < newMin){
			newMin = newValue;
		}
		if (newExtent + newValue > newMax){
			newExtent = newMax - newValue;
		}
		if (newExtent < 0){
			newExtent = 0;
		}
		var isChange:Boolean = newValue != value || newExtent != extent || newMin != min || newMax != max || adjusting != isAdjusting;
		if (isChange){
			value = newValue;
			extent = newExtent;
			min = newMin;
			max = newMax;
			isAdjusting = adjusting;
			fireStateChanged();
		}
	}
	
	public function addChangeListener(func:Function, obj:Object):Object{
		return addEventListener(Component.ON_STATE_CHANGED, func, obj);
	}
	
	public function fireStateChanged():Void{
		dispatchEvent(createEventObj(Component.ON_STATE_CHANGED));
	}	
	
	public function toString():String{
		var modelString:String = "value=" + getValue() + ", " + "extent=" + getExtent() + ", " + "min=" + getMinimum() + ", " + "max=" + getMaximum() + ", " + "adj=" + getValueIsAdjusting();
		return "org.aswing.DefaultBoundedRangeModel" + "[" + modelString + "]";
	}	
}
