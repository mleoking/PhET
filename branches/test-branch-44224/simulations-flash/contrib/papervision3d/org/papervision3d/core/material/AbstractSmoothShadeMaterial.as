package org.papervision3d.core.material
{
	import flash.geom.Matrix;
	
	import org.papervision3d.core.render.draw.ITriangleDrawer;
	import org.papervision3d.core.render.material.IUpdateBeforeMaterial;
	
	/**
	 * @Author Ralph Hauwert
	 */
	public class AbstractSmoothShadeMaterial extends AbstractLightShadeMaterial implements ITriangleDrawer, IUpdateBeforeMaterial
	{
		protected var transformMatrix:Matrix;
		protected var triMatrix:Matrix;
		
		public function AbstractSmoothShadeMaterial()
		{
			super();
			
		}
		
		override protected function init():void
		{
			super.init();
			transformMatrix = new Matrix();
			triMatrix = new Matrix();
		}
		
	}
}