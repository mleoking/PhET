package org.papervision3d.core.render.command
{
	import flash.display.BitmapData;
	import flash.display.Graphics;
	
	import org.papervision3d.core.geom.Pixels;
	import org.papervision3d.core.geom.renderables.Pixel3D;
	import org.papervision3d.core.geom.renderables.Vertex3DInstance;
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.view.layer.BitmapEffectLayer;
	
	public class RenderPixels extends RenderableListItem implements IRenderListItem
	{
		private var pixels:Pixels;
		public function RenderPixels(pixels:Pixels)
		{
			this.pixels = pixels;
			this.renderable = Pixels;
			this.renderableInstance = null;//pixels;
		}
		
		override public function render(renderSessionData:RenderSessionData, graphics:Graphics):void
		{
			var layer:BitmapEffectLayer = pixels.layer;
			
			var offsetX:Number = layer.width>>1;
			var offsetY:Number = layer.height>>1;
			var canvas:BitmapData = layer.canvas;
			
			var v3d:Vertex3DInstance;
			screenZ = 0;
			
			for each(var p:Pixel3D in pixels.pixels){
				v3d = p.vertex3D.vertex3DInstance;
				if(v3d.visible){
					canvas.setPixel32(v3d.x+offsetX, v3d.y+offsetY, p.color);
					screenZ += v3d.z;				
				}
			}
			
			screenZ /= pixels.pixels.length;
			layer.screenDepth += screenZ;
			layer.weight += 1;
		}

	}
}