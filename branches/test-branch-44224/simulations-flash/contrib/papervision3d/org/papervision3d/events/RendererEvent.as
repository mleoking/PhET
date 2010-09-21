package org.papervision3d.events
{
	import flash.events.Event;
	
	import org.papervision3d.core.render.data.RenderSessionData;

	public class RendererEvent extends Event
	{
		public static const RENDER_DONE:String = "renderDone";
		public static const PROJECTION_DONE:String = "projectionDone";
		
		public var renderSessionData:RenderSessionData;
		
		public function RendererEvent(type:String, renderSessionData:RenderSessionData)
		{
			super(type);
			this.renderSessionData = renderSessionData;
		}
		
		public function clear():void
		{
			renderSessionData = null;
		}
		
		override public function clone():Event
		{
			return new RendererEvent(type, renderSessionData);
		}
		
	}
}