package org.papervision3d.core.proto
{
	import org.papervision3d.core.math.Matrix3D;
	import org.papervision3d.materials.WireframeMaterial;
	import org.papervision3d.objects.DisplayObject3D;
	import org.papervision3d.objects.primitives.Sphere;

	public class LightObject3D extends DisplayObject3D
	{
		public var lightMatrix:Matrix3D;
		
		/** 
		 * A boolean value indicating whether to flip the light direction. Hack needed by DAE. 
		 * NOTE:  
		 */
		public var flipped:Boolean;
		
		private var _showLight:Boolean;
		
		private var displaySphere:Sphere;
		
		public function LightObject3D(showLight:Boolean = false, flipped:Boolean = false)
		{
			super();
			this.lightMatrix = Matrix3D.IDENTITY;
			this.showLight = showLight;
			this.flipped = flipped;
		}
		
		public function set showLight(show:Boolean):void
		{
			if(_showLight){
				removeChild(displaySphere);
			}
			if(show){
				displaySphere = new Sphere(new WireframeMaterial(0xffff00), 10, 3, 2);
				addChild(displaySphere);
			}
			_showLight = show;
		}
		
		public function get showLight():Boolean
		{
			return _showLight;
		}
	}
}