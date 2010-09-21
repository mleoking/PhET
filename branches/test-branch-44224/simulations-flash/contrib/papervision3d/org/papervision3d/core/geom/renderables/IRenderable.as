package org.papervision3d.core.geom.renderables
{
	/**
	 * @Author Ralph Hauwert
	 */
	 
	import org.papervision3d.core.render.command.IRenderListItem;
	
	public interface IRenderable
	{
		function getRenderListItem():IRenderListItem;
	}
}