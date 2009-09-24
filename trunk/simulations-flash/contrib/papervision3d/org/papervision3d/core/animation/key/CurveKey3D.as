package org.papervision3d.core.animation.key 
{
	/**
	 * @author Tim Knip / floorplanner.com
	 */
	public class CurveKey3D 
	{
		/**
		 * 
		 */
		public var input : Number;
		
		/**
		 * 
		 */
		public var output : Number;
		
		/**
		 * Constructor.
		 */
		public function CurveKey3D(input : Number = 0, output : Number = 0)
		{
			this.input = input;
			this.output = output;
		}
		
		/**
		 * Clone.
		 * 
		 * @return The cloned key.
		 */
		public function clone() : CurveKey3D
		{
			return new CurveKey3D(this.input, this.output);
		}
	}
}
