package away3d.materials
{
	import away3d.core.utils.*;
	import away3d.materials.shaders.*;
	
	import flash.display.*;
	
	/**
	 * Bitmap material with cached DOT3 shading.
	 */
	public class Dot3BitmapMaterialCache extends BitmapMaterialContainer
	{
		private var _shininess:Number;
		private var _specular:Number;
		private var _bitmapMaterial:BitmapMaterial;
		private var _phongShader:BitmapMaterialContainer;
		private var _ambientShader:AmbientShader;
		private var _diffuseDot3Shader:DiffuseDot3Shader;
		
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
            //_specularPhongShader.shininess = val;
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
			_specular = val;
            //_specularPhongShader.specular = val;
		}
		
        /**
        * Returns the bitmapData object being used as the material normal map.
        */
		public function get normalMap():BitmapData
		{
			return _diffuseDot3Shader.bitmap;
		}
        
		/**
		 * @inheritDoc
		 */
		public override function get bitmap():BitmapData
		{
			return _bitmapMaterial.bitmap;
		}
		
		/**
		 * Creates a new <code>Dot3BitmapMaterialCache</code> object.
		 * 
		 * @param	bitmap				The bitmapData object to be used as the material's texture.
		 * @param	normalMap			The bitmapData object to be used as the material's DOT3 map.
		 * @param	init	[optional]	An initialisation object for specifying default instance properties.
		 */
		public function Dot3BitmapMaterialCache(bitmap:BitmapData, normalMap:BitmapData, init:Object = null)
		{
			super(bitmap.width, bitmap.height, init);
			
			_shininess = ini.getNumber("shininess", 20);
			_specular = ini.getNumber("specular", 0.7);
			
			//create new materials
			_bitmapMaterial = new BitmapMaterial(bitmap, ini);
			_phongShader = new BitmapMaterialContainer(bitmap.width, bitmap.height, {blendMode:BlendMode.MULTIPLY, transparent:false});
			_phongShader.addMaterial(_ambientShader = new AmbientShader({blendMode:BlendMode.ADD}));
			_phongShader.addMaterial(_diffuseDot3Shader = new DiffuseDot3Shader(normalMap, {blendMode:BlendMode.ADD}));
			
			//add to materials array
			addMaterial(_bitmapMaterial);
			addMaterial(_phongShader);
			//materials.push(_specularPhongShader = new SpecularPhongShader({shininess:_shininess, specular:_specular, blendMode:BlendMode.ADD}));
		}
		
	}
}