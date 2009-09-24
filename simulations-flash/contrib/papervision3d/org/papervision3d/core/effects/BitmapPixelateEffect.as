/**
* ...
* @author Default
* @version 0.1
*/

package org.papervision3d.core.effects {
	import flash.display.BitmapData;
	import flash.geom.Rectangle;
	import org.papervision3d.view.layer.BitmapEffectLayer;

	public class BitmapPixelateEffect extends AbstractEffect{
		
		private var layer:BitmapEffectLayer;
		public var size:int;
		
		public function BitmapPixelateEffect(size:int = 4){
			
			this.size = size;
		}

		
		public override function attachEffect(layer:BitmapEffectLayer):void{
			
			this.layer = BitmapEffectLayer(layer);
			
		}
		public override function preRender():void{
			
			
		}
		
		public override function postRender():void{
			
			if(size <= 1)
				return;
			
			var xs:Number = Math.ceil(layer.canvas.width/size);
			var ys:Number = Math.ceil(layer.canvas.height/size);
			
			var xPos:Number = 1;
			var yPos:Number = 1;
			
			var rect:Rectangle = new Rectangle(1, 1, size, size);
			var canvas:BitmapData = layer.canvas;
			
			for(var i:Number = 0;i<=xs;i++){
				for(var j:Number = 0;j<=ys;j++){
					xPos = i*size+1;
					yPos = j*size+1;
					rect.x = xPos-size/2;
					rect.y = yPos-size/2;
					canvas.fillRect(rect, canvas.getPixel32(xPos, yPos));
					
				}
			}			
		}
	}
	
}
