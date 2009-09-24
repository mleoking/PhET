package org.papervision3d.core.animation.channel.transform 
{
	import org.papervision3d.core.animation.curve.Curve3D;	
	import org.papervision3d.core.animation.channel.Channel3D;	
	import org.papervision3d.core.math.Matrix3D;	
	import org.papervision3d.core.animation.channel.transform.TransformChannel3D;
	
	/**
	 * @author Tim Knip / floorplanner.com
	 */
	public class MatrixChannel3D extends TransformChannel3D 
	{
		/**
		 * Constructor.
		 * 
		 * @param transform
		 */
		public function MatrixChannel3D(transform : Matrix3D) 
		{
			super(transform);
		}
		
		/**
		 * 
		 */
		override public function clone() : Channel3D 
		{
			var channel : MatrixChannel3D = new MatrixChannel3D(this.transform);
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
			super.update(time);
			
			var i : int;
			var m : Matrix3D = this.transform;
			var curves : Array = _curves;
			var numCurves : int = curves.length;
			var props : Array = [
				"n11", "n12", "n13", "n14",
				"n21", "n22", "n23", "n24",
				"n31", "n32", "n33", "n34",
				"n41", "n42", "n43", "n44"
			];
			
			if(curves && numCurves > 11)
			{
				for(i = 0; i < numCurves; i++)
				{
					m[ props[i] ] = output[i];
				}
			}
		}
	}
}
