/**
* ...
* @author Default
* @version 0.1
*/

package org.papervision3d.core.effects {
	import flash.display.BitmapData;
	import flash.display.BlendMode;
	import flash.filters.BitmapFilter;
	import flash.geom.Point;
	
	import org.papervision3d.view.layer.BitmapEffectLayer;

	public class BitmapMotionEffect extends AbstractEffect{
		
		private var layer:BitmapEffectLayer;
		private var filter:BitmapFilter;
		private var color:uint;
		public var now:BitmapData;
		public var before:BitmapData;
		public var buffer:BitmapData;
		
		public function BitmapMotionEffect(color:uint = 0xFF00FF00){
			this.color = color;
		}

		
		public override function attachEffect(layer:BitmapEffectLayer):void{
			
			this.layer = BitmapEffectLayer(layer);
			var WIDTH:int = layer.width;
			var HEIGHT:int = layer.height;
			
			now = new BitmapData(WIDTH, HEIGHT, true);
			before = new BitmapData(WIDTH, HEIGHT, true);
			buffer = new BitmapData(WIDTH, HEIGHT, true);
			
		}
		public override function preRender():void{
			
			before.copyPixels(buffer, buffer.rect, new Point());
		}
		
		public override function postRender():void{
			
			buffer.draw(layer.drawLayer, layer.getTranslationMatrix());
			
			/*
			//TO RENDER EFFECT INTO SAME LAYER
			now.copyPixels(buffer, buffer.rect, new Point());
			now.draw(before, null, null, BlendMode.DIFFERENCE);
			now.threshold(now, now.rect, new Point(), ">", 0xFF111111, color, 0xFFFFFFFF, false);
			layer.canvas.draw(now, null, null, BlendMode.ADD); */
			
			//TO REPLACE CURRENT CONTENT
		 	layer.canvas.copyPixels(buffer, buffer.rect, new Point());
			layer.canvas.draw(before, null, null, BlendMode.DIFFERENCE);
			layer.canvas.threshold(layer.canvas, layer.canvas.rect, new Point(), ">", 0xFF101010, color, 0xFFFFFFFF, false); 
			
			
		}
	}
	
}
