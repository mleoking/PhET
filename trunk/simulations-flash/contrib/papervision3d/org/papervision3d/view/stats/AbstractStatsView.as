package org.papervision3d.view.stats
{
	import flash.display.MovieClip;
	import flash.events.Event;
	import flash.utils.getTimer;
	
	import org.papervision3d.core.render.AbstractRenderEngine;
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.events.RendererEvent;

	public class AbstractStatsView extends MovieClip
	{
		protected var _renderEngine:AbstractRenderEngine;
		protected var _renderSessionData:RenderSessionData;
		protected var _fps:int;
		protected var lastFrameTime:int;
		protected var currentFrameTime:int;
		
		public function AbstractStatsView()
		{
			super();
			setupListeners();
		}
		
		protected function setupListeners():void
		{
			addEventListener(Event.ENTER_FRAME, onFrame);
		}
		
		protected function onRenderDone(event:RendererEvent):void
		{
			renderSessionData = event.renderSessionData;
		}
		
		protected function onFrame(event:Event):void
		{
			currentFrameTime = getTimer();
			fps = 1000/(currentFrameTime - lastFrameTime);
			lastFrameTime = currentFrameTime;
		}
		
		public function set renderEngine(renderEngine:AbstractRenderEngine):void
		{
			if(_renderEngine){
				_renderEngine.removeEventListener(RendererEvent.RENDER_DONE, onRenderDone);
			}
			if(renderEngine != null){
				renderEngine.addEventListener(RendererEvent.RENDER_DONE, onRenderDone);
			}
			_renderEngine = renderEngine;
		}
		
		public function get renderEngine():AbstractRenderEngine
		{
			return _renderEngine;	
		}
		
		public function set renderSessionData(renderSessionData:RenderSessionData):void
		{
			_renderSessionData = renderSessionData;	
		}
		
		public function get renderSessionData():RenderSessionData
		{
			return _renderSessionData;
		}
		
		public function set fps(fps:int):void
		{
			_fps = fps;	
		}
		
		public function get fps():int
		{
			return _fps;
		}
	}
}