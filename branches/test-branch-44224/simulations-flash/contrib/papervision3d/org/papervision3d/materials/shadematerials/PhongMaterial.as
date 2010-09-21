package org.papervision3d.materials.shadematerials
{
	import org.papervision3d.core.proto.LightObject3D;
	import org.papervision3d.materials.utils.LightMaps;
	
	/**
	 * @Author Ralph Hauwert
	 */
	public class PhongMaterial extends EnvMapMaterial
	{
		public function PhongMaterial(light:LightObject3D, lightColor:uint, ambientColor:uint, specularLevel:uint)
		{
			super(light, LightMaps.getPhongMap(lightColor, ambientColor, specularLevel), null, ambientColor);
		}
		
	}
}