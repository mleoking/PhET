package org.papervision3d.core.render.material
{
	import org.papervision3d.core.render.data.RenderSessionData;
	
	public interface IUpdateBeforeMaterial
	{
		function updateBeforeRender(renderSessionData:RenderSessionData):void;
		function isUpdateable():Boolean;
	}
}