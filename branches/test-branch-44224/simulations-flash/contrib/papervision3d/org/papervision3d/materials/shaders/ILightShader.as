package org.papervision3d.materials.shaders
{
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.core.render.shader.ShaderObjectData;
	
	public interface ILightShader
	{
		function updateLightMatrix(sod:ShaderObjectData,renderSessionData:RenderSessionData):void;
	}
}