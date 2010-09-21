package org.papervision3d.view.layer
{
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.display.DisplayObject;
	import flash.display.Sprite;
	import flash.geom.Matrix;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	
	import org.papervision3d.core.effects.AbstractEffect;
	import org.papervision3d.core.effects.utils.BitmapClearMode;
	import org.papervision3d.core.effects.utils.BitmapDrawCommand;
	import org.papervision3d.objects.DisplayObject3D;
	import org.papervision3d.view.Viewport3D;

	public class BitmapEffectLayer extends ViewportLayer
	{
		
		public var canvas:BitmapData;
		private var transMat:Matrix;
		public var clearMode:String = BitmapClearMode.CLEAR_PRE;
		public var clippingRect:Rectangle;
		public var clippingPoint:Point;
		public var drawCommand:BitmapDrawCommand;
		public var clearBeforeRender:Boolean;
		public var bitmapContainer:Bitmap;
		private var _width:Number;
		private var _height:Number;
		
		public var trackingObject:DisplayObject3D;
		public var trackingOffset:Point;
		
		public var scrollX:Number = 0;
		public var scrollY:Number = 0;
		
		public var effects:Array;
		public var drawLayer:Sprite;
		public var renderAbove:Boolean = false;
		
		public function BitmapEffectLayer(viewport:Viewport3D, w:Number = 640, h:Number=480, transparent:Boolean = true, fillColor:uint=0, clearMode:String = "clear_pre", renderAbove:Boolean = false, clearBeforeRender:Boolean = false)
		{
			super(viewport, new DisplayObject3D(), false);
			
			effects = new Array();
			canvas = new BitmapData(w, h, transparent, fillColor);
			
			_width = w;
			_height = h;
			
			transMat = new Matrix();
			transMat.translate(w>>1, h>>1);
			
			bitmapContainer = new Bitmap(canvas);
			addChild(bitmapContainer);
			
			bitmapContainer.x = -(w*0.5);
			bitmapContainer.y = -(h*0.5);
			
			drawLayer = new Sprite();
			addChild(drawLayer);
			
			this.graphicsChannel = drawLayer.graphics;
			
			this.clearMode = clearMode;
			
			trackingOffset = new Point();
			clippingPoint = new Point();
			clippingRect = canvas.rect;
			
			drawCommand = new BitmapDrawCommand();
			
			this.clearBeforeRender = clearBeforeRender;
			if(!renderAbove)
				setChildIndex(drawLayer, 0);
		}
		
		public function setBitmapOffset(x:Number, y:Number):void{
			
			bitmapContainer.x = x-(_width*0.5);
			bitmapContainer.y = y-(_height*0.5);
			
			transMat = new Matrix();
			transMat.translate(_width>>1, _height>>1);
			
			transMat.translate(-x, -y);
		}
		
		public function setTracking(object:DisplayObject3D, offset:Point = null):void{
			trackingObject = object;
			if(offset)
				trackingOffset = offset;
			else
				trackingOffset = new Point();
		}
		
		public function setScroll(x:Number = 0, y:Number = 0):void{
			scrollX = x;
			scrollY = y;
		}
		
		public function fillCanvas(color:uint):void{
			canvas.fillRect(canvas.rect, color);
		}
		
		public function renderEffects():void{

			var drawTarget:DisplayObject = drawLayer;
			
			if(trackingObject)
				setBitmapOffset(trackingObject.screen.x+trackingOffset.x, trackingObject.screen.y+trackingOffset.y);			
			
			if(drawCommand.drawContainer){
				drawTarget = this;
			}
			
			if(scrollX != 0 || scrollY != 0)
				canvas.scroll(scrollX, scrollY);
			
			drawCommand.draw(canvas, drawTarget, transMat, clippingRect);

			for each(var e:AbstractEffect in effects){
				e.postRender();
			}
			if(clearMode == BitmapClearMode.CLEAR_POST)
				drawLayer.graphics.clear();
			
		}
		public function removeEffect(fx:AbstractEffect):void{
			

		}
		
		public function setClipping(rect:Rectangle, point:Point):void{
			this.clippingRect = rect;
			this.clippingPoint = point;
		}
		
		public function addEffect(fx:AbstractEffect):void{
			
			fx.attachEffect(this);
			effects.push(fx);
			
		}
		
		public override function updateBeforeRender():void
		{
			
			if(clearBeforeRender)
				canvas.fillRect(canvas.rect, 0);

			for each(var e:AbstractEffect in effects){
				e.preRender();
			}
			
			if(clearMode == BitmapClearMode.CLEAR_PRE)
				drawLayer.graphics.clear();
				
			super.updateBeforeRender();
		}
		
		public override function updateAfterRender():void{
			//super.updateAfterRender();
			renderEffects();
		}
		
		public function getTranslationMatrix():Matrix{
			return transMat;
		}
		
	}
		

}

