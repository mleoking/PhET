package org.papervision3d.materials.shaders
{
	import org.papervision3d.core.proto.LightObject3D;
	import org.papervision3d.materials.utils.LightMaps;
	
	/**
	 *@Author Ralph Hauwert 
	 */
	public class CellShader extends EnvMapShader
	{
		
		public function CellShader(light:LightObject3D, color_1:int = 0xFFFFFF, color_2:int = 0x000000, steps:int = 3)
		{
			super(light, LightMaps.getCellMap(color_1, color_2, steps),null, color_2,null,null);
		}
		
	}
}