package org.papervision3d.materials.special
{
	import flash.display.Graphics;
	
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.view.Viewport3D;
	
	public class FogMaterial
	{
		public var color:uint;
		public var alpha:Number;
		
		public function FogMaterial(color:uint=0)
		{
			this.color = color;	
		}
		
		public function draw(renderSessionData:RenderSessionData, graphics:Graphics, alpha:Number):void{
			var vp:Viewport3D = renderSessionData.viewPort;
			graphics.beginFill(color, alpha);
			graphics.drawRect(-(vp.width)*0.5, -(vp.height)*0.5, vp.width, vp.height);
			graphics.endFill();
		}

	}
}