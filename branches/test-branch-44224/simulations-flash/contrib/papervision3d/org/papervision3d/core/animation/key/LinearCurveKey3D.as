package org.papervision3d.core.animation.key 
{
	import org.papervision3d.core.animation.key.CurveKey3D;
	
	/**
	 * @author Tim Knip / floorplanner.com
	 */
	public class LinearCurveKey3D extends CurveKey3D 
	{
		/**
		 * 
		 */
		public function LinearCurveKey3D(input : Number = 0, output : Number = 0) 
		{
			super(input, output);
		}

		/**
		 * Clone.
		 * 
		 * @return The cloned key.
		 */
		override public function clone() : CurveKey3D 
		{
			return new LinearCurveKey3D(this.input, this.output);
		}
	}
}
