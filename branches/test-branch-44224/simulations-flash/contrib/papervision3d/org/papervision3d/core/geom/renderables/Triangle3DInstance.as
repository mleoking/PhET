package org.papervision3d.core.geom.renderables
{
	import flash.display.Sprite;
	
	import org.papervision3d.core.math.Number3D;
	import org.papervision3d.objects.DisplayObject3D;
	
	public class Triangle3DInstance
	{
		public var instance:DisplayObject3D;
		
		/**
		* container is initialized via DisplayObject3D's render method IF DisplayObject3D.faceLevelMode is set to true
		*/
		public var container:Sprite;
		public var visible:Boolean = false;
		public var screenZ:Number;
		public var faceNormal:Number3D;
		
		public function Triangle3DInstance(face:Triangle3D, instance:DisplayObject3D)
		{
			this.instance = instance;
			faceNormal = new Number3D();
		}
	}
}