package org.papervision3d.core.geom.renderables
{
	
	/**
	 * @author Andy Zupko.
	 */
	 
	import org.papervision3d.core.geom.Pixels;

	public class Pixel3D
	{

		public var vertex3D:Vertex3D;
		public var color:uint;
		public var instance:Pixels;
		
		public function Pixel3D(color:uint, x:Number=0, y:Number=0, z:Number=0)
		{
			this.color = color;
			vertex3D = new Vertex3D(x,y,z);
		}
		
		public function set x(x:Number):void
		{
			vertex3D.x = x;
		}
		
		public function get x():Number
		{
			return vertex3D.x;
		}
		
		public function set y(y:Number):void
		{
			vertex3D.y = y;
		}
		
		public function get y():Number
		{
			return vertex3D.y;
		}
		
		public function set z(z:Number):void
		{
			vertex3D.z = z;
		}
		
		public function get z():Number
		{
			return vertex3D.z;
		}
		
		
	}
}