package org.papervision3d.materials.shaders
{
	import org.papervision3d.core.proto.LightObject3D;
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.core.render.shader.ShaderObjectData;
	import org.papervision3d.materials.utils.LightMatrix;
	
	/**
	 * @Author Ralph Hauwert
	 */
	public class LightShader extends Shader implements IShader, ILightShader
	{
	
		public function LightShader():void
		{
			super();
		}
		
		public function set light(light:LightObject3D):void
		{
			_light = light;
		}
		
		public function get light():LightObject3D
		{
			return _light;	
		}
		
		public function updateLightMatrix(sod:ShaderObjectData, renderSessionData:RenderSessionData):void
		{
			sod.lightMatrices[this] = LightMatrix.getLightMatrix(light, sod.object, renderSessionData,sod.lightMatrices[this]);
		}
		
		private var _light:LightObject3D;
		
	}
}