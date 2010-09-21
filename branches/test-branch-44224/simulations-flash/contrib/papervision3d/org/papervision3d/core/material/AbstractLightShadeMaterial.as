package org.papervision3d.core.material
{
	import flash.utils.Dictionary;
	
	import org.papervision3d.core.material.TriangleMaterial;
	import org.papervision3d.core.math.Matrix3D;
	import org.papervision3d.core.proto.LightObject3D;
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.core.render.draw.ITriangleDrawer;
	import org.papervision3d.core.render.material.IUpdateBeforeMaterial;
	import org.papervision3d.materials.utils.LightMatrix;
	import org.papervision3d.objects.DisplayObject3D;

	public class AbstractLightShadeMaterial extends TriangleMaterial implements ITriangleDrawer, IUpdateBeforeMaterial
	{
	
		public var lightMatrices:Dictionary;
		private var _light:LightObject3D;
		protected static var lightMatrix:Matrix3D;
		
		public function AbstractLightShadeMaterial()
		{
			super();
			init();
		}
		
		protected function init():void
		{
			lightMatrices = new Dictionary();
		}
		
		public function updateBeforeRender(renderSessionData:RenderSessionData):void
		{	
			for(var object:Object in objects){
				var do3d:DisplayObject3D = object as DisplayObject3D;
				lightMatrices[object] = LightMatrix.getLightMatrix(light, do3d, renderSessionData, lightMatrices[object]);
			}
		}
		
		public function set light(light:LightObject3D):void
		{
			_light = light;
		}
		
		public function get light():LightObject3D
		{
			return _light;	
		}
		
	}
}