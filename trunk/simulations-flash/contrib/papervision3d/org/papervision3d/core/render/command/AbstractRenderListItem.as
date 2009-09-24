package org.papervision3d.core.render.command
{
	
	/**
	 * @Author Ralph Hauwert
	 */
	 
	import flash.display.Graphics;
	
	import org.papervision3d.core.render.data.RenderSessionData;
	
	public class AbstractRenderListItem implements IRenderListItem
	{
		public var screenZ:Number;
		
		public function AbstractRenderListItem()
		{
			
		}
	
		public function render(renderSessionData:RenderSessionData, graphics:Graphics):void
		{
			
		}
		
	}
}