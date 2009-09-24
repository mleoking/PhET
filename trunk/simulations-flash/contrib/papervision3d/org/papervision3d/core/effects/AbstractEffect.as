/**
* ...
* @author Default
* @version 0.1
*/

package org.papervision3d.core.effects {

	import flash.filters.BitmapFilter;
	
	import org.papervision3d.view.layer.BitmapEffectLayer;
	
	public class AbstractEffect implements IEffect{

		function AbstractEffect(){}
		
		public function attachEffect(layer:BitmapEffectLayer):void{}
		public function preRender():void{}
		public function postRender():void{}
		public function getEffect():BitmapFilter{
			return null;
		}
		
	}
	
}
