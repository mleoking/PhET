package org.papervision3d.core.render.material
{
	import org.papervision3d.core.render.data.RenderSessionData;
	
	public interface IUpdateAfterMaterial
	{
		function updateAfterRender(renderSessionData:RenderSessionData):void;
	}
}