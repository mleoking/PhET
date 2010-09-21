/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.DefaultBoundedRangeModel;

/**
 * DefaultIntegerBoundedRangeModel round all number value to int
 * @author iiley
 */
class org.aswing.DefaultIntegerBoundedRangeModel extends DefaultBoundedRangeModel {
	
	public function DefaultIntegerBoundedRangeModel(value:Number, extent:Number, min:Number, max:Number){
		if(value == undefined) value = 0;
		if(extent == undefined) extent = 0;
		if(min == undefined) min = 0;
		if(max == undefined) max = 100;
		
		this.isAdjusting = false;
		
		value = Math.round(value);
		extent = Math.round(extent);
		min = Math.round(min);
		max = Math.round(max);
		
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
		
	public function setRangeProperties(newValue:Number, newExtent:Number, newMin:Number, newMax:Number, adjusting:Boolean):Void{
		newValue = Math.round(newValue);
		newExtent = Math.round(newExtent);
		newMin = Math.round(newMin);
		newMax = Math.round(newMax);
		
		super.setRangeProperties(newValue, newExtent, newMin, newMax, adjusting);
	}	
}