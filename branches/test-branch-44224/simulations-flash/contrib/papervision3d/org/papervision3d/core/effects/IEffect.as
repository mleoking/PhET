/**
* ...
* @author Default
* @version 0.1
*/

package org.papervision3d.core.effects {
	import flash.filters.BitmapFilter;
	
	import org.papervision3d.view.layer.BitmapEffectLayer;

	public interface IEffect {
		
		function attachEffect(layer:BitmapEffectLayer):void;
		function preRender():void;
		function postRender():void;
		function getEffect():BitmapFilter;
		
	}
	
}
