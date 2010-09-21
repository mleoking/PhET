package org.papervision3d.core.animation.channel.transform {
	import org.papervision3d.core.animation.channel.Channel3D;	
	import org.papervision3d.core.animation.curve.Curve3D;	
	import org.papervision3d.core.math.Matrix3D;	
	import org.papervision3d.core.animation.channel.transform.TransformChannel3D;
	
	/**
	 * @author Tim Knip / floorplanner.com
	 */
	public class TranslationChannel3D extends TransformChannel3D 
	{
		/**
		 * Constructor.
		 * 
		 * @param transform
		 */
		public function TranslationChannel3D(transform : Matrix3D) 
		{
			super(transform);
		}
		
		/**
		 * 
		 */
		override public function clone() : Channel3D 
		{
			var channel : TranslationChannel3D = new TranslationChannel3D(this.transform);
			var curve : Curve3D;
			var i : int;
			
			for(i = 0; i < _curves.length; i++)
			{
				curve = _curves[i];
				channel.addCurve(curve.clone(), (i == _curves.length-1));
			}
			return channel;
		}
		
		/**
		 * 
		 */
		override public function update(time : Number) : void 
		{
			if(!_curves || _curves.length != 3)
			{
				return;
			}
			
			super.update(time);
		
			transform.reset();
			transform.n14 = output[0];
			transform.n24 = output[1];
			transform.n34 = output[2];	
		}
	}
}
