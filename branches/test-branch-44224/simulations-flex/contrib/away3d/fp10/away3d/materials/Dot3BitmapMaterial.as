package away3d.materials
{
	import away3d.core.utils.*;
	import away3d.materials.shaders.*;
	
	import flash.display.*;
	
	/**
	 * Bitmap material with DOT3 shading.
	 */
	public class Dot3BitmapMaterial extends CompositeMaterial
	{
		private var _shininess:Number;
		private var _specular:Number;
		private var _bitmapMaterial:BitmapMaterial;
		private var _phongShader:CompositeMaterial;
		private var _ambientShader:AmbientShader;
		private var _diffuseDot3Shader:DiffuseDot3Shader;
		private var _specularDot3Shader:SpecularDot3Shader;
		
		/**
		 * The exponential dropoff value used for specular highlights.
		 */
		public function get shininess():Number
		{
			return _shininess;
		}
		
		public function set shininess(val:Number):void
		{
			_shininess = val;
			_specularDot3Shader.shininess = val;
		}
		
		/**
		 * Coefficient for specular light level.
		 */
		public function get specular():Number
		{
			return _specular;
		}
		
		public function set specular(val:Number):void
		{
			if (_specular == val)
				return;
			
			_specular = val;
			_specularDot3Shader.specular = val;
			
			if (_specular && materials.length < 3)
        		addMaterial(_specularDot3Shader);
   			else if (!_specular && materials.length > 2)
            	removeMaterial(_specularDot3Shader);
		}
        
        /**
        * Returns the bitmapData object being used as the material normal map.
        */
		public function get normalMap():BitmapData
		{
			return _diffuseDot3Shader.bitmap;
		}
        
        /**
        * Returns the bitmapData object being used as the material texture.
        */
		public function get bitmap():BitmapData
		{
			return _bitmapMaterial.bitmap;
		}
		
		/**
		 * Creates a new <code>Dot3BitmapMaterial</code> object.
		 * 
		 * @param	bitmap				The bitmapData object to be used as the material's texture.
		 * @param	normalMap			The bitmapData object to be used as the material's DOT3 map.
		 * @param	init	[optional]	An initialisation object for specifying default instance properties.
		 */
		public function Dot3BitmapMaterial(bitmap:BitmapData, normalMap:BitmapData, init:Object = null)
		{
			if (init && init["materials"])
				delete init["materials"];
			
			super(init);
			
			_shininess = ini.getNumber("shininess", 20);
			_specular = ini.getNumber("specular", 0.5, {min:0, max:1});
			
            var renderNormalMap:BitmapData = new BitmapData(normalMap.width, normalMap.height, true, 0);
            
            var w:int = normalMap.width;
			var h:int = normalMap.height;
			
			var i:int = h;
			var j:int;
			var pixelValue:int;
			var rValue:Number;
			var gValue:Number;
			var bValue:Number;
			var mod:Number;
			
			//normalise map
			while (i--) {
				j = w;
				while (j--) {
					//get values
					pixelValue = normalMap.getPixel32(j, i);
					rValue = ((pixelValue & 0x00FF0000) >> 16) - 127;
					gValue = ((pixelValue & 0x0000FF00) >> 8) - 127;
					bValue = ((pixelValue & 0x000000FF)) - 127;
					
					//calculate modulus
					mod = Math.sqrt(rValue*rValue + gValue*gValue + bValue*bValue)*2;
					
					//set normalised values
					renderNormalMap.setPixel32(j, i, (0xFF << 24) + (int(0xFF*(rValue/mod + 0.5)) << 16) + (int(0xFF*(gValue/mod + 0.5)) << 8) + int(0xFF*(bValue/mod + 0.5)));
				}
			}
			
			//create new materials
			_bitmapMaterial = new BitmapMaterial(bitmap, ini);
			_phongShader = new CompositeMaterial({blendMode:BlendMode.MULTIPLY});
			_phongShader.addMaterial(_ambientShader = new AmbientShader({blendMode:BlendMode.ADD}));
			_phongShader.addMaterial(_diffuseDot3Shader = new DiffuseDot3Shader(renderNormalMap, {blendMode:BlendMode.ADD}));
			_specularDot3Shader = new SpecularDot3Shader(renderNormalMap, {shininess:_shininess, specular:_specular, blendMode:BlendMode.ADD});
			
			//add to materials array
			addMaterial(_bitmapMaterial);
			addMaterial(_phongShader);
			
			if (_specular)
				addMaterial(_specularDot3Shader);
		}
	}
}