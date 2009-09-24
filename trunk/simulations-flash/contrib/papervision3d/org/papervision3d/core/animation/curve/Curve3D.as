package org.papervision3d.core.animation.curve 
{
	import org.papervision3d.core.animation.enum.InfinityType;	
	import org.papervision3d.core.animation.key.BezierCurveKey3D;	
	import org.papervision3d.core.animation.key.LinearCurveKey3D;	
	import org.papervision3d.core.animation.key.CurveKey3D;

	/**
	 * A Curve3D stores a bunch of CurveKey3D's and given a value representing an input point on a curve returns 
	 * the output of the curve for that input.
	 * 
	 * @see org.papervision3d.core.animation.key.CurveKey3D
	 * 
	 * @author Tim Knip / floorplanner.com
	 */
	public class Curve3D 
	{
		/**
		 * The behavior of the curve after the last key. 
		 * 
		 * @see org.papervision3d.core.animation.enum.InfinityType
		 */
		public var postInfinity : uint;
		
		/**
		 * The behavior of the curve before the first key. 
		 * 
		 * @see org.papervision3d.core.animation.enum.InfinityType
		 */
		public var preInfinity : uint;
		
		/**
		 * 
		 */
		protected var _keys : Array;
		
		/** */
		private var _current : int = 0;

		/**
		 * Constructor.
		 * 
		 * @param preInfinity	The behavior of the curve before the first key. Defaults to InfinityType.CONSTANT
		 * @param postInfinity	The behavior of the curve after the last key. Defaults to InfinityType.CONSTANT
		 * 
		 * @see org.papervision3d.core.animation.enum.InfinityType
		 */
		public function Curve3D(preInfinity : int = -1, postInfinity : int = -1)
		{
			this.postInfinity = postInfinity < 0 ? InfinityType.CONSTANT : postInfinity; 
			this.preInfinity = preInfinity < 0 ? InfinityType.CONSTANT : preInfinity;
			
			_keys = new Array();
			_current = 0;
		}
		
		/**
		 * Adds a key.
		 * 
		 * @param key
		 * 
		 * @return The added key or null on failure
		 */
		public function addKey(key : CurveKey3D) : CurveKey3D
		{
			if(_keys.indexOf(key) == -1)
			{
				_keys.push(key);	
				return key;
			}
			return null;
		}

		/**
		 * Removes a key.
		 * 
		 * @param key
		 * 
		 * @return The removed key or null on failure
		 */
		public function removeKey(key : CurveKey3D) : CurveKey3D
		{
			var pos : int = _keys.indexOf(key);
			if(pos >= 0)
			{
				_keys.splice(pos, 1);
				return key;	
			}
			return null;	
		}
		
		/**
		 * Main workhorse of the animation system.
		 * 
		 * @param input	Time in seconds.
		 * 
		 * @return	The current value of this curve.
		 */
		public function evaluate(input : Number) : Number
		{
			var kfs : Array = _keys;
			var numKeys : int = kfs.length;
			
			// Check for empty curves and poses (curves with 1 key).
			if( numKeys == 0 ) return 0.0;
			if( numKeys == 1 ) return kfs[0].output;
			
			var index:int;
			var startKey : CurveKey3D = kfs[0];
			var endKey : CurveKey3D = kfs[numKeys-1];
			var outputStart:Number = startKey.output;
			var outputEnd:Number = endKey.output;
			var inputStart:Number = startKey.input;
			var inputEnd:Number = endKey.input;
			var inputSpan:Number = inputEnd - inputStart;
			var outputSpan:Number = outputEnd - outputStart;
			var cycleCount:Number;

			// Account for pre-infinity mode
			var outputOffset:Number = 0.0;
			
			if(input < inputStart)
			{
				switch(preInfinity)
				{
					case InfinityType.CONSTANT: 
						return outputStart;
					case InfinityType.LINEAR: 
						return outputStart + (input - inputStart) * (kfs[1].output - outputStart) / (kfs[1].input - inputStart);
					case InfinityType.CYCLE:
						cycleCount = Math.ceil((inputStart - input) / inputSpan); 
						input += cycleCount * inputSpan; 
						break;
					case InfinityType.CYCLE_RELATIVE:
						cycleCount = Math.ceil((inputStart - input) / inputSpan); 
						input += cycleCount * inputSpan; 
						outputOffset -= cycleCount * outputSpan;
						break;
					case InfinityType.OSCILLATE:
						cycleCount = Math.ceil((inputStart - input) / (2.0 * inputSpan)); 
						input += cycleCount * 2.0 * inputSpan; 
						input = inputEnd - Math.abs(input - inputEnd); 
						break;
					default: 
						return outputStart;
				}
			}
			else if(input >= inputEnd)
			{
				// Account for post-infinity mode
				switch (postInfinity)
				{
					case InfinityType.CONSTANT: 
						return outputEnd;
					case InfinityType.LINEAR: 
						return outputEnd + (input - inputEnd) * (kfs[numKeys-2].output - outputEnd) / (kfs[numKeys-2].input - inputEnd);
					case InfinityType.CYCLE:
						cycleCount = Math.ceil((input - inputEnd) / inputSpan);
						input -= cycleCount * inputSpan; 
						break;
					case InfinityType.CYCLE_RELATIVE:
						cycleCount = Math.ceil((input - inputEnd) / inputSpan); 
						input -= cycleCount * inputSpan; 
						outputOffset += cycleCount * outputSpan; 
						break;
					case InfinityType.OSCILLATE:
						cycleCount = Math.ceil((input - inputEnd) / (2.0 * inputSpan)); 
						input -= cycleCount * 2.0 * inputSpan; 
						input = inputStart + Math.abs(input - inputStart); 
						break;
					default: 
						return outputEnd;
				}
			}
			
			
			var kf : CurveKey3D = _current < numKeys ? kfs[_current] : null;
			var cf : CurveKey3D = _current - 1 >= 0 ? kfs[_current-1] : null;
			
			// might speed up interval search a bit
			_current = (cf && kf && cf.input < input) ? _current : 0;
			
			// Find the current interval
			// TODO: speed up interval search
			for( index = _current; index < numKeys; index++ )
			{
				kf = kfs[index];
				if(kf.input > input)
				{
					break;	
				}
			}
			
			_current = index;
			
			if(index < 1)
			{
				return outputOffset + outputStart;
			}
			
			// Get the keys and values for this interval
			endKey = kfs[index];
			startKey = kfs[index - 1];
			
			var endValue:Number = endKey.output;
			var startValue:Number = startKey.output;
			var inputInterval : Number = endKey.input - startKey.input;
			var outputInterval : Number = endValue - startValue;
			var output:Number;
			
			// TODO: handle Bezier curves.
			if(startKey is LinearCurveKey3D || startKey is BezierCurveKey3D)
			{
				output = startValue + ((input - startKey.input) / inputInterval) * outputInterval;
			}
			else
			{
				output = startValue;
			}

			return outputOffset + output;	
		}
		
		/**
		 * Clone.
		 * 
		 * @return	The cloned curve.
		 */
		public function clone() : Curve3D
		{
			var curve : Curve3D = new Curve3D(this.preInfinity, this.postInfinity);
			var key : CurveKey3D;
			var i : int;
			
			for(i = 0; i < _keys.length; i++)
			{
				key = _keys[i];
				curve.addKey(key.clone());
			}
			
			return curve;	
		}
		
		/**
		 * 
		 */
		public function get keys() : Array
		{
			return _keys;
		}
	}
}
