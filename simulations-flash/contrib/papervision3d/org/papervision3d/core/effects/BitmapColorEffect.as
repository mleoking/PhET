/**
* ...
* @author Default
* @version 0.1
*/

package org.papervision3d.core.effects {
	import flash.filters.ColorMatrixFilter;
	import flash.geom.Point;
	import org.papervision3d.view.layer.BitmapEffectLayer;
	

	public class BitmapColorEffect extends AbstractEffect{
		
		private var layer:BitmapEffectLayer;
		private var filter:ColorMatrixFilter;
		
		public function BitmapColorEffect(r:Number = 1, g:Number = 1, b:Number = 1, a:Number= 1){
			
		filter = new ColorMatrixFilter(
		[r,0,0,0,0,
		 0,g,0,0,0,
		 0,0,b,0,0,
		 0,0,0,a,0]
		 );
			
		}
		
		public function updateEffect(r:Number = 1, g:Number = 1, b:Number = 1, a:Number= 1):void{
			filter = new ColorMatrixFilter(
		[r,0,0,0,0,
		 0,g,0,0,0,
		 0,0,b,0,0,
		 0,0,0,a,0]
		 );
			
		}
		public override function attachEffect(layer:BitmapEffectLayer):void{
			
			this.layer = BitmapEffectLayer(layer);
			
		}
		public override function postRender():void{
			
			layer.canvas.applyFilter(layer.canvas, layer.canvas.rect, new Point(), filter);
			
		}
	}
	
}
