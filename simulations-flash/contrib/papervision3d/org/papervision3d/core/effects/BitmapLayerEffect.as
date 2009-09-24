/**
* ...
* @author Default
* @version 0.1
*/

package org.papervision3d.core.effects {
	import flash.filters.BitmapFilter;

	import org.papervision3d.view.layer.BitmapEffectLayer;

	public class BitmapLayerEffect extends AbstractEffect{
		
		private var layer:BitmapEffectLayer;
		private var filter:BitmapFilter;
		public var isPostRender:Boolean;
		
		public function BitmapLayerEffect(filter:BitmapFilter, isPostRender:Boolean = true){
			this.isPostRender = isPostRender;
			this.filter = filter;
		}
		
		public function updateEffect(filter:BitmapFilter):void{
			this.filter = filter;
		}
		
		public override function attachEffect(layer:BitmapEffectLayer):void{
			
			this.layer = BitmapEffectLayer(layer);
			
		}
		
		public override function preRender():void{
			if(!isPostRender)
				layer.canvas.applyFilter(layer.canvas, layer.clippingRect, layer.clippingPoint, filter);
			
		}
		public override function postRender():void{
			if(isPostRender)
				layer.canvas.applyFilter(layer.canvas, layer.clippingRect, layer.clippingPoint, filter);
			
		}
	}
	
}
