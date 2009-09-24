package away3d.materials
{
	import away3d.core.utils.*;
	import away3d.materials.shaders.*;
	
	import flash.display.*;
	import flash.geom.ColorTransform;
	
	/**
	 * Bitmap material with environment shading.
	 */
	public class EnviroBitmapMaterial extends CompositeMaterial
	{
		private var _mode:String;
		private var _reflectiveness:Number;	
		private var _bitmapMaterial:BitmapMaterial;
		private var _enviroShader:EnviroShader;
		
		/**
		 * Setting for possible mapping methods.
		 */
		public function get mode():String
		{
			return _mode;
		}
        
		public function set mode(val:String):void
		{
			_mode = val;
			_enviroShader.mode = val;
		}
				
		/**
		 * Coefficient for the reflectiveness of the material.
		 */
		public function get reflectiveness():Number
		{
			return _reflectiveness;
		}
        
		public function set reflectiveness(val:Number):void
		{
			_reflectiveness = val;
			_bitmapMaterial.colorTransform = new ColorTransform(1 - _reflectiveness, 1 - _reflectiveness, 1 - _reflectiveness, 1);
			_enviroShader.reflectiveness = val;
		}
		
        /**
        * Returns the bitmapData object being used as the material environment map.
        */
		public function get enviroMap():BitmapData
		{
			return _enviroShader.bitmap;
		}
        
        /**
        * Returns the bitmapData object being used as the material texture.
        */
		public function get bitmap():BitmapData
		{
			return _bitmapMaterial.bitmap;
		}
		
		/**
		 * Creates a new <code>EnviroBitmapMaterial</code> object.
		 * 
		 * @param	bitmap				The bitmapData object to be used as the material's texture.
		 * @param	enviroMap			The bitmapData object to be used as the material's normal map.
		 * @param	init	[optional]	An initialisation object for specifying default instance properties.
		 */
		public function EnviroBitmapMaterial(bitmap:BitmapData, enviroMap:BitmapData, init:Object = null)
		{
			//remove any reference to materials
			if (init && init["materials"])
				delete init["materials"];
			
			super(init);
			
			_mode = ini.getString("mode", "linear");
			_reflectiveness = ini.getNumber("reflectiveness", 0.5, {min:0, max:1});
			
			//create new materials
			_bitmapMaterial = new BitmapMaterial(bitmap, ini);
			_bitmapMaterial.colorTransform = new ColorTransform(1 - _reflectiveness, 1 - _reflectiveness, 1 - _reflectiveness, 1);
			_enviroShader = new EnviroShader(enviroMap, {mode:_mode, reflectiveness:_reflectiveness, blendMode:BlendMode.ADD});
			
			//add to materials array
			addMaterial(_bitmapMaterial);
			addMaterial(_enviroShader);
			
		}
		
	}
}