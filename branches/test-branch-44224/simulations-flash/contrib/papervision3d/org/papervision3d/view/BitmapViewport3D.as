package org.papervision3d.view
{
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.events.Event;
	import flash.geom.Matrix;
	
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.core.view.IViewport3D;
	
	/**
	 * @Author Ralph Hauwert
	 */
	public class BitmapViewport3D extends Viewport3D implements IViewport3D
	{
		
		public var bitmapData		:BitmapData;
		
		public var _containerBitmap	:Bitmap;
		protected var _fillBeforeRender:Boolean = true;
		protected var bgColor			:int;
		protected var bitmapTransparent:Boolean;
		
		public function BitmapViewport3D(viewportWidth:Number=640, viewportHeight:Number=480, autoScaleToStage:Boolean = false,bitmapTransparent:Boolean=false, bgColor:int=0x000000,  interactive:Boolean=false, autoCulling:Boolean=true)
		{
			super(viewportWidth, viewportHeight, autoScaleToStage, interactive, true, autoCulling);
			this.bgColor = bgColor;
			_containerBitmap = new Bitmap();
			
			bitmapData = _containerBitmap.bitmapData = new BitmapData(Math.round(viewportWidth), Math.round(viewportHeight), bitmapTransparent, bgColor);
			scrollRect = null;
			addChild(_containerBitmap);
			removeChild(_containerSprite);
		}
		
		override public function updateAfterRender(renderSessionData:RenderSessionData):void
		{
			super.updateAfterRender(renderSessionData);
			if(bitmapData.width != Math.round(viewportWidth) || bitmapData.height != Math.round(viewportHeight))
			{
				bitmapData = _containerBitmap.bitmapData = new BitmapData(Math.round(viewportWidth), Math.round(viewportHeight), bitmapTransparent, bgColor);
			}
			else
			{
				if(_fillBeforeRender){
					bitmapData.fillRect(bitmapData.rect, bgColor);
				}
			}

			var mat:Matrix = new Matrix();
			mat.translate(_hWidth, _hHeight);
			bitmapData.draw(_containerSprite, mat ,null, null, bitmapData.rect, false);
		}
		
		override protected function onStageResize(event:Event = null):void
		{
			if(_autoScaleToStage)
			{
				viewportWidth = stage.stageWidth;
				viewportHeight = stage.stageHeight;
			}
		}
		
		public function set fillBeforeRender(value:Boolean):void
		{
			_fillBeforeRender = value;	
		}
		
		public function get fillBeforeRender():Boolean
		{
			return _fillBeforeRender;
		}
		
		override public function set autoClipping(clip:Boolean):void
		{
			//Do nothing.
		}
		
		override public function get autoClipping():Boolean
		{
			return _autoClipping;	
		}
	}
}