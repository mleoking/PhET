/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{
	
import flash.events.EventDispatcher;
import flash.events.IEventDispatcher;
import org.aswing.event.InteractiveEvent;

/**
 * The default implementation of BoundedRangeModel.
 * @author iiley
 */
public class DefaultBoundedRangeModel extends EventDispatcher implements BoundedRangeModel{
	
	private var value:int;
	private var extent:int;
	private var min:int;
	private var max:int;
	private var isAdjusting:Boolean;
	
	/**
	 * Create a DefaultBoundedRangeModel
	 * @throws RangeError when invalid range properties
	 */
	public function DefaultBoundedRangeModel(value:int=0, extent:int=0, min:int=0, max:int=100){
		isAdjusting = false;
		
		if (max >= min && value >= min && value + extent >= value && value + extent <= max){
			this.value = value;
			this.extent = extent;
			this.min = min;
			this.max = max;
		}else{
			throw new RangeError("invalid range properties");
		}
	}
	
	public function getValue():int{
		return value;
	}
	
	public function getExtent():int{
		return extent;
	}
	
	public function getMinimum():int{
		return min;
	}
	
	public function getMaximum():int{
		return max;
	}
	
	public function setValue(n:int, programmatic:Boolean=true):void{
		n = Math.min(n, max - extent);
		var newValue:int = Math.max(n, min);
		setRangeProperties(newValue, extent, min, max, isAdjusting, programmatic);
	}
	
	public function setExtent(n:int):void{
		var newExtent:int = Math.max(0, n);
		if (value + newExtent > max){
			newExtent = max - value;
		}
		setRangeProperties(value, newExtent, min, max, isAdjusting);
	}
	
	public function setMinimum(n:int):void{
		var newMax:int = Math.max(n, max);
		var newValue:int = Math.max(n, value);
		var newExtent:int = Math.min(newMax - newValue, extent);
		setRangeProperties(newValue, newExtent, n, newMax, isAdjusting);
	}
	
	public function setMaximum(n:int):void{
		var newMin:int = Math.min(n, min);
		var newExtent:int = Math.min(n - newMin, extent);
		var newValue:int = Math.min(n - newExtent, value);
		setRangeProperties(newValue, newExtent, newMin, n, isAdjusting);
	}
	
	public function setValueIsAdjusting(b:Boolean):void{
		setRangeProperties(value, extent, min, max, b, false);
	}
	
	public function getValueIsAdjusting():Boolean{
		return isAdjusting;
	}
	
	public function setRangeProperties(newValue:int, newExtent:int, newMin:int, newMax:int, adjusting:Boolean, programmatic:Boolean=true):void{
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
			fireStateChanged(programmatic);
		}
	}
	
	public function addStateListener(listener:Function, priority:int=0, useWeakReference:Boolean=false):void{
		addEventListener(InteractiveEvent.STATE_CHANGED, listener, false, priority);
	}
	
	public function removeStateListener(listener:Function):void{
		removeEventListener(InteractiveEvent.STATE_CHANGED, listener);
	}
		
	protected function fireStateChanged(programmatic:Boolean):void{
		dispatchEvent(new InteractiveEvent(InteractiveEvent.STATE_CHANGED, programmatic));
	}
	
	override public function toString():String{
		var modelString:String = "value=" + getValue() + ", " + "extent=" + getExtent() + ", " + "min=" + getMinimum() + ", " + "max=" + getMaximum() + ", " + "adj=" + getValueIsAdjusting();
		return "DefaultBoundedRangeModel" + "[" + modelString + "]";
	}
	
}
}