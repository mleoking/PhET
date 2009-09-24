/**
* ...
* @author Default
* @version 0.1
*/

package org.papervision3d.core.effects.utils {
	import flash.display.BitmapData;
	import flash.display.BlendMode;
	import flash.display.DisplayObject;
	import flash.geom.ColorTransform;
	import flash.geom.Matrix;
	import flash.geom.Rectangle;

	public class BitmapDrawCommand {
		
		public var colorTransform:ColorTransform = null;
		public var transformMatrix:Matrix = null;
		public var blendMode:String = BlendMode.NORMAL;
		public var smooth:Boolean = false;
		public var drawContainer:Boolean = false;
		
		public function BitmapDrawCommand(transMat:Matrix = null, colorTransform:ColorTransform = null, blendMode:String = null, smooth:Boolean = false){

			this.transformMatrix = transMat;
			this.colorTransform = colorTransform;
			this.blendMode = blendMode;
			this.smooth = smooth;
			
			
		}
		
		public function draw(canvas:BitmapData, drawLayer:DisplayObject, transMat:Matrix = null, clipRect:Rectangle = null):void{
			
			var tMat:Matrix = transMat.clone();
			if(transformMatrix)
				tMat.concat(transformMatrix);
			
			
			canvas.draw(drawLayer, tMat, colorTransform, blendMode, clipRect, smooth);
		}
	}
	
}
