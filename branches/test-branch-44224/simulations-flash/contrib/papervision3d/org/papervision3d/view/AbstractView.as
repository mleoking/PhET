package org.papervision3d.view
{
	import flash.display.Sprite;
	import flash.events.Event;
	
	import org.papervision3d.core.proto.CameraObject3D;
	import org.papervision3d.core.view.IView;
	import org.papervision3d.render.BasicRenderEngine;
	import org.papervision3d.scenes.Scene3D;
	
	/**
	 * @Author Ralph Hauwert
	 */
	public class AbstractView extends Sprite implements IView
	{
		protected var _camera:CameraObject3D;
		protected var _height:Number;
		protected var _width:Number;
		
		public var scene:Scene3D;
		public var viewport:Viewport3D;
		public var renderer:BasicRenderEngine;
		
		public function AbstractView()
		{
			super();
		}
		
		public function startRendering():void
		{
			addEventListener(Event.ENTER_FRAME, onRenderTick);
			viewport.containerSprite.cacheAsBitmap = false;
		}
		
		public function stopRendering(reRender:Boolean = false, cacheAsBitmap:Boolean = false):void
		{
			removeEventListener(Event.ENTER_FRAME, onRenderTick);
			if(reRender){
				onRenderTick();	
			}
			if(cacheAsBitmap){
				viewport.containerSprite.cacheAsBitmap = true;
			}else{
				viewport.containerSprite.cacheAsBitmap = false;
			}
		}
		
		public function singleRender():void
		{
			onRenderTick();
		}
		
		protected function onRenderTick(event:Event = null):void
		{
			renderer.renderScene(scene, _camera, viewport);
		}
		
		public function get camera():CameraObject3D
		{
			return _camera;
		}
		
		public function set viewportWidth(width:Number):void
		{
			_width = width;
			viewport.width = width;
		}
		
		public function get viewportWidth():Number
		{
			return _width;
		}
		
		public function set viewportHeight(height:Number):void
		{
			_height = height;
			viewport.height = height;
		}
		
		public function get viewportHeight():Number
		{
			return _height;
		}
		
	}
}