package org.papervision3d.materials.shaders
{
	import flash.display.BitmapData;
	
	import org.papervision3d.core.proto.LightObject3D;
	import org.papervision3d.materials.utils.LightMaps;
	
	/**
	 *	@Author Ralph Hauwert
	 */
	public class PhongShader extends EnvMapShader
	{
		public function PhongShader(light:LightObject3D, lightColor:int, ambientColor:int = 0x000000, specularLevel:int = 0, bumpMap:BitmapData = null, specularMap:BitmapData = null)
		{
			super(light, LightMaps.getPhongMap(lightColor, ambientColor, specularLevel), null, ambientColor, bumpMap, specularMap);
		}
	}
}