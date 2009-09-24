package org.papervision3d.core.render.project.basic
{
	import org.papervision3d.core.render.data.RenderSessionData;
	
	public interface IProjector
	{
		function project(renderSessionData:RenderSessionData):void;
	}
}